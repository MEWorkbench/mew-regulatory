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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.AbstractSimulationMethodsFactory;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.ISteadyStateSimulationMethod;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.UnregistaredMethodException;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IIntegratedSteadyStateSimulationMethod;

public class IntegratedAbstractSimulationMethodsFactory extends AbstractSimulationMethodsFactory{

	public IntegratedAbstractSimulationMethodsFactory(Map<String, Class<?>> mapMethods) {
		super(mapMethods);

	}
	
	
	@Override
	public ISteadyStateSimulationMethod getMethod (String methodId, Map<String,Object> methodProperties, ISteadyStateModel model) throws InstantiationException, InvocationTargetException, UnregistaredMethodException
	{
		
		IIntegratedSteadyStateSimulationMethod method = null;
		
		Class<?> klass = getClassProblem(methodId);
		
		try {
			method = (IIntegratedSteadyStateSimulationMethod) klass.getDeclaredConstructor(ISteadyStateModel.class).newInstance(model);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (SecurityException e) {
			System.err.println("Nunca devia ter passado por aqui...");
			e.printStackTrace();
		} catch (InstantiationException e) {
			throw e;
		} catch (IllegalAccessException e) {
			System.err.println("Nunca devia ter passado por aqui...");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			throw e;
		} catch (NoSuchMethodException e) {
			System.err.println("Nunca devia ter passado por aqui...");
			e.printStackTrace();
		}
		
		if(method.checkIfMandatoryPropertiesSatisfied(methodProperties)){
			method.setProperty(SimulationProperties.METHOD_NAME, methodId);
		    method.putAllProperties(methodProperties);
			return method;
		}
		else
			throw new InstantiationException("Some of the mandatory properties of the simulation method [ "+method+" ] were not satisfied");

	}

}
