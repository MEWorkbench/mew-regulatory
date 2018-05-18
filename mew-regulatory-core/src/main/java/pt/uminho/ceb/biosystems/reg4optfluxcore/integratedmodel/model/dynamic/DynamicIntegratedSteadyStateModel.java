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
package pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

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
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IntegratedSteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.dynamic.DynamicRegulatoryBooleanModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.dynamic.IDynamicRegulatoryModel;

public class DynamicIntegratedSteadyStateModel extends IntegratedSteadyStateModel implements IDynamicIntegratedSteadyStateModel{


	private static final long serialVersionUID = 1L;
	private IntegratedNetworkInitialStateContainer initialvariablesInfoContainer;
	private DynamicIntegratedNetworkVariablesContainer varsContainerSnapshot=null;
	private ArrayList<String> genesconsideredasunconstrained;
	//private HashMap<String,Integer> initialcommonregulatorymetabolicgenesoptfluxmodelformat;
	//private IDynamicRegulatoryModel dynamicregulatorymodel;

	public DynamicIntegratedSteadyStateModel(String modelId, IStoichiometricMatrix stoichiometricMatrix,
			IndexedHashMap<String, Reaction> reactions, IndexedHashMap<String, Metabolite> metabolites,
			Map<String, Compartment> compartments, IndexedHashMap<String, Pathway> pathways,
			IndexedHashMap<String, Gene> genes, IndexedHashMap<String, Protein> proteins,
			IndexedHashMap<String, GeneReactionRule> geneReactionRules,
			IndexedHashMap<String, ProteinReactionRule> proteinReactionRules,
			String biomassid,
			IOptfluxRegulatoryModel optfluxregulatorymodelformat, 
			IRODDRegulatoryModel bddregulatorymodelformat, 
			IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables, 
			HashMap<String, String> possiblegeneid2ruleid,
			HashMap<String, String> geneid2genename,
			Boolean initemptyregulatorymodel)
			throws Exception {
			super(modelId, stoichiometricMatrix, reactions, metabolites, compartments, pathways, genes, proteins, geneReactionRules,
				proteinReactionRules, optfluxregulatorymodelformat, bddregulatorymodelformat);
			
	/*	if(optfluxregulatorymodelformat==null)
			throw new Exception("Dynamic Integrated Model only supports regulatory models in OptFlux regulatory model format");
		else{*/
			this.setBiomassFlux(biomassid);
			this.usebddregulatoryformat=false;
			initDynamicRegulatorymodel(optfluxregulatorymodelformat, new ArrayList<>(reactions.keySet()),possiblegeneid2ruleid, geneid2genename,possibletypevariables,initemptyregulatorymodel);
			mapRegulatoryToMetabolicGenes();
			//this.initialcommonregulatorymetabolicgenesoptfluxmodelformat=MTUMapUtils.deepClone(commonregulatorymetabolicgenesoptfluxmodelformat);
			try {
				setCurrentInitialRegulatoryVariablesStateContainer(createVariablesContainer(false));
			} catch (Exception e) {
				LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage(e);
			}
			
			this.initialvariablesInfoContainer=optregmodelvariablesInfoContainer.copy();
		//}
	}
	
	public DynamicIntegratedSteadyStateModel(String modelId, IStoichiometricMatrix stoichiometricMatrix,
			IndexedHashMap<String, Reaction> reactions, IndexedHashMap<String, Metabolite> metabolites,
			Map<String, Compartment> compartments, IndexedHashMap<String, Pathway> pathways,
			IndexedHashMap<String, Gene> genes, IndexedHashMap<String, Protein> proteins,
			IndexedHashMap<String, GeneReactionRule> geneReactionRules,
			IndexedHashMap<String, ProteinReactionRule> proteinReactionRules,
			String biomassid,
			IOptfluxRegulatoryModel optfluxregulatorymodelformat, 
			IRODDRegulatoryModel bddregulatorymodelformat, 
			IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables, 
			HashMap<String, String> possiblegeneid2ruleid,
			HashMap<String, String> geneid2genename)
			throws Exception {
		
	   this(modelId, stoichiometricMatrix, reactions, metabolites, compartments, pathways, genes, proteins, geneReactionRules, proteinReactionRules,biomassid, optfluxregulatorymodelformat, bddregulatorymodelformat, possibletypevariables, possiblegeneid2ruleid, geneid2genename, false);
	}
	
	
	public DynamicIntegratedSteadyStateModel(IIntegratedStedystateModel integratedmodel, 
			HashMap<String, String> extrageneid2ruleid,
			HashMap<String, String> extrageneid2genename,
			IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables,
			Boolean initemptyregulatorymodel)throws Exception {
		
		this(integratedmodel.getId(), 
				integratedmodel.getStoichiometricMatrix(),
				integratedmodel.getReactions(),
				integratedmodel.getMetabolites(),
				integratedmodel.getCompartments(),
				integratedmodel.getPathways(),
				integratedmodel.getGenes(),
				integratedmodel.getProteins(),
				integratedmodel.getGeneReactionRules(),
				integratedmodel.getProteinReactionRules(),
				integratedmodel.getBiomassFlux(),
				integratedmodel.getOptFluxRegulatoryModelFormat(),
				integratedmodel.getRODDRegulatoryModelFormat(),
				possibletypevariables,
				extrageneid2ruleid,
				extrageneid2genename,
				initemptyregulatorymodel);

		//this.setBiomassFlux(integratedmodel.getBiomassFlux());
		//System.out.println("BIOMASS: "+getBiomassFlux());
	}
	
/*	private DynamicIntegratedSteadyStateModel(DynamicIntegratedSteadyStateModel tocopy) throws Exception {
		super(tocopy.getId(), 
				tocopy.getStoichiometricMatrix(), 
				tocopy.getReactions(), 
				tocopy.getMetabolites(), 
				tocopy.getCompartments(), 
				tocopy.getPathways(), 
				tocopy.getGenes(),
				tocopy.getProteins(),
				tocopy.getGeneReactionRules(),
				tocopy.getProteinReactionRules());
		
		
		
		this.optfluxregulatorymodelformat=(IOptfluxRegulatoryModel) tocopy.getOptFluxRegulatoryModelFormat().copy();
		this.usebddregulatoryformat=false;
		this.supportmultivalueregulatorysimulation=false;
		this.varsContainerSnapshot=(DynamicIntegratedNetworkVariablesContainer) tocopy.varsContainerSnapshot.copy();
		this.initialvariablesInfoContainer=tocopy.initialvariablesInfoContainer.copy();
		this.genesconsideredasunconstrained=(ArrayList<String>) MTUCollectionsUtils.deepCloneObject(tocopy.genesconsideredasunconstrained);
		
		this.optregmodelvariablesInfoContainer=tocopy.optregmodelvariablesInfoContainer.copy();
		this.commonregulatorymetabolicgenesoptfluxmodelformat=(HashMap<String, Integer>) MTUCollectionsUtils.deepCloneObject(tocopy.commonregulatorymetabolicgenesoptfluxmodelformat);
		this.integratedgeneid2geneindexmap=(IndexedHashMap<String, Integer>) MTUCollectionsUtils.deepCloneObject(tocopy.integratedgeneid2geneindexmap);
		this.integratedgeneindex2geneidmap= (IndexedHashMap<Integer, String>) MTUCollectionsUtils.deepCloneObject(tocopy.integratedgeneindex2geneidmap);
	}*/
	
	
	
	private void initDynamicRegulatorymodel(IOptfluxRegulatoryModel origmodel, ArrayList<String> metabolicreactionids, HashMap<String, String> possiblegeneid2ruleid, HashMap<String, String> geneid2genename,IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables, boolean initemptyregulatorymodel){
		this.optfluxregulatorymodelformat=new DynamicRegulatoryBooleanModel(origmodel,metabolicreactionids,  possiblegeneid2ruleid, geneid2genename,possibletypevariables,initemptyregulatorymodel);
		
		((IDynamicRegulatoryModel)optfluxregulatorymodelformat).setSaveAPreviousState(true);
		
	}
	
	@Override
	protected void mapRegulatoryToMetabolicGenes(){
		
		if(optfluxregulatorymodelformat!=null){
			commonregulatorymetabolicgenesoptfluxmodelformat=new HashMap<String, Integer>();
			for(String optreggeneid : optfluxregulatorymodelformat.getRegulatorIDs()){
				if(this.genes.containsKey(optreggeneid)){
					commonregulatorymetabolicgenesoptfluxmodelformat.put(optreggeneid, getGeneIndex(optreggeneid));
				}
			}
		}
	}
	
	private void setCurrentInitialRegulatoryVariablesStateContainer(IntegratedNetworkInitialStateContainer container) throws Exception{
		this.optregmodelvariablesInfoContainer=new DynamicIntegratedNetworkVariablesContainer(container);
	}
	
	
	private void resetRegulatoryVariablesInfoContainer() throws Exception{
		setCurrentInitialRegulatoryVariablesStateContainer(initialvariablesInfoContainer);
	}
	
	/*private void resetCommonRegulatoryMetabolicGenes(){
		commonregulatorymetabolicgenesoptfluxmodelformat=MTUMapUtils.deepClone(initialcommonregulatorymetabolicgenesoptfluxmodelformat);
	}*/
	
	public IRegulatoryNetwork getRegulatoryNetwork(){
		return this.optfluxregulatorymodelformat;
	}
	
	public void resetIntegratedModelToInitialState() throws Exception{
		resetRegulatoryVariablesInfoContainer();
		((IDynamicRegulatoryModel)optfluxregulatorymodelformat).initializeDynamicRegulatoryModelParameters();
		mapRegulatoryToMetabolicGenes();
	}
	
	public void addGeneAsUnconstrained(String geneid){
		if(genesconsideredasunconstrained==null)
			genesconsideredasunconstrained=new ArrayList<>();
		genesconsideredasunconstrained.add(geneid);
	}
	
	
	/**
	 * This function will reset old added rules and will test only a new regulatory rule
	 * @param geneid
	 * @param rule
	 * @throws Exception
	 */
	@Override
	public void setNewSingleRegulatoryRule(String geneid, String rule) throws Exception {
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("New Rule: ", geneid, rule);
		resetRegulatoryVariablesInfoContainer();
		((IDynamicRegulatoryModel)optfluxregulatorymodelformat).setNewSingleRegulatoryRule(geneid, rule);
		mapRegulatoryToMetabolicGenes();
		rebuildVariablesContainer();
	}
	
	
	/**
	 * This function will append the new rule to current rules in regulatory model 
	 */
	@Override
	public void addNewSingleRegulatoryRule(String geneid, String rule) throws Exception {
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("New Rule: ", geneid, rule);
		((IDynamicRegulatoryModel)optfluxregulatorymodelformat).addNewSingleRegulatoryRule(geneid, rule);
		mapRegulatoryToMetabolicGenes();
		rebuildVariablesContainer();
	}

	
	/**
	 * This function will reset old added rules and will test only a new group of regulatory rules
	 * 
	 */
	@Override
	public void setNewGroupOfRegulatoryRules(IndexedHashMap<String, String> regrules) throws Exception {
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("New Set of Regulatory Rules: ", regrules);
		resetRegulatoryVariablesInfoContainer();
		((IDynamicRegulatoryModel)optfluxregulatorymodelformat).setNewGroupOfRegulatoryRules(regrules);
		mapRegulatoryToMetabolicGenes();
		rebuildVariablesContainer();
	}
	
	
	/**
	 * This function will append the new group of rules to current rules in regulatory model 
	 */
	@Override
	public void addNewGroupOfRegulatoryRules(IndexedHashMap<String, String> regrules) throws Exception {
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("New Set of Regulatory Rules: ", regrules);
		((IDynamicRegulatoryModel)optfluxregulatorymodelformat).addNewGroupOfRegulatoryRules(regrules);
		mapRegulatoryToMetabolicGenes();
		rebuildVariablesContainer();
	}


	@Override
	public void setIntegratedModelToPreviousState() {
		this.optregmodelvariablesInfoContainer=this.varsContainerSnapshot;
		((IDynamicRegulatoryModel)optfluxregulatorymodelformat).setRegModelToPreviousInternalState();
		mapRegulatoryToMetabolicGenes();
	}
	
	
	@Override
	public Boolean genepresentinbothnetworks(String geneId){
		return commonregulatorymetabolicgenesoptfluxmodelformat.containsKey(geneId);
	}
	
	@Override
	public ArrayList<String> getUnconstrainedGenes(){
		ArrayList<String> res=new ArrayList<>();
		ArrayList<String> unconstrainedregulatorygenes=null;
		
		if(optfluxregulatorymodelformat.getUnconstrainedGenes()!=null)
			unconstrainedregulatorygenes=new ArrayList<>(optfluxregulatorymodelformat.getUnconstrainedGenes());

		if(unconstrainedregulatorygenes!=null)
			res.addAll(unconstrainedregulatorygenes);

		LinkedHashSet<String> newconstrainedgenes=null;
		if(optfluxregulatorymodelformat instanceof IDynamicRegulatoryModel) {
			newconstrainedgenes=((IDynamicRegulatoryModel)optfluxregulatorymodelformat).getNewConstrainedGenes();
		}
		
		for (int i = 0; i < genes.size(); i++) {
			String geneid=genes.getKeyAt(i);
			boolean canadd=true;
			
			if(genepresentinbothnetworks(geneid))
			     canadd=false;
			else if(newconstrainedgenes!=null && newconstrainedgenes.contains(geneid))
				canadd=false;

			if(canadd)
				res.add(geneid);
		}
		
		//System.out.println("res prev: "+res);
		
		if(genesconsideredasunconstrained!=null){
			for (int i = 0; i < genesconsideredasunconstrained.size(); i++) {
				String geneid=genesconsideredasunconstrained.get(i);
				if(!res.contains(geneid))
					res.add(geneid);
			}
		}
		
		//System.out.println("genesconsideredasunconstrained: "+unconstrainedregulatorygenes);
		
	//	System.out.println(optfluxregulatorymodelformat.getClass()+" --> "+res);
		
		return res;
	}
	
	
	// fazer o metodo para update do geneid2ruleid 
	protected void rebuildVariablesContainer() throws Exception{
		
        this.varsContainerSnapshot=(DynamicIntegratedNetworkVariablesContainer) optregmodelvariablesInfoContainer.copy(); 
        
        LogMessageCenter.getLogger().toClass(getClass()).addDebugSeparator("Changes in Dynamic IntegratedNetworkVariablesContainer");
        
        ((DynamicIntegratedNetworkVariablesContainer) optregmodelvariablesInfoContainer).setOrderedRegulatoryGenes(optfluxregulatorymodelformat.getRegulatorIDs());
        ((DynamicIntegratedNetworkVariablesContainer) optregmodelvariablesInfoContainer).setOrderedRegulatoryVariables(optfluxregulatorymodelformat.getVariableNamesInNetwork());
        ((DynamicIntegratedNetworkVariablesContainer) optregmodelvariablesInfoContainer).setUnconstrainedGenes(getUnconstrainedGenes());
         
        IndexedHashMap<String, RegulatoryVariable> addedvariables=((IDynamicRegulatoryModel)optfluxregulatorymodelformat).getOnlyNewRegulatoryVariablesOfNetwork();
        IndexedHashMap<String, Integer> newRegulatoryVariablesIndexes=((IDynamicRegulatoryModel)optfluxregulatorymodelformat).getIndexesNewRegulatoryVariablesOfNetwork();
        
		for (int j = 0; j < addedvariables.size(); j++) {
		    String varid = addedvariables.getKeyAt(j);
		    RegulatoryVariable regvar=addedvariables.get(varid);
		    
		    RegulatoryModelComponent type=regvar.getType();
		    
		    LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Variable added: ", varid, regvar.getType(),"\n");
		    
		    if(!((DynamicIntegratedNetworkVariablesContainer)optregmodelvariablesInfoContainer).existsRegulatoryVariable(varid)){
		    	
		    	
		    	if(type.equals(RegulatoryModelComponent.REACTION_ID)){

		    		String varAux = regvar.getSimpleName();
		
		    		if(this.reactionMap.containsKey(varAux)){
			    	    ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addCommonMetabolicReactionAndRegulatoryReactionIdentifier(varid, newRegulatoryVariablesIndexes.get(varid));
			    	    ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addIndexThatLinksRegulatoryVariableToMetabolicVariable(varid,getReactionIndex(varAux)); 
			    	    ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.REACTION_ID);
			    	    regvar.setAsMetabolicVariable(true);
			    	    LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Link new Reaction Variable to metab model: ",varid,getReactionIndex(varAux),"\n");
			    	
		    		}
		    		else{
		    			((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addExclusiveRegulatoryVariable(varid, newRegulatoryVariablesIndexes.get(varid));
			    	    ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.REACTION_ID);
			    	    LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Added as user variable: ",varid,newRegulatoryVariablesIndexes.get(varid),"\n");
		    		}
		    	}
		    	else if(type.equals(RegulatoryModelComponent.METABOLITE_ID)){
		    		if(this.metaboliteMap.containsKey(varid)){
		    			((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addcommonMetabolicMetaboliteAndRegulatoryMetaboliteIdentifier(varid, newRegulatoryVariablesIndexes.get(varid));
			    	    int metabindex=getMetaboliteIndex(varid);
			    	    int drainreactionindex=getDrainIndexFromMetabolite(metabindex);
						if(drainreactionindex!=-1)
			    	       ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addIndexThatLinksRegulatoryVariableToMetabolicVariable(varid,drainreactionindex); 
						
						((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.METABOLITE_ID);
						regvar.setAsMetabolicVariable(true);
						LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Link new Metabolite Variable to metab model: ",varid,drainreactionindex,"\n");
			    	
		    		}
		    		else{
		    			((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addExclusiveRegulatoryVariable(varid, newRegulatoryVariablesIndexes.get(varid));
			    	    ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.METABOLITE_ID);
			    	    LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Added as user variable: ",varid, newRegulatoryVariablesIndexes.get(varid),"\n");
			    	
		    		}
				
		    	}
		    	else if(type.equals(RegulatoryModelComponent.ENV_CONDITION_ID)){
				
		    		if(regvar.getVariableSign()!=null){

					     String varAux =regvar.getSimpleName();
					     if(this.reactionMap.containsKey(varAux)){
					    	   ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addCommonMetabolicReactionAndRegulatoryReactionIdentifier(varid, newRegulatoryVariablesIndexes.get(varid));
					    	   ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addIndexThatLinksRegulatoryVariableToMetabolicVariable(varid,getReactionIndex(varAux)); 
					    	   ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.REACTION_ID);
					    	   regvar.setAsMetabolicVariable(true);
					    	   regvar.setComponentType(RegulatoryModelComponent.REACTION_ID);
					    	   LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Link new Reaction Variable to metab model: ",varid,getReactionIndex(varAux),"\n");
					    
					     }
					     else{
					    	   ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addExclusiveRegulatoryVariable(varid, newRegulatoryVariablesIndexes.get(varid));
					    	   ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.ENV_CONDITION_ID);
					    	   LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Added as user variable: ",varid, newRegulatoryVariablesIndexes.get(varid),"\n");
					
						}
		    		}
		    		else{
					
		    			if(this.metaboliteMap.containsKey(varid)){
		    				((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addcommonMetabolicMetaboliteAndRegulatoryMetaboliteIdentifier(varid, newRegulatoryVariablesIndexes.get(varid));
				    	    int metabindex=getMetaboliteIndex(varid);
				    	    int drainreactionindex=getDrainIndexFromMetabolite(metabindex);
							if(drainreactionindex!=-1)
				    	       ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addIndexThatLinksRegulatoryVariableToMetabolicVariable(varid,drainreactionindex); 
							
							((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.METABOLITE_ID);
							regvar.setAsMetabolicVariable(true);
							regvar.setComponentType(RegulatoryModelComponent.METABOLITE_ID);
							LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Link new Metabolite Variable to metab model: ",varid,drainreactionindex,"\n");
				    	 
		    			}
		    			else{
		    				((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addExclusiveRegulatoryVariable(varid, newRegulatoryVariablesIndexes.get(varid));
		    				((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.ENV_CONDITION_ID);
		    				LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Added as user variable: ",varid, newRegulatoryVariablesIndexes.get(varid),"\n");
				    	 
		    			}
		    		}
		    	}
		    	else if(type.equals(RegulatoryModelComponent.TRANS_FACTOR_ID)){
		    		((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addTranscriptionalFactorRegulatoryVariable(varid, newRegulatoryVariablesIndexes.get(varid));
				    ((DynamicIntegratedNetworkVariablesContainer)this.optregmodelvariablesInfoContainer).addRegulatoryVariableType(varid, RegulatoryModelComponent.TRANS_FACTOR_ID);
				    LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Added as new TF: ", varid,newRegulatoryVariablesIndexes.get(varid),"\n");
				
		    	}
		    }
		    
		 }
		
		IndexedHashMap<String, String> geneid2generuleid=((IDynamicRegulatoryModel)optfluxregulatorymodelformat).getMapGeneId2RuleId();
		((DynamicIntegratedNetworkVariablesContainer) optregmodelvariablesInfoContainer).updateGeneid2RuleidMap(geneid2generuleid);
		((DynamicIntegratedNetworkVariablesContainer) optregmodelvariablesInfoContainer).buildRegulatoryInitialState();
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("update geneid vs rule id: ", geneid2generuleid);
		
		//((DynamicIntegratedNetworkVariablesContainer) optregmodelvariablesInfoContainer).buildRegulatoryInitialState();
		
	}
	
	@Override
	public ArrayList<String> getAllGenes(){
		ArrayList<String> res=new ArrayList<>();
		
		LinkedHashSet<String> regulatoryunconstrainedgenes=optfluxregulatorymodelformat.getUnconstrainedGenes();
		
		ArrayList<String> regulatorygeneids=optfluxregulatorymodelformat.getRegulatorIDs();
	
		
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
	
	@Override
	public ArrayList<String> getOnlyRegulatoryGenes(){
		ArrayList<String> res= new ArrayList<>();
		ArrayList<String> regulatorygenes=optfluxregulatorymodelformat.getRegulatorIDs();
		
		for (int i = 0; i < regulatorygenes.size(); i++) {
			String id = regulatorygenes.get(i);
			if(!genes.containsKey(id) && !res.contains(id)){
				   res.add(id);
			}
		}
		return res;
	}
	
	@Override
	public void useLogicalModelNetworkFormat(boolean active){
		if(active)
			System.out.println("Dynamic Integrated Model only supports regulatory models in OptFlux regulatory model format");
		   this.usebddregulatoryformat=false;   
	}
	
	@Override
	public boolean supportLogicalModelFormat(){
		return false;
	}
	
	@Override
	public boolean isMultiValueRegulatoryModel(){
		return false;
	}
	  
	
	public IntegratedNetworkInitialStateContainer getIntegratedVariablesContainerWihoutValidation(){
	    return this.optregmodelvariablesInfoContainer;
	}
	
	@Override
	public IntegratedNetworkInitialStateContainer getIntegratedVariablesContainerWithValidation(EnvironmentalConditions envconds) throws Exception{
	    	
		DynamicIntegratedNetworkVariablesContainer copycontainer=new DynamicIntegratedNetworkVariablesContainer(optregmodelvariablesInfoContainer);
	    ArrayList<String> truestatevariables=checkTrueStateVariablesInIntegratedModel(this, envconds);
	    if(truestatevariables.size()>0){
	    	copycontainer.setRegulatoryVariablesToTrueState(truestatevariables);
	     }

	    return copycontainer;
	 }

	/*@Override
	public  IIntegratedStedystateModel copy() throws Exception {
		return new DynamicIntegratedSteadyStateModel(this);
	}*/
}
