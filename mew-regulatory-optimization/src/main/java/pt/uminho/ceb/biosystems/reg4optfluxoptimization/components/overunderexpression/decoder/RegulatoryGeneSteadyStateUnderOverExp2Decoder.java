package pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.overunderexpression.decoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pt.uminho.ceb.biosystems.jecoli.algorithm.components.representation.IRepresentation;
import pt.uminho.ceb.biosystems.jecoli.algorithm.components.representation.hybridset.HybridSetRepresentation;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneChangesList;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.GeneticConditions;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.strainoptimizationalgorithms.jecoli.components.decoder.ISteadyStateDecoder;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.pair.Pair;
import pt.uminho.ceb.biosystems.reg4optfluxcore.integratedmodel.model.IIntegratedStedystateModel;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.GeneregulatorychangesList;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;

public class RegulatoryGeneSteadyStateUnderOverExp2Decoder implements ISteadyStateDecoder {


	private static final long serialVersionUID = 1L;
	protected IIntegratedStedystateModel model; 
	protected List<String> notAllowedGeneRegulationsIDS = null;
	protected List<Integer> notAllowedGeneRegulationsindexes = null;
	protected ArrayList<Integer> internalDecodeTable = null;
	
	
	
	public RegulatoryGeneSteadyStateUnderOverExp2Decoder(IIntegratedStedystateModel model){
		this.model = model;
	}
	
	public RegulatoryGeneSteadyStateUnderOverExp2Decoder(IIntegratedStedystateModel model, List<String> notallowedGeneRegulations){
		/*this.model = model;
		this.notAllowedGeneRegulationsIDS = notallowedGeneRegulations;
		try {
			addNotAllowedIds(notAllowedGeneRegulationsIDS);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	

	

	@Override
	public ISteadyStateModel getModel() {
		// TODO Auto-generated method stub
		return this.model;
	}
	

	public void setModel(IIntegratedStedystateModel model) {
		this.model = model;
	}
	

	@Override
	public int getNumberVariables() {
		if (this.notAllowedGeneRegulationsIDS==null) 
			return model.getNumberOfGenes();
		else 
			return model.getNumberOfGenes() - notAllowedGeneRegulationsIDS.size();
		
	}
	
	public int getInitialNumberVariables(){
		
		return model.getNumberOfGenes();
		
	}


	@Override
	public void addNotAllowedIds(List<String> notAllowedIds) throws Exception {
		
        notAllowedGeneRegulationsindexes = new ArrayList<Integer>(notAllowedIds.size());
		
		Iterator<String> it = notAllowedIds.iterator();
		while(it.hasNext())
		{
			String nextId = it.next();
			notAllowedGeneRegulationsindexes.add(model.getGeneIndex(nextId));
		}
		createInternalDecodeTable();
		
	}


		protected void createInternalDecodeTable()  
		{
			this.internalDecodeTable = new ArrayList<Integer>();

			if (this.notAllowedGeneRegulationsindexes!= null){
				Collections.sort(this.notAllowedGeneRegulationsindexes);
				for(int nextValue = 0 ; nextValue< getInitialNumberVariables(); nextValue++ )
					if(!notAllowedGeneRegulationsindexes.contains(nextValue))
							internalDecodeTable.add(nextValue); 
			}
		}
		
		protected Integer convertValue (Integer valueToDecode)
		{
			Integer res = null;
			if (internalDecodeTable== null)	
				res = valueToDecode;
			else
			{
				res = internalDecodeTable.get(valueToDecode);
			}
			return res;
		}
		
	
		protected double convertExpressionValue(Integer realValueAtIndex) {
			if(realValueAtIndex == -6)
				return 0.0;
			else
				return Math.pow(2,realValueAtIndex);
		}
		
	

	
	public List<Pair<Integer,Double>> decodeGeneRegulations (IRepresentation genome) throws Exception
	{
		List<Pair<Integer,Double>> regulationList = new ArrayList<Pair<Integer,Double>>();

		int genomeSize = ((HybridSetRepresentation) genome).getNumberOfElements();
		for(int i=0; i<genomeSize ;i++){
			int GeneIndex = convertValue( ((HybridSetRepresentation<Integer,Integer>) genome).getElementAt((i)));
			double expressionValue = convertExpressionValue( ((HybridSetRepresentation<Integer,Integer>) genome).getListValueAt(i) );
			
			regulationList.add(new Pair<Integer, Double>( GeneIndex , expressionValue ));
		}
				
		return regulationList;
	}
	
	
	
	@Override
	public GeneticConditions decode (IRepresentation solution) throws Exception
	{
		List<Pair<Integer,Double>> regulationList = decodeGeneRegulations(solution);
		
	
		ArrayList<String> metabGenesid = new ArrayList<String>();
		ArrayList<Double> metabGenesvalue = new ArrayList<Double>();
		ArrayList<String> regGenesid = new ArrayList<String>();
		ArrayList<Double> regGenesvalue = new ArrayList<Double>();
		
		/*for(Pair<Integer,Double> pair : regulationList){
			
	
			
			
			
			if (((IIntegratedStedystateModel)model).isMetabolicGene(((IIntegratedStedystateModel)model).getRegulatoryNet().getGene(pair.getValue()).getId())){
			
					metabGenesid.add(((IIntegratedStedystateModel)model).getRegulatoryNet().getGene(pair.getValue()).getId());
					metabGenesvalue.add(pair.getPairValue());
					
					}
			else{
				regGenesid.add(((IIntegratedStedystateModel)model).getRegulatoryNet().getGene(pair.getValue()).getId());
				regGenesvalue.add(pair.getPairValue());
			}
		}
		
		GeneChangesList metchlist = new GeneChangesList(metabGenesid,metabGenesvalue);
		GeneregulatorychangesList regchlist = new GeneregulatorychangesList(regGenesid,regGenesvalue);*/

		
		//return new RegulatoryGeneticConditions(regchlist, metchlist, (IIntegratedStedystateModel)model, true);
		return null;
	}
	
	
	
	
	@Override
	public Object deepCopy() throws Exception {

		return new RegulatoryGeneSteadyStateUnderOverExp2Decoder(this.model, new ArrayList<String>(this.notAllowedGeneRegulationsIDS));
	}
	

	
}
