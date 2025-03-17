package de.hsrt.holbox.util;
/**
 * Function implementing a discrete integrator based on the backward euler method as per:
 * 
 *  y(k) = y(k-1) + x(k)*dTa
 *  
 *  It takes an input Signal and integrates on the specific time step Ta. The result is added to the output Signal. Since Ta is in seconds 
 *  and Volume is counted as litres input signal must be in litres/h and input time step in seconds. The class makes the pertinent conversions.
 *  
 * @author notholt
 *
 */

public class EnergyCounter extends Integrator{
	
	private Double max = null;
	private Double min = null;
	
	private Double minI = null;
	private Double maxI = null;
	
	private Double inputScale = 1.0;
	
	public EnergyCounter(Signal input, Signal output)
	{
		//TODO: Check if Signal units are correct, if not return Exception
		super(input, output);
	}
	
	public EnergyCounter(Signal input)
	{
		
		this(input, new Signal(input.Name +"Egy","kWh", 0.0));
	}
	
	public void setSaturation(Double min, Double max) throws ModelCodeException
	{		
		if(min <= max)
		{
			this.min = min;
			this.max = max;
		}
		else
		{
			throw new ModelCodeException("Max <= Min");
		}
	}
	
	public void setInputSaturation(Double min, Double max) throws ModelCodeException
	{		
		if(min <= max)
		{
			this.minI = min;
			this.maxI = max;
		}
		else
		{
			throw new ModelCodeException("Max <= Min");
		}
	}
	
	public double getMaxInput(double Ts)
	{
		double maxval = Double.MAX_VALUE;
		if(this.max!=null && Ts > 0.0)
		{
			maxval = (this.max - output.getValue())*3600.0/Ts;
		}
		return maxval;
	}
	
	public double getMinInput(double Ts)
	{
		double minval = -Double.MAX_VALUE;
		if(this.min!=null && Ts > 0.0)
		{
			minval = (this.min - output.getValue())*3600.0/Ts;
		}
		return minval;
	}
	
	public void limitInput(double Ts)
	{ 
		input.Value = Math.min(input.Value, getMaxInput(Ts));
		input.Value = Math.max(input.Value, getMinInput(Ts));
	}
	
	private double calculateInput()
	{
		double result = input.getValue() * this.inputScale;
		if(this.minI != null && this.maxI!=null)
		{
			result = Math.min(result, this.maxI);
			result = Math.max(result, this.minI);
		}
		return result;
	}
	
	public void setinputScaling(double is)
	{
		this.inputScale = is;
	}
	
	public void compute(double Ts)
	{
		double input = calculateInput(); 
		double integration = output.getValue() + input*(Ts/3600.0);
		
		if(this.min != null && this.max!=null)
		{
			integration = Math.min(integration, this.max);
			integration = Math.max(integration, this.min);
		}
		
		output.setValue(integration);
	}
	
	public void setValue(double energy)
	{
		this.output.setValue(energy);
	}

}
