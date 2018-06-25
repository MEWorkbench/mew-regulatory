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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import pt.ornrocha.printutils.MTUPrintUtils;



public class OptFluxRegulatoryNetworkMemory implements INetworkMemory{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The size of the memory. The number of steps that the memory can hold. */
	//private static final int LOOKBACK = 10;
	/** <code>boolean</code> matrix implementation of the memory for CPU performance and VM memory saving. */
	private ArrayList<ArrayList<Boolean>> statememory;
	/** The remaining capacity of the memory at any given time. */
	//private int capacity = LOOKBACK;
	private int capacity = 20;
	/** The length of each network stored in memory.  */
	private int net_length;
	/** The total number of iterations executed over the memory. */
	private int iterations;
	
	private ArrayList<String> orderedidentifiers;
	private ArrayList<String> saveonlyidentifiersinattractors;
	
	private ArrayList<Attractor> attractorlist;
	
	private boolean saveattractorstatespace=false;
	
	/**
	 * @param sim the <code>BooleanNetworkSimulation</code> that contains the network to be queued. 
	 * @throws Exception 
	 */
	public OptFluxRegulatoryNetworkMemory(int numberiter,ArrayList<Boolean> firstStep, ArrayList<String> orderedgeneidentifiers) throws Exception{
		capacity=numberiter;
		statememory = new ArrayList<ArrayList<Boolean>>();
		attractorlist=new ArrayList<>();
		statememory.add(firstStep);
		net_length = firstStep.size();
		if(orderedgeneidentifiers!=null)
			this.orderedidentifiers=orderedgeneidentifiers;
		else
			throw new Exception("The identifiers of components must be provided");
	}
	
	public OptFluxRegulatoryNetworkMemory(ArrayList<Boolean> firstStep, ArrayList<String> orderedgeneidentifiers) throws Exception{
		this(20,firstStep,orderedgeneidentifiers);
	}
	
	
	public void setIdentifiersToSaveInAttractors(ArrayList<String> identifiers){
		if(identifiers!=null)
			saveonlyidentifiersinattractors=identifiers;
		
	}

	/**
	 * @return the lOOKBACK value for this queue.
	 */
	/*public static int getLOOKBACK() {
		return LOOKBACK;
	}*/
	
	/**
	 * Inserts a new set of values at the top of the queue.
	 * 
	 * @param exp the new set of values
	 * @throws Exception exception
	 */
	public void addState(ArrayList<Boolean> exp) throws Exception{

		if(exp.size() == net_length){
			
		if(capacity==0)
			statememory.remove(statememory.size()-1);
		
		statememory.add(null);
		for(int i = statememory.size()-2; i >= 0; i--){
			ArrayList<Boolean> expAux = statememory.get(i);
			statememory.set(i+1, expAux);
		}
		statememory.set(0, exp);
		
		if(capacity>0) 
			capacity--;
		
	   
	  }
	  else
		throw new Exception("The size of input arraylist is different of previous network length");
	}
	
	
	/*public void addState(ArrayList<Boolean> exp) throws Exception{

		if(exp.size() == net_length){
		if(capacity==0)
			statememory.remove(statememory.size()-1);
		
		statememory.add(null);
		for(int i = statememory.size()-2; i >= 0; i--){
			ArrayList<Boolean> expAux = statememory.get(i);
			statememory.set(i+1, expAux);
		}
		statememory.set(0, exp);
		
		if(capacity>0) 
			capacity--;
		
	   
	  }
	  else
		throw new Exception("The size of input arraylist is different of previous network length");
	}
	*/
	
	
	/**
	 * @return true if the queue is full, false otherwise.
	 */
	/*public boolean isFull(){
		return capacity == 0;
	}*/
	
	/**
	 * @return the set of values at the bottom of the queue.
	 */
	public ArrayList<Boolean> lookTop(){	
		ArrayList<Boolean> toret =statememory.get(0);
		return toret;		                     
	}
	
	/**
	 * Search for repeated sets of values within the queue.
	 * 
	 * @return i the length of the detected cycle or -1 if no cycles detected.
	 * @throws Exception 
	 */
	public int checkRedundancy() throws Exception {
		
	
		ArrayList<Boolean> first = statememory.get(0);
		for(int i = 1;i<statememory.size();i++){
			if(first.equals(statememory.get(i))){
				
				Attractor at=null;
				if(saveonlyidentifiersinattractors!=null)
					at=new Attractor(orderedidentifiers, first, saveonlyidentifiersinattractors);
				else
					at=new Attractor(orderedidentifiers, first);
				
				if(!existAttractor(at)){
					attractorlist.add(at);
					saveattractorstatespace=true;
				}
				
				return i;
			}
		}
		return -1;
	}
	
	
	public int checkAttractor() throws Exception{
		
		int attractorpos=checkRedundancy();
		if(attractorpos!=-1 && saveattractorstatespace){
			attractorlist.get(attractorlist.size()-1).setAttractorBooleanStateTrajectories(getCycle());
		}
		saveattractorstatespace=false;
		return attractorpos;
	}
	
	
	
	@Override
	public boolean findAttractor() throws Exception {
        int value=checkAttractor();
        if(value!=-1)
        	return true;
		return false;
	}
	
	
	
	
	protected boolean existAttractor(Attractor atr){
		if(attractorlist.size()>0){
			
			for (int i = 0; i < attractorlist.size(); i++) {
				Attractor eatr=attractorlist.get(i);
				if(eatr.isEqualAttractor(atr, false))
					return true;
			}
		}
		return false;
	}
	

	/**
	 * @return the iterations
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @param iterations the iterations to set
	 */
	public void addIteration() {
		this.iterations++;
	}
	
//	/**
//	 * Clears the <code>NetworkMemory</code>.
//	 * 
//	 * @return the free memory in the JVM.
//	 */
//	public long clearMemory(){
//		memory = null;
//		System.gc(); //NOTE: Force the garbage collector to run now.		 
//		memory = new boolean[LOOKBACK][net_length];
//		iterations = 0;
//		capacity = LOOKBACK;
//		return Runtime.getRuntime().freeMemory(); //NOTE: calculate the total amount of free memory in the JVM... maybe unnecessary...
//	}
	
	public int size(){
		return statememory.size();
	}
	
	public ArrayList<ArrayList<Boolean>> getCycle() throws Exception{
		ArrayList<ArrayList<Boolean>> ret = new ArrayList<ArrayList<Boolean>>();
		int cycleLenght = checkRedundancy();
		if(cycleLenght <= 0)
			ret.add(statememory.get(0));
		else{
			for(int i = 0; i < cycleLenght; i++){
				ret.add(statememory.get(i));
			}
			
			if(ret.size()>1)
				Collections.reverse(ret);
		}
		
		return ret;
	}
	
	public ArrayList<ArrayList<Boolean>> getLastNSteps(int n){
		ArrayList<ArrayList<Boolean>> ret = new ArrayList<ArrayList<Boolean>>();
		for(int i = 0; i < n ;i++){
			ret.add(statememory.get(i));
		}
		return ret;
	}
	
	public ArrayList<Boolean> getStepPosition(int pos){
		if(pos<statememory.size()){
			return statememory.get(pos);
		}
		return null;
	}

	public String toString(){
		String text = "";
		for(int i = 0; i < statememory.size(); i++){
			text += statememory.get(i).toString()+ "\n";
		}
		return text;
	}

	@Override
	public ArrayList<Attractor> getListOfAttractors() {
		return attractorlist;
	}
	
	@Override
	public int getNumberAttractors(){
		return attractorlist.size();
	}
	
	@Override
	public Attractor getAttractor(int index) throws Exception{
		if(index<attractorlist.size())
			return attractorlist.get(index);
		else
			throw new Exception("Invalid index at attractor list");
	}

	@Override
	public ArrayList<String> getOrderIdentifiers() {
		return orderedidentifiers;
	}

	@Override
	public ArrayList<byte[]> getbyteStateMemory() {
		return null;
	}

	@Override
	public ArrayList<ArrayList<Boolean>> getBooleanStateMemory() {
		return statememory;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		ArrayList<Boolean> b1=new ArrayList<>(Arrays.asList(new Boolean[]{true,false,false,true}));
		ArrayList<Boolean> b2=new ArrayList<>(Arrays.asList(new Boolean[]{false,false,false,true}));
		ArrayList<Boolean> b3=new ArrayList<>(Arrays.asList(new Boolean[]{true,true,false,true}));
		ArrayList<Boolean> b4=new ArrayList<>(Arrays.asList(new Boolean[]{false,true,false,true}));
		ArrayList<Boolean> b5=new ArrayList<>(Arrays.asList(new Boolean[]{true,false,false,true}));
		ArrayList<Boolean> b6=new ArrayList<>(Arrays.asList(new Boolean[]{true,true,false,true}));
		
		ArrayList<ArrayList<Boolean>> list=new ArrayList<>(Arrays.asList(b2,b3,b4,b5,b6));
		
		OptFluxRegulatoryNetworkMemory memory=new OptFluxRegulatoryNetworkMemory(10,b1, new ArrayList<>(Arrays.asList("A","B","C","D")));
		
		 for (int i = 0; i < list.size(); i++) {
			memory.addState(list.get(i));
			
		 }
		 
		 ArrayList<Attractor> listatr=memory.getListOfAttractors();
		 
		 if(listatr.size()>0){
			 for (int i = 0; i < listatr.size(); i++) {
				Attractor atr=listatr.get(i);
			
				ArrayList<ArrayList<Boolean>> basin=atr.getAttractorBooleanStateTrajectories();
			
				for (int j = 0; j < basin.size(); j++) {
					MTUPrintUtils.printListOfBooleanValues(basin.get(j), "\t");
					
				}
			}
		 }

	}

	@Override
	public ArrayList<Integer> getSaveIndexes() {
		// TODO Auto-generated method stub
		return null;
	}

	
}


