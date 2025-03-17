package de.hsrt.holbox.util;

import java.util.LinkedHashMap;


/**
 * Manages a list of Parameters.
 * @author notholt
 *
 */

public class ParameterList {

	private LinkedHashMap<String, Parameter<?>> parameters;
	
	/**
	 * Creates an empty parameter list
	 */
	public ParameterList()
	{
		parameters = new LinkedHashMap<>();
	}
	
	/**
	 * Returns a parameter by its variable name (Id)
	 * @param varname
	 * @return
	 */
	public Parameter<?> getElementById(String varname)
	{
		if (!parameters.containsKey(varname)) {
			throw new RuntimeException("Error on getting quantity with name " + varname + ": Name does not exist in  system.");
		}
		return this.parameters.get(varname);
	}
	
	/**
	 * Adds a parameter object to the parameter list. The variable name (varName) is included as the key on the HashMap.
	 * @param param
	 */
	public void add(Parameter<?> param){
		if (param !=null)
		{
			if (parameters.containsKey(param.getVarName())) {
				throw new RuntimeException("Error on adding quantity with name " + param.getVarName() + ": Name already exists in  system.");
			}
			
			this.parameters.put(param.getVarName(), param);
		}else
		{
			//Log.print(Lvl.WARN, "Parameter to be added was null " );
		}
	}
	
	public int size()
	{
		return this.parameters.size();
	}
	
	
	public LinkedHashMap<String, Parameter<?>> getMap()
	{
		return this.parameters;
	}
	
}
