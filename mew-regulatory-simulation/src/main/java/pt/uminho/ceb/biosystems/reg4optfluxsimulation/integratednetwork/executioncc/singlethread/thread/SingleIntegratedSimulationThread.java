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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.thread;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.components.IntegratedSimulationThread;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;

public class SingleIntegratedSimulationThread extends IntegratedSimulationThread<IntegratedSimulationMethodResult>{
	
	protected RegulatoryGeneticConditions geneticConditions;
	
	
	
	public SingleIntegratedSimulationThread(IIntegratedStedystateModel model,
			IntegratedSimulationOptionsContainer simulationoptions,
			EnvironmentalConditions environmentalConditions) {
		super(model, environmentalConditions, simulationoptions);
       
	}
	

	public SingleIntegratedSimulationThread(IIntegratedStedystateModel model, 
			IntegratedSimulationOptionsContainer simulationoptions,
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions) {
		super(model, environmentalConditions, simulationoptions);
		setGeneticConditions(geneticConditions);
       
	}


	@Override
	public IntegratedSimulationMethodResult getSimulationResults() {
		return results;
	}

	@Override
	protected IntegratedSimulationMethodResult executeSimulationProcess() throws Exception {
		IntegratedSimulationMethodResult result=(IntegratedSimulationMethodResult) controlcenter.simulate();
		System.out.println("Cond: "+currentEnvironmentalConditionname+" Gene: "+controlcenter.getGeneticConditions()+" sol: "+result.getOFvalue());
		return result;
	}

}
