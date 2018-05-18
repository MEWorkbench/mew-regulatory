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
package pt.uminho.ceb.biosystems.reg4optfluxcore.container.components;

import java.io.Serializable;

public enum RegulatoryModelComponent implements Serializable {
	
	REACTION_ID{
		
		@Override
		public String getDenomination() {
			return "Reaction";
		}
		
		@Override
		public String toString() {
			return "Reaction";
		}
		
		@Override
		public String getCategory() {
			return "@reactions";
		}
	},
	METABOLITE_ID{
		@Override
		public String getDenomination() {
			return "Metabolite";
		}
		
		@Override
		public String toString() {
			return "Metabolite";
		}
		
		@Override
		public String getCategory() {
			return "@metabolites";
		}
	},
    TRANS_FACTOR_ID{
    	@Override
    	public String getDenomination() {
    		return "Transciptional_Factor";
    	}
    	
    	@Override
		public String toString() {
    		return "Transciptional Factor";
		}
    	
    	@Override
		public String getCategory() {
			return "@tfs";
		}
    },
    GENE_ID{
    	@Override
    	public String getDenomination() {
    		return "Gene";
    	}
    	
    	@Override
		public String toString() {
    		return "Gene";
		}
    	
    	@Override
		public String getCategory() {
			return "@genes";
		}
    },
	ENV_CONDITION_ID{
		@Override
		public String getDenomination() {
			return "Environmental_Condition";
		}
		
		@Override
		public String toString() {
			return "Environmental Condition";
		}
		
		@Override
		public String getCategory() {
			return "@environmentalconditions";
		}
	};

	public String getDenomination(){
		return getDenomination();
	}
	
	public String getCategory(){
		return getCategory();
	}
	
	@Override
	public String toString(){
		return toString();
	}
	
	public static RegulatoryModelComponent getRegulatoryComponentFromString(String vartype){
		
		for (RegulatoryModelComponent comp : RegulatoryModelComponent.values()) {
			if(vartype.toLowerCase().equals(comp.getCategory().toLowerCase()))
				return comp;
			else if(vartype.toLowerCase().equals(comp.getDenomination().toLowerCase()))
				return comp;
			
		}
		
		return null;
	}
}
