package de.hsrt.holbox.project.powersystem;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.hsrt.holbox.Models.Equipment;
import de.hsrt.holbox.Models.FrequencyModel;
import de.hsrt.holbox.Models.PowerManagement;
import de.hsrt.holbox.Models.Configuration.EquipmentSettings;
import de.hsrt.holbox.util.DoubleParameter;
import de.hsrt.holbox.util.Log;
import de.hsrt.holbox.util.ParameterList;
import de.hsrt.holbox.util.Settings;

import java.util.List;

/**
 * This class manages a given scenario and has the following properties:
 * * Information about the grid (buses, lines)
 * * Information about the equipment (type, etc.)
 * * Information about the possible combinations (sweep of PV size, Battery, Wind, etc.)
 * @author notholt
 *
 */

@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)

public class PowerSystemSettings extends Settings {

	@XmlElement
	protected DoubleParameter sysFreqNom;
	@XmlElement
	protected DoubleParameter baseMVA;
	//@XmlElement
	protected PowerManagement powerManagement;
	//@XmlElement
	protected FrequencyModel freqModel;
	
	
	protected ComponentList<BusSettings> 		buses;
	
	protected ComponentList<LineSettings> 		lines;
	
	protected ComponentList<LineTypeSettings> 	linetypes;
	
	protected ComponentList<Equipment>			equipment;
	
	protected ComponentList<EquipmentSettings>	equipmentSettings;
	
	
	public PowerSystemSettings()
	{
		sysFreqNom = new DoubleParameter("sysFreqNom", "Nominal System Frequency", "Hz", 50.0, 10.0, 400.0);
		baseMVA = new DoubleParameter("baseMVA", "Base MVA for per-unit calculations", "MVA", 150.0, 1.0, 1000.0);
		
		buses 		= new ComponentList<>();
		lines 		= new ComponentList<>();
		linetypes	= new ComponentList<>();
		
		equipment			= new ComponentList<>();
		equipmentSettings 	= new ComponentList<>();
		this.initMap();
	}
	
	
	public ComponentList<BusSettings> getBuses()
	{
		return this.buses;
	}
	
	public ComponentList<LineSettings> getLines()
	{
		return this.lines;
	}
	
	public ComponentList<LineTypeSettings> getLineTypes()
	{
		return this.linetypes;
	}
	
	public ComponentList<EquipmentSettings> getEquipmentSettings()
	{
		return this.equipmentSettings;
	}
	
	public ComponentList<Equipment> getEquipment()
	{
		return this.equipment;
	}
	
	
	@Override
	public void initMap() {
		this.parList = new ParameterList();
		this.parList.add(this.sysFreqNom);
		this.parList.add(this.baseMVA);		
	}
	
	
	
	@XmlElement(name="bus")
	@XmlElementWrapper(name="buses")
	public void setBusSettings(BusSettings[] busList)
	{
		buses.setFromArray(busList);
	}
	
	public BusSettings[] getBusSettings()
	{
		List<BusSettings> list = buses.getElementList();
		return list.toArray(new BusSettings[list.size()]);
	}
	
	@XmlElement(name="line")
	@XmlElementWrapper(name="lines")
	public void setLineSettings(LineSettings[] lineList)
	{
		lines.setFromArray(lineList);
	}
	
	public LineSettings[] getLineSettings()
	{
		List<LineSettings> list = lines.getElementList();
		return list.toArray(new LineSettings[list.size()]);
	}


	@XmlElement(name="linetype")
	@XmlElementWrapper(name="linetypes")
	public void setLineTypeSettings(LineTypeSettings[] lineList)
	{
		linetypes.setFromArray(lineList);
	}
	
	public LineTypeSettings[] getLineTypeSettings()
	{
		List<LineTypeSettings> list = linetypes.getElementList();
		return list.toArray(new LineTypeSettings[list.size()]);
	}
	
	@XmlElement(name="equipment")
	@XmlElementWrapper(name="equipmentList")
	public void setEquipmentList(Equipment[] list)
	{
		equipment.setFromArray(list);
	}
	
	public Equipment[] getEquipmentList()
	{
		List<Equipment> list = equipment.getElementList();
		return list.toArray(new Equipment[list.size()]);
	}
	
	@XmlElement(name="equipmentSettings")
	@XmlElementWrapper(name="equipmentSettingsList")
	public void setEquipmentSettingsList(EquipmentSettings[] list)
	{
		equipmentSettings.setFromArray(list);
	}
	
	public EquipmentSettings[] getEquipmentSettingsList()
	{
		List<EquipmentSettings> list = equipmentSettings.getElementList();
		return list.toArray(new EquipmentSettings[list.size()]);
	}
	
	
	/**
	 * Used to initialize the object properties when marshalling (reading from XML)	
	 * @param s
	 */
	public void assimilate(PowerSystemSettings s) {
		try {
			
			// Marshalling uses outdated objects so need to rebuild Map of the parsed 
			s.initMap();
			
			// Same as parent
			s.getParameters().getMap().forEach((key, param)->
				{
					try {
						parList.getElementById(key).setVal(param.getVal());
						//System.out.println("[I] Set {" + key + "} to " + param.getVal().toString());
						Log.print(Log.Lvl.INFO, "Set {" + key + "} to " + param.getVal().toString());
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
			);
			// Now we assimilate the arrays
			
			// Buses
			s.buses.assimilateTo(buses);
			// Lines
			s.lines.assimilateTo(lines);
			s.linetypes.assimilateTo(linetypes);
			// Equipment
			s.equipment.assimilateTo(equipment);
			s.equipmentSettings.assimilateTo(equipmentSettings);
			
			this.freqModel = (FrequencyModel)s.freqModel.getAssimilatedObject();
			this.powerManagement = (PowerManagement)s.powerManagement.getAssimilatedObject();
			
		}catch(Exception e)
		{
			Log.print(Log.Lvl.ERROR, "Error while parsing Project Parameters. Reason: "+e.getMessage());
			//System.out.println("[E] Error while parsing Project Parameters ");
		}	
		
	}

	
	public boolean comparable(PowerSystemSettings s)
	{
		Log.print(Log.Lvl.WARN, "Checking if power system settings have been correctly loaded has not been yet implemented!!");
		return true;
	}
	
	@XmlElement
	public void setPowerManagement(PowerManagement pm) {this.powerManagement = pm;}
	public PowerManagement getPowerManagement() {return this.powerManagement;}
	

	public void setFrequencyModel(FrequencyModel fm) {this.freqModel = fm;}
	public FrequencyModel getFrequencyModel() {return this.freqModel;}
	
}
