package ec.edu.espe.distribuidas.banquito.generador.personas;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonasApplication {

    @Autowired
    JobLauncher jobLauncher;
    
    @Autowired
    @Qualifier("generatePersons")
    Job jobs;
    
	public static void main(String[] args) {
		SpringApplication.run(PersonasApplication.class, args);
	}

}
