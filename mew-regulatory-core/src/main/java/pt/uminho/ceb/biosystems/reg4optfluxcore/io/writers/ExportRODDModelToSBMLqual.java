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
package pt.uminho.ceb.biosystems.reg4optfluxcore.io.writers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.ConnectivityMatrix;
import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.io.sbml.SBMLQualBundle;
import org.colomoto.logicalmodel.io.sbml.SBMLqualHelper;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ASTNode.Type;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.qual.FunctionTerm;
import org.sbml.jsbml.ext.qual.Input;
import org.sbml.jsbml.ext.qual.InputTransitionEffect;
import org.sbml.jsbml.ext.qual.OutputTransitionEffect;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.qual.Sign;
import org.sbml.jsbml.ext.qual.Transition;

import pt.ornrocha.collections.MTUMapUtils;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;

public class ExportRODDModelToSBMLqual{

	private final LogicalModel model;
	private final ConnectivityMatrix matrix;
	private final MDDManager ddmanager;
	
	private final SBMLQualBundle qualBundle;

	private final List<NodeInfo> coreNodes;
	private Map<String,String> nodename2nodeid;
	private final PathSearcher searcher;
	
	private Map<NodeInfo, QualitativeSpecies> node2species = new HashMap<NodeInfo, QualitativeSpecies>();
	private String[] coreIDS;
	private boolean needFilled = true;
	
	private String tr_prefix = "tr_";
	
	@SuppressWarnings("unchecked")
	public ExportRODDModelToSBMLqual(IRODDRegulatoryModel model) throws InstantiationException, IllegalAccessException {
		LogicalModel lgmodel=(LogicalModel) model;
		this.model = lgmodel;
		this.ddmanager = lgmodel.getMDDManager();
		this.searcher = new PathSearcher(ddmanager, true);
		this.matrix = new ConnectivityMatrix(lgmodel);
		this.coreNodes = lgmodel.getNodeOrder();
		setMapNodename2NodeId(model.getElementId2Index());

		this.qualBundle = SBMLqualHelper.newBundle();
		
	}
	
	@SuppressWarnings("unchecked")
	protected void setMapNodename2NodeId(Map<String,Integer> nodeid2index) throws InstantiationException, IllegalAccessException{
		Map<Integer,String> nodeindex2nodeid =MTUMapUtils.invertMap(nodeid2index);
		nodename2nodeid=new LinkedHashMap<>();
		
		for (int i = 0; i < coreNodes.size(); i++) {
			String nodename=coreNodes.get(i).getNodeID();
			String nodeid=nodeindex2nodeid.get(i);
			nodename2nodeid.put(nodename, nodeid);
		}
	
	}
	
	public SBMLDocument getSBMLDocument() {
		return getSBMLBundle().document;
	}
	
	protected SBMLQualBundle getSBMLBundle() {
		ensureFilled();
		return qualBundle;
	}
	
	
	/**
	 * Make sure that transition IDs can not conflict with species IDs
	 */
	private void ensureTransitionPrefix() {
		
		for (NodeInfo ni: coreNodes) {
			String curID = ni.getNodeID();
			while (curID.startsWith(tr_prefix)) {
				tr_prefix += "_";
			}
		}
		for (NodeInfo ni: model.getExtraComponents()) {
			String curID = ni.getNodeID();
			while (curID.startsWith(tr_prefix)) {
				tr_prefix += "_";
			}
		}
	}
	
	
	public synchronized void ensureFilled() {
		if (needFilled) {
			ensureTransitionPrefix();
			
			needFilled = false;
			// add a compartment
			//Compartment comp1 = qualBundle.model.createCompartment("comp1");
			//comp1.setConstant(true);
			
			// add qualitative species
			List<NodeInfo> nodes = coreNodes;
			coreIDS = new String[coreNodes.size()];
			int[] functions = model.getLogicalFunctions();
			
			for (int i=0 ; i<functions.length ; i++) {
				NodeInfo ni = nodes.get(i);
				
				String curID = ni.getNodeID();
				String nodeid=nodename2nodeid.get(curID);
				coreIDS[i] = curID;
				
				QualitativeSpecies sp = qualBundle.qmodel.createQualitativeSpecies(nodeid);
				if(curID!=nodeid)
					sp.setName(curID);
				sp.setMaxLevel( ni.getMax());
				node2species.put(ni, sp);
				
				if (ni.isInput()) {
					sp.setConstant(true);
					// TODO: check consistency between function and input role?
				} else {
					sp.setConstant(false);
				}

			}
			
			// add transitions
			for (int i=0 ; i<functions.length ; i++) {
				NodeInfo ni = nodes.get(i);
				if (!ni.isInput()) {
					addTransition(nodes.get(i), functions[i], matrix.getRegulators(i, false));
				}
			}
			
			// add species and transitions for extra nodes as well
			nodes = model.getExtraComponents();
			functions = model.getExtraLogicalFunctions();
			for (int i=0 ; i<functions.length ; i++) {
				NodeInfo ni = nodes.get(i);
				int function = functions[i];

				String curID = ni.getNodeID();
				QualitativeSpecies sp = qualBundle.qmodel.createQualitativeSpecies(curID);
				sp.setConstant(false);
				node2species.put(ni, sp);
				if (ni.isInput()) {
					sp.setConstant(true);
				}
				
				// add its transition
				addTransition(ni, function, matrix.getRegulators(i, true));
				i++;
			}
		}
	}
	
	
	 /**
     * Apply an initial condition to the exported model.
     *
     * @param state the initial levels for each core component (negative values for unspecified)
     */
    public void setInitialCondition(byte[] state) {
        ensureFilled();

        for (int idx=0 ; idx<state.length ; idx++) {
            int v = state[idx];
            if (v < 0) {
                continue;
            }

            NodeInfo ni = coreNodes.get(idx);
            QualitativeSpecies species = getSpecies(ni);
            if (species != null) {
                species.setInitialLevel(v);
            }
        }
    }
	
	public QualitativeSpecies getSpecies(NodeInfo ni) {
			return node2species.get(ni);
	}
	
	private void addTransition(NodeInfo ni, int function, int[] regulators) {
		
		String nodeid=nodename2nodeid.get(ni.getNodeID());
		
		String trID = tr_prefix+nodeid;
		Transition tr = qualBundle.qmodel.createTransition(trID);
		tr.createOutput(trID+"_out", node2species.get(ni), OutputTransitionEffect.assignmentLevel);
		
		if (ddmanager.isleaf(function)) {
			// only add a default term
			FunctionTerm fterm = new FunctionTerm();
			fterm.setDefaultTerm(true);
			fterm.setResultLevel(function);
			tr.addFunctionTerm(fterm);
			return;
		}
		
		for (int idx: regulators) {
			NodeInfo ni_reg = coreNodes.get(idx);
			//String niid_reg=nodename2nodeid.get(ni_reg);
			Input in = tr.createInput(trID+"_in_"+idx, node2species.get(ni_reg), InputTransitionEffect.none);
			
			// determine the sign of the regulation
			Sign sign = Sign.unknown;
			MDDVariable regVar = ddmanager.getVariableForKey(ni_reg);
			switch (ddmanager.getVariableEffect(regVar, function)) {
			case DUAL:
				sign = Sign.dual;
				break;
			case POSITIVE:
				sign = Sign.positive;
				break;
			case NEGATIVE:
				sign = Sign.negative;
				break;
			}
			in.setSign(sign);
		}
		
		
		// start with a default to 0
		FunctionTerm fterm = new FunctionTerm();
		fterm.setDefaultTerm(true);
		fterm.setResultLevel(0);
		tr.addFunctionTerm(fterm);
		
		// extract others from the actual functions
		ASTNode[] orNodes = new ASTNode[ni.getMax()+1];
		int[] path = searcher.setNode(function);
		int[] tmax = searcher.getMax();
		for (int leaf: searcher) {
			if (leaf == 0) {
				continue;
			}
			
			// build a condition for this path
			ASTNode andNode = new ASTNode(ASTNode.Type.LOGICAL_AND);
			for (int i=0 ; i<path.length ; i++) {
				
				String astnodename=nodename2nodeid.get(coreIDS[i]);
					if(astnodename==null)
						astnodename=coreIDS[i];
				
				int cst = path[i];
				if (cst < 0) {
					continue;
				}
				
				int max = tmax[i];
				if (max >= 0 && max < cst) {
					System.err.println("############## wrong max?");
					continue;
				}
				
				if (max == cst) {
					// constrain to a single value
					ASTNode constraintNode = new ASTNode(ASTNode.Type.RELATIONAL_EQ);
					constraintNode.addChild( new ASTNode(astnodename) );
					constraintNode.addChild( new ASTNode(cst) );
					andNode.addChild(constraintNode);
				} else {
					// constrain to a range
					
					if (cst > 0) {
						ASTNode constraintNode = new ASTNode(ASTNode.Type.RELATIONAL_GEQ);
						
						constraintNode.addChild( new ASTNode(astnodename) );
						constraintNode.addChild( new ASTNode(cst) );
						andNode.addChild(constraintNode);
					}
					
					if (max > 0) {
						ASTNode constraintNode = new ASTNode(ASTNode.Type.RELATIONAL_LEQ);
						constraintNode.addChild( new ASTNode(astnodename) );
						constraintNode.addChild( new ASTNode(max) );
						andNode.addChild(constraintNode);
					}
				}
			}
			
			// remove the and if only one constraint is defined
			if (andNode.getChildCount() == 1) {
				andNode = andNode.getChild(0);
			}

			
			ASTNode orNode = orNodes[leaf];
			if (orNode == null) {
				orNodes[leaf] = andNode;
			} else {
				if (orNode.getType() != Type.LOGICAL_OR) {
					ASTNode oldOrNode = orNode;
					orNode = new ASTNode(Type.LOGICAL_OR);
					orNode.addChild(oldOrNode);
					orNodes[leaf] = orNode;
				}
				orNode.addChild(andNode);
			}
			
		}
		
		// add all function terms
		for (int level=1 ; level<orNodes.length ; level++) {
			ASTNode math = orNodes[level];
			if (math == null) {
				continue;
			}
			FunctionTerm ft = new FunctionTerm();
			ft.setResultLevel(level);
			ft.setMath(math);
			tr.addFunctionTerm(ft);
		}
	}
	

}
