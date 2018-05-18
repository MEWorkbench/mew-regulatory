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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pt.ornrocha.collections.MTUCollectionsUtils;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;

public class RegulatoryNetworkGenesInfoContainer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashSet<String> knockoutgenes;
	private HashSet<String> geneswithinitialfalsestate;
	

	public RegulatoryNetworkGenesInfoContainer(){};
	
	public RegulatoryNetworkGenesInfoContainer(HashSet<String> knockoutgenes,
			HashSet<String> geneswithinitialfalsestate) {
		this.knockoutgenes = knockoutgenes;
		this.geneswithinitialfalsestate = geneswithinitialfalsestate;
	}
	
	
	public void addKnockoutGene(Gene gene){
		addKnockoutGene(gene.getId());	
	}
	
	public void addKnockoutGene(String geneid){
		if(knockoutgenes==null)
			knockoutgenes=new HashSet<>();
		knockoutgenes.add(geneid);	
	}
	
	public void addknockoutGenes(ArrayList<String> genesetids){
		for (String id : genesetids) {
			addKnockoutGene(id);
		}
	}
	
	
	public void setGeneWithInitialFalseState(String geneid){
		if(geneswithinitialfalsestate==null)
			this.geneswithinitialfalsestate=new HashSet<>();
		geneswithinitialfalsestate.add(geneid);	
	}
	
	public void setGeneWithInitialFalseState(Gene gene){
		setGeneWithInitialFalseState(gene.getId());
	}
	
	public void setGeneSetWithInitialFalseState(HashSet<String> geneids){
		this.geneswithinitialfalsestate=geneids;
	}
	
	public void addGeneSetWithInitialFalseState(Set<String> geneids){
		for (String id : geneids) {
			setGeneWithInitialFalseState(id);
		}
	}
	
	public HashSet<String> getKnockoutGenes() {
		return knockoutgenes;
	}


	public HashSet<String> getGenesWithInitialFalseState() {
		return geneswithinitialfalsestate;
	}
	
	
	public RegulatoryNetworkGenesInfoContainer copy() throws Exception{
		HashSet<String> geneswithinitialfalsestateclone=(HashSet<String>) MTUCollectionsUtils.deepCloneObject(geneswithinitialfalsestate);
		HashSet<String> knockoutgenesclone=(HashSet<String>) MTUCollectionsUtils.deepCloneObject(knockoutgenes);
		
		return new RegulatoryNetworkGenesInfoContainer(knockoutgenesclone, geneswithinitialfalsestateclone);
	}
	
	

}
