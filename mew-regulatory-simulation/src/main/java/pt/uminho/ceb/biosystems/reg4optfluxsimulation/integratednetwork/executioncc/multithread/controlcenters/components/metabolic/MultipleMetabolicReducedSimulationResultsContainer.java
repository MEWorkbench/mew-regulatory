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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.metabolic;

import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components.AbstractMultipleSimulationResultsContainer;

public class MultipleMetabolicReducedSimulationResultsContainer extends AbstractMultipleSimulationResultsContainer<MetabolicReducedSimulationResultsContainer> {
	
	
	public MultipleMetabolicReducedSimulationResultsContainer() {super();}

	
	
	public void appendResult(String environmentalconditionid,String knockoutedgeneid, ReducedGeneSteadyStateSimulationResult simulationresults){
		appendResult(environmentalconditionid, knockoutedgeneid, new MetabolicReducedSimulationResultsContainer(simulationresults, knockoutedgeneid, environmentalconditionid));
	}
	
	public void appendResults(ListMetabolicSolutions listmetabolicsolutions){
		
		for (int i = 0; i < listmetabolicsolutions.size(); i++) {
			MetabolicReducedSimulationResultsContainer container=listmetabolicsolutions.get(i);
			appendResult(container.getEnvironmentalConditionId(), container.getKnockoutedGeneId(), container);
			/*MapOfResults.insertNewValue(groupresults,container.getEnvironmentalConditionId(),container.getKnockoutedGeneId(),container);
			if(!groupresultskeyslist.contains(container.getEnvironmentalConditionId()))
				groupresultskeyslist.add(container.getEnvironmentalConditionId());	*/
		}
	}
}
