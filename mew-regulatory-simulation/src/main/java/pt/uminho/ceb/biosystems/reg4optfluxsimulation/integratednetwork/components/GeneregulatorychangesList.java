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

import java.util.List;

import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;

public class GeneregulatorychangesList extends GeneChangesList{
	
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    public GeneregulatorychangesList (){
    	super();
    }
    
    public GeneregulatorychangesList (List<String> regulatorygenes, List<Double> expression){
    	super(regulatorygenes,expression);
    }
    
    public GeneregulatorychangesList (List<String> regulatorygenes){
    	super(regulatorygenes);
    }
    
    
    public GeneregulatorychangesList copy(){
		return (GeneregulatorychangesList)(super.clone());
	}
   
    
    @Override
    public void addGene(int geneIndex,double geneRate, ISteadyStateGeneReactionModel model){
		String geneID = null;
		if(model instanceof IIntegratedStedystateModel)
				geneID=((IIntegratedStedystateModel)model).getGene(geneIndex).getId();
		else
			geneID=model.getGene(geneIndex).getId();
		addGene(geneID, geneRate);
	}


    
    
}