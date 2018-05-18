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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.tasks;

import java.util.concurrent.Callable;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic.ReducedGeneSteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.thread.SingleMetabolicSimulationThread;

public class SingleMetabolicSimulationTaskReducedSol implements Callable<ReducedGeneSteadyStateSimulationResult>{

	private SingleMetabolicSimulationThread task;
	
	public SingleMetabolicSimulationTaskReducedSol(SingleMetabolicSimulationThread task){
		this.task=task;
	}
	
	@Override
	public ReducedGeneSteadyStateSimulationResult call() throws Exception {
		task.run();
		SteadyStateSimulationResult res=task.getSimulationResults();
		
		ReducedGeneSteadyStateSimulationResult redsol=new ReducedGeneSteadyStateSimulationResult(null, res.getOFvalue(), res.getOFString(), res.getSolutionType());
		
		res=null;
		//LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage(String.valueOf(res.getOFvalue()));
		//System.out.println(res.getOFvalue());
		return redsol;
		//return runthread.getSimulationResult();
	}

}
