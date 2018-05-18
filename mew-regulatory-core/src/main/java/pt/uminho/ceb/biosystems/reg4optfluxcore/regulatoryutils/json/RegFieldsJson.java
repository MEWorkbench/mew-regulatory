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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.json;

public class RegFieldsJson {
	
	public static String REACTIONS_IDS="REACTIONS_IDS";
	public static String METABOLITES_IDS="METABOLITES_IDS";
	
	public static String SIMPLENAMETAG="SIMPLE_NAME";
	public static String COVERTNAMETAG="COVERT_NAME";
	
	public static String TAG_904_FLATFILE="904 Flat_File";
	public static String TAG_904_SBML="904 SBML";
	public static String TAG_1260_SBML="1260 SBML";
	public static String TAG_1366_SBML="1366 SBML";
	
	public static String MAP_REAC_METB_IDS_TAG="ALL_REACTs_METBs_IDS_MAPPING";
	public static String FIXED_COND_REAC_METB_IDS="FIXED_COND_REACTs_METBs_IDS_MAPPING";
	public static String CARBON_SOURCES="CARBON_SOURCES";
	public static String CARBON_SOURCES_VALUES="CARBON_SOURCES_VALUES";
	public static String NITROGEN_SOURCES="NITROGEN_SOURCES";
	public static String NITROGEN_SOURCES_VALUES="NITROGEN_SOURCES_VALUES";
	public static String FIXED_CARBON_SOURCE_VALUES="FIXED_CARBON_SOURCE_VALUES";
	public static String FIXED_NITROGEN_SOURCE_VALUES="FIXED_NITROGEN_SOURCE_VALUES";
	public static String COVERT_CONSTRAINTS_VALUES="COVERT_CONSTRAINTS_VALUES";
	
	
	public static String NITROGENSOURCETAG="Nitrogen Source";
	public static String CARBONSOURCETAG="Carbon Source";
	
	public static String LOWERLIMIT="LOWER_LIMIT";
	public static String UPPERLIMIT="UPPER_LIMIT";
	
	
	public static String GLUCOSECOVERTID="OD600 growth on a-D-Glucose";
	public static String GLUCOSESIMPLEID="a-D-Glucose";
	
	
	public static String GENENAMEINFO="GENE_INFO_BY_NAME";
	public static String GENEBNUMBERINFO="GENE_INFO_BY_BNUMBER";
	public static String GENENAME="GENE_NAME";
	public static String GENEBNUMBER="GENE_BNUMBER";
	public static String GENETFASSOCIATE="GENE_ASSOCIATED_TF";
	
	public static String REGULONTFTFINTERCTIONS="REGULON_TF_TF_INTERACTIONS";
	public static String REGULONTFGENEINTERACTIONS="REGULON_TF_GENE_INTERACTIONS";

}
