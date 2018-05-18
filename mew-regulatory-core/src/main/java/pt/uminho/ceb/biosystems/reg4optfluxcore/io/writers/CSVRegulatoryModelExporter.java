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
import java.util.Map;

import pt.ornrocha.fileutils.MTUFileUtils;
import pt.ornrocha.ioutils.writers.MTUWriterUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.AbstractRegulatoryNetworkReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.string.RegStringUtils;

public class CSVRegulatoryModelExporter implements IRegulatoryModelExporter{

	private IRegulatoryNetwork model;
	
	public CSVRegulatoryModelExporter(IRegulatoryNetwork model){
		this.model=model;
	}
	

	private String getModelStruture(){
		
		StringBuilder str=new StringBuilder();
		
		String variablesinfo=getVariables();
		if(variablesinfo!=null){
			str.append(AbstractRegulatoryNetworkReader.TAGSTARTRULES+"\n");
			str.append(getRegulatoryRules());
			str.append(AbstractRegulatoryNetworkReader.TAGENDRULES+"\n");
			str.append("\n\n");
			str.append(getVariables());
		}
		else
			str.append(getRegulatoryRules());
		
		return str.toString();
	}
	
	private String getRegulatoryRules(){
		
		StringBuilder str=new StringBuilder();
		IndexedHashMap<String, RegulatoryRule> rules=model.getRegulatoryRules();
		IndexedHashMap<String, Regulator> regulatorsinfo=model.getRegulators();
		for (Map.Entry<String, RegulatoryRule> ruleset : rules.entrySet()) {
			String ruleid=ruleset.getKey();
			RegulatoryRule rule=ruleset.getValue();
			
			String associationinrule=regulatorsinfo.get(ruleid).getName();
			
			str.append(ruleid+";"+RegStringUtils.convertTFnameToGeneName(associationinrule)+";"+RegStringUtils.convertGeneNameToTFname(associationinrule)+";"+rule.getRule()+"\n");
	
		}

		return str.toString();
	}
	
	
	private String getVariables(){
		
		StringBuilder str=new StringBuilder();
		for (RegulatoryModelComponent type : RegulatoryModelComponent.values()) {
			ArrayList<String> variablescategory=getVariablesType(type);
			if(variablescategory.size()>0){
				
				str.append(type.getCategory()+"\n");
				for (int i = 0; i < variablescategory.size(); i++) {
					str.append(variablescategory.get(i)+"\n");
				}
				str.append("\n");	
			}
		}
		
		if(str.length()==0)
			return null;
		else
			return str.toString();
		
	}
	
	
	private ArrayList<String> getVariablesType(RegulatoryModelComponent type){
		ArrayList<String> varids=new ArrayList<>();
		IndexedHashMap<String, RegulatoryModelComponent> variabletype=model.getTypeofRegulatoryVariables();
		for (Map.Entry<String, RegulatoryModelComponent> variableset : variabletype.entrySet()) {
			
			if(variableset.getValue().getCategory().equals(type.getCategory()))
				varids.add(variableset.getKey());
		}
		return varids;
	}
	

	
	@Override
	public void export(String filepath) throws Exception {
		String modelout=getModelStruture();
		filepath=MTUFileUtils.buildFilePathWithExtension(filepath, RegulatoryModelExportFormat.TEXTFILE.getExtension());
		MTUWriterUtils.writeDataTofile(filepath, modelout);
	}
	
	
	public static void export(IRegulatoryNetwork model, String filepath) throws Exception{
		CSVRegulatoryModelExporter exporter=new CSVRegulatoryModelExporter(model);
		exporter.export(filepath);
	}
	

}
