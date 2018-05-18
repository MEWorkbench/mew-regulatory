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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.integrated;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.AbstractSimulationResultsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;

public class IntegratedReducedSimulationResultsContainer extends AbstractSimulationResultsContainer<ReducedGeneIntegratedSimulationResult> {

	
	public IntegratedReducedSimulationResultsContainer(ReducedGeneIntegratedSimulationResult results, String knockoutedgeneid,String environmentalconditionid) {
		super(results, knockoutedgeneid, environmentalconditionid);
	}
	

	public IntegratedReducedSimulationResultsContainer(IntegratedSimulationMethodResult results, String knockoutedgeneid,String environmentalconditionid) {
		super(ReducedGeneIntegratedSimulationResult.newInstance(results), knockoutedgeneid, environmentalconditionid);
	}
	
	public IntegratedReducedSimulationResultsContainer(FluxValueMap fluxvalues, Double oFvalue,String oFString,LPSolutionType solutionType, String knockoutedgeneid,String environmentalconditionid) {
		super(new ReducedGeneIntegratedSimulationResult(fluxvalues, oFvalue, oFString, solutionType), knockoutedgeneid, environmentalconditionid);
	}

	@Override
	public FluxValueMap getFluxMap() {
		return results.getFluxValues();
	}

	@Override
	public double getOptimizationValue() {
		return results.getOFvalue();
	}
	
	
	

}
