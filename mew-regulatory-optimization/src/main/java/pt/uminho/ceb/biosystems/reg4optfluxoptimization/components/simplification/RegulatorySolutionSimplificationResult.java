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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.simplification;

import java.io.Serializable;
import java.util.List;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.objectivefunctions.IObjectiveFunction;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;


public class RegulatorySolutionSimplificationResult implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected IntegratedSimulationMethodResult simulationResult;
	protected GeneticConditions simplifiedSolution;
	protected double[] fitnesses;
	protected List<IObjectiveFunction> objectiveFunctions;
	
	public RegulatorySolutionSimplificationResult(IntegratedSimulationMethodResult simInitial, GeneticConditions newGC, List<IObjectiveFunction> objectiveFunctions, double[] fitnesses) {
		this.simulationResult = simInitial;
		this.simplifiedSolution = newGC;
		this.objectiveFunctions = objectiveFunctions;
		this.fitnesses = fitnesses;
	}
	
	public RegulatorySolutionSimplificationResult(IntegratedSimulationMethodResult simResult, double[] fitnesses){
		this(simResult, null, null, fitnesses);		
	}
	

	public IntegratedSimulationMethodResult getSimulationResult() {
		return simulationResult;
	}
	
	public void setSimulationResult(IntegratedSimulationMethodResult simulationResult) {
		this.simulationResult = simulationResult;
	}
	
	public GeneticConditions getSimplifiedSolution() {
		return simplifiedSolution;
	}
	
	public void setSimplifiedSolution(GeneticConditions simplifiedSolution) {
		this.simplifiedSolution = simplifiedSolution;
	}

	/**
	 * @return the fitnesses
	 */
	public double[] getFitnesses() {
		return fitnesses;
	}
	
	
	
}
