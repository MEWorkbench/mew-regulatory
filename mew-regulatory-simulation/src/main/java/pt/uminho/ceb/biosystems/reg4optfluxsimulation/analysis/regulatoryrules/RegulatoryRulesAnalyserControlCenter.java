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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.analysis.regulatoryrules;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import pt.ornrocha.ioutils.readers.MTUReadUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.dynamic.DynamicIntegratedModelOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.executors.MultiThreadSimulationExecutorCallableTasks;

public class RegulatoryRulesAnalyserControlCenter {
	
	
	 private IIntegratedStedystateModel integratedmodel;
	 private IntegratedSimulationOptionsContainer simulationproperties;
	 private DynamicIntegratedModelOptionsContainer dynamicproperties;
	 private IndexedHashMap<String, EnvironmentalConditions> envcondset;
	 private boolean analysecriticalgenes=false;
	 private ArrayList<String> usecriticalgenes;
	 private String savecriticalgenestodir;
	 private double threshold=0.001;
	 private AbstractIntegratedSimulationControlCenter controlcenter;
	 private IndexedHashMap<String, String> regulatoryrulestoanalyse;
	 private IndexedHashMap<String, Integer> originalrulesorder;
	 private IndexedHashMap<String, RulesAnalyserReporterContainer> analysisresults=new IndexedHashMap<>();
	 private int numberprocesses=-1;
	 
	 ///private boolean analyseexternalruleset=false;
 
	 
	
	
	 public RegulatoryRulesAnalyserControlCenter(IIntegratedStedystateModel integratedmodel, IndexedHashMap<String, EnvironmentalConditions> envcondset, IntegratedSimulationOptionsContainer simulationproperties,DynamicIntegratedModelOptionsContainer dynamicmodelproperties, boolean analysecriticalgenes){
		 this.integratedmodel=integratedmodel;
		 this.simulationproperties=simulationproperties;
		 this.dynamicproperties=dynamicmodelproperties;
		 this.envcondset=envcondset;
		 this.analysecriticalgenes=analysecriticalgenes; 
	 }
	 
	 
	 public void setNumberSimultaneousProcesses(int numberproc){
		 this.numberprocesses=numberproc;
	 }
	 
	 public void setThreshold(double threshold){
		 this.threshold=threshold;
	 }
	
	 
	 public void saveCriticalGenesTodir(String dirpath){
		 this.savecriticalgenestodir=dirpath;
	 }
	 
	 public void setCriticalGenesFile(String filepath) throws IOException{
		 this.usecriticalgenes=(ArrayList<String>) MTUReadUtils.readFileLines(filepath);
	 }
	 
	 public void setCriticalGenes(ArrayList<String> criticalgenes){
		 this.usecriticalgenes=criticalgenes;
	 }

	 public void setExternalRegulatoryModel(IRegulatoryNetwork model){
		 setRulesToAnalyse(model);	 
	 }
	
	 private void setRulesToAnalyse(IRegulatoryNetwork model){
		 IndexedHashMap<String, RegulatoryRule> rules=model.getRegulatoryRules();
		 OrderRuleDependencies sorter=OrderRuleDependencies.load(rules); 
		 this.regulatoryrulestoanalyse=sorter.getNewSortedRules();
		 this.originalrulesorder=sorter.getOriginalOrder();
	 }
	
	 public void execute(){
		 
		 if(regulatoryrulestoanalyse==null)
			 setRulesToAnalyse(integratedmodel.getRegulatoryNetwork());
		 
		 if(envcondset==null){
			 try {
				RegulatoryRulesAnalyserThread singlethread=new RegulatoryRulesAnalyserThread(integratedmodel, regulatoryrulestoanalyse, originalrulesorder, null, simulationproperties, dynamicproperties,analysecriticalgenes);
				singlethread.setAcceptableBiomassThreshold(threshold);
				if(usecriticalgenes!=null)
					singlethread.setCriticalGenes(usecriticalgenes);
				else if(savecriticalgenestodir!=null){
					String filepath=FilenameUtils.concat(savecriticalgenestodir, "_critical_genes_without_env_conds.txt");
					singlethread.writeCriticalGeneToFile(filepath);
				}
				singlethread.run();
				analysisresults.put("None", singlethread.getReport());
			} catch (Exception e) {
				LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage("Error in analysis of rules: ", e);
			}
		 }
		 else{
			 if(envcondset.size()==1){
				 try {
					    EnvironmentalConditions envconds=envcondset.getValueAt(0);
					    envconds.setId(envcondset.getKeyAt(0));
						RegulatoryRulesAnalyserThread singlethread=new RegulatoryRulesAnalyserThread(integratedmodel, regulatoryrulestoanalyse, originalrulesorder, envconds, simulationproperties, dynamicproperties,analysecriticalgenes);
						singlethread.setAcceptableBiomassThreshold(threshold);
						if(usecriticalgenes!=null)
							singlethread.setCriticalGenes(usecriticalgenes);
						else if(savecriticalgenestodir!=null){
							String filepath=FilenameUtils.concat(savecriticalgenestodir, "_critical_genes_to_"+envconds.getId()+".txt");
							singlethread.writeCriticalGeneToFile(filepath);
						}
						singlethread.run();
						analysisresults.put(envconds.getId(), singlethread.getReport());
					} catch (Exception e) {
						LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage("Error in analysis of rules: ", e);
					}
			 }
			 else{
				 ArrayList<RegulatoryRulesAnalyserTask> tasks=new ArrayList<>();
				 ArrayList<String> taskname=new ArrayList<>();
				 try {
					 for (int i = 0; i < envcondset.size(); i++) {
						 EnvironmentalConditions envcond=envcondset.getValueAt(i);
						 envcond.setId(envcondset.getKeyAt(i));
						 RegulatoryRulesAnalyserThread singlethread=new RegulatoryRulesAnalyserThread(integratedmodel, regulatoryrulestoanalyse, originalrulesorder, envcond, simulationproperties, dynamicproperties,analysecriticalgenes);
						 singlethread.setAcceptableBiomassThreshold(threshold);
						 RegulatoryRulesAnalyserTask task=new RegulatoryRulesAnalyserTask(singlethread);
						 tasks.add(task);
						 taskname.add(envcondset.getKeyAt(i));
					 }
				 } catch (Exception e) {
						LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage("Error in analysis of rules: ", e);
					}
				 
				 try {
					ArrayList<RulesAnalyserReporterContainer> results =(ArrayList<RulesAnalyserReporterContainer>) MultiThreadSimulationExecutorCallableTasks.execute(numberprocesses, tasks);
					for (int i = 0; i < results.size(); i++) {
						analysisresults.put(taskname.get(i), results.get(i));
					}
				} catch (Exception e) {
					LogMessageCenter.getLogger().toClass(getClass()).addCriticalErrorMessage("Error running the analysis of rules: ", e);
				}
			 }
		 } 
	 }
	 
	 
	 public void writeReportsToDirectory(String dir, boolean writepossibleproblems) throws IOException{
		 for (int i = 0; i < analysisresults.size(); i++) {
			analysisresults.getValueAt(i).WriteReportToTxtFile(dir, writepossibleproblems);
		}
	 }
	 
	 public void writeWorkingModelToDirectory(String dir) throws IOException{
		 for (int i = 0; i < analysisresults.size(); i++) {
			 analysisresults.getValueAt(i).WriteRegulatoryModelWithoutProblematicRule(dir);
		 }
	 }
	 
	 public void writeWorkingModelToDirectoryInSBMLFormat(String dir) throws Exception{
		 for (int i = 0; i < analysisresults.size(); i++) {
			 analysisresults.getValueAt(i).WriteRegulatoryModelWithoutProblematicRuleToSBMLQual(dir);
		 }
	 }
	 
	 public void writeWorkingModelToDirectoryInCSVFormat(String dir) throws Exception{
		 for (int i = 0; i < analysisresults.size(); i++) {
			 analysisresults.getValueAt(i).WriteRegulatoryModelWithoutProblematicRuleToCSVFormat(dir);
		 }
	 }
	 
	 public void writeRulesThatCanCauseGrowthInibition(String dir) throws Exception{
		 for (int i = 0; i < analysisresults.size(); i++) {
			 analysisresults.getValueAt(i).writeProblematicRulesToCSVFile(dir);
		 }
	 }
	 

}
