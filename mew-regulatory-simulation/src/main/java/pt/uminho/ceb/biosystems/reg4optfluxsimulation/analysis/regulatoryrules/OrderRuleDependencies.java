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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.analysis.regulatoryrules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import pt.ornrocha.collections.MTUMapUtils;
import pt.ornrocha.logutils.MTULogLevel;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.sbmlqual.SBMLQualReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;

public class OrderRuleDependencies {
	
	
	private IndexedHashMap<String, String> TFid2Rule=new IndexedHashMap<>();
	private IndexedHashMap<String, String> ruleid2geneid=new IndexedHashMap<>();
	private IndexedHashMap<String, String> geneid2ruleid=new IndexedHashMap<>();
	private IndexedHashMap<String, String> reorderedrules=new IndexedHashMap<>();
	private LinkedHashSet<String> tfswithoutdependencies=new LinkedHashSet<>();
	private LinkedHashMap<String, ArrayList<String>> tfswithdependencies=new LinkedHashMap<>();
	private LinkedHashMap<String, Integer> tfsdependencieslevel=new LinkedHashMap<>();
	private IndexedHashMap<String, Integer> originalorder=new IndexedHashMap<>();
	private HashSet<String> TFsIds=new HashSet<>();
	
	public OrderRuleDependencies(IndexedHashMap<String, RegulatoryRule> rules){
		processRules(rules);
		executeReorder();
	}
	
	
	private void executeReorder(){
		checkTFDependencies();
		checkLevelDependencies();
		reorderRuleIndexes();
	}
	
	
	
	private void processRules(IndexedHashMap<String, RegulatoryRule> rules){
		
		for (int i = 0; i < rules.size(); i++) {
			String geneid=rules.getKeyAt(i);
			RegulatoryRule rule=rules.get(geneid);
			String rulestring=rule.getRule();
			String ruleid=rule.getRuleId();
			
			TFsIds.add(ruleid);
			originalorder.put(geneid, i);
			if(rulestring!=null && !rulestring.isEmpty()){
				TFid2Rule.put(ruleid, rulestring);
				ruleid2geneid.put(ruleid, geneid);
				geneid2ruleid.put(geneid, ruleid);
				
				LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("TF --> Rule: ", ruleid, rulestring,"\n");
				LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("TF --> gene: ", ruleid, geneid,"\n");
			}
			
		}
		LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator(null);
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Total TFs: ",TFsIds,"\n");
	}
	
	
	
	private void checkTFDependencies(){
		
		for (int i = 0; i < TFid2Rule.size(); i++) {
			
			String tfident=TFid2Rule.getKeyAt(i);
			ArrayList<String> ruletfsdependencies=getTFSInrule(tfident, new ArrayList<String>());
			
			
			if(ruletfsdependencies!=null)
				tfswithdependencies.put(tfident, ruletfsdependencies);
			else
				tfswithoutdependencies.add(tfident);
		}
		
		LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator(" TFs with dependencies: ");
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("TFs with dependencies: ",tfswithdependencies,"\n");
		LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator(" TFs without dependencies: ");
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("No dependencies",tfswithoutdependencies,"\n");
	
	}
	
	
	private void checkLevelDependencies(){
		
		for (String tf : tfswithdependencies.keySet()) {
			ArrayList<String> depend=tfswithdependencies.get(tf);
			
			int level=getLevelDependency(depend, 1,tf);
			
			tfsdependencieslevel.put(tf, level);
		}
		
		LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator(" TFs level dependencies: ");
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("level: ",tfsdependencieslevel,"\n");
	}
	
	
	private int getLevelDependency(ArrayList<String> dependencies, int parentlevel, String roottf){
		
		int level=parentlevel;
		
		for (int i = 0; i < dependencies.size(); i++) {
			String tfid=dependencies.get(i);
			
			int inputlevel=0;
			
			if(tfswithoutdependencies.contains(tfid))
				inputlevel=parentlevel;
			else if(tfid.equals(roottf))
				inputlevel=parentlevel;
			else{
				level++;
				inputlevel=getLevelDependency(tfswithdependencies.get(tfid), level,roottf);
			}
			
			if(inputlevel>level)
				level=inputlevel;
			
		}
		
		return level;
	}
	
	
	private void reorderRuleIndexes(){
		
		LinkedHashMap<String, Integer> orderedlevels=(LinkedHashMap<String, Integer>) MTUMapUtils.sortMapByValues(tfsdependencieslevel, true);
		
		
		for (String id : tfswithoutdependencies) {
			reorderedrules.put(ruleid2geneid.get(id), TFid2Rule.get(id));
		}
		
		for (String id : orderedlevels.keySet()) {
			putTFwithDependenciesInReorderedRules(id, tfswithdependencies.get(id));
		}
		
		LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator("New reorder rules",2,1);
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("New Rules Order: ",reorderedrules,"\n");
		
	
	}
	
	
	private void putTFwithDependenciesInReorderedRules(String rootid, ArrayList<String> dependencies){
		
		for (int i = 0; i < dependencies.size(); i++) {
			
			String tfid=dependencies.get(i);
			if(tfswithoutdependencies.contains(tfid) || reorderedrules.containsKey(ruleid2geneid.get(tfid))){
				continue;
			}
			else{
				putTFwithDependenciesInReorderedRules(tfid, tfswithdependencies.get(tfid));
			}
		}
		if(!reorderedrules.containsKey(ruleid2geneid.get(rootid))){
			reorderedrules.put(ruleid2geneid.get(rootid), TFid2Rule.get(rootid));
		}
		
	}
	
	
	

	
	private ArrayList<String> getTFSInrule(String tfname, ArrayList<String> cache){
		
		ArrayList<String> res = null;
		 String rule = TFid2Rule.get(tfname);
		
		for (String tf : TFsIds) {
			
			if(rule.contains(tf)){

				if(res==null)
				    res=new ArrayList<>();
				
				res.add(tf);
			}
		}
		
		return res;
	}
	
	
	public IndexedHashMap<String, String> getNewSortedRules(){
		return reorderedrules;
	}
	
	public IndexedHashMap<String, Integer> getOriginalOrder(){
		return originalorder;
	}
	

	public static IndexedHashMap<String, String> sortRules(IndexedHashMap<String, RegulatoryRule> rules){
		OrderRuleDependencies reorder=new OrderRuleDependencies(rules);
		return reorder.getNewSortedRules();
	}
	
	
	public static OrderRuleDependencies load(IndexedHashMap<String, RegulatoryRule> rules){
		OrderRuleDependencies reorder=new OrderRuleDependencies(rules);
		return reorder;
	}
	
	
	
	
	public static void main(String[] args) throws Exception{
		
		String modelfile="/home/orocha/ownCloud/Models_Optflux/sbml1366/SBMLqualiMC1010v2_SBML1366.xml";
		SBMLQualReader reader=new SBMLQualReader(modelfile);
		reader.loadModel();
		
		LogMessageCenter.getLogger().restrictLogToClasses(OrderRuleDependencies.class);
		LogMessageCenter.getLogger().setLogLevel(MTULogLevel.TRACE);
		
		IRegulatoryNetwork model=reader.getOptFluxModelFormat();
		OrderRuleDependencies dep=new OrderRuleDependencies(model.getRegulatoryRules());
		
	}

}
