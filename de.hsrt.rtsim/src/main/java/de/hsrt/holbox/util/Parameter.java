package de.hsrt.holbox.util;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 
 * @author notholt
 *
 */

public abstract class Parameter<T>  {
	
	protected ChangeListener<String> listener;
	
	//@XmlElement(name="name")
	@XmlAttribute(name="name")
	protected String varName;
	protected String name;
	protected String unit;
	
	// Getters & Setters
	
	/**
	 * Returns the variable name of the parameter.
	 * @return Variable name of the Parameter object.
	 */
	public abstract String getVarName();
	
	/**
	 * Returns the unit of the parameter
	 * @return Unit of the parameter object.
	 */
	public abstract String getUnit();
		
	/**
	 * Returns the variable name
	 * @return Name
	 */
	public abstract String getName();
	
		
	/**
	 * Returns the class of the base parameter. 
	 * @return
	 */
	public abstract Class<?> getType();
	
	
	/**
	 * Sets the default value as current value
	 */
	public abstract void reset();
		
	/**
	 * Sets the parameter to a specific value
	 * @param object
	 * @throws ModelCodeException 
	 */
	public void setVal(Object value) throws ModelCodeException
	{
		setVal(value, true);
	}
	
	public void setValIfNotNull(Object value) throws ModelCodeException
	{
		if(value!=null)
		{
			setVal(value, true);
		}
	}
	
	public abstract void setVal(Object value, boolean publish) throws ModelCodeException;
	
	
	/**
	 * 
	 * @return
	 */
	public abstract T getVal();

	public void setListener(ChangeListener<String> listener)
	{
		this.listener = listener;
	}
	
	public void removeListener(ChangeListener<String> listener)
	{
		this.listener = null;
	}
	
	public abstract void changed();
	


}
