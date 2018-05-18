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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.sbmlqual;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.ext.qual.FunctionTerm;
import org.sbml.jsbml.ext.qual.Output;
import org.sbml.jsbml.ext.qual.QualConstants;
import org.sbml.jsbml.ext.qual.QualModelPlugin;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.qual.Transition;
import org.sbml.jsbml.xml.XMLNode;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.parser.ParseException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.AbstractRegulatoryNetworkReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.sbmlqual.transitions.QualTransitionsReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.logicalmodel.converters.OptfluxRegulatoryModelToBDDLogicalModelConverter;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.OptfluxRegulatoryModel;

public class SBMLQualReader extends AbstractRegulatoryNetworkReader{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Model sbmlmodel;
	protected QualModelPlugin qualmodel;
	//protected Object originalsbmlfile;
	
	protected LinkedHashMap<String, String> specieid2speciename=new LinkedHashMap();
	protected IndexedHashMap<String, String> geneproductmap=new IndexedHashMap<>();

	protected IndexedHashMap<String, Integer> initialvariableslevel=new IndexedHashMap<>();
	protected IndexedHashMap<String, RegulatoryModelComponent> variabletypemap=new IndexedHashMap<>();
	protected IndexedHashMap<String, Integer> maxvariableslevel=new IndexedHashMap<>();
	protected LinkedHashSet<String> listofconditionvariables=new LinkedHashSet<>();
	protected LinkedHashSet<String> variableswithtransitions;
	
	
	protected ArrayList<String> listconstantelems=new ArrayList<>();
	protected HashSet<String> withoutmaxlevel=new HashSet<>();
	protected HashSet<String> withoutinitiallevel=new HashSet<>();
	protected List<Transition> transitionslist;
	protected boolean forcebooleanstate=false;

	protected boolean exclusivebooleanmodel=true;
	protected IRODDRegulatoryModel logicalmodel;
	//protected boolean genesidlinkbyruleid=false;
	 protected boolean loadedmodel=false;
	
	
	public SBMLQualReader(File file)  throws Exception{
		super(file.getAbsolutePath());
		readSBMLFile(file);	
	 }
	
	
	 public SBMLQualReader(String filepath)  throws Exception{
		super(filepath);
		readSBMLFile(filepath);
	 }
	 
	 
	 
	 public SBMLQualReader(String filepath, String knownvariablesfile) throws Exception{
		 super(filepath, knownvariablesfile);
		 readSBMLFile(filepath);
	 }
	 
	 
	
	
	public SBMLQualReader(String filepath, String knownmetabolitesfile, String knownenvironmentalconditionsfile,
			String knownreactionsfile, String knowntfsfile, String knowngenesfiles) throws Exception {
		super(filepath, knownmetabolitesfile, knownenvironmentalconditionsfile, knownreactionsfile, knowntfsfile,
				knowngenesfiles);
		readSBMLFile(filepath);
	}

	
    public SBMLQualReader(String file, boolean forcebooleanformalism) throws Exception{
    	this(file);
    	this.forcebooleanstate=forcebooleanformalism;
    }
    
    
    public SBMLQualReader(SBMLDocument document) throws IOException{
    	super(null);
    	this.sbmlmodel=document.getModel();
		if(validQualModel()){
			this.qualmodel=(QualModelPlugin) sbmlmodel.getExtension(QualConstants.shortLabel);
			preloadModelInfo();
		}
		else
			throw new IOException("Invalid Qualitative SBML model file");
    }
    
    
    protected void readSBMLFile(File file) throws XMLStreamException, IOException{
    	SBMLReader reader=new SBMLReader();
		SBMLDocument document = reader.readSBML(file);
		//this.originalsbmlfile=file;
		this.sbmlmodel=document.getModel();
		if(validQualModel()){
			this.qualmodel=(QualModelPlugin) sbmlmodel.getExtension(QualConstants.shortLabel);
			preloadModelInfo();
		}
		else
			throw new IOException("Invalid Qualitative SBML model file");
    }
    
    protected void readSBMLFile(String filepath) throws XMLStreamException, IOException{
    	readSBMLFile(new File(filepath));
    }
	
    public void forceBooleanModel(boolean state){
    	this.forcebooleanstate=state;
    }
	
    protected boolean validQualModel(){
		if(sbmlmodel.getExtensionPackages().containsKey(QualConstants.shortLabel))
			return true;
		else
			return false;
	}
    
    
    protected void preloadModelInfo(){

    	List<QualitativeSpecies> species = qualmodel.getListOfQualitativeSpecies();
		readQualitativeSpeciesInModel(species);
		transitionslist=qualmodel.getListOfTransitions();
		checkMissingElementsValues(transitionslist);
		exclusivebooleanmodel=isBooleanModel();
		if(forcebooleanstate)
			exclusivebooleanmodel=true;
    }
    
    public boolean isBooleanModel(){
		   for (int i = 0; i < maxvariableslevel.size(); i++) {
			   int value=maxvariableslevel.getValueAt(i);
			   if(value<0 || value>1){
				   return false;
			    }
		    }
		   return true;
	}
    
    protected void checkMissingElementsValues(List<Transition> transitions){
    	this.variableswithtransitions=new LinkedHashSet<>();
    	
    	for (int i = 0; i <transitions.size(); i++) {
				Transition tr=transitions.get(i);
				Output outelem = tr.getListOfOutputs().get(0);
				String name=outelem.getQualitativeSpecies();
				
				variableswithtransitions.add(name);
    	
		//if(withoutinitiallevel.size()>0 || withoutmaxlevel.size()>0){
			
				
				if(name!=null && withoutinitiallevel.size()>0 && withoutinitiallevel.contains(name)){
					Integer value=getDefaultTerm(tr);
					if(value!=null)
						initialvariableslevel.put(name, value);
				}
				
				if(name!=null && withoutmaxlevel.size()>0 && withoutmaxlevel.contains(name)){
					Integer value=getHighestMaxTransitionValue(tr);
					if(value!=null){
						maxvariableslevel.put(name, value);
					}
				}
					
			//}
		}
    	
    	checkVariablesWithoutTransitionRules(variableswithtransitions);
	}
    
    protected void checkVariablesWithoutTransitionRules(LinkedHashSet<String> variableswithtransitions){
    	
    	  for (String varid : specieid2speciename.keySet()) {
    		  if(!variableswithtransitions.contains(varid) && !listconstantelems.contains(varid))
  				listconstantelems.add(varid);
		  }
    	
    }
    
    
    protected Integer getHighestMaxTransitionValue(Transition tr){
		Integer res=null;
		int oldvalue=Integer.MIN_VALUE;
		for (FunctionTerm ft : tr.getListOfFunctionTerms()) {
			if(!ft.isDefaultTerm()){
				int out=ft.getResultLevel();
				if(out>oldvalue){
					oldvalue=out;
					res=out;
				}
			}
		 }
		if(res!=Integer.MIN_VALUE)
			return res;
		return null;
	}
    
    
    
    @SuppressWarnings("unchecked")
    @Override
	public void loadModel() throws Exception {
    	
    	if(exclusivebooleanmodel || forcebooleanstate){
    		QualTransitionsReader transitionreader=new QualTransitionsReader(transitionslist, listconstantelems);
    		
    		transitionreader.setSpecieid2speciename(specieid2speciename);
    		if(geneproductmap!=null)
    			transitionreader.setGeneproductmap(geneproductmap);	
    		transitionreader.processTransitionRules();
    		GeneID2RuleID=transitionreader.getGeneID2RuleID();
    		genesidlinkbyruleid=transitionreader.isGeneinrulelinkbyruleid();
    		IndexedHashMap<String, String> decodedrules=transitionreader.getDecodedtransitionrules();
    		LinkedHashSet<String> decodedcontantsvars=transitionreader.getDecodedcontantsvars();
    		
    		mergeTypeVariablesMap(transitionreader.getMapofextractedmodelvariablestype());
    	
    		transformDataToOptfluxBooleanRegulatoryModel(decodedrules,decodedcontantsvars);
    		
    		this.logicalmodel=convertToBDDLogicalModel();
    	}
    	else{
    		throw new Exception("Not implemented yet");
    		/*SBMLqualImport importer=null;
    		if(originalsbmlfile instanceof File)
    			importer=new SBMLqualImport((File)originalsbmlfile);
    		else
    			importer=new SBMLqualImport((SBMLDocument)originalsbmlfile);
    		
    		this.logicalmodel=importer.getModel();
    		configureGeneMapAndGeneRules(null);*/
    	}
    	
    	loadedmodel=true;
    	
    }
    
    
    protected IRODDRegulatoryModel convertToBDDLogicalModel() throws Exception{
    	OptfluxRegulatoryModelToBDDLogicalModelConverter converttologicalmodel=new OptfluxRegulatoryModelToBDDLogicalModelConverter(regulatoryGenes, 
                regulatoryGeneRules, 
                regulatoryVariables,
                GeneID2RuleID,
                unconstrainedgenes,
                genesidlinkbyruleid);
    	return converttologicalmodel.convertModel();
    }

    
    protected void readQualitativeSpeciesInModel(List<QualitativeSpecies> species){
    	ArrayList<Boolean> verifiedtypevariable=new ArrayList<>();
		
    	for (int i = 0; i < species.size(); i++) {
				QualitativeSpecies specie=species.get(i);
				String id=specie.getId();
				String name=specie.getName();
				
				RegulatoryModelComponent type=getComponentType(specie.getMetaId());
				if(type==null)
					type=checkifCategorized(id);
				if(type!=null)
					verifiedtypevariable.add(true);
				else
					verifiedtypevariable.add(false);
			    
				
				if(name!=null && !name.isEmpty())
				   specieid2speciename.put(id, name);
				else
				   specieid2speciename.put(id, id);
				
				boolean constant=false;
				try {
					constant=specie.getConstant();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
				
				String product=getProduct(specie.getNotes());
				if(product!=null){
					geneproductmap.put(id, product);
					
				}
				
				if(type!=null){
						variabletypemap.put(id, type);
						
				}
				
				if(constant){
					listconstantelems.add(id);
				}
				else{
				   Integer initlevel=null;
				   try {
					 initlevel=specie.getInitialLevel();
				   } catch (Exception e) {
					  withoutinitiallevel.add(id);
				   }
				   
				   if(initlevel!=null)
					   initialvariableslevel.put(id, initlevel);
				
				   Integer maxlevel=null;
				   try {
					  maxlevel=specie.getMaxLevel();
				    } catch (Exception e) {
					  withoutmaxlevel.add(id);
				   }
				   
				   if(maxlevel!=null)
					maxvariableslevel.put(id, maxlevel);
				   }
			
			}
    	
    	boolean allvariablesverified=true;
    	for (int i = 0; i < verifiedtypevariable.size(); i++) {
			allvariablesverified&=verifiedtypevariable.get(i);
		}
    	this.verifiedvariables=allvariablesverified;
	}
    
    
    protected RegulatoryModelComponent checkifCategorized(String varid){
    	
    	if(knownenvironmentalconditionsids!=null && knownenvironmentalconditionsids.contains(varid))
    		return RegulatoryModelComponent.ENV_CONDITION_ID;
    	else if(knownmetabolitesids!=null && knownmetabolitesids.contains(varid))
    		return RegulatoryModelComponent.METABOLITE_ID;
    	else if(knowntfids!=null && knowntfids.contains(varid))
    		return RegulatoryModelComponent.TRANS_FACTOR_ID;
    	else if(knownreactionids!=null && knownreactionids.contains(varid))
    		return RegulatoryModelComponent.REACTION_ID;
    	else if(knowngeneids!=null && knowngeneids.contains(varid))
    		return RegulatoryModelComponent.GENE_ID;
    	else
    		return null;
    				
    	
    		
    }

    
    protected RegulatoryModelComponent getComponentType(String metainfo){
    	if(metainfo!=null){
    		String[] elems=metainfo.split("--");
    		if(elems.length>1){
    			String type=elems[0];
    			for (RegulatoryModelComponent typecomp : RegulatoryModelComponent.values()) {
					if(type.equals(typecomp.getDenomination()))
						return typecomp;
				}
    		}
    	}
    	
    	return null;
    }
    
    
    protected String getProduct(XMLNode xmlnode){
    	if(xmlnode!=null){
    		XMLNode notenode=xmlnode.getChildAt(1);
    		for (int i = 0; i < notenode.getChildCount(); i++) {
    			XMLNode cnode =notenode.getChildAt(i);
    			if(cnode.getName().equals("p")){
    				String t=cnode.getChildAt(0).getCharacters();
    				if(t.contains("Product")){
    					String[] elems=t.split("=");
    					return elems[1].trim();
    				}
    			}
			}
    	}
    	
    	return null;
    }
    
    
	protected Integer getDefaultTerm(Transition transition){
	    Integer res=null;
		for (FunctionTerm ft : transition.getListOfFunctionTerms()) {
			if(ft.isDefaultTerm())
				res=ft.getResultLevel();
		 }
   return res;
   }
	
	
	private void mergeTypeVariablesMap(IndexedHashMap<String, RegulatoryModelComponent> decodedvariablestype){
		
		if(decodedvariablestype.size()>0){
			for (int i = 0; i < decodedvariablestype.size(); i++) {
				String varid=decodedvariablestype.getKeyAt(i);
				
				if(!variabletypemap.containsKey(varid))
					variabletypemap.put(varid, decodedvariablestype.get(varid));
			
			}
		}
		
	}
	
	protected void transformDataToOptfluxBooleanRegulatoryModel(IndexedHashMap<String, String> decodedrules,LinkedHashSet<String> decodedcontantsvars) throws ParseException{
		
		configureGeneMapAndGeneRules(decodedrules);
		LogMessageCenter.getLogger().toClass(getClass()).addTraceMessage("Regulatory Genes: ", regulatoryGenes,"\n");
		configureRegulatoryVariables(decodedcontantsvars);
		extractModelInfo();
	}
	
	
	
	protected void configureGeneMapAndGeneRules(IndexedHashMap<String, String> decodedrules) throws ParseException{
		this.regulatoryGenes=new IndexedHashMap<>();
		this.regulatoryGeneRules=new IndexedHashMap<>();
		
		
		for (String id : specieid2speciename.keySet()) {
			
			if(!listconstantelems.contains(id)){
				
				if(decodedrules.containsKey(id) ||(variabletypemap!=null && variabletypemap.containsKey(id) && 
						(variabletypemap.get(id).equals(RegulatoryModelComponent.GENE_ID)))){
					
					String ruleid=null;
					if(GeneID2RuleID.containsKey(id))		
						ruleid=GeneID2RuleID.get(id);
					else
						ruleid=id;
					
				    RegulatoryRule generule=null;
				
				   if(decodedrules.containsKey(id)){ 
					   Regulator gene=new Regulator(id, specieid2speciename.get(id));
				       regulatoryGenes.put(id, gene);
					   
					   generule=new RegulatoryRule(ruleid, decodedrules.get(id));
				       regulatoryGeneRules.put(id, generule);
				   }
				   else{
					  if(unconstrainedgenes==null)
						  unconstrainedgenes=new LinkedHashSet<>();
					  unconstrainedgenes.add(id);
				   }
					
		
				}
				else
					listofconditionvariables.add(id);
			}
			else
				listofconditionvariables.add(id);
		}
		
	}
	
	
	
	protected void configureRegulatoryVariables(LinkedHashSet<String> decodedcontantsvars){
		regulatoryVariables=new IndexedHashMap<>();
		IndexedHashMap<String, RegulatoryModelComponent> filteredvartypes=new IndexedHashMap<>();
		
		
		for (String id : listofconditionvariables) {
			
			RegulatoryVariable var =null;
			if(variabletypemap!=null && variabletypemap.containsKey(id))
				var =RegulatoryVariable.setupVariable(id, variabletypemap.get(id));
			else
				var=RegulatoryVariable.setupVariable(id, RegulatoryModelComponent.ENV_CONDITION_ID);
			
			regulatoryVariables.put(var.getId(), var);
			filteredvartypes.put(var.getId(), var.getType());
		}
		
		
		for (String decodid : decodedcontantsvars) {
			
			if(!listofconditionvariables.contains(decodid)){
				RegulatoryVariable vardecod=RegulatoryVariable.setupVariable(decodid, variabletypemap.get(decodid));
				regulatoryVariables.put(vardecod.getId(),vardecod);
				filteredvartypes.put(vardecod.getId(), vardecod.getType());
			}
		}
		
		this.variabletypemap=filteredvartypes;
	}
	
	

	@Override
	public void updateRegulatoryVariablesType(IndexedHashMap<String, RegulatoryModelComponent> typevariables){
		for (int i = 0; i < typevariables.size(); i++) {
			String id=typevariables.getKeyAt(i);
			RegulatoryModelComponent type=typevariables.get(id);
			RegulatoryModelComponent previoustype=variabletypemap.get(id);
			if(previoustype!=null && type!=previoustype){
				regulatoryVariables.get(id).setComponentType(type);
				variabletypemap.putAt(variabletypemap.getIndexOf(id), id, type);
			}
		}
		
		try {
			this.logicalmodel=convertToBDDLogicalModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void extractModelInfo(){
		String modelname=sbmlmodel.getName();
		if(modelname!=null && !modelname.isEmpty())
			this.modelName=modelname;
	}
	


	@Override
	public boolean isStrictBooleanFormalism() {
		return exclusivebooleanmodel;
	}


	@Override
	public IRODDRegulatoryModel getROBDDModelFormat() {
		return logicalmodel;
	}

	
    
	public IOptfluxRegulatoryModel getOptFluxModelFormat() throws InstantiationException, IllegalAccessException{
		return new OptfluxRegulatoryModel(getModelName(), getRegulatoryGenes(), getRegulatoryGeneRules(), getRegulatoryVariables(), getGeneID2RuleID(), getUnconstrainedGenes(), genesInRuleLinkByRuleID());
		//return new OptfluxRegulatoryModel(getModelName(), getRegulatoryGenes(), getRegulatoryGeneRules(), getRegulatoryVariables(), getGeneID2RuleID(), getRegulatoryVariableType(), getUnconstrainedGenes(), genesInRuleLinkByRuleID());
	}


	@Override
	public boolean isModelLoaded() {
		return loadedmodel;
	}

    

}
