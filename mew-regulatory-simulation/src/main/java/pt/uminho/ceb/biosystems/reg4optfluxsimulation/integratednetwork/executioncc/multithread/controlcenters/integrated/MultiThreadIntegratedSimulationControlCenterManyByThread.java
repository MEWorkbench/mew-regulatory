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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.integrated;

import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.integrated.ListIntegratedReducedSolutions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.integrated.MultipleIntegratedSimulationResultsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.executors.MultiThreadSimulationExecutorRunneableSim;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.threads.integrated.IntegratedSimulationCCPerGeneSetThread;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.components.ISimulationThread;

public class MultiThreadIntegratedSimulationControlCenterManyByThread extends AbstractMultiThreadIntegratedSimulationControlCenter<MultipleIntegratedSimulationResultsContainer>{

	
	protected MultipleIntegratedSimulationResultsContainer results=null;
	protected boolean simulationsfinished=false;
	
	
	public MultiThreadIntegratedSimulationControlCenterManyByThread(IIntegratedStedystateModel model,Integer numberprocesses) {
		super(model,numberprocesses);
	}
	
	public MultiThreadIntegratedSimulationControlCenterManyByThread(IIntegratedStedystateModel model,
			IntegratedSimulationOptionsContainer simulationoptions,
			Integer numberprocesses) {
		super(model, simulationoptions,numberprocesses);
	}
	

	public MultiThreadIntegratedSimulationControlCenterManyByThread(IIntegratedStedystateModel model,
			IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions,
			ArrayList<String> testknockoutgenes, IntegratedSimulationOptionsContainer options, Integer numberprocesses)
			throws Exception {
		super(model, simulationEnvironmentalConditions, testknockoutgenes, options, numberprocesses);
		// TODO Auto-generated constructor stub
	}

	public MultiThreadIntegratedSimulationControlCenterManyByThread(IIntegratedStedystateModel model,
			IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions,
			IndexedHashMap<String, RegulatoryGeneticConditions> geneconditions,
			IntegratedSimulationOptionsContainer options, Integer numberprocesses) throws Exception {
		super(model, simulationEnvironmentalConditions, geneconditions, options, numberprocesses);
	}

	

	
	
	
	
	@Override
	public boolean isSimulationsFinished() {
		return simulationsfinished;
	}

	@Override
	public void execute() throws Exception {
		reset();
		
		IndexedHashMap<String, ISimulationThread<ListIntegratedReducedSolutions>> tasklist=new IndexedHashMap<>();
		
		for (int i = 0; i < simulationEnvironmentalConditions.size(); i++) {
			 String envcondid=simulationEnvironmentalConditions.getKeyAt(i);
			 EnvironmentalConditions envcond=simulationEnvironmentalConditions.getValueAt(i);
			 
			 IntegratedSimulationCCPerGeneSetThread thread= new IntegratedSimulationCCPerGeneSetThread(((IIntegratedStedystateModel)model), envcond, simulationoptions, geneticConditions);
			 thread.setEnvironmentalConditionName(envcondid);
			 tasklist.put(envcondid, thread);
			
		}
		
		IndexedHashMap<String,ListIntegratedReducedSolutions> tmpresults=MultiThreadSimulationExecutorRunneableSim.execute(nprocs, tasklist);
		
		for (int i = 0; i <tmpresults.size(); i++) {
			results.appendResults(tmpresults.getValueAt(i));
		}
		
		simulationsfinished=true;
		
	}

	@Override
	public MultipleIntegratedSimulationResultsContainer getSimulationResults() {
		return results;
	}

	@Override
	public void reset() {
		simulationsfinished=false;
		results=new MultipleIntegratedSimulationResultsContainer();
		
	}


	
	
	
	
	
		
	

}
