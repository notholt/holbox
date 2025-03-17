package de.hsrt.holbox.Models.Configuration;

import de.hsrt.holbox.Models.DistributedEnergyResource;
import de.hsrt.holbox.util.DoubleParameter;
import de.hsrt.holbox.util.ModelCodeException;

/**
 * Base class representation of Equipment.
 * @author notholt
 *
 */


public class DistributedEnergyResourceSettings extends EquipmentSettings{
	
	
	
	//@XmlElement
	protected DoubleParameter pwrApNom = new DoubleParameter("pwrApNom", "Nameplate (apparent) power", "kVA", 1.0, 0.0, 10000000.0);
	//@XmlElement
	protected DoubleParameter pwrAtNom = new DoubleParameter("pwrAtNom", "Maximum active power", "kW", 1.0, 0.0, 10000000.0);


	
	public DistributedEnergyResourceSettings()
	{
		super();
		pwrApNom = new DoubleParameter("pwrApNom", "Nameplate (apparent) power", "kVA", 1.0, 0.0, 10000000.0);
		pwrAtNom = new DoubleParameter("pwrAtNom", "Maximum active power", "kW", 1.0, 0.0, 10000000.0);
		this.initMap();
	}
	
	public DistributedEnergyResourceSettings(String id, Double pwrApNom, Double pwrAtNom, String resSigName, Double resInScale) throws ModelCodeException
	{
		this();
		
		setValues( id,  pwrApNom,  pwrAtNom,  resSigName,  resInScale);
	}

	@Override
	public void initMap() {
		super.initMap();
		//this.parList.add(this.id);
		this.parList.add(this.pwrApNom);
		this.parList.add(this.pwrAtNom);

	}
	
	public void setValues(String id, Double pwrApNom, Double pwrAtNom, String resSigName, Double resInScale) throws ModelCodeException
	{
		// BUGGY
		this.id.setValIfNotNull(id);
		this.pwrApNom.setValIfNotNull(pwrApNom);
		this.pwrAtNom.setValIfNotNull(pwrAtNom);
	}
	
	public DistributedEnergyResourceSettings(	String id) throws ModelCodeException
	{
		this();
		this.setValues(id);
	}
	
	
	public String getId()
	{
		return this.id.getVal();
	}
	
	public double getPwrAtNom() {return this.pwrAtNom.getVal();}
	public double getPwrApNom() {return this.pwrApNom.getVal();}
	
	public void setPwrAtNom(double pwrAtNom) {this.pwrAtNom.setVal(pwrAtNom);}
	public void setPwrApNom(double pwrApNom) {this.pwrApNom.setVal(pwrApNom);}

	@Override
	public Class<?> refersTo() {
		return DistributedEnergyResource.class;
	}

	@Override
	public String getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}


}
