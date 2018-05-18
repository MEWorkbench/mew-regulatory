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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components;

import java.util.LinkedHashSet;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;

public interface IRegulatoryNetworkReader {
	
	String getModelName();
	String getOrganismName();
	String getNotes();

	IndexedHashMap<String, Regulator> getRegulatoryGenes();
	IndexedHashMap<String, RegulatoryVariable> getRegulatoryVariables();
	IndexedHashMap<String, RegulatoryModelComponent> getRegulatoryVariableType();
	IndexedHashMap<String, RegulatoryRule> getRegulatoryGeneRules();
	IndexedHashMap<String, String> getGeneID2RuleID();
	boolean genesInRuleLinkByRuleID();
	LinkedHashSet<String> getUnconstrainedGenes();
	void loadModel() throws Exception;
	boolean isStrictBooleanFormalism();
	boolean isVerifiedvariables();
	IRODDRegulatoryModel getROBDDModelFormat();
	boolean isModelLoaded();
	

}
