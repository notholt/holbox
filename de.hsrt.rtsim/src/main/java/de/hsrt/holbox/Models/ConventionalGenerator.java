package de.hsrt.holbox.Models;

import javax.xml.bind.annotation.XmlRootElement;

import de.hsrt.holbox.Models.Configuration.*;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.*;


/**
 * Conventional generators are actors based in conventional generation concepts
 * this means:
 * 
 * <ul>
 * 		<li>Electrical Generator is based on a synchronous machine</li>
 * 		<li>Prime Machine is based on a thermal energy conversion (Diesel machine, steam turbine, etc.)</li>
 * </ul>
 * 
 * @author notholt
 *
 */

@XmlRootElement
public class ConventionalGenerator extends Equipment {

	protected ConventionalGeneratorSettings gCfg;
	
	//@XmlElement
	protected DoubleParameter			pwrAtMinPc	= new DoubleParameter("PwrAtMinPc", "Minimum power in % of Pnom", "%", 30.0, 0.0, 100.0);
	protected DoubleParameter			droop		;//= new DoubleParameter("PwrAtMinPc", "Minimum power in % of Pnom", "%", 30.0, 0.0, 100.0);
	protected Signal 					pwrAtSch 	= new Signal("GenPwrAtSch", "kW", 0.0);
	protected Signal 					fuelRate 	= new Signal("FuelRate", "l/h", 0.0, true);
	protected Signal 					genVolTot 	= new Signal("GenVolTot", "l", 0.0);
	protected Signal 					genEgyTot 	= new Signal("GenEgyTot", "kWh", 0.0);
	protected Signal 					genRunTm 	= new Signal("GenRunTm", "h", 0.0);
	protected VolumeCounter				fuelVol 	= new VolumeCounter(this.fuelRate, this.genVolTot);
	protected EnergyCounter				egyGen		= new EnergyCounter(this.pwrAt, this.genEgyTot);
	protected Signal					req			= new Signal("genReq", "REQ", 0.0);
	protected Signal					online		= new Signal("genOnline", "ON", 0.0, true);
	protected Integrator				runTm		= new Integrator(online, genRunTm);
	
	public ConventionalGenerator()
	{
		super();
		this.eqClass =EqClass.GEN;
		
		this.initMap();
	}
	
	public ConventionalGenerator(String id, ConventionalGeneratorSettings gCfg) throws ModelCodeException
	{
		super();
		this.id.setVal(id);
		this.eqClass =EqClass.GEN;
		this.gCfg = gCfg;
		this.settingsId.setVal(gCfg.getId());
		this.pwrAtMinPc = gCfg.pwrAtMinPc;
		this.droop 		= gCfg.droop;
		//this.pwrAtNom 	= gCfg.pwrAtNom;
		//this.pwrApNom	= gCfg.pwrApNom;
		this.statusSignals.add(fuelRate);
		this.statusSignals.add(genVolTot);
		this.statusSignals.add(genEgyTot);
		this.statusSignals.add(online);
		
		this.controlSignals.add(pwrAtSch);
		this.controlSignals.add(req);
		
		
		this.initMap();
	}
	
	public ConventionalGenerator(String id, ConventionalGeneratorSettings gCfg, double pwrApNom, double pwrAtNom) throws ModelCodeException
	{
		super(id, pwrApNom, pwrAtNom);
		this.gCfg = gCfg;
		//this.droop 		= gCfg.droop;
		//this.pwrAtNom 	= new DoubleParameter("GenPwrAtNom", "Genset nominal (mechanical) power", "kW", pwrAtNom, 0.1, 50000.0); //TODO: Remove magic number
		//this.pwrApNom	= new DoubleParameter("GenPwrApNom", "Genset nameplate power", "kVA", pwrApNom, 0.1, 50000.0);	// TODO: Remove magic number
		this.statusSignals.add(fuelRate);
		this.statusSignals.add(genVolTot);
		this.statusSignals.add(genEgyTot);
		this.statusSignals.add(online);
		
		this.controlSignals.add(pwrAtSch);
		this.controlSignals.add(req);
		this.initMap();
	}


	
	
	@Override
	public void recompute() {
		// Implements *stateless* business logic
		// In this first stub we check is power management has us turned on and if so, we deliver the scheduled power
		// The scheduled power is used to calculate fuel rate
		
		if(this.req.getValue()==1.0)
		{
			this.online.setValue(1.0);
			this.pwrAt.setValue(Math.min(this.pwrAtSch.getValue(), this.pwrAtNom.getVal()));
			this.calculateFuelRate();
		}
		else
		{
			this.online.setValue(0.0);
			this.pwrAt.setValue(0.0);
			this.calculateFuelRate();
		}
		
	}


	@Override
	public void computeStep(double Ts) {
		// Good Praxis is to use the recompute method to have the stateless business logic in one function only
		// Compute step only updates states (integrators)
		recompute();
		
		this.egyGen.compute(Ts);
		this.fuelVol.compute(Ts);
		this.runTm.compute(Ts/3600.0);
	}
	
	protected void calculateFuelRate()
	{
		// We get the index to know which value we should use...
		int percentLoadIdx = (int)Math.round(((this.pwrAt.getValue()*3.0)/this.pwrAtNom.getVal()));
		double fuelRt = 0;
		
		switch(percentLoadIdx)
		{
			case 0: //25% or less
				fuelRt = this.gCfg.fuel025Pc.getVal() * this.pwrAt.getValue();  // l/kWh * kW = l/h
				break;
			case 1: // 25% to 50%
				fuelRt = this.gCfg.fuel050Pc.getVal() * this.pwrAt.getValue();  // l/kWh * kW = l/h
				break;
			case 2: // 50% to 75%
				fuelRt = this.gCfg.fuel075Pc.getVal() * this.pwrAt.getValue();  // l/kWh * kW = l/h
				break;
			case 3: // 75% to 100%
				fuelRt = this.gCfg.fuel100Pc.getVal() * this.pwrAt.getValue();  // l/kWh * kW = l/h
				break;
		}
		
		this.fuelRate.setValue(fuelRt);
	}


	protected void setSettingsObj(EquipmentSettings s)
	{
		this.gCfg = (ConventionalGeneratorSettings)s;
	}
	
	@Override
	public void reset() {
		this.fuelVol.reset();
		this.egyGen.reset();
		this.runTm.reset();
	}
	
	public void reqStart()
	{
		this.req.setValue(1.0);
	}
	

	public void reqStop()
	{
		this.req.setValue(0.0);
	}

	public double isOnline()
	{
		return this.online.getValue();
	}

	@Override
	public void init() {
		this.statusSignals.add(this.fuelRate);
		this.statusSignals.add(this.genEgyTot);
		this.statusSignals.add(this.genVolTot);
		this.statusSignals.add(this.online);
		this.statusSignals.add(this.genRunTm);
	}

	@Override
	public String getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		ConventionalGenerator result = new ConventionalGenerator();
		result.assimilate(this);
		result.eqClass = EqClass.GEN;
		// Assimiliate Bus Attachments
		result.buses = this.buses;
		return result;
	}
	
	public void setPwrAtSch(double pwrAtSch)
	{
		this.pwrAtSch.setValue(pwrAtSch);
	}
	
	
}
