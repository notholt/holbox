package de.hsrt.holbox.util;

import java.util.Iterator;
import java.util.LinkedHashMap;

import de.hsrt.holbox.util.Log.Lvl;


/**
 * Manages a list of Parameters.
 * @author notholt
 *
 */

public class SignalList {

	private LinkedHashMap<String, Signal> signals;
	
	/**
	 * Creates an empty parameter list
	 */
	public SignalList()
	{
		signals = new LinkedHashMap<>();
	}
	
	/**
	 * Returns a parameter by its variable name (Id)
	 * @param varname
	 * @return
	 */
	public Signal getElementById(String varname)
	{
		if (!signals.containsKey(varname)) {
			throw new RuntimeException("Error on getting quantity with name " + varname + ": Name does not exist in  system.");
		}
		return this.signals.get(varname);
	}
	
	/**
	 * Adds a parameter object to the parameter list. The variable name (varName) is included as the key on the HashMap.
	 * @param param
	 */
	public void add(Signal param){
		this.add(param, null);
	}
	
	public void add(Signal param, String prefix){
		String paramName = (prefix!=null? prefix+"."+param.getVarName() : param.getVarName());
		if (signals.containsKey(paramName)) {
			signals.remove(paramName);
			Log.print(Lvl.WARN, paramName + " duplicated. Overwriting...");
			//throw new RuntimeException("Error on adding quantity with name " + paramName + ": Name already exists in  system.");
		}
		
		this.signals.put(paramName, param);
	}
	
	public int size()
	{
		return this.signals.size();
	}
	
	
	public LinkedHashMap<String, Signal> getMap()
	{
		return this.signals;
	}
	
	public Signal get(int i)
	{
		if(i < this.size())
		{
			Signal currentSignal = null;
			Iterator<Signal> it = this.signals.values().iterator();
			for(int j = 0; j <= i; j++)
			{
				currentSignal = it.next();
			}
			return currentSignal;
		}
		else
		{
			return null;
		}
	}
	
}
