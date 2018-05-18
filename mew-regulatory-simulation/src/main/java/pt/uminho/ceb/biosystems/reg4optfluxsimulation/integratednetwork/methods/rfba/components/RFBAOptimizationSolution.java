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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.components;

import java.util.ArrayList;

import org.javatuples.Pair;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;

public class RFBAOptimizationSolution {
	
	
	   protected ArrayList<String> indexvarname;
	   protected ArrayList<IndexedHashMap<String, Boolean>> stateresults=new ArrayList<>();
	   protected ArrayList<IndexedHashMap<String, Boolean>> generesults=new ArrayList<>();
	   protected ArrayList<IndexedHashMap<String, Boolean>> varresults=new ArrayList<>();
	   protected int cyclestart=-1;
	   protected SteadyStateSimulationResult fbasolution;
	   protected ArrayList<String> deletedGenes;
	   protected Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> startstep;
	 
	   
	   public RFBAOptimizationSolution(ArrayList<String> indexofelems){
		   this.indexvarname=indexofelems;
	   }
	   
	   public void setCycleStart(int v){
		   this.cyclestart=v;
	   }
	   
	   public void setFBaSolution(SteadyStateSimulationResult solution){
		   this.fbasolution=solution;
	   }
	   
	   public SteadyStateSimulationResult getFbasolution() {
		return fbasolution;
	}
	   
	   public ArrayList<String> getDeletedGenes() {
		return deletedGenes;
	}

	public void setDeletedGenes(ArrayList<String> deletedGenes) {
		this.deletedGenes = deletedGenes;
	}
	
	public void setStartStepState(IndexedHashMap<String, Boolean> genestates, IndexedHashMap<String, Boolean> varstates){
		this.startstep=new Pair<IndexedHashMap<String,Boolean>, IndexedHashMap<String,Boolean>>(genestates, varstates);
	}

	

	public Pair<IndexedHashMap<String, Boolean>, IndexedHashMap<String, Boolean>> getStartstep() {
		return startstep;
	}

	public void appendStateResult(IndexedHashMap<String, Boolean> genestates, IndexedHashMap<String, Boolean> varstates){
		   IndexedHashMap<String, Boolean> res=new IndexedHashMap<>();
		   
		   for (int i = 0; i < genestates.size(); i++) {
			 res.put(genestates.getKeyAt(i), genestates.getValueAt(i));
		   }
		   generesults.add(genestates);
		   
		   if(varstates!=null){
		   for (int i = 0; i < varstates.size(); i++) {
			   res.put(varstates.getKeyAt(i), varstates.getValueAt(i));
		     }
		   varresults.add(varstates);
		   }
		   
		   stateresults.add(res);
	   }
	

}
