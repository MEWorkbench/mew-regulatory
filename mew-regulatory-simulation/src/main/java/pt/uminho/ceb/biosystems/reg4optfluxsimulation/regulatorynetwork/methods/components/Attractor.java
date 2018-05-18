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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import pt.ornrocha.collections.MTUCollectionsUtils;

public class Attractor implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> nodeids;
	private byte[] bytestate;
	private ArrayList<Boolean> booleanstate;
	private ArrayList<byte[]> attractorbytestatetrajectories;
	private ArrayList<ArrayList<Boolean>> attractorbooleanstatetrajectories; //includes states interval from Attractor (including steady state point)

	
	private ArrayList<String> constanttruestate;
	private ArrayList<String> constantefalsestate;
	private ArrayList<String> unequablestate; 
	private ArrayList<String> originalnodeidlist;
	
	

	public Attractor(ArrayList<String> identifiers, byte[] state) throws Exception{
		if(identifiers!=null && identifiers.size()!=state.length)
			throw new Exception("The identifiers and state vectors must be of same length");
		else{
			this.nodeids=identifiers;
			this.bytestate=state;
			this.booleanstate=MTUCollectionsUtils.convertbyteArrayToBooleanList(state);
		}
	}
	

	
	public Attractor(ArrayList<String> identifiers, ArrayList<Boolean> boolstate) throws Exception{
		if(identifiers!=null && identifiers.size()!=boolstate.size())
			throw new Exception("The identifiers and state vectors must be of same length");
		else{
			this.nodeids=identifiers;
			this.booleanstate=boolstate;
			this.bytestate=MTUCollectionsUtils.convertBooleanListTobyteArray(boolstate);
		}
	}
	
	
	public Attractor(ArrayList<String> identifiers, ArrayList<Boolean> boolstate, ArrayList<String> saveonly) throws Exception{
		if(identifiers!=null && identifiers.size()!=boolstate.size())
			throw new Exception("The identifiers and state vectors must be of same length");
		else{
			this.originalnodeidlist=identifiers;
			ArrayList<String> reducedids=new ArrayList<>();
			ArrayList<Boolean> reducedstates=new ArrayList<>(); 
			
			for (int i = 0; i < saveonly.size(); i++) {
				String id=saveonly.get(i);
				if(identifiers.contains(id)){
					int index=identifiers.indexOf(id);
					reducedids.add(id);
					reducedstates.add(boolstate.get(index));
				}
			}
			
			this.nodeids=reducedids;
			this.booleanstate=reducedstates;
			this.bytestate=MTUCollectionsUtils.convertBooleanListTobyteArray(reducedstates);
		}
	}
	
	
	

	public ArrayList<byte[]> getAttractorbyteStateTrajectories() {
		return attractorbytestatetrajectories;
	}

	public void setAttractorbyteStateTrajectories(ArrayList<byte[]> bytebasinofattraction) {
		this.attractorbytestatetrajectories = bytebasinofattraction;
		
		this.attractorbooleanstatetrajectories=new ArrayList<>(bytebasinofattraction.size());
		
		for (int i = 0; i < bytebasinofattraction.size(); i++) {
			byte[] state=bytebasinofattraction.get(i);
			ArrayList<Boolean> boolstate=MTUCollectionsUtils.convertbyteArrayToBooleanList(state);
			attractorbooleanstatetrajectories.add(boolstate);
		}
	}

	public ArrayList<ArrayList<Boolean>> getAttractorBooleanStateTrajectories() {
		return attractorbooleanstatetrajectories;
	}

	public void setAttractorBooleanStateTrajectories(ArrayList<ArrayList<Boolean>> booleanbasinofattraction) {
		if(originalnodeidlist!=null){
			this.attractorbooleanstatetrajectories=getReducedAttractorBooleanStateTrajectories(booleanbasinofattraction);
		}
		else
			this.attractorbooleanstatetrajectories = booleanbasinofattraction;
	}
	
	
	private ArrayList<ArrayList<Boolean>> getReducedAttractorBooleanStateTrajectories(ArrayList<ArrayList<Boolean>> traj){
		ArrayList<ArrayList<Boolean>> res=new ArrayList<>();
		
		for (int i = 0; i < traj.size(); i++) {
			ArrayList<Boolean> state=traj.get(i);
			ArrayList<Boolean> redstate=new ArrayList<>();
			for (int j = 0; j < nodeids.size(); j++) {
				int origindex=originalnodeidlist.indexOf(nodeids.get(j));
				redstate.add(state.get(origindex));
			}
			res.add(redstate);	
		}
		return res;
		
	}

	public byte[] getByteState() {
		return bytestate;
	}

	public ArrayList<Boolean> getBooleanState() {
		return booleanstate;
	}
	
	
	public ArrayList<String> getAssociatedNames() {
		return nodeids;
	}

	public boolean isEqualAttractor(Attractor tocompare, boolean comparebybyte){
		if(comparebybyte){
		  if(Arrays.equals(tocompare.getByteState(), getByteState()))
			  return true;
		}
		else{
			boolean[] outvector=ArrayUtils.toPrimitive(tocompare.getBooleanState().toArray(new Boolean[tocompare.getBooleanState().size()]));
			boolean[] intvector=ArrayUtils.toPrimitive(getBooleanState().toArray(new Boolean[getBooleanState().size()]));
			if(Arrays.equals(outvector, intvector))
				return true;
		}
		return false;
	}
	
	
	public ArrayList<String> getComponentsAlwaysTrueInAttractor() throws Exception{
		if(constanttruestate==null)
			calculateStateStability();
		return constanttruestate;
	}
	
	
	public ArrayList<String> getComponentsAlwaysFalseInAttractor() throws Exception{
		if(constantefalsestate==null)
			calculateStateStability();
		return constantefalsestate;
	}
	
	public ArrayList<String> getComponentsAlwaysInconstantInAttractor() throws Exception{
		if(unequablestate==null)
			calculateStateStability();
		return unequablestate;
	}
	
	
	private void calculateStateStability(){
		
		 constanttruestate=new ArrayList<>();
		 constantefalsestate=new ArrayList<>();
		 unequablestate=new ArrayList<>();
		 
		 ArrayList<ArrayList<Boolean>> cyclespace =attractorbooleanstatetrajectories;
		 if(cyclespace==null){
			 cyclespace=new ArrayList<>();
			 cyclespace.add(getBooleanState());
		 }
		 
		// if(cyclespace!=null)
			 for (int i = 0; i < nodeids.size(); i++) {
			    boolean alwaystrue = cyclespace.get(0).get(i);
				boolean alwaysfalse = !cyclespace.get(0).get(i);
				
				for(int j = 1; j < cyclespace.size() && (alwaysfalse || alwaystrue); j++){
					alwaystrue = alwaystrue && cyclespace.get(j).get(i);
					alwaysfalse = alwaysfalse && !cyclespace.get(j).get(i);
				}
				
				if(alwaysfalse){
					constantefalsestate.add(nodeids.get(i));
				}
				else if(alwaystrue){
					constanttruestate.add(nodeids.get(i));
				}
				else
					unequablestate.add(nodeids.get(i));
		}
		 
		
		
	}
	

}
