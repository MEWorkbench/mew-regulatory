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

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.srfba.SRFBA;

public class SRFBAControlCenter extends AbstractIntegratedSimulationControlCenter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SRFBAControlCenter(ISteadyStateModel model, EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions, boolean isMaximization,
			SolverType solver) {
		super(model, environmentalConditions, geneticConditions, IntegratedSimulationMethod.SRFBA.getName(), isMaximization, solver);

	}

	@Override
	protected LinkedHashMap<String, Class<?>> getMethodsSupportedByControlCenter() {
		LinkedHashMap<String, Class<?>> support=new LinkedHashMap<>();
		support.put(IntegratedSimulationMethod.SRFBA.toString(), SRFBA.class);
		return support;
	}
	
	public void setComponentsBooleanInitialState(HashMap<String, Boolean> state){
		addProperty(RegulatorySimulationProperties.COMPONENTINITIALSTATE, state);
	}

}
