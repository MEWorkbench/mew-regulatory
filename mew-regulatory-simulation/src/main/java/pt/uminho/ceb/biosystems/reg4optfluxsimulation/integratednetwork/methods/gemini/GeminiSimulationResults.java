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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.gemini;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.common.collect.Multimap;

import jbiclustge.datatools.expressiondata.dataset.ExpressionData;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.prom.PromSimulationResult;

public class GeminiSimulationResults extends PromSimulationResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LinkedHashMap<String, ArrayList<String>> interactionsremovedforTF;
	private GeminiMetricType metric=GeminiMetricType.DEFAULT;
	private TypePhenotypeKnockout optimalthreshold=TypePhenotypeKnockout.LETHAL;

	public GeminiSimulationResults(ISteadyStateModel model, String method,
			Multimap<String, String> regulatortotargetsmap,
			//LinkedHashMap<String, HashMap<String, ReactionConstraint>> PromFluxResultsForTFs,
			LinkedHashMap<String, Double> promSimulatedBiomassWithTF,
			LinkedHashMap<String, FluxValueMap> promSimulatedFluxValuesWithTF,
			LinkedHashMap<String, Double> promGrowthSimulationWithTFEffect,
			LinkedHashMap<String, FluxValueMap> promGrowthSimulationFluxValuesWithTFEffect,
			LinkedHashMap<String, HashMap<String, Double>> promTFAffectGeneProbability,
			LinkedHashMap<String, LinkedHashMap<String, Boolean>> reactionstatesinfluencedbytf,
			LinkedHashMap<String, ArrayList<String>> interactionsremovedforTF,
			ExpressionData expressiondataset, 
			EnvironmentalConditions envconds, 
			double kappa, 
			double userdatathreshold,
			double usedpvalue, 
			double datathreshold,
			GeminiMetricType metric,
			TypePhenotypeKnockout optimalthreshold) {
		super(model, method, regulatortotargetsmap, promSimulatedBiomassWithTF,
				promSimulatedFluxValuesWithTF, promGrowthSimulationWithTFEffect, promGrowthSimulationFluxValuesWithTFEffect,
				promTFAffectGeneProbability, reactionstatesinfluencedbytf, expressiondataset, envconds, kappa,
				userdatathreshold, usedpvalue, datathreshold);
		
		this.interactionsremovedforTF=interactionsremovedforTF;
		this.metric=metric;
		this.optimalthreshold=optimalthreshold;

	}

	public LinkedHashMap<String, ArrayList<String>> getInteractionsRemovedForEachTF() {
		return interactionsremovedforTF;
	}
	
	public int getTotalInteractionsRemoved() {
		
		int n=0;
		
		for (String tfid : interactionsremovedforTF.keySet()) {
			n=n+interactionsremovedforTF.get(tfid).size();
		}
		return n;
	}

	public GeminiMetricType getUsedMetric() {
		return metric;
	}

	public TypePhenotypeKnockout getUsedPhenotypeOptimalThreshold() {
		return optimalthreshold;
	}
	
	
	
	

}
