/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.espe.distribuidas.banquito.generador.personas.tasks;

import ec.edu.espe.distribuidas.banquito.generador.personas.config.ApplicationValues;
import ec.edu.espe.distribuidas.banquito.generador.personas.util.Generador;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
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
    private List<String> nombresM = new ArrayList<>();
    private List<String> nombresF = new ArrayList<>();
    private List<String> apellidos = new ArrayList<>();
    private Map<String, List<String>> provincias = new HashMap<>();
    private Integer personas;
    

    public GeneracionPersona(ApplicationValues applicationValues) {
        this.applicationValues = applicationValues;
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
        
        int porcentaje = (int) ((double) 20 / 100 * this.personas);
    
        Generador t1 = new Generador(this.nombresM, this.nombresF, this.apellidos, this.provincias, porcentaje);
        Generador t2 = new Generador(this.nombresM, this.nombresF, this.apellidos, this.provincias, porcentaje);
        Generador t3 = new Generador(this.nombresM, this.nombresF, this.apellidos, this.provincias, porcentaje);
        Generador t4 = new Generador(this.nombresM, this.nombresF, this.apellidos, this.provincias, porcentaje);
        Generador t5 = new Generador(this.nombresM, this.nombresF, this.apellidos, this.provincias, porcentaje);    
               
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        
        
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution se) {
        return ExitStatus.COMPLETED;
    }
    
    
}
