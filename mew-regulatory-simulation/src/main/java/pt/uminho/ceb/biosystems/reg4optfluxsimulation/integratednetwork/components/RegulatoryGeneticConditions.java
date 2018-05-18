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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.pair.Pair;
import pt.uminho.ceb.biosystems.mew.utilities.java.StringUtils;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;

public class RegulatoryGeneticConditions extends GeneticConditions implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    protected GeneregulatorychangesList regulatorygeneList;
    protected IIntegratedStedystateModel model;

   
    
    public RegulatoryGeneticConditions(GeneregulatorychangesList reggeneList, GeneChangesList metgeneList, ISteadyStateGeneReactionModel model, boolean isOverUnder) throws Exception {
		super(metgeneList, model, isOverUnder);
		this.regulatorygeneList = reggeneList;
		
		if(model instanceof IIntegratedStedystateModel)
			this.model = (IIntegratedStedystateModel) model;
		
		// TODO Auto-generated constructor stub
	}
    

	
   public GeneregulatorychangesList getRegulatoryGenechangeList(){
	   return this.regulatorygeneList;
   }
	
	
	public void setGeneLists(GeneregulatorychangesList regulatoryGeneList, GeneChangesList geneList, ISteadyStateGeneReactionModel model) throws Exception
	{
		this.regulatorygeneList = regulatoryGeneList;
		this.geneList = geneList;
	    this.reactionList = geneList.getReactionUnderOverList(model);
	}
	
	
	public void setGeneLists(GeneregulatorychangesList regulatoryGeneList, GeneChangesList geneList){
		this.regulatorygeneList = regulatoryGeneList;
		this.geneList = geneList;
	}
	
	
	public boolean equals(RegulatoryGeneticConditions conditions){
		
		if(this.isOverUnder!=conditions.isOverUnder())
			return false;
		
		if(this.reactionList!=null && (!this.reactionList.equals(conditions.getReactionList())))
			return false;
		
		if(this.geneList!=null && (!this.geneList.equals(conditions.getGeneList())))
			return false;
		if(this.regulatorygeneList!=null && (!this.regulatorygeneList.equals(conditions.getRegulatoryGenechangeList())))
		    return false;
		
		return true;
	}
	
	
	 public ArrayList<String> getALLGeneKnockoutList(){
		  ArrayList<String> res = new ArrayList<String>();
		  // add metabolic gene
	   if(geneList!=null)	  
		  for (String genemet : geneList.getGeneKnockoutList()) {
			if(!res.contains(genemet)){ 
			 res.add(genemet);
			 }
		  }
	
	   if(regulatorygeneList!=null)
		  for (String genereg: regulatorygeneList.getGeneKnockoutList()){
              if(!res.contains(genereg))
			  res.add(genereg);
		   }
		 return res;
		 
	 }
	 
	 
	 public ArrayList<String> getMetabolicGenesKnockoutList(){
		 ArrayList<String> res = new ArrayList<String>();

		  for (String genemet : geneList.getGeneKnockoutList()) { 
			 res.add(genemet);
		  }
		  return res;
	 }
	 
	 
	 public ArrayList<String> getRegulatoryGenesKnockoutList(){
		 ArrayList<String> res = new ArrayList<String>();

		  for (String genereg : regulatorygeneList.getGeneKnockoutList()) {
			 res.add(genereg);
		  }
		  return res;
	 }
	 
	 
	 public ArrayList<String> getAllContainedGenes(){
			
			ArrayList<String> res = new ArrayList<String>();
			
			res.addAll(geneList.getGeneKnockoutList());
			res.addAll(regulatorygeneList.getGeneKnockoutList());
			
			
			return res;
			
		}
	 
	 public void removeGene(String geneid) throws Exception{
		 
		 if(geneList.containsGene(geneid))
			 geneList.removeGene(geneid);
		 else if(regulatorygeneList.containsGene(geneid))
			 regulatorygeneList.removeGene(geneid);
		 else
			 throw new Exception();
 
	 }
	 
	 public void addGene(String geneid){
		 
		 if(model.isMetabolicGene(geneid))
			 this.geneList.addGeneKnockout(geneid);
		 else
			 this.regulatorygeneList.addGeneKnockout(geneid);
		 
	 }
	 
	 
     @Override
	 public  GeneticConditions clone() {
    	 RegulatoryGeneticConditions clone = null;
			try {
				clone= new RegulatoryGeneticConditions(this.regulatorygeneList, this.geneList,this.model , this.isOverUnder);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

          return clone;
	 }
     
     @Override
     public String toString(){
 		List<Pair<String,Double>> pairs = new ArrayList<Pair<String,Double>>();
 		
 		if(getGeneList()!=null){
 			List<Pair<String,Double>> metabgenelist=getGeneList().getPairsList();
 			if(metabgenelist!=null && metabgenelist.size()>0)
 				pairs.addAll(metabgenelist);
 		}
 		/*else {
 			if(getReactionList().getPairsList()!=null && getReactionList().getPairsList().size()>0)
 			    pairs = getReactionList().getPairsList();
 		}*/
 		
 		if(regulatorygeneList!=null){
 			
 			List<Pair<String,Double>> reggenelist=regulatorygeneList.getPairsList();
 			//System.out.println("Regulatory List: "+reggenelist);
 			if(reggenelist!=null && regulatorygeneList.size()>0)
 				pairs.addAll(reggenelist);	
 		}
 		
 		
 		//System.out.println("Pairs: "+pairs+" genelist: "+getGeneList()+ "   reglist: "+regulatorygeneList+" reactlist: "+getReactionList().getPairsList());
 		StringBuffer sb = new StringBuffer();
 		for(int i=0; i< pairs.size(); i++){
 			Pair<String, Double> pair = pairs.get(i);
 			if(i>0)
 				sb.append(",");
 			sb.append(pair.getValue()+"="+pair.getPairValue());
 		}
 			
 		return sb.toString();
 	}
     

    @Override
 	public String toStringOptions(String sep,boolean excludeExpressionLevels){
 		List<Pair<String,Double>> pairs = new ArrayList<Pair<String,Double>>();
 		if(getGeneList()!=null && getRegulatoryGenechangeList()==null)
 			pairs = getGeneList().getPairsList();
 		else if(getGeneList()==null && getRegulatoryGenechangeList()!=null)
 			pairs=getRegulatoryGenechangeList().getPairsList();
 		else if(getGeneList()!=null && getRegulatoryGenechangeList()!=null) {
 			pairs.addAll(getGeneList().getPairsList());
 			pairs.addAll(getRegulatoryGenechangeList().getPairsList());
 		}
 		else pairs = getReactionList().getPairsList();
 		
 		StringBuffer sb = new StringBuffer();
 		for(int i=0; i< pairs.size(); i++){
 			Pair<String, Double> pair = pairs.get(i);
 			if(i>0)
 				sb.append(sep);
 			if(!excludeExpressionLevels)
 				sb.append(pair.getValue()+"="+pair.getPairValue());
 			else
 				sb.append(pair.getValue());
 		}
 			
 		return sb.toString();
 	}
 	
    @Override
 	public String toUniqueString(){
 		List<Pair<String,Double>> metabpairs = (getGeneList()!=null) ? getGeneList().getPairsList() : getReactionList().getPairsList();
 		
 		List<Pair<String,Double>> regpairs=getRegulatoryGenechangeList().getPairsList();
 				
 		TreeSet<String> ordered = new TreeSet<String>();	
 		
 		for (int i = 0; i < regpairs.size(); i++) {
 			Pair<String, Double> pair=regpairs.get(i);
 			ordered.add(pair.getValue());
		}
 		
 		for(int i=0; i< metabpairs.size(); i++){
 			Pair<String, Double> pair = metabpairs.get(i);			
 			if(isOverUnder){
 				ordered.add(pair.getValue()+"="+pair.getPairValue());				
 			}
 			else{
 				ordered.add(pair.getValue());				
 			}
 		}
 			
 		return StringUtils.concat(",", ordered);
 	}
     
     public static RegulatoryGeneticConditions getRegulatoryGeneticConditions(ArrayList<String> knockoutgenelist, IIntegratedStedystateModel model) throws Exception{
     	ArrayList<String> metabolicgenes=new ArrayList<>();
     	ArrayList<String> regulatorygenes=new ArrayList<>();
     	
     	for (int i = 0; i < knockoutgenelist.size(); i++) {
 			String geneid=knockoutgenelist.get(i);
 			if(model.isMetabolicGene(geneid))
 				metabolicgenes.add(geneid);
 			else if(model.isRegulatoryGene(geneid))
 				regulatorygenes.add(geneid);
 		}
     	
     	GeneChangesList metabolicgeneconditions=new GeneChangesList(metabolicgenes);
     	GeneregulatorychangesList regulatorygeneconditions=new GeneregulatorychangesList(regulatorygenes);
     	return new RegulatoryGeneticConditions(regulatorygeneconditions, metabolicgeneconditions, model, false);
     }
     
     public static RegulatoryGeneticConditions getRegulatoryGeneticConditions(String knockoutgeneid, IIntegratedStedystateModel model) throws Exception{
    	 ArrayList<String> list=new ArrayList<>();
    	 list.add(knockoutgeneid);
    	 return getRegulatoryGeneticConditions(list, model);
     }
     
     
 
	 

	 
	 
}

