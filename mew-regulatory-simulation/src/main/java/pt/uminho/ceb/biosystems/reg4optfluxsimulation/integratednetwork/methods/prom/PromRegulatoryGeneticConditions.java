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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.prom;

import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;

public class PromRegulatoryGeneticConditions extends RegulatoryGeneticConditions{

	
	protected ISteadyStateGeneReactionModel model;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PromRegulatoryGeneticConditions(GeneregulatorychangesList reggeneList, GeneChangesList metgeneList,
			ISteadyStateGeneReactionModel model) throws Exception {
		super(reggeneList, metgeneList, model, false);
		this.model=model;
		
		
	}
	
	
	@Override
	public void addGene(String geneid){
		 
		if(model.getGenes().containsKey(geneid))
			this.geneList.addGeneKnockout(geneid);
	 }
	
	
    @Override
	 public  GeneticConditions clone() {
    	 RegulatoryGeneticConditions clone = null;
			try {
				clone= new PromRegulatoryGeneticConditions(this.regulatorygeneList, this.geneList,this.model);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

          return clone;
	 }
    
    
    
    public static PromRegulatoryGeneticConditions getPromRegulatoryGeneticConditions(ArrayList<String> knockoutgenelist, ISteadyStateGeneReactionModel model) throws Exception{
     	ArrayList<String> metabolicgenes=new ArrayList<>();
     	ArrayList<String> regulatorygenes=new ArrayList<>();
     	
     	IndexedHashMap<String, Gene> genesmodel=model.getGenes();
     	
     	
     	for (int i = 0; i < knockoutgenelist.size(); i++) {
 			String geneid=knockoutgenelist.get(i);
 			if(genesmodel.containsKey(geneid))
 				metabolicgenes.add(geneid);
 		}
     	
     	GeneChangesList metabolicgeneconditions=new GeneChangesList(metabolicgenes);
     	GeneregulatorychangesList regulatorygeneconditions=new GeneregulatorychangesList(regulatorygenes);
     	return new PromRegulatoryGeneticConditions(regulatorygeneconditions, metabolicgeneconditions, model);
     }
	

}
