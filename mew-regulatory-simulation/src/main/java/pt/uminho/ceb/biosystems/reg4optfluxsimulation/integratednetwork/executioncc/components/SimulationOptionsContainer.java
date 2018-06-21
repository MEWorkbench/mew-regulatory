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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components;

import java.io.Serializable;
import java.util.HashMap;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.solvers.builders.CLPSolverBuilder;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;

public class SimulationOptionsContainer implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	protected String solver=CLPSolverBuilder.ID;
	protected String metabolicsimulationmethod=SimulationProperties.FBA;
	protected HashMap<String, Double> objfunct=null;
	protected RegulatoryGeneticConditions geneticconditions;
	protected String biomassflux=null;
	protected boolean isMaximization=true;
	
	

	public SimulationOptionsContainer(){}



	public String getBiomassflux() {
		return biomassflux;
	}

	public void setBiomassflux(String biomassflux) {
		this.biomassflux = biomassflux;
	}

	public HashMap<String, Double> getObjfunct() {
		return objfunct;
	}

	public void setObjfunct(HashMap<String, Double> objfunct) {
		this.objfunct = objfunct;
	}
	

	public String getSolver() {
		return solver;
	}

	public void setSolver(String solver) {
		this.solver = solver;
	}
	
	

	public boolean isMaximization() {
		return isMaximization;
	}


	public void setMaximization(boolean isMaximization) {
		this.isMaximization = isMaximization;
	}


	public String getMetabolicSimulationMethod() {
		return metabolicsimulationmethod;
	}

	public void setMetabolicSimulationMethod(String metabolicsimulationmethod) {
		this.metabolicsimulationmethod = metabolicsimulationmethod;
	}



	public RegulatoryGeneticConditions getGeneticconditions() {
		return geneticconditions;
	}



	public void setGeneticconditions(RegulatoryGeneticConditions geneticconditions) {
		this.geneticconditions = geneticconditions;
	}
	
	
	
	


}
