package de.hsrt.holbox.Models;

import de.hsrt.holbox.util.EnergyCounter;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.Signal;

/**
 * Simple Prhotovoltaic Model 
 * **** ATTENTION **** 
 * Still Buggy!!!
 * 
 * @author notholt
 *
 */
public class SimplePhotovoltaic extends Photovoltaic {
	
	private Signal 			pvPwrAtMaxPc; // Signal for controlling maximum PV power in %
	private Signal 			pvPwrRtSetpPc; // Signal for controlling maximum PV power in %
	private Signal 			pvPwrAtRes1kW; // Signal for PV resource normalized to 1kWp
	private Signal 			pvPwrAtGen;
	private Signal 			pvPwrRtGen;
	private EnergyCounter 	pvEgy;
	
	public SimplePhotovoltaic(String id, Signal pvPwrAtMaxPc, Signal pvPwrAtRes) throws ModelCodeException // Controllable
	{
		super(id, 0.0, 0.0); // TODO: Bad bad
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
	
	public SimplePhotovoltaic(String id, Signal pvPwrAtRes) throws ModelCodeException // Not controllable
	{
		this(id, null, pvPwrAtRes);
	}
	
	public void setPwrAtMaxPc(Signal pvPwrAtMaxPc)
	{
		this.pvPwrAtMaxPc = pvPwrAtMaxPc;
	}
	
	public void setPwrRtSetpPc(Signal pvPwrRtSetpPc)
	{
		this.pvPwrRtSetpPc = pvPwrRtSetpPc;
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
		
		double power = this.pvPwrAtRes1kW.getValue()*pwrAtNom.getVal(); 
		double pwrRt = 0;
		
		if (this.pvPwrAtMaxPc!= null)
		{
			power = Math.min(power, pwrAtNom.getVal()*this.pvPwrAtMaxPc.getValue()*0.01);
			//System.out.println("["+this.pvPwrAtMaxPc.getValue()+"]<"+ power +">");
		}
		
		if (this.pvPwrRtSetpPc!= null)
		{
			pwrRt = this.pwrApNom.getVal()*this.pvPwrRtSetpPc.getValue()*0.01;
			// Considering Reactive power priority
			power = Math.min(power, Math.sqrt(Math.pow(this.pwrApNom.getVal(),2.0) - Math.pow(pwrRt,2.0)));			
		}
		
		this.pvPwrAtGen.setValue(power);
		this.pvPwrRtGen.setValue(pwrRt);
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
