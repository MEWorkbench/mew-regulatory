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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components;

import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;

public class GeneExtinfo extends Gene{

	private static final long serialVersionUID = 1L;
	
	private String productid;
	
	
	
	public GeneExtinfo(String id){
		super(id);
	}
	
	public GeneExtinfo(String id, String name){
		super(id, name);
	}
	
	public GeneExtinfo(String id, String name, String product){
		super(id, name);
		this.productid=product;
		
	}

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}
	
	
	

}
