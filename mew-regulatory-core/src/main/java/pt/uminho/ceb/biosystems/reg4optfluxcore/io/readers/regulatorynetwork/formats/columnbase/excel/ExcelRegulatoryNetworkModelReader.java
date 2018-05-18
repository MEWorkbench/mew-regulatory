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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.columnbase.excel;

import java.util.ArrayList;

import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components.RegModelInfoContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.formats.columnbase.AbstractColumnBaseRegulatoryNetworkReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.excel.RegAuxModelReader;

public class ExcelRegulatoryNetworkModelReader extends AbstractColumnBaseRegulatoryNetworkReader{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String READERNAME= "Regulatory Network Excel Reader"; 


	
	public ExcelRegulatoryNetworkModelReader(String filepath) throws Exception{
		super(filepath);
	}
	
	public ExcelRegulatoryNetworkModelReader(String filepath, RegModelInfoContainer infocontainer) throws Exception{
		super(filepath, infocontainer);
	}
	
	public ExcelRegulatoryNetworkModelReader(String filepath, RegModelInfoContainer infocontainer, String knownvariablesfile) throws Exception{
		super(filepath, infocontainer, knownvariablesfile);
	}
	
	

	public ExcelRegulatoryNetworkModelReader(String filepath, RegModelInfoContainer infocontainer,
			String knownmetabolitesfile, String knownenvironmentalconditionsfile, String knownreactionsfile,
			String knowntfsfile, String knowngenesfiles) throws Exception {
		super(filepath, infocontainer, knownmetabolitesfile, knownenvironmentalconditionsfile, knownreactionsfile, knowntfsfile,
				knowngenesfiles);

	}

	
	@Override
	protected ArrayList<String> readFile(String filepath) throws Exception {
		return new RegAuxModelReader(filepath).getLines();
	}

	
	@Override
	public String getReaderName() {
		return READERNAME;
	}


	@Override
	public boolean isStrictBooleanFormalism() {
		return true;
	}


	@Override
	public IRODDRegulatoryModel getROBDDModelFormat() {
		return null;
	}






	

}
