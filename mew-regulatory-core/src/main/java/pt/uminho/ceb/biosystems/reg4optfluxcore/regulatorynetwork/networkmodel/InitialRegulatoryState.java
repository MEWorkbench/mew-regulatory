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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.ornrocha.collections.MTUMapUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;

public class InitialRegulatoryState implements Serializable{


	private static final long serialVersionUID = 1L;
	private ArrayList<String> ordereridentifiers;
	private byte[] initbytestates;
	private ArrayList<Boolean> initboolstates;
	private Set<String> knockoutgenes;
	private Set<Integer> indexknockoutgenes;
    private IndexedHashMap<String, String> geneid2ruleid;
    private IndexedHashMap<String, String> ruleid2geneid;

	@SuppressWarnings("unchecked")
	public InitialRegulatoryState(ArrayList<String> ordereridentifiers, byte[] statevector, IndexedHashMap<String, String> geneid2ruleid) throws Exception{
		if(ordereridentifiers.size()!=statevector.length){
			throw new Exception("The variables identifier vector and state vector must be of same size");
		}
		else{
			this.ordereridentifiers=ordereridentifiers;
			this.initbytestates=statevector;
			this.initboolstates=MTUCollectionsUtils.convertbyteArrayToBooleanList(statevector);
			this.geneid2ruleid=geneid2ruleid;
			this.ruleid2geneid=(IndexedHashMap<String, String>) MTUMapUtils.invertMap(geneid2ruleid);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public InitialRegulatoryState(ArrayList<String> ordereridentifiers, ArrayList<Boolean> statevector, IndexedHashMap<String, String> geneid2ruleid) throws Exception{
		if(ordereridentifiers.size()!=statevector.size()){
			throw new Exception("The variables identifier vector and state vector must be of same size");
		}
		else{
			this.ordereridentifiers=ordereridentifiers;
			this.initboolstates=statevector;
			this.initbytestates=MTUCollectionsUtils.convertBooleanListTobyteArray(statevector);
			this.geneid2ruleid=geneid2ruleid;
			this.ruleid2geneid=(IndexedHashMap<String, String>) MTUMapUtils.invertMap(geneid2ruleid);
		}
	}
	
	private InitialRegulatoryState(ArrayList<String> ordereridentifiers,ArrayList<Boolean> boolstates, byte[] bytestates, IndexedHashMap<String, String> geneid2ruleid, Set<String> knockoutgenes,Set<Integer> fixedgenesstateoff) throws Exception{
		this(ordereridentifiers,bytestates,geneid2ruleid);
		this.initboolstates=boolstates;
		this.knockoutgenes=knockoutgenes;
		this.indexknockoutgenes=fixedgenesstateoff;
	}
	
	
	private Pair<String, Integer> getGeneOrTFinfo(String id){
		String useid=null;
		if(ordereridentifiers.contains(id))
			useid=id;
		else if(geneid2ruleid.containsKey(id) && ordereridentifiers.contains(geneid2ruleid.get(id)))
			useid=geneid2ruleid.get(id);
		else if(ruleid2geneid.containsKey(id) && ordereridentifiers.contains(ruleid2geneid.get(id)))
			useid=ruleid2geneid.get(id);
		else
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("ElementID: ["+id+"] was not found in the current regulatory model");
		
		if(useid!=null)
			return new Pair<String, Integer>(useid, ordereridentifiers.indexOf(useid));
		else
			return null;
		
	}
	
	
	
	public void setGenesStatePermanentlyOff(Set<String> geneids){
		this.knockoutgenes=new HashSet<>();
		this.indexknockoutgenes=new HashSet<>();
		for (String id : geneids) {
			Pair<String, Integer> geneinfo=getGeneOrTFinfo(id);
			if(geneinfo!=null){
				knockoutgenes.add(geneinfo.getValue0());
				indexknockoutgenes.add(geneinfo.getValue1());
				
				int pos=ordereridentifiers.indexOf(geneinfo.getValue0());
				initboolstates.set(pos, false);
				initbytestates[pos]=0;
			}	
		}
		//System.out.println("Knockouts regulatory: "+knockoutgenes);
	}
	
	
	
	
	
	public ArrayList<String> getOrderedIdentifiers() {
		return ordereridentifiers;
	}



	public byte[] getInitialbyteStateArray() {
		return initbytestates;
	}



	public ArrayList<Boolean> getInitialBooleanStates() {
		return initboolstates;
	}
	
	



	public Set<Integer> getFixedGenesStateOff() {
		return indexknockoutgenes;
	}



/*	public void setFixedGenesStateAsOff(Set<Integer> fixedgenesstateoff) {
		this.fixedgenesstateoff = fixedgenesstateoff;
	}
*/
	

	public IndexedHashMap<String, String> getGeneid2Ruleid() {
		return geneid2ruleid;
	}

	


	public IndexedHashMap<String, String> getRuleid2geneid() {
		return ruleid2geneid;
	}



	public Set<String> getKnockoutgenes() {
		return knockoutgenes;
	}

    public void setInitialGeneStateAsOFF(Set<String> genesoff){
    	if(genesoff!=null){
    		for (String id : genesoff) {
    			setInitialGeneStateAsOFF(id);
			}
    	}
    }

	public void setInitialGeneStateAsOFF(String geneid){
		setGenesInitialBooleanState(geneid, false);
	}
	
	public void setInitialGeneStateAsON(String geneid){
		setGenesInitialBooleanState(geneid, true);
	}
	
	
	public void setInitialGeneStateAsON(Set<String> geneson){
	   if(geneson!=null){
	    	for (String id : geneson) {
	    		setInitialGeneStateAsON(id);
			}
	    }
	 }
	
	
	public void setInitialGenebyteState(String geneid, byte value){
		setGenesInitialbyteState(geneid, value);
	}
	
	
	/*public void setGeneSetInitalStateOFF(Set<String> geneids){
		for (String id : geneids) {
			setInitialGeneStateAsOFF(id);
		}
	}*/
	
	
	public void addGeneSetInitalbyteStates(Map<String, Byte> genestates){
		for (Map.Entry<String, Byte> map : genestates.entrySet()) {
			setInitialGenebyteState(map.getKey(), map.getValue());
		}
	}
	
	public void initializeComponentsBooleanState(Map<String,Boolean> componentstates){
		for (Map.Entry<String, Boolean> map : componentstates.entrySet()) {
		    initializeVariableBooleanState(map.getKey(), map.getValue());
		}
	}
	
	public void initializeComponentsbyteState(Map<String,Byte> componentstates){
		for (Map.Entry<String, Byte> map : componentstates.entrySet()) {
		    initializeVariablebyteState(map.getKey(), map.getValue());
		}
	}
	
	public void initializeVariableBooleanState(String varid, boolean state){
		if(state)
			setVariableInitialbyteState(varid, (byte) 1);
		else
			setVariableInitialbyteState(varid, (byte) 0);
	}
	
	public void initializeVariablebyteState(String varid, byte value){
		setVariableInitialbyteState(varid, value);
	}
	
	
	private void setVariableInitialbyteState(String varid, byte value){
		String useid=null;
		if(ordereridentifiers.contains(varid))
			useid=varid;
		else{
			Pair<String, Integer> varinfo=getGeneOrTFinfo(varid);
			if(varinfo!=null)
				useid=varinfo.getValue0();
		}
		
		if(useid!=null){
			int index=ordereridentifiers.indexOf(useid);
			initbytestates[index]=value;
			if(value>0)
			   initboolstates.set(index, true);
			else
			   initboolstates.set(index, false);
			
		}
	}
	
	
	
	private void setGenesInitialBooleanState(String geneid, boolean state){
		Pair<String, Integer> geneinfo=getGeneOrTFinfo(geneid);
		if(geneinfo!=null){
			int index=geneinfo.getValue1();
			initboolstates.set(index, state);
			
			if(state){
				initbytestates[index]=1;
			}
			else
				initbytestates[index]=0;
		}
	}
	
	private void setGenesInitialbyteState(String geneid, byte value){
		Pair<String, Integer> geneinfo=getGeneOrTFinfo(geneid);
		if(geneinfo!=null){
			int index=geneinfo.getValue1();
			initbytestates[index]=value;

			if((int)value>0){
				initboolstates.set(index, true);
			}
			else
				initboolstates.set(index,false);
		}
	}
	
	public Boolean getBooleanStateForComponentID(String id){
		if(ordereridentifiers.contains(id)){
			int index=ordereridentifiers.indexOf(id);
			return initboolstates.get(index);
		}
		return null;
	}
	
	public Boolean getBooleanStateForComponentAtIndex(int index){
		if(index<ordereridentifiers.size())
			return initboolstates.get(index);
		return null;
	}
	
	public boolean containsComponentID(String id){
		return ordereridentifiers.contains(id);
	}
	
	@SuppressWarnings("unchecked")
	public InitialRegulatoryState copy() throws Exception{
		
		return new InitialRegulatoryState((ArrayList<String>)MTUCollectionsUtils.deepCloneObject(ordereridentifiers), 
				(ArrayList<Boolean>)MTUCollectionsUtils.deepCloneObject(initboolstates), 
				(byte[])MTUCollectionsUtils.deepCloneObject(initbytestates), 
				(IndexedHashMap<String, String>)MTUCollectionsUtils.deepCloneObject(geneid2ruleid), 
				(Set<String>)MTUCollectionsUtils.deepCloneObject(knockoutgenes), 
				(Set<Integer>)MTUCollectionsUtils.deepCloneObject(indexknockoutgenes));
	}
	
	@Override
	public String toString() {
		
		StringBuilder str=new StringBuilder();
		str.append("Number variables: "+ordereridentifiers.size()+"\n");
		for (int i = 0; i < ordereridentifiers.size(); i++) {
			str.append(ordereridentifiers.get(i)+" --> "+initboolstates.get(i)+"\n");
		}
		str.append("\n\n");
		/*str.append("ordereridentifiers: "+ordereridentifiers+"\n");
		str.append("initboolstates: "+initboolstates+"\n");*/
		str.append("knockoutgenes: "+knockoutgenes+"\n");
		str.append("indexknockoutgenes: "+indexknockoutgenes+"\n");
		str.append("geneid2ruleid: "+geneid2ruleid+"\n");
		str.append("ruleid2geneid: "+ruleid2geneid+"\n");
		
		return str.toString();
	}
	
	

}
