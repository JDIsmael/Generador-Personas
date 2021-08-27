package ec.edu.espe.distribuidas.generadorpersonas;

import java.util.ArrayList;

public class IdGenerator {

    Person person;

    public IdGenerator(Person person) {
        this.person = person;
    }

    // Método para generar la siguiente cédula válida, incrementa desde una cédula de inicio
    public void generate(long minId) {
        long invalidIds = 0L, validId = 0L;
        char[] posChar = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] posInt = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        boolean flag = false;

        while (!flag) {
            minId++;
            String cad = Long.toString(minId);

            if (cad.length() == 9) {
                posInt[0] = 0;
                posChar[0] = '0';
                for (int j = 1; j < (cad.length() + 1); j++) {
                    posChar[j] = (char) cad.charAt(j - 1);
                    posInt[j] = Character.getNumericValue(posChar[j]);
                }
            } else {
                for (int j = 0; j < cad.length(); j++) {
                    posChar[j] = (char) cad.charAt(j);
                    posInt[j] = Character.getNumericValue(posChar[j]);
                }
            }
            if (lastDigit(posInt, posChar) && thirdDigit(posInt[2])) {
                //validId++;
                flag = true;
            //} else {
              //  invalidIds++;
            }
        }
        person.setIdentificacion(String.valueOf(posChar));
    }

    //Método para la validación de las cédulas generadas
    private static boolean lastDigit(int idInt[], char idChar[]) {
        int[] coefficient = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int result, sum = 0, nextTen = 0;

        for (int i = 0; i <= 8; i++) {
            result = idInt[i] * coefficient[i];
            if (result > 9) {
                result -= 9;
            }
            sum += result;
        }

        nextTen = (sum / 10 + 1) * 10;
        int last = nextTen - sum;

        if (last >= 10) {
            last = 0;
        }

        return (last == idInt[9]) ? true : false;
    }

    private static boolean thirdDigit(int digit) {
        return (digit < 6) ? true : false;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 
package ec.edu.espe.distribuidas.generadorpersonas;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Grupo 3
 
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
        System.out.println("ProvinceMap in Cedula(): " + provinceMap.toString());
    }

    private void randomProvinceBodyGeneration() {
        this.provinceDigits = ThreadLocalRandom.current().nextInt(1, 25);
        System.out.println("provinceDigits in randomProvinceBodyGeneration(): " + this.provinceDigits);
        this.generateBody();
        
    }
    private void generateBody(){
        if(provinceMap.get(this.provinceDigits)>=9999999){
            provinceMap.put(this.provinceDigits,Integer.toUnsignedLong(1));
            System.out.println("ProvinceMap in generateBody() if true: " + provinceMap);

            this.idBody = 1;
        }else{
            this.idBody = provinceMap.get(this.provinceDigits)+1;
            System.out.println("ProvinceMap in generateBody() if faalse: " + provinceMap);

            provinceMap.put(this.provinceDigits,this.idBody);
            System.out.println("ProvinceMap in generateBody() if faalse: " + provinceMap);

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
    
}*/
