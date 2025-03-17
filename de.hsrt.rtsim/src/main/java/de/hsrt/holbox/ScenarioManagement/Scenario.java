package de.hsrt.holbox.ScenarioManagement;

import java.time.Instant;
import java.util.ArrayList;

import de.hsrt.holbox.ClassicPowerFlow.Bus;
import de.hsrt.holbox.ClassicPowerFlow.PowerFlow;
import de.hsrt.holbox.Models.EqClass;
import de.hsrt.holbox.Models.Equipment;
import de.hsrt.holbox.Models.PowerManagement;
import de.hsrt.holbox.project.dataset.DataManager;
import de.hsrt.holbox.project.powersystem.*;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.Signal;


/**
 * A Scenario represents a set of settings which links the following
 * 
 * <ul>
 * 	<li>A power system configuration</li>
 * 	<li>A power management configuration</li>
 * 	<li>A post processing routine </li>
 * </ul>
 * 
 * @author notholt
 *
 */

public class Scenario {
	
	
	protected PowerManagement 		pm;
	
	protected PowerFlowFactory		pff;
	
	protected PowerFlow				pf;
	
	protected ComponentList<Equipment> 	equipment;
	
	protected ComponentList<BusSettings> busSettings;
	
	protected int possible = 1;
		
	public Scenario()
	{
	}
	
	public void init()
	{
	}
	
	public void resetScenario()
	{
		// We reset all equipment
		for(Equipment eq : this.equipment.getElementList())
		{
			eq.reset();
		}
		// We also reset power management
		this.pm.reset();
	}
	
	
	
	
	
	private void calculateEquipment(double Ta) throws ModelCodeException
	{
		this.pff.resetPowerSystemPowers();
		for(Equipment eq : this.equipment.getElementList())
		{
			eq.computeStep(Ta);
			eq.addPwrToAttachedBuses();
		}
	}
	
	private void recalculateEquipment() throws ModelCodeException
	{
		this.pff.resetPowerSystemPowers();
		for(Equipment eq : this.equipment.getElementList())
		{
			eq.recompute();
			eq.addPwrToAttachedBuses();
		}
	}
	
	
	/**
	 * This implementation of Scenario cycles through the input data
	 * @throws ModelCodeException 
	 */
	public void runScenario(boolean logIntermediate) throws ModelCodeException
	{
		
		this.possible = 1;
		this.resetScenario();
		this.pff.resetPowerSystemPowers();
		this.recalculateEquipment();
		this.pff.runPowerFlow();
		
		
		DataManager.getInputDataset().resetIndex();
		DataManager.getInputDataset().updateSignals();
		
		Instant t_1 = DataManager.getInputDataset().getTime();
		
		// Only if intermediate reults required
		if(logIntermediate)
		{
			for(Bus b : this.pff.getBuses())
			{
				DataManager.getIntermediateResultsDataset().addSignal(new Signal(b.getBusID()+":Vtg", "pu", 0.0));
				DataManager.getIntermediateResultsDataset().addSignal(new Signal(b.getBusID()+":Ang", "°", 0.0));
			}
		}
		//End intermediate results
		
		
		
		// Main Loop through all the input dataset
		for(int i=0; i<DataManager.getInputDataset().size(); i++)
		{
			DataManager.getInputDataset().setIndex(i);
			DataManager.getInputDataset().updateSignals();
			
			/*if(i == 14) 
			{
					boolean debug = true; 
			}*/
			
			this.pff.resetPowerSystemPowers();
			recalculateEquipment();
			//this.pff.runPowerFlow(); 	// Problem: Without power flow solution there is no genset generation.
										// TODO: Shall be optimized so that there is no necessity of computing 2 x power flow calc!!!
			
			double Ta = (DataManager.getInputDataset().getTime().toEpochMilli() - t_1.toEpochMilli())/1000.0;
			t_1 = DataManager.getInputDataset().getTime();
			
			if(pm!=null)
			{
				this.pm.computeStep(Ta);
			}
			
			this.pff.resetPowerSystemPowers();
			this.recalculateEquipment();
			this.pff.runPowerFlow();
			if(pm!=null)
			{
				this.pm.recalculateGensetPowers();
			}
			this.calculateEquipment(Ta);
			
			//this.pff.runPowerFlow();
		
			if(this.pm.hasError())
			{
				this.possible=0;
			}

			
			//this.pff.printResults();
			
			if (logIntermediate)
			{
				DataManager.logResults(t_1);
				
				// Only for intermediate results
				ArrayList<Float> data = new ArrayList<>(0);
				for(Bus b : this.pff.getBuses())
				{
					data.add((float)b.getVtg());
					data.add((float)b.getAng());
				}
				DataManager.getIntermediateResultsDataset().addData(t_1, data);
				// End intermediate results
			}
		}
		
	}
	
	public void setPM(PowerManagement pm)
	{
		this.pm = pm;
	}
	
	public double getPvEgyTot()
	{
		double egy = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.PV)
			{
				egy += eq.getStatusSignals().getElementById("egyTot").getValue(); 
			}
		}
		return egy;
	}
	
	
	
	
	/**
	 * Returns the installed PV power of the scenario
	 * @return
	 */
	public double getPvPwrAtNomTot()
	{
		double pwr = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.PV)
			{
				pwr += (double)eq.getParameters().getElementById("pwrAtNom").getVal();
			}
		}
		return pwr;
	}
	
	public double getWtEgyTot()
	{
		double egy = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.WIND)
			{
				egy += eq.getStatusSignals().getElementById("egyTot").getValue(); 
			}
		}
		return egy;
	}
	
	/**
	 * Returns the average installed WT power of the scenario
	 * @return
	 */
	public double getWtPwrAtNomAvg()
	{
		double pwr = 0.0;
		double n = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.WIND)
			{
				//pwr += (double)eq.getParameters().getElementById("pwrAtNom").getVal();
				pwr+=eq.getPwrAtNom();
				n++;
			}
		}
		
		if (n>0)
		{
			return pwr/n;
		}else
		{
			return 0.0;
		}
		
	}
	
	/**
	 * Returns the average installed WT power of the scenario
	 * @return
	 */
	public double getWtCount()
	{
		double n = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.WIND)
			{
				n++;
			}
		}
		
		return n;
		
	}
	
	public double getBattDisEgyTot()
	{
		double egy = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.STOR)
			{
				egy += eq.getStatusSignals().getElementById("egyDisBattTot").getValue(); 
			}
		}
		return egy;
	}
	
	/**
	 * Returns the battery capacity (not scaled) of the scenario
	 * @return
	 */
	public double getBattEgyNomTot()
	{
		double egy = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.STOR)
			{
				egy += (double)eq.getParameters().getElementById("stgEgyNom").getVal(); 
			}
		}
		return egy;
	}
	
	/**
	 * Returns the average power to energy ratio for the batteries in the scenario
	 * @return
	 */
	public double getBattPwrToEgyRatioAvg()
	{
		double egy = 0.0;
		double pwr = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.STOR)
			{
				egy += (double)eq.getParameters().getElementById("stgEgyNom").getVal();
				pwr += (double)eq.getParameters().getElementById("stgDisPwrAtNom").getVal();
			}
		}
		
		if(egy>0)
		{
			return pwr/egy;
		}else
		{
			return 0.0;
		}
	}
	
	public double getGenEgyTot()
	{
		double egy = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.GEN)
			{
				egy += eq.getStatusSignals().getElementById("GenEgyTot").getValue(); 
			}
		}
		return egy;
	}
	
	public double getGenRunTm()
	{
		double hours = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.GEN)
			{
				hours += eq.getStatusSignals().getElementById("GenRunTm").getValue(); 
			}
		}
		return hours;
	}
	
	public int getPossible()
	{
		return this.possible;
	}
	
	/**
	 * Returns the number of generators which actually ran
	 * @return
	 */
	public double getGenRunNb()
	{
		double n = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.GEN)
			{
				if (eq.getStatusSignals().getElementById("GenRunTm").getValue() > 0.0)
				{
					n++;
				}
			}
		}
		return n;
	}
	
	/**
	 * Returns the average power of generators which actually ran
	 * @return
	 */
	public double getGenRunPwrAtAvg()
	{
		double n = 0.0;
		double pwr = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.GEN)
			{
				if (eq.getStatusSignals().getElementById("GenRunTm").getValue() > 0.0)
				{
					n++;
					pwr += (double)eq.getParameters().getElementById("pwrAtNom").getVal();
				}
			}
		}
		if(n>0.0)
		{
			return pwr/n;
		}else
		{
			return 0.0;
		}
	}
	
	public double getGenFuelVolTot()
	{
		double egy = 0.0;
		for(Equipment eq : this.equipment.getElementList())
		{
			if(eq.getEqClass()==EqClass.GEN)
			{
				egy += eq.getStatusSignals().getElementById("GenVolTot").getValue(); 
			}
		}
		return egy;
	}
	
	public double getLdEgyTot()
	{
		return this.pm.getOutputSignals().getElementById("ldEgyTot").getValue();
	}
	
	public void printResults()
	{
		System.out.println(	"+--------------------------------------------------+------------------+");
		System.out.println(	"| Variable                                         |  Value           |");
		System.out.println(	"+--------------------------------------------------+------------------+");
		System.out.printf(	"| Total load energy consumption (incl. Sys Losses) |  %9.2f   MWh |\n", this.getLdEgyTot()/1000.0);
		System.out.printf(	"| Total PV energy production                       |  %9.2f   MWh |\n", this.getPvEgyTot()/1000.0);
		System.out.printf(	"| Total Wind energy production                     |  %9.2f   MWh |\n", this.getWtEgyTot()/1000.0);
		System.out.printf(	"| Total Battery energy discharge                   |  %9.2f   MWh |\n", this.getBattDisEgyTot()/1000.0);
		System.out.printf(	"| Total Genset energy production                   |  %9.2f   MWh |\n", this.getGenEgyTot()/1000.0);
		System.out.printf(	"| Total Genset consumption                         |  %9.2f   m³  |\n", this.getGenFuelVolTot()/1000.0);
		System.out.printf(	"| Total Genset running hours                       |  %9.0f   h   |\n", this.getGenRunTm());
		System.out.println(	"+--------------------------------------------------+------------------+");
		System.out.printf(	"| Average nominal power of WT                      |  %9.0f   kW  |\n", this.getWtPwrAtNomAvg());
		System.out.printf(	"| Number of Wind turbines                          |  %9.0f       |\n", this.getWtCount());
		System.out.printf(	"| Average nominal power of PV                      |  %9.0f   kW  |\n", this.getPvPwrAtNomTot());
		System.out.printf(	"| Average nominal power of Gensets                 |  %9.0f   kW  |\n", this.getGenRunPwrAtAvg());
		System.out.printf(	"| Number of DG running                             |  %9.0f       |\n", this.getGenRunNb());
		System.out.printf(	"| Battery Total Capacity                           |  %9.0f   kWh |\n", this.getBattEgyNomTot());
		System.out.printf(	"| Battery Power to Energy Ratio                    |  %9.0f   %%   |\n", this.getBattPwrToEgyRatioAvg()*100.0);
		System.out.println(	"+--------------------------------------------------+------------------+");

	}
	
	public PowerFlowFactory getPowerFlowFactory()
	{
		return this.pff;
	}

}
