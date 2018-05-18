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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pt.ornrocha.logutils.MTULogLevel;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.ManagerExceptionUtils;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.AbstractTwoStepIntegratedSimulation;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.components.DynamicRFBAProcess;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.results.RFBASimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;

public class DynamicRFBA extends AbstractTwoStepIntegratedSimulation implements Serializable{


	private static final long serialVersionUID = 1L;

	public DynamicRFBA(ISteadyStateModel model) {
		super(model);

	}
	
	
	@Override
	protected void initPropsKeys(){
	   
		propertiesmap = new HashMap<String, Object>();
		mandatoryProperties = new HashSet<String>();
		
		mandatoryProperties.add(RegulatorySimulationProperties.INITIALBIOMASS);
		mandatoryProperties.add(RegulatorySimulationProperties.TIMESTEP);
		mandatoryProperties.add(RegulatorySimulationProperties.NUMBERSTEPS);
		mandatoryProperties.add(RegulatorySimulationProperties.SUBSTRATES);
		
		
		//mandatoryProperties.add(RegulatorySimulationProperties.VARIABLES_CONTAINER);

		
		possibleProperties = new HashSet<String>();
		possibleProperties.add(RegulatorySimulationProperties.EXCLUDEUPTAKEREACTIONS);
		possibleProperties.add(RegulatorySimulationProperties.GENESSTARTINGWITHTRUESTATE);
		possibleProperties.add(RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS);
		possibleProperties.add(RegulatorySimulationProperties.SOLVER);
		possibleProperties.add(RegulatorySimulationProperties.RFBAINITIALVARIBLESSTATE);
		possibleProperties.add(RegulatorySimulationProperties.OBJECTIVE_FUNCTION);
		
		
	
	}
	
	
	public void setInitialBiomass(double initBiomass){
		setProperty(RegulatorySimulationProperties.INITIALBIOMASS,initBiomass);
	}

	public void setTimeStep(double timestep){
		setProperty(RegulatorySimulationProperties.TIMESTEP, timestep);
	}
	
	public void setNumberSteps(int numbersteps){
		setProperty(RegulatorySimulationProperties.NUMBERSTEPS, numbersteps);
	}
	
	public void setSubtrateReactions(IndexedHashMap<String, Double> initialsubstrateconc){
		setProperty(RegulatorySimulationProperties.SUBSTRATES, initialsubstrateconc);
	}

	public double getInitialBiomass() throws PropertyCastException, MandatoryPropertyException{
		return ManagerExceptionUtils.testCast(propertiesmap, Double.class, RegulatorySimulationProperties.INITIALBIOMASS, false);
	}
	

	public double getTimeStep() throws PropertyCastException, MandatoryPropertyException{
		return ManagerExceptionUtils.testCast(propertiesmap, Double.class, RegulatorySimulationProperties.TIMESTEP, false);
	}
	

	public int getNumberSteps() throws PropertyCastException, MandatoryPropertyException{
		return ManagerExceptionUtils.testCast(propertiesmap, Integer.class, RegulatorySimulationProperties.NUMBERSTEPS, false);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public HashSet<String> getGenesStartingWithTrueState()throws PropertyCastException, MandatoryPropertyException {
		return ManagerExceptionUtils.testCast(propertiesmap, HashSet.class, RegulatorySimulationProperties.GENESSTARTINGWITHTRUESTATE, true);
	}

	/*
	 * list of uptake reactions whose substrate concentrations do not change
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUptakeReactionsExcluded()throws PropertyCastException, MandatoryPropertyException {
		return ManagerExceptionUtils.testCast(propertiesmap, ArrayList.class, RegulatorySimulationProperties.EXCLUDEUPTAKEREACTIONS, true);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public IndexedHashMap<String, Double> getSubstrates()throws PropertyCastException, MandatoryPropertyException {
		return (IndexedHashMap<String, Double>)ManagerExceptionUtils.testCast(propertiesmap, IndexedHashMap.class, RegulatorySimulationProperties.SUBSTRATES, true);
	}
	

	
	@SuppressWarnings("unchecked")
	public Map<String, Boolean> getInitialVariablesState()throws PropertyCastException, MandatoryPropertyException {
		return (Map<String, Boolean>)ManagerExceptionUtils.testCast(propertiesmap, Map.class, RegulatorySimulationProperties.COMPONENTINITIALSTATE, true);
	}
	

	protected HashSet<String> getKnockoutGenes() throws PropertyCastException, MandatoryPropertyException{
		if(getGeneticConditions()!=null){
			ArrayList<String> allgenesoff=((RegulatoryGeneticConditions)getGeneticConditions()).getALLGeneKnockoutList();
			return new HashSet<>(allgenesoff);
		}
		return null;
	}
	
	
	@Override
	public RFBASimulationResult simulate() throws Exception {
		
		
		DynamicRFBAProcess rfba=new DynamicRFBAProcess((IIntegratedStedystateModel) integratedmodel, getEnvironmentalConditions(), getSubstrates(), getInitialBiomass(), getTimeStep(), getNumberSteps(), getUptakeReactionsExcluded());
	
		rfba.setSolver(getSolverType());
		rfba.setKnockoutGenes(getKnockoutGenes());
		rfba.setInitialGenesWithTrueState(getGenesStartingWithTrueState());
		rfba.setInitialStateForVariables(getInitialVariablesState());

		if(getObjectiveFunction()!=null)
			rfba.setObjectiveFunction((HashMap<String, Double>) getObjectiveFunction());
		
		/*if(LogMessageCenter.getLogger().isEnabled() && LogMessageCenter.getLogger().getLogLevel().equals(MTULogLevel.DEBUG)){
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\n\n\nParameters to start Dynamic RFBA: \n "+getInputInformation());
		}*/
	   // System.out.println(getInputInformation());
		
		rfba.run();
        
        return rfba.getResult();
	}

	@Override
	public Class<?> getFormulationClass() {
		return DynamicRFBA.class;
	}


	@Override
	public String getMetabolicSimulationMethod() throws PropertyCastException, MandatoryPropertyException {
		return SimulationProperties.FBA;
	}


	@Override
	public RegulatorySimulationMethod getRegulatoryNetworkSimulationMethod() throws PropertyCastException, MandatoryPropertyException {
		return null;
	}


	private String getInputInformation(){
		StringBuilder str=new StringBuilder();
		str.append("Model Class: "+integratedmodel.getClass().getSimpleName()+"\n");
		str.append("Environmental Conditions: "+getEnvironmentalConditions()+"\n");
		str.append("Initial Substrates concentration: "+getSubstrates()+"\n");
		str.append("User Excluded Uptake Reaction: "+getUptakeReactionsExcluded()+"\n");
		str.append("Initial Biomass: "+getInitialBiomass()+"\n");
		str.append("Time step: "+getTimeStep()+"\n");
		str.append("Number Steps: "+getNumberSteps()+"\n");
		str.append("Solver: "+getSolverType()+"\n");
		str.append("Gene Knockouts: "+getKnockoutGenes()+"\n");
		str.append("Genes starting with true state: "+getGenesStartingWithTrueState()+"\n");
		str.append("Initial input State of Regulatory Variables: "+getInitialVariablesState()+"\n");
		str.append("Objective Function: "+getObjectiveFunction()+"\n");
		
		return str.toString();
		
	}


	
	
	
	
	
	
	

}
