package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.mapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTree;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.DataTypeEnum;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.parser.ParseException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.OptfluxRegulatoryModel;

public class OverrideRegulatoryModel implements IRegulatoryOverrideModel, Serializable{

	
	
	private static final long serialVersionUID = 1L;
	protected Set<String> knockoutgenelist;
	protected IOptfluxRegulatoryModel originalModel;
	protected ArrayList<Integer> knockoutlistindex = null;
	
	public OverrideRegulatoryModel(IOptfluxRegulatoryModel originalmodel, Set<String> knockoutList){
		this.originalModel=originalmodel;
		this.knockoutgenelist=knockoutList;
		if(knockoutList!=null)
			generateIndexedGeneOFF(knockoutList);
	}
	
	private void generateIndexedGeneOFF(Set<String> knockoutgenelist) {
		  ArrayList<Integer> ret = new ArrayList<Integer>();
		
		  for(String id : knockoutgenelist){
			ret.add(originalModel.getRegulatoryGeneIndex(id));
		   }
		  
		  this.knockoutlistindex=ret; 
	}
	

	@Override
	public Gene getGene(int geneIndex) {
		return originalModel.getGene(geneIndex);
	}

	@Override
	public Gene getGene(String geneId) {
		return originalModel.getGene(geneId);
	}

	@Override
	public IndexedHashMap<String, Regulator> getRegulators() {
		return originalModel.getRegulators();
	}

	
	@Override
	public RegulatoryRule getRegulatoryRule(int ruleIndex) {
		RegulatoryRule ret = null;
		
		if(knockoutlistindex!=null && knockoutlistindex.contains(ruleIndex)){
			try {
				ret = new RegulatoryRule(originalModel.getRegulatoryRule(ruleIndex).getRuleId(),"");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else
			ret=originalModel.getRegulatoryRule(ruleIndex);
		
		return ret;
	}

	@Override
	public RegulatoryRule getRegulatoryRuleToRegulatorId(String geneid) {
		RegulatoryRule ret = null;
		if(knockoutgenelist!=null && knockoutgenelist.contains(geneid)){
			try {
				ret = new RegulatoryRule(originalModel.getRegulatoryRuleToRegulatorId(geneid).getRuleId(),"");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else
			ret=originalModel.getRegulatoryRuleToRegulatorId(geneid);
		
		return ret;
	}

	@Override
	public IndexedHashMap<String, RegulatoryRule> getRegulatoryRules() {
		IndexedHashMap<String, RegulatoryRule> originalrules=originalModel.getRegulatoryRules();
	     IndexedHashMap<String, RegulatoryRule> newrules=new IndexedHashMap<>();
	     
	     for (int i = 0; i < originalrules.size(); i++) {
	    	 String id=originalrules.getKeyAt(i);
	    	 
	    	 RegulatoryRule newrule=getRegulatoryRuleToRegulatorId(id);
	    	 newrules.put(id, newrule);
			
		}
		return newrules;
	}

	@Override
	public void setRegulatoryRules(IndexedHashMap<String, RegulatoryRule> newrules) {
		originalModel.setRegulatoryRules(newrules);
	}

	@Override
	public Integer getNumberOfRegulatoryRules() {
		return originalModel.getNumberOfRegulatoryRules();
	}

	

	@Override
	public Integer getNumberOfRegulators() {
		return originalModel.getNumberOfRegulators();
	}

	@Override
	public Integer getRegulatorIndex(String geneId) {
		return originalModel.getRegulatorIndex(geneId);
	}

	@Override
	public boolean isRegulatorOnlyAtRegulatoryNetwork(String geneid) {
		return originalModel.isRegulatorOnlyAtRegulatoryNetwork(geneid);
	}

	@Override
	public String getRegulatorIDAssociatedToRuleID(String ruleid) {
		return originalModel.getRegulatorIDAssociatedToRuleID(ruleid);
	}

	@Override
	public String getRuleIDAssociatedToRegulatorID(String geneid) {
		return originalModel.getRuleIDAssociatedToRegulatorID(geneid);
	}

	@Override
	public ArrayList<String> getRegulatorIDs() {
		return originalModel.getRegulatorIDs();
	}

	@Override
	public LinkedHashSet<String> getUnconstrainedGenes() {
		LinkedHashSet<String> unconstrainedgenes=new LinkedHashSet<>(originalModel.getUnconstrainedGenes());
		
		IndexedHashMap<String, RegulatoryRule> currentgenerules=getRegulatoryRules();
		
		for (int i = 0; i < currentgenerules.size(); i++) {
			String geneid=currentgenerules.getKeyAt(i);
			RegulatoryRule rule=currentgenerules.get(geneid);
			AbstractSyntaxTree<DataTypeEnum, IValue> ast=rule.getBooleanRule();
			if(ast==null && !unconstrainedgenes.contains(geneid))
				unconstrainedgenes.add(geneid);
		}

		return unconstrainedgenes;
	}

	@Override
	public Integer getRegulatoryGeneIndex(String geneId) {
		return originalModel.getRegulatoryGeneIndex(geneId);
	}

	@Override
	public Integer getRuleIndexForIdentifier(String ruleId) {
		return originalModel.getRuleIndexForIdentifier(ruleId);
	}

	@Override
	public String getIdentifierOfRuleIndex(Integer ruleIndex) {
		return originalModel.getIdentifierOfRuleIndex(ruleIndex);
	}

	@Override
	public String getRuleIdAtIndex(Integer ruleIndex) {
		return originalModel.getRuleIdAtIndex(ruleIndex);
	}

	@Override
	public IndexedHashMap<String, String> getMapGeneId2RuleId() {
		return originalModel.getMapGeneId2RuleId();
	}

	@Override
	public Integer getNumberOfVariables() {
		return originalModel.getNumberOfVariables();
	}

	@Override
	public Integer getVariableIndex(String variableName) {
		return originalModel.getVariableIndex(variableName);
	}

	@Override
	public ArrayList<String> getVariableNamesInNetwork() {
		return originalModel.getVariableNamesInNetwork();
	}

	@Override
	public RegulatoryModelComponent getTypeOfVariable(String varid) {
		return originalModel.getTypeOfVariable(varid);
	}

	@Override
	public IndexedHashMap<String, RegulatoryModelComponent> getTypeofRegulatoryVariables() {
		return originalModel.getTypeofRegulatoryVariables();
	}

	@Override
	public void changeVariableType(String varid, RegulatoryModelComponent type) {
		originalModel.changeVariableType(varid, type);
		
	}

	@Override
	public IndexedHashMap<String, RegulatoryVariable> getVariablesInRegulatoryNetwork() {
		return originalModel.getVariablesInRegulatoryNetwork();
	}

	@Override
	public RegulatoryVariable getVariableByIndex(int variableIDX) {
		return originalModel.getVariableByIndex(variableIDX);
	}

	@Override
	public RegulatoryVariable getRegulatoryVariable(String id) {
		return originalModel.getRegulatoryVariable(id);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public IRegulatoryNetwork copy() throws Exception {
		return new OptfluxRegulatoryModel(originalModel.getModelID(), 
				((IndexedHashMap<String, Regulator>)MTUCollectionsUtils.deepCloneObject(originalModel.getRegulators())), 
				((IndexedHashMap<String, RegulatoryRule>)MTUCollectionsUtils.deepCloneObject(getRegulatoryRules())), 
				((IndexedHashMap<String, RegulatoryVariable>)MTUCollectionsUtils.deepCloneObject(originalModel.getVariablesInRegulatoryNetwork())), 
				((IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(originalModel.getMapGeneId2RuleId())), 
				/*((IndexedHashMap<String, RegulatoryModelComponent>)MTUCollectionsUtils.deepCloneObject(originalModel.getTypeofRegulatoryVariables())),*/
				(LinkedHashSet<String>) MTUCollectionsUtils.deepCloneObject(getUnconstrainedGenes()),
				originalModel.genesInRuleLinkByRuleID());

	}

	@Override
	public String getModelID() {
		return originalModel.getModelID();
	}

	@Override
	public String getRegulatorIdAtIndex(int index) {
		return originalModel.getRegulatorIdAtIndex(index);
	}

	@Override
	public boolean genesInRuleLinkByRuleID() {
		return originalModel.genesInRuleLinkByRuleID();
	}
	
	

	

}
