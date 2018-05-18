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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.string;

public class RegStringUtils {

	
	public static String convertGeneNameToTFname(String genename){
		 if(genename.length()<4){
			 return genename.substring(0, 1).toUpperCase()+genename.substring(1).toLowerCase();
		 }
		 else{
			 if(genename.contains("-")){
				 String[]elems = genename.split("-");
				 return convertGeneNameToTFname(elems[0])+"-"+convertGeneNameToTFname(elems[1]);
			 }
			 else if(genename.contains("_")){
				 String[]elems = genename.split("_");
				 return convertGeneNameToTFname(elems[0])+"_"+elems[1];
			 }
			 else
			   return genename.substring(0, 1).toUpperCase()+genename.substring(1, (genename.length()-1)).toLowerCase()+genename.substring((genename.length()-1), genename.length()).toUpperCase();
		 }		 
	 }
	 
	 
	 
	 public static String convertTFnameToGeneName(String tfname){
		 if(tfname.length()<4){
			 return tfname.toLowerCase();
		 }
		 else{
			 if(tfname.contains("-")){
				 String[]elems = tfname.split("-");
				 return convertTFnameToGeneName(elems[0])+"-"+convertTFnameToGeneName(elems[1]);
			 }
			 else if(tfname.contains("_")){
				 String[]elems=tfname.split("_");
				 return convertTFnameToGeneName(elems[0])+"_"+elems[1];
			 }
			 else
			   return tfname.substring(0, (tfname.length()-1)).toLowerCase()+tfname.substring((tfname.length()-1), tfname.length()).toUpperCase();
		 }		 
	 }
	 
	 
	 public static String uniformizeGeneName(String gene){
		 if(gene.length()<4){
			 return gene.toLowerCase();
		 }
		 else{
			 if(gene.contains("_")){
				 String[] elems = gene.split("_");
				 return uniformizeGeneName(elems[0])+"_"+elems[1];
			 }
			 else
				 return gene.substring(0, (gene.length()-1)).toLowerCase()+gene.substring((gene.length()-1), gene.length()).toUpperCase();
		 }
			
		 
	 }
	
	
	
	
}
