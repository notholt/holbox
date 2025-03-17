package de.hsrt.holbox.Models.Configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import de.hsrt.holbox.Models.Equipment;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.ParameterList;
import de.hsrt.holbox.util.StringParameter;

/**
 * Base class representation of Equipment.
 * @author notholt
 *
 */

@XmlSeeAlso({	DistributedEnergyResourceSettings.class,
				ControlledDERSettings.class,
				ConventionalGeneratorSettings.class})
public class EquipmentSettings extends PowerSystemComponentSettings{
	
	@XmlElement
	protected StringParameter id;

	
	public EquipmentSettings()
	{
		super();
		id 			= new StringParameter("ID", "Node ID", "", null);
		this.initMap();
	
	}

	@Override
	public void initMap() {
		this.parList = new ParameterList();
		this.parList.add(this.id);
		
	}
	
	public void setValues(String id) throws ModelCodeException
	{
		this.id.setVal(id);
	}
	
	public EquipmentSettings(	String id) throws ModelCodeException
	{
		this();
		this.setValues(id);
	}
	
	
	public String getId()
	{
		return this.id.getVal();
	}
	
	public Class<?> refersTo()
	{
		return Equipment.class;
	}

	@Override
	public String getDescriptor() {
		return "GENERIC EQUIPMENT: NOT VALID";
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		EquipmentSettings result = new EquipmentSettings();
		result.assimilate(this);
		return result;
	}

}
