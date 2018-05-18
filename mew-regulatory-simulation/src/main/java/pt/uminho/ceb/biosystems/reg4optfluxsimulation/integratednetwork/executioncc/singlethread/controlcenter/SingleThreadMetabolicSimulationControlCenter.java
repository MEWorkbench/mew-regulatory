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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.controlcenter;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.AbstractThreadSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.SimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.thread.SingleMetabolicSimulationThread;

public class SingleThreadMetabolicSimulationControlCenter extends AbstractThreadSimulationControlCenter<SteadyStateSimulationResult>{

	protected EnvironmentalConditions environmentalConditions;
	protected RegulatoryGeneticConditions geneticConditions;
	protected SteadyStateSimulationResult results;
	
	public SingleThreadMetabolicSimulationControlCenter(ISteadyStateGeneReactionModel model,
			SimulationOptionsContainer simulationoptions, 
			EnvironmentalConditions environmentalConditions) {
		super(model, simulationoptions);
		this.environmentalConditions=environmentalConditions;
	}
	
	public SingleThreadMetabolicSimulationControlCenter(ISteadyStateGeneReactionModel model,
			SimulationOptionsContainer simulationoptions, 
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions) {
		this(model,simulationoptions,environmentalConditions);
		this.geneticConditions=geneticConditions;
	}

	@Override
	public void execute() throws Exception {
		SingleMetabolicSimulationThread thread=new SingleMetabolicSimulationThread(model,simulationoptions, environmentalConditions, geneticConditions);
		thread.run();
		results=thread.getSimulationResults();
	}

	@Override
	public SteadyStateSimulationResult getSimulationResults() {
		return results;
	}



	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	
	
	

}
