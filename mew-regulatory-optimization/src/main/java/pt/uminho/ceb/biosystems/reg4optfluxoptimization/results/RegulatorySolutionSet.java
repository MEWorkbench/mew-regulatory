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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.results;

import java.util.HashMap;
import java.util.List;

import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.configuration.GenericOptimizationProperties;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.configuration.IGenericConfiguration;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.optimizationresult.AbstractStrainOptimizationResultSet;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.optimizationresult.IStrainOptimizationReader;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.io.RegGeneKnockoutStrategyReader;


public class RegulatorySolutionSet<T extends IGenericConfiguration> extends AbstractStrainOptimizationResultSet<T, RegulatorySolution> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegulatorySolutionSet(T baseConfiguration) {
		super(baseConfiguration);
	}

	public RegulatorySolutionSet(T baseConfiguration, List<RegulatorySolution> resultList) {
        super(baseConfiguration, resultList);
    }
	
	@Override
	public RegulatorySolution createSolution(GeneticConditions gc) {
		return new RegulatorySolution(gc);
	}

	@Override
	public RegulatorySolution createSolution(GeneticConditions gc, List<Double> attributes) {
		return new RegulatorySolution(gc, new HashMap<String,SteadyStateSimulationResult>(), attributes);
	}

	@Override
	public IStrainOptimizationReader getSolutionReaderInstance() throws Exception {
		return new RegGeneKnockoutStrategyReader((ISteadyStateGeneReactionModel) baseConfiguration.getProperty(GenericOptimizationProperties.STEADY_STATE_GENE_REACTION_MODEL));
	}

	
	@Override
	public boolean addSolutionNoRepeat(RegulatorySolution solution) {
		if(solution.getGeneticConditions()!=null) {
			String unique = solution.getGeneticConditions().toUniqueString();
			if (!getSolutionHash().contains(unique)) {
				getSolutionHash().add(unique);
				addSolution(solution);
				return true;
			}
			return false;
		}
		return false;
	}
}
