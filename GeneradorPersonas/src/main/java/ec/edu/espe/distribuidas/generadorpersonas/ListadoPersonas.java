/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.espe.distribuidas.generadorpersonas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author jdismael
 */
public class ListadoPersonas {
    private List<Person> personas;
    
    public ListadoPersonas() {
        this.personas = new ArrayList<>();
    }

    public List<Person> getPersonas() {
        return personas;
    }

    public void setPersonas(List<Person> personas) {
        this.personas = personas;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.personas);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ListadoPersonas other = (ListadoPersonas) obj;
        if (!Objects.equals(this.personas, other.personas)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ListadoPersonas{" + "personas=" + personas + '}';
    }

    
    
}
