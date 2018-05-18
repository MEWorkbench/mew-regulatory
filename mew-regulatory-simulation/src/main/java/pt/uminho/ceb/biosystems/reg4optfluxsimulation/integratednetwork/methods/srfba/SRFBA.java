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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.srfba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pt.ornrocha.logutils.MTULogUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.GeneReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.core.model.exceptions.NonExistentIdException;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.IOverrideReactionBounds;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.OverrideSteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.abstractions.AbstractSSBasicSimulation;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.abstractions.VarTerm;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.abstractions.WrongFormulationException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.ManagerExceptionUtils;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraintType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPMapVariableValues;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblemRow;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolution;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.MILPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.LinearProgrammingTermAlreadyPresentException;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.MapStringNum;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTree;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTreeNode;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.DataTypeEnum;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.node.And;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.node.Not;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.node.Or;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.node.Variable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.VariableSignValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IIntegratedSteadyStateSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.doublelayer.OptfluxIntegratedSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.GenericRegulatorySimulationResults;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.InvalidRegulatoryModelException;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.Attractor;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;

public class SRFBA extends AbstractSSBasicSimulation<MILPProblem> implements IIntegratedSteadyStateSimulationMethod{
	
	boolean debug = false;
	boolean debugerror=false;
	private IOptfluxRegulatoryModel regulatorymodel;
	private IntegratedNetworkInitialStateContainer variablescontainer;
	private IndexedHashMap<String, Boolean> conditionVariablesState;
	private IndexedHashMap<String, Boolean> proteinVariablesState;
	private ArrayList<String> GeneKnockOuts;
	protected IOverrideReactionBounds overrideconst=null;
	private IndexedHashMap<String, Integer> booleanreactionassociations = null;
	private IndexedHashMap<String, Integer> conditionsassociations =null;
	private RegulatoryGeneticConditions regulatorygeneticconditions=null;
	
	private IndexedHashMap<Integer, String> mapreggenestoregrules=null;
	private ArrayList<String> unconstrainedgenes=null;
	
	private static String BOOLID="BV_";
	private static String BOOLNOT="XNot_";
	private static String BOOLOR="XOr_";
	private static String BOOLAND="XAnd_";
	private static String CVAR="XC_";
	private static double epsilon = 0.001;
	static private double INF = 100000000.0;
	
	//private IOverrideReactionBounds overrideRC=null;

	

	public SRFBA(ISteadyStateModel model) throws InvalidRegulatoryModelException {
		super(model);
		initsrfbaproperties();
		try {
			this.regulatorymodel=(IOptfluxRegulatoryModel) ((IIntegratedStedystateModel)model).getRegulatoryNetwork();
		} catch (Exception e) {
			throw new InvalidRegulatoryModelException("This method does not support the current regulatroy model");
		}
		
		MTULogUtils.addDebugMsgToClass(this.getClass(), "########################## INIT SRFBA #########################");
		
	}
	
	
	
	private void initsrfbaproperties(){
		mandatoryProperties.add(RegulatorySimulationProperties.OBJECTIVE_FUNCTION);
		optionalProperties.add(RegulatorySimulationProperties.VARIABLES_CONTAINER);
		optionalProperties.add(RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS);
		optionalProperties.add(RegulatorySimulationProperties.FORCEINITIALIZATIONTFSTRUESTATE);

	}
	
	
	
	  @SuppressWarnings({ "rawtypes", "unchecked" })
		public HashMap<String, Boolean> getComponentsInitialState(){
	    	return ManagerExceptionUtils.testCast(properties, HashMap.class, RegulatorySimulationProperties.COMPONENTINITIALSTATE, true);   
	    }
	
	
	@Override
	public GeneticConditions getGeneticConditions() throws PropertyCastException, MandatoryPropertyException{
		
		RegulatoryGeneticConditions reggencond = (RegulatoryGeneticConditions) ManagerExceptionUtils.testCast(properties, RegulatoryGeneticConditions.class, RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS, true);
		return reggencond;
	}
	
	public boolean getIsMaximization() throws PropertyCastException, MandatoryPropertyException{
		return ManagerExceptionUtils.testCast(properties, Boolean.class, SimulationProperties.IS_MAXIMIZATION, false);
	}


	
	@Override
	public String getObjectiveFunctionToString() {
		String ret = "";
		boolean max = true;
		try {
			max = getIsMaximization();
		} catch (PropertyCastException e) {
			e.printStackTrace();
		} catch (MandatoryPropertyException e) {
			e.printStackTrace();
		}
		
		if(max)
			ret = "max:";
		else
			ret = "min:";
		Map<String, Double> obj_coef = getObjectiveFunction();
		for(String id : obj_coef.keySet()){
			double v = obj_coef.get(id);
			if(v!=1)
				ret += " " + v;
			ret +=  " " + id;
		}
		
		return ret;
	}
	
	
	  @Override
	  public IOverrideReactionBounds createModelOverride() throws MandatoryPropertyException, PropertyCastException, WrongFormulationException {

			IOverrideReactionBounds overrideRC;

			EnvironmentalConditions environmentalConditions = getEnvironmentalConditions();
			GeneticConditions geneticConditions = getGeneticConditions();
/*			System.out.println("\n\n");
			System.out.println(environmentalConditions);
			System.out.println("\n\n");*/
			overrideRC = new OverrideSteadyStateModel(model, environmentalConditions, geneticConditions);

			return overrideRC;
		}
	
	/////////////////////////////////////////////////////////////////// Simulation and solution treatment methods ///////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	@Override
	public SteadyStateSimulationResult convertLPSolutionToSimulationSolution(LPSolution solution) throws PropertyCastException, MandatoryPropertyException {
	
		OptfluxIntegratedSimulationResult simulationresult=null;
		
		FluxValueMap fluxValues = null;
		LPSolutionType solutionType=null;
		String solverOutput =null;		
		Double oFvalue =Double.NaN;	
		String oFString=null;
		
					 
				
		try {		

		   if(solution == null){
			   solutionType=LPSolutionType.ERROR;
			   solverOutput = "The solver " + getSolverType() + " cannot generate an output..." ;
		     }
		   else{
			  fluxValues=getFluxValueListFromLPSolution(solution);
			  solutionType=solution.getSolutionType();
			  solverOutput=solution.getSolverOutput();
			  oFvalue=solution.getOfValue();
			  oFString=getObjectiveFunctionToString();	         
		    }
            
		   IRegulatoryModelSimulationResult regulatorystate=getRegulatorySimulationResults(solution);
		   simulationresult=new SRFBASimulationResults(model, 
				    getEnvironmentalConditions(),
				    getGeneticConditions(),
					regulatorygeneticconditions, 
					fluxValues, 
					solverOutput,
					oFvalue, 
					oFString, 
					solutionType, 
					regulatorystate,
					variablescontainer);
		  

			
		  } catch (Exception e) {
			  System.out.println(e.getMessage());
			//e.printStackTrace();
		  }
		
		  if(solutionType!=null && !solutionType.equals(LPSolutionType.ERROR)){
		      putMetaboliteExtraInfo(solution,simulationresult);
	          putReactionExtraInfo(solution,simulationresult);
		  }

		return simulationresult;
	}
	
	
	
	
	
	
	
	//############################# Support methods to build the IntegratedSimulationResult ######################## 
	
	protected void putReactionExtraInfo(LPSolution solution,SteadyStateSimulationResult res) {
		Map<String, MapStringNum> complementary = new HashMap<String, MapStringNum>();
		
		for(String id: solution.getVariableMetricsIds()){
			
			LPMapVariableValues cInfo =  solution.getPerVariableMetric(id);
			MapStringNum values = convertLPMapToMapString(model, cInfo, true);

			complementary.put(id, values);
		}
		
		res.setComplementaryInfoReactions(complementary);
		
	}
	

	
	protected IRegulatoryModelSimulationResult getRegulatorySimulationResults(LPSolution solution) throws Exception{
		  ArrayList<String> geneids=new ArrayList<>();
		  ArrayList<Boolean> genestates=new ArrayList<>();
		  ArrayList<String> metabgenes=new ArrayList<>();
		  ArrayList<String> onlyreggenes=new ArrayList<>();
		  
		  ArrayList<String> genesknockout = getNameGeneKnockouts(getKnockoutsByIDxMap(solution,this.mapreggenestoregrules));
		  
		  IndexedHashMap<String, Regulator> reggenes=regulatorymodel.getRegulators();
		  
		  for (int i = 0; i < reggenes.size(); i++) {
			  String geneid=reggenes.getKeyAt(i);
			  if(genesknockout.contains(geneid)){
				  genestates.add(false);
				  if(((IIntegratedStedystateModel)model).isMetabolicGene(geneid))
					  metabgenes.add(geneid);
				  else
					  onlyreggenes.add(geneid);	  
			  }
			  else
				  genestates.add(true);
			  geneids.add(geneid);
		  }
		  
		  GeneChangesList metabolicoffgenestate=new GeneChangesList(metabgenes);
		  GeneregulatorychangesList regulatoryoffgenestate=new GeneregulatorychangesList(onlyreggenes);
		  regulatorygeneticconditions=new RegulatoryGeneticConditions(regulatoryoffgenestate, metabolicoffgenestate, (ISteadyStateGeneReactionModel) model, false);
		  //System.out.println("geneids: "+geneids);
		  //System.out.println("States: "+genestates);
		  SRFBARegulatoryStateMemory regulatorystatememory=null;
		  if(geneids.size()>0 && genestates.size()>0)
		     regulatorystatememory=new SRFBARegulatoryStateMemory(geneids, genestates);
		  
		  return new GenericRegulatorySimulationResults(regulatorymodel, variablescontainer.getInitialRegulatoryState(), regulatorystatememory);
		
	}
	
	
	

	
   private ArrayList<Integer> getKnockoutsByIDxMap(LPSolution solution, IndexedHashMap<Integer, String> mapids){
		
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		LPMapVariableValues varValueList = null;
		if(solution != null)
			varValueList = solution.getValues();
		
		
		for (int i = 0; i < mapids.size(); i++) {
			
			double value = varValueList.get(mapids.getKeyAt(i));

		    long roudedValue = Math.round(value);
		    if(roudedValue == 0){
		    	res.add(i);
		    }
		}
		
		
		
		
		
		return res;
		
	}
	

	

	private ArrayList<String> getNameGeneKnockouts(ArrayList<Integer> res){
		
		ArrayList<String> genesout = new ArrayList<String>();
		ArrayList<String> genes = ((IIntegratedStedystateModel) model).getGenesInRegulatoryModel();

		for (int i = 0; i < res.size(); i++) {

			genesout.add(genes.get(res.get(i)));
		}

		return genesout;
	}
	

	
	///////////////////////////////////////////////// Get necessary input information, variables, gene knockouts etc../////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private void getVariablesState() throws PropertyCastException, MandatoryPropertyException,WrongFormulationException{

		
		IntegratedNetworkInitialStateContainer vars = null;
		vars = (IntegratedNetworkInitialStateContainer)ManagerExceptionUtils.testCast(properties, IntegratedNetworkInitialStateContainer.class, RegulatorySimulationProperties.VARIABLES_CONTAINER, true);
		if(vars==null){
			try {
				vars=((IIntegratedStedystateModel) model).getIntegratedVariablesContainerWithValidation(getEnvironmentalConditions());
			} catch (Exception e) {
				throw new WrongFormulationException(e);
			}
			
			if(vars==null)
				vars=((IIntegratedStedystateModel) model).getIntegratedVariablesContainerWihoutValidation();
			
			HashMap<String,Boolean> initialcomponentstate=getComponentsInitialState();
			if(initialcomponentstate!=null)
				vars.getInitialRegulatoryState().initializeComponentsBooleanState(initialcomponentstate);
		}
		this.variablescontainer=vars;
		
		Boolean forcetftruestate=null;
		try {
			forcetftruestate=ManagerExceptionUtils.testCast(properties, Boolean.class, RegulatorySimulationProperties.FORCEINITIALIZATIONTFSTRUESTATE, true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(forcetftruestate!=null && forcetftruestate)
			vars.forceInitializationTranscriptionalFactorsAsTrue();
		
		
		this.conditionVariablesState=vars.getUserVariablesState();
		//System.out.println("Variables State: "+conditionVariablesState);
		//System.out.println("conditionVariablesState "+conditionVariablesState);
		this.proteinVariablesState=vars.getTFsVariablesState();
		//System.out.println("proteinVariablesState "+proteinVariablesState);

	}


	
	
	//////////////////////////////////////////////////// Build Initial Milp Problem and define objective function ////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public MILPProblem constructEmptyProblem() {
		return new MILPProblem();
	}
	
	
	 @SuppressWarnings("unchecked")
     public Map<String, Double> getObjectiveFunction(){
	   Map<String, Double> obj_coef = null;
	   try {
		 obj_coef = ManagerExceptionUtils.testCast(properties, Map.class, RegulatorySimulationProperties.OBJECTIVE_FUNCTION, false);
		    } catch (Exception e) {
			obj_coef = new HashMap<String, Double>();
			obj_coef.put(((IIntegratedStedystateModel) model).getBiomassFlux(), 1.0);
			}
			return obj_coef;
		   }
	
	

	@Override
	protected void createObjectiveFunction() throws WrongFormulationException,PropertyCastException, MandatoryPropertyException {
        problem.setObjectiveFunction(new LPProblemRow(), getIsMaximization());
		
		Map<String, Double> obj_coef = getObjectiveFunction();
		for (String r : obj_coef.keySet()) {
			double coef = obj_coef.get(r);
			objTerms.add(new VarTerm(getIdToIndexVarMapings().get(r), coef, 0.0));
		}
	}
	

		
	//////////////////////////////////////// set information in MILP Problem ////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	private void addBooleanVarToMILP(String namevar){
		problem.addIntVariable(namevar, 0, 1);
	}


	
	private void addEquationToMILP(int[] colIdx, double[] colVal, double lowerB, double upperB) throws LinearProgrammingTermAlreadyPresentException{
		LPProblemRow left = new LPProblemRow();
		
		for(int i =0; i < colIdx.length; i++){
			left.addTerm(colIdx[i], colVal[i]);
		}

		problem.addConstraint(left, LPConstraintType.LESS_THAN, upperB);
		problem.addConstraint(left,LPConstraintType.GREATER_THAN,lowerB);	
	}
	

	
	
	private int getlastVariableindex(){
		return indexToIdVarMapings.size();
	}
	
	
	
	private  int checkComponentState(String var) throws PropertyCastException, MandatoryPropertyException{
		int ret = -2;
		
        if(conditionVariablesState.containsKey(var)){
        	// if true state
	         if (conditionVariablesState.get(var)){
			    ret = -1;
	         }
        }
        else if(proteinVariablesState.containsKey(var)){
        	// if true state
        	if(proteinVariablesState.get(var)){
        		
		        ret=-1;
		       
        	}
	    }
        else if(unconstrainedgenes!=null && unconstrainedgenes.contains(var)){
        	
        	if(GeneKnockOuts!=null && GeneKnockOuts.contains(var))
        		ret=-2;
        	else
        		ret=-1;
        }
      
		return ret;
	}
	
	
	
	@Override
	protected void createVariables() throws PropertyCastException, MandatoryPropertyException, WrongFormulationException {
		if(debug)
		   System.out.println(getClass().getSimpleName()+": ########## Creating Variables #########");
		super.createVariables();
		
		getVariablesState();
		
		this.unconstrainedgenes=((IIntegratedStedystateModel)model).getUnconstrainedGenes();
		
		if(getGeneticConditions()!=null){
		   this.GeneKnockOuts=((RegulatoryGeneticConditions)getGeneticConditions()).getALLGeneKnockoutList();
		   HashSet<String> reggenes=new HashSet<>();
		   for (int i = 0; i < GeneKnockOuts.size(); i++) {
			  String geneid=GeneKnockOuts.get(i);
			  if(unconstrainedgenes!=null){
				 if(!unconstrainedgenes.contains(geneid))
					reggenes.add(geneid); 
			  }
			  else
				  reggenes.add(geneid); 
				  
		   }
		   if(reggenes.size()>0)
			   this.variablescontainer.getInitialRegulatoryState().setGenesStatePermanentlyOff(reggenes);
		}
		
	
		int nlinkedreactions = variablescontainer.getNumberOfReactionsInBothModels();
		if(nlinkedreactions>0)
		 this.conditionsassociations=new IndexedHashMap<>(nlinkedreactions);
		createGeneVariables();
		createReactionLinkVariables();
	}
	
	@Override
	protected void createConstraints() throws WrongFormulationException, PropertyCastException, MandatoryPropertyException{
		
		if(debug)
			   System.out.println(getClass().getSimpleName()+": ########## Creating Constraints #########");
		super.createConstraints();
		
		
		try {
			genesregulatoryequationsconstraints();
		} catch (Exception e) {
			e.printStackTrace();
		}
			try {
				gprsequationsconstraints();
			} catch (LinearProgrammingTermAlreadyPresentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonExistentIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				reactionboundsequationsconstraints();
			} catch (LinearProgrammingTermAlreadyPresentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	/*	} catch (Exception e) {
			throw new WrongFormulationException(e);
		}*/
		
		
	}
	

	
	private void createGeneVariables(){
		
		int ind =0;
		try{
		int ngenesregmodel = regulatorymodel.getNumberOfRegulators();


		mapreggenestoregrules= new IndexedHashMap<>(ngenesregmodel);
	 
		   for (int i = 0; i < ngenesregmodel; i++) {
			   String boolgeneid = BOOLID+regulatorymodel.getGene(i).getId();
	
			   addBooleanVarToMILP(boolgeneid);
			   int lastvarindex=getlastVariableindex();
			 
			   mapreggenestoregrules.putAt(i, lastvarindex, boolgeneid);

			   putVarMappings(boolgeneid,lastvarindex);
               ind=i;
		    }
         // System.out.println(mapreggenestoregrules);
		 } catch (Exception e) {
				System.out.println("error at index: " +ind);
				throw e;
		} 

	}
	
	
	private void createReactionLinkVariables(){
		
		try{
		int nreactions = ((IIntegratedStedystateModel) model).getNumberOfReactions();
		this.booleanreactionassociations = new IndexedHashMap<>(nreactions);	
		
		
		for (int i = 0; i < nreactions; i++) {
			
			String boolreactid= BOOLID+((IIntegratedStedystateModel) model).getReactionId(i);
			addBooleanVarToMILP(boolreactid);
			int lastvarindex=getlastVariableindex();
			this.booleanreactionassociations.put(boolreactid, lastvarindex);
			putVarMappings(boolreactid,lastvarindex);
			
		  }

		} catch (Exception e) {
			throw e;
		}
		
	}
	
	
	private void genesregulatoryequationsconstraints() throws Exception{
		
		try{

		IndexedHashMap<String, Regulator> regGenes = regulatorymodel.getRegulators();
	
		
		for (int i = 0; i < regGenes.size(); i++) {
			
			int elementIndex = mapreggenestoregrules.getKeyAt(i);

			int index = -3;
		

			AbstractSyntaxTree<DataTypeEnum, IValue> generule =regulatorymodel.getRegulatoryRule(i).getBooleanRule();
			index= convertRulesToEquations(generule);

			if(this.GeneKnockOuts!=null){
				if(this.GeneKnockOuts.contains(regGenes.getKeyAt(i)))
					index=-2;
			}

		     convertValueRuleToMilpRule(index, elementIndex);   
		 }

	  } catch (Exception e) {
		  throw e;
		}
		
	}
	
	
     protected void gprsequationsconstraints() throws LinearProgrammingTermAlreadyPresentException, NonExistentIdException, Exception{
   
    	 
		HashMap<String, GeneReactionRule> rules = ((IIntegratedStedystateModel) model).getGeneReactionRules();
		
		int nboolreactassoc = this.booleanreactionassociations.size();

		
		for(int i =0; i< nboolreactassoc;i++){
			int elementIndex = this.booleanreactionassociations.getValueAt(i);

			GeneReactionRule rule = rules.get(model.getReactionId(i));
			//if(rule!=null)
				//System.out.println("Rule: "+model.getReactionId(i)+" --> "+rule.getRule().toString());
			
			int index = -3;
			if(rule != null){
				//System.out.println("GPR RULE: "+rule.getRule().toString());
				index = convertRulesToEquations(rule.getRule());
			}
			
			convertValueRuleToMilpRule(index, elementIndex);
		}
		
	}
	
	
	
	
	
	
	@SuppressWarnings("unused")
	private void reactionboundsequationsconstraints() throws LinearProgrammingTermAlreadyPresentException, PropertyCastException, MandatoryPropertyException{
		
		int nreactions = ((IIntegratedStedystateModel) model).getNumberOfReactions();
		
		for(int i =0; i < nreactions; i++){
			String namereaction = ((IIntegratedStedystateModel) model).getReactionId(i);
			
			double vMin = getReactionsConstraint(namereaction).getLowerLimit();
			double vMax = getReactionsConstraint(namereaction).getUpperLimit();
            
			
			
			int[] colIdxs = {i,this.booleanreactionassociations.getValueAt(i)};
	
			//TODO verificar estes valores, penso que isto devia ser +upperLimit
			double[] colVals1 = {1, -vMax};
			addEquationToMILP(colIdxs, colVals1, -INF, 0);
			
			double[] colVals2 ={1,-vMin};

			addEquationToMILP(colIdxs, colVals2, 0, INF);
			
			
		}
	}
	
	
	
	
	
	
	
	
	
	private void convertValueRuleToMilpRule(int valueRule, int variable) throws LinearProgrammingTermAlreadyPresentException{
		if(valueRule == -1){
			int[] colIdxs = {variable};
			double[] colVals = {1};
			addEquationToMILP(colIdxs, colVals, 1.0, 1.0);
			
		}else if(valueRule == -2){
			int[] colIdxs = {variable};
			double[] colVals = {1};
			addEquationToMILP(colIdxs, colVals, 0, 0);
			
		}else if(valueRule == -3){
			int[] colIdxs = {variable};
			double[] colVals = {1};
			addEquationToMILP(colIdxs, colVals, 0, 1.0);
			
		}else{
			int[] colIdxs = {variable, valueRule};
			double[] colVals = {1, -1};
			
			addEquationToMILP(colIdxs, colVals, 0, 0);
			
		}
		
	}
	
	private int convertRulesToEquations(AbstractSyntaxTree<DataTypeEnum, IValue> rule) throws LinearProgrammingTermAlreadyPresentException, NonExistentIdException, Exception{
		
		AbstractSyntaxTreeNode<DataTypeEnum, IValue> root = rule.getRootNode();
		ArrayList<Integer> ruleDS_indices = new ArrayList<Integer>();
		Integer ret = null;
		
		//int ret = -3;
		ArrayList<String> booloperators = null;
		
		if(root!=null){
			//System.out.println(rule.toString());
			if(debugerror)
				System.out.println(rule.toString());
			
			booloperators= new ArrayList<>();
			convertVariablesRules(root,booloperators,ruleDS_indices);
			ret = ruleDS_indices.get(0);
		}
		else{
			ret = -3;
		}
       
		return ret;
	}
	
	
	protected void convertVariablesRules(AbstractSyntaxTreeNode<DataTypeEnum, IValue> node, ArrayList<String> variables, ArrayList<Integer> ruleDS_indices) throws LinearProgrammingTermAlreadyPresentException, NonExistentIdException, Exception {
		
		//System.out.println(node.toString());
		if(node!=null){
			for(int i =0 ; i < node.getNumberOfChildren(); i++){
				//System.out.println("Node ID: "+node.getChildAt(i));
				convertVariablesRules(node.getChildAt(i), variables,ruleDS_indices);
			
			}
			
			if(node instanceof Variable){

				String var = node.toString();
				
				RegulatoryVariable regvar=regulatorymodel.getVariablesInRegulatoryNetwork().get(var);
				
				if(regvar!=null && regvar.getVariableSign()!=null){
					
					if(variablescontainer.isUserVarible(regvar.getId())){
						int val=checkComponentState(regvar.getId());
						ruleDS_indices.add(val);
						//System.out.println("user Variable:"+regvar.getId()+" Index:"+val);
					}
				else{
						int val = convertLinkedReactionsVariables(regvar);
						
		                this.conditionsassociations.put(var, val);
						
						ruleDS_indices.add(val);

				    }
				}
				
				else {
					int RegRuleIndex=-1;
					int RegGeneIndex=-1;
					
					if(regvar!=null)
						var=regvar.getId();
					
					
						try {
							RegRuleIndex=regulatorymodel.getRuleIndexForIdentifier(var);
							//System.out.println("var: "+var+" RegRuleIndex: "+RegRuleIndex);
						} catch (Exception e) {
							RegRuleIndex=-1;
						}
					
					    try {
							RegGeneIndex=regulatorymodel.getRegulatoryGeneIndex(var);
							//System.out.println("var: "+var+" RegGeneIndex: "+RegGeneIndex);
						} catch (Exception e) {
							RegGeneIndex=-1;
						}
	

					
					if(RegRuleIndex!=-1 && RegGeneIndex==-1){
						//System.out.println(RegRuleIndex+"-RegRuleIndex->"+var);
						LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage(RegRuleIndex+" RegRuleIndex -> "+var);
						//System.out.println("var: "+var+" RegRuleIndex: "+RegRuleIndex+" index= "+mapreggenestoregrules.getKeyAt(RegRuleIndex));
						ruleDS_indices.add(mapreggenestoregrules.getKeyAt(RegRuleIndex));
						
						
					}
					else if(RegGeneIndex!=-1 && RegRuleIndex==-1){
						LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("RegulatoryGeneIndex->"+var+"-->"+RegGeneIndex+"-->"+mapreggenestoregrules.getKeyAt(RegGeneIndex));
						//System.out.println("RegGeneIndex->"+var+"-->"+RegGeneIndex+"-->"+mapreggenestoregrules.getKeyAt(RegGeneIndex));
						//System.out.println("var: "+var+" RegGeneIndex: "+RegGeneIndex+" index= "+mapreggenestoregrules.getKeyAt(RegGeneIndex));
						ruleDS_indices.add(mapreggenestoregrules.getKeyAt(RegGeneIndex));
					}else{
						
						int varval=checkComponentState(var);
						LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Variable id->"+var+"-->"+varval);
						ruleDS_indices.add(varval);
						//System.out.println("Var Name: "+var+" index: "+varval);
					}
					
				}
				
			}
			else if(node instanceof Not){
				
				variables.add("NOT");
				int lastIndex = ruleDS_indices.size();
				int entry = ruleDS_indices.get(lastIndex-1);
				ruleDS_indices.remove(lastIndex-1);
				
				ruleDS_indices.add(convertNotOperator(entry));
			}else if(node instanceof Or){
				//System.out.println("Left: "+node.getChildAt(0).toString());
				//System.out.println("Right: "+node.getChildAt(1).toString());
				variables.add("OR");
				int lastIndex = ruleDS_indices.size();
			
				int rightEntryValue = ruleDS_indices.get(lastIndex-1);
				int leftEntryValue = ruleDS_indices.get(lastIndex-2);
				//System.out.println("Last Index: "+lastIndex+" --> rightEntryValue: "+rightEntryValue+" --> leftEntryValue: "+leftEntryValue);
				
				ruleDS_indices.remove(lastIndex-1);
				ruleDS_indices.remove(lastIndex-2);
				
				
				ruleDS_indices.add(convertOrOperator(leftEntryValue, rightEntryValue));
			}else if(node instanceof And){
				
				variables.add("AND");
				int lastIndex = ruleDS_indices.size();
				int rightEntryValue = ruleDS_indices.get(lastIndex-1);
				
				int leftEntryValue = ruleDS_indices.get(lastIndex-2);
			
				
				ruleDS_indices.remove(lastIndex-1);
				ruleDS_indices.remove(lastIndex-2);
				
				ruleDS_indices.add(convertAndOperator(leftEntryValue, rightEntryValue));
			}

		}

	}
	
	
	
	 protected int convertLinkedReactionsVariables(RegulatoryVariable var) throws LinearProgrammingTermAlreadyPresentException, NonExistentIdException, Exception{
			
		  
			int ret = -3;

			
			if(var.getVariableSign().equals(VariableSignValue.LESS)){
				 // Here I translate the meaning of constarains from the GLPK equations to constrains in the meaing of the 
				 //regulatory rules. 
	             // This means that if the rule asked if (flux < val), the real question is if (flux > -val).
	             // Two equations that are added (view document 'How to build the MIP problem.doc'):
	             //       1.	c * (const - Vmax) + v <= const
	             //       2.	Vmin <= c * (Vmin - const) + v
				
				//String[] data = var.split("<");
                String name=var.getSimpleName();
				
                ReactionConstraint constrainz=getReactionsConstraint(name);
                
				double vMax = constrainz.getUpperLimit(); 
				double vMin = constrainz.getLowerLimit(); 

				double c = Double.parseDouble(var.getSignValue()); 
				double compareVal = -c + epsilon;
				
		
				if(compareVal < vMin){
					ret = -1;
				}else if(compareVal >= vMax){
					ret = -2;	
				}

				else{
					//Add one more variable 'c' of the equations

					int lastvaridx = problem.getNumberVariables();
					String cvar = CVAR+lastvaridx;
					putVarMappings(cvar, lastvaridx);
					addBooleanVarToMILP(cvar);
					
					int reactionindex= ((IIntegratedStedystateModel) model).getReactionIndex(name);
					
					int[] colIdx = {lastvaridx, reactionindex};
					double [] colVal1={(compareVal-vMax), 1};
					
					addEquationToMILP(colIdx, colVal1, -INF, compareVal);
					
					
					double [] colVal2={(vMin-compareVal), 1};
					
					addEquationToMILP(colIdx, colVal2, vMin, INF);
					
					
					ret = lastvaridx;
					
				}
				
			}else if (var.getVariableSign().equals(VariableSignValue.GREATER)){
				//This means that if the rule asked if (flux > val), the real question is if (flux < -val).
	            //Two equations that are added (view document 'How to build the MIP problem.doc'):
	           //       1.	c * (Vmax - const) + v <= Vmax
	           //       2.	const <= c * (const - Vmin) + v
				
				
                String name=var.getSimpleName();
				
                ReactionConstraint constrainz=getReactionsConstraint(name);
                
				double vMax = constrainz.getUpperLimit();
				double vMin = constrainz.getLowerLimit();
				

				double c = Double.parseDouble(var.getSignValue());
				double compareVal = -c-epsilon;
					
				if(compareVal<=vMin)
						ret = -2;
				else if(vMax<compareVal)
						ret = -1;
				
				else{
					    
					    int lastvaridx = problem.getNumberVariables();
						String cvar = CVAR+lastvaridx;
						putVarMappings(cvar, lastvaridx);
						addBooleanVarToMILP(cvar);
						
						int reactionindex= ((IIntegratedStedystateModel) model).getReactionIndex(name);
						
						int[] colIdx = {lastvaridx, reactionindex};
						double [] colVal1={(vMax-compareVal), 1};
						
						addEquationToMILP(colIdx, colVal1, -INF, vMax);
						
						
						double [] colVal2={(compareVal-vMin), 1};
						
						addEquationToMILP(colIdx, colVal2, compareVal, INF);
						
					
						
						ret = lastvaridx;
				}
			}
			
			return ret; 
		}
	
	
	protected int convertNotOperator(int entryNum) throws Exception{
		int ret = -3;
		
		if(entryNum == -2)
			ret = -1;
		else if(entryNum==-1)
			ret=-2;
		else{
			int numvar = getCorrentNumOfVar();
			String notvar = BOOLNOT+numvar;
			putVarMappings(notvar, numvar);
			addBooleanVarToMILP(notvar);
			
			// a = not (b) is converted to  (b + a = 1)
			int[] colIdxs = {entryNum,numvar};
			double[] colVals = {1,1};
			addEquationToMILP(colIdxs, colVals, 1, 1);
			
			ret = numvar;
		}
		return ret;
	}
	
	
	protected int convertOrOperator(int leftEntryValue, int rightEntryValue) throws Exception{
		int ret = -3;
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("convertOrOperator ==> " +leftEntryValue + " OR " + rightEntryValue);
		//System.out.println("convertOrOperator ==> " +leftEntryValue + " OR " + rightEntryValue);
		if(rightEntryValue==-1 || leftEntryValue==-1)
			ret = -1;
		else if(rightEntryValue==-2 && leftEntryValue==-2)
			ret = -2;
		else{
			if(leftEntryValue == -2)
				ret = rightEntryValue;
			else if(rightEntryValue == -2)
				ret = leftEntryValue;
			else{
				//a = (b or c) is converted to (-2 <= 2*b + 2*c - 4*a <= 1)
				
				int numvar = getCorrentNumOfVar();
				String orvar = BOOLOR+numvar;
				putVarMappings(orvar, numvar);
				addBooleanVarToMILP(orvar);
				
				if(leftEntryValue==rightEntryValue){
					int[] colIdxs = {leftEntryValue, numvar};
					double[] colVals = {1, -1};
					
					addEquationToMILP(colIdxs, colVals, 0, 0);
				}
				/*if(leftEntryValue==rightEntryValue){
					int[] colIdxs = {leftEntryValue, numvar};
					double[] colVals = {2, -4};
					
					addEquationToMILP(colIdxs, colVals, -2, 1);
				}*/
				else{
					int[] colIdxs = {leftEntryValue,rightEntryValue, numvar};
					double[] colVals = {2,2,-4};
					addEquationToMILP(colIdxs, colVals, -2, 1);
				}

				ret = numvar;
			}
				
		}
		return ret;
	}
	
	
	protected int convertAndOperator(int leftEntryValue, int rightEntryValue) throws LinearProgrammingTermAlreadyPresentException, Exception{
		int ret = -3;
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("convertAndOperator ==> " +leftEntryValue + " AND " + rightEntryValue);
		//System.out.println("===>>> " +leftEntryValue + " AND " + rightEntryValue);
		if(rightEntryValue==-2 || leftEntryValue==-2)
			ret = -2;
		else if(rightEntryValue==-1 && leftEntryValue==-1)
			ret = -1;
		else{
			if(leftEntryValue == -1)
				ret = rightEntryValue;
			else if(rightEntryValue == -1)
				ret = leftEntryValue;
			else{
				//a = (b and c) is converted to (-1 <= 2*b + 2*c + 4*a <= 3)
				
				int numvar = getCorrentNumOfVar();
				String andvar = BOOLAND+numvar;
				putVarMappings(andvar, numvar);
				addBooleanVarToMILP(andvar);
				
				/*if(leftEntryValue==rightEntryValue){
					int[] colIdxs = {leftEntryValue, numvar};
					double[] colVals = {2, -4};
					
					addEquationToMILP(colIdxs, colVals, -1, 3);
				}*/
				if(leftEntryValue==rightEntryValue){
					int[] colIdxs = {leftEntryValue, numvar};
					double[] colVals = {1, -1};
					
					addEquationToMILP(colIdxs, colVals, 0, 0);
				}
				else{
					int[] colIdxs = {leftEntryValue,rightEntryValue, numvar};
					double[] colVals = {2,2,-4};
					addEquationToMILP(colIdxs, colVals, -1, 3);
				}
				
				ret = numvar;
			}
				
		}
		return ret;
	}
	
	
	protected ReactionConstraint getReactionsConstraint(String reactionId) throws WrongFormulationException, MandatoryPropertyException, PropertyCastException{
		IOverrideReactionBounds overrideRC=createModelOverride();
		return overrideRC.getReactionConstraint(reactionId);
	}
	
	
	
	
	
	private Integer getCorrentNumOfVar() {
		return problem.getNumberVariables();
	}
	
	  @Override
	public boolean checkIfMandatoryPropertiesSatisfied(Map<String, Object> properties) {
			
			for (String mprop : mandatoryProperties) {
				if(!properties.containsKey(mprop))
					return false;
			}
			
			return true;
		}
	



}
