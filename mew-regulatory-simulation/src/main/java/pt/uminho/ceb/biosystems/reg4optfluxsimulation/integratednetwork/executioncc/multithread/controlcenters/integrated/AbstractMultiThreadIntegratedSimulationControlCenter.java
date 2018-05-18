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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.integrated;

import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.AbstractThreadSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;

public abstract class AbstractMultiThreadIntegratedSimulationControlCenter<T> extends AbstractThreadSimulationControlCenter<T>{
	

	protected IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions=null;
	protected IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions=null;
	protected IndexedHashMap<String, ArrayList<String>> envcondstogeneslinkscheme=null;
	protected ArrayList<String> testknockoutgenes=null;
	protected int nprocs=1;
	protected boolean simulenvconds=false;
	protected boolean simulknockoutgenes=false;
	
	public abstract boolean isSimulationsFinished();
	
	
	public AbstractMultiThreadIntegratedSimulationControlCenter(IIntegratedStedystateModel model, Integer numberprocesses) {
		super(model);
		if(numberprocesses!=null)
			nprocs=numberprocesses;
		
	}
	
	public AbstractMultiThreadIntegratedSimulationControlCenter(IIntegratedStedystateModel model, IntegratedSimulationOptionsContainer simulationoptions, Integer numberprocesses) {
		super(model,simulationoptions);
		if(numberprocesses!=null)
			nprocs=numberprocesses;
	}
	
	public AbstractMultiThreadIntegratedSimulationControlCenter(IIntegratedStedystateModel model, 
			 IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions,
			 ArrayList<String> testknockoutgenes,
			 IntegratedSimulationOptionsContainer options,
			 Integer numberprocesses) throws Exception{
		this(model, options,numberprocesses);
		this.simulationEnvironmentalConditions=simulationEnvironmentalConditions;
		if(simulationEnvironmentalConditions!=null)
			simulenvconds=true;
		this.testknockoutgenes=testknockoutgenes;
		initConfigurations();
	}
	
	public AbstractMultiThreadIntegratedSimulationControlCenter(IIntegratedStedystateModel model, 
			 IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions,
			 IndexedHashMap<String, RegulatoryGeneticConditions> geneconditions,
			 IntegratedSimulationOptionsContainer options,
			 Integer numberprocesses) throws Exception{
		this(model, options,numberprocesses);
		this.simulationEnvironmentalConditions=simulationEnvironmentalConditions;
		if(simulationEnvironmentalConditions!=null)
			simulenvconds=true;
		this.geneticConditions=validateGeneticConditions(geneconditions);
		if(geneticConditions!=null)
			simulknockoutgenes=true;
	}

	public void setSimulationLinkSchemeForEnvironmentalConditionsAndGenes(IndexedHashMap<String, ArrayList<String>> envcondstogeneslinkscheme) {
		this.envcondstogeneslinkscheme=envcondstogeneslinkscheme;
	}
	
	
	public void setSimulationEnvironmentalConditions(IndexedHashMap<String, EnvironmentalConditions> simulationEnvironmentalConditions) {
		this.simulationEnvironmentalConditions = simulationEnvironmentalConditions;
		if(simulationEnvironmentalConditions!=null)
			simulenvconds=true;
		else
			simulenvconds=false;
	}


	public void setGeneticConditions(IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions) {
		try {
			this.geneticConditions = validateGeneticConditions(geneticConditions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(geneticConditions!=null)
			simulknockoutgenes=true;
		else
			simulknockoutgenes=false;
	}
	
	
	protected IndexedHashMap<String, RegulatoryGeneticConditions> validateGeneticConditions(IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions) throws Exception{
		
		IndexedHashMap<String, RegulatoryGeneticConditions> validatedgeneticConditions=new IndexedHashMap<>();
		for (int i = 0; i < geneticConditions.size(); i++) {
			String geneid=geneticConditions.getKeyAt(i);
			RegulatoryGeneticConditions genecond=geneticConditions.get(geneid);
			if(genecond==null) {
				RegulatoryGeneticConditions newgenecond=RegulatoryGeneticConditions.getRegulatoryGeneticConditions(geneid, ((IIntegratedStedystateModel)model));
				if(newgenecond!=null)
					validatedgeneticConditions.put(geneid,newgenecond);	
			}
			else
				validatedgeneticConditions.put(geneid, genecond);
		}
		return validatedgeneticConditions;
	}
	


	protected void initConfigurations() throws Exception{
		
		if(testknockoutgenes!=null){
			this.geneticConditions=new IndexedHashMap<>();
			simulknockoutgenes=true;

			for (int i = 0; i < testknockoutgenes.size(); i++) {
                 RegulatoryGeneticConditions genecondition=RegulatoryGeneticConditions.getRegulatoryGeneticConditions(testknockoutgenes.get(i), ((IIntegratedStedystateModel)model));
                 if(genecondition!=null)
			        geneticConditions.put(testknockoutgenes.get(i), genecondition);
                
			}
		  }	
	    }


	public void setNprocs(int nprocs) {
		this.nprocs = nprocs;
	}

   

}
