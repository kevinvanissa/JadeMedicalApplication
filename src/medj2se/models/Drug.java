package medj2se.models;

import java.util.*;
import java.math.BigDecimal;
import java.io.Serializable;

public class Drug implements Serializable{

                public  String drugID;
                public  String drugName;
                public  ArrayList <String> indicationUsage;
                public  BigDecimal cost;
                public  HashMap<String, String> availability;
                public  ArrayList<String> aContraindication;
                public  ArrayList<String> rContraindication;
                public  ArrayList<String> interaction;
                public  String drugForm;
                public  ArrayList<String> comorbidity;


        public Drug(
                String drugID,     
                String drugName,     
                ArrayList<String> indicationUsage,
                BigDecimal cost,
                HashMap<String, String> availability,
                ArrayList<String> aContraindication,
                ArrayList<String> rContraindication,
                ArrayList<String> interaction,
                String drugForm,
                ArrayList<String> comorbidity
            ){
                this.drugID = drugID;
                this.drugName = drugName;
                this.indicationUsage = indicationUsage;
                this.cost = cost;
                this.availability = availability;
                this.aContraindication = aContraindication;
                this.rContraindication = rContraindication;
                this.interaction = interaction;
                this.drugForm = drugForm;
                this.comorbidity = comorbidity;
            }

            public String getDrugID(){
                return this.drugID;
            }
    

            public String toString(){
                return this.drugID; 
            }

            public boolean canTreat(String diagnosis){
                boolean cantreat = false;
                if(indicationUsage.contains(diagnosis)){
                    cantreat = true;
                }
                //for(String s: indicationUsage){
                //}
                return cantreat; 
            }


                
                /**
                 * Get drugName.
                 *
                 * @return drugName as String.
                 */
                public String getDrugName()
                {
                    return drugName;
                }
                
                /**
                 * Get cost.
                 *
                 * @return cost as BigDecimal.
                 */
                public BigDecimal getCost()
                {
                    return cost;
                }
                
                /**
                 * Get drugForm.
                 *
                 * @return drugForm as String.
                 */
                public String getDrugForm()
                {
                    return drugForm;
                }

                public ArrayList<String> getIndicationUsage(){
                    return indicationUsage;
                }

                public HashMap<String,String> getAvailability(){
                    return availability;
                }

        public ArrayList<String> getAContraindication(){
                            return aContraindication;
                        }


        public ArrayList<String> getRContraindication(){
                            return rContraindication;
                        }

        public ArrayList<String> getInteraction(){
                            return interaction;
                        }


        public ArrayList<String> getComorbidity(){
                            return comorbidity;
                        }
}


