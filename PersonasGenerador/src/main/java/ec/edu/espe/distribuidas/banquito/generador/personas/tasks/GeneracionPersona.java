/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.espe.distribuidas.banquito.generador.personas.tasks;

import ec.edu.espe.distribuidas.banquito.generador.personas.config.ApplicationValues;
import ec.edu.espe.distribuidas.banquito.generador.personas.model.Persona;
import ec.edu.espe.distribuidas.banquito.generador.personas.model.PersonaRQ;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.web.client.RestTemplate;
/**
 *
 * @author Admin
 */
@Slf4j
public class GeneracionPersona implements Tasklet, StepExecutionListener {

    private final ApplicationValues applicationValues;
    private final String NOMBRES_FILE = "Nombres.txt";
    private final String APELLIDOS_FILE = "Apellidos.txt";
    private final String PROVINCIAS_FILE = "Provincias.txt";
    private String estadoCivil[] = {"CASAD","DIVORCIAD","SOLTER","VIUD"};
    private List<String> nombresM = new ArrayList<>();
    private List<String> nombresF = new ArrayList<>();
    private List<String> apellidos = new ArrayList<>();
    private Map<String, List<String>> provincias = new HashMap<>();
    private Integer personas;
    private RestTemplate restTemplate;

    public GeneracionPersona(ApplicationValues applicationValues) {
        this.applicationValues = applicationValues;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void beforeStep(StepExecution se) {
        try {
            log.info("Iniciando la lectura de datos para la generacion");
            Path fileNombres = Paths.get(this.applicationValues.getDataPath() + this.NOMBRES_FILE);
            List<String> nombres = Files.readAllLines(fileNombres);

            for (String nombre : nombres) {
                String[] dataNombre = nombre.split(",");
                if (dataNombre != null && dataNombre.length == 2) {
                    if ("0".equals(dataNombre[0])) {
                        this.nombresM.add(dataNombre[1]);
                    } else {
                        this.nombresF.add(dataNombre[1]);
                    }
                }
            }

            log.info("Se han cargado {} nombres masculinos y {} nombres femeninos", this.nombresM.size(), this.nombresF.size());
            Path fileApellidos = Paths.get(this.applicationValues.getDataPath() + this.APELLIDOS_FILE);
            this.apellidos = Files.readAllLines(fileApellidos);
            log.info("Se han cargado {} apellidos", this.apellidos.size());

            Path fileUbicaciones = Paths.get(this.applicationValues.getDataPath() + this.PROVINCIAS_FILE);
            List<String> ubicaciones = Files.readAllLines(fileUbicaciones);
            
            for (String provincia : ubicaciones) {
                String[] provinceParts = provincia.split(",");
                this.provincias.putIfAbsent(provinceParts[0], new ArrayList<>());
                List<String> provincasPut = this.provincias.get(provinceParts[0]);
                provincasPut.add(provinceParts[1] + "," + provinceParts[2] + "," + provinceParts[3]);
            }
            
            log.info("Se han cargado {} provincias con sus ubicaciones", this.provincias.size());
            ExecutionContext sc = se.getJobExecution().getExecutionContext();
            this.personas = (Integer) sc.get("records");
            
            log.info("personas: {}",this.personas);
            
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    
    
    
    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        
        Random r = new Random();
        PersonaRQ personasRQ = new PersonaRQ();
        personasRQ.setPersonas(new ArrayList<>());
        
        for(int i=1; i<= this.personas; i++){
            Persona persona = new Persona();
            
            persona.setIdentificacion(generarCedula());
            persona.setGenero(i % 2 == 0 ? "M" : "F");
            if("M".equals(persona.getGenero())){
                persona.setNombres(this.generarNombres(this.nombresM) );
            }else{
                persona.setNombres(this.generarNombres(this.nombresF) );
            }
            
            persona.setApellidos(this.apellidos.get(r.nextInt(this.apellidos.size())) + " " +
                   this.apellidos.get(r.nextInt(this.apellidos.size())));
            
            persona.setNombrePadre(this.generarNombres(this.nombresM) + " " 
                    + persona.getApellidos().split(" ")[0] + " " 
                    + this.apellidos.get(r.nextInt(this.apellidos.size())));
            
            persona.setNombreMadre(this.generarNombres(this.nombresF) + " "
                    + persona.getApellidos().split(" ")[1] + " "
                    + this.apellidos.get(r.nextInt(this.apellidos.size())));
            
            List<String> data = this.provincias.get(persona.getIdentificacion().substring(0,2));
            
            String rowData[] = data.get(r.nextInt(data.size())).split(",");            
            persona.setProvincia(rowData[0]);
            persona.setCanton((rowData[1]));
            persona.setParroquia(rowData[2]);
            
            persona.setFechaNacimiento(this.generarFechaNacimiento());
            persona.setEstadoCivil(this.estadoCivil[r.nextInt(this.estadoCivil.length)]
                    .concat(persona.getGenero() == "M" ? "O":"A"));
            
            persona.setCodigoDactilar(this.generarCodigoDactilar(
                    persona.getApellidos().split(" ")));
            
            personasRQ.getPersonas().add(persona);
            if (i % 50000 == 0){
                this.restTemplate.postForObject("http://localhost:8001/api/registrocivil/generador/", personasRQ, String.class);
                
                personasRQ.getPersonas().clear();
                System.gc(); 
            }
            //log.info(persona.toString());
        }
        
        if (this.personas % 50000 != 0) {
           this.restTemplate.postForObject("http://localhost:8001/api/registrocivil/generador/", personasRQ, String.class);
        } else {
            personasRQ.getPersonas().clear();
            //sb.setLength(0);
            System.gc();
        }
        log.info("acabo");
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution se) {
        return ExitStatus.COMPLETED;
    }
    
    private String generarCedula() {
        Random rnd = new Random();
        Integer start = rnd.nextInt(24) + 1;
        Integer middle = RandomUtils.nextInt(1000001, 9999999);
        String body = String.format("%02d", start) + middle.toString();
        int[] coefValCedula = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        Integer suma = 0;
        int digito = 0;
        for (int i = 0; i < body.length(); i++) {
            digito = Integer.parseInt(body.substring(i, i + 1)) * coefValCedula[i];
            suma += ((digito % 10) + (digito / 10));
        }
        suma = suma % 10 == 0 ? 0 : 10 - suma % 10;
        return body + suma.toString();
    }

    private String generarCodigoDactilar(String[] code){
        String codigoDactilar = "";
        for(int i=0;i<2;i++)
            codigoDactilar += code[i].substring(0,1) + RandomUtils.nextInt(1001, 9999);
        
        return  codigoDactilar;
    }
    
    private String generarNombres(List<String> nombres){
        Random r = new Random();
        return nombres.get(r.nextInt(nombres.size())) + " " 
                + nombres.get(r.nextInt(nombres.size()));
    }
    
    
    private String generarFechaNacimiento(){
        int month = 12;
        int day = 28;
        int minDay = (int) LocalDate.of(1940, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2002, month, day).toEpochDay();
        Random r = new Random();
        long randomDay = minDay + r.nextInt(maxDay - minDay);
        
        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedString = randomBirthDate.format(formatter); 

        return formattedString;
    }
}
