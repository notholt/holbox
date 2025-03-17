package de.hsrt.holbox.Models;

import de.hsrt.holbox.util.EnergyCounter;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.Signal;

public class SimpleWindTurbine extends Photovoltaic {
	
	private Signal 			pvPwrAtMaxPc; // Signal for controlling maximum PV power in %
	private Signal 			pvPwrAtRes1kW; // Signal for PV resource normalized to 1kWp
	private Signal 			pvPwrAtGen;
	private Signal 			pvPwrRtGen;
	private EnergyCounter 	pvEgy;
	
	public SimpleWindTurbine(String id, Signal pvPwrAtMaxPc, Signal pvPwrAtRes) throws ModelCodeException // Controllable
	{
		super(id, 1.0, 1.0); // TODO: BAD Bugfix
		this.pvPwrAtMaxPc = pvPwrAtMaxPc;
		this.pvPwrAtRes1kW = pvPwrAtRes;
		if (pvPwrAtMaxPc != null)
		{
			this.controlSignals.add(this.pvPwrAtMaxPc);
		}
		this.statusSignals.add(this.pvPwrAtRes1kW);
		this.pvPwrAtGen = this.statusSignals.getElementById("PwrAt");
		this.pvPwrRtGen = this.statusSignals.getElementById("PwrRt");
		
		Signal pvEgy = new Signal("PvEgy", "kWh", 0.0);
		this.pvEgy = new EnergyCounter(this.pvPwrAtGen, pvEgy);
		this.statusSignals.add(pvEgy);
	}
	
	public SimpleWindTurbine(String id, Signal pvPwrAtRes) throws ModelCodeException // Not controllable
	{
		this(id, null, pvPwrAtRes);
	}
	
	

	@Override
	public void reset() {
		// We reset energy counters
		this.pvEgy.reset();
	}

	@Override
	public void recompute() {
		// TODO Auto-generated method stub
	}

	@Override
	public void computeStep(double Ts) {
		
		double power = this.pvPwrAtRes1kW.getValue()*this.pwrAtNom.getVal();
		
		if (this.pvPwrAtMaxPc!= null)
		{
			power = Math.min(power, this.pwrAtNom.getVal()*this.pvPwrAtMaxPc.getValue()*0.01);
		}
		
		this.pvPwrAtGen.setValue(power);
		this.pvEgy.compute(Ts);

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

}
