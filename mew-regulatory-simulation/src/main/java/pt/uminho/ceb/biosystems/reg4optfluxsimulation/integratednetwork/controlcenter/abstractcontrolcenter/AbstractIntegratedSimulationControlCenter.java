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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import pt.ornrocha.logutils.MTULogUtils;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.AbstractSimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.factory.IntegratedMethodsFactory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;

public abstract class AbstractIntegratedSimulationControlCenter extends AbstractSimulationSteadyStateControlCenter implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The regulatory geneticconditions. */
	protected RegulatoryGeneticConditions regulatoryGeneticconditions;
    
    /** The IntegratedMethodsFactory factory. */
    protected static IntegratedMethodsFactory factory;
    
    /** The metabolic simulation method. */
    protected String metaboliSimulationMethod;
    
    protected RegulatorySimulationMethod regulatoryNetworkSimulationMethod;
    
	
	static{
		LinkedHashMap<String, Class<?>> mapMethods = new LinkedHashMap<String,Class<?>>();
		factory = new IntegratedMethodsFactory(mapMethods);
	}
	
	
	protected abstract LinkedHashMap<String, Class<?>> getMethodsSupportedByControlCenter();

//	FIXME: JAVADOC
//	/**
//	 * Instantiates a new integrated simulation control center.
//	 *
//	 * @param environmentalConditions the environmental conditions
//	 * @param geneticConditions the genetic conditions
//	 * @param model the model
//	 * @param methodType the method type
//	 * @param metabolimethodtype the metabolimethodtype
//	 * @param variables the variables
//	 * @param falsenodes the falsenodes
//	 */
	
	
	public AbstractIntegratedSimulationControlCenter(
			ISteadyStateModel model,
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions, 
			String methodType,
			boolean isMaximization,
			String solver 
			) {
		super(environmentalConditions, geneticConditions, model, methodType);
		setMaximization(isMaximization);
		setSolver(solver);
		checkRegisteredMethods();
		setGeneticConditions(geneticConditions);

	}
	

	private void checkRegisteredMethods(){
		Set<String> registeredmethods=factory.getRegisteredMethods();
		
		LinkedHashMap<String, Class<?>> newmethods=getMethodsSupportedByControlCenter();
		for (String methodid : newmethods.keySet()) {
			if(!registeredmethods.contains(methodid))
				getFactory().registerMethod(methodid, newmethods.get(methodid));
		}
	}
	
	
	
	
	@Override
	protected IntegratedMethodsFactory getFactory() {
		return factory;
	}


	public void addUnderOverRef() throws Exception {
		// TODO Auto-generated method stub	
	}
	
	
	public void setSolver(String solverType){
		addProperty(RegulatorySimulationProperties.SOLVER, solverType);	
	}
	
	
	
	
	/**
	 * Checks if is maximization.
	 *
	 * @return the boolean
	 */
	public Boolean isMaximization() {
		return (Boolean) (getProperty(RegulatorySimulationProperties.IS_MAXIMIZATION));
	}

	/**
	 * Sets the maximization.
	 *
	 * @param isMaximization the new maximization
	 */
	public void setMaximization(boolean isMaximization) {
		addProperty(RegulatorySimulationProperties.IS_MAXIMIZATION, isMaximization);
	}

	
	/**
	 * Sets the variables.
	 *
	 * @param variables the new variables
	 */
	public void setvariables(IntegratedNetworkInitialStateContainer variables){
		addProperty(RegulatorySimulationProperties.VARIABLES_CONTAINER, variables);
	}
	
	/**
	 * Gets the variables container.
	 *
	 * @return the variables container
	 */
	public IntegratedNetworkInitialStateContainer getVariablesContainer(){
		return (IntegratedNetworkInitialStateContainer)getProperty(RegulatorySimulationProperties.VARIABLES_CONTAINER);
	}
	
	
	
	public void setObjectiveFunction(Map<String, Double> obj_fun){
		addProperty(RegulatorySimulationProperties.OBJECTIVE_FUNCTION, obj_fun);
	}

	
	public void forceIntializationTranscriptFactorsAsTrueState(boolean force) {
		addProperty(RegulatorySimulationProperties.FORCEINITIALIZATIONTFSTRUESTATE, force);
	}
	
	
	static public void registMethod(String methodId, Class<?> klass) throws Exception{
		factory.addSimulationMethod(methodId, klass);
	}
	

	public static Set<String> getRegisteredMethods(){
		return factory.getRegisteredMethods();
	}
	
	
	public static void registerMethod(String id, Class<?> method) {
		factory.registerMethod(id, method);
		
	}
	

	
	
	@Override
	public void setGeneticConditions(GeneticConditions geneticConditions) {
		/*if(geneticConditions!=null)
			System.out.println(getClass().getSimpleName()+" GENE CONDITIONS: "+((RegulatoryGeneticConditions)geneticConditions).getALLGeneKnockoutList());*/
		if (geneticConditions == null) {
			removeProperty(SimulationProperties.GENETIC_CONDITIONS);
			removeProperty(RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS);
			removeProperty(SimulationProperties.IS_OVERUNDER_SIMULATION);
			removeProperty(SimulationProperties.OVERUNDER_REFERENCE_FLUXES);
		} else {
			if (geneticConditions.getReactionList().allKnockouts())
				addProperty(SimulationProperties.IS_OVERUNDER_SIMULATION, false);
			else
				addProperty(SimulationProperties.IS_OVERUNDER_SIMULATION, true);
				
			addProperty(SimulationProperties.GENETIC_CONDITIONS, geneticConditions);
			addProperty(RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS, geneticConditions);
		}
	}

	
	@Override
	public GeneticConditions getGeneticConditions()
	{   return (RegulatoryGeneticConditions)getProperty(RegulatorySimulationProperties.REGULATORY_GENETIC_CONDITIONS);		
	}
	
	
	public void stopProcess() {
		addProperty(RegulatorySimulationProperties.STOPCURRENTPROCESS, true);
	}
	
 
    @Override
	public  SteadyStateSimulationResult simulate() throws Exception {
		if(isUnderOverSimulation() && getUnderOverRef() == null){
			addUnderOverRef();
		}
		
		this.lastMethod = getFactory().getMethod(this.methodType, methodProperties, model);
		
		return lastMethod.simulate();
		
	}
    
    public void changeProperty(String key, Object value) throws Exception {
    	if(methodProperties.containsKey(key)){
    		methodProperties.put(key, value);
    	}
    	else
    		throw new Exception("Invalid Property Key Exception");
    	
    }
    
    
    
    public static RegulatoryGeneticConditions getRegulatoryGeneticConditionsFromGeneList(IIntegratedStedystateModel model, ArrayList<String> genelist) throws Exception{
    	
    	 GeneChangesList metabolicgenes =null;
		 GeneregulatorychangesList regulatorygenes =null;
		 
		 ArrayList<String> metabolicgenelist=new ArrayList<>();
		 ArrayList<String> regulatorygenelist=new ArrayList<>();
		 
		 
		 for (int i = 0; i < genelist.size(); i++) {
			
			 String geneid=genelist.get(i);
		  
			 boolean ismetabolic=model.isMetabolicGene(geneid);
			 boolean isregulatory=model.isRegulatoryGene(geneid);
	
			 
			 if(ismetabolic){
                metabolicgenelist.add(geneid);
			 }
			 else if(isregulatory && !ismetabolic)
				 regulatorygenelist.add(geneid);

		 }
		 
		 
		 if(metabolicgenelist.size()>0)
			 metabolicgenes=new GeneChangesList(metabolicgenelist);
		 else
			 metabolicgenes=new GeneChangesList();
		 
		 if(regulatorygenelist.size()>0)
			 regulatorygenes=new GeneregulatorychangesList(regulatorygenelist);
		 else
			 regulatorygenes=new GeneregulatorychangesList();
		 
		 
		 return new RegulatoryGeneticConditions(regulatorygenes, metabolicgenes, model, false);
    	
    }
    
    public static RegulatoryGeneticConditions getRegulatoryGeneticConditionsForGeneID(IIntegratedStedystateModel model, String geneid) throws Exception{
    	ArrayList<String> genelist=new ArrayList<>();
    	genelist.add(geneid);
    	
    	return getRegulatoryGeneticConditionsFromGeneList(model, genelist);
    }
    
    public static IndexedHashMap<String, RegulatoryGeneticConditions> getRegulatoryGeneticConditionsForEachGeneInList(IIntegratedStedystateModel model, ArrayList<String> genelist) throws Exception{
    	IndexedHashMap<String, RegulatoryGeneticConditions> res=null;
    	
    	if(genelist!=null){
    		
    		res=new IndexedHashMap<>();
    		
    		for (int i = 0; i < genelist.size(); i++) {
                boolean valid=true;
                
				 String geneid=genelist.get(i);
			
				 GeneChangesList metabolicgenes =null;
				 GeneregulatorychangesList regulatorygenes =null;
				 
				 boolean ismetabolic=model.isMetabolicGene(geneid);
				 boolean isregulatory=model.isRegulatoryGene(geneid);
				 
				 //System.out.println("Gene: "+geneid+" Is Metabolic: "+ismetabolic+" Is Regulatory: "+isregulatory);
				 
				 if(ismetabolic && !isregulatory){
					 ArrayList<String> metabgenes =new ArrayList<>();
					 metabgenes.add(geneid);
					 metabolicgenes=new GeneChangesList(metabgenes);
					 regulatorygenes= new GeneregulatorychangesList();
			
				  }
				 else if(!ismetabolic && isregulatory){
					 ArrayList<String> reggenes =new ArrayList<>();
					 reggenes.add(geneid);
					 metabolicgenes=new GeneChangesList();
					 regulatorygenes=new GeneregulatorychangesList(reggenes);

				 }
				 else{
					 valid=false;
					// metabolicgenes=new GeneChangesList();
					// regulatorygenes=new GeneregulatorychangesList(); 
				 }
				 
                if(valid){
			        RegulatoryGeneticConditions regulatoryGeneticConditions = new RegulatoryGeneticConditions(regulatorygenes, metabolicgenes, model, false);
			        MTULogUtils.addInfoMsgToClass(AbstractIntegratedSimulationControlCenter.class, "Added Gene knockout {} is metabolic: {} isregulatory: {}", geneid,ismetabolic,isregulatory);
			        res.put(geneid, regulatoryGeneticConditions);
                }

    	    }
    	}

    	return res;
    }

	
	
	
	
	
	
	
	
	
	/*public AbstractIntegratedSimulationControlCenter(
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions, 
			ISteadyStateModel model,
			String methodType,
			String metabolimethodtype,
			String regulatorysimmethodtype,
			IntegratedNetworkVariablesContainer variables,
			boolean isMaximization,
			SolverType solver 
			) {
		super(environmentalConditions, geneticConditions, model, methodType);

		setvariables(variables);
		setMetabolicMethodType(metabolimethodtype);
		setRegulatoryNetworkSimulationMethodType(regulatorysimmethodtype);
		setFalseValuesInitStep(falsenodes);
		
		
		//isOverUnderSimulation(false); // to be changed
		
		//setObjectiveFunction(obj_coef);
		setMaximization(isMaximization);
		setSolver(solver);
		setGeneticConditions(geneticConditions);
	}*/
	
	
	/*public AbstractIntegratedSimulationControlCenter(
			ISteadyStateModel model,
			EnvironmentalConditions environmentalConditions,
			RegulatoryGeneticConditions geneticConditions, 
			String methodType,
			IntegratedSimulationPropertiesContainer properties){
		super(environmentalConditions, geneticConditions, model, methodType);
		setGeneticConditions(geneticConditions);
		try {
			applySimulationSettings(properties);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
	
	
	/*public void applySimulationSettings(IntegratedSimulationPropertiesContainer properties) throws Exception{
		
		setSolver((SolverType) properties.getPropertyNotNull(IntegratedSimulationPropertiesContainer.SOLVERTYPE));
		setObjectiveFunction((Map<String, Double>) properties.getPropertyNotNull(IntegratedSimulationPropertiesContainer.OBJECTIVEFUNCTION));
		setMaximization((boolean) properties.getProperty(IntegratedSimulationPropertiesContainer.ISMAXIMIZATION, true));
		
		if(!methodType.equals(IntegratedSimulationMethods.DYNAMICRFBA)){
		
		    if(methodType.equals(IntegratedSimulationMethods.INTEGRATED_BRN)){
		       setMetabolicMethodType((String) properties.getProperty(IntegratedSimulationPropertiesContainer.METABOLICSIMULATIONMETHOD, SimulationProperties.FBA));
		       setRegulatoryNetworkSimulationMethodType((String) properties.getProperty(IntegratedSimulationPropertiesContainer.REGULATORYSIMULATIONMETHOD, RegulatoryNetworkSimulationMethods.OPTFLUXSYNCHRONOUSBOOLEANSIMULATION));
		     }
		
		
		     setFalseValuesInitStep((HashSet<String>) properties.getProperty(IntegratedSimulationPropertiesContainer.INITIALOFFGENES));
		     if(properties.containsProperty(IntegratedSimulationPropertiesContainer.INDEPENDENTTRANSFACTORSINITIALSTATE))
		        setIndependentTransFactorsAssumeInitialState((Boolean) properties.getProperty(IntegratedSimulationPropertiesContainer.INDEPENDENTTRANSFACTORSINITIALSTATE));
		     changeSettingsOfIntegratedVariablesContainer(properties);
		
		}
		
		if(methodType.equals(IntegratedSimulationMethods.DYNAMICRFBA)){
			addProperty(RegulatorySimulationProperties.INITIALBIOMASS, properties.getPropertyNotNull(RegulatorySimulationProperties.INITIALBIOMASS));
			addProperty(RegulatorySimulationProperties.TIMESTEP, properties.getPropertyNotNull(RegulatorySimulationProperties.TIMESTEP));
			addProperty(RegulatorySimulationProperties.NUMBERSTEPS, properties.getPropertyNotNull(RegulatorySimulationProperties.NUMBERSTEPS));
			addProperty(RegulatorySimulationProperties.SUBSTRATES, properties.getPropertyNotNull(RegulatorySimulationProperties.SUBSTRATES));
			HashSet<String> geneswithinittruestate=(HashSet<String>) properties.getProperty(RegulatorySimulationProperties.GENESSTARTINGWITHTRUESTATE);
			if(geneswithinittruestate!=null)
				addProperty(RegulatorySimulationProperties.GENESSTARTINGWITHTRUESTATE, geneswithinittruestate);
			ArrayList<String> excludereactions=(ArrayList<String>) properties.getProperty(RegulatorySimulationProperties.EXCLUDEUPTAKEREACTIONS);
			if(excludereactions!=null)
				addProperty(RegulatorySimulationProperties.EXCLUDEUPTAKEREACTIONS, excludereactions);
			
			IndexedHashMap<String, Boolean> initialvarstate=(IndexedHashMap<String, Boolean>) properties.getProperty(RegulatorySimulationProperties.RFBAINITIALVARIBLESSTATE);
			if(initialvarstate!=null)
				addProperty(RegulatorySimulationProperties.RFBAINITIALVARIBLESSTATE, initialvarstate);
		}
	}
	
	protected void changeSettingsOfIntegratedVariablesContainer(IntegratedSimulationPropertiesContainer properties){
		if(model instanceof IIntegratedStedystateModel){
			
			IntegratedNetworkVariablesContainer copyoforiginalcont=((IIntegratedStedystateModel)model).getIntegratedVariablesContainerWihoutValidation().copy();
			
			ArrayList<String> changevarstotruestate=(ArrayList<String>) properties.getProperty(IntegratedSimulationPropertiesContainer.VARIABLEWITHTRUESTATE);
			copyoforiginalcont.setVariablesssToActive(changevarstotruestate);
			
			ArrayList<String> changetfstotruestate=(ArrayList<String>) properties.getProperty(IntegratedSimulationPropertiesContainer.TRUESTATETFS);
			copyoforiginalcont.setVariablesssToActive(changetfstotruestate);
			
			ArrayList<String> changevarstofalsestate=(ArrayList<String>) properties.getProperty(IntegratedSimulationPropertiesContainer.VARIABLEWITHFALSESTATE);
			copyoforiginalcont.setVariablesToNOTActiveState(changevarstofalsestate);
			
			ArrayList<String> changetfstofalsestate=(ArrayList<String>) properties.getProperty(IntegratedSimulationPropertiesContainer.FALSESTATETFS);
			copyoforiginalcont.setVariablesToNOTActiveState(changetfstofalsestate);
			
			setvariables(copyoforiginalcont);
			System.out.println(copyoforiginalcont.getALLVariablesWithTrueState());
			//System.out.println(copyoforiginalcont.getVariablesContainerInfoLog());
		}
		
	}*/

	
	
	
	
/*
    public void mandatoryAttractorKnockoutAgreement(boolean use){
    	addProperty(RegulatorySimulationProperties.FORCEATTRACTORMUSTHAVESSAMEGENEKNOCKOUTS, use);
    }
    
    public void setIndependentTransFactorsAssumeInitialState(boolean state){
    	if(model instanceof DynamicIntegratedSteadyStateModel)
    		((DynamicIntegratedSteadyStateModel)model).setIndependentTransFactorsInitialState(state);
    	addProperty(RegulatorySimulationProperties.INDEPENDENTTRANSFACTORSSTATE, state);
    }*/

	

	

}
