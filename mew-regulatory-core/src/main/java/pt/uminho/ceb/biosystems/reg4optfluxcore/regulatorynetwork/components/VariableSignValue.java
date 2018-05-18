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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components;

public enum VariableSignValue {
	
	
	LESS{
		@Override
		public String getSign() {
			return "<";
		}
		
		@Override
		public String getName() {
			return "_less_than_";
		}
		
		@Override
		public String buildVariableName(String varid, String value) {
			if(value.matches("\\d+\\.\\d+"))
				value=value.replaceAll("\\.", "dot");
				
			return varid+getName()+value;
		}
		
	},
	
	LESSOREQUALTO{
		@Override
		public String getSign() {
			return "<=";
		}
		
		@Override
		public String getName() {
			return "_less_or_equal_";
		}
		
		@Override
		public String buildVariableName(String varid, String value) {
			if(value.matches("\\d+\\.\\d+"))
				value=value.replaceAll("\\.", "dot");
			return varid+getName()+value;
		}
		
	},
	
	GREATER{
		@Override
		public String getSign() {
			return ">";
		}
		
		@Override
		public String getName() {
			return "_greater_than_";
		}
		
		@Override
		public String buildVariableName(String varid, String value) {
			if(value.matches("\\d+\\.\\d+"))
				value=value.replaceAll("\\.", "dot");
			return varid+getName()+value;
		}
	},
	
	GREATEROREQUALTO{
		@Override
		public String getSign() {
			return ">=";
		}
		
		@Override
		public String getName() {
			return "_greater_or_equal_";
		}
		
		@Override
		public String buildVariableName(String varid, String value) {
			if(value.matches("\\d+\\.\\d+"))
				value=value.replaceAll("\\.", "dot");
			return varid+getName()+value;
		}
	};
	
	public String getName(){
		return getName();
	}
	
	
	public String getSign(){
		return getSign();
	}
	
	public String buildVariableName(String varid, String value){
		return buildVariableName(varid, value);
	}

}
