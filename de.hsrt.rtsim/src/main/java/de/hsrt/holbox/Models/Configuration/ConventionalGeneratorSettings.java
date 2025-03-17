package de.hsrt.holbox.Models.Configuration;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.Models.ConventionalGenerator;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.DoubleParameter;
import de.hsrt.holbox.util.ModelCodeException;

public class ConventionalGeneratorSettings extends EquipmentSettings{
	@XmlElement
	public DoubleParameter 	droop 		= new DoubleParameter("GenPwrAtDrpPc", "Genset droop setting", "%", 5.0, 1.0, 10.0);
	//@XmlElement
	//public DoubleParameter 	pwrAtNom 	= new DoubleParameter("pwrAtNom", "Genset nominal (mechanical) power", "kW", 1000.0, 0.1, 50000.0);
	//@XmlElement
	//public DoubleParameter 	pwrApNom 	= new DoubleParameter("pwrApNom", "Genset nameplate power", "kVA", 1250.0, 0.1, 50000.0);
	@XmlElement
	public DoubleParameter 	pwrAtMinPc 	= new DoubleParameter("pwrAtMinPc", "Genset minimum load", "%", 30.0, 0.0, 100.0);
	//https://fwpower.co.uk/wp-content/uploads/2018/12/Diesel-Generator-Fuel-Consumption-Chart-in-Litres.pdf -- Specific cons for 1000kW
	@XmlElement
	public DoubleParameter 	fuel100Pc 	= new DoubleParameter("fuel100pc", "Genset specific consumption at 100%", "l/kWh", 259.0/1000.0, 0.0, 10.0);
	@XmlElement
	public DoubleParameter 	fuel075Pc 	= new DoubleParameter("fuel075pc", "Genset specific consumption at 75%", "l/kWh", 194.0/750.0, 0.0, 10.0);
	@XmlElement
	public DoubleParameter 	fuel050Pc 	= new DoubleParameter("fuel050pc", "Genset specific consumption at 50%", "l/kWh", 130.0/500.0, 0.0, 10.0);
	@XmlElement
	public DoubleParameter 	fuel025Pc 	= new DoubleParameter("fuel025pc", "Genset specific consumption at 25%", "l/kWh", 65.0/250.0, 0.0, 10.0);
	
	
	
	public ConventionalGeneratorSettings()
	{
		super();
		this.initMap();
	}
	
	
	public ConventionalGeneratorSettings(	String id) throws ModelCodeException
	{
		this();
		this.setValues(id);
	}
	
	public void initMap()
	{
		super.initMap();

		this.parList.add(droop);
		//this.parList.add(pwrAtNom);
		//this.parList.add(pwrApNom);
		this.parList.add(pwrAtMinPc);
		this.parList.add(fuel100Pc);
		this.parList.add(fuel075Pc);		
		this.parList.add(fuel050Pc);		
		this.parList.add(fuel025Pc);		
		
	}

	
	@Override
	public Class<?> refersTo() {
		
		return ConventionalGenerator.class;
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		ConventionalGeneratorSettings result = new ConventionalGeneratorSettings();
		result.assimilate(this);
		return result;
	}
	
}
