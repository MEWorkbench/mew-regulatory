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
package pt.uminho.ceb.biosystems.reg4optfluxcore.stoichiometricgprs.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.javatuples.Pair;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import pt.uminho.ceb.biosystems.booleanutils.astutils.extract.ASTExtractUtils;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.CompartmentCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.GeneCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.MetaboliteCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionConstraintCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.StoichiometryValueCI;
import pt.uminho.ceb.biosystems.mew.core.model.components.ColtSparseStoichiometricMatrix;
import pt.uminho.ceb.biosystems.mew.core.model.components.Compartment;
import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.core.model.components.GeneReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.IStoichiometricMatrix;
import pt.uminho.ceb.biosystems.mew.core.model.components.Metabolite;
import pt.uminho.ceb.biosystems.mew.core.model.components.Pathway;
import pt.uminho.ceb.biosystems.mew.core.model.components.Protein;
import pt.uminho.ceb.biosystems.mew.core.model.components.ProteinReactionRule;
import pt.uminho.ceb.biosystems.mew.core.model.components.Reaction;
import pt.uminho.ceb.biosystems.mew.core.model.components.ReactionConstraint;
import pt.uminho.ceb.biosystems.mew.core.model.components.enums.ReactionType;
import pt.uminho.ceb.biosystems.mew.core.model.exceptions.InvalidSteadyStateModelException;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.gpr.ISteadyStateGeneReactionModel;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTree;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.TreeUtils;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.DataTypeEnum;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.reg4optfluxcore.stoichiometricgprs.ContainerStoichiometricGPRmodel;
import pt.uminho.ceb.biosystems.reg4optfluxcore.stoichiometricgprs.StoichiometricGPRSteadyStateModel;

public class SteadyStateGeneModelToStoichGPRConverter {


	private ISteadyStateGeneReactionModel origmodel;
	private Container origmodelcontainer;

	private String genemetabprefix="G_";
	private String genereactprefix="RG_";
	private int numberreactionswithoutgenes=0;

	// model
	private IStoichiometricMatrix stoichmatrix;
	private IndexedHashMap<String, Reaction> reactions;
	private IndexedHashMap<String, Metabolite> metabolites;
	private Map<String, Compartment> compartments;
	private IndexedHashMap<String, Pathway> pathways;
	private IndexedHashMap<String, Gene> genes;
	private IndexedHashMap<String, Protein> proteins;
	private IndexedHashMap<String, GeneReactionRule> geneReactionRules;
	private IndexedHashMap<String, ProteinReactionRule> proteinReactionRules;
	//private HashMap<String,ArrayList<String>> geneReactionMapping = null;

	private IndexedHashMap<String, ReactionConstraintCI> tempreactionsConstraintstocontainer;

	private IndexedHashMap<String, ArrayList<Pair<String, Double>>> reactionProducts;
	private IndexedHashMap<String, ArrayList<Pair<String, Double>>> reactionReactants;
	// model container


	protected Map<String, ReactionCI> contreactions;
	protected Map<String, MetaboliteCI> contmetabolites;
	protected Map<String, CompartmentCI> contcompartments;
	protected Map<String, GeneCI> contgenes;

	protected Map<String, ReactionConstraintCI> contdefaultEC;

	private ContainerStoichiometricGPRmodel newmodelcontainer;
	private List<String> drains;


	// Irreversible model components

	private IndexedHashMap<String, Reaction> irrevreactionsmap;
	private IndexedHashMap<String, Pair<String, Boolean>> irrevreacid2origreactid; // map new irrev reaction id to original, true is forward reaction , false is backward reaction
	private IndexedHashMap<String, Pair<String, String>> rev2irrevreactidsmap; 

	private IndexedHashMap<String,Boolean> mapreactionsreversible;
	private IndexedHashMap<String,String> mapextendedreactionsids2origreactid;

	// check GPR rules

	private IndexedHashMap<String, ArrayList<AbstractSyntaxTree<DataTypeEnum, IValue>>> react2listgprrules;
	private IndexedHashMap<String,Boolean> react2haveisoenzmap;


	// check genes 
	private IndexedHashMap<String, String> geneid2genemetabid;
	private IndexedHashMap<String, ArrayList<String>> newreactionsids2assocgeneids; // add gprs as reactions
	private IndexedHashMap<String, String> genereactid2geneid;
	private IndexedHashMap<String, String> genereactid2genemetabid;
	private IndexedHashMap<String, String> genemetabid2genereactid;


	private IndexedHashMap<String,ArrayList<Pair<String, Double>>> mapoldreaction2metabolites;


	public SteadyStateGeneModelToStoichGPRConverter(ISteadyStateGeneReactionModel model, Container origmodelcontainer) {
		this.origmodel=model;
		this.origmodelcontainer=origmodelcontainer;
	}






	public StoichiometricGPRSteadyStateModel convertModel() throws InvalidSteadyStateModelException {

		buildNewStoichiometricMatrix();
		buildContainer();

		String modelid=origmodel.getId()+"_mod_gprs";



		StoichiometricGPRSteadyStateModel newmodel= new StoichiometricGPRSteadyStateModel(modelid, 
				stoichmatrix, 
				reactions, 
				metabolites, 
				compartments, 
				pathways, 
				genes, 
				proteins,
				geneReactionRules, 
				proteinReactionRules);

		newmodel.setBiomassFlux(origmodel.getBiomassFlux());
		return newmodel;
	}

	public ContainerStoichiometricGPRmodel getModelContainer() {
		return newmodelcontainer;
	}


	private void buildNewStoichiometricMatrix() {

		drains=origmodel.identifyDrainReactionsFromStoichiometry();
		generateIrreversivelReactionSet();
		processGPRRules();
		copyModelComponents();
		setGeneToMetabolites();
		extendReactionsWithGeneInfo();
		setGeneAsReactions();
		extractMapReactionMetaboliteStoichiometryOriginalModel();



		SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(metabolites.size(),reactions.size());

		reactionProducts=new IndexedHashMap<>();
		reactionReactants=new IndexedHashMap<>();



		for (int i = 0; i < numberreactionswithoutgenes; i++) {

			String extreactid=reactions.getKeyAt(i);
			String origid=mapextendedreactionsids2origreactid.get(extreactid);


			ArrayList<Pair<String, Double>> products=new ArrayList<>();
			ArrayList<Pair<String, Double>> reactants=new ArrayList<>();

			boolean isrev=mapreactionsreversible.get(extreactid);
			boolean isdrain=drains.contains(origid);
			ArrayList<Pair<String, Double>> assocmetabs=mapoldreaction2metabolites.get(origid);


			for (int j = 0; j < assocmetabs.size(); j++) {

				String metabid=assocmetabs.get(j).getValue0();

				if(metabid.endsWith("_b"))
					continue;

				else {
					double val=assocmetabs.get(j).getValue1();


					if(isrev && !isdrain)
						val=-1*val;


					matrix.set(metabolites.getIndexOf(metabid), reactions.getIndexOf(extreactid), val);	


					if(isdrain && isrev)
						val=-1*val;

					if(val<0.0)
						reactants.add(new Pair<String, Double>(metabid, -1*val));
					else
						products.add(new Pair<String, Double>(metabid, val));
				}

			}

			ArrayList<String> assocgenes=newreactionsids2assocgeneids.get(extreactid);

			if(assocgenes!=null && assocgenes.size()>0) {

				for (int k = 0; k < assocgenes.size(); k++) {
					String geneid=assocgenes.get(k);
					String genemetabid=geneid2genemetabid.get(geneid);

					int mindex=metabolites.getIndexOf(genemetabid);

					if(mindex>0) {
						matrix.set(mindex,reactions.getIndexOf(extreactid),-1.0);
						reactants.add(new Pair<String, Double>(genemetabid, 1.0));
					}
				}
			}

			reactionProducts.put(extreactid, products);
			reactionReactants.put(extreactid, reactants);

		}


		for (int i = numberreactionswithoutgenes; i < reactions.size(); i++) {

			String genereactid=reactions.getKeyAt(i);
			matrix.set(metabolites.getIndexOf(genereactid2genemetabid.get(genereactid)), reactions.getIndexOf(genereactid), -1.0);

			Pair<String, Double> prod=new Pair<String, Double>(genereactid2genemetabid.get(genereactid), 1.0);
			ArrayList<Pair<String, Double>> products=new ArrayList<>();
			products.add(prod);
			ArrayList<Pair<String, Double>> reactants=new ArrayList<>();

			reactionProducts.put(genereactid, products);
			reactionReactants.put(genereactid, reactants);

		}

		stoichmatrix=new ColtSparseStoichiometricMatrix(matrix);

	}


	private void generateIrreversivelReactionSet(){

		irrevreactionsmap=new IndexedHashMap<>();
		irrevreacid2origreactid=new IndexedHashMap<>();
		rev2irrevreactidsmap=new IndexedHashMap<>();

		IndexedHashMap<String, Reaction> origreactions = origmodel.getReactions();

		for (int i = 0; i < origreactions.size(); i++) {

			String reactid=origreactions.getKeyAt(i);
			Reaction react=origreactions.getValueAt(i);

			if(react.isReversible() && react.getConstraints().getLowerLimit()<0.0 && react.getConstraints().getUpperLimit()>0.0) {

				String nameforward=reactid+"_fw";
				Reaction reactf=new Reaction(nameforward, false,react.getType(), Math.max(0.0, react.getConstraints().getLowerLimit()), Math.max(0.0, react.getConstraints().getUpperLimit()));
				Pair<String, Boolean> reactforward=new Pair<String, Boolean>(reactid, true);
				irrevreacid2origreactid.put(nameforward, reactforward);
				irrevreactionsmap.put(nameforward, reactf);

				String namebackward=reactid+"_bw";
				Reaction reactb=new Reaction(namebackward, false,react.getType(), Math.max(-react.getConstraints().getUpperLimit(), 0.0), Math.max(-react.getConstraints().getLowerLimit(),0.0));
				Pair<String, Boolean> reactbackward=new Pair<String, Boolean>(reactid, false);
				irrevreacid2origreactid.put(namebackward, reactbackward);
				irrevreactionsmap.put(namebackward, reactb);

				rev2irrevreactidsmap.put(reactid, new Pair<String, String>(nameforward, namebackward));

			}
			else {

				Reaction reactcopy=new Reaction(reactid, false,react.getType(), react.getConstraints().getLowerLimit(),react.getConstraints().getUpperLimit());
				irrevreactionsmap.put(reactid, reactcopy);

			}


		}

	}


	/**
	 * Will split rules with IsoEnzymes (OR operator) or preserve rules without IsoEnzymes
	 */
	private void processGPRRules() {

		react2listgprrules=new IndexedHashMap<>();
		react2haveisoenzmap=new IndexedHashMap<>();

		IndexedHashMap<String, GeneReactionRule> origgeneReactionRules=origmodel.getGeneReactionRules();

		for (int i = 0; i < origgeneReactionRules.size(); i++) {
			String reactid=origgeneReactionRules.getKeyAt(i);
			GeneReactionRule gprrule=origgeneReactionRules.getValueAt(i);

			ArrayList<AbstractSyntaxTree<DataTypeEnum, IValue>> listnodes=null;
			if(gprrule.getRule()!=null) {
				listnodes= ASTExtractUtils.getORNodeChildList(gprrule.getRule(),true);
				if(listnodes.size()>0) {
					react2listgprrules.put(reactid, listnodes);
					react2haveisoenzmap.put(reactid, true);
				}
				else {
					listnodes=new ArrayList<>();
					listnodes.add(gprrule.getRule().copy());
					react2listgprrules.put(reactid,listnodes);
					react2haveisoenzmap.put(reactid, false);
				}

			}
		}

	}


	private void copyModelComponents() {

		compartments=new HashMap<String,Compartment>();

		Map<String,Compartment> origcompartments=origmodel.getCompartments();

		for (String idcomp : origcompartments.keySet()) {

			Compartment orcomp=origcompartments.get(idcomp);

			Compartment newcomp=new Compartment(orcomp.getId(), orcomp.isExternal(),orcomp.getSize());
			ArrayList<String> metabcopy=new ArrayList<String>(orcomp.getMetaboliteList().size());
			Collections.copy(metabcopy, orcomp.getMetaboliteList());
			newcomp.setMetaboliteList(metabcopy);

			compartments.put(idcomp, newcomp);
		}



		metabolites=new IndexedHashMap<>();

		IndexedHashMap<String, Metabolite> origmetabolites=origmodel.getMetabolites();

		for (int i = 0; i < origmetabolites.size(); i++) {
			String metabid=origmetabolites.getKeyAt(i);
			Metabolite metab=origmetabolites.getValueAt(i);

			Metabolite metabcopy=new Metabolite(metab.getId(), metab.getName(), compartments.get(metab.getCompartment().getId()),metab.isConstant(), metab.isBoundaryCondition());
			metabolites.put(metabid, metabcopy);
		}


		genes=new IndexedHashMap<>();

		IndexedHashMap<String, Gene> origgenes=origmodel.getGenes();

		for (int i = 0; i < origgenes.size(); i++) {
			String gid=origgenes.getKeyAt(i);

			genes.put(gid, new Gene(origgenes.getValueAt(i).getId(), origgenes.getValueAt(i).getName()));

		}


	}


	private void setGeneToMetabolites() {

		Compartment genecomp=new Compartment("Genes", false);
		compartments.put("Genes", genecomp);

		geneid2genemetabid=new IndexedHashMap<>();

		IndexedHashMap<String,Gene> origenes=origmodel.getGenes();


		for (int i = 0; i < origenes.size(); i++) {

			String geneid=origenes.getKeyAt(i);

			String genemetabid=genemetabprefix+geneid;
			geneid2genemetabid.put(geneid, genemetabid);
			Metabolite genemetabelem=new Metabolite(genemetabid, genemetabid, genecomp);
			metabolites.put(genemetabid, genemetabelem);

		}
	}


	private void extendReactionsWithGeneInfo() {

		reactions=new IndexedHashMap<>();
		newreactionsids2assocgeneids=new IndexedHashMap<>();
		geneReactionRules=new IndexedHashMap<>();
		mapreactionsreversible=new IndexedHashMap<>();
		mapextendedreactionsids2origreactid=new IndexedHashMap<>();
		tempreactionsConstraintstocontainer=new IndexedHashMap<>();

		for (int i = 0; i < irrevreactionsmap.size(); i++) {

			String reactid=irrevreactionsmap.getKeyAt(i);
			Reaction react=irrevreactionsmap.getValueAt(i);


			String origreactname=null;
			Pair<String, Boolean> origidisrev=irrevreacid2origreactid.get(reactid);
			if(origidisrev!=null)
				origreactname=origidisrev.getValue0();
			else
				origreactname=reactid;


			boolean isrev=false;
			if(origidisrev!=null && !origidisrev.getValue1())
				isrev=true;

			boolean isdrain=false;
			if(origreactname!=null && drains.contains(origreactname)) {
				isdrain=true;
			}

			addReaction(reactid, origreactname, react.getConstraints(), isrev,isdrain);

		}

	}



	private void addReaction(String irreversibleid, String origid, ReactionConstraint irrevreactlimits, boolean isfromrevreact, boolean isdrain) {


		String currentname=null;
		String originalname=origmodel.getReaction(origid).getName();

		if(react2listgprrules.containsKey(origid)) {


			ArrayList<AbstractSyntaxTree<DataTypeEnum, IValue>> listrules=react2listgprrules.get(origid);

			for (int i = 0; i < listrules.size(); i++) {
				String irrevreactname=null;

				if(listrules.size()>1)
					irrevreactname=irreversibleid+"_isoenz_"+(i+1);
				else
					irrevreactname=irreversibleid;
				AbstractSyntaxTree<DataTypeEnum, IValue> rule=listrules.get(i);

				currentname=irrevreactname;

				geneReactionRules.put(irrevreactname, new GeneReactionRule(rule));

				ReactionType typereact=origmodel.getReaction(origid).getType();


				tempreactionsConstraintstocontainer.put(irrevreactname, new ReactionConstraintCI(irrevreactlimits.getLowerLimit(), irrevreactlimits.getUpperLimit()));


				Reaction newreact=new Reaction(irrevreactname, false,typereact, irrevreactlimits.getLowerLimit(), irrevreactlimits.getUpperLimit());
				if(originalname!=null)
					newreact.setName(originalname);
				
				
				reactions.put(irrevreactname, newreact);

				ArrayList<String> geneassoc=TreeUtils.withdrawVariablesInRule(rule);
				newreactionsids2assocgeneids.put(irrevreactname, geneassoc);

				mapreactionsreversible.put(irrevreactname, isfromrevreact);	
				mapextendedreactionsids2origreactid.put(irrevreactname, origid);
			}

		}
		else {

			tempreactionsConstraintstocontainer.put(irreversibleid, new ReactionConstraintCI(irrevreactlimits.getLowerLimit(), irrevreactlimits.getUpperLimit()));
			reactions.put(irreversibleid, irrevreactionsmap.get(irreversibleid));
			if(originalname!=null)
				irrevreactionsmap.get(irreversibleid).setName(originalname);
			
			mapreactionsreversible.put(irreversibleid, isfromrevreact);	
			mapextendedreactionsids2origreactid.put(irreversibleid, origid);
			currentname=irreversibleid;
		}


		if(isfromrevreact && isdrain) {
			ReactionConstraint newlimits=new ReactionConstraint(-1*irrevreactlimits.getUpperLimit(), irrevreactlimits.getLowerLimit());	
			reactions.get(currentname).setConstraints(newlimits);
		}



	}


	private void setGeneAsReactions() {

		numberreactionswithoutgenes=reactions.size();
		genereactid2geneid=new IndexedHashMap<>();
		genereactid2genemetabid=new IndexedHashMap<>();
		genemetabid2genereactid=new IndexedHashMap<>();

		for (int i = 0; i < geneid2genemetabid.size(); i++) {

			String genereactname=genereactprefix+geneid2genemetabid.getKeyAt(i);
			//Reaction genereaction=new Reaction(genereactname, false, ReactionType.DRAIN, 0.0, 1000);
			Reaction genereaction=new Reaction(genereactname, false, ReactionType.DRAIN, -1000, 0.0);
			reactions.put(genereactname, genereaction);
			genereactid2geneid.put(genereactname, geneid2genemetabid.getKeyAt(i));
			genereactid2genemetabid.put(genereactname, geneid2genemetabid.getValueAt(i));
			genemetabid2genereactid.put(geneid2genemetabid.getValueAt(i), genereactname);
		}
	}




	private void extractMapReactionMetaboliteStoichiometryOriginalModel() {

		mapoldreaction2metabolites=new IndexedHashMap<>();
		IndexedHashMap<String,Reaction> reactionMap=origmodel.getReactions();
		IndexedHashMap<String,Metabolite> metaboliteMap=origmodel.getMetabolites();


		for (int i = 0; i < reactionMap.size(); i++) {

			String reactid=reactionMap.getKeyAt(i);

			ArrayList<Pair<String, Double>> metabassoc=new ArrayList<>();

			for (int j = 0; j < metaboliteMap.size(); j++) { 
				String metabid=metaboliteMap.getKeyAt(j);
				if(origmodel.getStoichiometricValue(j,i)!=0.0) {
					double value=origmodel.getStoichiometricValue(j,i);

					Pair<String, Double> assoc=new Pair<String, Double>(metabid, value);
					metabassoc.add(assoc);

				} 
			}

			mapoldreaction2metabolites.put(reactid, metabassoc);  
		}
	}






	private void buildContainer(){

		contreactions=new HashMap<String, ReactionCI>();

		for (int i = 0; i < reactions.size(); i++) {

			String reactid=reactions.getKeyAt(i);

			Pair<Map<String, StoichiometryValueCI>, Map<String, StoichiometryValueCI>> reactionassoc=getReactionReactvsProd(reactid);

			String reactname=null;
			if(mapextendedreactionsids2origreactid.containsKey(reactid))
				reactname=origmodel.getReaction(mapextendedreactionsids2origreactid.get(reactid)).getName();
			else
				reactname=reactid;

			ReactionCI reactionci=new ReactionCI(reactid, reactname, false, reactionassoc.getValue0(), reactionassoc.getValue1()); // mudar a funcao para o nome da reaccao.
			if(geneReactionRules.containsKey(reactid))
				reactionci.setGeneRule(geneReactionRules.get(reactid).getRule());
			contreactions.put(reactid, reactionci);
		}


		contmetabolites=new HashMap<String, MetaboliteCI>();
		Map<String, MetaboliteCI> origcontmetabolites=origmodelcontainer.getMetabolites();

		for (String id : origcontmetabolites.keySet()) {
			contmetabolites.put(id, origcontmetabolites.get(id).clone());
		}

		for (int i = 0; i < geneid2genemetabid.size(); i++) {
			String genemetabid=geneid2genemetabid.getValueAt(i);
			MetaboliteCI metabci=new MetaboliteCI(genemetabid, genemetabid);
			metabci.addReaction(genemetabid2genereactid.get(genemetabid));
			metabci.setFormula("");
			contmetabolites.put(genemetabid, metabci);
		}

		contcompartments=new HashMap<String, CompartmentCI>();
		Map<String, CompartmentCI> orgcontcompartments=origmodelcontainer.getCompartments();

		for (String id : orgcontcompartments.keySet()) {

			CompartmentCI comp=orgcontcompartments.get(id);
			contcompartments.put(id, comp.clone());

		}

		CompartmentCI compci=new CompartmentCI("Genes", "Genes", "");
		compci.setMetabolitesInCompartmentID(new TreeSet<>(geneid2genemetabid.values()));
		contcompartments.put("Genes", compci);




		IndexedHashMap<String, ArrayList<String>> geneids2newreactionids =buildGeneReactionMap();
		contgenes =new HashMap<String, GeneCI>();
		Map<String, GeneCI> origcontgenes=origmodelcontainer.getGenes();

		for (String geneid : origcontgenes.keySet()) {
			GeneCI origgeneci=origcontgenes.get(geneid);

			GeneCI newgeneci=new GeneCI(origgeneci.getGeneId(), origgeneci.getGeneName());

			ArrayList<String> associreacts=geneids2newreactionids.get(geneid);
			if(associreacts!=null) {
				for (int i = 0; i < associreacts.size(); i++) {
					newgeneci.addReactionId(associreacts.get(i));
				}
			}
			contgenes.put(geneid, newgeneci);
		}


		contdefaultEC=new HashMap<String, ReactionConstraintCI>();

		for (int i = 0; i < reactions.size(); i++) {

			ReactionConstraint constraints=reactions.getValueAt(i).getConstraints();

			ReactionConstraintCI constci=null;

			if(i<numberreactionswithoutgenes) {
				constci=tempreactionsConstraintstocontainer.get(reactions.getKeyAt(i));
			}
			else
				constci=new ReactionConstraintCI(constraints.getUpperLimit(), -1*constraints.getLowerLimit());
				//constci=new ReactionConstraintCI(constraints.getLowerLimit(),constraints.getUpperLimit());


			contdefaultEC.put(reactions.getKeyAt(i), constci);
		}


		String externalcompid=origmodelcontainer.getExternalCompartmentId();

		newmodelcontainer=new ContainerStoichiometricGPRmodel();
		newmodelcontainer.setReactions(contreactions);
		newmodelcontainer.setReactionConstraints(contdefaultEC);
		newmodelcontainer.setMetabolites(contmetabolites);
		newmodelcontainer.setCompartments(contcompartments);
		newmodelcontainer.setGene(contgenes);
		newmodelcontainer.setExternalCompartmentID(externalcompid);
		newmodelcontainer.setBiomassId(origmodelcontainer.getBiomassId());
	}


	private Pair<Map<String, StoichiometryValueCI>, Map<String, StoichiometryValueCI>> getReactionReactvsProd(String reactid){

		Map<String, StoichiometryValueCI> prod=new HashMap<String, StoichiometryValueCI>();
		Map<String, StoichiometryValueCI> react=new HashMap<String, StoichiometryValueCI>();

		ArrayList<Pair<String, Double>> products=reactionProducts.get(reactid);
		ArrayList<Pair<String, Double>> reactants=reactionReactants.get(reactid);


		for (int i = 0; i < products.size(); i++) {
			String metabname=products.get(i).getValue0();
			StoichiometryValueCI stoich=new StoichiometryValueCI(metabname,products.get(i).getValue1(), metabolites.get(metabname).getCompartment().getId());
			prod.put(metabname, stoich);
		}

		for (int i = 0; i <reactants.size(); i++) {
			String metabname=reactants.get(i).getValue0();
			StoichiometryValueCI stoich=new StoichiometryValueCI(metabname,reactants.get(i).getValue1(), metabolites.get(metabname).getCompartment().getId());
			react.put(metabname, stoich);
		}

		return new Pair<Map<String,StoichiometryValueCI>, Map<String,StoichiometryValueCI>>(react, prod);
	}


	private IndexedHashMap<String, ArrayList<String>> buildGeneReactionMap(){


		IndexedHashMap<String, ArrayList<String>> res=new IndexedHashMap<>();


		for (int i = 0; i < newreactionsids2assocgeneids.size(); i++) {

			String reactid=newreactionsids2assocgeneids.getKeyAt(i);

			ArrayList<String> genesassoc=newreactionsids2assocgeneids.getValueAt(i);

			for (int j = 0; j < genesassoc.size(); j++) {
				String geneid=genesassoc.get(j);
				if(res.containsKey(geneid))
					res.get(geneid).add(reactid);
				else {
					ArrayList<String> reacts=new ArrayList<>();
					reacts.add(reactid);
					res.put(geneid, reacts);
				}
			}

		}

		return res;
	}



	public static void main(String[] args) throws Exception {

		GeneReactionRule rule=new GeneReactionRule("( b0241  or  b0929  or  b1377  or  b2215 )");

		ArrayList<AbstractSyntaxTree<DataTypeEnum, IValue>> listnodes= ASTExtractUtils.getORNodeChildList(rule.getRule(),true);
		System.out.println(listnodes);

	}


}
