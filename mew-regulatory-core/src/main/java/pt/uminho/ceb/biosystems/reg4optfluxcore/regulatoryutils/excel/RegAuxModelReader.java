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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RegAuxModelReader {
	
	private String fileext;
	protected FileInputStream filein=null;
	protected ArrayList<String> excellines=new ArrayList<>();
	protected int nmaxcol=0;
	
	public RegAuxModelReader(String Filepath){
		this.fileext=FilenameUtils.getExtension(Filepath).toLowerCase();
		try {
			this.filein = new FileInputStream(new File(Filepath));
			readExcelFile();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public RegAuxModelReader(File file){
		this.fileext=FilenameUtils.getExtension(file.getAbsolutePath());
		try {
			this.filein = new FileInputStream(file);
			readExcelFile();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getLines(){
		return this.excellines;
	}
	
	public int getNumberColumnsFile(){
		return nmaxcol;
	}
	
	
	private void readExcelFile() throws Exception{
		
		switch (fileext) {
		case "xls":
			readXLSFile();
			break;
		case "xlsx":
			readXLSXFile();
			break;	

		default:
			throw new Exception("Unsuported file");

		}
	}
	
	private void readXLSXFile(){
		try{
		   XSSFWorkbook workbook = new XSSFWorkbook(filein);
		   XSSFSheet sheet = workbook.getSheetAt(0);
		
		   Iterator<Row> rowIterator = sheet.iterator();
		      while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();
		         
		        //For each row, iterate through each columns
		        Iterator<Cell> cellIterator = row.cellIterator();
		        String line ="";
		        int nc=0;
		        while(cellIterator.hasNext()) {
		             
		            Cell cell = cellIterator.next();
		            line=line+getValueOfCellStringFormat(cell)+";";
		            
		            nc++;
		        }
		        checkmaxcolumns(nc);
		        excellines.add(removeLastSemicolon(line));
		    }
		    filein.close();
		    
		    //PrintLines();

		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private void readXLSFile(){
		try{
		HSSFWorkbook workbook = new HSSFWorkbook(filein);
		HSSFSheet sheet = workbook.getSheetAt(0);
		
		 Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();
		         
		        //For each row, iterate through each columns
		        Iterator<Cell> cellIterator = row.cellIterator();
		        String line ="";
		        int nc=0;
		        while(cellIterator.hasNext()) {
		             
		            Cell cell = cellIterator.next();
		            line=line+getValueOfCellStringFormat(cell)+";";
		            nc++;
		        }
		        checkmaxcolumns(nc);
		        excellines.add(removeLastSemicolon(line));
		    }
		    filein.close();

		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private String getValueOfCellStringFormat(Cell cell){
		
		switch(cell.getCellType()) {
        case Cell.CELL_TYPE_BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
        case Cell.CELL_TYPE_NUMERIC:
        	 return String.valueOf(cell.getNumericCellValue());
        case Cell.CELL_TYPE_STRING:
        	return String.valueOf(cell.getStringCellValue());
        case Cell.CELL_TYPE_FORMULA:
        	return String.valueOf(cell.getCellFormula());
        default:
        	return "";
		}
		
	}
	
	public void PrintLines(){
		for (String line: excellines) {
			System.out.println(line);
		}
	}
	
	
	private void checkmaxcolumns(int n){
		if(n>nmaxcol)
			nmaxcol=n;
	}
	
	private String removeLastSemicolon(String line) {
		 return line.substring(0, (line.length()-1));
	}

}
