package de.hsrt.holbox.util;

public class Limiter implements Simulatable {

	String name = "Lim";
	
	SignalList inputs = new SignalList();
	SignalList outputs = new SignalList();
	ParameterList param = new ParameterList();
	
	Signal in;
	Signal out;
	Signal min;
	Signal max;
	
	
	
	public Limiter(Signal min, Signal max, Signal input, Signal output)
	{
		this.in = input;
		this.out =  output;
		this.min = min;
		this.max = max;
		inputs.add(this.in);
		inputs.add(this.min);
		inputs.add(this.max);
		outputs.add(this.out);
	}
	
	public Limiter(double min, Signal max, Signal input, Signal output)
	{
		this(new Signal("Constant", "-", min), max, input, output);
	}

	public Limiter(Signal min, double max, Signal input, Signal output)
	{
		this(min, new Signal("Constant", "-", max), input, output);
	}

	
	@Override
	public void computeStep(double Ts) {

		double x = in.getValue();
		double y;
		
		if(x < max.getValue())
		{
			y = x;
		}
		else
		{
			y = max.getValue();
		}
		
		if(y < min.getValue())
		{
			y = min.getValue();
		}
		
		out.setValue(y);
		
	}

	@Override
	public void reset() {
		// Stateless block so no specific reset needed, just recompute
		recompute();
	}

	@Override
	public void recompute() {
		//Easy for stateless blocks
		computeStep(0.0);
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
