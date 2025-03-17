package de.hsrt.holbox.util;
/**
 * Function implementing a discrete integrator based on the backward euler method as per:
 * 
 *  y(k) = y(k-1) + x(k)*dTa
 *  
 *  It takes an input Signal and integrates on the specific time step Ta. The result is added to the output Signal. Since Ta is in seconds 
 *  and Energy is counted as kWh input signal must be in kW and input time step in seconds. The class makes the pertinent conversions.
 *  
 * @author notholt
 *
 */

public class VolumeCounter extends Integrator{
	
	
	public VolumeCounter(Signal input, Signal output)
	{
		//TODO: Check if Signal units are correct, if not return Exception
		super(input, output);
	}
	
	public VolumeCounter(Signal input)
	{
		
		this(input, new Signal(input.Name +"Volume","l", 0.0));
	}
	
	
	public void compute(double Ta)
	{
		double integration = output.getValue() + input.getValue()*(Ta/3600.0);
		output.setValue(integration);
	}

}
