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
package pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.json;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import pt.ornrocha.jsonutils.MTUJsonIOUtils;
import pt.ornrocha.jsonutils.MTUJsonUtils;
import pt.uminho.ceb.biosystems.reg4optfluxcore.regulatoryutils.string.RegStringUtils;

public class RegGeneInfoJSONReader {
	
	private JSONObject mainobj = null;
	private JSONObject geneinfobyname = null;
	private JSONObject geneinfobybnumber= null;
	private JSONObject regulontftfinter=null;
	private JSONObject regulontfgeneinter=null;
	
	private HashMap<String, JSONObject> gene_TFs_interactions = null;
	private HashMap<String, JSONObject> TFs_TFs_interactions = null;
	private HashMap<String, JSONObject> infobygenename = null;
	private HashMap<String, JSONObject> infobygenebnumber=null;
	
	
	public RegGeneInfoJSONReader(String file){

		
		try {
			mainobj=MTUJsonIOUtils.readJsonFile(file);
			this.geneinfobyname=(JSONObject) mainobj.get(RegFieldsJson.GENENAMEINFO);
			this.geneinfobybnumber= (JSONObject) mainobj.get(RegFieldsJson.GENEBNUMBERINFO);
			this.regulontftfinter = (JSONObject) mainobj.get(RegFieldsJson.REGULONTFTFINTERCTIONS);
			this.regulontfgeneinter = (JSONObject) mainobj.get(RegFieldsJson.REGULONTFGENEINTERACTIONS);
		
			decodeInfo();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private void decodeInfo(){
    	this.gene_TFs_interactions= MTUJsonUtils.getJSonObjectsElemsHashMap(regulontfgeneinter);
    	this.TFs_TFs_interactions =  MTUJsonUtils.getJSonObjectsElemsHashMap(regulontftfinter);
    	this.infobygenename=  MTUJsonUtils.getJSonObjectsElemsHashMap(geneinfobyname);
    	this.infobygenebnumber=  MTUJsonUtils.getJSonObjectsElemsHashMap(geneinfobybnumber);
    }
    
    
    public String getGeneBnumber(String genename){
    	if(infobygenename.containsKey(genename))
    		return (String) infobygenename.get(genename).get(RegFieldsJson.GENEBNUMBER);
    	else
    		return null;
    }
    
    
    public String getTFBnumber(String tfname){
    	String genename = RegStringUtils.convertTFnameToGeneName(tfname);
    	return getGeneBnumber(genename);
    }
    
    public String getGeneNameFromBnumber(String bnumber){
    	if(infobygenebnumber.containsKey(bnumber)){
    		return (String) infobygenebnumber.get(bnumber).get(RegFieldsJson.GENENAME);
    	}
    	else
    	  return null;
    }
    
    public String getTFNameFromBnumber(String bnumber){
    	String genename = getGeneNameFromBnumber(bnumber);
    	if(genename!=null)
    		return RegStringUtils.convertGeneNameToTFname(genename);
    	else
    		return null;
    }
    
    public String getGeneAssociatedTF(String genename){
    	if(infobygenename.containsKey(genename))
    		return (String) infobygenename.get(genename).get(RegFieldsJson.GENETFASSOCIATE);
    	else
    		return null;
    }
	
    
    
    public ArrayList<String> getTfsRegulateGene(String genename){
    	ArrayList<String> res = null;
    	if(gene_TFs_interactions.containsKey(genename)){
    		res = new ArrayList<>();
    		JSONObject obj = gene_TFs_interactions.get(genename);
    		HashMap<String, String> map = MTUJsonUtils.getJSonStringElemsHashMap(obj);
    		
    		for (Map.Entry<String, String> elemp : map.entrySet()) {
				if(!elemp.getKey().equals(RegStringUtils.convertGeneNameToTFname(genename))){
					if(elemp.getKey().contains("-")){
						String[] elems = elemp.getKey().split("-");
						res.add(elems[0]);
						res.add(elems[1]);
					}
					else
					 res.add(elemp.getKey());
				}	
			}
    	}
    	return res;
    }
    
    
    public ArrayList<String> getTfsRegulateTF(String tfname){
    	ArrayList<String> res = null;
    	if(TFs_TFs_interactions.containsKey(tfname)){
    		res = new ArrayList<>();
    		JSONObject obj = TFs_TFs_interactions.get(tfname);
    		System.out.println(obj);
    		HashMap<String, String> map = MTUJsonUtils.getJSonStringElemsHashMap(obj);
    		
    		for (Map.Entry<String, String> elemp : map.entrySet()) {
				if(!elemp.getKey().equals(tfname)){
					if(elemp.getKey().contains("-")){
						String[] elems = elemp.getKey().split("-");
						res.add(elems[0]);
						res.add(elems[1]);
					}
					else
					 res.add(elemp.getKey());
				}	
			}
    	}
    	return res;
    }
    
   public ArrayList<String> getTFsRegulateGeneByBnumber(String bnumber){
    	String genename = getGeneNameFromBnumber(bnumber);
    	if(genename!=null)
    	  return getTfsRegulateGene(genename);
    	else 
    		return null;
    }
    
    
    /*public IndexedHashMap<String, String> getTFsRegulateGeneAndInteractionType(String genename){
    	IndexedHashMap<String, String> res = null;
    	if(gene_TFs_interactions.containsKey(genename)){
    		res = new IndexedHashMap<>();
    		JSONObject obj = gene_TFs_interactions.get(genename);
    		HashMap<String, String> map = MTUJsonUtils.getJSonStringElemsHashMap(obj);
    		for (Map.Entry<String, String> elemp : map.entrySet()) {
				if(!elemp.getKey().equals(MTURegStringUtils.convertGeneNameToTFname(genename)))
					res.put(elemp.getKey(), elemp.getValue());
			}
    	}
    	return res;
    }*/
    
    
    public ArrayList<String> getTFsbnumberRegulateGene(String genename){
    	ArrayList<String> res =null;
    	ArrayList<String> tfs = getTfsRegulateGene(genename);
    	
    	if(tfs!=null){
    		res = new ArrayList<>();
    		for (String tf : tfs) {
    			String tfbnumber = getGeneBnumber(RegStringUtils.convertTFnameToGeneName(tf));
    			if(tfbnumber!=null)
    				res.add(tfbnumber);
    			else
    				res.add(tf);
			}
    	}
    	
    	return res;
    		
    }

}
