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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.file;

public class RegDirAndFileUtils {
	
	   public static String setDiretory(String mainpath,String integratedmethod,String namemetabolite, int nknockouts, String optimizationalgorithm, String objtype){
		   	
		   	return mainpath+"/"+namemetabolite+"/"+integratedmethod+"/"+objtype+"/"+optimizationalgorithm+"/"+String.valueOf(nknockouts)+"_MaxKnockouts";
		   	
		   }
		   
		  public static String setDiretoryNoRegOptimization(String mainpath,String namemetabolite, int nknockouts, String optimizationalgorithm, String objtype){
		   	
		   	return mainpath+"/"+namemetabolite+"/"+objtype+"/"+optimizationalgorithm+"/"+String.valueOf(nknockouts)+"_MaxKnockouts";
		   	
		   }
		  
		  
		  
		  
			public static String getFileName(String namemetabolite, int nknockouts, String optimizationalgorithm, String objtype){
				
				return namemetabolite+"_"+String.valueOf(nknockouts)+"maxknocks"+"_"+optimizationalgorithm+"_"+objtype;	
		}
		  
		  
		  
		  
	

}
