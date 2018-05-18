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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components;

import java.util.ArrayList;
import java.util.HashMap;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.DynamicRFBAControlCenter;

public class RFBASimulationOptionsContainer extends IntegratedSimulationOptionsContainer{

	private static final long serialVersionUID = 1L;
	
	private double initBiomass;
	private double timestep;
	private int numbersteps;
	
	private IndexedHashMap<String, Double> substrateconcentrations;
	private ArrayList<String> excludeduptakereactions;
    
	// key must have same name of environmental condition
	private IndexedHashMap<String, IndexedHashMap<String, Double>> initialsubstrateconcentrationmap;
	private IndexedHashMap<String, ArrayList<String>> excludeduptakereactionsmap;
	
	
	public RFBASimulationOptionsContainer(){
		setSimulationMethod(IntegratedSimulationMethod.DYNAMICRFBA);
	}
	public double getInitialBiomass() {
		return initBiomass;
	}


	public void setInitialBiomass(double initBiomass) {
		this.initBiomass = initBiomass;
	}


	public double getTimeStep() {
		return timestep;
	}


	public void setTimeStep(double timestep) {
		this.timestep = timestep;
	}


	public int getNumberSteps() {
		return numbersteps;
	}


	public void setNumberSteps(int numbersteps) {
		this.numbersteps = numbersteps;
	}


	public IndexedHashMap<String, Double> getInitialSubstrateConcentrations() {
		return substrateconcentrations;
	}


	public void setInitialSubstrateConcentrations(IndexedHashMap<String, Double> initialsubstrateconc) {
		this.substrateconcentrations = initialsubstrateconc;
	}
	
	


	/**
	 * @return the initialsubstrateconcentrationmap
	 */
	public IndexedHashMap<String, IndexedHashMap<String, Double>> getMapInitialsubstrateconcentration() {
		return initialsubstrateconcentrationmap;
	}
	/**
	 * @param initialsubstrateconcentrationmap the initialsubstrateconcentrationmap to set
	 */
	public void setMapOfInitialsubstrateconcentration(IndexedHashMap<String, IndexedHashMap<String, Double>> initialsubstrateconcentrationmap) {
		this.initialsubstrateconcentrationmap = initialsubstrateconcentrationmap;
	}
	
	
	public IndexedHashMap<String, Double> getInitialSubstrateConcentrationsForID(String id){
		if(initialsubstrateconcentrationmap!=null && initialsubstrateconcentrationmap.containsKey(id))
			return initialsubstrateconcentrationmap.get(id);
		return null;
	}
	
	
	public IndexedHashMap<String, ArrayList<String>> getExcludedUptakeReactionsMap() {
		return excludeduptakereactionsmap;
	}
	
	
	public void setExcludedUptakeReactionsMap(IndexedHashMap<String, ArrayList<String>> excludeduptakereactionsmap) {
		this.excludeduptakereactionsmap = excludeduptakereactionsmap;
	}
	
	public ArrayList<String> getUptakeReactionToExclude(String condid){
		if(excludeduptakereactionsmap!=null && excludeduptakereactionsmap.containsKey(condid))
			return excludeduptakereactionsmap.get(condid);
		return null;
	}
	
	/*public HashSet<String> getInitialGenesWithTrueState() {
		return initgeneswithtruestate;
	}


	public void setInitialGenesWithTrueState(HashSet<String> initgeneswithtruestate) {
		this.initgeneswithtruestate = initgeneswithtruestate;
	}*/


	
	public ArrayList<String> getUptakeReactionsToExcludeFromInitialConfiguration() {
		return excludeduptakereactions;
	}


	public void setUptakeReactionsToExcludeFromInitialConfiguration(ArrayList<String> listreactions) {
		this.excludeduptakereactions = listreactions;
	}

    @Override
	public AbstractIntegratedSimulationControlCenter getSimulationControlCenterInstance(IIntegratedStedystateModel model, EnvironmentalConditions environmentalConditions) {
    	
    	if(objfunct==null) {
			objfunct=new HashMap<>();
			objfunct.put(model.getBiomassFlux(), 1.0);
		}
    	
    	DynamicRFBAControlCenter controlcenter= new DynamicRFBAControlCenter(model, environmentalConditions, geneticconditions, solver, initBiomass, timestep, numbersteps, substrateconcentrations);
    	controlcenter.setComponentsBooleanInitialState(getInitialComponentsbooleanState());
    	if(excludeduptakereactions!=null && excludeduptakereactions.size()>0)
    		//excludeduptakereactions
			controlcenter.setUptakeReactionsToExcludeFromInitialConfiguration(excludeduptakereactions);
	
		if(getInitialGenesONState()!=null && getInitialGenesONState().size()>0 )
			controlcenter.setGeneGroupToStartWithTrueState(getInitialGenesONState());
		
		controlcenter.setObjectiveFunction(objfunct);
		
		return controlcenter;
		
	}

}
