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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pt.ornrocha.logutils.messagecomponents.LogMessageCenter;

public class MultiThreadSimulationExecutorCallableTasks {
	
	
	
	private int nprocs=1;
	
	
	private ThreadPoolExecutor executor = null;
	 
	
	public MultiThreadSimulationExecutorCallableTasks(Integer numberprocesses) {
		if(numberprocesses==null || numberprocesses<1)
			numberprocesses=Runtime.getRuntime().availableProcessors();
		System.out.println(numberprocesses);
		nprocs=numberprocesses;
		executor= (ThreadPoolExecutor) Executors.newFixedThreadPool(numberprocesses);
	}
	 

	
	 
	 
	 public <T> List<T> run(Collection<? extends Callable<T>> tasks) throws Exception {

	        List<T> results = new ArrayList<T>(tasks.size());
	        if (executor == null) {
	            for (Callable<T> task : tasks) {
	                results.add(task.call());
	            }
	        } else {
	            if (executor.getActiveCount() < nprocs) {
	                List<Future<T>> futures = executor.invokeAll(tasks);
	                for (Future<T> future : futures) {
	                    results.add(future.get());
	                }
	            } else {
	                // Thread pool is busy. Just run in the caller's thread.
	                for (Callable<T> task : tasks) {
	                    results.add(task.call());
	                }
	            }
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
	        
	        return results;
	   }
	 
	 public void shutdownNow() {
		  executor.shutdownNow();
	  }
	 
	 
	 public static <T> List<T> execute(Integer numberprocesses,Collection<? extends Callable<T>> tasks) throws Exception {
		 MultiThreadSimulationExecutorCallableTasks exe=new MultiThreadSimulationExecutorCallableTasks(numberprocesses);
		 return exe.run(tasks);
	 }
	 


}
