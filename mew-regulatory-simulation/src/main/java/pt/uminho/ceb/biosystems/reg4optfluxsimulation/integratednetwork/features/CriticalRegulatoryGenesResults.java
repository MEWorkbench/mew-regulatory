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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.features;

import java.io.Serializable;
import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;


public class CriticalRegulatoryGenesResults implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<String> criticalGenesId = null;
	protected IIntegratedStedystateModel model;
	protected IntegratedSimulationOptionsContainer simuoptions;
	protected EnvironmentalConditions envConditions = null;
	protected boolean geneIDsFromFile = false;
	
	
	
	public CriticalRegulatoryGenesResults(ArrayList<String> listgenes, IIntegratedStedystateModel model) {
		// TODO Auto-generated constructor stub
		
		this.criticalGenesId = listgenes;
		this.model = model;
	}
	
	public CriticalRegulatoryGenesResults(ArrayList<String> listgenes, IIntegratedStedystateModel model, IntegratedSimulationOptionsContainer simulationsettings) {
		this(listgenes,model);
		this.simuoptions=simulationsettings;
	}
	
	
	
	public ArrayList<String> getCriticalRegulatoryGeneList(){
		return criticalGenesId;
	}
	
	public IIntegratedStedystateModel getModel(){
		return model;
	}
	
	
	
	public void setModel(IIntegratedStedystateModel model) {
		this.model = model;
	}

	public void setCriticalGenesId(ArrayList<String> criticalGenesId) {
		this.criticalGenesId = criticalGenesId;
	}

	


	public IntegratedSimulationOptionsContainer getSimulationSettings() {
		return simuoptions;
	}

	public void setSimulationSettings(IntegratedSimulationOptionsContainer simuoptions) {
		this.simuoptions = simuoptions;
	}

	public boolean isGeneIDsFromFile() {
		return geneIDsFromFile;
	}

	public void setGeneIDsFromFile(boolean geneIDsFromFile) {
		this.geneIDsFromFile = geneIDsFromFile;
	}

	public EnvironmentalConditions getEnvConditions() {
		return envConditions;
	}

	public void setEnvConditions(EnvironmentalConditions envConditions) {
		this.envConditions = envConditions;
	}
	
	
	

}
