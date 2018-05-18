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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.Dynamic;

import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;

public class DynamicRegulatoryGeneticConditions extends RegulatoryGeneticConditions{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DynamicRegulatoryGeneticConditions(ISteadyStateGeneReactionModel model, boolean isOverUnder)
			throws Exception {
		super(new GeneregulatorychangesList(), new GeneChangesList(), model, isOverUnder);
		
	}
	
	public DynamicRegulatoryGeneticConditions(
			GeneregulatorychangesList regulatorygeneList,
			GeneChangesList geneList, IIntegratedStedystateModel model,
			boolean isOverUnder) throws Exception {
		super(regulatorygeneList, geneList, model, isOverUnder);
	}
	
	

	private void addGeneKnockout(String geneid){
		if(model.isMetabolicGene(geneid))
			this.geneList.addGeneKnockout(geneid);
		else
			this.regulatorygeneList.addGeneKnockout(geneid);
	}
	
	public void addSingleMetabolicGeneKnockout(String geneid){
		resetState();
		 this.geneList.addGeneKnockout(geneid);
		}
	
	public void addSingleGeneKnockout(String geneid){
		resetState();
		addGeneKnockout(geneid);
	}
	
	public  void addSetOfGeneKnockouts(ArrayList<String> geneids){
		resetState();
		for (String id : geneids) {
			addGeneKnockout(id);
		}
		
	}
	
	private void resetState(){
		this.geneList=new GeneChangesList();
		this.regulatorygeneList=new GeneregulatorychangesList();
	}
	
	
	 
    @Override
	 public  GeneticConditions clone() {
    	DynamicRegulatoryGeneticConditions clone = null;
			try {
				clone= new DynamicRegulatoryGeneticConditions(this.regulatorygeneList, this.geneList,this.model , this.isOverUnder);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

         return clone;
	 }

}
