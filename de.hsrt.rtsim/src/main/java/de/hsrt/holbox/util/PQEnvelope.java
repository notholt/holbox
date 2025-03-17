package de.hsrt.holbox.util;

public class PQEnvelope implements Simulatable {

	private String name = "pwrApLimiter";
	
	SignalList inputs = new SignalList();
	SignalList outputs = new SignalList();
	ParameterList param = new ParameterList();
	
	double pwrAtMax;
	double pwrApNom;

	Signal inP;
	Signal inQ;
	Signal outP;
	Signal outQ;
	
	boolean prioQ = false;

	
	public PQEnvelope(double pwrAtMax, double pwrApNom, Signal pwrAtIn, Signal pwrRtIn, Signal pwrAtOut, Signal pwrRtOut, boolean prioQ)
	{
		this.pwrAtMax = pwrAtMax;
		this.pwrApNom = pwrApNom;
		//param.add(this.pwrAtMax);
		//param.add(this.pwrApNom);
		
		this.inP = pwrAtIn;
		this.inQ = pwrRtIn;

		this.outP = pwrAtOut;
		this.outQ = pwrRtOut;

		
		inputs.add(inP);
		inputs.add(inQ);
		
		outputs.add(outP);
		outputs.add(outQ);

	}
	
	
	@Override
	public void computeStep(double Ts) {
		
		double p = inP.getValue();
		double q = inQ.getValue();
		double s = pwrApNom;//.getVal();
		double pmax = pwrAtMax;//.getVal();
		
		if(prioQ)
		{			// pwrRt Priority
			q = Math.min(q, s);
			q = Math.max(q, -s);
			
			pmax = Math.sqrt(s*s - q*q);
			
			p = Math.min(p, pmax);
			p = Math.max(p, -pmax);
			
		}else
		{			// pwrAt Priority
			
			p = Math.min(p, pmax);
			p = Math.max(p, -pmax);
			
			
			double qmax = Math.sqrt(s*s - p*p);
			
			q = Math.min(q, qmax);
			q = Math.max(q, -qmax);
			
			
		}
		
		outP.setValue(p);
		outQ.setValue(q);
		
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

	public void setPwrAtNom(double p)
	{
		this.pwrAtMax = p;
	}
	public void setPwrApNom(double p)
	{
		this.pwrApNom = p;
	}

	
}
