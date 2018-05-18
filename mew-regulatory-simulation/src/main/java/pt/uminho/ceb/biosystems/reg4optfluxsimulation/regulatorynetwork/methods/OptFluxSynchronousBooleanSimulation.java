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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pt.ornrocha.collections.MTUMapUtils;
import pt.ornrocha.logutils.MTULogUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.Environment;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.BooleanValue;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IntegratedSteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.mapper.OverrideRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.mapper.RegulatoryModelMapper;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.InvalidRegulatoryModelException;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.OptFluxRegulatoryNetworkMemory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.containers.SynchronousSimulationResults;

public class OptFluxSynchronousBooleanSimulation extends AbstractRegulatoryNetworkSimulationMethod{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int iterations = 100;
	protected OptFluxRegulatoryNetworkMemory memory = null;
	protected ArrayList<String> ordereridentifiers;
	protected ArrayList<Boolean> initialstate;
	protected Set<String> knockoutGenes;
	protected Set<String> knockoutgeneproduct;
	
	protected OverrideRegulatoryModel overrideModel;
	protected RegulatoryModelMapper modelMapper;
	
	protected IndexedHashMap<String, String> geneid2ruleid=null;
	protected IndexedHashMap<String, String> ruleid2geneid=null;

	protected IndexedHashMap<String, Boolean> regulatoryvariablesbooleanstate= null;
	private boolean TFsAssumeTrueStateIfNotDefinedByUser=true;
	

   
	public OptFluxSynchronousBooleanSimulation(IRegulatoryNetwork model) throws InvalidRegulatoryModelException {
		super(model);
		MTULogUtils.addDebugMsgToClass(this.getClass(), "\n\n\n\n#################### Regulatory Simulation BRNV ##################################");
	}

	
	@Override
	public boolean simulationMethodSupportsRegulatoryNetworkType(IRegulatoryNetwork model) {
		if(model instanceof IOptfluxRegulatoryModel)
			return true;
		return false;
	}
	
	
	
	
    @Override
	public IRegulatoryModelSimulationResult simulate() throws Exception{
    	run();
	
        return new SynchronousSimulationResults(model, getInitialRegulatoryState(), memory);
    	//return new BRNSimulationResults(model, memory.getCycle(), getgeneIndex(), getRegulatoryVariablesContainer(),getRegulatoryGenesInfoContainer(),trueGenes,falseGenes,undefinedGenes);

	}
	
	
	@SuppressWarnings("unchecked")
	protected void run(){
		
		try {
			
			LogMessageCenter.getLogger().toClass(getClass()).addDebugSeparator("Input Parameters for Regulatory Simulation");
			
			Integer definediters=getMaxIterations();
			if(definediters!=null)
				iterations=definediters;

			InitialRegulatoryState initstate=getInitialRegulatoryState();
			//System.out.println("class OptFluxSynchronousBooleanSimulation:"+initstate);
			this.ordereridentifiers=initstate.getOrderedIdentifiers();
			this.initialstate=initstate.getInitialBooleanStates();
			this.knockoutGenes=initstate.getKnockoutgenes();
			this.geneid2ruleid=model.getMapGeneId2RuleId();
			if(knockoutGenes!=null)
				this.knockoutgeneproduct=IntegratedSteadyStateModel.convertRegulatorIdToRegulatorProductID(knockoutGenes, geneid2ruleid);
			//System.out.println("##########################: "+knockoutgeneproduct);
			
			
			
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Ordered Identifiers: ", ordereridentifiers);
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Initial State: ", initialstate);
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("knockout Genes: ", knockoutGenes);
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Map gene id to rule id", geneid2ruleid);
	
			this.ruleid2geneid=(IndexedHashMap<String, String>) MTUMapUtils.invertMap(geneid2ruleid);
			
			LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator(null);
			
			createModelOverride(knockoutGenes);
			ArrayList<Boolean> firststate=calcFirstStep();
			
			
			int checkCycle = -1;
			memory = new OptFluxRegulatoryNetworkMemory(iterations,firststate,model.getRegulatorIDs());
			
			ArrayList<Boolean> geneExp = memory.lookTop();
           
			Environment<IValue> environment = createEnvironment(geneExp);
        
		  boolean stop=false;
		  int totaliter=0;
		  for (int i = 0; i < iterations && !stop; i++) {

				addExpressionInIEnvironment(environment, geneExp);
				geneExp = oneIteration(environment);
				memory.addState(geneExp);
				checkCycle = memory.checkAttractor();
				if(isStopFirstAttractor() && checkCycle!=-1)
					stop=true;
				
				totaliter++;
			 }
			//this.lenghtCycle = checkCycle;
		  
		  LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Number Regulatory iterations performed: "+totaliter);

		} catch (PropertyCastException e) {
			e.printStackTrace();
		} catch (MandatoryPropertyException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//MTULogUtils.addDebugMsgToClass(this.getClass(), "State Of all variables Before starting simulation: {}", regulatoryvariablesbooleanstate);
		
		
		
		//MTULogUtils.addDebugMsgToClass(this.getClass(), "Attractors Results:\n {}", getAttractorsStatePrint());
	}
	
	
	protected void createModelOverride(Set<String> geneknockouts) throws PropertyCastException, MandatoryPropertyException{
		overrideModel = new OverrideRegulatoryModel((IOptfluxRegulatoryModel) model, geneknockouts);
		modelMapper = new RegulatoryModelMapper((IOptfluxRegulatoryModel) model, overrideModel,null);
		this.model = modelMapper;
		
	}
	
	
	
	
	private ArrayList<Boolean> calcFirstStep() throws PropertyCastException, MandatoryPropertyException{
		
		 ArrayList<Boolean> firstStepIteration = new ArrayList<Boolean>();

		
		for (int i = 0; i < model.getNumberOfRegulators(); i++) {
			String geneid=model.getRegulatorIdAtIndex(i);
			
			if(knockoutGenes!=null && knockoutGenes.contains(geneid))
				firstStepIteration.add(false);
			else{
				int pos=ordereridentifiers.indexOf(geneid);
				boolean state=initialstate.get(pos);
				firstStepIteration.add(state);
			}
		}
		
		MTULogUtils.addDebugMsgToClass(this.getClass(), "First Iteration step: {}",firstStepIteration);
		return firstStepIteration;
	}
	

	
	public Environment<IValue> createEnvironment(ArrayList<Boolean> geneExpression) {
		
		 HashSet<String> idsinenvironment=new HashSet<>();
		 Environment<IValue> environment = new Environment<IValue>();
		 IndexedHashMap<String, RegulatoryRule> rules=((IOptfluxRegulatoryModel) model).getRegulatoryRules();
		 
		 for (int i = 0; i < rules.size(); i++) {
			 String geneid=rules.getKeyAt(i);
			 HashSet<String> rulevars=null;
			 RegulatoryRule rule=rules.get(geneid);
			 // System.out.println("rule: "+rule.getRule());
			 if(rule.getBooleanRule()!=null)
				 rulevars=new HashSet<>(rule.getVariables());
			
			 if(rulevars!=null){
				 for (String id : rulevars) {
					if(!idsinenvironment.contains(id)){
						boolean inputstate=false;
						boolean isreactionid=false;
						int index=-1;
		                 
						index=ordereridentifiers.indexOf(id);
						//System.out.println("Input id: "+id);
						String useid=null;
						if(index==-1){
							if(geneid2ruleid.containsKey(id))
								useid=geneid2ruleid.get(id);
								
							else if(ruleid2geneid.containsKey(id))
								useid=ruleid2geneid.get(id);
							
							else{
								RegulatoryVariable tmpid=RegulatoryVariable.setupVariable(id, null);
								useid=tmpid.getId();
								isreactionid=true;
							}
						}
						
						
						if(useid!=null && ordereridentifiers.contains(useid)){
							index=ordereridentifiers.indexOf(useid);
							if(!isreactionid)
								id=useid;
							//System.out.println("Use Id: "+useid+" index: "+index);
						}
						
						if(index!=-1){
							inputstate=initialstate.get(index);
							environment.associate(id, new BooleanValue(inputstate));
							idsinenvironment.add(id);
						}
					}
				 }
			 }
			 
		}

		addExpressionInIEnvironment(environment, geneExpression);
	
		return environment;
	}
	
	
	private void addExpressionInIEnvironment(Environment<IValue> environment, ArrayList<Boolean> exp) {
      
		for (int i = 0; i < model.getNumberOfRegulatoryRules(); i++){
			String assocID=((IOptfluxRegulatoryModel)model).getRegulatoryRule(i).getRuleId();
			BooleanValue boolvalue=new BooleanValue(exp.get(i));
			if(knockoutgeneproduct!=null && knockoutgeneproduct.contains(assocID))
				boolvalue=new BooleanValue(false);
			
			environment.associate(assocID, boolvalue);
		}
	}
	
	

	
	private ArrayList<Boolean> oneIteration(Environment<IValue> environment) {
		ArrayList<Boolean> antIteration = memory.lookTop();
		ArrayList<Boolean> iteration = new ArrayList<Boolean>();

		
		for (int i = 0; i < model.getNumberOfRegulatoryRules(); i++) {
			RegulatoryRule rule = ((IOptfluxRegulatoryModel)model).getRegulatoryRule(i);
			if (rule.getBooleanRule().getRootNode() != null) {
				boolean value = rule.getBooleanRule().evaluate(environment).getBooleanValue();
				iteration.add(value);

			} else{
				iteration.add(antIteration.get(i));
			}
		}

		return iteration;
	}



}
