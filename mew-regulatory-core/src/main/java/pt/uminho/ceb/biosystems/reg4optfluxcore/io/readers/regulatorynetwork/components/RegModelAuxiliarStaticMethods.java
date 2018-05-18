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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;

public class RegModelAuxiliarStaticMethods {
	
	
	
	public static boolean usingexcelfile(String filepath){
		String fileext = FilenameUtils.getExtension(filepath);
		if(fileext.toLowerCase().equals("xls") || fileext.toLowerCase().equals("xlsx"))
		   return true;
		else
			return false;
	}
	
	
	
	public static ModDelimiters autodetectDelimiter(String line){
		int nmaxWords=0;
		ModDelimiters possibledelimiter=ModDelimiters.SEMICOLON;
		//String[] delimiters = {",",";","/","\\t",":","@"};
		//String[] delimiters = ModDelimiters.values().
		//HashMap<Integer, String> results = new HashMap<>();
		for (int i = 0; i < ModDelimiters.values().length; i++) {
			ModDelimiters delimiter=ModDelimiters.values()[i];
			String[] nwords = line.split(delimiter.getDelimiter());
			//System.out.println(delimiters[i]+"-->"+nwords.length);
			if(nwords.length>nmaxWords){
				nmaxWords=nwords.length;
				possibledelimiter=delimiter;
			}
		}
	   return possibledelimiter;
	}
	
	public static ModDelimiters checkBestPossibleDelimiter(ArrayList<String> lines, int nrows){
		
		HashMap<ModDelimiters, Integer> deltimes=new HashMap<>();
		ModDelimiters bestmatch=null;
		
		for (int i = 0; i < nrows ; i++) {
			ModDelimiters reschar = autodetectDelimiter(lines.get(i));
			if(deltimes.containsKey(reschar))
				deltimes.put(reschar, deltimes.get(reschar)+1);
			else
				deltimes.put(reschar, 1);
		}
		
		int maxValueInMap=(Collections.max(deltimes.values()));  
        for (Entry<ModDelimiters, Integer> entry : deltimes.entrySet()) {  
            if (entry.getValue()==maxValueInMap) {
                bestmatch=entry.getKey(); 
            }
        }
        return bestmatch;
	}
	
	public static int getMaxNumberColumnsCSVFile(ArrayList<String> lines, String delimiter){
		
		int nmaxCol=-1;
		
		for (String line : lines) {

			String[] datacol = line.split(delimiter);
			if(datacol.length>nmaxCol)
				nmaxCol=datacol.length;
		}
		
		return nmaxCol;
	}
	
	
	
	public static void main(String[] args){
		/*ArrayList<String> list = new ArrayList<>();
		list.add("\"ahas\"\"asd\"");
		list.add("sdad;sad;sad");
		list.add("sda,sad,asd");
		list.add("sda,asda,ad,asd,");
		System.out.println(checkBestPossibleDelimiter(list, 2));*/
		System.out.println(autodetectDelimiter("\"ahas\",\"asd\""));
		
	}

}
