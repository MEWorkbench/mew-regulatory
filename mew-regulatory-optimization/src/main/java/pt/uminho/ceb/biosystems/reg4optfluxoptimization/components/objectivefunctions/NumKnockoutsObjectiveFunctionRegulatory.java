package pt.uminho.ceb.biosystems.reg4optfluxoptimization.components.objectivefunctions;

import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.core.strainoptimization.objectivefunctions.ofs.NumKnockoutsObjectiveFunction;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.RegulatoryGeneticConditions;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.methods.results.IntegratedSimulationMethodResult;


public class NumKnockoutsObjectiveFunctionRegulatory extends NumKnockoutsObjectiveFunction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static boolean debug = false;
	
	
	
	public NumKnockoutsObjectiveFunctionRegulatory(Boolean maximize) {
		super(maximize);
		
	}
	
	
	@Override
	public double evaluate(SteadyStateSimulationResult simResult) {
		
		IntegratedSimulationMethodResult simregres=(IntegratedSimulationMethodResult) simResult;
	    
		RegulatoryGeneticConditions inputconds=(RegulatoryGeneticConditions) simregres.getInitialGeneticConditions();
		ArrayList<String> geneknockouts = inputconds.getALLGeneKnockoutList();
		
		if(debug)
			System.out.println(">>>>>>>> N kos: " + geneknockouts.size());

		int numKnockouts = geneknockouts.size();
		if(debug) System.out.println("NK = "+numKnockouts);
		
		if(isMaximization())
			return numKnockouts;
		else{			
			return ( 1.0/ ( new Double(numKnockouts) +1 ));
		}
	}

}
