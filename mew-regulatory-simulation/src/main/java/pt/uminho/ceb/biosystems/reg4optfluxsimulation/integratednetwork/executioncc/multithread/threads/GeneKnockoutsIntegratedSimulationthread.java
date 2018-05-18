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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.threads;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.components.IntegratedSimulationThread;

public class GeneKnockoutsIntegratedSimulationthread extends IntegratedSimulationThread<IndexedHashMap<String, Double>>{
	
	protected IndexedHashMap<String,RegulatoryGeneticConditions> geneticConditions;

	public GeneKnockoutsIntegratedSimulationthread(IIntegratedStedystateModel model,
			EnvironmentalConditions environmentalConditions, IntegratedSimulationOptionsContainer simulationoptions, IndexedHashMap<String,RegulatoryGeneticConditions> geneticConditions) {
		super(model, environmentalConditions, simulationoptions);
		
		this.geneticConditions=geneticConditions;

	}


	@Override
	public IndexedHashMap<String, Double> getSimulationResults() {
		return results;
	}

	@Override
	protected IndexedHashMap<String, Double> executeSimulationProcess() throws Exception {
		IndexedHashMap<String, Double> res=new IndexedHashMap<>();
		
		for (int i = 0; i <geneticConditions.size(); i++) {
			String geneid=geneticConditions.getKeyAt(i);
			RegulatoryGeneticConditions genecond=geneticConditions.get(geneid);
			controlcenter.setGeneticConditions(genecond);
			SteadyStateSimulationResult simures=controlcenter.simulate();
			double valueoutput=0.0;
			if(!Double.isNaN(simures.getOFvalue()) && simures.getOFvalue()>valueoutput)
			   valueoutput=simures.getOFvalue();
			
		    res.put(geneid, valueoutput);
		    System.out.println(environmentalConditions.getId()+" --> "+geneid+" --> "+simures.getOFvalue());
		}
		return res;
	}

}
