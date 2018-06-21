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
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;

import pt.ornrocha.collections.MTUMapUtils;
import pt.uminho.ceb.biosystems.mew.core.model.components.GeneReactionRule;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.MandatoryPropertyException;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.PropertyCastException;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTree;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.Environment;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.TreeUtils;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.BooleanValue;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.DataTypeEnum;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IntegratedSteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.InitialRegulatoryState;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.mapper.OverrideRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.methods.components.OptFluxRegulatoryNetworkMemory;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.IRegulatoryModelSimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.results.containers.SynchronousSimulationResults;

public class ConditionalIntegratedNetworkSimulation extends AbstractIntegratedNetworkSimulationMethod{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> unconstrainedgenesknockouts;
	protected Set<String> knockoutGenes;
	protected Set<String> knockoutgeneproduct;
	private InitialRegulatoryState initregulatorystate;
	private IntegratedNetworkInitialStateContainer integratedvariables;
	protected OptFluxRegulatoryNetworkMemory memory = null;
	private HashMap<String, GeneReactionRule> metabolicreactionsinfluencedbyregulatorymodel;
	private HashedMap<String, String> metabolicgenesassociatedreactions;
	private ArrayList<String> sortedmemoryelems;
	private ArrayList<String> sortedmemoryelemsruleslink;
	protected IndexedHashMap<String, String> geneid2ruleid=null;
	protected IndexedHashMap<String, String> ruleid2geneid=null;
	protected int iterations = 100;
	private IRegulatoryNetwork regulatorymodel;

	public ConditionalIntegratedNetworkSimulation(IIntegratedStedystateModel model, IntegratedNetworkInitialStateContainer integratedvariables,ArrayList<String> unconstrainedgenesknockouts) {
		super(model);
		this.integratedvariables=integratedvariables;
		initregulatorystate=this.integratedvariables.getInitialRegulatoryState();
		this.unconstrainedgenesknockouts=unconstrainedgenesknockouts;
	}
	
	public void setUnconstrainedgeneKnockouts(ArrayList<String> genesknockouts){
		this.unconstrainedgenesknockouts=genesknockouts;
	}
	
	public void setInitialRegulatoryState(InitialRegulatoryState initstate){
		this.initregulatorystate=initstate;
	}
	
	/*public void setEnvironmentalConditions(EnvironmentalConditions envconds){
		this.envconds=envconds;
	}*/
	
	public void setIntegratedNetworkInitialStateContainer(IntegratedNetworkInitialStateContainer integratedvariables){
		this.integratedvariables=integratedvariables;
	}

	@Override
	public IRegulatoryModelSimulationResult simulate() throws Exception {
		run();
		return new SynchronousSimulationResults(model.getRegulatoryNetwork(), initregulatorystate, memory);
	}
	
	
	
	protected void run() throws Exception{
		
		//System.out.println("Conditional Regulatory Simulation");
		this.knockoutGenes=initregulatorystate.getKnockoutgenes();
		this.geneid2ruleid=model.getRegulatoryMapGeneId2RuleId();
		if(knockoutGenes!=null)
			this.knockoutgeneproduct=IntegratedSteadyStateModel.convertRegulatorIdToRegulatorProductID(knockoutGenes,geneid2ruleid);
		this.ruleid2geneid=(IndexedHashMap<String, String>) MTUMapUtils.invertMap(geneid2ruleid);
		
		createModelOverride(knockoutGenes);
		metabolicreactionsinfluencedbyregulatorymodel=getMetabolicReactionsInfluencedByRegulatoryModel();
		initializeSynchronousMemory();
		
		ArrayList<Boolean> regulatorystate = memory.lookTop();
		Environment<IValue> environment = createEnvironment(regulatorystate);
		
		int checkCycle = -1;
		boolean stop=false;
		for (int i = 0; i < iterations && !stop; i++) {
		   
				addExpressionInIEnvironment(environment, regulatorystate);
				regulatorystate = oneIteration(environment);
				memory.addState(regulatorystate);
				checkCycle = memory.checkAttractor();
				if(isStopFirstAttractor() && checkCycle!=-1)
					stop=true;
			
		}
		
	}
	
	
	protected void createModelOverride(Set<String> geneknockouts) throws PropertyCastException, MandatoryPropertyException{
		this.regulatorymodel= new OverrideRegulatoryModel((IOptfluxRegulatoryModel) model.getRegulatoryNetwork(), geneknockouts);

		
	}

	
	protected HashMap<String, GeneReactionRule> getMetabolicReactionsInfluencedByRegulatoryModel(){
		HashMap<String, GeneReactionRule> res=new HashMap<>();
		ArrayList<String> regulatorymodelgenes=model.getRegulatoryNetwork().getRegulatorIDs();
		IndexedHashMap<String, RegulatoryVariable> regulatoryvariables=model.getRegulatoryNetwork().getVariablesInRegulatoryNetwork();
		
		for (int i = 0; i < regulatorymodelgenes.size(); i++) {
			String geneid=regulatorymodelgenes.get(i);
			if(model.isMetabolicGene(geneid)){
				ArrayList<String> inflreactions=model.getReactionsInfluencedByGene(geneid);
				if(inflreactions!=null && inflreactions.size()>0){
					for (int j = 0; j < regulatoryvariables.size(); j++) {
						RegulatoryVariable var=regulatoryvariables.getValueAt(j);
						if(inflreactions.contains(var.getSimpleName()) && !res.containsKey(var.getId()) && model.getGeneReactionRule(var.getSimpleName())!=null)
							res.put(var.getId(), model.getGeneReactionRule(var.getSimpleName()));
					}
				}
			}
		}
		
		return filterMetabolicReactionsThatCanReachTrueState(res);
	}
	
	
	protected HashMap<String, GeneReactionRule> filterMetabolicReactionsThatCanReachTrueState(HashMap<String, GeneReactionRule> tofilter){
		HashMap<String, GeneReactionRule> res=new HashMap<>();
		
		for (Map.Entry<String, GeneReactionRule> vars : tofilter.entrySet()) {
			String varid=vars.getKey();
			if(initregulatorystate.containsComponentID(varid)){
				boolean varstate=initregulatorystate.getBooleanStateForComponentID(varid);
				if(varstate)
					res.put(varid, vars.getValue());
			}
		}
		
		return res;
	}
	
	
	protected void initializeSynchronousMemory() throws Exception{
		 
		sortedmemoryelems=new ArrayList<>();
		sortedmemoryelemsruleslink=new ArrayList<>();
		ArrayList<String> savegeneids=new ArrayList<>();
		ArrayList<Boolean> firststate=new ArrayList<>();
		
		if(metabolicreactionsinfluencedbyregulatorymodel.size()>0)
		for (String varid : metabolicreactionsinfluencedbyregulatorymodel.keySet()) {
			sortedmemoryelems.add(varid);
			sortedmemoryelemsruleslink.add(varid);
			firststate.add(true);
		}
		
		
		
		for (int i = 0; i < regulatorymodel.getNumberOfRegulators(); i++) {
			String geneid=regulatorymodel.getRegulatorIdAtIndex(i);
			savegeneids.add(geneid);
			sortedmemoryelems.add(geneid);
			sortedmemoryelemsruleslink.add(regulatorymodel.getRuleIDAssociatedToRegulatorID(geneid));
			if(initregulatorystate.getKnockoutgenes()!=null && initregulatorystate.getKnockoutgenes().contains(geneid))
				firststate.add(false);
			else{
				int pos=initregulatorystate.getOrderedIdentifiers().indexOf(geneid);
				boolean state=initregulatorystate.getBooleanStateForComponentAtIndex(pos);
				firststate.add(state);
			}
		}
		
		memory=new OptFluxRegulatoryNetworkMemory(iterations,firststate, sortedmemoryelems);
		memory.setIdentifiersToSaveInAttractors(savegeneids);
		
	}
	
	
	public Environment<IValue> createEnvironment(ArrayList<Boolean> regulatorystate) {
		
		 HashSet<String> idsinenvironment=new HashSet<>();
		 Environment<IValue> environment = new Environment<IValue>();
		 IndexedHashMap<String, RegulatoryRule> rules=regulatorymodel.getRegulatoryRules();
		 IndexedHashMap<String, String> geneid2ruleid=initregulatorystate.getGeneid2Ruleid();
		 IndexedHashMap<String, String> ruleid2geneid=initregulatorystate.getRuleid2geneid();
		 
		 for (int i = 0; i < rules.size(); i++) {
			 String geneid=rules.getKeyAt(i);
			 HashSet<String> rulevars=null;
			 RegulatoryRule rule=rules.get(geneid);
			 // System.out.println("rule: "+rule.getRule());
			 if(rule.getBooleanRule()!=null)
				 rulevars=new HashSet<>(rule.getVariables());
			
			 if(rulevars!=null){
				 for (String id : rulevars) {
					if(!idsinenvironment.contains(id) && !sortedmemoryelems.contains(id) && !sortedmemoryelemsruleslink.contains(id)){
						boolean inputstate=false;
						boolean isreactionid=false;
						int index=-1;
		                 
						index=initregulatorystate.getOrderedIdentifiers().indexOf(id);
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
						
						
						if(useid!=null && initregulatorystate.getOrderedIdentifiers().contains(useid)){
							index=initregulatorystate.getOrderedIdentifiers().indexOf(useid);
							if(!isreactionid)
								id=useid;
							//System.out.println("Use Id: "+useid+" index: "+index);
						}
						
						if(index!=-1){
							inputstate=initregulatorystate.getInitialBooleanStates().get(index);
							environment.associate(id, new BooleanValue(inputstate));
							idsinenvironment.add(id);
						}
					}
				 }
			 } 
		}
		 
	    if(metabolicreactionsinfluencedbyregulatorymodel.size()>0){
	    	metabolicgenesassociatedreactions=new HashedMap<>();
	    	for (Map.Entry<String, GeneReactionRule> metbreactions : metabolicreactionsinfluencedbyregulatorymodel.entrySet()) {
	    		ArrayList<String> ruleelems=TreeUtils.withdrawVariablesInRule(metbreactions.getValue().getRule());
	    		
	    		for (int i = 0; i < ruleelems.size(); i++) {
	    			String id=ruleelems.get(i);
	    			//System.out.println(id+"-->"+idsinenvironment.contains(id)+" --> "+sortedmemoryelems.contains(id));
	    			if(!idsinenvironment.contains(id) && !sortedmemoryelems.contains(id)){
	    				if(unconstrainedgenesknockouts!=null && unconstrainedgenesknockouts.contains(id))
	    					environment.associate(id, new BooleanValue(false));
	    				else if(model.getUnconstrainedGenes()!=null && model.getUnconstrainedGenes().contains(id))
	    					environment.associate(id, new BooleanValue(true));
	    				idsinenvironment.add(id);
	    			}
	    			else if(!idsinenvironment.contains(id)){
	    				metabolicgenesassociatedreactions.put(geneid2ruleid.get(id), id);
	    				idsinenvironment.add(id);
	    			}
				  
	    		}
	    	}
	    }
		addExpressionInIEnvironment(environment, regulatorystate);
	  
		return environment;
	}
	
	
	private void addExpressionInIEnvironment(Environment<IValue> environment, ArrayList<Boolean> exp) {
		
		for (int i = 0; i < sortedmemoryelemsruleslink.size(); i++) {
			
			String id=sortedmemoryelemsruleslink.get(i);
			BooleanValue statevalue=new BooleanValue(exp.get(i));
			if((knockoutgeneproduct!=null && knockoutgeneproduct.contains(id)) || (knockoutGenes!=null && knockoutGenes.contains(id)))
				statevalue=new BooleanValue(false);
			
			environment.associate(id,statevalue);
			if(metabolicgenesassociatedreactions!=null && metabolicgenesassociatedreactions.containsKey(id)){
				environment.associate(metabolicgenesassociatedreactions.get(id), statevalue);
			}
		}

	}
	
	
	
	private ArrayList<Boolean> oneIteration(Environment<IValue> environment) {
		ArrayList<Boolean> antIteration = memory.lookTop();
		ArrayList<Boolean> iteration = new ArrayList<Boolean>();

		
		for (int i = 0; i < sortedmemoryelems.size(); i++) {
			AbstractSyntaxTree<DataTypeEnum, IValue> rule=null;
			String elemid=sortedmemoryelems.get(i);
			if(metabolicreactionsinfluencedbyregulatorymodel.containsKey(elemid))
				rule=metabolicreactionsinfluencedbyregulatorymodel.get(elemid).getRule();
			else
				rule=regulatorymodel.getRegulatoryRuleToRegulatorId(elemid).getBooleanRule();
			
			
			if(knockoutGenes!=null &&  knockoutGenes.contains(elemid))
				iteration.add(false);
			else{
				if(rule.getRootNode()!=null){
					
					boolean value = (Boolean)rule.evaluate(environment).getValue();
					iteration.add(value);
				}
				else
					iteration.add(antIteration.get(i));
				}
		}
	
		return iteration;
	}
	
	
	@Override
	public InitialRegulatoryState getInitialRegulatoryState() throws PropertyCastException, MandatoryPropertyException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
