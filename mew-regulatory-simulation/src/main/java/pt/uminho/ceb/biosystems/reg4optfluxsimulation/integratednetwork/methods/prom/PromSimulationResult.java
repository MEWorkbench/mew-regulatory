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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.prom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.common.collect.Multimap;

import jbiclustge.datatools.expressiondata.dataset.ExpressionData;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;

public class PromSimulationResult extends SteadyStateSimulationResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ExpressionData expressiondataset;
	protected double kappa=1;
	protected double userdatathreshold=0.33;
	protected double datathreshold=0.33;
	protected double minpvalue=0.05;
	//protected LinkedHashMap<String, HashMap<String, ReactionConstraint>> promFluxResultsForTFs;
	protected LinkedHashMap<String, Double> promSimulatedBiomassWithTF;
	protected LinkedHashMap<String, FluxValueMap> promSimulatedFluxValuesWithTF;
	protected LinkedHashMap<String, Double> promGrowthSimulationWithTFKoEffect;
	protected LinkedHashMap<String, FluxValueMap> promGrowthSimulationFluxValuesWithTFKoEffect;
	protected Multimap<String, String> regulatortotargetsmap;
	protected LinkedHashMap<String, HashMap<String, Double>> promTFAffectGeneProbability;
	protected LinkedHashMap<String, LinkedHashMap<String, Boolean>> reactionstatesinfluencedbytf;
	protected HashMap<String, HashMap<String,Double>> userTFAffectGeneProbability;
	protected ArrayList<String> userregulatorssubset;

	/*public PromSimulationResult(ISteadyStateModel model, String method, FluxValueMap fluxValues) {
		super(model, method, fluxValues);
	}*/
	
	
	public PromSimulationResult(ISteadyStateModel model,
			String method,Multimap<String, String> regulatortotargetsmap, 
			//LinkedHashMap<String, HashMap<String, ReactionConstraint>> PromFluxResultsForTFs,
			LinkedHashMap<String, Double> promSimulatedBiomassWithTF,
			LinkedHashMap<String, FluxValueMap> promSimulatedFluxValuesWithTF,
			LinkedHashMap<String, Double> promGrowthSimulationWithTFEffect,
			LinkedHashMap<String, FluxValueMap> promGrowthSimulationFluxValuesWithTFEffect,
			LinkedHashMap<String, HashMap<String, Double>> promTFAffectGeneProbability,
			LinkedHashMap<String, LinkedHashMap<String, Boolean>> reactionstatesinfluencedbytf,
			ExpressionData expressiondataset,
			EnvironmentalConditions envconds,
			double kappa,
			double userdatathreshold,
			double usedpvalue,
			double datathreshold){
		super(model, method, null);
		this.kappa=kappa;
		this.userdatathreshold=userdatathreshold;
		this.minpvalue=usedpvalue;
		this.datathreshold=datathreshold;
		this.environmentalConditions=envconds;
		this.expressiondataset=expressiondataset;
		this.regulatortotargetsmap=regulatortotargetsmap;
		//this.promFluxResultsForTFs=PromFluxResultsForTFs;
		this.promSimulatedBiomassWithTF=promSimulatedBiomassWithTF;
		this.promSimulatedFluxValuesWithTF=promSimulatedFluxValuesWithTF;
		this.promGrowthSimulationWithTFKoEffect=promGrowthSimulationWithTFEffect;
		this.promGrowthSimulationFluxValuesWithTFKoEffect=promGrowthSimulationFluxValuesWithTFEffect;
		this.promTFAffectGeneProbability=promTFAffectGeneProbability;
		this.reactionstatesinfluencedbytf=reactionstatesinfluencedbytf;
		
	}

	
	

/*	public LinkedHashMap<String, HashMap<String, ReactionConstraint>> getPromFluxResultsForTFs() {
		return promFluxResultsForTFs;
	}*/


	public LinkedHashMap<String, Double> getPromSimulatedBiomassWithTF() {
		return promSimulatedBiomassWithTF;
	}


	public LinkedHashMap<String, FluxValueMap> getPromSimulatedFluxValuesWithTF() {
		return promSimulatedFluxValuesWithTF;
	}


	public LinkedHashMap<String, Double> getPromGrowthSimulationWithTFKoEffect() {
		return promGrowthSimulationWithTFKoEffect;
	}


	public LinkedHashMap<String, FluxValueMap> getPromGrowthSimulationFluxValuesWithTFKoEffect() {
		return promGrowthSimulationFluxValuesWithTFKoEffect;
	}


	public Multimap<String, String> getRegulatortotargetsmap() {
		return regulatortotargetsmap;
	}


	public LinkedHashMap<String, HashMap<String, Double>> getPromTFAffectGeneProbability() {
		return promTFAffectGeneProbability;
	}

	public ArrayList<String> getListofRegulators(){
		ArrayList<String> res=new ArrayList<>();
		
		for (String id : promGrowthSimulationFluxValuesWithTFKoEffect.keySet()) {
			res.add(id);
		}
		
		return res;
	}
	

	public HashMap<String, HashMap<String, Double>> getUserTFAffectGeneProbability() {
		return userTFAffectGeneProbability;
	}




	public void setUserTFAffectGeneProbability(HashMap<String, HashMap<String, Double>> userTFAffectGeneProbability) {
		this.userTFAffectGeneProbability = userTFAffectGeneProbability;
	}




	public ExpressionData getExpressiondataset() {
		return expressiondataset;
	}


	public double getKappa() {
		return kappa;
	}


	public double getUserdataThreshold() {
		return userdatathreshold;
	}
	
	


	public double getDataThreshold() {
		return datathreshold;
	}

	
	
	
	public double getpvalue() {
		return minpvalue;
	}


	public LinkedHashMap<String, LinkedHashMap<String, Boolean>> getReactionStatesInfluencedByTFs() {
		return reactionstatesinfluencedbytf;
	}




	public ArrayList<String> getUserRegulatorsSubset() {
		return userregulatorssubset;
	}




	public void setUserRegulatorSubset(ArrayList<String> userregulatorssubset) {
		this.userregulatorssubset = userregulatorssubset;
	}
	
	
	
	

}
