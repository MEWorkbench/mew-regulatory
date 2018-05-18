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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.coupled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.simulation.MultipleSuccessorsUpdater;
import org.colomoto.logicalmodel.tool.simulation.SimulationStrategy;
import org.colomoto.logicalmodel.tool.simulation.updater.AsynchronousUpdater;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.asynchronous.MultipleSuccessorSimulationWithMemory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.asynchronous.QueuedState;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.containers.AsynchronousSimulationResults;

public class AsynchronousSimulatorWithCoupledMetabolicSimulation extends MultipleSuccessorSimulationWithMemory {

	public HashSet<byte[]> hs;
	public HashSet<Integer> hascodes;
	
	private InitialRegulatoryState initcontainer;
	private IRODDRegulatoryModel regnetw; // to avoid transformations
	private SimulationSteadyStateControlCenter simulationControlCenter;
	private ArrayList<String> unconstrainedgenesknockout;
	
	public AsynchronousSimulatorWithCoupledMetabolicSimulation(IRODDRegulatoryModel model, InitialRegulatoryState initcontainer, ArrayList<Integer> saveindexes, SimulationSteadyStateControlCenter simulationControlCenter) throws Exception {
		this(model,initcontainer.getInitialbyteStateArray(),saveindexes,null, simulationControlCenter);
		this.initcontainer=initcontainer;
	}
	
	
	public AsynchronousSimulatorWithCoupledMetabolicSimulation(IRODDRegulatoryModel model, byte[] initialstate, ArrayList<Integer> saveindexes, Integer maxiterations, SimulationSteadyStateControlCenter simulationControlCenter) throws Exception {
		super((LogicalModel) model, initialstate, saveindexes, maxiterations,false);
		hs = new HashSet<byte[]>();
		hascodes=new HashSet<Integer>();
		this.regnetw=(IRODDRegulatoryModel) model.copy();
		this.simulationControlCenter=simulationControlCenter;
	}
   
	public void setUnconstrainedGenes(ArrayList<String> genes) {
		this.unconstrainedgenesknockout=genes;
	}
	

	public void simulate() throws Exception {
        
		boolean attractorfound=false;

		addState(initialstate);
		
		double growthvalue=0.0;
		int iter=0;

		while (!queue.isEmpty() && iter<maxiter && growthvalue<=0.0 ) {
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
				attractorfound=memory.findAttractor();
				if(attractorfound) {
					System.out.println("ITERATION: "+iter);
					AsynchronousSimulationResults currentresults=new AsynchronousSimulationResults(regnetw, initcontainer, memory);
					ArrayList<String> genesknockout=currentresults.getKnockoutGenesList();
					growthvalue=biomassispositive(genesknockout);
				}

			}
			iter++;
		}
	}
	
	
	private double biomassispositive(ArrayList<String> genesknockout) throws Exception {
		
		IIntegratedStedystateModel integratedmodel=(IIntegratedStedystateModel) simulationControlCenter.getModel();
		
		
		if(unconstrainedgenesknockout!=null && unconstrainedgenesknockout.size()>0){
			for (int i = 0; i < unconstrainedgenesknockout.size(); i++) {
				if(!genesknockout.contains(unconstrainedgenesknockout.get(i)))
					genesknockout.add(unconstrainedgenesknockout.get(i));
			}
		}
		
		GeneChangesList metabolicgenes = new GeneChangesList(integratedmodel.filterOnlyMetabolicGenes(genesknockout)); 
		GeneregulatorychangesList regulatorygenes = new GeneregulatorychangesList(integratedmodel.filterOnlyRegulatoryGenes(genesknockout));
		RegulatoryGeneticConditions RegulatoryGeneConditionsNewKnockouts = new RegulatoryGeneticConditions(regulatorygenes, metabolicgenes, integratedmodel, false);
		
		simulationControlCenter.setGeneticConditions(RegulatoryGeneConditionsNewKnockouts);
		SteadyStateSimulationResult metabolicsolution = simulationControlCenter.simulate();
		
		System.out.println("BIOMASSSSSSSS: "+metabolicsolution.getOFvalue());

		return metabolicsolution.getOFvalue();
		
	}
	
	
	
	@Override
	public void addState(byte[] state) throws Exception {
		int hash=state.hashCode();
		addNewState(state);
		if (!contains(state) ) {
			hascodes.add(hash);
			enqueue(state);	
		}
		
	}
	

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
