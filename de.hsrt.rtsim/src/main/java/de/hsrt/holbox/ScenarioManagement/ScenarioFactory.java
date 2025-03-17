package de.hsrt.holbox.ScenarioManagement;

import java.util.ArrayList;

import de.hsrt.holbox.ClassicPowerFlow.Bus;
import de.hsrt.holbox.ClassicPowerFlow.Sys;
import de.hsrt.holbox.Models.BusAttachment;
import de.hsrt.holbox.Models.EqClass;
import de.hsrt.holbox.Models.Equipment;
import de.hsrt.holbox.Models.FrequencyModel;
import de.hsrt.holbox.Models.PowerManagement;
import de.hsrt.holbox.Models.Configuration.EquipmentSettings;
import de.hsrt.holbox.project.dataset.DataManager;
import de.hsrt.holbox.project.powersystem.*;
import de.hsrt.holbox.util.Log;
import de.hsrt.holbox.util.Log.Lvl;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.Signal;
import de.hsrt.holbox.Models.*;

public class ScenarioFactory {
	
	private PowerSystemSettings 	ps;
	private Scenario				s;
	private PowerManagement			pm;
	private FrequencyModel			fm;
	
	public ScenarioFactory(PowerSystemSettings ps)
	{
		this.ps = ps;
	}
	
	/**
	 * Initializes the internal structures beginning from Settings
	 * @throws ModelCodeException 
	 */
	public void init(boolean log) throws ModelCodeException
	{
		
		// We assign the required variables
		this.s = new Scenario();
		
		this.pm = ps.getPowerManagement(); // power management
		this.fm = ps.getFrequencyModel(); // Frequency model
		
		PowerFlowFactory pff = new PowerFlowFactory(ps);
		
		this.s.pff = pff;
		this.s.busSettings = this.ps.getBuses();
		this.s.equipment = this.ps.getEquipment();
		
		// Very first thing: We define base power
		Sys.baseMVA = (double)ps.getParameters().getElementById("baseMVA").getVal();
		Sys.freqNom = (double)ps.getParameters().getElementById("sysFreqNom").getVal();
		
		// Setting power flow problem
		this.s.pf = pff.getPowerFlow();
		
		//Parse Equipment, its Settings and check if attached buses are actually there 
		for (Equipment eq : this.ps.getEquipment().getElementList())
		{
			
			// First we check if we need Settings for Equipment
			if(eq.getSettingsId() != null)
			{
				//Log.print(Lvl.DEBUG, "For EqID: " + eq.getId() + " Settings ID: " + eq.getSettingsId());
				EquipmentSettings eqs = ps.getEquipmentSettings().getElementById(eq.getSettingsId());
				eq.setSettings(eqs);
			}
			
			// We initialize the equipment
			eq.init();
			
			ArrayList<Bus> buses = pff.getBuses();
			
			// Now we add bus objects to BusAttachments
			for(BusAttachment b : eq.getBuses())
			{
				for(int i = 0; i < buses.size(); i++)
				{
					if(b.id.equals(buses.get(i).getBusID()))
					{
						b.bus = buses.get(i);
						break;
					}
				}
				if (b.bus == null)
				{
					throw new ModelCodeException("On Equipment {"+eq.getId()+"} could not find bus to attach to: ["+b.id+"]");
				}
			}
			
			// We attach signals to required signal inputs
			for(String name: eq.getInputSignalNames())
			{
				try {
					eq.getInputSignals().add(DataManager.getInputDataset().getSignals().getElementById(name));
				}catch(Exception e)
				{
					throw new ModelCodeException("On Equipment {"+eq.getId()+"} could not find input signal to be attached: ["+name+"]");
				}
			}
			
			// We attach output signals to DataManger
			for(Signal sig: eq.getStatusSignals().getMap().values())
			{
				if(sig.isLogged())
				{
					DataManager.getResultsDataset().addSignalWithPrefix(sig, eq.getId());
				}
			}
		}
		
		// We now attach Equipment to power management
		if(this.pm != null) // If no power management
		{
			this.pm.setPowerFlowModel(this.s.pff);
			this.pm.init(this.ps.getEquipment(), log);
			this.s.pm = this.pm;
		}else
		{
			// If no power management then all renewables just go uncontrolled but no error is necessary
			Log.print(Lvl.WARN, "Could not find a power management in the project description");
		}

		
	}
	
	public Scenario getScenario() throws ModelCodeException
	{
		//init();
		return this.s;
	}
	
	public void setPvScale(double scale)
	{
		for(Equipment e: this.ps.getEquipment().getElementList())
		{
			if(e.getEqClass() == EqClass.PV)
			{
				DistributedEnergyResource der = (DistributedEnergyResource)e;
				der.setScale(scale);
			}
		}
	}
	
	public void setWtScale(double scale)
	{
		for(Equipment e: this.ps.getEquipment().getElementList())
		{
			if(e.getEqClass() == EqClass.WIND)
			{
				DistributedEnergyResource der = (DistributedEnergyResource)e;
				der.setScale(scale);
			}
		}
	}
	
	public void setBattScale(double scale)
	{
		for(Equipment e: this.ps.getEquipment().getElementList())
		{
			if(e.getEqClass() == EqClass.STOR)
			{
				// TODO: Derive Simple Battery from DER
				SimpleBattery der = (SimpleBattery)e;
				der.setScale(scale);
				der.init();
			}
		}
	}

}
