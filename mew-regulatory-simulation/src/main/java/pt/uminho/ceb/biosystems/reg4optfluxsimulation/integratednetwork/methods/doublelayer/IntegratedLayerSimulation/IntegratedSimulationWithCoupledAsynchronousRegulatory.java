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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.IntegratedLayerSimulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.solvers.builders.CLPSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.builders.GLPKBinSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.MILPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.IntegratedSimulation;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.OptfluxIntegratedSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatoryNetworkSimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.controlcenters.RegulatoryNetworkSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public class IntegratedSimulationWithCoupledAsynchronousRegulatory extends IntegratedSimulation{


	private static final long serialVersionUID = 1L;

	public IntegratedSimulationWithCoupledAsynchronousRegulatory(ISteadyStateModel model) {
		super(model);
	}
	
	
	
	@Override
	public OptfluxIntegratedSimulationResult simulate() throws Exception {

		//RegulatorySimulationMethod regulatorymethod=getRegulatoryNetworkSimulationMethod();
	
		if(!((IIntegratedStedystateModel) integratedmodel).isInBDDRegulatoryNetworkFormat())
			((IIntegratedStedystateModel) integratedmodel).useLogicalModelNetworkFormat(true);


		IntegratedNetworkInitialStateContainer integratedvariables=getIntegratedVariablesContainer();
		if(integratedvariables==null)
			integratedvariables=((IIntegratedStedystateModel) integratedmodel).getIntegratedVariablesContainerWithValidation(getEnvironmentalConditions());

		if(integratedvariables==null)
			integratedvariables=((IIntegratedStedystateModel) integratedmodel).getIntegratedVariablesContainerWihoutValidation();


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

		
		
		SimulationSteadyStateControlCenter simulationControlCenter = new SimulationSteadyStateControlCenter(getEnvironmentalConditions(), null, getModel(),getMetabolicSimulationMethod());

		if(getSolverType() == null){
			Class<?> clazz = SimulationSteadyStateControlCenter.getProblemTypeFromMethod(getMetabolicSimulationMethod());
			String defaulttype = null;
			if(LPProblem.class.equals(clazz))
				defaulttype = CLPSolverBuilder.ID;
			else if(MILPProblem.class.equals(clazz))
				defaulttype = GLPKBinSolverBuilder.ID;
			else if(QPProblem.class.equals(clazz))
				defaulttype = GLPKBinSolverBuilder.ID;
			simulationControlCenter.setSolver(defaulttype);
		}
		else
			simulationControlCenter.setSolver(getSolverType());

		simulationControlCenter.setFBAObj(getObjectiveFunction());
		simulationControlCenter.setMaximization(getIsMaximization());
		
		
		//if(!RegulatoryNetworkSimulationControlCenter.getRegisteredMethods().contains(RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION.getName()))
		//	RegulatoryNetworkSimulationControlCenter.registMethod(RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION.getName(), BDDAsynchronousStateWithCoupleMetabolicSimulation.class);
		
		RegulatoryNetworkSimulationControlCenter regulatorycontrolcenter = new RegulatoryNetworkSimulationControlCenter(((IIntegratedStedystateModel) integratedmodel).getRegulatoryNetwork(),
				RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION,
				initstate);
		
		
		regulatorycontrolcenter.addProperty(RegulatoryNetworkSimulationProperties.METABOLICCONTROLCENTER, simulationControlCenter);
		regulatorycontrolcenter.addProperty(RegulatoryNetworkSimulationProperties.UNCONSTRAINEDGENES, unconstrainedgenesknockout);
		if(getRegulatorySimualtionMaxIterations()!=null)
			regulatorycontrolcenter.setMaxIterations(getRegulatorySimualtionMaxIterations());

		IRegulatoryModelSimulationResult regulatorynetworksimulation = regulatorycontrolcenter.simulate();
		//////////////// get all gene knockouts after boolean regulatory simulation //////////////////////
		ArrayList<String> geneKnockoutAfterSim =null;

		if(regulatorynetworksimulation.getAttractors().size()>0) {
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Number Attractors found: "+regulatorynetworksimulation.getAttractors().size());
			geneKnockoutAfterSim =regulatorynetworksimulation.getKnockoutGenesList();
   
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

			/*SimulationSteadyStateControlCenter simulationControlCenter = new SimulationSteadyStateControlCenter(getEnvironmentalConditions(), RegulatoryGeneConditionsNewKnockouts, getModel(),getMetabolicSimulationMethod());

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
*/
			simulationControlCenter.setGeneticConditions(RegulatoryGeneConditionsNewKnockouts);
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

			res.setRegulatorySimulationMethod(RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION);
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
	public Class<?> getFormulationClass() {
		return IntegratedSimulationWithCoupledAsynchronousRegulatory.class;
	}
	
	
	

}
