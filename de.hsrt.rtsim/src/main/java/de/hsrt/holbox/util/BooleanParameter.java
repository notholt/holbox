package de.hsrt.holbox.util;

import de.hsrt.holbox.util.Log.Lvl;

public class BooleanParameter extends IntegerParameter{
	

	
	/**
	 * Parameter constructor. It requires all information to define the parameter.
	 * @param varName 	The variable name in IEC form
	 * @param name 		The long name of the variable
	 * @param unit		Unit of the parameter
	 * @param def		Default value (will be used)
	 * @throws ModelCodeException 
	 */
	public BooleanParameter(String varName, String name, String unit, boolean def) 
	{
		super(varName, name, unit, (def==true?1:0), 0, 1);
			
	}
	
	public boolean isTrue()
	{
		return ((this.val==1?true:false));
	}
	
	public void setVal(boolean in)
	{
		try {
			this.setNumVal((in==true? 1: 0));
		} catch (ModelCodeException e) {
			Log.print(Lvl.ERROR, "Error setting boolean value of parameter {"+this.name+"}");
		}
	}
	
	public BooleanParameter()
	{
		this.min =	0;
		this.max = 1;
		
	}
	
	
}
