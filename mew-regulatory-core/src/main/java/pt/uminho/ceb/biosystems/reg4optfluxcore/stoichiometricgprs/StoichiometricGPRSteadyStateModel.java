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
package pt.uminho.ceb.biosystems.reg4optfluxcore.stoichiometricgprs;

import java.util.Map;

import pt.uminho.ceb.biosystems.mew.core.model.components.Compartment;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.core.model.components.GeneReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.IStoichiometricMatrix;
import pt.uminho.ceb.biosystems.mew.core.model.components.Metabolite;
import pt.uminho.ceb.biosystems.mew.core.model.components.Pathway;
import pt.uminho.ceb.biosystems.mew.core.model.components.Protein;
import pt.uminho.ceb.biosystems.mew.core.model.components.ProteinReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.Reaction;
import pt.uminho.ceb.biosystems.mew.core.model.exceptions.InvalidSteadyStateModelException;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.SteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;

public class StoichiometricGPRSteadyStateModel extends SteadyStateGeneReactionModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StoichiometricGPRSteadyStateModel(String modelId, IStoichiometricMatrix stoichiometricMatrix,
			IndexedHashMap<String, Reaction> reactions, IndexedHashMap<String, Metabolite> metabolites,
			Map<String, Compartment> compartments, IndexedHashMap<String, Pathway> pathways,
			IndexedHashMap<String, Gene> genes, IndexedHashMap<String, Protein> proteins,
			IndexedHashMap<String, GeneReactionRule> geneReactionRules,
			IndexedHashMap<String, ProteinReactionRule> proteinReactionRules) throws InvalidSteadyStateModelException {
		super(modelId, stoichiometricMatrix, reactions, metabolites, compartments, pathways, genes, proteins, geneReactionRules,
				proteinReactionRules);

	}





}
