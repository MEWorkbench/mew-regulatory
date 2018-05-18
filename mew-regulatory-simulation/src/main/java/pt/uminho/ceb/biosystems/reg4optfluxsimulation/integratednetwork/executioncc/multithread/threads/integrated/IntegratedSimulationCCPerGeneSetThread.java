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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.threads.integrated;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.SimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.integrated.IntegratedReducedSimulationResultsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.integrated.ListIntegratedReducedSolutions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;

public class IntegratedSimulationCCPerGeneSetThread extends IntegratedMultiGenesReusableSimulationThread<ListIntegratedReducedSolutions>{


	
	
	public IntegratedSimulationCCPerGeneSetThread(ISteadyStateGeneReactionModel model,
			EnvironmentalConditions environmentalConditions, SimulationOptionsContainer simulationoptions) {
		super(model, environmentalConditions, simulationoptions);
	}
	
	

	public IntegratedSimulationCCPerGeneSetThread(ISteadyStateGeneReactionModel model,
			EnvironmentalConditions environmentalConditions, SimulationOptionsContainer simulationoptions,
			IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions) {
		super(model, environmentalConditions, simulationoptions, geneticConditions);
	}



	@Override
	public ListIntegratedReducedSolutions getSimulationResults() {
		return results;
	}

	@Override
	protected ListIntegratedReducedSolutions executeSimulationProcess() throws Exception {

		ListIntegratedReducedSolutions res=new ListIntegratedReducedSolutions(geneticConditions.size());
		
		for (int i = 0; i < geneticConditions.size(); i++) {
			
			String geneid=geneticConditions.getKeyAt(i);
			
			RegulatoryGeneticConditions geneconds=geneticConditions.getValueAt(i);
			
			controlcenter.setGeneticConditions(geneconds);
			
			
			try {
				IntegratedSimulationMethodResult simures=(IntegratedSimulationMethodResult) controlcenter.simulate();
			    LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Cond: "+currentEnvironmentalConditionname+" Gene: "+geneconds+" sol: "+simures.getOFvalue());
			    res.add(new IntegratedReducedSimulationResultsContainer(simures, geneid, currentEnvironmentalConditionname));
			    	
			} catch (Exception e) {
				res.add(new IntegratedReducedSimulationResultsContainer(null, 0.0, "0.0", LPSolutionType.INFEASIBLE, geneid, currentEnvironmentalConditionname));
				LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Cond: "+currentEnvironmentalConditionname+" Gene: "+geneconds+" sol: "+"0.0");
				//LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage(e);
			}
			
			
		}
		
		return res;
	}

}
