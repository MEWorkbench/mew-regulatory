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
package pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.decoders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.representation.IRepresentation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.representation.set.SetRepresentation;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.strainoptimizationalgorithms.jecoli.components.decoder.GKDecoder;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;


public class RegulatoryGenesKnockoutDecoderinitial extends GKDecoder{
    
	

	private static final long serialVersionUID = 1L;
	//private ArrayList<String> regulatorygenes = null;
	private boolean restricttoregulatorygenes=false;
	private IndexedHashMap<Integer, Integer> decoderindex2regulatoryindex;
	private IndexedHashMap<Integer, String> decoderindex2geneid;

	public RegulatoryGenesKnockoutDecoderinitial(IIntegratedStedystateModel model) {
		super(model);
	}

	
	public RegulatoryGenesKnockoutDecoderinitial(IIntegratedStedystateModel model,List<String> notAllowedGeneKnockouts){
		this(model,false,notAllowedGeneKnockouts);
	}
	
	public RegulatoryGenesKnockoutDecoderinitial(IIntegratedStedystateModel model,boolean restricttoregulatorymodel,List<String> notAllowedGeneKnockouts){
		super(model);
		this.restricttoregulatorygenes=restricttoregulatorymodel;
		try {
			
			
			if(notAllowedGeneKnockouts != null)
				createNotAllowedGenesFromIds(notAllowedGeneKnockouts);
		} catch (Exception e) {
			LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage(e);
		}
	}
	/*
	protected void createDecoderIndex2GeneidMap() {
		deco
		
	}*/
	
	
	
	@Override
	public int getNumberVariables()
	{
		
		int totalgenes=0;
		if(restricttoregulatorygenes) {
		    totalgenes=((IIntegratedStedystateModel)model).getRegulatoryNetwork().getNumberOfRegulators();
		}
		else
			totalgenes=((IIntegratedStedystateModel)model).getTotalGenesIntegratedNetwork();
		
		
		if(notAllowedKnockouts!=null)
			totalgenes=totalgenes-notAllowedKnockouts.size();
		
		return totalgenes;
	}
	
	

	
	
	@Override
	public int getInitialNumberVariables(){
		if(restricttoregulatorygenes)
			return ((IIntegratedStedystateModel)model).getRegulatoryNetwork().getNumberOfRegulators();
		else
			return ((IIntegratedStedystateModel)model).getTotalGenesIntegratedNetwork();
	}
	


	@Override
	public void createNotAllowedGenesFromIds (List<String> notAllowedGeneIds) throws Exception
	{
		notAllowedKnockouts = new ArrayList<Integer>();
		
		ArrayList<String> modelgenes=null;
		if(restricttoregulatorygenes) {
			modelgenes=((IIntegratedStedystateModel)model).getRegulatoryNetwork().getRegulatorIDs();
		}
		else
			modelgenes=((IIntegratedStedystateModel)model).getAllGenes();
		
		for (int i = 0; i < notAllowedGeneIds.size(); i++) {
			String geneid=notAllowedGeneIds.get(i);
			if(restricttoregulatorygenes)
				if(modelgenes.contains(geneid))
					notAllowedKnockouts.add(((IIntegratedStedystateModel)model).getRegulatoryNetwork().getRegulatorIndex(geneid));
			else if(modelgenes.contains(geneid))
				notAllowedKnockouts.add(((IIntegratedStedystateModel)model).getIntegratedGeneIndex(geneid));
		}
		
		createInternalDecodeTable();
		
	}
	


	
	@Override
	public RegulatoryGeneticConditions decode (IRepresentation solution) throws Exception{
		TreeSet<Integer> genome = ((SetRepresentation)solution).getGenome();
		
		List<Integer> geneKnockoutList = decodeGeneKnockouts(genome);
		
		ArrayList<String> knockoutgeneids=new ArrayList<>();
		
		for (int i = 0; i < geneKnockoutList.size(); i++) {
			int index=geneKnockoutList.get(i);
			if(restricttoregulatorygenes)
				knockoutgeneids.add(((IIntegratedStedystateModel)model).getRegulatoryNetwork().getRegulatorIdAtIndex(index));
			else
				knockoutgeneids.add(((IIntegratedStedystateModel)model).getIntegratedGeneName(index));
		}
		
		System.out.println("KNOKOUTS: "+knockoutgeneids);
		
		return RegulatoryGeneticConditions.getRegulatoryGeneticConditions(knockoutgeneids, (IIntegratedStedystateModel) model);
		
	}
		

	
}
