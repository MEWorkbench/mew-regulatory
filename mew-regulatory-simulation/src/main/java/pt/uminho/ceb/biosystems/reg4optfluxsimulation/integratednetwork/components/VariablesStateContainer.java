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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;

public class VariablesStateContainer implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> truetfs;
	private ArrayList<String> falsetfs;
	private ArrayList<String> trueenvconditions;
	private ArrayList<String> falseenvconditions;
	private ArrayList<String> knockoutgenes;
	private HashSet<String> initoffgenes;
	private HashSet<String> initongenes;
	private HashMap<String, Boolean> initboolstate=null;
	private HashMap<String, Byte> initbytestate=null;
	private HashMap<String, Object> extrafeatures;
	
	
	
	
	public VariablesStateContainer() {}
	
	
	public VariablesStateContainer(ArrayList<String> truetfs, ArrayList<String> falsetfs,
			ArrayList<String> trueenvconditions, ArrayList<String> falseenvconditions,
			ArrayList<String> knockoutgenes, HashSet<String> initoffgenes,HashSet<String> initongenes) {
		setTruetfs(truetfs);
		setFalsetfs(falsetfs);
		setTrueenvconditions(trueenvconditions);
		setFalseenvconditions(falseenvconditions);
		setKnockoutgenes(knockoutgenes);
		setInitialGenesOFF(initoffgenes);
		setInitialGenesON(initongenes);
	}

	public void appendExtraFeature(String key, Object value) {
		if(extrafeatures==null)
			extrafeatures=new HashMap<>();
		extrafeatures.put(key, value);
	}
	
	public Object getExtraFeature(String key) {
		if(extrafeatures!=null && extrafeatures.containsKey(key))
			return extrafeatures.get(key);
		return null;
	}

	public HashMap<String, Boolean> getInitialComponentsbooleanState() {
		return initboolstate;
	}

	public void setInitialComponentsbooleanState(HashMap<String, Boolean> initboolstate) {
		this.initboolstate = initboolstate;
		synchronizeComponentsByteState(initboolstate);
		
	}

	public HashMap<String, Byte> getInitialComponentsbyteState() {
		return initbytestate;
	}

	public void setInitialComponentsbyteState(HashMap<String, Byte> initbytestate) {
		this.initbytestate = initbytestate;
		synchronizeComponentsBooleanState(initbytestate);
	}
	
	
	public void addVariableBooleanState(String id, boolean state) {
		if(initboolstate==null)
			initboolstate=new HashMap<>();
		if(initbytestate==null)
			initbytestate=new HashMap<>();
		
		initboolstate.put(id, state);
		if(state)
			initbytestate.put(id, (byte) 1);
		else
			initbytestate.put(id, (byte) 0);
	}
	
	
	public void addVariableByteState(String id, byte state) {
		if(initboolstate==null)
			initboolstate=new HashMap<>();
		if(initbytestate==null)
			initbytestate=new HashMap<>();
		initbytestate.put(id, state);
		if(state>0)
			initboolstate.put(id, true);
		else
			initboolstate.put(id, false);
	}

	public ArrayList<String> getTruetfs() {
		return truetfs;
	}



	public void setTruetfs(ArrayList<String> truetfs) {
		if(truetfs!=null) {
			this.truetfs = truetfs;
			addListElementsState(truetfs, true);
		}
	}



	public ArrayList<String> getFalsetfs() {
		return falsetfs;
	}



	public void setFalsetfs(ArrayList<String> falsetfs) {
		if(falsetfs!=null) {
			this.falsetfs = falsetfs;
			addListElementsState(falsetfs, false);
		}
	}



	public ArrayList<String> getTrueenvconditions() {
		return trueenvconditions;
	}



	public void setTrueenvconditions(ArrayList<String> trueenvconditions) {
		if(trueenvconditions!=null) {
			this.trueenvconditions = trueenvconditions;
			addListElementsState(trueenvconditions, true);
		}
	}

	
	public void appendTrueRegulatoryEnvCondition(String name) {
		if(trueenvconditions==null)
			trueenvconditions=new ArrayList<>();
		
		trueenvconditions.add(name);
		addVariableBooleanState(name, true);
	}


	public ArrayList<String> getFalseenvconditions() {
		return falseenvconditions;
	}



	public void setFalseenvconditions(ArrayList<String> falseenvconditions) {
		if(falseenvconditions!=null) {
			this.falseenvconditions = falseenvconditions;
			addListElementsState(falseenvconditions, false);
		}
	}



	public ArrayList<String> getKnockoutgenes() {
		return knockoutgenes;
	}



	public void setKnockoutgenes(ArrayList<String> knockoutgenes) {
		this.knockoutgenes = knockoutgenes;
	}
	
	

	
	public HashSet<String> getInitialGenesOFF() {
		return initoffgenes;
	}



	public void setInitialGenesOFF(Set<String> initoffgenes) {
		if(initoffgenes!=null) {
			this.initoffgenes = new HashSet<>(initoffgenes);
			addListElementsState(new ArrayList<>(initoffgenes), false);
		}
	}
	
	public HashSet<String> getInitialGenesON() {
		return initongenes;
	}



	public void setInitialGenesON(Set<String> initongenes) {
		if(initongenes!=null) {
			this.initongenes = new HashSet<>(initongenes);
			addListElementsState(new ArrayList<>(initongenes), true);
		}
	}

	
	public IndexedHashMap<String, Boolean> getStateOfRegulatoryConditionVariables(){
		IndexedHashMap<String, Boolean> res=new IndexedHashMap<>();
		
		ArrayList<String> truevariables=getTrueenvconditions();
		if(truevariables!=null && truevariables.size()>0)
			for (int i = 0; i < truevariables.size(); i++) {
				res.put(truevariables.get(i), true);
			}
	
		ArrayList<String> falsevariables=getFalseenvconditions();
		if(falsevariables!=null && falsevariables.size()>0)
			for (int i = 0; i < falsevariables.size(); i++) {
				res.put(falsevariables.get(i), false);
			}
		return res;
	}
	
	
	public IndexedHashMap<String, Boolean> getStateOfRegulatoryTranscriptionalFactorsVariables(){
		IndexedHashMap<String, Boolean> res=new IndexedHashMap<>();
		ArrayList<String> tfson=getTruetfs();
		if(tfson!=null && tfson.size()>0)
			for (int i = 0; i < tfson.size(); i++) {
				res.put(tfson.get(i), true);
			}
	
		ArrayList<String> tfoff=getFalsetfs();
		if(tfoff!=null && tfoff.size()>0)
			for (int i = 0; i < tfoff.size(); i++) {
				res.put(tfoff.get(i), false);
			}
	    return res;
		
	}
	
	
	public IndexedHashMap<String, Boolean> getStateOfGenes(){
		IndexedHashMap<String, Boolean> res=new IndexedHashMap<>();
		
		HashSet<String> geneson=getInitialGenesON();
		if(geneson!=null && geneson.size()>0)
			for (String id : geneson) {
				res.put(id, true);
			}

		HashSet<String> genesoff=getInitialGenesOFF();
		if(genesoff!=null && genesoff.size()>0)
			for (String id : genesoff) {
				res.put(id, false);
			}
	    return res;
		
		
	}
	


	public HashMap<String, Boolean> getInitialStateOfAllVariables(){
		
		if(initboolstate!=null) {
			return initboolstate;
		}
		else {
			HashMap<String, Boolean> res=new HashMap<>();
		
			ArrayList<String> tfson=getTruetfs();
			if(tfson!=null && tfson.size()>0)
				for (int i = 0; i < tfson.size(); i++) {
					res.put(tfson.get(i), true);
				}
		
			ArrayList<String> tfoff=getFalsetfs();
			if(tfoff!=null && tfoff.size()>0)
				for (int i = 0; i < tfoff.size(); i++) {
					res.put(tfoff.get(i), false);
				}
		
			ArrayList<String> truevariables=getTrueenvconditions();
			if(truevariables!=null && truevariables.size()>0)
				for (int i = 0; i < truevariables.size(); i++) {
					res.put(truevariables.get(i), true);
				}
		
			ArrayList<String> falsevariables=getFalseenvconditions();
			if(falsevariables!=null && falsevariables.size()>0)
				for (int i = 0; i < falsevariables.size(); i++) {
					res.put(falsevariables.get(i), false);
				}
		
			if(res.size()>0)
				return res;
			return null;
			}
	}
	
	
	private void synchronizeComponentsByteState(HashMap<String, Boolean> initboolstate) {
		initbytestate=new HashMap<>();
		for (String id : initboolstate.keySet()) {
			boolean state=initboolstate.get(id);
			if(state)
				initbytestate.put(id, (byte) 1);
			else
				initbytestate.put(id, (byte) 0);
		}
	}
	

	private void synchronizeComponentsBooleanState(HashMap<String, Byte> initbytestate) {
		initboolstate=new HashMap<>();
		for (String id : initbytestate.keySet()) {
			byte state=initbytestate.get(id);
			if(state>0)
				initboolstate.put(id, true);
			else
				initboolstate.put(id, false);
		}
	}
	
	private void addListElementsState(ArrayList<String> list, boolean state) {
		
		for (int i = 0; i < list.size(); i++) {
			addVariableBooleanState(list.get(i), state);
		}
	}
	

}
