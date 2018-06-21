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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.controlcenter;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.ceb.biosystems.jecoli.algorithm.AlgorithmTypeEnum;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.algorithm.IAlgorithm;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.algorithm.writer.IAlgorithmResultWriter;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.configuration.IConfiguration;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.configuration.InvalidConfigurationException;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.container.ReproductionOperatorContainer;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.reproduction.set.SetGrowthMutation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.reproduction.set.SetRandomMutation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.reproduction.set.SetRelativeGrowMutation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.reproduction.set.SetRelativeRandomMutation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.reproduction.set.SetRelativeShrinkMutation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.reproduction.set.SetShrinkMutation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.reproduction.set.SetUniformCrossover;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.selection.EnvironmentalSelection;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.operator.selection.TournamentSelection;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.randomnumbergenerator.DefaultRandomNumberGenerator;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.randomnumbergenerator.IRandomNumberGenerator;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.representation.IElementsRepresentation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.representation.integer.IntegerSetRepresentationFactory;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.representation.set.SetRepresentation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.solution.ISolution;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.solution.ISolutionFactory;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.solution.ISolutionSet;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.statistics.StatisticsConfiguration;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.terminationcriteria.ITerminationCriteria;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.terminationcriteria.IterationTerminationCriteria;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.terminationcriteria.NumFunctionEvaluationsListenerHybridTerminationCriteria;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.archive.aggregation.IAggregationFunction;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.archive.aggregation.SimpleMultiplicativeAggregation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.archive.components.ArchiveManager;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.archive.components.InsertionStrategy;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.archive.components.ProcessingStrategy;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.archive.trimming.ITrimmingFunction;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.archive.trimming.ZitzlerTruncation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.spea2.SPEA2;
import pt.uminho.ceb.biosystems.jecoli.algorithm.multiobjective.spea2.SPEA2Configuration;
import pt.uminho.ceb.biosystems.jecoli.algorithm.singleobjective.evolutionary.EvolutionaryAlgorithm;
import pt.uminho.ceb.biosystems.jecoli.algorithm.singleobjective.evolutionary.EvolutionaryConfiguration;
import pt.uminho.ceb.biosystems.jecoli.algorithm.singleobjective.evolutionary.RecombinationParameters;
import pt.uminho.ceb.biosystems.jecoli.algorithm.singleobjective.simulatedannealing.IAnnealingSchedule;
import pt.uminho.ceb.biosystems.jecoli.algorithm.singleobjective.simulatedannealing.SimulatedAnnealing;
import pt.uminho.ceb.biosystems.jecoli.algorithm.singleobjective.simulatedannealing.SimulatedAnnealingConfiguration;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.objectivefunctions.IObjectiveFunction;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.optimizationresult.archivetrimming.SelectionValueTrimmer;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.strainoptimizationalgorithms.jecoli.components.decoder.ISteadyStateDecoder;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.strainoptimizationalgorithms.jecoli.ea.JecoliEACSOMConfig;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.strainoptimizationalgorithms.jecoli.sa.JecoliSACSOMConfig;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.strainoptimizationalgorithms.jecoli.spea2.JecoliSPEAIICSOMConfig;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.configuration.RegulatoryGenericConfiguration;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.decoders.RegulatoryGenesKnockoutDecoder;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.evaluationfunctions.RegulatoryGeneKnockoutEvaluationFunction;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.results.RegulatorySolution;
import pt.uminho.ceb.biosystems.reg4optfluxoptimization.results.RegulatorySolutionSet;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;

public class RegulatoryGeneKnockoutOptimizationControlCenter {
	
	
    protected IIntegratedStedystateModel model;
	
	
	//boolean overUnder = false; // if false = KOs
	
	boolean variableSize = false;
	
	int maxSolutionSize = 6; //default
	
	protected AlgorithmTypeEnum optimizationMethod = AlgorithmTypeEnum.EA;

	protected IAlgorithm<IElementsRepresentation<?>> optimizationAlgorithm;
	
	protected ISteadyStateDecoder decoder;
	
	protected RegulatoryGeneKnockoutEvaluationFunction evaluationFunction;
	
	protected IAggregationFunction aggregationFunction;
	
	protected ArchiveManager<?,IElementsRepresentation<?>> archive = null;

	protected IRandomNumberGenerator randomGenerator;
	
	//protected SolverType solver;
	
	protected List<String> notAllowedIDs;
	
	protected EnvironmentalConditions environmentalConditions;

	protected RegulatoryGenericConfiguration regulatoryConfiguration = new RegulatoryGenericConfiguration();
	protected List<IObjectiveFunction> objFunctions;
	protected IntegratedSimulationOptionsContainer simulationoptions;
	protected boolean restrictanalysistoregulatorymodel=false;
	
	protected JecoliEACSOMConfig eaconfig=new JecoliEACSOMConfig();
	protected JecoliSPEAIICSOMConfig spea2config=new JecoliSPEAIICSOMConfig();
	protected JecoliSACSOMConfig saconfig=new JecoliSACSOMConfig();

	public RegulatoryGeneKnockoutOptimizationControlCenter(
			IIntegratedStedystateModel model,
			boolean variableSize,
			int maxSolutionSize, 
			AlgorithmTypeEnum optimizationMethod,
			String solver, 
			List<String> notAllowedIDs,
			EnvironmentalConditions environmentalConditions,
			IConfiguration<IElementsRepresentation<?>> algorithmConfiguration,
			List<IObjectiveFunction> objFunctions,
			IntegratedSimulationOptionsContainer simulationoptions,
			boolean restrictanalysistoregulatorymodel) throws Exception {
		super();
		this.model = model;
		this.variableSize = variableSize;
		this.maxSolutionSize = maxSolutionSize;
		this.optimizationMethod = optimizationMethod;
		this.notAllowedIDs = notAllowedIDs;
		this.environmentalConditions = environmentalConditions;
		this.randomGenerator = new DefaultRandomNumberGenerator();
		this.objFunctions=objFunctions;
		this.simulationoptions=simulationoptions;
		this.restrictanalysistoregulatorymodel=restrictanalysistoregulatorymodel;
		configureModelToTypeSimulation();
		createDecoder();
		createEvaluationFunction();
		if(algorithmConfiguration==null)
			configureAlgorithm(optimizationMethod,  maxSolutionSize, this.variableSize);
		optimizationAlgorithm.getConfiguration().getStatisticConfiguration().setScreenIterationInterval(1);
		
		buildConfiguration();
	}
	
	
	private void configureModelToTypeSimulation() {
		
		if(simulationoptions.getSimulationMethod().equals(IntegratedSimulationMethod.INTEGRATEDSIMULATION)) {
			RegulatorySimulationMethod regmethod=simulationoptions.getRegulatorySimulationMethod();
			model.useLogicalModelNetworkFormat(regmethod.supportsBDDFormat());
		}
		else
			model.useLogicalModelNetworkFormat(false);
		
	}
	
	
	/**
	 * <p> Creates a decoder, taking into account if it should use Gene-Protein-Reaction (GPR) information and/or over/under expression approach. </p>
	 * 
	 * @param geneOpt uses GPR information if true, doesn't use otherwise 
	 * @param overUnder uses the over/under expression approach if true, uses the knockout approach otherwise
	 * @throws Exception if adding not allowed IDs to decoder catches an Exception 
	 */
	public void createDecoder () throws Exception{
			decoder = new RegulatoryGenesKnockoutDecoder(model,restrictanalysistoregulatorymodel, notAllowedIDs);
	}
	
	public void createEvaluationFunction() {
		this.evaluationFunction=new RegulatoryGeneKnockoutEvaluationFunction(model, decoder,objFunctions, environmentalConditions, simulationoptions);
	}
	
    
	
	/**
	 * 
	 * 
	 * @param geneOpt
	 * @param overUnder
	 * @param maxSetSize
	 * @return
	 */
	public ISolutionFactory<?> createSolutionFactory (int maxSetSize){
		
		int maxSetValue = decoder.getNumberVariables();
        return new IntegerSetRepresentationFactory(maxSetValue, maxSetSize, evaluationFunction.getNumberOfObjectives());
	}
	
	
	/**
	 * 
	 * @param algorithmType
	 * @param maxSetSize
	 * @param isVariableSizeGenome
	 * @throws Exception
	 */
	public void configureAlgorithm(AlgorithmTypeEnum algorithmType, int maxSetSize, boolean isVariableSizeGenome) throws Exception
	{
		this.optimizationMethod = algorithmType;

		ISolutionFactory solutionFactory = createSolutionFactory(maxSetSize);
			
		if(optimizationMethod.equals(AlgorithmTypeEnum.EA)){
			configureEA(solutionFactory,isVariableSizeGenome);
		}
		else if(optimizationMethod.equals(AlgorithmTypeEnum.SA)){
			configureSA(solutionFactory,isVariableSizeGenome);
		}
		else if(optimizationMethod.equals(AlgorithmTypeEnum.SPEA2)){
			configureSPEA2(solutionFactory,isVariableSizeGenome);
		}
		else throw new Exception("Unsupported optimization algorithm");
	}
	

	public void configureEA(ISolutionFactory solutionFactory, boolean isVariableSizeGenome) throws Exception, InvalidConfigurationException
	{
		EvolutionaryConfiguration configuration = new EvolutionaryConfiguration();	
		configuration.setEvaluationFunction(evaluationFunction);
		configuration.setSolutionFactory(solutionFactory); 
		
		int populationSize = 100;
		configuration.setPopulationSize(populationSize);

	
		configuration.setRandomNumberGenerator(randomGenerator);
		configuration.setProblemBaseDirectory("nullDirectory");
		configuration.setAlgorithmStateFile("nullFile");
		configuration.setSaveAlgorithmStateDirectoryPath("nullDirectory");
		configuration.setAlgorithmResultWriterList(new ArrayList<IAlgorithmResultWriter<SetRepresentation>>());
		configuration.setStatisticsConfiguration(new StatisticsConfiguration());
		
		int numberIterations = 500;
		ITerminationCriteria terminationCriteria = new IterationTerminationCriteria(numberIterations);
		configuration.setTerminationCriteria(terminationCriteria);
		
		RecombinationParameters recombinationParameters = new RecombinationParameters(50,49,1,true);
		configuration.setRecombinationParameters(recombinationParameters);
		
		configuration.setSelectionOperator(new TournamentSelection<SetRepresentation>(1,2));
		configuration.setSurvivorSelectionOperator(new TournamentSelection<SetRepresentation>(1,2));
		configuration.getStatisticConfiguration().setNumberOfBestSolutionsToKeepPerRun(15);
		
		ReproductionOperatorContainer reproductionOperatorContainer = createEAReproductionOperatorContainer(isVariableSizeGenome);
		configuration.setReproductionOperatorContainer(reproductionOperatorContainer);
		
		optimizationAlgorithm = new EvolutionaryAlgorithm(configuration);	
		
		regulatoryConfiguration.setTerminationCriteria(terminationCriteria);
		
	}
	
	
	
	protected ReproductionOperatorContainer createEAReproductionOperatorContainer(boolean isVariableSizeGenome) throws Exception{
		ReproductionOperatorContainer reproductionOperatorContainer = new ReproductionOperatorContainer();
		
		
			if(isVariableSizeGenome){
				reproductionOperatorContainer.addOperator(0.25, new SetUniformCrossover());
				reproductionOperatorContainer.addOperator(0.5, new SetRandomMutation());	
				reproductionOperatorContainer.addOperator(0.125, new SetGrowthMutation());
				reproductionOperatorContainer.addOperator(0.125, new SetShrinkMutation());
			}
			else {
				reproductionOperatorContainer.addOperator(0.5, new SetUniformCrossover());
				reproductionOperatorContainer.addOperator(0.5, new SetRandomMutation());
			}

		return reproductionOperatorContainer;	
	}
	
/*	protected ReproductionOperatorContainer createEAReproductionOperatorContainer(boolean isVariableSizeGenome) throws Exception{
		ReproductionOperatorContainer reproductionOperatorContainer = new ReproductionOperatorContainer();
		
		double crossoverProbability = eaconfig.getCrossoverProbability();
		double mutationProbability = eaconfig.getMutationProbability();
		double growProbability = eaconfig.getGrowProbability();
		double shrinkProbability = eaconfig.getShrinkProbability();
		double mutationRadiusPercentage = eaconfig.getMutationRadiusPercentage();
		
		if (isVariableSizeGenome) {
			reproductionOperatorContainer.addOperator(crossoverProbability, new SetUniformCrossover());
			reproductionOperatorContainer.addOperator(mutationProbability, new SetRelativeRandomMutation<>(mutationRadiusPercentage));
			reproductionOperatorContainer.addOperator(growProbability, new SetRelativeGrowMutation<>());
			reproductionOperatorContainer.addOperator(shrinkProbability, new SetRelativeShrinkMutation<>());
		} else {
			reproductionOperatorContainer.addOperator(crossoverProbability, new SetUniformCrossover());
			reproductionOperatorContainer.addOperator(mutationProbability, new SetRelativeRandomMutation(mutationRadiusPercentage));
		}
		
		return reproductionOperatorContainer;
	}*/
	

	
	public void configureSA(ISolutionFactory solutionFactory, boolean isVariableSizeGenome) throws Exception, InvalidConfigurationException
	{
		SimulatedAnnealingConfiguration configuration = new SimulatedAnnealingConfiguration();
		configuration.setEvaluationFunction(evaluationFunction);
		configuration.setSolutionFactory(solutionFactory); 
		
		IAnnealingSchedule annealingSchedule = saconfig.getAnnealingSchedule();
		configuration.setAnnealingSchedule(annealingSchedule);
		

		configuration.setRandomNumberGenerator(randomGenerator);
		configuration.setProblemBaseDirectory("nullDirectory");
		configuration.setAlgorithmStateFile("nullFile");
		configuration.setSaveAlgorithmStateDirectoryPath("nullDirectory");
		configuration.setAlgorithmResultWriterList(new ArrayList<IAlgorithmResultWriter<SetRepresentation>>());
		
		StatisticsConfiguration statconf=new StatisticsConfiguration();
		statconf.setVerbose(true);
		configuration.setStatisticsConfiguration(statconf);

		ITerminationCriteria terminationCriteria =new NumFunctionEvaluationsListenerHybridTerminationCriteria(50000);
		configuration.setTerminationCriteria(terminationCriteria);
		
		configuration.getStatisticConfiguration().setNumberOfBestSolutionsToKeepPerRun(15);
		
		ReproductionOperatorContainer reproductionOperatorContainer = createSAReproductionOperatorContainer(isVariableSizeGenome);
		configuration.setMutationOperatorContainer(reproductionOperatorContainer);
		
		optimizationAlgorithm = new SimulatedAnnealing(configuration);
		
		regulatoryConfiguration.setTerminationCriteria(terminationCriteria);
	}
	
	
	protected ReproductionOperatorContainer createSAReproductionOperatorContainer(boolean isVariableSizeGenome) throws Exception{
		ReproductionOperatorContainer reproductionOperatorContainer = new ReproductionOperatorContainer();

			if(isVariableSizeGenome){
				reproductionOperatorContainer.addOperator(0.50,new SetRandomMutation(2));	
				reproductionOperatorContainer.addOperator(0.25,new SetGrowthMutation());
				reproductionOperatorContainer.addOperator(0.25,new SetShrinkMutation());
			}
			else
			{
				reproductionOperatorContainer.addOperator(1,new SetRandomMutation(2));
			}

		return reproductionOperatorContainer;	
	}
	
	public void configureSPEA2(ISolutionFactory solutionFactory,boolean isVariableSizeGenome) throws Exception, InvalidConfigurationException{
		
		SPEA2Configuration configuration = new SPEA2Configuration();	
		configuration.setEvaluationFunction(evaluationFunction);
		configuration.setSolutionFactory(solutionFactory); 
		configuration.setNumberOfObjectives(evaluationFunction.getNumberOfObjectives());
		IRandomNumberGenerator randomGenerator = new DefaultRandomNumberGenerator();
		configuration.setRandomNumberGenerator(randomGenerator);
		configuration.setProblemBaseDirectory("nullDirectory");
		configuration.setAlgorithmStateFile("nullFile");
		configuration.setSaveAlgorithmStateDirectoryPath("nullDirectory");
		configuration.setAlgorithmResultWriterList(new ArrayList<IAlgorithmResultWriter<SetRepresentation>>());
	
		StatisticsConfiguration statconf=new StatisticsConfiguration();
		statconf.setVerbose(true);
		configuration.setStatisticsConfiguration(statconf);
	
		
		configuration.setPopulationSize(spea2config.getPopulationSize());
		configuration.setMaximumArchiveSize(spea2config.getArchiveSize());
		configuration.getStatisticConfiguration().setNumberOfBestSolutionsToKeepPerRun(spea2config.getArchiveSize());

		/*int numberIterations = 1000;
		ITerminationCriteria terminationCriteria = new IterationTerminationCriteria(numberIterations);*/
		
		ITerminationCriteria terminationCriteria = new NumFunctionEvaluationsListenerHybridTerminationCriteria(50000);
		configuration.setTerminationCriteria(terminationCriteria);
		
		RecombinationParameters recombinationParameters = new RecombinationParameters(spea2config.getNumberOfSurvivors(),spea2config.getOffSpringSize(),spea2config.getNumberOfElitistIndividuals(),true);
		configuration.setRecombinationParameters(recombinationParameters);
		
		configuration.setEnvironmentalSelectionOperator(new EnvironmentalSelection());
		configuration.setSelectionOperator(new TournamentSelection(1,2));
		
		
		ReproductionOperatorContainer reproductionOperatorContainer = createSPEA2ReproductionOperatorContainer(isVariableSizeGenome);
		configuration.setReproductionOperatorContainer(reproductionOperatorContainer);
		optimizationAlgorithm = new SPEA2(configuration);	
		
		regulatoryConfiguration.setTerminationCriteria(terminationCriteria);
	
	}
	
	protected ReproductionOperatorContainer createSPEA2ReproductionOperatorContainer(boolean isVariableSizeGenome) throws Exception{
		ReproductionOperatorContainer reproductionOperatorContainer = new ReproductionOperatorContainer();

		if (isVariableSizeGenome) {
            reproductionOperatorContainer.addOperator(0.25, new SetUniformCrossover());
            reproductionOperatorContainer.addOperator(0.5, new SetRelativeRandomMutation<>());
            reproductionOperatorContainer.addOperator(0.125, new SetRelativeGrowMutation<>());
            reproductionOperatorContainer.addOperator(0.125, new SetRelativeShrinkMutation<>());
        } else {
            reproductionOperatorContainer.addOperator(0.5, new SetUniformCrossover());
            reproductionOperatorContainer.addOperator(0.5, new SetRandomMutation());
        }
		return reproductionOperatorContainer;	
	}
	
	
	
		public void setNumberFunctionEvaluations(int nfe) throws Exception{		
			IConfiguration configuration = optimizationAlgorithm.getConfiguration();
			ITerminationCriteria terminationCriteria = new NumFunctionEvaluationsListenerHybridTerminationCriteria(nfe);
			configuration.setTerminationCriteria(terminationCriteria);
		}
		
		public void setTerminationCriteria(ITerminationCriteria terminationCriteria){
			IConfiguration configuration = optimizationAlgorithm.getConfiguration();
			configuration.setTerminationCriteria(terminationCriteria);
		}
		
		
		public void configureDefaultArchive(){
			
			archive = new ArchiveManager(
					optimizationAlgorithm,
					InsertionStrategy.ADD_ON_SINGLE_EVALUATION_FUNCTION_EVENT,
					InsertionStrategy.ADD_SMART,
					ProcessingStrategy.PROCESS_ARCHIVE_ON_SINGLE_EVALUATION_FUNCTION_EVENT
					);

			archive.setMaximumArchiveSize(100);

			ITrimmingFunction trimmer = (evaluationFunction.getNumberOfObjectives()>1) 	? new ZitzlerTruncation(archive.getMaximumArchiveSize(), evaluationFunction)
			: new SelectionValueTrimmer(archive.getMaximumArchiveSize(), 0.000002);

			archive.addTrimmingFunction(trimmer);		
		}

		/**
		 * Runnable. Must be invoked after all the necessary parameters have been set. <br>
		 * Returns a solution set that contains the solutions kept by the archive during the execution of the algorithm. <br>
		 * 
		 * @return result the resulting <code>SteadyStateOptimizationResult</code>.
		 * 
		 * @throws Exception
		 */
		public RegulatorySolutionSet run () throws Exception{		

			//archive
			if(archive==null)
				configureDefaultArchive();
			
			// evaluation function multi -> single objective aggregation policy
			if(aggregationFunction==null && !optimizationMethod.isMultiObjective()){ 
				aggregationFunction = new SimpleMultiplicativeAggregation();
				evaluationFunction.setFitnessAggregation(aggregationFunction);
			}
			
			// execute algorithm
			optimizationAlgorithm.run();
			
			
			// build the results container
			RegulatorySolutionSet result = new RegulatorySolutionSet(regulatoryConfiguration);
			
			ISolutionSet<IElementsRepresentation<?>> finalSolutionSet = archive.getArchive();
			//archive.printArchive();
			
			// rebuild simulations from solutions
			AbstractIntegratedSimulationControlCenter intSimulationCC = evaluationFunction.getSimulationControlCenter();
			

			for(ISolution<IElementsRepresentation<?>> solution : finalSolutionSet.getListOfSolutions()){
				

				RegulatoryGeneticConditions gc = (RegulatoryGeneticConditions) decoder.decode(solution.getRepresentation());

				intSimulationCC.setGeneticConditions(gc);
				
				// simulate phenotype
				IntegratedSimulationMethodResult simResult = (IntegratedSimulationMethodResult) intSimulationCC.simulate();
				
				
				//RegulatorySolution regulatorySolution = new RegulatorySolution(simResult.getGeneticConditions());
				RegulatorySolution regulatorySolution = new RegulatorySolution(gc);
				
				String simMethod =simulationoptions.getSimulationMethod().shortName();

				regulatorySolution.addSimulationResultForMethod(simMethod, simResult);
				
				// retrieve fitnesses
				ArrayList<Double> fitnesses = new ArrayList<Double>();
				for(int i=0; i<solution.getNumberOfObjectives(); i++)
					fitnesses.add(i,solution.getFitnessValue(i));
				
				regulatorySolution.setAttributes(fitnesses);
				
				// add new optimization result
				if(isValuableSolution(regulatorySolution))
					result.addSolutionNoRepeat(regulatorySolution);
			}

			return result;
		}
		
		
		public static boolean isValuableSolution(RegulatorySolution regulatorySolution) {
			
			List<Double> attributes=regulatorySolution.getAttributes();
			for (int i = 0; i < attributes.size(); i++) {
				double val=attributes.get(i);
				if(val>0)
					return true;
			}
			return false;
		}
		
		
		
		/*
		 * Computes a reference flux distribution for over/under expression problems. <br>
		 * 
		 * @param simulationMethod the method to be used in the computation of the flux distribution
		 * @param envCond the set of environmental conditions to be taken into account
		 * @param model the base model
		 * 
		 * @return <code>Map<String,Double></code> a map containing a <code>String</code> for the flux id and a <code>Double</code> for the respective flux value 
		 * 
		 * @throws OverUnderReferenceComputationException if any problem occurs when calculating the flux distribution
		 */
		/*public Map<String,Double> computeOverUnderReferenceDistribution(String simulationMethod,EnvironmentalConditions envCond, ISteadyStateModel model, String metabsimulationMethod, VariablesContainer variables, HashSet<String> falsenodes, SolverType solverType ) throws OverUnderReferenceComputationException{
			
			
			
			
			
			IntegratedSimulationControlCenter intSimCC = new IntegratedSimulationControlCenter(envCond, null, model, simulationMethod, metabsimulationMethod,regulatorynetowrksimulationmethod, variables, falsenodes, true,solverType);

			Map<String,Double> result = null;
			try {
				result = intSimCC.simulate().getFluxValues();
			} catch (Exception e) {
				e.printStackTrace();
//				throw new OverUnderReferenceComputationException("Problem computing over/under expression reference distribution while using method ["+simulationMethod+"]");
			}
			
			return result;
		}*/
		
		public void setModel(IIntegratedStedystateModel model) {
			this.model =  model;
		}

		public IIntegratedStedystateModel getModel() {
			return model;
		}
		
		
		public Object getSimulationProperty(String propertyKey)
		{
			return evaluationFunction.getSimulationProperty(propertyKey);
		}
		
		public void setSimulationProperty(String key, Object value)
		{
			evaluationFunction.setSimulationProperty(key, value);
		}
		
		public void setSimulationMethod (String simulationMethod)
		{
			this.evaluationFunction.setMethodType(simulationMethod);
		}
		
		public String getSimulationMethod()
		{
			return this.evaluationFunction.getMethodType();
		}

		
		public AlgorithmTypeEnum getOptimizationMethod() {
			return optimizationMethod;
		}

		public void setOptimizationMethod(AlgorithmTypeEnum optimizationMethod) {
			this.optimizationMethod = optimizationMethod;
		}

		/**
		 * @return the archive
		 */
		public ArchiveManager<?, IElementsRepresentation<?>> getArchive() {
			return archive;
		}

		/**
		 * @param archive the archive to set
		 */
		public void setArchive(ArchiveManager<?,IElementsRepresentation<?>> archive) {
			this.archive = archive;
		}

	
		
		public IAlgorithm getOptimizationAlgorithm(){
			return optimizationAlgorithm;
		}
		
		public RegulatoryGeneKnockoutEvaluationFunction getEvaluationFunction(){
			return evaluationFunction;
		}

		/**
		 * @return the aggregationFunction
		 */
		public IAggregationFunction getAggregationFunction() {
			return aggregationFunction;
		}

		/**
		 * @param aggregationFunction the aggregationFunction to set
		 */
		public void setAggregationFunction(IAggregationFunction aggregationFunction) {
			this.aggregationFunction = aggregationFunction;
		}
		
		public ISteadyStateDecoder getdecoder(){
			return this.decoder;
		}
		
		protected void buildConfiguration(){
//			regulatoryConfiguration = new RegulatoryGenericConfiguration();
			regulatoryConfiguration.setModel(model);
			regulatoryConfiguration.setArchiveManager(archive);
			regulatoryConfiguration.setNotAllowedIds(notAllowedIDs);
			regulatoryConfiguration.setIsVariableSizeGenome(variableSize);
			regulatoryConfiguration.setMaxSetSize(maxSolutionSize);
			regulatoryConfiguration.setOptimizationAlgorithm(optimizationMethod.getShortName());
            regulatoryConfiguration.setSimulationSettingsContainer(simulationoptions);
			regulatoryConfiguration.setEnvironmentalConditions(environmentalConditions);
			
		}

		
	}

