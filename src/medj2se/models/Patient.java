package medj2se.models;

import java.util.*;
import java.io.Serializable;

public class Patient implements Serializable{

    public  String patientID;
    public  String firstname;
    public  String lastname;
    public  Date dob;
    public  String sex;
    public  String maritalStatus;
    public  String bloodGroup;
    public  String currentDiagnosis;
    public  ArrayList<String> allergies;
    public  ArrayList<String> secondaryDiagnosis;
    public  ArrayList<String> primaryDiagnosis;
    public  HashMap<String,String> familyHistory;
    public  HashMap<String,String> vitalsHistory;
    public  ArrayList<String> currentMedication;
    public  ArrayList<String> currentSymptoms;
    public  ArrayList<String> socialHistory;
    
    
    public Patient(
           String patientID,String firstname, String lastname,
           Date dob, String sex, String maritalStatus,String bloodGroup,
           ArrayList<String> allergies,ArrayList<String> secondaryDiagnosis,
           ArrayList<String> primaryDiagnosis,
           HashMap<String,String> familyHistory,
           HashMap<String,String> vitalsHistory,
           ArrayList<String> currentMedication,
           ArrayList<String> currentSymptoms,
           ArrayList<String> socialHistory 
            ){
                this.patientID = patientID;
                this.firstname = firstname;
                this.lastname = lastname;
                this.dob = dob;
                this.sex = sex;
                this.maritalStatus = maritalStatus;
                this.bloodGroup = bloodGroup;
                this.allergies = allergies;
                this.secondaryDiagnosis = secondaryDiagnosis;
                this.primaryDiagnosis = primaryDiagnosis;
                this.familyHistory = familyHistory;
                this.vitalsHistory = vitalsHistory;
                this.currentMedication = currentMedication;
                this.currentSymptoms = currentSymptoms;
                this.socialHistory = socialHistory;
            }

 
           public boolean isAllergic(Drug drug){
                return this.allergies.contains(drug.drugID);
           }



            public boolean isComorbid(Drug drug){
                boolean comorbid = false;
                for(String diag: this.secondaryDiagnosis){
                    if(drug.aContraindication.contains(diag)){
                       comorbid=true;
                    }
                }
                return comorbid;
            }

    
            public boolean isInteract(Drug drug){
                boolean interact = false; 
                for(String cDrug: this.currentMedication){
                    if(drug.interaction.contains(cDrug)){
                        interact = true;
                        return interact;
                    }
                }
                return interact; 
            }
    

          public String toString(){
            return patientID + " " + firstname + " "+lastname;
          } 

    
    /**
     * Get patientID.
     *
     * @return patientID as String.
     */
    public String getPatientID()
    {
        return patientID;
    }
    
    /**
     * Get firstname.
     *
     * @return firstname as String.
     */
    public String getFirstname()
    {
        return firstname;
    }
    
    /**
     * Get lastname.
     *
     * @return lastname as String.
     */
    public String getLastname()
    {
        return lastname;
    }
    
    /**
     * Get dob.
     *
     * @return dob as Date.
     */
    public Date getDob()
    {
        return dob;
    }
    
    /**
     * Get maritalStatus.
     *
     * @return maritalStatus as String.
     */
    public String getMaritalStatus()
    {
        return maritalStatus;
    }
    
    /**
     * Get bloodGroup.
     *
     * @return bloodGroup as String.
     */
    public String getBloodGroup()
    {
        return bloodGroup;
    }
 

    public ArrayList<String> getAllergies(){
        return allergies; 
    }

    public ArrayList<String> getSecondaryDiagnosis(){
        return secondaryDiagnosis; 
    }

    public ArrayList<String> getPrimaryDiagnosis(){
        return primaryDiagnosis; 
    }


    public HashMap<String,String> getFamilyHistory(){
        return familyHistory;
    }

    public HashMap<String,String> getVitalsHistory(){
        return vitalsHistory;
    }

    public ArrayList<String> getCurrentMedication(){
        return currentMedication;
    }

    public ArrayList<String> getCurrentSymptoms(){
        return currentSymptoms;
    }

    public ArrayList<String> getSocialHistory(){
        return socialHistory;
    }

    public String getSex(){
        return sex;
    }

    public void setCurrentDiagnosis(String diagnosis){
        this.currentDiagnosis = diagnosis;
    }

   public String getCurrentDiagnosis(){
        return this.currentDiagnosis;
   }

}

