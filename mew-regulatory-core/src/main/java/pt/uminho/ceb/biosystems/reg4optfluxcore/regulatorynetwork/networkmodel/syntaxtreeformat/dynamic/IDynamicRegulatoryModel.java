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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.dynamic;


import java.util.LinkedHashSet;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;


public interface IDynamicRegulatoryModel extends IOptfluxRegulatoryModel{
	
	
	public static String IGNORERULETAG="NORULE";
	
	void initializeDynamicRegulatoryModelParameters();
	void setNewSingleRegulatoryRule(String geneid, String rule) throws Exception;
	void addNewSingleRegulatoryRule(String geneid, String rule)  throws Exception;
	void setNewGroupOfRegulatoryRules(IndexedHashMap<String, String> regrules) throws Exception;
    void addNewGroupOfRegulatoryRules(IndexedHashMap<String, String> regrules)  throws Exception ;
    
   // void appendNewSingleRegulatoryRule(String geneid, String rule)  throws Exception ;

    
    IndexedHashMap<String, RegulatoryVariable> getOnlyNewRegulatoryVariablesOfNetwork();
    IndexedHashMap<String, Integer> getIndexesNewRegulatoryVariablesOfNetwork();
    
    void setSaveAPreviousState(boolean saveAPreviousState);
    void setRegModelToPreviousInternalState();
    //void checkMetabolicGeneMissingRules(IndexedHashMap<String,Gene> metabolicgenes);
    
    IDynamicRegulatoryModel getNewInstance() throws Exception;
    LinkedHashSet<String> getNewConstrainedGenes();
}
