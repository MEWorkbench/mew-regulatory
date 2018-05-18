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
package pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model;

import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.Protein;
import pt.uminho.ceb.biosystems.mew.core.model.components.ProteinReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.GeneType;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.IntegratedNetworkInitialStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.RegulatoryNetworkFormat;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;

public interface IIntegratedStedystateModel extends ISteadyStateGeneReactionModel{
	

	Boolean isRegulatoryGene(String geneId);
	Boolean isMetabolicGene(String geneId);
	Boolean genepresentinbothnetworks(String geneId);
	IRegulatoryNetwork getRegulatoryNetwork();
	IRegulatoryNetwork getRegulatoryNetworkFormat(RegulatoryNetworkFormat format);
	boolean isInBDDRegulatoryNetworkFormat();
	Integer getNumberOfCommonGenesInBothNetwork();
	GeneType getGeneType(String geneid);
	ArrayList<String> getAllGenes();
	ArrayList<String> getUnconstrainedGenes();
	IndexedHashMap<String, String> getRegulatoryMapGeneId2RuleId();
	
	void changeRegulatoryVariableType(String varid, RegulatoryModelComponent type);
	
	void setOptfluxRegulatoryNetworkFormat(IOptfluxRegulatoryModel regmodel) throws Exception ;
	void setROBDDRegulatoryNetworkFormat(IRODDRegulatoryModel regmodel)throws Exception ;
	void useLogicalModelNetworkFormat(boolean active);
	boolean supportsMultiValuedRegulatorySimulation();
	boolean supportLogicalModelFormat();
	boolean isMultiValueRegulatoryModel();
	
	IOptfluxRegulatoryModel getOptFluxRegulatoryModelFormat();
	IRODDRegulatoryModel getRODDRegulatoryModelFormat();
	
	IntegratedNetworkInitialStateContainer getIntegratedVariablesContainerWihoutValidation();
	IntegratedNetworkInitialStateContainer getIntegratedVariablesContainerWithValidation(EnvironmentalConditions envconds) throws Exception;
	ArrayList<String> getGenesInRegulatoryModel();
	ArrayList<String> getonlyMetabolicGenes();
	ArrayList<String> getOnlyRegulatoryGenes();
	ArrayList<String> filterOnlyMetabolicGenes(ArrayList<String> geneslist);
	ArrayList<String> filterOnlyRegulatoryGenes(ArrayList<String> geneslist);
	/*void setMetabolicMissingGenesToRegulatoryModel();*/
	
	IndexedHashMap<String,Protein> getProteins();
	IndexedHashMap<String,ProteinReactionRule> getProteinReactionRules();
	//IIntegratedStedystateModel copy() throws Exception;
	
	
	////////////// for optimization purposes /////////////
	
	int getIntegratedGeneIndex(String geneid);
	String getIntegratedGeneName(int index);
	int getTotalGenesIntegratedNetwork();
	
	
	

}
