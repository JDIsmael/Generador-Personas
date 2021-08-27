package ec.edu.espe.distribuidas.generadorpersonas;

import java.util.*;

public class Registers {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {                
        FileManager filen = new FileManager();  
        Scanner input = new Scanner(System.in);
        //lectura de archivos
        ArrayList<String> malesList = nameListByGender(0,filen.readn("Nombres.txt"));
        ArrayList<String> femalesList = nameListByGender(1,filen.readn("Nombres.txt"));
        ArrayList<String> lastNamesList = filen.readn("Apellidos.txt");
        Map<String, ArrayList<String>> provincesMap = provincesMapper();
        //ingreso de registros
        System.out.println("Ingrese el número de registros: ");
        int nRegisters = input.nextInt();
        
        
        
        //calculo de numero por grupo etario en base a porcentajes dados
        int porcentaje = (int) ((double) 12 / 100 * nRegisters);
        int porcentaje8 = (int) ((double) 16 / 100 * nRegisters);
        //creación de hilos para generar cada grupo etario
        filen.createFile("Registers.txt");
        PopulationGenerator t1 = new PopulationGenerator(porcentaje,1922,1956,malesList,femalesList,lastNamesList,provincesMap,1);
        PopulationGenerator t2 = new PopulationGenerator(porcentaje,1957,1971,malesList,femalesList,lastNamesList,provincesMap,2);
        PopulationGenerator t3 = new PopulationGenerator(porcentaje,1972,1991,malesList,femalesList,lastNamesList,provincesMap,3);
        PopulationGenerator t4 = new PopulationGenerator(porcentaje,1992,2001,malesList,femalesList,lastNamesList,provincesMap,4);
        PopulationGenerator t5 = new PopulationGenerator(porcentaje,1980,2002,malesList,femalesList,lastNamesList,provincesMap,5);    
        PopulationGenerator t6 = new PopulationGenerator(porcentaje,1972,1991,malesList,femalesList,lastNamesList,provincesMap,6);
        PopulationGenerator t7 = new PopulationGenerator(porcentaje,1992,2001,malesList,femalesList,lastNamesList,provincesMap,7);
        PopulationGenerator t8 = new PopulationGenerator(porcentaje8,1980,2002,malesList,femalesList,lastNamesList,provincesMap,8);
                
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
              
    }
    //Devuelve un arreglo que devuelve una lista de nombres dependiendo del sexo
    public static ArrayList<String> nameListByGender(int gender,ArrayList<String> lines){
        ArrayList<String> names = new ArrayList<>(); 

        for(String i : lines){
            try{
                if(i.contains(gender+"")) names.add(i.replace(gender+",", ""));
            }catch(NullPointerException e){}
        }
        return names;
    }
    //Ingresa los datos del archivo provincias en un cubo
    public static Map<String, ArrayList<String>> provincesMapper() {
        FileManager filen = new FileManager(); 
        ArrayList<String> provincesList = filen.readn("Provincias.txt");
        Map<String, ArrayList<String>> provincesMap = new HashMap<>();
        
        for(String province : provincesList) {
            
            String[] provinceParts = province.split(",");
            ArrayList<String> provinces = new ArrayList<>();
            provincesMap.putIfAbsent(provinceParts[0], new ArrayList<>());
            provinces = provincesMap.get(provinceParts[0]);
            provinces.add(provinceParts[1] + "," + provinceParts[2] + "," + provinceParts[3]);
        }
        return provincesMap;
    }
}