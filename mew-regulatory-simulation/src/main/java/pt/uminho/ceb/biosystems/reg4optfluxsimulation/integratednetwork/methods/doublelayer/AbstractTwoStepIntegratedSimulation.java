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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.ManagerExceptionUtils;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IIntegratedSteadyStateSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;

public abstract class AbstractTwoStepIntegratedSimulation implements IIntegratedSteadyStateSimulationMethod, Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The integratedmodel. */
	protected ISteadyStateModel integratedmodel;
	
	/** The possible properties. */
	protected Set<String> possibleProperties;
	
	/** The mandatory props. */
	protected Set<String> mandatoryProperties;
	
	/** The propreties. */
	protected Map<String, Object> propertiesmap;
	
	/** The metabolic simulationmethod. */
	protected String metabolicSimulationMethod;
	
	protected String regulatoryNetworkSimulationMethod;
	
	protected Map<String, Double> obj_funct=null;
	
 /**
  * Instantiates a new abstract two step integrated simulation.
  *
  * @param model  steady state model
  */
 public AbstractTwoStepIntegratedSimulation(ISteadyStateModel model){
		this.integratedmodel = model;
		initPropsKeys();
	
	}
	
    /**
     * Inits the props keys.
     */
    protected void initPropsKeys(){
    
		mandatoryProperties = new HashSet<String>();
		
		
		propertiesmap = new HashMap<String, Object>();
		possibleProperties = new HashSet<String>();
		possibleProperties.add(RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS);
		possibleProperties.add(RegulatorySimulationProperties.SOLVER);
		possibleProperties.add(SimulationProperties.ENVIRONMENTAL_CONDITIONS);
		possibleProperties.add(RegulatorySimulationProperties.VARIABLES_CONTAINER);
	    possibleProperties.add(RegulatorySimulationProperties.OBJECTIVE_FUNCTION);
	   
		
		
	
	}
	
 


	@Override
	public ISteadyStateModel getModel() {
		return this.integratedmodel;
	}
	
	
	
	
	/**
	 * Gets the checks if is maximization.
	 *
	 * @return the checks if is maximization
	 * @throws PropertyCastException the property cast exception
	 * @throws MandatoryPropertyException the mandatory property exception
	 */
	public Boolean getIsMaximization()throws PropertyCastException, MandatoryPropertyException {
		return (Boolean)ManagerExceptionUtils.testCast(propertiesmap, Boolean.class, RegulatorySimulationProperties.IS_MAXIMIZATION, true);
	}
	
	
	/**
	 * Gets the metabolic simulation method.
	 *
	 * @return the metabolic simulation method
	 * @throws PropertyCastException the property cast exception
	 * @throws MandatoryPropertyException the mandatory property exception
	 */
	
	public abstract String getMetabolicSimulationMethod()throws PropertyCastException, MandatoryPropertyException;
	public abstract RegulatorySimulationMethod getRegulatoryNetworkSimulationMethod()throws PropertyCastException, MandatoryPropertyException;

	
	
	@SuppressWarnings("unchecked")
	public Map<String, Double> getObjectiveFunction() throws PropertyCastException, MandatoryPropertyException{
		
		Map<String, Double> objfunct = null;
		try {
			objfunct = ManagerExceptionUtils.testCast(propertiesmap, Map.class, SimulationProperties.OBJECTIVE_FUNCTION, false);
		} catch (Exception e) {
			objfunct = new HashMap<String, Double>();
			
			objfunct.put(this.integratedmodel.getBiomassFlux(), 1.0);
		
		}
		
		return objfunct;
	}
	

	
	@Override
	public EnvironmentalConditions getEnvironmentalConditions()throws PropertyCastException, MandatoryPropertyException {
		return (EnvironmentalConditions)ManagerExceptionUtils.testCast(propertiesmap, EnvironmentalConditions.class, RegulatorySimulationProperties.ENVIRONMENTAL_CONDITIONS, true);
	}

	
	
	
	@Override
	public void setEnvironmentalConditions(EnvironmentalConditions environmentalConditions) {
		setProperty(RegulatorySimulationProperties.ENVIRONMENTAL_CONDITIONS, environmentalConditions);
		
	}

	
	@Override
	public GeneticConditions getGeneticConditions()throws PropertyCastException, MandatoryPropertyException {
		return (RegulatoryGeneticConditions) ManagerExceptionUtils.testCast(propertiesmap, RegulatoryGeneticConditions.class, RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS, true);
	}

	@Override
	public void setGeneticConditions(GeneticConditions geneconds) {
		setProperty(RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS, geneconds);
	}
	
	protected IntegratedNetworkInitialStateContainer getIntegratedVariablesContainer() throws PropertyCastException, MandatoryPropertyException{
		
		return  (IntegratedNetworkInitialStateContainer)ManagerExceptionUtils.testCast(propertiesmap, IntegratedNetworkInitialStateContainer.class, RegulatorySimulationProperties.VARIABLES_CONTAINER, true);
	}
	
	

	

/*	public boolean getIndependentTransFactorsInitialState()throws PropertyCastException, MandatoryPropertyException { 
		boolean initTFstate=true;
		try {
			initTFstate=ManagerExceptionUtils.testCast(propertiesmap, Boolean.class, RegulatorySimulationProperties.INDEPENDENTTRANSFACTORSSTATE, true);
		} catch (Exception e) {
			initTFstate=true; 
		}
		return  initTFstate;
	}


	public void setIndependentTransFactorsInitialState(boolean state) {
		setProperty(RegulatorySimulationProperties.INDEPENDENTTRANSFACTORSSTATE, state);	
	}*/


	
	
	@Override
	public Set<String> getPossibleProperties() {
		return this.possibleProperties;
	}

	
	@Override
	public Set<String> getMandatoryProperties() {
		return this.mandatoryProperties;
	}

	
	@Override
	public void setProperty(String m, Object o) {
		propertiesmap.put(m, o);
	}
	
	@Override
	public <T> T getProperty(String k) {
		return (T) propertiesmap.get(k);
	}

	
	
	public void putAllProperties(Map<String, Object> properties) {
		this.propertiesmap.putAll(properties);
	}
	
	public void setSolver(String solver){
		setProperty(RegulatorySimulationProperties.SOLVER, solver);
	}
	
    public String getSolverType()throws PropertyCastException, MandatoryPropertyException {
		return (String) ManagerExceptionUtils.testCast(propertiesmap, String.class, RegulatorySimulationProperties.SOLVER, true);
	}
	

    @Override
    public void clearAllProperties() {
    	// TODO Auto-generated method stub
    	
    }
    

    @Override
	public boolean checkIfMandatoryPropertiesSatisfied(Map<String, Object> properties) {
		
		for (String mprop : mandatoryProperties) {
			if(!properties.containsKey(mprop))
				return false;
		}
		
		return true;
	}
	



}
