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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.synchronous;

import java.util.ArrayList;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.simulation.DeterministicUpdater;
import org.colomoto.logicalmodel.tool.simulation.updater.SequentialUpdater;

import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.SimulationWithMemory;

public class SequencialSimulationWithMemory extends SimulationWithMemory {

	protected  DeterministicUpdater updater;
	protected int maxiterations=100;
	protected boolean stopfirstattractor=false;
	
	public SequencialSimulationWithMemory(LogicalModel model, byte[] initialstate, ArrayList<Integer> saveindexes)
			throws Exception {
		super(model, initialstate, saveindexes);
        this.updater=new SequentialUpdater(model);
	}
	

	public SequencialSimulationWithMemory(LogicalModel model, byte[] initialstate, ArrayList<String> identifiers,
			ArrayList<Integer> saveindexes) throws Exception {
		super(model, initialstate, identifiers, saveindexes);
		 this.updater=new SequentialUpdater(model);
	}



	@Override
	public boolean checkAttractors() throws Exception {
		 int iter=0;
		 byte[] currentstate=initialstate;
		 boolean isattractor=false;
		 while (currentstate!=null) {	
			currentstate=updater.getSuccessor(currentstate);
			if(currentstate!=null){
				memory.addState(currentstate);
			    isattractor=memory.findAttractor();
			}
	
			if(stopfirstattractor && isattractor)
				currentstate=null;
			else if((iter+1)==maxiterations)
				currentstate=null;
			else
				iter++;
		 }
		 
		 return isattractor;
	}

}
