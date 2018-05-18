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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.analysis.regulatoryrules;

import java.util.ArrayList;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.criticality.CriticalGenes;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.dynamic.DynamicIntegratedModelOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.dynamic.DynamicIntegratedSteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.dynamic.IDynamicIntegratedSteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.DynamicRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.IntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.SRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.RFBASimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;

public class RegulatoryRulesAnalyserThread implements Runnable{

	
	private IndexedHashMap<String, String> regulatoryrulestoanalyse;
	private IndexedHashMap<String, Integer> originalrulesorder;
	private EnvironmentalConditions envconds;
	private boolean findcriticalgenes=false;
	private ArrayList<String> criticalgenes=null;
	private String savecriticalgenestofile=null;
	private IIntegratedStedystateModel originalmodel;
	private IntegratedSimulationOptionsContainer simulationproperties;
	private IDynamicIntegratedSteadyStateModel dynamicmodel=null;
	//private DynamicIntegratedModelOptionsContainer dynamicproperties;
	private AbstractIntegratedSimulationControlCenter controlcenter;
	private double threshold=0.001;
	
	private RulesAnalyserReporterContainer reporter;
	
	public RegulatoryRulesAnalyserThread(IIntegratedStedystateModel model, 
			IndexedHashMap<String, String> regulatoryrulestoanalyse,
			IndexedHashMap<String, Integer> originalrulesorder,
			EnvironmentalConditions environmentalconditions, 
			IntegratedSimulationOptionsContainer simulationproperties, 
			DynamicIntegratedModelOptionsContainer dynamicproperties) throws Exception {
		this.regulatoryrulestoanalyse=regulatoryrulestoanalyse;
		this.originalrulesorder=originalrulesorder;
		this.envconds=environmentalconditions;
		this.originalmodel=model;
		this.simulationproperties=simulationproperties;
		configureDynamicModel(dynamicproperties);
		
	}
	

	public RegulatoryRulesAnalyserThread(IIntegratedStedystateModel model,IndexedHashMap<String, String> regulatoryrulestoanalyse,IndexedHashMap<String, Integer> originalrulesorder, EnvironmentalConditions environmentalconditions, IntegratedSimulationOptionsContainer simulationproperties,DynamicIntegratedModelOptionsContainer dynamicproperties,boolean calculatecriticalgenes) throws Exception {
		this(model,regulatoryrulestoanalyse,originalrulesorder, environmentalconditions,simulationproperties,dynamicproperties);
		this.findcriticalgenes=calculatecriticalgenes;
	}
	
	
	public void setCriticalGenes(ArrayList<String> criticalgenes){
		this.criticalgenes=criticalgenes;
	}
	
	public void writeCriticalGeneToFile(String filepath){
		this.savecriticalgenestofile=filepath;
	}
	
	
	private void findCriticalGenes(EnvironmentalConditions envconds) throws Exception{
		
		CriticalGenes critgenesoper= new CriticalGenes(originalmodel, envconds, simulationproperties.getSolver());
		critgenesoper.identifyCriticalGenes();
		if(savecriticalgenestofile!=null)
			critgenesoper.writeCriticalGenesToFile(savecriticalgenestofile);
		this.criticalgenes=(ArrayList<String>) critgenesoper.getCriticalGenesIds();
		
	}
	
	public void setAcceptableBiomassThreshold(double biomassthreshold){
		this.threshold=biomassthreshold;
	}
	
	
	private void configureSimulationControlCenter() throws Exception{
		
		IntegratedSimulationMethod integratedsimumethod=simulationproperties.getSimulationMethod();
		
		SolverType solver=simulationproperties.getSolver();
		RegulatoryGeneticConditions geneconditions=simulationproperties.getGeneticconditions();
		
	
		if(integratedsimumethod.equals(IntegratedSimulationMethod.INTEGRATEDSIMULATION)){
			
			String metabsimumethod =simulationproperties.getMetabolicSimulationMethod();
			
			RegulatorySimulationMethod regsimumethod=simulationproperties.getRegulatorySimulationMethod();
			

			controlcenter=new IntegratedSimulationControlCenter(dynamicmodel, 
					envconds, 
					geneconditions, 
					true, 
					solver, 
					metabsimumethod, 
					regsimumethod);
			
	
			if(simulationproperties.isStopSimulationAtFirstAttractor()!=null)
				((IntegratedSimulationControlCenter)controlcenter).stopRegulatorySimulationOnFirstAttractor(simulationproperties.isStopSimulationAtFirstAttractor());
			
			if(simulationproperties.getInitialComponentsbooleanState()!=null)
				((IntegratedSimulationControlCenter)controlcenter).setComponentsBooleanInitialState(simulationproperties.getInitialComponentsbooleanState());
	
			if(simulationproperties.getRegulatorySimulationIterations()!=null)
				((IntegratedSimulationControlCenter)controlcenter).setMaxNumberIterationsRegulatorySimulation(simulationproperties.getRegulatorySimulationIterations());
		
			
			((IntegratedSimulationControlCenter)controlcenter).setObjectiveFunction(simulationproperties.getObjfunct());
				
				
		}
		else if(integratedsimumethod.equals(IntegratedSimulationMethod.SRFBA)){
			controlcenter=new SRFBAControlCenter(dynamicmodel, envconds, geneconditions, true,solver);
			
			if(simulationproperties.getInitialComponentsbooleanState()!=null)
				((SRFBAControlCenter)controlcenter).setComponentsBooleanInitialState(simulationproperties.getInitialComponentsbooleanState());
			((SRFBAControlCenter)controlcenter).setObjectiveFunction(simulationproperties.getObjfunct());
		}
		else if(integratedsimumethod.equals(IntegratedSimulationMethod.DYNAMICRFBA)){
			if(simulationproperties instanceof RFBASimulationOptionsContainer){
				RFBASimulationOptionsContainer options=(RFBASimulationOptionsContainer)simulationproperties;
				
				controlcenter=new DynamicRFBAControlCenter(dynamicmodel, 
					envconds, 
					geneconditions, 
					options.getSolver(), 
					options.getInitialBiomass(), 
					options.getTimeStep(), 
					options.getNumberSteps(), 
					options.getInitialSubstrateConcentrations());
			
				if(simulationproperties.getInitialComponentsbooleanState()!=null)
					((DynamicRFBAControlCenter)controlcenter).setComponentsBooleanInitialState(simulationproperties.getInitialComponentsbooleanState());
				
				((DynamicRFBAControlCenter)controlcenter).setObjectiveFunction(simulationproperties.getObjfunct());
			
			}
			else
				throw new Exception("Invalid simulation options container, must include RFBA simulation properties");
			
		}
		else
			throw new Exception("Unsupported simulation method");
		

	}
	
	protected void configureDynamicModel(DynamicIntegratedModelOptionsContainer dynamicproperties) throws Exception{
		this.dynamicmodel=new DynamicIntegratedSteadyStateModel(originalmodel, dynamicproperties.getExtraGeneid2Ruleid(), dynamicproperties.getExtraGeneid2GeneName(),dynamicproperties.getPossibleTypeVariables(),true);
	    
	}
	
	
	
	@Override
	public void run() {
        
		try {
			configureSimulationControlCenter();
		} catch (Exception e1) {
			LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage(e1);
		}
		
		if(findcriticalgenes)
			try {
				LogMessageCenter.getLogger().toClass(getClass()).addInfoMessage("Please wait a moment... finding critical genes");
				findCriticalGenes(envconds);
				LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Critical Genes: "+criticalgenes);
			} catch (Exception e) {
				LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage(e);
			}
		
		execute();
	
	}
	
	private void execute() {
		
		reporter=new RulesAnalyserReporterContainer(dynamicmodel, criticalgenes,originalrulesorder,simulationproperties,envconds);
		
		IndexedHashMap<String, String> rulesettosimulate=new IndexedHashMap<>();
		
		for (int i = 0; i < regulatoryrulestoanalyse.size(); i++) {
			String geneid=regulatoryrulestoanalyse.getKeyAt(i);
			String rule=regulatoryrulestoanalyse.get(geneid);
			
			rulesettosimulate.put(geneid, rule);
			
			IntegratedSimulationMethodResult results=null;
			try {
				results=runRuleSet(rulesettosimulate);	
			} catch (Exception e) {
				LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage("Error in simulation process:", e);
				break;
			}
			
			double growtheval=results.getOFvalue();
			
			reporter.setSimulationCurrentResults(results);

			if(growtheval<=threshold){
				
				
				if(growtheval<=0.0){
					dynamicmodel.addGeneAsUnconstrained(geneid);
					reporter.addProblematicRule(geneid, rule);
					FluxValueMap simfluxes=results.getFluxValues();
					try {
						reporter.analyseInhibitionOfBiomassCofactors(geneid, rule,simfluxes);
					} catch (Exception e) {
						LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage("Error analysing inhibition of biomass cofactors: ", e);
					}
				}
				else
					reporter.addRuleWithAffectsBiomassGrowth(geneid, growtheval, rule);
				
				rulesettosimulate=resetToPreviousState(rulesettosimulate, geneid);
				
			}
			else
				reporter.addRuleWithoutProblems(geneid, growtheval,rule);
			
			
		}
		

		
	}
	
	
	protected IndexedHashMap<String, String> resetToPreviousState(IndexedHashMap<String, String> actualcache, String ignoregenerule){
		IndexedHashMap<String, String> res=new IndexedHashMap<>();
		
		for (int i = 0; i < actualcache.size(); i++) {
			String geneid=actualcache.getKeyAt(i);
			if(!geneid.equals(ignoregenerule)){
				res.put(geneid, actualcache.get(geneid));
			}
		}
		return res;
	}
	
	
	
	private IntegratedSimulationMethodResult runRuleSet(IndexedHashMap<String, String> rulesettosimulate) throws Exception{
		
		dynamicmodel.setNewGroupOfRegulatoryRules(rulesettosimulate);
	    return (IntegratedSimulationMethodResult) controlcenter.simulate();	
	}
	
	public RulesAnalyserReporterContainer getReport(){
		return reporter;
	}

}
