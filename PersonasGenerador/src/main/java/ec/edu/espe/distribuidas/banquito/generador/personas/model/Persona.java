/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.espe.distribuidas.banquito.generador.personas.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Admin
 */
@Data
public class Persona {
    private String identificacion;
    private String nombres;
    private String apellidos;
    private String genero;
    private String provincia;
    private String canton;
    private String parroquia;
    private String codigoDactilar;
    private String nombrePadre;
    private String nombreMadre;
    private String estadoCivil;
    private String fechaNacimiento;
}
