package medj2se.models;

import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File; 

import java.util.Date;

public class CaseBaseManager{

    public static CaseBase createCase(Patient patient, 
            ArrayList<Drug> drugList,String rating){

        CaseBase cb = new CaseBase(
                patient.getDob(),                
                patient.getSex(),                
                patient.getBloodGroup(),
                patient.getAllergies(),
                patient.getSecondaryDiagnosis(),
                patient.getPrimaryDiagnosis(),
                drugList,
                rating,
                patient.getFamilyHistory(),
                patient.getVitalsHistory(),
                patient.getCurrentMedication(),
                patient.getCurrentSymptoms(),
                patient.getSocialHistory()
                );
        return cb;
    }
    

    public static void saveCase(CaseBase cb,String foldername){
       //String path =  System.getProperty("user.dir") +"/"+foldername;
       String path =  "/home/dundee/workspace/medj2se/resources/"+foldername;
       try{
           
            File f = new File(path+"/"+uniqueFileName("case"));
            while(f.exists()){
                f = new File(path+"/"+uniqueFileName("case"));
            }
            f.createNewFile();
            //String fileToCreate = f.getName();
            FileOutputStream fout = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(cb);
            oos.close();
            System.out.println("Saved Object Successfully");
       }catch(Exception ex){
            ex.printStackTrace(); 
       }//End try catch
    }//End saveCase


    //private Helper
    private static String uniqueFileName(String filename){
        java.util.Random nahcis = new java.util.Random();
        String rs = Integer.toString(nahcis.nextInt(9999));

        String DATE_FORMAT="yyyyMMdd_HHmmss-SSS";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        return filename + "_" + sdf.format(new Date()) + rs;
    }



    public static ArrayList<CaseBase> matchCases(Patient patient,String foldername){
       //String path =  System.getProperty("user.dir") +"/"+foldername;
       String path =  "/home/dundee/workspace/medj2se/resources/"+foldername;
       ArrayList<CaseBase> cbList = new ArrayList<CaseBase>();
       String files;
       File folder = new File(path);
       File[] listOfFiles = folder.listFiles();
       for(int i =0;i<listOfFiles.length;i++){
           if(listOfFiles[i].isFile()){
               files = listOfFiles[i].getName();
               CaseBase cb = deserializeCaseBase(path,files);
               //FIXME: LET THE MATCH BE HIGHER THAN THIS
                   if(cb.match(patient) > 30 ){
                        cbList.add(cb);
                    }
           }//Endif
       }//EndFor
       return cbList;
    }//End matchCases

    //Helper for matchCases
    private static CaseBase deserializeCaseBase(String path, String filename){
        CaseBase cb = null; 
        try{
            FileInputStream fin = new FileInputStream(path+"/"+filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Object o = ois.readObject();
            cb = (CaseBase) o;
            ois.close();       
            return cb;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }//end deserializeCaseBase






}
