package de.hsrt.holbox.Models.Configuration;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.Models.*;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.DoubleParameter;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.StringParameter;

public class ControlledDERSettings extends DistributedEnergyResourceSettings {
	
	@XmlElement
	public DoubleParameter 	freqNom		= new DoubleParameter("freqNom", "Nominal Frequency", "Hz", 50.0, 40.0, 70.0);
	//@XmlElement
	//public DoubleParameter 	pwrAtPk 	= new DoubleParameter("PvPwrAtPk", "PV peak power", "kWp", 1000.0, 0.0, 50000.0);
	//@XmlElement
	//public DoubleParameter 	pwrAtNom 	= new DoubleParameter("PvPwrAtNom", "nominal power", "kW", 1000.0, 0.0, 50000.0);
	//@XmlElement
	//public DoubleParameter 	pwrApNom 	= new DoubleParameter("PvPwrApNom", "nameplate power", "kVA", 1250.0, 0.0, 50000.0);
	
	@XmlElement
	public DoubleParameter 	hzDroopPc	= new DoubleParameter("hzDroopPc", "Droop setting", "%", 5.0, 1.0, 10.0);
	@XmlElement
	public DoubleParameter 	hzStart		= new DoubleParameter("hzStart", "Begin of droop characteristic", "Hz", 50.2, 47.0, 53.0);
	
	@XmlElement
	public DoubleParameter 	hzFilTau		= new DoubleParameter("hzfilTau", "Time constant of frequency input filter", "s", 1.0, 0.001, 3600.0);
	@XmlElement
	public DoubleParameter 	pwrAtFilTau		= new DoubleParameter("pwrAtFilTau", "Time constant of active power output filter", "s", 1.0, 0.001, 3600.0);
	@XmlElement
	public StringParameter  resPwrSigName	= new StringParameter("resPwrSigName", "Signal name for ressource active power in kW/kW_nom nom power", "", "pvPwrAtNorm");
	


	
	public ControlledDERSettings()
	{
		super();
		/*freqNom		= new DoubleParameter("freqNom", "Nominal Frequency", "Hz", 50.0, 40.0, 70.0);
		pwrAtPk 	= new DoubleParameter("PvPwrAtPk", "PV peak power", "kWp", 1000.0, 0.0, 50000.0);
		hzDroopPc	= new DoubleParameter("hzDroopPc", "Droop setting", "%", 5.0, 1.0, 10.0);
		hzStart		= new DoubleParameter("hzStart", "Begin of droop characteristic", "Hz", 50.2, 47.0, 53.0);
		hzFilTau		= new DoubleParameter("hzfilTau", "Time constant of frequency input filter", "s", 1.0, 0.001, 3600.0);
		pwrAtFilTau		= new DoubleParameter("pwrAtFilTau", "Time constant of active power output filter", "s", 1.0, 0.001, 3600.0);
		resPwrSigName	= new StringParameter("resPwrSigName", "Signal name for ressource active power in kW/kW_nom nom power", "", "pvPwrAtNorm");*/
		
		this.initMap();
	}
	
	public ControlledDERSettings(String id) throws ModelCodeException
	{
		this();
		this.id.setVal(id);
		//initMap();
	}
	
	public void initMap()
	{
		super.initMap();
		//this.parList.add(pwrAtNom);
		//this.parList.add(pwrApNom);

		this.parList.add(hzDroopPc);
		this.parList.add(hzStart);

		this.parList.add(hzFilTau);
		this.parList.add(pwrAtFilTau);
		this.parList.add(resPwrSigName);

	}

	public double hzDroopPc() {return this.hzDroopPc.getVal();}
	public double hzStart() {return this.hzStart.getVal();}
	public double hzFilTau() {return this.hzFilTau.getVal();}
	public double pwrAtFilTau() {return this.pwrAtFilTau.getVal();}
	
	@Override
	public Class<?> refersTo() {
		
		return ControlledDER.class;
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		ControlledDERSettings result = new ControlledDERSettings();
		result.assimilate(this);
		return result;
	}
	
}
