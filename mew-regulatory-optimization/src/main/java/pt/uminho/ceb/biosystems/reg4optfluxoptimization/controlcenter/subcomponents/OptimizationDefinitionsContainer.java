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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.controlcenter.subcomponents;

import java.io.Serializable;
import java.util.ArrayList;

import pt.uminho.ceb.biosystems.jecoli.algorithm.AlgorithmTypeEnum;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;

public class OptimizationDefinitionsContainer implements Serializable{


	private static final long serialVersionUID = 1L;
	
	
	protected int numberOfFunctionEvaluations;
	protected int numberOfKnockouts;
	protected boolean isVariableSizeGenome;
	protected AlgorithmTypeEnum algorithmType;
    protected IntegratedSimulationOptionsContainer simulationoptions;
    protected EnvironmentalConditions envconds;
    protected ArrayList<String> criticalgenes;
	

	public OptimizationDefinitionsContainer(int numberOfFunctionEvaluations,
			int numberOfKnockouts,
			boolean isVariableSizeGenome, 
			AlgorithmTypeEnum algorithmType, 
			IntegratedSimulationOptionsContainer simulationoptions) {
		this.numberOfFunctionEvaluations = numberOfFunctionEvaluations;
		this.numberOfKnockouts = numberOfKnockouts;
		this.isVariableSizeGenome = isVariableSizeGenome;
		this.algorithmType = algorithmType;
		this.simulationoptions=simulationoptions;
	}
	
	public OptimizationDefinitionsContainer(int numberOfFunctionEvaluations,
			int numberOfKnockouts,
			boolean isVariableSizeGenome, 
			AlgorithmTypeEnum algorithmType, 
			IntegratedSimulationOptionsContainer simulationoptions,
			EnvironmentalConditions envconds) {
		this.numberOfFunctionEvaluations = numberOfFunctionEvaluations;
		this.numberOfKnockouts = numberOfKnockouts;
		this.isVariableSizeGenome = isVariableSizeGenome;
		this.algorithmType = algorithmType;
		this.simulationoptions=simulationoptions;
		this.envconds=envconds;
	}
	
	
	
	
	public int getNumberOfFunctionEvaluations() {
		return numberOfFunctionEvaluations;
	}
	public void setNumberOfFunctionEvaluations(int numberOfFunctionEvaluations) {
		this.numberOfFunctionEvaluations = numberOfFunctionEvaluations;
	}
	public int getNumberOfKnockouts() {
		return numberOfKnockouts;
	}
	public void setNumberOfKnockouts(int numberOfKnockouts) {
		this.numberOfKnockouts = numberOfKnockouts;
	}
	public boolean isVariableSizeGenome() {
		return isVariableSizeGenome;
	}
	public void setVariableSizeGenome(boolean isVariableSizeGenome) {
		this.isVariableSizeGenome = isVariableSizeGenome;
	}


	public AlgorithmTypeEnum getAlgorithmType() {
		return algorithmType;
	}


	public void setAlgorithmType(AlgorithmTypeEnum algorithmType) {
		this.algorithmType = algorithmType;
	}


	public IntegratedSimulationOptionsContainer getUsedSimulationOptions() {
		return simulationoptions;
	}


	public void setUsedSimulationOptions(IntegratedSimulationOptionsContainer simulationoptions) {
		this.simulationoptions = simulationoptions;
	}

	public EnvironmentalConditions getEnvironmentalconditions() {
		return envconds;
	}

	public void setEnvironmentalconditions(EnvironmentalConditions envconds) {
		this.envconds = envconds;
	}

	public ArrayList<String> getCriticalGenes() {
		return criticalgenes;
	}

	public void setCriticalGenes(ArrayList<String> criticalgenes) {
		this.criticalgenes = criticalgenes;
	}


	
    

	
	
	
	
	
	
	
	
	
	
	
	
	

}
