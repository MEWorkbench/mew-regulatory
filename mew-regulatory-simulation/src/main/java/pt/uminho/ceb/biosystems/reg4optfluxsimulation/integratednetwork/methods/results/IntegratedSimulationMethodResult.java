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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.Attractor;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public abstract class IntegratedSimulationMethodResult extends SteadyStateSimulationResult implements Serializable{


	private static final long serialVersionUID = 1L;
	protected IntegratedSimulationMethod simulationmethod;
	protected IRegulatoryModelSimulationResult regulatorysimulationresults;
	protected HashMap<String, Object> auxiliarinformation;
	//protected RegulatoryGeneticConditions finalgeneticconditions;
	protected GeneticConditions initialgeneticconditions;
	//protected 

	public IntegratedSimulationMethodResult(ISteadyStateModel model, EnvironmentalConditions environmentalConditions,
			GeneticConditions initialgeneticConditions,GeneticConditions finalgeneticconditions, String method, FluxValueMap fluxValues, String solverOutput,
			Double oFvalue, String oFString, LPSolutionType solutionType, IntegratedSimulationMethod integratedsimulationmethod, IRegulatoryModelSimulationResult regulatoryresults) {
		super(model, environmentalConditions, finalgeneticconditions, method, fluxValues, solverOutput, oFvalue, oFString,
				solutionType);
		
		this.initialgeneticconditions=initialgeneticConditions;
		this.simulationmethod=integratedsimulationmethod;
		this.regulatorysimulationresults=regulatoryresults;
	}
	
	public abstract TypeIntegratedSimulationResult getTypeResult();
	
/*	
	public ArrayList<String> getMetabolicGenesKnock() {
		if(finalgeneticconditions!=null)
			return (ArrayList<String>) finalgeneticconditions.getMetabolicGenesKnockoutList();
		return null;
	}

    public ArrayList<String> getRegulatoryGenesKnockout() {
    	if(finalgeneticconditions!=null)
    		return (ArrayList<String>) finalgeneticconditions.getRegulatoryGenesKnockoutList();
    	return null;
	}
	
    
    public ArrayList<String> getALLGenesKnockout() {
    	if(finalgeneticconditions!=null)
    		return (ArrayList<String>) finalgeneticconditions.getALLGeneKnockoutList();
    	return null;
	}*/
	
	
	/*public RegulatoryGeneticConditions getCalculatedGeneticConditions() {
		return finalgeneticconditions;
	}*/
	
	public GeneticConditions getInitialGeneticConditions() {
		return initialgeneticconditions;
	}
    
    public IRegulatoryModelSimulationResult getRegulatorySimulationResults() {
		return regulatorysimulationresults;
	}
    
    public ArrayList<Attractor> getAttractors(){
		return regulatorysimulationresults.getAttractors();
	}
    
    public boolean getGeneStateInAttractors(String geneid) throws Exception{
		return regulatorysimulationresults.getGeneStateInAttractors(geneid);
	}



	public IntegratedSimulationMethod getIntegratedSimulationMethod() {
		return simulationmethod;
	}
	
	public void setAuxiliarInformation(HashMap<String, Object> auxiliarinformation) {
		this.auxiliarinformation=auxiliarinformation;
	}
    
	public void appendAuxiliarInformation(String key, Object value) {
		if(auxiliarinformation==null)
			auxiliarinformation=new HashMap<>();
		auxiliarinformation.put(key, value);
	}
	
	public Object getAuxiliarInformation(String key) {
		if(auxiliarinformation!=null && auxiliarinformation.containsKey(key))
			return auxiliarinformation.get(key);
		return null;
	}
    
	public HashMap<String, Object> getAuxiliarInformation(){
		return auxiliarinformation;
	}

}
