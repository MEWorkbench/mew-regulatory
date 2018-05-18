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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.columnbase.text;

import java.util.ArrayList;

import pt.ornrocha.ioutils.readers.MTUReadUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.components.RegulatoryModelComponent;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components.RegModelInfoContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.columnbase.AbstractColumnBaseRegulatoryNetworkReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;

public class CSVRegulatoryNetworkModelReader extends AbstractColumnBaseRegulatoryNetworkReader{

	

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final String READERNAME= "Regulatory Network CSV Reader"; 

	 

	 public CSVRegulatoryNetworkModelReader (String filepath) throws Exception{
			super(filepath);
	  	}
	 
	 
	 public CSVRegulatoryNetworkModelReader(String filepath, RegModelInfoContainer infocontainer) throws Exception{
		 super(filepath, infocontainer);
	 }
	 
	 public CSVRegulatoryNetworkModelReader(String filepath, RegModelInfoContainer infocontainer, String knownvariablesfile) throws Exception{
		 super(filepath, infocontainer, knownvariablesfile);
	 }
	 
	 public CSVRegulatoryNetworkModelReader(String filepath, RegModelInfoContainer infocontainer,
				String knownmetabolitesfile, String knownenvironmentalconditionsfile, String knownreactionsfile,
				String knowntfsfile, String knowngenesfiles) throws Exception {
			super(filepath, infocontainer, knownmetabolitesfile, knownenvironmentalconditionsfile, knownreactionsfile, knowntfsfile,
					knowngenesfiles);
	 }
	 
	@Override
	protected ArrayList<String> readFile(String filepath) throws Exception {
		readKnownVariablesFile(filepath);
		ArrayList<String> modellines=(ArrayList<String>) MTUReadUtils.readFileLines(filepath);

		ArrayList<String> rules=new ArrayList<>();

		if(definedknownvariables && !auxvarsfilesused){
			boolean cacherule=false;
			for (int i = 0; i < modellines.size(); i++) {
				String line=modellines.get(i);
				if(line.trim().equals(TAGSTARTRULES)){
					cacherule=true;
					continue;
				}
				else if(line.trim().equals(TAGENDRULES) || 
						line.trim().equals(TAGENVIRONMENTALCONDITIONS) ||
						line.trim().equals(TAGTRANSCRIPTIONALFACTORS) ||
						line.trim().equals(TAGREACTIONS) ||
						line.trim().equals(TAGMETABOLITES) ||
						line.trim().equals(TAGGENES)){
					cacherule=false;
					
				}
				if(cacherule){
					rules.add(line);
				}
			}
			
		}
		else{
			for (int i = 0; i < modellines.size(); i++) {
				String line=modellines.get(i);
				
				if(line.startsWith("@"))
					continue;
				else if(!line.isEmpty())
					rules.add(line);
				
			}
		}
		
		return rules;

	}
	 
	
	@Override
	public String getReaderName() {
		// TODO Auto-generated method stub
		return READERNAME;
	}

	

	@Override
	public boolean isStrictBooleanFormalism() {
		return true;
	}


	

   public static void main(String[] args) throws Exception{
	   
	  // String file="/home/orocha/AATestRegModels/ecoli.csv";
	   String file="/home/orocha/Música/iMC1010v2_SBML1260_geneforce.csv";
	   String auxfile="/home/orocha/MEOCloud/TRABALHO/Models/irj904_xml_alterado_acordo_estudo_covert/variables.txt"; 
	   RegModelInfoContainer info=RegModelInfoContainer.getDefaultContainer();
	   
	 // LogMessageCenter.getLogger().setLogLevel(MTULogLevel.DEBUG);
	   CSVRegulatoryNetworkModelReader reader=new CSVRegulatoryNetworkModelReader(file, info);
	   reader.loadModel();
	   
	   
	  IndexedHashMap<String, RegulatoryModelComponent> variables=reader.getRegulatoryVariableType();
	   IndexedHashMap<String,RegulatoryRule> rules=reader.getRegulatoryGeneRules();
	   

	   
	    for (String geneid : rules.keySet()) {
			System.out.println(geneid+" --> "+rules.get(geneid).getRule());
		}
	   
	    System.out.println("\n\n\n");
	   for (String varid : variables.keySet()) {
		   System.out.println("Variable: "+varid+" type: "+variables.get(varid).getDenomination());
	   }
	   
   }


	


	


}
