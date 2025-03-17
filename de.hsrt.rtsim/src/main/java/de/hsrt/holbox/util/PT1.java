package de.hsrt.holbox.util;

import de.hsrt.holbox.ClassicPowerFlow.Sys;

public class PT1 implements Simulatable {

	private String name = "PT1";
	
	SignalList inputs = new SignalList();
	SignalList outputs = new SignalList();
	ParameterList param = new ParameterList();
	
	DoubleParameter tau;
	Signal in;
	Signal out;
	
	double x_1 = 0; //Equivalent to x*z^-1
	double y_1 = 0; //Equivalent to y*z^-1
	
	public PT1(double tauI, Signal input, Signal output)
	{
		tau = new DoubleParameter("tau", "Time constant", "s", tauI, 0.0, Double.MAX_VALUE);
		param.add(tau);
		in = input;
		out =  output;
		inputs.add(in);
		outputs.add(out);
	}
	
	
	@Override
	public void computeStep(double Ts) {

		// First we compute the factor of the Z transform exp(-tau*Ts)
		double factor = Math.exp(-tau.getVal()*Ts);
		double x = in.getValue();
		
		// PT1 with large sampling times hava very bad problem: The input is differred on one sample time
		// due to the zero order hold. We can achieve almost same functionality but no large diferral by 
		// Eliminating the ZOH and thus using x instead of x_1
		// double y = (1-factor)*x_1 + factor*y_1; // Bad but correct formula with ZOH
		double y = (1-factor)*x + factor*y_1; // No ZOH
		
		// There is a mathematical bug is computeStep is misused to recalculate. The programmer may just think of using
		// computeStep(0.0), which automatically takes the previous value since factor is then 1 (exp of 0 = 1). 
		// In this special case and to achieve static stability, the it is assumed the PT1 has already achieved asymptotic
		// Stability but then, when used in dynamic analysis it will not work as expected... So a global var on Sys was implemented
		if(Sys.steadyState)
		{
			y = x;
		}else
		{
			x_1 = x;
			y_1 = y;
		}
		
		out.setValue(y);
		
	}

	@Override
	public void reset() {
		// Resets to output = input
		double x = in.getValue();
		x_1 = x;
		y_1 = x;
		out.setValue(x);
	}

	@Override
	public void recompute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ParameterList getParameters() {
		return param;
	}

	@Override
	public SignalList getInputSignals() {
		return inputs;
	}

	@Override
	public SignalList getOutputSignals() {
		return outputs;
	}
	
	@Override
	public String getName() {
		
		return this.name;
	}

	@Override
	public void setName(String name) {
		
		this.name = name;
	}

}
