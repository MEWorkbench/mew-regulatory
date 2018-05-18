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

package pt.uminho.ceb.biosystems.reg4optfluxcore.container.components;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.GeneCI;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.GeneExtinfo;

public class RegulatoryGeneCI extends GeneCI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String productid;
	

	public RegulatoryGeneCI(GeneCI geneCI) {
		super(geneCI);
	}
	
	public RegulatoryGeneCI(String geneId, String geneName) {
		super(geneId,geneName);
	}
	
	public RegulatoryGeneCI(String geneId, String geneName, String productid) {
		super(geneId,geneName);
		this.productid=productid;
	}
	
	public Gene convertToExtendedGeneInfo(){
		return new GeneExtinfo(this.geneId, this.geneName, this.productid);
	}
	
	
    public static Gene convertToGeneExtendedInfo(RegulatoryGeneCI gene){
		return gene.convertToExtendedGeneInfo();
	}

}
