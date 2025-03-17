
package de.hsrt.holbox.Economics;

import java.util.*;

public class OutputElement {

	public int nbrBat;
	public int nbrPV;
	public double eLd;
	public double ePv;
	public double eWt;
	public double eDg;
	public double fuel;
	public double hours;
	public double sF;
	public double eCur;
	public double possible;
	public double lcoe;
	
	public OutputElement(int nbrBat, int nbrPV, double eLd, double ePv, double eWt, double eDg, double fuel, double hours, double sF, double eCur, double possible, double lcoe) {
		super();
		this.nbrBat = nbrBat;
		this.nbrPV = nbrPV;
		this.eLd = eLd;
		this.ePv = ePv;
		this.eWt = eWt;
		this.eDg = eDg;
		this.fuel = fuel;
		this.hours = hours;
		this.sF = sF;
		this.eCur = eCur;
		this.possible = possible;
		this.lcoe = lcoe;
	}
    public int getNbrBat() {
        return nbrBat;
    }

    public int getNbrPV() {
        return nbrPV;
    }

    public double getLcoe() {
        return lcoe;
    }


	public double getELd() {
		return eLd;
	}
	public double getEPv() {
		return ePv;
	}
	
	public double getEWt() {
		return eWt;
	}
	public double getEDg() {
		return eDg;
	}
	public double getFuel() {
		return fuel;
	}
	public double getHours() {
		return hours;
	}
	public double getSF() {
		return sF;
	}
	public double getECur() {
		return eCur;
	}
	public double getPossible() {
		return possible;
	}
	
	
	@Override
	public String toString() {
		return "OutputElement [nbrBat=" + nbrBat + ", nbrPV=" + nbrPV + ", lcoe=" + lcoe + "]";
	}
	
	

}
