package de.hsrt.holbox.util;

import javax.xml.bind.annotation.XmlElement;

/**
 * Class defining the general properties of Parameters.
 * 
 * @author notholt
 *
 */

public class FileParameter extends Parameter<String>{
	
	//private String varName;
	//private String name;
	//private String unit;
	@XmlElement(name="value")
	private String value;
	private int lines = 0;
	
	/**
	 * Parameter constructor. It requires all information to define the parameter.
	 * @param varName 	The variable name in IEC form
	 * @param name 		The long name of the variable
	 * @param unit		Unit of the parameter
	 * @param def		Default value (will be used)
	 * @param min		Minimum acceptable value
	 * @param max		Maximum acceptable value
	 * @throws ModelCodeException 
	 */
	public FileParameter(String varName, String name, String unit, String def) 
	{
		this.varName 	= varName;
		this.name 		= name;
		this.unit		= unit;
		this.value 		= def;				
	}
	
	public FileParameter(String varName, String name, String unit, String def, int lines) 
	{
		this.varName 	= varName;
		this.name 		= name;
		this.unit		= unit;
		this.value 		= def;
		this.lines		= lines;
	}
	
	public FileParameter()
	{
		
	}
	
	// Getters & Setters
	
	/**
	 * Returns the variable name of the parameter.
	 * @return Variable name of the Parameter object.
	 */
	public String getVarName()
	{
		return varName;
	}
	
	/**
	 * Returns the unit of the parameter
	 * @return Unit of the parameter object.
	 */
	public String getUnit()
	{
		return this.unit;
	}
	
	/**
	 * Returns the variable name
	 * @return Name
	 */
	public String getName()
	{
		return this.name;
	}
		
	/**
	 * Returns the class of the base parameter. 
	 * @return
	 */
	public Class<?> getType()
	{
		return value.getClass();
	}
	
	
	/**
	 * Sets the default value as current value
	 */
	public void reset()
	{
		this.value = "";
	}
	
	/**
	 * Sets the parameter to a specific value
	 * @param value
	 * @throws ModelCodeException 
	 */

	public void setVal(Object value, boolean publish) throws ModelCodeException
	{
			this.value 	= (String)value;
			if(publish && this.listener != null)
			{
				this.changed();
			}
	}
	
	public String getVal()
	{
		return this.value;
	}

	public int getLines()
	{
		return this.lines;
	}


	public void changed()
	{
		this.listener.changed(this.value.toString());
	}
	
}
