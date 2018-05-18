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

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

import jbiclustge.datatools.expressiondata.dataset.ExpressionData;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.prom.Prom;

public class PromSimulationControlCenter extends AbstractIntegratedSimulationControlCenter{


	private static final long serialVersionUID = 1L;

	public PromSimulationControlCenter(ISteadyStateModel model, 
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions,
			SolverType solver,
			ExpressionData expressiondataset,
			ArrayList<String> regulators,
			ArrayList<String> targets
			) {
		super(model, environmentalConditions, geneticConditions, IntegratedSimulationMethod.PROM.getName(), true, solver);
		setExpressionDataset(expressiondataset);
		setRegulators(regulators);
		setTargets(targets);
		
	}
	
	
	public PromSimulationControlCenter(ISteadyStateModel model, 
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions,
			SolverType solver,
			String expressionfile,
			ArrayList<String> regulators,
			ArrayList<String> targets
			) {
		super(model, environmentalConditions, geneticConditions, IntegratedSimulationMethod.PROM.getName(), true, solver);
		setExpressionDatasetFile(expressionfile);
		setRegulators(regulators);
		setTargets(targets);
	}
	
	public PromSimulationControlCenter(ISteadyStateModel model, 
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions,
			SolverType solver,
			ExpressionData expressiondataset,
			Multimap<String, String> regulatortargetmap) {
		super(model, environmentalConditions, geneticConditions, IntegratedSimulationMethod.PROM.getName(), true, solver);
		setExpressionDataset(expressiondataset);
        processRegulatorTargetMap(regulatortargetmap);
	}
	
	
	
	protected PromSimulationControlCenter(ISteadyStateModel model, 
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions,
			SolverType solver,
			ExpressionData expressiondataset,
			ArrayList<String> regulators,
			ArrayList<String> targets,
			String method
			) {
		super(model, environmentalConditions, geneticConditions, method, true, solver);
		setExpressionDataset(expressiondataset);
		setRegulators(regulators);
		setTargets(targets);
		
	}
	
	
	protected PromSimulationControlCenter(ISteadyStateModel model, 
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions,
			SolverType solver,
			String expressionfile,
			ArrayList<String> regulators,
			ArrayList<String> targets,
			String method
			) {
		super(model, environmentalConditions, geneticConditions, method, true, solver);
		setExpressionDatasetFile(expressionfile);
		setRegulators(regulators);
		setTargets(targets);
	}
	
	

	@Override
	protected LinkedHashMap<String, Class<?>> getMethodsSupportedByControlCenter() {
		LinkedHashMap<String, Class<?>> support=new LinkedHashMap<>();
		support.put(IntegratedSimulationMethod.PROM.getName(), Prom.class);
		return support;
	}
	
	public void setExpressionDataset(ExpressionData dataset){
		addProperty(RegulatorySimulationProperties.EXPRESSIONDATASET, dataset);
	}
	
	public void setExpressionDatasetFile(String filepath){
		addProperty(RegulatorySimulationProperties.EXPRESSIONDATAFILE, filepath);
	}
	
	
	public void setRegulators(ArrayList<String> regulators){
		addProperty(RegulatorySimulationProperties.PROMREGULATORS, regulators);
	}
	
	public void setTargets(ArrayList<String> targets){
		addProperty(RegulatorySimulationProperties.PROMTARGETS, targets);
	}
	
	public void setBiomassFluxID(String biomassID){
		addProperty(RegulatorySimulationProperties.BIOMASSFLUX, biomassID);
	}
	
	public void setFluxVariabilityAnalysisResults(Map<String, double[]> fluxvariability){
		addProperty(RegulatorySimulationProperties.FLUXVARIABILITYDATA,fluxvariability);
	}
	
	
	public void setKappaValue(double kappa){
		addProperty(RegulatorySimulationProperties.PROMKAPPA, kappa);
	}
	
	public void setExpressionDataThreshold(double t) throws IOException{
		if(t<0 && t>1)
			throw new IOException("Invalid input value ["+t+"] for data threshold, the value must be between (0,1)");
		else
		addProperty(RegulatorySimulationProperties.PROMDATATHRESHOLD, t);
	}
	
	public void setTFGeneProbabilityList(HashMap<String, HashMap<String,Double>> userTFGeneprobabilityList){
		addProperty(RegulatorySimulationProperties.PROMKNOWNTFGENEPROBABILITY, userTFGeneprobabilityList);
	}
	
	public void setSubsetRegulators(ArrayList<String> subset) {
		addProperty(RegulatorySimulationProperties.PROMTFSUBSET, subset);
	}
	
	
	public void setPROMListener(PropertyChangeListener listener) {
		addProperty(RegulatorySimulationProperties.PROMLISTENER, listener);
	}
	
	
	private void processRegulatorTargetMap(Multimap<String, String> map){
		
		ArrayList<String> regulators=new ArrayList<>();
		ArrayList<String> targets =new ArrayList<>();
		
		
		for (String regulator : map.keySet()) {
			List<String> target=(List<String>) map.get(regulator);
			for (int i = 0; i < target.size(); i++) {
				regulators.add(regulator);
				targets.add(target.get(i));
			}
		}
		
		setRegulators(regulators);
		setTargets(targets);
	}

}
