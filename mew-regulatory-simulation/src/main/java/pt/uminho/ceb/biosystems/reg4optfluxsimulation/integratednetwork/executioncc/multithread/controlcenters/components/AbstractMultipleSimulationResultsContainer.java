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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.controlcenters.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.javatuples.Triplet;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import pt.ornrocha.excelutils.ExcelVersion;
import pt.ornrocha.excelutils.MTUExcelWriterUtils;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.aux.MapOfResults;

public abstract class AbstractMultipleSimulationResultsContainer<T extends AbstractSimulationResultsContainer> {



	protected Object2ObjectOpenHashMap<String, MapOfResults<T>> groupresults;
	protected ArrayList<String> groupresultskeyslist=new ArrayList<>();

	
	
	protected int truepositive=0;
	protected int falsepositive=0;
	protected int falsenegative=0;
	protected int truenegative=0;
	protected int predictedpositive=0;
	protected int predictednegative=0;
	protected int experimentalpositive=0;
	protected int experimentalnegative=0;
	protected int totalevaluationsiniteration=0;
	
	protected IndexedHashMap<String, Double> accuracymap=new IndexedHashMap<>();
	protected IndexedHashMap<String, Double> scoref1map=new IndexedHashMap<>();
	protected IndexedHashMap<String, Double> recallmap=new IndexedHashMap<>();
	protected IndexedHashMap<String, Double> fdrmap=new IndexedHashMap<>();
	protected IndexedHashMap<String, Double> precisionmap=new IndexedHashMap<>();
	protected IndexedHashMap<String, Double> falsePositiveRatemap=new IndexedHashMap<>();
	protected IndexedHashMap<String, Double> falseNegativemap=new IndexedHashMap<>();


	public AbstractMultipleSimulationResultsContainer(){
		groupresults=new Object2ObjectOpenHashMap<>();
	}


	public void appendResult(String environmentalconditionid,String knockoutedgeneid, T resultscontainer){
		MapOfResults.insertNewValue(groupresults, environmentalconditionid, knockoutedgeneid, resultscontainer);
		if(!groupresultskeyslist.contains(environmentalconditionid))
			groupresultskeyslist.add(environmentalconditionid);
	}



	public Double getSimulatedValueFor(String environmentalconditionid,String knockoutedgeneid) {
		if(groupresults.containsKey(environmentalconditionid)) {
			if(groupresults.get(environmentalconditionid).containsKey(knockoutedgeneid))
				return groupresults.get(environmentalconditionid).get(knockoutedgeneid).getOptimizationValue();
		}
		return null;
	}

	public FluxValueMap getSimulatedFluxMapFor(String environmentalconditionid,String knockoutedgeneid) {
		if(groupresults.containsKey(environmentalconditionid)) {
			if(groupresults.get(environmentalconditionid).containsKey(knockoutedgeneid))
				return groupresults.get(environmentalconditionid).get(knockoutedgeneid).getFluxMap();
		}

		return null;
	}


	public boolean containsEnvironmentalCondition(String envcondid) {
		return groupresults.containsKey(envcondid);
	}


	public boolean containsAssociation(String envcondid, String knockoutedgeneid) {
		if(containsEnvironmentalCondition(envcondid))
			return groupresults.get(envcondid).containsKey(knockoutedgeneid);
		return false;
	}


	public int getNumberGenesAssociatedToEnvCondition(String envcondid) {
		if(containsEnvironmentalCondition(envcondid))
			return groupresults.get(envcondid).size();
		return 0;
	}

	public int getNumberEnviromentalConditions() {
		return groupresults.size();
	}


	public ArrayList<String> getListEnvironmentalConditions(){
		return new ArrayList<>(groupresults.keySet());
	}

	public ArrayList<String> getListKnockoutGenesAssociatedToEnvCondition(String envcondid){
		return new ArrayList<>(groupresults.get(envcondid).keySet());
	}

	public void print() {
		System.out.println(groupresults);
	}


	public MapOfResults<T> getResultsForCondition(String condid) {
		if(groupresults.containsKey(condid))
			return groupresults.get(condid);
		return null;
	}

	public Double getResultOfGeneAtCondition(String condid, String geneid) {

		MapOfResults<T> rescond=getResultsForCondition(condid);
		if(rescond!=null) {

			if(rescond.containsKey(geneid))
				return rescond.get(geneid).getOptimizationValue();
			else
				return null;
		}
		return null;

	}

	protected ArrayList<String> getGeneHeaderList(){

		HashSet<String> uniquegeneids=new HashSet<>();

		ArrayList<String> condids=getListEnvironmentalConditions();

		for (int i = 0; i < condids.size(); i++) {
			ArrayList<String> assogenes=getListKnockoutGenesAssociatedToEnvCondition(condids.get(i));
			uniquegeneids.addAll(assogenes);
		}

		return new ArrayList<>(uniquegeneids);


	}


	public void writeToExcelFile(String filepath,String sheetname) throws IOException {

		ArrayList<Object> genesheader=new ArrayList<>();
		genesheader.add("Conditions Vs genes");

		ArrayList<String> totalgenes=getGeneHeaderList();
		genesheader.addAll(totalgenes);

		ArrayList<ArrayList<Object>> datatoexcel=new ArrayList<>();
		datatoexcel.add(genesheader);

		ArrayList<String> condids=getListEnvironmentalConditions();

		for (int i = 0; i < condids.size(); i++) {
			String condid=condids.get(i);
			ArrayList<Object> rowdata=new ArrayList<>(genesheader.size());
			rowdata.add(condid);

			for (int j = 0; j < totalgenes.size(); j++) {
				Double value=getResultOfGeneAtCondition(condid, totalgenes.get(j));
				if(value!=null) {


					rowdata.add(value);
				}
				else
					rowdata.add("NULL");
			}
			datatoexcel.add(rowdata);
		}

		if(sheetname==null)
			sheetname="Simulation_results";
		MTUExcelWriterUtils.WriteDataToNewExcelFile(filepath, ExcelVersion.XLSX, datatoexcel, sheetname);
	}
	
	
	
	protected Triplet<ArrayList<ArrayList<Object>>,  LinkedHashMap<Integer, LinkedHashMap<Integer,Short>>, ArrayList<ArrayList<Object>>> getComparison(IndexedHashMap<String, IndexedHashMap<String, Double>> experimentalresults,boolean usesymbol){
		
		 ArrayList<String> totalgenes=getGeneHeaderList();
		 
		 ArrayList<ArrayList<Object>> matchratios=new ArrayList<>();
		 

		 
		 ArrayList<Object> genesheader=new ArrayList<>();
		 genesheader.add("Conditions Vs genes");
		 genesheader.addAll(totalgenes);
		 
		 ArrayList<ArrayList<Object>> datatoexcel=new ArrayList<>();
		  datatoexcel.add(genesheader);
		  
		  LinkedHashMap<Integer, LinkedHashMap<Integer,Short>> colorrowcolumnmap =new LinkedHashMap<>();
		  int totalmatch=0;
		  int totalanalysed=0;
		
		 for (int i = 0; i < experimentalresults.size(); i++) {
			String condition=experimentalresults.getKeyAt(i);
			
			 ArrayList<Object> rowdata=new ArrayList<>(genesheader.size());
			 rowdata.add(condition);
			 
			 int colorrow=i+1;
			 colorrowcolumnmap.put(colorrow, new LinkedHashMap<Integer,Short>());
			 
			 int numbermatch=0;
			 
			 IndexedHashMap<String, Double> expgenesres=experimentalresults.get(condition);
			
			 for (int j = 0; j < totalgenes.size(); j++) {
				 Double simvalue=getResultOfGeneAtCondition(condition, totalgenes.get(j));
				 Double expvalue=expgenesres.get(totalgenes.get(j));
				
				 if(simvalue!=null && expvalue!=null && usesymbol) {
					 
					 String expsymbol="-";
					 String simsymbol="-";
					 
					 if(expvalue>0.0)
						 expsymbol="+";
					 if(simvalue>0.0)
						 simsymbol="+";
					 
					 String comp=expsymbol+"/"+simsymbol;
					 rowdata.add(comp);
					 
					 if(expsymbol.equals(simsymbol)) {
						 colorrowcolumnmap.get(colorrow).put(j+1, IndexedColors.GREEN.getIndex());
						 numbermatch++;
						 totalmatch++;
					 }
					 else
						 colorrowcolumnmap.get(colorrow).put(j+1, IndexedColors.RED.getIndex()); 
					
					 totalanalysed++;
				 }
				 else if(simvalue!=null && expvalue!=null && !usesymbol) {
					 
					 int expvaltag=0;
					 int simvaltag=0;
					 
					 if(expvalue>0.0)
						 expvaltag=1;
					 if(simvalue>0.0)
						 simvaltag=1;
					 
					 int compvalue=0;
					 if(expvaltag==simvaltag) {
						 compvalue=1;
						 colorrowcolumnmap.get(colorrow).put(j+1, IndexedColors.GREEN.getIndex());
						 numbermatch++;
						 totalmatch++;
					 }
					  else
						 colorrowcolumnmap.get(colorrow).put(j+1, IndexedColors.RED.getIndex()); 
					
					 rowdata.add(compvalue);
					 totalanalysed++;
					 
				 }
				 else {
					 rowdata.add("NULL");
					 colorrowcolumnmap.get(colorrow).put(j+1, IndexedColors.GREY_50_PERCENT.getIndex()); 
				 }
			  }
			 
			 double matchratiocond=((double)numbermatch/totalgenes.size());
			 ArrayList<Object> condmatchs=new ArrayList<>();
			 condmatchs.add(condition);
			 condmatchs.add(matchratiocond*100);
			 matchratios.add(condmatchs);
			 
			 datatoexcel.add(rowdata);
		 }
		 
		 double totalmatchratiocond=((double)totalmatch/totalanalysed);
		 ArrayList<Object> totalcondmatchs=new ArrayList<>();
		 totalcondmatchs.add("Total");
		 totalcondmatchs.add(totalmatchratiocond*100);
		 matchratios.add(totalcondmatchs);

		 
		 return new Triplet<ArrayList<ArrayList<Object>>, LinkedHashMap<Integer,LinkedHashMap<Integer,Short>>, ArrayList<ArrayList<Object>>>(datatoexcel, colorrowcolumnmap, matchratios);

	}
	
	protected void reset(){
    	truepositive=0;
    	falsepositive=0;
    	falsenegative=0;
    	truenegative=0;
    	predictedpositive=0;
    	predictednegative=0;
    	experimentalpositive=0;
    	experimentalnegative=0;
    	totalevaluationsiniteration=0;
    }
	
	protected ArrayList<ArrayList<Object>> getROCParameters(IndexedHashMap<String, IndexedHashMap<String, Double>> experimentalresults){
		reset();
		ArrayList<String> totalgenes=getGeneHeaderList();
		 ArrayList<ArrayList<Object>> rocinfo=new ArrayList<>();
		
		for (int i = 0; i < experimentalresults.size(); i++) {
			String condition=experimentalresults.getKeyAt(i);
			IndexedHashMap<String, Double> expgenesres=experimentalresults.get(condition);
			
			 for (int j = 0; j < totalgenes.size(); j++) {
				 Double simvalue=getResultOfGeneAtCondition(condition, totalgenes.get(j));
				 Double expvalue=expgenesres.get(totalgenes.get(j));
				
				 if(simvalue!=null && expvalue!=null) {
					 
					 int convexpval= convertToBinary(expvalue);
					 int convsimval=convertToBinary(simvalue);
					
					 processRocGrowthParameters(convexpval,convsimval);
					 totalevaluationsiniteration++;
				 }
			  }
		 }
		
		ArrayList<Object> accuracy=getDescriptionAndValueList("Accuracy", accuracyRoc());
		rocinfo.add(accuracy);
		
		ArrayList<Object> recall=getDescriptionAndValueList("Recall", recall());
		rocinfo.add(recall);
		
		ArrayList<Object> precision=getDescriptionAndValueList("Precision", precision());
		rocinfo.add(precision);
		
		ArrayList<Object> f1score=getDescriptionAndValueList("F-score", scoreF1());
		rocinfo.add(f1score);
		
		ArrayList<Object> fdr=getDescriptionAndValueList("False Discovery Rate", falseDiscoveryRate());
		rocinfo.add(fdr);
		
		ArrayList<Object> fpr=getDescriptionAndValueList("False Positive Rate", falsePositiveRate());
		rocinfo.add(fpr);
		
		ArrayList<Object> plr=getDescriptionAndValueList("Positive Likelihood Ratio", positiveLikelihoodRatio());
		rocinfo.add(plr);
		
		ArrayList<Object> nlr=getDescriptionAndValueList("Negative Likelihood Ratio", negativeLikelihoodRatio());
		rocinfo.add(nlr);
		
		ArrayList<Object> fnr=getDescriptionAndValueList("False Negative Rate", falseNegativeRate());
		rocinfo.add(fnr);
		
		ArrayList<Object> tnr=getDescriptionAndValueList("True Negative Rate", trueNegativeRate());
		rocinfo.add(tnr);
		
		return rocinfo;
	}
	
	protected void processRocGrowthParameters(int expvalbinval, int simbinval ) {


		if(expvalbinval>0.0 && simbinval>0.0)
			truepositive++;
		else if(expvalbinval<=0.0 && simbinval<=0.0)
			truenegative++;
		else if(expvalbinval>0.0 && simbinval<=0.0) {
			falsenegative++;
		}
		else if(expvalbinval<=0.0 && simbinval>0.0)
			falsepositive++;


		if(simbinval>0.0)
			predictedpositive++;
		else
			predictednegative++;

		if(expvalbinval>0.0)
			experimentalpositive++;
		else
			experimentalnegative++;

	}
	
	protected int convertToBinary(double value) {
		if(value>0.0)
			return 1;
		else
			return 0;
	}
	
	
	protected ArrayList<Object> getDescriptionAndValueList(String legend, double value){
		ArrayList<Object> list=new ArrayList<>();
		list.add(legend);
		list.add(value);
		return list;
	}
	
	
	public void writeComparisonToExcelFile(IndexedHashMap<String, IndexedHashMap<String, Double>> experimentalresults, String filepath,String sheetname, boolean usesymbol, boolean showratiopercentages, boolean showrocinfo) throws IOException {
		
		
		Triplet<ArrayList<ArrayList<Object>>, LinkedHashMap<Integer,LinkedHashMap<Integer,Short>>, ArrayList<ArrayList<Object>>> processeddata=getComparison(experimentalresults, usesymbol);

		if(sheetname==null)
			  sheetname="Simulation_results";
		
		MTUExcelWriterUtils.WriteDataToNewExcelFileWithCellColors(filepath, ExcelVersion.XLSX, processeddata.getValue0(), processeddata.getValue1(), sheetname);
		if(showratiopercentages)
			MTUExcelWriterUtils.updateDataOfExcelFile(filepath+".xlsx", processeddata.getValue2(), "Match_Ratios_percentage");
		
		if(showrocinfo) {
			ArrayList<ArrayList<Object>> rocinfo=getROCParameters(experimentalresults);
			MTUExcelWriterUtils.updateDataOfExcelFile(filepath+".xlsx", rocinfo, "Performance Measures");
		}

	}
	
	
	
	
	
	protected double recall() {
		return ((double)truepositive/experimentalpositive);
	}
	
	protected double falsePositiveRate() {
		return ((double)falsepositive/experimentalnegative);
	}
	
	protected double falseNegativeRate() {
		return ((double)falsenegative/experimentalpositive);
	}
	
	protected double trueNegativeRate() {
		return ((double)truenegative/experimentalnegative);
	}
	
	
	protected double falseDiscoveryRate() {
		return ((double)falsepositive/predictedpositive);
	}
	
	protected double precision() {
		return ((double)truepositive/predictedpositive);
	}
	
	protected double accuracyRoc() {
		return ((double)(truepositive+truenegative))/totalevaluationsiniteration;
	}
	
	protected double scoreF1() {
		return 2/((1/recall())+(1/precision()));
	}
	
	protected double positiveLikelihoodRatio() {
		return recall()/falsePositiveRate();
	}
	
	protected double negativeLikelihoodRatio() {
		return falseNegativeRate()/trueNegativeRate();
	}

}
