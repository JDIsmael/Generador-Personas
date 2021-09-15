/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.espe.distribuidas.banquito.generador.personas.util;

import ec.edu.espe.distribuidas.banquito.generador.personas.model.Persona;
import ec.edu.espe.distribuidas.banquito.generador.personas.model.PersonaRQ;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author jdismael
 */
@Slf4j
public class Generador extends Thread {

    private final RestTemplate restTemplate;

    private String estadoCivil[] = {"CASAD", "DIVORCIAD", "SOLTER", "VIUD"};
    private List<String> nombresM;
    private List<String> nombresF;
    private List<String> apellidos;
    private Map<String, List<String>> provincias;
    private Integer personas;

    public Generador(List<String> nombresM, List<String> nombresF, List<String> apellidos, Map<String, List<String>> provincias, Integer personas) {
        this.nombresM = nombresM;
        this.nombresF = nombresF;
        this.apellidos = apellidos;
        this.provincias = provincias;
        this.personas = personas;

        this.restTemplate = new RestTemplate();
    }

    @Override
    public void run() {
        Random r = new Random();
        PersonaRQ personasRQ = new PersonaRQ();
        personasRQ.setPersonas(new ArrayList<>());
        boolean check;
        String cedula;
        for (int i = 1; i <= this.personas; i++) {
            Persona persona = new Persona();
            check = true;

            do {
                cedula = this.generarCedula();
                String respVerificacion = this.restTemplate.getForObject("http://20.85.192.181:8001/api/registrocivil/verificacion/" + cedula, String.class);
                
                if (respVerificacion.equals("N"))
                    check = false;
                
            } while (check);

            persona.setIdentificacion(cedula);
            persona.setGenero(i % 2 == 0 ? "M" : "F");
            if ("M".equals(persona.getGenero())) {
                persona.setNombres(this.generarNombres(this.nombresM));
            } else {
                persona.setNombres(this.generarNombres(this.nombresF));
            }

            persona.setApellidos(this.apellidos.get(r.nextInt(this.apellidos.size())) + " "
                    + this.apellidos.get(r.nextInt(this.apellidos.size())));

            persona.setNombrePadre(this.generarNombres(this.nombresM) + " "
                    + persona.getApellidos().split(" ")[0] + " "
                    + this.apellidos.get(r.nextInt(this.apellidos.size())));

            persona.setNombreMadre(this.generarNombres(this.nombresF) + " "
                    + persona.getApellidos().split(" ")[1] + " "
                    + this.apellidos.get(r.nextInt(this.apellidos.size())));

            List<String> data = this.provincias.get(persona.getIdentificacion().substring(0, 2));

            String rowData[] = data.get(r.nextInt(data.size())).split(",");
            persona.setProvincia(rowData[0]);
            persona.setCanton((rowData[1]));
            persona.setParroquia(rowData[2]);

            persona.setFechaNacimiento(this.generarFechaNacimiento());
            persona.setEstadoCivil(this.estadoCivil[r.nextInt(this.estadoCivil.length)]
                    .concat(persona.getGenero() == "M" ? "O" : "A"));

            persona.setCodigoDactilar(this.generarCodigoDactilar(
                    persona.getApellidos().split(" ")));

            personasRQ.getPersonas().add(persona);
            if (i % 100 == 0) {
                this.restTemplate.postForObject("http://20.85.192.181:8001/api/registrocivil/generador/", personasRQ, String.class);
                log.info("Insertados : {}", personasRQ.getPersonas().size());
                personasRQ.getPersonas().clear();
                System.gc();
            }

        }

        if (this.personas % 100 != 0) {
            this.restTemplate.postForObject("http://20.85.192.181:8001/api/registrocivil/generador/", personasRQ, String.class);
            log.info("Insertados Ultimos : {}", personasRQ.getPersonas().size());
        } else {
            personasRQ.getPersonas().clear();
            System.gc();
        }
        log.info("acabo");
    }

    private String generarCodigoDactilar(String[] code) {
        String codigoDactilar = "";
        for (int i = 0; i < 2; i++) {
            codigoDactilar += code[i].substring(0, 1) + RandomUtils.nextInt(1001, 9999);
        }

        return codigoDactilar;
    }

    private String generarNombres(List<String> nombres) {
        Random r = new Random();
        return nombres.get(r.nextInt(nombres.size())) + " "
                + nombres.get(r.nextInt(nombres.size()));
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

    private String generarFechaNacimiento() {
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
