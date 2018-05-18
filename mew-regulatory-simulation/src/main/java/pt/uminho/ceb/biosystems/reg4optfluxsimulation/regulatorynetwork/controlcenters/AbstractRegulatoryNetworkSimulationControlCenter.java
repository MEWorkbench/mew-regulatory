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

import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.IRegulatoryNetworkSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatoryNetworkSimulationMethodsFactory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatoryNetworkSimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public abstract class AbstractRegulatoryNetworkSimulationControlCenter {
	
	
	
	//protected IRegulatoryNetwork regmodel;
	protected RegulatorySimulationMethod methodType = RegulatorySimulationMethod.OPTFLUXSYNCHRONOUSBOOLEANSIMULATION;
	protected IRegulatoryNetworkSimulationMethod lastmethod;
	protected abstract RegulatoryNetworkSimulationMethodsFactory getFactory();
	
     	
	
	public AbstractRegulatoryNetworkSimulationControlCenter(IRegulatoryNetwork regmodel, RegulatorySimulationMethod regmethodType, InitialRegulatoryState initstate){
		
		//this.regmodel = regmodel;
		getFactory().addProperty(RegulatoryNetworkSimulationProperties.REGULATORYINITIALSTATE, initstate);
		getFactory().setModel(regmodel);
		setMethodType(regmethodType);

	}
	
	
	
	public InitialRegulatoryState getInitialRegulatoryStateContainer(){
		return (InitialRegulatoryState) getFactory().getProperty(RegulatoryNetworkSimulationProperties.REGULATORYINITIALSTATE);
	}
	
	
	public void setInitialRegulatoryStateContainer(InitialRegulatoryState initstate){
		getFactory().addProperty(RegulatoryNetworkSimulationProperties.REGULATORYINITIALSTATE, initstate);	
	}
	
	public void setMaxIterations(int numberiterations){
		getFactory().addProperty(RegulatoryNetworkSimulationProperties.SIMULATIONMAXITERATIONS, numberiterations);
	}
	
	public void stopFirstAttractor(boolean stop){
		getFactory().addProperty(RegulatoryNetworkSimulationProperties.STOPFIRSTATTRACTOR, stop);
	}
	
	
	public Object getSimulationProperty (String key)
	{
		return getFactory().getProperty(key);
	}

	public void setSimulationProperty (String key, Object value)
	{
		getFactory().addProperty(key, value);
	}
		
		
	public String getMethodType() {
		return methodType.getName();
	}

	public void setMethodType(RegulatorySimulationMethod methodType) {
		this.methodType = methodType;
	}
	
	
	public IRegulatoryModelSimulationResult simulate() throws Exception {

		this.lastmethod = getFactory().getMethod(this.methodType.getName());
		return lastmethod.simulate(); 
	}
	
	
	public IRegulatoryNetworkSimulationMethod getSimulatedMethod(){
		return lastmethod;
	}
		
   
	public void addProperty(String id, Object obj){
		getFactory().addProperty(id, obj);
	}
	
	
	
}
