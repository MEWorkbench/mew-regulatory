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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.results;

import java.util.ArrayList;
import java.util.HashSet;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.VariablesStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.TypeIntegratedSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.components.RFBASolutionType;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public class RFBASimulationResult extends IntegratedSimulationMethodResult{


	
	private static final long serialVersionUID = 1L;
    
	private double initBiomass;
	private double timestep;
	private int numbersteps;
	private HashSet<String> knockoutgenes;
	private HashSet<String> initgeneswithtruestate;
	private ArrayList<String> excludeUptakeReactions;
	private IndexedHashMap<String, Double> initsubstrateconcentrations;
	private ArrayList<Double> biomassprofile;
	private ArrayList<Double> timevector;
	//private GeneticConditions finalGeneticConditions;
	private ArrayList<ArrayList<Double>> concentrationMatrix;
	private IndexedHashMap<String, ArrayList<Double>> concentrationsprofile;
	private EnvironmentalConditions finalEnvconds;
	private RFBASolutionType rfbasolutiontype;
	private VariablesStateContainer initialvariablesstatecontainer;
	
	
	
	
	public RFBASimulationResult(ISteadyStateModel model, 
			EnvironmentalConditions environmentalConditions,
			GeneticConditions initialintegratedgeneticConditions,
			RegulatoryGeneticConditions calculatedgeneticconditions,
			FluxValueMap fluxValues, 
			String solverOutput,
			Double oFvalue, 
			String oFString, 
			LPSolutionType solutionType,
			IRegulatoryModelSimulationResult regulatoryresults,
			IndexedHashMap<String, Double> initsubstrateconcentrations,
			double initBiomass,
			double timestep,
			int numbersteps,
			ArrayList<Double> biomassprofile,
			ArrayList<Double> timevector,
			ArrayList<ArrayList<Double>> concentrationsresults,
			IndexedHashMap<String, ArrayList<Double>> concentrationsprofile,
			RFBASolutionType rfbasolutiontype, 
			ArrayList<String> excludeduptakereactions) {
		super(model, environmentalConditions, initialintegratedgeneticConditions,calculatedgeneticconditions, IntegratedSimulationMethod.DYNAMICRFBA.getName(), fluxValues, solverOutput, oFvalue, oFString,
				solutionType, IntegratedSimulationMethod.DYNAMICRFBA,regulatoryresults);
		
		this.initBiomass=initBiomass;
		this.timestep=timestep;
		this.numbersteps=numbersteps;
		this.initsubstrateconcentrations=initsubstrateconcentrations;
		this.biomassprofile=biomassprofile;
		this.timevector=timevector;
		this.concentrationMatrix=concentrationsresults;
		this.concentrationsprofile=concentrationsprofile;
		this.rfbasolutiontype=rfbasolutiontype;
		this.excludeUptakeReactions=excludeduptakereactions;
	
	}

	public IndexedHashMap<String, ArrayList<Double>> getConcentrationsprofile() {
		return concentrationsprofile;
	}

	@Override
	public TypeIntegratedSimulationResult getTypeResult() {
		return TypeIntegratedSimulationResult.RFBA;
	}

	public double getInitialBiomass() {
		return initBiomass;
	}

	public double getTimeStep() {
		return timestep;
	}

	public int getNumberSteps() {
		return numbersteps;
	}

	public HashSet<String> getInitialKnockoutGenes() {
		return knockoutgenes;
	}

	public HashSet<String> getInitialGenesWithTrueState() {
		return initgeneswithtruestate;
	}

	public ArrayList<String> getUptakeReactionsExcluded() {
		return excludeUptakeReactions;
	}
	
	

	/*public void setExcludeUptakeReactions(ArrayList<String> excludeUptakeReactions) {
		this.excludeUptakeReactions = excludeUptakeReactions;
	}*/

	public IndexedHashMap<String, Double> getInitialSubstrateConcentrations() {
		return initsubstrateconcentrations;
	}

	public ArrayList<Double> getBiomassProfileResult() {
		return biomassprofile;
	}
	
	public double getFinalBiomass(){
		return biomassprofile.get(biomassprofile.size()-1);
	}

	public ArrayList<Double> getTimeVectorResult() {
		return timevector;
	}

	/*public GeneticConditions getFinalGeneticConditions() {
		return finalGeneticConditions;
	}*/

	public ArrayList<ArrayList<Double>> getConcentrationMatrixResult() {
		return concentrationMatrix;
	}

	public EnvironmentalConditions getFinalEnvironmentalConditions() {
		return finalEnvconds;
	}

	public RFBASolutionType getRFBASolutionType() {
		return rfbasolutiontype;
	}

	public VariablesStateContainer getInitialVariablesStateContainer() {
		return initialvariablesstatecontainer;
	}

	public void setInitialVariablesStateContainer(VariablesStateContainer initialvariablesstatecontainer) {
		this.initialvariablesstatecontainer = initialvariablesstatecontainer;
	}



	
	
	
	
	
	

}
