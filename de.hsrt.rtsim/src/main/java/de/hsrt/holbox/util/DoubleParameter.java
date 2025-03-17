package de.hsrt.holbox.util;

import javax.xml.bind.annotation.XmlElement;

public class DoubleParameter extends NumberParameter<Double>{
	

	
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
	public DoubleParameter(String varName, String name, String unit, Double def, Double min, Double max) 
	{
		this.varName 	= varName;
		this.name 		= name;
		this.unit		= unit;
		this.min		= min;
		this.def		= def;
		
		if (min.compareTo(max) <= 0){		// Check bounds
			this.max	= max;
		}
		else{
			assert true;
		}
		
		if(def!=null)
		{
			if ((def.compareTo(this.max) <= 0) && (def.compareTo(this.min) >= 0) ){
				this.def	= def;
				this.val 	= def;
			}
			else{
				assert true;
			}
		}
	}
	
	public DoubleParameter()
	{
		this.min = Double.NEGATIVE_INFINITY;
		this.max = Double.POSITIVE_INFINITY;	
	}
	

	@XmlElement(name="value")
	public Double getVal()
	{
		return this.val;
	}
	
	public void setVal(Double value)
	{
		try
		{
			setVal(value, true);
		}catch(Exception e)
		{
			System.out.println("[E] on Parameter "+varName+" with reason: " + e.getMessage());
		}
		
	}

	@Override
	public String toString() {
		return "DoubleParameter [val=" + val + ", min=" + min + ", max=" + max + ", def=" + def + ", listener="
				+ listener + ", varName=" + varName + ", name=" + name + ", unit=" + unit + ", getVal()=" + getVal()
				+ ", getVarName()=" + getVarName() + ", getUnit()=" + getUnit() + ", getName()=" + getName()
				+ ", getType()=" + getType() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
