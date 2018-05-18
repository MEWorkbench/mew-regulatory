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
package pt.uminho.ceb.biosystems.reg4optfluxcore.container;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashSet;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components.IRegulatoryNetworkReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.OptfluxRegulatoryModel;

public class RegulatoryContainer implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static boolean debug = false;
	
	protected String name;
	protected String organism;
	protected String notes;
	protected boolean booleanformalism=true;
	protected IndexedHashMap<String, Regulator> regulatorygenes;
	protected IndexedHashMap<String, RegulatoryVariable> variables;
	//protected IndexedHashMap<String, RegulatoryModelComponent> variablesType;
	protected IndexedHashMap<String, RegulatoryRule> regulatorygeneRules;
	protected IndexedHashMap<String, String> geneid2ruleid;
	protected LinkedHashSet<String> unconstrainedGenes=null;
	protected IRODDRegulatoryModel logicalmodel=null;
	protected boolean genesidlinkbyruleid=false;
	
	
	
	
	/*
	 * Create an empty regulatory model
	 */
	public RegulatoryContainer (){
		this.name="Regulatory Model";
		this.organism="not defined";
		this.regulatorygenes = new IndexedHashMap<String, Regulator>();
		this.variables = new IndexedHashMap<String, RegulatoryVariable>();
		//this.variablesType=new IndexedHashMap<String, RegulatoryModelComponent>();
		this.regulatorygeneRules=new IndexedHashMap<String, RegulatoryRule>();
		this.geneid2ruleid=new IndexedHashMap<String, String>();
		
		
	}
	
	
	public RegulatoryContainer(IRegulatoryNetworkReader reader) throws Exception{
		
		if(!reader.isModelLoaded())
			reader.loadModel();
		this.name = reader.getModelName();
		this.organism = reader.getOrganismName();
		this.notes = reader.getNotes();
		this.booleanformalism=reader.isStrictBooleanFormalism();
		this.regulatorygenes = reader.getRegulatoryGenes();
		
		if(booleanformalism){
		    this.variables = reader.getRegulatoryVariables();
		   // this.variablesType=reader.getRegulatoryVariableType();
		    this.regulatorygeneRules = reader.getRegulatoryGeneRules();
		    this.geneid2ruleid=reader.getGeneID2RuleID();
		    this.logicalmodel=reader.getROBDDModelFormat();
		    this.unconstrainedGenes=reader.getUnconstrainedGenes();
		    this.genesidlinkbyruleid=reader.genesInRuleLinkByRuleID();
		}
		else{
			if(reader.getROBDDModelFormat()==null)
				throw new IOException("Invalid Multivalued Logical model");
			else{
				this.logicalmodel=reader.getROBDDModelFormat();
			}
		}
	}
	
	
	public RegulatoryContainer(RegulatoryContainer container){
		
		this.name 				= container.name;
		this.organism 			= container.organism;
		this.notes 				= container.notes;
		this.booleanformalism= container.isStrictlybooleanmodel();
		
		this.regulatorygenes = new IndexedHashMap<String, Regulator>();
	       for (String id : container.getRegulatoryGenes().keySet()) {
			    this.regulatorygenes.put(id,(Regulator) container.getRegulatoryGene(id).copy());
		    }
		
		if(booleanformalism){
			
	       this.variables = new IndexedHashMap<String, RegulatoryVariable>();
	       for (String id : container.getRegulatoryVariables().keySet()) {
			     this.variables.put(id, container.getSingleVariable(id));
		  }
	       
	      /* this.variablesType=new IndexedHashMap<String,RegulatoryModelComponent>();
	       for (int i = 0; i < container.getVariablesType().size(); i++) {
	    	   String id=container.getVariablesType().getKeyAt(i);
	    	   this.variablesType.put(id, container.getVariablesType().get(id));
		  }*/
	    
	      this.regulatorygeneRules = new IndexedHashMap<String, RegulatoryRule>();
	      for (String id : container.getRegulatorygeneRules().keySet()) {
	    	  this.regulatorygeneRules.put(id, container.getRegulatoryRulebyID(id));
		  }
		
	       this.geneid2ruleid = new IndexedHashMap<String, String>();
	       for (String id : container.getGeneId2RuleID().keySet()) {
	    	   this.geneid2ruleid.put(id, container.getRuleIdFromGeneId(id));
		   }
	       
	       if(container.getUnconstrainedGenes()!=null){
	    	   this.unconstrainedGenes=new LinkedHashSet<>();
	    	   for (String geneid : container.getUnconstrainedGenes()) {
				   unconstrainedGenes.add(geneid);
			   }
	       }
	       
	       this.genesidlinkbyruleid=container.isGenesidlinkbyruleid();

		}
		if(container.getLogicalmodel()!=null)
			try {
				this.logicalmodel=(IRODDRegulatoryModel) container.getLogicalmodel().copy();
			} catch (Exception e) {
				e.printStackTrace();
			}

	}
	

	
	
	public String getModelName() {
		return name;
	}

	public void setModelName(String name) {
		this.name = name;
	}

	public String getOrganismName() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}


	public IndexedHashMap<String, Regulator> getRegulatoryGenes(){
		return regulatorygenes;
	}
	
	
	public Regulator getRegulatoryGene(String id){
		return regulatorygenes.get(id);
	}
	
	public IndexedHashMap<String, RegulatoryVariable> getRegulatoryVariables(){
		return this.variables;
	}
	
	
	
	
	/*public IndexedHashMap<String, RegulatoryModelComponent> getVariablesType() {
		return variablesType;
	}*/


	public RegulatoryVariable getSingleVariable(String id){
		return variables.get(id);
	}
	
    public RegulatoryRule getRegulatoryRulebyID(String id){
    	return regulatorygeneRules.get(id);
    }
	
	
	public IndexedHashMap<String, RegulatoryRule> getRegulatorygeneRules() {
		return regulatorygeneRules;
	}
	
	
    

	public LinkedHashSet<String> getUnconstrainedGenes() {
		return unconstrainedGenes;
	}


	public boolean isStrictlybooleanmodel() {
		return booleanformalism;
	}


	public IndexedHashMap<String, String> getGeneId2RuleID() {
		return geneid2ruleid;
	}
	
	public String getRuleIdFromGeneId(String id){
		return geneid2ruleid.get(id);
	}

	public IRODDRegulatoryModel getLogicalmodel() {
		return logicalmodel;
	}

	

	public boolean isGenesidlinkbyruleid() {
		return genesidlinkbyruleid;
	}


	@Override
	public RegulatoryContainer clone() {
		return new RegulatoryContainer(this);
	}
	
	
	public static IOptfluxRegulatoryModel loadRegulatoryModelOptFluxFormat(IRegulatoryNetworkReader reader) throws Exception {
		RegulatoryContainer cont=new RegulatoryContainer(reader);
		return new OptfluxRegulatoryModel("Generic", cont.getRegulatoryGenes(),cont.getRegulatorygeneRules(), cont.getRegulatoryVariables(), cont.geneid2ruleid, cont.getUnconstrainedGenes(), cont.genesidlinkbyruleid);
		//return new OptfluxRegulatoryModel("Generic", cont.getRegulatoryGenes(),cont.getRegulatorygeneRules(), cont.getRegulatoryVariables(), cont.geneid2ruleid, cont.getVariablesType(), cont.getUnconstrainedGenes(), cont.genesidlinkbyruleid);
	}

}
