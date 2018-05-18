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
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;

public abstract class SimulationWithMemory {

	
	
	
	  protected LogicalModel model;
	  protected byte[] initialstate;
	  protected ArrayList<String> identifiers;
	  protected ArrayList<Integer> saveindexes;
	  protected LogicalModelSimulationStateMemory memory;

	  
	  
	  public SimulationWithMemory(LogicalModel model, byte[] initialstate, ArrayList<Integer> saveindexes) throws Exception{
		  this.model=model;
		  this.initialstate=initialstate;
		  this.saveindexes=saveindexes;
		  loadIdentifiers();
		  memory=new LogicalModelSimulationStateMemory(saveindexes, identifiers);
	  }
	
	  
	  public SimulationWithMemory(LogicalModel model, byte[] initialstate, ArrayList<String> identifiers, ArrayList<Integer> saveindexes) throws Exception{
		  this.model=model;
		  this.initialstate=initialstate;
		  if(identifiers!=null)
		    this.identifiers=identifiers;
		  else
			  loadIdentifiers();
		  this.saveindexes=saveindexes;
		  memory=new LogicalModelSimulationStateMemory(saveindexes, this.identifiers);
		  //System.out.println(this.identifiers);
	  }
	  
	  
	  protected void loadIdentifiers(){
		  List<NodeInfo> nodesid=model.getNodeOrder();
		  identifiers=new ArrayList<>();
		  for (NodeInfo nodeInfo : nodesid) {
			 identifiers.add(nodeInfo.getNodeID());
		  }
	  }
	  
	  public abstract boolean checkAttractors()  throws Exception;
	  
	  
	  public  LogicalModelSimulationStateMemory getStateMemoryContainer(){
		 return memory;  
	  }
	  
	  
	  public void addNewState(byte[] state) throws Exception{
		memory.addState(state);
	  }
 
      
	
}
