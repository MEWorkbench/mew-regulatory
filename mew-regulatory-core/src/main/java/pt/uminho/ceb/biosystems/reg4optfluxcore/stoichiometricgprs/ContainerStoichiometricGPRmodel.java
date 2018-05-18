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
package pt.uminho.ceb.biosystems.reg4optfluxcore.stoichiometricgprs;


import java.util.Map;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.CompartmentCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.GeneCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.MetaboliteCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionConstraintCI;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.IntegratedModelContainer;

public class ContainerStoichiometricGPRmodel extends IntegratedModelContainer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String ext_compartment;
	
	public ContainerStoichiometricGPRmodel() {
		super();
	}
	
	
	public void setReactions(Map<String, ReactionCI> contreactions) {
		this.reactions=contreactions;
	}
	
	public void setMetabolites(Map<String, MetaboliteCI> contmetabolites) {
		this.metabolites=contmetabolites;
	}
	
	public void setCompartments(Map<String, CompartmentCI> contcompartments) {
		this.compartments=contcompartments;
	}
	
	public void setGene(Map<String, GeneCI> contgenes) {
		this.genes=contgenes;
	}
	
	public void setReactionConstraints(Map<String, ReactionConstraintCI> contdefaultEC) {
		this.defaultEC=contdefaultEC;
		
	}
	
	public void setExternalCompartmentID(String id) {
		this.ext_compartment=id;
	}
	
	@Override
	public CompartmentCI getExternalCompartment() {
		CompartmentCI ret = compartments.get(ext_compartment);

		return ret;
	}

}
