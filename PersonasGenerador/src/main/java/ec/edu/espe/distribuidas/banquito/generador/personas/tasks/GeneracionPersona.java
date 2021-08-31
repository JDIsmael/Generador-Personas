/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.espe.distribuidas.banquito.generador.personas.tasks;

import ec.edu.espe.distribuidas.banquito.generador.personas.config.ApplicationValues;
import ec.edu.espe.distribuidas.banquito.generador.personas.model.Persona;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author Admin
 */
@Slf4j
public class GeneracionPersona implements Tasklet, StepExecutionListener {

    private final ApplicationValues applicationValues;
    private final MongoTemplate mongoTemplate;
    private final String NOMBRES_FILE = "Nombres.txt";
    private final String APELLIDOS_FILE = "Apellidos.txt";
    private final String PROVINCIAS_FILE = "Provincias.txt";
    private List<String> nombresM = new ArrayList<>();
    private List<String> nombresF = new ArrayList<>();
    private List<String> apellidos = new ArrayList<>();
    private Map<String, List<String>> provincias = new HashMap<>();
    private Integer personas;

    public GeneracionPersona(ApplicationValues applicationValues, MongoTemplate mongoTemplate) {
        this.applicationValues = applicationValues;
        this.mongoTemplate = mongoTemplate;
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
            ExecutionContext sc = se.getExecutionContext();
            this.personas = (Integer)sc.get("records");
        } catch (Exception e) {
            log.error("");
        }
    }

    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        Random nombresR = new Random();
        Random apellidosR = new Random();
        for(int i=1; i<=this.personas; i++){
            Persona persona = new Persona();
            persona.setCedula(generarCedula());
            persona.setGenero("" + i % 2);
            if("0".equals(persona.getGenero())){
                persona.setNombres(this.nombresM.get(nombresR.nextInt(this.nombresM.size())) 
                        + "" + this.nombresM.get(nombresR.nextInt(this.nombresM.size())) );
            }else{
                persona.setNombres(this.nombresF.get(nombresR.nextInt(this.nombresF.size())) 
                        + "" + this.nombresF.get(nombresR.nextInt(this.nombresF.size())) );
            }
            persona.setApellidos(this.apellidos.get(apellidosR.nextInt(this.apellidos.size())) + " " +
                   this.apellidos.get(apellidosR.nextInt(this.apellidos.size())));
            List<String> data = this.provincias.get(persona.getCedula().substring(0,2));
            String rowData[] = data.get(nombresR.nextInt(data.size())).split(",");            
            persona.setProvincia(rowData[0]);
            persona.setCanton((rowData[1]));
            persona.setParroquia(rowData[2]);
            log.info(persona.toString());
            
        }
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

}
