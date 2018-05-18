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
import java.util.HashSet;
import java.util.Map;

import org.javatuples.Pair;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.OptFluxRegulatoryNetworkMemory;

public class OptimizeRegulatoryModel {
	
	
	private IIntegratedStedystateModel integratedmodel;
	private Map<String, Boolean> initregulatoryvariablesstate;
	private Map<String, Double> objfunct;

	private EnvironmentalConditions envconds;
	private HashSet<String> knockoutgenes;
	private HashSet<String> currentgenewithtruestate;
	
	private ArrayList<String> indexallvariables;
	private ArrayList<String> geneids;
	private boolean onlygenecyclecheck=false;
	private boolean regulatorymodelTFinitstate=false;
	private boolean attractorisnotfound=true;
	private OptFluxRegulatoryNetworkMemory memory=null;
	private ArrayList<ArrayList<Boolean>> solution=null;
	private int cyclestart=0;
	private ArrayList<String> alwaysgeneoff;
	private SteadyStateSimulationResult fbasimul;
	private SolverType solver=SolverType.GLPK;
	
	private IndexedHashMap<String, Boolean> currentgenestate;
	private IndexedHashMap<String, Boolean> currentvarsstate;
    private RFBAOptimizationSolution optimizationsolution;

	

	public OptimizeRegulatoryModel(IIntegratedStedystateModel model,EnvironmentalConditions envconds, HashSet<String> inittruegenes, HashSet<String> knockoutgenes,Map<String, Boolean> initregulatoryvariablesstate){
		this.integratedmodel=model;
		this.envconds=envconds;
		this.knockoutgenes=knockoutgenes;
		this.currentgenewithtruestate=validateinputTrueGenes(inittruegenes);
		this.initregulatoryvariablesstate=initregulatoryvariablesstate;
		setupDefaultObjctiveFunction();

	}
	
	public void setOnlyGeneCycleCheck(boolean value){
		this.onlygenecyclecheck=value;
	}
	
	protected void setupDefaultObjctiveFunction(){
		this.objfunct=new HashMap<String, Double>();
		objfunct.put(this.integratedmodel.getBiomassFlux(), 1.0);
	}
	
	public void setObjectiveFunction(Map<String, Double> objfunct){
		if(objfunct!=null)
			this.objfunct=objfunct;
	}
	
	  public void setSolver(SolverType solver){
		  if(solver!=null)
	    	this.solver=solver;
	    }


	private HashSet<String> validateinputTrueGenes(HashSet<String> inputgenes){
		if(knockoutgenes!=null && inputgenes!=null){
			HashSet<String> newset=new HashSet<>();
			for (String id : inputgenes) {
				if(!knockoutgenes.contains(id))
					newset.add(id);
			}
			return newset;
		}
		return inputgenes;
	}
	
	
	
	public RFBAOptimizationSolution getOptimizationsolution() {
		return optimizationsolution;
	}

	
	protected void initVariablesIndexListAndNetworkMemory() throws Exception{
		this.indexallvariables=new ArrayList<>();
		this.currentgenestate=new IndexedHashMap<>();
		this.currentvarsstate=new IndexedHashMap<>();

		ArrayList<Boolean> initcycle=initRegulatoryGeneState();
		initRegulatoryVariablesList(initcycle);
        this.optimizationsolution=new RFBAOptimizationSolution(indexallvariables);
		optimizationsolution.appendStateResult(currentgenestate, currentvarsstate);
        
		if(!onlygenecyclecheck)
			memory=new OptFluxRegulatoryNetworkMemory(initcycle,indexallvariables);
		else{
			memory=new OptFluxRegulatoryNetworkMemory(initcycle,geneids);
		}
		
	}
	
	protected ArrayList<Boolean> initRegulatoryGeneState(){
		IOptfluxRegulatoryModel regmodel=(IOptfluxRegulatoryModel) integratedmodel.getRegulatoryNetwork();
		int ngenes=regmodel.getNumberOfRegulators();
		ArrayList<Boolean> res =new ArrayList<>(ngenes);
		geneids=new ArrayList<>();
		
		for (int i = 0; i < ngenes; i++) {
			String geneid=regmodel.getGene(i).getId();
			geneids.add(geneid);
			
			boolean state=false;
			if(currentgenewithtruestate!=null && currentgenewithtruestate.contains(geneid))
				state=true;
            
			currentgenestate.put(geneid, state);
			indexallvariables.add(geneid);
			res.add(state);
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Input Regulatory Gene State: ", geneid,state,"\n");
		}
		
	
		return res;
	}
	
	protected void initRegulatoryVariablesList(ArrayList<Boolean> initcycle) throws UnknownRegulatoryModelComponentException{
		
		IOptfluxRegulatoryModel regmodel=(IOptfluxRegulatoryModel) integratedmodel.getRegulatoryNetwork();
		
		IndexedHashMap<String, RegulatoryVariable> regulatoryvariables=regmodel.getVariablesInRegulatoryNetwork();
		

        for (int i = 0; i < regulatoryvariables.size(); i++) {
        	String varid=regulatoryvariables.getKeyAt(i);
        	RegulatoryVariable var=regulatoryvariables.getValueAt(i);
        	Boolean state=null;
            
        	if(var.getType().equals(RegulatoryModelComponent.TRANS_FACTOR_ID)){
        		String geneoftf=regmodel.getRegulatorIDAssociatedToRuleID(varid);
        		
        		if(geneoftf!=null){
        			if(currentgenewithtruestate!=null && currentgenewithtruestate.contains(geneoftf)){
        				state=true;
        			}
        			else if(initregulatoryvariablesstate!=null && initregulatoryvariablesstate.containsKey(varid)){
        				 state=initregulatoryvariablesstate.get(varid);
        			}
        			else{
        				state=regulatorymodelTFinitstate;
        			}
        		}
        		else{
        			if(initregulatoryvariablesstate!=null && initregulatoryvariablesstate.containsKey(varid)){
        				state=initregulatoryvariablesstate.get(varid);
        			}
        			else{
        				state=regulatorymodelTFinitstate;
        			}
        		}
        	}
        	else if(var.getType().equals(RegulatoryModelComponent.METABOLITE_ID) || 
        			var.getType().equals(RegulatoryModelComponent.REACTION_ID) || 
        			var.getType().equals(RegulatoryModelComponent.ENV_CONDITION_ID)){
        		if(initregulatoryvariablesstate!=null && initregulatoryvariablesstate.containsKey(varid)){
        			state=initregulatoryvariablesstate.get(varid);
        			
        		}
   			    else{
   			    	state=false;
   			    }
        	
        	}
        	else if(var.getType().equals(RegulatoryModelComponent.GENE_ID) && !indexallvariables.contains(varid)){
        		if(initregulatoryvariablesstate!=null && initregulatoryvariablesstate.containsKey(varid)){
        			state=initregulatoryvariablesstate.get(varid);
        		}
  			    else{
  			    	state=false;
  			    }
        	}
        	
        
        	if(state!=null){
        		if(!onlygenecyclecheck){
        		   initcycle.add(state);
        		}
        		indexallvariables.add(varid);
        		currentvarsstate.put(varid, state);
        		
        	}
        	else
        		throw new UnknownRegulatoryModelComponentException(varid);
        	
        	LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Input Regulatory Variable State: ", varid,state,"\n");
		}
        
	}
	
	private void doFirstStep() throws Exception{
		Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>>firststepresults=BooleanRegulatoryModelSolver.solve(integratedmodel, envconds,currentgenestate, currentvarsstate, onlygenecyclecheck, (HashMap<String, Double>) objfunct,solver);
		appendNewResults(firststepresults);
	}
	
	protected void appendNewResults(Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> results) throws Exception{
		IndexedHashMap<String, Boolean> generes=results.getValue0();
		IndexedHashMap<String, Boolean> varsres=results.getValue1();
   
		
		ArrayList<Boolean> step=new ArrayList<>();

		IndexedHashMap<String, Boolean> newgenestate=new IndexedHashMap<>();
		IndexedHashMap<String, Boolean> newvarstate=new IndexedHashMap<>();
		
		for (int i = 0; i < indexallvariables.size(); i++) {
			String id=indexallvariables.get(i);
			if(generes.containsKey(id)){
				boolean value=generes.get(id);
				if(knockoutgenes!=null && knockoutgenes.contains(id))
					value=false;
				step.add(value);
				newgenestate.put(id, value);
				
			}
			else if(varsres!=null && varsres.containsKey(id)){
			    boolean value=varsres.get(id);	
				newvarstate.put(id, value);
				
				if(!onlygenecyclecheck)
				    step.add(value);
			}
		}
		
		this.currentgenestate=newgenestate;
		if(newvarstate.size()>0)
			this.currentvarsstate=newvarstate;
		
		optimizationsolution.appendStateResult(newgenestate, newvarstate);
		memory.addState(step);
		
	}
	

	
	public void run() throws Exception{
		initVariablesIndexListAndNetworkMemory();
		doFirstStep();

		while (attractorisnotfound) {
			Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> step=BooleanRegulatoryModelSolver.solve(integratedmodel,envconds, currentgenestate,currentvarsstate, onlygenecyclecheck, (HashMap<String, Double>) objfunct,solver);
			appendNewResults(step);
			int cyclematch=memory.checkRedundancy();
			
			if(cyclematch!=-1){
				attractorisnotfound=false;
				solution=memory.getCycle();
				cyclestart=memory.size()-cyclematch;
				optimizationsolution.setCycleStart(cyclestart);
				addStartStepPoint();
				
			}

		}

	
		this.alwaysgeneoff=getGenesAlwaysOff();
		fbasimul=performMetabolicSimulation(getMetabolicOffGenes(integratedmodel,alwaysgeneoff));
		optimizationsolution.setFBaSolution(fbasimul);
		optimizationsolution.setDeletedGenes(alwaysgeneoff);

	}
	
	
	protected void addStartStepPoint(){
		ArrayList<Boolean> stateresult=memory.getStepPosition(memory.size()-cyclestart-1);

		IndexedHashMap<String, Boolean> startgenestates=new IndexedHashMap<>();
		IndexedHashMap<String, Boolean> startvarsstate=new IndexedHashMap<>();
		
		for (int i = 0; i < currentgenestate.size(); i++) {
			 boolean value=stateresult.get(i);
			 startgenestates.put(currentgenestate.getKeyAt(i), value);
		}
		
		int pos=0;
		for (int i = currentgenestate.size(); i < indexallvariables.size(); i++) {
			boolean value=stateresult.get(i);
			startvarsstate.put(currentvarsstate.getKeyAt(pos), value);
			pos++;
		}
		
		optimizationsolution.setStartStepState(startgenestates, startvarsstate);
	}

	
	protected ArrayList<String> getGenesAlwaysOff(){
		
		ArrayList<String> res=new ArrayList<>();
        ArrayList<String> regulatorygenes=new ArrayList<>(currentgenestate.keySet());
		
		for (int i = 0; i < regulatorygenes.size(); i++) {
			boolean geneAlwaysFalse = !solution.get(0).get(i);
			
			for (int j = 1; j < solution.size() && geneAlwaysFalse; j++) {
				geneAlwaysFalse = geneAlwaysFalse && !solution.get(j).get(i);
			}
			
			if(geneAlwaysFalse)
				res.add(regulatorygenes.get(i));
		}
		
		
		return res;
	}
	
	public static ArrayList<String> getMetabolicOffGenes(IIntegratedStedystateModel model, ArrayList<String> list){
		ArrayList<String> res =new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			String geneid=list.get(i);
			if(model.isMetabolicGene(geneid))
				res.add(geneid);
		}
		return res;
	}
	
   
	
	protected  SteadyStateSimulationResult performMetabolicSimulation(ArrayList<String> geneknockouts) throws Exception{
		
		GeneChangesList metabgenesknockoutslist = new GeneChangesList(geneknockouts);
		GeneticConditions metabolicgenesknockouts =new GeneticConditions(metabgenesknockoutslist ,(ISteadyStateGeneReactionModel) integratedmodel, false);
		SimulationSteadyStateControlCenter simulationControlCenter = new SimulationSteadyStateControlCenter(envconds,metabolicgenesknockouts, integratedmodel,SimulationProperties.FBA);
		simulationControlCenter.setFBAObj(objfunct);
		simulationControlCenter.setMaximization(true);
		simulationControlCenter.setSolver(solver);
		
		return simulationControlCenter.simulate();
	}
	
	
	public static RFBAOptimizationSolution runOptimization(IIntegratedStedystateModel model,EnvironmentalConditions envconds,HashSet<String> inittruegenes, HashSet<String> knockoutgenes,IndexedHashMap<String, Boolean> initregulatoryvariablesstate, Map<String, Double> objfunct) throws Exception{
		OptimizeRegulatoryModel optm=new OptimizeRegulatoryModel(model, envconds,inittruegenes,knockoutgenes, initregulatoryvariablesstate);
		optm.setObjectiveFunction(objfunct);
		optm.run();
		return optm.getOptimizationsolution();
	}
	
	
	public static RFBAOptimizationSolution runOptimization(IIntegratedStedystateModel model,EnvironmentalConditions envconds,HashSet<String> inittruegenes, HashSet<String> knockoutgenes,Map<String, Boolean> initregulatoryvariablesstate, Map<String, Double> objfunct,SolverType solvertype) throws Exception{
		OptimizeRegulatoryModel optm=new OptimizeRegulatoryModel(model, envconds,inittruegenes,knockoutgenes, initregulatoryvariablesstate);
		optm.setObjectiveFunction(objfunct);
		optm.setSolver(solvertype);
		optm.run();
		return optm.getOptimizationsolution();
	}
	


}
