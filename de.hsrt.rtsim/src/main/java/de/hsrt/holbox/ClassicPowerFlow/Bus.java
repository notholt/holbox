package de.hsrt.holbox.ClassicPowerFlow;

import org.apache.commons.numbers.complex.Complex;

import de.hsrt.holbox.util.DoubleParameter;

//Class for bus data: P,Q, V, delta for each bus (node)

//import org.apache.commons.numbers.complex.*;

public class Bus {
	
	/**
	 * 
	 */
	protected String busID;			//ID of the bus
	protected int busNo;			//Number of the bus. The slack bus is 0
	protected BusMode busType;		// Slack, PQ, PV - it is proper to use constants from enum
	
	//Powers
	protected double pwrAtGen;		// Pgen, active power generated [per units]
	protected double pwrRtGen;		// Qgen, reactive power generated [per units]
	protected double pwrAtDem;		// Pload, active power demand [per units]
	protected double pwrRtDem;		// Qload, reactive power demand [per units]
	
		//Scheduled
	protected double pwrAtSch;		//Scheduled active power [per units]
	protected double pwrRtSch;		//Scheduled reactive power [per units]
	 

	//Voltage
	protected double vtg;			// RMS Voltage [per units] - magnitude of the complex value vtgM*exp(j*ang)
	protected double ang;			//angle of the voltage [rad]
	protected double vtgMgnRef;		//reference voltage magnitude for the bus, applicable only for PV buses [per units]
	protected DoubleParameter vtgBase; 	//Base voltage, for future use. Will have units for the 'Base Voltage' specified, thus the type of this parameter
	
	protected boolean hasShuntB;	// indication of whether the bus has shunt succeptance
	protected double shuntB;		//shunt succeptance [per unit]
	
	//CONSTRUCTOR

	public Bus(String busID, int busNo, BusMode busType, double pwrAtGen, double pwrRtGen, double pwrAtDem,
			double pwrRtDem, double vtg, double ang, double refVtg) {
		super();
		this.busID = busID;
		this.busNo = busNo;
		this.busType = busType;
		this.pwrAtGen = pwrAtGen;
		this.pwrRtGen = pwrRtGen;
		this.pwrAtDem = pwrAtDem;
		this.pwrRtDem = pwrRtDem;
		this.vtg = vtg;
		this.ang = ang;
		this.vtgMgnRef = refVtg;
	} //end of constructor
	
	
	/**
	 * Contstructor with shunt succeptance
	 * @param busID
	 * @param busNo
	 * @param busType
	 * @param pwrAtGen
	 * @param pwrRtGen
	 * @param pwrAtDem
	 * @param pwrRtDem
	 * @param vtg
	 * @param ang
	 * @param vtgMgnRef
	 * @param hasShuntB
	 * @param shuntB
	 */
	public Bus(String busID, int busNo, BusMode busType, double pwrAtGen, double pwrRtGen, double pwrAtDem,
			double pwrRtDem, double vtg, double ang, double vtgMgnRef, boolean hasShuntB, double shuntB) {
		super();
		this.busID = busID;
		this.busNo = busNo;
		this.busType = busType;
		this.pwrAtGen = pwrAtGen;
		this.pwrRtGen = pwrRtGen;
		this.pwrAtDem = pwrAtDem;
		this.pwrRtDem = pwrRtDem;
		this.vtg = vtg;
		this.ang = ang;
		this.vtgMgnRef = vtgMgnRef;
		this.hasShuntB = hasShuntB;
		this.shuntB = shuntB;
	}
	

	
						//Business

	protected void updateVtg (Complex vtgEst) {		// Updating the voltage attributes of the bus according to calculated complex voltage
		double vtgMgn = vtgEst.abs();				// Voltage magnitude
		double vtgAng = vtgEst.arg();				// Voltage angle
		double corrGain; 							// correction gain (or coefficient) - for correcting the voltage magnitude (for PV buses only)
		
		if (busType == BusMode.PV_BUS) {
			corrGain = vtgMgnRef/vtgMgn; 
		} else {
			corrGain = 1;
		}
		
		vtg = vtgMgn*corrGain;
		ang = vtgAng;
		
		
	} // end of method

	protected void updateQsch (double freq) {
		//updating demand and scheduled reactive power for the buses with static capacitors
		
		double Bf = shuntB * freq / Sys.freqNom;					//shunt succeptance, corrected for frequency
		double Qcap = 1 * Math.pow(vtg, 2) * Bf;
		pwrRtSch = pwrRtGen - (pwrRtDem - Qcap); 					//updating the scheduled reactive power
	}


	
	@Override
	public String toString() {
		return "Bus [busID=" + busID + ", busNo=" + busNo + ", busType=" + busType + ", pwrAtGen=" + pwrAtGen
				+ ", pwrRtGen=" + pwrRtGen + ", pwrAtDem=" + pwrAtDem + ", pwrRtDem=" + pwrRtDem + ", vtg=" + vtg
				+ ", ang=" + ang + "]";
	} //toString


	public String getBusID() {
		return busID;
	}

	public void setBusID(String busID) {
		this.busID = busID;
	}

	public int getBusNo() {
		return busNo;
	}

	public void setBusNo(int busNo) {
		this.busNo = busNo;
	}

	public BusMode getBusType() {
		return busType;
	}

	public void setBusType(BusMode busType) {
		this.busType = busType;
	}

	public double getPwrAtGen() {
		return pwrAtGen;
	}

	public void setPwrAtGen(double pwrAtGen) {
		this.pwrAtGen = pwrAtGen;
	}

	public double getPwrRtGen() {
		return pwrRtGen;
	}

	public void setPwrRtGen(double pwrRtGen) {
		this.pwrRtGen = pwrRtGen;
	}

	public double getPwrAtDem() {
		return pwrAtDem;
	}

	public void setPwrAtDem(double pwrAtDem) {
		this.pwrAtDem = pwrAtDem;
	}

	public double getPwrRtDem() {
		return pwrRtDem;
	}

	public void setPwrRtDem(double pwrRtDem) {
		this.pwrRtDem = pwrRtDem;
	}

	public double getPwrAtSch() {
		return pwrAtSch;
	}

	public void setPwrAtSch(double pwrAtSch) {
		this.pwrAtSch = pwrAtSch;
	}

	public double getPwrRtSch() {
		return pwrRtSch;
	}

	public void setPwrRtSch(double pwrRtSch) {
		this.pwrRtSch = pwrRtSch;
	}

	public double getVtg() {
		return vtg;
	}

	public void setVtg(double vtg) {
		this.vtg = vtg;
	}

	public double getAng() {
		return ang;
	}

	public void setAng(double ang) {
		this.ang = ang;
	}
	
	public double getBaseVtg()
	{
		return this.vtgBase.getVal();
	}

} // end of class 'Bus'
