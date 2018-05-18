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

import java.io.Serializable;
import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTree;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTreeNode;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.TreeUtils;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.DataTypeEnum;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.IValue;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.parser.ParseException;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.parser.ParserSingleton;



public class RegulatoryRule implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String ruleId;
	private String rule;
	protected AbstractSyntaxTree<DataTypeEnum, IValue> booleanRule;
	
	public RegulatoryRule(String ruleId,String rule) throws ParseException{
		this.ruleId = ruleId;
		this.rule = rule;
		
		if(rule == null || rule.equals("")){
			this.booleanRule = new AbstractSyntaxTree<DataTypeEnum, IValue>();
		}else{
		
			AbstractSyntaxTreeNode<DataTypeEnum, IValue> ast;
			
			ast = ParserSingleton.boolleanParserString(rule);
			
			this.booleanRule = new AbstractSyntaxTree<DataTypeEnum, IValue>(ast);
		}
	}
	
	public RegulatoryRule(String ruleId,String rule, AbstractSyntaxTree<DataTypeEnum, IValue> booleanrule ) throws ParseException{
		this.ruleId = ruleId;
		this.rule = rule;
		this.booleanRule = booleanrule;
		
	}
	
	public void setRule(String rule) throws ParseException{
		this.rule= rule;
		if(rule == null || rule.equals("")){
			this.booleanRule = new AbstractSyntaxTree<DataTypeEnum, IValue>();
		}else{
		
			AbstractSyntaxTreeNode<DataTypeEnum, IValue> ast;
			
			ast = ParserSingleton.boolleanParserString(rule);
			
			this.booleanRule = new AbstractSyntaxTree<DataTypeEnum, IValue>(ast);
		}
	}

	public String getRuleId() {
		return ruleId;
	}

	public String getRule() {
		return rule;
	}

	public AbstractSyntaxTree<DataTypeEnum, IValue> getBooleanRule() {
		return booleanRule;
	}
	
	public ArrayList<String> getVariables(){
		return TreeUtils.withdrawVariablesInRule(booleanRule);
	}
	
	
	public RegulatoryRule copy() throws ParseException{
		
		return new RegulatoryRule(this.ruleId, this.rule, this.booleanRule.copy());
	}

	
	public static void main(String[] args) throws ParseException {
		RegulatoryRule rule=new RegulatoryRule("A", "( narL AND NOT R_o2_e_ ) OR ( narL AND dcuR AND NOT R_o2_e_ )");
		System.out.println(rule.getRule());
	}

}
