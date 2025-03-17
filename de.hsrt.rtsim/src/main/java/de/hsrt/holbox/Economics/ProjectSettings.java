package de.hsrt.holbox.Economics;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.*;

public class ProjectSettings extends Settings {

	// Project data structure   
    @XmlElement
	private
     IntegerParameter		CCEquipmentWT; // [$/kW] capital cost  
    @XmlElement
     IntegerParameter		CCEquipmentDG; // [$/kW]
    @XmlElement
     IntegerParameter		CCEquipmentPV;	// [$/kW]
    @XmlElement
     IntegerParameter		InitialInvestmentBAT;	// [$/kW]
    @XmlElement
     IntegerParameter		AnnualReplacementCost;	// [$]
    @XmlElement
     IntegerParameter		InitialInvestmentPowerequipmentBAT;	// [$/kW]  
    @XmlElement
     IntegerParameter		OperationMaintenanceCostWT;	// Percent of the Capital cost between 0 and 100
    @XmlElement
     IntegerParameter		OperationMaintenanceCosPV; // Percent of the Capital cost between 0 and 100	 
    @XmlElement
     IntegerParameter		OperationCostDG; // [$/L] price/Liter
    @XmlElement
     IntegerParameter		MaintenanceCostDG; // $ price

    @XmlElement
     IntegerParameter		OperationMaintenanceCostBAT; // $ price
    @XmlElement
     IntegerParameter		DiscountRateWT; // % from 0 to 1 (0 to 100%)
    @XmlElement
     IntegerParameter		DiscountRatePV; // % from 0 to 1 (0 to 100%)
    @XmlElement
     IntegerParameter		DiscountRateDG; // % from 0 to 1 (0 to 100%)
    @XmlElement
     IntegerParameter		DiscountRateBAT; // % from 0 to 1 (0 to 100%)
    @XmlElement
     IntegerParameter		UsfulLifeTimeWT; // years
    @XmlElement
     IntegerParameter		UsfulLifeTimePV; // years
    @XmlElement
     IntegerParameter		UsfulLifeTimeDG; // years
    @XmlElement
     IntegerParameter		UsfulLifeTimeBAT; // years
    @XmlElement
     DoubleParameter carboncost;
    @XmlElement
	 DoubleParameter carbonemissionfactor;

    
    
	public ProjectSettings()
	{		
		// 	public IntegerParameter(String varName, String name, String unit, Integer def, Integer min, Integer max) 

		this.CCEquipmentWT 			= new IntegerParameter("CCEquWT","CCEquipmentWT","[$/kW]", 0,0,100000);
		this.CCEquipmentDG 			= new IntegerParameter("CCEquDG","CCEquipmentDG","[$/kW]", 0,0,100000);
		this.CCEquipmentPV 			= new IntegerParameter("CCEquPV","CCEquipmentPV","[$/kW]", 0,0,100000);
		this.InitialInvestmentBAT 	= new IntegerParameter("InitBAT","InitialInvestmentBAT","[$/kW]", 0,0,100000);
		this.AnnualReplacementCost 	= new IntegerParameter("AnRep","AnnualReplacementCost","[$]", 0,0,1000000000);
		this.InitialInvestmentPowerequipmentBAT = new IntegerParameter("InInvPowEqBAT","InitialInvestmentPowerequipmentBAT","[$/kW]", 0,0,1000000000);
		this.OperationMaintenanceCostWT = new IntegerParameter("OpMainWT","OperationMaintenanceCostWT","%", 0,0,100);
		this.OperationMaintenanceCosPV = new IntegerParameter("OpMainPV","OperationMaintenanceCosPV","%", 0,0,100);
		this.OperationMaintenanceCostBAT = new IntegerParameter("OpMainBAT","OperationMaintenanceCostBAT","[$]", 0,0,100000);		
		this.OperationCostDG = new IntegerParameter("OpDG","OperationCostDG","[$/L]", 0,0,10000);
		this.MaintenanceCostDG = new IntegerParameter("MaintenDG","MaintenanceCostDG","[$]", 0,0,10000);
		this.DiscountRateWT = new IntegerParameter("DisRtWT","DiscountRateWT","%", 0,0,100);
		this.DiscountRatePV = new IntegerParameter("DisRtPV","DiscountRatePV","%", 0,0,100);
		this.DiscountRateDG = new IntegerParameter("DisRtWT","DiscountRateDG","%", 0,0,100);
		this.DiscountRateBAT = new IntegerParameter("DisRtBAT","DiscountRateBAT","%", 0,0,100);
		this.UsfulLifeTimeWT = new IntegerParameter("UseLfTWT","UsfulLifeTimeWT","Years", 0,0,1000);
		this.UsfulLifeTimePV = new IntegerParameter("UseLfTPV","UsfulLifeTimePV","Years", 0,0,1000);
		this.UsfulLifeTimeDG = new IntegerParameter("UseLfTDG","UsfulLifeTimeDG","Years", 0,0,1000);
		this.UsfulLifeTimeBAT = new IntegerParameter("UseLfTBAT","UsfulLifeTimeBAT","Years", 0,0,1000);
		this.carboncost = new DoubleParameter("COCost","carboncost","Euro/tonCO2", 0.0,0.0,1000.0);
		this.carbonemissionfactor= new DoubleParameter("COCost","carboncost","Euro/tonCO2", 0.0,0.0,1000.0); //Operational  Costs of the Diesel Generator

		
// depreciation costs is already in the lcoe 
		// 
		
		this.initMap();
	}
	
	
	public void initMap()
	{
		this.parList				= new ParameterList();
		this.parList.add(this.CCEquipmentWT);
		this.parList.add(this.CCEquipmentDG);
		this.parList.add(this.CCEquipmentPV);
		this.parList.add(this.InitialInvestmentBAT);
		this.parList.add(this.AnnualReplacementCost);
		this.parList.add(this.InitialInvestmentPowerequipmentBAT);
		this.parList.add(this.OperationMaintenanceCostWT);
		this.parList.add(this.OperationMaintenanceCosPV);
		this.parList.add(this.OperationMaintenanceCostBAT);
		this.parList.add(this.OperationCostDG);
		this.parList.add(this.MaintenanceCostDG);
		this.parList.add(this.DiscountRateWT);
		this.parList.add(this.DiscountRateWT);
		this.parList.add(this.DiscountRatePV);
		this.parList.add(this.DiscountRateDG);
		this.parList.add(this.DiscountRateBAT);
		this.parList.add(this.UsfulLifeTimeWT);
		this.parList.add(this.UsfulLifeTimePV);
		this.parList.add(this.InitialInvestmentBAT);
		this.parList.add(this.UsfulLifeTimeDG);
		this.parList.add(this.UsfulLifeTimeBAT);
		this.parList.add(this.carboncost);
		this.parList.add(this.carbonemissionfactor);

	}


	
}
