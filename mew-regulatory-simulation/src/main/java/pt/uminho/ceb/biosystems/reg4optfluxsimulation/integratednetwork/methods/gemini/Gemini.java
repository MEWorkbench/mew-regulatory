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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.gemini;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.javatuples.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pt.ornrocha.collections.MTUMapUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.ornrocha.mathutils.MTUMathUtils;
import pt.ornrocha.mathutils.MTUStatisticsUtils;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.core.model.components.GeneReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.Reaction;
import pt.uminho.ceb.biosystems.mew.core.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.core.simulation.formulations.exceptions.ManagerExceptionUtils;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatorySimulationProperties;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.PromSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.prom.Prom;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.prom.PromSimulationResult;

public class Gemini extends Prom{

	
	private LinkedHashMap<String, ArrayList<String>> currentreactionaffectedbygenesaffectedbyTF;

	private LinkedHashMap<String, ArrayList<Integer>> interactionsremovedforTF;
	private LinkedHashMap<String,Boolean> phenotypeviable;
	private GeminiMetricType metric=GeminiMetricType.DEFAULT;
	private TypePhenotypeKnockout phenotypeknockoutthreshold=TypePhenotypeKnockout.LETHAL;
	private double optimalthreshold=0.05;
	
	
	protected LinkedHashMap<String, HashMap<String, ReactionConstraint>> GeminiPromFluxResultsForTFs=new LinkedHashMap<>();
	protected LinkedHashMap<String, Double> GeminipromSimulatedBiomassWithTF=new LinkedHashMap<>();
	protected LinkedHashMap<String, FluxValueMap> GeminipromSimulatedFluxValuesWithTF=new LinkedHashMap<>();
	protected LinkedHashMap<String, Double> GeminipromGrowthSimulationWithTFEffect=new LinkedHashMap<>();
	protected LinkedHashMap<String, FluxValueMap> GeminipromGrowthSimulationFluxValuesWithTFEffect=new LinkedHashMap<>();
	protected LinkedHashMap<String, LinkedHashMap<String, Boolean>> Geminireactionstatesinfluencedbytf=new LinkedHashMap<>();
	
	
	public Gemini(ISteadyStateModel model) {
		super(model);
	}
	
	@Override
	protected void initProperties(){
		super.initProperties();
		optionalProperties.add(RegulatorySimulationProperties.GEMINIPHENOTYPEVIABILITY);
		optionalProperties.add(RegulatorySimulationProperties.GEMINIPHENOTYPEKNOCKOUTTYPE);
		optionalProperties.add(RegulatorySimulationProperties.GEMINIMETRICTYPE);
	}
	
	
/*	protected void setCurrentTask(String key,String status) {
		if(!key.toLowerCase().contains("key"))
			key="task_"+key;
		changesupport.firePropertyChange(key, null, status);
	}*/
	
	
	private LinkedHashMap<String,Boolean> getPhenotypeViability(){
		LinkedHashMap<String,Boolean> viability=null;
		try {
			viability=ManagerExceptionUtils.testCast(properties, LinkedHashMap.class,RegulatorySimulationProperties.GEMINIPHENOTYPEVIABILITY,true);
		} catch (Exception e) {
			LogMessageCenter.getLogger().toClass(getClass()).addErrorMessage("Phenotype viability data is null, it will be considered all TFs allows viable phenotypes");
		}
		
		if(viability==null) {

			 Set<String> regulators=regulatortotargetsmap.keySet();
			 viability=new LinkedHashMap<>(regulators.size());
			 for (String id : regulators) {
				 viability.put(id, true);
			}
		}
		
       return viability;
	}
	
	
	private double getPhenotypeOptimalTreshold() {
		TypePhenotypeKnockout optimalthresholdtype=null;
		try {
			optimalthresholdtype=ManagerExceptionUtils.testCast(properties, TypePhenotypeKnockout.class,RegulatorySimulationProperties.GEMINIPHENOTYPEKNOCKOUTTYPE,true);
		} catch (Exception e) {
			
		}
		
		if(optimalthresholdtype==null)
			optimalthresholdtype=TypePhenotypeKnockout.LETHAL;
		
		this.phenotypeknockoutthreshold=optimalthresholdtype;
		return optimalthresholdtype.getOtpimalThreshold();
	}
	
	private GeminiMetricType getTypeOfMetric() {
		GeminiMetricType metric=null;
		
		try {
			metric=ManagerExceptionUtils.testCast(properties, GeminiMetricType.class,RegulatorySimulationProperties.GEMINIMETRICTYPE,true);
		} catch (Exception e) {
			
		}
		if(metric==null)
			metric=GeminiMetricType.DEFAULT;
		
		return metric;
		
	}
	
	
	
	public void printRemovedInterations() {
		
		int ninteraction=0; 
		
		StringBuilder str=new StringBuilder();
		for (String tfid : interactionsremovedforTF.keySet()) {
			ArrayList<Integer> assoc=interactionsremovedforTF.get(tfid);
			ninteraction=ninteraction+assoc.size();
			
			ArrayList<String> genenames=getTargetNamesFromIndexes(ArrayUtils.toPrimitive(assoc.toArray(new Integer[assoc.size()])));
			
			for (int i = 0; i < genenames.size(); i++) {
				str.append(tfid+"--"+genenames.get(i)+"\n");
			}
		}
		
		//System.out.println(str.toString());
		//System.out.println("\n\nnumber removed interactions: "+ninteraction+"\n ");
		
	}
	
	
	

	protected void run() throws Exception{

		addPropertyNameToProgress("promstatus");
		addPropertyNameToProgress("promprogress");
		setCurrentProgress("promprogress", 0);


		optimalthreshold=getPhenotypeOptimalTreshold();
		phenotypeviable=getPhenotypeViability();
		metric=getTypeOfMetric();
		interactionsremovedforTF=new LinkedHashMap<>();

		ISteadyStateGeneReactionModel modelcast=(ISteadyStateGeneReactionModel) model;

		LinkedHashMap<String, Double> vm=new LinkedHashMap<>(wildtyperesults.getFluxValues().size());
		for (String vf : wildtyperesults.getFluxValues().keySet()) {
			vm.put(vf, 0.0);
		}

		HashMap<String, Double> weights11 = new HashMap<String, Double>();
		weights11.put(getBiomassID(), 1.0);

		int numberreactions=model.getNumberOfReactions();
		IndexedHashMap<String, Gene> metabolicgenes=modelcast.getGenes();

		for (int i = 0; i < tfnames.size() && execute(); i++) {
			String tf=tfnames.get(i);
			setCurrentStatus("promstatus", "Analysing regulator: "+tf);

			boolean belongstometabolicgenes=metabolicgenes.containsKey(tf);

			currentreactionaffectedbygenesaffectedbyTF=new LinkedHashMap<>();

			ArrayList<Integer> removedInterationsforTF=new ArrayList<>();


			resetVariableParameters();

			int[] targetindexes=getIndexesOfTF(tf);
			ArrayList<String> genesaffectedbytf=getTargetNamesFromIndexes(targetindexes);

			ArrayList<Integer> controlreactionsaffectedbytf=new ArrayList<>();
			ArrayList<Boolean> controlconstrainedReactions=new ArrayList<>();

			Multimap<String, Integer> mapgenetoreaction=ArrayListMultimap.create();


			for (int j = 0; j < genesaffectedbytf.size() && execute(); j++) {
				String genename=genesaffectedbytf.get(j);

				Double geneprobability=null;


				if(mapIndexTFGeneInteractionToItsProbalility!=null && mapIndexTFGeneInteractionToItsProbalility.containsKey(targetindexes[j]))
					geneprobability=mapIndexTFGeneInteractionToItsProbalility.get(targetindexes[j]);

				ArrayList<Integer> controlduplicatesamereactionforgenename=new ArrayList<>();
				ArrayList<String> affectedreactionsbygene=modelcast.getReactionsInfluencedByGene(genename);


				if(affectedreactionsbygene!=null){
					for (int k = 0; k < affectedreactionsbygene.size() && execute(); k++) {
						int reactindex=model.getReactionIndex(affectedreactionsbygene.get(k));


						Reaction currentreaction=model.getReaction(reactindex);
						String currentreactionname=model.getReactionId(reactindex);
						ReactionConstraint currentreactionconstraints=null;
						if(getEnvironmentalConditions()!=null && getEnvironmentalConditions().containsKey(currentreactionname)){
							currentreactionconstraints=getEnvironmentalConditions().get(currentreactionname);
						}
						else
							currentreactionconstraints=currentreaction.getConstraints();

						if(belongstometabolicgenes){
							if(currentreaction.isReversible()){
								tempreactionlimits.get(currentreactionname).setLowerLimit(-thresholdlimit);
								tempreactionlimits.get(currentreactionname).setUpperLimit(thresholdlimit);
							}
							else
								tempreactionlimits.get(currentreactionname).setLowerLimit(-thresholdlimit);
						}

						if(!controlduplicatesamereactionforgenename.contains(reactindex)){
							controlreactionsaffectedbytf.add(reactindex);
							mapgenetoreaction.put(genename, reactindex);
							controlduplicatesamereactionforgenename.add(reactindex);

							GeneReactionRule rule=modelcast.getGeneReactionRule(model.getReactionId(reactindex));
							boolean istoconstrain=constrainReaction(genesaffectedbytf, rule);
							controlconstrainedReactions.add(istoconstrain);

							if(istoconstrain && geneprobability!=null){

								if(currentreactionaffectedbygenesaffectedbyTF.containsKey(currentreactionname)) {
									currentreactionaffectedbygenesaffectedbyTF.get(currentreactionname).add(genename);
								}
								else {
									ArrayList<String> genesofreaction=new ArrayList<>();
									genesofreaction.add(genename);
									currentreactionaffectedbygenesaffectedbyTF.put(currentreactionname, genesofreaction);
								}



								if(geneprobability<1){

									double wildtypeflux=getValueWithThreshold(wildtyperesults.getFluxValues().get(currentreactionname));

									if(geneprobability!=0.0){

										Pair<Double, Double> currentreactionvariability=getFluxVariabilityAnalysisForReactionId(currentreactionname);

										double[] fluxvalues= new double[]{currentreactionvariability.getValue0(), currentreactionvariability.getValue1(), wildtypeflux};

										if(wildtypeflux<0){
											//vm.put(currentreactionname, VMThreshold(Collections.min(Arrays.asList(ArrayUtils.toObject(fluxvalues)))));
											vm.put(currentreactionname,Collections.min(Arrays.asList(ArrayUtils.toObject(fluxvalues))));
										}
										else if(wildtypeflux>0){
											//vm.put(currentreactionname, VMThreshold(Collections.max(Arrays.asList(ArrayUtils.toObject(fluxvalues)))));
											vm.put(currentreactionname, Collections.max(Arrays.asList(ArrayUtils.toObject(fluxvalues))));
										}
										else{
											fluxvalues= new double[]{Math.abs(currentreactionvariability.getValue0()), Math.abs(currentreactionvariability.getValue1()), Math.abs(wildtypeflux)};
											//vm.put(currentreactionname, VMThreshold(Collections.max(Arrays.asList(ArrayUtils.toObject(fluxvalues)))));
											vm.put(currentreactionname, Collections.max(Arrays.asList(ArrayUtils.toObject(fluxvalues))));
										}

									}


									double xx=vm.get(currentreactionname)*geneprobability;

									if(wildtypeflux<0){
										double[] temvalues= new double[]{currentreactionconstraints.getLowerLimit(), xx, tempreactionlimits.get(currentreactionname).getLowerLimit()};
										double temmax=Collections.max(Arrays.asList(ArrayUtils.toObject(temvalues)));
										tempreactionlimits.get(currentreactionname).setLowerLimit(Math.min(temmax, -thresholdlimit));
										String varname=indexToIdVarMapings.get(numberreactions+reactindex);
										newproblemreactionconstraints.get(varname).setUpperLimit(1000);
										double tempweight11=(-1*penaltykappa/Math.abs(vm.get(currentreactionname)))*Math.abs(initsolution.getOFvalue());

										double vv=Math.max(Math.abs(vm.get(currentreactionname)), mthreshold);
										double vb=(-penaltykappa*Math.abs(initsolution.getOFvalue()))/Math.abs(vv);

										weights11.put(varname, Math.min(vb, tempweight11));
										// System.out.println("Flux less 0  reaction:"+currentreactionname+"  weigth: "+tempweight11+ " vv: "+vv+ " vb: "+vb);
									}

									else if(wildtypeflux>0){
										double[] temvalues= new double[]{currentreactionconstraints.getUpperLimit(), xx, tempreactionlimits.get(currentreactionname).getUpperLimit()};
										double temmin=Collections.min(Arrays.asList(ArrayUtils.toObject(temvalues)));
										tempreactionlimits.get(currentreactionname).setUpperLimit(Math.max(temmin, thresholdlimit));
										String varname=indexToIdVarMapings.get(numberreactions*2+reactindex);
										newproblemreactionconstraints.get(varname).setUpperLimit(1000);
										double vv=Math.max(Math.abs(vm.get(currentreactionname)), mthreshold);
										double vb=(-penaltykappa*Math.abs(initsolution.getOFvalue()))/Math.abs(vv);

										double tempweight=0.0;
										if(weights11.containsKey(varname)){
											tempweight=weights11.get(varname);
											// weights11.put(varname, Math.min(tempweight, vb));
										}
										/*else{
			    					  weights11.put(varname, Math.min(0.0, vb));
			    				  }*/
										weights11.put(varname, Math.min(tempweight, vb));
										// System.out.println("Flux upper 0  reaction:"+currentreactionname+"  weigth: "+tempweight+ " vv: "+vv+ " vb: "+vb);  
									}
								} 
							}  
						}
					}
				}
			}


			LinkedHashMap<String, Boolean> reactstate=new LinkedHashMap<>();


			ArrayList<String> reactionsconstrained=new ArrayList<>();
			for (int j = 0; j < controlreactionsaffectedbytf.size(); j++) {
				reactstate.put(model.getReactionId(controlreactionsaffectedbytf.get(j)), controlconstrainedReactions.get(j));
				if(controlconstrainedReactions.get(j))
					reactionsconstrained.add(model.getReactionId(controlreactionsaffectedbytf.get(j)));
			}


			reactionstatesinfluencedbytf.put(tf, reactstate);

			setObjectiveFunction(weights11);
			SteadyStateSimulationResult currentsolution=buildCurrentProblemAndSimulate();




			LPSolutionType typesolution=currentsolution.getSolutionType();
			FluxValueMap currentfluxvalues=currentsolution.getFluxValues();
			double biomasscoef=currentfluxvalues.get(getBiomassID());
			double objfunctval=currentsolution.getOFvalue();

			FluxValueMap lastfluxvalues=makeFluxValuesCopy(currentfluxvalues);



			boolean growthisnotvalid=(objfunctval < wildtyperesults.getOFvalue()*optimalthreshold); 

			if(phenotypeviable.containsKey(tf) && phenotypeviable.get(tf) && execute()) {

				if(growthisnotvalid) {

					tempreactionlimits.get(getBiomassID()).setLowerLimit(currentsolution.getOFvalue()*optimalthreshold);

					currentsolution=buildCurrentProblemAndSimulate();



					if(!currentsolution.getSolutionType().equals(LPSolutionType.UNDEFINED)) {
						while (!currentsolution.getSolutionType().equals(LPSolutionType.OPTIMAL)) {

							tempreactionlimits.get(getBiomassID()).setLowerLimit(currentsolution.getOFvalue()*0.99);
							currentsolution=buildCurrentProblemAndSimulate();
						}	


					}



					if(reactionsconstrained.size()>0) {


						ArrayList<String> orderedelementanalysis=new ArrayList<>();

						if(metric.equals(GeminiMetricType.RATIOTHRESHOLD)) {
							ArrayList<String> reactionwithbiggestratiodifferences=new ArrayList<>();
							ArrayList<String> reactionswithlowerratiodifferences=new ArrayList<>();

							LinkedHashMap<String, Double> differences= getDifferenceVectorFluxes(currentsolution.getFluxValues(), lastfluxvalues, reactionsconstrained);

							double [] diffvector=new double[differences.size()];
							int n=0;
							for (String r : differences.keySet()) {
								diffvector[n]=differences.get(r);
								n++;
							}

							double[] zscorearray=MTUStatisticsUtils.scaleArrayByZScores(diffvector);
							double[] diffratio=MTUMathUtils.getAbsoluteValuesArray(zscorearray);

							for (int j = 0; j < diffratio.length; j++) {
								double value=diffratio[j];
								if(value>2)
									reactionwithbiggestratiodifferences.add(reactionsconstrained.get(j));
								else
									reactionswithlowerratiodifferences.add(reactionsconstrained.get(j));
							}


							orderedelementanalysis.addAll(reactionwithbiggestratiodifferences);
							orderedelementanalysis.addAll(reactionswithlowerratiodifferences);



						}

						else if(metric.equals(GeminiMetricType.PROBABILITY)) {

							LinkedHashMap<String, Double> geneoftf=filterGeneProbabilitesAssociatedToTF(tf);
							LinkedHashMap<String, Double> sortedprobabilities=(LinkedHashMap<String, Double>) MTUMapUtils.sortMapofDoubleValues(geneoftf, true);

							for (String gid : sortedprobabilities.keySet()) {
								orderedelementanalysis.add(gid);
							}

						}

						else {

							LinkedHashMap<String, Double> differences= getDifferenceVectorFluxes(currentsolution.getFluxValues(), lastfluxvalues, reactionsconstrained);
							LinkedHashMap<String, Double> absdifferences=new LinkedHashMap<>();
							for (String reactid : differences.keySet()) {
								double val=Math.abs(differences.get(reactid));
								absdifferences.put(reactid, val);
							}

							LinkedHashMap<String, Double> sortedabsdifferences=(LinkedHashMap<String, Double>) MTUMapUtils.sortMapofDoubleValues(absdifferences, false);


							for (String rid: sortedabsdifferences.keySet()) {
								orderedelementanalysis.add(rid);
							}


						}


						ArrayList<String> tfsubset=new ArrayList<>();
						tfsubset.add(tf);

						int niter=0;




						for (String checkelement : orderedelementanalysis) {	


							ArrayList<Integer> removeinterations=null;

							if(metric.equals(GeminiMetricType.PROBABILITY)) {

								ArrayList<String> singlegenelist=new ArrayList<>();
								singlegenelist.add(checkelement);
								removeinterations=getInteractionsToRemove(tf,singlegenelist);

							}
							else {
								removeinterations=getInteractionsToRemove(tf,currentreactionaffectedbygenesaffectedbyTF.get(checkelement));

							}

							ArrayList<String> targs=getTargetNamesFromIndexes(ArrayUtils.toPrimitive(removeinterations.toArray(new Integer[removeinterations.size()])));
							setCurrentStatus("promstatus", "Checking interactions of "+tf+" to "+targs);


							removedInterationsforTF.addAll(removeinterations);


							Pair<ArrayList<String>, ArrayList<String>> updatedinteractions=getNewListRegulatorVsTargetsWithoutInteractions(removedInterationsforTF);



							PromSimulationControlCenter runauxprom=new PromSimulationControlCenter(model, getEnvironmentalConditions(), null,getSolverType(),expressiondataset , updatedinteractions.getValue0(), updatedinteractions.getValue1());
							runauxprom.setFluxVariabilityAnalysisResults(fluxvariability);
							runauxprom.setBiomassFluxID(getBiomassID());
							runauxprom.setKappaValue(penaltykappa);
							runauxprom.setExpressionDataThreshold(userdatathreshold);
							runauxprom.setSubsetRegulators(tfsubset);

							PromSimulationResult results=null;

							try {
								results= (PromSimulationResult) runauxprom.simulate();
							} catch (Exception e) {
								// if exception remove interaction an ignore solution, pass to next interaction
								System.err.println(e.getMessage());
								// TODO: handle exception
							}

							if(results!=null) {	
								Double biomasswithcurrenttf=results.getPromSimulatedBiomassWithTF().get(tf);
								if(biomasswithcurrenttf!=null) {
									//System.out.println("Iteration: "+(niter+1)+" Current biomass: "+biomasswithcurrenttf+" --> Aim: "+wildtyperesults.getOFvalue()*optimalthreshold);

									if(biomasswithcurrenttf >= (wildtyperesults.getOFvalue()*optimalthreshold)) {


										interactionsremovedforTF.put(tf, removedInterationsforTF);

										GeminipromSimulatedBiomassWithTF.put(tf, results.getPromSimulatedBiomassWithTF().get(tf));
										GeminipromSimulatedFluxValuesWithTF.put(tf, results.getPromSimulatedFluxValuesWithTF().get(tf));
										GeminipromGrowthSimulationWithTFEffect.put(tf, results.getPromGrowthSimulationWithTFKoEffect().get(tf));
										GeminipromGrowthSimulationFluxValuesWithTFEffect.put(tf, results.getPromGrowthSimulationFluxValuesWithTFKoEffect().get(tf));
										Geminireactionstatesinfluencedbytf.put(tf, results.getReactionStatesInfluencedByTFs().get(tf));
										break;
									}
								}



							}

							niter++;
						}
					}

				}
				else 
				{
					//System.out.println("predicted phenotype matches measured phenotype for TF: "+tf);

					//}


					HashMap<String, ReactionConstraint> tempcopyconstraints=copyConstraints(tempreactionlimits);

					HashMap<String, Double> tmpobj=new HashMap<>();
					tmpobj.put(getBiomassID(), 1.0);
					setObjectiveFunction(tmpobj);


					int count=0;
					boolean stop=false;
					while ((!typesolution.equals(LPSolutionType.OPTIMAL) || biomasscoef< 0.0) && !stop) {

						HashMap<String,ReactionConstraint> modreactlimits=new HashMap<>();


						for (String reactid: tempreactionlimits.keySet()) {

							ReactionConstraint c=tempreactionlimits.get(reactid);
							double lbm=c.getLowerLimit();
							double ubm=c.getUpperLimit();

							double nlb=0.0;
							double nub=0.0;

							double modellb=0.0;
							double modelub=0.0;
							if(getEnvironmentalConditions()!=null && getEnvironmentalConditions().containsKey(reactid)){
								modellb=getEnvironmentalConditions().get(reactid).getLowerLimit();
								modelub=getEnvironmentalConditions().get(reactid).getUpperLimit();
							}
							else{
								modellb=model.getReactionConstraint(reactid).getLowerLimit();
								modelub=model.getReactionConstraint(reactid).getUpperLimit();
							}


							if(lbm!=modellb)
								nlb=lbm-0.001;
							else
								nlb=lbm;

							if(ubm!=modelub)
								nub=ubm+0.001;
							else
								nub=ubm;

							ReactionConstraint newconst=new ReactionConstraint(nlb, nub);
							modreactlimits.put(reactid, newconst);

						}

						tempreactionlimits=modreactlimits;


						currentsolution=buildCurrentProblemAndSimulate();
						typesolution=currentsolution.getSolutionType();
						biomasscoef=currentsolution.getFluxValues().get(getBiomassID());
						count++;

						if(count>2){


							SteadyStateSimulationResult simplefba=runFBAWithConditions(buildEnvironmentalCond(tempcopyconstraints), tmpobj, true);


							biomasscoef=simplefba.getOFvalue();
							currentfluxvalues.setValue(getBiomassID(), biomasscoef);

							stop=true;
						}
					}

					Geminireactionstatesinfluencedbytf.put(tf, reactionstatesinfluencedbytf.get(tf));
					//promSimulatedBiomassWithTF.put(tf, biomasscoef);
					GeminipromSimulatedBiomassWithTF.put(tf, biomasscoef);
					//promSimulatedFluxValuesWithTF.put(tf, currentfluxvalues);
					GeminipromSimulatedFluxValuesWithTF.put(tf, currentfluxvalues);

					SteadyStateSimulationResult runFBAwithko=runFBAWithConditions(buildEnvironmentalCond(tempreactionlimits), tmpobj, true);
					//System.out.println("TF: "+tf+" Biomass with tf: "+biomasscoef+" with ko: "+runFBAwithko.getOFvalue());
					//promGrowthSimulationWithTFEffect.put(tf, runFBAwithko.getOFvalue());
					GeminipromGrowthSimulationWithTFEffect.put(tf, runFBAwithko.getOFvalue());

					//promGrowthSimulationFluxValuesWithTFEffect.put(tf, runFBAwithko.getFluxValues());
					GeminipromGrowthSimulationFluxValuesWithTFEffect.put(tf, runFBAwithko.getFluxValues());
				}
			}

			float progress = ((float)i+1)/(float)tfnames.size();
			setCurrentProgress("promprogress", progress);
		}

		setCurrentStatus("promstatus", "Saving results");
		setCurrentProgress("promprogress", 100);

	}
	
	

	@Override
	protected SteadyStateSimulationResult assembleSolution(){


		LinkedHashMap<String, ArrayList<String>> mapremovedinteractions=new LinkedHashMap<>();

		for (String tfid : interactionsremovedforTF.keySet()) {
			ArrayList<Integer> interindexes=interactionsremovedforTF.get(tfid);

			ArrayList<String> geneinteractions=new ArrayList<>();
			for (int i = 0; i < interindexes.size(); i++) {
				int index=interindexes.get(i);
				geneinteractions.add(regulatedorderlist.get(index));
			}
			mapremovedinteractions.put(tfid, geneinteractions);
		}



		Multimap<String, String> newregulatortotargetsmap=ArrayListMultimap.create();

		for (String idtf: regulatortotargetsmap.keySet()) {
			ArrayList<String> assoctargets=new ArrayList<>(regulatortotargetsmap.get(idtf));

			for (int i = 0; i < assoctargets.size(); i++) {
				String geneid=assoctargets.get(i);
				if(!mapremovedinteractions.containsKey(idtf))
					newregulatortotargetsmap.put(idtf, geneid);
				else if(mapremovedinteractions.containsKey(idtf) && !mapremovedinteractions.get(idtf).contains(geneid))
					newregulatortotargetsmap.put(idtf, geneid);
			}
		}


		LinkedHashMap<String, HashMap<String, Double>> mapofprobability=new LinkedHashMap<>();

		for (int i = 0; i < regulatororderlist.size(); i++) {
			String reg=regulatororderlist.get(i);

			if(!interactionsremovedforTF.containsKey(reg) || !interactionsremovedforTF.get(reg).contains(i)) {

				if(!mapofprobability.containsKey(reg)){
					HashMap<String, Double> geneprob=new HashMap<>();
					geneprob.put(regulatedorderlist.get(i), mapIndexTFGeneInteractionToItsProbalility.get(i));
					mapofprobability.put(reg, geneprob);
				}
				else{
					mapofprobability.get(reg).put(regulatedorderlist.get(i), mapIndexTFGeneInteractionToItsProbalility.get(i));
				}
			}
		}






		return new GeminiSimulationResults(model, "GEMINI",
				newregulatortotargetsmap, 
				GeminipromSimulatedBiomassWithTF,
				GeminipromSimulatedFluxValuesWithTF,
				GeminipromGrowthSimulationWithTFEffect,
				GeminipromGrowthSimulationFluxValuesWithTFEffect,
				mapofprobability,
				Geminireactionstatesinfluencedbytf,
				mapremovedinteractions,
				expressiondataset,
				getEnvironmentalConditions(),
				penaltykappa,
				userdatathreshold,
				minpvalue,
				datathreshold,
				metric,
				phenotypeknockoutthreshold);
	}

	
	
	
	
	public static FluxValueMap makeFluxValuesCopy(FluxValueMap origfluxes) {
		
		FluxValueMap copy=new FluxValueMap();
		
		for (String fid : origfluxes.keySet()) {
			copy.setValue(fid, origfluxes.get(fid));
		}
		
		return copy;
	}
	
	
	private LinkedHashMap<String, Double> getDifferenceVectorFluxes(FluxValueMap currenfluxes, FluxValueMap lastfluxes, ArrayList<String> checkfluxesids){
		
		LinkedHashMap<String, Double> diffvector=new LinkedHashMap<>();
		for (int i = 0; i <checkfluxesids.size(); i++) {
			String id=checkfluxesids.get(i);
			double vc=currenfluxes.get(id);
			double vl=lastfluxes.get(id);
			
			double dif=vc-vl;
			diffvector.put(id, dif);
			
		}
		
		return diffvector;
	}
	
	
	private ArrayList<Integer> getInteractionsToRemove(String tfname, ArrayList<String> genestoremovelink) {
		
		ArrayList<Integer> res=new ArrayList<>();
		
		for (int i = 0; i < regulatedorderlist.size(); i++) {
			String geneid=regulatedorderlist.get(i);
			if(genestoremovelink.contains(geneid) && regulatororderlist.get(i).equals(tfname)) {
				res.add(i);
			}
		}
		return res;
	}
	
	
	
	private Pair<ArrayList<String>, ArrayList<String>> getNewListRegulatorVsTargetsWithoutInteractions(ArrayList<Integer> excludeinteractions){
		
		ArrayList<String> regulators=new ArrayList<>();
		ArrayList<String> targets=new ArrayList<>();
		
		for (int i = 0; i < regulatororderlist.size(); i++) {
			
			if(!excludeinteractions.contains(i)) {
				regulators.add(regulatororderlist.get(i));
				targets.add(regulatedorderlist.get(i));
			}
			
		}
		
		return new Pair<ArrayList<String>, ArrayList<String>>(regulators, targets);
		
	}
	
	private LinkedHashMap<String, Double> filterGeneProbabilitesAssociatedToTF(String tfname){
		int[] indexestf=getIndexesOfTF(tfname);
		ArrayList<String> indexesgenes=getTargetNamesFromIndexes(indexestf);
		
		LinkedHashMap<String, Double> res=new LinkedHashMap<>();
		
		for (int i = 0; i < indexestf.length; i++) {
			res.put(indexesgenes.get(i), mapIndexTFGeneInteractionToItsProbalility.get(indexestf[i]));
		}
		return res;
	}
	
	
	

}
