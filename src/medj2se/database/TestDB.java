package medj2se.database;

import java.sql.SQLException;
import medj2se.models.Patient;
import medj2se.models.Drug;
import medj2se.helpers.CreateSerializablePatient;
import medj2se.helpers.Helper;
import java.util.ArrayList;

public class TestDB{


public static void main(String args[]){
Patient patient=null;
ArrayList<Drug> drugList = new ArrayList<Drug>();

DBConnection db = new DBConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/emr","root","");
    try{ 
        patient = db.getPatient("P001");
        drugList = db.getDrugListForDiagnosis("DIAG001");
    }catch(SQLException e){}
    System.out.print("ID and Name: ");
    System.out.println(patient);
    System.out.print("DOB: ");
    System.out.println(patient.getDob());
    System.out.print("Marital Status: ");
    System.out.println(patient.getMaritalStatus());
    System.out.print("Allergies: ");
    Helper.printArrayList(patient.getAllergies());
    System.out.print("Secondary Diagnosis: ");
    Helper.printArrayList(patient.getSecondaryDiagnosis());
    System.out.print("Primary Diagnosis: ");
    Helper.printArrayList(patient.getPrimaryDiagnosis());
    System.out.print("Family History: ");
    Helper.printHashMap(patient.getFamilyHistory());
    System.out.print("Vitals History: ");
    Helper.printHashMap(patient.getVitalsHistory());
    System.out.print("Current Medication: ");
    Helper.printArrayList(patient.getCurrentMedication());
    System.out.print("Current Symptoms: ");
    Helper.printArrayList(patient.getCurrentSymptoms());
    System.out.print("SocialHistory: ");
    Helper.printArrayList(patient.getSocialHistory());
    System.out.print("Sex: ");
    System.out.println(patient.getSex());

    System.out.println("============== Printing Test for Drug=======================");

    for(Drug d: drugList){
        System.out.print("Drug ID: ");
        System.out.println(d);
        
        System.out.print("Drug Name: ");
        System.out.println(d.getDrugName());
        
        System.out.print("Indication/Usage: ");
        Helper.printArrayList(d.getIndicationUsage());
        
        System.out.print("Cost: ");
        System.out.println(d.getCost());
        
        System.out.print("Availability: ");
        Helper.printHashMap(d.getAvailability());

        System.out.print("Absolute Contraindication: ");
        Helper.printArrayList(d.getAContraindication());

        System.out.print("Relative Contraindication: ");
        Helper.printArrayList(d.getRContraindication());

        System.out.print("Interaction: ");
        Helper.printArrayList(d.getInteraction());

        System.out.print("Comorbidity: ");
        Helper.printArrayList(d.getComorbidity());


        System.out.println();
        System.out.println("-------------End Drug-------------");
    }


}

}
