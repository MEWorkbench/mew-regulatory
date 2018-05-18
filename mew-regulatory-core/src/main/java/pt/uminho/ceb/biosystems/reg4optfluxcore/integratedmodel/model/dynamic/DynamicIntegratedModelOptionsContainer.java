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
package pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.dynamic;

import java.util.HashMap;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;

public class DynamicIntegratedModelOptionsContainer {
	
	IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables;
	HashMap<String, String> extrageneid2ruleid;
	HashMap<String, String> extrageneid2genename;

	public DynamicIntegratedModelOptionsContainer() {

	}

	public DynamicIntegratedModelOptionsContainer(
			IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables,
			HashMap<String, String> extrageneid2ruleid, HashMap<String, String> extrageneid2genename) {
		this.possibletypevariables = possibletypevariables;
		this.extrageneid2ruleid = extrageneid2ruleid;
		this.extrageneid2genename = extrageneid2genename;
	}

	public IndexedHashMap<String, RegulatoryModelComponent> getPossibleTypeVariables() {
		return possibletypevariables;
	}

	public void setPossibleTypeVariables(IndexedHashMap<String, RegulatoryModelComponent> possibletypevariables) {
		this.possibletypevariables = possibletypevariables;
	}

	public HashMap<String, String> getExtraGeneid2Ruleid() {
		return extrageneid2ruleid;
	}

	public void setExtraGeneid2Ruleid(HashMap<String, String> extrageneid2ruleid) {
		this.extrageneid2ruleid = extrageneid2ruleid;
	}

	public HashMap<String, String> getExtraGeneid2GeneName() {
		return extrageneid2genename;
	}

	public void setExtraGeneid2Geneame(HashMap<String, String> extrageneid2genename) {
		this.extrageneid2genename = extrageneid2genename;
	}
	
	
	

}
