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
import java.util.HashSet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;

public class LinkedGenesAnalyser {

	
	private IIntegratedStedystateModel model;
	private ArrayList<String> geneidstoanalyse;
	
	IndexedHashMap<String, String> ruleid2modelgeneid=new IndexedHashMap<>();
	IndexedHashMap<String, String> modelgeneid2ruleid=new IndexedHashMap<>();
	
	Multimap<String, String> componentsincludedon=ArrayListMultimap.create();
	
	public LinkedGenesAnalyser(IIntegratedStedystateModel model, ArrayList<String> geneids) {
		this.model=model;
		this.geneidstoanalyse=geneids;
		decomposeRuleComponents();
	}

	
	
	
	
	private void decomposeRuleComponents() {
		
		IndexedHashMap<String, RegulatoryRule> rules=model.getRegulatoryNetwork().getRegulatoryRules();
		
		for (int i = 0; i < rules.size(); i++) {
			RegulatoryRule rule=rules.getValueAt(i);
			String ruleid=rule.getRuleId();
			ArrayList<String> rulevars=rule.getVariables();
			
			ruleid2modelgeneid.put(ruleid, rules.getKeyAt(i));
			modelgeneid2ruleid.put(rules.getKeyAt(i), ruleid);
			
			for (int j = 0; j < rulevars.size(); j++) {
				componentsincludedon.put(rulevars.get(j), ruleid);
			}
		}
		
		
		/*for (String key : componentsincludedon.keySet()) {
			System.out.println(key+" --> "+componentsincludedon.get(key));
		}
		
       System.out.println("\n\n");*/
		
	}
	
	
	
	public ArrayList<String> getGenesInRegulatoryModelDirectlyAffectedByInputGeneList(){
		
		
		ArrayList<String> res=new ArrayList<>();
		
		
		for (int i = 0; i < geneidstoanalyse.size(); i++) {
			
			String ruleidtag=modelgeneid2ruleid.get(geneidstoanalyse.get(i));
			
			ArrayList<String> influenceon=checkInfluencesOfGeneInRegulatoryRules(ruleidtag);
			for (int j = 0; j < influenceon.size(); j++) {
				String decoded=ruleid2modelgeneid.get(influenceon.get(j));
				if(!res.contains(decoded))
					res.add(decoded);
			}
		}
		
		return res;
	}
	
	
	
	
	
	private ArrayList<String> checkInfluencesOfGeneInRegulatoryRules(String id){
		
		ArrayList<String> res=new ArrayList<>();
		
		
		HashSet<String> influencedrules=new HashSet<>(componentsincludedon.get(id));

		
		if(influencedrules.size()>0) {

			for (String ruleid : influencedrules) {
				
				ArrayList<String> links=checkInfluencesOfGeneInRegulatoryRules(ruleid);
				if(links.size()>0) {
					for (int i = 0; i < links.size(); i++) {
						if(!res.contains(links.get(i)))
							res.add(links.get(i));
					}
				}
				else {
					if(res.add(ruleid));
				}
			}
		}

	  return res;
		
	}
	
	public static ArrayList<String> extractGenesInRegulatoryModelDirectlyAffectedByInputGeneList(IIntegratedStedystateModel model, ArrayList<String> geneids){
		LinkedGenesAnalyser analyser=new LinkedGenesAnalyser(model, geneids);
		return analyser.getGenesInRegulatoryModelDirectlyAffectedByInputGeneList();
	}
	
	
}
