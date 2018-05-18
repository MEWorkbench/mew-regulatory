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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;

public interface IRegulatoryNetwork extends Serializable{
	
	
	Integer getNumberOfRegulatoryRules();
	
	IRegulatoryNetwork copy() throws Exception;
	
	String getModelID();
	
	// Genes
	IndexedHashMap<String, Regulator> getRegulators();
	RegulatoryRule getRegulatoryRule(int ruleIndex);
	RegulatoryRule getRegulatoryRuleToRegulatorId(String geneid);
	IndexedHashMap<String, RegulatoryRule> getRegulatoryRules();
	
	Integer getNumberOfRegulators();
	Integer getRegulatorIndex(String geneId);
	String getRegulatorIdAtIndex(int index);
	boolean isRegulatorOnlyAtRegulatoryNetwork(String geneid);
	String getRegulatorIDAssociatedToRuleID(String ruleid);
	String getRuleIDAssociatedToRegulatorID(String geneid);
	ArrayList<String> getRegulatorIDs();
	LinkedHashSet<String> getUnconstrainedGenes();
	
	
	// Gene Rules
	Integer getRegulatoryGeneIndex(String geneId);
	Integer getRuleIndexForIdentifier(String ruleId);
	String getIdentifierOfRuleIndex(Integer ruleIndex);
	String getRuleIdAtIndex(Integer ruleIndex);
	IndexedHashMap<String, String> getMapGeneId2RuleId();
	
	
	
	// variables
	Integer getNumberOfVariables();
	Integer getVariableIndex(String variableName);
	
	ArrayList<String> getVariableNamesInNetwork();
	RegulatoryModelComponent getTypeOfVariable(String varid);
	IndexedHashMap<String, RegulatoryModelComponent> getTypeofRegulatoryVariables();
	void changeVariableType(String varid, RegulatoryModelComponent type);
	IndexedHashMap<String, RegulatoryVariable> getVariablesInRegulatoryNetwork();
	RegulatoryVariable getVariableByIndex(int variableIDX);
	RegulatoryVariable getRegulatoryVariable(String id);
	
	boolean genesInRuleLinkByRuleID();
	
	
	
}
