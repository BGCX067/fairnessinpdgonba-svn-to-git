package fairnessandpdgba;

import java.awt.geom.Arc2D.Float;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.context.space.graph.*;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.space.graph.Network;
import repast.simphony.context.space.graph.*;

  @SuppressWarnings("unchecked")
  public class LivingWorld extends DefaultContext implements ContextBuilder<player>{
	//Here we will first construct the world which the agent lives in it.
	//first creates the context, then add the agent 
	//then creates a small world complex network
	//after the agents' complex interaction network is done,
	//the model will random schedule the agent's step method
	private ArrayList agentlist= new ArrayList();
	private int actionNumber;
	private final  int stop=3000;
	private int numberCooperation;
	private int numberDefection;
	
	public Context build(Context<player> context){
		Parameters p = RunEnvironment.getInstance().getParameters();
		int num = (Integer)p.getValue("number of agent");
		int length = (Integer)p.getValue("neighbor size");
		double wsProbability=(Double)p.getValue("WS probability");
		actionNumber = (Integer)p.getValue("number of agent in one step");
		numberCooperation= (Integer)p.getValue("number of initial cooperation");
		numberDefection=num-numberCooperation;
		int agentID=0;
        agentlist.clear();
		//add the agent to the context
		for(int i=0;i<num;i++ ){
			//System.out.println("creat new agent");
			player agent=new player(); 
			agent.setID(i);
			context.add(agent);
			agentlist.add((Object)agent);
			agent.setCurrentStrategy('D');
		}

		//build WS network 
		NetworkGenerator gen= new WattsBetaSmallWorldGenerator(wsProbability,length,false);
		NetworkBuilder builder=new NetworkBuilder("Living world",context,false);
		builder.setGenerator(gen);
		Network net = builder.buildNetwork();
		System.out.println("the network creation is done!!!");
        System.out.println(" the network degree    "+net.getDegree());
        
//        NetworkFactory factory=NetworkFactoryFinder.createNetworkFactory(new HashMap());
//		Network net=factory.createNetwork("Living world",context,false);		
//		System.out.println("begin creat network");
//		
//		int m = (Integer)p.getValue("initial Random Network node number");
//		System.out.println("initial Random Network node number       "+m);
//		int m0=(Integer)p.getValue("number of added adge each step");
//		System.out.println("number of added adge each step       "+m0);
//		ArrayList addedList= new ArrayList();
//		ArrayList<Double> probList= new ArrayList();
//		//construct the scale free network
//		//construct the random network first
//		for(int i=0;i<m;i++){
//			addedList.add(i, agentlist.get(i));
//			//System.out.println(" add agent to addlist");
//		}
//		
//		for(int i=0;i<m;i++){   //Here we can simply use net.isAdjacent(addedList.get(i), addedList.get(j)) and net.addEdge
//			                             // and it works,but in repast GUI, reset will show some error sometimes!!!
//			for(int j=0;j<m;j++){
//			   int mark=0;
//				if(i==j)continue;
//			   Iterator it=net.getNodes().iterator();
//			   while(it.hasNext()){
//				  player agent=(player) it.next();
//			      if((addedList.get(i).equals(agent))&&(addedList.get(j).equals(agent))){
//			    	  mark=1;
//			    	  break;
//			      }
//			   }
//				  if (mark==1){
//					  if (!(net.isAdjacent(addedList.get(i), addedList.get(j))))
//						  net.addEdge(addedList.get(i),addedList.get(j));
//					  else continue;
//				  }
//				  else {
//					  net.addEdge(addedList.get(i),addedList.get(j));
//			         // System.out.println(" add agent to addlist");
//			    }
//		   }
//		}
//		
//		//System.out.println("Randdom Network built");
//		
//		for(int i=m;i<num;i++){
//			 //System.out.println("add edge  for agent  "+i+"  begin");
//			 HashSet addedSet=new HashSet();
//			 //addedSet.addAll(addedList);
//			 probList.clear();
//			 addedSet.clear();
//			 int sumDegree=0;
//	    		for(int h=0;h<addedList.size();h++){
//	    			sumDegree+=net.getDegree(addedList.get(h));
//	    		}
//			 for(int j=0;j<addedList.size();j++){
//				double addProb=(double)net.getDegree(addedList.get(j))/(sumDegree);
//				//System.out.println("add prob   "+addProb+"   "+net.getDegree(addedList.get(j))+"      "+sumDegree);
//			 	probList.add(addProb);
//			 }
//			 //System.out.println("list size    "+addedSet.size());
//			// System.out.println("added size    "+addedList.size());
//			
//			int step=m0;
//			while(step>0){ // add m0 edges when a new node added
//				double random=RandomHelper.nextDoubleFromTo(0,1);
//				player agent=(player) agentlist.get(i);	
//				//System.out.println("addset clear    "+ random);
//				double prob=probList.get(0);
//				int l=0;
//				while(l<=(addedList.size())-1){
//				    	if(random<=prob){
//				    		if(!addedSet.contains(addedList.get(l))){
//				    			net.addEdge(agent, addedList.get(l));
//				    			addedSet.add(addedList.get(l));
//					    		//System.out.println("add an edge    "+step + "    for agent "+i+"   to  " +l );
//					    		step-=1;
//					    		probList.clear();
//					    		sumDegree=0;
//					    		for(int h=0;h<addedList.size();h++){
//					    			sumDegree+=net.getDegree(addedList.get(h));
//					    		}
//					    		for(int g=0;g<addedList.size();g++){
//									double addProb1=(double)net.getDegree(addedList.get(g))/(sumDegree);
//									probList.add(addProb1);
//					    		}
//				    	}
//				    		else{
//				    			//System.out.println("try to add to an existing node");
//				    		}
//				    		break;
//				    	}
//				    	else {
//				    		l++;
//							prob+=probList.get(l);
//							//System.out.println("choose another one");
//							continue;
//				    	}
//				   }
//			 }
//			addedList.add(i,(agentlist.get(i)));	
//			//System.out.println("add edge  for agent  "+i+"complete");
//			}
//		
////		for(int i=0;i<net.size();i++){
////			System.out.println("the degree of node   "+i+"     is   "+net.getDegree(agentlist.get(i)));
////		}
////		
//		System.out.println("SF network is built     "+net.getDegree()+"   size   "+net.size());		
		
		//set the initial status
		int numbertemp=0;
		while( numbertemp<numberCooperation){
			RandomHelper.createUniform();
			int random=RandomHelper.nextIntFromTo(0, num-1);
			if (((player)agentlist.get(random)).getCurrentStrategy()=='D')
			           	{
				           ((player)agentlist.get(random)).setCurrentStrategy('C');
				           numbertemp++;
			           	}
			else continue;
		}
		
//		for(int j=0;j<agentlist.size();j++){
//			System.out.println("agent   "+((player)agentlist.get(j)).getID()+"    "+((player)agentlist.get(j)).getCurrentStrategy());
//		}
		
		int numSocialPreference = (Integer)p.getValue("number of social preference agent");
		int sum=0;
		
		while(sum<numSocialPreference){
		    RandomHelper.createUniform();
			int random=RandomHelper.nextIntFromTo(0, num-1);
		    if (!((player)agentlist.get(random)).isIfSocialPreference()){
		    ((player)agentlist.get(random)).setIfSocialPreference(true);
		    sum++;
		    }
		}
		  
		System.out.println("set the agent which is the social preference type  is done!!!");
		// for(int k=0;k<agentlist.size();k++){
	   	    	//System.out.println("PayoffCC is set to "+((player)agentlist.get(k)).getPayoffOfCC()+"   ID is   "
	   	    			//+((player)agentlist.get(k)).getID()) ;
	   	    	//System.out.println("step   ");
	   	  //  }
		return context;
	  }
	
	@ScheduledMethod(start=0,interval=1)
	  public void step(){
 	    Parameters p = RunEnvironment.getInstance().getParameters();
      actionNumber = (Integer)p.getValue("number of agent in one step");
      player[] runlist= new player[actionNumber];
      int num = (Integer)p.getValue("number of agent");
      Object[] alist=new Object[num];
      alist=this.toArray();
      for(int i=0;i<alist.length;i++){
      	agentlist.add(alist[i]);
      }
             
      int j=0;
 	    while(j<actionNumber){
 	    	int RunID=RandomHelper.nextIntFromTo(0, num-1);
 	    	ArrayList RunIDList = new ArrayList();
 	    	player temp=(player)agentlist.get(RunID);
	        if (!RunIDList.contains(RunID)){
	        	RunIDList.add(RunID);
	           	runlist[j]=temp;
	            j+=1;
	        }
	    }
 	    
 	    for(int k=0;k<runlist.length;k++){
 	    	((player)runlist[k]).step1();
 	    }
 	    
  	 for(int k=0;k<runlist.length;k++){
  		 ((player)runlist[k]).step2();
	    }
 	    
 	   for(int k=0;k<runlist.length;k++){
 		((player)runlist[k]).postStep();
    }
 	   
 }
	
	@ScheduledMethod(start=stop)
	public void end(){
		try {
			FileWriter  fwresult = new FileWriter("./SimulationDataOne.txt",true);
			BufferedWriter bwresult = new BufferedWriter(fwresult);
          PrintWriter pwresult= new PrintWriter(bwresult);
          numberCooperation=0;
          numberDefection=0;
          
          Parameters p = RunEnvironment.getInstance().getParameters();
           int num = (Integer)p.getValue("number of agent");
//          Object[] alist=new Object[num];
//          alist=this.toArray();
//          for(int i=0;i<alist.length;i++){
//          	agentlist.add(alist[i]);
//          }
          
          for(int i=0;i<agentlist.size();i++){
              if(((player) (agentlist.get(i))).getChoosedStrategy()=='C')numberCooperation++;
              else numberDefection++;
          }
          
          double payoffOfDC=(Double)p.getValue("payoffOfDC");
          int numberOfSocialAgent=(Integer) p.getValue("number of social preference agent");
          double alpha=(Double)p.getValue("alpha of social preference function");
          int initialNodeNumber = (Integer)p.getValue("initial Random Network node number");
          int addedNumber=(Integer)p.getValue("number of added adge each step");
        //  double wsProbability=(Double)p.getValue("WS probability");
        //  int degree=(Integer)p.getValue("neighbor size");
          pwresult.print("       ");
          pwresult.printf("%.15f",((double)(numberCooperation)/(numberCooperation+numberDefection)));
          pwresult.print("       ");
          pwresult.printf("%.15f",((double)(numberDefection)/(numberCooperation+numberDefection)));
          pwresult.print("       ");
          pwresult.printf("%.1f",(RunEnvironment.getInstance()).getCurrentSchedule().getTickCount()); 
          pwresult.print("       ");
          pwresult.printf("%f",payoffOfDC); 
          pwresult.print("       ");
          pwresult.printf("%d",numberOfSocialAgent); 
          pwresult.print("       ");
//          pwresult.printf("%d",degree); 
//          pwresult.print("       ");
//          pwresult.printf("%f",wsProbability); 
//          pwresult.print("       ");
          pwresult.printf("%d", initialNodeNumber); 
          pwresult.print("       ");
          pwresult.printf("%f",addedNumber); 
          pwresult.print("       ");
          pwresult.printf("%f",alpha); 
          pwresult.print("       ");
           pwresult.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	   System.out.println("finished write to the file");
		(RunEnvironment.getInstance()).endAt(stop);
	}
}