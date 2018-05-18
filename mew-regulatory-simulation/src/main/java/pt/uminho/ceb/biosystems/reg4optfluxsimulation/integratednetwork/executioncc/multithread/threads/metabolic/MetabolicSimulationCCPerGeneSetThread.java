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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.threads.metabolic;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.SimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic.ListMetabolicSolutions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic.MetabolicReducedSimulationResultsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic.ReducedGeneSteadyStateSimulationResult;

public class MetabolicSimulationCCPerGeneSetThread extends MetabolicMultiGenesReusableSimulationThread<ListMetabolicSolutions>{

	//public class MetabolicSimulationCCPerGeneSetThread extends MetabolicReusableSimulationThread<ArrayList<MetabolicSimulationResultsContainer>>{	
	public MetabolicSimulationCCPerGeneSetThread(ISteadyStateGeneReactionModel model,
			EnvironmentalConditions environmentalConditions, SimulationOptionsContainer simulationoptions) {
		super(model, environmentalConditions, simulationoptions);
		// TODO Auto-generated constructor stub
	}
	
	

	public MetabolicSimulationCCPerGeneSetThread(ISteadyStateGeneReactionModel model,
			EnvironmentalConditions environmentalConditions, SimulationOptionsContainer simulationoptions,
			IndexedHashMap<String, RegulatoryGeneticConditions> geneticConditions) {
		super(model, environmentalConditions, simulationoptions, geneticConditions);
		// TODO Auto-generated constructor stub
	}



	@Override
	public ListMetabolicSolutions getSimulationResults() {
		return results;
	}

	@Override
	protected ListMetabolicSolutions executeSimulationProcess() throws Exception {

		ListMetabolicSolutions res=new ListMetabolicSolutions(geneticConditions.size());
		
		for (int i = 0; i < geneticConditions.size(); i++) {
			
			String geneid=geneticConditions.getKeyAt(i);
			
			RegulatoryGeneticConditions geneconds=geneticConditions.getValueAt(i);
			
			controlcenter.setGeneticConditions(geneconds);
			SteadyStateSimulationResult simures=controlcenter.simulate();
			LogMessageCenter.getLogger().toClass(getClass()).addInfoMessage("Cond: "+currentEnvironmentalConditionname+" Gene: "+geneconds+" sol: "+simures.getOFvalue());
			res.add(new MetabolicReducedSimulationResultsContainer(ReducedGeneSteadyStateSimulationResult.newInstance(simures), geneid,currentEnvironmentalConditionname));
			
		}
		
		return res;
	}

}
