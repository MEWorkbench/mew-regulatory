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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;

public class ReducedGeneSteadyStateSimulationResult {
	

	protected FluxValueMap fluxValues;
	protected Double OFvalue;
	protected String OFString;
	protected LPSolutionType solutionType;
	

	public ReducedGeneSteadyStateSimulationResult(FluxValueMap fluxValues, Double oFvalue, String oFString,
			LPSolutionType solutionType) {
		super();
		this.fluxValues = fluxValues;
		
		if(solutionType.equals(LPSolutionType.OPTIMAL) || solutionType.equals(LPSolutionType.FEASIBLE))
			OFvalue = oFvalue;
		else
			OFvalue =0.0;
		OFString = oFString;
		this.solutionType = solutionType;
	}
	
	
	
	public FluxValueMap getFluxValues() {
		return fluxValues;
	}
	public void setFluxValues(FluxValueMap fluxValues) {
		this.fluxValues = fluxValues;
	}
	public Double getOFvalue() {
		return OFvalue;
	}
	public void setOFvalue(Double oFvalue) {
		OFvalue = oFvalue;
	}
	public String getOFString() {
		return OFString;
	}
	public void setOFString(String oFString) {
		OFString = oFString;
	}
	public LPSolutionType getSolutionType() {
		return solutionType;
	}
	public void setSolutionType(LPSolutionType solutionType) {
		this.solutionType = solutionType;
	}

	
	public static ReducedGeneSteadyStateSimulationResult newInstance(FluxValueMap fluxValues, Double oFvalue, String oFString,
			LPSolutionType solutionType) {
		return new ReducedGeneSteadyStateSimulationResult((FluxValueMap) fluxValues.clone(),oFvalue,oFString,solutionType);
	}
	
	
	public static ReducedGeneSteadyStateSimulationResult newInstance(SteadyStateSimulationResult simures) {
		return new ReducedGeneSteadyStateSimulationResult((FluxValueMap) simures.getFluxValues().clone(),simures.getOFvalue(),simures.getOFString(),simures.getSolutionType());
	}
	
}
