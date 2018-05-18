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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.ornrocha.printutils.MTUPrintUtils;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.ManagerExceptionUtils;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.MILPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.IntegratedLayerSimulation.ConditionalIntegratedNetworkSimulation;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatoryNetworkSimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.controlcenters.RegulatoryNetworkSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public class IntegratedSimulation extends AbstractTwoStepIntegratedSimulation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

    
/**
 * Instantiates a new BRN.
 *
 * @param model the model
 * @throws PropertyCastException the property cast exception
 * @throws MandatoryPropertyException the mandatory property exception
 */
    public IntegratedSimulation(ISteadyStateModel model) {
    	super(model);
        initBRNpossiblekeys();
    }




/**
 * Init the BRN possiblekeys.
 */
    protected void initBRNpossiblekeys(){
    	possibleProperties.add(RegulatorySimulationProperties.GENESINITIALOFFSTATE);
    	possibleProperties.add(RegulatorySimulationProperties.COMPONENTINITIALSTATE);
    	possibleProperties.add(RegulatoryNetworkSimulationProperties.STOPFIRSTATTRACTOR);
    	possibleProperties.add(RegulatoryNetworkSimulationProperties.SIMULATIONMAXITERATIONS);
    	mandatoryProperties.add(RegulatorySimulationProperties.METABOLIC_SIMULATION_METHOD);
    	mandatoryProperties.add(RegulatorySimulationProperties.REGULATORY_NETWORK_SIMULATION_METHOD);
    	possibleProperties.add(RegulatorySimulationProperties.FORCEINITIALIZATIONTFSTRUESTATE);
	//possibleProperties.add(RegulatorySimulationProperties.FORCEATTRACTORMUSTHAVESSAMEGENEKNOCKOUTS);
	
}



    
    @SuppressWarnings("rawtypes")
	public HashMap getComponentsInitialState(){
    	return ManagerExceptionUtils.testCast(propertiesmap, HashMap.class, RegulatorySimulationProperties.COMPONENTINITIALSTATE, true);   
    }
    
    @SuppressWarnings("unchecked")
	public Set<String> getGeneInitialStateAsOFF(){
    	return ManagerExceptionUtils.testCast(propertiesmap, Set.class, RegulatorySimulationProperties.GENESINITIALOFFSTATE, true);   
    }


	public String getMetabolicSimulationMethod()throws PropertyCastException, MandatoryPropertyException {
		return (String)ManagerExceptionUtils.testCast(propertiesmap, String.class, RegulatorySimulationProperties.METABOLIC_SIMULATION_METHOD, false);
	}


	public RegulatorySimulationMethod getRegulatoryNetworkSimulationMethod()throws PropertyCastException, MandatoryPropertyException {
		return ManagerExceptionUtils.testCast(propertiesmap, RegulatorySimulationMethod.class, RegulatorySimulationProperties.REGULATORY_NETWORK_SIMULATION_METHOD, false);
	}

	public boolean stopRegulatorySimulationOnFirstAttractor()throws PropertyCastException, MandatoryPropertyException {
		boolean stop=true;
		try{
			stop=ManagerExceptionUtils.testCast(propertiesmap, Boolean.class, RegulatoryNetworkSimulationProperties.STOPFIRSTATTRACTOR, true);
		}catch (Exception e) {

		}
		return stop;
	}
	
	public Integer getRegulatorySimualtionMaxIterations()throws PropertyCastException, MandatoryPropertyException {
		return ManagerExceptionUtils.testCast(propertiesmap, Integer.class, RegulatoryNetworkSimulationProperties.SIMULATIONMAXITERATIONS, true);
	}

	
	public boolean getIsToForceInitializationOfTFsAsTrueState() {
		boolean force=false;
		try {
			force=ManagerExceptionUtils.testCast(propertiesmap, Boolean.class, RegulatorySimulationProperties.FORCEINITIALIZATIONTFSTRUESTATE, true);
		} catch (Exception e) {
			force=false;
		}
		return force;
	}
	

	@Override
	public OptfluxIntegratedSimulationResult simulate() throws Exception {



		RegulatorySimulationMethod regulatorymethod=getRegulatoryNetworkSimulationMethod();
		//System.out.println("Using Regulatory Method: "+regulatorymethod);
		/* if(regulatorymethod.equals(RegulatorySimulationMethod.BDDSYNCHRONOUSBOOLEANSIMULATION) ||
	    		   regulatorymethod.equals(RegulatorySimulationMethod.BDDSYNCHRONOUSBOOLEANSIMULATION))*/


		if(regulatorymethod.supportsBDDFormat() && !((IIntegratedStedystateModel) integratedmodel).isInBDDRegulatoryNetworkFormat())
			((IIntegratedStedystateModel) integratedmodel).useLogicalModelNetworkFormat(true);
		else if(!regulatorymethod.supportsBDDFormat() && ((IIntegratedStedystateModel) integratedmodel).isInBDDRegulatoryNetworkFormat())
			((IIntegratedStedystateModel) integratedmodel).useLogicalModelNetworkFormat(false);
		/* if(regulatorymethod.supportsBDDFormat())
	    	   ((IIntegratedStedystateModel) integratedmodel).useLogicalModelNetworkFormat(true);
	       else
	    	   ((IIntegratedStedystateModel) integratedmodel).useLogicalModelNetworkFormat(false);*/



		IntegratedNetworkInitialStateContainer integratedvariables=getIntegratedVariablesContainer();
		if(integratedvariables==null || regulatorymethod.equals(RegulatorySimulationMethod.OPTFLUXINTEGRATEDSYNCHRONOUSBOOLEANSIMULATION)){
			integratedvariables=((IIntegratedStedystateModel) integratedmodel).getIntegratedVariablesContainerWithValidation(getEnvironmentalConditions());
		}
		if(integratedvariables==null)
			integratedvariables=((IIntegratedStedystateModel) integratedmodel).getIntegratedVariablesContainerWihoutValidation();





		//System.out.println("Common: "+integratedvariables.getCommonIntegratedVariableAndMetabolicVariable());

		InitialRegulatoryState initstate=integratedvariables.getInitialRegulatoryState();
		initstate.setInitialGeneStateAsOFF(getGeneInitialStateAsOFF());

		@SuppressWarnings("unchecked")
		HashMap<String,Object> initialcomponentstate=getComponentsInitialState();
		if(initialcomponentstate!=null)
			for (Map.Entry<String, Object> map: initialcomponentstate.entrySet()) {
				if(map.getValue() instanceof Boolean)
					initstate.initializeVariableBooleanState(map.getKey(), (boolean) map.getValue());
				else
					initstate.initializeVariablebyteState(map.getKey(), (byte) map.getValue());
			}

		ArrayList<String> unconstrainedgenesknockout=new ArrayList<>();
		RegulatoryGeneticConditions genecond=(RegulatoryGeneticConditions) getGeneticConditions();

		if(genecond!=null){
			ArrayList<String> unconstrainedgenes=((IIntegratedStedystateModel) integratedmodel).getUnconstrainedGenes();
			HashSet<String> reggenesoff=new HashSet<>();

			for (int i = 0; i < genecond.getALLGeneKnockoutList().size(); i++) {
				String geneid=genecond.getALLGeneKnockoutList().get(i);
				if(unconstrainedgenes.contains(geneid))
					unconstrainedgenesknockout.add(geneid);
				else
					reggenesoff.add(geneid);
			}
			if(reggenesoff.size()>0)
				initstate.setGenesStatePermanentlyOff(reggenesoff);
		}

		if(unconstrainedgenesknockout.size()>0)
			integratedvariables.setUnconstrainedGenesThatWereKnockouted(unconstrainedgenesknockout);


		if(getIsToForceInitializationOfTFsAsTrueState()) {
			integratedvariables.forceInitializationTranscriptionalFactorsAsTrue();
			IndexedHashMap<String, Boolean> tfs= integratedvariables.getTFsVariablesState();
			for (String tfid : tfs.keySet()) {
				initstate.initializeVariableBooleanState(tfid,tfs.get(tfid));
			}
		}


		// System.out.println(getInputInformation());
		//System.out.println(integratedvariables.toString());
		/* ArrayList<String> identifiers=initstate.getOrderedIdentifiers();
			ArrayList<Boolean> stateinit=initstate.getInitialBooleanStates();
			for (int i = 0; i < identifiers.size(); i++) {
				System.out.println(identifiers.get(i)+" --> "+stateinit.get(i));
			}*/


		////////////////// Boolean regulatory Network Simulation /////////////////////////////////// 
		IRegulatoryModelSimulationResult regulatorynetworksimulation = null;

		if(!regulatorymethod.equals(RegulatorySimulationMethod.OPTFLUXINTEGRATEDSYNCHRONOUSBOOLEANSIMULATION)){
			IRegulatoryNetwork regulatorymodel=((IIntegratedStedystateModel) integratedmodel).getRegulatoryNetwork();

			try{
				RegulatoryNetworkSimulationControlCenter regulatorycontrolcenter = new RegulatoryNetworkSimulationControlCenter(regulatorymodel, regulatorymethod,initstate);
				regulatorycontrolcenter.stopFirstAttractor(stopRegulatorySimulationOnFirstAttractor());
				if(getRegulatorySimualtionMaxIterations()!=null)
					regulatorycontrolcenter.setMaxIterations(getRegulatorySimualtionMaxIterations());
				regulatorynetworksimulation = regulatorycontrolcenter.simulate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
			regulatorynetworksimulation = new ConditionalIntegratedNetworkSimulation((IIntegratedStedystateModel) integratedmodel, integratedvariables, unconstrainedgenesknockout).simulate();


		//////////////// get all gene knockouts after boolean regulatory simulation //////////////////////
		ArrayList<String> geneKnockoutAfterSim =null;

		if(regulatorynetworksimulation.getAttractors().size()>0) {
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Number Attractors found: "+regulatorynetworksimulation.getAttractors().size());
			geneKnockoutAfterSim =regulatorynetworksimulation.getKnockoutGenesList();
           //System.out.println(geneKnockoutAfterSim);
			// System.out.println("Knockout genes after simulation: "+geneKnockoutAfterSim);
			/* if(geneKnockoutAfterSim.size()==0 && (regulatorymethod.equals(RegulatorySimulationMethod.BDDSEQUENCIALBOOLEANSIMULATION)) || regulatorymethod.equals(RegulatorySimulationMethod.BDDASYNCHRONOUSBOOLEANSIMULATION)){
	    	   geneKnockoutAfterSim=((SynchronousSimulationResults)regulatorynetworksimulation).calculateMemoryStateStability(GeneBehavior.off);

	       }*/



			if(unconstrainedgenesknockout.size()>0){
				for (int i = 0; i < unconstrainedgenesknockout.size(); i++) {
					if(!geneKnockoutAfterSim.contains(unconstrainedgenesknockout.get(i)))
						geneKnockoutAfterSim.add(unconstrainedgenesknockout.get(i));
				}
			}
			//System.out.println(unconstrainedgenesknockout);

			//System.out.println(geneKnockoutAfterSim);
			GeneChangesList metabolicgenes = new GeneChangesList(((IIntegratedStedystateModel) integratedmodel).filterOnlyMetabolicGenes(geneKnockoutAfterSim)); 

			GeneregulatorychangesList regulatorygenes = new GeneregulatorychangesList(((IIntegratedStedystateModel) integratedmodel).filterOnlyRegulatoryGenes(geneKnockoutAfterSim));

			RegulatoryGeneticConditions RegulatoryGeneConditionsNewKnockouts = new RegulatoryGeneticConditions(regulatorygenes, metabolicgenes, (IIntegratedStedystateModel)integratedmodel, false);

			//System.out.println("ENV CONDS: "+getEnvironmentalConditions());

			SimulationSteadyStateControlCenter simulationControlCenter = new SimulationSteadyStateControlCenter(getEnvironmentalConditions(), RegulatoryGeneConditionsNewKnockouts, getModel(),getMetabolicSimulationMethod());

			if(getSolverType() == null){
				Class<?> clazz = SimulationSteadyStateControlCenter.getProblemTypeFromMethod(getMetabolicSimulationMethod());
				SolverType defaulttype = null;
				if(LPProblem.class.equals(clazz))
					defaulttype = SolverType.CLP;
				else if(MILPProblem.class.equals(clazz))
					defaulttype = SolverType.GLPK;
				else if(QPProblem.class.equals(clazz))
					defaulttype = SolverType.GLPK;
				simulationControlCenter.setSolver(defaulttype);
			}
			else
				simulationControlCenter.setSolver(getSolverType());

			simulationControlCenter.setFBAObj(getObjectiveFunction());
			simulationControlCenter.setMaximization(getIsMaximization());

			SteadyStateSimulationResult metabolicsolution = simulationControlCenter.simulate();

			// System.out.println("Simulation input Metabgenes: "+RegulatoryGeneConditionsNewKnockouts.getMetabolicGenesKnockoutList());

			OptfluxIntegratedSimulationResult res = new OptfluxIntegratedSimulationResult((IIntegratedStedystateModel) integratedmodel,
					getEnvironmentalConditions(),
					getGeneticConditions(),
					RegulatoryGeneConditionsNewKnockouts,
					IntegratedSimulationMethod.INTEGRATEDSIMULATION.getName(),
					metabolicsolution.getFluxValues(), 
					metabolicsolution.getSolverOutput(), 
					metabolicsolution.getOFvalue(),
					metabolicsolution.getOFString(),
					metabolicsolution.getSolutionType(),
					IntegratedSimulationMethod.INTEGRATEDSIMULATION,
					regulatorynetworksimulation,
					integratedvariables,
					getMetabolicSimulationMethod());

			res.setRegulatorySimulationMethod(regulatorymethod);
			res.setComplementaryInfoMetabolites(metabolicsolution.getComplementaryInfoMetabolites());
			res.setComplementaryInfoReactions(metabolicsolution.getComplementaryInfoReactions());


			res.appendAuxiliarInformation(RegulatoryNetworkSimulationProperties.STOPFIRSTATTRACTOR, stopRegulatorySimulationOnFirstAttractor());
			res.appendAuxiliarInformation(RegulatoryNetworkSimulationProperties.SIMULATIONMAXITERATIONS, getRegulatorySimualtionMaxIterations());


			return res;
		}
		else
			throw new Exception("The number of found Attractors is zero, please change settings to simulate regulatory network");

	}




	@Override
	public ISteadyStateModel getModel() {
		return integratedmodel;
	}




	@Override
	public Class<?> getFormulationClass() {
		return IntegratedSimulation.class;
	}




	
	private String getInputInformation(){
		StringBuilder str=new StringBuilder();
		str.append("Model Class: "+integratedmodel.getClass().getSimpleName()+"\n");
		str.append("Environmental Conditions: "+getEnvironmentalConditions()+"\n");
		str.append("Gene Knockouts: "+getGeneticConditions()+"\n");
		str.append("Gene initial state off: "+getGeneInitialStateAsOFF()+"\n");
		str.append("Initial variables state: "+getComponentsInitialState()+"\n");
		str.append("Metabolic simualtion method: "+getMetabolicSimulationMethod()+"\n");
		str.append("Regulatory simualtion method: "+getRegulatoryNetworkSimulationMethod()+"\n");
		str.append("Solver: "+getSolverType()+"\n");
		str.append("Objective Function: "+getObjectiveFunction()+"\n");
		//str.append("Initial variables state: "+getIntegratedVariablesContainer().toString());
		return str.toString();
		
	}
















}
