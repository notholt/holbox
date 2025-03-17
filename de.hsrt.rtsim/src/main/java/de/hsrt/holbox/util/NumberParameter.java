package de.hsrt.holbox.util;


/**
 * Class defining the general properties of Parameters.
 * 
 * @author notholt
 *
 */

public abstract class NumberParameter<T extends Number & Comparable<T>> extends Parameter<T>{
	
	//protected String varName;
	//protected String name;
	//protected String unit;
	protected T val;
	protected T min;
	protected T max;
	protected T def;
	
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
	public NumberParameter(String varName, String name, String unit, T def, T min, T max) 
	{
		this.varName 	= varName;
		this.name 		= name;
		this.unit		= unit;
		this.min		= min;
		
		if (min.compareTo(max) <= 0){		// Check bounds
			this.max	= max;
		}
		else{
			assert true;
		}
		
		if ((def.compareTo(this.max) <= 0) && (def.compareTo(this.min) >= 0) ){
			this.def	= def;
			this.val 	= def;
		}
		else{
			assert true;
		}				
	}
	
	public NumberParameter()
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
		return val.getClass();
	}
	
	
	/**
	 * Sets the default value as current value
	 */
	public void reset()
	{
		this.val = this.def;
	}
	
	/**
	 * Sets the parameter to a specific value
	 * @param value
	 * @throws ModelCodeException 
	 */

	public void setVal(Object oval, boolean publish) throws ModelCodeException
	{
		if(oval!=null)
		{
			if(oval instanceof Number)
			{
				@SuppressWarnings("unchecked")
				T value = (T) oval;
				
				setNumVal(value);
			
			}else
			{
				throw new ModelCodeException("Parameter "+varName+" tried to be written with a " + oval.getClass().toString());
			}
		}
	}
	
	protected void setNumVal(T value) throws ModelCodeException
	{
		if ((value.compareTo(this.max) <= 0) && (value.compareTo(this.min) >= 0) ){
			this.val 	= value;
		}
		else{
			throw new ModelCodeException("Parameter "+varName+" was set to a value outside the limits");
		}	
	}
	
	//@XmlElement(name="value")
	public T getVal()
	{
		return this.val;
	}
	

	public void changed()
	{
		this.listener.changed(this.val.toString());
	}


}
