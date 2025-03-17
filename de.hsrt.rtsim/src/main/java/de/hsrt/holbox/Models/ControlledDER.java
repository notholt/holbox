package de.hsrt.holbox.Models;

import de.hsrt.holbox.util.*;
import de.hsrt.holbox.util.Log.Lvl;
import de.hsrt.holbox.Models.Configuration.*;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;

public class ControlledDER extends DistributedEnergyResource {
	
	ControlledDERSettings config;
	
	private Signal 			sysFreq;
	private Signal 			pvPwrAtMaxPc; // Signal for controlling maximum PV power in %
	private Signal 			pvPwrRtSetpPc; // Signal for controlling maximum PV power in %
	private Signal 			pvPwrAtRes1kW; // Signal for PV resource normalized to 1kWp
	private Signal 			pvPwrAtGen;
	private Signal 			pvPwrRtGen;
	
	//Intermediate Signals
	private Signal			pwrAtRes = new Signal("pwrAtRes", "kW", 0.0);
	private Signal			pwrRtSoll = new Signal("pwrRtSoll", "kvar", 0.0);
	private Signal			sysFreqFilter;
	private Signal			pwrAtFilter = new Signal("pwrAtFilter", "kW", 0.0);
	private Signal			pwrAtCharac = new Signal("pwrAtCharac", "kW", 0.0);
	
	//private EnergyCounter 	pvEgy;
	private PT1				hzPT1;
	private PT1				pwrAtPT1;
	//private Limiter			pwrAtLim;
	private PofF			pofF;
	private PQEnvelope		pqEnvelope;
	
	public ControlledDER()
	{
		super();
	}
	
	public ControlledDER(String id, Signal pvPwrAtMaxPc, Signal pvPwrRtSetpPc, ControlledDERSettings conf) throws ModelCodeException // Controllable
	{
		super(id, conf, conf.getPwrApNom(), conf.getPwrAtNom(), conf.resPwrSigName.getVal(), 1.0);
		
		this.config = conf;
		
		this.pvPwrAtMaxPc = pvPwrAtMaxPc;
		
		this.pvPwrRtSetpPc = pvPwrRtSetpPc;
		
		
		this.init();
		
	}
	
	
	
	public void setPwrAtMaxPc(Signal pvPwrAtMaxPc)
	{
		this.pvPwrAtMaxPc = pvPwrAtMaxPc;
		this.controlSignals.add(this.pvPwrAtMaxPc);
	}
	
	public void setPwrRtSetpPc(Signal pvPwrRtSetpPc)
	{
		this.pvPwrRtSetpPc = pvPwrRtSetpPc;
		this.controlSignals.add(this.pvPwrRtSetpPc);
	}

	@Override
	public void reset() {
		// We reset energy counters
		this.egy.reset();
	}

	@Override
	public void recompute() {
		try {
			this.computeStep(0.0); // TODO: Cleaner implementation!
		} catch (ModelCodeException e) {
			Log.print(Lvl.ERROR, "Error Recomputing controlledDER. Reason: " + e.getMessage());
		} 
	}

	@Override
	public void computeStep(double Ts) throws ModelCodeException {

		if(this.resPwr == null)
		{
			this.resPwr = this.inputSignals.getElementById(this.resPwrSigName.getVal());
			if(this.resPwr == null)
			{
				throw new ModelCodeException("ControlledDER: No resource Signal found!");
			}
			this.pvPwrAtRes1kW = this.resPwr;
		}
		
		
		this.pwrAtRes.setValue(pvPwrAtRes1kW.getValue()*this.pwrAtNomScaled*this.resInScale.getVal());				// Compute kW Powers
		
		this.pwrRtSoll.setValue((this.pvPwrRtSetpPc!=null? this.pvPwrRtSetpPc.getValue()*this.pwrApNomScaled : 0.0));

		
		// If we have a frequency signal
		if(this.hzPT1 != null)
		{
			this.hzPT1.computeStep(Ts);
		}
		
		// Run P(f) characteristic 
		this.pofF.computeStep(Ts);
		
		
		pwrAtCharac.setValue(Math.min(pwrAtCharac.getValue(), this.pwrAtNomScaled*this.pvPwrAtMaxPc.getValue()*0.01)); // Limiting
		
		this.pwrAtPT1.computeStep(Ts);
		
		this.pqEnvelope.computeStep(Ts);
		
		this.egy.compute(Ts);

	}
	
	public void setFrequencyInput(Signal freq)
	{
		this.sysFreq = freq;
		// init filter
		this.hzPT1 = new PT1(config.hzFilTau(), this.sysFreq, this.sysFreqFilter);
		this.hzPT1.reset();
	}
	
	/*
	public void setPeakPower(double P) throws ModelCodeException
	{
		this.config.setPwrAtNom(P);
		this.config.setPwrApNom(P*1.2);
		this.config.pwrAtPk.setVal(P);
		this.pwrApNom.setVal(P*1.2);
		this.pwrAtNom.setVal(P);
	}*/


	@Override
	public void init() throws ModelCodeException {
		super.init();
		

		if (pvPwrAtMaxPc != null)
		{
			this.controlSignals.add(this.pvPwrAtMaxPc);
		}else
		{
			this.pvPwrAtMaxPc = new Signal("pvPwrAtMaxPc", "%", 100.0);
			this.controlSignals.add(this.pvPwrAtMaxPc);
		}
		
		if (pvPwrRtSetpPc != null)
		{
			this.controlSignals.add(this.pvPwrRtSetpPc);
		}else
		{
			this.pvPwrRtSetpPc = new Signal("pvPwrRtSetpPc", "%", 0.0);
			this.controlSignals.add(this.pvPwrRtSetpPc);
		}
		
		this.pvPwrAtGen = this.statusSignals.getElementById("PwrAt");
		this.pvPwrRtGen = this.statusSignals.getElementById("PwrRt");
		

		
		// Init equipment AC powers
		//this.setPeakPower(this.config.pwrAtPk.getVal()); // Deprecated since pwrAtPk exclusive for PV is. It has been handled by resInScale
		
		sysFreqFilter = new Signal("sysFreqFilter", "Hz", config.freqNom.getVal());
		
		// Init PT1
		pwrAtPT1 = new PT1(this.config.pwrAtFilTau(), this.pwrAtCharac, this.pwrAtFilter);
		
		// Init PofF
		pofF = new PofF(this.config.hzStart, this.config.hzDroopPc, this.config.freqNom, this.pwrAtRes, this.pwrAtCharac, this.pwrAtRes, this.sysFreqFilter);
		
		// Init PQ ENvelope
		pqEnvelope = new PQEnvelope(this.pwrAtNomScaled, this.pwrApNomScaled, pwrAtFilter, pwrRtSoll, pvPwrAtGen, pvPwrRtGen, false); // Prio P

	}
	
	@Override
	public void setScale(double s)
	{
		//if (this.scalable.isTrue())  // In Version 1 all PV is scalable
		{
			this.scale = s;
			this.pwrApNomScaled = s*this.pwrApNom.getVal();
			this.pwrAtNomScaled = s*this.pwrAtNom.getVal();
			this.pqEnvelope.setPwrAtNom(this.pwrAtNomScaled);
			this.pqEnvelope.setPwrApNom(this.pwrApNomScaled);
		}
	}

	protected void setSettingsObj(EquipmentSettings s)
	{
		this.config = (ControlledDERSettings)s;
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		ControlledDER result = new ControlledDER();
		result.assimilate(this);
		result.eqClass = EqClass.valueOf(this.eqClassP.getVal());
		// Assimiliate Bus Attachments
		result.buses = this.buses;
		return result;
	}
	
	public boolean isControllable()
	{
		return true;
	}
	
	public double getPwrAtRes()
	{
		return this.pwrAtRes.getValue();
	}
}
