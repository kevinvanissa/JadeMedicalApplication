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
import medj2se.models.Patient;
import medj2se.models.Drug;
import medj2se.helpers.CreateSerializablePatient;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.*;
import jade.core.AID;

public class PatientDBAgent extends Agent {
    private String patientID; 
    private String senderLocalName;
    private Patient patient;
    private Location destination;
    private AID controller;

    public void setup(){

        //Register the language and the ontology
		getContentManager().registerLanguage(new SLCodec());
		getContentManager().registerOntology(MobilityOntology.getInstance());

        Object [] args = getArguments();
        patientID = (String) args[0];
        senderLocalName = (String) args[1];
        controller = (AID) args[2];

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
                    System.out.println("YEP...I got the move request this is PatientDBAgent...");

					ContentElement content = getContentManager().extractContent(msg);
					Concept concept = ((Action)content ).getAction();

					if(concept instanceof MoveAction ){
						MoveAction ma = (MoveAction)concept;
						Location l = ma.getMobileAgentDescription().getDestination();
						if(l != null) doMove(destination=l);
						
					}
				}catch (Exception ex){ ex.printStackTrace();}

			}else {System.out.println("Unexpected msg from controller agent says PatientDBAgent"); }
		}//action
	}//End ReceiveCommands


//Inner Class to send the Patient Model 
	private class RequestPreProcess extends Behaviour{
		private int step = 0;
		private MessageTemplate mt;
		private int repliesCnt=0;
		private String doctorAgentName;
		
		public void action(){
			switch(step){
				case 0:

                   CreateSerializablePatient csm = new CreateSerializablePatient();                    
                   csm.setPatient(patient);
					ACLMessage inform = new ACLMessage(ACLMessage.CONFIRM);
                    try{
                        inform.setContentObject((java.io.Serializable) (Object) csm);
                    }catch(Exception e){e.printStackTrace();}
                    
					inform.addReceiver(new AID(senderLocalName, AID.ISLOCALNAME));
                    inform.setConversationId("models-data");
					inform.setReplyWith("inform" + System.currentTimeMillis() );
					myAgent.send(inform);
                    System.out.println("I am sending the patient model......");
					//prepare template to get message
					mt= MessageTemplate.and(MessageTemplate.MatchConversationId("models-data"),
							MessageTemplate.MatchInReplyTo(inform.getReplyWith()));
					step=1;
					break;
				case 1:
					//Receive all replies from doctor agent
					ACLMessage reply = myAgent.receive(mt);
					if(reply != null){
						if(reply.getPerformative() == ACLMessage.INFORM_REF){
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
		System.out.println("PatientDB: I am moving now.....");
	}

    protected void afterMove(){
    System.out.println("I Have moved Successfully PatientDBAgent");
    DBConnection db = new DBConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/emr_ndf","root","");
    try{ 
        patient = db.getPatient(patientID);
    }catch(SQLException e){}
        addBehaviour(new RequestPreProcess());
    }
}
