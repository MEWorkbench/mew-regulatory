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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;


public class RegulatoryVariable implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	protected String id;
	protected String originalname;
	protected String unsignedsimplename;
	protected boolean maptometabolicnetwork = false;
	protected boolean linktometabolicgene= false;
	protected boolean independentvariable=false;
	protected RegulatoryModelComponent typecomp;
	protected VariableSignValue variablesign=null;
	protected String signvalue=null;

	public RegulatoryVariable(String id, String name,RegulatoryModelComponent type) {
		this.id=id;
		this.originalname=name;
		this.typecomp=type;
		setSimpleVariableName(name);
	}
    
	public RegulatoryVariable(String id, String name,VariableSignValue variablesign, String signvalue, RegulatoryModelComponent type) {
		this(id,name,type);
		this.variablesign=variablesign;
		this.signvalue=signvalue;
	} 

	public RegulatoryVariable(String id, String name,RegulatoryModelComponent type, boolean metabolicvariable) {
		this(id,name,type);
		this.maptometabolicnetwork=metabolicvariable;
	}
	
	private RegulatoryVariable(String id, String name,String simplename, VariableSignValue variablesign, String signvalue,RegulatoryModelComponent type, boolean metabolicvar,boolean linktogene,boolean independentvar) {
		this.id=id;
		this.originalname=name;
		this.unsignedsimplename=simplename;
		this.variablesign=variablesign;
		this.signvalue=signvalue;
		this.typecomp=type;
		this.maptometabolicnetwork=metabolicvar;
		this.linktometabolicgene=linktogene;
		this.independentvariable=independentvar;
	}

	
	public RegulatoryVariable(RegulatoryVariable var){
		this.id=var.getId();
		this.originalname=var.getName();
		this.unsignedsimplename=var.getSimpleName();
		this.typecomp=var.getType();
		this.variablesign=var.getVariableSign();
        this.signvalue=var.getSignValue();
		this.maptometabolicnetwork=var.isMetabolicCondition();
		this.linktometabolicgene=var.linktoMetabolicGene();
		this.independentvariable=var.isIndependentvariable();
	}
	
	
	protected void setSimpleVariableName(String originalname){
		if(originalname.contains("<") || originalname.contains(">")){
			String[] elems=originalname.split("[<>]");
			this.unsignedsimplename=elems[0].trim();
		}
		else if(originalname.contains("<="))
			this.unsignedsimplename=originalname.split("<=")[0].trim();
		else if(originalname.contains(">="))
			this.unsignedsimplename=originalname.split(">=")[0].trim();
		else
			this.unsignedsimplename=originalname;
	}
	

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return originalname;
	}


	public void setName(String name) {
		this.originalname = name;
		setSimpleVariableName(name);
	}
	
	
	
	public String getSimpleName() {
		return unsignedsimplename;
	}

	public RegulatoryModelComponent getType() {
		return typecomp;
	}
	
	
	public VariableSignValue getVariableSign() {
		return variablesign;
	}

	public void setVariableSign(VariableSignValue variablesign) {
		this.variablesign= variablesign;
	}

	public String getSignValue() {
		return signvalue;
	}

	public void setSignValue(String value) {
		this.signvalue = value;
	}

	public void setAsMetabolicVariable(boolean st){
		this.maptometabolicnetwork=st;
	}
	
	public boolean isMetabolicCondition(){
		return this.maptometabolicnetwork;
	}
	
	public void setComponentType(RegulatoryModelComponent type){
		this.typecomp=type;
	}
	
	

	public boolean linktoMetabolicGene() {
		return linktometabolicgene;
	}


	public void setLinktoMetabolicGene(boolean linktometabolicgene) {
		this.linktometabolicgene = linktometabolicgene;
	}
	
	


	public boolean isIndependentvariable() {
		return independentvariable;
	}


	public void setIndependentvariable(boolean independentvariable) {
		this.independentvariable = independentvariable;
	}


	public RegulatoryVariable copy(){
		return new RegulatoryVariable(this.id, this.originalname,this.unsignedsimplename, this.variablesign, this.signvalue,this.typecomp, this.maptometabolicnetwork,this.linktometabolicgene,this.independentvariable);
	}
	
	
	public static RegulatoryVariable setupVariable(String name, RegulatoryModelComponent type){
        
		
		if(name.contains("<") || name.contains(">")){
			
			if(type==null)
				type=RegulatoryModelComponent.REACTION_ID;
			
			String[] elems=name.split("[<>]");
			if(name.contains("<")){
				String id=VariableSignValue.LESS.buildVariableName(elems[0], elems[1]);
				return new RegulatoryVariable(id, name, VariableSignValue.LESS, elems[1], type);
			}
			else{
				String id=VariableSignValue.GREATER.buildVariableName(elems[0], elems[1]);
				return new RegulatoryVariable(id, name, VariableSignValue.GREATER, elems[1], type);
			}
			
		}
		else if(name.contains("<=") || name.contains(">=")){
			if(type==null)
				type=RegulatoryModelComponent.REACTION_ID;
			
			if(name.contains("<=")){
				String[] elems=name.split("<=");
				String id=VariableSignValue.LESSOREQUALTO.buildVariableName(elems[0], elems[1]);
				return new RegulatoryVariable(id, name, VariableSignValue.LESSOREQUALTO, elems[1], type);
			}
			else{
				String[] elems=name.split(">=");
				String id=VariableSignValue.GREATEROREQUALTO.buildVariableName(elems[0], elems[1]);
				return new RegulatoryVariable(id, name, VariableSignValue.GREATEROREQUALTO, elems[1], type);
			}
		}
		else if(isVariableWithSignValue(name)){
			RegulatoryVariable temp=setupRegulatoryVariableWithSign(name, type);
			if(temp!=null)
				return temp;
			else
				return getRegulatoryVariableWithoutSign(name, type);
		}
		else{
			return getRegulatoryVariableWithoutSign(name, type);
		}
	}
	
	protected static RegulatoryVariable getRegulatoryVariableWithoutSign(String name, RegulatoryModelComponent type){
		if(type==null){
			if(isMetaboliteVariable(name))
			   type=RegulatoryModelComponent.METABOLITE_ID;
			else  
			   type=RegulatoryModelComponent.ENV_CONDITION_ID;
		}
		
		return new RegulatoryVariable(name, name, type);
	}
	
	
	protected static boolean isMetaboliteVariable(String name){
		String pattern="(M_\\w+(-*\\w+)*_e)";
		Pattern patt = Pattern.compile(pattern);
		Matcher match = patt.matcher(name);
		if(match.find())
			return true;
		
		return false;
	}
	
	
	public static boolean isVariableWithSignValue(String name){
		
		if(name.contains(VariableSignValue.LESS.getName()) ||
		   name.contains(VariableSignValue.LESSOREQUALTO.getName())	||
		   name.contains(VariableSignValue.GREATER.getName()) ||
		   name.contains(VariableSignValue.GREATEROREQUALTO.getName()))
			
			return true;
		return false;
	}
	
	public static RegulatoryVariable setupRegulatoryVariableWithSign(String name, RegulatoryModelComponent type){
		
		VariableSignValue signpat=null;
		if(name.contains(VariableSignValue.LESS.getName()))
			signpat=VariableSignValue.LESS;
		else if(name.contains(VariableSignValue.LESSOREQUALTO.getName()))
			signpat=VariableSignValue.LESSOREQUALTO;
		else if(name.contains(VariableSignValue.GREATER.getName()))
			signpat=VariableSignValue.GREATER;
		else if(name.contains(VariableSignValue.GREATEROREQUALTO.getName()))
			signpat=VariableSignValue.GREATEROREQUALTO;

		if(signpat!=null){
			String globalpat="(\\w+([-,_]\\w+)*)"+signpat.getName()+"((-)*\\d+(dot\\d+)*)";
			Pattern pat = Pattern.compile(globalpat);
			Matcher match = pat.matcher(name);
			String varname=null;
			String value=null;
			
			if(match.find()){
				varname=match.group(1);
				value=match.group(3);
			}
			
			if(varname!=null && value!=null){
				if(type==null)
					type=RegulatoryModelComponent.REACTION_ID;
				
				if(value.contains("dot"))
					   value=value.replaceAll("dot", "\\.");
				
				String origname=varname+signpat.getSign()+value;
				return new RegulatoryVariable(name, origname, signpat, value, type);
			}
			else{
				if(type==null)
					type=RegulatoryModelComponent.ENV_CONDITION_ID;
			   return new RegulatoryVariable(name, name, type);
			}
		}
		else{
			if(type==null)
				type=RegulatoryModelComponent.ENV_CONDITION_ID;
			return new RegulatoryVariable(name, name, type);
		}
	}
	

   	public static RegulatoryVariable getModelVariableByOriginalName(IOptfluxRegulatoryModel model, String varname){
   		
   		IndexedHashMap<String, RegulatoryVariable> variables=model.getVariablesInRegulatoryNetwork();
   		for (String varid: variables.keySet()) {
			RegulatoryVariable var=variables.get(varid);
			if(var.getName().equals(varname))
				return var;
		}
		return null;	
	}
   	
  	public static IndexedHashMap<String, RegulatoryVariable> getRegulatoryVariableMapWithSignInName(IOptfluxRegulatoryModel model){
  		IndexedHashMap<String, RegulatoryVariable> newvariablesmap=new IndexedHashMap<>();
   		IndexedHashMap<String, RegulatoryVariable> variables=model.getVariablesInRegulatoryNetwork();
   		for (int i = 0; i < variables.size(); i++) {
			String id=variables.getKeyAt(i);
			RegulatoryVariable var=variables.get(id);
			newvariablesmap.put(var.getName(), var);
		}
   		return newvariablesmap;
	}
  	
  	public static void main(String[] args){
  		String var="R_EX_pi_less_than_0dot000000004";
  		RegulatoryVariable regvar =setupRegulatoryVariableWithSign(var,null);
  		System.out.println("VARID: "+regvar.getId()+" ORIG NAME: "+regvar.getName()+ "  SIGN: "+regvar.getVariableSign()+ " VALUE: "+regvar.getSignValue());
  	}
   	

}
