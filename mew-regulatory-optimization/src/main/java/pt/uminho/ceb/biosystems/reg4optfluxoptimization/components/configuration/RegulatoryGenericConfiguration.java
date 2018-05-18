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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.configuration;

import java.util.List;
import java.util.Map;

import pt.uminho.ceb.biosystems.jecoli.algorithm.components.terminationcriteria.ITerminationCriteria;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.terminationcriteria.InvalidTerminationCriteriaParameter;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.terminationcriteria.NumFunctionEvaluationsListenerHybridTerminationCriteria;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.archive.components.ArchiveManager;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.configuration.GenericConfiguration;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.configuration.GenericOptimizationProperties;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.objectivefunctions.IObjectiveFunction;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.strainoptimizationalgorithms.jecoli.JecoliOptimizationProperties;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;

public class RegulatoryGenericConfiguration extends GenericConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public RegulatoryGenericConfiguration() {
		super();
		loadMandatoryOptionalProperties();
	}
	
	public RegulatoryGenericConfiguration(Map<String,Object> propertyMapToCopy) {
		super(propertyMapToCopy);
		loadMandatoryOptionalProperties();
	}

	private void loadMandatoryOptionalProperties() {
		mandatoryPropertyMap.put(GenericOptimizationProperties.OPTIMIZATION_ALGORITHM, String.class);
		mandatoryPropertyMap.put(JecoliOptimizationProperties.IS_VARIABLE_SIZE_GENOME, Boolean.class);
		mandatoryPropertyMap.put(GenericOptimizationProperties.STEADY_STATE_GENE_REACTION_MODEL, ISteadyStateGeneReactionModel.class);
		mandatoryPropertyMap.put(GenericOptimizationProperties.SIMULATION_CONFIGURATION, IntegratedSimulationOptionsContainer.class);
		mandatoryPropertyMap.put(GenericOptimizationProperties.MAP_OF2_SIM, IndexedHashMap.class);
		mandatoryPropertyMap.put(JecoliOptimizationProperties.TERMINATION_CRITERIA, ITerminationCriteria.class);
		
		optionalPropertyMap.put(GenericOptimizationProperties.NOT_ALLOWED_IDS, List.class);
		optionalPropertyMap.put(GenericOptimizationProperties.MAX_SET_SIZE, Integer.class);
		optionalPropertyMap.put(SimulationProperties.ENVIRONMENTAL_CONDITIONS, EnvironmentalConditions.class);
	}
	
	public String getOptimizationAlgorithm(){
		return (String)getProperty(GenericOptimizationProperties.OPTIMIZATION_ALGORITHM);
	}
	
	public void setOptimizationAlgorithm(String optimizationAlgorithm){
		getPropertyMap().put(GenericOptimizationProperties.OPTIMIZATION_ALGORITHM, optimizationAlgorithm);
	}
	
	public int getNumberOfObjectives() {
		return getObjectiveFunctionsMap().size();
	}
	
	public int getMaxSetSize() {
		return getDefaultValue(GenericOptimizationProperties.MAX_SET_SIZE, 1);
	}
	
	public void setIsVariableSizeGenome(boolean isVariableSizeGenome) {
		propertyMap.put(JecoliOptimizationProperties.IS_VARIABLE_SIZE_GENOME, isVariableSizeGenome);
	}
	
	public void setSimulationSettingsContainer(IntegratedSimulationOptionsContainer simulationsettings){
		propertyMap.put(GenericOptimizationProperties.SIMULATION_CONFIGURATION, simulationsettings);
	}
	
	public IntegratedSimulationOptionsContainer getSimulationSettingsContainer(){
		return (IntegratedSimulationOptionsContainer) propertyMap.get(GenericOptimizationProperties.SIMULATION_CONFIGURATION);
	}
	
	public boolean getIsVariableSizeGenome() {
		return getDefaultValue(JecoliOptimizationProperties.IS_VARIABLE_SIZE_GENOME, true);
	}
	
	public void setNotAllowedIds(List<String> notAllowedIds) {
		propertyMap.put(GenericOptimizationProperties.NOT_ALLOWED_IDS, notAllowedIds);
	}
	
	public List<String> getNonAllowedIds() {
		return (List<String>) propertyMap.get(GenericOptimizationProperties.NOT_ALLOWED_IDS);
	}
	
	public void setMaxSetSize(int maxSetSize) {
		propertyMap.put(GenericOptimizationProperties.MAX_SET_SIZE, maxSetSize);
	}
	
	public ITerminationCriteria getTerminationCriteria() throws InvalidTerminationCriteriaParameter {
		return getDefaultValue(JecoliOptimizationProperties.TERMINATION_CRITERIA, new NumFunctionEvaluationsListenerHybridTerminationCriteria(50000));
	}
	
	public void setTerminationCriteria(ITerminationCriteria terminationCriteria) throws InvalidTerminationCriteriaParameter {
		getPropertyMap().put(JecoliOptimizationProperties.TERMINATION_CRITERIA, terminationCriteria);
	}
	
	public IndexedHashMap<IObjectiveFunction, String> getObjectiveFunctionsMap() {
		return (IndexedHashMap<IObjectiveFunction, String>) propertyMap.get(GenericOptimizationProperties.MAP_OF2_SIM);
	}
	
	public void setObjectiveFunctionsMap(IndexedHashMap<IObjectiveFunction, String> objectiveFunctionMap) {
		propertyMap.put(GenericOptimizationProperties.MAP_OF2_SIM, objectiveFunctionMap);
	}
	
	public void setEnvironmentalConditions(EnvironmentalConditions envconds) {
		propertyMap.put(SimulationProperties.ENVIRONMENTAL_CONDITIONS, envconds);
	}
	
	public EnvironmentalConditions getEnvironmentalConditons() {
		return (EnvironmentalConditions) propertyMap.get(SimulationProperties.ENVIRONMENTAL_CONDITIONS);
	}

	
	public ISteadyStateGeneReactionModel getSteadyStateModel() {
		return (ISteadyStateGeneReactionModel) propertyMap.get(GenericOptimizationProperties.STEADY_STATE_GENE_REACTION_MODEL);
	}
	
	public void setModel(ISteadyStateModel model) {
		propertyMap.put(GenericOptimizationProperties.STEADY_STATE_GENE_REACTION_MODEL, model);
	}

	public ArchiveManager getArchiveManager(){
		return (ArchiveManager) propertyMap.get(JecoliOptimizationProperties.ARCHIVE_MANAGER);
	}
	
	public void setArchiveManager(ArchiveManager archiveManager){
		propertyMap.put(JecoliOptimizationProperties.ARCHIVE_MANAGER, archiveManager);
	}
	
}
