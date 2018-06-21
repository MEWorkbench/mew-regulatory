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
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import jbiclustge.datatools.databases.kegg.collectors.geneinfo.EcoliGeneInfoKeggConnector;
import jbiclustge.datatools.databases.kegg.components.ECOLIGENEIDENTIFIER;
import jbiclustge.datatools.databases.kegg.containers.KeggGeneInfoContainer;
import pt.ornrocha.collections.MTUMapUtils;
import pt.ornrocha.fileutils.MTUDirUtils;
import pt.ornrocha.ioutils.writers.MTUWriterUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.ornrocha.webutils.connectionutils.WebConnectionException;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.model.components.GeneReactionRule;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.Environment;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.TreeUtils;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.BooleanValue;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.BiomassCofactorsAnalyzer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.components.GeneReactionsInteractionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.writers.CSVRegulatoryModelExporter;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.writers.SBMLqualRegulatoryModelExporter;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.string.RegStringUtils;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;

public class RulesAnalyserReporterContainer {
	
		private ArrayList<String> criticalgenes=null;
	
	   private IndexedHashMap<String, String> problematicRules=new IndexedHashMap<>();
	   private IndexedHashMap<String, String> problematicRulesreport=new IndexedHashMap<>();
	   private IndexedHashMap<String, String> problematicRulesWithFaultEssencialGenesreport=new IndexedHashMap<>();
	   private IndexedHashMap<String,  HashSet<String>> essencialgenesoff=new IndexedHashMap<>();
	   
	   private IndexedHashMap<String, Pair<Double,String>> highbiomassimpactrule=new IndexedHashMap<>();
	   private IndexedHashMap<String, String> highbiomassimpactrulereport=new IndexedHashMap<>();
	   
	   private IndexedHashMap<String, Pair<Double,String>> ruleswithoutproblems=new IndexedHashMap<>();
	   private IndexedHashMap<String, String> ruleswithoutproblemsreport=new IndexedHashMap<>();
	   
	    private IndexedHashMap<String, IndexedHashMap<String, ArrayList<String>>> inhibitbiomasscofactors=new IndexedHashMap<>();
	    private IndexedHashMap<String, String> inhibitbiomasscofactorsreport=new IndexedHashMap<>();
	   
	   private IIntegratedStedystateModel model;
	   private IndexedHashMap<String, Integer> originalrulesorder;
	   private GeneReactionsInteractionsContainer genereactionanalyzer;
	   private BiomassCofactorsAnalyzer cofactorsanalyzer;
	  
	   private IntegratedSimulationMethodResult currentsimulationresults;
	   private boolean showgenekegginformation=true;
	   private IndexedHashMap<String, KeggGeneInfoContainer> metabolicgenesinfo=new IndexedHashMap<>();
	   private IntegratedSimulationOptionsContainer simulationoptions;
	   private EnvironmentalConditions envconds;
	   
	   
	   public RulesAnalyserReporterContainer(IIntegratedStedystateModel model, ArrayList<String> criticalgenes, IndexedHashMap<String, Integer> originalrulesorder, IntegratedSimulationOptionsContainer simulationoptions, EnvironmentalConditions envconds){
		   this.model=model;
		   this.criticalgenes=criticalgenes;
		   this.originalrulesorder=originalrulesorder;
		   this.cofactorsanalyzer=new BiomassCofactorsAnalyzer(model);
		   this.genereactionanalyzer=new GeneReactionsInteractionsContainer(model);
		   this.simulationoptions=simulationoptions;
		   this.envconds=envconds;
		   
	   }
	
	
	   
	   public void addProblematicRule(String geneid, String rule){
		   problematicRules.put(geneid, rule);
		   buildProblematicRulesReport(geneid,rule);
	   }
	   
	   public void addRuleWithAffectsBiomassGrowth(String geneid, double valuebiomass, String rule){
		   highbiomassimpactrule.put(geneid, new Pair<Double, String>(valuebiomass, rule));
		   buildRuleAffectBiomassGrowthReport(geneid, valuebiomass, rule);
	   }
	   
	   public void addRuleWithoutProblems(String geneid, double valuebiomass,String rule){
		   ruleswithoutproblems.put(geneid, new Pair<Double, String>(valuebiomass, rule));
		   buildRulesWithoutProblemsReport(geneid, valuebiomass, rule);
	   }
	   
	   
	   public void analyseInhibitionOfBiomassCofactors(String geneid, String rule,FluxValueMap fluxes) throws Exception{
		   IndexedHashMap<String, ArrayList<String>> cofactorreactions=cofactorsanalyzer.getInhibitorGrowthCoFactorReactions(fluxes);
		   inhibitbiomasscofactorsreport.put(geneid, getInteractionGeneReactionsOfBiomassCofactors(cofactorreactions, geneid, rule));
		  // System.out.println(getInteractionGeneReactionsOfBiomassCofactors(cofactorreactions, rule));
		   inhibitbiomasscofactors.put(geneid, cofactorreactions);
	   }
	   
	   public void setSimulationCurrentResults(IntegratedSimulationMethodResult results){
		   this.currentsimulationresults=results;
		  // System.out.println("Reporter Metabolic genes out: "+currentsimulationresults.getMetabolicGenesKnock());
	   }
	
	   
	   private void buildProblematicRulesReport(String geneid, String rule){
		   StringBuilder str=new StringBuilder();
		   
		   LogMessageCenter.getLogger().toClass(getClass()).addInfoSeparator("A problematic rule was found", 3, 1);
		   if(criticalgenes!=null){
			   boolean criticalgene=false;
			   if(criticalgenes.contains(geneid))
				   criticalgene=true;
			   str.append("Rule associated To: "+geneid+" Is critical Gene: "+criticalgene+" Rule: "+rule+"\n");
			   LogMessageCenter.getLogger().toClass(getClass()).addInfoMessage("Rule associated To: "+geneid+" Is critical Gene: "+criticalgene+" Rule: "+rule);
		   }
		   else{
			  str.append("Rule associated To: "+geneid+" Rule: "+rule+"\n"); 
			  LogMessageCenter.getLogger().toClass(getClass()).addInfoMessage("Rule associated To: "+geneid+" Rule: "+rule);
		   }
		   LogMessageCenter.getLogger().toClass(getClass()).addInfoSeparator(null, 1, 1);   
		  
		   problematicRulesreport.put(geneid, str.toString());
	   }
	   
	   private void buildRulesWithoutProblemsReport(String geneid,double value, String rule){
		   StringBuilder str=new StringBuilder();
		   
		   if(criticalgenes!=null){
			   boolean criticalgene=false;
			   if(criticalgenes.contains(geneid))
				   criticalgene=true;
			   str.append("Rule associated To: "+geneid+" Growth value: "+value+" Is critical Gene: "+criticalgene+" Rule: "+rule+"\n");
			   LogMessageCenter.getLogger().toClass(getClass()).addInfoMessage("Rule associated To: "+geneid+" Growth value: "+value+" Is critical Gene: "+criticalgene+" Rule: "+rule);
		   }
		   else{
			  str.append("Rule associated To: "+geneid+" Growth value: "+value+" Rule: "+rule+"\n"); 
			  LogMessageCenter.getLogger().toClass(getClass()).addInfoMessage("Rule associated To: "+geneid+" Growth value: "+value+" Rule: "+rule);
		   }
		 
		  
		   ruleswithoutproblemsreport.put(geneid, str.toString());
	   }
	   
	   private void buildRuleAffectBiomassGrowthReport(String geneid,double value, String rule){
		   StringBuilder str=new StringBuilder();
		   
		   LogMessageCenter.getLogger().toClass(getClass()).addInfoSeparator("A rule which affects highly the biomass growth was found", 3, 1);
		   if(criticalgenes!=null){
			   boolean criticalgene=false;
			   if(criticalgenes.contains(geneid))
				   criticalgene=true;
			   str.append("Rule associated To: "+geneid+" Growth value: "+value+" Is critical Gene: "+criticalgene+" Rule: "+rule+"\n");
			   LogMessageCenter.getLogger().toClass(getClass()).addInfoMessage("Rule associated To: "+geneid+" Growth value: "+value+" Is critical Gene: "+criticalgene+" Rule: "+rule);
		   }
		   else{
			  str.append("Rule associated To: "+geneid+" Growth value: "+value+" Rule: "+rule+"\n"); 
			  LogMessageCenter.getLogger().toClass(getClass()).addInfoMessage("Rule associated To: "+geneid+" Growth value: "+value+" Rule: "+rule);
		   }
		   LogMessageCenter.getLogger().toClass(getClass()).addInfoSeparator(null, 1, 1);   
		  
		   highbiomassimpactrulereport.put(geneid, str.toString());
	   }
	   
	   
/*	   protected String getCurrentMetabolicGeneState(ArrayList<String> geneids){
		   StringBuilder str=new StringBuilder();
		   str.append("[ ");
		   
		   if(geneids!=null)
			   for (String id : geneids) {
				   if(currentsimulationresults.getMetabolicGenesKnock().contains(id))
					   str.append(id+":OFF"+" ");
				   else
					   str.append(id+":ON"+" ");
			   }
		   str.append("]\n");
		   return str.toString();
	   }*/
	   
	   private Triplet<String,ArrayList<String>, ArrayList<String>>  getCurrentMetabolicGeneState(ArrayList<String> geneids){
		   
		   ArrayList<String> geneson=new ArrayList<>();
		   ArrayList<String> genesoff=new ArrayList<>();
		   
		   StringBuilder str=new StringBuilder();
		   str.append("[ ");
		   
		   if(geneids!=null){
			   for (String id : geneids) {
				   RegulatoryGeneticConditions geneconds=(RegulatoryGeneticConditions) currentsimulationresults.getGeneticConditions();
				   if(geneconds.getMetabolicGenesKnockoutList().contains(id)){
					   str.append(id+":OFF"+" ");
					   genesoff.add(id);
				   }
				   else{
					   str.append(id+":ON"+" ");
					   geneson.add(id);
				   }
			   }
		   }
		   str.append("]\n");
		   return new Triplet<String, ArrayList<String>, ArrayList<String>>(str.toString(), geneson, genesoff);
	   }
	   
	   
	   private boolean canMetabolicReactionBeActive(String reactionid, ArrayList<String> genesoff){
		   
		   GeneReactionRule react=model.getGeneReactionRule(reactionid);

		   if(react!=null && react.getRule()!=null){
			   ArrayList<String> totalgenes=TreeUtils.withdrawVariablesInRule(react.getRule());
			   HashSet<String> filterdup=new HashSet<>(totalgenes);
		   
			   Environment<IValue> environment = new Environment<IValue>();
			   for (String id : filterdup) {
				   if(genesoff.contains(id))
				 	 environment.associate(id, new BooleanValue(false));
				   else
					   environment.associate(id, new BooleanValue(true));
			   }
		   
			   return (Boolean)react.getRule().evaluate(environment).getValue();
		   }
		   return true;
	   }
	   
	   
	   private boolean checkRegulatoryRuleResult(String geneid, IndexedHashMap<String, Boolean> rulelements){
		   
		   RegulatoryRule regrule=model.getRegulatoryNetwork().getRegulatoryRuleToRegulatorId(geneid);

		   if(regrule!=null && regrule.getRule()!=null){
			   Environment<IValue> environment = new Environment<IValue>();
			   for (int i = 0; i < rulelements.size(); i++) {
				    environment.associate(rulelements.getKeyAt(i), new BooleanValue(rulelements.getValueAt(i)));
			   }
		   
			   return (Boolean)regrule.getBooleanRule().evaluate(environment).getValue();
		   }
		   return true;
	   }
	   
	   
	   protected String getInteractionGeneReactionsOfBiomassCofactors(IndexedHashMap<String, ArrayList<String>> cofactorreactions, String inputgeneid,String addedrule) throws Exception{
			
		    StringBuilder str=new StringBuilder();
			
			IndexedHashMap<String, IndexedHashMap<String, ArrayList<String>>> reactionscanblockcofactors=new IndexedHashMap<>();
			
			for (int i = 0; i < cofactorreactions.size(); i++) {
                String cofactorname=cofactorreactions.getKeyAt(i);
				//str.append("################### "+ cofactorname+" ##################\n\n");
				ArrayList<String> zerofluxreaction=cofactorreactions.getValueAt(i);
				IndexedHashMap<String, ArrayList<String>> reactionblockcofactor=new IndexedHashMap<>();
				
				for (int j = 0; j < zerofluxreaction.size(); j++) {
					String rid=zerofluxreaction.get(j);
					
					ArrayList<String> metabgenes=genereactionanalyzer.getGenesRegulateReaction(rid);
					//if(metabgenes!=null)
						//totalmetabgenes.addAll(metabgenes);
					
					
					Triplet<String, ArrayList<String>, ArrayList<String>> metabgenestates=getCurrentMetabolicGeneState(metabgenes);
					boolean reactioncanactivated=canMetabolicReactionBeActive(rid, metabgenestates.getValue2());
					if(!reactioncanactivated)
						reactionblockcofactor.put(rid, metabgenestates.getValue2());
					//str.append("Reaction ID With zero flux: "+rid+" Reaction can be active: "+reactioncanactivated+"   Metabolic Genes that regulate Reaction: "+metabgenestates.getValue0()+"\n");
					
				}
				
				reactionscanblockcofactors.put(cofactorname, reactionblockcofactor);
				//str.append("\n\n");	
			}
			
			//System.out.println("Reaction Can Block: "+reactionscanblockcofactors);
			
	       HashSet<String> essentialgenesfalsestate=new HashSet<>();
	        
	        IndexedHashMap<String, IndexedHashMap<String, ArrayList<String>>> geneinhibitsreactiontocofactor=new IndexedHashMap<>();
	        
			
			for (int i = 0; i < reactionscanblockcofactors.size(); i++) {
				String cofactor=reactionscanblockcofactors.getKeyAt(i);
				
				IndexedHashMap<String, ArrayList<String>> reactions=reactionscanblockcofactors.get(cofactor);
				if(reactions.size()>0){
					//str.append("============================= biomass cofactor: "+cofactor+", can be affected by reactions there are always off ===================================\n");
				
					for (int j = 0; j < reactions.size(); j++) {
					       String r=reactions.getKeyAt(j);
					       ArrayList<String> genesoff=reactions.get(r);
					       essentialgenesfalsestate.addAll(genesoff);
					       //str.append("Reaction: "+r+" can be blocked by genes that are off: "+genesoff+"\n");
					       for (int k = 0; k < genesoff.size(); k++) {
							   String geneid=genesoff.get(k);
					    	   cacheFalseGeneStateInformation(geneinhibitsreactiontocofactor,geneid,r,cofactor);
						   }
					}
					//str.append("\n\n");
				}
			}
			
			essencialgenesoff.put(inputgeneid, essentialgenesfalsestate);
			IndexedHashMap<String, RegulatoryRule> modelrules=model.getRegulatoryNetwork().getRegulatoryRules();
			String tfid=modelrules.get(inputgeneid).getRuleId();
			String essencialgenereport="Gene identifier= "+inputgeneid+", Gene Name= "+RegStringUtils.convertTFnameToGeneName(tfid)+", Associated TF= "+tfid+", Rule= "+addedrule+", Essencial Genes that are OFF: "+essentialgenesfalsestate+"\n";
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\t"+essencialgenereport);
			problematicRulesWithFaultEssencialGenesreport.put(inputgeneid, essencialgenereport);
			
			
			//ArrayList<String> falsegenestate=new ArrayList<>(essentialgenesfalsestate);
			//System.out.println("Gene Inhib: "+geneinhibitsreactiontocofactor);
			
			IRegulatoryNetwork regmodel=model.getRegulatoryNetwork();
			
			str.append("\n\n\t============================================================================================================\n");
			str.append("\t====== Genes that are affecting directly an essential reaction of a cofactor for Growth of Biomass =======\n\n");
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\n\n\t============================================================================================================\n");
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\t====== Genes that are affecting directly an essential reaction of a cofactor for Growth of Biomass =======\n\n");
			
			
			for (int i = 0; i < geneinhibitsreactiontocofactor.size(); i++) {
				String geneid=geneinhibitsreactiontocofactor.getKeyAt(i);
				str.append("\t=:=:=:=:=:=  Regulatory Model Gene: "+geneid+" =:=:=:=:=:=\n");
				LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\t=:=:=:=:=:=  Regulatory Model Gene: "+geneid+" =:=:=:=:=:=\n");
				
				if(showgenekegginformation){
					String kegginfo=getKeggInformation(geneid);
					str.append("\t"+kegginfo);
					LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\t"+kegginfo);
				}
				IndexedHashMap<String, ArrayList<String>> assocreactions=geneinhibitsreactiontocofactor.get(geneid);
				for (int j = 0; j < assocreactions.size(); j++) {
					str.append("\tAffected reaction: "+assocreactions.getKeyAt(j)+ " affected biomass cofactors: "+assocreactions.getValueAt(j)+"\n");
					LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\tAffected reaction: "+assocreactions.getKeyAt(j)+ " affected biomass cofactors: "+assocreactions.getValueAt(j)+"\n");
				}
				
				String rulegene=regmodel.getRegulatoryRuleToRegulatorId(geneid).getRule();
				str.append("\tRegulatory Rule of gene: "+rulegene+"\n");
				LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\tRegulatory Rule of gene: "+rulegene+"\n");
				IndexedHashMap<String, Boolean> rulelements=getstateOFRuleComponentsForProblematicRule(regmodel.getRegulatoryRuleToRegulatorId(geneid).getRule());
				for (int j = 0; j < rulelements.size(); j++) {
					str.append("\t"+rulelements.getKeyAt(j)+" --> "+rulelements.getValueAt(j)+"\n");
					LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\t"+rulelements.getKeyAt(j)+" --> "+rulelements.getValueAt(j)+"\n");
					if(j<rulelements.size()-1){
						str.append("\n");
						LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\n");
					}
					else if(j==rulelements.size())
						str.append("\n\n");
					LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\n\n");
				}
				
				boolean regulatoryrulestate=checkRegulatoryRuleResult(geneid,rulelements);
				str.append("\tOutput of the regulatory rule, using the boolean state of their elements, presented above: "+regulatoryrulestate+"\n");
				LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\tOutput of the regulatory rule, using the boolean state of their elements, presented above: "+regulatoryrulestate+"\n");
				str.append("\n\n\n");
				LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("\n\n\n");
				
			}
			
		
			
		/*	str.append("\n\n########################################################################################################\n");
			str.append("############################### End of analysis of Growth impact of added rule: ###########################\n");
			str.append(" ######## "+ addedrule+"\n");
			str.append("########################################################################################################\n\n");*/
			
			return str.toString();
		}
	   
	   private String getKeggInformation(String geneid){
		   StringBuilder str=new StringBuilder();
		   if(metabolicgenesinfo.containsKey(geneid)){
				KeggGeneInfoContainer info=metabolicgenesinfo.get(geneid);
				str.append("Identifier: "+info.getAccessionNumber()+"\t Gene Name: "+info.getGenename()+
						"\t Transcriptional Factor: "+info.getTfname()+"\t Definition: "+info.getDefinition()+
						"\t Orthology: "+info.getOrthology()+"\n");
			}
			else{
				IndexedHashMap<String, KeggGeneInfoContainer> kegginfo = null;
				try {
					kegginfo =new IndexedHashMap<>();
					kegginfo.putAll(EcoliGeneInfoKeggConnector.getEcoliGenesInformation(geneid, ECOLIGENEIDENTIFIER.NAME));    //EcoliGeneInfoQueryProcessor.getGeneKeggInformation(geneid);
				} catch (IOException | WebConnectionException e) {
					e.printStackTrace();
				}
				if(kegginfo.size()>0){
					metabolicgenesinfo.putAll(kegginfo);
					KeggGeneInfoContainer info=kegginfo.get(geneid);
					str.append("Identifier: "+info.getAccessionNumber()+"\t Gene Name: "+info.getGenename()+
							"\t Transcriptional Factor: "+info.getTfname()+"\t Definition: "+info.getDefinition()+
							"\t Orthology: "+info.getOrthology()+"\n");
				}
			}
		   return str.toString();
	   }
	   
	   
	   public void cacheFalseGeneStateInformation(IndexedHashMap<String, IndexedHashMap<String, ArrayList<String>>> geneinhibitsreactiontocofactor, String geneid, String reactionid, String cofactorid){
		   
		   if(geneinhibitsreactiontocofactor.containsKey(geneid)){
			   IndexedHashMap<String, ArrayList<String>> reactrelate=geneinhibitsreactiontocofactor.get(geneid);
			   if(reactrelate.containsKey(reactionid))
				   reactrelate.get(reactionid).add(cofactorid);
			   else{
				   ArrayList<String> cofactorlist=new ArrayList<>();
				   cofactorlist.add(cofactorid);
				   reactrelate.put(reactionid, cofactorlist);
			   }
		   }
		   else{
			   IndexedHashMap<String, ArrayList<String>> reactlist=new IndexedHashMap<>();
			   ArrayList<String> cofactorlist=new ArrayList<>();
			   cofactorlist.add(cofactorid);
			   reactlist.put(reactionid, cofactorlist);
			   geneinhibitsreactiontocofactor.put(geneid, reactlist);
		   }
	   }
	   
	   protected IndexedHashMap<String, Boolean> getstateOFRuleComponentsForProblematicRule(String rule) throws Exception{
			IndexedHashMap<String, Boolean> elemsres=new IndexedHashMap<>();
			
			if(rule!=null && !rule.isEmpty()){
			
			
				HashSet<String> elements=getRuleElements(rule);
			
				IRegulatoryNetwork regmodel=model.getRegulatoryNetwork();
				IndexedHashMap<String, String> ruleid2geneid=null;
				if(regmodel.getMapGeneId2RuleId()!=null)
					ruleid2geneid=(IndexedHashMap<String, String>) MTUMapUtils.invertMap(regmodel.getMapGeneId2RuleId());
			
				for (String elem : elements) {
				  
					RegulatoryVariable var=RegulatoryVariable.setupRegulatoryVariableWithSign(elem, null);
                 
					if(regmodel.getVariableNamesInNetwork().contains(var.getId())){
						boolean st=currentsimulationresults.getRegulatorySimulationResults().getInitialRegulatoryState().getBooleanStateForComponentID(var.getId());
						elemsres.put(elem, st); 
					}
					else{
						if(ruleid2geneid!=null && ruleid2geneid.containsKey(elem)){
							if(ruleid2geneid.containsKey(elem)){
								boolean state=currentsimulationresults.getGeneStateInAttractors(ruleid2geneid.get(elem));
								elemsres.put(elem, state);
							}
						}
						else if(regmodel.getMapGeneId2RuleId()!=null && regmodel.getMapGeneId2RuleId().containsKey(elem)){
							boolean state=currentsimulationresults.getGeneStateInAttractors(elem);
							elemsres.put(elem, state);
						}
                	 	
					}

				}
			}
			
			return elemsres;
		}
	   
	   protected HashSet<String> getRuleElements(String rule){
			HashSet<String> res=new HashSet<>();
			String pattern="(\\w+(([-,_]\\w+)*(\\(\\w\\))*[<>](-)*\\d+(\\.\\d+)*)*)";
			Pattern patt = Pattern.compile(pattern);
			Matcher match = patt.matcher(rule);
			while(match.find()){
				String oc = match.group();
				if(!oc.matches("or|OR|and|AND|not|NOT")){
				    res.add(oc);
				}
			}
			return res;
		}
	   
	   public void writeReportToTxtFile(String filepath, boolean writepossibleproblems) throws IOException{
		   StringBuilder str=new StringBuilder();
		   
		  
		   
		   str.append("############################################################################################\n");
		   str.append("##################### Rules that not affect the growth of the biomass ##########################\n");
		   str.append("############################################################################################\n");
		   for (int i = 0; i < ruleswithoutproblemsreport.size(); i++) {
			  str.append(ruleswithoutproblemsreport.getValueAt(i));
		   }
		   
		   if(highbiomassimpactrulereport.size()>0){
			   str.append("\n\n\n############################################################################################\n");
			   str.append("################ Rules that have high influence in the growth of the biomass ####################\n");
			   str.append("############################################################################################\n");
			   
			   for (int i = 0; i < highbiomassimpactrulereport.size(); i++) {
				  str.append(highbiomassimpactrulereport.getValueAt(i));
			   }
			   
			   
		   }
		   
		   if(problematicRulesreport.size()>0){
			   str.append("\n\n\n############################################################################################\n");
			   str.append("##################### Rules that inhibit the growth of the biomass ##########################\n");
			   str.append("############################################################################################\n");
			   
			  
			   for (int i = 0; i < problematicRulesWithFaultEssencialGenesreport.size(); i++) {
				   str.append(problematicRulesWithFaultEssencialGenesreport.getValueAt(i));  
			   }
			   
			   
			   str.append("\n\n\n############################################################################################\n");
			   str.append("##################### Possible causes to the inhibition of the biomass growth ##########################\n");
			   str.append("############################################################################################\n");
			   
		   
		   		for (int i = 0; i < problematicRulesreport.size(); i++) {
			        String geneid=problematicRulesreport.getKeyAt(i);
			        str.append("\n\n\n\n#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#==#=#=#=#=#=#=#=#=#=#=#==#=#=#=#=#=#=#=#=#=#=#\n");
			        str.append(problematicRulesreport.getValueAt(i)+"\n\n");
		   			if(writepossibleproblems && inhibitbiomasscofactorsreport.size()>0 && inhibitbiomasscofactorsreport.containsKey(geneid)){
		   				str.append(inhibitbiomasscofactorsreport.get(geneid));
		   			}
		   		}
		   }
		   
		   MTUWriterUtils.writeStringWithFileChannel(str.toString(), filepath,0);
		   
	   }
	   
	   public void WriteReportToTxtFile(String dirpath,boolean writepossibleproblems) throws IOException{
		   
		   String filename=getBaseFileName(null, dirpath)+".txt";
		   String filepath=FilenameUtils.concat(dirpath, filename);
		   writeReportToTxtFile(filepath, writepossibleproblems);
		   
	   }
	   
	   
	   private String getBaseFileName(String prefix,String dirpath){
		   String filename=null;
		   if(envconds!=null && !envconds.getId().isEmpty()){
			   if(prefix!=null)
				   filename=prefix+"_Rule_analysis_SM@"+simulationoptions.getSimulationMethod().getName()+"_EC@"+envconds.getId();
			   else
				   filename="Rule_analysis_SM@"+simulationoptions.getSimulationMethod().getName()+"_EC@"+envconds.getId();
		   }
		   else{
			   if(prefix!=null)
				   filename=prefix+"_Rule_analysisSM@"+simulationoptions.getSimulationMethod().getName();
			   else
				   filename="Rule_analysisSM@"+simulationoptions.getSimulationMethod().getName();
		   }
		   return filename;
	   }
	   
	   public void WriteRegulatoryModelWithoutProblematicRule(String dirpath){
		   StringBuilder str=new StringBuilder();


		   IndexedHashMap<String, RegulatoryRule> modelrules=model.getRegulatoryNetwork().getRegulatoryRules(); 
		   for (int i = 0; i < ruleswithoutproblems.size(); i++) {
			   String geneid=ruleswithoutproblems.getKeyAt(i);
			   String rule=ruleswithoutproblems.getValueAt(i).getValue1();
			   String ruleid=modelrules.get(geneid).getRuleId();
			   String genename=RegStringUtils.convertTFnameToGeneName(ruleid);
			   str.append(geneid+";"+genename+";"+ruleid+";"+rule+"\n");
		   }

		   String filepath=FilenameUtils.concat(dirpath, getBaseFileName("Working_RegulatoryModel", dirpath)+".csv");
		   try {
			   String savetodir=FilenameUtils.getFullPath(filepath);
			   MTUDirUtils.checkDirectory(savetodir);

			   MTUWriterUtils.writeStringWithFileChannel(str.toString(),filepath, 0);
		   } catch (IOException e) {
			   LogMessageCenter.getLogger().addCriticalErrorMessage("Error saving model: ", e);
		   }
	   }
	   
	   
	  public void WriteRegulatoryModelWithoutProblematicRuleToSBMLQual(String dirpath) throws Exception{

		  IRegulatoryNetwork regnet= model.getRegulatoryNetwork();
		  String filepath=FilenameUtils.concat(dirpath, getBaseFileName("Sbmlqual_Working_RegulatoryModel", dirpath)+".xml");
		  String savetodir=FilenameUtils.getFullPath(filepath);
		   MTUDirUtils.checkDirectory(savetodir);
		   SBMLqualRegulatoryModelExporter.export(regnet, filepath);
	   }
	  
	  public void WriteRegulatoryModelWithoutProblematicRuleToCSVFormat(String dirpath) throws Exception{

		  CSVRegulatoryModelExporter exporter=new CSVRegulatoryModelExporter(model.getRegulatoryNetwork());
		  String filepath=FilenameUtils.concat(dirpath, getBaseFileName("CSV_Working_RegulatoryModel", dirpath)+".xml");
		  String savetodir=FilenameUtils.getFullPath(filepath);
		   MTUDirUtils.checkDirectory(savetodir);
		  exporter.export(filepath);
	   }
	  
	  public void writeProblematicRulesToCSVFile(String dirpath) throws IOException {
		  StringBuilder str=new StringBuilder();
		  if(problematicRulesreport.size()>0){
			   str.append("\n\n\n############################################################################################\n");
			   str.append("##################### Rules that inhibit the growth of the biomass ##########################\n");
			   str.append("############################################################################################\n");
			   
			  
			   for (int i = 0; i < problematicRulesWithFaultEssencialGenesreport.size(); i++) {
				   str.append(problematicRulesWithFaultEssencialGenesreport.getValueAt(i));  
			   }
			   
			   
			   str.append("\n\n\n############################################################################################\n");
			   str.append("##################### Possible causes to the inhibition of the biomass growth ##########################\n");
			   str.append("############################################################################################\n");
			   
		   
		   		for (int i = 0; i < problematicRulesreport.size(); i++) {
			        //String geneid=problematicRulesreport.getKeyAt(i);
			        str.append(problematicRulesreport.getValueAt(i)+"\n\n");

		   		}
		   }
		  
		  String filepath=FilenameUtils.concat(dirpath, "Rules_that_can_cause_problem_in_growth.csv");
		  MTUWriterUtils.writeDataTofile(filepath, str.toString());
	  }

}
