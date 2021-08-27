package ec.edu.espe.distribuidas.generadorpersonas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileManager {
    
    //Inicializa archivo con una línea. Borra registros previamente guardados.
    public void createFile(String path){
        try{
            BufferedWriter bw = new BufferedWriter(
                                    new OutputStreamWriter(
                                        new FileOutputStream(path), StandardCharsets.UTF_8));
            bw.newLine();
            bw.write("");
            bw.close();            
        }catch(IOException e){
            System.out.println("Ha ocurrido un error en el archivo: " + e);
        }        
    }  
    //Adjunta una línea nueva al archivo
    public void writeRecords(String data, String path){
        try{
            BufferedWriter bw = new BufferedWriter(
                                    new OutputStreamWriter(
                                        new FileOutputStream(path, true), StandardCharsets.UTF_8));
            bw.append(data);
            bw.close();            
        }catch(IOException e){
            System.out.println("Ha ocurrido un error adjuntando línea en el archivo: " + e);
        }        
    }  
    //Lee el archivo y devuelve sus líneas en un ArrayList
    public ArrayList<String> readn(String path){        
        ArrayList<String> lines = new ArrayList<>();     
        try{
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));) {
                String line = in.readLine();
                while(line != null)
                {
                  if (line.equals(""))
                  {
                      continue;
                  }                    
                    lines.add(line);
                    line = in.readLine();
                 }
            }            
        }
        catch(IOException e)
        {
            System.out.println("Ha ocurrido un error al leer el archivo: "+e);
            return null;
        }       
        return lines;  
    }    
}

