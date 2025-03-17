package de.hsrt.holbox.util;

/**
 * An abstract class representing the settings of a specific task/workflow. 
 * 
 * For example: project settings, Power Flow settings, generator settings. It takes care mainly of the process of reading and writing
 * of XML data, necessary for project save to disk and read from disk. 
 * 
 * @author notholt
 *
 */

public abstract class Settings {
	
	//public Class<?> refersTo = null;
	protected ParameterList parList;
	
	
	/**
	 * Primary constructor, used to initialize the settings object with no specific information (just defaults)
	 */
	public Settings() {
		// Is to be implemented in each initMap to reduce unnecessary creation of objects 
		//parList = new ParameterList();
	}
	
	public abstract void initMap();
	
	/**
	 * Used to initialize the object properties when marshalling (reading from XML)	
	 * @param s
	 */
	public void assimilate(Settings s) {
		try {
			
			// Marshalling creates new objects, which are not yet on the parameterList 
			// the current objects there are thus outdated. We need to rebuild Map of the 
			// parsed parameters.
			
			s.initMap();
			
			// This method allows for backward compatibility. The parameter read is looked for in the current
			// data structure. If found, it will be updated. If not, it is an error in the file or the version
			// changed too much. All other (new) parameters are kept at their defaults without giving an error
			s.getParameters().getMap().forEach((key, param)->
				{
					try {
						parList.getElementById(key).setVal(param.getVal());
						Log.print(Log.Lvl.INFO, "Set {" + key + "} to " + (param.getVal()!=null? param.getVal().toString(): "null")
									); // New log system
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
			);
			
		}catch(Exception e)
		{
			Log.print(Log.Lvl.ERROR, "Error while parsing Project Parameters");
			//System.out.println("[E] Error while parsing Project Parameters ");
		}
		
		
	}

	
	/**
	 * Returns the ParameterList object containing all parameters.
	 * @return
	 */
	public ParameterList getParameters()
	{
		//Log.print(Lvl.DEBUG, "Settings:77 Parlist" + this.parList);
		return this.parList;
	}
	
	/**
	 * 
	 * @return
	 */
	//TODO: Implement correctly
	public boolean comparable(Settings s) {        
        
		
        for(@SuppressWarnings("rawtypes") Parameter p : this.parList.getMap().values())
        {
        	try 
        	{
	        	Object val = s.getParameters().getElementById(p.varName).getVal();
	        	if(!p.getVal().toString().equals(val.toString()))
	        	{
	        		Log.print(Log.Lvl.WARN, "Obj {" + p.varName + "} not equal.");
	        		//System.out.println("[W] Obj {"+p.varName+"} not equal.");
	        		return false;
	        	}
        	}catch(Exception e)
        	{
        		Log.print(Log.Lvl.WARN,"Obj {"+p.varName+"} not found.");
        		//System.out.println("[W] Obj {"+p.varName+"} not found.");
        		return false;
        	}
        }
                
        return true;
    }
	
	public void reset()
	{
		parList.getMap().forEach((key, param)->{
			param.reset();
		});
	}
	
	
}
