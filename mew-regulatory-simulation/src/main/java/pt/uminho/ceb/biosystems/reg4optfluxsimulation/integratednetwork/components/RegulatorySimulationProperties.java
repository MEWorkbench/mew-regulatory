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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components;

import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;





public class RegulatorySimulationProperties extends SimulationProperties {
	
	public static String SRFBA_BOOLEAN_PREFIX = "BV_";
	public static String SRFBA_EPSILON = "SRFBA_EPSILON";
	

	
	public static final String VARIABLES_CONTAINER ="variablescontainer";
	public static final String COMPONENTINITIALSTATE="componentinitialstate";
	//public static final String COMPONENTBYTEINITIALSTATE="componentbyteinitialstate";
	public static final String REGULATORY_GENETIC_CONDITIONS ="regulatorygeneticconditions";
	public static final String GENESINITIALOFFSTATE="geneswithfalseinitialstate";
    public static final String METABOLIC_SIMULATION_METHOD="metabolicsimualtionmethod";
    public static final String INTEGRATED_SIMULATION_METHOD="integratedsimulationmethod";
    public static final String REGULATORY_NETWORK_SIMULATION_METHOD="regulatorynetworksimulationmethod";
    public static final String FORCEATTRACTORMUSTHAVESSAMEGENEKNOCKOUTS="forceattractorsmusthavesameknockouts";
    public static final String FORCEINITIALIZATIONTFSTRUESTATE="forceinitializationtranscriptionalfactorstruestate";
    //public static final String INDEPENDENTTRANSFACTORSSTATE="forceindependenttransfactorshavetruestate";
    public static final String BIOMASSFLUX ="biomassfluxid";
    
    
    public static final String STOPCURRENTPROCESS ="STOPCURRENTPROCESS";
    
    /*
     * RFBA Parameters
     */
    public static final String INITIALBIOMASS ="initbiomass";
    public static final String TIMESTEP ="timestep";
    public static final String NUMBERSTEPS ="numbersteps";
    public static final String SUBSTRATES ="initsubtrates";
    public static final String EXCLUDEUPTAKEREACTIONS ="excludeuptakereactions";
    public static final String GENESSTARTINGWITHTRUESTATE ="genesstartingwithtruestate";
    public static final String RFBAINITIALVARIBLESSTATE ="rfbainitialvariablesstate";
    
    /*
     * 
     * Prom Parameters
     */
    
    public static final String EXPRESSIONDATASET="expressiondataset";
    public static final String EXPRESSIONDATAFILE="expressiondata";
    public static final String PROMREGULATORS="promregulators";
    public static final String PROMTARGETS="promtargets";
    public static final String PROMKAPPA="promkappa";
    public static final String PROMDATATHRESHOLD="promdatathreshold";
    public static final String PROMTFSUBSET="promtfsubset";
    public static final String PROMKNOWNTFGENEPROBABILITY="promknowntfgeneprobability";
    public static final String FLUXVARIABILITYDATA ="fluxvariabilitydata";
    public static final String PROMLISTENER ="promlistener";
    
    
    /*
     * 
     * Gemini Parameters
     */
    
    public static final String GEMINIPHENOTYPEVIABILITY="geminiphenotype";
    public static final String GEMINIPHENOTYPEKNOCKOUTTYPE="geminiphenotypeknockouttype";
    public static final String GEMINIMETRICTYPE="geminimetrictype";
    
}
