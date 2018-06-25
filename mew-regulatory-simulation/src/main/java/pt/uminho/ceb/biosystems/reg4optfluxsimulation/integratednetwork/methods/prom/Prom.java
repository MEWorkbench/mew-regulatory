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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.javatuples.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import jbiclustge.datatools.expressiondata.dataset.ExpressionData;
import jbiclustge.datatools.expressiondata.transformdata.normalization.QuantileNormalization;
import pt.ornrocha.arrays.MTUArrayUtils;
import pt.ornrocha.mathutils.MTUStatisticsUtils;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.core.model.components.GeneReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.Reaction;
import pt.uminho.ceb.biosystems.mew.core.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.abstractions.AbstractSSBasicSimulation;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.abstractions.VarTerm;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.abstractions.WrongFormulationException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.ManagerExceptionUtils;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.mew.core.simulation.fva.FBAFluxVariabilityAnalysisNew;
import pt.uminho.ceb.biosystems.mew.solvers.SolverFactory;
import pt.uminho.ceb.biosystems.mew.solvers.builders.CLPSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraint;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraintType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblemRow;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolution;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPVariable;
import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.LinearProgrammingTermAlreadyPresentException;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.Environment;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.IEnvironment;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.BooleanValue;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IIntegratedSteadyStateSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import smile.imputation.KNNImputation;
import smile.stat.hypothesis.KSTest;


/**
 * 
 */


public class Prom extends AbstractSSBasicSimulation<LPProblem> implements IIntegratedSteadyStateSimulationMethod{

	// Expressionset
	protected ExpressionData expressiondataset;
	protected ArrayList<String> expressiongeneidentifiers;
	
	// Input Data
	protected Multimap<String, String> regulatortotargetsmap;
	protected ArrayList<String> regulatororderlist;
	protected ArrayList<String> regulatedorderlist;
	protected ArrayList<Integer> lost_tfgeneinteraction=new ArrayList<>();
	protected LinkedHashMap<Integer, Double> mapIndexTFGeneInteractionToItsProbalility; // map of tfgene probability to regulated and regulator index at regulatororderlist and regulatedorderlist
	protected double penaltykappa=1;
	protected double userdatathreshold=0.33;
	protected double minpvalue=0.05;
	protected boolean forcefluxvariability=false;
	protected HashMap<String, HashMap<String,Double>> userTFGeneprobabilities;
	protected boolean ispartialTFGeneproblist=false;
	protected PropertyChangeSupport changesupport=new PropertyChangeSupport(this);
	
	protected Map<String, double[]> fluxvariability;
	protected double[][] quantilenormalizeddata;
	protected int[][] binarizeddata;
	protected double datathreshold;
	protected ArrayList<String> tfnames;
	protected double thresholdlimit=0.000001;
	protected double mthreshold=0.001;
	
	
	
	// Problem design
	
	protected ArrayList<String> listReactionVariables;
	protected Map<String, Double> current_objfunct;
	protected ArrayList<Integer> endofLpconstraintType=new ArrayList<>(3);
	protected HashMap<String,ReactionConstraint> tempreactionlimits;
	protected HashMap<String, ReactionConstraint> newproblemreactionconstraints;
	protected SteadyStateSimulationResult wildtyperesults;
	protected LPProblem p;
	protected SteadyStateSimulationResult initsolution = null;
	protected ArrayList<String> matlaborder;
	
	// results
	//protected LinkedHashMap<String, HashMap<String, ReactionConstraint>> PromFluxResultsForTFs=new LinkedHashMap<>();
	protected LinkedHashMap<String, Double> promSimulatedBiomassWithTF=new LinkedHashMap<>();
	protected LinkedHashMap<String, FluxValueMap> promSimulatedFluxValuesWithTF=new LinkedHashMap<>();
	protected LinkedHashMap<String, Double> promGrowthSimulationWithTFEffect=new LinkedHashMap<>();
	protected LinkedHashMap<String, FluxValueMap> promGrowthSimulationFluxValuesWithTFEffect=new LinkedHashMap<>();
	protected LinkedHashMap<String, LinkedHashMap<String, Boolean>> reactionstatesinfluencedbytf=new LinkedHashMap<>();
	protected boolean exec=true;
	
	
	private static String ADDPROPERTYNAME="propertyname";
	
	
	public Prom(ISteadyStateModel model) {
		super(model);
		initProperties();
		
	}
	
	protected void initProperties(){
		mandatoryProperties.add(RegulatorySimulationProperties.EXPRESSIONDATAFILE);
		mandatoryProperties.add(RegulatorySimulationProperties.PROMREGULATORS);
		mandatoryProperties.add(RegulatorySimulationProperties.PROMTARGETS);
		
		
		optionalProperties.add(RegulatorySimulationProperties.PROMTFSUBSET);
		optionalProperties.add(RegulatorySimulationProperties.PROMKAPPA);
		optionalProperties.add(RegulatorySimulationProperties.PROMDATATHRESHOLD);
		optionalProperties.add(RegulatorySimulationProperties.BIOMASSFLUX);
		optionalProperties.add(RegulatorySimulationProperties.FLUXVARIABILITYDATA);   
		optionalProperties.add(RegulatorySimulationProperties.PROMKNOWNTFGENEPROBABILITY);
		optionalProperties.add(RegulatorySimulationProperties.PROMLISTENER);
		optionalProperties.add(RegulatorySimulationProperties.STOPCURRENTPROCESS);
	}
	
	
	protected void addPropertyNameToProgress(String propname) {
		changesupport.firePropertyChange(ADDPROPERTYNAME, null, propname);
	}

	protected void setCurrentStatus(String key,String status) {
		changesupport.firePropertyChange(key, null, status);
	}
	
	
	protected void setCurrentProgress(String key,float progress) {
		changesupport.firePropertyChange(key, null, progress);
	}
	
	protected boolean execute() {
		boolean stop=false;
		try {
			stop=ManagerExceptionUtils.testCast(properties, Boolean.class, RegulatorySimulationProperties.STOPCURRENTPROCESS, true);
		} catch (Exception e) {
			stop=false;
		}
		
		return !stop;
	}
	
	
	protected void configureListener() {
		PropertyChangeListener listener=null;
		try {
			listener=ManagerExceptionUtils.testCast(properties, PropertyChangeListener.class,RegulatorySimulationProperties.PROMLISTENER,true);
		} catch (Exception e) {
			
		}
		//System.out.println("LISTENER: "+ listener);
		if(listener!=null)
			changesupport.addPropertyChangeListener(listener);
	}


    @Override
	public String getSolverType() throws PropertyCastException, MandatoryPropertyException {
    	String solverType =CLPSolverBuilder.ID;
    	try {
			solverType = (String) ManagerExceptionUtils.testCast(properties, String.class, SimulationProperties.SOLVER, false);
		} catch (Exception e) {
			return solverType;
		}
		
		return solverType;
	}

    
	/*public void setBiomassFluxID(String biomassID){
		properties.put(RegulatorySimulationProperties.BIOMASSFLUX, biomassID);
	}
	*/
	public String getBiomassID(){
		String id=model.getBiomassFlux();
		try {
			id = ManagerExceptionUtils.testCast(properties,String.class,RegulatorySimulationProperties.BIOMASSFLUX,false);
		} catch (Exception e) {
			id=model.getBiomassFlux();
		}
		if(id==null)
			id=model.getBiomassFlux();
		
		return id;
	}
	
	protected void setObjectiveFunction(Map<String, Double> obj_coef) {
		properties.put(SimulationProperties.OBJECTIVE_FUNCTION, obj_coef);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Double> getObjectiveFunction() {
		Map<String, Double> obj_coef = null;
		String biomassid=getBiomassID();
		try {
			obj_coef = ManagerExceptionUtils.testCast(properties,Map.class,SimulationProperties.OBJECTIVE_FUNCTION,true);
		} catch (Exception e) {
			obj_coef = new HashMap<String, Double>();
			obj_coef.put(biomassid, 1.0);
		}
		
		if(obj_coef==null){
			obj_coef = new HashMap<String, Double>();
			obj_coef.put(biomassid, 1.0);
		}
		
		return obj_coef;
	}
	
	
	/*public void setFluxVariabilityResultsData(Map<String, double[]> fluxvariability){
		properties.put(RegulatorySimulationProperties.FLUXVARIABILITYDATA,fluxvariability);
	}*/
	
	@SuppressWarnings("unchecked")
	public Map<String, double[]> getFluxVariabilityData(){
		Map<String, double[]> res=null;
		try {
			res =ManagerExceptionUtils.testCast(properties,Map.class,RegulatorySimulationProperties.FLUXVARIABILITYDATA,true);
		} catch (Exception e) {
			res=null;
		}
		return res;
	}
	
	
	private ExpressionData getExpressionDataSet(){
		ExpressionData datatset=null;
		try {
			datatset=ManagerExceptionUtils.testCast(properties, ExpressionData.class,RegulatorySimulationProperties.EXPRESSIONDATASET,true);
		} catch (Exception e) {
			return null;
		}
       return datatset;
	}
	
	
	
	private void loadExpressionData() throws MandatoryPropertyException{
		
		
		ExpressionData datatset=getExpressionDataSet();
		if(datatset!=null){
			expressiondataset=datatset;
		}
		else{
			String expressionfile= ManagerExceptionUtils.testCast(properties, String.class,RegulatorySimulationProperties.EXPRESSIONDATAFILE,false);
			if(expressionfile!=null){
				try {
					expressiondataset=ExpressionData.loadDataset(expressionfile, null);
				} catch (Exception e) {
					try {
						expressiondataset=ExpressionData.loadDataset(expressionfile, new KNNImputation(4));
					} catch (Exception e2) {
						throw new MandatoryPropertyException(e2.getMessage(), ExpressionData.class);
					}
				}
			}
		}
		expressiongeneidentifiers=expressiondataset.getGeneNamesList();	
	}
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	private void getRegulatorsTargetsMap() throws IOException{

		ArrayList<String> regulators=ManagerExceptionUtils.testCast(properties, ArrayList.class,RegulatorySimulationProperties.PROMREGULATORS,false);
		ArrayList<String> targets=ManagerExceptionUtils.testCast(properties, ArrayList.class,RegulatorySimulationProperties.PROMTARGETS,false);
		
		if(regulators.size()==targets.size()){
			
			this.regulatortotargetsmap = ArrayListMultimap.create();
			this.regulatororderlist=regulators;
			this.regulatedorderlist=targets;
			
			for (int j = 0; j < regulators.size(); j++) {
				regulatortotargetsmap.put(regulators.get(j), targets.get(j));
				
			}
			
			this.tfnames=new ArrayList<>(new LinkedHashSet<>(regulatororderlist));
		}
		else
			throw new IOException("Regulators vector must be the same size of Targets vector");

		
	}
	
	
	
	private void getKappaAndDatathreshold(){
		try {
			this.penaltykappa=ManagerExceptionUtils.testCast(properties, Double.class,RegulatorySimulationProperties.PROMKAPPA,true);
		} catch (Exception e) {
			this.penaltykappa=1.0;
		}
		
		try {
			this.userdatathreshold=ManagerExceptionUtils.testCast(properties, Double.class,RegulatorySimulationProperties.PROMDATATHRESHOLD,true);
		} catch (Exception e) {
			this.userdatathreshold=0.33;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void getTFsSubsets(){
		ArrayList<String> tfstofilter=(ArrayList<String>)ManagerExceptionUtils.testCast(properties, ArrayList.class,RegulatorySimulationProperties.PROMTFSUBSET,true);
	    
		if(tfstofilter!=null){
			Multimap<String, String> filteredregulatortotargetsmap=ArrayListMultimap.create();
			ArrayList<String> filterregulatororderlist=new ArrayList<>();
			ArrayList<String> filterregulatedorderlist=new ArrayList<>();
			
			
			for (int i = 0; i < tfstofilter.size(); i++) {
				String tfname=tfstofilter.get(i);
				if(regulatortotargetsmap.containsKey(tfname)){
					Collection<String> tfregulated=regulatortotargetsmap.get(tfname);
					for (String regulated : tfregulated) {
						filteredregulatortotargetsmap.put(tfname, regulated);
						filterregulatororderlist.add(tfname);
						filterregulatedorderlist.add(regulated);
					}
				}
					
			}

			
			if(filteredregulatortotargetsmap.size()>0 && filterregulatororderlist.size()>0 && filterregulatedorderlist.size()>0){
				this.regulatortotargetsmap=filteredregulatortotargetsmap;
				this.regulatororderlist=filterregulatororderlist;
				this.regulatedorderlist=filterregulatedorderlist;
				this.tfnames=new ArrayList<>(new LinkedHashSet<>(regulatororderlist));
			}
		
		}
	
	}
	
	
	public void isTFGeneprobabilitylist(boolean bol){
		this.ispartialTFGeneproblist=bol;
	}
	
	public void setTFGeneProbabilityList(HashMap<String, HashMap<String,Double>> userTFGeneprobabilityList){
		this.userTFGeneprobabilities=userTFGeneprobabilityList;
		properties.put(RegulatorySimulationProperties.PROMKNOWNTFGENEPROBABILITY, userTFGeneprobabilityList);
		
		ArrayList<String> regulatedvector=new ArrayList<>();
		
		for (String regulatorid : userTFGeneprobabilityList.keySet()) {
			if(regulatororderlist.contains(regulatorid)){
			    Collection<String> grouptargets=regulatortotargetsmap.get(regulatorid);
				HashMap<String, Double> targets=userTFGeneprobabilityList.get(regulatorid);
					for (String t : targets.keySet()) {
						if(grouptargets.contains(t))
						   regulatedvector.add(t);
					}
			}
		}
		if(regulatedvector.size()==regulatedorderlist.size())
			ispartialTFGeneproblist=false;
		else
			ispartialTFGeneproblist=true;
	}
	
	
	@Override
	public LPProblem constructEmptyProblem() {
		LPProblem newProblem = new LPProblem();
		return newProblem;
	}

	
	
	protected void runFluxVariability() throws Exception {
		
		addPropertyNameToProgress("analysingfluxvariability");
		setCurrentStatus("analysingfluxvariability", "Analysing flux variability...");
		
		Map<String, double[]> inputfv=getFluxVariabilityData();
		
		if(inputfv!=null)
			this.fluxvariability=inputfv;
		else{
			if(!forcefluxvariability && regulatororderlist.size()>1000)
				forcefluxvariability=true;
		
			if(forcefluxvariability){
				FBAFluxVariabilityAnalysisNew fluxvariabanalysis=new FBAFluxVariabilityAnalysisNew(this.model,getEnvironmentalConditions(),null,getSolverType());
				this.fluxvariability=fluxvariabanalysis.limitsAllFluxes(1);
			}
			else
				this.fluxvariability=new HashMap<>();
			}
		
	}
	
	
	

		
	protected void processExperimentalDataset() throws Exception{
		addPropertyNameToProgress("processexpdata");
		setCurrentStatus("processexpdata", "Processing dataset...");
		quantilenormalizeddata=expressiondataset.getNormalizedMatrix(new QuantileNormalization());
		datathreshold=MTUStatisticsUtils.getQuantileValueSortDataFirst(MTUArrayUtils.convertMatrixToArray(quantilenormalizeddata, true), userdatathreshold);
		binarizeddata=binarizePROMExpressionDataset(quantilenormalizeddata, datathreshold);
	}
	
	protected void setTFGeneProbabilities(){
		
		if(userTFGeneprobabilities!=null && !ispartialTFGeneproblist){
			
			this.mapIndexTFGeneInteractionToItsProbalility=new LinkedHashMap<>();
			for (int i = 0; i < regulatororderlist.size(); i++) {
				String regulator=regulatororderlist.get(i);
				String target=regulatedorderlist.get(i);
				
				if(userTFGeneprobabilities.containsKey(regulator)){
					if(userTFGeneprobabilities.get(regulator).containsKey(target))
						mapIndexTFGeneInteractionToItsProbalility.put(i, userTFGeneprobabilities.get(regulator).get(target));
					
				}
			}
		}
		else
			findTFGeneProbabilities();
		
	}
		
	
	protected void findTFGeneProbabilities(){
		
		addPropertyNameToProgress("findprobabilitiesstatus");
		setCurrentStatus("findprobabilitiesstatus", "Calculating probabilities...");
		
		this.mapIndexTFGeneInteractionToItsProbalility=new LinkedHashMap<>(regulatedorderlist.size());
		
		for (int i = 0; i < regulatedorderlist.size(); i++) {
			
			int regulatedpos=-1;
			int regulatorpos=-1;
			
			String regulated=regulatedorderlist.get(i);
			String regulator=regulatororderlist.get(i);
			
			
			if(userTFGeneprobabilities!=null && ispartialTFGeneproblist && userTFGeneprobabilities.containsKey(regulator) && userTFGeneprobabilities.get(regulator).containsKey(regulated)){
				 mapIndexTFGeneInteractionToItsProbalility.put(i, userTFGeneprobabilities.get(regulator).get(regulated));
			}
			else{
				
				try {
					regulatedpos=expressiongeneidentifiers.indexOf(regulated);
				} catch (Exception e) {
					regulatedpos=-1;
				}
			
				try {
					regulatorpos=expressiongeneidentifiers.indexOf(regulator);
				} catch (Exception e) {
					regulatorpos=-1;
				}
			
				if(regulatedpos>-1 && regulatorpos>-1){
				
				    // Distribution of the gene expression data when TF is on =1 
					double[]regulatedonedist=MTUArrayUtils.getDoubleValuesMappedtoArrayOfIndexes(quantilenormalizeddata[regulatedpos],MTUArrayUtils.getArrayIndexesWithBinaryValue(binarizeddata[regulatorpos], true));
					// Distribution of the gene expression data when TF is off =0 
					double[]regulatedzerodist=MTUArrayUtils.getDoubleValuesMappedtoArrayOfIndexes(quantilenormalizeddata[regulatedpos],MTUArrayUtils.getArrayIndexesWithBinaryValue(binarizeddata[regulatorpos], false));
				
					// check if both gene distributions are different of each other, when TF is on and off
					if(KolmogorovSmirnovTest(regulatedonedist, regulatedzerodist, minpvalue)){
					
						int[] regulatedvsregulatoroff=MTUArrayUtils.getIntegerValuesMappedtoArrayOfIndexes(binarizeddata[regulatedpos], MTUArrayUtils.getArrayIndexesWithBinaryValue(binarizeddata[regulatorpos], false));
					
						int sum=0;
						// number of times that gene it is on
						for (int j = 0; j < regulatedvsregulatoroff.length; j++) {
							sum+=regulatedvsregulatoroff[j];
						}
						
					    // probability of gene is on when TF is off
						double prob=(double)sum/regulatedvsregulatoroff.length;
					
						mapIndexTFGeneInteractionToItsProbalility.put(i, prob);
					}
					else{
						mapIndexTFGeneInteractionToItsProbalility.put(i, 1.0);
						lost_tfgeneinteraction.add(i);
					}
				}
				else{
					mapIndexTFGeneInteractionToItsProbalility.put(i, 1.0);
					lost_tfgeneinteraction.add(i);
				}

			}
			
		 }
	}
	

	



	protected void configureParameters(){
		try {
			loadExpressionData();
		    getRegulatorsTargetsMap();
		    getKappaAndDatathreshold();
		    getTFsSubsets();  
		} catch (Exception e) {
            throw new WrongFormulationException(e);
		}
	}
	
	
	protected double getValueWithThreshold(double v){
		if(Math.abs(v)<thresholdlimit)
			return 0.0;
		return v;
	}
	
	@Override
	public SteadyStateSimulationResult simulate() throws PropertyCastException, MandatoryPropertyException, WrongFormulationException, SolverException {
		
		
		if (debug_times) initTime = System.currentTimeMillis();
		configureAndSimulateProblem();
		
		if (debug_times) times.put("solve", System.currentTimeMillis() - initTime);
				
		return assembleSolution();
	}
	
	
	protected void configureAndSimulateProblem() throws PropertyCastException, MandatoryPropertyException, WrongFormulationException, SolverException {
		
		try{
			configureListener();
			configureParameters();
			runFluxVariability();
			processExperimentalDataset();
			setTFGeneProbabilities();
			runWildTypeFluxBalanceAnalysis();
			initProblem();
			run();
		} catch (Exception e) {
            throw new WrongFormulationException(e);
		}
	}
	
	protected void run() throws Exception{
		
		addPropertyNameToProgress("promstatus");
		addPropertyNameToProgress("promprogress");
		setCurrentProgress("promprogress", 0);
		
		
		ISteadyStateGeneReactionModel modelcast=(ISteadyStateGeneReactionModel) model;
		
		LinkedHashMap<String, Double> vm=new LinkedHashMap<>(wildtyperesults.getFluxValues().size());
		for (String vf : wildtyperesults.getFluxValues().keySet()) {
			vm.put(vf, 0.0);
		}
		
		HashMap<String, Double> weights11 = new HashMap<String, Double>();
		weights11.put(getBiomassID(), 1.0);
		
		int numberreactions=model.getNumberOfReactions();
		IndexedHashMap<String, Gene> metabolicgenes=modelcast.getGenes();
		
		for (int i = 0; i < tfnames.size() && execute(); i++) {
			String tf=tfnames.get(i);
			setCurrentStatus("promstatus", "Analysing regulator: "+tf);
			boolean belongstometabolicgenes=metabolicgenes.containsKey(tf);
			
			//MTULogUtils.addDebugMsg("Current TF: {}", tf);
			
			resetVariableParameters();
			
			int[] targetindexes=getIndexesOfTF(tf);
			ArrayList<String> genesaffectedbytf=getTargetNamesFromIndexes(targetindexes);
			
			ArrayList<Integer> controlreactionsaffectedbytf=new ArrayList<>();
			ArrayList<Boolean> controlconstrainedReactions=new ArrayList<>();
			
			Multimap<String, Integer> mapgenetoreaction=ArrayListMultimap.create();
		

			for (int j = 0; j < genesaffectedbytf.size(); j++) {
				String genename=genesaffectedbytf.get(j);
				
				Double geneprobability=null;
				

				if(mapIndexTFGeneInteractionToItsProbalility!=null && mapIndexTFGeneInteractionToItsProbalility.containsKey(targetindexes[j]))
					geneprobability=mapIndexTFGeneInteractionToItsProbalility.get(targetindexes[j]);
				
				ArrayList<Integer> controlduplicatesamereactionforgenename=new ArrayList<>();
				ArrayList<String> affectedreactionsbygene=modelcast.getReactionsInfluencedByGene(genename);
				
			
			    if(affectedreactionsbygene!=null){
			      for (int k = 0; k < affectedreactionsbygene.size() && execute(); k++) {
			    	int reactindex=model.getReactionIndex(affectedreactionsbygene.get(k));
			    	
			    	
			    	Reaction currentreaction=model.getReaction(reactindex);
			    	String currentreactionname=model.getReactionId(reactindex);
			    	ReactionConstraint currentreactionconstraints=null;
			    	if(getEnvironmentalConditions()!=null && getEnvironmentalConditions().containsKey(currentreactionname)){
			    		currentreactionconstraints=getEnvironmentalConditions().get(currentreactionname);
			    	}
			    	else
			    		currentreactionconstraints=currentreaction.getConstraints();
			    	
			    	if(belongstometabolicgenes){
			    		if(currentreaction.isReversible()){
			    			tempreactionlimits.get(currentreactionname).setLowerLimit(-thresholdlimit);
			    			tempreactionlimits.get(currentreactionname).setUpperLimit(thresholdlimit);
			    		}
			    		else
			    			tempreactionlimits.get(currentreactionname).setLowerLimit(-thresholdlimit);
			    	}
			    
			    	if(!controlduplicatesamereactionforgenename.contains(reactindex)){
			    	   controlreactionsaffectedbytf.add(reactindex);
			    	   mapgenetoreaction.put(genename, reactindex);
			    	   controlduplicatesamereactionforgenename.add(reactindex);
			    	   
			    	   GeneReactionRule rule=modelcast.getGeneReactionRule(model.getReactionId(reactindex));
			    	   boolean istoconstrain=constrainReaction(genesaffectedbytf, rule);
			    	   controlconstrainedReactions.add(istoconstrain);
			    	  
			    	   if(istoconstrain && geneprobability!=null){
			    		   
			    		  if(geneprobability<1){
			    			  
			    			  double wildtypeflux=getValueWithThreshold(wildtyperesults.getFluxValues().get(currentreactionname));
			    			  
			    			  if(geneprobability!=0.0){
			    				  
			    				  Pair<Double, Double> currentreactionvariability=getFluxVariabilityAnalysisForReactionId(currentreactionname);
			    			  
			    				  double[] fluxvalues= new double[]{currentreactionvariability.getValue0(), currentreactionvariability.getValue1(), wildtypeflux};
			    			  
			    				  if(wildtypeflux<0){
			    					  //vm.put(currentreactionname, VMThreshold(Collections.min(Arrays.asList(ArrayUtils.toObject(fluxvalues)))));
			    					  vm.put(currentreactionname,Collections.min(Arrays.asList(ArrayUtils.toObject(fluxvalues))));
			    				  }
			    				  else if(wildtypeflux>0){
			    					  //vm.put(currentreactionname, VMThreshold(Collections.max(Arrays.asList(ArrayUtils.toObject(fluxvalues)))));
			    					  vm.put(currentreactionname, Collections.max(Arrays.asList(ArrayUtils.toObject(fluxvalues))));
			    				  }
			    				  else{
			    					  fluxvalues= new double[]{Math.abs(currentreactionvariability.getValue0()), Math.abs(currentreactionvariability.getValue1()), Math.abs(wildtypeflux)};
			    					  //vm.put(currentreactionname, VMThreshold(Collections.max(Arrays.asList(ArrayUtils.toObject(fluxvalues)))));
			    					  vm.put(currentreactionname, Collections.max(Arrays.asList(ArrayUtils.toObject(fluxvalues))));
			    				  }
			    				  
			    			  }
			    			  
			    			 
			    			  double xx=vm.get(currentreactionname)*geneprobability;
			    			  
			    			  if(wildtypeflux<0){
			    				  double[] temvalues= new double[]{currentreactionconstraints.getLowerLimit(), xx, tempreactionlimits.get(currentreactionname).getLowerLimit()};
			    				  double temmax=Collections.max(Arrays.asList(ArrayUtils.toObject(temvalues)));
			    				  tempreactionlimits.get(currentreactionname).setLowerLimit(Math.min(temmax, -thresholdlimit));
			    			      String varname=indexToIdVarMapings.get(numberreactions+reactindex);
			    				  newproblemreactionconstraints.get(varname).setUpperLimit(1000);
			    				  double tempweight11=(-1*penaltykappa/Math.abs(vm.get(currentreactionname)))*Math.abs(initsolution.getOFvalue());
			    				  
			    				  double vv=Math.max(Math.abs(vm.get(currentreactionname)), mthreshold);
			    				  double vb=(-penaltykappa*Math.abs(initsolution.getOFvalue()))/Math.abs(vv);
			    				  
			    				  weights11.put(varname, Math.min(vb, tempweight11));
			    				 // System.out.println("Flux less 0  reaction:"+currentreactionname+"  weigth: "+tempweight11+ " vv: "+vv+ " vb: "+vb);
			    			  }
			    			  
			    			  else if(wildtypeflux>0){
			    				  double[] temvalues= new double[]{currentreactionconstraints.getUpperLimit(), xx, tempreactionlimits.get(currentreactionname).getUpperLimit()};
			    				  double temmin=Collections.min(Arrays.asList(ArrayUtils.toObject(temvalues)));
			    				  tempreactionlimits.get(currentreactionname).setUpperLimit(Math.max(temmin, thresholdlimit));
			    				  String varname=indexToIdVarMapings.get(numberreactions*2+reactindex);
			    				  newproblemreactionconstraints.get(varname).setUpperLimit(1000);
			    				  double vv=Math.max(Math.abs(vm.get(currentreactionname)), mthreshold);
			    				  double vb=(-penaltykappa*Math.abs(initsolution.getOFvalue()))/Math.abs(vv);
			    				  
			    				  double tempweight=0.0;
			    				  if(weights11.containsKey(varname)){
			    					  tempweight=weights11.get(varname);
			    					 // weights11.put(varname, Math.min(tempweight, vb));
			    				  }
			    				  /*else{
			    					  weights11.put(varname, Math.min(0.0, vb));
			    				  }*/
			    				  weights11.put(varname, Math.min(tempweight, vb));
			    				 // System.out.println("Flux upper 0  reaction:"+currentreactionname+"  weigth: "+tempweight+ " vv: "+vv+ " vb: "+vb);  
			    			  }
			    		  } 
			    	   	}  
			    	}
				  }
			    }
			 }
			
			
			LinkedHashMap<String, Boolean> reactstate=new LinkedHashMap<>();
			for (int j = 0; j < controlreactionsaffectedbytf.size(); j++) {
				reactstate.put(model.getReactionId(controlreactionsaffectedbytf.get(j)), controlconstrainedReactions.get(j));	
			}
			
			reactionstatesinfluencedbytf.put(tf, reactstate);

			setObjectiveFunction(weights11);
			SteadyStateSimulationResult currentsolution=buildCurrentProblemAndSimulate();
			
			
			LPSolutionType typesolution=currentsolution.getSolutionType();
			FluxValueMap currentfluxvalues=currentsolution.getFluxValues();
			double biomasscoef=currentfluxvalues.get(getBiomassID());
			double objfunctval=currentsolution.getOFvalue();
			
			
			
			HashMap<String, ReactionConstraint> tempcopyconstraints=copyConstraints(tempreactionlimits);
			
			HashMap<String, Double> tmpobj=new HashMap<>();
			tmpobj.put(getBiomassID(), 1.0);
			setObjectiveFunction(tmpobj);
			
			
			int count=0;
			boolean stop=false;
			while ((!typesolution.equals(LPSolutionType.OPTIMAL) || biomasscoef< 0.0) && !stop) {
				
				HashMap<String,ReactionConstraint> modreactlimits=new HashMap<>();
				
				
				for (String reactid: tempreactionlimits.keySet()) {
					
					ReactionConstraint c=tempreactionlimits.get(reactid);
					double lbm=c.getLowerLimit();
					double ubm=c.getUpperLimit();
					
					double nlb=0.0;
					double nub=0.0;
					
					double modellb=0.0;
					double modelub=0.0;
					if(getEnvironmentalConditions()!=null && getEnvironmentalConditions().containsKey(reactid)){
						modellb=getEnvironmentalConditions().get(reactid).getLowerLimit();
						modelub=getEnvironmentalConditions().get(reactid).getUpperLimit();
					}
					else{
						modellb=model.getReactionConstraint(reactid).getLowerLimit();
						modelub=model.getReactionConstraint(reactid).getUpperLimit();
					}
					
					
					if(lbm!=modellb)
						nlb=lbm-0.001;
					else
						nlb=lbm;
					
					if(ubm!=modelub)
						nub=ubm+0.001;
					else
						nub=ubm;
					
					ReactionConstraint newconst=new ReactionConstraint(nlb, nub);
					modreactlimits.put(reactid, newconst);
					
				}
				
					tempreactionlimits=modreactlimits;
				
				
					currentsolution=buildCurrentProblemAndSimulate();
					typesolution=currentsolution.getSolutionType();
					biomasscoef=currentsolution.getFluxValues().get(getBiomassID());
					//System.out.println(biomasscoef);
					count++;
					//System.out.println("Verifing new solution: "+count+" "+biomasscoef);
					
					if(count>50){
						
				
						SteadyStateSimulationResult simplefba=runFBAWithConditions(buildEnvironmentalCond(tempcopyconstraints), tmpobj, true);
						
					
						biomasscoef=simplefba.getOFvalue();
						currentfluxvalues.setValue(getBiomassID(), biomasscoef);
						
						stop=true;
					}
			}
			
			promSimulatedBiomassWithTF.put(tf, biomasscoef);
			promSimulatedFluxValuesWithTF.put(tf, currentfluxvalues);
			
			SteadyStateSimulationResult runFBAwithko=runFBAWithConditions(buildEnvironmentalCond(tempreactionlimits), tmpobj, true);
			//System.out.println(tf+" "+objfunctval+" "+biomasscoef+" with ko: "+runFBAwithko.getOFvalue());
			promGrowthSimulationWithTFEffect.put(tf, runFBAwithko.getOFvalue());
			promGrowthSimulationFluxValuesWithTFEffect.put(tf, runFBAwithko.getFluxValues());
			
			float progress = ((float)i+1)/(float)tfnames.size();
			setCurrentProgress("promprogress", progress);
		}
       
		setCurrentStatus("promstatus", "Saving results");
		setCurrentProgress("promprogress", 100);
	
	}
	
	
	protected SteadyStateSimulationResult assembleSolution(){
		
		LinkedHashMap<String, HashMap<String, Double>> mapofprobability=new LinkedHashMap<>();
		
		for (int i = 0; i < regulatororderlist.size(); i++) {
			String reg=regulatororderlist.get(i);
			
			if(!mapofprobability.containsKey(reg)){
				HashMap<String, Double> geneprob=new HashMap<>();
				geneprob.put(regulatedorderlist.get(i), mapIndexTFGeneInteractionToItsProbalility.get(i));
				mapofprobability.put(reg, geneprob);
			}
			else{
				mapofprobability.get(reg).put(regulatedorderlist.get(i), mapIndexTFGeneInteractionToItsProbalility.get(i));
			}
			
		}

		return new PromSimulationResult(model, "PROM",
				regulatortotargetsmap, 
				//PromFluxResultsForTFs, 
				promSimulatedBiomassWithTF,
				promSimulatedFluxValuesWithTF,
				promGrowthSimulationWithTFEffect,
				promGrowthSimulationFluxValuesWithTFEffect,
				mapofprobability,
				reactionstatesinfluencedbytf,
				expressiondataset,
				getEnvironmentalConditions(),
				penaltykappa,
				userdatathreshold,
				minpvalue,
				datathreshold);
	}
	
	
	protected SteadyStateSimulationResult buildCurrentProblemAndSimulate(){
		
		LPProblem p=getProblem();
		
		LPSolution solution =null;
		String solverType = getSolverType();
		try {
		  _solver= SolverFactory.getInstance().lpSolver(solverType, p);
		   solution = _solver.solve();
		} catch (Exception e) {
			throw e;
		}
		  
		return convertLPSolutionToSimulationSolution(solution);
	}
	
	protected HashMap<String, ReactionConstraint> copyConstraints(HashMap<String, ReactionConstraint> tocopy){
		HashMap<String, ReactionConstraint> copy=new HashMap<>();
		
		for (String rid : tocopy.keySet()) {
			ReactionConstraint rctocopy=tocopy.get(rid);
			ReactionConstraint newrc=new ReactionConstraint(rctocopy.getLowerLimit(), rctocopy.getUpperLimit());
			copy.put(rid, newrc);
		}
		
		return copy;
		
	}
	

	
	protected void initProblem(){
		initVariables();
		this.p=getProblem();
		String solverType = getSolverType();
		 _solver= SolverFactory.getInstance().lpSolver(solverType, p);
		
		LPSolution solution = _solver.solve();
		this.initsolution=convertLPSolutionToSimulationSolution(solution);
	}
	
	@Override
	public LPProblem getProblem() throws MandatoryPropertyException, PropertyCastException, WrongFormulationException {
		if(problem==null)
			createProblem();
		else
			recreateProblem();
		return problem;
	}
	
	protected void createProblem(){
		this.problem=constructEmptyProblem();
		createVariables();
		createConstraints();
		createObjectiveFunction();
		putObjectiveFunctionIntoProblem();	
	}
	
	protected void recreateProblem(){
		updateVariables();
		updateConstraints();
		createObjectiveFunction();
		putObjectiveFunctionIntoProblem();	
	}
	
	
	
	protected void resetVariableParameters(){
		this.tempreactionlimits=new HashMap<>(model.getNumberOfReactions());
		
		EnvironmentalConditions envconds=getEnvironmentalConditions();
		
		for (int i = 0; i <model.getNumberOfReactions(); i++) {
			Reaction react=model.getReaction(i);
			if(envconds!=null && envconds.containsKey(react.getId())){
				ReactionConstraint rc=envconds.get(react.getId());
				tempreactionlimits.put(react.getId(), new ReactionConstraint(rc.getLowerLimit(), rc.getUpperLimit()));
			}
			else	
			    tempreactionlimits.put(react.getId(), new ReactionConstraint(react.getConstraints().getLowerLimit(), react.getConstraints().getUpperLimit()));
		}
		
		newproblemreactionconstraints=new HashMap<>();
		
		int numberModelVariables = model.getNumberOfReactions();
		
		for (int i = 0; i < listReactionVariables.size(); i++) {

			String reactname=listReactionVariables.get(i);
			
			double lb=0.0;
			double ub=0.0;
			
			if(i<numberModelVariables){
				lb=-1000;
				ub=1000;
			}
			newproblemreactionconstraints.put(reactname, new ReactionConstraint(lb, ub));
		}

	}
	
    protected void initVariables(){
    	
    	this.listReactionVariables=new ArrayList<>();
    	int numberVariables = model.getNumberOfReactions();
		int totalvaribles=numberVariables*3;
		
		for (int i = 0; i < totalvaribles; i++) {
			Reaction r =null;
			String reactname=null;
			if(i<numberVariables){
				r=model.getReaction(i);
				reactname=r.getId();
			}
			else if(i>=numberVariables && i<numberVariables*2){
				r=model.getReaction((i+numberVariables)-(numberVariables*2));
				reactname=r.getId()+"_mat1";
			}
			else{
				r=model.getReaction((i+numberVariables)-(numberVariables*3));
				reactname=r.getId()+"_mat2";
			}
			listReactionVariables.add(reactname);
		}
		
		resetVariableParameters();
    }

    
    @Override
	protected void createVariables() throws PropertyCastException, MandatoryPropertyException, WrongFormulationException {
		
		for (int i = 0; i < listReactionVariables.size(); i++) {
			
			String reactid=listReactionVariables.get(i);
			
			putVarMappings(reactid, i);
			LPVariable var = new LPVariable(reactid, newproblemreactionconstraints.get(reactid).getLowerLimit(), newproblemreactionconstraints.get(reactid).getUpperLimit());
			problem.addVariable(var);
		}
	}
    
    protected void updateVariables(){
    	for (int i = 0; i < listReactionVariables.size(); i++) {
    		String reactid=listReactionVariables.get(i);
    		LPVariable var =problem.getVariable(idToIndexVarMapings.get(reactid));
    		var.setLowerBound(newproblemreactionconstraints.get(reactid).getLowerLimit());
    		var.setUpperBound(newproblemreactionconstraints.get(reactid).getUpperLimit());
		}
    }
    
    
    protected double getbvalue(boolean limit, boolean isUB,int index){
    
    	if(!limit)
    		return 0.0;
    	else if(limit && !isUB)
    		return tempreactionlimits.get(model.getReaction(index).getId()).getLowerLimit();
    	else
    		return tempreactionlimits.get(model.getReaction(index).getId()).getUpperLimit();
    }
    
    @Override
	protected void createConstraints() throws WrongFormulationException, PropertyCastException, MandatoryPropertyException {
		int numberVariables = model.getNumberOfReactions();
		int numberConstraints = model.getNumberOfMetabolites();
		
		INDArray auxidentitymatrix=Nd4j.eye(numberVariables);

		int totalvariables=listReactionVariables.size();
		
		int currentPos=0;
		for (int i = 0; i < numberConstraints; i++) {
			LPProblemRow row = new LPProblemRow();
			
			for (int j = 0; j < totalvariables; j++) {
				double value =0.0;
				
				if(j<numberVariables){
					value = model.getStoichiometricValue(i, j);	
				}
				
				if (value != 0)try{
					row.addTerm(j, value);	
				} catch (LinearProgrammingTermAlreadyPresentException e) {
					throw new WrongFormulationException("Cannot add term " + j + "to row with value: " + value);
				}
			
			}
			
			LPConstraint constraint = new LPConstraint(LPConstraintType.EQUALITY, row, getbvalue(false, false, i));
			problem.addConstraint(constraint);
			currentPos++;
		}
		
		endofLpconstraintType.add(currentPos);
		
		for (int i = 0; i < numberVariables; i++) {
			LPProblemRow row = new LPProblemRow();
			
			for (int j = 0; j < totalvariables; j++) {
				double value =0.0;
				
				if(j<numberVariables)
				     value=auxidentitymatrix.getDouble(i, j);
				else if(j>=numberVariables && j<numberVariables*2)
					value=auxidentitymatrix.getDouble(i, ((j+numberVariables)-(numberVariables*2)));
				
				
				if (value != 0)try{
					row.addTerm(j, value);	
				} catch (LinearProgrammingTermAlreadyPresentException e) {
					throw new WrongFormulationException("Cannot add term " + j + "to row with value: " + value);
				}
			}
		
			LPConstraint constraint = new LPConstraint(LPConstraintType.GREATER_THAN, row, getbvalue(true, false, i));
			problem.addConstraint(constraint);
			
			currentPos++;
		}
		
		endofLpconstraintType.add(currentPos);
		
		for (int i = 0; i < numberVariables; i++) {
			LPProblemRow row = new LPProblemRow();
			
			for (int j = 0; j < totalvariables; j++) {
				double value =0.0;
				
				if(j<numberVariables)
				     value=auxidentitymatrix.getDouble(i, j);
				else if(j>=numberVariables && j<numberVariables*2)
					value=0.0;
				else{
					value=auxidentitymatrix.getDouble(i, ((j+numberVariables)-(numberVariables*3)));
					if(value!=0.0)
						value=value*-1;
				
				}
				
				
				if (value != 0)try{
					row.addTerm(j, value);	
				} catch (LinearProgrammingTermAlreadyPresentException e) {
					throw new WrongFormulationException("Cannot add term " + j + "to row with value: " + value);
				}
			}
			
			LPConstraint constraint = new LPConstraint(LPConstraintType.LESS_THAN, row,getbvalue(true, true, i));
			problem.addConstraint(constraint);
			currentPos++;
		}
		endofLpconstraintType.add(currentPos);
	}
    
    
    public void updateConstraints(){
    	int begin=0;
		
		for (int i = 0; i < endofLpconstraintType.size(); i++) {
			
			int end=endofLpconstraintType.get(i);
			
			int index=0;
			for (int j = begin; j < end; j++) {
				
				if(i==0){
					problem.getConstraint(j).setRightSide(getbvalue(false, false, index));
				}
				else if(i==1){
					problem.getConstraint(j).setRightSide(getbvalue(true, false, index));
				}
				else
					problem.getConstraint(j).setRightSide(getbvalue(true, true, index));

				index++;
				
			}
			begin=end;
		}
    }
    
   /* public void updateConstraints(){
    	int begin=0;
		//int total=0;
		for (int i = 0; i < endofLpconstraintType.size(); i++) {
			
			int end=endofLpconstraintType.get(i);
			
			int index=0;
			for (int j = begin; j < end; j++) {
				
				if(i==0){
					problem.getConstraint(j).setRightSide(0.0);
				}
				else if(i==1){
					problem.getConstraint(j).setRightSide(tempreactionlimits.get(model.getReactionId(index)).getLowerLimit());
				}
				else
					problem.getConstraint(j).setRightSide(tempreactionlimits.get(model.getReactionId(index)).getUpperLimit());

				index++;
				//total++;
			}
			begin=end;
		}
    }*/
	
	
	
	
	public void setIsMaximization(boolean isMaximization) {
		properties.put(SimulationProperties.IS_MAXIMIZATION, isMaximization);

	}
	
	public boolean getIsMaximization() throws PropertyCastException, MandatoryPropertyException {
		boolean ismaximization=true;
		try {
			ismaximization=ManagerExceptionUtils.testCast(properties, Boolean.class, SimulationProperties.IS_MAXIMIZATION, false);
		} catch (Exception e) {
			ismaximization=true;
		}
		return ismaximization;
	}
	
	@Override
	protected void createObjectiveFunction() throws PropertyCastException, MandatoryPropertyException {
		problem.setObjectiveFunction(new LPProblemRow(), getIsMaximization());
		objTerms.clear();
		
		Map<String, Double> obj_coef = getObjectiveFunction();
		for (String r : obj_coef.keySet()) {
			double coef = obj_coef.get(r);
			objTerms.add(new VarTerm(getIdToIndexVarMapings().get(r), coef, 0.0));
		}
		//System.out.println("OBJFUNC: "+obj_coef);
	}

	
	

	@Override
	public boolean checkIfMandatoryPropertiesSatisfied(Map<String, Object> properties) {
		
		
		
		return false;
	}



	@Override
	public String getObjectiveFunctionToString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static int[][] binarizePROMExpressionDataset(double[][] quantilenormalizeddataset, double datathreshold){
		
		int[][] binarizeddata=new int[quantilenormalizeddataset.length][quantilenormalizeddataset[0].length];
		
			for (int i = 0; i < quantilenormalizeddataset.length; i++) {
				
			    for (int j = 0; j < quantilenormalizeddataset[0].length; j++) {
					
			    	double value=quantilenormalizeddataset[i][j];
			    	
			    	if(datathreshold<0){
			    		if(value>=datathreshold)
			    			binarizeddata[i][j]=1;
			    		else
			    			binarizeddata[i][j]=0;
			    	}
			    	else{
			    		if(value<datathreshold)
			    			binarizeddata[i][j]=0;
			    		else
			    			binarizeddata[i][j]=1;
			    	}
				}
			}
	
		return binarizeddata;
	}
	
	
	protected void runWildTypeFluxBalanceAnalysis() throws Exception{
		
		SimulationSteadyStateControlCenter simulationControlCenter = new SimulationSteadyStateControlCenter(getEnvironmentalConditions(), null, model,SimulationProperties.FBA);
		simulationControlCenter.setSolver(getSolverType());
        simulationControlCenter.setMaximization(true);
		simulationControlCenter.setFBAObj(getObjectiveFunction());
		this.wildtyperesults=simulationControlCenter.simulate();
		//System.out.println("Wild GROWTH: "+wildtyperesults.getOFvalue());
	
	}
	
	protected Pair<Double, Double> getFluxVariabilityAnalysisForReactionId(String reactionid) throws PropertyCastException, MandatoryPropertyException, Exception{
		
		 double[] limits=null;
		 if(fluxvariability!=null && fluxvariability.containsKey(reactionid)){
			 limits=fluxvariability.get(reactionid); 
		 }
		 else{
			 
			 if(fluxvariability==null)
				 fluxvariability=new HashMap<>();
			
			 FBAFluxVariabilityAnalysisNew fluxvariabanalysis=new FBAFluxVariabilityAnalysisNew(this.model,getEnvironmentalConditions(),null, getSolverType());
			 limits=fluxvariabanalysis.limitsFlux(reactionid,1);
			 fluxvariability.put(reactionid, limits);
		 }
		 
		 if(Double.isNaN(limits[0]))
			 limits[0]=0.0;
		 if(Double.isNaN(limits[1]))
			 limits[1]=0.0;
		 
		 return new Pair<Double, Double>(getValueWithThreshold(limits[0]), getValueWithThreshold(limits[1])); 
		 //return new Pair<Double, Double>(limits[0], limits[1]); 
	}
	
	
	
	
		
	protected SteadyStateSimulationResult runFBAWithConditions(EnvironmentalConditions envcond, Map<String, Double> objfunc, boolean maximization) throws Exception{
		SimulationSteadyStateControlCenter simulationControlCenter = new SimulationSteadyStateControlCenter(envcond, null, model,SimulationProperties.FBA);
		simulationControlCenter.setSolver(getSolverType());
		simulationControlCenter.setMaximization(true);
		simulationControlCenter.setFBAObj(objfunc);
		return simulationControlCenter.simulate();
	}
	
	
	
	protected int[] getIndexesOfTF(String tfname){
		ArrayList<Integer> indexes=new ArrayList<>();
		for (int i = 0; i < regulatororderlist.size(); i++) {
			String name=regulatororderlist.get(i);
			if(name.equals(tfname))
				indexes.add(i);
		}
		if(indexes.size()>0)
			return ArrayUtils.toPrimitive(indexes.toArray(new Integer[indexes.size()]));
		else
			return null;
	}
	
	
	protected ArrayList<String> getTargetNamesFromIndexes(int[] indx){
		ArrayList<String> res=new ArrayList<>();
		for (int i = 0; i < indx.length; i++) {
			res.add(regulatedorderlist.get(indx[i]));
		}
		return res;
	}
	
	protected boolean KolmogorovSmirnovTest(double[] x1, double[] x2, double minpvalue){
		
		KSTest ks = KSTest.test(x1, x2);
		double obtpvalue=ks.pvalue;
		if(obtpvalue<minpvalue)
			return true;
		return false;
	}
	
	
	
	protected IEnvironment<IValue> getModelGPRenvironment(){
		IEnvironment<IValue> environment = new Environment<IValue>();
		ISteadyStateGeneReactionModel modelcast=(ISteadyStateGeneReactionModel) model;
		for (int i = 0; i < modelcast.getNumberOfGenes(); i++) {
			environment.associate(modelcast.getGene(i).getId(),new BooleanValue(true));
		}
		return environment;
	}
	
	protected boolean constrainReaction(ArrayList<String> genestofalse, GeneReactionRule gprrule){
		IEnvironment<IValue> environment =getModelGPRenvironment();
		if(gprrule.getRule()==null)
			return false;
		else{
			if(genestofalse!=null && genestofalse.size()>0){
				for (int i = 0; i <genestofalse.size(); i++) {
					environment.associate(genestofalse.get(i), new BooleanValue(false));
				}
			}
			
			if(((Boolean)gprrule.getRule().evaluate(environment).getValue())==false)
				return true;
			else
				return false;	
		}
		
	}
	
	protected EnvironmentalConditions buildEnvironmentalCond(HashMap<String, ReactionConstraint> constr){
		EnvironmentalConditions cond=new EnvironmentalConditions("envcond");
		
		for (String rid : constr.keySet()) {
			
			//cond.addReactionConstraint(rid, constr.get(rid));
			ReactionConstraint c=constr.get(rid);
			cond.addReactionConstraint(rid, new ReactionConstraint(c.getLowerLimit(), c.getUpperLimit()));
		}
		return cond;
	}
	
	protected void printwildtypefluxes(){
		if(matlaborder!=null){
			System.out.println(" Wildtype Flux");
			
			FluxValueMap wildfluxes=wildtyperesults.getFluxValues();
			//System.out.println(wildfluxes);
			for (int i = 0; i < matlaborder.size(); i++) {
				System.out.println(wildfluxes.get(matlaborder.get(i)));
				
			}
			System.out.println("\n\n\n");
		}
	}
	
	
	
	
	

}
