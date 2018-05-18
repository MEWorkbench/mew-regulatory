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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.io;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.optimizationresult.io.gk.GKStrategyReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;

public class RegGeneKnockoutStrategyReader extends GKStrategyReader{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IIntegratedStedystateModel model;
	
	public RegGeneKnockoutStrategyReader(ISteadyStateGeneReactionModel model) {
		super(model);
		this.model=(IIntegratedStedystateModel) model;

	}
	
	
	@Override
	protected GeneticConditions processGeneticConditions(String geneticConditionString) throws Exception {
		List<String> modificationList = new ArrayList<>();
		
		if (geneticConditionString != null) {
			String[] lineArray = geneticConditionString.split(",");
			
			for (String knockoutId : lineArray) {
				String[] tokens = knockoutId.split("=");
				modificationList.add(tokens[0].trim());
			}
		}
		
		
		return RegulatoryGeneticConditions .getRegulatoryGeneticConditions((ArrayList<String>) modificationList, model);
	}
	
	

}
