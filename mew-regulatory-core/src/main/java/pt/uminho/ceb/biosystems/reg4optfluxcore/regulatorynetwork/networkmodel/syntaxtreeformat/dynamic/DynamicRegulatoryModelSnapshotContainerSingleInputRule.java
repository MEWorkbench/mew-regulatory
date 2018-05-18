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

import java.io.Serializable;
import java.util.LinkedHashSet;

public class DynamicRegulatoryModelSnapshotContainerSingleInputRule implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	protected String lastinputgeneid = null;
	protected String lastASTIDsRules =null;
	protected int lastIndexmapgeneRule;
	protected int lastindexrulesincrement;
	protected int lastindexvariablesincrement;
	protected LinkedHashSet<String> listofinputVariables=null;
	protected boolean newgeneid=false;
	//protected boolean ispreviousunconstrainedgene;
	
	
	public DynamicRegulatoryModelSnapshotContainerSingleInputRule(){
		this.listofinputVariables=new LinkedHashSet<>();
	}
	
	public DynamicRegulatoryModelSnapshotContainerSingleInputRule(String lastinputgeneid,
			String lastASTIDsRules,
			LinkedHashSet<String> listofinputVariables,
			int lastIndexmapgeneRule,
			int lastindexrulesincrement,
			int lastindexvariablesincrement,
			boolean newgeneid
			){
		this.lastinputgeneid=lastinputgeneid;
		this.lastASTIDsRules=lastASTIDsRules;
		this.listofinputVariables=listofinputVariables;
		this.lastIndexmapgeneRule=lastIndexmapgeneRule;
		this.lastindexrulesincrement=lastindexrulesincrement;
		this.lastindexvariablesincrement=lastindexvariablesincrement;
		this.newgeneid=newgeneid;
		
	}


    public void setLastInputGeneid(String geneid){
    	this.lastinputgeneid=geneid;
    }
	
	public void setIndexmapgeneRule(int index){
		this.lastIndexmapgeneRule=index;
	}
	
	public void setLastNumberOfNewRules(int incr){
		this.lastindexrulesincrement=incr;
	}
	
	public void setTotalVariables(int incr){
		this.lastindexvariablesincrement=incr;
	}
	
	public void setLastInputRuleID(String ruleid){
		this.lastASTIDsRules=ruleid;
	}
	
	public void resetPreviousListofVariables(){
		this.listofinputVariables=new LinkedHashSet<>();
	}
	
	public void addVariabletoList(String varid){
		this.listofinputVariables.add(varid);
	}




	public String getLastinputgeneid() {
		return lastinputgeneid;
	}


	public String getLastASTIDsRules() {
		return lastASTIDsRules;
	}


	public int getLastIndexmapgeneRule() {
		return lastIndexmapgeneRule;
	}


	public int getLastTotalNewRules() {
		return lastindexrulesincrement;
	}


	public int getLastTotalNewVariables() {
		return lastindexvariablesincrement;
	}
	
	
/*	public boolean isPreviousUnconstrainedGene() {
		return ispreviousunconstrainedgene;
	}



	public void setAsPreviousUnconstrainedGene(String unconstrainedgene) {
		if(listpreviousunconstrainedgenes==null)
			listpreviousunconstrainedgenes=new ArrayList<>();
		listpreviousunconstrainedgenes.add(unconstrainedgene);
	}
*/
	/*public boolean isNewgeneid() {
		return newgeneid;
	}*/

/*	public void setNewgeneid(boolean newgeneid) {
		this.newgeneid = newgeneid;
	}*/

	public LinkedHashSet<String> getListofinputVariables() {
		return listofinputVariables;
	}
	
	
	public DynamicRegulatoryModelSnapshotContainerSingleInputRule copy(){
		LinkedHashSet<String> copylistofinputVariables = null;
		if(this.listofinputVariables!=null){
			copylistofinputVariables= new LinkedHashSet<>();
			for (String var : this.listofinputVariables) {
				copylistofinputVariables.add(var);
			}
		}
		
		return new DynamicRegulatoryModelSnapshotContainerSingleInputRule(this.lastinputgeneid, this.lastASTIDsRules, copylistofinputVariables, this.lastIndexmapgeneRule, this.lastindexrulesincrement, this.lastindexvariablesincrement, this.newgeneid);
	}
	
	

}
