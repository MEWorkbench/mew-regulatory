package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.syntaxtreeformat;

import pt.uminho.ceb.biosystems.mew.core.model.components.Gene;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.components.RegulatoryRule;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatorynetwork.networkmodel.IRegulatoryNetwork;



public interface IOptfluxRegulatoryModel extends IRegulatoryNetwork {
	


	// Genes
	Gene getGene(int geneIndex);
	Gene getGene(String geneId);
	//IndexedHashMap<String, Regulator> getRegulatoryGenes();
	
	
	
	// Regulatory rules
	
	/*RegulatoryRule getRegulatoryRule(int ruleIndex);
	RegulatoryRule getRegulatoryRuleOfGeneId(String geneid);
	IndexedHashMap<String, RegulatoryRule> getGeneRegulatoryRules();*/

	void setRegulatoryRules(IndexedHashMap<String, RegulatoryRule> newrules);
	

}
