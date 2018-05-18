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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.asynchronous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.simulation.MultipleSuccessorsUpdater;
import org.colomoto.logicalmodel.tool.simulation.updater.AsynchronousUpdater;

public class AsynchronousSimulator extends MultipleSuccessorSimulationWithMemory {

	public HashSet<byte[]> hs;
	public HashSet<Integer> hascodes;
	
	public AsynchronousSimulator(LogicalModel model, byte[] initialstate, ArrayList<Integer> saveindexes,boolean stopfirstattractor) throws Exception {
		this(model,initialstate,saveindexes,null,stopfirstattractor);
	}
	
	
	public AsynchronousSimulator(LogicalModel model, byte[] initialstate, ArrayList<Integer> saveindexes, Integer maxiterations, boolean stopfirstattractor) throws Exception {
		super(model, initialstate, saveindexes, maxiterations,stopfirstattractor);
		hs = new HashSet<byte[]>();
		hascodes=new HashSet<Integer>();
	}
   
	
	
	
	
	@Override
	public void addState(byte[] state) throws Exception {
		int hash=state.hashCode();
		addNewState(state);
		if (!contains(state) ) {
			//hs.add(state);
			hascodes.add(hash);
			enqueue(state);	
		}
		
	}
	
	/*boolean contains(byte[] a) {
		boolean found = false;
		for(byte[] b: hs) {
			boolean idem = true;
			for(int i = 0; i < b.length; i++) {
				idem = idem && b[i] == a[i];
			}
			found = found || idem;
		}
		return found;
	}*/
	
	boolean contains(byte[] a) {
		for (byte[] b : hs) {
			if(Arrays.equals(b, a))
				return true;
		}
		return false;
	}
	
	boolean contains(int hascode) {
		 if(hascodes.contains(hascode))
				return true;
		return false;
	}

	@Override
	public void addTransition(byte[] from, byte[] to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkAttractors() throws Exception {
		return memory.findAttractor();
		
	}

	@Override
	public MultipleSuccessorsUpdater initializeUpdater(LogicalModel model) {
		return new AsynchronousUpdater(model);
	}
	


}
