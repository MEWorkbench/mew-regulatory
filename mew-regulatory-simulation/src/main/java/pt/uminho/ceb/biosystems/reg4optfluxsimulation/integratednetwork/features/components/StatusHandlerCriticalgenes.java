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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.features.components;

public class StatusHandlerCriticalgenes  {
	
	
	public float progress ;
	public String status = "0.0%";
	protected int current=0;
	private int maxNumberOfFunctionEvaluations = 0;
	private boolean terminationflag = false;
	
	

	public void processEvaluationEvent(int current) {

						
			//current++;
			float progress = (float)current/(float)maxNumberOfFunctionEvaluations;
			int progressRound = Math.round(progress*100);
			if(progressRound > 100){
				progressRound = 100;
				progress = 1;
			}
			setProgress(progress);
			setStatus("Running: "+progressRound+"%");
		}
	

	synchronized public float getProgress() {
		return progress;
	}
	
	synchronized public void setProgress(float progress) {
		this.progress= progress;
	}   
	
	synchronized public String getStatus(){
		return status;
	}
	
	synchronized public void setStatus(String status){
		this.status = status;
	}
	
	public void setNumberOfFunctionEvaluations(int maxNumberFunctionEvaluations) {
		this.maxNumberOfFunctionEvaluations = maxNumberFunctionEvaluations;
	}

	
	
	

}
