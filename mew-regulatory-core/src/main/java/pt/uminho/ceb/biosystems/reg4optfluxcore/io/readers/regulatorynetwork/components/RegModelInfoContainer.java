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

import java.io.Serializable;

public class RegModelInfoContainer implements Serializable{
	

	private static final long serialVersionUID = 1L;
	private int gprlinkcolumn=0;
	private int ruleslinkbycolumn=-1;
	private int genenamescolumn=-1;
	private int rulescolumn=-1;

	
	private String delimiter=null;
	
	
	
	public RegModelInfoContainer(int GPRLinkColumn, int rulescolumn,String delimiter) {
		this.gprlinkcolumn = GPRLinkColumn;
		this.rulescolumn = rulescolumn;
		this.delimiter=delimiter;
	}
	
	

	public RegModelInfoContainer(int GPRLinkColumn, int TFsLinkColumn, int rulescolumn, String delimiter) {
		this.gprlinkcolumn = GPRLinkColumn;
		this.ruleslinkbycolumn = TFsLinkColumn;
		this.rulescolumn=rulescolumn;
		this.delimiter = delimiter;
	}
	
	public RegModelInfoContainer(int GPRLinkColumn, int TFsLinkColumn, int GeneNamesColumn, int rulescolumn, String delimiter) {
		this.gprlinkcolumn = GPRLinkColumn;
		this.ruleslinkbycolumn = TFsLinkColumn;
		this.genenamescolumn = GeneNamesColumn;
		this.rulescolumn=rulescolumn;
		this.delimiter = delimiter;
	}
	


	public int getGPRLinkColumn() {
		return gprlinkcolumn;
	}


	public int getRuleLinkColumn() {
		return ruleslinkbycolumn;
	}


	public int getGeneNamesColumn() {
		return genenamescolumn;
	} 
	
	public String getDelimiter() {
		return delimiter;
	}

	public int getRulescolumn() {
		return rulescolumn;
	}
	
	public static RegModelInfoContainer getDefaultContainer(){
		return new RegModelInfoContainer(0, 2, 1, 3, ";");
	}
	
	@Override
	public String toString() {

		StringBuilder str=new StringBuilder();
		
		str.append("gprlinkcolumn: "+gprlinkcolumn+"\n");
		str.append("ruleslinkbycolumn: "+ruleslinkbycolumn+"\n");
		str.append("genenamescolumn: "+genenamescolumn+"\n");
		str.append("rulescolumn: "+rulescolumn+"\n");
		str.append("Delimiter: "+delimiter+"\n");
		
		return str.toString();
	}

}
