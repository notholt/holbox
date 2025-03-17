package de.hsrt.holbox.Economics;

public class LCOEModelUS {
	
	private EconomicSettings economicSettings;
	
	private double LCOE_total_US;

/**
 * 
 * @param economicSettings
 * @param n_WT number of Wind Turbines
 * @param n_PV number of Photovoltaic modules
 * @param n_DG number of Diesel Generators
 * @param n_BAT number of Battery modules
 */

	public LCOEModelUS(EconomicSettings economicSettings,int n_WT,int n_PV,int n_DG, int n_BAT) {
		
		// capacity factor 
		// Heat Rate
		// Taxrate
		this.economicSettings = economicSettings;

		// needs to be added hier and in Economic Settings
		
		
		double DPV_WT=(double)this.economicSettings.getParameters().getElementById("DPV_WT").getVal(); // Tax Rate WT
		double TaxrateWt=(double)this.economicSettings.getParameters().getElementById("TaxrateWt").getVal(); // Tax Rate WT
		double CapitalcostWt=(double)this.economicSettings.getParameters().getElementById("CapCostWt").getVal(); // Capital cost Wind Turbine
		double fixedOMCostsWT=(double)this.economicSettings.getParameters().getElementById("FixOMCostWT").getVal(); // fixed Operating and Maintenance Costs Wind Turbine
		double variableOMCostsWT=(double)this.economicSettings.getParameters().getElementById("VarOMCostWT").getVal(); // Variable Operating and Maintenance Costs Wind Turbine
		double CapFacWT=(double)this.economicSettings.getParameters().getElementById("CapFacWT").getVal(); // Variable Operating and Maintenance Costs Wind Turbine
		double DiscountRateWT=(double)this.economicSettings.getParameters().getElementById("DisRtWT").getVal(); // Discount Rate Wind Turbine
		double UsfulLifeTimeWT=(double)this.economicSettings.getParameters().getElementById("UseLfTWT").getVal();//Useful Lifetime Windturbine
		//double CCEquipmentWT= (double)this.economicSettings.getParameters().getElementById("CCEquWT").getVal();//Equipment Cost Wind Turbine
		//double OperationMaintenanceCostWT= (double)this.economicSettings.getParameters().getElementById("OpMainWT").getVal();//Operational and Maintnance Costs of the Wind Turbine

		
		// needs to be added hier and in Economic Settings
		double DPV_PV=(double)this.economicSettings.getParameters().getElementById("DPV_PV").getVal(); // Tax Rate WT
		double TaxratePV=(double)this.economicSettings.getParameters().getElementById("TaxratePV").getVal(); // Tax Rate WT
		double CapitalcostPV=(double)this.economicSettings.getParameters().getElementById("CapCostPV").getVal(); // Capital cost PV
		double fixedOMCostsPV=(double)this.economicSettings.getParameters().getElementById("FixOMCostPV").getVal(); // fixed Operating and Maintenance Costs PV
		double variableOMCostsPV=(double)this.economicSettings.getParameters().getElementById("VarOMCostPV").getVal(); // Variable Operating and Maintenance Costs PV
		double CapFacPV=(double)this.economicSettings.getParameters().getElementById("CapFacPV").getVal(); // Variable Operating and Maintenance Costs Photovoltaic
		double DiscountRatePV=(double)this.economicSettings.getParameters().getElementById("DisRtPV").getVal();// Discount Rate Photovoltaaik
		double UsfulLifeTimePV=(double)this.economicSettings.getParameters().getElementById("UseLfTPV").getVal();//Useful Lifetime Photovoltaik
		//double CCEquipmentPV= (double)this.economicSettings.getParameters().getElementById("CCEquPV").getVal();//Equipment Cost Photovoltaik
		//double OperationMaintenanceCostPV= (double)this.economicSettings.getParameters().getElementById("OpMainPV").getVal();//Operational and Maintnance Costs of the Photovoltaik
		
		
		// needs to be added hier and in Economic Settings
		double DPV_DG=(double)this.economicSettings.getParameters().getElementById("DPV_DG").getVal(); // Tax Rate DG
		double TaxrateDG=(double)this.economicSettings.getParameters().getElementById("TaxrateDG").getVal(); // Tax Rate DG
		double CapitalcostDG=(double)this.economicSettings.getParameters().getElementById("CapCostDG").getVal(); // Capital cost Diesel Generator
		double fixedOMCostsDG=(double)this.economicSettings.getParameters().getElementById("FixOMCostDG").getVal(); // fixed Operating and Maintenance Costs Diesel Genertor 
		double variableOMCostsDG=(double)this.economicSettings.getParameters().getElementById("VarOMCostDG").getVal(); // Variable Operating and Maintenance Costs Diesel Genertor
		double CapFacDG=(double)this.economicSettings.getParameters().getElementById("CapFacPV").getVal(); // Variable Operating and Maintenance Costs Diesel Genertor
		double DiscountRateDG=(double)this.economicSettings.getParameters().getElementById("DisRtDG").getVal();// Discount Rate Diesel Generator
		double UsfulLifeTimeDG=(double)this.economicSettings.getParameters().getElementById("UseLfTDG").getVal();//Useful Lifetime Diesel Generator
		//double CCEquipmentDG= (double)this.economicSettings.getParameters().getElementById("CCEquDG").getVal();//Equipment Cost Diesel Generator
		//double MaintenanceCostDG= (double)this.economicSettings.getParameters().getElementById("MaintenDG").getVal();//Maintnance Costs of the Diesel Generator
		//double OperationCostDG= (double)this.economicSettings.getParameters().getElementById("OpDG").getVal(); //Operational  Costs of the Diesel Generator

		
		//The operational cost [$/L] of the DG is different than the maintennace cost the maintenace cost in $ per Year per DG 
	
		
		// needs to be added hier and in Economic Settings
		double DPV_BAT=(double)this.economicSettings.getParameters().getElementById("DPV_BAT").getVal(); // Tax Rate DG
		double TaxrateBAT=(double)this.economicSettings.getParameters().getElementById("TaxrateBAT").getVal(); // Tax Rate DG
		double CapitalcostBAT=(double)this.economicSettings.getParameters().getElementById("CapCostBAT").getVal(); // Capital cost Battery
		double fixedOMCostsBAT=(double)this.economicSettings.getParameters().getElementById("FixOMCostBAT").getVal(); // fixed Operating and Maintenance Costs Batterie 
		double variableOMCostsBAT=(double)this.economicSettings.getParameters().getElementById("VarOMCostBAT").getVal(); // Variable Operating and Maintenance Costs Batterie
		double CapFacBAT=(double)this.economicSettings.getParameters().getElementById("CapFacBAT").getVal(); // Variable Operating and Maintenance Costs Batterie
		double DiscountRateBAT=(double)this.economicSettings.getParameters().getElementById("DisRtBAT").getVal();// Discount Rate Battery
		double UsfulLifeTimeBAT=(double)this.economicSettings.getParameters().getElementById("UseLfTBAT").getVal();//Useful Lifetime Battery
		//double InitialInvestmentBAT=(double)this.economicSettings.getParameters().getElementById("InitBAT").getVal();//Initial investmentcost Batterie 
		//double InitialInvestmentPowerequipmentBAT=(double)this.economicSettings.getParameters().getElementById("InInvPowEqBAT").getVal();//Initial investmentcost power equipment Battery 
		//double OperationMaintenanceCostBAT=(double)this.economicSettings.getParameters().getElementById("OpMainBAT").getVal();		//Operational and Maintnance Costs of the Battery 
		
		
		double CRFWT = (DiscountRateWT*Math.pow((1+DiscountRateWT),(UsfulLifeTimeWT)) )/(Math.pow((1+DiscountRateWT),(UsfulLifeTimeWT))-1);// CRF Capital Recovery factor wind Turbine 
		double CRFPV = (DiscountRatePV*Math.pow((1+DiscountRatePV),(UsfulLifeTimePV)) )/(Math.pow((1+DiscountRatePV),(UsfulLifeTimePV))-1);// CRF Capital Recovery factor Photovoltaik 
		double CRFDG = (DiscountRatePV*Math.pow((1+DiscountRateDG),(UsfulLifeTimeDG)) )/(Math.pow((1+DiscountRateDG),(UsfulLifeTimeDG))-1);// CRF Capital Recovery factor Diesel Generator  
		double CRFBAT = (DiscountRateBAT*Math.pow((1+DiscountRateBAT),(UsfulLifeTimeBAT)) )/(Math.pow((1+DiscountRateBAT),(UsfulLifeTimeBAT))-1);// CRF Capital Recovery factor Battery
		
		
		double LCOE_WT = (n_WT*CapitalcostWt*CRFWT*(1-TaxrateWt*DPV_WT))/(8760*CapFacWT*(1-TaxrateWt))+fixedOMCostsWT/(8760*CapFacWT)+variableOMCostsWT;  // Laverage Cost of Energy for The Wind Turbine
		double LCOE_PV =(n_PV*CapitalcostPV*CRFPV*(1-TaxratePV*DPV_PV))/(8760*CapFacPV*(1-TaxratePV))+fixedOMCostsPV/(8760*CapFacPV)+variableOMCostsPV;// Laverage Cost of Energy for The Photovoltaic
		double LCOE_DG =(n_DG*CapitalcostDG*CRFDG*(1-TaxrateDG*DPV_DG))/(8760*CapFacDG*(1-TaxrateDG))+fixedOMCostsDG/(8760*CapFacDG)+variableOMCostsDG;// Laverage Cost of Energy for  Diesel Generator
		double LCOE_BAT =(n_BAT*CapitalcostBAT*CRFBAT*(1-TaxrateBAT*DPV_BAT))/(8760*CapFacBAT*(1-TaxrateBAT))+fixedOMCostsBAT/(8760*CapFacBAT)+variableOMCostsBAT;// Laverage Cost of Energy for  Battery

		//double LCOE_BAT = stor_Cap*(InitialInvestmentBAT+InitialInvestmentPowerequipmentBAT*max_charg_discharg)+(OperationMaintenanceCostBAT/CRFBAT);//Laverage Cost of Energy for The Battery
		LCOE_total_US = LCOE_DG+LCOE_WT+LCOE_PV+LCOE_BAT; // Total Laverage Cost of Energy 
//
	}
	
	public double LCOE_total_US() {
		return LCOE_total_US;
	}


}
















//3.	US LCOE model and Transparent Cost Data

//((cap cost*CRF*(1-TaxRate*Present value of depreciation(DPV)))/(8760*capacity Factor (1-T)))+(Fixed O&M/(8760*Capacity Factor)) + Variable O&M + Fuel Price*Heat Rate

//with T = Tax Rate 

//CRF = (D*(1+D)^n)/((1+D)^n)-1)

//Heat Rate = energy content of fuel used per kWh generated

// Present value of depreciation(DPV): for RES (excluding hydropower) - 0.83155; for combustion turbine and nuclear - 0.59476; for
// combined cycle, coal, and hydropower - 0.54407.	
// capacity factor : https://en.wikipedia.org/wiki/Capacity_factor
// The net capacity factor is the unitless ratio of actual electrical energy output over a 
//given period of time to the theoretical maximum electrical energy output over that period
// fixed O&M costs $Mwh
// variable O&M costs $/run-hour per MW of Pmax
