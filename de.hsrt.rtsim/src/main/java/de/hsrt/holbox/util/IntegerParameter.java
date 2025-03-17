package de.hsrt.holbox.util;

import javax.xml.bind.annotation.XmlElement;

public class IntegerParameter extends NumberParameter<Integer>{
	

	
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
	public IntegerParameter(String varName, String name, String unit, Integer def, Integer min, Integer max) 
	{
		this.varName 	= varName;
		this.name 		= name;
		this.unit		= unit;
		this.min		= min;
		this.def		= def;
		
		if (this.min.compareTo(max) <= 0){		// Check bounds
			this.max	= max;
		}
		else{
			assert true;
		}
		
		if ((this.def.compareTo(this.max) <= 0) && (this.def.compareTo(this.min) >= 0) ){
			this.def	= def;
			this.val 	= def;
		}
		else{
			assert true;
		}				
	}
	
	public IntegerParameter()
	{
		this.min = Integer.MIN_VALUE;
		this.max = Integer.MAX_VALUE;
		
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
	
	@Override
	public void setVal(Object oval, boolean publish) throws ModelCodeException
	{
		if(oval instanceof Integer)
		{
			Integer value = (Integer) oval;
			
			super.setNumVal(value);
			if(publish && this.listener != null)
			{
				this.changed();
			}
		
		}else
		{
			try
			{
				Integer value = Integer.valueOf((String)oval);
				super.setNumVal(value);
				if(publish && this.listener != null)
				{
					this.changed();
				}
			}catch(Exception e)
			{
				throw new ModelCodeException("Parameter "+varName+" tried to be written with a " + oval.getClass().toString() + "-->" + e.getLocalizedMessage());
			}
			
		}
	}
	
	
	@XmlElement(name="value")
	public Integer getVal()
	{
		return this.val;
	}
	
	public void setVal(Integer value)
	{
		try
		{
			setVal(value, true);
		}catch(Exception e)
		{
			System.out.println("[E] on Parameter "+varName+" ");
		}
		
	}
	

}
