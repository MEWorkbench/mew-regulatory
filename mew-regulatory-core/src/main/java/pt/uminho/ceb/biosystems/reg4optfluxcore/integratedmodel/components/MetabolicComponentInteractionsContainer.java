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
package pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components;

import java.io.Serializable;
import java.util.HashSet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pt.ornrocha.collections.MTUGuavaUtils;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;

public class MetabolicComponentInteractionsContainer implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected ISteadyStateModel model;
	protected Multimap<String, String> reactionreactants;
	protected Multimap<String, String> reactionproducts;
	protected Multimap<String, String> drainreactions;

	public MetabolicComponentInteractionsContainer(ISteadyStateModel model){
		this.model=model;
		checkMetabolitesOfReactions();
	}
	
	
	
	public Multimap<String, String> getReactionReactantsMap() {
		return reactionreactants;
	}

    public Multimap<String, String> getReactantReactionsMap(){
    	return MTUGuavaUtils.invertMultimap(getReactionReactantsMap(), ArrayListMultimap.create());
    }

	public Multimap<String, String> getReactionProductsMap() {
		return reactionproducts;
	}
 
	public Multimap<String, String> getProductReactionsMap(){
		return MTUGuavaUtils.invertMultimap(getReactionProductsMap(), ArrayListMultimap.create());
	}


	public Multimap<String, String> getDrainReactionMetaboliteMap() {
		return drainreactions;
	}
	
	public Multimap<String, String> getMetaboliteOfDrainReactionMap(){
		return  MTUGuavaUtils.invertMultimap(getDrainReactionMetaboliteMap(), ArrayListMultimap.create());
	}



	protected void checkMetabolitesOfReactions(){
		reactionreactants=ArrayListMultimap.create();
		reactionproducts=ArrayListMultimap.create();
		drainreactions=ArrayListMultimap.create();
		
		HashSet<String> reactions=new HashSet<>(model.getReactions().keySet());
		for (String id : reactions) {
			int index=model.getReactionIndex(id);
			double[] metabindexes=model.getStoichiometricMatrix().getColumn(index);
			for (int i = 0; i < metabindexes.length; i++) {
				if(metabindexes[i]!=0){
					String metabid=model.getMetabolite(i).getId();
					int drainreactionlink=model.getDrainIndexFromMetabolite(i);
					if(drainreactionlink!=-1 && model.getReactionId(drainreactionlink).equals(id)){
					  	drainreactions.put(id, metabid);
					}
					else{	
					    double metabvalue=metabindexes[i];
					    if(metabvalue>0)
						    reactionproducts.put(id, metabid);
					    else
						    reactionreactants.put(id, metabid);
					}
				}
			}
		}
	}

	public static MetabolicComponentInteractionsContainer createContainer(ISteadyStateModel model){
		return new MetabolicComponentInteractionsContainer(model);
	}


	

}
