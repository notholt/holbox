package de.hsrt.holbox.Models;


import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.ClassicPowerFlow.Bus;
import de.hsrt.holbox.ClassicPowerFlow.Sys;
import de.hsrt.holbox.project.dataset.DataManager;
import de.hsrt.holbox.project.powersystem.ComponentList;
import de.hsrt.holbox.project.powersystem.PowerFlowFactory;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.*;
import de.hsrt.holbox.util.Log.Lvl;

public class SimplePowerManagement extends PowerManagement{
	
	@XmlElement
	protected DoubleParameter genResPwrAtPc = new DoubleParameter("GenResPwrAtPc", "Load reserve for spinning reserve", "%", 25.0, 0.0, 100.0); 
	@XmlElement
	protected DoubleParameter confidenceValue = new DoubleParameter("confidenceValue", "Confidence Value for PV", "%", 50.0, 0.0, 100.0);
	
	protected PowerFlowFactory pff;
	
	protected int genOn = 1; // Number of generators on
	
	// Aggregated Generator Signals
	protected Signal pvPwrAtNomTot = new Signal("pvPwrAtNomTot", "kW", 0.0);
	protected Signal windPwrAtNomTot = new Signal("windPwrAtNomTot", "kW", 0.0);
	
	protected Signal stgPwrAtChgMaxTot = new Signal("stgPwrAtChgMaxTot", "kW", 0.0);
	protected Signal stgPwrAtDisMaxTot = new Signal("stgPwrAtDisMaxTot", "kW", 0.0);
	protected Signal stgEgyTot = new Signal("stgEgyTot", "kWh", 0.0);
	protected Signal stgEgyCapTot = new Signal("stgEgyCapTot", "kWh", 0.0);
	protected Signal stgSoCTot = new Signal("stgSoCTot", "kWh", 0.0);
	protected Signal genPwrAtNomTot = new Signal("genPwrAtNomTot", "kW", 0.0);
	
	protected Signal pvPwrAtTot = new Signal("pvPwrAtTot", "kW", 0.0);
	protected Signal pvPwrAtResTot = new Signal("pvPwrAtResTot", "kW", 0.0);
	protected Signal windPwrAtTot = new Signal("windPwrAtTot", "kW", 0.0);
	protected Signal windPwrAtResTot = new Signal("windPwrAtResTot", "kW", 0.0);
	protected Signal stgPwrAtTot = new Signal("stgPwrAtTot", "kW", 0.0);
	protected Signal genPwrAtTot = new Signal("genPwrAtTot", "kW", 0.0);
	protected Signal ldPwrAtTot = new Signal("ldPwrAtTot", "kW", 0.0);
	protected Signal ldPwrRtTot = new Signal("ldPwrAtTot", "kW", 0.0);
	protected Signal genPwrAtMin = new Signal("genPwrAtMin", "kW", 0.0);
	
	protected Signal pvPwrAtMaxPc = new Signal("pvPwrAtMaxPc", "%", 100.0);
	protected Signal wtPwrAtMaxPc = new Signal("wtPwrAtMaxPc", "%", 100.0);
	protected Signal stgPwrAtSetpTot = new Signal("stgPwrAtSetpTot", "kW", 100.0);
	protected Signal genPwrAtTotOnline = new Signal ("genPwrAtTotOnline", "kW", 0.0);
	protected Signal ldEgyTot = new Signal ("ldEgyTot", "kWh", 0.0);

	private Signal ldSlack = new Signal("ldSlack", "kW", 0.0);
	
	protected EnergyCounter ldEgyCounter = new EnergyCounter(ldPwrAtTot, ldEgyTot); 
	
	protected SignalList outputSignals = new SignalList();
	
	private void logSignals()
	{
		DataManager.getResultsDataset().addSignalWithPrefix(ldPwrAtTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(ldSlack, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(genPwrAtNomTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(genPwrAtTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(genPwrAtMin, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(pvPwrAtMaxPc, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(pvPwrAtResTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(wtPwrAtMaxPc, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(windPwrAtResTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(stgPwrAtTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(stgEgyTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(stgPwrAtSetpTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(stgPwrAtChgMaxTot, "PM");
		DataManager.getResultsDataset().addSignalWithPrefix(stgPwrAtDisMaxTot, "PM");
	}
	
	@Override
	public void reset() {
		this.error = false;
		//Resets all components
		for (Equipment i : pv)
		{
			i.reset();
		}
		for (Equipment i : wind)
		{
			i.reset();
		}
		for (Equipment i : stg)
		{
			i.reset();
		}
		for (Equipment i : genset)
		{
			i.reset();
		}
		
		this.ldEgyCounter.reset();
		
	}
	
	@Override
	public void recompute() {
		
		//Resets all components
		for (Equipment i : pv)
		{
			i.recompute();
		}
		for (Equipment i : wind)
		{
			i.recompute();
		}
		
		for (Equipment i : stg)
		{
			i.recompute();
		}
		for (Equipment i : genset)
		{
			i.recompute();
		}	
		
	}

	@Override
	public void computeStep(double Ts) throws ModelCodeException {
		
		// Step 1: Fill out all status signals
		recompute();
		calculateAggregatedSignals();
		
		// Step 2: Calculate load from previous step
		calculateLoad();
		this.ldEgyCounter.compute(Ts);
		
		// Step 2.1: Calculate the current power of gensets
		this.recalculateGensetPowers();
		
		
		// Step 2.9: We take out the nominal power of energy storage since we consider this is a spinning reserve provider
		// This is accounted as a reduction in load required for the calculation of reserve power.
		double pwrAtNomStg = 0.0;
		
		for (Storage i : stg)
		{
			pwrAtNomStg += i.getPwrAtDisMax();
		}

		double spinningReserveLoad = 0.01*(this.genResPwrAtPc.getVal()+100.0)*this.ldPwrAtTot.getValue()  
									- (0.01 * this.genResPwrAtPc.getVal() * pwrAtNomStg); // TODO: Confidence interval -	Math.min(this.pvPwrAtNomTot.getValue()*0.01*this.confidenceValue.getVal(), this.pvPwrAtTot.getValue());


		// Step 3: Calculate number of Gensets to run
		double pwrAtSpinning = 0.0;
		
		
		int gensets = 0;
		for(int i=0; i < this.genset.size(); i++)
		{
			if(pwrAtSpinning < spinningReserveLoad)
			{
				this.genset.get(i).req.setValue(1.0);
				pwrAtSpinning += this.genset.get(i).pwrAtNom.getVal();
				gensets++;
			}else
			{
				this.genset.get(i).req.setValue(0.0);
			}
		}
		
		// If no gensets are required, the first one is turned on unless there is no gensets
		if(gensets==0 && this.genset.size()>0)
		{
			this.genset.get(0).req.setValue(1.0);
		}
		else
		{
			if(this.genset.size()==0) {throw new ModelCodeException("SimplePowerManagement: No gensets are configured!!");}
		}
		

		this.recompute();
		this.calculateAggregatedSignals();
		this.recalculateGensetPowers();
		this.calculateAggregatedSignals();
		
		
		// Step 4: Calculate maximum PV. This is Load - GensetMinLoad + Maximum Charging Power of Battery
		
		recomputePvPwrSetpoints();
		//this.pvPwrAtMaxPc.setValue(10.0);
		
		// Step 4.1: Calculate maximum Wind. This is Load - GensetMinLoad + Maximum Charging Power of Battery
		double wtPwrAtMax = this.ldPwrAtTot.getValue() - this.genPwrAtMin.getValue() + this.stgPwrAtTot.getValue();
		double wtPwrAtMaxPc = Math.max(100.0 * wtPwrAtMax / Math.max(this.pvPwrAtNomTot.getValue() + this.windPwrAtNomTot.getValue(), 0.1), 0.0);
		this.wtPwrAtMaxPc.setValue(wtPwrAtMaxPc);
		
		// Step 5: Calculate actual PV power injection
		
		this.recompute();
		this.calculateAggregatedSignals();
		
		/*this.pvPwrAtTot.setValue(0.0);
		for (DistributedEnergyResource i : pv)
		{
			i.recompute();
			this.pvPwrAtTot.addValue(i.pwrAt.getValue());
		}
		
		this.windPwrAtTot.setValue(0.0);
		for (DistributedEnergyResource i : wind)
		{
			i.recompute();
			this.windPwrAtTot.addValue(i.pwrAt.getValue());
		}*/
		
		// Step 6: Calculate battery setpoint
		// Very simple algorithm... just charge excess PV and discharge until SoC 20%
		double resource = this.windPwrAtResTot.getValue() + this.pvPwrAtResTot.getValue();
		double injection = this.windPwrAtTot.getValue() + this.pvPwrAtTot.getValue();
		if( resource > injection + 1.0)
		{
			double battPwrAtChg = Math.min(resource-injection, this.stgPwrAtChgMaxTot.getValue()); // Maximum is only dependent on the excess energy
			
			this.stgPwrAtSetpTot.setValue(-battPwrAtChg);
		}else
		{
			double battPwrAtDis = Math.max(Math.min(	this.ldPwrAtTot.getValue() 
											- this.genPwrAtMin.getValue() 
											- this.pvPwrAtTot.getValue()
											- this.windPwrAtTot.getValue(), this.stgPwrAtDisMaxTot.getValue()), 0.0);
			
			this.stgPwrAtSetpTot.setValue(battPwrAtDis);
		}
		
		
		this.recomputePvPwrSetpoints();
		this.recompute();
		this.calculateAggregatedSignals();
		//this.pff.resetPowerSystemPowers();
		//this.pff.runPowerFlow();
		this.recalculateGensetPowers();
		this.calculateAggregatedSignals();
	}
	
	
	private void recomputePvPwrSetpoints()
	{
		double pvPwrAtMax = this.ldPwrAtTot.getValue() - this.genPwrAtMin.getValue() - this.stgPwrAtSetpTot.getValue();
		double pvPwrAtMaxPc = Math.max(100.0 * pvPwrAtMax / Math.max(this.windPwrAtNomTot.getValue() + this.pvPwrAtNomTot.getValue(), 0.1), 0.0);
		this.pvPwrAtMaxPc.setValue(pvPwrAtMaxPc);
	}
	
	private void calculateLoad() throws ModelCodeException
	{
		
		// THis method uses the direct measurement of all loads... 
		// We assume we have a power Flow if not in version 1 we error!
		if(this.pff != null)
		{
			double ldPwrAtTot = 0.0;
			double ldPwrRtTot = 0.0;
			for(Bus b : this.pff.getBuses())
			{
				ldPwrAtTot += b.getPwrAtDem()*1000.0*Sys.baseMVA;
				ldPwrRtTot += b.getPwrRtDem()*1000.0*Sys.baseMVA;
			}
			this.ldPwrAtTot.setValue(ldPwrAtTot /*+ this.pff.getPwrAtLoss()*/); // The total active and reactive power are 
			this.ldPwrRtTot.setValue(ldPwrRtTot);							// the sum of all generating active and reactive power plus grid losses.
		}else
		{
			throw new ModelCodeException("Cannot calculate load from generating nodes because there is no power flow problem defined in SimplePowerManagement");
		}
		
		/*
		// We recalculate the powers from genset
		// The actual effective load is the load measured by all managed equipment. All other are considered negative loads
		recalculateGensetPowers();
		this.ldPwrAtTot.setValue(0.0);
		this.ldPwrRtTot.setValue(0.0);
		
		for(Equipment i : this.genset)
		{
			this.ldPwrAtTot.addValue(i.getPwrAt());
			this.ldPwrRtTot.addValue(i.getPwrRt());
		}
		
		for(Equipment i : this.pv)
		{
			this.ldPwrAtTot.addValue(i.getPwrAt());
			this.ldPwrRtTot.addValue(i.getPwrRt());
		}
		
		for(Equipment i : this.wind)
		{
			this.ldPwrAtTot.addValue(i.getPwrAt());
			this.ldPwrRtTot.addValue(i.getPwrRt());
		}
		
		for(Equipment i : this.stg)
		{
			this.ldPwrAtTot.addValue(i.getPwrAt());
			this.ldPwrRtTot.addValue(i.getPwrRt());
		}*/
		
		
	}
	
	private void calculateAggregatedSignals()
	{
		// Aggregated Signals of PV
		this.pvPwrAtNomTot.setValue(0.0);
		this.pvPwrAtTot.setValue(0.0);
		this.pvPwrAtResTot.setValue(0.0);
		for(int i=0; i<this.pv.size(); i++)
		{
			this.pvPwrAtNomTot.addValue(pv.get(i).getPwrAtNom());
			this.pvPwrAtTot.addValue(pv.get(i).pwrAt.getValue());
			this.pvPwrAtResTot.addValue(pv.get(i).getPwrAtRes());
		}
		
		// Aggregated Signals of Wind
		this.windPwrAtNomTot.setValue(0.0);
		this.windPwrAtTot.setValue(0.0);
		this.windPwrAtResTot.setValue(0.0);
		for(int i=0; i<this.wind.size(); i++)
		{
			this.windPwrAtNomTot.addValue(wind.get(i).getPwrAtNom());
			this.windPwrAtTot.addValue(wind.get(i).pwrAt.getValue());
			this.windPwrAtResTot.addValue(wind.get(i).getPwrAtRes());
		}
		
		this.recalculateGensetPowers();
		
		// Aggregated Signals of Storage Units
		this.stgPwrAtChgMaxTot.setValue(0.0);
		this.stgPwrAtDisMaxTot.setValue(0.0);
		this.stgPwrAtTot.setValue(0.0);
		this.stgEgyTot.setValue(0.0);
		this.stgEgyCapTot.setValue(0.0);
		
		for(int i=0; i<this.stg.size(); i++)
		{
			this.stgPwrAtChgMaxTot.addValue(this.stg.get(i).getPwrAtChgMax());
			this.stgPwrAtDisMaxTot.addValue(this.stg.get(i).getPwrAtDisMax());
			this.stgPwrAtTot.addValue(this.stg.get(i).pwrAt.getValue());
			this.stgEgyTot.addValue(this.stg.get(i).egyStg.getValue());
			this.stgEgyCapTot.addValue(this.stg.get(i).egyStg.getValue());		
		}
		
		// Aggregated load is now only the load signal
			
	}

	
	/**
	 * 
	 */
	
	
	public void setLdPwrAtTot(double load)
	{
		this.ldPwrAtTot.setValue(load);
	}
	
	public Signal getGenPwrAtTotOnline()
	{
		return genPwrAtTotOnline;
	}

	public Signal getPvPwrAtMaxPc()
	{
		return this.pvPwrAtMaxPc;
	}
	
	public double getGenFuelRate()
	{
		double rate = 0.0;
		for (ConventionalGenerator i : this.genset)
		{
			rate += i.getStatusSignals().getElementById("FuelRate").getValue();
		}
		
		return rate;
	}



	public Signal getWindPwrAtMaxPc() {
		return this.pvPwrAtMaxPc; // At this point PV and Wind get the same setpoint
	}


	/**
	 *   
	 */
	@Override
	public void init(ComponentList<Equipment> eql, boolean log) {
		
		// We go through all equipment and add accordingly
		for(Equipment eq : eql.getElementList())
		{
			switch(eq.getEqClass())
			{
				case PV:
						if(eq.managed.isTrue())
						{
							DistributedEnergyResource der = (DistributedEnergyResource)eq;
							this.pv.add(der);
							if(der.isControllable())
							{
								ControlledDER cd = (ControlledDER)der;
								cd.setPwrAtMaxPc(this.pvPwrAtMaxPc);
							}
							Log.print(Lvl.INFO, "PV added, ID: " + eq.getId());
						}
						break;
						
				case WIND:
						if(eq.managed.isTrue())
						{
							DistributedEnergyResource der = (DistributedEnergyResource)eq;
							this.wind.add(der);
							if(der.isControllable())
							{
								ControlledDER cd = (ControlledDER)der;
								cd.setPwrAtMaxPc(this.wtPwrAtMaxPc);
							}
							Log.print(Lvl.INFO, "WIND added, ID: " + eq.getId());
						}
						break;
						
				case STOR:
						if(eq.managed.isTrue())
						{
							Storage stg = (Storage)eq;
							this.stg.add(stg);
							stg.setPwrAtSetp(this.stgPwrAtSetpTot); // TODO: If more than one battery implement a distributor
							
							Log.print(Lvl.INFO, "BATTERY added, ID: " + eq.getId());
						}
						break;
						
				case GEN:
						//if(eq.managed.isTrue()) // In Holbox Version 1 all gensets are managed, even is not explicitly marked
						{
							this.genset.add((ConventionalGenerator)eq);
							Log.print(Lvl.INFO, "GENSET added, ID: " + eq.getId());
						}
						break;
						
				case LOAD:
						break;
				
				default:
					Log.print(Lvl.WARN, "Could not assign equipment ID: " + eq.getId() + " since it is of type: " + eq.getEqClass() );
			}
		}
		
		if(log)
		{
			this.logSignals();
		}
		
		// Docking signals
		this.outputSignals.add(this.ldEgyTot);
		
		// Signal limiting
		this.pvPwrAtMaxPc.setInputSaturation(0.0, 100.0);
		this.wtPwrAtMaxPc.setInputSaturation(0.0, 100.0);
		
	}

	@Override
	public SignalList getInputSignals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SignalList getOutputSignals() {
		// TODO Auto-generated method stub
		return this.outputSignals;
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

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PowerSystemComponentSettings getAssimilatedObject() {
		SimplePowerManagement result = new SimplePowerManagement();
		result.assimilate(this);
		return result;
	}

	@Override
	public void initMap() {
		
		this.parList = new ParameterList();
		this.parList.add(genResPwrAtPc);
		this.parList.add(confidenceValue);
		
	}
	
	public SimplePowerManagement()
	{
		this.initMap();
	}
	
	public SimplePowerManagement(PowerFlowFactory pf)
	{
		this.initMap();
		this.pff = pf;
	}

	@Override
	public void setPowerFlowModel(PowerFlowFactory pf) {
		this.pff = pf;
	}
	
	public void recalculateGensetPowers()
	{
		//This is a small conceptual bug. If we get the values from the Slack bus we are only getting the previous calculation
		//Thus we will calculate based on the current load and add only the losses from previous calulcation... 
		//double gensetLoad = this.pff.getSlackBus().getPwrAtGen()*1000.0*Sys.baseMVA;
		double gensetLoad = this.ldPwrAtTot.getValue() + this.pff.getPwrAtLoss(); // There is no way yet to know the losses...
		
		gensetLoad = gensetLoad - this.pvPwrAtTot.getValue() - this.windPwrAtTot.getValue() - this.stgPwrAtTot.getValue();
		
		this.ldSlack.setValue(gensetLoad);
		double gensetOnlinePwrAtNom = this.genPwrAtNomTot.getValue();
		
		// Aggregated Signals of gensets
		this.genPwrAtNomTot.setValue(0.0);
		this.genPwrAtTot.setValue(0.0);
		this.genPwrAtMin.setValue(0.0);
		
		for (ConventionalGenerator g : genset)
		{
			
			// Recompute states
			g.recompute();
			
			this.genPwrAtNomTot.addValue(g.pwrAtNom.getVal()*g.online.getValue());
			this.genPwrAtTot.addValue(g.pwrAt.getValue());
			this.genPwrAtMin.addValue(0.01*g.pwrAtNom.getVal()*g.pwrAtMinPc.getVal()*g.isOnline());
		}
		
		
		
		if(gensetLoad < 0)
		{
			gensetLoad=0;
			this.error = true;
			//Log.print(Lvl.WARN, DataManager.getInputDataset().getTime() + ": Genset in reverse power!!!!"); // TODO: Misplaced log
		}
			
		
		
		for (ConventionalGenerator g : genset)
		{
			
			int online = (int)g.online.getValue();
			double pwrAtNom = (double) g.getParameters().getElementById("pwrAtNom").getVal();
			
			if(online == 1)
			{
				g.setPwrAtSch(gensetLoad * pwrAtNom / gensetOnlinePwrAtNom);
			}
		}	
	}
	
}
