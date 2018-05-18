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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.metabolic;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.AbstractThreadSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.SimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic.ListMetabolicSolutions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic.MultipleMetabolicReducedSimulationResultsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.executors.MultiThreadSimulationExecutorRunneableSim;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.threads.metabolic.MetabolicSimulationCCPerGeneSetThread;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.components.ISimulationThread;

public class MultiThreadMetabolicSimulationControlCenterManyByThread extends AbstractThreadSimulationControlCenter<MultipleMetabolicReducedSimulationResultsContainer>{


	protected IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions=null;
	protected IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions=null;
	protected int nprocs=1;
	
	protected MultipleMetabolicReducedSimulationResultsContainer results;
	protected boolean simulationsfinished=false;
	
	
	
	
	public MultiThreadMetabolicSimulationControlCenterManyByThread(ISteadyStateGeneReactionModel model) {
		super(model);
	}
	
	
	public MultiThreadMetabolicSimulationControlCenterManyByThread(ISteadyStateGeneReactionModel model,
			IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions,
			IndexedHashMap<String, RegulatoryGeneticConditions> geneconditions,
			SimulationOptionsContainer options,
			Integer numberprocesses) {
		super(model,options);
		
		this.simulationEnvironmentalConditions=simulationEnvironmentalConditions;
		this.geneticConditions=geneconditions;
		if(numberprocesses!=null)
			nprocs=numberprocesses;
	}
	
	
	
	@Override
	public MultipleMetabolicReducedSimulationResultsContainer getSimulationResults() {
		return results;
	}
	
	@Override
	public void reset() {
		this.simulationsfinished=false;
		results=new MultipleMetabolicReducedSimulationResultsContainer();
		
	}
	
	
	
	@Override
	public  void execute() throws Exception {
		reset();
		
		IndexedHashMap<String, ISimulationThread<ListMetabolicSolutions>> tasklist=new IndexedHashMap<>();
		
		for (int i = 0; i < simulationEnvironmentalConditions.size(); i++) {
			 String envcondid=simulationEnvironmentalConditions.getKeyAt(i);
			 EnvironmentalConditions envcond=simulationEnvironmentalConditions.getValueAt(i);
			 MetabolicSimulationCCPerGeneSetThread thread=new MetabolicSimulationCCPerGeneSetThread(model, envcond, simulationoptions, geneticConditions);
			 thread.setEnvironmentalConditionName(envcondid);
			 tasklist.put(envcondid, thread);
		}
		
		
		IndexedHashMap<String,ListMetabolicSolutions> tmpresults=MultiThreadSimulationExecutorRunneableSim.execute(nprocs, tasklist);
		
		
		for (int i = 0; i <tmpresults.size(); i++) {
			results.appendResults(tmpresults.getValueAt(i));
		}
		
		simulationsfinished=true;

	}
		
	

}
