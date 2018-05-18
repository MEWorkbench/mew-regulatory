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
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.columnbase.AbstractColumnBaseRegulatoryNetworkReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;

public class RuleProcessor {
	
	private HashSet<String> detectedreactions = null;
	protected IndexedHashMap<String, String> mapofknownreactionsids;
	private HashSet<String> detectedmetabolies=null;
	private HashSet<String> extractedunknownvars =null;
	private IndexedHashMap<String,RegulatoryVariable> regulatoryVariables =null;
	private IndexedHashMap<String, RegulatoryModelComponent> variabletype;
	private String rule=null;
	private ArrayList<String> metabolicreactionidentifiers;
	private ArrayList<String> possibleTFindentifiers;
	
	//private HashSet<String> knownasTF=null;
	//private HashSet<String> knownasCond=null;
	
	public RuleProcessor (String inrule,ArrayList<String> metabolicreactionidentifiers, ArrayList<String> possibleTFindentifiers,IndexedHashMap<String, RegulatoryModelComponent> variabletype){
		this.rule=inrule;
		this.variabletype=variabletype;
		this.metabolicreactionidentifiers=metabolicreactionidentifiers;
		this.possibleTFindentifiers=possibleTFindentifiers;
		processrule();
	}
	
	
	private void processrule(){
		findknownreactionsinrule();
		findknownmetabolitesinrule();
		extractRemainVariablesAndTFs();
		setupExtractedVariables();

	}
	
	   
	
	private void findknownreactionsinrule(){
		
        String pattern="(\\w+([-,_]\\w+)*(\\(\\w\\))*[<>](-)*\\d+(\\.\\d+)*)";
   		
   		Pattern patt = Pattern.compile(pattern);
   		detectedreactions = new HashSet<String>();
   		mapofknownreactionsids=new IndexedHashMap<>();
   		Matcher match = patt.matcher(rule);
   		while(match.find()){
   			String id=match.group();
   			if(id!=null && !id.isEmpty()){
   				RegulatoryVariable var=RegulatoryVariable.setupVariable(match.group(), RegulatoryModelComponent.REACTION_ID);
   				
   				
   				if(metabolicreactionidentifiers!=null) {
   					String simplename=var.getSimpleName();
   					if(metabolicreactionidentifiers.contains(simplename))
   						detectedreactions.add(var.getId());
   				}
   				else if (canBeAdded(id, RegulatoryModelComponent.REACTION_ID)){
   					detectedreactions.add(var.getId());
   				}
   				mapofknownreactionsids.put(id, var.getId());
   			}
		 }
	  }
	
	
	private void findknownmetabolitesinrule(){
		
		//String pattern="(M_\\w+(-*\\w+)*_e)|(\\w+(-*\\w+)*\\(e\\))[\\s*\\)]";
		String pattern="(M_\\w+(-*\\w+)*_e)";
   		
   		Pattern patt = Pattern.compile(pattern);
   		detectedmetabolies = new HashSet<String>();
   		Matcher match = patt.matcher(rule);
   		while(match.find()){
   			String foundrule1=match.group(1);
   			if(foundrule1!=null){
   				if(canBeAdded(foundrule1, RegulatoryModelComponent.METABOLITE_ID)){
   					RegulatoryVariable var=RegulatoryVariable.setupVariable(foundrule1, RegulatoryModelComponent.METABOLITE_ID);
   					detectedmetabolies.add(var.getId());
   				}
   			}

   		 }
	 }
		
	
	
	
	private void extractRemainVariablesAndTFs(){
		String pattern="(\\w+(([-,_]\\w+)*(\\(\\w\\))*[<>](-)*\\d+(\\.\\d+)*)*)";
		Pattern patt = Pattern.compile(pattern);
		this.extractedunknownvars = new HashSet<>();
		Matcher match = patt.matcher(rule);
	    while(match.find()){
		     String oc = match.group();
		    
		    if(!oc.matches("or|OR|and|AND|not|NOT")){
		    	 oc=RegulatoryVariable.setupVariable(oc, null).getId();
			     if(!this.detectedreactions.contains(oc) && !this.detectedmetabolies.contains(oc))
			    	 this.extractedunknownvars.add(oc); 	  
		    }
		 }
	   }
	
	
	
	private boolean canBeAdded(String id, RegulatoryModelComponent category){
		String alternativeid=RegulatoryVariable.setupVariable(id, category).getId();
		
		if(variabletype!=null){
			if((variabletype.containsKey(id) && variabletype.get(id).equals(category) || 
				variabletype.containsKey(alternativeid) && variabletype.get(alternativeid).equals(category)))
				return true;
			else if(variabletype.containsKey(id) && !variabletype.get(id).equals(category))
				return false;
			else if(variabletype.containsKey(alternativeid) && !variabletype.get(alternativeid).equals(category))
				return false;
			else
				return true;
		}
		return true;
	}
	
     
	
   		
		// takes too long finding matches , check in future
     /*   String pattern2="(\\w+(-*\\w+)*\\(e\\))(\\s+|\\))";
   		
   		Pattern patt2 = Pattern.compile(pattern2);
   		//detectedmetabolies = new HashSet<String>();
   		Matcher match2 = patt2.matcher(rule);
   
   		while(match2.find()){
   			//String foundrule1=match.group(1);
   			String foundrule2=match2.group(1);

   			if(foundrule2!=null){
   				detectedmetabolies.add(foundrule2);
   				System.out.println(foundrule2);
   			}
   			else if(foundrule2!=null){
   				detectedmetabolies.add(foundrule2);
   				System.out.println(foundrule2);
   			}
   		 }*/
   		

	
	
	

	
	private void setupExtractedVariables(){
		regulatoryVariables= new IndexedHashMap<String, RegulatoryVariable>();
		
		if(detectedreactions.size()>0)
			for (String knowncond : detectedreactions) {
				RegulatoryVariable var = RegulatoryVariable.setupVariable(knowncond, RegulatoryModelComponent.REACTION_ID);
				if(metabolicreactionidentifiers!=null && !metabolicreactionidentifiers.contains(var.getSimpleName()))
					var.setComponentType(RegulatoryModelComponent.ENV_CONDITION_ID);
				regulatoryVariables.put(var.getId(), var);
			}
		
		if(detectedmetabolies.size()>0)
		   for (String knownmetab : detectedmetabolies) {
			  RegulatoryVariable var = RegulatoryVariable.setupVariable(knownmetab, RegulatoryModelComponent.METABOLITE_ID);
			  regulatoryVariables.put(var.getId(), var);
		   }
		
		
		if(extractedunknownvars!=null && extractedunknownvars.size()>0){
			
			
			for (String varid : extractedunknownvars) {
				
				RegulatoryModelComponent type=null;
				RegulatoryVariable tmpvar=RegulatoryVariable.setupVariable(varid, null);
				String tmpid=tmpvar.getId();
				
				ArrayList<String> currentvars=new ArrayList<>(regulatoryVariables.keySet());
				
				if(possibleTFindentifiers!=null && possibleTFindentifiers.contains(tmpid))
					type=RegulatoryModelComponent.TRANS_FACTOR_ID;
				
				
				if(variabletype!=null && type==null){
					if(!variabletype.containsKey(tmpid))
						tmpid=tmpvar.getName();
					
					if(variabletype.containsKey(tmpid))
						type=variabletype.get(tmpid);
				}
				
				if(type==null)
					type=RegulatoryModelComponent.ENV_CONDITION_ID;
					
				
				RegulatoryVariable var=RegulatoryVariable.setupVariable(varid, type);
				if(!regulatoryVariables.containsKey(var.getId())) {
					regulatoryVariables.put(var.getId(), var);
				}
			}
		}
		

	}


	public IndexedHashMap<String, RegulatoryVariable> getRegulatoryVariables() {
		return regulatoryVariables;
	}
	
	
	public String getRuleWithIdentifiersNormalized(){
		if(mapofknownreactionsids.size()>0)
			return AbstractColumnBaseRegulatoryNetworkReader.changeRuleReactionsIdentifiers(rule, new HashSet<>(mapofknownreactionsids.keySet()), mapofknownreactionsids);
		else
			return rule;
	}
	
	/*public static String getModifiedRuleIdentifiers(String inrule, IndexedHashMap<String, RegulatoryModelComponent> variabletype){
		
	}*/
	

}
