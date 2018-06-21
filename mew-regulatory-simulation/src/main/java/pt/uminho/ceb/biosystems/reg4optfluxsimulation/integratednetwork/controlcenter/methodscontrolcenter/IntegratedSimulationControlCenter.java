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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.TwoStepIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.IntegratedSimulation;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.IntegratedLayerSimulation.IntegratedSimulationWithCoupledAsynchronousRegulatory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatoryNetworkSimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;

public class IntegratedSimulationControlCenter extends TwoStepIntegratedSimulationControlCenter{


	private static final long serialVersionUID = 1L;
	
	
	
	public IntegratedSimulationControlCenter(ISteadyStateModel model,
			EnvironmentalConditions environmentalConditions, 
			RegulatoryGeneticConditions geneticConditions,
			String methodType, 
			boolean isMaximization, 
			String solver,
			String metabolicsimulationmethod,
			RegulatorySimulationMethod regulatorysimulationmethod){
		super(model, environmentalConditions, geneticConditions, methodType, isMaximization, solver);
        setMetabolicModelSimulationMethod(metabolicsimulationmethod);
        setRegulatoryModelSimulationMethod(regulatorysimulationmethod);
	}
	

	public IntegratedSimulationControlCenter(ISteadyStateModel model, 
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions,
			boolean isMaximization,
			String solver,
			String metabolicsimulationmethod,
			RegulatorySimulationMethod regulatorysimulationmethod) {
		super(model, environmentalConditions, geneticConditions, IntegratedSimulationMethod.INTEGRATEDSIMULATION.getName(), isMaximization, solver);
        setMetabolicModelSimulationMethod(metabolicsimulationmethod);
        setRegulatoryModelSimulationMethod(regulatorysimulationmethod);
	}
	
	
	@Override
	protected LinkedHashMap<String, Class<?>> getMethodsSupportedByControlCenter() {
		LinkedHashMap<String, Class<?>> support=new LinkedHashMap<>();
		support.put(IntegratedSimulationMethod.INTEGRATEDSIMULATION.toString(), IntegratedSimulation.class);
		support.put(IntegratedSimulationMethod.ASYNCHINTEGRATEDSIMULATION.toString(), IntegratedSimulationWithCoupledAsynchronousRegulatory.class);
		return support;
	}

	
	public void setGeneInitialStateAsOFF(Set<String> genesoff){
		 addProperty(RegulatorySimulationProperties.GENESINITIALOFFSTATE,genesoff);
	}
	

	
	 public void setComponentsbyteInitialState(HashMap<String, Byte> state){
	    	addProperty(RegulatorySimulationProperties.COMPONENTINITIALSTATE, state);
	    }

    public void stopRegulatorySimulationOnFirstAttractor(boolean stop){
		 addProperty(RegulatoryNetworkSimulationProperties.STOPFIRSTATTRACTOR,stop);
	}
    
    public void setMaxNumberIterationsRegulatorySimulation(Integer numberiter){
    	addProperty(RegulatoryNetworkSimulationProperties.SIMULATIONMAXITERATIONS, numberiter);
    }
    
    /**
	 * Sets the metabolic method type.
	 *
	 * @param method the new metabolic method type
	 */
	public void setMetabolicModelSimulationMethod(String method){
		addProperty(RegulatorySimulationProperties.METABOLIC_SIMULATION_METHOD, method);
	}
	
	/**
	 * Gets the metabolic method type.
	 *
	 * @return the metabolic method type
	 */
	public String getMetabolicModelSimulationMethod(){
		
		return (String)getProperty(RegulatorySimulationProperties.METABOLIC_SIMULATION_METHOD);
	}
	
	
	public void setRegulatoryModelSimulationMethod(RegulatorySimulationMethod method){
		addProperty(RegulatorySimulationProperties.REGULATORY_NETWORK_SIMULATION_METHOD, method);
	}
	
	
    public RegulatorySimulationMethod getRegulatoryModelSimulationMethod(){
		
		return (RegulatorySimulationMethod) getProperty(RegulatorySimulationProperties.REGULATORY_NETWORK_SIMULATION_METHOD);
	}



	

}
