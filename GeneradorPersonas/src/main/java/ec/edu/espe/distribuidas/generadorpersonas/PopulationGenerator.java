package ec.edu.espe.distribuidas.generadorpersonas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
Clase que hereda de hilos para generar la cantidad de personas en base a los porcentajes
dados por grupo etario
 */
public class PopulationGenerator extends Thread {

    private Person person;
    private int startYear;
    private int hilo;
    private int endYear;
    private int populationNumber;
    private ArrayList<String> malesList;
    private ArrayList<String> femalesList;
    private ArrayList<String> lastNamesList;
    Map<String, ArrayList<String>> provincesMap;

    public PopulationGenerator(
            int percent,
            int start,
            int end,
            ArrayList<String> males,
            ArrayList<String> females,
            ArrayList<String> lastNames,
            Map<String, ArrayList<String>> provincesMap,
            int hilo
    ) {
        this.populationNumber = percent;
        this.startYear = start;
        this.endYear = end;
        this.malesList = males;
        this.femalesList = females;
        this.lastNamesList = lastNames;
        this.provincesMap = provincesMap;
        this.hilo = hilo;
    }

    //Serie de instrucciones que ejecuta cada hilo
    @Override
    public void run() {
        //FileManager filen = new FileManager();  
        ArrayList<String> names;
        Random r = new Random();

        System.out.println("registros del hilo: " + populationNumber);
        //StringBuilder sb = new StringBuilder(); //Para un mejor manejo de cadenas extensas

        ListadoPersonas personas = new ListadoPersonas();
        //List<Person> personasAdd = new ArrayList<>();
        ClienteApi clientes[] = new ClienteApi[5];
        // "crear los nuevos atributos de cada persona y agregar a la cadena"
        for (int i = 1, cont = 0; i <= populationNumber; i++) {
            this.person = new Person();
            this.person.setGenero((r.nextInt(2) + 1 == 1) ? "M" : "F");
            names = (this.person.getGenero().equals("M")) ? malesList : femalesList;

            this.person.setIdentificacion(new Cedula().idGeneration());
            this.person.setBdateAge(startYear, endYear);
            this.person.generateNameAndSurnameGender(names, lastNamesList);
            this.person.generateNamesAndSurnamesPadres(malesList, femalesList, lastNamesList);
            this.person.generateDirection(this.person.getIdentificacion().substring(0, 2), provincesMap);
            personas.getPersonas().add(this.person);
            if (i % 50000 == 0) {
                //filen.writeRecords(sb.toString(),"Registers.txt"); 
                System.out.println("proceso hilo: " + hilo);
                clientes[cont] = new ClienteApi(personas);
                clientes[cont].start();
                try {
                    clientes[cont].join();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                cont++;

                System.out.println("cont: " + cont);
                //cliente.send(personas);
                personas.getPersonas().clear();
                //sb.setLength(0);
                System.gc(); //Reciclar objtos no utilizados para liberar memoria
            }

        }
        /*for(Person persona : personas.getPersonas())
            System.out.println(persona.toString());  */

        if (populationNumber % 50000 != 0) {
            //filen.writeRecords(sb.toString(),"Registers.txt");   
            System.out.println("proceso hilo: " + hilo);
            ClienteApi cli = new ClienteApi(personas);
            cli.start();
            try {
                cli.join();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //cliente.send(personas);
        } else {
            personas.getPersonas().clear();
            //sb.setLength(0);
            System.gc();
        }

        System.out.println(populationNumber + " Insertados");
    }

}
