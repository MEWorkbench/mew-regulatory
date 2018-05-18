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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.metabolicandregulatorynetworks;

import java.io.File;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.MatrixEnum;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.FlatFilesReader;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.RegulatoryContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.interfaces.IContainerIntegratedModelBuilder;
import pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components.IRegulatoryNetworkReader;

public class FlatFileIntegratedModelReader extends FlatFilesReader implements IContainerIntegratedModelBuilder{
     
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected RegulatoryContainer regulatorymodelcontainer= null;
	
	public FlatFileIntegratedModelReader(File reactionsFile, File matrixFile, File metabolitesFile, File genesFile, String modelID, MatrixEnum mt, String userReactionsDelimiter, String userMetabolitesDelimiter, String userMatrixDelimiter, IRegulatoryNetworkReader reader) throws Exception{
		super(reactionsFile,matrixFile,metabolitesFile, genesFile, modelID, mt, userReactionsDelimiter, userMetabolitesDelimiter,userMatrixDelimiter );
		loadRegulatoryContainer(reader);
	}
    
	public FlatFileIntegratedModelReader(String reactionsFilePath, String matrixFilePath, String metabolitesFilePath, String genesFilePath, String modelID, MatrixEnum mt, String userReactionsDelimiter, String userMetabolitesDelimiter, String userMatrixDelimiter, IRegulatoryNetworkReader reader) throws Exception{
		super(reactionsFilePath,matrixFilePath,metabolitesFilePath, genesFilePath, modelID, mt, userReactionsDelimiter, userMetabolitesDelimiter,userMatrixDelimiter );
		loadRegulatoryContainer(reader);
	}
	
	
	
	protected void loadRegulatoryContainer(IRegulatoryNetworkReader reader ) throws Exception{
		regulatorymodelcontainer = new RegulatoryContainer(reader);
	}
	

	
	@Override
	public RegulatoryContainer getRegulatoryContainer() {
		return regulatorymodelcontainer;
	}
	 
   

	
	
	
}
