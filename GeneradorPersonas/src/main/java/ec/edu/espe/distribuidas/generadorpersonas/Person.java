
package ec.edu.espe.distribuidas.generadorpersonas;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;


public class Person {
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
    private String fechaNacimiento;
    private Integer edad;


    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }    
    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAge() {
        return edad;
    }
    public String getIdentificacion() {
        return identificacion;
    }
    public String getGenero() {
        return genero;
    }
    //Inicializa un objeto con el menor valor que puede tener una cédula
    public Person() {       
        this.codigoDactilar = "A34SDS2";
    }
    //Escoge randómicamente el nombre de la persona y el apellido de los despectivos ArrayList
    public void generateNameAndSurnameGender(ArrayList<String> names,ArrayList<String> lastnames){                
        String data = "";        
        int randomNumber;
        Random random = new Random();        
        
        for(int i = 0; i<2; i++){            
            randomNumber = random.nextInt(names.size());
            data += names.get(randomNumber) + " ";
        }
        this.nombres = data;
        data = "";
        for(int j = 0; j<2; j++){           
            randomNumber = random.nextInt(lastnames.size());            
            data += lastnames.get(randomNumber) + " ";
        }        
        this.apellidos = data;
    }
    
    
    public void generateNamesAndSurnamesPadres(ArrayList<String> malesList,ArrayList<String> femalesList,ArrayList<String> lastnames){
        String dataPadre= "";
        String dataMadre= "";
        Random random = new Random();  
        
        for(int i = 0; i<2; i++){    
            dataPadre += malesList.get(random.nextInt(malesList.size())) + " ";
            dataMadre += femalesList.get(random.nextInt(femalesList.size())) + " ";
        }
        
        String[] apellidosPadres = this.apellidos.split(" ");
        dataPadre += apellidosPadres[0] + " " + lastnames.get(random.nextInt(lastnames.size()));
        dataMadre += apellidosPadres[1] + " " + lastnames.get(random.nextInt(lastnames.size()));
        
        this.nombrePadre = dataPadre;
        this.nombreMadre = dataMadre;
        
    }
    
    /*
    Escoge randómicamente un registro del cubo de provincias que corresponden al código de la cédula y 
    almacena la provincia, cantón y parroquía en los respectivos atributos del objeto
    */
    public void generateDirection(String stateCode,Map<String, ArrayList<String>> provincesMap){        
        int random;        
        Random r = new Random();
        random = r.nextInt(provincesMap.get(stateCode).size());
        String data[] = provincesMap.get(stateCode).get(random).split(",");
        
        this.provincia = data[0];
        this.canton = data[1];
        this.parroquia = data[2];
    }
    //Calcula una fecha randómica de nacimiento entre dos años y calcula la edad de la persona
    public void setBdateAge(int start,int end){        
        int month = (end==2021) ? 6 : 12;
        int day = (end==2021) ? 9 : 28;
        int minDay = (int) LocalDate.of(start, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(end, month, day).toEpochDay();
        Random r = new Random();
        long randomDay = minDay + r.nextInt(maxDay - minDay);
        
        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);        
        Period ageCalc = Period.between(randomBirthDate, LocalDate.now()); //calcular el tiempo entre dos fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //establecer el formato de la fecha
        String formattedString = randomBirthDate.format(formatter); //convertir de LocalDate a String en base al formato establecido

        //Date date = Date.from(randomBirthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.fechaNacimiento = formattedString;
        this.edad = ageCalc.getYears();
    }
    //Transforma a texto todos los campos del texto tabulados
    @Override
    public String toString(){
        return identificacion+"\t"+nombres+"\t"+apellidos+"\t"+genero+"\t"+provincia+"\t"+canton+"\t"+parroquia+"\t"+codigoDactilar+"\t"+nombrePadre+"\t"+nombreMadre+"\t"+fechaNacimiento+"\t"+edad+"\n";
    }
    
}