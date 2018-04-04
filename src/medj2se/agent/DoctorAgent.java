package medj2se.agent;

import java.sql.SQLException;
import medj2se.database.DBConnection;
import medj2se.models.Drug;
import medj2se.models.Patient;
import medj2se.models.CaseBaseManager;
import medj2se.models.CaseBase;
import medj2se.helpers.Helper;
import medj2se.helpers.CreateSerializableDrugs;
import medj2se.helpers.CreateSerializablePatient;
import medj2se.helpers.AgentCreation;
import java.util.*;
import java.math.BigDecimal;

import jade.core.Agent;
import jade.core.AID;
import jade.core.Location;
import jade.domain.mobility.MoveAction;
import jade.content.onto.basic.Action;
import jade.domain.mobility.MobileAgentDescription;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.content.ContentElement;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;

import jade.domain.mobility.MobilityOntology;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Result;

public class DoctorAgent extends Agent 
  { 
     String rationale;
     private String myLocalName;
     ArrayList<Drug> drugList=null;
     Patient patient=null;
     String patientID;
     String diagnosis;
     String radioChoice;
     String mechanismActionCode;
     String pharmacoKineticsCode;
     String physiologicCode;
     String therapeuticCode;
     boolean CaseBaseReasoner = false;
    //private AID controller;
    ThreadedBehaviourFactory tbf = null;

      protected void setup() 
      { 

        tbf = new ThreadedBehaviourFactory();

        //Register the language and the ontology - IMPORTANT for moving agent
		getContentManager().registerLanguage(new SLCodec());
		getContentManager().registerOntology(MobilityOntology.getInstance());

        //Register the Database Agent in the yellow pages
     	DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID() );
		ServiceDescription sd= new ServiceDescription();
		sd.setType("doctorAgent-manager");
		sd.setName("doctor-App");
		dfd.addServices(sd);
		try{

			DFService.register(this,dfd);
		}
		catch(FIPAException fe) {
			//fe.printStackTree();
		}

        myLocalName = getLocalName();
        //Object args []  = new Object[1];
        //controller = (AID) args[0];

          System.out.println("My name is "+ myLocalName); 
          //Behaviour to respond to request from Phone Agent
          addBehaviour(new ServingRequest());
          //preProcessor();
      }
  
  //This is the inner class to deal with the request from agent on Phone 
	private class ServingRequest extends CyclicBehaviour{
        //String alternatives ="";
		public void action(){
       		MessageTemplate mt = MessageTemplate.
                MatchPerformative(ACLMessage.REQUEST);

			ACLMessage msg=myAgent.receive(mt);
			if(msg != null){
				String theRequest=msg.getContent();
                System.out.println("This message was received: "+theRequest);
				ACLMessage reply= msg.createReply();
			        setPatientDiagnosis(theRequest);	
                    reply.setPerformative(ACLMessage.INFORM);
                    //addBehaviour(new ReceiveDrug(myAgent));
                    addBehaviour(new ReceivePatient(myAgent));
                    addBehaviour(new ReceiveDrug(myAgent));
                   
                   if(CaseBaseReasoner == false ){
                        processMobileAgentPatient(patientID);
                        addBehaviour(new CheckForAlternatives(myAgent,reply));
                        
                   } 
                    if( CaseBaseReasoner == true){
                        processMobileAgents(patientID,diagnosis);
                        addBehaviour(new CheckForModels(myAgent,reply));
                    }
                   //if(drugList != null && patient != null ){ 
                        ////preProcessor();
                        //alternatives = fromArrayListToString();
                   //}else{
                        //System.out.println("The drug List and or patient object(s) is or are null!");
                   //}
                   ////while (drugList == null && patient == null) {
                       //System.out.println("Waiting for druglist and patient to be updated....");
                   //}
                    //alternatives = fromArrayListToString();
                    //alternatives = "Drug1 Drug2 Drug3";
                //reply.setContent("This was alternatives but removed to another behaviour");
                 //myAgent.send(reply);
			}else{
				block();
			}		
		}
	} //End of inner class
 

//------cyclic behaviour to recevie Drug 
    class ReceiveDrug extends CyclicBehaviour {
	public ReceiveDrug(Agent a){
	    super(a);
	}
	public void action(){
       MessageTemplate mt = MessageTemplate.
                MatchPerformative(ACLMessage.AGREE);
        ACLMessage msg = receive(mt);
	    if(msg==null){block();return;}
	    try{
		Object content = msg.getContentObject();
		switch (msg.getPerformative()) {
		case(ACLMessage.AGREE):
		   //System.out.println("handling request from " + msg.getSender().getLocalName());
           System.out.println("I have received something from drug");
		   if(content instanceof CreateSerializableDrugs )
		       addBehaviour(new HandleCreateDrugs(myAgent,msg));
		   break;
		default:
		    System.out.println("message not understood");
		}
	    }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
	}//End action
    }//------End ReceiveMessages



    //---------Cyclic Behaviour to check for patient and drug --------
    class CheckForModels extends CyclicBehaviour{

        boolean ALLERGIES = true;
        boolean ACONTRAINDICATION = true;
        boolean COMORBIDITY = true;
        boolean INTERACTION = true;

        String alternatives ="";
        ACLMessage reply;
        public CheckForModels(Agent a, ACLMessage reply){
            super(a);
            this.reply = reply;
        }
       public void action(){
            if(drugList != null && patient != null) {
                //RUN RULES HERE
            System.out.println("CheckForModels: drugList=NOT NULL and patient = NOT NULL");
            if(ALLERGIES){
            ArrayList<Drug> toRemove = new ArrayList<Drug>();
            for(Drug drug: drugList){
                if(patient.isAllergic(drug)){
                        toRemove.add(drug);
                }
            }  
                drugList.removeAll(toRemove);
            }

            
            if(COMORBIDITY){
            System.out.println("Entered Comorbidity...checking");
            ArrayList<Drug> toRemove = new ArrayList<Drug>();
                for(Drug drug: drugList){
                    if(patient.isComorbid(drug)){
                        toRemove.add(drug);
                        System.out.print("Remove Drug: ");
                        System.out.println(drug);
                    }
                }
                drugList.removeAll(toRemove);
            }



        if(INTERACTION){
            ArrayList<Drug> toRemove = new ArrayList<Drug>();
            for(Drug drug: drugList){
                if(patient.isInteract(drug)){
                        toRemove.add(drug);
                    }
                }
                drugList.removeAll(toRemove);
        }



                alternatives = fromArrayListToString();
                System.out.println("This is from CheckForModels: " + alternatives);
                System.out.println("I am sending this reply to doctor agent now....");
                this.reply.setContent("Rules-Result:!"+alternatives);
 				myAgent.send(this.reply);

                //block();
                myAgent.removeBehaviour(this);
            }
       } //End Action method
    }//End CheckFor Models


//---------Cyclic Behaviour to check for drug using Case-Based --------
    class CheckForAlternatives extends CyclicBehaviour{
        String alternatives ="";
        ACLMessage reply;
        public CheckForAlternatives(Agent a, ACLMessage reply){
            super(a);
            this.reply = reply;
        }
       public void action(){
            if(patient != null && drugList == null ) {

                patient.setCurrentDiagnosis(diagnosis);
                ArrayList<CaseBase> mcb =  CaseBaseManager.matchCases(patient,"CaseStore");
                //TODO:NEED TO GET ONLY THE BEST MATCH CASE.FOR NOW WILL TAKE FIRST 
                for(CaseBase c: mcb){
                    //FIXME: fix here for case based reasoner
                    drugList = c.getDrugsTaken();
                    alternatives = fromArrayListToString();
                    break;
                }
                if (alternatives == null || alternatives == ""){
                        processMobileAgentDrug(diagnosis);
                        addBehaviour(new CheckForModels(myAgent,reply));
                }else{
                    System.out.println("This is from CheckForAlternatives: " + alternatives);
                    System.out.println("CaseBase: I am sending this reply to doctor agent now....");
                    this.reply.setContent("Case-Based-Result:!"+alternatives);
                    myAgent.send(this.reply);
                    //block();
               }
                myAgent.removeBehaviour(this);
            }
       } //End Action method
    }//End CheckForAlternatives




   //------cyclic behaviour to recevie Drug 
    class ReceivePatient extends CyclicBehaviour {
	public ReceivePatient(Agent a){
	    super(a);
	}
	public void action(){
       MessageTemplate mt = MessageTemplate.
                MatchPerformative(ACLMessage.CONFIRM);
        ACLMessage msg = receive(mt);
        
	    if(msg==null){block();return;}
        System.out.println("I have recived the MESSAGE........");
	    try{
		Object content = msg.getContentObject();
		switch (msg.getPerformative()) {
		case(ACLMessage.CONFIRM):
		   //System.out.println("handling request from " + msg.getSender().getLocalName());
           System.out.println("I have received something from patient");
		   if(content instanceof CreateSerializablePatient )
		       addBehaviour(new HandleCreatePatient(myAgent,msg));
		   break;
		default:
		    System.out.println("message not understood");
		}
		
	    }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
	}//End action
    }//------End ReceiveMessages



    //behaviour to hanndle models creation
    class HandleCreateDrugs extends OneShotBehaviour {
	private ACLMessage request;
    	
	HandleCreateDrugs(Agent a, ACLMessage request){
	    this.request=request;
	}//End Constructor
	
	public void action(){
	    try{
            CreateSerializableDrugs csm = (CreateSerializableDrugs) request.getContentObject();
            
            if(csm.isDrugSet()){
                drugList = csm.getDrugList();
            }


            for(Drug d: drugList){
                System.out.println(d);
            }

            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM_IF);
            reply.setContent("updated");
            send(reply);
	    }catch (Exception ex){
            ex.printStackTrace();
	    }
	}//End action
}//End class HandleCreateTemperature


//behaviour to hanndle models creation
    class HandleCreatePatient extends OneShotBehaviour {
	private ACLMessage request;
    	
	HandleCreatePatient(Agent a, ACLMessage request){
	    this.request=request;
	}//End Constructor
	
	public void action(){
	    try{
            CreateSerializablePatient csm = (CreateSerializablePatient) request.getContentObject();
            
            if(csm.isPatientSet()){
                patient = csm.getPatient();
                System.out.println("I am printing the patient received");
                System.out.println(patient);
                System.out.println("Did you receive the patient?");
            }else{
                System.out.println("The Patient was not set.");
            }

            System.out.println(patient);
            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM_REF);
            reply.setContent("updated");
            send(reply);
	    }catch (Exception ex){
            ex.printStackTrace();
	    }
	}//End action
}//End class HandleCreateTemperature


  private void setPatientDiagnosis(String patient_diagnosis ){
      String [] results = patient_diagnosis.split(" ");
      this.patientID = results[0];
      this.diagnosis = results[1];
      this.radioChoice = results[2];
      this.mechanismActionCode = results[3];
      this.pharmacoKineticsCode = results[4];
      this.physiologicCode = results[5];
      this.therapeuticCode = results[6];
  }

   private void processMobileAgents(String patientID, String diagnosis){

       AgentCreation ac = new AgentCreation();
       Object args []= new Object[3];
       args[0]=patientID;
       args[1]=myLocalName;
       args[2]=getAID();

       Object args1 []= new Object[8];
       args1[0]=diagnosis;
       args1[1]=myLocalName;
       args1[2]=getAID();
       args1[3] = this.radioChoice;
       args1[4]=this.mechanismActionCode;
       args1[5]=this.pharmacoKineticsCode;
       args1[6]=this.physiologicCode;
       args1[7]=this.therapeuticCode;

       ac.createAgent("PatientDBAgent1",PatientDBAgent.class.getName(),args);
       ac.createAgent("DrugDBAgent1",DrugDBAgent.class.getName(),args1);
       Location pDBloc = findServerLocation("PatientDBServer"); 
       Location dDBloc = findServerLocation("DrugDBServer"); 
       //moveAgent(pDBloc,"PatientDBAgent1"); 
       //FIXME:moving agent to PatientDBServer gives error
       moveAgent(dDBloc,"PatientDBAgent1"); 
       moveAgent(dDBloc,"DrugDBAgent1"); 
   }


private void processMobileAgentPatient(String patientID){

       AgentCreation ac = new AgentCreation();
       Object args []= new Object[3];
       args[0]=patientID;
       args[1]=myLocalName;
       args[2]=getAID();

       ac.createAgent("PatientDBAgent1",PatientDBAgent.class.getName(),args);
       Location dDBloc = findServerLocation("DrugDBServer"); 
       //moveAgent(pDBloc,"PatientDBAgent1"); 
       //FIXME:moving agent to PatientDBServer gives error
       moveAgent(dDBloc,"PatientDBAgent1"); 
   }


private void processMobileAgentDrug(String diagnosis){

       AgentCreation ac = new AgentCreation();
       
       Object args1 []= new Object[8];
       args1[0]=diagnosis;
       args1[1]=myLocalName;
       args1[2]=getAID();
       args1[3]=this.radioChoice;
       args1[4]=this.mechanismActionCode;
       args1[5]=this.pharmacoKineticsCode;
       args1[6]=this.physiologicCode;
       args1[7]=this.therapeuticCode;

       ac.createAgent("DrugDBAgent1",DrugDBAgent.class.getName(),args1);
       Location dDBloc = findServerLocation("DrugDBServer"); 
       //moveAgent(pDBloc,"PatientDBAgent1"); 
       //FIXME:moving agent to PatientDBServer gives error
       moveAgent(dDBloc,"DrugDBAgent1"); 
   }

    private Location findServerLocation(String docLocation) {
		Location loc = null;
		try{
		// Get available locations with AMS
		 sendRequest(new Action(getAMS(), new QueryPlatformLocationsAction()));
		//Receive response from AMS
         MessageTemplate mt = MessageTemplate.and(
			                  MessageTemplate.MatchSender(getAMS()),
			                  MessageTemplate.MatchPerformative(ACLMessage.INFORM));
         ACLMessage resp = blockingReceive(mt);
         ContentElement ce = getContentManager().extractContent(resp);
         Result result = (Result) ce;
         jade.util.leap.Iterator it = result.getItems().iterator();
		 
		 while (it.hasNext()) {
             
			 loc = (Location)it.next();
				if(docLocation.equals(loc.getName()) ) { 
					System.out.println("THIS IS THE LCOATION NAME: " +loc.getName());
					break;
				}

		 }
			}catch(Exception e){System.out.println("Problem communicating with AMS");}
		return loc;
	}//End findAPLocation


    private void moveAgent (Location loc,  String mAgent) {
            System.out.println("I am in the moveAgent Method");
            System.out.println("Location " +loc);
            System.out.println("Agent  " + mAgent);
			AID aid = new AID(mAgent,AID.ISLOCALNAME);
 		 MobileAgentDescription mad = new MobileAgentDescription();
         mad.setName(aid);
         mad.setDestination(loc);
         MoveAction ma = new MoveAction();
         ma.setMobileAgentDescription(mad);
         sendRequest(new Action(aid, ma));
	}//End moveAgent

	private void sendRequest(Action action) {
    	ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
      	request.setLanguage(new SLCodec().getName());
      	request.setOntology(MobilityOntology.getInstance().getName());
      	try {
	     getContentManager().fillContent(request, action);
	     request.addReceiver(action.getActor());
	     send(request);
		 System.out.println("IN SENDREQUEST METHOD AT THE END");
	  	}
	  catch (Exception ex) { ex.printStackTrace(); }
   }


    private ArrayList<Drug> getDrugsCanTreat(ArrayList<Drug> drugList, String diagnosis){
            ArrayList<Drug> treatDrugs = new ArrayList<Drug>();
            for(Drug d: drugList){
                if(d.canTreat(diagnosis)){
                    treatDrugs.add(d);
                }
            }
            return treatDrugs;
    }

    //Test Method
     private void preProcessor(){
        diagnosis = "DIAG001";
        Drug d1=null;
        Drug d2=null;
        Drug d3=null;
        Drug d4=null;
        Drug d5=null;
        Drug d6=null;
        Patient patient=null;
        DBConnection db = new DBConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/emr_ndf","root","");
        try{
            d1 = db.getDrug("N0000145918");
            d2 = db.getDrug("N0000146784");
            d3 = db.getDrug("N0000146005");
            d4 = db.getDrug("N0000146109");
            d5 = db.getDrug("N0000145898");
            d6 = db.getDrug("N0000146109");
            patient = db.getPatient("P011");
        }catch(SQLException e){}

        ArrayList<Drug> drugList1 = Helper.createArrayList(d1,d2,d3,d4);
        ArrayList<Drug> drugList = getDrugsCanTreat(drugList1, diagnosis);
        
        //=======================Test Case Base Reasonsing========================
        //TODO:PLEASE REMEMBER THAT THIS DRUG LIST WILL BE THE DRUGS TAKEN!!!
        //ArrayList<String> drug_list = Helper.createArrayList("DRUG001");
        //ArrayList<Drug> drug_list = Helper.createArrayList(d1,d2,d3,d4);
        ArrayList<Drug> drug_list = Helper.createArrayList(d6);
        CaseBase cb = CaseBaseManager.createCase(patient,drug_list,"Excellent");
        cb.setCurrentDiagnosis("N0000003812");
        CaseBaseManager.saveCase(cb,"CaseStore");
        patient.setCurrentDiagnosis("N0000003812");
        ArrayList<CaseBase> mcb =  CaseBaseManager.matchCases(patient,"CaseStore");
        for(CaseBase c : mcb){
            System.out.println(c); 
        }//endfor
        //=========================End Test Case Base Reasoning=======================

        boolean ALLERGIES = true;
        boolean ACONTRAINDICATION = true;
        boolean COMORBIDITY = true;
        boolean INTERACTION = true;


        if(ALLERGIES){
          ArrayList<Drug> toRemove = new ArrayList<Drug>();
           for(Drug drug: drugList){
               if(patient.isAllergic(drug)){
                    toRemove.add(drug);
               }
           }  
            drugList.removeAll(toRemove);
        }

        
        if(COMORBIDITY){
          ArrayList<Drug> toRemove = new ArrayList<Drug>();
            for(Drug drug: drugList){
                if(patient.isComorbid(drug)){
                    toRemove.add(drug);
                }
            }
            drugList.removeAll(toRemove);
        }



       if(INTERACTION){
          ArrayList<Drug> toRemove = new ArrayList<Drug>();
           for(Drug drug: drugList){
               if(patient.isInteract(drug)){
                    toRemove.add(drug);
                }
               }
            drugList.removeAll(toRemove);
       }

     for(Drug drug: drugList){
        System.out.println(drug);
     }
}

    public String fromArrayListToString(){
        String stringDrugList="";
        int counter=0;
        if(this.drugList == null){}else{
            for(Drug d: this.drugList){
                if(counter != drugList.size()-1){
                stringDrugList = stringDrugList + d.getDrugName() + "!";
                }else{
                    stringDrugList = stringDrugList + d.getDrugName(); 
                }
                counter++;
            }//endfor
        }//endif

        return stringDrugList;
    }
}


