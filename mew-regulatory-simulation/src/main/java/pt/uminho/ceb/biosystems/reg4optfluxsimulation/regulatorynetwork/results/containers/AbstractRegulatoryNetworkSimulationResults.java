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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.containers;

import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.Attractor;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.INetworkMemory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public abstract class AbstractRegulatoryNetworkSimulationResults implements IRegulatoryModelSimulationResult{
	
    
	public enum GeneBehavior {
		
		on,
		off,
		undefined;
		
	}
	
	
	private static final long serialVersionUID = 1L;
	protected IRegulatoryNetwork model;
	protected InitialRegulatoryState initialregulatorystate;
	//protected ArrayList<Attractor> attractors;
	protected INetworkMemory memorycontainer;
	protected IndexedHashMap<GeneBehavior, ArrayList<String>> genesbehavior;

	//public AbstractRegulatoryNetworkSimulationResults(IRegulatoryNetwork model, InitialRegulatoryState initialregulatorystate, ArrayList<Attractor> attractors){
	public AbstractRegulatoryNetworkSimulationResults(IRegulatoryNetwork model, InitialRegulatoryState initialregulatorystate, INetworkMemory memorycontainer){
		this.model=model;
		this.initialregulatorystate=initialregulatorystate;
		this.memorycontainer=memorycontainer;
	}
	

	

	@Override
	public IRegulatoryNetwork getRegulatoryNetworkModel() {
		return model;
	}

	@Override
	public ArrayList<Attractor> getAttractors() {
		if(memorycontainer!=null)
			return memorycontainer.getListOfAttractors();
		return null;
	}

	@Override
	public InitialRegulatoryState getInitialRegulatoryState() {
		return initialregulatorystate;
	}
	
	@Override
	public ArrayList<String> getRegulatoryModelComponentIdentifiers() {
		return initialregulatorystate.getOrderedIdentifiers();
	}
	
	
	
	
	public INetworkMemory getNetworkMemorycontainer() {
		return memorycontainer;
	}


	public ArrayList<String> getKnockoutGenesList() throws Exception {
		ArrayList<String> res=new ArrayList<>();
		ArrayList<Attractor> attractors=getAttractors();
		
		if(attractors!=null && attractors.size()>1){
			
			ArrayList<ArrayList<String>> commongenesoff=getStableGeneBehaviorAlongAttractors(attractors, GeneBehavior.off);
			
			ArrayList<String> onesample=commongenesoff.get(0);
			for (int i = 0; i < onesample.size(); i++) {
				String geneid=onesample.get(i);
				boolean addcommon=true;
				for (int j = 1; j < commongenesoff.size(); j++) {
					if(!commongenesoff.get(j).contains(geneid))
						addcommon=false;
				}
				if(addcommon)
					res.add(geneid);
			}
			return checkGeneIds(res);
		}
		else if(attractors!=null && attractors.size()==1){
			return checkGeneIds(attractors.get(0).getComponentsAlwaysFalseInAttractor());
			
		}
		else
			return res;
	}
	
	public ArrayList<String> getCommonGeneStateInAttractors(GeneBehavior behavior) throws Exception {
		ArrayList<String> res=new ArrayList<>();
		ArrayList<Attractor> attractors=getAttractors();
		
		if(attractors!=null && attractors.size()>1){
			
			ArrayList<ArrayList<String>> commongenestate=getStableGeneBehaviorAlongAttractors(attractors, behavior);
			
			ArrayList<String> onesample=commongenestate.get(0);
			for (int i = 0; i < onesample.size(); i++) {
				String geneid=onesample.get(i);
				boolean addcommon=true;
				for (int j = 1; j < commongenestate.size(); j++) {
					if(!commongenestate.get(j).contains(geneid))
						addcommon=false;
				}
				if(addcommon)
					res.add(geneid);
			}
			return checkGeneIds(res);
		}
		else if(attractors!=null && attractors.size()==1){
			if(behavior.equals(GeneBehavior.off))
				return checkGeneIds(attractors.get(0).getComponentsAlwaysFalseInAttractor());
			else if(behavior.equals(GeneBehavior.on))
				return checkGeneIds(attractors.get(0).getComponentsAlwaysTrueInAttractor());
			else
				return checkGeneIds(attractors.get(0).getComponentsAlwaysInconstantInAttractor());
			
		}
		else
			return res;
	}
	
	
	public boolean getGeneStateInAttractors(String geneid) throws Exception{
		
		if(genesbehavior!=null){
			ArrayList<String> geneson=genesbehavior.get(GeneBehavior.on);
			if(geneson.contains(geneid))
				return true;
			else
				return false;
		}
		else{
			genesbehavior=new IndexedHashMap<>();
			genesbehavior.put(GeneBehavior.on, getCommonGeneStateInAttractors(GeneBehavior.on));
			genesbehavior.put(GeneBehavior.off, getCommonGeneStateInAttractors(GeneBehavior.off));
			genesbehavior.put(GeneBehavior.undefined, getCommonGeneStateInAttractors(GeneBehavior.undefined));
			
			ArrayList<String> geneson=genesbehavior.get(GeneBehavior.on);
			if(geneson.contains(geneid))
				return true;
			else
				return false;
		}
		
		
	}
	
	public ArrayList<String> checkGeneIds(ArrayList<String> currentgeneids){
		ArrayList<String> res=new ArrayList<>();
		ArrayList<String> modelgeneids=model.getRegulatorIDs();
		for (int i = 0; i < currentgeneids.size(); i++) {
			String cr=currentgeneids.get(i);
			if(!modelgeneids.contains(cr)){
				res.add(model.getRegulatorIDAssociatedToRuleID(cr));
			}
			else
				res.add(cr);
		}
		return res;
	}
	
	
	
	
	
	public static ArrayList<ArrayList<String>> getStableGeneBehaviorAlongAttractors(ArrayList<Attractor> attractors, GeneBehavior genebehavior) throws Exception{
		
		ArrayList<ArrayList<String>> commonbehavior=new ArrayList<>();
		for (int i = 0; i < attractors.size(); i++) {
			if(genebehavior.equals(GeneBehavior.on))
				commonbehavior.add(attractors.get(i).getComponentsAlwaysTrueInAttractor());
			else if(genebehavior.equals(GeneBehavior.off))
				commonbehavior.add(attractors.get(i).getComponentsAlwaysFalseInAttractor());
			else
				commonbehavior.add(attractors.get(i).getComponentsAlwaysInconstantInAttractor());
		}
		return commonbehavior;
	}
	
	
	
	
	
	public ArrayList<String> calculateMemoryStateStability(GeneBehavior genestate){
		
		 ArrayList<String> constanttruestate=new ArrayList<>();
		 ArrayList<String> constantefalsestate=new ArrayList<>();
		 ArrayList<String>  unequablestate=new ArrayList<>();
		 ArrayList<String> orderedidentifiers=memorycontainer.getOrderIdentifiers();
		 ArrayList<Integer> saveindexes=memorycontainer.getSaveIndexes();
		 
		 ArrayList<ArrayList<Boolean>> cyclespace =memorycontainer.getBooleanStateMemory();
		 for (int i = 0; i < orderedidentifiers.size(); i++) {
			    boolean alwaystrue = cyclespace.get(0).get(i);
				boolean alwaysfalse = !cyclespace.get(0).get(i);
				
				for(int j = 1; j < cyclespace.size() && (alwaysfalse || alwaystrue); j++){
					alwaystrue = alwaystrue && cyclespace.get(j).get(i);
					alwaysfalse = alwaysfalse && !cyclespace.get(j).get(i);
				}
				
				if(alwaysfalse && saveindexes.contains(i)){
					constantefalsestate.add(orderedidentifiers.get(i));
				}
				else if(alwaystrue && saveindexes.contains(i)){
					constanttruestate.add(orderedidentifiers.get(i));
				}
				else if(saveindexes.contains(i))
					unequablestate.add(orderedidentifiers.get(i));
		}
		 
		 if(genestate.equals(GeneBehavior.on))
			 return checkGeneIds(constanttruestate);
		 else if(genestate.equals(GeneBehavior.off))
			 return checkGeneIds(constantefalsestate);
		 else
			 return checkGeneIds(unequablestate);
	
	}
	
/*	public static IndexedHashMap<RegulatoryGene, Boolean> getGeneStateBetweenAtractors(ArrayList<Attractor> atractors){
   	 
   	  int numberatractores=atractors.size();
   	  
   	  for (int i = 0; i < genemapindex.size(); i++) {
   		  boolean state=true;
   		  RegulatoryGene g=genemapindex.getValueAt(i);
   		  
   		  for (int j = 0; j < numberatractores; j++) {
					state &=atractors.get(j).get(i);
				}
   		  
   		  res.put(g, state);
		   }
   	return res;
   }*/


}
