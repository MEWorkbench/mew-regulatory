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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.sbmlqual.transitions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ext.qual.FunctionTerm;
import org.sbml.jsbml.ext.qual.Output;
import org.sbml.jsbml.ext.qual.Transition;
import org.sbml.jsbml.math.parser.ParseException;

import pt.ornrocha.collections.MTUMapUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.VariableSignValue;

public class QualTransitionsReader {

	protected List<Transition> transitionslist;
	protected ArrayList<String> listofconstants;
	protected IndexedHashMap<String, String> decodedtransitionrules=new IndexedHashMap<>();
	protected LinkedHashSet<String> decodedcontantsvars=new LinkedHashSet<>();
	protected IndexedHashMap<String, RegulatoryModelComponent> mapofextractedmodelvariablestype=new IndexedHashMap<>();
	protected IndexedHashMap<String, RegulatoryModelComponent> variabletypemap;
	protected IndexedHashMap<String, String> geneproductmap;
	protected LinkedHashMap<String, String> specieid2speciename;
	protected LinkedHashMap<String, String> speciename2specieid;
	protected IndexedHashMap<String,String> GeneID2RuleID=new IndexedHashMap<>();
    protected boolean geneinrulelinkbyruleid=false;
	
	
	public QualTransitionsReader(List<Transition> transitionslist, ArrayList<String> listofconstants){
		this.transitionslist=transitionslist;
		this.listofconstants=listofconstants;
	}
	
	
	
	
	public void setVariabletypemap(IndexedHashMap<String, RegulatoryModelComponent> variabletypemap) {
		this.variabletypemap = variabletypemap;
	}

    


	public void setGeneproductmap(IndexedHashMap<String, String> geneproductmap) {
		this.geneproductmap = geneproductmap;
	}

    


	@SuppressWarnings("unchecked")
	public void setSpecieid2speciename(LinkedHashMap<String, String> specieid2speciename) {
		this.specieid2speciename = specieid2speciename;
		try {
			this.speciename2specieid=(LinkedHashMap<String, String>) MTUMapUtils.invertMap(specieid2speciename);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}




	public IndexedHashMap<String, String> getDecodedtransitionrules() {
		return decodedtransitionrules;
	}




	public LinkedHashSet<String> getDecodedcontantsvars() {
		return decodedcontantsvars;
	}




	public IndexedHashMap<String, RegulatoryModelComponent> getMapofextractedmodelvariablestype() {
		return mapofextractedmodelvariablestype;
	}


	public IndexedHashMap<String, String> getGeneID2RuleID() {
		return GeneID2RuleID;
	}




	public void processTransitionRules() throws ParseException{

		
		for (int i = 0; i < transitionslist.size(); i++) {
			Transition tr=transitionslist.get(i);
			String outspecie=getTransitionOutputSpecie(tr);
			
			if(!specieid2speciename.keySet().contains(outspecie) && !specieid2speciename.values().contains(outspecie)){
				throw new ParseException("Unknown element["+outspecie+"] for which transition rule is assigned, instead set the related \"qualitativespecie id\" ");
			}
			else{
				
				String ruleid=null;
				String spid=null;
			
				if(specieid2speciename.values().contains(outspecie))
					spid=speciename2specieid.get(outspecie);
				else
					spid=outspecie;
			
				if(geneproductmap!=null && geneproductmap.size()>0 && geneproductmap.containsKey(spid))
					ruleid=geneproductmap.get(spid);
				else
					ruleid=spid;
	
			
				if(spid!=ruleid)
					geneinrulelinkbyruleid=true;
			    GeneID2RuleID.put(spid, ruleid);
				String rule=getTransitionRuleToString(tr);
				
				if(rule!=null)
				  decodedtransitionrules.put(spid, rule);
	
			}
			
		}
	
	
	}
	
	protected String getTransitionOutputSpecie(Transition tr){
		Output outelem = tr.getListOfOutputs().get(0);
		return outelem.getQualitativeSpecies();
	}
	
	
	
	protected String getTransitionRuleToString(Transition tr) throws ParseException{
		String rule=null;
		
		List<FunctionTerm> functionterms=tr.getListOfFunctionTerms();
		for (int i = 0; i < functionterms.size(); i++) {
			String rulepart=getFunctionTermRule(functionterms.get(i));
			String temprule=null;
			if(rulepart!=null){
				temprule="( "+rulepart+" )";
				
				if(rule==null)
					rule=new String(temprule);
				else{
					
					if(i<functionterms.size())
						rule=rule+" OR ";
					rule+=temprule;
				}
			}
		}
		
		return rule;
	}
	
	
	protected String getFunctionTermRule(FunctionTerm ft) throws ParseException{
		String rule=null;
		if(!ft.isDefaultTerm()){
			ASTNode rulenode = ft.getMath();
			
			if(rulenode!=null && rulenode.getChildCount()>0){
				
				rule=parseSBMLASTNode(rulenode);
			}	
		}
	
		return rule;	
	}

	protected String parseSBMLASTNode(ASTNode node) throws ParseException{
		
		 if(node.getChild(0).getChildCount()==0)
		    return transformSingleNodeElement(node.toString());
		 else{
			
		    String firststep=transformRuleElements(node.toString());
		    if(firststep!=null){
		    	String secondstep=firststep.replaceAll("(!|not)", "NOT ");
		        String thirthstep=secondstep.replaceAll("(&&|(\\s+and\\s+))", " AND ");
		        String laststep=thirthstep.replaceAll("(\\|\\||\\s+or\\s+)", "OR");
		 
		       return laststep;
		    }
		    else
		    	throw new ParseException("The node "+node.toString()+" cannot be parsed");
		 }
	}
	
	protected String transformSingleNodeElement(String rule){
		return transformElementsToBooleanForm(rule);
	}
	
	protected String transformRuleElements(String rule){

		String pat="\\(((\\w+([-,.]\\w+)*)\\s(==|<=|>=|<|>)\\s((-)*\\d+))\\)";
		//else
		 //   pat="\\(((\\w+([-,.]\\w+)*)\\s(==|<=|>=|<|>)\\s(0|1))\\)";
		String inputrule=null;
		Pattern p = Pattern.compile(pat);
		
		Matcher m = p.matcher(rule);
		
		while(m.find()) {
			
			String match=m.group(1);
			String replacement=transformElementsToBooleanForm(match);

			if(match!=null && replacement!=null){
				if(inputrule==null)
					inputrule=rule.replaceFirst(match, replacement);
				else
					inputrule=inputrule.replaceFirst(match, replacement);
				
				m=p.matcher(inputrule);
			}

		}
		
		return inputrule;
		
	}
	
	protected String transformElementsToBooleanForm(String element){
		String pat="((\\w+([-,.]\\w+)*)\\s(==|<=|>=|<|>)\\s((-)*\\d+))";
		//else
		 //   pat="((\\w+([-,.]\\w+)*)\\s(==|<=|>=|<|>)\\s(0|1))";
		Pattern p = Pattern.compile(pat);
		Matcher m = p.matcher(element);
		
		String res=null;
		if(m.find()){

			String elemname=m.group(2);
			String sign=m.group(4);
			String value=m.group(5);
			
			
			if(elemname!=null && sign!=null && value!=null){
				elemname=elemname.trim(); sign=sign.trim(); value=value.trim();

				if(listofconstants.contains(elemname))
			       res=getBooleanFormatForConstantWithSign(elemname, sign, value);
				else
					res=getElementBooleanForm(elemname, sign, value);
			
		  }
		}
	   
		return res;
	}
	
	

	
	protected String getBooleanFormatForConstantWithSign(String element, String sign, String value){
		
		String varname=null;
		RegulatoryModelComponent type=getRegulatoryComponentType(element,RegulatoryModelComponent.ENV_CONDITION_ID);
		switch (sign) {
		case "<":
			varname=VariableSignValue.LESS.buildVariableName(element, value);
			type=getRegulatoryComponentType(element,RegulatoryModelComponent.REACTION_ID);
			break;
		case ">":
			varname=VariableSignValue.GREATER.buildVariableName(element, value);
			type=getRegulatoryComponentType(element,RegulatoryModelComponent.REACTION_ID);
			break;
		case "<=":
			varname=VariableSignValue.LESSOREQUALTO.buildVariableName(element, value);
			type=getRegulatoryComponentType(element,RegulatoryModelComponent.REACTION_ID);
			break;
		case ">=":
			varname=VariableSignValue.GREATEROREQUALTO.buildVariableName(element, value);
			type=getRegulatoryComponentType(element,RegulatoryModelComponent.REACTION_ID);
			break;
        
			
		default:
			 int val=Integer.parseInt(value);
			 if(val<1)
				 varname="NOT "+element;
			 else
				 varname=element;
		}
		
		if(!varname.matches("NOT\\s\\w+([-,.]\\w+)*")){
		   decodedcontantsvars.add(varname);
		   mapofextractedmodelvariablestype.put(varname, type);
		}
		else{
			String varnonot=varname.substring(4, varname.length());
			decodedcontantsvars.add(varnonot);
			mapofextractedmodelvariablestype.put(varnonot, type);
		}
		   
		return varname;
	}
	
	private RegulatoryModelComponent getRegulatoryComponentType(String elemid,RegulatoryModelComponent defaulttype){
		if(variabletypemap!=null && variabletypemap.containsKey(elemid))
			return variabletypemap.get(elemid);
		else
			return defaulttype;
	}
	
	
	protected String getElementBooleanForm(String element, String sign, String boolstate){
		     int val=Integer.parseInt(boolstate);
		     
		     if(geneproductmap!=null && geneproductmap.containsKey(element))
			  element=geneproductmap.get(element);
		 
		     
	
			 if( (sign.equals("<") || sign.equals("<=")) && val<=0){
				 return "NOT "+element;
			 }
			 else  if( sign.equals("<") && val==1){
				 return "NOT "+element;
			 }
			 else  if( sign.equals("<=") && val>=1){
				 return element;
			 }
			 else  if(sign.equals("<") && val>1){
				 return element;
			 }
			 else if( sign.equals(">=") && val==0){
				 return "NOT "+element;
			 }
			 else if( sign.equals(">") && val==0){
				 return element;
			 }
			 else  if( (sign.equals(">") || sign.equals(">=")) && val>0){
				 return element;
			 }
			 else if(sign.equals("==") && val<1)
				 return "NOT "+element;
			 else
				 return element; 

	}




	public boolean isGeneinrulelinkbyruleid() {
		return geneinrulelinkbyruleid;
	}
	
	
	
	
	
}
