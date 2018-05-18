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
package pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryModelType;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;

public class IntegratedNetworkInitialStateContainer implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<String> sortedregulatorygenes;
	protected ArrayList<String> sortedregulatoryvariables;
	protected ArrayList<String> unconstrainedgenes;
	protected IndexedHashMap<String, Integer> onlyVariablesInRegulatoryNetwork;
	protected IndexedHashMap<String, Integer> commonMetabolicReactionIDAndRegulatoryReactionID;
	protected IndexedHashMap<String, Integer> commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID;
	protected IndexedHashMap<String, Integer> allIntegratedModelVariables;
	protected IndexedHashMap<String, RegulatoryModelComponent> variablesType;
	protected HashMap<String, Integer> commonRegulatoryVariableAndMetabolicVariable;
	protected IndexedHashMap<String, Integer> mapTFVariables;
	protected IndexedHashMap<String, String> ruleid2metabolicgeneid;
	protected IndexedHashMap<String, String> geneid2ruleid;
	protected IndexedHashMap<String, String> ruleid2geneid;
	protected RegulatoryModelType typemodel;
	protected InitialRegulatoryState initialregulatorystate;
	protected ArrayList<String> unconstrainedgenesknockouted;
	protected boolean forceTFinitStateastrue=false; // if(user 
	
	public IntegratedNetworkInitialStateContainer(ArrayList<String> sortedregulatorygenes, 
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
			RegulatoryModelType typemodel) throws Exception{
		
		 this.sortedregulatorygenes=sortedregulatorygenes;
		 this.sortedregulatoryvariables=sortedregulatoryvariables;
		 this.unconstrainedgenes=unconstrainedgenes;
		 this.onlyVariablesInRegulatoryNetwork=onlyVariablesInRegulatoryNetwork;
		 this.commonMetabolicReactionIDAndRegulatoryReactionID=commonMetabolicReactionIDAndRegulatoryReactionID;
		 this.commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID=commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID;
		 this.allIntegratedModelVariables=allIntegratedModelVariables;
		 this.variablesType=variablesType;
		 this.commonRegulatoryVariableAndMetabolicVariable=commonIntegratedVariableAndMetabolicVariable;
		 this.mapTFVariables=mapTFVariables;
		 this.ruleid2metabolicgeneid=ruleid2metabolicgeneid;
		 this.geneid2ruleid=geneid2ruleid;
		 this.typemodel=typemodel;
		 
		 buildRegulatoryInitialState();
		 
		/*  System.out.println("ONLY REG VARS: "+this.onlyVariablesInRegulatoryNetwork);
		  System.out.println("MAP TFS: "+ this.mapTFVariables);
		  System.out.println("MAP geneid2ruleid: "+ this.geneid2ruleid);*/
	}
	
	@SuppressWarnings("unchecked")
	public IntegratedNetworkInitialStateContainer (IntegratedNetworkInitialStateContainer container) throws Exception{
		
		this((ArrayList<String>)MTUCollectionsUtils.deepCloneObject(container.getSortedRegulatoryGenes()), 
				(ArrayList<String>)MTUCollectionsUtils.deepCloneObject(container.getAllVariables()), 
				(ArrayList<String>)MTUCollectionsUtils.deepCloneObject(container.getUnconstrainedGenes()), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getOnlyVariablesInRegulatoryNetwork()), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getReactionVariablesPresentInBothModels()), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getMetaboliteVariablesPresentInBothModels()), 
				(IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getRegulatoryVariableIndexes()), 
				(IndexedHashMap<String, RegulatoryModelComponent>)MTUCollectionsUtils.deepCloneObject(container.getVariablesType()), 
			    (IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getCommonIntegratedVariableAndMetabolicVariable()), 
			    (IndexedHashMap<String, Integer>)MTUCollectionsUtils.deepCloneObject(container.getMapTFVariables()), 
			    (IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(container.getRuleid2metabolicgeneid()), 
			    (IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(container.getRuleid2metabolicgeneid()), 
			    container.getTypemodel());
		
	}
	
	protected IntegratedNetworkInitialStateContainer(ArrayList<String> sortedregulatorygenes, 
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
		
		 this.sortedregulatorygenes=sortedregulatorygenes;
		 this.sortedregulatoryvariables=sortedregulatoryvariables;
		 this.unconstrainedgenes=unconstrainedgenes;
		 this.onlyVariablesInRegulatoryNetwork=onlyVariablesInRegulatoryNetwork;
		 this.commonMetabolicReactionIDAndRegulatoryReactionID=commonMetabolicReactionIDAndRegulatoryReactionID;
		 this.commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID=commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID;
		 this.allIntegratedModelVariables=allIntegratedModelVariables;
		 this.variablesType=variablesType;
		 this.commonRegulatoryVariableAndMetabolicVariable=commonIntegratedVariableAndMetabolicVariable;
		 this.mapTFVariables=mapTFVariables;
		 this.ruleid2metabolicgeneid=ruleid2metabolicgeneid;
		 this.geneid2ruleid=geneid2ruleid;
		 this.typemodel=typemodel;
	     this.initialregulatorystate=initialregulatorystate;
	     
	   
	}
	
	protected void buildRegulatoryInitialState() throws Exception{
		
		if(typemodel.equals(RegulatoryModelType.BDDFORMAT) || typemodel.equals(RegulatoryModelType.MDDFORMAT)){
			
			ArrayList<String> sortedidentifiers=new ArrayList<>();
			for (int i = 0; i < sortedregulatoryvariables.size(); i++) {
				sortedidentifiers.add(sortedregulatoryvariables.get(i));
			}
			for (int i = 0; i < sortedregulatorygenes.size(); i++) {
				sortedidentifiers.add(sortedregulatorygenes.get(i));
			}
			byte[] initialstate =new byte[sortedidentifiers.size()];
			
			for (int i = 0; i < initialstate.length; i++) {
				initialstate[i]=0;
			}
			
			this.initialregulatorystate=new InitialRegulatoryState(sortedidentifiers, initialstate,this.geneid2ruleid);
		}
		else{
			
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
				//initialstate.add(true);
			}
			
			this.initialregulatorystate=new InitialRegulatoryState(sortedidentifiers, initialstate, geneid2ruleid);
		}
		
	}
	
	public void initializeRegulatoryNetworkState() throws Exception{
		buildRegulatoryInitialState();
	}
	
	
	public ArrayList<String> getAllVariables(){
		return sortedregulatoryvariables;
	}
	
	public void setUnconstrainedGenesThatWereKnockouted(ArrayList<String> unconstrainedgenesknockout) {
		this.unconstrainedgenesknockouted=unconstrainedgenesknockout;
	}
	
	
	public void forceInitializationTranscriptionalFactorsAsTrue() {
		this.forceTFinitStateastrue=true;
	}
	
	/**
	 * Return the index of the Metabolic Reaction that is related to variable. 
	 * Or return -1 if do not exist any interaction to a metabolic reaction.  
	 */
	public Integer getIndexMetabolicReactionForVariable(String variable){
		int index = -1;
		
		if(commonMetabolicReactionIDAndRegulatoryReactionID.containsKey(variable))
			index = commonRegulatoryVariableAndMetabolicVariable.get(variable);
		else if(commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID.containsKey(variable) && commonRegulatoryVariableAndMetabolicVariable.containsKey(variable))
			index= commonRegulatoryVariableAndMetabolicVariable.get(variable);
		
		return index;
	}
	
	public void setRegulatoryVariablesToTrueState(ArrayList<String> truevariables){
		
		for (int i = 0; i < truevariables.size(); i++) {
			initialregulatorystate.initializeVariableBooleanState(truevariables.get(i), true);
			
		}
	}
	
	
	
	public InitialRegulatoryState getInitialRegulatoryState() {
		return initialregulatorystate;
	}

	public RegulatoryModelType getTypemodel() {
		return typemodel;
	}
	
	public Integer getNumberOfMetabolitesInBothModels(){
		return this.commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID.size();
	}
	
	public IndexedHashMap<String, Integer> getMetaboliteVariablesPresentInBothModels() {
		return commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID;
	}
	
	public Integer getNumberOfReactionsInBothModels(){
		return this.commonMetabolicReactionIDAndRegulatoryReactionID.size();
	}

	public IndexedHashMap<String, Integer> getReactionVariablesPresentInBothModels() {
		return commonMetabolicReactionIDAndRegulatoryReactionID;
	}
	
	public Boolean isUserVarible(String variable){
		return onlyVariablesInRegulatoryNetwork.containsKey(variable);
	}
	
	public ArrayList<String> getUserVariablesList(){
		ArrayList<String> variablesID = new ArrayList<String>();
		for (int i = 0; i < onlyVariablesInRegulatoryNetwork.size(); i++) {
			variablesID.add(onlyVariablesInRegulatoryNetwork.getKeyAt(i));
		}
		return variablesID;
	}
	
	public IndexedHashMap<String, Boolean> getTFsVariablesState(){
		IndexedHashMap<String, Boolean> res = new IndexedHashMap<String, Boolean>();
		for (int i = 0; i < variablesType.size(); i++) {
			String id=variablesType.getKeyAt(i);
			RegulatoryModelComponent type=variablesType.get(id);
			if(type.equals(RegulatoryModelComponent.TRANS_FACTOR_ID)){
				if(forceTFinitStateastrue)
					res.put(id, true);
				else {
					Boolean state=initialregulatorystate.getBooleanStateForComponentID(id);
					if(state!=null)
						res.put(id, state);
				}
			}

		}
		return res;
	}
	
	
	public IndexedHashMap<String, Boolean> getUserVariablesState(){
		  
		  IndexedHashMap<String, Boolean> res = new IndexedHashMap<String, Boolean>();
		  for (int i = 0; i < onlyVariablesInRegulatoryNetwork.size(); i++) {
			String varid=onlyVariablesInRegulatoryNetwork.getKeyAt(i);
			Boolean state=initialregulatorystate.getBooleanStateForComponentID(varid);
			if(state!=null)
				res.put(varid, state);
		  }

		   return res;
	  }
	
	
	
	
	public ArrayList<String> getSortedRegulatoryGenes() {
		return sortedregulatorygenes;
	}


	public ArrayList<String> getUnconstrainedGenes() {
		return unconstrainedgenes;
	}

	public IndexedHashMap<String, Integer> getOnlyVariablesInRegulatoryNetwork() {
		return onlyVariablesInRegulatoryNetwork;
	}

	

	public IndexedHashMap<String, Integer> getRegulatoryVariableIndexes() {
		return allIntegratedModelVariables;
	}

	public IndexedHashMap<String, RegulatoryModelComponent> getVariablesType() {
		return variablesType;
	}

	public HashMap<String, Integer> getCommonIntegratedVariableAndMetabolicVariable() {
		return commonRegulatoryVariableAndMetabolicVariable;
	}

	public IndexedHashMap<String, Integer> getMapTFVariables() {
		return mapTFVariables;
	}

	public IndexedHashMap<String, String> getRuleid2metabolicgeneid() {
		return ruleid2metabolicgeneid;
	}

	public IndexedHashMap<String, String> getGeneid2ruleid() {
		return geneid2ruleid;
	}
	
	public IndexedHashMap<String, String> getRuleid2Geneid() {
		if(ruleid2geneid==null) {
			if(geneid2ruleid==null)
				return null;
			else
				ruleid2geneid=new IndexedHashMap<>();
			
			for (String id : geneid2ruleid.keySet()) {
				ruleid2geneid.put(geneid2ruleid.get(id), id);
			}
			
		}
		return ruleid2geneid;
	}
	
	public IndexedHashMap<String, Boolean> getInitialStateofAllGenes(){
		
		IndexedHashMap<String, Boolean> state=new IndexedHashMap<>();
		
		for (int i = 0; i < sortedregulatorygenes.size(); i++) {
			String id=sortedregulatorygenes.get(i);
			boolean genestate=initialregulatorystate.getBooleanStateForComponentID(id);
			
			if(!geneid2ruleid.containsKey(id) && geneid2ruleid.containsValue(id))
				id=getRuleid2Geneid().get(id);	
			state.put(id, genestate);
		}
		
		for (int i = 0; i < unconstrainedgenes.size(); i++) {
			String id=unconstrainedgenes.get(i);
			
			if(!state.containsKey(id)) {
				if(unconstrainedgenesknockouted!=null && unconstrainedgenesknockouted.contains(id))
					state.put(id, false);
				else
					state.put(id, true);
				
			}
		}
		
		return state;
	}
	
	public ArrayList<String> getKnockoutGenes(){
		Set<String> genesout=initialregulatorystate.getKnockoutgenes();
		ArrayList<String> knockoutgenes=null;
		if(genesout!=null) {
			knockoutgenes=new ArrayList<>(genesout);
		}
		
		if(unconstrainedgenesknockouted!=null) {
			if(knockoutgenes==null)
				knockoutgenes=new ArrayList<>();
			for (String id : unconstrainedgenesknockouted) {
				if(!knockoutgenes.contains(id))
					knockoutgenes.add(id);
			}
		}
		
		return knockoutgenes;
	}
	
	
	

	@SuppressWarnings("unchecked")
	public IntegratedNetworkInitialStateContainer copy() throws Exception{
		return new IntegratedNetworkInitialStateContainer((ArrayList<String>)MTUCollectionsUtils.deepCloneObject(sortedregulatorygenes),
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
	
	
	
	@Override
	public String toString() {

		
		StringBuilder str=new StringBuilder();
		str.append("sortedregulatorygenes: "+sortedregulatorygenes+"\n");
		str.append("sortedregulatoryvariables: "+sortedregulatoryvariables+"\n");
		str.append("unconstrainedgenes: "+unconstrainedgenes+"\n");
		str.append("onlyVariablesInRegulatoryNetwork: "+onlyVariablesInRegulatoryNetwork+"\n");
		str.append("commonMetabolicReactionIDAndRegulatoryReactionID: "+commonMetabolicReactionIDAndRegulatoryReactionID+"\n");
		str.append("commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID: "+commonMetabolicMetaboliteIDAndRegulatoryMetaboliteID+"\n");
		str.append("allIntegratedModelVariables: "+allIntegratedModelVariables+"\n");
		str.append("variablesType: "+variablesType+"\n");
		str.append("commonRegulatoryVariableAndMetabolicVariable: "+commonRegulatoryVariableAndMetabolicVariable+"\n");
		str.append("mapTFVariables: "+mapTFVariables+"\n");
		str.append("ruleid2metabolicgeneid: "+ruleid2metabolicgeneid+"\n");
		str.append("geneid2ruleid: "+geneid2ruleid+"\n");
		str.append("ruleid2geneid: "+ruleid2geneid+"\n");
		str.append("unconstrainedgenesknockouted: "+unconstrainedgenesknockouted+"\n");
		str.append("\n--------------- initialregulatorystate ------------\n");
		str.append(initialregulatorystate.toString());
		str.append("\n\n");
		
		return str.toString();
		
	}

}
