/************************************************************************** 
 * Copyright 2011 - 2018
 *
 * University of Minho 
 * 
 * This is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This code is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Public License for more details. 
 * 
 * You should have received a copy of the GNU Public License 
 * along with this code. If not, see http://www.gnu.org/licenses/ 
 *  
 * Created by Orlando Rocha inside the BIOSYSTEMS Group (https://www.ceb.uminho.pt/BIOSYSTEMS)
 */
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;

import pt.ornrocha.logutils.MTULogUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.Reaction;
import pt.uminho.ceb.biosystems.mew.core.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.core.model.components.enums.ReactionType;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.solvers.builders.GLPKBinSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.Environment;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.BooleanValue;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.VariableSignValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.InvalidRegulatoryModelException;

public class BooleanRegulatoryModelSolver {
	
	private IOptfluxRegulatoryModel regmodel;
	private ISteadyStateModel integratedmodel;
	private EnvironmentalConditions envconds;
	private IntegratedNetworkInitialStateContainer variablescontainer;
	private Map<String, Double> objfunct;
	
	private IndexedHashMap<String, Boolean> initialgenestates;
	private IndexedHashMap<String, Boolean> initialvarstates;
	

	private IndexedHashMap<String, Boolean> finalgenestates;

	private IndexedHashMap<String, Boolean> currentmetabolitestates;
	private IndexedHashMap<String, Boolean> currentdrainreactions;
	
	private ArrayList<String> internalreactions;
	private IndexedHashMap<String, Boolean> currentinternalreactionstatesmap;
	
	protected IndexedHashMap<String, Boolean> possibleTfsLinkedTogenes=null;
    protected IndexedHashMap<String, Boolean> nextvariablesbooleanstate= null;
    protected boolean usertfsinitstate=false;
	private String solver=GLPKBinSolverBuilder.ID;
	
	boolean debug=false;
	
	public BooleanRegulatoryModelSolver(IIntegratedStedystateModel integratedmodel,EnvironmentalConditions envconds, IndexedHashMap<String, Boolean> genestate, IndexedHashMap<String, Boolean> varstate) throws Exception{
		this.integratedmodel=integratedmodel;
		((IIntegratedStedystateModel)this.integratedmodel).useLogicalModelNetworkFormat(false);
		this.regmodel=(IOptfluxRegulatoryModel) integratedmodel.getRegulatoryNetwork();
		this.envconds=envconds;
		this.initialgenestates=genestate;
		this.initialvarstates=varstate;
		this.variablescontainer=integratedmodel.getIntegratedVariablesContainerWihoutValidation().copy();
		if(regmodel!=null){
		   setupDefaultObjctiveFunction();
		   checkTfsWithGeneLink();
		}
		else
			throw new InvalidRegulatoryModelException("This method does not support the current regulaory model");
		
	}
	
	protected void setupDefaultObjctiveFunction(){
		this.objfunct=new HashMap<String, Double>();
		objfunct.put(this.integratedmodel.getBiomassFlux(), 1.0);
	}
	
	public void setObjectiveFunction(Map<String, Double> objfunct){
		if(objfunct!=null)
			this.objfunct=objfunct;
	}
	
	 public void setSolver(String solver){
	    	this.solver=solver;
	    }


	public IndexedHashMap<String, Boolean> getGeneBooleanStates(){
		return finalgenestates;
	}
	
	public IndexedHashMap<String, Boolean> getRegulatoryVariablesBooleanStates(){
		return nextvariablesbooleanstate;
	}


	
	protected void checkTfsWithGeneLink(){
		possibleTfsLinkedTogenes=new IndexedHashMap<>();
		IndexedHashMap<String, RegulatoryRule> rules=regmodel.getRegulatoryRules();
		
		for (int i = 0; i < rules.size(); i++) {
			String geneid=rules.getKeyAt(i);
			String ruleid=rules.getValueAt(i).getRuleId();
			
			if(ruleid!=geneid){
				boolean state=false;
				
				if(initialgenestates.containsKey(geneid))
					state=initialgenestates.get(geneid);
				else if(initialvarstates.containsKey(ruleid))
					state=initialvarstates.get(ruleid);

				possibleTfsLinkedTogenes.put(ruleid, state);
				
			}
		}
    
	}
	
  
	protected void checkvariablesstateinmodel(){
		
		if(variablescontainer!=null){
			
			if(variablescontainer.getNumberOfMetabolitesInBothModels()>0){

				IndexedHashMap<String, Integer> metabvariables=variablescontainer.getMetaboliteVariablesPresentInBothModels();
				this.currentmetabolitestates=new IndexedHashMap<>();
				
				for (int i = 0; i < metabvariables.size(); i++) {
					String metabid=metabvariables.getKeyAt(i);
					
					int drainreactmetabindex=variablescontainer.getIndexMetabolicReactionForVariable(metabid);
					
					if(drainreactmetabindex!=-1){
						String reactid=integratedmodel.getReactionId(drainreactmetabindex);
					    ReactionConstraint reactionconst=integratedmodel.getReactionConstraint(drainreactmetabindex);
					    if(envconds.containsKey(reactid))
					    	reactionconst=envconds.get(reactid);
					    
					    double lowerlimit=reactionconst.getLowerLimit();
			    	    if(lowerlimit<0)
			    		   currentmetabolitestates.put(metabid, true);
			    	    else
			    		   currentmetabolitestates.put(metabid, false);
			          }
				}
			}
	
			if(variablescontainer.getNumberOfReactionsInBothModels()>0){
				
				IndexedHashMap<String, Integer> reactions=variablescontainer.getReactionVariablesPresentInBothModels();
               
				for (int i = 0; i < reactions.size(); i++) {
					
					String reactionid=reactions.getKeyAt(i);
					RegulatoryVariable var=regmodel.getRegulatoryVariable(reactionid);
					
					if(var.getVariableSign()!=null && (var.getVariableSign().equals(VariableSignValue.LESS) || var.getVariableSign().equals(VariableSignValue.GREATER)))
						reactionid=var.getSimpleName();
	                 
					/*if(reactionid.contains("<") || reactionid.contains(">"))
						reactionid=reactionid.split("[<>]")[0].trim();*/
					
					Reaction reaction=integratedmodel.getReaction(reactionid);

					if(reaction!=null && reaction.getType().equals(ReactionType.DRAIN)){
					   
						//if(reactions.getKeyAt(i).contains(">")){
						if(var.getVariableSign().equals(VariableSignValue.GREATER)){
						  if(currentdrainreactions==null)
							 currentdrainreactions=new IndexedHashMap<>();
					
							
							ReactionConstraint reactionconst=integratedmodel.getReactionConstraint(reactionid);
							if(envconds.containsKey(reactionid))
								reactionconst=envconds.get(reactionid);
							
							if(reactionconst!=null){
								double lowerlimit=reactionconst.getLowerLimit();
								
								double regconst=Double.parseDouble(var.getSignValue());
	    						if(lowerlimit<regconst)
	    							currentdrainreactions.put(reactions.getKeyAt(i), true);
	    						else
	    							currentdrainreactions.put(reactions.getKeyAt(i), false);
								
							}
						}
						
					}
					else if(reaction!=null && reaction.getType().equals(ReactionType.INTERNAL)){
						if(internalreactions==null)
							internalreactions=new ArrayList<>();
						internalreactions.add(reactionid);	
					}
					
				}
				
			}
		}
	}
	
	
	
	public void run() throws Exception{
		checkvariablesstateinmodel();
		checkTfsWithGeneLink();
		SteadyStateSimulationResult fbasimul=null;
		try {
		    fbasimul=performMetabolicSimulation(getMetabolicGeneKnockouts());
	     } catch (Exception e) {
	    	 LogMessageCenter.getLogger().addCriticalErrorMessage("Error in RFBA metabolic simulation: ", e);
	     }
		
		if(internalreactions!=null)
		  setStateOfMetabolicReactions(fbasimul);
		
		setRegulatoryVariablesStateMap();
		evaluate();

	}
	
	protected ArrayList<String> getMetabolicGeneKnockouts(){
		ArrayList<String> res=new ArrayList<>();
		for (int i = 0; i < initialgenestates.size(); i++) {
			String genename=initialgenestates.getKeyAt(i);
			boolean state=initialgenestates.getValueAt(i);	
			
			if(((IIntegratedStedystateModel) integratedmodel).isMetabolicGene(genename) && !state){
				res.add(genename);
			}
		}
		return res;
	}
	
	
	protected void setStateOfMetabolicReactions(SteadyStateSimulationResult simulation){
		
		LPSolutionType soltype=LPSolutionType.INFEASIBLE;
		if(simulation!=null)
		    soltype=simulation.getSolutionType();
		currentinternalreactionstatesmap=new IndexedHashMap<>();
		
		if(soltype.equals(LPSolutionType.FEASIBLE) || soltype.equals(LPSolutionType.OPTIMAL)){
			
			FluxValueMap fluxvalues=simulation.getFluxValues();
			
			for (int i = 0; i < internalreactions.size(); i++) {
				String reactid=internalreactions.get(i);
				
				RegulatoryVariable var=regmodel.getRegulatoryVariable(reactid);

				if(var!=null && var.getVariableSign()!=null && (var.getVariableSign().equals(VariableSignValue.LESS) || var.getVariableSign().equals(VariableSignValue.GREATER)))
					reactid=var.getSimpleName();

				if(fluxvalues.containsKey(reactid)){
					double value=fluxvalues.get(reactid);
					//System.out.println(internalreactions.get(i)+" --> "+reactid+" --> "+value);
					if(value == 0.0)
						currentinternalreactionstatesmap.put(internalreactions.get(i), false);
					else
						currentinternalreactionstatesmap.put(internalreactions.get(i), true);
				}
				else
					currentinternalreactionstatesmap.put(internalreactions.get(i), false);
			}
			
			
		}
		else{
			
			for (int i = 0; i < internalreactions.size(); i++) {
				currentinternalreactionstatesmap.put(internalreactions.get(i), false);
			}
		}
		
		
	}
	
	protected  SteadyStateSimulationResult performMetabolicSimulation(ArrayList<String> geneknockouts) throws Exception{
		
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Performing metabolic simualtion with knockouts: "+geneknockouts);
		
		GeneChangesList metabgenesknockoutslist = new GeneChangesList(geneknockouts);
		GeneticConditions metabolicgenesknockouts =new GeneticConditions(metabgenesknockoutslist ,(ISteadyStateGeneReactionModel) integratedmodel, false);
		SimulationSteadyStateControlCenter simulationControlCenter = new SimulationSteadyStateControlCenter(envconds,metabolicgenesknockouts, integratedmodel,SimulationProperties.FBA);
		simulationControlCenter.setFBAObj(objfunct);
		simulationControlCenter.setMaximization(true);
		simulationControlCenter.setSolver(solver);
		return simulationControlCenter.simulate();
	}
	
	
	
	protected Environment<IValue> createEnvironment() {

		Environment<IValue> environment = new Environment<IValue>();

		
		
		if(possibleTfsLinkedTogenes!=null)
		   for (int i = 0; i <possibleTfsLinkedTogenes.size(); i++) {
			 environment.associate(possibleTfsLinkedTogenes.getKeyAt(i), new BooleanValue(possibleTfsLinkedTogenes.getValueAt(i)));
		   }
		
		for (int i = 0; i < initialvarstates.size(); i++) {
			environment.associate(initialvarstates.getKeyAt(i), new BooleanValue(initialvarstates.getValueAt(i)));
		}
		
		for (int i = 0; i < initialgenestates.size(); i++) {
			environment.associate(initialgenestates.getKeyAt(i), new BooleanValue(initialgenestates.getValueAt(i)));
		}


		return environment;
	}
	
	
	protected void evaluate() {
		Environment<IValue> environment =createEnvironment();
		finalgenestates=new IndexedHashMap<>();
		
		IndexedHashMap<String, RegulatoryRule> generules=regmodel.getRegulatoryRules();
		
		for (int i = 0; i < generules.size(); i++) {
			String geneid=generules.getKeyAt(i);
			RegulatoryRule rule=generules.getValueAt(i);
			
			if (rule.getBooleanRule().getRootNode() != null && !rule.getRule().isEmpty()) {
				
				boolean value = (Boolean)rule.getBooleanRule().evaluate(environment).getValue();
				finalgenestates.put(geneid, value);

			}
			else
				finalgenestates.put(geneid, true);
			
		}
		

	}
	
	protected void setRegulatoryVariablesStateMap(){
		
		IndexedHashMap<String, RegulatoryVariable> regulatoryvariables=regmodel.getVariablesInRegulatoryNetwork();
        this.nextvariablesbooleanstate= new IndexedHashMap<>();
   
        for (int i = 0; i < regulatoryvariables.size(); i++) {
        	String varid=regulatoryvariables.getKeyAt(i);
        	RegulatoryVariable var=regulatoryvariables.getValueAt(i);
 
        	boolean state=false;
        	
        	if(var.getType().equals(RegulatoryModelComponent.TRANS_FACTOR_ID)){

        		if(possibleTfsLinkedTogenes.containsKey(varid))
        			state=possibleTfsLinkedTogenes.get(varid);
        		else if(initialvarstates.containsKey(varid))
        			state=initialvarstates.get(varid);
        		else
        			state=usertfsinitstate;
        	}
        	else if(var.getType().equals(RegulatoryModelComponent.METABOLITE_ID)){
        		if(currentmetabolitestates!=null && currentmetabolitestates.containsKey(varid))
        			state=currentmetabolitestates.get(varid);
        	}
        	else if(var.getType().equals(RegulatoryModelComponent.REACTION_ID) || var.getType().equals(RegulatoryModelComponent.ENV_CONDITION_ID)){
        		if(currentdrainreactions!=null && currentdrainreactions.containsKey(varid))
        			state=currentdrainreactions.get(varid);
        		else if(currentinternalreactionstatesmap!=null && currentinternalreactionstatesmap.containsKey(varid)){
        			state=currentinternalreactionstatesmap.get(varid);	
        		}
        		else if(initialvarstates.containsKey(varid))
        			state=initialvarstates.get(varid);
        		/*else if(var.getType().equals(RegulatoryModelComponent.ENV_CONDITION_ID) && var.isIndependentvariable() && initialvarstates.containsKey(varid))
        			state=initialvarstates.get(varid);*/
        	}
        	else if(var.getType().equals(RegulatoryModelComponent.GENE_ID) && !initialgenestates.containsKey(varid)){
        		state=usertfsinitstate;
        		
        	}
        	
        	MTULogUtils.addDebugMsgToClass(this.getClass(), "Variable: "+varid+" ---> "+state);
        	
        	nextvariablesbooleanstate.put(varid,state);
		}
        
		MTULogUtils.addDebugMsgToClass(this.getClass(), "Initial Regulatory variables State Map: {}",nextvariablesbooleanstate);
	}
	
	public Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> getBooleanSolutionOfGenesAndVariables(boolean onlygenes){
		if(onlygenes)
			return new Pair<IndexedHashMap<String,Boolean>, IndexedHashMap<String,Boolean>>(finalgenestates, null);
		else
			return new Pair<IndexedHashMap<String,Boolean>, IndexedHashMap<String,Boolean>>(finalgenestates, nextvariablesbooleanstate);
	
	}
	
	public static Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> solve(IIntegratedStedystateModel integratedmodel, EnvironmentalConditions envconds,IndexedHashMap<String, Boolean> genestate, IndexedHashMap<String, Boolean> varstate, boolean returnonlygenestate, Map<String, Double> objfunct) throws Exception{
		BooleanRegulatoryModelSolver solver=new BooleanRegulatoryModelSolver(integratedmodel,envconds, genestate,varstate);
		solver.setObjectiveFunction(objfunct);
		solver.run();
		return solver.getBooleanSolutionOfGenesAndVariables(returnonlygenestate);
	}
	
	public static Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> solve(IIntegratedStedystateModel integratedmodel, EnvironmentalConditions envconds,IndexedHashMap<String, Boolean> genestate, IndexedHashMap<String, Boolean> varstate, boolean returnonlygenestate, Map<String, Double> objfunct, String solvertype) throws Exception{
		BooleanRegulatoryModelSolver solver=new BooleanRegulatoryModelSolver(integratedmodel,envconds, genestate,varstate);
		solver.setObjectiveFunction(objfunct);
		solver.setSolver(solvertype);
		solver.run();
		
		return solver.getBooleanSolutionOfGenesAndVariables(returnonlygenestate);
	}
	
	
}
