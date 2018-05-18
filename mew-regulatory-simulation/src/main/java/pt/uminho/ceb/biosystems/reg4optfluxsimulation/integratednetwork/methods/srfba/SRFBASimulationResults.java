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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.srfba;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.OptfluxIntegratedSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.TypeIntegratedSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public class SRFBASimulationResults extends OptfluxIntegratedSimulationResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SRFBASimulationResults(ISteadyStateModel model, EnvironmentalConditions environmentalConditions,
			GeneticConditions initialintegratedgeneticConditions,RegulatoryGeneticConditions calculatedgeneticconditions, FluxValueMap fluxValues, String solverOutput,
			Double oFvalue, String oFString, LPSolutionType solutionType,
			IRegulatoryModelSimulationResult regulatoryresults,
			IntegratedNetworkInitialStateContainer initialregulatoryconditions) {
		super(model, environmentalConditions, initialintegratedgeneticConditions,calculatedgeneticconditions, IntegratedSimulationMethod.SRFBA.getName(), fluxValues, solverOutput, oFvalue, oFString,
				solutionType, IntegratedSimulationMethod.SRFBA, regulatoryresults, initialregulatoryconditions);

	}
	
	@Override
	public TypeIntegratedSimulationResult getTypeResult() {
		return TypeIntegratedSimulationResult.SRFBA;
	}

}
