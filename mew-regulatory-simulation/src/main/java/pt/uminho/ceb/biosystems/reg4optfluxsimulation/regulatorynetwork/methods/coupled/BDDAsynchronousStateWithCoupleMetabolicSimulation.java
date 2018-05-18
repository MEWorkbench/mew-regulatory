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
import java.util.Set;

import org.colomoto.logicalmodel.LogicalModel;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.ManagerExceptionUtils;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.InvalidRegulatoryModelException;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatoryNetworkSimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.AbstractRegulatoryNetworkSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.logicalmodelextensions.LogicalModelSimulationStateMemory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.containers.AsynchronousSimulationResults;

public class BDDAsynchronousStateWithCoupleMetabolicSimulation extends AbstractRegulatoryNetworkSimulationMethod{

	private static final long serialVersionUID = 1L;
    private SimulationSteadyStateControlCenter simulationControlCenter;




	public BDDAsynchronousStateWithCoupleMetabolicSimulation(IRegulatoryNetwork model) throws InvalidRegulatoryModelException {
		super(model);
	}

	
	@Override
	public boolean simulationMethodSupportsRegulatoryNetworkType(IRegulatoryNetwork model) {
		if(model instanceof IRODDRegulatoryModel)
			return true;
		return false;
	}
	
	public SimulationSteadyStateControlCenter getMetabolicSimulationControlCenter() throws PropertyCastException, MandatoryPropertyException{
		return ManagerExceptionUtils.testCast(properties, SimulationSteadyStateControlCenter.class, RegulatoryNetworkSimulationProperties.METABOLICCONTROLCENTER, false);
	}
	
	public ArrayList<String> getUnconstrainedGenes() throws PropertyCastException, MandatoryPropertyException{
		return ManagerExceptionUtils.testCast(properties, ArrayList.class, RegulatoryNetworkSimulationProperties.UNCONSTRAINEDGENES, false);
	}

	@Override
	public IRegulatoryModelSimulationResult simulate() throws Exception {
		
		this.simulationControlCenter=getMetabolicSimulationControlCenter();

		InitialRegulatoryState initcontainer=getInitialRegulatoryState();
		
		//System.out.println(initcontainer.toString());
		
		 byte[] initialstates=initcontainer.getInitialbyteStateArray();
		 
		 Integer maxiter=getMaxIterations();
		 
		 ArrayList<String> unconstrainedgenes=getUnconstrainedGenes();


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
		 
		 
		 AsynchronousSimulatorWithCoupledMetabolicSimulation simulator=new AsynchronousSimulatorWithCoupledMetabolicSimulation((IRODDRegulatoryModel) model, initialstates,geneindexes, maxiter,simulationControlCenter);
		if(unconstrainedgenes!=null)
			simulator.setUnconstrainedGenes(unconstrainedgenes);
		 simulator.simulate();
		LogicalModelSimulationStateMemory memory=simulator.getStateMemoryContainer();
		
		
		
		return new AsynchronousSimulationResults(this.model,getInitialRegulatoryState(),memory);
	}

	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

}
