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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.writers;

public enum RegulatoryModelExportFormat {
	
	
	SBMLQUAL{
		@Override
		public String getName() {
			return "Sbml-qual";
		}
		
		@Override
		public String getFormatName() {
			return "Sbml-qual file format";
		}
		
		@Override
		public String getExtension() {
			return "xml";
		}
	},
	
	TEXTFILE{
		@Override
		public String getName() {
			return "Comma-separated";
		}
		
		@Override
		public String getFormatName() {
			return "Comma-separated values (csv)";
		}
		
		@Override
		public String getExtension() {
			return "csv";
		}
	};
	
	
	public String getName(){
		return getName();
	}
	
	public String getFormatName(){
		return getFormatName();
	}
	
	public String getExtension(){
		return getExtension();
	}

	@Override
	public String toString() {
		return getFormatName();
	}
}
