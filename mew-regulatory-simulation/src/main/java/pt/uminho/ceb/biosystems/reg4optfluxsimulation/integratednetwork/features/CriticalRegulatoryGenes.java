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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.features;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.core.model.components.EnvironmentalConditions;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.FluxValueMap;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.IntegratedSimulationMethod;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.VariablesStateContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.abstractcontrolcenter.AbstractIntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.DynamicRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.IntegratedSimulationControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.controlcenter.methodscontrolcenter.SRFBAControlCenter;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IExecutionKiller;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.IntegratedSimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.components.RFBASimulationOptionsContainer;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.features.components.StatusHandlerCriticalgenes;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.rfba.results.RFBASimulationResult;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.regulatorynetwork.components.RegulatorySimulationMethod;

public class CriticalRegulatoryGenes implements Serializable,IExecutionKiller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double MINIMAL_VALUE = 0.05;
	protected List<String> criticalGenesId = null;
	protected IIntegratedStedystateModel model;
	//protected String simulationMethod = SimulationProperties.FBA;
	protected AbstractIntegratedSimulationControlCenter center = null;
	protected EnvironmentalConditions envConditions = null;
	protected String biomassFlux;
	protected FluxValueMap wildTypeFluxes = null;
	protected double wildtypebiomassflux;
	protected String solvertype;
	protected VariablesStateContainer vars = null;
	protected boolean debug = true;
	protected int CurrentIteration = 0;
	protected boolean terminationFlag = false;
	protected StatusHandlerCriticalgenes status= null;
	protected IntegratedSimulationOptionsContainer simoptions;
	protected PropertyChangeSupport changesupport=new PropertyChangeSupport(this);

	

	public CriticalRegulatoryGenes(IIntegratedStedystateModel model,VariablesStateContainer variables, EnvironmentalConditions envConditions,IntegratedSimulationOptionsContainer simulationoptions) throws Exception 
	{
		this.model = model;		
		this.envConditions = envConditions;
        this.vars=variables;
		this.biomassFlux = ((IIntegratedStedystateModel) model).getBiomassFlux();
		this.simoptions=simulationoptions;
		configureSimulationControlCenter();

	   
		
	   computewildbiomassFlux();
		
		//wildTypeFluxes = computeWildType();
		//System.out.println("Flux biomass "+wildTypeFluxes.getValue(biomassFlux));
	}
	
	
	public void addSolutionPropertyChangeListener(PropertyChangeListener listener) {
		changesupport.addPropertyChangeListener(listener);
	}
	
	private void setCriticalGenesProgressMsg(String msg) {
		changesupport.firePropertyChange("criticalgenesprogressmsg", null, msg);
	}
	
	private void setCriticalGenesProgress(float progress) {
		changesupport.firePropertyChange("criticalgenesprogress", null, progress);
	}
	
	
	private void configureSimulationControlCenter() {



		if(simoptions.getSimulationMethod().equals(IntegratedSimulationMethod.INTEGRATEDSIMULATION)) {

			IntegratedSimulationControlCenter confcenter=null;
			if( simoptions.getRegulatorySimulationMethod().equals(RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION)) 
				confcenter=new IntegratedSimulationControlCenter(model, envConditions, null,IntegratedSimulationMethod.ASYNCHINTEGRATEDSIMULATION.toString(), true, simoptions.getSolver(), simoptions.getMetabolicSimulationMethod(), simoptions.getRegulatorySimulationMethod());
			else 
				confcenter=new IntegratedSimulationControlCenter(model, envConditions, null, true, simoptions.getSolver(), simoptions.getMetabolicSimulationMethod(), simoptions.getRegulatorySimulationMethod());

			confcenter.setComponentsBooleanInitialState(vars.getInitialStateOfAllVariables());

			if(vars.getInitialGenesOFF()!=null)
				confcenter.setGeneInitialStateAsOFF(vars.getInitialGenesOFF());

			confcenter.setMaxNumberIterationsRegulatorySimulation(simoptions.getRegulatorySimulationIterations());
			
			if(!simoptions.getRegulatorySimulationMethod().equals(RegulatorySimulationMethod.BDDASYNCHRONOUSWITHCOUPLEDMETABOLICSIMULATION))
				confcenter.stopRegulatorySimulationOnFirstAttractor(simoptions.isStopSimulationAtFirstAttractor());
			
			center=confcenter;

		}
		else if (simoptions instanceof RFBASimulationOptionsContainer) {

			RFBASimulationOptionsContainer options=(RFBASimulationOptionsContainer) simoptions;

			DynamicRFBAControlCenter confcenter =new DynamicRFBAControlCenter(model, envConditions, null, options.getSolver(), options.getInitialBiomass(), options.getInitialBiomass(), options.getNumberSteps(), options.getInitialSubstrateConcentrations());

			if(vars.getInitialGenesON()!=null)
				confcenter.setGeneGroupToStartWithTrueState(vars.getInitialGenesON());

			if(options.getUptakeReactionsToExcludeFromInitialConfiguration()!=null)
				confcenter.setUptakeReactionsToExcludeFromInitialConfiguration(options.getUptakeReactionsToExcludeFromInitialConfiguration());

			center=confcenter;
		}
		else {

			SRFBAControlCenter tmpcenter=new SRFBAControlCenter(model, envConditions, null, true, simoptions.getSolver());
			tmpcenter.setComponentsBooleanInitialState(vars.getInitialStateOfAllVariables());

			center=tmpcenter;
		}



		Map<String, Double> obj_coef = new HashMap<String, Double>();
		obj_coef.put(this.biomassFlux, 1.0);
		center.setObjectiveFunction(obj_coef);

	}

	


	public CriticalRegulatoryGenes(List<String> criticalGenesId , IIntegratedStedystateModel model){
		this.criticalGenesId = criticalGenesId;
		this.model = model;
		
	}
	
	/*private FluxValueMap computeWildType() throws Exception{

		IntegratedSimulationResult res = (IntegratedSimulationResult) center.simulate();
		//res.getFluxValues();
		FluxValueMap ret = res.getFluxValues();
		
		
		
		return ret;
	}*/
	
	/*public void run() throws Exception {
		computewildbiomassFlux();
	}*/
	
	
	
	private void computewildbiomassFlux() throws Exception{
		IntegratedSimulationMethodResult res = (IntegratedSimulationMethodResult) center.simulate();
		if(res instanceof RFBASimulationResult)
		   this.wildtypebiomassflux=((RFBASimulationResult)res).getFinalBiomass();
		else
			this.wildtypebiomassflux=res.getOFvalue();
		
	}
	
	
	public CriticalRegulatoryGenesResults identifyCriticalgenes() throws Exception
	{

		criticalGenesId = new ArrayList<String>();
		if(status!=null)
		status.setNumberOfFunctionEvaluations(model.getGenesInRegulatoryModel().size());
		
		
		ArrayList<String> allgenes =model.getAllGenes();
		
		setCriticalGenesProgressMsg("Calculating Critical Genes...");
		setCriticalGenesProgress(0);
		
		for(int i=0; i < allgenes.size(); i++)
		{   

			CurrentIteration++;
			
			float progress = (float)CurrentIteration/(float)allgenes.size();
			setCriticalGenesProgress(progress);
			int prog=Math.round(progress*100);
			setCriticalGenesProgressMsg("Calculating Critical Genes..."+prog+"%");
			
			if(status!=null)
				status.processEvaluationEvent(i);
			if(terminationFlag == false){
				String geneId = allgenes.get(i);
			if(isCriticalGene(geneId)) {
				criticalGenesId.add(geneId);
			    //setCriticalGenesProgressMsg("Gene: "+geneId+" analysed as critical");
			}
			/*else
				setCriticalGenesProgressMsg("Gene: "+geneId+" analysed as not critical");*/
			}
			
			
			
		}
		
		CriticalRegulatoryGenesResults results=new CriticalRegulatoryGenesResults((ArrayList<String>) criticalGenesId, model,simoptions);
		if(envConditions!=null)
			results.setEnvConditions(envConditions);
		
		return results;
		
	}
	
	
	
	public void setMINIMAL_VALUE(double minimal_value) {
		MINIMAL_VALUE = minimal_value;
	}



	public void setEnvConditions(EnvironmentalConditions envConditions) throws Exception {
		this.envConditions = envConditions;
		computewildbiomassFlux();
		//wildTypeFluxes = computeWildType();
	}


	public void setSolver(String solver) {
		center.setSolver(solver);
	}



	public boolean isCriticalGene(String geneId) throws Exception{
		
		boolean ret = true;
		

		GeneChangesList metgeneList = new GeneChangesList();
		GeneregulatorychangesList reggeneList = new GeneregulatorychangesList();
		
		if (model.isMetabolicGene(geneId)){
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Metabolic Gene: "+geneId);
			metgeneList.addGeneKnockout(geneId);
		}
		else if (model.isRegulatoryGene(geneId)){
			LogMessageCenter.getLogger().toClass(getClass()).addDebugMessage("Regulatory Gene: "+geneId);
			reggeneList.addGeneKnockout(geneId);
		}
		else throw new Exception();
		
	
		RegulatoryGeneticConditions genecond = new RegulatoryGeneticConditions(reggeneList, metgeneList, model, false);
		Map<String, Double> obj_coef = new HashMap<String, Double>();
		obj_coef.put(this.biomassFlux, 1.0);
		

		center.setGeneticConditions(genecond);

		IntegratedSimulationMethodResult result =(IntegratedSimulationMethodResult) center.simulate();
	   
		//double wtbiomass = wildTypeFluxes.getValue(this.biomassFlux);
		
		
		if(result != null){
			//FluxValueMap fluxes = result.getFluxValues();
			double biomassfluxz=result.getOFvalue();
		   
			String msg="Biomass="+biomassfluxz+">="+(MINIMAL_VALUE*wildtypebiomassflux)+"-->"+(biomassfluxz >= (MINIMAL_VALUE*wildtypebiomassflux));
				//LogMessageCenter.getLogger().addDebugMessage("Biomass="+biomassfluxz+">="+(MINIMAL_VALUE*wildtypebiomassflux)+"-->"+(biomassfluxz >= (MINIMAL_VALUE*wildtypebiomassflux)));
		
			if(biomassfluxz >= (MINIMAL_VALUE*wildtypebiomassflux)){
				
				
				msg=msg+" ---> Not critical";
				
				ret = false;
			}
			else
				msg=msg+" ---> Is critical";
			
			LogMessageCenter.getLogger().addDebugMessage(msg);
		}
		
		
		
		return ret;
	}
	
	public void setStatusHandler(StatusHandlerCriticalgenes st){
		this.status=st;
	}
	
	public int getCurrentIteration(){
		return this.CurrentIteration;
	}
	
	public int getNumberMaxIteration(){
		return this.getModel().getGenesInRegulatoryModel().size();
	}
	
	public void setTerminationFlag(boolean bol){
		this.terminationFlag = bol ;
	}
	
	public boolean stopedoperation(){
		return terminationFlag;
	}
	
	public double getMINIMAL_VALUE() {
		return MINIMAL_VALUE;
	}

	public List<String> getCriticalGenesId() {
		return criticalGenesId;
	}

	public void setCriticalGenesId(List<String> criticalGenesId) {
		this.criticalGenesId = criticalGenesId;
	}

	public IIntegratedStedystateModel getModel() {
		return model;
	}


	public AbstractIntegratedSimulationControlCenter getCenter() {
		return center;
	}

	public EnvironmentalConditions getEnvConditions() {
		return envConditions;
	}

	public String getBiomassFlux() {
		return biomassFlux;
	}

	public FluxValueMap getWildTypeFluxes() {
		return wildTypeFluxes;
	}
	
	public void saveInFile(String file) throws IOException{
		
		FileWriter wfile = new FileWriter(file);
		BufferedWriter writer = new BufferedWriter(wfile);
		
		for(int i =0; i< criticalGenesId.size(); i++){
			writer.write(criticalGenesId.get(i)+"\n");
		}
		
		writer.close();
		wfile.close();
	}
	
	
	public static  CriticalRegulatoryGenesResults loadCriticalRegulatoryGenes(String file, IIntegratedStedystateModel model) throws Exception{
		
		CriticalRegulatoryGenesResults result = null;
		
		FileReader rfile = new FileReader(file);
		BufferedReader reader = new BufferedReader(rfile);
		boolean geneInModel = true;
		
		ArrayList<String> criticalGenesId = new ArrayList<String>();
		
		String line = reader.readLine();
		
		ArrayList<String> allgenes=model.getAllGenes();
		
		while(geneInModel && line != null && !line.isEmpty()){
			String geneid=line.trim();
			
			if(allgenes.contains(geneid))
				criticalGenesId.add(line);
			/*else
				geneInModel = false;*/
			line = reader.readLine();
		}
		
		/*if(!geneInModel)
			throw new NonExistentIdException();
		else*/
		return new CriticalRegulatoryGenesResults(criticalGenesId, model);
	
		
		/*return result;*/
	}


	@Override
	public void stopExecution() {
       terminationFlag=true;
	}


   
}
