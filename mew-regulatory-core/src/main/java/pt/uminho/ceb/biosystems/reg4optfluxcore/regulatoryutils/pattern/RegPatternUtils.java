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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegPatternUtils {
	
	
	public static String getGeneAssociationRuleOnly(String rule){
		 
		 Pattern pat = Pattern.compile("GENE_ASSOCIATION:\\s+((\\()*(\\w+(\\s+\\w+)*)(\\))*)");

		 Matcher match=pat.matcher(rule);
		 
		 if(match.matches()){
			 String found = match.group(3);
			 return found;
		 }
		 
		 
		 return null;
	 }
	
	
	
	

}
