package de.hsrt.holbox.Economics;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.*;


public class EconomicSettings extends Settings {

	//TODO: EconomicSettings has a big mismatch...
	// The Economic Setting Class has more Parameters than the ones used to calculate LCOE because other calculation Methods
	// of LCOE will be introduced in the development of the Project so it makes sense to include more parameters   
	// Project data structure   
	// CC Capital cost
    //@XmlElement
    private DoubleParameter		CCEquipmentWT; // [$/kW] capital cost  
    //@XmlElement
    private DoubleParameter		CCEquipmentDG; // [$/kW]
    //@XmlElement
    private DoubleParameter		CCEquipmentPV;	// [$/kW]
    @XmlElement
    private DoubleParameter		InitialInvestmentBAT;	// [$/kW]
    @XmlElement
    private DoubleParameter		AnnualReplacementCostBat;	// [$]
    @XmlElement
    private DoubleParameter		InitialInvestmentPowerequipmentBAT;	// [$/kW]  
    @XmlElement
    private DoubleParameter		OperationMaintenanceCostWT;	// Percent of the Capital cost between 0 and 100
    @XmlElement
    private DoubleParameter		OperationMaintenanceCosPV; // Percent of the Capital cost between 0 and 100	 
    @XmlElement
    private DoubleParameter		OperationCostDG; // [$/L] price/Liter
    @XmlElement
    private DoubleParameter		MaintenanceCostDG; // $ price

    @XmlElement
    private DoubleParameter		OperationCostBAT; // $/kWh price
    @XmlElement
    private DoubleParameter		MaintenanceCostBAT; // $/kW-year price

    @XmlElement
    private DoubleParameter		DiscountRateWT; // % from 0 to 1 (0 to 100%)
    @XmlElement
    private DoubleParameter		DiscountRatePV; // % from 0 to 1 (0 to 100%)
    @XmlElement
    private DoubleParameter		DiscountRateDG; // % from 0 to 1 (0 to 100%)
    @XmlElement
    private DoubleParameter		DiscountRateBAT; // % from 0 to 1 (0 to 100%)
    @XmlElement
    private DoubleParameter		UsfulLifeTimeWT; // years
    @XmlElement
    private DoubleParameter		UsfulLifeTimePV; // years
    @XmlElement
    private DoubleParameter		UsfulLifeTimeDG; // years
    @XmlElement
    private DoubleParameter		UsfulLifeTimeBAT; // years
    //@XmlElement
	private DoubleParameter carboncost;
    //@XmlElement
	private DoubleParameter carbonemissionfactor;
   
    
	public EconomicSettings()
	{
		
		// 	public IntegerParameter(String varName, String name, String unit, Integer def, Integer min, Integer max) 
		
		// Capital Investments
		this.CCEquipmentWT 			= new DoubleParameter("CCEquWT",	"Specific CAPEX WT",	"$/kW", 1784.0, 0.0, 100000.0);
		this.CCEquipmentDG 			= new DoubleParameter("CCEquDG",	"Specific CAPEX DG",	"$/kW", 333.0, 0.0, 100000.0);
		this.CCEquipmentPV 			= new DoubleParameter("CCEquPV",	"Specific CAPEX PV",	"$/kW", 990.0, 0.0,100000.0);
		this.InitialInvestmentBAT 	= new DoubleParameter("InitBAT",	"Specific CAPEX BAT",	"$/kWh", /*225.0*/300.0, 0.0, 100000.0);
		this.InitialInvestmentPowerequipmentBAT = new DoubleParameter("InInvPowEqBAT",	"Specific CAPEX BAT Inverter",	"$/kW", /*190.0*/200.0, 0.0, 1000000000.0);
		
		// Operation and Maintenance costs
		this.OperationMaintenanceCostWT = new DoubleParameter("OpMainWT",	"Annual WT OPEX as a proportion of CAPEX",	"p.u.", 0.03, 0.0, 100.0); // this is a % of the equipment cost in $ //1.5% to 3% of the original turbine cost
		this.OperationMaintenanceCosPV = new DoubleParameter("OpMainPV",	"Annual PV OPEX as a proportion of CAPEX",	"p.u", 0.01, 0.0, 100.0); //(default value = 0.5% per year) 10$/year
		this.OperationCostBAT = new DoubleParameter("OpBAT",				"OPEX BAT as a proportion of capacity",		"$/kWh", 0.0004, 0.0, 100000.0); // with most in the $6–14/kW-yr range for 2 MW Battery 10.000$		
		this.MaintenanceCostBAT = new DoubleParameter("MainBAT",			"Maintenance Cost BAT as a proportion of capacity",	"$/kWh", 9.8,0.0,100000.0); // with most in the $6–14/kWh-yr range for 2 MW Battery 10.000$		
		this.AnnualReplacementCostBat 	= new DoubleParameter("AnRepBat",	"Annual Replacement Cost Battery",	"$", 0.0, 0.0, 1000000000.0);
		this.OperationCostDG = new DoubleParameter("OpDG",					"Fuel price",						"$/L", /*1.421*/1.4,0.0,10000.0); // we assume 1,5 $/liter
		this.MaintenanceCostDG = new DoubleParameter("MaintenDG",			"OPEX DG as a proportion of installed power",	"$/kW", 1.0,0.0,1000000.0); //0.02$/Kwh  $6–14/kW-yr range  for a 2 MW diesel Generator between 12000 $ and 20000 $ 
		// there is a bug if i set value to 20000 there is a problem !!!
		
		this.carboncost = new DoubleParameter("COCost",					"Carbon costs",			"$/tonn CO2", 90.0,0.0,10000.0); // 90$/ton https://ember-climate.org/data/data-tools/carbon-price-viewer/
		this.carbonemissionfactor = new DoubleParameter("COemission",	"Carbon Emission Factor",	"tonn CO2 / KWh of DG", 0.00127,0.0,10000.0); //1,27 kg/kwh   ---> paper 	https://ir.unimas.my/id/eprint/16308/1/Estimation.pdf

		// CRF: discount rates and useful lifetime [years]
		this.DiscountRateWT = new DoubleParameter("DisRtWT",		"Discount Rate WT",		"p.u.", 0.06,0.0,100.0); // 7,5 % pro Jahr	https://www.wind-energy-the-facts.org/the-cost-of-energy-generated-by-wind-power-7.html
		this.DiscountRatePV = new DoubleParameter("DisRtPV",		"Discount Rate PV",		"p.u.", 0.06,0.0,100.0); // 	5,5 % in Europe	 https://www.linkedin.com/pulse/what-appropriate-discount-rates-valuing-renewable-energy-freyman-1e
		this.DiscountRateDG = new DoubleParameter("DisRtDG",		"Discount Rate DG",		"p.u.", 0.06,0.0,100.0); // 10% https://www.sciencedirect.com/science/article/pii/S0360544222014323
		this.DiscountRateBAT = new DoubleParameter("DisRtBAT",		"Discount Rate BAT",	"p.u.", 0.06,0.0,100.0); //	10%		 http://umu.diva-portal.org/smash/get/diva2:1801895/FULLTEXT01.pdf
		this.UsfulLifeTimeWT = new DoubleParameter("UseLfTWT",		"Useful Lifetime WT",	"years", 20.0,0.0,1000.0); 		// 15 years
		this.UsfulLifeTimePV = new DoubleParameter("UseLfTPV",		"Useful Lifetime PV",	"years", 25.0,0.0,1000.0); // 24 years
		this.UsfulLifeTimeDG = new DoubleParameter("UseLfTDG",		"Useful Lifetime DG",	"years", 10.0,0.0,1000.0); // 20 years
		this.UsfulLifeTimeBAT = new DoubleParameter("UseLfTBAT",	"Useful Lifetime BAT",	"years", 10.0,0.0,1000.0); // 9 years
		
		this.initMap();
	}
	

	public void initMap()
	{
		this.parList= new ParameterList();
		this.parList.add(this.CCEquipmentWT);
		this.parList.add(this.CCEquipmentDG);
		this.parList.add(this.CCEquipmentPV);
		this.parList.add(this.InitialInvestmentBAT);
		this.parList.add(this.AnnualReplacementCostBat);
		this.parList.add(this.InitialInvestmentPowerequipmentBAT);
		this.parList.add(this.OperationMaintenanceCostWT);
		this.parList.add(this.OperationMaintenanceCosPV);
		this.parList.add(this.MaintenanceCostBAT);
		this.parList.add(this.OperationCostBAT);
		this.parList.add(this.OperationCostDG);
		this.parList.add(this.MaintenanceCostDG);
		this.parList.add(this.carboncost);
		this.parList.add(this.carbonemissionfactor);
		this.parList.add(this.DiscountRateWT);
		this.parList.add(this.DiscountRatePV);
		this.parList.add(this.DiscountRateDG);
		this.parList.add(this.DiscountRateBAT);
		this.parList.add(this.UsfulLifeTimeWT);
		this.parList.add(this.UsfulLifeTimePV);
		this.parList.add(this.UsfulLifeTimeDG);
		this.parList.add(this.UsfulLifeTimeBAT);
	}
	

	//Setters and getters
	public DoubleParameter getCCEquipmentWT() {
		return CCEquipmentWT;
	}
	
	public void setCCEquipmentWT(DoubleParameter cCEquipmentWT) {
		CCEquipmentWT = cCEquipmentWT;
	}

	public DoubleParameter getCCEquipmentDG() {
		return CCEquipmentDG;
	}

	public void setCCEquipmentDG(DoubleParameter cCEquipmentDG) {
		CCEquipmentDG = cCEquipmentDG;
	}

	public DoubleParameter getCCEquipmentPV() {
		return CCEquipmentPV;
	}

	public void setCCEquipmentPV(DoubleParameter cCEquipmentPV) {
		CCEquipmentPV = cCEquipmentPV;
	}

	public DoubleParameter getInitialInvestmentBAT() {
		return InitialInvestmentBAT;
	}

	public void setInitialInvestmentBAT(DoubleParameter initialInvestmentBAT) {
		InitialInvestmentBAT = initialInvestmentBAT;
	}

	public DoubleParameter getAnnualReplacementCostBat() {
		return AnnualReplacementCostBat;
	}

	public void setAnnualReplacementCostBat(DoubleParameter annualReplacementCostBat) {
		AnnualReplacementCostBat = annualReplacementCostBat;
	}

	public DoubleParameter getInitialInvestmentPowerequipmentBAT() {
		return InitialInvestmentPowerequipmentBAT;
	}

	public void setInitialInvestmentPowerequipmentBAT(DoubleParameter initialInvestmentPowerequipmentBAT) {
		InitialInvestmentPowerequipmentBAT = initialInvestmentPowerequipmentBAT;
	}


	public DoubleParameter getOperationMaintenanceCostWT() {
		return OperationMaintenanceCostWT;
	}

	public void setOperationMaintenanceCostWT(DoubleParameter operationMaintenanceCostWT) {
		OperationMaintenanceCostWT = operationMaintenanceCostWT;
	}

	public DoubleParameter getOperationMaintenanceCosPV() {
		return OperationMaintenanceCosPV;
	}

	public void setOperationMaintenanceCosPV(DoubleParameter operationMaintenanceCosPV) {
		OperationMaintenanceCosPV = operationMaintenanceCosPV;
	}

	public DoubleParameter getOperationCostDG() {
		return OperationCostDG;
	}

	public void setOperationCostDG(DoubleParameter operationCostDG) {
		OperationCostDG = operationCostDG;
	}

	public DoubleParameter getMaintenanceCostDG() {
		return MaintenanceCostDG;
	}

	public void setMaintenanceCostDG(DoubleParameter maintenanceCostDG) {
		MaintenanceCostDG = maintenanceCostDG;
	}

	public DoubleParameter getOperationCostBAT() {
		return OperationCostBAT;
	}

	public void setOperationCostBAT(DoubleParameter operationCostBAT) {
		OperationCostBAT = operationCostBAT;
	}

	public DoubleParameter getMaintenanceCostBAT() {
		return MaintenanceCostBAT;
	}

	public void setMaintenanceCostBAT(DoubleParameter maintenanceCostBAT) {
		MaintenanceCostBAT = maintenanceCostBAT;
	}

	public DoubleParameter getDiscountRateWT() {
		return DiscountRateWT;
	}

	public void setDiscountRateWT(DoubleParameter discountRateWT) {
		DiscountRateWT = discountRateWT;
	}

	public DoubleParameter getDiscountRatePV() {
		return DiscountRatePV;
	}

	public void setDiscountRatePV(DoubleParameter discountRatePV) {
		DiscountRatePV = discountRatePV;
	}

	public DoubleParameter getDiscountRateDG() {
		return DiscountRateDG;
	}

	public void setDiscountRateDG(DoubleParameter discountRateDG) {
		DiscountRateDG = discountRateDG;
	}

	public DoubleParameter getDiscountRateBAT() {
		return DiscountRateBAT;
	}

	public void setDiscountRateBAT(DoubleParameter discountRateBAT) {
		DiscountRateBAT = discountRateBAT;
	}

	public DoubleParameter getUsfulLifeTimeWT() {
		return UsfulLifeTimeWT;
	}

	public void setUsfulLifeTimeWT(DoubleParameter usfulLifeTimeWT) {
		UsfulLifeTimeWT = usfulLifeTimeWT;
	}

	public DoubleParameter getUsfulLifeTimePV() {
		return UsfulLifeTimePV;
	}

	public void setUsfulLifeTimePV(DoubleParameter usfulLifeTimePV) {
		UsfulLifeTimePV = usfulLifeTimePV;
	}

	public DoubleParameter getUsfulLifeTimeDG() {
		return UsfulLifeTimeDG;
	}

	public void setUsfulLifeTimeDG(DoubleParameter usfulLifeTimeDG) {
		UsfulLifeTimeDG = usfulLifeTimeDG;
	}

	public DoubleParameter getUsfulLifeTimeBAT() {
		return UsfulLifeTimeBAT;
	}

	public void setUsfulLifeTimeBAT(DoubleParameter usfulLifeTimeBAT) {
		UsfulLifeTimeBAT = usfulLifeTimeBAT;
	}

	public DoubleParameter getCarboncost() {
		return carboncost;
	}

	public void setCarboncost(DoubleParameter carboncost) {
		this.carboncost = carboncost;
	}

	public DoubleParameter getCarbonemissionfactor() {
		return carbonemissionfactor;
	}

	public void setCarbonemissionfactor(DoubleParameter carbonemissionfactor) {
		this.carbonemissionfactor = carbonemissionfactor;
	}

	@Override
	public String toString() {
		return "EconomicSettings [CCEquipmentWT=" + CCEquipmentWT + ", CCEquipmentDG=" + CCEquipmentDG
				+ ", CCEquipmentPV=" + CCEquipmentPV + ", InitialInvestmentBAT=" + InitialInvestmentBAT
				+ ", AnnualReplacementCostBat=" + AnnualReplacementCostBat + ", InitialInvestmentPowerequipmentBAT="
				+ InitialInvestmentPowerequipmentBAT + ", OperationMaintenanceCostWT=" + OperationMaintenanceCostWT
				+ ", OperationMaintenanceCosPV=" + OperationMaintenanceCosPV + ", OperationCostDG=" + OperationCostDG
				+ ", MaintenanceCostDG=" + MaintenanceCostDG + ", OperationCostBAT=" + OperationCostBAT
				+ ", MaintenanceCostBAT=" + MaintenanceCostBAT + ", DiscountRateWT=" + DiscountRateWT
				+ ", DiscountRatePV=" + DiscountRatePV + ", DiscountRateDG=" + DiscountRateDG + ", DiscountRateBAT="
				+ DiscountRateBAT + ", UsfulLifeTimeWT=" + UsfulLifeTimeWT + ", UsfulLifeTimePV=" + UsfulLifeTimePV
				+ ", UsfulLifeTimeDG=" + UsfulLifeTimeDG + ", UsfulLifeTimeBAT=" + UsfulLifeTimeBAT + ", carboncost="
				+ carboncost + ", carbonemissionfactor=" + carbonemissionfactor + "]";
	}

	
}
