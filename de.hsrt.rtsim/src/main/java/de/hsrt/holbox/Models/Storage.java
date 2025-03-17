package de.hsrt.holbox.Models;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.DoubleParameter;
import de.hsrt.holbox.util.Log;
import de.hsrt.holbox.util.Log.Lvl;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.Signal;

public abstract class Storage extends Equipment{
	
	@XmlElement
	protected DoubleParameter stgDisPwrAtNom = new DoubleParameter("stgDisPwrAtNom", "Battery nominal discharging power", "kW", 0.0, 0.0, 1.0E12);
	@XmlElement
	protected DoubleParameter stgChgPwrAtNom = new DoubleParameter("stgChgPwrAtNom", "Battery nominal charging power", "kW", 0.0, 0.0, 1.0E12);
	@XmlElement
	protected DoubleParameter stgEgyNom = new DoubleParameter("stgEgyNom", "Battery nominal capacity", "kWh", 0.0, 0.0, 1.0E12); 
	
	public Storage()
	{
		super();
		this.eqClass = EqClass.STOR;
	}
	
	public Storage(String id, double storageEgyNom, double storageChgPwrAtNom, double storageDisPwrAtNom) throws ModelCodeException
	{
		super(id, Math.max(storageChgPwrAtNom, storageDisPwrAtNom), Math.max(storageChgPwrAtNom, storageDisPwrAtNom));
		this.eqClass = EqClass.STOR;
		this.stgEgyNom.setVal(storageEgyNom);
		this.stgChgPwrAtNom.setVal(storageChgPwrAtNom);
		this.stgDisPwrAtNom.setVal(storageDisPwrAtNom);

	}
	
	@Override
	public void initMap()
	{
		super.initMap();
		this.parList.add(stgChgPwrAtNom);
		this.parList.add(stgDisPwrAtNom);
		this.parList.add(stgEgyNom);
	}
	
	protected Signal battSoC = new Signal("stgSoC", "%", 0.0);
	protected Signal battSoH = new Signal("stgSoH", "%", 100.0);
	
	protected Signal egyStg = new Signal("egyStored", "kWh", 0.0);

	
	public double getPwrAtDisMax()
	{
		return this.stgDisPwrAtNom.getVal();
	}
	
	public double getPwrAtChgMax()
	{
		return this.stgChgPwrAtNom.getVal();
	}
	
	
	public void setPwrAtSetp(Signal setpoint)
	{
		Log.print(Lvl.WARN, "Attempted to attach setpoint to abstract class Storage");
	}

	
}
