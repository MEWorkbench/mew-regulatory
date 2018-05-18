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
package pt.uminho.ceb.biosystems.reg4optfluxcore.converters;



import pt.uminho.ceb.biosystems.mew.core.model.converters.ContainerConverter;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.IntegratedModelContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.container.RegulatoryContainer;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IntegratedSteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.logicalmodel.converters.OptfluxRegulatoryModelToBDDLogicalModelConverter;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.IOptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat.OptfluxRegulatoryModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.stoichiometricgprs.StoichiometricGPRSteadyStateModel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.stoichiometricgprs.convert.SteadyStateGeneModelToStoichGPRConverter;

public class IntegratedModelConverter extends ContainerConverter {
	
	private IntegratedModelContainer container;
	private boolean usegprstoichiometry=false;
	
	public IntegratedModelConverter(IntegratedModelContainer container) {
		super(container);
		this.container=container;
	}
	
	
	public void setUseOfGPRStoichiometry(boolean v) {
		this.usegprstoichiometry=v;
	}
	
	
	

	public IntegratedModelContainer getContainer() {
		return container;
	}


	public static IOptfluxRegulatoryModel getOptfluxRegulatoryModelFormat(RegulatoryContainer regulatorycontainer)throws InstantiationException, IllegalAccessException{
		IOptfluxRegulatoryModel regmodel=null;
		
		if(regulatorycontainer.isStrictlybooleanmodel()){
			regmodel=new OptfluxRegulatoryModel(regulatorycontainer.getModelName(), 
					regulatorycontainer.getRegulatoryGenes(), 
					regulatorycontainer.getRegulatorygeneRules(), 
					regulatorycontainer.getRegulatoryVariables(), 
					regulatorycontainer.getGeneId2RuleID(), 
					/*regulatorycontainer.getVariablesType(), */
					regulatorycontainer.getUnconstrainedGenes(),
					regulatorycontainer.isGenesidlinkbyruleid());
		}

		return regmodel;
	}
	
	
	public static IRODDRegulatoryModel getRODDRegulatoryModelFormat(RegulatoryContainer regulatorycontainer) throws InstantiationException, IllegalAccessException, Exception{

		IRODDRegulatoryModel robddmodel=null;
		
		if(regulatorycontainer.getLogicalmodel()!=null)
			robddmodel=regulatorycontainer.getLogicalmodel();
		else{
			if(regulatorycontainer.isStrictlybooleanmodel())
				robddmodel=new OptfluxRegulatoryModelToBDDLogicalModelConverter(getOptfluxRegulatoryModelFormat(regulatorycontainer)).convertModel();
			else
				throw new Exception("Not implemented yet");
		}

		return robddmodel;
	}
	
	
	
	
	
	protected IOptfluxRegulatoryModel getOptfluxRegulatoryModelFormat() throws InstantiationException, IllegalAccessException{
		return getOptfluxRegulatoryModelFormat(container.getRegulatoryModelcontainer());
	}
	
	
	protected IRODDRegulatoryModel getRODDRegulatoryModelFormat() throws InstantiationException, IllegalAccessException, Exception{
		return getRODDRegulatoryModelFormat(container.getRegulatoryModelcontainer());
	}
	
	
	
	
	public IntegratedSteadyStateModel getConvertedIntegratedModel() throws Exception{

		IntegratedSteadyStateModel integratedmodel =null;
		if(usegprstoichiometry) {
			ISteadyStateGeneReactionModel origmodel=convertToGeneReactionModel();
			SteadyStateGeneModelToStoichGPRConverter convertertogprstoic=new SteadyStateGeneModelToStoichGPRConverter(origmodel, container);
			StoichiometricGPRSteadyStateModel newmodel=convertertogprstoic.convertModel();
			integratedmodel=new IntegratedSteadyStateModel(newmodel, getOptfluxRegulatoryModelFormat(), getRODDRegulatoryModelFormat());
			integratedmodel.setBiomassFlux(origmodel.getBiomassFlux());
			RegulatoryContainer regulatorycontainer=container.getRegulatoryModelcontainer();
            this.container=convertertogprstoic.getModelContainer();
            this.container.setRegcontainer(regulatorycontainer);
		}
		else {
			getSSGeneModelInfoFromContainer();
			getSSModelInfoFromContainer();

			integratedmodel = new IntegratedSteadyStateModel(container.getModelName(), 
					smatrix, 
					reactions, 
					metabolites, 
					compartments, 
					pathways, 
					genes, 
					null, 
					geneReactionRules, 
					null, 
					getOptfluxRegulatoryModelFormat(),
					getRODDRegulatoryModelFormat());

			integratedmodel.setBiomassFlux(container.getBiomassId());
		}

		return integratedmodel;

	}
	


	public static IntegratedSteadyStateModel convertToIntegratedModel(IntegratedModelContainer container) throws Exception{
		return new IntegratedModelConverter(container).getConvertedIntegratedModel();
	}
	
	
	public static IntegratedSteadyStateModel convertToIntegratedModel(ISteadyStateGeneReactionModel metabolicmodel, RegulatoryContainer regulatorycontainer) throws InstantiationException, IllegalAccessException, Exception{
		if(regulatorycontainer==null) {
			IOptfluxRegulatoryModel optfluxregulatoryformat=OptfluxRegulatoryModel.getEmptyInstance();
			IRODDRegulatoryModel bddregulatoryformat=OptfluxRegulatoryModelToBDDLogicalModelConverter.convertToBDDLogicalModel(optfluxregulatoryformat);
			
			return new IntegratedSteadyStateModel(metabolicmodel, optfluxregulatoryformat, bddregulatoryformat);
		}
		else
			return new IntegratedSteadyStateModel(metabolicmodel, getOptfluxRegulatoryModelFormat(regulatorycontainer), getRODDRegulatoryModelFormat(regulatorycontainer));
	}
	
	
	
	

}
