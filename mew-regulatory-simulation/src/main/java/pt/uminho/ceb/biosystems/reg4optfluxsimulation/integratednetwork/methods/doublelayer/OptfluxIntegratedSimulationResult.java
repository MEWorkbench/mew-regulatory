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

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.TypeIntegratedSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public class OptfluxIntegratedSimulationResult extends IntegratedSimulationMethodResult{


	private static final long serialVersionUID = 1L;
	
	             
/*	
	 * map of genes in regulatory network indexed in the same way of regulatoryAtractorsets
	 * String =gene id
	 * Gene = gene object
	 
	protected IndexedHashMap<String,RegulatoryGene> genemapindex;
	
	
	 * map of gene name to gene id, to simplify the search of genes by name
	 * String 1 = gene name
	 * String 2 = gene id
	 
	protected IndexedHashMap<String, String> mapgenenamevsgeneid;
	
	
	 * map of final state of all genes, saves the comparison of gene state between the different attractors
	 * String = gene id
	 * Boolean = state of gene (false or true)
	 
	protected IndexedHashMap<String, Boolean> mapoffinalgenestate=null;
	*/
	protected String metabolicsimulationmethod = null;
	protected RegulatorySimulationMethod regulatorysimulationmethod=null;
	
	protected IntegratedNetworkInitialStateContainer initialregulatoryconditions;
	
	
	public OptfluxIntegratedSimulationResult(ISteadyStateModel model,
			EnvironmentalConditions environmentalConditions,
			GeneticConditions initialintegratedgeneticConditions,
			RegulatoryGeneticConditions calculatedgeneticconditions,
			String method,
			FluxValueMap fluxValues, 
			String solverOutput, 
			Double oFvalue,
			String oFString, 
			LPSolutionType solutionType,
			IntegratedSimulationMethod integratedsimulationmethod,
			IRegulatoryModelSimulationResult regulatoryresults,
			IntegratedNetworkInitialStateContainer initialregulatoryconditions) {
		super(model, environmentalConditions, initialintegratedgeneticConditions, calculatedgeneticconditions,method, fluxValues,
				solverOutput, oFvalue, oFString, solutionType, integratedsimulationmethod, regulatoryresults);	  
		
		
		this.initialregulatoryconditions=initialregulatoryconditions;
	}
	
	
	public OptfluxIntegratedSimulationResult(ISteadyStateModel model,
			EnvironmentalConditions environmentalConditions,
			GeneticConditions integratedgeneticConditions,
			RegulatoryGeneticConditions calculatedgeneticconditions,
			String method,
			FluxValueMap fluxValues, 
			String solverOutput, 
			Double oFvalue,
			String oFString, 
			LPSolutionType solutionType,
			IntegratedSimulationMethod integratedsimulationmethod,
			IRegulatoryModelSimulationResult regulatoryresults,
			IntegratedNetworkInitialStateContainer initialregulatoryconditions,
			String metabolicSimulationMethod
			) {
		super(model, environmentalConditions, integratedgeneticConditions,calculatedgeneticconditions, method, fluxValues,
				solverOutput, oFvalue, oFString, solutionType,integratedsimulationmethod,regulatoryresults);

		this.metabolicsimulationmethod = metabolicSimulationMethod;
		this.initialregulatoryconditions=initialregulatoryconditions;
	
	  
	}


	
	public String getMetabolicSimulationMethod() {
		return metabolicsimulationmethod;
	}

	public void setMetSimulMethod(String metSimulMethod) {
		this.metabolicsimulationmethod = metSimulMethod;
	}
	
	public void setRegulatorySimulationMethod(RegulatorySimulationMethod regulatorysimulationmethod) {
		this.regulatorysimulationmethod=regulatorysimulationmethod;
	}
	
	

	public RegulatorySimulationMethod getRegulatorysimulationmethod() {
		return regulatorysimulationmethod;
	}


	public IntegratedNetworkInitialStateContainer getInitialRegulatoryConditionsContainer() {
		return initialregulatoryconditions;
	}


	@Override
	public TypeIntegratedSimulationResult getTypeResult() {
		return TypeIntegratedSimulationResult.TWOSTAGE;
	}
	
	

  
	

}
