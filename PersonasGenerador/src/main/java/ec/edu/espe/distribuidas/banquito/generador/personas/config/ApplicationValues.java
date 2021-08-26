/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.espe.distribuidas.banquito.generador.personas.config;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
@Slf4j
@ToString
public class ApplicationValues {
    
    private final String mongoHost;
    private final String mongoDB;
    private final String mongoAut;
    private final String mongoUser;
    private final String mongoPassword;
    private final String configFile;
    
    @Autowired
    public ApplicationValues(@Value("${registrocivil.mongo.host}") String mongoHost,
            @Value("${registrocivil.mongo.db}") String mongoDB,
            @Value("${registrocivil.mongo.aut}") String mongoAut,
            @Value("${registrocivil.mongo.usr}") String mongoUser,
            @Value("${registrocivil.mongo.pwd}") String mongoPassword,
            @Value("${registrocivil.config.file}") String configFile)
    {
        this.mongoHost = mongoHost;
        this.mongoDB = mongoDB;
        this.mongoAut = mongoAut;
        this.mongoUser = mongoUser;
        this.mongoPassword = mongoPassword;
        this.configFile = configFile;
        log.info("Propiedades cargadas: " + this.toString());
    }

    public String getMongoHost() {
        return mongoHost;
    }

    public String getMongoDB() {
        return mongoDB;
    }

    public String getMongoAut() {
        return mongoAut;
    }

    public String getMongoUser() {
        return mongoUser;
    }

    public String getMongoPassword() {
        return mongoPassword;
    }

    public static Logger getLog() {
        return log;
    }

    public String getConfigFile() {
        return configFile;
    }
    
    
    
    
}
