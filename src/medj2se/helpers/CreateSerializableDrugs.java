package medj2se.helpers;

import medj2se.models.Drug;
import java.util.*;

public class CreateSerializableDrugs implements java.io.Serializable{

     private ArrayList<Drug> drugList=null;
     private boolean DRUGSET = false;
  
    //getters    
    public ArrayList<Drug> getDrugList(){
        return drugList;
    }
    
    public boolean isDrugSet(){
	return this.DRUGSET;
    }
    //==========================================
    //modifier
    public void setDrugList(ArrayList<Drug> drugList){
       this.drugList = drugList;
       this.DRUGSET=true;
    }
}
