package de.hsrt.holbox.Models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import de.hsrt.holbox.Models.Configuration.DistributedEnergyResourceSettings;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.*;

/**
 * Generic class for all Distributed Energy Resources (DER)
 * Note that this generic model only takes an input signal, scales it and
 * gives it as active power. Serves as basis for all others.
 * 
 * @author notholt
 *
 */

@XmlRootElement
public class DistributedEnergyResource extends Equipment {
	
	
	DistributedEnergyResourceSettings p;
	
	@XmlElement
	protected StringParameter resPwrSigName	= new StringParameter("resPwrSigName", "Signal name for ressource active power in kW/kW_nom nom power", "", null);
	@XmlElement
	protected DoubleParameter resInScale	= new DoubleParameter("resInScale", "Input scaling of resource", "--", 1.0, 0.0000001, 1000000.0);// TODO: Not yet used
	@XmlElement
	public StringParameter  eqClassP	= new StringParameter("eqClass", "Generator Class", "", "UNDEFINED");
	@XmlElement
	public BooleanParameter  scalable	= new BooleanParameter("scalable", "To be considered when scaling", "", false);

	protected Signal resPwr;
	protected EnergyCounter egy;
	
	protected double scale = 1.0;
	
	public DistributedEnergyResource() 
	{
		super();
		this.eqClass = EqClass.valueOf(this.eqClassP.getVal());
		this.initMap();
	}


	
	public DistributedEnergyResource(String id, DistributedEnergyResourceSettings settings, double pwrApNom, double pwrAtNom, String sigName, double inScale) throws ModelCodeException 
	{
		super(id, pwrApNom, pwrAtNom);
		this.settingsId.setVal((settings!=null ? settings.getId(): null));
		this.resPwrSigName.setVal(sigName);
		this.resInScale.setVal(inScale);
		this.eqClass = EqClass.valueOf(this.eqClassP.getVal());
		this.initMap();
	}
	
	public void setPwrAtNom(double pwrAtNom) throws ModelCodeException
	{
		this.pwrAtNom.setVal(pwrAtNom);
	}
	
	public void setPwrApNom(double pwrApNom)
	{
		this.pwrApNom.setVal(pwrApNom);
	}
	
	public void setScale(double s)
	{
		//if (this.scalable.isTrue())  // In Version 1 all PV is scalable
		{
			this.scale = s;
			this.pwrApNomScaled = s*this.pwrApNom.getVal();
			this.pwrAtNomScaled = s*this.pwrAtNom.getVal();
		}
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		this.egy.reset();
	}

	@Override
	public void recompute() {
		if(this.resPwr == null)
		{
			this.resPwr = this.inputSignals.getElementById(this.resPwrSigName.getVal());
		}
		
		this.pwrAt.setValue(resPwr.getValue() * resInScale.getVal() * this.pwrAtNomScaled);
		
		// If we assume no cosphi or Q control, we have no reactive power

		this.pwrRt.setValue(0.0);

	}

	@Override
	public void computeStep(double Ts) throws ModelCodeException {
		
		if(this.resPwr == null)
		{
			this.resPwr = this.inputSignals.getElementById(this.resPwrSigName.getVal());
		}
		
		this.pwrAt.setValue(resPwr.getValue() * resInScale.getVal() * this.pwrAtNomScaled);
		
		// If we assume no cosphi or Q control, we have no reactive power

		this.pwrRt.setValue(0.0);
		
		this.egy.compute(Ts);
	}
	
	public void initMap()
	{
		super.initMap();
		this.parList.add(resPwrSigName);
		this.parList.add(resInScale);
		this.parList.add(eqClassP);
		this.parList.add(scalable);
	}


	@Override
	public void init() throws ModelCodeException {
		// Initializing and declaring Input Signal Names
		this.getInputSignalNames().add(this.resPwrSigName.getVal());
		Signal egyTot = new Signal("egyTot", "kWh", 0.0);
		this.egy = new EnergyCounter(this.pwrAt, egyTot);
		this.statusSignals.add(egyTot);
		
		this.pwrApNomScaled = (this.scalable.isTrue()? this.pwrApNom.getVal()*this.scale: this.pwrApNom.getVal()); 
		this.pwrAtNomScaled = (this.scalable.isTrue()? this.pwrAtNom.getVal()*this.scale: this.pwrAtNom.getVal());
	}



	@Override
	public String getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		DistributedEnergyResource result = new DistributedEnergyResource();
		result.assimilate(this);
		result.eqClass = EqClass.valueOf(this.eqClassP.getVal());
		// Assimiliate Bus Attachments
		result.buses = this.buses;
		return result;
	}
	
	public void setEqClass(EqClass gc) throws ModelCodeException
	{
		this.eqClass = gc;
		this.eqClassP.setVal(gc.name);
	}
	
	public boolean isControllable()
	{
		return false;
	}
	
	public double getPwrAtRes()
	{
		return this.resPwr.getValue();
	}

}
