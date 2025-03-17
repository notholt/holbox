package de.hsrt.holbox.Models;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.ClassicPowerFlow.Sys;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.*;
import de.hsrt.holbox.util.Log.Lvl;


/**
 * 
 * @author notholt
 *
 */
public class SimpleBattery extends Storage{
	
	
	@XmlElement
	protected DoubleParameter battNomCycles = new DoubleParameter("battNomCycles", "Battery nominal equivalent cycles", "cycles", 3000.0, 1.0, 1E6);
	@XmlElement
	protected DoubleParameter battPwrDerDisThdPc = new DoubleParameter("battPwrDerDisThdPc", "Battery derating threshold (discharging)", "%", 20.0, 00.0, 50.0);
	@XmlElement
	protected DoubleParameter battPwrDerChgThdPc = new DoubleParameter("battPwrDerChgThdPc", "Battery derating threshold (charging)", "%", 80.0, 50.0, 100.0);
	@XmlElement
	protected DoubleParameter battPwrRampChg = new DoubleParameter("battPwrRampChg", "Battery Ramp rate (charging)", "P_nom/s", 10.0, 0.1, 1000.0);
	@XmlElement
	protected DoubleParameter battPwrRampDis = new DoubleParameter("battPwrRampDis", "Battery Ramp rate (discharging)", "P_nom/s", 10.0, 0.1, 1000.0);
	@XmlElement
	protected DoubleParameter battDroop = new DoubleParameter("battDroop", "Droop Characteristic", "%", 3.0, 0.1, 10.0);
	
	protected double scale = 1.0;
	
	protected Signal pwrAtInvSetp = new Signal("pwrAtInvSetp", "kW", 0.0);
	protected Signal pwrRtInvSetp = new Signal("pwrRtInvSetp", "kvar", 0.0);

	//protected Signal pwrAtBattSetp = new Signal("pwrAtBattSetp", "kW", 0.0);
	
	protected Signal pwrAtInv = new Signal("pwrAtInv", "kW", 0.0);
	protected Signal pwrRtInv = new Signal("pwrRtInv", "kvar", 0.0);
	

	protected Signal egyChgTotal = new Signal("egyChgBattTot", "kWh", 0.0);
	protected Signal egyDisTotal = new Signal("egyDisBattTot", "kWh", 0.0);
	
	
	private double pwrAtChgMax = 0.0;
	private double pwrAtDisMax = 0.0;
	
	
	
	private EnergyCounter egyStored;
	private EnergyCounter egyDisTot;
	private EnergyCounter egyChgTot;

	public SimpleBattery()
	{
		super();
		this.init();
		this.initMap();
		
	}
	
	
	/**
	 * Constructor
	 * @param battEgyNom	Nominal capacity in kWh
	 * @param battChgPwrAtNom
	 * @param battDisPwrAtNom
	 * @throws ModelCodeException 
	 */
	public SimpleBattery(String id, double battEgyNom, double battChgPwrAtNom, double battDisPwrAtNom, Signal pwrAtSetp, Signal pwrRtSetp) throws ModelCodeException
	{
		super(id, battEgyNom, battChgPwrAtNom, battDisPwrAtNom);
		this.init();
		
		// Adding control signals
		
		if(pwrAtSetp != null)
		{
			this.controlSignals.add(pwrAtSetp);
			this.pwrAtInvSetp = pwrAtSetp;
		}
		else
		{
			this.pwrAtInvSetp = new Signal("pwrAtSetp", "kW", 0.0);
		}
		
		if(pwrRtSetp != null)
		{
			this.controlSignals.add(pwrRtSetp);
			this.pwrRtInvSetp = pwrRtSetp;
		}
		else
		{
			this.pwrRtInvSetp = new Signal("pwrRtSetp", "kvar", 0.0);
		}

	}
	
	private double computeDroopSetpoint()
	{
		double pwrAtSetp = Math.min((this.sysFreqNom - this.sysFreq.getValue())/(this.sysFreqNom*this.battDroop.getVal()*0.01), 1.0)
							*this.stgDisPwrAtNom.getVal()*scale;
		
		return pwrAtSetp;
	}
	
	/**
	 * Calculates the next integration step
	 * @param Ts
	 */
	@Override
	public void computeStep(double Ts) {
		/*
		 * Step #1: Add external setpoint and droop setpoint for active power internal setpoint
		 */
		
		//pwrAtBattSetp.setValue(-this.pwrAtInvSetp.getValue());
		//pwrAtIntSetp.setValue(this.pwrAtInvSetp.getValue() + computeDroopSetpoint());
		
		// TODO: Ramp rate limiter and SoC power limiter here!!! Right now, we just route the signal
				
		pwrAtInv.setValue(pwrAtInvSetp.getValue());
		pwrRtInv.setValue(pwrRtInvSetp.getValue());
		
		// Limiting the power
		limitSoCDependentChgPwrAt(pwrAtInv);
		
		this.pwrAt.setValue(pwrAtInv.getValue());
		this.pwrRt.setValue(pwrRtInv.getValue());
		
		// Run integrators
		this.egyStored.compute(Ts);
		this.egyChgTot.compute(Ts);
		this.egyDisTot.compute(Ts);
		
		// Update state variables
		this.battSoC.setValue(100.0*this.egyStg.getValue()/(this.stgEgyNom.getVal()*scale*this.battSoH.getValue()*0.01));
		// TODO: Integrate SoH in calculations
		
		
	}
	
	private double linearInterpolation(double x, double x0, double y0, double x1, double y1, double min, double max)
	{
		double m = 1.0;
		
		if(x1-x0 != 0.0)
		{
			m = (y1-y0)/(x1-x0);
		}else
		{
			Log.print(Lvl.WARN, "linear interpolation warning!!!! Chech parameters for [" + this.getId() +"]" );
		}
		
		double c = y0 - (m*x0);
		double y = (m*x) + c;
		
		return Math.max(Math.min(y, max), min);
	}
	
	private void limitSoCDependentChgPwrAt(Signal signal)
	{
		pwrAtChgMax = linearInterpolation(this.battSoC.getValue(), this.battPwrDerChgThdPc.getVal(), 100.0, 100.0, 0.0, 0.0, 100.0)
						* 0.01 * this.stgChgPwrAtNom.getVal() * scale;
		
		pwrAtDisMax = linearInterpolation(this.battSoC.getValue(), this.battPwrDerDisThdPc.getVal(), 100.0, 0.0, 0.0, 0.0, 100.0)
						* 0.01 * this.stgChgPwrAtNom.getVal() * scale;
		//System.out.printf("Egy: %f, SoC: %f, Chg: %f, Dis: %f\n ", this.egyStg.getValue(), this.battSoC.getValue(), pwrAtChgMax, pwrAtDisMax);
		
		double limitedSignal = Math.max(signal.getValue(), -pwrAtChgMax);
		limitedSignal = Math.min(limitedSignal, pwrAtDisMax);
		
		signal.setValue(limitedSignal);
	}
	
	public double getPwrAt()
	{
		return this.pwrAt.getValue();
	}
	
	public void setPwrAtSetp(double setpoint)
	{
		this.pwrAtInvSetp.setValue(setpoint);
	}
	
	public void setPwrAtSetp(Signal setpoint)
	{
		this.pwrAtInvSetp = setpoint;
	}
	
	public double getPwrAtDisMax()
	{
		return this.pwrAtDisMax;
	}
	
	public double getPwrAtChgMax()
	{
		return this.pwrAtChgMax;
	}

	@Override
	public void reset() {
		// We reset all energy counters
		egyStored.reset();
		egyDisTot.reset();
		egyChgTot.reset();
		
	}

	@Override
	public void recompute() {
		this.computeStep(0.0);
	}

	public void setEnergy(double egy)
	{
		this.egyStored.setValue(egy);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		try
		{
			this.egyStored = new EnergyCounter(pwrAtInv, egyStg);
			this.egyStored.setSaturation(0.0, this.stgEgyNom.getVal()*scale);
			this.egyStored.setinputScaling(-1.0);
			
			this.egyChgTot = new EnergyCounter(pwrAtInv, egyChgTotal);
			this.egyChgTot.setInputSaturation(0.0, Double.MAX_VALUE);
			this.egyChgTot.setinputScaling(-1.0);

			this.egyDisTot = new EnergyCounter(pwrAtInv, egyDisTotal);
			this.egyDisTot.setInputSaturation(0.0, Double.MAX_VALUE);
			this.egyDisTot.setinputScaling(1.0);

			
		}catch(Exception e) {Log.print(Lvl.ERROR, "Error initializing Simplebattery: "+ e.getMessage());}
		
		this.sysFreq = Sys.sysFreq;
		
		// Adding status signals
		this.statusSignals.add(this.battSoC);
		this.statusSignals.add(this.battSoH);
		this.statusSignals.add(this.egyStg);
		this.statusSignals.add(this.egyChgTotal);
		this.statusSignals.add(this.egyDisTotal);
	}

	@Override
	public String getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void initMap()
	{
		super.initMap();
		this.parList.add(battNomCycles);
		this.parList.add(battPwrDerDisThdPc);
		this.parList.add(battPwrDerChgThdPc);
		this.parList.add(battPwrRampChg);
		this.parList.add(battPwrRampDis);
		this.parList.add(battDroop);
		
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		SimpleBattery result = new SimpleBattery();
		result.assimilate(this);
		// Assimiliate Bus Attachments
		result.buses = this.buses;
		return result;
	}
	
	public void setScale(double s)
	{
		this.scale = s;
	}
	
	
}
