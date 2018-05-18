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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components;

public enum RegulatorySimulationMethod {
	
	OPTFLUXSYNCHRONOUSBOOLEANSIMULATION{
		@Override
		public String getName() {
			return "OptFlux synchronous simulation";
		}
		
		@Override
		public String shortName() {
			return "OSBS";
		}
		
		@Override
		public boolean supportsBDDFormat() {
			return false;
		}
	},
	OPTFLUXINTEGRATEDSYNCHRONOUSBOOLEANSIMULATION{
		@Override
		public String getName() {
			return "OptFlux integrated synchronous simulation";
		}
		
		@Override
		public String shortName() {
			return "OISBS";
		}
		
		@Override
		public boolean supportsBDDFormat() {
			return false;
		}
	},
	BDDSYNCHRONOUSBOOLEANSIMULATION{
		@Override
		public String getName() {
			return "BDD synchronous simulation";
		}
		
		@Override
		public String shortName(){
			return "BDDSBS";
		}
		
		@Override
		public boolean supportsBDDFormat() {
			return true;
		}
	},
	BDDASYNCHRONOUSBOOLEANSIMULATION{
		@Override
		public String getName() {
			return "BDD asynchronous simulation";
		}
		
		@Override
		public String shortName(){
			return "BDDASBS";
		}
		
		@Override
		public boolean supportsBDDFormat() {
			return true;
		}
	},
	BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION{
		@Override
		public String getName() {
			return "BDD asynchronous simulation with biomass verification";
		}
		
		@Override
		public String shortName(){
			return "BDDASBSG";
		}
		
		@Override
		public boolean supportsBDDFormat() {
			return true;
		}
	};
	/*BDDSEQUENCIALBOOLEANSIMULATION{
		@Override
		public String getName() {
			return "BDD sequencial simulation";
		}
		
		@Override
		public String shortName(){
			return "BDDSEQBS";
		}
		
		@Override
		public boolean supportsBDDFormat() {
			return true;
		}
	};*/
	
	
	public String getName(){
		return getName();
	}
	
	public String shortName(){
		return shortName();
	}
	
	public boolean supportsBDDFormat(){
		return supportsBDDFormat();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	
	public static RegulatorySimulationMethod[] getStrictlyRegulatoryMethods() {
		return new RegulatorySimulationMethod[]{RegulatorySimulationMethod.OPTFLUXSYNCHRONOUSBOOLEANSIMULATION, 
				RegulatorySimulationMethod.BDDSYNCHRONOUSBOOLEANSIMULATION,
				RegulatorySimulationMethod.BDDASYNCHRONOUSBOOLEANSIMULATION};
	}
	
	

	public static RegulatorySimulationMethod[] getRegulatorySimulationMethodsArray(int...indexes) {
		
		RegulatorySimulationMethod[] res=new RegulatorySimulationMethod[indexes.length];
		RegulatorySimulationMethod[] all=RegulatorySimulationMethod.values();
		
		for (int i = 0; i < indexes.length; i++) {
			res[i]=all[indexes[i]];
		}
		return res;
	}

}
