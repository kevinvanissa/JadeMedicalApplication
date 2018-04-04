package medj2se.models;

import java.io.Serializable;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import medj2se.helpers.Helper;

public class CaseBase implements Serializable{

    public  String ID;
    public  Date dob;
    public  String sex;
    public  String bloodGroup;
    public  String currentDiagnosis;
    public  ArrayList<String> allergies;
    public  ArrayList<String> secondaryDiagnosis;
    public  ArrayList<String> primaryDiagnosis;
    public  ArrayList<Drug> drugsTaken;
    public  String ratingSuccess;
    public  HashMap<String,String> familyHistory;
    public  HashMap<String,String> vitalsHistory;
    public  ArrayList<String> currentMedication;
    public  ArrayList<String> currentSymptoms;
    public  ArrayList<String> socialHistory;


    public CaseBase(
           Date dob, String sex,String bloodGroup,
           ArrayList<String> allergies,
           ArrayList<String> secondaryDiagnosis,
           ArrayList<String> primaryDiagnosis,
           ArrayList<Drug> drugsTaken,
           String ratingSuccess,
           HashMap<String,String> familyHistory,
           HashMap<String,String> vitalsHistory,
           ArrayList<String> currentMedication,
           ArrayList<String> currentSymptoms,
           ArrayList<String> socialHistory){


                this.dob = dob;
                this.sex = sex;
                this.bloodGroup = bloodGroup;
                this.allergies = allergies;
                this.secondaryDiagnosis = secondaryDiagnosis;
                this.primaryDiagnosis = primaryDiagnosis;
                this.drugsTaken = drugsTaken;
                this.familyHistory = familyHistory;
                this.vitalsHistory = vitalsHistory;
                this.currentMedication = currentMedication;
                this.currentSymptoms = currentSymptoms;
                this.socialHistory = socialHistory;
                this.ID = UUID.randomUUID().toString();
    }//End Constructor

   //This method will return how closely match this patient 
   //is to this case
   public double match(Patient patient){
        //11 items x 10 (each out of)
        int overallTotal = 6;
        double subTotal = 0;
        double matchPercent = 0.0; 

        double bg = matchBloodGroup(patient.getBloodGroup());
        Helper.debug("bg",bg);

        double allergies =  Helper.calculateDelta(this.allergies,patient.getAllergies());
        Helper.debug("allergies",allergies);
        //System.out.println("Allegries++"+this.allergies);

        double sDiagnosis =  Helper.calculateDelta(this.secondaryDiagnosis,patient.getSecondaryDiagnosis());
        Helper.debug("SDiagnosis",sDiagnosis);


        double cMedication =  Helper.calculateDelta(this.currentMedication,patient.getCurrentMedication());
        Helper.debug("cMedication",cMedication);


        double cSymptoms =  Helper.calculateDelta(this.currentSymptoms,patient.getCurrentSymptoms());
        Helper.debug("cSymptoms",cSymptoms);

        double sHistory =  Helper.calculateDelta(this.socialHistory,patient.getSocialHistory());
        Helper.debug("sHistory",sHistory);


        subTotal = bg + allergies + sDiagnosis + cMedication + cSymptoms + sHistory;
                

        matchPercent = (subTotal/overallTotal);
        System.out.println("MatchPercent: "+matchPercent);
        return matchPercent;
   }

    //=============Helper Functions for match=============
    
   private double matchDob(Date dob){
        double matchDobPercent = 0.0; 
        
        return matchDobPercent;
    }

    private double matchBloodGroup(String bloodGroup){
        double matchBloodGroupPercent = 0.0; 
        if(bloodGroup.equals(this.bloodGroup)){
            matchBloodGroupPercent = 100.0;
        
        }
        return matchBloodGroupPercent;
    }

    private double matchAllergies(ArrayList<String> allergies){
        double matchAllergiesPercent=0.0;
        return matchAllergiesPercent;
    }

   private double matchSecondaryDiagnosis(ArrayList<String> sDiagnosis){
        double matchSDiagnosisPercent=0.0;
        return matchSDiagnosisPercent;
    }

    private double matchPrimaryDiagnosis(ArrayList<String> pDiagnosis){
        double matchPDiagnosisPercent=0.0;
        return matchPDiagnosisPercent;
    
    }

//=============END helpers for match================================

    public String toString(){
        return "Case Object with ID: " + this.ID;
    }
    
    /**
     * Set ID.
     *
     * @param ID the value to set.
     */
    public void setID(String ID)
    {
        this.ID = ID;
    }
    
    /**
     * Set dob.
     *
     * @param dob the value to set.
     */
    public void setDob(Date dob)
    {
        this.dob = dob;
    }
    
    /**
     * Set sex.
     *
     * @param sex the value to set.
     */
    public void setSex(String sex)
    {
        this.sex = sex;
    }
    
    
    
    /**
     * Set bloodGroup.
     *
     * @param bloodGroup the value to set.
     */
    public void setBloodGroup(String bloodGroup)
    {
        this.bloodGroup = bloodGroup;
    }
    
    /**
     * Set ratingSuccess.
     *
     * @param ratingSuccess the value to set.
     */
    public void setRatingSuccess(String ratingSuccess)
    {
        this.ratingSuccess = ratingSuccess;
    }


   public void setCurrentDiagnosis(String diagnosis){
        this.currentDiagnosis = diagnosis;
   }

  public String getCurrentDiagnosis(){
    return this.currentDiagnosis;
  }

    public ArrayList<Drug> getDrugsTaken(){
        return this.drugsTaken;
    }
   
}//End Class
