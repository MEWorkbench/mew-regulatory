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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.multithread.executors;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.map.indexedhashmap.IndexedHashMap;
import pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.executioncc.singlethread.components.ISimulationThread;

public class MultiThreadSimulationExecutorRunneableSim {
	
	
	
	private ThreadPoolExecutor executor = null;
	
	
	public MultiThreadSimulationExecutorRunneableSim(Integer numberprocesses) {
		if(numberprocesses==null)
			numberprocesses=Runtime.getRuntime().availableProcessors();
		createThreadPool(numberprocesses);
	}
	 
	 
	/** Creates the worker thread pool. */
	private  void createThreadPool(int numberprocesses) {
		executor= (ThreadPoolExecutor) Executors.newFixedThreadPool(numberprocesses);
	 }
	

	public <T> IndexedHashMap<String,T> run(IndexedHashMap<String, ISimulationThread<T>> tasklist) throws Exception {	
		
		
		IndexedHashMap<String,T> results=new IndexedHashMap<>(tasklist.size());
		
		for (int i = 0; i < tasklist.size(); i++) {
			ISimulationThread<T> v=tasklist.getValueAt(i);
			executor.execute(v);
		}
		executor.shutdown();
		
		boolean waitclose=true;
    	while (waitclose) {
			try{
				waitclose=!executor.awaitTermination(5, TimeUnit.SECONDS);
				if(waitclose)
					LogMessageCenter.getLogger().addTraceMessage("Waiting to finish "+executor.getActiveCount()+" processes");
			}catch (InterruptedException e) {
    		LogMessageCenter.getLogger().addTraceMessage("Interruped while awaiting completion of callback threads - trying again...");
			
		   }
    	}
		 
    	
    	for (int i = 0; i < tasklist.size(); i++) {
			results.put(tasklist.getKeyAt(i), tasklist.getValueAt(i).getSimulationResults());
		}
		 
		 
		return results;
	}
	
	  
	  public void shutdownNow() {
		  executor.shutdownNow();
	  }


	public static <T> IndexedHashMap<String, T> execute(Integer numberprocesses,IndexedHashMap<String, ISimulationThread<T>> tasklist) throws Exception {

		MultiThreadSimulationExecutorRunneableSim exe=new MultiThreadSimulationExecutorRunneableSim(numberprocesses);
		return exe.run(tasklist);
	}
	
	public static MultiThreadSimulationExecutorRunneableSim newInstance(Integer numberprocesses){
		return new MultiThreadSimulationExecutorRunneableSim(numberprocesses);
	}

}
