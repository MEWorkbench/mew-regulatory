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

import java.util.ArrayList;

import org.javatuples.Pair;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.AbstractThreadSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.SimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic.MultipleMetabolicSimulationResultsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.executors.MultiThreadSimulationExecutorCallableTasks;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.tasks.SingleMetabolicSimulationTask;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.thread.SingleMetabolicSimulationThread;

public class MultiThreadMetabolicSimulationControlCenterOneByThread extends AbstractThreadSimulationControlCenter<MultipleMetabolicSimulationResultsContainer>{


	protected IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions=null;
	protected IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions=null;
	protected int nprocs=1;
	
	protected ArrayList<SingleMetabolicSimulationTask> simulationtasklist;
	protected ArrayList<Pair<String, String>> mapConditionGeneSimulation;
	protected ArrayList<SteadyStateSimulationResult> results;
	protected boolean simulationsfinished=false;
	
	
	
	
	public MultiThreadMetabolicSimulationControlCenterOneByThread(ISteadyStateGeneReactionModel model) {
		super(model);
	}
	
	
	public MultiThreadMetabolicSimulationControlCenterOneByThread(ISteadyStateGeneReactionModel model,
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
	public MultipleMetabolicSimulationResultsContainer getSimulationResults() {
		MultipleMetabolicSimulationResultsContainer container=new MultipleMetabolicSimulationResultsContainer();

		for (int i = 0; i < mapConditionGeneSimulation.size(); i++) {
			Pair<String, String> condvsgene=mapConditionGeneSimulation.get(i);
			String cond=condvsgene.getValue0();
			String geneid=condvsgene.getValue1();
			container.appendResult(cond,geneid,results.get(i));
		}

	    return container;
	}
	
	@Override
	public void reset() {
		this.simulationtasklist=new ArrayList<>();
        this.mapConditionGeneSimulation=new ArrayList<>();
		this.simulationsfinished=false;
		
	}
	
	
	
	@Override
	public void execute() throws Exception {
		reset();
		
		if(simulationEnvironmentalConditions!=null){
			  for (int i = 0; i < simulationEnvironmentalConditions.size(); i++) {
				 String envcondid=simulationEnvironmentalConditions.getKeyAt(i);
				 EnvironmentalConditions envcond=simulationEnvironmentalConditions.getValueAt(i);
				
				 if(geneticConditions!=null){
					 for (int j = 0; j < geneticConditions.size(); j++) {
						 String geneid=geneticConditions.getKeyAt(j);
						 RegulatoryGeneticConditions genecondition=geneticConditions.get(geneid);
					     addThread(envcondid,envcond, genecondition); 
					     Pair<String, String> mapcondvsgene=new Pair<String, String>(envcondid, geneid);
					     mapConditionGeneSimulation.add(mapcondvsgene);
					 }
				 }
				 else{
					 addThread(envcondid,envcond, null);
					 Pair<String, String> mapcond=new Pair<String, String>(envcondid, null);
					 mapConditionGeneSimulation.add(mapcond);
				 }
					 
			  }
		  }
		  else{
			  if(geneticConditions!=null){
				  for (int j = 0; j < geneticConditions.size(); j++) {
						  String geneid=geneticConditions.getKeyAt(j);
						  RegulatoryGeneticConditions genecondition=geneticConditions.get(geneid);
					      addThread(null,null, genecondition); 
					      Pair<String, String> mapgene=new Pair<String, String>(null, geneid);
					      mapConditionGeneSimulation.add(mapgene);
					 }
			  }
		  }
		  

		 results=(ArrayList<SteadyStateSimulationResult>) MultiThreadSimulationExecutorCallableTasks.execute(nprocs, simulationtasklist);
	     System.out.println("Simulations finished: "+simulationsfinished);
		
	}
	
	
	
	
	
	protected void addThread(String envcondname, EnvironmentalConditions environmentalConditions, RegulatoryGeneticConditions geneticConditions) throws Exception{
		SingleMetabolicSimulationThread thread=new SingleMetabolicSimulationThread(model,simulationoptions, environmentalConditions, geneticConditions);
		thread.setEnvironmentalConditionName(envcondname);
		simulationtasklist.add(new SingleMetabolicSimulationTask(thread));
	}

	

}
