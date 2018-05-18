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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.columnbase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import pt.ornrocha.logutils.MTULogUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.UnsuportedVariableException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components.RegModelInfoContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components.RegModelTempinformationContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.AbstractRegulatoryNetworkReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;

public abstract class AbstractColumnBaseRegulatoryNetworkReader extends AbstractRegulatoryNetworkReader{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	protected RegModelInfoContainer infocontainer;
	
	protected String delimiter = ";";

	
	protected HashSet<String> remainingunknownVars=null;
	protected ArrayList<String> modelLines=null;
    protected IndexedHashMap<String, String> mapofknownreactionsids;
    protected boolean loadedmodel=false;
	
	

	
	public AbstractColumnBaseRegulatoryNetworkReader(String filepath) throws Exception{
		super(filepath);
		this.modelLines=readFile(filepath);
	}
	
	public AbstractColumnBaseRegulatoryNetworkReader(String filepath, RegModelInfoContainer infocontainer) throws Exception{
		super(filepath);
		this.modelLines=readFile(filepath);
		this.infocontainer=infocontainer;
	}
	
	
	public AbstractColumnBaseRegulatoryNetworkReader(String filepath, RegModelInfoContainer infocontainer, String knownvariablesfile) throws Exception{
		super(filepath, knownvariablesfile);
		this.modelLines=readFile(filepath);
		this.infocontainer=infocontainer;	
	}
	
	public AbstractColumnBaseRegulatoryNetworkReader(String filepath, 
			RegModelInfoContainer infocontainer, 
			String knownmetabolitesfile, 
			String knownenvironmentalconditionsfile,
			String knownreactionsfile,
			String knowntfsfile,
			String knowngenesfiles) throws Exception{
		super(filepath, knownmetabolitesfile, knownenvironmentalconditionsfile, knownreactionsfile, knowntfsfile, knowngenesfiles);
		
		this.modelLines=readFile(filepath);
		this.infocontainer=infocontainer;
			
	} 
	
	
	
	
	abstract public String getReaderName();
	abstract protected ArrayList<String> readFile(String filepath) throws Exception;
	
	
	public void setRegulatoryModelFile(String filepath) throws Exception{
		if(filepath!=null && new File(filepath).exists()){
			this.modelLines=readFile(filepath);
		}
		else
			throw new Exception("The file "+filepath+" does not exist!"); 
	}
	

	
	public IndexedHashMap<String, RegulatoryModelComponent> getRemainingunknownVars(){
		if(remainingunknownVars.size()>0){
			IndexedHashMap<String, RegulatoryModelComponent> res=new IndexedHashMap<>();
			for (String id : remainingunknownVars) {
				res.put(id, RegulatoryModelComponent.ENV_CONDITION_ID);
			}
			return res;
		}
		return null;
	}
	
	@Override
	public void loadModel() throws Exception {
		readRegulatoryModelInformation();
		loadedmodel=true;
		
	}

	
	protected void readRegulatoryModelInformation() throws Exception {
		
		if(infocontainer==null){
			this.infocontainer=new RegModelInfoContainer(0, 1, delimiter);
		}
		
		
		RegModelTempinformationContainer tempinformation = new RegModelTempinformationContainer(this.modelLines, this.infocontainer);
		
		//this.variabletypemap=new IndexedHashMap<>();
		if(tempinformation.getUnconstrainedgenes().size()>0)
			this.unconstrainedgenes=new LinkedHashSet<>(tempinformation.getUnconstrainedgenes());
		
		processKnownReactions(tempinformation.getListofdetectedReactions());
		processExtractedRulesAndGenesInformation(tempinformation);
		
		setVariablesAsMetabolites(tempinformation.getListofdetectedExchangeMetabolites());
		
		
		if(tempinformation.haveUnknownVarsTypeInRules())
			filtervariablesidentifiedbyuser(tempinformation.getUnkownvariablestype());
	
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Remaining Unknown Variables: "+ remainingunknownVars);


	 }
	
	
    protected void processExtractedRulesAndGenesInformation(RegModelTempinformationContainer tempinformation) throws Exception{
		
		regulatoryGeneRules = new IndexedHashMap<String, RegulatoryRule>();
	    regulatoryGenes = new IndexedHashMap<String, Regulator>();
		GeneID2RuleID=new IndexedHashMap<String,String>();

		 
		IndexedHashMap<String, String> mapMainId2Rule = tempinformation.getMapLinkerIdentifier2Rule();
		
		IndexedHashMap<String, String> mapGPRlinker2RuleByID = tempinformation.getMapGPRLink2RuleByIdentifier();
		
		IndexedHashMap<String, String> mapMainId2GeneName = tempinformation.getGeneidMapToGeneName();
	
		IndexedHashMap<Integer, HashSet<String>> mapofdetectedreactionsids=tempinformation.getMapofdetectedreactionsinrule();
	
		HashSet<String> ElementsAlreadyMappedToRegulatoryRule= new HashSet<String>();
         
		for (int i = 0; i < mapMainId2Rule.size(); i++) {
		
			String ruleidentifier=mapGPRlinker2RuleByID.get(mapMainId2Rule.getKeyAt(i));
			if(ElementsAlreadyMappedToRegulatoryRule.contains(ruleidentifier)){
				throw new Exception(mapMainId2Rule.getKeyAt(i)+" have a duplicated element link that bind diferent Regulatory rules");
			}
			else{
				if(mapMainId2Rule.getKeyAt(i)!=mapMainId2Rule.getValueAt(i))
					genesidlinkbyruleid=true;
				
				boolean addrule=true;
				String rule=mapMainId2Rule.getValueAt(i);
				
				if(unconstrainedgenes!=null && unconstrainedgenes.contains(mapMainId2Rule.getKeyAt(i)))
					addrule=false;
				else if(rule.isEmpty())
					addrule=false;
				
				
				if(addrule){
					
					if(mapofdetectedreactionsids.containsKey(i))
						rule=changeRuleReactionsIdentifiers(rule, mapofdetectedreactionsids.get(i),mapofknownreactionsids);
				
					GeneID2RuleID.put(mapMainId2Rule.getKeyAt(i), mapGPRlinker2RuleByID.get(mapMainId2Rule.getKeyAt(i)));
					regulatoryGeneRules.put(mapMainId2Rule.getKeyAt(i), new RegulatoryRule(ruleidentifier, rule));
					ElementsAlreadyMappedToRegulatoryRule.add(ruleidentifier);
			
					String geneproduct=null;
					String genename=null;
				
					if(ruleidentifier!=mapMainId2Rule.getKeyAt(i))
						geneproduct=ruleidentifier;
				
					if(mapMainId2GeneName!=null)
						genename=mapMainId2GeneName.get(mapMainId2Rule.getKeyAt(i));
					else
						genename=mapMainId2Rule.getKeyAt(i);
				
					Regulator geneCI=new Regulator(mapMainId2Rule.getKeyAt(i), genename, geneproduct);
					regulatoryGenes.put(mapMainId2Rule.getKeyAt(i), geneCI);
					//variabletypemap.put(mapMainId2Rule.getKeyAt(i), RegulatoryModelComponent.GENE_ID);
				}
			  }
           }
		
	    }
    
    
    protected void processKnownReactions(HashSet<String> list){
		
		if(list!=null){
			 if(regulatoryVariables==null)
		         regulatoryVariables = new IndexedHashMap<String, RegulatoryVariable>();
			 
			 mapofknownreactionsids=new IndexedHashMap<>();
			
			for (String cond : list) {
				RegulatoryVariable var = RegulatoryVariable.setupVariable(cond, RegulatoryModelComponent.REACTION_ID); 

				if(knownenvironmentalconditionsids!=null && knownenvironmentalconditionsids.contains(var.getId())){
					var.setComponentType(RegulatoryModelComponent.ENV_CONDITION_ID);
				}
				regulatoryVariables.put(var.getId(), var);
				//variabletypemap.put(var.getId(), var.getType());
				mapofknownreactionsids.put(cond, var.getId());
				LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Conditions Auto-Detected:", cond);
			}
		}	
	}
    
    
 
     
     
	protected void processIdentifiedTFs(HashSet<String> tfids, RegModelTempinformationContainer tempinformation) throws UnsuportedVariableException{
		
		if(regulatoryVariables==null)
			regulatoryVariables = new IndexedHashMap<String, RegulatoryVariable>();
		
		ArrayList<String> linkersidstoGenes = tempinformation.getListOfIdentifiersThatLinkRules();
		
		if(tfids!=null){
			
			for (String linkerid : tfids) {
				if(!linkersidstoGenes.contains(linkerid)){
				  RegulatoryVariable tf =new RegulatoryVariable(linkerid, linkerid,RegulatoryModelComponent.TRANS_FACTOR_ID);
				  tf.setIndependentvariable(true);
				  regulatoryVariables.put(linkerid, tf);
				 // variabletypemap.put(linkerid, RegulatoryModelComponent.TRANS_FACTOR_ID);
				  
				  MTULogUtils.addDebugMsgToClass(this.getClass(), "Transcriptional Factor without rule association: {}", linkerid);
				}
			}
		}
	}
     
     
     
     
     protected void filtervariablesidentifiedbyuser(HashSet<String> variablestofilter){
    	
    	HashSet<String> remainsunknown=new HashSet<>();
    	
    	for (String id : variablestofilter) {
    		
    		RegulatoryVariable var=null;
			
    		if(knownmetabolitesids!=null && knownmetabolitesids.contains(id)){
    			var=RegulatoryVariable.setupVariable(id, RegulatoryModelComponent.METABOLITE_ID);
    		}
    		else if(knownenvironmentalconditionsids!=null && knownenvironmentalconditionsids.contains(id)){
    			var=RegulatoryVariable.setupVariable(id, RegulatoryModelComponent.ENV_CONDITION_ID);
    		}
    		else if(knownreactionids!=null && knownreactionids.contains(id)){
    			var=RegulatoryVariable.setupVariable(id, RegulatoryModelComponent.REACTION_ID);
    		}
    		else if(knowntfids!=null && knowntfids.contains(id)){
    			var=RegulatoryVariable.setupVariable(id, RegulatoryModelComponent.TRANS_FACTOR_ID);
    		}
    		else if(knowngeneids!=null && knowngeneids.contains(id)){
    			var=RegulatoryVariable.setupVariable(id, RegulatoryModelComponent.GENE_ID);
    		}
    		else{
    			var=RegulatoryVariable.setupVariable(id, RegulatoryModelComponent.ENV_CONDITION_ID);
    			remainsunknown.add(id);
    		}
    		
    		if(var!=null){
    		   regulatoryVariables.put(var.getId(), var);
    		   //variabletypemap.put(var.getId(), var.getType());
    		}
    		
		}
    	
    	if(remainsunknown.size()>0){
    		verifiedvariables=false;
    		this.remainingunknownVars=remainsunknown;
    	}
    	else
    		verifiedvariables=true;
    	
    }
    
    
    
    public static String changeRuleReactionsIdentifiers(String rule, HashSet<String> modids,  Map<String, String> mapidstoreplace){
    	
    	String newrule=null;
    	for (String idtoreplace : modids) {
			String newid=mapidstoreplace.get(idtoreplace);
			if(newrule==null)
				newrule=rule.replaceAll(idtoreplace, newid);
			else
				newrule=newrule.replaceAll(idtoreplace, newid);
		}
    	if(newrule==null)
    		newrule=rule;
    	
    	return newrule;
    }
    
    

	
	
	public void setVariablesAsTFs(HashSet<String> tfs) throws UnsuportedVariableException{
		
		if(tfs!=null){
			if(regulatoryVariables==null)
				regulatoryVariables = new IndexedHashMap<String, RegulatoryVariable>();
		
			for (String identifiedTF : tfs) {
				RegulatoryVariable tf = new RegulatoryVariable(identifiedTF, identifiedTF,RegulatoryModelComponent.TRANS_FACTOR_ID);
				tf.setIndependentvariable(true);
				regulatoryVariables.put(identifiedTF, tf);
				//variabletypemap.put(identifiedTF, RegulatoryModelComponent.TRANS_FACTOR_ID);
			}
		}
	}
	
	
	

	public void setVariablesAsEnvironmentalConditions(HashSet<String> conds) throws UnsuportedVariableException{
		
		if(conds!=null){
			if(regulatoryVariables==null)
	   		   regulatoryVariables = new IndexedHashMap<String, RegulatoryVariable>();
		
			for (String identifiedcond : conds) {
				RegulatoryVariable cond = RegulatoryVariable.setupVariable(identifiedcond, RegulatoryModelComponent.ENV_CONDITION_ID);
				cond.setIndependentvariable(true);
				regulatoryVariables.put(cond.getId(), cond);
				//variabletypemap.put(cond.getId(), RegulatoryModelComponent.ENV_CONDITION_ID);
			}
		}
	}
	
	public void setVariablesAsMetabolites(HashSet<String> metaboliteids){
		if(metaboliteids!=null){
			if(regulatoryVariables==null)
				regulatoryVariables = new IndexedHashMap<String, RegulatoryVariable>();
			
			for (String metab : metaboliteids) {
				RegulatoryVariable var = RegulatoryVariable.setupVariable(metab, RegulatoryModelComponent.METABOLITE_ID);
				regulatoryVariables.put(metab, var);
				//variabletypemap.put(metab, RegulatoryModelComponent.METABOLITE_ID);
				MTULogUtils.addDebugMsgToClass(this.getClass(), "Metabolites Auto-Detected: {}", metab);
			}
		}
	}
	
	public void setVariablesAsReactions(HashSet<String> reactids){
		if(reactids!=null){
			if(regulatoryVariables==null)
				regulatoryVariables = new IndexedHashMap<String, RegulatoryVariable>();
			
			for (String reactid : reactids) {
				RegulatoryVariable var = RegulatoryVariable.setupVariable(reactid, RegulatoryModelComponent.REACTION_ID);
				regulatoryVariables.put(var.getId(), var);
				//variabletypemap.put(var.getId(), RegulatoryModelComponent.REACTION_ID);
				MTULogUtils.addDebugMsgToClass(this.getClass(), "Reaction identified by user: {}", reactid);
			}
		}
	}
	
	public void setVariablesAsGenes(HashSet<String> geneids){
		if(geneids!=null){
			if(regulatoryVariables==null)
				regulatoryVariables = new IndexedHashMap<String, RegulatoryVariable>();
			
			for (String id : geneids) {
				RegulatoryVariable var = RegulatoryVariable.setupVariable(id, RegulatoryModelComponent.GENE_ID);
				regulatoryVariables.put(var.getId(), var);
				//variabletypemap.put(id, RegulatoryModelComponent.GENE_ID);
				MTULogUtils.addDebugMsgToClass(this.getClass(), "Gene identified by user: {}", id);
			}
		}
	}
	
	
	
	
	
	@Override
	public boolean isModelLoaded() {
		return loadedmodel;
	}

	

	
	public void setDelimiter(String del){
		this.delimiter = del;
	}

	
	@Override
	public IRODDRegulatoryModel getROBDDModelFormat() {
		return null;
	}


	
	

}
