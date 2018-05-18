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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.writers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.ext.qual.FunctionTerm;
import org.sbml.jsbml.ext.qual.Input;
import org.sbml.jsbml.ext.qual.InputTransitionEffect;
import org.sbml.jsbml.ext.qual.OutputTransitionEffect;
import org.sbml.jsbml.ext.qual.QualConstants;
import org.sbml.jsbml.ext.qual.QualModelPlugin;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.qual.Transition;

import pt.ornrocha.fileutils.MTUFileUtils;
import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.booleanutils.sbmltools.AbstractSyntaxTreeConverterTools;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;

public class SBMLqualRegulatoryModelExporter implements IRegulatoryModelExporter{

	private IIntegratedStedystateModel integmodel;
	private IRegulatoryNetwork regmodel;
	private Model sbmlqualmodel;
	private QualModelPlugin qualmodelext;
	private SBMLDocument sbmldoc;
	private IndexedHashMap<String, RegulatoryRule> inputgenerules=new IndexedHashMap<>();
	private HashMap<String, QualitativeSpecies> mappedspecies=new HashMap<>();
	private HashMap<String, String> registeredvariables=new HashMap<>();
	private HashMap<String, String> geneproductassociation=new HashMap<>();
	private boolean usedefaultexporter=true;
	
	private String TRANSITIONPREFIX = "tr_";
	
	public SBMLqualRegulatoryModelExporter(IOptfluxRegulatoryModel model){
		this.regmodel=model;
		initQualPlugin(null);
	}
	
	public SBMLqualRegulatoryModelExporter(IRODDRegulatoryModel model){
		this.regmodel=model;
		usedefaultexporter=false;
	}
	

	
	
	public SBMLqualRegulatoryModelExporter(IIntegratedStedystateModel model){
		if(!model.supportsMultiValuedRegulatorySimulation()){
			model.useLogicalModelNetworkFormat(false);
			IRegulatoryNetwork regmodel=model.getRegulatoryNetwork();
			if(regmodel instanceof IRODDRegulatoryModel)
				usedefaultexporter=false;
			else
				usedefaultexporter=true;
		}
		else{
			usedefaultexporter=false;
		}
		
		this.regmodel=model.getRegulatoryNetwork();
		this.integmodel=model;
	}
	
	public SBMLqualRegulatoryModelExporter(IOptfluxRegulatoryModel model, String qualmodelid){
		this.regmodel=model;
        initQualPlugin(qualmodelid);
	}
	
/*	public SBMLqualExport(IIntegratedStedystateModel model, String qualmodelid){
		this(model.getRegulatoryNetwork(),qualmodelid);
		this.integmodel=model;	
	}*/
	
	
	private void initQualPlugin(String qualmodelid){
		if(qualmodelid!=null)
		    this.sbmlqualmodel=new Model(qualmodelid,3, 1);
		else
			this.sbmlqualmodel=new Model(3, 1);
		qualmodelext=new QualModelPlugin(sbmlqualmodel);
		sbmlqualmodel.addExtension(QualConstants.namespaceURI, qualmodelext);
	}
	
	
	protected void loadModelInformation() throws Exception{
		
		if(usedefaultexporter){
			loadOptFluxRegulatoryModelInformation();
		}
		else{
			loadROBDDRegulatoryModelInformation();
		}
		
		
		
	}
	
	
	protected void loadOptFluxRegulatoryModelInformation() throws Exception{
		createGeneAsQualitativeSpecies();
		createVariablesAsQualitativeSpecies();
		addGeneTransitions();
	}
	
	
	public void loadROBDDRegulatoryModelInformation()throws Exception{
		
		  ExportRODDModelToSBMLqual exportlogicalmodel=new ExportRODDModelToSBMLqual((IRODDRegulatoryModel) regmodel);
		  sbmldoc= exportlogicalmodel.getSBMLDocument();
		 
		  qualmodelext=(QualModelPlugin) sbmldoc.getModel().getExtension(QualConstants.namespaceURI);
		  addExtraGeneInformation();
		  addExtraVariablesInformation();
		
	}
	
	
	
	protected void addExtraGeneInformation() throws XMLStreamException{
	
		 IRODDRegulatoryModel model=(IRODDRegulatoryModel) regmodel;
		
		 IndexedHashMap<String, Integer> geneindexes=model.getGeneIndexes();
		 
		 for (int i = 0; i < geneindexes.size(); i++) {
			String geneid=geneindexes.getKeyAt(i);
			
			QualitativeSpecies sp =qualmodelext.getQualitativeSpecies(geneid);
			
			String ruleassoc=model.getRuleIDAssociatedToRegulatorID(geneid);
			if(ruleassoc!=null){
				sp.setNotes("Product="+ruleassoc);
			}
			 
			if(integmodel!=null){
		    	sp.appendNotes("GeneType="+integmodel.getGeneType(geneid).toString());
		    }
			sp.setMetaId(RegulatoryModelComponent.GENE_ID.getDenomination()+"--"+geneid); 
			 
		  }
	}
	
	protected void addExtraVariablesInformation(){
		IndexedHashMap<String, RegulatoryVariable> variables =regmodel.getVariablesInRegulatoryNetwork();
		for (int i = 0; i < variables.size(); i++) {
			String varid=variables.getKeyAt(i);
		    RegulatoryModelComponent comptype=variables.get(varid).getType();
		    QualitativeSpecies sp =qualmodelext.getQualitativeSpecies(varid);
		    if(comptype!=null)
		          sp.setMetaId(comptype.getDenomination()+"--"+varid);
		}
		
	}
	
	
	
	
	
	protected void createGeneAsQualitativeSpecies() throws XMLStreamException{
		IOptfluxRegulatoryModel model=(IOptfluxRegulatoryModel) regmodel;
		
		IndexedHashMap<String, Regulator> genespecies=model.getRegulators();
		IndexedHashMap<String, RegulatoryRule> generules=model.getRegulatoryRules();
		
		
		for (int i = 0; i < genespecies.size(); i++) {
			String geneid=genespecies.getKeyAt(i);
			//StringBuilder notes=new StringBuilder();
			
			String rule=generules.get(geneid).getRule();
			if(rule!=null && !rule.isEmpty()){
				String genename=genespecies.get(geneid).getName();
				QualitativeSpecies sp =qualmodelext.createQualitativeSpecies(geneid);
			    sp.setMaxLevel(1);
			    sp.setConstant(false);
			    sp.setInitialLevel(0);
			    if(genename!=null && genename!=geneid)
			    	sp.setName(genename);
			    
			    String geneproduct=generules.get(geneid).getRuleId();
			    if(geneproduct!=geneid){
			        geneproductassociation.put(geneproduct,geneid);
			        sp.setNotes("Product="+geneproduct);
			    }
			    
			    if(integmodel!=null){
			    	sp.appendNotes("GeneType="+integmodel.getGeneType(geneid).toString());
			    }
			    	
			    // if(notes.length()>0)
			    //	 sp.setNotes(notes.toString());
			    
			    sp.setMetaId(RegulatoryModelComponent.GENE_ID.getDenomination()+"--"+geneid); 
			    inputgenerules.put(geneid, generules.get(geneid));
				mappedspecies.put(geneid, sp);
			}
				
		}
	}
	
	protected void createVariablesAsQualitativeSpecies(){
		IndexedHashMap<String, RegulatoryVariable> variables =regmodel.getVariablesInRegulatoryNetwork();
		
		for (int i = 0; i < variables.size(); i++) {
			String varid=variables.getKeyAt(i);
			
			String registerid=null;
			RegulatoryModelComponent comptype=null;
			
			if(varid.contains("<") || varid.contains(">")){
				String[] tempids=varid.split("[<>]");
				varid=tempids[0].trim();
				if(!registeredvariables.containsValue(varid)){
					registerid=varid;
				}
				registeredvariables.put(tempids[0], varid);
			}
			else{
				registerid=varid;
				registeredvariables.put(varid, varid);
			}
			comptype=variables.get(variables.getKeyAt(i)).getType();	
			
			if(registerid!=null){
				QualitativeSpecies sp =qualmodelext.createQualitativeSpecies(registerid);
		        //sp.setMaxLevel(1);
		        sp.setConstant(true);
		        if(comptype!=null)
		          sp.setMetaId(comptype.getDenomination()+"--"+varid);
		        mappedspecies.put(registerid, sp);
			}
		}
	}
	
	protected void addGeneTransitions() throws Exception{
		
		for (int i = 0; i < inputgenerules.size(); i++) {
			String geneid=inputgenerules.getKeyAt(i);
			RegulatoryRule rule=inputgenerules.get(geneid);
			String trid=TRANSITIONPREFIX+geneid;
			Transition tr =qualmodelext.createTransition(trid);
			tr.createOutput(geneid+"_state_out", mappedspecies.get(geneid), OutputTransitionEffect.assignmentLevel);
			
			FunctionTerm defaultoutstate = new FunctionTerm();
			defaultoutstate.setDefaultTerm(true);
			defaultoutstate.setResultLevel(0);
			tr.addFunctionTerm(defaultoutstate);
			
			ArrayList<String> regulators =new ArrayList<>(AbstractSyntaxTreeConverterTools.getOrderedFirstRegulatorInputs(rule.getBooleanRule()));
			
			for (int j = 0; j < regulators.size(); j++) {
				String regulatorid=validateInputVariable(regulators.get(j));
				String inputid=trid+"_input_"+regulatorid;
				Input in =null;
				if(!containsInput(tr.getListOfInputs(), inputid))
					in =tr.createInput(inputid, mappedspecies.get(regulatorid), InputTransitionEffect.none);
				if(in!=null)
					in.setThresholdLevel(1);
			}
			
			ArrayList<ASTNode> functionterms=AbstractSyntaxTreeConverterTools.getListOfFunctionTermsMathForNode(rule.getBooleanRule(),geneproductassociation);
			for (int j = 0; j < functionterms.size(); j++) {
				FunctionTerm ft = new FunctionTerm();
				ft.setResultLevel(1);
				ft.setMath(functionterms.get(j));
				tr.addFunctionTerm(ft);
			}
		}	
	}
	
	protected String validateInputVariable(String varid){
		if(varid.contains("<") || varid.contains(">")){
			String[] tempids=varid.split("[<>]");
			varid=tempids[0].trim();
		}
		else if(geneproductassociation.containsKey(varid))
			varid=geneproductassociation.get(varid);
		return varid;
	}
	
	
	
	protected SBMLDocument configureDocument(){
          SBMLDocument sbmldoc = new SBMLDocument(3,1);
          sbmldoc.enablePackage(QualConstants.namespaceURI);
          return sbmldoc;
		
	}
	
	public SBMLDocument getSBMLDocument() throws Exception {
		loadModelInformation();
		
		if(usedefaultexporter){
			sbmldoc=configureDocument();
			sbmldoc.setModel(sbmlqualmodel);
		}
		
		return sbmldoc;
	}
	
	@Override
	public void export(String file) throws Exception{
		SBMLWriter writer = new SBMLWriter();
		writer.write(getSBMLDocument(), MTUFileUtils.buildFilePathWithExtension(file, "xml"));
	}
	
	public static void export(IRegulatoryNetwork model, String filepath) throws Exception{
		SBMLqualRegulatoryModelExporter exporter=null;
		if(model instanceof IOptfluxRegulatoryModel)
			exporter=new SBMLqualRegulatoryModelExporter((IOptfluxRegulatoryModel) model);
		else
			exporter=new SBMLqualRegulatoryModelExporter((IRODDRegulatoryModel) model);
		
		filepath=MTUFileUtils.buildFilePathWithExtension(filepath, RegulatoryModelExportFormat.SBMLQUAL.getExtension());
		LogMessageCenter.getLogger().toClass(SBMLqualRegulatoryModelExporter.class).addInfoMessage("Regulatory model was saved to: "+filepath);
		exporter.export(filepath);
	}
	
	
	protected boolean containsInput(ListOf<Input> inputlist, String inputid){
		for (Input input : inputlist) {
			if(input.getId().equals(inputid))
				return true;
		}
		return false;
	}
	
	


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
