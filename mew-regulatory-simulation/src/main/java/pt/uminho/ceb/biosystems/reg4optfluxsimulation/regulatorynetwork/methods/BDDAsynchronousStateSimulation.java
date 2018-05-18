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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods;

import java.util.ArrayList;
import java.util.Set;

import org.colomoto.logicalmodel.LogicalModel;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.InvalidRegulatoryModelException;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.LogicalModelSimulationStateMemory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.asynchronous.AsynchronousSimulator;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.containers.AsynchronousSimulationResults;

public class BDDAsynchronousStateSimulation extends AbstractRegulatoryNetworkSimulationMethod{

	private static final long serialVersionUID = 1L;





	public BDDAsynchronousStateSimulation(IRegulatoryNetwork model) throws InvalidRegulatoryModelException {
		super(model);
	}

	
	@Override
	public boolean simulationMethodSupportsRegulatoryNetworkType(IRegulatoryNetwork model) {
		if(model instanceof IRODDRegulatoryModel)
			return true;
		return false;
	}

	@Override
	public IRegulatoryModelSimulationResult simulate() throws Exception {

		InitialRegulatoryState initcontainer=getInitialRegulatoryState();
		
		
		 byte[] initialstates=initcontainer.getInitialbyteStateArray();
		 
		 Boolean stopifattractor=isStopFirstAttractor();
		 Integer maxiter=getMaxIterations();
		 

		 AsynchronousSimulator simulator=null;
		 
		 LogicalModel model=null;
		 
		 if(initcontainer.getFixedGenesStateOff()!=null){
			 model=((LogicalModel) getModel()).clone();
			 Set<Integer> fixed=initcontainer.getFixedGenesStateOff();
			 for (Integer pos: fixed) {
				model.getNodeOrder().get(pos).setInput(true);
			 }
		 }
		 else
			 model=(LogicalModel) getModel();
		
		 IndexedHashMap<String,Integer> genes= ((IRODDRegulatoryModel)model).getGeneIndexes();
		 ArrayList<Integer> geneindexes=new ArrayList<>(genes.values());
		 
		 
		simulator=new AsynchronousSimulator(model, initialstates,geneindexes, maxiter,stopifattractor);
		simulator.simulate();
		LogicalModelSimulationStateMemory memory=simulator.getStateMemoryContainer();
		
		
		
		return new AsynchronousSimulationResults(this.model,getInitialRegulatoryState(),memory);
	}

	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

}
