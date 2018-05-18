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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class DynamicRegulatoryModelSnapshotContainerMultipleInputRules implements Serializable{


	private static final long serialVersionUID = 1L;
	
	protected HashSet<String> lastinputgenes;
	protected HashSet<String> lastinputruleids;
	protected HashSet<Integer> lastindexesgenerules;
	protected int lastindexrulesincrement=-1;
	protected int lastindexvariablesincrement=-1;
	protected LinkedHashSet<String> listofinputVariables=null;
	protected ArrayList<String> listpreviousunconstrainedgenes;
	
	public DynamicRegulatoryModelSnapshotContainerMultipleInputRules(){
		 lastinputgenes=new HashSet<>();
		 lastindexesgenerules=new HashSet<>();
		 lastinputruleids=new HashSet<>();
		 listofinputVariables=new LinkedHashSet<>();
	}
	
	
	
	public void addLastInputGeneid(String geneid){
		lastinputgenes.add(geneid);
	}



	public HashSet<String> getLastinputgenes() {
		return lastinputgenes;
	}



	public HashSet<Integer> getLastindexesgenerules() {
		return lastindexesgenerules;
	}



	public void addLastindexesgenerules(int generuleindex) {
		lastindexesgenerules.add(generuleindex);
	}
	
	public void addLastInputRuleid(String ruleid){
		lastinputruleids.add(ruleid);
	}



	public HashSet<String> getLastInputRuleIds() {
		return lastinputruleids;
	}
	
	public void setLastNumberOfNewRules(int incr){
		if(lastindexrulesincrement==-1)
			this.lastindexrulesincrement=incr;
	}
	
	public int getLastTotalNewRules() {
		return lastindexrulesincrement;
	}
	
	
	public void setTotalVariables(int incr){
		if(lastindexvariablesincrement==-1)
			this.lastindexvariablesincrement=incr;
	}
	
	public int getLastTotalNewVariables() {
		return lastindexvariablesincrement;
	}
	
	public void addVariabletoList(String varid){
		this.listofinputVariables.add(varid);
	}
	
	public LinkedHashSet<String> getListofinputVariables() {
		return listofinputVariables;
	}



	public ArrayList<String> getListpreviousunconstrainedgenes() {
		return listpreviousunconstrainedgenes;
	}



	public void appendPreviousInconstrainedGene(String unconstrainedgene) {
		if(listpreviousunconstrainedgenes==null)
			listpreviousunconstrainedgenes=new ArrayList<>();
		listpreviousunconstrainedgenes.add(unconstrainedgene);
	}
	
	

}
