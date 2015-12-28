package fairnessandpdgba;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class player {
	private int ID;
	private char currentStrategy;
	private char choosedStrategy;
	private double currentSocialPreferencePayoff;
	private double alpha;
	private double payoffOfCC;
	private double payoffOfCD;
	private double payoffOfDC;
	private double payoffOfDD;
	private boolean isIfSocialPreference=false;
	private int isCooperate=0;
	double defectPayoff;
	double currentPayoff;
	double defectSocialPayoff;
	double fairPayoffFromCooperation;
	double fairPayoffFromDefection;
	double noise;
	
	public player( ){
		//initialize  agent  parameter
		Parameters p = RunEnvironment.getInstance().getParameters();
        this.payoffOfCC=(Double)p.getValue("payoffOfCC");
		this.payoffOfCD=(Double)p.getValue("payoffOfCD");
		this.payoffOfDC=(Double)p.getValue("payoffOfDC");
		this.payoffOfDD=(Double)p.getValue("payoffOfDD");
		this.alpha = (Double)p.getValue("alpha of social preference function");
		this.currentStrategy='N';
		this.noise=(Double)p.getValue("noise");
	}
		
	public double singlePlayCooperation(player agent1,player agent2){
		if (agent2.currentStrategy=='D'){
			//System.out.println("payoff of CD  is  "+payoffOfCD);
			return payoffOfCD;
		}
		else 	{
			//System.out.println("payoff of CC  is  "+payoffOfCC);
			return payoffOfCC;
		    }
		}
	
	public double singlePlayDefection(player agent1,player agent2){
		if (agent2.currentStrategy=='D'){
		//System.out.println("payoff of DD  is  "+payoffOfDD);		
		return payoffOfDD;
		       }
		else{ 	
			//System.out.println("payoff of DC  is  "+payoffOfDC);		
			return payoffOfDC;
		    }
		}
	
   public double computeKindnessToNeighbor_Cooperation(player NeighboringAgent){
	   double kindnessToNeighbor=0;
	   if(((player)NeighboringAgent).getCurrentStrategy()=='C'){
	 	   kindnessToNeighbor=(payoffOfCC-(double)1/2*(payoffOfCC+payoffOfCD))/(payoffOfCC-payoffOfCD);
	   }
		   else if (((player)NeighboringAgent).getCurrentStrategy()=='D'){
			   kindnessToNeighbor=(payoffOfDC-(double)1/2*(payoffOfDC+payoffOfDD))/(payoffOfDC-payoffOfDD);
		   }
	      // System.out.println(" Kindness to Neighbor      "+kindnessToNeighbor);
	   	   return kindnessToNeighbor;
	      }
    
   public double computeKindnessToNeighbor_Defection(player NeighboringAgent){
	   double kindnessToNeighbor=0;
	   if(((player)NeighboringAgent).getCurrentStrategy()=='C'){
		   kindnessToNeighbor=(payoffOfCD-(double)1/2*(payoffOfCD+payoffOfCC))/(payoffOfCC-payoffOfCD);
	   }
		   else if (((player)NeighboringAgent).getCurrentStrategy()=='D'){
			   kindnessToNeighbor=(payoffOfDD-(double)1/2*(payoffOfDD+payoffOfDC))/(payoffOfDC-payoffOfDD);
		   }
	      //System.out.println(" Kindness to Neighbor      "+kindnessToNeighbor);
	   	   return kindnessToNeighbor;
	    }
	  
   public double computeKindnessOfNeighbor_Cooperation(player NeighboringAgent){
	   double kindnessOfNeighbor=0;
	   if(((player)NeighboringAgent).getCurrentStrategy()=='C'){
		   kindnessOfNeighbor=(payoffOfCC-(double)1/2*(payoffOfCD+payoffOfCC))/(payoffOfCC-payoffOfCD);
	   }
		   else if (((player)NeighboringAgent).getCurrentStrategy()=='D'){
			   kindnessOfNeighbor=(payoffOfCD-(double)1/2*(payoffOfCD+payoffOfCC))/(payoffOfCC-payoffOfCD);
		   }
	     //System.out.println(" Kindness to Neighbor      "+kindnessOfNeighbor);
	   	   return kindnessOfNeighbor;
   }
   
   
   public double computeKindnessOfNeighbor_Defection(player NeighboringAgent){
	   double kindnessOfNeighbor=0;
	   if(((player)NeighboringAgent).getCurrentStrategy()=='C'){
		   kindnessOfNeighbor=(payoffOfDC-(double)1/2*(payoffOfDD+payoffOfDC))/(payoffOfDC-payoffOfDD);
	   }
		   else if (((player)NeighboringAgent).getCurrentStrategy()=='D'){
			   kindnessOfNeighbor=(payoffOfDD-(double)1/2*(payoffOfDD+payoffOfDC))/(payoffOfDC-payoffOfDD);
		   }
     	   //System.out.println(" Kindness to Neighbor      "+kindnessOfNeighbor);
	   	   return kindnessOfNeighbor;
	    }
   
   public double computeFairnessPayoffOfCooperation(){
	   //double utilityMaterial=0;
	   //double utilityFairness=0;
	   double utility=0;
	   LivingWorld context = (LivingWorld) ContextUtils.getContext(this);
	   Network network=(Network)context.getProjection("Living world");
	   Iterable neighbors=network.getAdjacent(this); 
      	for (Object o : neighbors) {
      		utility+=singlePlayCooperation(this, ((player)(o)))+
      		(alpha+(1-alpha)*computeKindnessOfNeighbor_Cooperation((player)(o)))*(1+computeKindnessToNeighbor_Cooperation((player)(o)));
       		//utilityMaterial+=singlePlayCooperation(this, ((player)(o)));
       		//utilityFairness+=	(alpha+(1-alpha)*computeKindnessOfNeighbor_Cooperation((player)(o)))*(1+computeKindnessToNeighbor_Cooperation((player)(o)));
  		//System.out.println("computeKindnessOfNeighbor_Cooperation    "+computeKindnessOfNeighbor_Cooperation((player)(o))
		//		+"    computeKindnessToNeighbor_Cooperation    "+computeKindnessToNeighbor_Cooperation((player)(o))
		//				+"    utility  "+utility +"   this strategy  "
	//				+this.getCurrentStrategy()+"   opponent strategy   "+((player)(o)).getCurrentStrategy());

      	}
      	      	//utility=Math.pow(utilityMaterial,1)+utilityFairness;
                //System.out.println("total  the fairness payoff of Cooperation is    "+utility);
      	     	return utility;
   }
   
   public double computeFairnessPayoffOfDefection(){
	   //double utilityMaterial=0;
	  // double utilityFairness=0;
	   double utility=0;
	   LivingWorld context = (LivingWorld) ContextUtils.getContext(this);
	   Network network=(Network)context.getProjection("Living world");
	   Iterable neighbors=network.getAdjacent(this);  
      	for (Object o : neighbors) {
      		utility+=singlePlayDefection(this,((player)(o)))+
      		(alpha+(1-alpha)*computeKindnessOfNeighbor_Defection((player)(o)))*(1+computeKindnessToNeighbor_Defection((player)(o)));
      		//utilityMaterial+=singlePlayDefection(this,((player)(o)));
      		//utilityFairness+=(alpha+(1-alpha)*computeKindnessOfNeighbor_Defection((player)(o)))*(1+computeKindnessToNeighbor_Defection((player)(o)));
	//      			System.out.println("computeKindnessOfNeighbor_Defection    "+computeKindnessOfNeighbor_Defection((player)(o))
    //		     				+"     computeKindnessToNeighbor_Defection    "+computeKindnessToNeighbor_Defection((player)(o))
	//					+"    utility  "+utility +"   this strategy  "
  	//					+this.getCurrentStrategy()+"   opponent strategy   "+((player)(o)).getCurrentStrategy());
      	}
      	//utility=Math.pow(utilityMaterial, 1)+utilityFairness;
     //System.out.println(" total the fairness payoff of defection is    "+utility);
      	return utility;
   }
   
   public double computePayoffOfCooperation(){
	   double utility=0;
	   LivingWorld context = (LivingWorld) ContextUtils.getContext(this);
	   Network network=(Network)context.getProjection("Living world");
	   Iterable neighbors=network.getAdjacent(this);  
      	for (Object o : neighbors) {
       		utility+=singlePlayCooperation(this,((player)(o)));
       		}
      	return utility;
   }
   
   public double computePayoffOfDefection(){
	   double utility=0;
	   LivingWorld context = (LivingWorld) ContextUtils.getContext(this);
	   Network network=(Network)context.getProjection("Living world");
	   Iterable neighbors=network.getAdjacent(this);  
      	for (Object o : neighbors) {
       		utility+=singlePlayDefection(this,((player)(o)));
       		}
      	return utility;
   }
   
   public void computeRealPayoff(){
	   if (this.getCurrentStrategy()=='C'){
		   currentPayoff=computePayoffOfCooperation();
	   }
	   else  currentPayoff=computePayoffOfDefection();
	   
   }
   public void computePayoff(){
	   if (isIfSocialPreference()){
	   fairPayoffFromCooperation=computeFairnessPayoffOfCooperation();
	   fairPayoffFromDefection=computeFairnessPayoffOfDefection();
      }
	   else{
		   computeRealPayoff();
		   }
	   }                                               
   
   public void makeChoice(){
	 double probability;
	 RandomHelper.init();
     double randomProb=RandomHelper.nextDoubleFromTo(0, 1);
//     probability=(double)1/(1+Math.exp((fairPayoffFromDefection-fairPayoffFromCooperation)));
     if (isIfSocialPreference()){
     probability=(double)1/(1+Math.exp(noise*(fairPayoffFromDefection-fairPayoffFromCooperation)));
    // System.out.println(" probability   "+probability);
//      if (randomProb<=probability) {
//	    	//System.out.println("    random strategy  is  "+random.getCurrentStrategy());
//	    	this.choosedStrategy='C'; 
//	    }
//      else this.choosedStrategy='D';
	 if (fairPayoffFromCooperation>fairPayoffFromDefection)
	   this.choosedStrategy='C';
	   else if (fairPayoffFromCooperation<fairPayoffFromDefection)choosedStrategy='D';
	   else choosedStrategy=currentStrategy;
     }
     else {
    	player  random;
     	RandomHelper.init();
     	randomProb=RandomHelper.nextDoubleFromTo(0, 1);
        LivingWorld context = (LivingWorld) ContextUtils.getContext(this);
 	    Network network=(Network)context.getProjection("Living world");
         random=(player)network.getRandomAdjacent(this);
         double ramdomNeighborayoff=random.getCurrentPayoff();
         probability=(double)1/(1+Math.exp(noise*(this.getCurrentPayoff()-ramdomNeighborayoff)));
 	    if (randomProb<=probability) {
	    	//System.out.println("    random strategy  is  "+random.getCurrentStrategy());
	    	this.choosedStrategy=random.getCurrentStrategy();
	    }
	    else{
	    	this.choosedStrategy=this.getCurrentStrategy();
	    	//System.out.println("strategy no changed");
	    }
     }
     System.out.println(" the Random  is    "+randomProb+"      the probability  is    "+probability);
     System.out.println("the choosed strategy  is    "+choosedStrategy+"    the payoff  of  D and C is   "+fairPayoffFromDefection+  "     "+
    		 fairPayoffFromCooperation);
     
 	if(this.choosedStrategy=='C'){
		isCooperate=1;
		//System.out.println("choosed Cooperation");
	}
	else {
		isCooperate=0;
		//System.out.println("choose defection");
	  }
   }
   
    public void step1() {
    	computePayoff();
    }
    
    public void step2() {
       makeChoice();
    }
    
    public void postStep(){
        this.currentStrategy=choosedStrategy;
    }

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param id the iD to set
	 */
	public void setID(int id) {
		ID = id;
	}

	/**
	 * @return the currentStrategy
	 */
	public char getCurrentStrategy() {
		return currentStrategy;
	}

	/**
	 * @param currentStrategy the currentStrategy to set
	 */
	public void setCurrentStrategy(char currentStrategy) {
		this.currentStrategy = currentStrategy;
	}

	/**
	 * @return the choosedStrategy
	 */
	public char getChoosedStrategy() {
		return choosedStrategy;
	}

	/**
	 * @param choosedStrategy the choosedStrategy to set
	 */
	public void setChoosedStrategy(char choosedStrategy) {
		this.choosedStrategy = choosedStrategy;
	}

	/**
	 * @return the currentSocialPreferencePayoff
	 */
	public double getCurrentSocialPreferencePayoff() {
		return currentSocialPreferencePayoff;
	}

	/**
	 * @param currentSocialPreferencePayoff the currentSocialPreferencePayoff to set
	 */
	public void setCurrentSocialPreferencePayoff(
			double currentSocialPreferencePayoff) {
		this.currentSocialPreferencePayoff = currentSocialPreferencePayoff;
	}

	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return the payoffOfCC
	 */
	public double getPayoffOfCC() {
		return payoffOfCC;
	}

	/**
	 * @param payoffOfCC the payoffOfCC to set
	 */
	public void setPayoffOfCC(double payoffOfCC) {
		this.payoffOfCC = payoffOfCC;
	}

	/**
	 * @return the payoffOfCD
	 */
	public double getPayoffOfCD() {
		return payoffOfCD;
	}

	/**
	 * @param payoffOfCD the payoffOfCD to set
	 */
	public void setPayoffOfCD(double payoffOfCD) {
		this.payoffOfCD = payoffOfCD;
	}

	/**
	 * @return the payoffOfDC
	 */
	public double getPayoffOfDC() {
		return payoffOfDC;
	}

	/**
	 * @param payoffOfDC the payoffOfDC to set
	 */
	public void setPayoffOfDC(double payoffOfDC) {
		this.payoffOfDC = payoffOfDC;
	}

	/**
	 * @return the payoffOfDD
	 */
	public double getPayoffOfDD() {
		return payoffOfDD;
	}

	/**
	 * @param payoffOfDD the payoffOfDD to set
	 */
	public void setPayoffOfDD(double payoffOfDD) {
		this.payoffOfDD = payoffOfDD;
	}

	/**
	 * @return the isIfSocialPreference
	 */
	public boolean isIfSocialPreference() {
		return isIfSocialPreference;
	}

	/**
	 * @param isIfSocialPreference the isIfSocialPreference to set
	 */
	public void setIfSocialPreference(boolean isIfSocialPreference) {
		this.isIfSocialPreference = isIfSocialPreference;
	}

	/**
	 * @return the isCooperate
	 */
	public int getIsCooperate() {
		return isCooperate;
	}

	/**
	 * @return the currentPayoff
	 */
	public double getCurrentPayoff() {
		return currentPayoff;
	}

	/**
	 * @param currentPayoff the currentPayoff to set
	 */
	public void setCurrentPayoff(double currentPayoff) {
		this.currentPayoff = currentPayoff;
	}

	/**
	 * @param isCooperate the isCooperate to set
	 */
	public void setIsCooperate(int isCooperate) {
		this.isCooperate = isCooperate;
	}

	/**
	 * @return the defectPayoff
	 */
	public double getDefectPayoff() {
		return defectPayoff;
	}

	/**
	 * @param defectPayoff the defectPayoff to set
	 */
	public void setDefectPayoff(double defectPayoff) {
		this.defectPayoff = defectPayoff;
	}

	/**
	 * @return the defectSocialPayoff
	 */
	public double getDefectSocialPayoff() {
		return defectSocialPayoff;
	}

	/**
	 * @param defectSocialPayoff the defectSocialPayoff to set
	 */
	public void setDefectSocialPayoff(double defectSocialPayoff) {
		this.defectSocialPayoff = defectSocialPayoff;
	}

	/**
	 * @return the fairPayoffFromCooperation
	 */
	public double getFairPayoffFromCooperation() {
		return fairPayoffFromCooperation;
	}

	/**
	 * @param fairPayoffFromCooperation the fairPayoffFromCooperation to set
	 */
	public void setFairPayoffFromCooperation(double fairPayoffFromCooperation) {
		this.fairPayoffFromCooperation = fairPayoffFromCooperation;
	}

	/**
	 * @return the fairPayoffFromDefection
	 */
	public double getFairPayoffFromDefection() {
		return fairPayoffFromDefection;
	}

	/**
	 * @param fairPayoffFromDefection the fairPayoffFromDefection to set
	 */
	public void setFairPayoffFromDefection(double fairPayoffFromDefection) {
		this.fairPayoffFromDefection = fairPayoffFromDefection;
	}
}