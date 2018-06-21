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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.solvers.builders.GLPKBinSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.GenericRegulatorySimulationResults;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.results.RFBASimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public class DynamicRFBAProcess {
	
	
	private IIntegratedStedystateModel model;
	private EnvironmentalConditions envconds;
	private HashSet<String> knockoutgenes;
	private HashSet<String> initgeneswithtruestate;
	private IndexedHashMap<String, Double> initsubstrateconcentrations;
	private Map<String, Boolean> initvariableswithstate;
	private ArrayList<String> excludeUptakeReactions;
	private double initBiomass;
	private double timestep;
	private int numbersteps;
	private int nogrowthcount=0;
	private String biomassid=null;
	
	private IndexedHashMap<String, Double> concentrationofexchangefluxesmap;

	private Map<String, Double> objfunct;
	private String solver=GLPKBinSolverBuilder.ID;
	
	
	// Store Variables
	private double currentbiomass;
	private ArrayList<Double> biomassprofile=new ArrayList<>();
	private ArrayList<Double> timevector=new ArrayList<>();
	private SteadyStateSimulationResult currentsimulationresult;
	private IndexedHashMap<String, Double> currentconcentrations;
	private ArrayList<ArrayList<Double>> concentrations =new ArrayList<>();
	//private IndexedHashMap<String, ArrayList<Double>> concentrationsprofile=new IndexedHashMap<>();
	private ArrayList<String> currentgenesoff=null;
	private ArrayList<String> previousgenesoff=null;
	private IndexedHashMap<String, Boolean> previousgenestates;
	private IndexedHashMap<String, Boolean> previousvarstates;
	private IndexedHashMap<String, Boolean> currentgenestates;
	private IndexedHashMap<String, Boolean> currentvarstates;
	private EnvironmentalConditions currentEnvconds;
	private RFBASolutionType solutiontype=RFBASolutionType.FEASIBLE;
  
	
	public DynamicRFBAProcess(IIntegratedStedystateModel model, EnvironmentalConditions envconds, IndexedHashMap<String, Double> initialsubstrateconc, double initBiomass, double timestep, int nsteps, ArrayList<String> excludeUptakeReactions){
		this.model=model;
		this.envconds=envconds;
		this.initsubstrateconcentrations=initialsubstrateconc;
		this.initBiomass=initBiomass;
		this.currentbiomass=initBiomass;
		this.timestep=timestep;
		this.numbersteps=nsteps;
		
		if(excludeUptakeReactions!=null)
			this.excludeUptakeReactions=excludeUptakeReactions;
		else
			this.excludeUptakeReactions=findpossibleuptakereactionstoexclude();
		setupDefaultObjctiveFunction();
	}

	
	protected ArrayList<String> findpossibleuptakereactionstoexclude(){
		ArrayList<String> alldrains=(ArrayList<String>) model.identifyDrainReactionsFromStoichiometry();
		return detectPossibleUptakeReactionsToexclude(alldrains);
		/*ArrayList<String> res =new ArrayList<>();
		ArrayList<String> alldrains=(ArrayList<String>) model.identifyDrainReactionsFromStoichiometry();
		String pattern="((R_)*(EX_(co2|o2|h2o|h|nh4|pi))[_\\(]).?";
		Pattern r = Pattern.compile(pattern);
        
		for (int i = 0; i < alldrains.size(); i++) {
			Matcher m = r.matcher(alldrains.get(i));
			if(m.find())
				res.add(alldrains.get(i));
		}
		
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Possible Uptake Reactions to Exclude: ", res);
	    return res;*/
	}
	
	public static ArrayList<String> detectPossibleUptakeReactionsToexclude(ArrayList<String> alldrains){
		
		ArrayList<String> res =new ArrayList<>();
		String pattern="((R_)*(EX_(co2|o2|h2o|h|nh4|pi))[_\\(]).?";
		Pattern r = Pattern.compile(pattern);
        
		for (int i = 0; i < alldrains.size(); i++) {
			Matcher m = r.matcher(alldrains.get(i));
			if(m.find())
				res.add(alldrains.get(i));
		}
		
		LogMessageCenter.getLogger().toClass(DynamicRFBAProcess.class).addDebugMessage("Possible Uptake Reactions to Exclude: ", res);
	    return res;
	}
	
/*	protected ArrayList<String> filterpossiblereactionstoexclude(IndexedHashMap<String, Double> initconc){
		ArrayList<String> res =new ArrayList<>();
		
		String pattern="((R_)*(EX_)*((co2|o2|h2o|h|nh4|pi))[_\\(]e).?";
		Pattern r = Pattern.compile(pattern);
		for (int i = 0; i < initconc.size(); i++) {
			String reactid=initconc.getKeyAt(i);
			Matcher m = r.matcher(reactid);
			if(m.find())
				res.add(reactid);
		}
		

			if(envconds==null)
				envconds=new EnvironmentalConditions();
			for (int i = 0; i < initconc.size(); i++) {
				String reactid=initconc.getKeyAt(i);
				double value=initconc.getValueAt(i);
					 if(value!=0)
						 value=-1*value;
				
			
				    if(!envconds.containsKey(reactid)){
					   ReactionConstraint newconst=new ReactionConstraint(value, model.getReactionConstraint(reactid).getUpperLimit());
					   envconds.addReactionConstraint(reactid, newconst);
				    }
				
			}
   
		return res;
	}*/
	
	
	protected void setupDefaultObjctiveFunction(){
		this.objfunct=new HashMap<String, Double>();
		objfunct.put(this.model.getBiomassFlux(), 1.0);
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Default Objective Function: ", objfunct);
	}
	
	public void setObjectiveFunction(HashMap<String, Double> objfunct){
		if(objfunct!=null)
			this.objfunct=objfunct;
	}
	
	
	public void setBiomassID(String biomassid){
		this.biomassid=biomassid;
		this.objfunct=new HashMap<String, Double>();
		objfunct.put(biomassid, 1.0);
	}
	
	
	public void setKnockoutGenes(HashSet<String> knockoutgenes) {
		this.knockoutgenes = knockoutgenes;
	}

    public void setInitialStateForVariables(Map<String, Boolean> initvars){
    	this.initvariableswithstate=initvars;
    }


	public void setInitialGenesWithTrueState(HashSet<String> initgeneswithtruestate) {
		this.initgeneswithtruestate = initgeneswithtruestate;
	}

    public void setSolver(String solver){
    	if(solver!=null)
    		this.solver=solver;
    }

	
	/*
	 * Find exchange Reactions without uptake Reactions that were excluded 
	 */
	protected ArrayList<String> getexchangeFluxes(){
		ArrayList<String> res =new ArrayList<>();
		ArrayList<String> alldrains=(ArrayList<String>) model.identifyDrainReactionsFromStoichiometry();
		for (String name : alldrains) {
			if(excludeUptakeReactions!=null){
				if(!excludeUptakeReactions.contains(name))
				    res.add(name);
			}
			else
				res.add(name);
		}
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Exchange Fluxes: ", res);
		return res;
	}
	
	
	
	/*
	 * Configure Initial Concentrations
	 */
	protected void configureExchangeFluxesConcentrations(){
		
		ArrayList<String> exchangefluxes=getexchangeFluxes();
		this.concentrationofexchangefluxesmap=new IndexedHashMap<>();
		ArrayList<Double> initconcvector=new ArrayList<>();
		
		for (int i = 0; i < exchangefluxes.size(); i++) {
			String reactname=exchangefluxes.get(i);
			
			double lowerbound=model.getReaction(reactname).getConstraints().getLowerLimit();
			if(envconds!=null && envconds.containsKey(reactname))
				lowerbound=envconds.get(reactname).getLowerLimit();
			

			if(initsubstrateconcentrations!=null && initsubstrateconcentrations.containsKey(reactname)){
				concentrationofexchangefluxesmap.put(reactname, initsubstrateconcentrations.get(reactname));
				initconcvector.add(initsubstrateconcentrations.get(reactname));
			}
			else{
				if(-1*lowerbound>0){
					concentrationofexchangefluxesmap.put(reactname, 1000.0);
					initconcvector.add(1000.0);
				}
				else{
					concentrationofexchangefluxesmap.put(reactname, 0.0);
					initconcvector.add(0.0);
				}
			}
			
		}
		
		concentrations.add(initconcvector);
		
		try {
			this.currentconcentrations=(IndexedHashMap<String, Double>) MTUCollectionsUtils.deepCloneObject(concentrationofexchangefluxesmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Exchange Fluxes initial Concentrations: ", currentconcentrations);
	  
	}
	

	protected IndexedHashMap<String, ReactionConstraint> configureInitialReactionContraints(){
		IndexedHashMap<String, ReactionConstraint> newreactionconstraints=new IndexedHashMap<>();
		
		
		for (int i = 0; i < concentrationofexchangefluxesmap.size(); i++) {
			String reactname=concentrationofexchangefluxesmap.getKeyAt(i);
			
			double uptakebound=concentrationofexchangefluxesmap.get(reactname)/(initBiomass*timestep);
		
			ReactionConstraint originalconstr=model.getReactionConstraint(reactname);
			
			
			if(envconds!=null && envconds.containsKey(reactname)){
				originalconstr=envconds.get(reactname);
			}
			
			double originallowerbound=originalconstr.getLowerLimit();
			ReactionConstraint newconst=null;
			
			if(uptakebound>-1*originallowerbound && -1*originallowerbound>0)
				newconst=new ReactionConstraint(originalconstr.getLowerLimit(), originalconstr.getUpperLimit());
			else{
				if(uptakebound!=0.0)
					uptakebound=-1*uptakebound;
				newconst=new ReactionConstraint(uptakebound, originalconstr.getUpperLimit());
			}
			
			newreactionconstraints.put(reactname, newconst);
		}
	    
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Exchange Reactions Constraints: ", newreactionconstraints);
		return newreactionconstraints;
		
	}
	
	
	protected EnvironmentalConditions setupEnvironemntalConditions(IndexedHashMap<String, ReactionConstraint> reactionconstr){
		EnvironmentalConditions initenvconds=null;
		if(envconds!=null)
			initenvconds=envconds.copy();
		
		if(reactionconstr!=null){
			
			if(initenvconds==null)
				initenvconds=new EnvironmentalConditions();
			

			for (int i = 0; i < reactionconstr.size(); i++) {
				
				if(initenvconds.containsKey(reactionconstr.getKeyAt(i)))
					initenvconds.addReplaceReactionContraint(reactionconstr.getKeyAt(i), reactionconstr.getValueAt(i));
				else
					initenvconds.put(reactionconstr.getKeyAt(i), reactionconstr.getValueAt(i));
			}
		}
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Input Environmental Conditions: ", initenvconds);
		return initenvconds;
	}
	
	
		
	public void run() throws Exception{
		
		configureExchangeFluxesConcentrations();
		EnvironmentalConditions useenvconds=setupEnvironemntalConditions(configureInitialReactionContraints());
	
		RFBAOptimizationSolution optmregmodel=OptimizeRegulatoryModel.runOptimization(model, useenvconds,initgeneswithtruestate, knockoutgenes,initvariableswithstate,objfunct,solver);
		Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> stepstart=optmregmodel.getStartstep();
		this.currentgenestates=stepstart.getValue0();
		this.currentvarstates=stepstart.getValue1();
		this.currentgenesoff=optmregmodel.getDeletedGenes();
		this.biomassprofile.add(currentbiomass);
		//this.timevector.add(0.0);
		this.currentEnvconds=useenvconds;
	

		boolean valid=true;
		for (int i = 0; i < numbersteps; i++) {
			
			boolean validresult=runStep(currentgenesoff);
		
			if(!validresult){
				nogrowthcount++;
				currentbiomass=biomassprofile.get(biomassprofile.size()-1);
				if(nogrowthcount>=20){
					valid=false;
					break;
					
				}
			}
			else{
				currentbiomass=currentbiomass*Math.exp(currentsimulationresult.getOFvalue()*timestep);
				updateConcentrations();
				
			}
			//System.out.println("Time:"+(i*timestep)+" Biomass:"+currentbiomass);
			biomassprofile.add(currentbiomass);
			timevector.add(i*timestep);
			saveConcentrations();
			updateRegulatoryState();
			
		}
	
		if(!valid){
			solutiontype=RFBASolutionType.NOFEASIBLESOLUTION;
			//System.out.println("No feasible solution, nutrients exhausted");
		}
		/*System.out.println("Biomass size: "+biomassprofile.size());
		System.out.println("time size: "+timevector.size());
		System.out.println("Concentrations size: "+concentrations.size());*/
		
	/*	for (int i = 0; i < concentrations.size(); i++) {
			ArrayList<Double> list=concentrations.get(i);
			for (int j = 0; j < list.size(); j++) {
				System.out.print(list.get(j));
				if(j<list.size()-1)
					System.out.print("\t");
			}
			System.out.println();
		}*/
	}
	
	
	protected boolean runStep(ArrayList<String> genesoff) throws Exception{
		
		currentsimulationresult=runMetabolicSimulation(genesoff,currentEnvconds);
		
		LPSolutionType soltype=currentsimulationresult.getSolutionType();
		
	  if((!soltype.equals(LPSolutionType.FEASIBLE) && !soltype.equals(LPSolutionType.OPTIMAL)) || currentsimulationresult.getOFvalue()<=0){
		  return false;
	  }
		
		return true;
	}
	
	
	protected void updateConcentrations(){
		
		IndexedHashMap<String, Double> newconcentration=new IndexedHashMap<>();
		
		FluxValueMap fluxvalues=currentsimulationresult.getFluxValues();
		
		for (int i = 0; i < currentconcentrations.size(); i++) {
			String fluxid=currentconcentrations.getKeyAt(i);
			double oldvalue=currentconcentrations.getValueAt(i);
			
			double simulatedvalue=fluxvalues.get(fluxid);
			
			double simulobjtval=currentsimulationresult.getOFvalue();
			
			double treat=(simulatedvalue/simulobjtval)*currentbiomass*(1-Math.exp(simulobjtval*timestep));
			
			double newvalue=oldvalue-treat;
			
			if(newvalue<=0){
				newconcentration.put(fluxid, 0.0);	
			}
			else{
				newconcentration.put(fluxid, newvalue);
			}
				
		}
		this.currentconcentrations=newconcentration;
	
	}
	
	protected void saveConcentrations(){
		ArrayList<Double> concentrationvector=new ArrayList<>();
		for (int i = 0; i < currentconcentrations.size(); i++) {
			concentrationvector.add(currentconcentrations.getValueAt(i));
		}
		concentrations.add(concentrationvector);
	}
	
	protected void updateRegulatoryState() throws Exception{
		
		currentEnvconds=setupEnvironemntalConditions(updateReactionContraints());
		if(previousgenestates!=null)
			previousgenesoff=updateCurrentOffGenes(previousgenestates);
		previousgenestates=(IndexedHashMap<String, Boolean>) MTUCollectionsUtils.deepCloneObject(currentgenestates);
		previousvarstates=(IndexedHashMap<String, Boolean>) MTUCollectionsUtils.deepCloneObject(currentvarstates);
		Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> newregulatorystate=BooleanRegulatoryModelSolver.solve(model, currentEnvconds, currentgenestates, currentvarstates, false,objfunct,solver);
		
		if(knockoutgenes!=null){
			IndexedHashMap<String, Boolean> checkknockoutgenes=validateWithKnockoutGenes(newregulatorystate.getValue0());
			currentgenesoff=updateCurrentOffGenes(checkknockoutgenes);
			currentgenestates=checkknockoutgenes;	
		}
		else{
			currentgenesoff=updateCurrentOffGenes(newregulatorystate.getValue0());
		    currentgenestates=newregulatorystate.getValue0();
		}
		
		currentvarstates=newregulatorystate.getValue1();
	}
	

	
	protected ArrayList<String> updateCurrentOffGenes(IndexedHashMap<String, Boolean> inputgenestates){
		ArrayList<String> genesoff=new ArrayList<>();

		for (int i = 0; i < inputgenestates.size(); i++) {
			String geneid=inputgenestates.getKeyAt(i);
			boolean value=inputgenestates.getValueAt(i);
			if(!value)
				genesoff.add(geneid);
		}
		return genesoff;
		//this.currentgenesoff=genesoff;
	}
	
	protected IndexedHashMap<String, Boolean> validateWithKnockoutGenes(IndexedHashMap<String, Boolean> inputgenestates){
		
		IndexedHashMap<String, Boolean> res=new IndexedHashMap<>();
		for (int i = 0; i < inputgenestates.size(); i++) {
			String geneid=inputgenestates.getKeyAt(i);
			boolean value=inputgenestates.getValueAt(i);
			if(knockoutgenes!=null && knockoutgenes.contains(geneid))
				res.put(geneid, false);
			else
				res.put(geneid, value);
		}
		return res;
	}
	
	
	protected IndexedHashMap<String, ReactionConstraint> updateReactionContraints(){
		IndexedHashMap<String, ReactionConstraint> newreactionconstraints=new IndexedHashMap<>();
		
		for (int i = 0; i < currentconcentrations.size(); i++) {
			String reactname=currentconcentrations.getKeyAt(i);
			double uptakebound=currentconcentrations.getValueAt(i)/(currentbiomass*timestep);
			
			if(uptakebound>1000.0)
				uptakebound=1000.0;

			ReactionConstraint originalconstr=model.getReactionConstraint(reactname);

			if(envconds!=null && envconds.containsKey(reactname))
				originalconstr=envconds.get(reactname);
			
			double originallowerbound=originalconstr.getLowerLimit();

			ReactionConstraint newconst=null;
			
			if(uptakebound>-1*originallowerbound && -1*originallowerbound>0){
				newconst=new ReactionConstraint(originallowerbound, originalconstr.getUpperLimit());
			}
			else{
				if(Math.abs(uptakebound)<1E-9)
					uptakebound=0.0;
				
				if(uptakebound!=0)
					uptakebound=-1*uptakebound;
				newconst=new ReactionConstraint(uptakebound, originalconstr.getUpperLimit());
			}
			
			newreactionconstraints.put(reactname, newconst);
		}

		return newreactionconstraints;
		
	}
	
	
	
	
   
	protected  SteadyStateSimulationResult runMetabolicSimulation(ArrayList<String> geneknockouts, EnvironmentalConditions envconds) throws Exception{
		
		ArrayList<String> metabolicgenes=OptimizeRegulatoryModel.getMetabolicOffGenes(model, geneknockouts);
		GeneChangesList metabgenesknockoutslist = new GeneChangesList(metabolicgenes);
		GeneticConditions metabolicgenesknockouts =new GeneticConditions(metabgenesknockoutslist ,model, false);
		SimulationSteadyStateControlCenter simulationControlCenter = new SimulationSteadyStateControlCenter(envconds,metabolicgenesknockouts, model,SimulationProperties.FBA);
		simulationControlCenter.setFBAObj(objfunct);
		simulationControlCenter.setMaximization(true);
		simulationControlCenter.setSolver(solver);
		
		return simulationControlCenter.simulate();
	}
	
	
	public RFBASimulationResult getResult() throws Exception{
		
		ArrayList<String> geneids=new ArrayList<>();
		ArrayList<Boolean> genestate=new ArrayList<>();
		
		for (int i = 0; i < currentgenestates.size(); i++) {
			String geneid=currentgenestates.getKeyAt(i);
			geneids.add(geneid);
			genestate.add(currentgenestates.get(geneid));
		}
		
		
		RFBARegulatoryStateMemory statememory=new RFBARegulatoryStateMemory(geneids, genestate);
		
		
		IRegulatoryModelSimulationResult regulatorystate=new GenericRegulatorySimulationResults(model.getRegulatoryNetwork(), getLastInputInitialState(), statememory);
		
		RegulatoryGeneticConditions initgeneticconditions=RegulatoryGeneticConditions.getRegulatoryGeneticConditions(previousgenesoff, model);
		RegulatoryGeneticConditions finalgeneticconditions=RegulatoryGeneticConditions.getRegulatoryGeneticConditions(currentgenesoff, model);
		
		
       //System.out.println("current growth: "+currentsimulationresult.getOFvalue());
		return new RFBASimulationResult(model, 
				envconds,
				initgeneticconditions,
				finalgeneticconditions,
				currentsimulationresult.getFluxValues(), 
				currentsimulationresult.getSolverOutput(), 
				currentsimulationresult.getOFvalue(), 
				currentsimulationresult.getOFString(), 
				currentsimulationresult.getSolutionType(),
				regulatorystate,
				initsubstrateconcentrations, 
				initBiomass, 
				timestep, 
				numbersteps,
				biomassprofile,
				timevector,
				concentrations,
				getProfileofExchangeFluxesConcentratrions(),
				solutiontype,
				excludeUptakeReactions);
	}
	
	private InitialRegulatoryState getLastInputInitialState() throws Exception{

		ArrayList<String> ordereridentifiers =new ArrayList<>();
		ArrayList<Boolean> statevector=new ArrayList<>();
		IndexedHashMap<String, String> geneid2ruleid=new IndexedHashMap<>();
		IndexedHashMap<String, RegulatoryRule> rules=model.getRegulatoryNetwork().getRegulatoryRules();

		if(previousgenestates!=null)
			for (int i = 0; i < previousgenestates.size(); i++) {
				String geneid=previousgenestates.getKeyAt(i);
				ordereridentifiers.add(geneid);
				statevector.add(previousgenestates.get(geneid));
				geneid2ruleid.put(geneid, rules.get(geneid).getRuleId());
			}

		if(previousvarstates!=null)
			for (int i = 0; i < previousvarstates.size(); i++) {
				String varid=previousvarstates.getKeyAt(i);
				ordereridentifiers.add(varid);
				statevector.add(previousvarstates.get(varid));
			}

		return new InitialRegulatoryState(ordereridentifiers,statevector,geneid2ruleid);
	}
	
	
	private IndexedHashMap<String, ArrayList<Double>> getProfileofExchangeFluxesConcentratrions(){
		
		IndexedHashMap<String, ArrayList<Double>> res=new IndexedHashMap<>(currentconcentrations.size());
		
		for (int i = 0; i <currentconcentrations.size(); i++) {
			String exchid=currentconcentrations.getKeyAt(i);
			res.put(exchid, new ArrayList<Double>());
		}
		
		
		for (int i = 0; i < concentrations.size(); i++) {
			ArrayList<Double> vectorvalues=concentrations.get(i);
			
			for (int j = 0; j < vectorvalues.size(); j++) {
				res.getValueAt(j).add(vectorvalues.get(j));
			}
			
		}
		
		return res;
	}
	


}
