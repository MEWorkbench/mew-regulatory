package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.mapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTree;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.DataTypeEnum;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.OptfluxRegulatoryModel;


public class RegulatoryModelMapper implements IRegulatoryModelMapper,Serializable{

	
	
	private static final long serialVersionUID = 1L;
	protected IOptfluxRegulatoryModel model;
	protected IRegulatoryOverrideModel overrideModel;
	protected IRegulatoryDecoder decoder;
	
	public RegulatoryModelMapper(IOptfluxRegulatoryModel model, IRegulatoryOverrideModel overrideModel, IRegulatoryDecoder decoder){
		this.model = model;
		this.overrideModel = overrideModel;
		this.decoder = decoder;
	}

	@Override
	public Gene getGene(int geneIndex) {
		return model.getGene(geneIndex);
	}

	@Override
	public Gene getGene(String geneId) {
		return model.getGene(geneId);
	}

	@Override
	public IndexedHashMap<String, Regulator> getRegulators() {
		return model.getRegulators();
	}

	@Override
	public RegulatoryRule getRegulatoryRule(int ruleIndex) {
		RegulatoryRule result = null;
		
		if(overrideModel != null)
			result = overrideModel.getRegulatoryRule(ruleIndex);
		
		if ((result == null) && (decoder != null))
			result = decoder.getRegulatoryRule(ruleIndex);
		
		if(result == null)
			result = model.getRegulatoryRule(ruleIndex);
		
		return result;
	}

	@Override
	public RegulatoryRule getRegulatoryRuleToRegulatorId(String geneid) {
		RegulatoryRule result = null;
		
		if(overrideModel != null)
			result = overrideModel.getRegulatoryRuleToRegulatorId(geneid);
		if ((result == null) && (decoder != null))
			result = decoder.getRegulatoryRuleToRegulatorId(geneid);
		if(result == null)
			result = model.getRegulatoryRuleToRegulatorId(geneid);
		
		
		return result;
	}

	@Override
	public IndexedHashMap<String, RegulatoryRule> getRegulatoryRules() {
		IndexedHashMap<String, RegulatoryRule> originalrules=model.getRegulatoryRules();
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
		model.setRegulatoryRules(newrules);
	}

	@Override
	public Integer getNumberOfRegulatoryRules() {
		return model.getNumberOfRegulatoryRules();
	}

	

	@Override
	public Integer getNumberOfRegulators() {
		return model.getNumberOfRegulators();
	}

	@Override
	public Integer getRegulatorIndex(String geneId) {
		return model.getRegulatorIndex(geneId);
	}

	@Override
	public boolean isRegulatorOnlyAtRegulatoryNetwork(String geneid) {
		return model.isRegulatorOnlyAtRegulatoryNetwork(geneid);
	}

	@Override
	public String getRegulatorIDAssociatedToRuleID(String ruleid) {
		return model.getRegulatorIDAssociatedToRuleID(ruleid);
	}

	@Override
	public String getRuleIDAssociatedToRegulatorID(String geneid) {
		return model.getRuleIDAssociatedToRegulatorID(geneid);
	}

	@Override
	public ArrayList<String> getRegulatorIDs() {
		return model.getRegulatorIDs();
	}

	@Override
	public LinkedHashSet<String> getUnconstrainedGenes() {
		LinkedHashSet<String> unconstrainedgenes=new LinkedHashSet<>(model.getUnconstrainedGenes());
		
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
		return model.getRegulatoryGeneIndex(geneId);
	}

	@Override
	public Integer getRuleIndexForIdentifier(String ruleId) {
		return model.getRuleIndexForIdentifier(ruleId);
	}

	@Override
	public String getIdentifierOfRuleIndex(Integer ruleIndex) {
		return model.getIdentifierOfRuleIndex(ruleIndex);
	}

	@Override
	public String getRuleIdAtIndex(Integer ruleIndex) {
		return model.getRuleIdAtIndex(ruleIndex);
	}

	@Override
	public IndexedHashMap<String, String> getMapGeneId2RuleId() {
		return model.getMapGeneId2RuleId();
	}

	@Override
	public Integer getNumberOfVariables() {
		return model.getNumberOfVariables();
	}

	@Override
	public Integer getVariableIndex(String variableName) {
		return model.getVariableIndex(variableName);
	}

	@Override
	public ArrayList<String> getVariableNamesInNetwork() {
		return model.getVariableNamesInNetwork();
	}

	@Override
	public RegulatoryModelComponent getTypeOfVariable(String varid) {
		return model.getTypeOfVariable(varid);
	}

	@Override
	public IndexedHashMap<String, RegulatoryModelComponent> getTypeofRegulatoryVariables() {
		return model.getTypeofRegulatoryVariables();
	}

	@Override
	public void changeVariableType(String varid, RegulatoryModelComponent type) {
        model.changeVariableType(varid, type);
		
	}

	@Override
	public IndexedHashMap<String, RegulatoryVariable> getVariablesInRegulatoryNetwork() {
		return model.getVariablesInRegulatoryNetwork();
	}

	@Override
	public RegulatoryVariable getVariableByIndex(int variableIDX) {
		return model.getVariableByIndex(variableIDX);
	}

	@Override
	public RegulatoryVariable getRegulatoryVariable(String id) {
		return model.getRegulatoryVariable(id);
	}

	@Override
	public IRegulatoryDecoder getDecoder() {
		return decoder;
	}

	@Override
	public void setDecoder(IRegulatoryDecoder decoder) {
		this.decoder=decoder;
		
	}

	@Override
	public IRegulatoryOverrideModel getOverrideModel() {
		return overrideModel;
	}

	@Override
	public void setOverrideModel(IRegulatoryOverrideModel overrideModel) {
		this.overrideModel=overrideModel;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IRegulatoryNetwork copy() throws Exception {
		return new OptfluxRegulatoryModel(model.getModelID(), 
				((IndexedHashMap<String, Regulator>)MTUCollectionsUtils.deepCloneObject(model.getRegulators())), 
				((IndexedHashMap<String, RegulatoryRule>)MTUCollectionsUtils.deepCloneObject(getRegulatoryRules())), 
				((IndexedHashMap<String, RegulatoryVariable>)MTUCollectionsUtils.deepCloneObject(model.getVariablesInRegulatoryNetwork())), 
				((IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(model.getMapGeneId2RuleId())), 
				/*((IndexedHashMap<String, RegulatoryModelComponent>)MTUCollectionsUtils.deepCloneObject(model.getTypeofRegulatoryVariables())),*/
				(LinkedHashSet<String>) MTUCollectionsUtils.deepCloneObject(getUnconstrainedGenes()),
				model.genesInRuleLinkByRuleID());

	}

	@Override
	public String getModelID() {
		return model.getModelID();
	}

	@Override
	public String getRegulatorIdAtIndex(int index) {
		return model.getRegulatorIdAtIndex(index);
	}

	@Override
	public boolean genesInRuleLinkByRuleID() {
		return model.genesInRuleLinkByRuleID();
	}

	

	

}
