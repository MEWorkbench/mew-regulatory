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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.LogicalModelImpl;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.ornrocha.collections.MTUMapUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;

public class BDDLogicalModel extends LogicalModelImpl implements IRODDRegulatoryModel{
	
	
	private static final long serialVersionUID = 1L;
	protected IndexedHashMap<String, Regulator> regulators;
	protected IndexedHashMap<String, RegulatoryRule> optfluxregulatoryrules;
	protected LinkedHashSet<String> unconstrainedgenes;
	protected IndexedHashMap<String,Integer> geneid2index;
	protected IndexedHashMap<Integer,String> index2geneid;
	protected IndexedHashMap<String, Integer> mapvariableid2index;
	protected IndexedHashMap<String, RegulatoryVariable> regulatoryvariables;
	protected IndexedHashMap<String, RegulatoryModelComponent> mapvariable2comptype;
	protected Map<String, String> mapelementid2elementname;
	protected Map<String, String> mapelementname2elemenetid;
	protected Map<String, Integer> mapelementid2index;
	protected IndexedHashMap<String, String> GeneID2RuleID=null;
	protected IndexedHashMap<String, String> RuleID2GeneID=null;
	protected boolean genesInRuleLinkByRuleID=false; 
	
	

	
	@SuppressWarnings("unchecked")
	public BDDLogicalModel(List<NodeInfo> nodeOrder, 
			MDDManager ddmanager, 
			int[] functions,
			IndexedHashMap<String,Integer> mapgeneid2index,
			IndexedHashMap<String, Integer> mapvariableid2index,
			IndexedHashMap<String, Regulator> genes,
			IndexedHashMap<String, RegulatoryRule> regulatoryRules,
			IndexedHashMap<String, RegulatoryVariable> regulatoryvariables,
			IndexedHashMap<String, RegulatoryModelComponent> mapvariable2comptype,
			Map<String, String> mapvaridtovarname,
			Map<String, Integer> mapvaridtoindex,
			IndexedHashMap<String, String> geneid2ruleid,
			LinkedHashSet<String> notinfluencedgenes,
			boolean genesidlinkbyruleid) throws InstantiationException, IllegalAccessException {
		super(nodeOrder, ddmanager, functions);
		this.geneid2index=mapgeneid2index;
		if(mapgeneid2index!=null)
			this.index2geneid=(IndexedHashMap<Integer, String>) MTUMapUtils.invertMap(mapgeneid2index);
		this.mapvariableid2index=mapvariableid2index;
		this.regulators=genes;
		this.optfluxregulatoryrules=regulatoryRules;
		this.regulatoryvariables=regulatoryvariables;
		this.mapvariable2comptype=mapvariable2comptype;		
		this.mapelementid2elementname=mapvaridtovarname;
		if(mapvaridtovarname!=null)
			this.mapelementname2elemenetid=MTUMapUtils.invertMap(mapvaridtovarname);
		this.mapelementid2index=mapvaridtoindex;
		this.unconstrainedgenes=notinfluencedgenes;
		this.GeneID2RuleID=geneid2ruleid;
		this.RuleID2GeneID=(IndexedHashMap<String, String>) MTUMapUtils.invertMap(geneid2ruleid);
		this.genesInRuleLinkByRuleID=genesidlinkbyruleid;
	}
	

	
	
	
	@Override
	public Integer getNumberOfRegulatoryRules() {
		return geneid2index.size();
	}
	
	@Override
	public Integer getRegulatoryGeneIndex(String geneId) {
		if(geneid2index.containsKey(geneId))
		    return geneid2index.get(geneId);
		return null;
	}
	
	
	@Override
	public Integer getRuleIndexForIdentifier(String ruleId) {
		String geneid=RuleID2GeneID.get(ruleId);
		if(geneid!=null && geneid2index.containsKey(geneid))
			return geneid2index.get(geneid);
		return -1;
	}


	@Override
	public String getIdentifierOfRuleIndex(Integer ruleIndex) {
		if(index2geneid!=null && index2geneid.containsKey(ruleIndex))
			return index2geneid.get(ruleIndex);
		return null;
	}
	
	@Override
	public String getRuleIdAtIndex(Integer ruleIndex) {
		String geneid=index2geneid.get(ruleIndex);
		if(geneid!=null && GeneID2RuleID.containsKey(geneid))
			return GeneID2RuleID.get(geneid);
		return null;
	}
	
	@Override
	public IndexedHashMap<String, String> getMapGeneId2RuleId() {
		return (IndexedHashMap<String, String>) GeneID2RuleID;
	}
	
	@Override
	public Integer getNumberOfVariables() {
		return mapvariableid2index.size();
	}
	
	@Override
	public Integer getVariableIndex(String variableName) {
		if(mapvariableid2index.containsKey(variableName))
			return mapvariableid2index.get(variableName);
		return -1;
	}
	
	
	@Override
	public ArrayList<String> getVariableNamesInNetwork() {
		ArrayList<String> vars = new ArrayList<String>();
		for (String varid : mapvariableid2index.keySet()) {
			vars.add(varid);
		}
		return vars;
	}
	
	@Override
	public RegulatoryModelComponent getTypeOfVariable(String varid) {
		if(mapvariable2comptype.containsKey(varid))
			return mapvariable2comptype.get(varid);
		return null;
	}
	
	@Override
	public void changeVariableType(String varid, RegulatoryModelComponent type) {
		if(regulatoryvariables.containsKey(varid)){
			regulatoryvariables.get(varid).setComponentType(type);
			mapvariable2comptype.put(varid, type);	
		}
	}
	
	
	@Override
	public IndexedHashMap<String, RegulatoryVariable> getVariablesInRegulatoryNetwork() {
		return regulatoryvariables;
	}



	@Override
	public RegulatoryVariable getVariableByIndex(int variableIDX) {
		if(variableIDX <= regulatoryvariables.size())
			return regulatoryvariables.getValueAt(variableIDX);
		return null;
	}



	@Override
	public RegulatoryVariable getRegulatoryVariable(String id) {
		if(regulatoryvariables.containsKey(id))
			return regulatoryvariables.get(id);
		return null;
	}
	
	public void setGenesWithoutInteractions(LinkedHashSet<String> genesnointeractions){
		this.unconstrainedgenes=genesnointeractions;
	}
	
	
	@Override
	public IndexedHashMap<String, RegulatoryModelComponent> getTypeofRegulatoryVariables() {
		IndexedHashMap<String, RegulatoryModelComponent> vartype=new IndexedHashMap<>();
		for (Map.Entry<String, RegulatoryModelComponent> map : mapvariable2comptype.entrySet()) {
			String id=map.getKey();
			if(mapvariableid2index.containsKey(id))
				vartype.put(id, map.getValue());
		}
		return vartype;
	}
	
	@Override
	public Integer getNumberOfRegulators() {
		return geneid2index.size();
	}
	
	@Override
	public Integer getRegulatorIndex(String geneId) {
		if(geneid2index.containsKey(geneId))
			return geneid2index.get(geneId);
		return null;
	}
	
	@Override
	public IndexedHashMap<String, Integer> getGeneIndexes() {
		return geneid2index;
	}
	@Override
	public boolean isRegulatorOnlyAtRegulatoryNetwork(String geneid){
		return geneid2index.containsKey(geneid);
	}
	
	@Override
	public String getRegulatorIDAssociatedToRuleID(String ruleid) {
		if(RuleID2GeneID.containsKey(ruleid))
			return RuleID2GeneID.get(ruleid);
		return null;
	}
	
	@Override
	public String getRuleIDAssociatedToRegulatorID(String geneid) {
		if(GeneID2RuleID.containsKey(geneid))
			return GeneID2RuleID.get(geneid);
		return null;
	}
	
	
	@Override
	public ArrayList<String> getRegulatorIDs() {
		ArrayList<String> geneids=new ArrayList<>();
		for (int i = 0; i < geneid2index.size(); i++) {
			geneids.add(geneid2index.getKeyAt(i));
		}
		return geneids;
	}
	
	
	@Override
	public LinkedHashSet<String> getUnconstrainedGenes() {
		return unconstrainedgenes;
	}
	
	
	public Map<String, Integer> getElementId2Index() {
		return mapelementid2index;
	}
	
	@Override
	public IRegulatoryNetwork copy() {
		return (IRegulatoryNetwork) clone();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public LogicalModel clone(){
		BDDLogicalModel clone=null;
		try {
			clone= new BDDLogicalModel(getNodeOrder(),
					getMDDManager(),
					getLogicalFunctions().clone(), 
					(IndexedHashMap<String,Integer>)MTUCollectionsUtils.deepCloneObject(geneid2index),
					(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(mapvariableid2index),
					(IndexedHashMap<String, Regulator>) MTUCollectionsUtils.deepCloneObject(regulators),
					(IndexedHashMap<String, RegulatoryRule>) MTUCollectionsUtils.deepCloneObject(optfluxregulatoryrules),
					(IndexedHashMap<String, RegulatoryVariable>)MTUCollectionsUtils.deepCloneObject(regulatoryvariables),
					(IndexedHashMap<String, RegulatoryModelComponent>)MTUCollectionsUtils.deepCloneObject(mapvariable2comptype),
					(Map<String, String>)MTUCollectionsUtils.deepCloneObject(mapelementid2elementname),
					(Map<String, Integer>)MTUCollectionsUtils.deepCloneObject(mapelementid2index),
					(IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(GeneID2RuleID),
					(LinkedHashSet<String>)MTUCollectionsUtils.deepCloneObject(unconstrainedgenes),
					this.genesInRuleLinkByRuleID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clone;
		
	}



	@Override
	public String getModelID() {
		return null;
	}



	@Override
	public String getRegulatorIdAtIndex(int index) {
		return index2geneid.get(index);
	}



	@Override
	public boolean genesInRuleLinkByRuleID() {
		return genesInRuleLinkByRuleID;
	}



	@Override
	public IndexedHashMap<String, Regulator> getRegulators() {
		return regulators;
	}



	@Override
	public RegulatoryRule getRegulatoryRule(int ruleIndex) {
		return optfluxregulatoryrules.getValueAt(ruleIndex);
	}



	@Override
	public RegulatoryRule getRegulatoryRuleToRegulatorId(String geneid) {
		return optfluxregulatoryrules.get(geneid);
	}



	@Override
	public IndexedHashMap<String, RegulatoryRule> getRegulatoryRules() {
		return optfluxregulatoryrules;
	}



	




	



	






	


	


	


	


	


	


	


	


	


	


	


	
	
	
	


	




	

}
