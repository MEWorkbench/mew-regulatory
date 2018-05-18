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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.NoConstructorMethodException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.UnregistaredMethodException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;

public class RegulatoryNetworkSimulationMethodsFactory {
	
	
	protected Map<String,Class<?>> mapMethods;
	protected Map<String,Object> methodProperties;
	protected IRegulatoryNetwork regmodel; 

	
	
	
	public RegulatoryNetworkSimulationMethodsFactory(Map<String,Class<?>> mapMethods)
	{
		this.mapMethods = new HashMap<String, Class<?>>();
		for(String id : mapMethods.keySet())
			registerMethod(id, mapMethods.get(id));
		methodProperties = new HashMap<String, Object>();
	}
	
	

	public void registerMethod(String id, Class<?> method){
		mapMethods.put(id,method);
	}
	
	
	public Set<String> getRegisteredMethods(){
		LinkedHashSet<String> setMethods = new LinkedHashSet<String>();
		for(String methodId : mapMethods.keySet())
			setMethods.add(methodId);
		return setMethods;
	}
	
	
	public Class<?> getClassProblem( String methodId) throws UnregistaredMethodException{
		if(!mapMethods.containsKey(methodId)) throw new UnregistaredMethodException(methodId);
		return mapMethods.get(methodId);
	}
	
	
	
	public IRegulatoryNetworkSimulationMethod getMethod (String methodId) throws InstantiationException, InvocationTargetException, UnregistaredMethodException 
	{
		
		IRegulatoryNetworkSimulationMethod method = null;
		
		Class<?> klass = getClassProblem(methodId);
		
		try {
			method = (IRegulatoryNetworkSimulationMethod) klass.getDeclaredConstructor(IRegulatoryNetwork.class).newInstance(regmodel);
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
		
		method.setProperty(RegulatoryNetworkSimulationProperties.METHOD_NAME, methodId);
		method.putAllProperties(methodProperties);
		return method;
	}
	
	
	public void addProperty(String key, Object value){
		methodProperties.put(key, value);
	}
	
	public void removeProperty(String key){
		
		methodProperties.remove(key);
	}
	
	public Object getProperty(String key){
		return methodProperties.get(key);
	}
	
	public void addSimulationMethod(String methodId, Class<?> method) throws Exception{
		
		if(mapMethods.containsKey(methodId)) throw new Exception("The simulation method " + methodId + " is already registed");
		if(method==null) throw new Exception("The simulation method is already a null class");
		try{
			method.getConstructor(IOptfluxRegulatoryModel.class);
		}catch (Exception e) {
			throw new NoConstructorMethodException(method);
		}
		
		mapMethods.put(methodId, method);
		
	}


	public IRegulatoryNetwork getModel() {
		return regmodel;
	}


	public void setModel(IRegulatoryNetwork model) {
		this.regmodel = model;
	}


	
	

}
