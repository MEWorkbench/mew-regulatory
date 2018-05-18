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
package pt.uminho.ceb.biosystems.reg4optfluxcore.logicalmodel.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManagerFactory;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ext.qual.Input;

import pt.uminho.ceb.biosystems.booleanutils.sbmltools.AbstractSyntaxTreeConverterTools;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.BDDLogicalModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;

public class OptfluxRegulatoryModelToBDDLogicalModelConverter extends AbstractOptfluxRegulatoryModelToLogicalModelConverter{
	


	private static final long serialVersionUID = 1L;
	protected IndexedHashMap<String, RegulatoryVariable> copymodelvariables;
    protected Map<Integer, String> index2identifier;
	protected IndexedHashMap<String, Integer> geneid2index;
	protected IndexedHashMap<String, Integer> variableid2index;
	protected IndexedHashMap<String, RegulatoryModelComponent> mapvariable2comptype;
	protected LinkedHashMap<String, String> mapvaridtovarname;
	protected LinkedHashSet<String> colectedvariable;
	protected IndexedHashMap<String, RegulatoryRule> validgenerules;
	

	public OptfluxRegulatoryModelToBDDLogicalModelConverter(IndexedHashMap<String, Regulator> modelgenes,
			IndexedHashMap<String, RegulatoryRule> generules, IndexedHashMap<String, RegulatoryVariable> modelvariables,
			IndexedHashMap<String, String> geneid2ruleid, LinkedHashSet<String> unconstrainedgenes,boolean genesidlinkbyruleid) {
		super(modelgenes, generules, modelvariables, geneid2ruleid, unconstrainedgenes,genesidlinkbyruleid);
	}
	
	public OptfluxRegulatoryModelToBDDLogicalModelConverter(IOptfluxRegulatoryModel regmodel){
		this(regmodel.getRegulators(),regmodel.getRegulatoryRules(), regmodel.getVariablesInRegulatoryNetwork(),
				regmodel.getMapGeneId2RuleId(),regmodel.getUnconstrainedGenes(),regmodel.genesInRuleLinkByRuleID());
		
	}

	
	
	

	public IRODDRegulatoryModel convertModel() throws Exception{
		
		List<NodeInfo> variables=getVariables();
		name2index=new IndexedHashMap<>();
		int n=0;
		for (NodeInfo ni: variables) {
			String nodename=ni.getNodeID();
			mvf.add(ni, (byte)(ni.getMax()+1));
			name2index.put(nodename, n);
			n++;
		}
		ddmanager = MDDManagerFactory.getManager(mvf, 2);
		ddvariables = ddmanager.getAllVariables();
		int[] functions = new int[variables.size()];
		

		if(!forcenodeidsinregulatoryrule){
		   this.ruleid2geneid=null;
		}
		else{
			genesInRuleLinkByRuleID=false;
		}
		
		for (int i = 0; i < validgenerules.size(); i++) {
			
			m_curInputs.clear();
			String currentid=validgenerules.getKeyAt(i);
			RegulatoryRule rule=validgenerules.get(currentid);
			
			ASTNode sbmlnode=AbstractSyntaxTreeConverterTools.convertOptfluxTreeNodeToSBMLNodeTreeNode(rule.getBooleanRule().getRootNode(), this.ruleid2geneid);
			
			LinkedHashSet<String> ruleinputelems=AbstractSyntaxTreeConverterTools.getOrderedFirstRegulatorInputs(rule.getBooleanRule());
			
			List<Input> ruleinputs=getRuleInputs(sbmlnode, ruleinputelems);
			for (int j = 0; j < ruleinputs.size(); j++) {
				Input in=ruleinputs.get(j);
				m_curInputs.put(in.getId(), in);
			}
			
			int f = getMDDForMathML(ddmanager, sbmlnode, 1);
			int mdd = MDDBaseOperators.OR.combine(ddmanager, 0, f);
			
			
			int idx = getIndexForName(currentid);
			NodeInfo ni = variables.get(idx);
			if (ni.isInput()) {
				throw new RuntimeException("Constants can not be used as transition output");
			}
			functions[idx] = mdd;
			
		}
		
		int idx = 0;
		for (NodeInfo ni: variables) {
			
			if (ni.isInput()) {
				MDDVariable var = ddmanager.getVariableForKey(ni);
				int max = ni.getMax();
				if (max == 1) {
					functions[idx] = var.getNode(0, 1);
				} else {
					int[] values = new int[max+1];
					for (int i=0 ; i<values.length ; i++) {
						values[i] = i;
					}
					functions[idx] = var.getNode(values);
					
				}
			}
			idx++;
		}
		
		
		return new BDDLogicalModel(variables, ddmanager, functions,geneid2index,variableid2index,getCopyRegulators(),getCopyRegulatoryRules(),copymodelvariables, mapvariable2comptype,mapvaridtovarname, identifier2index,geneid2ruleid,unconstrainedgenes,genesInRuleLinkByRuleID);
	}
	
	
	
	protected List<NodeInfo> getVariables() {
		identifier2index = new HashMap<String, Integer>();
		geneid2index=new IndexedHashMap<>();
		variableid2index=new IndexedHashMap<>();
		index2identifier=new IndexedHashMap<>();
		copymodelvariables=new IndexedHashMap<>();
		mapvariable2comptype=new IndexedHashMap<>();
		colectedvariable=new LinkedHashSet<>();
		if(unconstrainedgenes==null)
			this.unconstrainedgenes=new LinkedHashSet<>();
		List<NodeInfo> listvariables = new ArrayList<NodeInfo>();
		this.validgenerules=new IndexedHashMap<>();
	
		getConditionVariables(listvariables);
		getGeneVariables(listvariables);
		
		return listvariables;
	}
	
	
	
	protected void getConditionVariables(List<NodeInfo> listvariables){
        
		for (int i =0 ; i < originalmodelvariables.size(); i++) {
			RegulatoryVariable var=originalmodelvariables.get(originalmodelvariables.getKeyAt(i));
			String varid=var.getId();
			
			if(!colectedvariable.contains(varid)){
				NodeInfo ni = new NodeInfo(varid, (byte)1);
				ni.setInput(true);
				listvariables.add(ni);
				identifier2index.put(varid, i);
				index2identifier.put(i, varid);
				variableid2index.put(varid, i);
				
				copymodelvariables.put(varid, var.copy());
				mapvariable2comptype.put(varid, var.getType());
			}	
		}
	}
	
	protected void getGeneVariables(List<NodeInfo> listvariables){
		
		int count=listvariables.size();
		for (int i = 0; i < modelgenes.size(); i++) {
			String geneid=modelgenes.getKeyAt(i);
			RegulatoryRule generule=generules.get(geneid);
			if(generule.getBooleanRule().getRootNode()!=null){
			   this.validgenerules.put(geneid, generule);	
			   NodeInfo ni = new NodeInfo(generule.getRuleId(), (byte)1);
			   listvariables.add(ni);
			   identifier2index.put(geneid, count);
			   index2identifier.put(count, geneid);
			   geneid2index.put(geneid, count);
			   count++;
			}
			else{
				if(unconstrainedgenes!=null && !unconstrainedgenes.contains(geneid))
					unconstrainedgenes.add(geneid);	
				
			}
		}
	}
	

	
	public static IRODDRegulatoryModel convertToBDDLogicalModel(IOptfluxRegulatoryModel regmodel) throws Exception {
		OptfluxRegulatoryModelToBDDLogicalModelConverter converter=new OptfluxRegulatoryModelToBDDLogicalModelConverter(regmodel);
		return converter.convertModel();
	}


}
