package pt.uminho.ceb.biosystems.reg4optfluxcore.graphs.auxiliar;

import java.util.HashSet;
import java.util.Stack;

import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTree;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.AbstractSyntaxTreeNode;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.NodeContainer;
import pt.uminho.ceb.biosystems.mew.utilities.grammar.syntaxtree.TreeUtils;
import pt.uminho.ceb.biosystems.mew.utilities.math.language.mathboolean.parser.ParseException;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;

public class AbstractSyntaxTreeNodeTools {
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashSet<String> getNodeElementIdentifiers(AbstractSyntaxTree rootnode){
        if(rootnode == null)
            return null;
        
        else{
        	
            HashSet<String> elements=new HashSet<>();
        	AbstractSyntaxTreeNode currentNode = rootnode.getRootNode();
        	Stack<NodeContainer> nodeStack = new Stack<NodeContainer>();
            nodeStack.push(new NodeContainer(currentNode));

        while(nodeStack.size() >0){
            NodeContainer nodeContainer = nodeStack.pop();
            AbstractSyntaxTreeNode node = nodeContainer.getNode();
            int childNumberToVisit = nodeContainer.getCurrentChild();
            //System.out.println("Childsvisit: "+childNumberToVisit+" --> "+node.getNumberOfChildren()+"  Node: "+node.toString());
            if(childNumberToVisit == node.getNumberOfChildren() && node.isLeaf()){
            	String nodeid=node.toString();
                elements.add(nodeid);
            }	
            else if(childNumberToVisit < node.getNumberOfChildren()){
                nodeContainer.incrementCurrentChild();
                nodeStack.push(nodeContainer);
                AbstractSyntaxTreeNode childNode = node.getChildAt(childNumberToVisit);
                if(childNode.isLeaf()){
                   String nodeid=childNode.toString();
                   elements.add(nodeid);
                }
                
                nodeStack.push(new NodeContainer(childNode));
            } else if(currentNode.getParent() != null){
                currentNode = currentNode.getParent();  
            }

          }
        
        return elements;
	   }
    }

	
	
	
	

	public static void main(String[] args) throws ParseException {
		RegulatoryRule rule =new RegulatoryRule("teste", "(Crp AND NOT (GalS)) OR GalS");
		//System.out.println(rule.getBooleanRule().height());
        System.out.println(AbstractSyntaxTreeNodeTools.getNodeElementIdentifiers(rule.getBooleanRule()));
        System.out.println(new HashSet<>(TreeUtils.withdrawVariablesInRule(rule.getBooleanRule())));
	}

}
