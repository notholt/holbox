package de.hsrt.holbox.Economics;



//	
/**
 * 
 * @author benelhen
 * 
 * This class defines a matrix with elements that contains :1. Nr. PV, 2. Nr. Bat, 3. Nr DG, 4. Nr.WT, 4. concemption DG  
	5. powerflow (Boolean) powerflow a boolean to check the feasability of the combination of PV, DG, WT  and Batteries 
 	6. LCOE is the calculated LCOE for this combination
 *
 */

public class CellProperty  {

	// 
	
	protected int nbrPV;			//Number of the PV Modules
	protected int nbrDG;			//Number of the Diesel Generators
	protected int nbrBAT;		//Number of the Batteries
	protected int nbrWT;			//Number of the Wind Turbines

	
	//Powers
	protected double consDG;		// Concemption of the DG [l]
	protected boolean powerFlow;	// boolean if the combination works for the powerflow
	
	
	// Energy (electricity production)
	protected double egyWT;		// electricity production of the WT [kWh]
	protected double egyPV;	// electricity production of the PV [kWh]
	protected double egyDG;		// electricity production of the DG [kWh]
	protected double egyBAT;	// electricity production of the Battery [kWh]
	protected double egyTOT;	// Total electricity production  [kWh]

	
	// Economic
	
	protected double lcoe;		// LCOE [$]

	
	//CONSTRUCTOR


	public CellProperty(int nbrPV, int nbrDG, int nbrBAT, int nbrWT, double consDG, boolean powerFlow, double egyWT,
			double egyPV, double egyDG, double egyBAT, double egyTOT, double lcoe) {
		super();
		this.nbrPV = nbrPV;
		this.nbrDG = nbrDG;
		this.nbrBAT = nbrBAT;
		this.nbrWT = nbrWT;
		this.consDG = consDG;
		this.powerFlow = powerFlow;
		this.egyWT = egyWT;
		this.egyPV = egyPV;
		this.egyDG = egyDG;
		this.egyBAT = egyBAT;
		this.egyTOT = egyTOT;
		this.lcoe = lcoe;
	}
	//end of constructor


	@Override
	public String toString() {
		return "MatrixElement [nbrPV=" + nbrPV + ", nbrDG=" + nbrDG + ", nbrBAT=" + nbrBAT + ", nbrWT=" + nbrWT
				+ ", consDG=" + consDG + ", powerFlow=" + powerFlow + ", egyWT=" + egyWT + ", egyPV=" + egyPV
				+ ", egyDG=" + egyDG + ", egyBAT=" + egyBAT + ", egyTOT=" + egyTOT + ", lcoe=" + lcoe + "]";
	}

	
	
	
}
