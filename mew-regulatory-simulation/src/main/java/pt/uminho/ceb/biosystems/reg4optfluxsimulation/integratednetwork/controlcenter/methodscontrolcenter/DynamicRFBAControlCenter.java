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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.TwoStepIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.DynamicRFBA;

public class DynamicRFBAControlCenter extends TwoStepIntegratedSimulationControlCenter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DynamicRFBAControlCenter(ISteadyStateModel model, EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions,
			SolverType solver,
			double initialbiomass,
			double timestep,
			int numbersteps,
			IndexedHashMap<String, Double> initialsubstrateconcentration) {
		super(model, environmentalConditions, geneticConditions, IntegratedSimulationMethod.DYNAMICRFBA.getName(), true, solver);
	    setInitialBiomass(initialbiomass);
	    setTimeStep(timestep);
	    setNumberSteps(numbersteps);
	    setSubtrateReactions(initialsubstrateconcentration);
	}
	
	@Override
	protected LinkedHashMap<String, Class<?>> getMethodsSupportedByControlCenter() {
		LinkedHashMap<String, Class<?>> support=new LinkedHashMap<>();
		support.put(IntegratedSimulationMethod.DYNAMICRFBA.toString(), DynamicRFBA.class);
		return support;
	}

	
	
	public void setInitialBiomass(double initBiomass){
		addProperty(RegulatorySimulationProperties.INITIALBIOMASS,initBiomass);
	}

	public void setTimeStep(double timestep){
		addProperty(RegulatorySimulationProperties.TIMESTEP, timestep);
	}
	
	public void setNumberSteps(int numbersteps){
		addProperty(RegulatorySimulationProperties.NUMBERSTEPS, numbersteps);
	}
	
	public void setSubtrateReactions(IndexedHashMap<String, Double> initialsubstrateconc){
		addProperty(RegulatorySimulationProperties.SUBSTRATES, initialsubstrateconc);
	}
	
	public void addSubtrateReaction(String reactionid, double initialconcentration) throws PropertyCastException, MandatoryPropertyException{
		@SuppressWarnings("unchecked")
		IndexedHashMap<String, Double> values=(IndexedHashMap<String, Double>) getProperty(RegulatorySimulationProperties.SUBSTRATES);
		if(values!=null){
			values.put(reactionid, initialconcentration);
		}
		else{
			IndexedHashMap<String, Double> newsubs=new IndexedHashMap<>();
			newsubs.put(reactionid, initialconcentration);
			setSubtrateReactions(newsubs);
		}
	}
	
	public void setGeneGroupToStartWithTrueState(HashSet<String> initgeneswithtruestate){
		addProperty(RegulatorySimulationProperties.GENESSTARTINGWITHTRUESTATE, initgeneswithtruestate);
	}
	
	public void setUptakeReactionsToExcludeFromInitialConfiguration(ArrayList<String> listreactions){
		addProperty(RegulatorySimulationProperties.EXCLUDEUPTAKEREACTIONS, listreactions);
	}
	
	
/*	public void setInitialVariablesState(IndexedHashMap<String, Boolean> initvarstate){
		addProperty(RegulatorySimulationProperties.RFBAINITIALVARIBLESSTATE, initvarstate);
	}*/

}
