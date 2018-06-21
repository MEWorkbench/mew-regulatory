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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import jbiclustge.datatools.expressiondata.dataset.ExpressionData;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.gemini.Gemini;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.gemini.GeminiMetricType;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.gemini.TypePhenotypeKnockout;

public class GeminiSimulationControlCenter extends PromSimulationControlCenter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GeminiSimulationControlCenter(ISteadyStateModel model, EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions, String solver, ExpressionData expressiondataset,
			ArrayList<String> regulators, ArrayList<String> targets) {
		super(model, environmentalConditions, geneticConditions, solver, expressiondataset, regulators, targets,IntegratedSimulationMethod.GEMINI.getName());
	}


	public GeminiSimulationControlCenter(ISteadyStateModel model, EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions, String solver, String expressionfile,
			ArrayList<String> regulators, ArrayList<String> targets) {
		super(model, environmentalConditions, geneticConditions, solver, expressionfile, regulators, targets,IntegratedSimulationMethod.GEMINI.getName());
	}
	
	
	
	@Override
	protected LinkedHashMap<String, Class<?>> getMethodsSupportedByControlCenter() {
		LinkedHashMap<String, Class<?>> support=new LinkedHashMap<>();
		support.put(IntegratedSimulationMethod.GEMINI.getName(), Gemini.class);
		return support;
	}
	
	
	public void setMetricType(GeminiMetricType metric){
		addProperty(RegulatorySimulationProperties.GEMINIMETRICTYPE, metric);
	}
	
	public void setPhenotypeKnockoutType(TypePhenotypeKnockout type){
		addProperty(RegulatorySimulationProperties.GEMINIPHENOTYPEKNOCKOUTTYPE, type);
	}
	
	public void setPhenotypeViability(LinkedHashMap<String,Boolean> viability){
		addProperty(RegulatorySimulationProperties.GEMINIPHENOTYPEVIABILITY, viability);
		ArrayList<String> subsettfs=analyseOnlySelectedTFs(viability);
		if(subsettfs!=null)
			setSubsetRegulators(subsettfs);
	}
	
	
	private ArrayList<String> analyseOnlySelectedTFs(LinkedHashMap<String,Boolean> viability){
		
		ArrayList<String> res=null;
		if(viability!=null && viability.size()>0) {
			res=new ArrayList<>();
			
			for (String id : viability.keySet()) {
				boolean state=viability.get(id);
				if(state)
					res.add(id);
			}
		}
		return res;
	}

}
