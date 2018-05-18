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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import pt.ornrocha.ioutils.readers.MTUReadUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components.IRegulatoryNetworkReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;

public abstract class AbstractRegulatoryNetworkReader implements IRegulatoryNetworkReader, Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String modelfile;
	
	protected String modelName;
	protected String organismName;
	protected String notes;
	protected int version;
	
	protected HashSet<String> knownmetabolitesids;
	protected HashSet<String> knownenvironmentalconditionsids;
	protected HashSet<String> knownreactionids;
	protected HashSet<String> knowntfids;
	protected HashSet<String> knowngeneids;
	
	protected boolean verifiedvariables=false;
	protected boolean auxvarsfilesused=false;
	protected boolean definedknownvariables=false;
	protected IndexedHashMap<String,Regulator> regulatoryGenes;
	protected IndexedHashMap<String,RegulatoryRule> regulatoryGeneRules;
	protected IndexedHashMap<String,RegulatoryVariable> regulatoryVariables;
	//protected IndexedHashMap<String, RegulatoryModelComponent> variabletypemap;
	protected LinkedHashSet<String> unconstrainedgenes;
	protected IndexedHashMap<String,String> GeneID2RuleID;
	protected boolean genesidlinkbyruleid=false;
	
	
	public static String TAGENVIRONMENTALCONDITIONS="@environmentalconditions";
	public static String TAGTRANSCRIPTIONALFACTORS="@tfs";
	public static String TAGREACTIONS="@reactions";
	public static String TAGMETABOLITES="@metabolites";
	public static String TAGGENES="@genes";
	public static String TAGSTARTRULES="@start_rules";
	public static String TAGENDRULES="@end_rules";
	
	public AbstractRegulatoryNetworkReader(String filepath){
		this.modelfile=filepath;
	}
	
	public AbstractRegulatoryNetworkReader(String filepath, String knownvariablesfile) throws Exception{
		this.modelfile=filepath;
		if(knownvariablesfile!=null){
			auxvarsfilesused=true;
			readKnownVariablesFile(knownvariablesfile);
		}
	}
	
	
	public AbstractRegulatoryNetworkReader(String filepath,
			String knownmetabolitesfile, 
			String knownenvironmentalconditionsfile,
			String knownreactionsfile,
			String knowntfsfile,
			String knowngenesfiles) throws Exception{
		this.modelfile=filepath;
		setKnownMetabolitesIDsFile(knownmetabolitesfile);
		setKnownEnvConditionsIDsFile(knownenvironmentalconditionsfile);
		setKnownReactionIDsFile(knownreactionsfile);
		setKnownTFIDsFile(knowntfsfile);
		setKnownGeneIDsFile(knowngenesfiles);
	
	}
	
	
	public void setKnownMetabolitesIDsFile(String file) throws IOException{
		if(file!=null)
			this.knownmetabolitesids=new HashSet<>((ArrayList<String>) MTUReadUtils.readFileLines(file));
	}
	
	public void setKnownEnvConditionsIDsFile(String file) throws IOException{
		if(file!=null)
			this.knownenvironmentalconditionsids=new HashSet<>((ArrayList<String>) MTUReadUtils.readFileLines(file));
	}
	
	public void setKnownReactionIDsFile(String file) throws IOException{
		if(file!=null)
			this.knownreactionids=new HashSet<>((ArrayList<String>) MTUReadUtils.readFileLines(file));
	}
	
	public void setKnownTFIDsFile(String file) throws IOException{
		if(file!=null)
			this.knowntfids=new HashSet<>((ArrayList<String>) MTUReadUtils.readFileLines(file));
	}
	
	public void setKnownGeneIDsFile(String file) throws IOException{
		if(file!=null)
			this.knowngeneids=new HashSet<>((ArrayList<String>) MTUReadUtils.readFileLines(file));
	}
	
	
	public void setKnownMetabolitesIDs(ArrayList<String> ids){
			this.knownmetabolitesids=new HashSet<>(ids);
	}
	
	public void setKnownEnvConditionsIDs(ArrayList<String> ids){
			this.knownenvironmentalconditionsids=new HashSet<>(ids);
	}
	
	public void setKnownReactionIDs(ArrayList<String> ids){
			this.knownreactionids=new HashSet<>(ids);
	}
	
	public void setKnownTFIDs(ArrayList<String> ids){
			this.knowntfids=new HashSet<>(ids);
	}
	
	public void setKnownGeneIDs(ArrayList<String> ids){
			this.knowngeneids=new HashSet<>(ids);
	}
	
	
	public void setKnownVariblesFile(String auxiliarFile) throws Exception {
		readKnownVariablesFile(auxiliarFile);
		verifiedvariables=true;
	}
	
	public boolean isVerifiedvariables() {
		return verifiedvariables;
	}
	
	
	
	public void updateRegulatoryVariablesType(IndexedHashMap<String, RegulatoryModelComponent> typevariables){
		
		for (int i = 0; i < typevariables.size(); i++) {
			String id=typevariables.getKeyAt(i);
			RegulatoryModelComponent type=typevariables.get(id);
			
			if(regulatoryVariables.containsKey(id)){
				RegulatoryModelComponent previoustype=regulatoryVariables.get(id).getType();
				
				if(type!=previoustype)
					regulatoryVariables.get(id).setComponentType(type);
				
			}
		}
	}
	
	
	
	
	
	protected void readKnownVariablesFile(String filepath)throws IOException{
		
		
		 FileReader r=new FileReader(filepath);
		 BufferedReader br = null;
		 
		 
		 try {
			 br=new BufferedReader(r);
			 String currentLine;
			 int step=0;
			 
			 while ((currentLine = br.readLine()) != null) {
				 
				 if(currentLine.toLowerCase().contains(TAGENVIRONMENTALCONDITIONS)){
			     		this.knownenvironmentalconditionsids=new HashSet<>();
			     		currentLine = br.readLine();
			     		step=1;	
			     }
				 else if(currentLine.toLowerCase().contains(TAGTRANSCRIPTIONALFACTORS)){
				 		this.knowntfids=new HashSet<>();
				 		currentLine = br.readLine();
				 		step=2;
				 }
				 else if(currentLine.toLowerCase().contains(TAGREACTIONS)){
				 		this.knownreactionids=new HashSet<>();
				 		currentLine = br.readLine();
				 		step=3;
				 }
				 else if(currentLine.toLowerCase().contains(TAGMETABOLITES)){
				 		this.knownmetabolitesids=new HashSet<>();
				 		currentLine = br.readLine();
				 		step=4;
				 }
				 else if(currentLine.toLowerCase().contains(TAGGENES)){
				 		this.knowngeneids=new HashSet<>();
				 		currentLine = br.readLine();
				 		step=5;
				 }
				 else if(currentLine.isEmpty() || currentLine.toLowerCase().contains(TAGSTARTRULES)){
					 step=0;
					 continue;
				 }
				
				 
				 switch (step) {
					 case 1:{
						 RegulatoryVariable var = RegulatoryVariable.setupVariable(currentLine.trim(), RegulatoryModelComponent.REACTION_ID);
				     	 currentLine=var.getId();
						 knownenvironmentalconditionsids.add(currentLine);
						 break;
					 	}
					 case 2:{
					 	 knowntfids.add(currentLine.trim());
					 	 break;
					   }
					 case 3:{
					 	 knownreactionids.add(currentLine.trim());
					 	 break;
					   }
					 case 4:{
					 	 knownmetabolitesids.add(currentLine.trim());
					 	 break;
					   }
					 case 5:{
					 	 knowngeneids.add(currentLine.trim());
					 	 break;
					   }
					 default:
						 break;
					 }
				 }
			
			 if(knownenvironmentalconditionsids!=null ||
					                 knowntfids!=null ||
					           knownreactionids!=null ||
					        knownmetabolitesids!=null ||
					               knowngeneids!=null)
				 definedknownvariables=true;
				 
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
				if(br!=null)
					br.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		 
		 if(knownmetabolitesids!=null)
			 LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Metabolites identified:", knownmetabolitesids);
		 if(knownenvironmentalconditionsids!=null)
			 LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Environmental conditions identified:", knownenvironmentalconditionsids);
		 if(knownreactionids!=null)
			 LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Reactions identified:", knownreactionids);
		 if(knowntfids!=null)
			 LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Tfs identified:", knowntfids);
		 if(knowngeneids!=null)
			 LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Genes identified:", knowngeneids);
	}
	
	
	public String getModelName() {
		return modelName;
	}

	
	public String getOrganismName() {
		return organismName;
	}
	

	public String getNotes() {
		return notes;
	}
	

	public Integer getVersion() {
		return version;
	}
	
    
	public IndexedHashMap<String, Regulator> getRegulatoryGenes() {
		return regulatoryGenes;
	}
	
	public IndexedHashMap<String, RegulatoryVariable> getRegulatoryVariables(){
		return regulatoryVariables;
	}
	
	
	public IndexedHashMap<String, RegulatoryRule> getRegulatoryGeneRules() {
		return regulatoryGeneRules;
	}
	
	public IndexedHashMap<String, String> getGeneID2RuleID() {
		return GeneID2RuleID;
	}


	public IndexedHashMap<String, RegulatoryModelComponent> getRegulatoryVariableType() {
		IndexedHashMap<String, RegulatoryModelComponent> components=new IndexedHashMap<>();
		
		if(regulatoryVariables!=null)
		for (String varid : regulatoryVariables.keySet()) {
			components.put(varid, regulatoryVariables.get(varid).getType());
		}
		return components;
	}
	

	public LinkedHashSet<String> getUnconstrainedGenes() {
		return unconstrainedgenes;
	}
	
	public boolean genesInRuleLinkByRuleID(){
		return genesidlinkbyruleid;
	}

}
