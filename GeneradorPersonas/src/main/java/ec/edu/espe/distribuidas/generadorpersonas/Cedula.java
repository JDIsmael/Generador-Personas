/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.espe.distribuidas.generadorpersonas;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Grupo 3
 */
public class Cedula {

    private Integer provinceDigits;
    private long idBody;
    private String cedula;
    private HashMap<Integer, Long> provinceMap = new HashMap<>();

    public Cedula() {
        this.provinceDigits = 0;
        this.idBody = 0;
        this.cedula = "9999999999";
        for(int i=0;i<25;i++){
            provinceMap.put(i, ThreadLocalRandom.current().nextLong(1, 9999999));
        }
    }

    private void randomProvinceBodyGeneration() {
        this.provinceDigits = ThreadLocalRandom.current().nextInt(1, 25);
        this.generateBody();
        
    }
    private void generateBody(){
        if(provinceMap.get(this.provinceDigits)>=9999999){
            provinceMap.put(this.provinceDigits,Integer.toUnsignedLong(1));
            this.idBody = 1;
        }else{
            this.idBody = provinceMap.get(this.provinceDigits)+1;
            provinceMap.put(this.provinceDigits,this.idBody);
        }
    }
    public String idGeneration() {
        randomProvinceBodyGeneration();
        int[] coefValCedula = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        this.cedula = getBody();
        Integer suma = 0;
        int digito = 0;
        for (int i = 0; i < (this.cedula.length()); i++) {
            digito = Integer.parseInt(this.cedula.substring(i, i + 1)) * coefValCedula[i];
            suma += ((digito % 10) + (digito / 10));
        }
        suma = suma%10 == 0?0:10-suma%10;
        this.cedula = new StringBuilder(this.cedula).append(suma.toString()).toString();
        return this.cedula;
    }

    public String getBody() {
        String province = this.provinceDigits < 10 ? String.format("%02d", this.provinceDigits) : this.provinceDigits.toString();
        return new StringBuilder(province).append(String.format("%07d",this.idBody)).toString();
    }
    
}
