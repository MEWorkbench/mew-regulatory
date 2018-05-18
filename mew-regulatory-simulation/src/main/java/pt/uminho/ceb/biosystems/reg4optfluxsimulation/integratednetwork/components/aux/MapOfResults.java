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
package pt.uminho.ceb.biosystems.reg4optfluxsimulation.integratednetwork.components.aux;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class MapOfResults<V> extends Object2ObjectOpenHashMap<String,V>{

	
	private static final long serialVersionUID = 1L;
	
	public static <V> MapOfResults<MapOfResults<V>> initMapofMap(String outerkey, String innerkey, V val) {
		return initMapofMap(outerkey, initMap(innerkey, val));
	}
	
	@SuppressWarnings("unchecked")
	public static <V> MapOfResults<MapOfResults<V>> initMapofMap(String key, MapOfResults<V> val) {
		MapOfResults<V> init=new MapOfResults<>();
		 init.put(key, (V) val);
		 return (MapOfResults<MapOfResults<V>>) init;
	}
	
	
	public static <V> MapOfResults<V> initMap(String id, V val) {
		 MapOfResults<V> init=new MapOfResults<>();
		 init.put(id, (V) val);
		 return init;
	}
	
	public void putMap(String key, MapOfResults<V> val) {
		this.put(key, (V) val);
	}
	
	
	public static <V> void  insertNewValue(MapOfResults<MapOfResults<V>> mainmap, String outerkey,String innerkey,V val) {
		if(mainmap.containsKey(outerkey)) {
			MapOfResults<V> innermap=mainmap.get(outerkey);
			innermap.put(innerkey, val);
		}
		else {
			MapOfResults<V> innermap=initMap(innerkey, val);
			mainmap.put(outerkey, innermap);
		}
	}
	
	
	public static <V> MapOfResults<V> insertNewValue(MapOfResults<V> maptoaddvalue, String key, V val){
		if(maptoaddvalue==null) {
			return initMap(key, val);
		}
		else {
			maptoaddvalue.put(key, val);
			return maptoaddvalue;
		}
	}
	

	public static <V> void insertNewValue(Map<String,MapOfResults<V>> maptoaddvalue,String outerkey, String innerkey, V val){
		if(maptoaddvalue.containsKey(outerkey)) {
			MapOfResults<V> innermap=maptoaddvalue.get(outerkey);
			innermap.put(innerkey, val);
		}
		else {
			MapOfResults<V> innermap=initMap(innerkey, val);
			maptoaddvalue.put(outerkey, innermap);
		}
	}
	
	
	
	

}
