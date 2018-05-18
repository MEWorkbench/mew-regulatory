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
package pt.uminho.ceb.biosystems.reg4optfluxcore.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.CompartmentCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.GeneCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.MetaboliteCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionConstraintCI;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.interfaces.IContainerIntegratedModelBuilder;

public class IntegratedModelContainer extends Container{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected RegulatoryContainer regulatorycontainer;
	
	
	public IntegratedModelContainer(){
		
		reactions = new HashMap<String, ReactionCI>();
		metabolites = new HashMap<String, MetaboliteCI>();
		compartments = new HashMap<String, CompartmentCI>();
		genes = new HashMap<String, GeneCI>();
		defaultEC = new HashMap<String, ReactionConstraintCI>();
		metabolitesExtraInfo = new HashMap<String, Map<String,String>>();
		reactionsExtraInfo = new HashMap<String, Map<String,String>>();
		regulatorycontainer = new RegulatoryContainer();
		
	}
	
	
	
	public IntegratedModelContainer(IContainerIntegratedModelBuilder builder) throws IOException{
		super(builder);
		regulatorycontainer=builder.getRegulatoryContainer();
	}
	
	
	public IntegratedModelContainer (Container metabolicontainer){
		super(metabolicontainer.clone());
	}
	
	public IntegratedModelContainer (Container metabolicontainer, RegulatoryContainer regcontainer){
		this(metabolicontainer);
		setRegcontainer(regcontainer);
	}
	
	public RegulatoryContainer getRegulatoryModelcontainer() {
		return regulatorycontainer;
	}


	public void setRegcontainer(RegulatoryContainer regcontainer) {
		this.regulatorycontainer = regcontainer;
	}

	
	
	
	

}
