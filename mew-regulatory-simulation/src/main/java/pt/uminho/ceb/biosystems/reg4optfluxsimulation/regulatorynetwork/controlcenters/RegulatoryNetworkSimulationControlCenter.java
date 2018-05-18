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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.controlcenters;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.UnregistaredMethodException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatoryNetworkSimulationMethodsFactory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.BDDAsynchronousStateSimulation;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.BDDSynchronousStateSimulation;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.OptFluxSynchronousBooleanSimulation;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.coupled.BDDAsynchronousStateWithCoupleMetabolicSimulation;

public class RegulatoryNetworkSimulationControlCenter extends AbstractRegulatoryNetworkSimulationControlCenter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static RegulatoryNetworkSimulationMethodsFactory factory;
	
	
	static{
		
		LinkedHashMap<String, Class<?>> mapMethods = new LinkedHashMap<String,Class<?>>();
		mapMethods.put(RegulatorySimulationMethod.OPTFLUXSYNCHRONOUSBOOLEANSIMULATION.getName(), OptFluxSynchronousBooleanSimulation.class);
		mapMethods.put(RegulatorySimulationMethod.BDDSYNCHRONOUSBOOLEANSIMULATION.getName(), BDDSynchronousStateSimulation.class);
		mapMethods.put(RegulatorySimulationMethod.BDDASYNCHRONOUSBOOLEANSIMULATION.getName(), BDDAsynchronousStateSimulation.class);
		mapMethods.put(RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION.getName(), BDDAsynchronousStateWithCoupleMetabolicSimulation.class);
		//mapMethods.put(RegulatorySimulationMethod.BDDSEQUENCIALBOOLEANSIMULATION.getName(), BDDSequencialStateSimulation.class);
		factory = new RegulatoryNetworkSimulationMethodsFactory(mapMethods);
	}
	
	
	
	

	public RegulatoryNetworkSimulationControlCenter(IRegulatoryNetwork regmodel,RegulatorySimulationMethod regmethodType, InitialRegulatoryState initstate) {
		super(regmodel,regmethodType,initstate);

	}

	@Override
	protected RegulatoryNetworkSimulationMethodsFactory getFactory() {
		return factory;
	}
	
	static public void registMethod(String methodId, Class<?> klass) throws Exception{
		factory.addSimulationMethod(methodId, klass);
	}
	
	public static Set<String> getRegisteredMethods(){
		return factory.getRegisteredMethods();
	}
	
	public static Class<?> getMethod(String method) throws UnregistaredMethodException{
		return factory.getClassProblem(method);
	}


	
	
	

}
