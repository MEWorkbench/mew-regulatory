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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components;

public enum IntegratedSimulationMethod {
	
	INTEGRATEDSIMULATION{
		@Override
		public String getName() {
			return "Integrated Regulatory Simulation";
		}
		
		@Override
		public String shortName() {
			return "IRSIM";
		}
	},
	DYNAMICRFBA{
		@Override
		public String getName() {
			return "Dynamic Regulatory FBA";
		}
		
		@Override
		public String shortName() {
			return "DRFBA";
		}
	},
	SRFBA{
		@Override
		public String getName() {
			return "Steady-state Regulatory FBA";
		}
		
		@Override
		public String shortName() {
			return "SRFBA";
		}
	},
	PROM{
		@Override
		public String getName() {
			return "Probabilistic Regulation of Metabolism";
		}
		
		@Override
		public String shortName() {
			return "PROM";
		}
	},
	GEMINI{
		@Override
		public String getName() {
			return "Gene Expression and Metabolism Integrated for Network Inference";
		}
		
		@Override
		public String shortName() {
			return "GEMINI";
		}
	},
	ASYNCHINTEGRATEDSIMULATION{
		@Override
		public String getName() {
			return "Integrated Regulatory Simulation With Async growth stop";
		}
		
		@Override
		public String shortName() {
			return "IRSIMAGS";
		}
	};
	
	
	/*
	 * get Method Name
	 */
	public String getName(){
		return getName();
	}
	
	public String shortName(){
		return shortName();
	}
	
	public String toString(){
		return getName();
	}
	
	public static IntegratedSimulationMethod getIntegratedSimulationMethodFromString(String method){
		
		for (IntegratedSimulationMethod met : IntegratedSimulationMethod.values()) {
			if(method.toLowerCase().equals(met.shortName().toLowerCase()))
				return met;
			else if(method.toLowerCase().equals(met.getName().toLowerCase()))
				return met;
		}
		return IntegratedSimulationMethod.INTEGRATEDSIMULATION;
	}
	
	
	public static IntegratedSimulationMethod[] getIntegratedSimulationMethodsArray(int...indexes) {
		
		IntegratedSimulationMethod[] res=new IntegratedSimulationMethod[indexes.length];
		IntegratedSimulationMethod[] all=IntegratedSimulationMethod.values();
		
		for (int i = 0; i < indexes.length; i++) {
			res[i]=all[indexes[i]];
		}
		return res;
	}

}
