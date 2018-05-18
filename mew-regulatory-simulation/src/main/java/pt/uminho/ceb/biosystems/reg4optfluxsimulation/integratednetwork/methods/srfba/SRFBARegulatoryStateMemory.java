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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.srfba;

import java.util.ArrayList;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.Attractor;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.INetworkMemory;

public class SRFBARegulatoryStateMemory implements INetworkMemory{

	
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Attractor> listAttractors;
	private ArrayList<String> orderedidentifiers;
	private ArrayList<Integer> saveindexes;
	private ArrayList<ArrayList<Boolean>> statememory;
	
	public SRFBARegulatoryStateMemory(ArrayList<String> identifiers, ArrayList<Boolean> state) throws Exception{
		fillInformation(identifiers, state);
	}
	
	private void fillInformation(ArrayList<String> identifiers, ArrayList<Boolean> state) throws Exception{
		this.statememory=new ArrayList<>();
		this.statememory.add(state);
		this.orderedidentifiers=identifiers;
		
		this.listAttractors=new ArrayList<Attractor>();
		Attractor attractor=new Attractor(identifiers, state);
		listAttractors.add(attractor);
	}

	@Override
	public ArrayList<Attractor> getListOfAttractors() {
		return listAttractors;
	}

	@Override
	public int getNumberAttractors() {
		return listAttractors.size();
	}

	@Override
	public Attractor getAttractor(int index) throws Exception {
		return listAttractors.get(index);
	}

	@Override
	public ArrayList<String> getOrderIdentifiers() {
		return orderedidentifiers;
	}

	@Override
	public ArrayList<Integer> getSaveIndexes() {
		return saveindexes;
	}

	@Override
	public ArrayList<byte[]> getbyteStateMemory() {
		ArrayList<byte[]> bytememory=new ArrayList<>();
		bytememory.add(MTUCollectionsUtils.convertBooleanListTobyteArray(getBooleanStateMemory().get(0)));
		return bytememory;

	}

	@Override
	public ArrayList<ArrayList<Boolean>> getBooleanStateMemory() {
		return statememory;
	}

	@Override
	public boolean findAttractor() throws Exception {
		return true;
	}

}
