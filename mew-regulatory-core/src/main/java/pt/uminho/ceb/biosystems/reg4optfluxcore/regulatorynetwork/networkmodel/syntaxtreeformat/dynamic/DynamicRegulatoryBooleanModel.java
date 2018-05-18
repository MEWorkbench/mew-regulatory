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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.ornrocha.logutils.MTULogUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.parser.ParseException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.OptfluxRegulatoryModel;



public class DynamicRegulatoryBooleanModel implements IDynamicRegulatoryModel{
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IOptfluxRegulatoryModel originalmodel=null; 
	protected  HashSet<String> knownastfs=null;
	protected  HashSet<String> knownascondids=null;

	
	// dynamicElements
	 protected IndexedHashMap<String, RegulatoryRule> dynamicgeneRulesOfRegulatoryNetwork=null; // Rules that are changing during the process
	 protected IndexedHashMap<Integer, RegulatoryRule> dynamicIndexGeneRulesRegulatoryNetwork=null;
	 protected IndexedHashMap<String, Integer> indexofADDEDorCHANGEDRules=null;
	 protected IndexedHashMap<String, Regulator> newGenes=null;
	 protected IndexedHashMap<Integer, String> indexnewgenes=null;
	 protected IndexedHashMap<String, Integer> newgenesvsindex=null;
	 protected int totalNewRules=0;
	 protected IndexedHashMap<String, String> mapOfRegGenesToASTRuleID=null;
	 protected LinkedHashSet<String> newinputruleforgenethatexpresstf=null;
	 protected LinkedHashSet<String> changedTFid=null;
	 protected LinkedHashSet<String> newcondvars=null;
	 protected IndexedHashMap<String, RegulatoryVariable> newvariablesRegNetwork =null;
	 protected IndexedHashMap<Integer, String> mapIndexVsNameVars=null;
	 protected IndexedHashMap<String, Integer> mapNameVarsVsIndex=null;
	 protected int totalvariables=0;
	 
	 protected IndexedHashMap<String, RegulatoryVariable> tempaggregatedvariables=null;

	// gene rules related
	 protected LinkedHashSet<String> tfsinorigmodel=null;
	
	
	// gene related
	protected HashMap<String, String> geneid2genename=null;
	protected IndexedHashMap<String, String> geneid2ruleid=null;
	protected LinkedHashSet<String> newconstrainedgenes=null;
	protected ArrayList<String> possibleTFindentifiers;


	// variables related
	protected ArrayList<String> metabolicreactionidentifiers;
	protected IndexedHashMap<String, RegulatoryModelComponent> variabletype;


	protected boolean saveAPreviousState =true;
	protected boolean inputsetrules=false;
	protected DynamicRegulatoryModelSnapshotContainerSingleInputRule singleruleSnapshot =null;
	protected DynamicRegulatoryModelSnapshotContainerMultipleInputRules multipleruleSnapshot =null;
	
	private boolean debugerror =false;
	

	

	public DynamicRegulatoryBooleanModel(IOptfluxRegulatoryModel origmodel,ArrayList<String> metabolicreactionids,  HashMap<String, String> possiblegeneid2ruleid, HashMap<String, String> geneid2genename,IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables, boolean initemptyregulatorymodel){
		
		if(origmodel==null || initemptyregulatorymodel)
			this.originalmodel=OptfluxRegulatoryModel.getEmptyInstance();
		else 
			this.originalmodel=origmodel;
		
		this.variabletype=possibletypevariables;
		this.metabolicreactionidentifiers=metabolicreactionids;

		initializeDynamicRegulatoryModelParameters();
		checkTFsIdentifiersInOriginalModel();
		
		this.geneid2ruleid=new IndexedHashMap<>();
		if(originalmodel.getMapGeneId2RuleId()!=null && originalmodel.getMapGeneId2RuleId().size()>0)
			addgeneid2ruleid(originalmodel.getMapGeneId2RuleId());
		
		
		if(possiblegeneid2ruleid!=null && possiblegeneid2ruleid.size()>0)
			addgeneid2ruleid(possiblegeneid2ruleid);

		if(geneid2genename!=null)
		  this.geneid2genename=geneid2genename;
		
		
		 LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator("Initial Dynamic Regulatory Model Instance");
		 LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Known tfs: ",knownastfs);
		 LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Known Conditions: ",knownascondids);
		 LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Map Gene id to TF in Rule: ", geneid2ruleid);
		 LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Map Gene id to Gene Name: ",geneid2genename);
		 


	}
	
	
	
	private DynamicRegulatoryBooleanModel(IOptfluxRegulatoryModel origmodel,
			ArrayList<String> metabolicreactionids,
			HashMap<String, String> possiblegeneid2ruleid, 
			HashMap<String, String> geneid2genename,
			IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables){
		this(origmodel,metabolicreactionids,possiblegeneid2ruleid,geneid2genename,possibletypevariables,false);
	}
	
	
	
	
	private DynamicRegulatoryBooleanModel(IOptfluxRegulatoryModel origmodel,
			IndexedHashMap<String, RegulatoryRule> dynamicgeneRulesIntegratedRegNetwork,
			IndexedHashMap<Integer, RegulatoryRule> dynamicIndexmapgeneRulesIntegratedRegNetwork,
			IndexedHashMap<String, Integer> indexofnewASTIDsRules,
			IndexedHashMap<String, Regulator> dynamicgenesIntegratedRegNetwork,
			IndexedHashMap<Integer, String> indexnewgenes,
			IndexedHashMap<String, Integer> newgenesvsindex,
			LinkedHashSet<String> newinputtfs,
			IndexedHashMap<String, RegulatoryVariable> newvariablesRegNetwork,
			IndexedHashMap<Integer, String> mapIndexVsNameVars,
			IndexedHashMap<String, Integer> mapNameVarsVsIndex,
			LinkedHashSet<String> newcondvars,
			int indexrulesincrement,
			int indexvariablesincrement,
			IndexedHashMap<String, String> mapOfRegGenesToASTRuleID,
			LinkedHashSet<String> changedTFid,
			HashMap<String, String> geneid2genename,
			IndexedHashMap<String, String> geneid2ruleid,
			IndexedHashMap<String, RegulatoryModelComponent> variabletype,
			LinkedHashSet<String> unconstrained2constrainedgenes
			//boolean saveAPreviousState,
			//DynamicRegulatoryModelSnapshotContainerSingleInputRule RegModelSnapshot 
			){
		
		this.originalmodel=origmodel;
		checkTFsIdentifiersInOriginalModel();
		this.dynamicgeneRulesOfRegulatoryNetwork=dynamicgeneRulesIntegratedRegNetwork;
		this.dynamicIndexGeneRulesRegulatoryNetwork=dynamicIndexmapgeneRulesIntegratedRegNetwork;
		this.indexofADDEDorCHANGEDRules=indexofnewASTIDsRules;
		this.newGenes=dynamicgenesIntegratedRegNetwork;
		this.indexnewgenes=indexnewgenes;
		this.newgenesvsindex=newgenesvsindex;
		this.newinputruleforgenethatexpresstf=newinputtfs;
		this.newvariablesRegNetwork=newvariablesRegNetwork;
		this.mapIndexVsNameVars=mapIndexVsNameVars;
		this.mapNameVarsVsIndex=mapNameVarsVsIndex;
		this.newcondvars=newcondvars;
		this.totalNewRules=indexrulesincrement;
		this.totalvariables=indexvariablesincrement;
		//this.saveAPreviousState=saveAPreviousState;
		//this.singleruleSnapshot=RegModelSnapshot;
		this.mapOfRegGenesToASTRuleID=mapOfRegGenesToASTRuleID;
	    this.changedTFid=changedTFid;
        this.geneid2genename=geneid2genename;
		this.geneid2ruleid=geneid2ruleid;
		this.variabletype=variabletype;
		this.newconstrainedgenes=unconstrained2constrainedgenes;
	}
	
	protected void addgeneid2ruleid(Map<String,String> geneid2ruleid){
		
		if(possibleTFindentifiers==null)
			possibleTFindentifiers=new ArrayList<>();
		
		for (Map.Entry<String, String> geneentry : geneid2ruleid.entrySet()) {
			if(!this.geneid2ruleid.containsKey(geneentry.getKey()))
				this.geneid2ruleid.put(geneentry.getKey(), geneentry.getValue());
			
			if(!this.possibleTFindentifiers.contains(geneentry.getValue()))
				this.possibleTFindentifiers.add(geneentry.getValue());
		}
	}
	
	
	public void setSaveAPreviousState(boolean saveAPreviousState) {
		this.saveAPreviousState = saveAPreviousState;
	}

	
	private void checkTFsIdentifiersInOriginalModel(){
		this.tfsinorigmodel=new LinkedHashSet<>();
		
		if(originalmodel!=null){
			IndexedHashMap<String, RegulatoryRule> oldregrules = originalmodel.getRegulatoryRules();
		
		
			for (int i = 0; i < oldregrules.size(); i++) {
				String geneid=oldregrules.getKeyAt(i);
				String tfid = oldregrules.getValueAt(i).getRuleId();
				if(!geneid.equals(tfid))
					tfsinorigmodel.add(tfid);
			}
		}
		
	}



	private void addSingleRegulatoryRule(String geneid, String newrule) throws  Exception {

		geneid=geneid.trim();
		
		
	  	IndexedHashMap<String, RegulatoryRule> origrules = originalmodel.getRegulatoryRules();

	  	LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator("Processing Single Regulatory Rule");
	  	LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Adding rule to gene:", geneid, newrule);
	 
	  	
	  	if(saveAPreviousState){
	  		if(!inputsetrules && this.singleruleSnapshot==null){
	  			this.singleruleSnapshot=new DynamicRegulatoryModelSnapshotContainerSingleInputRule();
	  			this.multipleruleSnapshot=null;
	  		}
	  		else if(inputsetrules && multipleruleSnapshot==null){
	  			this.multipleruleSnapshot=new DynamicRegulatoryModelSnapshotContainerMultipleInputRules();
	  			this.singleruleSnapshot=null;
	  		}
	  	}
		  	 
	  	RegulatoryRule newregrule=null;
	  	boolean processnewrule=false;
	  	String regulatoryruleid=null;
	  	
	  	if(origrules.containsKey(geneid)){
	  		RegulatoryRule oldrule = origrules.get(geneid);
	  		String possibleid=checkPossibleRuleid(geneid,oldrule.getRuleId());
	  		regulatoryruleid=possibleid;
	  		//newregrule = new RegulatoryRule(possibleid, newrule);
	  		processnewrule=true;
	  		if(saveAPreviousState){
	  			if(inputsetrules)
	  				multipleruleSnapshot.addLastInputGeneid(geneid);
	  				//singleruleSnapshot.setNewgeneid(false);
	  			else
	  				singleruleSnapshot.setLastInputGeneid(geneid);
	  		}
	  		//MTULogUtils.addDebugMsgToClass(this.getClass(),  "Rule of Gene: {} changed to : {} --> {}",geneid,newregrule.getRuleId(),newregrule.getRule());
	  	} 
	  	else{
	  		regulatoryruleid=geneid;
	  		if(geneid2ruleid!=null && geneid2ruleid.containsKey(geneid))
	  			regulatoryruleid = geneid2ruleid.get(geneid);
	  	
	  		newinputruleforgenethatexpresstf.add(regulatoryruleid);
	  		addnewgene(geneid);
	  		processnewrule=true;

	  		//newregrule = new RegulatoryRule(ruleid, newrule);
	  		if(saveAPreviousState){
	  			if(inputsetrules)
	  				multipleruleSnapshot.addLastInputGeneid(geneid);
	  			else
	  				singleruleSnapshot.setLastInputGeneid(geneid);
		  		//singleruleSnapshot.setNewgeneid(true);
	  		}
	  		//MTULogUtils.addDebugMsgToClass(this.getClass(),"Added New Gene {} expressing {} With Rule {}",geneid,newregrule.getRuleId(),newregrule.getRule());
	  		
	  		
	  		
	  		
	  		/*else
	  			throw new Exception("Errors can occur because was not assigned any link of geneid to ruleid, please assign the corresponding link that geneid: "+geneid+" will have to rule: "+newrule);*/
	  	}
	  	
	  	
        if(processnewrule){
        	RuleProcessor processrule = new RuleProcessor(newrule,metabolicreactionidentifiers,possibleTFindentifiers, variabletype);
     
        	newregrule = new RegulatoryRule(regulatoryruleid, processrule.getRuleWithIdentifiersNormalized());
        	
        	this.dynamicgeneRulesOfRegulatoryNetwork.put(geneid, newregrule);
        	addIndexOfRegulatoryRule(geneid,newregrule);
	  		//filterNewRegulatoryVaribles(processrule.getRegulatoryVariables());
	  		cacheNewRegulatoryVariables(processrule.getRegulatoryVariables());
	  		if(newregrule.getBooleanRule()!=null)
	  			newconstrainedgenes.add(geneid);
	  		
	  		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Processed Rule: ", newregrule.getRule());
        }
	  	
	  	
  }
	
	  private void addnewgene(String geneid){
		  
		    Regulator newgene =null;
		    String genename=geneid;
	    	if(geneid2genename!=null && geneid2genename.containsKey(geneid)){
	    		genename = geneid2genename.get(geneid);	
	    	}
	    
	    	newgene = new Regulator(geneid, genename);
	    	newGenes.put(geneid, newgene);
	    }
	
	 
	  
	  /*
	  *  Try to assign a new name to the Rule identifier (equal to TF name), 
	  *  if and only if a map of the gene id versus the corresponding TF name is used as input parameter 
	  *  or if the input rule identifier differs from the previous one.   
	  */
	  
	private String checkPossibleRuleid(String geneid,String origruleid){
		if(geneid2ruleid.containsKey(geneid)){
			String mappedid = geneid2ruleid.get(geneid);
			if(!origruleid.equals(mappedid)){
				changedTFid.add(mappedid);
				return mappedid;
			}
			else
				return origruleid;
		}
		else
			return origruleid;
		
	}
	
	
	private void addIndexOfRegulatoryRule(String geneid, RegulatoryRule newrule){
		IndexedHashMap<String, RegulatoryRule> origrules = originalmodel.getRegulatoryRules();

	    if(origrules.containsKey(geneid)){
	    	int geneindex=origrules.getIndexOf(geneid);
	    	this.dynamicIndexGeneRulesRegulatoryNetwork.put(geneindex, newrule);
	    	this.indexofADDEDorCHANGEDRules.put(newrule.getRuleId(), geneindex);
	    	if(saveAPreviousState){
	    		if(inputsetrules){
	    			multipleruleSnapshot.addLastindexesgenerules(geneindex);
	    			multipleruleSnapshot.addLastInputRuleid(newrule.getRuleId());
	    		}
	    		else{
	    			singleruleSnapshot.setIndexmapgeneRule(geneindex);
	    			singleruleSnapshot.setLastInputRuleID(newrule.getRuleId());
	    		}
	    	}
	    	
	    	MTULogUtils.addDebugMsgToClass(this.getClass(), "Existing Rule gene index: {} --> {} ",geneid,origrules.getIndexOf(geneid));
	    }
	    else{
	    	int index=origrules.size()+newGenes.size()-1;
	    	this.dynamicIndexGeneRulesRegulatoryNetwork.put(index, newrule);
	    	this.indexofADDEDorCHANGEDRules.put(newrule.getRuleId(), index);
	    	this.indexnewgenes.put(index, geneid);
	    	this.newgenesvsindex.put(geneid, index);
	    	
	    	if(saveAPreviousState){
	    		if(inputsetrules){
	    			multipleruleSnapshot.addLastindexesgenerules(index);
	    			multipleruleSnapshot.addLastInputRuleid(newrule.getRuleId());
	    			multipleruleSnapshot.setLastNumberOfNewRules(totalNewRules);
	    		}
	    		else{
	    			singleruleSnapshot.setIndexmapgeneRule(index);
	    			singleruleSnapshot.setLastInputRuleID(newrule.getRuleId());
	    			singleruleSnapshot.setLastNumberOfNewRules(totalNewRules);
	    		}
	    	}
	    	totalNewRules ++;
	    	
	    }
	   
	}
	
	
	
	/**
	 * This function will reset old added rules and will test only a new regulatory rule
	 * @param geneid
	 * @param rule
	 * @throws Exception
	 */
	
	@Override
	public void setNewSingleRegulatoryRule(String geneid, String rule) throws Exception {
		initializeDynamicRegulatoryModelParameters();
		if(!rule.equals(IDynamicRegulatoryModel.IGNORERULETAG))
			addNewSingleRegulatoryRule(geneid, rule);
	}
	
	/**
	 * This function will append the new rule to current rules in regulatory model 
	 */
	@Override
	public void addNewSingleRegulatoryRule(String geneid, String rule) throws Exception {
		this.inputsetrules=false;
		this.singleruleSnapshot=null;
		addSingleRegulatoryRule(geneid, rule);
        
		filterNewRegulatoryVaribles(tempaggregatedvariables);
	}


	/**
	 * This function will reset old added rules and will test only a new group of regulatory rules
	 * 
	 */
	@Override
	public void setNewGroupOfRegulatoryRules(IndexedHashMap<String, String> regrules) throws Exception{
		initializeDynamicRegulatoryModelParameters();
		addNewGroupOfRegulatoryRules(regrules);
	}
	
	/**
	 * This function will append the new group of rules to current rules in regulatory model 
	 */
	@Override
	public void addNewGroupOfRegulatoryRules(IndexedHashMap<String, String> regrules) throws Exception{
        this.inputsetrules=true;
        this.multipleruleSnapshot=null;
		for (int i = 0; i < regrules.size(); i++) {
			String rule=regrules.getValueAt(i);
			if(!rule.equals(IDynamicRegulatoryModel.IGNORERULETAG)) {
				addSingleRegulatoryRule(regrules.getKeyAt(i), rule);
			}
		} 
		filterNewRegulatoryVaribles(tempaggregatedvariables);
		
		//MTUPrintUtils.printMap(tempaggregatedvariables);
		
		/*System.out.println("\n\n cached vars\n");
		MTUPrintUtils.printMap(tempaggregatedvariables);
		
		
		
		System.out.println("\n\nNew VARS\n");
		MTUPrintUtils.printMap(newvariablesRegNetwork);*/
		//System.out.println(tempaggregatedvariables);
		
	 }
	

	
	
	public void initializeDynamicRegulatoryModelParameters(){
		// genes
		this.dynamicgeneRulesOfRegulatoryNetwork= new IndexedHashMap<>();
		this.dynamicIndexGeneRulesRegulatoryNetwork=new IndexedHashMap<>();
		this.indexofADDEDorCHANGEDRules= new IndexedHashMap<>();
		this.newGenes=new IndexedHashMap<>();
		this.indexnewgenes= new IndexedHashMap<>();
		this.newgenesvsindex=new IndexedHashMap<>();
		this.totalNewRules=0;
		this.mapOfRegGenesToASTRuleID=null;
		this.newinputruleforgenethatexpresstf=new LinkedHashSet<>();
		this.changedTFid= new LinkedHashSet<>();
		this.newconstrainedgenes=new LinkedHashSet<>();
		this.tempaggregatedvariables=new IndexedHashMap<>();
		
		
		// variables
		this.newvariablesRegNetwork=new IndexedHashMap<>();
		this.mapIndexVsNameVars= new IndexedHashMap<>();
		this.mapNameVarsVsIndex= new IndexedHashMap<>();
		this.newcondvars = new LinkedHashSet<>();
		this.totalvariables=originalmodel.getVariablesInRegulatoryNetwork().size();
	}
	

	@Override
	public Integer getNumberOfRegulatoryRules() {
		return originalmodel.getNumberOfRegulatoryRules()+newGenes.size();
	}


	@Override
	public RegulatoryRule getRegulatoryRule(int ruleIndex) {
		if(this.dynamicIndexGeneRulesRegulatoryNetwork.containsKey(ruleIndex)){
			return dynamicIndexGeneRulesRegulatoryNetwork.get(ruleIndex);
		}
		else 
			return originalmodel.getRegulatoryRule(ruleIndex);
	}
	
	
	@Override
	public RegulatoryRule getRegulatoryRuleToRegulatorId(String geneid){
		if(dynamicgeneRulesOfRegulatoryNetwork.containsKey(geneid))
			return dynamicgeneRulesOfRegulatoryNetwork.get(geneid);
		else
			return originalmodel.getRegulatoryRuleToRegulatorId(geneid);
	}



	@Override
	public Integer getRuleIndexForIdentifier(String ruleId) {
		if(this.indexofADDEDorCHANGEDRules.containsKey(ruleId))
			return indexofADDEDorCHANGEDRules.get(ruleId);
		else
			return originalmodel.getRuleIndexForIdentifier(ruleId);
		
	}



	@Override
	public String getIdentifierOfRuleIndex(Integer ruleIndex) {
		if(dynamicIndexGeneRulesRegulatoryNetwork.containsKey(ruleIndex)){
			int pos = dynamicIndexGeneRulesRegulatoryNetwork.getIndexOf(ruleIndex);
			return dynamicgeneRulesOfRegulatoryNetwork.getKeyAt(pos);		
		}
		else
			return originalmodel.getIdentifierOfRuleIndex(ruleIndex);
	}






	@Override
	public String getRuleIdAtIndex(Integer ruleIndex) {
		if(dynamicIndexGeneRulesRegulatoryNetwork.containsKey(ruleIndex)){
			int pos = dynamicIndexGeneRulesRegulatoryNetwork.getIndexOf(ruleIndex);
			return dynamicgeneRulesOfRegulatoryNetwork.getValueAt(pos).getRuleId();	
		}
		else
			return originalmodel.getRuleIdAtIndex(ruleIndex);
	}





	@Override
	public Integer getRegulatoryGeneIndex(String geneId) {
		if(dynamicgeneRulesOfRegulatoryNetwork.containsKey(geneId)){
			int pos = dynamicgeneRulesOfRegulatoryNetwork.getIndexOf(geneId);
			return dynamicIndexGeneRulesRegulatoryNetwork.getKeyAt(pos);
		}
		else
			return originalmodel.getRegulatoryGeneIndex(geneId);
	}


	@Override
	public RegulatoryVariable getRegulatoryVariable(String id) {
		if(newvariablesRegNetwork.containsKey(id))
			return newvariablesRegNetwork.get(id);
		else
			return originalmodel.getRegulatoryVariable(id);
	}



	@Override
	public IndexedHashMap<String, RegulatoryRule> getRegulatoryRules() {
		IndexedHashMap<String, RegulatoryRule> origrules = originalmodel.getRegulatoryRules();
		IndexedHashMap<String, RegulatoryRule> newgenerules = new IndexedHashMap<>(origrules.size()+newGenes.size());
		
		for (int i = 0; i < origrules.size(); i++) {
			String geneid = origrules.getKeyAt(i);
			 if(dynamicgeneRulesOfRegulatoryNetwork.containsKey(geneid))
				  newgenerules.put(geneid, dynamicgeneRulesOfRegulatoryNetwork.get(geneid));
			 else
				 newgenerules.put(geneid, origrules.get(geneid));
		  }
		
		for (int i = 0; i < newGenes.size(); i++) {
			String gid=newGenes.getKeyAt(i);
			newgenerules.put(gid, dynamicgeneRulesOfRegulatoryNetwork.get(gid));
		}
	
		return newgenerules;
	}






	@Override
	public IndexedHashMap<String, String> getMapGeneId2RuleId() {

		
		if(this.mapOfRegGenesToASTRuleID!=null){
			return this.mapOfRegGenesToASTRuleID;
		}
		
		else{
		IndexedHashMap<String, RegulatoryRule> origrules = originalmodel.getRegulatoryRules();
		this.mapOfRegGenesToASTRuleID = new IndexedHashMap<>(origrules.size()+newGenes.size());
		
		for (int i = 0; i < origrules.size(); i++) {
			String geneid = origrules.getKeyAt(i);
			if(dynamicgeneRulesOfRegulatoryNetwork.containsKey(geneid)){
				String ruleid=dynamicgeneRulesOfRegulatoryNetwork.get(geneid).getRuleId();
				mapOfRegGenesToASTRuleID.put(geneid, ruleid);	
			}
			else{
				String ruleid=origrules.get(geneid).getRuleId();
				mapOfRegGenesToASTRuleID.put(geneid, ruleid);
			}
		}
		
		for (int i = 0; i < newGenes.size(); i++) {
			String gid = newGenes.getKeyAt(i);
			String ruleid = dynamicgeneRulesOfRegulatoryNetwork.get(gid).getRuleId();
			mapOfRegGenesToASTRuleID.put(gid, ruleid);
		}

		
		return mapOfRegGenesToASTRuleID;
	  }
	}


	



	@Override
	public void setRegulatoryRules(IndexedHashMap<String, RegulatoryRule> newrules) {
		try {
		for (int i = 0; i < newrules.size(); i++) {
			
				addSingleRegulatoryRule(newrules.getKeyAt(i), newrules.getValueAt(i).getRule());
			
		}
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

   
	@Override
	public IndexedHashMap<String, RegulatoryVariable> getOnlyNewRegulatoryVariablesOfNetwork(){
		return this.newvariablesRegNetwork;
	}
	
	@Override
	public IndexedHashMap<String, Integer> getIndexesNewRegulatoryVariablesOfNetwork(){
		return this.mapNameVarsVsIndex;
	}



	@Override
	public IndexedHashMap<String, RegulatoryVariable> getVariablesInRegulatoryNetwork() {
		IndexedHashMap<String, RegulatoryVariable> oldvarset = originalmodel.getVariablesInRegulatoryNetwork();
		IndexedHashMap<String, RegulatoryVariable> newvarset = new IndexedHashMap<>();
		
		for (int i = 0; i < oldvarset.size(); i++) {
			newvarset.put(oldvarset.getKeyAt(i), oldvarset.getValueAt(i));
		}
		
		for (String var : newcondvars) {
			newvarset.put(var, newvariablesRegNetwork.get(var));
		}
		
		return newvarset;
	}






	@Override
	public Integer getNumberOfVariables() {
		return originalmodel.getVariablesInRegulatoryNetwork().size()+newcondvars.size();
	}






	@Override
	public Integer getVariableIndex(String variableName){
		int index = -1;
		if(mapNameVarsVsIndex.containsKey(variableName))
			index= mapNameVarsVsIndex.get(variableName);
		else
			index= originalmodel.getVariableIndex(variableName);

		return index;
	}






	@Override
	public RegulatoryVariable getVariableByIndex(int variableIDX) {
		if(mapIndexVsNameVars.containsKey(variableIDX))
			return newvariablesRegNetwork.get(mapIndexVsNameVars.get(variableIDX));
		else
			return originalmodel.getVariableByIndex(variableIDX);
	}






	@Override
	public RegulatoryModelComponent getTypeOfVariable(String var) {
		if(this.newvariablesRegNetwork.containsKey(var))
			return newvariablesRegNetwork.get(var).getType();
		else{
			return originalmodel.getTypeOfVariable(var);
		}
		
	}



	@Override
	public IndexedHashMap<String, RegulatoryModelComponent> getTypeofRegulatoryVariables() {
		IndexedHashMap<String, RegulatoryModelComponent> allvartype = new IndexedHashMap<>();
		IndexedHashMap<String, RegulatoryModelComponent> originalmodeltypevars=originalmodel.getTypeofRegulatoryVariables();
		if(originalmodeltypevars.size()>0)
			for (int i = 0; i < originalmodeltypevars.size(); i++) {
				allvartype.put(originalmodeltypevars.getKeyAt(i), originalmodeltypevars.getValueAt(i));
			}
		for (String varid : newcondvars) {
			allvartype.put(varid, newvariablesRegNetwork.get(varid).getType());
		}
		
		return allvartype;
	}






	@Override
	public Integer getNumberOfRegulators() {
		return originalmodel.getNumberOfRegulators()+newGenes.size();
	}






	@Override
	public Gene getGene(int geneIndex) {
		if(indexnewgenes.containsKey(geneIndex)){
			String geneid=indexnewgenes.get(geneIndex);
			return newGenes.get(geneid);
		}
		else{
			return originalmodel.getGene(geneIndex);
		}
	}






	@Override
	public Gene getGene(String geneId) {
		if(newGenes.containsKey(geneId))
			return newGenes.get(geneId);
		else
			return originalmodel.getGene(geneId);
	}






	@Override
	public Integer getRegulatorIndex(String geneId) {
		if(newgenesvsindex.containsKey(geneId))
			return newgenesvsindex.get(geneId);
		else
			return originalmodel.getRegulatorIndex(geneId);
	}






	@Override
	public IndexedHashMap<String, Regulator> getRegulators() {
		IndexedHashMap<String, Regulator> newgeneset = new IndexedHashMap<>();
		IndexedHashMap<String, Regulator> oldgeneset= originalmodel.getRegulators();
		for (int i = 0; i < oldgeneset.size(); i++) {
			newgeneset.put(oldgeneset.getKeyAt(i), oldgeneset.getValueAt(i));
		}
		
		for (int i = 0; i < newGenes.size(); i++) {
			newgeneset.put(newGenes.getKeyAt(i), newGenes.getValueAt(i));
		}
		
		return newgeneset;
	}






	@Override
	public String getRegulatorIDAssociatedToRuleID(String ruleid) {
		IndexedHashMap<String, String> mapRegGenesToASTRuleID = getMapGeneId2RuleId();
        for (Map.Entry<String, String> map : mapRegGenesToASTRuleID.entrySet()) {
    		
    		if(map.getValue().equals(ruleid))
			 return map.getKey();
		}
    	return null;
	}






	@Override
	public ArrayList<String> getRegulatorIDs() {
		ArrayList<String> res = new ArrayList<>();
		ArrayList<String> old = originalmodel.getRegulatorIDs();
		
		res.addAll(old);
		
		for (int i = 0; i < newGenes.size(); i++) {
			res.add(newGenes.getKeyAt(i));
		}

		return res;
	}





	@Override
	public IRegulatoryNetwork copy() throws Exception {
		

		IOptfluxRegulatoryModel originalmodelclone =null;
		IndexedHashMap<String, RegulatoryRule> clonedynamicgeneRulesIntegratedRegNetwork = new IndexedHashMap<>();
		IndexedHashMap<Integer, RegulatoryRule> clonedynamicIndexmapgeneRulesIntegratedRegNetwork = new IndexedHashMap<>();
		IndexedHashMap<String, Integer> cloneindexofnewASTIDsRules = new IndexedHashMap<>();
		IndexedHashMap<String, Regulator> clonedynamicgenesIntegratedRegNetwork = new IndexedHashMap<>();
		IndexedHashMap<Integer, String> cloneindexnewgenes = new IndexedHashMap<>();
		IndexedHashMap<String, Integer> clonenewgenesvsindex = new IndexedHashMap<>();
		LinkedHashSet<String> clonenewinputtfs = new LinkedHashSet<>();
		IndexedHashMap<String, RegulatoryVariable> clonenewvariablesRegNetwork = new IndexedHashMap<>();
		IndexedHashMap<Integer, String> clonemapIndexVsNameVars = new IndexedHashMap<>();
		IndexedHashMap<String, Integer> clonemapNameVarsVsIndex = new IndexedHashMap<>();
		LinkedHashSet<String> clonenewcondvars = new LinkedHashSet<>();
		
		
		
		try {
		for (int i = 0; i < dynamicgeneRulesOfRegulatoryNetwork.size(); i++) {
			
				clonedynamicgeneRulesIntegratedRegNetwork.put(dynamicgeneRulesOfRegulatoryNetwork.getKeyAt(i), dynamicgeneRulesOfRegulatoryNetwork.getValueAt(i).copy());
			
		}
	
		
		for (int i = 0; i < dynamicIndexGeneRulesRegulatoryNetwork.size(); i++) {
				clonedynamicIndexmapgeneRulesIntegratedRegNetwork.put(dynamicIndexGeneRulesRegulatoryNetwork.getKeyAt(i), dynamicIndexGeneRulesRegulatoryNetwork.getValueAt(i).copy());
		}
		
		} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		for (int i = 0; i < indexofADDEDorCHANGEDRules.size(); i++) {
				cloneindexofnewASTIDsRules.put(indexofADDEDorCHANGEDRules.getKeyAt(i), indexofADDEDorCHANGEDRules.getValueAt(i));
		}


	
		for (int i = 0; i < newGenes.size(); i++) {
			Regulator oldgene = newGenes.getValueAt(i);
			clonedynamicgenesIntegratedRegNetwork.put(newGenes.getKeyAt(i), (Regulator) oldgene.copy());
		}

		
		
		
		for (int i = 0; i < indexnewgenes.size(); i++) {
			cloneindexnewgenes.put(indexnewgenes.getKeyAt(i), indexnewgenes.getValueAt(i));
		  }

		

		for (int i = 0; i < newgenesvsindex.size(); i++) {
	
			clonenewgenesvsindex.put(newgenesvsindex.getKeyAt(i), newgenesvsindex.getValueAt(i));
		}

		
		 
	     for (String tf : newinputruleforgenethatexpresstf) {
	    	clonenewinputtfs.add(tf);
			}

		
		
	     for (int i = 0; i < newvariablesRegNetwork.size(); i++) {
				clonenewvariablesRegNetwork.put(newvariablesRegNetwork.getKeyAt(i), newvariablesRegNetwork.getValueAt(i).copy());
		    }
	
		    
		 for (int i = 0; i < mapIndexVsNameVars.size(); i++) {
			    clonemapIndexVsNameVars.put(mapIndexVsNameVars.getKeyAt(i), mapIndexVsNameVars.getValueAt(i));
			    clonemapNameVarsVsIndex.put(mapNameVarsVsIndex.getKeyAt(i), mapNameVarsVsIndex.getValueAt(i));
			}		
	
		 
		 
		for (String var : newcondvars) {
			clonenewcondvars.add(var);
		}
		
		originalmodelclone=(IOptfluxRegulatoryModel) originalmodel.copy();
		
		/*DynamicRegulatoryModelSnapshotContainerSingleInputRule SnapshotContainercopy = null;
		if(this.singleruleSnapshot!=null)
			SnapshotContainercopy= this.singleruleSnapshot.copy();*/
		
		IndexedHashMap<String, String> clonemapOfRegGenesToASTRuleID=(IndexedHashMap<String, String>) MTUCollectionsUtils.deepCloneObject(mapOfRegGenesToASTRuleID);
		LinkedHashSet<String> clonechangedTFid=(LinkedHashSet<String>) MTUCollectionsUtils.deepCloneObject(changedTFid);
		HashMap<String, String> clonegeneid2genename=(HashMap<String, String>) MTUCollectionsUtils.deepCloneObject(geneid2genename);
		IndexedHashMap<String, String> clonegeneid2ruleid=(IndexedHashMap<String, String>) MTUCollectionsUtils.deepCloneObject(geneid2ruleid);
		IndexedHashMap<String, RegulatoryModelComponent> clonevariabletype=(IndexedHashMap<String, RegulatoryModelComponent>) MTUCollectionsUtils.deepCloneObject(variabletype);
		LinkedHashSet<String> cloneunconstrained2constrainedgenes=(LinkedHashSet<String>) MTUCollectionsUtils.deepCloneObject(newconstrainedgenes);
		
       return new DynamicRegulatoryBooleanModel(originalmodelclone, 
    		   clonedynamicgeneRulesIntegratedRegNetwork, 
    		   clonedynamicIndexmapgeneRulesIntegratedRegNetwork, 
    		   cloneindexofnewASTIDsRules, 
    		   clonedynamicgenesIntegratedRegNetwork, 
    		   cloneindexnewgenes, 
    		   clonenewgenesvsindex, 
    		   clonenewinputtfs, 
    		   clonenewvariablesRegNetwork, 
    		   clonemapIndexVsNameVars, 
    		   clonemapNameVarsVsIndex, 
    		   clonenewcondvars, 
    		   this.totalNewRules, 
    		   this.totalvariables, 
    		   //this.saveAPreviousState, 
    		   //SnapshotContainercopy
    		   clonemapOfRegGenesToASTRuleID,
    		   clonechangedTFid,
    		   clonegeneid2genename,
    		   clonegeneid2ruleid,
    		   clonevariabletype,
    		   cloneunconstrained2constrainedgenes);
		 
	}


	private void cacheNewRegulatoryVariables(IndexedHashMap<String,RegulatoryVariable> newvars) {
		
		IndexedHashMap<String, RegulatoryVariable> existingVariables = originalmodel.getVariablesInRegulatoryNetwork(); 
		
		for (int i = 0; i < newvars.size(); i++) {
			String varname = newvars.getKeyAt(i);
			if(!existingVariables.containsKey(varname) && !tempaggregatedvariables.containsKey(varname))
				tempaggregatedvariables.put(varname, newvars.get(varname));
			
		}
		
		
	}
	
	
	private void filterNewRegulatoryVaribles(IndexedHashMap<String,RegulatoryVariable> newvars){
		//IndexedHashMap<String, RegulatoryVariable> existingVariables = originalmodel.getVariablesInRegulatoryNetwork(); 
		if(saveAPreviousState){
			if(inputsetrules){
				multipleruleSnapshot.setTotalVariables(totalvariables);
			}
			else{
				singleruleSnapshot.setTotalVariables(totalvariables);
				singleruleSnapshot.resetPreviousListofVariables();
			}
		}

		for (int i = 0; i < newvars.size(); i++) {	
			String varname = newvars.getKeyAt(i);
			
			if(!tfsinorigmodel.contains(varname) && !newinputruleforgenethatexpresstf.contains(varname) && !changedTFid.contains(varname) && !newvariablesRegNetwork.containsKey(varname)) {
				newvariablesRegNetwork.put(varname, newvars.getValueAt(i));
				mapIndexVsNameVars.put(totalvariables, varname);
				mapNameVarsVsIndex.put(varname, totalvariables);
				newcondvars.add(varname);
				if(saveAPreviousState){
					if(inputsetrules)
						multipleruleSnapshot.addVariabletoList(varname);
					else
						singleruleSnapshot.addVariabletoList(varname); 
				}
				totalvariables ++;
				LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Added New Variable: ", varname); 
			}
			
		}
	}

	
 /*   private void filterNewRegulatoryVaribles(IndexedHashMap<String,RegulatoryVariable> newvars){
    	  IndexedHashMap<String, RegulatoryVariable> existingVariables = originalmodel.getVariablesInRegulatoryNetwork(); 
    	  if(saveAPreviousState){
    		  if(inputsetrules){
    			  multipleruleSnapshot.setTotalVariables(totalvariables);
    		  }
    		  else{
    			  singleruleSnapshot.setTotalVariables(totalvariables);
    			  singleruleSnapshot.resetPreviousListofVariables();
    		  }
    	  }

    	  for (int i = 0; i < newvars.size(); i++) {

    		  if(!existingVariables.containsKey(newvars.getKeyAt(i))){
				if(!newcondvars.contains(newvars.getKeyAt(i))){
					String varname = newvars.getKeyAt(i);
					if(!tfsinorigmodel.contains(varname) && !newinputruleforgenethatexpresstf.contains(varname) && !changedTFid.contains(varname)){
					     newvariablesRegNetwork.put(varname, newvars.getValueAt(i));
					     mapIndexVsNameVars.put(totalvariables, varname);
					     mapNameVarsVsIndex.put(varname, totalvariables);
					     newcondvars.add(varname);
					     if(saveAPreviousState){
					    	 if(inputsetrules)
					    		 multipleruleSnapshot.addVariabletoList(varname);
					    	 else
					    		 singleruleSnapshot.addVariabletoList(varname); 
					     }
					     totalvariables ++;
					   LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Added New Variable: ", varname);  
					  // MTULogUtils.addDebugMsgToClass(this.getClass(),"Added New Variable: {}, that is a {}", varname,newvars.getValueAt(i).getType());  
				   }
				}
			 }   
		  }
    	 
    }*/
    
  
    public void setRegModelToPreviousInternalState(){
    	
    		IndexedHashMap<String, RegulatoryRule> previousdynamicgeneRulesIntegratedRegNetwork = new IndexedHashMap<>();
    		IndexedHashMap<Integer, RegulatoryRule> previousdynamicIndexmapgeneRulesIntegratedRegNetwork = new IndexedHashMap<>();
    		IndexedHashMap<String, Integer> previousindexofnewASTIDsRules = new IndexedHashMap<>();
    		IndexedHashMap<String, Regulator> previousdynamicgenesIntegratedRegNetwork = new IndexedHashMap<>();
    		IndexedHashMap<Integer, String> previousindexnewgenes = new IndexedHashMap<>();
    		IndexedHashMap<String, Integer> previousnewgenesvsindex = new IndexedHashMap<>();
    		//IndexedHashMap<String, String> previousmapOfRegGenesToASTRuleID = new IndexedHashMap<>();
    		LinkedHashSet<String> previousnewinputtfs = new LinkedHashSet<>();
    		IndexedHashMap<String, RegulatoryVariable> previousnewvariablesRegNetwork = new IndexedHashMap<>();
    		IndexedHashMap<Integer, String> previousmapIndexVsNameVars = new IndexedHashMap<>();
    		IndexedHashMap<String, Integer> previousmapNameVarsVsIndex = new IndexedHashMap<>();
    		LinkedHashSet<String> previousnewcondvars = new LinkedHashSet<>();
    	
    	if(!inputsetrules && singleruleSnapshot!=null){
    		
    		String lastinputgeneid = this.singleruleSnapshot.getLastinputgeneid();
    		String lastASTIDsRule = this.singleruleSnapshot.getLastASTIDsRules();
    		int lastIndexmapgeneRule = this.singleruleSnapshot.getLastIndexmapgeneRule();
    		LinkedHashSet<String> listofinputVariables = this.singleruleSnapshot.getListofinputVariables();

    		this.totalNewRules= singleruleSnapshot.getLastTotalNewRules();
    		this.totalvariables=singleruleSnapshot.getLastTotalNewVariables();
    	
    		
    		for (int i = 0; i < dynamicgeneRulesOfRegulatoryNetwork.size(); i++) {
				if(!dynamicgeneRulesOfRegulatoryNetwork.getKeyAt(i).equals(lastinputgeneid))
					previousdynamicgeneRulesIntegratedRegNetwork.put(dynamicgeneRulesOfRegulatoryNetwork.getKeyAt(i), dynamicgeneRulesOfRegulatoryNetwork.getValueAt(i));
			}
    		this.dynamicgeneRulesOfRegulatoryNetwork=previousdynamicgeneRulesIntegratedRegNetwork;
    		

    		
    		for (int i = 0; i < dynamicIndexGeneRulesRegulatoryNetwork.size(); i++) {
				if(!dynamicIndexGeneRulesRegulatoryNetwork.getKeyAt(i).equals(lastIndexmapgeneRule)){
					previousdynamicIndexmapgeneRulesIntegratedRegNetwork.put(dynamicIndexGeneRulesRegulatoryNetwork.getKeyAt(i), dynamicIndexGeneRulesRegulatoryNetwork.getValueAt(i));
				}
			 }
    		
    		this.dynamicIndexGeneRulesRegulatoryNetwork=previousdynamicIndexmapgeneRulesIntegratedRegNetwork;

    		
    		
    		for (int i = 0; i < indexofADDEDorCHANGEDRules.size(); i++) {
    			if(!indexofADDEDorCHANGEDRules.getKeyAt(i).equals(lastASTIDsRule))
				    previousindexofnewASTIDsRules.put(indexofADDEDorCHANGEDRules.getKeyAt(i), indexofADDEDorCHANGEDRules.getValueAt(i));
			}
    		this.indexofADDEDorCHANGEDRules= previousindexofnewASTIDsRules;

    		
    		
    		if(newGenes.containsKey(lastinputgeneid)){
    			for (int i = 0; i < newGenes.size(); i++) {
					if(!newGenes.getKeyAt(i).equals(lastinputgeneid))
						previousdynamicgenesIntegratedRegNetwork.put(newGenes.getKeyAt(i), newGenes.getValueAt(i));
				}
    			this.newGenes=previousdynamicgenesIntegratedRegNetwork;
    		}

    		
    		
    		
    		if(indexnewgenes.containsKey(lastIndexmapgeneRule)){
    			for (int i = 0; i < indexnewgenes.size(); i++) {
					if(!indexnewgenes.getKeyAt(i).equals(lastIndexmapgeneRule))
						previousindexnewgenes.put(indexnewgenes.getKeyAt(i), indexnewgenes.getValueAt(i));
				}
    			this.indexnewgenes=previousindexnewgenes;
    		}

    		
    		
    		if(newgenesvsindex.containsKey(lastinputgeneid)){
    			for (int i = 0; i < newgenesvsindex.size(); i++) {
					if(!newgenesvsindex.getKeyAt(i).equals(lastinputgeneid))
						previousnewgenesvsindex.put(newgenesvsindex.getKeyAt(i), newgenesvsindex.getValueAt(i));
				}
    			this.newgenesvsindex=previousnewgenesvsindex;
    		}

    		

    		
    		
    		
    		if(this.newinputruleforgenethatexpresstf.contains(lastASTIDsRule)){
    			for (String tf : newinputruleforgenethatexpresstf) {
					if(!tf.equals(lastASTIDsRule))
						previousnewinputtfs.add(tf);
				}
    			this.newinputruleforgenethatexpresstf=previousnewinputtfs;	
    		}

    		
    		
    		
    		
    	     for (int i = 0; i < newvariablesRegNetwork.size(); i++) {
    	    	 String varname = newvariablesRegNetwork.getKeyAt(i);
				if(!listofinputVariables.contains(varname))
					previousnewvariablesRegNetwork.put(newvariablesRegNetwork.getKeyAt(i), newvariablesRegNetwork.getValueAt(i));
			    }
    		    this.newvariablesRegNetwork=previousnewvariablesRegNetwork;
    		
    		    
    		    
    		 for (int i = 0; i < mapIndexVsNameVars.size(); i++) {
				if(!listofinputVariables.contains(mapIndexVsNameVars.getValueAt(i))){
					previousmapIndexVsNameVars.put(mapIndexVsNameVars.getKeyAt(i), mapIndexVsNameVars.getValueAt(i));
					previousmapNameVarsVsIndex.put(mapNameVarsVsIndex.getKeyAt(i), mapNameVarsVsIndex.getValueAt(i));
				}		
			} 
    		 
    		 this.mapIndexVsNameVars=previousmapIndexVsNameVars;
    		 this.mapNameVarsVsIndex=previousmapNameVarsVsIndex;  
    		 
    		 
    		for (String var : newcondvars) {
				if(!listofinputVariables.contains(var))
					previousnewcondvars.add(var);
			}
    		this.newcondvars=previousnewcondvars;
    		
    	
    	}
    	else if(inputsetrules && multipleruleSnapshot!=null){
    		
    		
    		HashSet<String> lastinputgenes=multipleruleSnapshot.getLastinputgenes();
    		
    		
    		for (int i = 0; i < dynamicgeneRulesOfRegulatoryNetwork.size(); i++) {
				if(!lastinputgenes.contains(dynamicgeneRulesOfRegulatoryNetwork.getKeyAt(i)))
					previousdynamicgeneRulesIntegratedRegNetwork.put(dynamicgeneRulesOfRegulatoryNetwork.getKeyAt(i), dynamicgeneRulesOfRegulatoryNetwork.getValueAt(i));
			}
    		this.dynamicgeneRulesOfRegulatoryNetwork=previousdynamicgeneRulesIntegratedRegNetwork;
    		
    		
    		for (int i = 0; i < dynamicIndexGeneRulesRegulatoryNetwork.size(); i++) {
				if(!multipleruleSnapshot.getLastindexesgenerules().contains(dynamicIndexGeneRulesRegulatoryNetwork.getKeyAt(i))){
					previousdynamicIndexmapgeneRulesIntegratedRegNetwork.put(dynamicIndexGeneRulesRegulatoryNetwork.getKeyAt(i), dynamicIndexGeneRulesRegulatoryNetwork.getValueAt(i));
				}
			 }
    		this.dynamicIndexGeneRulesRegulatoryNetwork=previousdynamicIndexmapgeneRulesIntegratedRegNetwork;
    		
    		
    		for (int i = 0; i < indexofADDEDorCHANGEDRules.size(); i++) {
    			if(!multipleruleSnapshot.getLastInputRuleIds().contains(indexofADDEDorCHANGEDRules.getKeyAt(i)))
				    previousindexofnewASTIDsRules.put(indexofADDEDorCHANGEDRules.getKeyAt(i), indexofADDEDorCHANGEDRules.getValueAt(i));
			}
    		this.indexofADDEDorCHANGEDRules= previousindexofnewASTIDsRules;

    		

    		for (int i = 0; i < newGenes.size(); i++) {
				if(!multipleruleSnapshot.getLastinputgenes().contains(newGenes.getKeyAt(i)))
					previousdynamicgenesIntegratedRegNetwork.put(newGenes.getKeyAt(i), newGenes.getValueAt(i));
			}
    		this.newGenes=previousdynamicgenesIntegratedRegNetwork;
    	
    		

    	    for (int i = 0; i < indexnewgenes.size(); i++) {
				if(!multipleruleSnapshot.getLastindexesgenerules().contains(indexnewgenes.getKeyAt(i)))
					previousindexnewgenes.put(indexnewgenes.getKeyAt(i), indexnewgenes.getValueAt(i));
			}
    		this.indexnewgenes=previousindexnewgenes;

 
    		for (int i = 0; i < newgenesvsindex.size(); i++) {
				if(!multipleruleSnapshot.getLastinputgenes().contains(newgenesvsindex.getKeyAt(i)))
					previousnewgenesvsindex.put(newgenesvsindex.getKeyAt(i), newgenesvsindex.getValueAt(i));
			}
    		this.newgenesvsindex=previousnewgenesvsindex;

   
    		for (String tf : newinputruleforgenethatexpresstf) {
				if(!multipleruleSnapshot.getLastInputRuleIds().contains(tf))
					previousnewinputtfs.add(tf);
			}
    		this.newinputruleforgenethatexpresstf=previousnewinputtfs;	

    		
    		 for (int i = 0; i < newvariablesRegNetwork.size(); i++) {
    	    	 String varname = newvariablesRegNetwork.getKeyAt(i);
				if(!multipleruleSnapshot.getListofinputVariables().contains(varname))
					previousnewvariablesRegNetwork.put(newvariablesRegNetwork.getKeyAt(i), newvariablesRegNetwork.getValueAt(i));
			    }
    		 this.newvariablesRegNetwork=previousnewvariablesRegNetwork;
    		
    		
    		 for (int i = 0; i < mapIndexVsNameVars.size(); i++) {
 				if(!multipleruleSnapshot.getListofinputVariables().contains(mapIndexVsNameVars.getValueAt(i))){
 					previousmapIndexVsNameVars.put(mapIndexVsNameVars.getKeyAt(i), mapIndexVsNameVars.getValueAt(i));
 					previousmapNameVarsVsIndex.put(mapNameVarsVsIndex.getKeyAt(i), mapNameVarsVsIndex.getValueAt(i));
 				}		
 			  } 
     		 
     		 this.mapIndexVsNameVars=previousmapIndexVsNameVars;
     		 this.mapNameVarsVsIndex=previousmapNameVarsVsIndex; 
     		 
     		for (String var : newcondvars) {
				if(!multipleruleSnapshot.getListofinputVariables().contains(var))
					previousnewcondvars.add(var);
			}
    		this.newcondvars=previousnewcondvars;
    		
    		this.totalNewRules= multipleruleSnapshot.getLastTotalNewRules();
    		this.totalvariables=multipleruleSnapshot.getLastTotalNewVariables();
    	}
    	
    	newconstrainedgenes=new LinkedHashSet<>();
    }


	
	public void setdebugerrorfalse(){
		this.debugerror=false;
	}





	@SuppressWarnings("unchecked")
	@Override
	public IDynamicRegulatoryModel getNewInstance() throws Exception {
		IndexedHashMap<String, RegulatoryModelComponent> typevariables=null;
		if(this.variabletype!=null)
			typevariables=(IndexedHashMap<String, RegulatoryModelComponent>)MTUCollectionsUtils.deepCloneObject(this.variabletype);
		
		HashMap<String, String> possiblegeneid2ruleid=null;
		if(this.geneid2ruleid!=null){
			IndexedHashMap<String, String> geneid2ruleid=(IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(this.geneid2ruleid);
			possiblegeneid2ruleid=new HashMap<>(geneid2ruleid);
		}
		
		HashMap<String, String> geneid2genename=null;
		if(this.geneid2genename!=null)
			geneid2genename=(HashMap<String, String>)MTUCollectionsUtils.deepCloneObject(this.geneid2genename);
		
		ArrayList<String> metabreactids=null;
		if(this.metabolicreactionidentifiers!=null)
			metabreactids=(ArrayList<String>) MTUCollectionsUtils.deepCloneObject(this.metabolicreactionidentifiers);
		
		return new DynamicRegulatoryBooleanModel((IOptfluxRegulatoryModel) originalmodel.copy(), 
				metabreactids, 
				possiblegeneid2ruleid, 
				geneid2genename,
				typevariables);
	}



	@Override
	public String getModelID() {
		return originalmodel.getModelID();
	}


	@Override
	public String getRegulatorIdAtIndex(int index) {
		if(indexnewgenes.containsKey(index))
			return indexnewgenes.get(index);
		else
			return originalmodel.getRegulatorIdAtIndex(index);
	}


	@Override
	public boolean isRegulatorOnlyAtRegulatoryNetwork(String geneid) {
		if(newGenes.containsKey(geneid))
			return true;
		else
			return originalmodel.isRegulatorOnlyAtRegulatoryNetwork(geneid);
	}


	@Override
	public String getRuleIDAssociatedToRegulatorID(String geneid) {
		IndexedHashMap<String, String> geneid2ruleid=getMapGeneId2RuleId();
		if(geneid2ruleid.containsKey(geneid))
			return geneid2ruleid.get(geneid);
		return null;
	}


	@Override
	public LinkedHashSet<String> getUnconstrainedGenes() {
		
		if(newconstrainedgenes.size()>0 && originalmodel.getUnconstrainedGenes()!=null) {
			LinkedHashSet<String> uncgenes=new LinkedHashSet<>();
			for (String id : originalmodel.getUnconstrainedGenes()) {
				if(!newconstrainedgenes.contains(id))
					uncgenes.add(id);
			}
			return uncgenes;
		}
		else 
			return originalmodel.getUnconstrainedGenes();

	}


	@Override
	public ArrayList<String> getVariableNamesInNetwork() {
		ArrayList<String> vars = new ArrayList<String>();
		ArrayList<String> origmodelvars=originalmodel.getVariableNamesInNetwork();
		if(origmodelvars!=null)
			vars.addAll(origmodelvars);
		
		for (int i = 0; i <newvariablesRegNetwork.size(); i++) {
			String varid=newvariablesRegNetwork.getKeyAt(i);
			vars.add(newvariablesRegNetwork.get(varid).getId());
		}
		
		return vars;
	}


	@Override
	public void changeVariableType(String varid, RegulatoryModelComponent type) {
		if(newvariablesRegNetwork.containsKey(varid))
			newvariablesRegNetwork.get(varid).setComponentType(type);
		else 
			originalmodel.changeVariableType(varid, type);
	}


	@Override
	public boolean genesInRuleLinkByRuleID() {
		return originalmodel.genesInRuleLinkByRuleID();
	}



	@Override
	public LinkedHashSet<String> getNewConstrainedGenes() {
		return newconstrainedgenes;
	}
	

}
