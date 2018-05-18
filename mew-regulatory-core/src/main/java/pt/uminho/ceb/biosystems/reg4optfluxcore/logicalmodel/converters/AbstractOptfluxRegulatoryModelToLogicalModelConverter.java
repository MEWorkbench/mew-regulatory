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
package pt.uminho.ceb.biosystems.reg4optfluxcore.logicalmodel.converters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDOperator;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.MDDVariableFactory;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ASTNode.Type;
import org.sbml.jsbml.ext.qual.Input;
import org.sbml.jsbml.ext.qual.InputTransitionEffect;
import org.sbml.jsbml.ext.qual.Sign;

import pt.ornrocha.collections.MTUMapUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.parser.ParseException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.Regulator;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryVariable;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.bddformat.IRODDRegulatoryModel;

public abstract class AbstractOptfluxRegulatoryModelToLogicalModelConverter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IndexedHashMap<String, Regulator> modelgenes;
	protected IndexedHashMap<String, RegulatoryRule> generules;
	protected IndexedHashMap<String, RegulatoryVariable> originalmodelvariables;
	protected IndexedHashMap<String, String> geneid2ruleid;
	protected IndexedHashMap<String, String> ruleid2geneid;
	protected LinkedHashSet<String> unconstrainedgenes;
	
	protected Map<String, Integer> identifier2index;
	protected IndexedHashMap<String, Integer> name2index;
	protected Map<String, Input> m_curInputs = new HashMap<String, Input>();
	protected MDDManager ddmanager;
	protected MDDVariable[] ddvariables;
	protected MDDVariableFactory mvf = new MDDVariableFactory();
	
	protected boolean forcenodeidsinregulatoryrule=false;
	protected boolean genesInRuleLinkByRuleID=false;
	
	
	
	@SuppressWarnings("unchecked")
	public AbstractOptfluxRegulatoryModelToLogicalModelConverter(IndexedHashMap<String, Regulator> modelgenes,
            IndexedHashMap<String, RegulatoryRule> generules,
            IndexedHashMap<String, RegulatoryVariable> modelvariables,
            IndexedHashMap<String, String> geneid2ruleid,
            LinkedHashSet<String> unconstrainedgenes,
            boolean genesidlinkbyruleid){
			this.modelgenes=modelgenes;
			this.generules=generules;
			this.originalmodelvariables=modelvariables;
			this.unconstrainedgenes=unconstrainedgenes;
			this.geneid2ruleid=geneid2ruleid;
			this.genesInRuleLinkByRuleID=genesidlinkbyruleid;
			try {
				  //if(geneid2ruleid!=null)
					  this.ruleid2geneid=(IndexedHashMap<String, String>) MTUMapUtils.invertMap(geneid2ruleid);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
	}
	
	public void setForceNodeIdsInRegulatoryRule(boolean forcenodeidsinregulatoryrule) {
		this.forcenodeidsinregulatoryrule = forcenodeidsinregulatoryrule;
	}
	
	public abstract IRODDRegulatoryModel convertModel() throws Exception;
	
    
    protected List<Input> getRuleInputs(ASTNode sbmlnode, LinkedHashSet<String> ruleinputelems){
		
		ArrayList<Input> ruleinputs=new ArrayList<>();
		for (String id : ruleinputelems) {
			List<Input> eleminput=getInputInfoForNameNode(sbmlnode, id);
			if(eleminput!=null)
				ruleinputs.addAll(eleminput);
		}
		return ruleinputs;
	}
	
	
	protected List<Input> getInputInfoForNameNode(ASTNode tree, String name){
		
		
		ArrayList<Input> inputnodes=new ArrayList<>();
		List<ASTNode> nodes=getNodeNameAndValue(tree, name);
		ArrayList<ASTNode> checked=new ArrayList<>(); 
		
		for (int i = 0; i < nodes.size(); i++) {
			ASTNode node=nodes.get(i);
			
		  if(!checked.contains(node)){
			  int value=node.getRightChild().getInteger();
				   
			  Input inp=new Input(name);
			  inp.setQualitativeSpecies(name);
			  if(value==1)
				 inp.setSign(Sign.positive);
			  else if(value ==0)
				inp.setSign(Sign.negative);
			  else
				 inp.setSign(Sign.unknown);
			  
			  inp.setThresholdLevel(1);
			  inp.setTransitionEffect(InputTransitionEffect.none);
				   
			  inputnodes.add(inp);
			  checked.add(node);
		  }
		}
        if(inputnodes.size()>0)
        	return inputnodes;
		return null;
	}
	
	
	protected List<ASTNode> getNodeNameAndValue(ASTNode tree, String name){
		
		 ASTNode currentnode=null;
		 
		 ArrayList<ASTNode> nodes=new ArrayList<>();
		 
	
		 
		 if(tree.getLeftChild().getNumChildren()==0 && tree.getLeftChild().getName().equals(name)){
			 nodes.add(tree);
		 }
		 else{
			 Stack<ASTNODEContainer> nodestack=new Stack<>();
			 nodestack.push(new ASTNODEContainer(tree));
		 
		 
		 
			 while (nodestack.size()>0) {
				 ASTNODEContainer nodecontainer=nodestack.pop();
			 
				 ASTNode node=nodecontainer.getNode();
				 int childnumber=nodecontainer.getcurrentChild();
			 
				 if(childnumber < node.getChildCount()){
				 
					 nodecontainer.incrementchild();
					 nodestack.push(nodecontainer);
				 
					 ASTNode childnode=node.getChild(childnumber);
					 if(childnode.getType().equals(Type.RELATIONAL_EQ)){
						 String astid=childnode.getLeftChild().getName();
						 if(astid.equals(name)){
							 nodes.add(childnode);
						 }
					 }
				
					 currentnode=childnode;
					 nodestack.push(new ASTNODEContainer(childnode));
				 
				 }
				 else if(currentnode.getParent()!=null)
					 currentnode=(ASTNode) currentnode.getParent();
			 		
			 }
		 }
		
		return nodes;
		
		
	}
 
	protected int getIndexForName(String name) {
		Integer index =null;
		index=identifier2index.get(name);
		if(index==null)
			index=name2index.get(name);
		if (index == null) {
			throw new RuntimeException("Could not find ID: "+name);
		}
		
		return index;
	}
	
  protected int getMDDForMathML(MDDManager ddmanager, ASTNode math, int value) {
		
		Type type = math.getType();

		switch (type) {
		
		case NAME:
			String name = math.getName().trim();
			int threshold = 1;

			Input input = m_curInputs.get(name); // ter atencao isto
			if (input != null) {
				name = input.getQualitativeSpecies().trim();
				threshold = input.getThresholdLevel();
			}
			
			if (threshold < 1) {
				// not really a constraint!
				return value;
			}
			
			int index = getIndexForName(name);
			MDDVariable var = ddvariables[index];
			if (threshold >= var.nbval) {
				throw new RuntimeException("Invalid threshold in "+input);
			}
				
			if (var.nbval == 2 ) {
				return var.getNode(0, value);
			}
			int[] children = new int[var.nbval];
			for (int i=threshold ; i< var.nbval ; i++) {
				children[i] = value;
			}
			return var.getNode(children);

		case RELATIONAL_GEQ:
		case RELATIONAL_GT:
		case RELATIONAL_LEQ:
		case RELATIONAL_LT:
		case RELATIONAL_NEQ:
		case RELATIONAL_EQ:
			return getMDDForRelation(math, value);
			
		case CONSTANT_FALSE:
			return 0;
		case CONSTANT_TRUE:
			return value;

		case LOGICAL_NOT:
			if (math.getChildCount() != 1) {
				throw new RuntimeException("Invalid number of children in relation: "+math);
			}
			ASTNode child = math.getChild(0);
			int mdd = getMDDForMathML(ddmanager, child, value);
			return ddmanager.not(mdd);
		}

		
		// now we should have a logical operation or some unrecognised MathML...
		MDDOperator op = null;
		switch (type) {

		case LOGICAL_AND:
			op = MDDBaseOperators.AND;
			break;
			
		case LOGICAL_OR:
			op = MDDBaseOperators.OR;
			break;
			
		default:
			throw new RuntimeException("TODO: support MathML node for: "+math);
		}

		// if we get here, we have a recognised logical operation, hooray!
		// start by recursively identifying children!
		List<ASTNode> children = math.getChildren();
		int childCount = children.size();
		int[] childrenFunctions = new int[childCount];
		int i=0;
		for (ASTNode child: children) {
			childrenFunctions[i] = getMDDForMathML(ddmanager, child, value);
			i++;
		}


		// combine children
		switch (childCount) {
		case 0:
			throw new RuntimeException("Logical operation without children");
		case 1:
			return childrenFunctions[0];
		case 2:
			// probably the most common case
			return op.combine(ddmanager, childrenFunctions[0], childrenFunctions[1]);
		default:
			return op.combine(ddmanager, childrenFunctions);
		}
	}
	
	/**
	 * Parse a relation term and get a matching MDD.
	 * 
	 * @param relation an ASTNode corresponding to a relation
	 * @param value the value to return when the relation is satisfied
	 * @return a MDD testing this relation
	 */
	private int getMDDForRelation(ASTNode relation, int value) {
		
		Type type = relation.getType();
		
		// consistency check: should only be called for relation nodes
		switch (type) {
		case RELATIONAL_GEQ:
		case RELATIONAL_GT:
		case RELATIONAL_LEQ:
		case RELATIONAL_LT:
		case RELATIONAL_NEQ:
		case RELATIONAL_EQ:
			break;
		default:
			throw new RuntimeException("Not a relation: "+relation);
		}
		
		// a relation should always have two children
		if (relation.getChildCount() != 2) {
			throw new RuntimeException("Invalid number of children in relation: "+relation);
		}
		ASTNode varNode = relation.getChild(0);
		ASTNode valueNode = relation.getChild(1);
		
		String varName = varNode.getName().trim();
		Integer relValue = null;
		boolean reversed = false;

		// extract content from children (NAME and INTEGER only)
		if (varNode.getType() == Type.NAME && valueNode.getType() == Type.INTEGER) {
			relValue = valueNode.getInteger();
		} else if (varNode.getType() == Type.INTEGER && valueNode.getType() == type.NAME) {
			reversed = true;
			varName = valueNode.getName().trim();
			relValue = varNode.getInteger();
		} else if (varNode.getType() == Type.NAME && valueNode.getType() == Type.NAME) {
			String valueName = valueNode.getName().trim();
			Input input = m_curInputs.get(valueName);
			if (input == null) {
				// try reversing the relation
				input = m_curInputs.get(varName);
				if (input != null) {
					reversed = true;
					String stmp = varName;
					varName = valueName;
					valueName = stmp;
				}
			}
			 
			if (input != null) {
				if (!varName.equals(input.getQualitativeSpecies().trim())) {
					throw new RuntimeException("Constraint '"+input.getQualitativeSpecies().trim()+"' and variable '"+varName+"' do not match in: "+relation);
				}
				try {
					relValue = input.getThresholdLevel();
				} catch (Exception e) {
					relValue = 1;
				}
			}
		}

		if (relValue == null) {
			throw new RuntimeException("Could not find a value in: "+relation);
		}
		
		// handle inequalities in reversed relations ( "1 > g2" becomes "g2 < 1" )
		if (reversed) {
			switch (type) {
			case RELATIONAL_GEQ:
				type = Type.RELATIONAL_LEQ;
				break;
			case RELATIONAL_LEQ:
				type = Type.RELATIONAL_GEQ;
				break;
			case RELATIONAL_GT:
				type = Type.RELATIONAL_LT;
				break;
			case RELATIONAL_LT:
				type = Type.RELATIONAL_GT;
				break;
			}
		}

		int index = getIndexForName(varName);
		if (index < 0) {
			throw new RuntimeException("Unrecognized name in relation: "+relation);
		}

		MDDVariable var = ddvariables[index];
		
		
		// normalise inequalities and handle border cases (always true or false)
		switch (type) {
		
		case RELATIONAL_GT:
			type = Type.RELATIONAL_GEQ;
			relValue += 1;
		case RELATIONAL_GEQ:
			if (relValue <= 0) {
				return value;
			}
			if (relValue >= var.nbval) {
				return 0;
			}
			break;

			
		case RELATIONAL_LEQ:
			type = Type.RELATIONAL_LT;
			relValue += 1;
		case RELATIONAL_LT:
			if (relValue >= var.nbval) {
				return value;
			}
			if (relValue <= 0) {
				return 0;
			}
			break;

			
		case RELATIONAL_NEQ:
			if (relValue < 0 || relValue >= var.nbval) {
				return value;
			}
			break;
			
		case RELATIONAL_EQ:
			if (relValue < 0 || relValue >= var.nbval) {
				return 0;
			}
			break;
			
		default:
			throw new RuntimeException("unknown relation type: "+relation);
		}

		
		
		// now we should have a valid relValue and only EQ, NEQ, GEQ or LT relations
		if (0 > relValue || var.nbval <= relValue) {
			throw new RuntimeException("Relation value out of [0.."+var.nbval+"[ range: "+valueNode);
		}

		
		if (var.nbval == 2) {
			switch (type) {
			
			case RELATIONAL_LT:
				return var.getNode(value, 0);
				
			case RELATIONAL_GEQ:
				return var.getNode(0, value);
				
			case RELATIONAL_EQ:
				if (relValue == 0) {
					return var.getNode(value, 0);
				}
				return var.getNode(0, value);

			case RELATIONAL_NEQ:
				if (relValue == 0) {
					return var.getNode(0, value);
				}
				return var.getNode(value, 0);
			}
			
			throw new RuntimeException("Could not handle relation: "+relation);
		}

		
		int[] values = new int[var.nbval];
		switch (type) {
		
		case RELATIONAL_GEQ:
			for (int v=relValue ; v<var.nbval ; v++) {
				values[v] = value;
			}
			return var.getNode(values);
		
		case RELATIONAL_LT:
			for (int v=0 ; v<relValue ; v++) {
				values[v] = value;
			}
			return var.getNode(values);

		case RELATIONAL_NEQ:
			for (int v=0 ; v<var.nbval ; v++) {
				if (v == relValue) {
					values[v] = 0;
				} else {
					values[v] = value;
				}
			}
			return var.getNode(values);
			
		case RELATIONAL_EQ:
			values[relValue] = value;
			return var.getNode(values);

		}
		
		throw new RuntimeException("Could not handle relation: "+relation);
	}
	
   protected class ASTNODEContainer{
	   protected ASTNode currentnode;
	   protected int currentchild;
	   
	   public ASTNODEContainer(ASTNode node){
		   this.currentnode=node;
		   currentchild=0;
	   }
	   
	   public int getcurrentChild(){
		   return currentchild;
	   }
	   
	   public ASTNode getNode(){
		   return currentnode;
	   }
	   
	   public void incrementchild(){
		   currentchild++;
	   }
   }
	
   protected IndexedHashMap<String, Regulator> getCopyRegulators(){
	   IndexedHashMap<String, Regulator> copy=new IndexedHashMap<>();
	   for (int i = 0; i < modelgenes.size(); i++) {
		   copy.put(modelgenes.getKeyAt(i), (Regulator) modelgenes.getValueAt(i).copy());
	   }
	   return copy;
   }
   
   protected IndexedHashMap<String, RegulatoryRule> getCopyRegulatoryRules(){
	   IndexedHashMap<String, RegulatoryRule> copy=new IndexedHashMap<>();
	   for (int i = 0; i < generules.size(); i++) {
		   try {
			copy.put(generules.getKeyAt(i),generules.getValueAt(i).copy());
		   } catch (ParseException e) {
			e.printStackTrace();
		   }
	   }
	   return copy;
   }

}
