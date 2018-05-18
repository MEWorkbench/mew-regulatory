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
import java.util.LinkedList;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.simulation.MultipleSuccessorsUpdater;
import org.colomoto.logicalmodel.tool.simulation.SimulationStrategy;

import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.SimulationWithMemory;

public abstract class MultipleSuccessorSimulationWithMemory extends SimulationWithMemory implements IMultipleSuccessorSimulationMemoryFeature{
	
	
	protected final LinkedList<QueuedState> queue;
	protected final MultipleSuccessorsUpdater updater;
	protected SimulationStrategy strategy;
	protected boolean stopfirsattractor=false;
	protected int maxiter=100;

	protected int max_depth = -1;
	protected int current_depth = 0;


	public MultipleSuccessorSimulationWithMemory(LogicalModel model, byte[] initialstate, ArrayList<Integer> saveindexes, Integer maxiterations, boolean stopfirstattractor) throws Exception {
		this(null,SimulationStrategy.DEPTH_FIRST,model,initialstate,saveindexes, maxiterations,stopfirstattractor);
	}

	public MultipleSuccessorSimulationWithMemory(MultipleSuccessorsUpdater updater, SimulationStrategy strategy,LogicalModel model, byte[] initialstate, ArrayList<Integer> saveindexes, Integer maxiterations,boolean stopfirstattractor) throws Exception {
		this(updater,strategy,model,initialstate,null,saveindexes, maxiterations,stopfirstattractor);
	}
	
	public MultipleSuccessorSimulationWithMemory(MultipleSuccessorsUpdater updater, SimulationStrategy strategy,LogicalModel model, byte[] initialstate, ArrayList<String> identifiers, ArrayList<Integer> saveindexes, Integer maxiterations,boolean stopfirstattractor) throws Exception {
		super(model, initialstate, identifiers, saveindexes);
		if(updater!=null)
			this.updater = updater;
		else
			this.updater=initializeUpdater(model);
		this.strategy = strategy;
		this.stopfirsattractor=stopfirstattractor;
		if(maxiterations!=null)
			this.maxiter=maxiterations;
		//System.out.println("Max iter: "+maxiter);
		queue = new LinkedList<QueuedState>();
	}
	
	public abstract MultipleSuccessorsUpdater initializeUpdater(LogicalModel model);
	
	
	public void setMaxIterations(Integer niters) {
		if(niters!=null)
			this.maxiter=niters;
	}
	

	public void simulate() throws Exception {
        
		boolean attractorfound=false;
		boolean stop=false;
		addState(initialstate);
		int iter=0;
		
		//System.out.println("STOP FIRST ATR: "+stopfirsattractor);
		//int inside=0;
		//while (!queue.isEmpty() && !attractorfound && iter<maxiter) {

		while (!queue.isEmpty()  && iter<maxiter && !stop) {
			QueuedState queued;
			if (this.strategy == SimulationStrategy.BREADTH_FIRST) {
				queued = queue.removeFirst();
			} else {
				queued = queue.removeLast();
			}

			byte[] state = queued.state;
			current_depth = queued.depth + 1;

			if (max_depth > 0 && current_depth > max_depth) {
				continue;
			}

			for(byte[] child : updater.getSuccessors(state)) {
				this.addState(child);
				//addTransition(state, child);
				attractorfound=memory.findAttractor();
				if(attractorfound) {
					//System.out.println("ITERATION: "+iter);
					if(stopfirsattractor)
						stop=true;
				}
			}
			iter++;
			//;
			//System.out.println(attractorfound);
			//System.out.println("#######inside "+inside);
			//System.out.println("############## "+iter);
			
		}
		//System.out.println("have attractor: "+attractorfound);
	}

	protected void enqueue(byte[] state) {
		this.queue.addLast( new QueuedState(state, current_depth) );
	}

	/**
	 * Add a state to the result of this simulation.
	 * If the state is new, it should be enqueued
	 *
	 * @param state
	 */
	public abstract void addState(byte[] state) throws Exception;

	/**
	 * Add a transition
	 *
	 * @param from source state
	 * @param to target state
	 */
	public abstract void addTransition(byte[] from, byte[] to);
}

/**
 * Associate a queued state with its depth
 */
	 /*class QueuedState {
		public final byte[] state;
		public final int depth;

		public QueuedState(byte[] state, int depth) {
			this.state = state;
			this.depth = depth;
	}
}*/
	


