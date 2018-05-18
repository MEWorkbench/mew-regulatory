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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.metabolicandregulatorynetworks;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.CompartmentCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.GeneCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.MetaboliteCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionConstraintCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.interfaces.IContainerBuilder;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.JSBMLLevel3Reader;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.JSBMLReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.RegulatoryContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.interfaces.IContainerIntegratedModelBuilder;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components.IRegulatoryNetworkReader;

public class SBMLIntegratedModelReader implements IContainerIntegratedModelBuilder{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected RegulatoryContainer regulatorymodelcontainer= null;
	protected InputStream instream;
	protected boolean checkConsistency=true;
	protected String organismName;
	protected Double defaultLB=0.0;
	protected Double defaultUB=10000.0;
	protected IContainerBuilder metabolicmodelbuilder;
	
	  
	
	public SBMLIntegratedModelReader(String filePath, String organismName, IRegulatoryNetworkReader regreader) throws Exception {
         this(filePath, organismName, true,regreader);
			
	}
	
	public SBMLIntegratedModelReader(String filepath, String organismName, boolean checkConsistency, IRegulatoryNetworkReader regreader) throws Exception {
		this(new File(filepath),organismName, checkConsistency,regreader);
	}
	 
	public SBMLIntegratedModelReader(File file, String organismName, boolean checkConsistency, IRegulatoryNetworkReader regreader) throws Exception {
        this.organismName=organismName;
        this.checkConsistency=checkConsistency;
        loadSBMLModelFile(file);
        if(regreader!=null)
        	loadRegulatoryContainer(regreader);
	}
	
	
	public SBMLIntegratedModelReader(String filePath, String organismName, boolean checkConsistency, Double defLb, Double defUb, IRegulatoryNetworkReader regreader) throws Exception {
		this(filePath,organismName,checkConsistency,regreader);
		this.defaultLB=defLb;
		this.defaultUB=defUb;
	}
	
	public SBMLIntegratedModelReader(String filePath, String organismName, boolean checkConsistency, Double defLb, Double defUb) throws Exception {
		this(filePath,organismName,checkConsistency,defLb,defUb,null);
	}
	
	
	protected void loadSBMLModelFile(File file) throws Exception{

		this.instream=new FileInputStream(file);
		SBMLReader reader = new SBMLReader();
		SBMLDocument tempdoc=reader.readSBML(file);
		
		if(tempdoc.getVersion()>0 && tempdoc.getLevel()>2)
			metabolicmodelbuilder=new JSBMLLevel3Reader(instream, organismName, checkConsistency);
		else
			metabolicmodelbuilder=new JSBMLReader(instream, organismName, checkConsistency, defaultLB, defaultUB);
		
	}
	
	public void setRegulatoryModelReader(IRegulatoryNetworkReader reader) throws Exception{
	     loadRegulatoryContainer(reader);
	}
	
	protected void loadRegulatoryContainer(IRegulatoryNetworkReader reader ) throws Exception{
		regulatorymodelcontainer = new RegulatoryContainer(reader);
	}

	
	@Override
	public String getModelName() {
		return metabolicmodelbuilder.getModelName();
	}

	@Override
	public String getOrganismName() {
		return metabolicmodelbuilder.getOrganismName();
	}

	@Override
	public String getNotes() {
		return metabolicmodelbuilder.getNotes();
	}

	@Override
	public Integer getVersion() {
		return metabolicmodelbuilder.getVersion();
	}

	@Override
	public Map<String, CompartmentCI> getCompartments() {
		return metabolicmodelbuilder.getCompartments();
	}

	@Override
	public Map<String, ReactionCI> getReactions() {
		return metabolicmodelbuilder.getReactions();
	}

	@Override
	public Map<String, MetaboliteCI> getMetabolites() {
		return metabolicmodelbuilder.getMetabolites();
	}

	@Override
	public Map<String, GeneCI> getGenes() {
		return metabolicmodelbuilder.getGenes();
	}

	@Override
	public Map<String, Map<String, String>> getMetabolitesExtraInfo() {
		return metabolicmodelbuilder.getMetabolitesExtraInfo();
	}

	@Override
	public Map<String, Map<String, String>> getReactionsExtraInfo() {
		return metabolicmodelbuilder.getReactionsExtraInfo();
	}

	@Override
	public String getBiomassId() {
		return metabolicmodelbuilder.getBiomassId();
	}

	@Override
	public Map<String, ReactionConstraintCI> getDefaultEC() {
		return metabolicmodelbuilder.getDefaultEC();
	}

	@Override
	public String getExternalCompartmentId() {
		return metabolicmodelbuilder.getExternalCompartmentId();
	}

	@Override
	public RegulatoryContainer getRegulatoryContainer() {
		return regulatorymodelcontainer;
	}
	
	public boolean hasWarnings() {
		if(metabolicmodelbuilder instanceof JSBMLLevel3Reader)
			return ((JSBMLLevel3Reader)metabolicmodelbuilder).hasWarnings();
		else
			return ((JSBMLReader)metabolicmodelbuilder).hasWarnings();
	}

	public List<String> getWarnings() {
		if(metabolicmodelbuilder instanceof JSBMLLevel3Reader)
			return ((JSBMLLevel3Reader)metabolicmodelbuilder).getWarnings();
		else
			return ((JSBMLReader)metabolicmodelbuilder).getWarnings();
	}

}
