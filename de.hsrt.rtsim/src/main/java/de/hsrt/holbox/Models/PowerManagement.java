package de.hsrt.holbox.Models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlSeeAlso;


import de.hsrt.holbox.project.powersystem.ComponentList;
import de.hsrt.holbox.project.powersystem.PowerFlowFactory;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.Simulatable;

/**
 * The abstract class PowerManagement defines the interfaces and basic behaviour of a power management subsystem
 * 
 * @author notholt
 *
 */
@XmlSeeAlso({	SimplePowerManagement.class})
public abstract class PowerManagement extends PowerSystemComponentSettings implements Simulatable{
	
	/**
	 * Initializes power management
	 * @param eql The equipment list to be processed by power management
	 */
	protected boolean error = false;
	
	public abstract void init(ComponentList<Equipment> eql, boolean log);
	
	public abstract void reset();
	public abstract void computeStep(double Ts) throws ModelCodeException;
	public abstract void recompute();
	
	protected ArrayList<DistributedEnergyResource> 	pv 		= new ArrayList<>(0);
	protected ArrayList<DistributedEnergyResource> 	wind 	= new ArrayList<>(0);
	protected ArrayList<Storage> 					stg 	= new ArrayList<>(0);
	protected ArrayList<ConventionalGenerator> 		genset 	= new ArrayList<>(0);
	
	public ArrayList<DistributedEnergyResource>		getPvList()		{	return this.pv;		}
	public ArrayList<DistributedEnergyResource>		getWtList()		{	return this.wind;	}
	public ArrayList<Storage> 						getStgList()	{	return this.stg;	}
	public ArrayList<ConventionalGenerator> 		getGensetList()	{	return this.genset;	}

	public abstract void setPowerFlowModel(PowerFlowFactory pf);
	public abstract void recalculateGensetPowers();

	public boolean hasError() {
		return this.error;
	}


}
