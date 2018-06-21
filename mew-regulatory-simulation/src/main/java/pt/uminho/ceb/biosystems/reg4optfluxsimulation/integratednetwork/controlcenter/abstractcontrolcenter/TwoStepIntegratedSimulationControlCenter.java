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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter;

import java.util.HashMap;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;

public abstract class TwoStepIntegratedSimulationControlCenter extends AbstractIntegratedSimulationControlCenter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public TwoStepIntegratedSimulationControlCenter(ISteadyStateModel model,
			EnvironmentalConditions environmentalConditions, RegulatoryGeneticConditions geneticConditions,
			String methodType, boolean isMaximization, String solver) {
		super(model, environmentalConditions, geneticConditions, methodType, isMaximization, solver);
	}

	
	
	/*@Override
	protected LinkedHashMap<String, Class<?>> getMethodsSupportedByControlCenter() {
		LinkedHashMap<String, Class<?>> support=new LinkedHashMap<>();
		support.put(IntegratedSimulationMethod.INTEGRATEDSIMULATION.toString(), IntegratedSimulation.class);
		support.put(IntegratedSimulationMethod.CONDITIONALINTEGRATEDSIMULATION.toString(), ConditionalIntegratedSimulation.class);
		support.put(IntegratedSimulationMethod.DYNAMICRFBA.toString(), DynamicRFBA.class);
		return support;
	}*/
	
	public void setComponentsBooleanInitialState(HashMap<String, Boolean> state){
		addProperty(RegulatorySimulationProperties.COMPONENTINITIALSTATE, state);
    }

}
