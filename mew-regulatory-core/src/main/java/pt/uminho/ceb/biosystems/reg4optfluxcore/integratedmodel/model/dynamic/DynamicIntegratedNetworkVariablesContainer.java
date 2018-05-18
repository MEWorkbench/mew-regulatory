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

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryModelType;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;

public class DynamicIntegratedNetworkVariablesContainer extends IntegratedNetworkInitialStateContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DynamicIntegratedNetworkVariablesContainer(ArrayList<String> sortedregulatorygenes,
			ArrayList<String> sortedregulatoryvariables, 
			ArrayList<String> unconstrainedgenes,
			IndexedHashMap<String, Integer> onlyVariablesInRegulatoryNetwork,
			IndexedHashMap<String, Integer> commonMetabolicReactionIDAndRegulatoryReactionID,
			IndexedHashMap<String, Integer> commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID,
			IndexedHashMap<String, Integer> allIntegratedModelVariables,
			IndexedHashMap<String, RegulatoryModelComponent> variablesType,
			HashMap<String, Integer> commonIntegratedVariableAndMetabolicVariable,
			IndexedHashMap<String, Integer> mapTFVariables, IndexedHashMap<String, String> ruleid2metabolicgeneid,
			IndexedHashMap<String, String> geneid2ruleid, RegulatoryModelType typemodel) throws Exception {
		super(sortedregulatorygenes, sortedregulatoryvariables, unconstrainedgenes, onlyVariablesInRegulatoryNetwork,
				commonMetabolicReactionIDAndRegulatoryReactionID, commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID,
				allIntegratedModelVariables, variablesType, commonIntegratedVariableAndMetabolicVariable, mapTFVariables,
				ruleid2metabolicgeneid, geneid2ruleid, typemodel);

	}
	
	@SuppressWarnings("unchecked")
	public DynamicIntegratedNetworkVariablesContainer (IntegratedNetworkInitialStateContainer container) throws Exception{
		
		this((ArrayList<String>)MTUCollectionsUtils.deepCloneObject(container.getSortedRegulatoryGenes()), 
				(ArrayList<String>)MTUCollectionsUtils.deepCloneObject(container.getAllVariables()), 
				(ArrayList<String>)MTUCollectionsUtils.deepCloneObject(container.getUnconstrainedGenes()), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getOnlyVariablesInRegulatoryNetwork()), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getReactionVariablesPresentInBothModels()), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getMetaboliteVariablesPresentInBothModels()), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getRegulatoryVariableIndexes()), 
				(IndexedHashMap<String, RegulatoryModelComponent>)MTUCollectionsUtils.deepCloneObject(container.getVariablesType()), 
			    (HashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getCommonIntegratedVariableAndMetabolicVariable()), 
			    (IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getMapTFVariables()), 
			    (IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(container.getRuleid2metabolicgeneid()), 
			    (IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(container.getGeneid2ruleid()), 
			    container.getTypemodel());
		
	}

	
	private DynamicIntegratedNetworkVariablesContainer(ArrayList<String> sortedregulatorygenes, 
			ArrayList<String> sortedregulatoryvariables,
			ArrayList<String> unconstrainedgenes,
			IndexedHashMap<String, Integer> onlyVariablesInRegulatoryNetwork,
			IndexedHashMap<String, Integer> commonMetabolicReactionIDAndRegulatoryReactionID,
			IndexedHashMap<String, Integer> commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID,
			IndexedHashMap<String, Integer> allIntegratedModelVariables,
			IndexedHashMap<String, RegulatoryModelComponent> variablesType,
			HashMap<String, Integer> commonIntegratedVariableAndMetabolicVariable,
			IndexedHashMap<String, Integer> mapTFVariables,
			IndexedHashMap<String, String> ruleid2metabolicgeneid,
			IndexedHashMap<String, String> geneid2ruleid,
			RegulatoryModelType typemodel,
			InitialRegulatoryState initialregulatorystate) throws Exception{
		super(sortedregulatorygenes, 
				sortedregulatoryvariables, 
				unconstrainedgenes, 
				onlyVariablesInRegulatoryNetwork, 
				commonMetabolicReactionIDAndRegulatoryReactionID, 
				commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID, 
				allIntegratedModelVariables, 
				variablesType, 
				commonIntegratedVariableAndMetabolicVariable,
				mapTFVariables, 
				ruleid2metabolicgeneid, 
				geneid2ruleid, 
				typemodel);

	}
	
	
	
    @Override
	protected void buildRegulatoryInitialState() throws Exception{
		
			ArrayList<String> sortedidentifiers=new ArrayList<>();
			for (int i = 0; i < sortedregulatorygenes.size(); i++) {
				sortedidentifiers.add(sortedregulatorygenes.get(i));
			}
			for (int i = 0; i < sortedregulatoryvariables.size(); i++) {
				sortedidentifiers.add(sortedregulatoryvariables.get(i));
			}
			ArrayList<Boolean> initialstate=new ArrayList<>();
			
			for (int i = 0; i < sortedidentifiers.size(); i++) {
				initialstate.add(false);
			}
			
			this.initialregulatorystate=new InitialRegulatoryState(sortedidentifiers, initialstate, geneid2ruleid);
			
			LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator(" Regulatory initial state");
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Input Identifiers: ", sortedidentifiers);
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Input Identifiers State: ", initialstate);
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Input gene id to rule id: ", geneid2ruleid);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IntegratedNetworkInitialStateContainer copy() throws Exception{
		return new DynamicIntegratedNetworkVariablesContainer((ArrayList<String>)MTUCollectionsUtils.deepCloneObject(sortedregulatorygenes),
				(ArrayList<String>)MTUCollectionsUtils.deepCloneObject(sortedregulatoryvariables), 
				(ArrayList<String>)MTUCollectionsUtils.deepCloneObject(unconstrainedgenes), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(onlyVariablesInRegulatoryNetwork), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(commonMetabolicReactionIDAndRegulatoryReactionID), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(allIntegratedModelVariables), 
				(IndexedHashMap<String, RegulatoryModelComponent>)MTUCollectionsUtils.deepCloneObject(variablesType), 
				(HashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(commonRegulatoryVariableAndMetabolicVariable),
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(mapTFVariables), 
				(IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(ruleid2metabolicgeneid), 
				(IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(geneid2ruleid), 
				typemodel,
				initialregulatorystate.copy());
		
	}
	
	
	public void setOrderedRegulatoryGenes(ArrayList<String> regulatorygenes){
		this.sortedregulatorygenes=regulatorygenes;
	}
	
	public void setOrderedRegulatoryVariables(ArrayList<String> regulatoryvariables){
		this.sortedregulatoryvariables=regulatoryvariables;
	}
	
	public void setUnconstrainedGenes(ArrayList<String> unconstrainedgenes){
		this.unconstrainedgenes=unconstrainedgenes;
	}
	
	public boolean existsRegulatoryVariable(String varid){
		return allIntegratedModelVariables.containsKey(varid);
	}
	
	public void addCommonMetabolicReactionAndRegulatoryReactionIdentifier(String varid, int varindex){
		this.commonMetabolicReactionIDAndRegulatoryReactionID.put(varid, varindex);
		this.allIntegratedModelVariables.put(varid, varindex);
	}
	
	public void addIndexThatLinksRegulatoryVariableToMetabolicVariable(String varid, int metabolicvarindex){
		this.commonRegulatoryVariableAndMetabolicVariable.put(varid, metabolicvarindex);
	}
	
	public void addRegulatoryVariableType(String varid, RegulatoryModelComponent type){
		this.variablesType.put(varid, type);
	}
	
	public void addExclusiveRegulatoryVariable(String varid, int varindex){
		this.onlyVariablesInRegulatoryNetwork.put(varid, varindex);
		this.allIntegratedModelVariables.put(varid, varindex);
	}
	
	public void addcommonMetabolicMetaboliteAndRegulatoryMetaboliteIdentifier(String varid, int varindex){
		this.commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID.put(varid, varindex);
		this.allIntegratedModelVariables.put(varid, varindex);
	}
	
	public void addTranscriptionalFactorRegulatoryVariable(String varid, int varindex){
		this.mapTFVariables.put(varid, varindex);
		this.allIntegratedModelVariables.put(varid, varindex);
	}
	
	public void updateGeneid2RuleidMap(IndexedHashMap<String, String> geneid2ruleid){
		this.geneid2ruleid=geneid2ruleid;
	}


}
