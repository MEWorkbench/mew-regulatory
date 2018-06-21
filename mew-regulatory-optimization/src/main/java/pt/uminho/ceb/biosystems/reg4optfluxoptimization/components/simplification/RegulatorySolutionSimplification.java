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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.simplification;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.objectivefunctions.IObjectiveFunction;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.controlcenter.RegulatoryGeneKnockoutOptimizationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.results.RegulatorySolution;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.results.RegulatorySolutionSet;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IExecutionKiller;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;

public class RegulatorySolutionSimplification implements Serializable, IExecutionKiller{
	
	private static final long serialVersionUID = 1L;
	
	private double delta = 0.000001;

	protected IIntegratedStedystateModel model;
	
	protected List<IObjectiveFunction> objectiveFunctions;

	protected AbstractIntegratedSimulationControlCenter controlCenter = null;
	
    protected PropertyChangeSupport changesupport=new PropertyChangeSupport(this);
	
    protected boolean stopexecution=false;
	
	
	public RegulatorySolutionSimplification(IIntegratedStedystateModel model, 
			List<IObjectiveFunction> objFunctions, 
			EnvironmentalConditions envCond, 
			IntegratedSimulationOptionsContainer simulationoptions)
	{
		this.model = model;
		this.objectiveFunctions = objFunctions;
		this.controlCenter =simulationoptions.getSimulationControlCenterInstance(model, envCond);
	}
	

	public void addSolutionPropertyChangeListener(PropertyChangeListener listener) {
		changesupport.addPropertyChangeListener(listener);
	}
	
	
	private void setSimplificationProgressMsg(String msg) {
		changesupport.firePropertyChange("simplificationprogressmsg", null, msg);
	}
	
	private void setSimplificationProgress(float progress) {
		changesupport.firePropertyChange("simplificationprogress", null, progress);
	}
	
	
	
	public void setMargin (double delta)
	{
		this.delta = delta;
	}
  

	public RegulatorySolutionSet simplifySteadyStateOptimizationResult(RegulatorySolutionSet optResultIN) throws Exception{
		
		RegulatorySolutionSet optResultOut = new RegulatorySolutionSet(optResultIN.getBaseConfiguration());
		
		List<RegulatorySolution> regList = optResultIN.getResultList();
//		
		int n=0;
		setSimplificationProgress(0);
		setSimplificationProgressMsg("Simplifying "+regList.size()+" solutions, please wait...");
		
		for (RegulatorySolution regSol : regList) {
			
			if(stopexecution)
				break;
			else {

				RegulatorySolution simp = simplifyRegulatoryGenesSolution(regSol);
	
/*
				List<Double> fits = simp.getAttributes();	
				ArrayList<Double> fitnesses = new ArrayList<Double>();
				for(double f: fits)
					fitnesses.add(f);*/
				
				if(RegulatoryGeneKnockoutOptimizationControlCenter.isValuableSolution(simp))
					optResultOut.addSolutionNoRepeat(simp);
				n++;
				float progress = (float)n/(float)regList.size();
				setSimplificationProgress(progress);
				setSimplificationProgressMsg("Solution "+n+" of "+regList.size()+" was simplified");
			}
		}
		
		return optResultOut;
	}
	
	
	
	
	
	public RegulatorySolution simplifyRegulatoryGenesSolution(RegulatorySolution sol) throws Exception{
		
		HashMap<String, SteadyStateSimulationResult> simplifiedMap = new HashMap<>();
		
		
		/*if (initialRes == null){
			controlCenter.setGeneticConditions(initialSolution);
			origSimResult = (IntegratedSimulationMethodResult) controlCenter.simulate();
		}
		else{
			origSimResult = (IntegratedSimulationMethodResult) initialRes.getSimulationResultMap().values().iterator().next();
		}
		
		resMap.put(origSimResult.getMethod(), origSimResult);*/
		
		RegulatoryGeneticConditions geneconds=(RegulatoryGeneticConditions) sol.getGeneticConditions();
		
		IntegratedSimulationMethodResult simResult=(IntegratedSimulationMethodResult) sol.getSimulationResultMap().values().iterator().next();
		
		TreeSet<String> geneIds = new TreeSet<String>(geneconds.getALLGeneKnockoutList());
//		Set<String> geneIds = initialSolution.getGeneList().getGeneIds();
		
		double[] initialFitnesses = evaluateSolution(simResult);
	
		
		List<String> genesIDsIterator = new ArrayList<String>(geneIds);
		ArrayList<String> modallist=new ArrayList<>(geneIds);
		
		
		double [] finalFitnesses = initialFitnesses;
		
		IntegratedSimulationMethodResult finalRes = simResult;
		RegulatoryGeneticConditions newgc=null;
		
		for (String geneId: genesIDsIterator){
			
			modallist.remove(geneId);
			
			newgc=RegulatoryGeneticConditions.getRegulatoryGeneticConditions(modallist, model);
			controlCenter.setGeneticConditions(newgc);
			IntegratedSimulationMethodResult res = (IntegratedSimulationMethodResult) controlCenter.simulate();
			double [] simpfitnesses = evaluateSolution(res);
			
			if (compare(finalFitnesses, simpfitnesses)){
				finalFitnesses = simpfitnesses;
				finalRes = res;
			}
			else
				modallist.add(geneId);
		
		}

		List<Double> fitness = new ArrayList<>();
		for (int i = 0; i < finalFitnesses.length; i++)
			fitness.add(finalFitnesses[i]);
		
		simplifiedMap.put(simResult.getMethod(), finalRes);
		
		RegulatorySolution toRet = new RegulatorySolution(newgc, simplifiedMap, fitness);
		
		return toRet;
	}

	
	
	/*public RegulatorySolution simplifyRegulatoryGenesSolution(RegulatoryGeneticConditions initialSolution, RegulatorySolution initialRes) throws Exception{
			
		IntegratedSimulationMethodResult origSimResult;
		HashMap<String, SteadyStateSimulationResult> resMap = new HashMap<>();
		
		
		if (initialRes == null){
			controlCenter.setGeneticConditions(initialSolution);
			origSimResult = (IntegratedSimulationMethodResult) controlCenter.simulate();
		}
		else{
			origSimResult = (IntegratedSimulationMethodResult) initialRes.getSimulationResultMap().values().iterator().next();
		}
		
		resMap.put(origSimResult.getMethod(), origSimResult);
		
		
		TreeSet<String> geneIds = new TreeSet<String>(initialSolution.getGeneList().getGeneIds());
//		Set<String> geneIds = initialSolution.getGeneList().getGeneIds();
		
		double[] initialFitnesses = evaluateSolution(origSimResult);
	
		
		List<String> genesIDsIterator = new ArrayList<String>(geneIds); 
		
		RegulatoryGeneticConditions finalSolution = initialSolution;
		double [] finalFitnesses = initialFitnesses;
		IntegratedSimulationMethodResult finalRes = origSimResult;
		
		
		for (String geneId: genesIDsIterator){
			
			finalSolution.getGeneList().removeGene(geneId);
			finalSolution.updateReactionsList((IIntegratedStedystateModel) model);
						
			controlCenter.setGeneticConditions(finalSolution);

			IntegratedSimulationMethodResult res = (IntegratedSimulationMethodResult) controlCenter.simulate();
			
			double [] simpfitnesses = evaluateSolution(res);
			
			if (compare(finalFitnesses, simpfitnesses))
			{
				finalFitnesses = simpfitnesses;
				finalRes = res;
			}
			else
			{			
				finalSolution.addGene(geneId);
				finalSolution.updateReactionsList((IIntegratedStedystateModel) model);
				
			}
		}				
		
		List<Double> fitness = new ArrayList<>();
		for (int i = 0; i < finalFitnesses.length; i++)
			fitness.add(finalFitnesses[i]);
		
		resMap.put(origSimResult.getMethod(), finalRes);
		
		RegulatorySolution toRet = new RegulatorySolution(finalSolution, resMap, fitness);
		
		return toRet;
	}*/
	
	
	
	
	
	private double[] evaluateSolution (IntegratedSimulationMethodResult result) throws Exception{
		
		
		
		
		
		int size = objectiveFunctions.size();
		double resultList[] = new double[size];
		for(int i=0;i<size;i++)
		{
			IObjectiveFunction of = objectiveFunctions.get(i);
			resultList[i]  = of.evaluate(result);
		}
		return resultList;
	}
	
	
	
	
	
	
	private boolean compare(double[] fitnesses, double[] simplifiedFitness)
	{
		boolean res = true;
		int i = 0;

		while(res && i < objectiveFunctions.size())
		{
			IObjectiveFunction of = objectiveFunctions.get(i);
			if (of.isMaximization()) {
				if (fitnesses[i] - simplifiedFitness[i] > delta) res = false;
			}
			else if (simplifiedFitness[i] - fitnesses[i] > delta) res = false;
			i++;
		}
		
		return res;
	}


	@Override
	public void stopExecution() {
		stopexecution=true;
		setSimplificationProgressMsg("Stopping simplification process");
		
	}	
	
	
}
