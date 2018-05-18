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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.evaluationfunctions;

import java.util.List;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.evaluationfunction.AbstractMultiobjectiveEvaluationFunction;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.evaluationfunction.IEvaluationFunction;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.evaluationfunction.InvalidEvaluationFunctionInputDataException;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.representation.IRepresentation;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.evaluationfunction.IOptimizationEvaluationFunction;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.objectivefunctions.IObjectiveFunction;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.strainoptimizationalgorithms.jecoli.components.decoder.ISteadyStateDecoder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.OptfluxIntegratedSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.components.RFBASolutionType;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.results.RFBASimulationResult;

public class RegulatoryGeneKnockoutEvaluationFunction extends AbstractMultiobjectiveEvaluationFunction<IRepresentation> implements IOptimizationEvaluationFunction {


	protected final boolean debug = false;

	protected List<IObjectiveFunction> objectiveFunctions; 
	
	protected IIntegratedStedystateModel model;
	
	protected ISteadyStateDecoder decoder;
	
	protected AbstractIntegratedSimulationControlCenter controlCenter;
	
	protected int numberOfObjectives = 1;

	protected IntegratedSimulationOptionsContainer simulationoptions;
	
	protected EnvironmentalConditions environmentalConditions;
	
	private static final long serialVersionUID = 1L;

	public RegulatoryGeneKnockoutEvaluationFunction(IIntegratedStedystateModel model, 
			ISteadyStateDecoder decoder, 
			List<IObjectiveFunction> objectiveFunctions, 
			EnvironmentalConditions envConds,
			IntegratedSimulationOptionsContainer simulationoptions) {
		this.model = model;
		this.decoder = decoder;
		this.objectiveFunctions = objectiveFunctions;
		this.environmentalConditions = envConds;
        this.simulationoptions=simulationoptions;
		this.numberOfObjectives = objectiveFunctions.size();
		configureSimulationControlCenter();
		
	}
	
	
	
	protected void configureSimulationControlCenter() {
		 controlCenter=simulationoptions.getSimulationControlCenterInstance(model, environmentalConditions);	
	}

	
	public ISteadyStateDecoder getDecoder (){
		return decoder;
	}

	
	public void setMethodType(String methodType){
		controlCenter.setMethodType(methodType);
	}
	
	public String getMethodType()
	{
		return controlCenter.getMethodType();
	}
	
	public void setEnvironmentalConditions(EnvironmentalConditions environmentalConditions) {
			controlCenter.setEnvironmentalConditions(environmentalConditions);
	}

	@Override
	public int getNumberOfObjectives() {
		return numberOfObjectives;
	}

	public void setNumberOfObjectives(int numberOfObjectives) {
		this.numberOfObjectives = numberOfObjectives;
	}

	public Object getSimulationProperty(String propertyKey)
	{
		return controlCenter.getProperty(propertyKey);
	}
	
	public void setSimulationProperty(String key, Object value)
	{
		controlCenter.setSimulationProperty(key, value);
	}
	
	public AbstractIntegratedSimulationControlCenter getSimulationControlCenter(){
		return controlCenter;
	}
	

	
	
	@Override
	public void verifyInputData()
			throws InvalidEvaluationFunctionInputDataException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IEvaluationFunction<IRepresentation> deepCopy() throws Exception {
		return new RegulatoryGeneKnockoutEvaluationFunction(this.model, this.decoder, this.objectiveFunctions, controlCenter.getEnvironmentalConditions(),this.simulationoptions);
	}
     
	/**
	 * @return the objectiveFunctions
	 */
	public List<IObjectiveFunction> getObjectiveFunctions() {
		return objectiveFunctions;
	}
	
	

	@Override
	public Double[] evaluateMO(IRepresentation solution)throws Exception {
		
		Double[] resultList =  new Double[objectiveFunctions.size()];
		try {
			
			RegulatoryGeneticConditions gc = (RegulatoryGeneticConditions) decoder.decode(solution);
			
			controlCenter.setGeneticConditions(gc);	
			
			IntegratedSimulationMethodResult result = (IntegratedSimulationMethodResult) controlCenter.simulate();
	

			double fitness = 0.0;
			
			if(result instanceof RFBASimulationResult) {
				
				RFBASimulationResult castres=(RFBASimulationResult) result;
				
				if(castres.getRFBASolutionType().equals(RFBASolutionType.FEASIBLE)) {
					
					for (int i = 0; i < objectiveFunctions.size(); i++) {
						IObjectiveFunction of = objectiveFunctions.get(i);
						fitness = of.evaluate(result);
						resultList[i] = fitness;
					}
				}
				else {
					for (int i = 0; i < objectiveFunctions.size(); i++) {
						IObjectiveFunction of = objectiveFunctions.get(i);
						resultList[i] = of.getWorstFitness();
					}
				}
				
			}
			else if(result instanceof OptfluxIntegratedSimulationResult) {
			
			
				if (result != null && (result.getSolutionType().equals(LPSolutionType.OPTIMAL)|| result.getSolutionType().equals(LPSolutionType.FEASIBLE))){
				
					int size = objectiveFunctions.size();
					for(int i=0;i<size;i++){
						IObjectiveFunction of = objectiveFunctions.get(i);
						fitness = of.evaluate(result);
						resultList[i] = fitness;
					}
				
				}else{
					int size = objectiveFunctions.size();
					
					for(int i=0;i<size;i++){
					//NOTE: this may not be correct for clashing OFs, i.e., max vs min. Should be evaluated separately for each OF. (of.isMaximization ?)
					IObjectiveFunction of = objectiveFunctions.get(i);
					resultList[i] = of.getWorstFitness();
					}
				}
			}
		} catch (Exception e) {
			LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage(e);
		}
				
		return resultList;
	}

}
