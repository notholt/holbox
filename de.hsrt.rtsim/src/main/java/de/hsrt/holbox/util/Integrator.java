package de.hsrt.holbox.util;
/**
 * Function implementing a discrete energy counter based on the backward euler method as per:
 * 
 *  y(k) = y(k-1) + x(k)*dTa
 *  
 *  It takes an input Signal and integrates on the specific time step Ta. The result is added to the output Signal.
 *  
 * @author notholt
 *
 */

public class Integrator {
	
	protected Signal input;
	protected Signal output;
	
	public Integrator(Signal input, Signal output)
	{
		//TODO: Check if Signal units are correct, if not return Exception
		this.input = input;
		this.output = output;
	}
	
	public Integrator(Signal input)
	{
		
		this(input, new Signal(input.Name +"Int",input.Unit+"·s", 0.0));
	}
	
	public void reset()
	{
		output.setValue(0.0);
	}
	
	public Signal getOutputSignal()
	{
		return this.output;
	}
	
	public void compute(double Ta)
	{
		double integration = output.getValue() + input.getValue()*(Ta);
		output.setValue(integration);
	}

}
