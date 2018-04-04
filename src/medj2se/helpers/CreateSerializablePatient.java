package medj2se.helpers;

import medj2se.models.Patient;
import java.util.*;

public class CreateSerializablePatient implements java.io.Serializable{

     private Patient patient=null;
     private boolean PATIENTSET=false;
    //getters    
     public Patient getPatient(){
       return patient;
    }
    public boolean isPatientSet(){
	return this.PATIENTSET;
    }
    //==========================================
    //modifier
    public void setPatient (Patient patient){
      	this.patient = patient;
	this.PATIENTSET=true;
        
    }

}
