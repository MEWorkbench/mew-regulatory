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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.VariablesStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.IntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.SRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;

public class IntegratedSimulationOptionsContainer extends SimulationOptionsContainer{
	

	private static final long serialVersionUID = 1L;
	
	protected IntegratedSimulationMethod simulationmethod=IntegratedSimulationMethod.INTEGRATEDSIMULATION;
	protected RegulatorySimulationMethod regulatorysimulationmethod=RegulatorySimulationMethod.OPTFLUXSYNCHRONOUSBOOLEANSIMULATION;
	protected Boolean stopfirstattractor;
	protected Integer numberregulatoryiterations=100;
	protected boolean forcetfinitializationastrue=false;
	protected VariablesStateContainer variablesInputStateContainer=new VariablesStateContainer();
	
	
	
	public boolean isForceTFInitializationAsTrue() {
		return forcetfinitializationastrue;
	}


	public void setForceTFInitializationAsTrue(boolean forcetfinitializationastrue) {
		this.forcetfinitializationastrue = forcetfinitializationastrue;
	}


	//protected RegulatoryGeneticConditions geneticconditions;
	//protected String biomassflux=null;
	
	//protected boolean isMaximization=true;

	public IntegratedSimulationOptionsContainer(){}


	public VariablesStateContainer getVariablesInputStateContainer() {
		return variablesInputStateContainer;
	}

	public void setVariablesInputStateContainer(VariablesStateContainer variablesInputStateContainer) {
		this.variablesInputStateContainer = variablesInputStateContainer;
	}

	/*public String getBiomassflux() {
		return biomassflux;
	}

	public void setBiomassflux(String biomassflux) {
		this.biomassflux = biomassflux;
	}

	public HashMap<String, Double> getObjfunct() {
		return objfunct;
	}

	public void setObjfunct(HashMap<String, Double> objfunct) {
		this.objfunct = objfunct;
	}*/
	
	public IntegratedSimulationMethod getSimulationMethod() {
		return simulationmethod;
	}

	public void setSimulationMethod(IntegratedSimulationMethod simulationmethod) {
		this.simulationmethod = simulationmethod;
	}

	/*public SolverType getSolver() {
		return solver;
	}

	public void setSolver(SolverType solver) {
		this.solver = solver;
	}
	
	

	public boolean isMaximization() {
		return isMaximization;
	}


	public void setMaximization(boolean isMaximization) {
		this.isMaximization = isMaximization;
	}


	public String getMetabolicSimulationMethod() {
		return metabolicsimulationmethod;
	}

	public void setMetabolicSimulationMethod(String metabolicsimulationmethod) {
		this.metabolicsimulationmethod = metabolicsimulationmethod;
	}*/

	public RegulatorySimulationMethod getRegulatorySimulationMethod() {
		return regulatorysimulationmethod;
	}

	public void setRegulatorySimulationMethod(RegulatorySimulationMethod regulatorysimulationmethod) {
		this.regulatorysimulationmethod = regulatorysimulationmethod;
	}

	public HashMap<String, Boolean> getInitialComponentsbooleanState() {
		return variablesInputStateContainer.getInitialComponentsbooleanState();

	}

	public void setInitialComponentsbooleanState(HashMap<String, Boolean> initboolstate) {
		variablesInputStateContainer.setInitialComponentsbooleanState(initboolstate);
	}

	public HashMap<String, Byte> getInitialComponentsbyteState() {
		return variablesInputStateContainer.getInitialComponentsbyteState();

	}

	public void setInitialComponentsbyteState(HashMap<String, Byte> initbytestate) {
		variablesInputStateContainer.setInitialComponentsbyteState(initbytestate);
	}

	public Boolean isStopSimulationAtFirstAttractor() {
		return stopfirstattractor;
	}

	public void setStopSimulationAtFirstAttractor(Boolean stopfirstattractor) {
		this.stopfirstattractor = stopfirstattractor;
	}

	public Integer getRegulatorySimulationIterations() {
		return numberregulatoryiterations;
	}

	public void setRegulatorySimulationIterations(Integer numberregulatoryiterations) {
		this.numberregulatoryiterations = numberregulatoryiterations;
	}
	
	public HashSet<String> getInitialGenesONState() {
		return variablesInputStateContainer.getInitialGenesON();
	}
	
	
	public void setInitialGenesONState(Set<String> initialgenesonstate) {
		variablesInputStateContainer.setInitialGenesON(initialgenesonstate);
	}
	
	
	public HashSet<String> getInitialGenesOffState() {
		return variablesInputStateContainer.getInitialGenesOFF();
	}



	public void setInitialGenesOffState(Set<String> initialgenesoffstate) {
		variablesInputStateContainer.setInitialGenesOFF(initialgenesoffstate);
	}


	/*public RegulatoryGeneticConditions getGeneticconditions() {
		return geneticconditions;
	}*/


    @Override
	public void setGeneticconditions(RegulatoryGeneticConditions geneticconditions) {
    	if(geneticconditions!=null) {
    		this.geneticconditions = geneticconditions;
    		variablesInputStateContainer.setKnockoutgenes(geneticconditions.getALLGeneKnockoutList());
    	}
	}
	
	
	public void setVariablesWithTrueState(ArrayList<String> truevariablestate){
		if(truevariablestate!=null){
			for (String id : truevariablestate) {
				variablesInputStateContainer.addVariableBooleanState(id, true);
			}
		}
	}
	
	public void setVariablesWithFalseState( ArrayList<String> falsevariablesstate) {
		if(falsevariablesstate!=null){
			for (String id : falsevariablesstate) {
				variablesInputStateContainer.addVariableBooleanState(id, false);
			}
		}
		
	}
	
	public void setTFsWithTrueState(ArrayList<String> inittrueTFs) {
		if(inittrueTFs!=null){
			for (String id : inittrueTFs) {
				variablesInputStateContainer.addVariableBooleanState(id, true);
			}
		}
		
		
	}
	
	public void setTFsWithFalseState(ArrayList<String> initfalseTFs) {
		if(initfalseTFs!=null){
			for (String id : initfalseTFs) {
				variablesInputStateContainer.addVariableBooleanState(id, false);
			}
		}
		
	}


	public void setInitialStateOfVariables(ArrayList<String> truevariablestate, ArrayList<String> falsevariablesstate, ArrayList<String> inittrueTFs, ArrayList<String> initfalseTFs){
		
		setVariablesWithTrueState(truevariablestate);
		setVariablesWithFalseState(falsevariablesstate);
		setTFsWithTrueState(inittrueTFs);
		setTFsWithFalseState(initfalseTFs);
	}
	
	
	public AbstractIntegratedSimulationControlCenter getSimulationControlCenterInstance(IIntegratedStedystateModel model, EnvironmentalConditions environmentalConditions) {

		AbstractIntegratedSimulationControlCenter ccinstance=null;

		if(objfunct==null) {
			objfunct=new HashMap<>();
			objfunct.put(model.getBiomassFlux(), 1.0);
		}


		if(simulationmethod.equals(IntegratedSimulationMethod.SRFBA)) {
			ccinstance=new SRFBAControlCenter(model, environmentalConditions, geneticconditions, isMaximization, solver);
			((SRFBAControlCenter)ccinstance).setComponentsBooleanInitialState(getInitialComponentsbooleanState());
			if(forcetfinitializationastrue)
				((SRFBAControlCenter)ccinstance).forceIntializationTranscriptFactorsAsTrueState(true);
		}
		else {
			if(regulatorysimulationmethod.equals(RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION))
				ccinstance=new IntegratedSimulationControlCenter(model, environmentalConditions, geneticconditions,IntegratedSimulationMethod.ASYNCHINTEGRATEDSIMULATION.toString(),isMaximization, solver, metabolicsimulationmethod, regulatorysimulationmethod);
			else
				ccinstance=new IntegratedSimulationControlCenter(model, environmentalConditions, geneticconditions,isMaximization, solver, metabolicsimulationmethod, regulatorysimulationmethod);

			((IntegratedSimulationControlCenter)ccinstance).setComponentsBooleanInitialState(getInitialComponentsbooleanState());

			if(!regulatorysimulationmethod.equals(RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION))
				((IntegratedSimulationControlCenter)ccinstance).stopRegulatorySimulationOnFirstAttractor(stopfirstattractor);

			((IntegratedSimulationControlCenter)ccinstance).setMaxNumberIterationsRegulatorySimulation(numberregulatoryiterations);
			if(getInitialGenesOffState()!=null)
				((IntegratedSimulationControlCenter)ccinstance).setGeneInitialStateAsOFF(getInitialGenesOffState());
			if(forcetfinitializationastrue)
				((IntegratedSimulationControlCenter)ccinstance).forceIntializationTranscriptFactorsAsTrueState(true);
		}


		ccinstance.setObjectiveFunction(objfunct);

		return ccinstance;
	}

}
