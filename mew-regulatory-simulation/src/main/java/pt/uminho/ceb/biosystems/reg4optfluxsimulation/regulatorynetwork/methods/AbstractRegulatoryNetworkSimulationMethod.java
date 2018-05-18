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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.ManagerExceptionUtils;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.IRegulatoryNetworkSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.InvalidRegulatoryModelException;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatoryNetworkSimulationProperties;

public abstract class AbstractRegulatoryNetworkSimulationMethod implements IRegulatoryNetworkSimulationMethod{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IRegulatoryNetwork model;
	protected Map<String, Object> properties;
    protected Set<String> optionalProperties;
	protected Set<String> mandatoryProps;

	
	
	


	public AbstractRegulatoryNetworkSimulationMethod(IRegulatoryNetwork model) throws InvalidRegulatoryModelException{
		
		if(simulationMethodSupportsRegulatoryNetworkType(model)){
			this.model = model;
			properties = new HashMap<String, Object>();
			initPropsKeys();
		}
		else
			throw new InvalidRegulatoryModelException("The current model is not supported by this regulatory simulation method");
	}
	

	
	protected void initPropsKeys(){
		
		mandatoryProps = new HashSet<String>();
		mandatoryProps.add(RegulatoryNetworkSimulationProperties.REGULATORYINITIALSTATE);
		
		optionalProperties=new HashSet<String>();
		optionalProperties.add(RegulatoryNetworkSimulationProperties.STOPFIRSTATTRACTOR);
		optionalProperties.add(RegulatoryNetworkSimulationProperties.SIMULATIONMAXITERATIONS);

	}
	

	
	public IRegulatoryNetwork getModel(){
		return model;
	}
	
	
	public void setProperty(String m, Object o){
		properties.put(m, o);
	}
	
	@SuppressWarnings("unchecked")
	public Object getProperty(String k) {
		return properties.get(k);
	}
	
	public Set<String> getOptionalProperties(){
		return optionalProperties;		
	}
	
	public Set<String> getMandatoryProperties(){
		return mandatoryProps;
	}
	
	public void putAllProperties(Map<String, Object> p){
		this.properties.putAll(p);		
	}
	
	@Override
	public InitialRegulatoryState getInitialRegulatoryState() throws PropertyCastException, MandatoryPropertyException{
		return ManagerExceptionUtils.testCast(properties, InitialRegulatoryState.class, RegulatoryNetworkSimulationProperties.REGULATORYINITIALSTATE, false);
	}
	
	public void setInitialRegulatoryState(InitialRegulatoryState initstate){
		setProperty(RegulatoryNetworkSimulationProperties.REGULATORYINITIALSTATE, initstate);
	}
	
	public boolean isStopFirstAttractor() throws PropertyCastException, MandatoryPropertyException{
		
	     boolean stop=true;
		try {
			stop=ManagerExceptionUtils.testCast(properties, Boolean.class, RegulatoryNetworkSimulationProperties.STOPFIRSTATTRACTOR, true);
		} catch (Exception e) {
			stop=true;
		}
		  return stop;
		
	}
	
	public void stopWhenAttractorIsFound(Boolean stop){
		setProperty(RegulatoryNetworkSimulationProperties.STOPFIRSTATTRACTOR, stop);
	}
	
	public Integer getMaxIterations() throws PropertyCastException, MandatoryPropertyException{
		return ManagerExceptionUtils.testCast(properties, Integer.class, RegulatoryNetworkSimulationProperties.SIMULATIONMAXITERATIONS, true);
	}
	
	public void setMaxIterations(Integer maxiterations){
		setProperty(RegulatoryNetworkSimulationProperties.SIMULATIONMAXITERATIONS, maxiterations);
	}
	


}
