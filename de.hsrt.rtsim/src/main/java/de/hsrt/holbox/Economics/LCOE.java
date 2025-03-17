package de.hsrt.holbox.Economics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;


import de.hsrt.holbox.ScenarioManagement.*;
import de.hsrt.holbox.ScenarioManagement.ScenarioFactory;



/** 
 * 
 * 
 * 
 * 
 * 
 * 
 */

import de.hsrt.holbox.util.*;

public class LCOE  {
	// This class calculates the LCOE and determines the optimum LCOE for a given max number of Batteries and PV Modules
	// The Calculation can be 
	
	protected double result1 ;
	protected int  nbrPVOpt;
	protected int  nbrBATOpt;
	protected CellProperty [][] myMatrix;

	private EconomicSettings economicSettings;
	private ScenarioFactory sf;
	


	/**
	 * 
	 * 
	 * 
	 * This class calculates the  Total Laverage Cost of Energy
	 * The calculation takes as input 
	 * 
	 * 
	 * 
	 */
	
	
	
	//MatrixElement [][] myMatrix = new MatrixElement [][]
	
	
	
	/** @
	 * 
	 * @param economicSettings
	 * @param nwt								Number of Wind Turbines
	 * @param npv								Number of  photovoltaik modules
	 * @param ndg								Number of  Diesel Generators
	 * @param nbat								Number of  Battery modules	
	 * @param pwt								Rated Power of Wind Turbines
	 * @param ppv								Rated Power of a PV Module
	 * @param pdg								Rated Power of a a Diesel Generator
	 * @param producedenergywt					Produced electricity Wind Turbine
	 * @param producedenergypv					Produced electricity PV
	 * @param producedenergydg					Produced electricity Diesel Generator
	 * @param dischargeenergybatterie			discharge electricity / energy Battery 
	 * @param egyTOT								Total electricity produced [kWh]
	 * @param concemptiondg						Fuel Concemption of the Diesel Generator [l]	
	 * @param storcap							storage capacity to install [kWh]
	 * @param max_charg_discharg				Maximum charge and discharge
	 * @param InitialInvestmentBAT				Initial investment (213 is a value we can use) [$/kWh]
	 * @param InitialInvestmentPowerequipmentBAT InitialInvestmentPowerequipmentBAT [$/kW]
	 * @param  Q  								Maximum charge/discharge rate [%/h] (8% is a typical number)
	 * 
	 * @param OperationMaintenanceCostBAT		Operation cost (example Value is 0.0004) [ $/kWh]
	 * @return 
	 * 
	 */
	
	// 
	
	
	public LCOE(EconomicSettings economicSettings,ScenarioFactory sf) {
		super();
		this.economicSettings = economicSettings;
		this.sf = sf;
		
	}


	protected double crf (double discountRate, double usefulLifeTime) {
		double result;
		result = (discountRate*Math.pow((1+discountRate),(usefulLifeTime)) )/(Math.pow((1+discountRate),(usefulLifeTime))-1);
		return result;
	}
	
	protected double tlccWt (EconomicSettings economicSettings,int nbrWT,double pwrWT) {
		double result;
//		DoubleParameter investment=economicSettings.getCCEquipmentWT();
		double cceequiWt = (double)economicSettings.getParameters().getElementById("CCEquWT").getVal();
		double omcWt = (double)economicSettings.getParameters().getElementById("OpMainWT").getVal();
		double discountrateWT = (double)economicSettings.getParameters().getElementById("DisRtWT").getVal();
		double usefulLifeTimewt=(double)economicSettings.getParameters().getElementById("UseLfTWT").getVal();//Useful Lifetime Windturbine
		double crf = crf(discountrateWT,usefulLifeTimewt);
		result = (nbrWT*pwrWT*cceequiWt*(1+omcWt/crf));// Laverage Cost of Energy for The Photovoltaic

		return result;
	}
	
	protected double tlccPV (EconomicSettings economicSettings,int nbrPV,double pwrPV) {
		double result;
//		DoubleParameter investment=economicSettings.getCCEquipmentWT();
		double cceequiPV = (double)economicSettings.getParameters().getElementById("CCEquPV").getVal();
		double omcPV = (double)economicSettings.getParameters().getElementById("OpMainPV").getVal();
		double discountratePV = (double)economicSettings.getParameters().getElementById("DisRtPV").getVal();
		double usefulLifeTimePV=(double)economicSettings.getParameters().getElementById("UseLfTPV").getVal();//Useful Lifetime Windturbine
		double crfPV = crf(discountratePV,usefulLifeTimePV);
		result = (nbrPV*(pwrPV)*cceequiPV*(1+omcPV/crfPV));// Laverage Cost of Energy for The Photovoltaic
// the rated power of PV is on W and the equip cost is in kW/h
		return result;
	}
	
	
	protected double tlccDG (EconomicSettings economicSettings,int nbrdg,double pwrdg,double concemptiondg,double ndgh) {
		double result;
//		DoubleParameter investment=economicSettings.getCCEquipmentWT();
		double cceequiDG = (double)economicSettings.getParameters().getElementById("CCEquDG").getVal();
		double discountrateDG = (double)economicSettings.getParameters().getElementById("DisRtDG").getVal();
		double usefulLifeTimeDG=(double)economicSettings.getParameters().getElementById("UseLfTDG").getVal();//Useful Lifetime Windturbine
		double crfDG = crf(discountrateDG,usefulLifeTimeDG);
		double maintenanceCostDG= (double)economicSettings.getParameters().getElementById("MaintenDG").getVal();//Maintnance Costs of the Diesel Generator
		double ocdg= (double)economicSettings.getParameters().getElementById("OpDG").getVal(); //Operational  Costs of the Diesel Generator
		result = nbrdg*(pwrdg)*cceequiDG+(ndgh*maintenanceCostDG+ocdg*concemptiondg)/crfDG;// Laverage Cost of Energy for The Photovoltaic
		// the rated power of DG is on W and the equip cost is in kW/h so need to divide through 100 factor

		return result;
	}

	
	protected double tlccBAT (EconomicSettings economicSettings,int nbrBAT,double storCap,double fMax,double dischEng) {
		double result;
//		DoubleParameter investment=economicSettings.getCCEquipmentWT();
		double initInvestBAT=(double)economicSettings.getParameters().getElementById("InitBAT").getVal();//Initial investmentcost Batterie 
		double initInvestPwrBAT=  (double)economicSettings.getParameters().getElementById("InInvPowEqBAT").getVal();
		double discountrateBAT = (double)economicSettings.getParameters().getElementById("DisRtBAT").getVal();
		double usefulLifeTimeBAT=(double)economicSettings.getParameters().getElementById("UseLfTBAT").getVal();//Useful Lifetime Battery
		double crfBAT = crf(discountrateBAT,usefulLifeTimeBAT);
		double maintenanceCostBAT= (double)economicSettings.getParameters().getElementById("MainBAT").getVal();//Maintnance Costs of the Diesel Generator
		double ocBAT= (double)economicSettings.getParameters().getElementById("OpBAT").getVal(); //Operational  Costs of the Diesel Generator
		double repCosBAT= (double)economicSettings.getParameters().getElementById("AnRepBat").getVal(); //Operational  Costs of the Diesel Generator
		result =nbrBAT* storCap*(initInvestBAT+initInvestPwrBAT*fMax)+(ocBAT*dischEng/crfBAT)+(maintenanceCostBAT*fMax*storCap/crfBAT)+(repCosBAT*storCap/crfBAT);// Laverage Cost of Energy for The Battery
		return result;
	}

	
//	double tlccpv = (npv*ppv*cceequipv*(1+omcpv/crfpv));// Laverage Cost of Energy for The Photovoltaic

	
	//public   void runLCOE(EconomicSettings economicSettings,int nwt,int npv,int ndg, int nbat,double pwt,double ppv,double pdg,double producedenergywt,double producedenergypv,double producedenergydg,double dischargeenergybatterie,double E_TOT,double concemptiondg,double storcap,double max_charg_discharg, int ndgh )
	public   void testRunLCOE()
	{
		
		// it is a function that validates the LCOE for a given Parameters it has no other use 
		double getTLCCbat=tlccBAT(economicSettings,10,558.0,0.08,19000.0);	
			
		double getTLCCDG=tlccDG(economicSettings,5,17.6,20000.0,16000);	
		double getTLCCPV=tlccPV(economicSettings,646,0.25);	
		double getTLCCWT=tlccWt(economicSettings,2,20.0);	
		double TLCC_total = getTLCCbat+getTLCCDG+getTLCCPV+getTLCCWT;
		double LCOE = (TLCC_total/332000.0)*crf(0.06,20);

		//double getTLCCbat=tlccBAT(economicSettings,558.0,0.08,19000.0);	

		
		//double crfWT = crf(0.06, 20);
		System.out.printf("TLCC_bat %.3f\nTLCC_DG %.3f  \nTLCC_PV %.3f \nTLCC_WT %.3f \nTLCC_total %.3f \nLCOE %.3f\n"  , getTLCCbat  ,  getTLCCDG , getTLCCPV , getTLCCWT , TLCC_total , LCOE);

		
	}

	public   double calculateLCOE(int nbrPV,int nbrBAT)
	// This Method calculates LCOE using the same CRF for all the Components 
	//(Discount Rate = 6% and Useful Life Time = 20 Years)
	{
		
		Scenario s = null;
		try {
			 s = this.sf.getScenario();
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double storCap = 588.0;
		try {
			storCap = sf.getScenario().getBattEgyNomTot();
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double maxChargDischarg = 0.08;
		try {
			maxChargDischarg = sf.getScenario().getBattPwrToEgyRatioAvg();//Apparently Marouan needs it as proportion and not as percent :-) *100.0;
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		double dischEng = s.getBattDisEgyTot();
		
		double concemptiondg=s.getGenFuelVolTot();
		
		double ndgh = s.getGenRunTm();
		
		double pwrPV = 0.25;
		try {
			pwrPV = sf.getScenario().getPvPwrAtNomTot();
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int nbrWT = 10;
		try {
			nbrWT = (int) sf.getScenario().getWtCount();
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		double pwrWT = 20.0;
		try {
			pwrWT = sf.getScenario().getWtPwrAtNomAvg();
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// for the WT nbr and power is the average power
		
		
		int nbrDG = 5;
		try {
			nbrDG = (int) sf.getScenario().getGenRunNb();
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double pwrDG = 100.0;
		try {
			pwrDG = sf.getScenario().getGenRunPwrAtAvg();
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double eCon = s.getLdEgyTot();
		
		double usefullifeTimeBat =  economicSettings.getUsfulLifeTimeBAT().getVal();
		double usefullifeTimeDG =  economicSettings.getUsfulLifeTimeDG().getVal();
		double usefullifeTimePV =  economicSettings.getUsfulLifeTimePV().getVal();
		double usefullifeTimeWT =  economicSettings.getUsfulLifeTimeWT().getVal();

		double discountRateBat = economicSettings.getDiscountRateBAT().getVal();
		double discountRateDG = economicSettings.getDiscountRateDG().getVal();
		double discountRatePV = economicSettings.getDiscountRatePV().getVal();
		double discountRateWT = economicSettings.getDiscountRateWT().getVal();

				
		double getTLCCbat=tlccBAT(economicSettings,nbrBAT,storCap,maxChargDischarg ,dischEng)*crf(discountRateBat,usefullifeTimeBat);		
		double getTLCCWT=tlccWt(economicSettings,nbrWT,pwrWT)*crf(discountRateWT,usefullifeTimeWT);
		double getTLCCPV=tlccPV(economicSettings,nbrPV,pwrPV)*crf(discountRatePV,usefullifeTimePV);	
		double getTLCCDG=tlccDG(economicSettings,nbrDG,pwrDG,concemptiondg,ndgh)*crf(discountRateDG,usefullifeTimeDG);	
		double tlccTotal = getTLCCbat+getTLCCDG+getTLCCPV+getTLCCWT;
		double lcoeTotal = (tlccTotal/eCon );
		
		//double getTLCCbat=tlccBAT(economicSettings,558.0,0.08,19000.0);	

		//double crfWT = crf(0.06, 20);
		return lcoeTotal;		
		
	}
	public  ArrayList <OutputElement> determineLCOEminMethodOne( int maxnbrPV,int maxnbrBAT ) 
	
// This Methods gives an array List of Type (OutputElement) with the Combination varying the number of PVs and Batteries 	
	
	{
		 ArrayList <OutputElement> result = new ArrayList <OutputElement>();
		// initialising the Nbr of DG
		
		int xMin = 0;
		int xMax = maxnbrPV;
		int xStep= 2;
		int yMin = 0;
		int yMax = maxnbrBAT;
		int yStep= 2;

		for (int x=xMin;x<xMax;x+=xStep) {
			
			for (int y =yMin;y<yMax;y+=yStep) {
				// set the scale of BAT and PV
//				sf.setPvScale(x);
//				sf.setBattScale(y + 0.00001);

				// initialize the scenario
//				try {
//					sf.init(false);
//					sf.getScenario().runScenario(false);
//					// run the scenario
//
//				} catch (ModelCodeException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				
				
				int nbrPV=x;//+1;
				int nbrBAT=y;//+1;
				sf.setPvScale(nbrPV);
				sf.setBattScale(nbrBAT+ 0.00001);
				sf.setWtScale(1.0);
				try {
				sf.getScenario().runScenario(false);
				sf.getScenario().printResults();
				// run the scenario

			} catch (ModelCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

				
				double currentLCOE= calculateLCOE(nbrPV,nbrBAT);
				Scenario s=null;
				try {
					s = sf.getScenario();
				} catch (ModelCodeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result.add(new OutputElement(nbrBAT,nbrPV, s.getLdEgyTot(), 
															s.getPvEgyTot(),
															s.getWtEgyTot(),
															s.getGenEgyTot(), 
															s.getGenFuelVolTot(),
															s.getGenRunTm(),
															s.getPvEgyTot()/s.getLdEgyTot(),
															0.0,
															s.getPossible(),
															currentLCOE
																		));
											} // end of the inner for loop
									}// end of the Out  for loop
		return result;
	}
// end of the Method determineLCOEminMethodOne
// This Method 



// Method findMinLcoeElement of the Type OutputElement finds the element of an ArrayList regarding the LCOE 
public  OutputElement findMinLcoeElement(ArrayList<OutputElement> elements) {
    return elements.stream()
    		.min(Comparator.comparingDouble(e -> e.lcoe))
    		.orElseThrow(NoSuchElementException::new);
}// end of the Method findMinLcoeElement





}
			

// 5.	Department of Energy and Climate Change (UK Government) electricity costs model
// LCOE estimate = NPV of Total Costs / NPV of Electricity generation 
// NPV of Total Costs = sum (total capex and apex costs/(1+discountrate)^n)
// NPV of Electricity Generation = sum (net electricity generation/(1+discount rate)^n)
// n- time Period 
// Capex costs = construction costs Capex includes = pre development costs, construction costs, infrastructure costs (adjust6ed for learning)
// Opex costs = O&M costs = fixed opex (adjusted for learning) variable opex insurance connection costs carbon transport and storage costs
// Heat revenues Fuel Price carbon costs
// other input parameters of the model capacity of the plant, expected availability, expected efficiency, expected load factor


// Source DECC,2013


//6. Bureau of Resources and Energy Economics (BREE) Australia Energy Technology Assessment (AETA) model


