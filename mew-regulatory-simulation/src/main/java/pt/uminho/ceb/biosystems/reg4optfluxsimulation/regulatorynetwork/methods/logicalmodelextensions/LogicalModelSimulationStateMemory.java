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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions;

import java.util.ArrayList;
import java.util.Arrays;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.ornrocha.printutils.MTUPrintUtils;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.Attractor;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.INetworkMemory;

public class LogicalModelSimulationStateMemory implements INetworkMemory{
	
	

	private static final long serialVersionUID = 1L;
	protected ArrayList<byte[]> statememory;
	
	protected int statelength=0;
	protected Integer initattractor=null;
	protected ArrayList<Attractor> attractorlist;
    protected ArrayList<String> orderedidentifiers;
    protected ArrayList<Integer> saveindexes;
	protected int nstate=0;
	protected boolean saveattractorstatespace=false;
	

	public LogicalModelSimulationStateMemory(ArrayList<Integer> savebyteindexes,ArrayList<String> orderedidentifiers) throws Exception{
		this.statememory=new ArrayList<>();
		this.attractorlist=new ArrayList<>();
		this.saveindexes=savebyteindexes;
		if(orderedidentifiers!=null)
		  this.orderedidentifiers=orderedidentifiers;
		else
			throw new Exception("The identifiers of components must be provided");
	}
	
	
	public void addState(byte[] state) throws Exception{

		boolean stop=false;
		if(statelength==0)
			this.statelength=state.length;
		else{
	       if(statelength!=state.length){
	    	   stop=true;
	    	   throw new Exception("The current input state size is different of the previous one");
	       }
		}
		
		if(!stop){
			statememory.add(state);
			nstate++;
		}
	}
	

	protected boolean checkAttractor() throws Exception{
		byte[] currentstate=statememory.get(statememory.size()-1);
		
		for (int i = 0; i < statememory.size()-2; i++) {
			byte[] previousstate=statememory.get(i);
			if(Arrays.equals(currentstate, previousstate)){
				initattractor=i;
				
				Attractor at=null;
				if(saveindexes!=null){
					byte[] reducedstate=getByteIndexes(currentstate);
					at=new Attractor(getbyteIdentifiers(), reducedstate);
				}
				else
					at=new Attractor(orderedidentifiers, currentstate);

				if(!existAttractor(at)){
				  attractorlist.add(at);
				  saveattractorstatespace=true;
				}
				return true;
			}
		}
		return false;
	}
	
	
	private byte[] getByteIndexes(byte[] currenstate){
		byte[] reduce=new byte[saveindexes.size()];
		for (int i = 0; i < saveindexes.size(); i++) {
			reduce[i]=currenstate[saveindexes.get(i)];
		}
		return reduce;
	}
	
	private ArrayList<String> getbyteIdentifiers(){
		ArrayList<String> res=new ArrayList<>();
		for (int i = 0; i < saveindexes.size(); i++) {
			res.add(orderedidentifiers.get(saveindexes.get(i)));
		}
		return res;
	}
	
	public boolean findAttractor() throws Exception{
		
		boolean haveattractor=checkAttractor();
		if(haveattractor && saveattractorstatespace){
			ArrayList<byte[]> basin=new ArrayList<>();
			for (int i = initattractor+1; i < statememory.size(); i++) {
				if(saveindexes!=null){
					byte[] reducedstate=getByteIndexes(statememory.get(i));
					basin.add(reducedstate);
				}
				else
					basin.add(statememory.get(i));
			}
			
			attractorlist.get(attractorlist.size()-1).setAttractorbyteStateTrajectories(basin);
			
		}
		saveattractorstatespace=false;
		return haveattractor;
	}
	
	
	
	protected boolean existAttractor(Attractor atr){
		if(attractorlist.size()>0){
			
			for (int i = 0; i < attractorlist.size(); i++) {
				Attractor eatr=attractorlist.get(i);
				if(eatr.isEqualAttractor(atr, true))
					return true;
			}
		}
		return false;
	}
	
	
	
	public ArrayList<Attractor> getAttractors() {
		return attractorlist;
	}
	
	
	public int getNumberAttractors(){
		return attractorlist.size();
	}
	
	public Attractor getAttractor(int index) throws Exception{
		if(index<attractorlist.size())
			return attractorlist.get(index);
		else
			throw new Exception("Invalid index at attractor list");
	}


	@Override
	public ArrayList<Attractor> getListOfAttractors() {
		return attractorlist;
	}


	@Override
	public ArrayList<String> getOrderIdentifiers() {
		return orderedidentifiers;
	}
	
	


	public ArrayList<Integer> getSaveIndexes() {
		return saveindexes;
	}


	@Override
	public ArrayList<byte[]> getbyteStateMemory() {
		return statememory;
	}


	@Override
	public ArrayList<ArrayList<Boolean>> getBooleanStateMemory() {
		if(statememory!=null){
			ArrayList<ArrayList<Boolean>> res=new ArrayList<>();
			for (int i = 0; i < statememory.size(); i++) {
				byte[] state=statememory.get(i);
				ArrayList<Boolean> set=MTUCollectionsUtils.convertbyteArrayToBooleanList(state);
				res.add(set);
			}
		    return res;
		}
		return null;
	}

	
	
	/*public ArrayList<String> calculateMemoryStateStability(GeneBehavior genestate){
		
		 ArrayList<String> constanttruestate=new ArrayList<>();
		 ArrayList<String> constantefalsestate=new ArrayList<>();
		 ArrayList<String>  unequablestate=new ArrayList<>();
		 
		 ArrayList<ArrayList<Boolean>> cyclespace =getBooleanStateMemory();
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
			 return constanttruestate;
		 else if(genestate.equals(GeneBehavior.off))
			 return constantefalsestate;
		 else
			 return unequablestate;
	
	}*/
	
	


	public static void main(String[] args) throws Exception {
		
		byte[] b1=new byte[]{1,0,0,1};
		byte[] b2=new byte[]{0,0,0,1};
		byte[] b3=new byte[]{1,1,0,1};
		byte[] b4=new byte[]{0,1,0,1};
		byte[] b5=new byte[]{1,0,0,1};
		byte[] b6=new byte[]{1,1,0,1};
		
		ArrayList<byte[]> list=new ArrayList<>(Arrays.asList(b1,b2,b3,b4,b5,b6));
		
		LogicalModelSimulationStateMemory memory=new LogicalModelSimulationStateMemory(null, new ArrayList<>(Arrays.asList("A","B","C","D")));
		
		 for (int i = 0; i < list.size(); i++) {
			memory.addState(list.get(i));
			System.out.println(memory.findAttractor());
		 }
		 
		 ArrayList<Attractor> listatr=memory.getAttractors();
		 
		 if(listatr.size()>0){
			 for (int i = 0; i < listatr.size(); i++) {
				Attractor atr=listatr.get(i);
				System.out.println("\n Basin Of attraction");
				ArrayList<byte[]> basin=atr.getAttractorbyteStateTrajectories();
				System.out.println(basin.size());
				for (int j = 0; j < basin.size(); j++) {
					MTUPrintUtils.printArrayofBytes(basin.get(j), "\t");
					System.out.println();
				}
			}
		 }

	}

}
