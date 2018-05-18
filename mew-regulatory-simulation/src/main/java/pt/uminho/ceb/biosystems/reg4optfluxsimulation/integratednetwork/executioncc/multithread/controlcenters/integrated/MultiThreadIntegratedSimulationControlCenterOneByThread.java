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

import org.javatuples.Pair;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.integrated.MultipleIntegratedSimulationResultsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.executors.MultiThreadSimulationExecutorCallableTasks;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.tasks.SingleIntegratedSimulationTask;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.thread.SingleIntegratedSimulationThread;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;

public class MultiThreadIntegratedSimulationControlCenterOneByThread extends AbstractMultiThreadIntegratedSimulationControlCenter<MultipleIntegratedSimulationResultsContainer>{

	

	protected ArrayList<SingleIntegratedSimulationTask> simulationtasklist;
	protected ArrayList<IntegratedSimulationMethodResult> results;
	protected ArrayList<Pair<String, String>> mapConditionGeneSimulation;
	protected boolean simulationsfinished=false;

	
	public MultiThreadIntegratedSimulationControlCenterOneByThread(IIntegratedStedystateModel model,Integer numberprocesses) {
		super(model,numberprocesses);
	}
	
	
	public MultiThreadIntegratedSimulationControlCenterOneByThread(IIntegratedStedystateModel model, IntegratedSimulationOptionsContainer simulationoptions,Integer numberprocesses) {
		super(model,simulationoptions,numberprocesses);
	}
	
	public MultiThreadIntegratedSimulationControlCenterOneByThread(IIntegratedStedystateModel model,
			IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions,
			ArrayList<String> testknockoutgenes, IntegratedSimulationOptionsContainer options,Integer numberprocesses) throws Exception {
		super(model, simulationEnvironmentalConditions, testknockoutgenes, options,numberprocesses);
	}


	public MultiThreadIntegratedSimulationControlCenterOneByThread(IIntegratedStedystateModel model,
			IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions,
			IndexedHashMap<String, RegulatoryGeneticConditions> geneconditions, IntegratedSimulationOptionsContainer options,
			Integer numberprocesses) throws Exception {
		super(model, simulationEnvironmentalConditions, geneconditions, options, numberprocesses);

	}


	@Override
	public void execute() throws Exception {
		  reset();
		  
		  if(envcondstogeneslinkscheme!=null && simulationEnvironmentalConditions!=null && geneticConditions!=null) {
			  
			  
			  for (int i = 0; i < envcondstogeneslinkscheme.size(); i++) {
				  String envcondid=envcondstogeneslinkscheme.getKeyAt(i);

				  EnvironmentalConditions envcond=simulationEnvironmentalConditions.get(envcondid);
				  if(envcond!=null) {
					  
					  ArrayList<String> geneids=envcondstogeneslinkscheme.get(envcondid);
					  if(geneids!=null)  
						  for (int j = 0; j < geneids.size(); j++) {
							  
							  RegulatoryGeneticConditions genecondition=geneticConditions.get(geneids.get(j));
							  
							  /*if(genecondition==null)
								  genecondition=RegulatoryGeneticConditions.getRegulatoryGeneticConditions(geneids.get(j), model);*/
							  
							  if(genecondition!=null) {
								  addThread(envcondid,envcond, genecondition);
								  Pair<String, String> mapcondvsgene=new Pair<String, String>(envcondid, geneids.get(j));
								  mapConditionGeneSimulation.add(mapcondvsgene);
								  
							  }
						  }
					  
				  }  
			  } 
		  } 
		  else if(simulationEnvironmentalConditions!=null){
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
		  
		  
		results=(ArrayList<IntegratedSimulationMethodResult>) MultiThreadSimulationExecutorCallableTasks.execute(nprocs, simulationtasklist);
		simulationsfinished=true;
		System.out.println("Simulations finished: "+simulationsfinished);
		
	}
	
	
	
	protected void addThread(String envcondname, EnvironmentalConditions environmentalConditions, RegulatoryGeneticConditions geneticConditions) throws Exception{
		SingleIntegratedSimulationThread thread=new SingleIntegratedSimulationThread(((IIntegratedStedystateModel)model),(IntegratedSimulationOptionsContainer) simulationoptions, environmentalConditions, geneticConditions);
		thread.setEnvironmentalConditionName(envcondname);
		simulationtasklist.add(new SingleIntegratedSimulationTask(thread));
	}
	
	

	@Override
	public MultipleIntegratedSimulationResultsContainer getSimulationResults() {

		MultipleIntegratedSimulationResultsContainer container=new MultipleIntegratedSimulationResultsContainer();

		for (int i = 0; i < mapConditionGeneSimulation.size(); i++) {
			Pair<String, String> condvsgene=mapConditionGeneSimulation.get(i);
			String cond=condvsgene.getValue0();
			String geneid=condvsgene.getValue1();
			container.appendResult(cond,geneid,results.get(i));
		}

	    return container;
	}

	@Override
	public boolean isSimulationsFinished() {
		return simulationsfinished;
	}


	@Override
	public void reset() {
		this.simulationtasklist=new ArrayList<>();
        this.mapConditionGeneSimulation=new ArrayList<>();
		this.simulationsfinished=false;
		
	}

}
