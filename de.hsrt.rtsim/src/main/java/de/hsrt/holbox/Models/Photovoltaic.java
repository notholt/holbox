package de.hsrt.holbox.Models;

import de.hsrt.holbox.util.ModelCodeException;


public abstract class Photovoltaic extends Equipment {
	
	//protected double pvPwrAtNom = 1; //kW
	//protected double pwrApNom = 1; //kVA

	public Photovoltaic(String id, double pwrApNom, double pwrAtNom) throws ModelCodeException 
	{
		super(id, pwrApNom, pwrAtNom);
	}
	
	public void setPeakPower(double pvPwrAtNom) throws ModelCodeException
	{
		this.pwrAtNom.setVal(pvPwrAtNom);
		this.pwrApNom.setVal(pvPwrAtNom); // TODO: Clean implementation, please
	}
	
	public void setPwrApNom(double pwrApNom)
	{
		this.pwrApNom.setVal(pwrApNom);
	}
	
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void recompute() {
		// TODO Auto-generated method stub

	}

	@Override
	public void computeStep(double Ts) {
		// TODO Auto-generated method stub

	}
	
	public void initMap()
	{
		super.initMap();
		
	}

}
