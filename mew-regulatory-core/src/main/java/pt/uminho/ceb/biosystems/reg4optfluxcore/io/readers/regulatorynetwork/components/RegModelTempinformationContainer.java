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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;

public class RegModelTempinformationContainer {
	

	protected ArrayList<String> ruleslinkbyidentifiers = new ArrayList<String>();
	protected ArrayList<String> geneNames = new ArrayList<String>();
	protected ArrayList<String> identifierthatlinktogpr = new ArrayList<String>();
	protected IndexedHashMap<Integer,String> RegulatoryRulesToTreat = new IndexedHashMap<>();
	protected IndexedHashMap<String, String> MapRule2LinkerIdentifier = new IndexedHashMap<String, String>();
	protected IndexedHashMap<Integer, HashSet<String>> mapofdetectedreactionsinrule=new IndexedHashMap<>();
	protected IndexedHashMap<String, String> GPRLinkToRuleByIdentifier = new IndexedHashMap<String, String>();
	protected IndexedHashMap<String, String> GeneidMapToGeneName = null;
	

	protected String delimiter =";";
	protected HashSet<String> unknownBnumbersFound = null;
	protected HashSet<String> ListofdetectedReactionsIDs = null;
	protected HashSet<String> ListofdetectedMetabolites=new HashSet<String>();
	protected HashSet<String> Listofallvariablesdefinedinrules =new HashSet<String>();
	protected HashSet<String> knownTFsandGenesInRules = new HashSet<String>();
	protected HashSet<String> Unkownvariablestype =null;
	protected RegModelInfoContainer modelinfocontainer=null;
	protected ArrayList<String> unconstrainedgenes=new ArrayList<>();
	
	protected ArrayList<String> modelLines=null;
	
	public RegModelTempinformationContainer (){

	}

	
	public RegModelTempinformationContainer (ArrayList<String> regulatorymodellines, RegModelInfoContainer modelinfocontainer){
        this.modelLines=regulatorymodellines;
        this.modelinfocontainer=modelinfocontainer;
        if(modelinfocontainer.getDelimiter()!=null)
        	this.delimiter=modelinfocontainer.getDelimiter();
        try {
			getdefinedLabelsInRegModel();
			performcheckRulesOperations();
	
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	
	

	public ArrayList<String> getListOfIdentifiersThatLinkRules() {
		return ruleslinkbyidentifiers;
	}
   

	public void setSingleTransFactorsNameAssocitoGene(String transFactorName) {
		if(this.ruleslinkbyidentifiers==null){
			this.ruleslinkbyidentifiers = new ArrayList<String>();
		}
		this.ruleslinkbyidentifiers.add(transFactorName);
	}


	public ArrayList<String> getRegulatoryGeneIDs() {
		return identifierthatlinktogpr;
	}



	public void setRegulatoryGeneIDs(String regulatoryGeneID) {
		if(this.identifierthatlinktogpr==null){
			this.identifierthatlinktogpr = new ArrayList<String>();
		}
		this.identifierthatlinktogpr.add(regulatoryGeneID);
	}

	public HashSet<String> getUnknownBnumbersFound() {
		return unknownBnumbersFound;
	}
	
	public boolean haveUnknownBnumbersInRules(){
		if(this.unknownBnumbersFound !=null)
			return true;
		else
			return false;
	}
	
	public boolean haveUnknownVarsTypeInRules(){
		if(this.Unkownvariablestype !=null)
			return true;
		else
			return false;
	}
	
	

	public HashSet<String> getKnownTfsinRules() {
		return knownTFsandGenesInRules;
	}

	public HashSet<String> getUnkownvariablestype() {
		return Unkownvariablestype;
	}

	public HashSet<String> getListofdetectedExchangeMetabolites() {
		return ListofdetectedMetabolites;
	}
	
	
	public HashSet<String> getListofdetectedReactions() {
		return ListofdetectedReactionsIDs;
	}
	
	public HashSet<String> getListofALLdetectedVariables() {
		return Listofallvariablesdefinedinrules;
	}
	
	public IndexedHashMap<String, String> getMapLinkerIdentifier2Rule(){
		return this.MapRule2LinkerIdentifier;
	}
	
	public IndexedHashMap<String, String> getMapGPRLink2RuleByIdentifier(){
		return this.GPRLinkToRuleByIdentifier;
	}
    
	public IndexedHashMap<String, String> getGeneidMapToGeneName(){
		return this.GeneidMapToGeneName;
	}
	
	
	
	public IndexedHashMap<Integer, HashSet<String>> getMapofdetectedreactionsinrule() {
		return mapofdetectedreactionsinrule;
	}
	
	
	public ArrayList<String> getUnconstrainedgenes() {
		return unconstrainedgenes;
	}


/*	protected void readModelFile(File regulatoryfile) throws IOException{
		this.modelLines = (ArrayList<String>) FileUtils.readLines(regulatoryfile, "utf-8");
	}*/
	
	
	

	protected void getdefinedLabelsInRegModel() throws IOException{
		
		int GPRlinkbycolumn = this.modelinfocontainer.getGPRLinkColumn();
		int Ruleslinkbycolumn = this.modelinfocontainer.getRuleLinkColumn();
		int GeneNamescolumn = this.modelinfocontainer.getGeneNamesColumn();
		int rulescolumn=this.modelinfocontainer.getRulescolumn();
		
		System.out.println(modelinfocontainer);
		//System.out.println(GPRlinkbycolumn+" "+Ruleslinkbycolumn+" "+GeneNamescolumn+" "+rulescolumn);
	
        int lineindex=0;
	    for (String line : modelLines) {
           
			String[] dataline = line.split(delimiter);
			
			
			String mainidlinker=null;
			
			
			if(dataline.length==2 && Ruleslinkbycolumn==-1){
				mainidlinker=dataline[0];
				rulescolumn=1;
			}
			else{
				mainidlinker=dataline[GPRlinkbycolumn];
			}
			this.identifierthatlinktogpr.add(mainidlinker);
			
			
			
			if(Ruleslinkbycolumn==-1 && GeneNamescolumn==-1 && rulescolumn>0){
				if(dataline.length==(rulescolumn+1)){
				   this.ruleslinkbyidentifiers.add(mainidlinker);
				   this.RegulatoryRulesToTreat.put(lineindex, dataline[rulescolumn]);
				   this.GPRLinkToRuleByIdentifier.put(mainidlinker,mainidlinker);
				  
				   String out=dataline[0]+" | "+dataline[1]+"\n";
				   LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage(out);
				
				}
				else if (dataline.length==(rulescolumn-1)){
					String out=dataline[0]+"\n";
					unconstrainedgenes.add(out);
					 LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage(out);
				}
			}
			
			else if(Ruleslinkbycolumn>-1 && rulescolumn>-1 && GPRlinkbycolumn < dataline.length && Ruleslinkbycolumn < dataline.length){
			
				 if(GeneNamescolumn>-1 && GeneNamescolumn<dataline.length){
					this.geneNames.add(dataline[GeneNamescolumn]);
					if(GeneidMapToGeneName==null)
						GeneidMapToGeneName=new IndexedHashMap<>();
						
					this.GeneidMapToGeneName.put(dataline[GPRlinkbycolumn], dataline[GeneNamescolumn]);	   
                  }
			
			     
				 this.ruleslinkbyidentifiers.add(dataline[Ruleslinkbycolumn]);
			     this.GPRLinkToRuleByIdentifier.put(dataline[GPRlinkbycolumn],dataline[Ruleslinkbycolumn] );
			
			
			     if(dataline.length >rulescolumn){
			    	 
			    	  String rule=dataline[rulescolumn];
			    	  
			    	  this.RegulatoryRulesToTreat.put(lineindex,rule);
				     // this.RegulatoryRulesToTreat.add(rule);
				      this.MapRule2LinkerIdentifier.put(dataline[GPRlinkbycolumn], rule);
				
				      if(GeneNamescolumn>-1 && GeneNamescolumn<dataline.length){
					    String out1=dataline[GPRlinkbycolumn]+"|"+dataline[GeneNamescolumn]+"|"+dataline[Ruleslinkbycolumn]+"|"+rule+"\n";
					    LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage(out1);
					   }
					  else{
						String out2=dataline[GPRlinkbycolumn]+"|"+"|"+dataline[Ruleslinkbycolumn]+"|"+rule+"\n";
						 LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage(out2);
					  }
			      }
			     else if(GPRlinkbycolumn < dataline.length){
				     this.MapRule2LinkerIdentifier.put(dataline[GPRlinkbycolumn], "");
				     unconstrainedgenes.add(dataline[GPRlinkbycolumn]);
				
				    if(GeneNamescolumn>-1 && GeneNamescolumn<dataline.length){
					   String out3=dataline[GPRlinkbycolumn]+"|"+dataline[GeneNamescolumn]+"|"+dataline[Ruleslinkbycolumn]+"\n";	
					   LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage(out3);
				    }
					else{
						String out4=dataline[GPRlinkbycolumn]+"|"+"|"+dataline[Ruleslinkbycolumn]+"\n";
						 LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage(out4);
					}
			     }
	         }
			lineindex++;
		  }
	    
	  }
	
	
	protected  void performcheckRulesOperations(){
		

		for (int i = 0; i < RegulatoryRulesToTreat.size(); i++) {
			int indexrule=RegulatoryRulesToTreat.getKeyAt(i);
			String rule=RegulatoryRulesToTreat.get(indexrule);
			
			findPossibleExchangeMetabolitesInRule(rule);
			findPossibleReactionsinrule(rule, indexrule);
			findallvariablesdefinedinrule(rule);
			
		}
		

		checkUnknownVariablesType();
		
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Metabolites Detected In Regulatory Model: ", ListofdetectedMetabolites);
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Reaction identifiers Detected In Regulatory Model: ", ListofdetectedReactionsIDs);
		LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Total Variables Detected In Regulatory Model: ", Listofallvariablesdefinedinrules);
	}
	
	
	protected void findPossibleExchangeMetabolitesInRule(String rule){
		
		    //String pattern="(M_\\w+(-*\\w+)*_e)|(\\w+(-*\\w+)*\\(e\\))[\\s*\\)]";
		    String pattern="(M_\\w+(-*\\w+)*_e)";
	   		Pattern patt = Pattern.compile(pattern);
	   		Matcher match = patt.matcher(rule);
			
	   		
	   		while(match.find()){
	   			String foundrule1=match.group(1);
	   			//String foundrule2=match.group(3);

	   			if(foundrule1!=null){
	   				ListofdetectedMetabolites.add(foundrule1);
	   			}
	   			/*else if(foundrule2!=null)
	   				ListofdetectedMetabolites.add(foundrule2);*/
	   		}
		
	 }
	


	// Find only Conditions on regulatory model
	protected void findPossibleReactionsinrule(String rule, int ruleindex){
		
		if(!rule.isEmpty()){
		   HashSet<String> detectedids=new HashSet<>();
           String pattern="(\\w+([-,_]\\w+)*(\\(\\w\\))*[<>](-)*\\d+(\\.\\d+)*)";
   		
   		   Pattern patt = Pattern.compile(pattern);
   		   Matcher match = patt.matcher(rule);
		
   		   if(ListofdetectedReactionsIDs ==null){
   			   ListofdetectedReactionsIDs = new HashSet<String>();	
   		    }
   		
   		   while(match.find()){
   			   String found=match.group();
   			   ListofdetectedReactionsIDs.add(found);
   			   detectedids.add(found);
   		   }
   		   if(detectedids.size()>0)
   		       mapofdetectedreactionsinrule.put(ruleindex, detectedids);
	    }
	}
	
	
	// find all variables defined in regulatory model including TFs, Conditions
	protected void findallvariablesdefinedinrule(String rule){
		//String pattern="(\\w+(([-,_]\\w+)*(\\(\\w\\))*[<>](-)*\\w+)*)";
		String pattern=  "(\\w+(([-,_]\\w+)*(\\(\\w\\))*[<>](-)*\\d+(\\.\\d+)*)*)";
		Pattern patt = Pattern.compile(pattern);
		Matcher match = patt.matcher(rule);
		while(match.find()){
			String oc = match.group();
			
			if(!oc.matches("or|OR|and|AND|not|NOT")){
   			    this.Listofallvariablesdefinedinrules.add(oc);
   			    if(this.ruleslinkbyidentifiers.contains(oc) || this.identifierthatlinktogpr.contains(oc))
   			    	this.knownTFsandGenesInRules.add(oc);
			}
   		}
		
	}
	
	protected void checkUnknownVariablesType(){
		
	   for (String var : this.Listofallvariablesdefinedinrules) {
		 if(!this.ListofdetectedReactionsIDs.contains(var) && !this.knownTFsandGenesInRules.contains(var) && !this.ListofdetectedMetabolites.contains(var)){
			 if(this.Unkownvariablestype ==null)
				 this.Unkownvariablestype =new HashSet<String>();
			 this.Unkownvariablestype.add(var); 
		 }
	  }
	}
	
	
	
/*	
	

	
	public void saveListofConditionstoFile(String filepath){
		
		try {
    		File file = new File(filepath);
    		FileWriter f=new FileWriter(file +".txt");
			BufferedWriter bw=new BufferedWriter(f);
			
			// write conditions
			for (String var : ListofdetectedReactionsIDs) {
				bw.write(var+"\n");
			}
			
			
			bw.close();
			f.close();
	    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/
	
	

}
