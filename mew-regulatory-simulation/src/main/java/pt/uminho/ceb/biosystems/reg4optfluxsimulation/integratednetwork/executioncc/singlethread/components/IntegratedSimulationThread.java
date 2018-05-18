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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.components;

import java.util.HashMap;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.DynamicRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.IntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.SRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.RFBASimulationOptionsContainer;

public abstract class IntegratedSimulationThread<T> implements ISimulationThread<T>{

	
	protected AbstractIntegratedSimulationControlCenter controlcenter;
	protected IntegratedSimulationOptionsContainer simulationoptions;
	protected IIntegratedStedystateModel model;
	protected EnvironmentalConditions environmentalConditions;
	protected String currentEnvironmentalConditionname;
	protected RegulatoryGeneticConditions geneticConditions;
	protected HashMap<String, Double> objfunction;
    protected T results;
	
	 public IntegratedSimulationThread(IIntegratedStedystateModel model,
			 EnvironmentalConditions environmentalConditions,
			 IntegratedSimulationOptionsContainer simulationoptions){
		 
		  
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
	 
	 public void setGeneticConditions(RegulatoryGeneticConditions geneticConditions){
		 this.geneticConditions=geneticConditions;
	 }
	 
	 public void setEnvironmentalConditionName(String envcondname) {
		 this.currentEnvironmentalConditionname=envcondname;
	 }
	
	 protected abstract T executeSimulationProcess() throws Exception;
	
	 protected synchronized void initControlCenter() throws Exception{
		     
			if(simulationoptions.getSimulationMethod().equals(IntegratedSimulationMethod.INTEGRATEDSIMULATION)){
				controlcenter=new IntegratedSimulationControlCenter(model, 
						environmentalConditions, 
						null, 
						true, 
						simulationoptions.getSolver(), 
						simulationoptions.getMetabolicSimulationMethod(), 
						simulationoptions.getRegulatorySimulationMethod());
				
	
				if(simulationoptions.isStopSimulationAtFirstAttractor()!=null)
					((IntegratedSimulationControlCenter)controlcenter).stopRegulatorySimulationOnFirstAttractor(simulationoptions.isStopSimulationAtFirstAttractor());
				
				if(simulationoptions.getInitialComponentsbooleanState()!=null)
					((IntegratedSimulationControlCenter)controlcenter).setComponentsBooleanInitialState(simulationoptions.getInitialComponentsbooleanState());
				
				if(simulationoptions.getInitialComponentsbyteState()!=null && simulationoptions.getInitialComponentsbooleanState()==null)
					((IntegratedSimulationControlCenter)controlcenter).setComponentsbyteInitialState(simulationoptions.getInitialComponentsbyteState());
				
				if(simulationoptions.getRegulatorySimulationIterations()!=null)
					((IntegratedSimulationControlCenter)controlcenter).setMaxNumberIterationsRegulatorySimulation(simulationoptions.getRegulatorySimulationIterations());
			
				
				((IntegratedSimulationControlCenter)controlcenter).setObjectiveFunction(objfunction);
					
					
			}
			else if(simulationoptions.getSimulationMethod().equals(IntegratedSimulationMethod.SRFBA)){
				controlcenter=new SRFBAControlCenter(model, environmentalConditions, null, true, simulationoptions.getSolver());
				
				if(simulationoptions.getInitialComponentsbooleanState()!=null)
					((SRFBAControlCenter)controlcenter).setComponentsBooleanInitialState(simulationoptions.getInitialComponentsbooleanState());
				((SRFBAControlCenter)controlcenter).setObjectiveFunction(objfunction);
				
			}
			else if(simulationoptions.getSimulationMethod().equals(IntegratedSimulationMethod.DYNAMICRFBA)){
				if(simulationoptions instanceof RFBASimulationOptionsContainer){
					RFBASimulationOptionsContainer options=(RFBASimulationOptionsContainer) simulationoptions;
					
					IndexedHashMap<String, Double> initialSubstrateConcentrations = null;
					
					if(currentEnvironmentalConditionname!=null)
						initialSubstrateConcentrations=options.getInitialSubstrateConcentrationsForID(currentEnvironmentalConditionname);
					
					if(initialSubstrateConcentrations==null)
						initialSubstrateConcentrations=options.getInitialSubstrateConcentrations();
					
					if(initialSubstrateConcentrations!=null) {
					//if(options.getMapInitialsubstrateconcentration()!=null && currentEnvironmentalConditionname!=null)
					//	initialSubstrateConcentrations=options.ge
					
					controlcenter=new DynamicRFBAControlCenter(model, 
						environmentalConditions, 
						null, 
						options.getSolver(), 
						options.getInitialBiomass(), 
						options.getTimeStep(), 
						options.getNumberSteps(), 
						initialSubstrateConcentrations);
				
					if(simulationoptions.getInitialComponentsbooleanState()!=null)
						((DynamicRFBAControlCenter)controlcenter).setComponentsBooleanInitialState(simulationoptions.getInitialComponentsbooleanState());
					if(((RFBASimulationOptionsContainer) simulationoptions).getUptakeReactionsToExcludeFromInitialConfiguration()!=null)
						((DynamicRFBAControlCenter)controlcenter).setUptakeReactionsToExcludeFromInitialConfiguration(options.getUptakeReactionsToExcludeFromInitialConfiguration());
					
					((DynamicRFBAControlCenter)controlcenter).setObjectiveFunction(objfunction);
					
					}
					else
						throw new Exception("Invalid simulation options container, must include Initial Substrate Concentrations");	
				}
				else
					throw new Exception("Invalid simulation options container, must include RFBA simulation properties");
				
			}
			else
				throw new Exception("Unsupported simulation method");
			
			if(controlcenter!=null && geneticConditions!=null)
				controlcenter.setGeneticConditions(geneticConditions);
		}
	
	 
	 
	    @Override
		public void run() {
		 	try {
				initControlCenter();
				if(geneticConditions!=null)
					controlcenter.setGeneticConditions(geneticConditions);
				results=executeSimulationProcess();
			} catch (Exception e) {
				LogMessageCenter.getLogger().addCriticalErrorMessage("Error in Simulation thread: ", e);
			}
		 	
	    }
}
