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

public class StaticMultiThreadSimulationExecutorCallableTasks {
	
	
	
	private static int nprocs=-1;
	
	
	private static ThreadPoolExecutor executor = null;
	 
	 
	    /** Creates the worker thread pool. */
	private static void createThreadPool(final Integer numberprocesses) {
	        if (nprocs == -1) {
	            int n = -1;
	            
	            if(numberprocesses!=null)
	            	n=numberprocesses;
	            if (n < 1) 
	                nprocs = Runtime.getRuntime().availableProcessors();
	           else
	                nprocs = n;


	            if (nprocs > 1) 
	            	 executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nprocs);
	      
	            System.out.println("Number Processes: "+nprocs);
	     }
	 }
	
	
	 public static int getThreadPoolSize() {
	        createThreadPool(null);
	        return nprocs;
	    }
	
	 
	 
	 public static <T> List<T> run(Integer numberprocesses, Collection<? extends Callable<T>> tasks) throws Exception {
	        createThreadPool(numberprocesses);

	        List<T> results = new ArrayList<T>(tasks.size());
	       // List<T> results = new ObjectArrayList<T>(tasks.size());
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
	        
	       // close();
	        return results;
	   }
	 
	 
	 public static <T> List<T> run(Collection<? extends Callable<T>> tasks) throws Exception {
		 return run(null, tasks);
	 }
	 
	 
	 
	 public static void shutdown() {
	      if (executor != null) {
	         executor.shutdown();
	      }
	  }
	 
	  protected static void close(){
	    	
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
	    }

}
