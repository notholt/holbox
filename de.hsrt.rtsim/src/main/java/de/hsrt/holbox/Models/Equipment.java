package de.hsrt.holbox.Models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import de.hsrt.holbox.ClassicPowerFlow.Sys;
import de.hsrt.holbox.Models.Configuration.EquipmentSettings;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.*;
import de.hsrt.holbox.util.Log.Lvl;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent class for all Actors in the electrical power system this includes: 
 * Generators, Loads, FACTS, etc. but does not include other passive equipment 
 * such as lines, transformers, busbars, protections. These fall under the category 
 * power system components (see project.powersystem)
 * 
 * Note: This class extends PowerSystemComponentSettings in order to provide Save/Load 
 * functionality without the need of rewritting a lot of code...
 *  
 * @author notholt
 *
 */

@XmlSeeAlso({	ConventionalGenerator.class,
				ControlledDER.class,
				DistributedEnergyResource.class,
				SimpleBattery.class})
public class Equipment extends PowerSystemComponentSettings implements Simulatable{
	
	private ArrayList<String> inputSignalNames = new ArrayList<>(0);
	protected EqClass eqClass;
	
	@XmlElement
	protected StringParameter id = new StringParameter("EquipmentId", "Equipment ID", "", "");
	@XmlElement
	protected StringParameter settingsId = new StringParameter("SettingsId", "Equipment Settings ID", "", null);
	@XmlElement
	protected DoubleParameter pwrApNom = new DoubleParameter("pwrApNom", "Nameplate power", "kVA", 1.0, 0.0, 10000000.0);
	@XmlElement
	protected DoubleParameter pwrAtNom = new DoubleParameter("pwrAtNom", "Maximum AC active power", "kW", 1.0, 0.0, 10000000.0);
	@XmlElement
	protected DoubleParameter pwrApShort = new DoubleParameter("pwrApShort", "Short circuit power (max I on fault x nominal voltage)", "kVA", 0.0, 0.0, 10000000.0);
	@XmlElement
	protected BooleanParameter managed = new BooleanParameter("managed", "Equipment is seen by power management","", false);

	@XmlElement(name="BusAttachment")
	ArrayList<BusAttachment> buses = new ArrayList<>(0);
	
	protected SignalList inputSignals = new SignalList();
	protected SignalList controlSignals = new SignalList();
	protected SignalList statusSignals = new SignalList();
	
	protected Signal pwrAt = new Signal("PwrAt", "kW", 0.0, true);
	protected Signal pwrRt = new Signal("PwrRt", "kvar", 0.0, false); // Logging of reactive power not needed now
	
	protected Signal sysFreq;
	protected double sysFreqNom;
	protected double currentTs;
	protected double pwrAtNomScaled;
	protected double pwrApNomScaled;
	
	public Equipment()
	{
		super();
		this.statusSignals.add(this.pwrAt);
		this.statusSignals.add(this.pwrRt);
		if(eqClass == null) {eqClass = EqClass.UNDEFINED;}
		this.initMap();
		//Log.print(Lvl.DEBUG, "Equipment:70 Parlist" + this.parList);
	}
	
	
	public Equipment(String id, double pwrApNom, double pwrAtNom) throws ModelCodeException
	{
		this.statusSignals.add(this.pwrAt);
		this.statusSignals.add(this.pwrRt);
		this.id.setVal(id);
		this.pwrApNom.setVal(pwrApNom);
		this.pwrAtNom.setVal(pwrAtNom);
		this.pwrAtNomScaled = pwrAtNom;
		this.pwrApNomScaled = pwrApNom;
		
		// TODO: Assuming there is only one class defining the system wide variables.....
		this.sysFreq = Sys.sysFreq;
		this.sysFreqNom = Sys.freqNom;
		
		if(eqClass == null) {eqClass = EqClass.UNDEFINED;}
		this.initMap();
	}
	
	/**
	 * Resets the actor for a new simulation
	 */
	public  void reset() {}

	
	/**
	 * Recomputes the current step
	 */
	public  void recompute() {}
	
	/**
	 * Computes the next integration step
	 * @param Ts
	 * @throws ModelCodeException 
	 */
	public  void computeStep(double Ts) throws ModelCodeException{}
	
	
	/**
	 * Initializes equipment. Especially interesting for setting input signals and deploying output signals
	 * @throws ModelCodeException 
	 */
	public  void init() throws ModelCodeException
	{
		this.pwrApNomScaled = this.pwrApNom.getVal();
		this.pwrAtNomScaled = this.pwrAtNom.getVal();
	}
	
	
	/**
	 * Returns the control signals for the given actor
	 * @return
	 */
	public SignalList getInputSignals()
	{
		return this.inputSignals;
	}
	
	
	/**
	 * Returns the control signals for the given actor
	 * @return
	 */
	public SignalList getControlSignals()
	{
		return this.controlSignals;
	}
	
	
	/**
	 * Returns the status signals for the given actor
	 * @return
	 */
	public SignalList getStatusSignals()
	{
		return this.statusSignals;
	}
	
	public void setFrequencySignal(Signal f, double sysFNom)
	{
		this.sysFreq = f;
		this.sysFreqNom = sysFNom;
	}
	
	public void initMap()
	{
		this.parList = new ParameterList();
		this.parList.add(id);
		this.parList.add(settingsId);
		this.parList.add(pwrApNom);
		this.parList.add(pwrAtNom);
		this.parList.add(pwrApShort);
		this.parList.add(managed);
	}
		
	public String getId()
	{
		return this.id.getVal();
	}
	
	public List<BusAttachment> getBuses()
	{
		return this.buses;
	}
	
	public void addBus(String id, double scale, BusAttachment.Type as)
	{
		this.buses.add(new BusAttachment(id, scale, as));
	}
	
	public EqClass getEqClass()
	{
		return this.eqClass;
	}
	
	public String getSettingsId()
	{
		return this.settingsId.getVal();
	}
	
	protected void setSettingsObj(EquipmentSettings s)
	{
		Log.print(Lvl.WARN, "Settings <"+s.getId()+">have been imported but are not used yet they are implementation specific");
	}
	
	public void setSettings(EquipmentSettings s) throws ModelCodeException
	{
		if(s.refersTo() == this.getClass())
		{
			setSettingsObj(s);
		}else
		{
			throw new ModelCodeException("Settings <"+ s.getId() +"> are of type " 
											+ s.refersTo().getSimpleName() 
											+  " not of the same type of the current class: "
											+ this.getClass().getSimpleName());
		}
	}
	
	public ArrayList<String> getInputSignalNames()
	{
		return this.inputSignalNames;
	}
	
	public void addPwrToAttachedBuses()
	{
		for(BusAttachment ba: this.buses)
		{
			if(ba.bus!=null)
			{
				if(ba.as == BusAttachment.Type.GEN)
				{
					ba.bus.setPwrAtGen(ba.bus.getPwrAtGen() + this.pwrAt.getValue()/(1000.0*Sys.baseMVA));
					ba.bus.setPwrRtGen(ba.bus.getPwrRtGen() + this.pwrRt.getValue()/(1000.0*Sys.baseMVA) );
				}else // Is load
				{
					ba.bus.setPwrAtDem(ba.bus.getPwrAtDem() + this.pwrAt.getValue()/(1000.0*Sys.baseMVA) );
					ba.bus.setPwrRtDem(ba.bus.getPwrRtDem() + this.pwrRt.getValue()/(1000.0*Sys.baseMVA) );
				}
			}else
			{
				Log.print(Lvl.WARN, "BusAttachment has information but not a real bus attached in "+this.getId()+"->"+ba.id);
			}
		}
	}


	@Override
	public String getDescriptor() {
		
		return "GENERIC EQUIPMENT: NOT VALID";
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		Equipment result = new Equipment();
		result.assimilate(this);
		return result;
	}


	@Override
	public SignalList getOutputSignals() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}
	
	public double getPwrAtNom()
	{
		return this.pwrAtNomScaled;
	}
	
	public double getPwrAt()
	{
		return this.pwrAt.getValue();
	}
	
	public double getPwrRt()
	{
		return this.pwrRt.getValue();
	}
	
}
