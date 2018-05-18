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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.ornrocha.collections.MTUMapUtils;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;

public class OptfluxRegulatoryModel implements IOptfluxRegulatoryModel{

	
	private static final long serialVersionUID = 1L;
	
	protected String modelID;
	protected IndexedHashMap<String, Regulator> regulators;
	protected IndexedHashMap<String, RegulatoryRule> regulatorRulesRegulatoryNetwork;
	protected IndexedHashMap<String, RegulatoryVariable> variablesOfRegulatoryNetwork;
	//protected IndexedHashMap<String, RegulatoryModelComponent> variableTypeOfRegulatoryNetwork;
	protected LinkedHashSet<String> unconstrainedgenes;
	protected IndexedHashMap<String, String> RegulatorID2RuleID=null;
	protected IndexedHashMap<String, String> RuleID2RegulatorID=null;
	protected boolean regulatoridlinkbyruleid=false;

	
	@SuppressWarnings("unchecked")
	public OptfluxRegulatoryModel(String modelid,
			IndexedHashMap<String, Regulator> genes,
			IndexedHashMap<String, RegulatoryRule> regulatoryRules, 
			IndexedHashMap<String, RegulatoryVariable> variables, 
			IndexedHashMap<String, String> geneid2ruleid,
			/*IndexedHashMap<String, RegulatoryModelComponent> componentcategories,*/
			LinkedHashSet<String> unconstrainedgenes,
			boolean genesidlinkbyruleid) throws InstantiationException, IllegalAccessException {
		
		this.modelID=modelid;
		this.regulators=genes;
		this.regulatorRulesRegulatoryNetwork=regulatoryRules;
		this.variablesOfRegulatoryNetwork=variables;
		this.RegulatorID2RuleID=geneid2ruleid;
		if(geneid2ruleid!=null)
			this.RuleID2RegulatorID=(IndexedHashMap<String, String>) MTUMapUtils.invertMap(geneid2ruleid);
		//this.variableTypeOfRegulatoryNetwork=componentcategories;
		this.unconstrainedgenes=unconstrainedgenes;
		this.regulatoridlinkbyruleid=genesidlinkbyruleid;
		
	}
	
	
	// Create Empty model for Dynamic Regulatory model operations
	private OptfluxRegulatoryModel() {
		this.modelID="EmptyModel";
		this.regulators=new IndexedHashMap<>();
		this.regulatorRulesRegulatoryNetwork=new IndexedHashMap<>();
		this.variablesOfRegulatoryNetwork=new IndexedHashMap<>();
		this.RegulatorID2RuleID=new IndexedHashMap<>();
		this.RuleID2RegulatorID=new IndexedHashMap<>();
		this.variablesOfRegulatoryNetwork=new IndexedHashMap<>();
		this.unconstrainedgenes=new LinkedHashSet<>();
		this.regulatoridlinkbyruleid=true;
	}
	

	@Override
	public Integer getNumberOfRegulatoryRules() {
		return regulatorRulesRegulatoryNetwork.size();
	}

	
	@Override
	public Integer getRegulatoryGeneIndex(String geneId) {
		if(regulators.containsKey(geneId))
			return regulators.getIndexOf(geneId);
		return null;
	}
	
	
	@Override
	public Integer getRuleIndexForIdentifier(String ruleId) {
		return regulatorRulesRegulatoryNetwork.getIndexOf(RuleID2RegulatorID.get(ruleId));
	}

	@Override
	public String getIdentifierOfRuleIndex(Integer ruleIndex) {
		return regulatorRulesRegulatoryNetwork.getKeyAt(ruleIndex);
	}
	
	
	@Override
	public String getRuleIdAtIndex(Integer ruleIndex) {
		String geneid=getIdentifierOfRuleIndex(ruleIndex);
		if(geneid!=null && RegulatorID2RuleID.containsKey(geneid))
			return RegulatorID2RuleID.get(geneid);
		return null;
	}
	
	@Override
	public IndexedHashMap<String, String> getMapGeneId2RuleId() {
		return (IndexedHashMap<String, String>) RegulatorID2RuleID;
	}
	
	@Override
	public RegulatoryRule getRegulatoryRule(int ruleIndex) {
		return regulatorRulesRegulatoryNetwork.getValueAt(ruleIndex);
	}


	@Override
	public RegulatoryRule getRegulatoryRuleToRegulatorId(String geneid) {
		return regulatorRulesRegulatoryNetwork.get(geneid);
	}


	@Override
	public IndexedHashMap<String, RegulatoryRule> getRegulatoryRules() {
		return regulatorRulesRegulatoryNetwork;
	}


	@Override
	public void setRegulatoryRules(IndexedHashMap<String, RegulatoryRule> newrules) {
			this.regulatorRulesRegulatoryNetwork=newrules;
	}
	
	
	@Override
	public Integer getNumberOfVariables() {
		return variablesOfRegulatoryNetwork.size();
	}
	
	@Override
	public Integer getVariableIndex(String variableName) {
		if (variablesOfRegulatoryNetwork.containsKey(variableName))
			   return variablesOfRegulatoryNetwork.getIndexOf(variableName);
		return -1;
	}
	
	
	@Override
	public ArrayList<String> getVariableNamesInNetwork() {
		ArrayList<String> vars = new ArrayList<String>();
		for (int i = 0; i <variablesOfRegulatoryNetwork.size(); i++) {
			String varid=variablesOfRegulatoryNetwork.getKeyAt(i);
			vars.add(variablesOfRegulatoryNetwork.get(varid).getId());
		}
		
		return vars;
	}
	
	@Override
	public RegulatoryModelComponent getTypeOfVariable(String varid) {
		if(variablesOfRegulatoryNetwork.containsKey(varid))
			return variablesOfRegulatoryNetwork.get(varid).getType();
		return null;
	}
	
	@Override
	public void changeVariableType(String varid, RegulatoryModelComponent type) {
		if(variablesOfRegulatoryNetwork.containsKey(varid)){
			variablesOfRegulatoryNetwork.get(varid).setComponentType(type);
			
			//variableTypeOfRegulatoryNetwork.put(varid, type);
		}
		
	}
	
	@Override
	public IndexedHashMap<String, RegulatoryModelComponent> getTypeofRegulatoryVariables() {
		IndexedHashMap<String, RegulatoryModelComponent> vartype=new IndexedHashMap<>();
		
		for (String varid : variablesOfRegulatoryNetwork.keySet()) {
			vartype.put(varid, variablesOfRegulatoryNetwork.get(varid).getType());
		}
		/*
		for (Map.Entry<String, RegulatoryModelComponent> map : variableTypeOfRegulatoryNetwork.entrySet()) {
			String id=map.getKey();
			if(variablesOfRegulatoryNetwork.containsKey(id))
				vartype.put(id, map.getValue());
		}*/
		return vartype;
	}
	
	@Override
	public IndexedHashMap<String, RegulatoryVariable> getVariablesInRegulatoryNetwork() {
		return variablesOfRegulatoryNetwork;
	}


	@Override
	public RegulatoryVariable getVariableByIndex(int variableIDX) {
		if(variableIDX <= variablesOfRegulatoryNetwork.size())
			return variablesOfRegulatoryNetwork.getValueAt(variableIDX);
		return null;
	}


	@Override
	public RegulatoryVariable getRegulatoryVariable(String id) {
		if(variablesOfRegulatoryNetwork.containsKey(id))
			return variablesOfRegulatoryNetwork.get(id);
		return null;
	}
	@Override
	public Integer getNumberOfRegulators() {
		return regulators.size();
	}
	
	@Override
	public Integer getRegulatorIndex(String geneId) {
		if(regulators.containsKey(geneId))
			return regulators.getIndexOf(geneId);
		return null;
	}
	
	@Override
	public boolean isRegulatorOnlyAtRegulatoryNetwork(String geneid){
		return regulators.containsKey(geneid);
	}
	
	@Override
	public String getRegulatorIDAssociatedToRuleID(String ruleid) {
		if(RuleID2RegulatorID.containsKey(ruleid))
			return RuleID2RegulatorID.get(ruleid);
		return null;
	}
	
	@Override
	public String getRuleIDAssociatedToRegulatorID(String geneid) {
		if(RegulatorID2RuleID.containsKey(geneid))
			return RegulatorID2RuleID.get(geneid);
		return null;
	}
	
	@Override
	public ArrayList<String> getRegulatorIDs() {
		ArrayList<String> geneids=new ArrayList<>();
		for (int i = 0; i < regulators.size(); i++) {
			geneids.add(regulators.getKeyAt(i));
		}
		return geneids;
	}
	
	
	@Override
	public Gene getGene(int geneIndex) {
		return regulators.getValueAt(geneIndex);
	}
	
	@Override
	public String getRegulatorIdAtIndex(int index) {
		return regulators.getKeyAt(index);
	}
	
	@Override
	public Gene getGene(String geneId) {
		return regulators.get(geneId);
	}
	
	@Override
	public IndexedHashMap<String, Regulator> getRegulators() {
		return regulators;
	}
	
	@Override
	public LinkedHashSet<String> getUnconstrainedGenes() {
		return unconstrainedgenes;
	}
	
	@Override
	public String getModelID() {
		return modelID;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public IRegulatoryNetwork copy() throws Exception {
		return new OptfluxRegulatoryModel(modelID, 
				((IndexedHashMap<String, Regulator>)MTUCollectionsUtils.deepCloneObject(regulators)), 
				((IndexedHashMap<String, RegulatoryRule>)MTUCollectionsUtils.deepCloneObject(regulatorRulesRegulatoryNetwork)), 
				((IndexedHashMap<String, RegulatoryVariable>)MTUCollectionsUtils.deepCloneObject(variablesOfRegulatoryNetwork)), 
				((IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(RegulatorID2RuleID)), 
				//((IndexedHashMap<String, RegulatoryModelComponent>)MTUCollectionsUtils.deepCloneObject(variableTypeOfRegulatoryNetwork)), 
				(LinkedHashSet<String>) MTUCollectionsUtils.deepCloneObject(unconstrainedgenes),
				this.regulatoridlinkbyruleid);

	}


	@Override
	public boolean genesInRuleLinkByRuleID() {
		return regulatoridlinkbyruleid;
	}


	public static OptfluxRegulatoryModel getEmptyInstance(){
		return new OptfluxRegulatoryModel();
	}


	


	


	


	


	


	

	

	

}