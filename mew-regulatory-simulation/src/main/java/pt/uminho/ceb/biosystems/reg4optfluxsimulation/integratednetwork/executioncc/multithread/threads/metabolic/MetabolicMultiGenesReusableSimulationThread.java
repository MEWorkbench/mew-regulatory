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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.threads.metabolic;

import java.util.HashMap;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.AbstractSimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.SimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.components.ISimulationThread;

public abstract class MetabolicMultiGenesReusableSimulationThread<T> implements ISimulationThread<T>{

	
	protected AbstractSimulationSteadyStateControlCenter controlcenter;
	protected SimulationOptionsContainer simulationoptions;
	protected ISteadyStateGeneReactionModel model;
	protected EnvironmentalConditions environmentalConditions;
	protected String currentEnvironmentalConditionname;
	protected IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions;
	protected HashMap<String, Double> objfunction;
    protected T results;
	
	 public MetabolicMultiGenesReusableSimulationThread(ISteadyStateGeneReactionModel model,
			 EnvironmentalConditions environmentalConditions,
			 SimulationOptionsContainer simulationoptions){
		 
		  
			this.model=model;
			this.environmentalConditions=environmentalConditions;
		    this.simulationoptions=simulationoptions;
		    this.objfunction=simulationoptions.getObjfunct();
		    if(objfunction==null){
		    	HashMap<String, Double> objectiveFunction = new HashMap<String, Double>();
				objectiveFunction.put(model.getBiomassFlux(), 1.0);
				this.objfunction=objectiveFunction;
		    }      	
	 }
	 
	 public MetabolicMultiGenesReusableSimulationThread(ISteadyStateGeneReactionModel model,
			 EnvironmentalConditions environmentalConditions,
			 SimulationOptionsContainer simulationoptions,
			 IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions){
		 this(model,environmentalConditions,simulationoptions);
		 this.geneticConditions=geneticConditions;
	 }
	 
	 
	 
	 public void setGroupGeneticConditions(IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions){
		 this.geneticConditions=geneticConditions;
	 }
	 
	 public void setEnvironmentalConditionName(String envcondname) {
		 this.currentEnvironmentalConditionname=envcondname;
	 }
	
	 protected abstract T executeSimulationProcess() throws Exception;
	 
	 
	 protected synchronized void initControlCenter() throws Exception{
		 
		 controlcenter=new SimulationSteadyStateControlCenter(environmentalConditions, null, model, simulationoptions.getMetabolicSimulationMethod());
		 ((SimulationSteadyStateControlCenter) controlcenter).setFBAObj(objfunction);
		 ((SimulationSteadyStateControlCenter) controlcenter).setSolver(simulationoptions.getSolver());
		 ((SimulationSteadyStateControlCenter) controlcenter).setMaximization(simulationoptions.isMaximization());

		 
	 }
	
	
	
	 
	 
	    @Override
		public void run() {
		 	try {
				initControlCenter();
				results=executeSimulationProcess();
				/*System.out.println("Cond: "+environmentalConditions.getId()+"  GeneKnockout: "+controlcenter.getGeneticConditions().getGeneList());
				//System.out.println("Cond: "+environmentalConditions.getId()+"  ENVCONDS: "+controlcenter.getEnvironmentalConditions()+"  GeneKnockout: "+controlcenter.getGeneticConditions().getGeneList());
				results=executeSimulationProcess();*/
			} catch (Exception e) {
				LogMessageCenter.getLogger().addCriticalErrorMessage("Error in Simulation thread: ", e);
			}
		 	
	    }
}
