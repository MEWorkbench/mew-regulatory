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

import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;

public abstract class AbstractThreadSimulationControlCenter<T> {
	
	
	protected ISteadyStateGeneReactionModel model=null;
	protected SimulationOptionsContainer simulationoptions;
	
	
	
	public AbstractThreadSimulationControlCenter(ISteadyStateGeneReactionModel model) {
		this.model=model;
	}
	
	
	public AbstractThreadSimulationControlCenter(ISteadyStateGeneReactionModel model, SimulationOptionsContainer simulationoptions){
		this(model);
		this.simulationoptions=simulationoptions;
	}
	
	public void setSimulationOptions(SimulationOptionsContainer simulationoptions) {
		this.simulationoptions=simulationoptions;
	}
	
	
	public abstract void execute() throws Exception;
	public abstract T getSimulationResults();
	//public abstract boolean isSimulationsFinished();
	public abstract void reset();
	
	public SimulationOptionsContainer getSimulationOptionsContainer() {
		return simulationoptions;
	};
	
	
/*	protected RegulatoryGeneticConditions setupRegulatoryGeneticConditionsForGeneID(String geneid, IIntegratedStedystateModel model) throws Exception {
		 boolean valid=true;
		 GeneChangesList metabolicgenes=null;
		 GeneregulatorychangesList regulatorygenes=null;
		
		 boolean ismetabolic=model.isMetabolicGene(geneid);
		 boolean isregulatory=model.isRegulatoryGene(geneid);
		 

		 if(ismetabolic && !isregulatory){
			 ArrayList<String> metabgenes =new ArrayList<>();
			 metabgenes.add(geneid);
			 metabolicgenes=new GeneChangesList(metabgenes);
			 regulatorygenes= new GeneregulatorychangesList();
	
		  }
		 else if(!ismetabolic && isregulatory){
			 ArrayList<String> reggenes =new ArrayList<>();
			 reggenes.add(geneid);
			 metabolicgenes=new GeneChangesList();
			 regulatorygenes=new GeneregulatorychangesList(reggenes);

		 }
		 else
			 valid=false;
		 
       if(valid){
      	MTULogUtils.addInfoMsgToClass(this.getClass(), "Added Gene knockout {} is metabolic: {} isregulatory: {}", geneid,ismetabolic,isregulatory);
	        return new RegulatoryGeneticConditions(regulatorygenes, metabolicgenes, model, false);
       }
       return null;

	}*/

}
