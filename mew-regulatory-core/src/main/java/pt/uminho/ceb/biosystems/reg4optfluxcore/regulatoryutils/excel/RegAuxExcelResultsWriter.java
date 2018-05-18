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
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;


public class RegAuxExcelResultsWriter {
	
	
	public static void writeMatchesResultsToExcelFile(IndexedHashMap<String, IndexedHashMap<String, Boolean>> simulres, String filename) throws Exception{
		
		  
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Simulation_Data");
		
		CellStyle stylered = workbook.createCellStyle();
		stylered.setFillForegroundColor(IndexedColors.RED.getIndex());
		stylered.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		CellStyle stylegreen = workbook.createCellStyle();
		stylegreen.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		stylegreen.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		
		IndexedHashMap<String, Boolean> genes = simulres.getValueAt(1);
		
		
		int nrow = 0;
        Row rowheader = sheet.createRow(nrow);
		for (int i = 0; i < genes.size(); i++) {
			Cell cell= rowheader.createCell(i+1);
			cell.setCellValue(genes.getKeyAt(i));
		}
  
		nrow++;

		for (int i = 0; i < simulres.size(); i++) {
			
			IndexedHashMap<String, Boolean> cols = simulres.getValueAt(i);
			Row row = sheet.createRow(nrow);	
			Cell cellnames= row.createCell(0);
			cellnames.setCellValue(simulres.getKeyAt(i));
			
			for (int j = 0; j < cols.size(); j++) {
				boolean val = cols.getValueAt(j);
				Cell cell= row.createCell(j+1);
				cell.setCellValue(val);
				
				if(val)
					cell.setCellStyle(stylegreen);
				else
					cell.setCellStyle(stylered);
					
				//cellnum++;
			}
			nrow++;
		}
		
		 try
	        {
	            //Write the workbook in file system
	            FileOutputStream out = new FileOutputStream(new File(filename+".xlsx"));
	            workbook.write(out);
	            out.close();
	            System.out.println("written successfully on disk.");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		
		
	}
	
	
	public static void writeSimulationResultsToExcelFile(IndexedHashMap<String, IndexedHashMap<String, Double>> simulres, String filename) throws Exception{
		
		  
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Simulation_Data");
		
		CellStyle stylered = workbook.createCellStyle();
		stylered.setFillForegroundColor(IndexedColors.RED.getIndex());
		stylered.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		CellStyle stylegreen = workbook.createCellStyle();
		stylegreen.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		stylegreen.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		
		IndexedHashMap<String, Double> genes = simulres.getValueAt(1);
		
		
		int nrow = 0;
        Row rowheader = sheet.createRow(nrow);
		for (int i = 0; i < genes.size(); i++) {
			Cell cell= rowheader.createCell(i+1);
			cell.setCellValue(genes.getKeyAt(i));
		}
  
		nrow++;

		for (int i = 0; i < simulres.size(); i++) {
			
			IndexedHashMap<String, Double> cols = simulres.getValueAt(i);
			Row row = sheet.createRow(nrow);	
			Cell cellnames= row.createCell(0);
			cellnames.setCellValue(simulres.getKeyAt(i));
			
			for (int j = 0; j < cols.size(); j++) {
				double val = cols.getValueAt(j);
				Cell cell= row.createCell(j+1);
				cell.setCellValue(val);

					
				//cellnum++;
			}
			nrow++;
		}
		
		 try
	        {
	            //Write the workbook in file system
	            FileOutputStream out = new FileOutputStream(new File(filename+".xlsx"));
	            workbook.write(out);
	            out.close();
	            System.out.println("written successfully on disk.");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	
	}
	


}
