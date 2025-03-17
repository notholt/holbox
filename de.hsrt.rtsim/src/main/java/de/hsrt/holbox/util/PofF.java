package de.hsrt.holbox.util;

public class PofF implements Simulatable {

	private String name = "pwrHzCtrl";
	
	SignalList inputs = new SignalList();
	SignalList outputs = new SignalList();
	ParameterList param = new ParameterList();
	
	DoubleParameter hzStart;
	DoubleParameter hzDroopPc;
	DoubleParameter freqNom;
	Signal in;
	Signal out;
	Signal ref;
	Signal freq;

	
	public PofF(DoubleParameter hzStart, DoubleParameter hzDroopPc, DoubleParameter freqNom, Signal input, Signal output, Signal pwrAtRef, Signal sysFreq)
	{
		this.hzStart = hzStart;
		this.hzDroopPc = hzDroopPc;
		this.freqNom = freqNom;
		param.add(this.hzStart);
		param.add(this.hzDroopPc);
		ref = pwrAtRef;
		freq = sysFreq;
		in = input;
		out =  output;
		inputs.add(in);
		//inputs.add(ref);
		outputs.add(out);
	}
	
	
	@Override
	public void computeStep(double Ts) {
		
		double x = in.getValue();
		
		double deltaFPc = 100.0*Math.min((this.hzStart.getVal() - this.freq.getValue())/this.freqNom.getVal(), 0.0);
		
		double factor = Math.max(1+(deltaFPc/hzDroopPc.getVal()), 0.0);
		
		double y = Math.min(factor*ref.getValue(), x);
		
		
		out.setValue(y);
		
	}

	@Override
	public void reset() {
		//Stateless, no need to reset
		computeStep(0.0);
	}

	@Override
	public void recompute() {
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
