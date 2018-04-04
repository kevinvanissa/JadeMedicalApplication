package medj2se.agent;

import jade.core.Agent;

import jade.domain.mobility.MoveAction;
import jade.core.Location;
import jade.domain.mobility.MobilityOntology;
import jade.domain.mobility.MoveAction;
import jade.content.ContentElement;
import jade.content.Concept;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;

import java.sql.SQLException;
import medj2se.database.DBConnection;
import medj2se.models.Drug;
import medj2se.helpers.CreateSerializableDrugs;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.*;
import jade.core.AID;

import java.util.ArrayList;


public class DrugDBAgent extends Agent {
   private String diagnosis; 
   private String senderLocalName;
   private ArrayList<Drug> drugList;
   private Location destination;
   private AID controller;
   private String radioChoice;
   private String mechanismActionCode;
   private String pharmacoKineticsCode;
   private String physiologicCode;
   private String therapeuticCode;
   private final int MECHANISM=1;
   private final int PHARMACO=2;
   private final int PHYSIOLOGIC=3;
   private final int THERAPEUTIC=4;


    public void setup(){
       //Register the language and the ontology
		getContentManager().registerLanguage(new SLCodec());
		getContentManager().registerOntology(MobilityOntology.getInstance());

        Object [] args = getArguments();
        diagnosis = (String) args[0];
        senderLocalName = (String) args[1];
        controller = (AID) args[2];
        radioChoice = (String) args[3];
        mechanismActionCode = (String) args[4];
        pharmacoKineticsCode = (String) args[5];
        physiologicCode = (String) args[6];
        therapeuticCode = (String) args[7];
        
        addBehaviour(new ReceiveCommands(this));
    }

    //Receive commands from Doctor agent
	class ReceiveCommands extends CyclicBehaviour{
		ReceiveCommands(Agent a){
			super(a);
		}

		public void action(){
           //System.out.println("cyclic..."+myAgent.getCurQueueSize());
			ACLMessage msg = receive(MessageTemplate.MatchSender(controller));
			if(msg==null){ block(); return;}
			if(msg.getPerformative() == ACLMessage.REQUEST){
				try{
					ContentElement content = getContentManager().extractContent(msg);
					Concept concept = ((Action)content ).getAction();

					if(concept instanceof MoveAction ){
						MoveAction ma = (MoveAction)concept;
						Location l = ma.getMobileAgentDescription().getDestination();
						if(l != null) doMove(destination=l);
						
					}
				}catch (Exception ex){ ex.printStackTrace();}

			}else {System.out.println("Unexpected msg from controller agent says DrugDBAgent"); }
		}//action
	}//End ReceiveCommands


//Inner Class to send the Drug Model 
	private class RequestPreProcess extends Behaviour{
		private int step = 0;
		private MessageTemplate mt;
		private int repliesCnt=0;
		private String doctorAgentName;
		
		
		public void action(){
			switch(step){
				case 0:

                    CreateSerializableDrugs csm = new CreateSerializableDrugs();                    
                    csm.setDrugList(drugList);
					//Send the inform to the doctor agent
					ACLMessage inform = new ACLMessage(ACLMessage.AGREE);
                    try{
                        inform.setContentObject((java.io.Serializable) (Object) csm);
                    }catch(Exception e){e.printStackTrace();}
					inform.addReceiver(new AID(senderLocalName, AID.ISLOCALNAME));
					//inform.setContent("setModels");
					inform.setConversationId("models-data");
					inform.setReplyWith("inform" + System.currentTimeMillis() );
					myAgent.send(inform);
					//prepare template to get message
					mt= MessageTemplate.and(MessageTemplate.MatchConversationId("models-data"),
							MessageTemplate.MatchInReplyTo(inform.getReplyWith()));
					step=1;
					break;
				case 1:
					//Receive all replies from doctor agent
					ACLMessage reply = myAgent.receive(mt);
					if(reply != null){
						if(reply.getPerformative() == ACLMessage.INFORM_IF){
							String myalternatives = reply.getContent();
							System.out.println(" Reply Received "+ myalternatives);
							doctorAgentName = reply.getSender().getLocalName();
                            System.out.println("From "+ reply.getSender().getName() );
						}//End if
						repliesCnt++;	
						if(repliesCnt > 0){
							//we received all replies
							step=2;
						}

					}else{
						block();
					}
					break;
				default: 
					System.out.println("How the hell did I reach here?!");
					break;	
			}//End Switch
		}//End Action Method
		
		public boolean done(){
			return step == 2;
		}
		
	}
	
    protected void beforeMove(){
		System.out.println("DatabaseDB: I am moving now.....");
	}

    protected void afterMove(){
        System.out.println("I have moved successfully DrugDBAgent");
        DBConnection db = new DBConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/emr_ndf","root","");
       try{ 
        drugList = db.getDrugListForDiagnosis(diagnosis);
        if(drugList.isEmpty()){
            System.out.println("DrugList is empty after get diagnosis");
        }else{
            System.out.println("DrugList is NOT empty after get diagnosis. We found some drugs");
        }
 
        //Eliminate Drugs Here
        if(!(mechanismActionCode.equals("empty"))){
            drugList = db.processOption(drugList,MECHANISM,mechanismActionCode,radioChoice);
            if(drugList.isEmpty()){
                System.out.println("DrugList has become empty after Mechanism Action Code");
            }
        }  


        if(!(pharmacoKineticsCode.equals("empty"))){
            drugList = db.processOption(drugList,PHARMACO,pharmacoKineticsCode,radioChoice);
        }  


        if(!(physiologicCode.equals("empty"))){
            System.out.println("Entering PhysiologicCode in DrugDBAgent");
            drugList = db.processOption(drugList,PHYSIOLOGIC,physiologicCode,radioChoice);
        }  


        if(!(therapeuticCode.equals("empty"))){
            drugList = db.processOption(drugList,THERAPEUTIC,therapeuticCode,radioChoice);
        }


       }catch(SQLException e){}
        addBehaviour(new RequestPreProcess());
    }

}
