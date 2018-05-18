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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.excel.RegAuxModelReader;



public class RegModelPreprocessingChecker {
	
	private String filepath;
	private boolean useexcel=false;
	private int NSAMPLE=10;
	
	
	private ModDelimiters bestmatchdelimiter;
	
	public RegModelPreprocessingChecker(String filepath){
		this.filepath=filepath;
		this.useexcel=RegModelAuxiliarStaticMethods.usingexcelfile(filepath);
	}
	
	
	public int getFileNumberOfColumns(){
		
		if(!useexcel){
			ArrayList<String> lines=null;
			try {
				lines = (ArrayList<String>) FileUtils.readLines(new File(this.filepath), "utf-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(lines.size()<NSAMPLE)
				NSAMPLE=lines.size();
			bestmatchdelimiter = RegModelAuxiliarStaticMethods.checkBestPossibleDelimiter(lines, NSAMPLE);
			return RegModelAuxiliarStaticMethods.getMaxNumberColumnsCSVFile(lines, bestmatchdelimiter.getDelimiter());
		}
		else
			return new RegAuxModelReader(this.filepath).getNumberColumnsFile();
	 
	}
	
	public boolean usingExcelFile(){
		return this.useexcel;
	}


	public ModDelimiters getBestmatchdelimiter() {
		return bestmatchdelimiter;
	}
	


	

}
