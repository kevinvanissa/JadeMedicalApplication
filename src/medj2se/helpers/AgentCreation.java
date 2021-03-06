package medj2se.helpers;

import jade.core.Agent;
import jade.util.leap.Properties;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;

public class AgentCreation{
    AgentController alphaAgent=null;

	public void createAgent(String agentNickName,String agentClassName,Object [] agentArgs) {
        try{
            //Get a hold of Jade-Runtime
            Runtime rt = Runtime.instance();
			//Exit JVM when there are no more containers around
            rt.setCloseVM(true);
            //Create a default profile
            Profile p = new ProfileImpl(false);
            //Create a new non-main container
            AgentContainer ac = rt.createAgentContainer(p);
            //Create a new agent
            alphaAgent = ac.createNewAgent(agentNickName,agentClassName,agentArgs);
            //start the agent
            alphaAgent.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }//End createSubDB

    private void killAlphaAgent(){
        if(alphaAgent != null){
            try{
                alphaAgent.kill();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }//Endif
      }//End killSubDB

}//End Class
