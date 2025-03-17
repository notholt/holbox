package de.hsrt.holbox.project.powersystem;

import java.util.LinkedHashMap;


/**
 * Manages a list of Parameters.
 * @author notholt
 *
 */

public class BusList {

	private LinkedHashMap<String, BusSettings> buses;
	
	/**
	 * Creates an empty parameter list
	 */
	public BusList()
	{
		buses = new LinkedHashMap<>();
	}
	
	/**
	 * Returns a parameter by its variable name (Id)
	 * @param varname
	 * @return
	 */
	public BusSettings getElementById(String varname)
	{
		if (!buses.containsKey(varname)) {
			throw new RuntimeException("Error on getting quantity with name " + varname + ": Name does not exist in  system.");
		}
		return this.buses.get(varname);
	}
	
	/**
	 * Adds a parameter object to the parameter list. The variable name (varName) is included as the key on the HashMap.
	 * @param param
	 */
	public void add(BusSettings bus){
		if (buses.containsKey(bus.getId())) {
			throw new RuntimeException("Error on adding quantity with name " + bus.getId() + ": Name already exists in  system.");
		}
		
		this.buses.put(bus.getId(), bus);
	}
	
	public int size()
	{
		return this.buses.size();
	}
	
	
	public LinkedHashMap<String, BusSettings> getMap()
	{
		return this.buses;
	}
	
}
