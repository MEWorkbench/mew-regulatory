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
package pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.SteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;

public class GeneReactionsInteractionsContainer {
	
	private ISteadyStateGeneReactionModel model;
	private IndexedHashMap<String, ArrayList<String>> genetoreactions=new IndexedHashMap<>();
	private IndexedHashMap<String, ArrayList<String>> reactiontogenes=new IndexedHashMap<>();
	
	public GeneReactionsInteractionsContainer(ISteadyStateGeneReactionModel model){
		this.model=model;
		makeGeneReactionsMapping();
	}
	
	
	protected void makeGeneReactionsMapping(){
		HashMap<String, ArrayList<String>> genetoreactionmap=((SteadyStateGeneReactionModel)model).getGeneReactionMapping();
		for (Map.Entry<String, ArrayList<String>> elem: genetoreactionmap.entrySet()) {
			genetoreactions.put(elem.getKey(), elem.getValue());
			
			String geneid=elem.getKey();
			ArrayList<String> reactions =elem.getValue();
			for (int i = 0; i < reactions.size(); i++) {
				addReactionGeneInteraction(reactions.get(i), geneid);
			}
			
		}
		
	}
	
	protected void addReactionGeneInteraction(String reactionid, String geneid){
		if(reactiontogenes.containsKey(reactionid))
			reactiontogenes.get(reactionid).add(geneid);
		else{
			ArrayList<String> genes =new ArrayList<>();
			genes.add(geneid);
			reactiontogenes.put(reactionid, genes);
		}
	}
	
	
	public ArrayList<String> getGenesRegulateReaction(String reactid){
		if(reactiontogenes.containsKey(reactid))
			return reactiontogenes.get(reactid);
		return null;
	}
	
	public IndexedHashMap<String, ArrayList<String>> getGenesRegulateReactions(ArrayList<String> reactionids){
		IndexedHashMap<String, ArrayList<String>> res=new IndexedHashMap<>();
		for (int i = 0; i < reactionids.size(); i++) {
			ArrayList<String> genes=getGenesRegulateReaction(reactionids.get(i));
			if(genes!=null)
				res.put(reactionids.get(i), genes);
		}
		return res;
	}
	
	
	public ArrayList<String> getReactionsRegulatedByGene(String geneid){
		if(genetoreactions.containsKey(geneid))
			return genetoreactions.get(geneid);
		return null;
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
