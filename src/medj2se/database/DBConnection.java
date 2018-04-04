package medj2se.database;

import javax.sql.rowset.serial.SerialArray;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.math.BigDecimal;

import medj2se.models.Drug;
import medj2se.models.Patient;

import java.util.*;

public class DBConnection {
    String DRIVER=null;
    String DATABASE_URL;
    String DBUSER;
    String PASSWORD;
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;   

   public DBConnection(
           String DRIVER, String DATABASE_URL,String DBUSER,
           String PASSWORD   
           ){
                this.DRIVER = DRIVER;
                this.DATABASE_URL = DATABASE_URL;
                this.DBUSER = DBUSER;
                this.PASSWORD = PASSWORD;
           }

            public void getConnection(){
                try{
                    Class.forName(this.DRIVER).newInstance();
                    this.con = DriverManager.getConnection(this.DATABASE_URL,this.DBUSER,
                            this.PASSWORD);
                    if(!this.con.isClosed()){
                        System.out.println("Successfully connected to mysql database via TCP/IP...");
                    }
                    this.st=con.createStatement(); 
                }catch(Exception e){
                    System.err.println("Exception "+e.getMessage());
                }
            } 


           public Patient getPatient(String patientID) throws SQLException{
               getConnection();
               String fname="";
               String lname="";
               Date dob=null;
               String sex="";
               String maritalStatus="";
               String bloodGroup="";
               ArrayList<String> allergies=new ArrayList<String>();
               ArrayList<String> secondaryDiagnosis=new ArrayList<String>();
               ArrayList<String> primaryDiagnosis=new ArrayList<String>();
               HashMap<String,String> familyHistory=new HashMap<String,String>();
               ArrayList<String> currentMedication=new ArrayList<String>();
               ArrayList<String> currentSymptoms=new ArrayList<String>();
               ArrayList<String> socialHistory= new ArrayList<String>();
               HashMap<String,String> vitalsHistory=new HashMap<String,String>();

               Patient patient=null;

               String sqlGeneral = "select * from Patient where patient_ID='"+patientID+"'";
               String sqlAllergies = "select drug_code from Patient_Allergies_log where patient_ID='"+patientID+"'";
               String sqlSecondaryDiagnosis = "select diagnosis_code from Patient_Diagnosis_log where is_current='YES' AND patient_ID='"+patientID+"'";
               String sqlCurrentMedication = "select drug_code from Patient_Medication_log where is_current='YES' AND patient_ID='"+patientID+"'";
               try{
                    ResultSet rs = st.executeQuery(sqlGeneral); 
                    while(rs.next()){
                        fname = rs.getString("fname");
                        lname = rs.getString("lname");
                        dob = rs.getDate("dob");
                        maritalStatus = rs.getString("maritalStatus");
                        bloodGroup = rs.getString("bloodGroup");
                    }
                        
                    ResultSet rs1 = st.executeQuery(sqlAllergies);
                    while(rs1.next()){
                        allergies.add(rs1.getString("drug_code"));
                    }

                    ResultSet rs2 = st.executeQuery(sqlSecondaryDiagnosis);
                    while(rs2.next()){
                        secondaryDiagnosis.add(rs2.getString("diagnosis_code"));
                    }

                    ResultSet rs5 = st.executeQuery(sqlCurrentMedication);
                    while(rs5.next()){
                        currentMedication.add(rs5.getString("drug_code"));
                    }

               }catch(SQLException e){
                   e.printStackTrace();
               }
                    
            return new Patient(
          patientID, fname,lname,dob,
          sex,maritalStatus,bloodGroup,allergies,secondaryDiagnosis,
          primaryDiagnosis,familyHistory,vitalsHistory,currentMedication,
          currentSymptoms,socialHistory);
     
           }
            
       public Drug getEmptyDrug(){
               String drugID="NO DRUG FOUND";
               String drugName="NO DRUG FOUND";
               BigDecimal cost = null;
               HashMap<String, String> availability=new HashMap<String,String>();
               ArrayList<String> aContraindication=new ArrayList<String>();
               ArrayList<String> rContraindication=new ArrayList<String>();
               ArrayList<String> interaction=new ArrayList<String>();
               String drugForm="";
               ArrayList<String> comorbidity=new ArrayList<String>();
               ArrayList <String> indicationUsage = new ArrayList<String>();

            return new Drug(drugID,drugName,indicationUsage,cost,availability,
                        aContraindication,rContraindication,interaction,
                        drugForm,comorbidity);
       }//Drug


        public Drug getDrug(String drugID) throws SQLException{
               getConnection();
               String drugName="";
               BigDecimal cost = null;
               HashMap<String, String> availability=new HashMap<String,String>();
               ArrayList<String> aContraindication=new ArrayList<String>();
               ArrayList<String> rContraindication=new ArrayList<String>();
               ArrayList<String> interaction=new ArrayList<String>();
               String drugForm="";
               ArrayList<String> comorbidity=new ArrayList<String>();
    
               Drug drug =null;

               String sqlGeneral = "select * from Drug where drug_code='"+drugID+"'";
               String sqlaContraindication = "select diagnosis_code from ContraIndication where drug_code='"+drugID+"'";
               String sqlInteraction = "select other_drug_code from DrugInteraction where drug_code='"+drugID+"'";

               try{
                    ResultSet rs = st.executeQuery(sqlGeneral); 
                    while(rs.next()){
                         drugName = rs.getString("drug_type_name");
                    }
                        
                    ResultSet rs3 = st.executeQuery(sqlaContraindication);
                    while(rs3.next()){
                        aContraindication.add(rs3.getString("diagnosis_code"));
                    }

                    ResultSet rs5 = st.executeQuery(sqlInteraction);
                    while(rs5.next()){
                        interaction.add(rs5.getString("other_drug_code"));
                    }


               }catch(SQLException e){
                   e.printStackTrace();
               }
                       //FIXME:temporary fix for indicationUsage
                       ArrayList <String> indicationUsage = new ArrayList<String>();
                       //indicationUsage.add("DIAG001");
           
                return new Drug(drugID,drugName,indicationUsage,cost,availability,
                        aContraindication,rContraindication,interaction,
                        drugForm,comorbidity);
           }

    public ArrayList<Drug> getDrugListForDiagnosis(String diagnosis_code) throws SQLException{
        getConnection();
        ArrayList<Drug> drugList = new ArrayList<Drug>();
        String sql = "select drug_code from IndicationUsage where diagnosis_code='"+diagnosis_code+"'";
        try{
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                drugList.add(this.getDrug(rs.getString("drug_code")));
            }
                    
        }catch(SQLException e){
                   e.printStackTrace();
               }
        return drugList; 
    }

    public ArrayList<Drug> processOption(ArrayList<Drug> drugList,int optionIndicator,String optionCode,String radioChoice) throws SQLException{
        ArrayList<Drug> processedDrugList = new ArrayList<Drug>();
    
       switch(optionIndicator){
          case 1: 
              System.out.println("In processOption switch case 1");
               processedDrugList = helpProcessOptions(drugList,"MechanismActionInDrugs",optionCode,"mechanism_code",radioChoice);
               break;
          case 2:
               processedDrugList = helpProcessOptions(drugList,"PharmacoKineticsInDrugs",optionCode,"pharmaco_code",radioChoice);
               break;
          case 3:
               processedDrugList = helpProcessOptions(drugList,"PhysiologicEffectInDrugs",optionCode,"physiologic_code",radioChoice);
               break;
          case 4:
               processedDrugList = helpProcessOptions(drugList,"TherapeuticCategoryInDrugs",optionCode,"therapeutic_code",radioChoice);
               break;
          default:
               System.out.println("Error: I should have never reached here:DBConnection.java - Switch statement default");
       }

        if(processedDrugList.isEmpty()){
            processedDrugList.add(this.getEmptyDrug());  
        }

        return processedDrugList;
    }

    private ArrayList<Drug> helpProcessOptions(ArrayList<Drug> drugList,String tableName, String optionCode,String columnName,String radioChoice) throws SQLException{
        getConnection();
        String sql;
        System.out.println("In helpProcessOptions for: "+tableName);
        System.out.println("radioChoice: "+radioChoice);
        ArrayList<Drug> newDrugList = new ArrayList<Drug>();
        for(Drug d: drugList){
           if(radioChoice.equals("NO")){
                sql = "select * from "+tableName+" where drug_code='"+d+"' and "+columnName+"<>'"+optionCode+"'"; 
            }else{
            
                sql = "select * from "+tableName+" where drug_code='"+d+"' and "+columnName+"='"+optionCode+"'"; 
            }
           System.out.println("Query: "+sql);
        try{
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                newDrugList.add(d);
                System.out.print("I have added the drug: ");
                System.out.println(d);
                break;
            }
        }catch(SQLException e){
                   e.printStackTrace();
               }
        }//EndFor

        if(newDrugList.isEmpty()){
            System.out.println("The DrugList is now empty");
        }

        return newDrugList;
    }
}
