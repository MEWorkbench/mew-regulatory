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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.threads.integrated;

import java.util.ArrayList;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.DynamicRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.IntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.SRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.RFBASimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.SimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.threads.metabolic.MetabolicMultiGenesReusableSimulationThread;

public abstract class IntegratedMultiGenesReusableSimulationThread<T> extends MetabolicMultiGenesReusableSimulationThread<T>{


    public IntegratedMultiGenesReusableSimulationThread(ISteadyStateGeneReactionModel model,
			EnvironmentalConditions environmentalConditions, SimulationOptionsContainer simulationoptions) {
		super(model, environmentalConditions, simulationoptions);
	}
    
    
    
     public IntegratedMultiGenesReusableSimulationThread(ISteadyStateGeneReactionModel model,
			EnvironmentalConditions environmentalConditions, SimulationOptionsContainer simulationoptions,
			IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions) {
		super(model, environmentalConditions, simulationoptions, geneticConditions);

	}
	



     protected synchronized void initControlCenter() throws Exception{

    	 if(((IntegratedSimulationOptionsContainer) simulationoptions).getSimulationMethod().equals(IntegratedSimulationMethod.INTEGRATEDSIMULATION)){
    		 controlcenter=new IntegratedSimulationControlCenter(model, 
    				 environmentalConditions, 
    				 null, 
    				 true, 
    				 simulationoptions.getSolver(), 
    				 simulationoptions.getMetabolicSimulationMethod(), 
    				 ((IntegratedSimulationOptionsContainer) simulationoptions).getRegulatorySimulationMethod());


    		 if(((IntegratedSimulationOptionsContainer) simulationoptions).isStopSimulationAtFirstAttractor()!=null)
    			 ((IntegratedSimulationControlCenter)controlcenter).stopRegulatorySimulationOnFirstAttractor(((IntegratedSimulationOptionsContainer) simulationoptions).isStopSimulationAtFirstAttractor());

    		 if(((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbooleanState()!=null)
    			 ((IntegratedSimulationControlCenter)controlcenter).setComponentsBooleanInitialState(((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbooleanState());

    		 if(((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbyteState()!=null && ((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbooleanState()==null)
    			 ((IntegratedSimulationControlCenter)controlcenter).setComponentsbyteInitialState(((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbyteState());

    		 if(((IntegratedSimulationOptionsContainer) simulationoptions).getRegulatorySimulationIterations()!=null)
    			 ((IntegratedSimulationControlCenter)controlcenter).setMaxNumberIterationsRegulatorySimulation(((IntegratedSimulationOptionsContainer) simulationoptions).getRegulatorySimulationIterations());

    		 if(((IntegratedSimulationOptionsContainer) simulationoptions).isForceTFInitializationAsTrue())
    			 ((IntegratedSimulationControlCenter)controlcenter).forceIntializationTranscriptFactorsAsTrueState(true);

    		 ((IntegratedSimulationControlCenter)controlcenter).setObjectiveFunction(objfunction);


    	 }
    	 else if(((IntegratedSimulationOptionsContainer) simulationoptions).getSimulationMethod().equals(IntegratedSimulationMethod.SRFBA)){
    		 controlcenter=new SRFBAControlCenter(model, environmentalConditions, null, true, simulationoptions.getSolver());

    		 if(((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbooleanState()!=null)
    			 ((SRFBAControlCenter)controlcenter).setComponentsBooleanInitialState(((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbooleanState());
    		 ((SRFBAControlCenter)controlcenter).setObjectiveFunction(objfunction);
    		 
    		 if(((IntegratedSimulationOptionsContainer) simulationoptions).isForceTFInitializationAsTrue())
    			 ((SRFBAControlCenter)controlcenter).forceIntializationTranscriptFactorsAsTrueState(true);

    	 }
    	 else if(((IntegratedSimulationOptionsContainer) simulationoptions).getSimulationMethod().equals(IntegratedSimulationMethod.DYNAMICRFBA)){
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

    				 if(((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbooleanState()!=null)
    					 ((DynamicRFBAControlCenter)controlcenter).setComponentsBooleanInitialState(((IntegratedSimulationOptionsContainer) simulationoptions).getInitialComponentsbooleanState());
    				 
    				 ArrayList<String> excludeuptakereactions=null;
    				 
    				 if(currentEnvironmentalConditionname!=null)
    					 excludeuptakereactions=((RFBASimulationOptionsContainer) simulationoptions).getUptakeReactionToExclude(currentEnvironmentalConditionname);
    				
    				 
    				 if(excludeuptakereactions==null &&((RFBASimulationOptionsContainer) simulationoptions).getUptakeReactionsToExcludeFromInitialConfiguration()!=null)
    					 excludeuptakereactions=options.getUptakeReactionsToExcludeFromInitialConfiguration();
    					 
    				 ((DynamicRFBAControlCenter)controlcenter).setUptakeReactionsToExcludeFromInitialConfiguration(excludeuptakereactions);

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

    	 /*if(controlcenter!=null && geneticConditions!=null)
				controlcenter.setGeneticConditions(geneticConditions);*/
     }
	
	 
	 
	    @Override
		public void run() {
		 	try {
				initControlCenter();
/*				if(geneticConditions!=null)
					controlcenter.setGeneticConditions(geneticConditions);*/
				results=executeSimulationProcess();
			} catch (Exception e) {
				LogMessageCenter.getLogger().addCriticalErrorMessage("Error in Simulation thread: ", e);
			}
		 	
	    }
}
