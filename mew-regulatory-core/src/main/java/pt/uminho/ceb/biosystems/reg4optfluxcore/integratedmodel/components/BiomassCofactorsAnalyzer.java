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

import java.util.ArrayList;

import org.javatuples.Pair;

import pt.ornrocha.logutils.MTULogLevel;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;

public class BiomassCofactorsAnalyzer {
	
	private ISteadyStateModel model=null;
	private IndexedHashMap<String, Integer> biomasscofactors=new IndexedHashMap<>();
	private IndexedHashMap<String, ArrayList<String>> mapofproductreactionstobiomasscofactors=new IndexedHashMap<>();
	private IndexedHashMap<String, ArrayList<String>> mapcofactorstoreactionsproducts=new IndexedHashMap<>();
	private IndexedHashMap<String, ArrayList<String>> mapcofactorstoreactionsreactants=new IndexedHashMap<>();
	private String biomassid;
	
	
	public BiomassCofactorsAnalyzer(ISteadyStateModel model){
		this.model=model;
		LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator("BiomassCofactorsAnalyzer ", 3, 2);
		loadData();
	}
	
	public BiomassCofactorsAnalyzer(ISteadyStateModel model, String biomassid){
		this.model=model;
		this.biomassid=biomassid;
		loadData();
	}
	
	
	
	protected void loadData(){
		checkBiomassCofactors();
		checkCofactorsReactionsMap();
	}
	
	
	protected void checkBiomassCofactors(){
		
		if(biomassid==null)
		   biomassid=model.getBiomassFlux();
		
		System.out.println(biomassid);
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Biomass identifier", biomassid);
		int reactindex=model.getReactionIndex(biomassid);
		double[] indexes=model.getStoichiometricMatrix().getColumn(reactindex);
		for (int i = 0; i < indexes.length; i++) {
			if(indexes[i]!=0){
				biomasscofactors.put(model.getMetaboliteId(i),i);
				LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Cofactor: "+model.getMetaboliteId(i)+"--> value: "+indexes[i]);
				//MTULogUtils.addDebugMsgToClass(this.getClass(), "Cofactor: "+model.getMetaboliteId(i)+"--> value: "+indexes[i]);
			}
		}
	}
	
	protected void addMapOfProductReactionToCofactorId(String metabid, ArrayList<String> reacts){
		
		for (int i = 0; i < reacts.size(); i++) {
			String reactid=reacts.get(i);
			if(mapofproductreactionstobiomasscofactors.containsKey(reactid)){
				ArrayList<String> metabs=mapofproductreactionstobiomasscofactors.get(reactid);
				if(!metabs.contains(metabid))
					metabs.add(metabid);
			}
			else{
				ArrayList<String> metabs=new ArrayList<>();
				metabs.add(metabid);
				mapofproductreactionstobiomasscofactors.put(reactid, metabs);
			}
		}
	}
	
	protected void checkCofactorsReactionsMap(){
		for (int i = 0; i < biomasscofactors.size(); i++) {
			LogMessageCenter.getLogger().toClass(getClass()).addTraceSeparator("Cofactor: "+biomasscofactors.getKeyAt(i));
			//MTULogUtils.addDebugMsgToClass(this.getClass(),"\n\n\n############### "+biomasscofactors.getKeyAt(i)+"  cofactor ######################\n");
			Pair<ArrayList<String>, ArrayList<String>> metabreactionsmap=getReactionsOfCofactor(biomasscofactors.getValueAt(i));
			
			ArrayList<String> isreactantofreaction=metabreactionsmap.getValue0();
			ArrayList<String> isproductofreaction=metabreactionsmap.getValue1();
			
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Is reactant in reactions: "+isreactantofreaction);
			LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Is product in reactions: "+isproductofreaction);
			
			
			if(isreactantofreaction.size()>0)
				mapcofactorstoreactionsreactants.put(biomasscofactors.getKeyAt(i), isreactantofreaction);
			if(isproductofreaction.size()>0){
				mapcofactorstoreactionsproducts.put(biomasscofactors.getKeyAt(i), isproductofreaction);
				addMapOfProductReactionToCofactorId(biomasscofactors.getKeyAt(i), isproductofreaction);
			}
		}
		
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Reactants of Reactions: ", mapcofactorstoreactionsreactants, "\n");
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Products of Reactions: ", mapcofactorstoreactionsproducts, "\n");
		
	}
	
	protected Pair<ArrayList<String>, ArrayList<String>> getReactionsOfCofactor(int indexmetab){
		double[] indexes=model.getStoichiometricMatrix().getRow(indexmetab);
		ArrayList<String> product=new ArrayList<>();
		ArrayList<String> reactant=new ArrayList<>();
		for (int i = 0; i < indexes.length; i++) {
			if(indexes[i]!=0){
				String reactid=model.getReactionId(i);
				double val=model.getStoichiometricValue(indexmetab, i);
				if(val>0)
					product.add(reactid);
				else if(val<0)
					reactant.add(reactid);
			}
		}
		return new Pair<ArrayList<String>, ArrayList<String>>(reactant, product);
	}
	
	
	public IndexedHashMap<String, ArrayList<String>> getInhibitorGrowthCoFactorReactions(FluxValueMap simfluxvalues){
		
		IndexedHashMap<String, ArrayList<String>>res=new IndexedHashMap<>();
		  for (int i = 0; i < mapcofactorstoreactionsproducts.size(); i++) {
			  ArrayList<String> reactids=mapcofactorstoreactionsproducts.getValueAt(i);

			  int reactzerovalue=0;
			  for (int j = 0; j < reactids.size(); j++) {
				   String reactid=reactids.get(j);
		           if(simfluxvalues.containsKey(reactid)){
				     double val=simfluxvalues.get(reactid);
				     if(val<=0.0)
					   reactzerovalue++;
		           }
			   }
			  
			  if(reactzerovalue==reactids.size())
				  res.put(mapcofactorstoreactionsproducts.getKeyAt(i), mapcofactorstoreactionsproducts.getValueAt(i));
		  }
		  
		 if(LogMessageCenter.getLogger().isEnabled() && LogMessageCenter.getLogger().getLogLevel().equals(MTULogLevel.TRACE))  
			 for (int i = 0; i < res.size(); i++) {
				 ArrayList<String> reactions=res.getValueAt(i);
			 
				 LogMessageCenter.getLogger().addTraceMessage("==================== "+res.getKeyAt(i)+ " ===================");

				 for (String id : reactions) {
					 LogMessageCenter.getLogger().addTraceMessage(id+" --> "+simfluxvalues.get(id));
				 }
				 LogMessageCenter.getLogger().addTraceMessage("\n");
		
			
			 }

		return res;
	}
	

	public ArrayList<String> getBiomassFactorsForReactionID(String reactid){
		if(mapofproductreactionstobiomasscofactors.containsKey(reactid))
			return mapofproductreactionstobiomasscofactors.get(reactid);
		return null;
	}
	
	public ArrayList<String> getBiomassFactorsInfluencedByReactions(ArrayList<String> reactids){
		ArrayList<String> res=new ArrayList<>();
		
		for (int i = 0; i < reactids.size(); i++) {
			
			String react=reactids.get(i);
			ArrayList<String> metabs=getBiomassFactorsForReactionID(react);
			if(metabs!=null){
				for (String string : metabs) {
					if(!res.contains(string))
						res.add(string);
				}
			}
			
			
		}
		return res;
	}
	
	
	public ArrayList<String> validateReactionsThatInfluenceBiomassCofactors(ArrayList<String> reactids){
		ArrayList<String> res=new ArrayList<>();
		
		for (int i = 0; i < reactids.size(); i++) {
			String reactid=reactids.get(i);
			if(mapofproductreactionstobiomasscofactors.containsKey(reactid) && !res.contains(reactid))
				res.add(reactid);
		}
		
		return res;
	}
	

}
