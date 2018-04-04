package medj2se.wardcontainers;

import java.util.*;
import java.io.*;

import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.core.*;
import jade.domain.*;
import jade.domain.JADEAgentManagement.*;
import jade.content.lang.sl.*;
import jade.domain.mobility.*;


public class ContainerCreation  {
// --------------------------------------------

   private jade.wrapper.AgentContainer home;
   private jade.wrapper.AgentContainer[] container = null;

   // Get a JADE Runtime instance
   jade.core.Runtime runtime = jade.core.Runtime.instance();

   protected void setup() {
// ------------------------
      try {
         // Create the container objects
	ProfileImpl p = new ProfileImpl(); 
    p.setParameter(jade.core.Profile.MAIN,"false");
    p.setParameter(jade.core.Profile.MAIN_HOST,"localhost");
	//p.setParameter(jade.core.Profile.MAIN_HOST,"72.252.108.220");
	p.setParameter(jade.core.Profile.MAIN_PORT,"1099");
//	home = runtime.createAgentContainer(new ProfileImpl(false));
         container = new jade.wrapper.AgentContainer[3];
         for (int i = 1; i < 4; i++){
	      p.setParameter(jade.core.Profile.CONTAINER_NAME,"Ward"+i);
	      container[0] = runtime.createAgentContainer(p);
	     }
         p.setParameter(jade.core.Profile.CONTAINER_NAME,"PatientDBServer");
         container[0] = runtime.createAgentContainer(p);
         p.setParameter(jade.core.Profile.CONTAINER_NAME,"DrugDBServer");
         container[0] = runtime.createAgentContainer(p);
	  }
	  catch (Exception e) { e.printStackTrace(); }
   }

	public static void main(String args []) {
		ContainerCreation containercreation = new ContainerCreation();	
		containercreation.setup();
	}
}//class Controller
