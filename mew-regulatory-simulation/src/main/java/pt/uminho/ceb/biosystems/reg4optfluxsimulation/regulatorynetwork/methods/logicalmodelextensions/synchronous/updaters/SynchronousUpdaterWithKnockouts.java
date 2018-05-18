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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.synchronous.updaters;

import java.util.ArrayList;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.simulation.updater.SynchronousUpdater;

public class SynchronousUpdaterWithKnockouts extends SynchronousUpdater{

	ArrayList<Integer> constantzerovars;
	
	public SynchronousUpdaterWithKnockouts(LogicalModel model) {
		super(model);
	}
	
	public SynchronousUpdaterWithKnockouts(LogicalModel model, ArrayList<Integer> constantzerovars) {
		super(model);
		this.constantzerovars=constantzerovars;
	}
	
	public void setConstantZeroVariableIndexes(ArrayList<Integer> constantzerovars) {
		this.constantzerovars=constantzerovars;
	}

	@Override
	 protected int nodeChange(byte[] state, int index) {
		if(constantzerovars!=null && constantzerovars.contains(index))
			return 0;
		else {
	        byte curState = state[index];
	        byte nextState = model.getTargetValue(index, state);

	        // now see if the node is willing to change it's state
	        if (nextState > curState){
	            return 1;
	        } else if (nextState < curState){
	            return -1;
	        }
	        return 0;
	    }
	}
	
}
