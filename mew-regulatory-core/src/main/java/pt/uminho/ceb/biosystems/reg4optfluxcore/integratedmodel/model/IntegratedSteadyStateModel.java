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
package pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.javatuples.Triplet;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.Compartment;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.core.model.components.GeneReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.IStoichiometricMatrix;
import pt.uminho.ceb.biosystems.mew.core.model.components.Metabolite;
import pt.uminho.ceb.biosystems.mew.core.model.components.Pathway;
import pt.uminho.ceb.biosystems.mew.core.model.components.Protein;
import pt.uminho.ceb.biosystems.mew.core.model.components.ProteinReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.Reaction;
import pt.uminho.ceb.biosystems.mew.core.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.core.model.components.enums.ModelType;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.SteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.GeneType;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryModelType;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.VariableSignValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.RegulatoryNetworkFormat;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;

public class IntegratedSteadyStateModel extends SteadyStateGeneReactionModel implements IIntegratedStedystateModel, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IOptfluxRegulatoryModel optfluxregulatorymodelformat;
	protected IRODDRegulatoryModel roddregulatorymodelformat;
	protected IntegratedNetworkInitialStateContainer optregmodelvariablesInfoContainer;
	protected IntegratedNetworkInitialStateContainer roddmodelvariablesInfoContainer;
	

	// Genes
	protected HashMap<String,Integer> commonregulatorymetabolicgenesoptfluxmodelformat;
	protected HashMap<String,Integer> commonregulatorymetabolicgenesbddmodelformat;
	
	protected IndexedHashMap<String, Integer> integratedgeneid2geneindexmap;
	protected IndexedHashMap<Integer, String> integratedgeneindex2geneidmap;
	
	protected boolean usebddregulatoryformat=false;
	protected boolean supportmultivalueregulatorysimulation=false;
	

	
	public IntegratedSteadyStateModel(String modelId,
			IStoichiometricMatrix stoichiometricMatrix,
			IndexedHashMap<String, Reaction> reactions,
			IndexedHashMap<String, Metabolite> metabolites,
			Map<String, Compartment> compartments,
			IndexedHashMap<String, Pathway> pathways,
			IndexedHashMap<String, Gene> genes, // metabolic genes
			IndexedHashMap<String, Protein> proteins,
			IndexedHashMap<String, GeneReactionRule> geneReactionRules,
			IndexedHashMap<String, ProteinReactionRule> proteinReactionRules,
			IOptfluxRegulatoryModel optfluxregulatorymodelformat,
			IRODDRegulatoryModel bddregulatorymodelformat)
	
			throws Exception {
		super(modelId, stoichiometricMatrix, reactions, metabolites, compartments,
				pathways, genes, proteins, geneReactionRules, proteinReactionRules);
		
		if(optfluxregulatorymodelformat!=null)
			this.optfluxregulatorymodelformat = optfluxregulatorymodelformat;
		else
			usebddregulatoryformat=true;
		
		this.roddregulatorymodelformat=bddregulatorymodelformat;
		
		
		if(usebddregulatoryformat && bddregulatorymodelformat==null)
			throw new InvalidIntegratedModelException("Due to a null input of the regulatory model, is not possible to establish an integrated network model");
		else{
			
			if(!bddregulatorymodelformat.isBoolean()){
				usebddregulatoryformat=true;
				supportmultivalueregulatorysimulation=true;
				this.optfluxregulatorymodelformat=null;
			}
			mapRegulatoryToMetabolicGenes();
			createIntegratedGeneMap();
			if(this.optfluxregulatorymodelformat!=null)
				this.optregmodelvariablesInfoContainer=createVariablesContainer(false);
			if(roddregulatorymodelformat!=null)
				this.roddmodelvariablesInfoContainer=createVariablesContainer(true);
		}
	}
	
	
	public IntegratedSteadyStateModel(ISteadyStateGeneReactionModel metabolicmodel, 
			IOptfluxRegulatoryModel optfluxregulatorymodelformat,
			IRODDRegulatoryModel bddregulatorymodelformat) throws Exception{
		
		this(metabolicmodel.getId(), 
				metabolicmodel.getStoichiometricMatrix(), 
				metabolicmodel.getReactions(), 
				metabolicmodel.getMetabolites(), 
				metabolicmodel.getCompartments(), 
				metabolicmodel.getPathways(), 
				metabolicmodel.getGenes(), 
				null, 
				metabolicmodel.getGeneReactionRules(), 
				null, 
				optfluxregulatorymodelformat, 
				bddregulatorymodelformat);
	}
	
	
	protected IntegratedSteadyStateModel(String modelId,
			IStoichiometricMatrix stoichiometricMatrix,
			IndexedHashMap<String, Reaction> reactions,
			IndexedHashMap<String, Metabolite> metabolites,
			Map<String, Compartment> compartments,
			IndexedHashMap<String, Pathway> pathways,
			IndexedHashMap<String, Gene> genes, // metabolic genes
			IndexedHashMap<String, Protein> proteins,
			IndexedHashMap<String, GeneReactionRule> geneReactionRules,
			IndexedHashMap<String, ProteinReactionRule> proteinReactionRules)
	
			throws Exception {
		super(modelId, stoichiometricMatrix, reactions, metabolites, compartments,
				pathways, genes, proteins, geneReactionRules, proteinReactionRules);
		
		
	}
			
	

	
	/*
	 * Map genes from metabolic model with genes that are present in Regulatory model, the mapping is performed by the name 
	 * of regulatory gene to their position on metabolic model 
	 * 
	 */
	protected void mapRegulatoryToMetabolicGenes(){
		
		if(optfluxregulatorymodelformat!=null){
			commonregulatorymetabolicgenesoptfluxmodelformat=new HashMap<String, Integer>(); 
			for(String optreggeneid : optfluxregulatorymodelformat.getRegulatorIDs()){
				if(this.genes.containsKey(optreggeneid)){
					commonregulatorymetabolicgenesoptfluxmodelformat.put(optreggeneid, getGeneIndex(optreggeneid));
				}
			}
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Common metabolic vs regulatory genes: ", commonregulatorymetabolicgenesbddmodelformat);
		}
		
		if(roddregulatorymodelformat!=null){
			commonregulatorymetabolicgenesbddmodelformat=new HashMap<String, Integer>(); 
			for (String bddgeneid : roddregulatorymodelformat.getRegulatorIDs()) {
				if(this.genes.containsKey(bddgeneid))
					commonregulatorymetabolicgenesbddmodelformat.put(bddgeneid, getGeneIndex(bddgeneid));
			}
		}
	}
	
	/*
	 * necessary for optimization purposes
	 */
	protected void createIntegratedGeneMap() {
		integratedgeneid2geneindexmap=new IndexedHashMap<>();
		integratedgeneindex2geneidmap=new IndexedHashMap<>();
		
		int n=0;
		
		IRegulatoryNetwork currentmodel=getRegulatoryNetwork();
		ArrayList<String> regulatorygenes=currentmodel.getRegulatorIDs();
		for (int i = 0; i <regulatorygenes.size(); i++) {
			String regulatorid=regulatorygenes.get(i);
			integratedgeneid2geneindexmap.put(regulatorid, n);
			integratedgeneindex2geneidmap.put(n, regulatorid);
			n++;
		}
		
		for (int i = 0; i < genes.size(); i++) {
			String metabgeneid=genes.getKeyAt(i);
			if(!integratedgeneid2geneindexmap.containsKey(metabgeneid)) {
				integratedgeneid2geneindexmap.put(metabgeneid, n);
				integratedgeneindex2geneidmap.put(n, metabgeneid);
				n++;
			}
		}
	}
	
	
	
	
	
	public void useLogicalModelNetworkFormat(boolean active){
		if(roddregulatorymodelformat!=null) {
		   this.usebddregulatoryformat=active;
		   createIntegratedGeneMap();
		}
	}
	
	public boolean supportLogicalModelFormat(){
		if(roddregulatorymodelformat!=null)
			return true;
		return false;
	}
	
	@Override
	public boolean isInBDDRegulatoryNetworkFormat() {
		return usebddregulatoryformat;
	}
	
	
	public boolean isMultiValueRegulatoryModel(){
		return supportmultivalueregulatorysimulation;
	}
	
	@Override
	public IRegulatoryNetwork getRegulatoryNetworkFormat(RegulatoryNetworkFormat format) {
		if(format.equals(RegulatoryNetworkFormat.BDDFORMAT))
			return roddregulatorymodelformat;
		else
			return optfluxregulatorymodelformat;
	}

	
	public Boolean genepresentinbothnetworks(String geneId){
		if(usebddregulatoryformat)
			return commonregulatorymetabolicgenesbddmodelformat.containsKey(geneId);
		else
			return commonregulatorymetabolicgenesoptfluxmodelformat.containsKey(geneId);
	}
	
	public IRegulatoryNetwork getRegulatoryNetwork(){
		if(usebddregulatoryformat)
			return roddregulatorymodelformat;
		else
			return optfluxregulatorymodelformat;
	}
	
	@Override
	public Integer getNumberOfCommonGenesInBothNetwork(){
		if(usebddregulatoryformat)
			return commonregulatorymetabolicgenesbddmodelformat.size();
		else
		   return commonregulatorymetabolicgenesoptfluxmodelformat.size();
	}
	
	
	public Boolean isRegulatoryGene(String geneId){
		if(usebddregulatoryformat){
			return roddregulatorymodelformat.isRegulatorOnlyAtRegulatoryNetwork(geneId) && getGene(geneId)==null;
		}
		else
			return optfluxregulatorymodelformat.isRegulatorOnlyAtRegulatoryNetwork(geneId) && getGene(geneId)==null;
	}
	
	public Boolean isMetabolicGene(String geneId){
		return getGene(geneId)!=null;
	}
	
	public ArrayList<String> getAllGenes(){
		ArrayList<String> res=new ArrayList<>();
		
		LinkedHashSet<String> regulatoryunconstrainedgenes;
		ArrayList<String> regulatorygeneids;
		
		if(usebddregulatoryformat){
			regulatoryunconstrainedgenes=roddregulatorymodelformat.getUnconstrainedGenes();
			regulatorygeneids=roddregulatorymodelformat.getRegulatorIDs();
		}
		else{
			regulatoryunconstrainedgenes=optfluxregulatorymodelformat.getUnconstrainedGenes();
			regulatorygeneids=optfluxregulatorymodelformat.getRegulatorIDs();
		}
		
		
		for (int i = 0; i < regulatorygeneids.size(); i++) {
			String id=regulatorygeneids.get(i);
			if(!genepresentinbothnetworks(id) && !res.contains(id))
				res.add(id);
		 }
		
		for (int i = 0; i < genes.size(); i++) {
			String gid=genes.getKeyAt(i);
			if(!res.contains(gid))
				res.add(gid);
		}
		
		if(regulatoryunconstrainedgenes!=null){
			for (String id : regulatoryunconstrainedgenes) {
				if(!res.contains(id))
					res.add(id);
			}
		}
		
		return res;
	}
	
	
	public GeneType getGeneType(String geneid){
		if(isMetabolicGene(geneid))
			return GeneType.metabolic;
		else if(isRegulatoryGene(geneid))
			return GeneType.regulatory;
		else
			return GeneType.undefined;
	}
	
	
	public ArrayList<String> getonlyMetabolicGenes(){
		ArrayList<String> metbgenes = new ArrayList<>();
		
		IndexedHashMap<String, Gene> metabolicGenes = getGenes();
		for(String geneId : metabolicGenes.keySet()){
				metbgenes.add(geneId);
		}
		return metbgenes;
	}
	
	
	
	
	public ArrayList<String> getOnlyRegulatoryGenes(){
		ArrayList<String> res= new ArrayList<>();
		ArrayList<String> regulatorygenes = null;
		
		if(usebddregulatoryformat)
			regulatorygenes=roddregulatorymodelformat.getRegulatorIDs();
		else
			regulatorygenes=optfluxregulatorymodelformat.getRegulatorIDs();
		
		for (int i = 0; i < regulatorygenes.size(); i++) {
			String id = regulatorygenes.get(i);
			if(!genes.containsKey(id) && !res.contains(id)){
				   res.add(id);
			}
		}
		return res;
	}
	
	public ArrayList<String> getUnconstrainedGenes(){
		ArrayList<String> res=new ArrayList<>();
		ArrayList<String> unconstrainedregulatorygenes=null;
		if(usebddregulatoryformat){
			if(roddregulatorymodelformat.getUnconstrainedGenes()!=null)
				unconstrainedregulatorygenes=new ArrayList<>(roddregulatorymodelformat.getUnconstrainedGenes());
		}
		else{
			if(optfluxregulatorymodelformat.getUnconstrainedGenes()!=null)
				unconstrainedregulatorygenes=new ArrayList<>(optfluxregulatorymodelformat.getUnconstrainedGenes());
		}
		
		if(unconstrainedregulatorygenes!=null)
			res.addAll(unconstrainedregulatorygenes);
		
		for (int i = 0; i < genes.size(); i++) {
			String geneid=genes.getKeyAt(i);
			if(!genepresentinbothnetworks(geneid))
				res.add(geneid);
		}
		return res;
	}
	
	
	public ArrayList<String> filterOnlyMetabolicGenes(ArrayList<String> geneslist){
		ArrayList<String> metabolicGenes = new ArrayList<String>();
		for(int i =0; i < geneslist.size(); i++){
			if(genes.containsKey(geneslist.get(i))){
				metabolicGenes.add(geneslist.get(i));
			}
		}
		return metabolicGenes;
	}
	
	
	
	 public ArrayList<String> filterOnlyRegulatoryGenes(ArrayList<String> geneslist){
		ArrayList<String> regulatoryGenes = new ArrayList<String>();
		for(int i =0; i < geneslist.size(); i++){
			if(!genes.containsKey(geneslist.get(i))){
				regulatoryGenes.add(geneslist.get(i));
			}	
		}
		return regulatoryGenes;
	}
	
	
	// get all regulatory genes 
	public ArrayList<String> getGenesInRegulatoryModel(){
		if(usebddregulatoryformat)
			return new ArrayList<>(roddregulatorymodelformat.getRegulatorIDs());
		else
			return new ArrayList<>(optfluxregulatorymodelformat.getRegulatorIDs());
	}
	
	
	/*public IndexedHashMap<String,Protein> getProteins(){
		return proteins;
	}

	public IndexedHashMap<String,ProteinReactionRule> getProteinReactionRules(){
		return proteinReactionRules;
	}*/
	
	
    public IntegratedNetworkInitialStateContainer getIntegratedVariablesContainerWihoutValidation(){
    	if(usebddregulatoryformat)
    		return this.roddmodelvariablesInfoContainer;
    	else
    		return this.optregmodelvariablesInfoContainer;
	}
    
    
    public ArrayList<String> getRegulatoryVariablesIDs(){
    	if(usebddregulatoryformat)
    		return roddregulatorymodelformat.getVariableNamesInNetwork();
    	else
    		return optfluxregulatorymodelformat.getVariableNamesInNetwork();
    }
    
    
    public IntegratedNetworkInitialStateContainer getIntegratedVariablesContainerWithValidation(EnvironmentalConditions envconds) throws Exception{

    	IntegratedNetworkInitialStateContainer copycontainer=null;
    	if(usebddregulatoryformat){
    		if(roddmodelvariablesInfoContainer!=null)
    			copycontainer=this.roddmodelvariablesInfoContainer.copy();
    	}
    	else{
    		if(optregmodelvariablesInfoContainer!=null)
    			copycontainer=this.optregmodelvariablesInfoContainer.copy();
    	}

    	copycontainer.initializeRegulatoryNetworkState();
    	if(copycontainer!=null){
    		ArrayList<String> truestatevariables=checkTrueStateVariablesInIntegratedModel(this, envconds);
    		if(truestatevariables.size()>0){
    			copycontainer.setRegulatoryVariablesToTrueState(truestatevariables);
    		}
    	}

    	return copycontainer;
    }
	
    
    
    
    public boolean supportsMultiValuedRegulatorySimulation() {
		return supportmultivalueregulatorysimulation;
	}

    public IndexedHashMap<String, String> getRegulatoryMapGeneId2RuleId(){
    	if(usebddregulatoryformat)
    		return roddregulatorymodelformat.getMapGeneId2RuleId();
    	else
    		return optfluxregulatorymodelformat.getMapGeneId2RuleId();
    }

	public void changeRegulatoryVariableType(String varid, RegulatoryModelComponent type){
    	optfluxregulatorymodelformat.changeVariableType(varid, type);
    	if(roddregulatorymodelformat!=null)
    		roddregulatorymodelformat.changeVariableType(varid, type);
    }

	
	protected IntegratedNetworkInitialStateContainer createVariablesContainer(boolean useroddformat) throws Exception{
		
		ArrayList<String> sortedregulatorygenes=getGenesInRegulatoryModel();
		ArrayList<String> sortedregulatoryvariables=getRegulatoryVariablesIDs();
		ArrayList<String> unconstrainedgenes=getUnconstrainedGenes();
		IndexedHashMap<String, Integer> onlyVariablesInRegulatoryNetwork=new IndexedHashMap<String, Integer>();
		IndexedHashMap<String, Integer> commonMetabolicReactionIDAndRegulatoryReactionID=new IndexedHashMap<String, Integer>();
		IndexedHashMap<String, Integer> commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID=new IndexedHashMap<String, Integer>();
		IndexedHashMap<String, Integer> allIntegratedModelVariables=new IndexedHashMap<String, Integer>();
		IndexedHashMap<String, RegulatoryModelComponent> variablesType = new IndexedHashMap<String, RegulatoryModelComponent>();
		HashMap<String, Integer> regulatoryVariablelinktoMetabolicVariablebyindex=new HashMap<String, Integer>();
		IndexedHashMap<String, Integer> mapTFVariables=new IndexedHashMap<String, Integer>();
		IndexedHashMap<String, String> ruleid2metabolicgeneid=new IndexedHashMap<String, String>();
		
		IndexedHashMap<String, RegulatoryVariable> regulatoryVariables=null;
		if(useroddformat)
			regulatoryVariables=roddregulatorymodelformat.getVariablesInRegulatoryNetwork();
		else
			regulatoryVariables=optfluxregulatorymodelformat.getVariablesInRegulatoryNetwork();
		
	
		
		for (int j = 0; j < regulatoryVariables.size(); j++) {
		    String varid=regulatoryVariables.getKeyAt(j);
			RegulatoryModelComponent vartype=regulatoryVariables.get(varid).getType();
			RegulatoryVariable regvar=regulatoryVariables.get(varid);

			
			if(vartype.equals(RegulatoryModelComponent.REACTION_ID)){
				
				processPossibleReaction(j,varid,regvar,
						commonMetabolicReactionIDAndRegulatoryReactionID,
						regulatoryVariablelinktoMetabolicVariablebyindex,
						onlyVariablesInRegulatoryNetwork,
						variablesType);
			}
			else if(vartype.equals(RegulatoryModelComponent.METABOLITE_ID)){
				
				 processPossibleMetabolite(j, varid, regvar,
						 commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID,
						 regulatoryVariablelinktoMetabolicVariablebyindex,
						 onlyVariablesInRegulatoryNetwork,
						 variablesType);

			}
			else if(vartype.equals(RegulatoryModelComponent.ENV_CONDITION_ID)){
				String varname = regulatoryVariables.getKeyAt(j);
				if(regvar.getVariableSign()!=null){
					processPossibleReaction(j,varid,regvar,
							commonMetabolicReactionIDAndRegulatoryReactionID,
							regulatoryVariablelinktoMetabolicVariablebyindex,
							onlyVariablesInRegulatoryNetwork,
							variablesType);
				}
				else if(metaboliteMap.containsKey(varid))
					processPossibleMetabolite(j, varid, regvar,
							commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID,
							regulatoryVariablelinktoMetabolicVariablebyindex,
							onlyVariablesInRegulatoryNetwork,
							variablesType);
				else if(reactionMap.containsKey(varid)){
					processPossibleReaction(j,varid,regvar,
							commonMetabolicReactionIDAndRegulatoryReactionID,
							regulatoryVariablelinktoMetabolicVariablebyindex,
							onlyVariablesInRegulatoryNetwork,
							variablesType);
					
					regulatoryVariables.getValueAt(j).setComponentType(RegulatoryModelComponent.REACTION_ID);
					
				}
				else{
					onlyVariablesInRegulatoryNetwork.put(varid, j);
					variablesType.put(varid, RegulatoryModelComponent.ENV_CONDITION_ID);
				}
				
			}
			else if(vartype.equals(RegulatoryModelComponent.TRANS_FACTOR_ID)){
				
				mapTFVariables.put(varid, j);
				variablesType.put(varid, RegulatoryModelComponent.TRANS_FACTOR_ID);
				
			}
	        
			allIntegratedModelVariables.put(varid, j);
			
			//System.out.println(varid+" --> "+regvar.getType());
		}
		
		IndexedHashMap<String, String> geneid2ruleid=getRegulatoryMapGeneId2RuleId();
				
		for (int i = 0; i < geneid2ruleid.size(); i++) {
			String geneid=geneid2ruleid.getKeyAt(i);
			if(this.genes.containsKey(geneid))
				ruleid2metabolicgeneid.put(geneid2ruleid.get(geneid), geneid);
		}
		
		RegulatoryModelType typemodel=null;
		if(useroddformat){
			if(supportmultivalueregulatorysimulation)
				typemodel=RegulatoryModelType.MDDFORMAT;
			else
				typemodel=RegulatoryModelType.BDDFORMAT;
		}
		else
			typemodel=RegulatoryModelType.OPTFLUXFORMAT;
		
		return new IntegratedNetworkInitialStateContainer(sortedregulatorygenes, 
				sortedregulatoryvariables, 
				unconstrainedgenes, 
				onlyVariablesInRegulatoryNetwork, 
				commonMetabolicReactionIDAndRegulatoryReactionID, 
				commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID, 
				allIntegratedModelVariables, 
				variablesType, 
				regulatoryVariablelinktoMetabolicVariablebyindex, 
				mapTFVariables, 
				ruleid2metabolicgeneid,
				geneid2ruleid,
				typemodel);
	}
	
	
	
	
	

	/*
	 * Map the variables that are present in Regulatory model and Metabolic Model, also do the mapping of the variables and the linked 
	 * metabolic reactions.
	 */
	
	
	
	
	protected void processPossibleReaction(int currentpos, String varid, RegulatoryVariable regvar,
			IndexedHashMap<String, Integer> mapOfMatchMetabolicReactionsAndRegulatoryReactions,
			HashMap<String, Integer> regulatoryVariablelinksMetabolicReactionbyindex,
			IndexedHashMap<String, Integer> mapOfRegulatoryVariablesOnly,
			IndexedHashMap<String, RegulatoryModelComponent> mapOfThetypeOfTheVariables){
		
		String varname =regvar.getSimpleName();

		if(this.reactionMap.containsKey(varname)){
			 regvar.setAsMetabolicVariable(true);
	    	 mapOfMatchMetabolicReactionsAndRegulatoryReactions.put(varid, currentpos);
			 regulatoryVariablelinksMetabolicReactionbyindex.put(varid,getReactionIndex(varname)); 
			 mapOfThetypeOfTheVariables.put(varid, RegulatoryModelComponent.REACTION_ID);
			 changeRegulatoryVariableType(varid,RegulatoryModelComponent.REACTION_ID);
			
	     }
	     else{
	    	 mapOfRegulatoryVariablesOnly.put(varid,currentpos);
	    	 regvar.setComponentType(RegulatoryModelComponent.ENV_CONDITION_ID);
	    	 mapOfThetypeOfTheVariables.put(varid, RegulatoryModelComponent.ENV_CONDITION_ID);
	    	 changeRegulatoryVariableType(varid, RegulatoryModelComponent.ENV_CONDITION_ID);
	     }
	     
	    	
	}
	
	
	
	protected void processPossibleMetabolite(int currentpos, String varid, RegulatoryVariable regvar,
			IndexedHashMap<String, Integer> mapOfMatchMetabolicMetabolitesAndRegulatoryMetabolites,
			HashMap<String, Integer> mapOfIntegratedVariableToMetabolicReaction,
			IndexedHashMap<String, Integer> mapOfRegulatoryVariablesOnly,
			IndexedHashMap<String, RegulatoryModelComponent> mapOfThetypeOfTheVariables){
		
		
		if(metaboliteMap.containsKey(varid)){
			regvar.setAsMetabolicVariable(true);
			int metabindex=getMetaboliteIndex(varid);
			mapOfMatchMetabolicMetabolitesAndRegulatoryMetabolites.put(varid, currentpos);
			int drainreactionindex=getDrainIndexFromMetabolite(metabindex);
			if(drainreactionindex!=-1)
				mapOfIntegratedVariableToMetabolicReaction.put(varid,drainreactionindex);
		}
		else
			mapOfRegulatoryVariablesOnly.put(varid,currentpos);
		
		mapOfThetypeOfTheVariables.put(varid, RegulatoryModelComponent.METABOLITE_ID);	
	}
	
	
	@Override
	public ModelType getModelType() {
		return ModelType.INTEGRATED_STEADY_STATE_MODEL;
	}
	
	@Override
	public void setOptfluxRegulatoryNetworkFormat(IOptfluxRegulatoryModel regmodel) throws Exception {
		this.optfluxregulatorymodelformat=regmodel;
		if(regmodel!=null){
			usebddregulatoryformat=false;
			supportmultivalueregulatorysimulation=false;
			mapRegulatoryToMetabolicGenes();
			createVariablesContainer(false);
		}
	}
	
	@Override
	public void setROBDDRegulatoryNetworkFormat(IRODDRegulatoryModel regmodel) throws Exception {
		this.roddregulatorymodelformat=regmodel;
		
		if(regmodel!=null){
			if(!regmodel.isBoolean()){
				usebddregulatoryformat=true;
				supportmultivalueregulatorysimulation=true;
			}
			mapRegulatoryToMetabolicGenes();
			createVariablesContainer(true);
		}
		
	}


	/*@SuppressWarnings("unchecked")
	@Override
	public  IIntegratedStedystateModel copy() throws Exception {
		
		IRODDRegulatoryModel copyrobdd=null;
		if(roddregulatorymodelformat!=null)
			copyrobdd=(IRODDRegulatoryModel) roddregulatorymodelformat.copy();
		
		IOptfluxRegulatoryModel copyoptreg=null;
		if(optfluxregulatorymodelformat!=null)
			copyoptreg=(IOptfluxRegulatoryModel) optfluxregulatorymodelformat.copy();

		IIntegratedStedystateModel copy= new IntegratedSteadyStateModel(this.getId(), this.getStoichiometricMatrix(), 
				(IndexedHashMap<String, Reaction>)MTUCollectionsUtils.deepCloneObject(getReactions()), 
				(IndexedHashMap<String, Metabolite>)MTUCollectionsUtils.deepCloneObject(getMetabolites()), 
				(Map<String, Compartment>)MTUCollectionsUtils.deepCloneObject(this.getCompartments()), 
				(IndexedHashMap<String, Pathway>)MTUCollectionsUtils.deepCloneObject(this.getPathways()), 
				(IndexedHashMap<String, Gene>)MTUCollectionsUtils.deepCloneObject(this.getGenes()), 
				(IndexedHashMap<String,Protein>)MTUCollectionsUtils.deepCloneObject(this.getProteins()), 
				(IndexedHashMap<String, GeneReactionRule>)MTUCollectionsUtils.deepCloneObject(this.getGeneReactionRules()), 
				(IndexedHashMap<String,ProteinReactionRule>)MTUCollectionsUtils.deepCloneObject(this.getProteinReactionRules()),
			    copyoptreg,
				copyrobdd);
		
		copy.setBiomassFlux(this.getBiomassFlux());
		return copy;
	}*/
	
	@Override
	public IOptfluxRegulatoryModel getOptFluxRegulatoryModelFormat() {
		return this.optfluxregulatorymodelformat;
	}



	@Override
	public IRODDRegulatoryModel getRODDRegulatoryModelFormat() {
		return this.roddregulatorymodelformat;
	}

	
	public static ArrayList<String> checkTrueStateVariablesInIntegratedModel(IIntegratedStedystateModel model, EnvironmentalConditions env) throws Exception {
		
	    	IndexedHashMap<String, RegulatoryVariable> regulatoryVariables= model.getRegulatoryNetwork().getVariablesInRegulatoryNetwork();
	    	IntegratedNetworkInitialStateContainer variablescontainer=model.getIntegratedVariablesContainerWihoutValidation();
	    	ArrayList<String> variables = variablescontainer.getAllVariables();
		
	    	ArrayList<String> truemodelvariables = new ArrayList<String>();

		
	
	    	for(String varid: variables){
	    		RegulatoryVariable regvar=regulatoryVariables.get(varid);
	    		int indexReaction=-1;
			
	    		indexReaction = variablescontainer.getIndexMetabolicReactionForVariable(varid);
	    		RegulatoryModelComponent vartype=regvar.getType();
			
			
			
	    		if(indexReaction!=-1){
	    			boolean reachtruestate = false;
	    			String varname=regvar.getSimpleName();
	    			ReactionConstraint limits=null; 
				
				
	    			if(vartype.equals(RegulatoryModelComponent.REACTION_ID)){
					
	    				if(env!=null)
	    					limits=env.getReactionConstraint(varname);
	    				if(limits==null)
	    					limits=model.getReaction(varname).getConstraints();
					
	    				if(regvar.getVariableSign()!=null){
	    					VariableSignValue varsign=regvar.getVariableSign();
						  	String value=regvar.getSignValue();
						  
						  	if(varsign.equals(VariableSignValue.LESS)){
						  		reachtruestate=limits.getUpperLimit()>Double.parseDouble(value); 
						  	}
						  	else if(varsign.equals(VariableSignValue.GREATER)){
						  		reachtruestate=limits.getLowerLimit()<Double.parseDouble(value);
						  	}
						  

	    				}
	    				else{
	    					reachtruestate=limits.getLowerLimit()<0.0;  
	    				}
	    			}
	    			else if(vartype.equals(RegulatoryModelComponent.METABOLITE_ID)){
					
	    				int drainreactmetabindex=variablescontainer.getIndexMetabolicReactionForVariable(varname);
	    				if(drainreactmetabindex!=-1){
	    					String reactid=model.getReactionId(drainreactmetabindex);
	    					if(env!=null)
	    						limits=env.get(reactid);
	    					if(limits==null)
	    						limits=model.getReactionConstraint(drainreactmetabindex);
						
	    					if(limits!=null)
	    						reachtruestate=limits.getLowerLimit()<0.0; 
	    				}
	    			}

	    			if(reachtruestate)
	    				truemodelvariables.add(varid);
			
	    		}
	    	}
		
	    	return truemodelvariables;
		}
	
	
	public static ArrayList<Triplet<String,ReactionConstraint,Boolean>> getStateVariablesInIntegratedModelWithConstraints(IIntegratedStedystateModel model, EnvironmentalConditions env) throws Exception {
		
    	IndexedHashMap<String, RegulatoryVariable> regulatoryVariables= model.getRegulatoryNetwork().getVariablesInRegulatoryNetwork();
    	IntegratedNetworkInitialStateContainer variablescontainer=model.getIntegratedVariablesContainerWihoutValidation();
    	ArrayList<String> variables = variablescontainer.getAllVariables();

    	ArrayList<Triplet<String,ReactionConstraint,Boolean>> commonmodelvariables = new ArrayList<Triplet<String,ReactionConstraint,Boolean>>();

	

    	for(String varid: variables){
    		RegulatoryVariable regvar=regulatoryVariables.get(varid);
    		int indexReaction=-1;
		
    		indexReaction = variablescontainer.getIndexMetabolicReactionForVariable(varid);
    		RegulatoryModelComponent vartype=regvar.getType();
		
		
		
    		if(indexReaction!=-1){
    			
    			boolean reachtruestate = false;
    			String varname=regvar.getSimpleName();
    			ReactionConstraint limits=null; 
			
			
    			if(vartype.equals(RegulatoryModelComponent.REACTION_ID)){
				
    				if(env!=null)
    					limits=env.getReactionConstraint(varname);
    				if(limits==null)
    					limits=model.getReaction(varname).getConstraints();
				
    				if(regvar.getVariableSign()!=null){
    					VariableSignValue varsign=regvar.getVariableSign();
					  	String value=regvar.getSignValue();
					  
					  	if(varsign.equals(VariableSignValue.LESS)){
					  		reachtruestate=limits.getUpperLimit()>Double.parseDouble(value); 
					  	}
					  	else if(varsign.equals(VariableSignValue.GREATER)){
					  		reachtruestate=limits.getLowerLimit()<Double.parseDouble(value);
					  	}
					

    				}
    				else{
    					reachtruestate=limits.getLowerLimit()<0.0;  
    				}
    			}
    			else if(vartype.equals(RegulatoryModelComponent.METABOLITE_ID)){
				
    				int drainreactmetabindex=variablescontainer.getIndexMetabolicReactionForVariable(varname);
    				if(drainreactmetabindex!=-1){
    					String reactid=model.getReactionId(drainreactmetabindex);
    					if(env!=null)
    						limits=env.get(reactid);
    					if(limits==null)
    						limits=model.getReactionConstraint(drainreactmetabindex);
					
    					if(limits!=null)
    						reachtruestate=limits.getLowerLimit()<0.0; 
    				}
    			}

    			
    			Triplet<String,ReactionConstraint,Boolean> info=new Triplet<String, ReactionConstraint, Boolean>(varname, limits, reachtruestate);
    			commonmodelvariables.add(info);
    		}
    	}
	
    	return commonmodelvariables;
	}


	
	public static HashSet<String> convertRegulatorIdToRegulatorProductID(Set<String> setids, IndexedHashMap<String, String> regulatorid2ruleid){
		HashSet<String> res=new HashSet<>();
		for (String id : setids) {
			if(regulatorid2ruleid!=null && regulatorid2ruleid.containsKey(id))
				res.add(regulatorid2ruleid.get(id));
			else
				res.add(id);
				
		}
		return res;
	}


	@Override
	public int getIntegratedGeneIndex(String geneid) {
		return integratedgeneid2geneindexmap.get(geneid);
	}


	@Override
	public String getIntegratedGeneName(int index) {
		return integratedgeneindex2geneidmap.get(index);
	}


	@Override
	public int getTotalGenesIntegratedNetwork() {
		return integratedgeneid2geneindexmap.size();
	}





	


}
