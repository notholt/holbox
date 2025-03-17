package de.hsrt.holbox.Models;

import org.apache.commons.numbers.complex.*;

import de.hsrt.holbox.Models.Configuration.GridFrequencyModelSettings;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.ConfigurationException;
import de.hsrt.holbox.util.ParameterList;
import de.hsrt.holbox.util.Signal;
import de.hsrt.holbox.util.SignalList;




public class GridFrequencyModel extends FrequencyModel{ 
	
	private GridFrequencyModelSettings	psCfg;	
	
	private Signal freqRef; 	//f_0 - Input
	private Signal pwrAtLd;		//P_e - Input
	private Signal fSys;		// Output

	
	private double Ks_omega, Ks_p;
	private Complex n1 = Complex.ZERO;		// Zero 1
	private Complex p1 = Complex.ZERO;		// Pole 1
	private Complex p2 = Complex.ZERO;		// Pole 2
	
	
	private double omega_nom;
	private double Kp;						//Controller gain
	private double pwrAtNom;					// Nominal power
	private double Tm;						//Time constant
	private double Dsys;					// Systems damping
	private double Hsys;					//System inertia
	
	
	
	private double x_1_omega = 0;
	private double y_1_omega = 0;
	private double y_2_omega = 0;
	
	private double x_1_p = 0;
	private double y_1_p = 0;
	private double y_2_p = 0;

	
	//private double t = 0; 
	
	private double Kz_omega, Kz_p, a1_omega, a0_omega, a1_p, a0_p, b2, b1, b0;

	public GridFrequencyModel()
	{
		super();
		this.name = "SimpleGridFreqControl";
		initMap();
	}
	
	public GridFrequencyModel(	GridFrequencyModelSettings config,
										Double pwrAtNom,
										Signal pwrAt,
										Signal freqRef,
										Signal fSys) throws ConfigurationException
	{
		
		// Configuring System
		this.psCfg = config;
		// Transferring configuration to internal variables
		omega_nom 	= psCfg.freqBase.getVal()*2*Math.PI;
				
		if(psCfg.droop.getVal()<=0.0){	// Checks if no parameter Errors 
			throw new ConfigurationException("ERROR: Droop cannot be zero if frequency evaluation is necessary");
		}else {
			Kp			= 1/(omega_nom*(psCfg.droop.getVal()*0.01));
		}
		
		if(pwrAtNom != null) // If constructor does not declare a nominal power, we use base power
		{
			this.pwrAtNom = pwrAtNom;
		}else
		{
			this.pwrAtNom = psCfg.sysPwrApBase.getVal();
		}
		
		this.Tm = psCfg.tm.getVal();
		
		this.Dsys = psCfg.dSys.getVal();
		
		this.Hsys = psCfg.hSys.getVal();
		
		
		// We now calculate the poles and zeroes of the system in the continuous domain
		
		double a = 2*Hsys*Tm; 
		double b = 2*Hsys + Dsys*Tm;
		double c = Kp + Dsys;
		
		Ks_omega = (Kp/c);
		Ks_p	 = 1/c;
		
		Complex det = Complex.ZERO.add(b*b).subtract(4*a*c).sqrt(); // Determinant
		
		n1 = n1.add(-1/Tm);  // Calculate Zero
		
		p1 = p1.add(-b).add(det).divide(2*a);
		p2 = p2.add(-b).subtract(det).divide(2*a);
		
				
		
		// Routing Signals
		
		this.freqRef = freqRef;
		this.pwrAtLd = pwrAt;
		this.fSys = fSys;
		
		computeFactors(/*Ts*/0.1); // For Debugging	
		
	}
	
	public void reset()
	{
		x_1_omega = 0;
		y_1_omega = 0;
		y_2_omega = 0;
		x_1_p = 0;
		y_1_p = 0;
		y_2_p = 0;
	}
	
	private void computeFactors(double Ts)
	{
		Complex p1z = p1.multiply(Ts).exp();
		Complex p2z = p2.multiply(Ts).exp();
		//double pi = 0;
		//if(p1.getImaginary()*Ts < 1)
		//{
		//	pi= Math.exp(p1.getImaginary()*Ts);
		//}
		
		//Factors for P
		a1_p = 1;
		a0_p = n1.multiply(Ts).exp().getReal();
		
		b2 = 1;
		b1 = -Complex.ZERO.add(p1z).add(p2z).getReal();
		b0 = Complex.ONE.multiply(p1).multiply(p2).getReal();
		
		Kz_p = (Ks_p*(b2+b1+b0))/(a1_p + a0_p);
		
		// Factors for Omega0
		a1_omega = 0;
		a0_omega = 1;
		Kz_omega = (Ks_omega*(b2+b1+b0))/(a1_omega + a0_omega);
	}
	
	public void computeStep(double Ts)
	{

		double x_p = this.pwrAtLd.getValue()/this.pwrAtNom;
		double x_omega = 2*Math.PI*this.freqRef.getValue() - omega_nom;
		
		computeFactors(Ts);
		
		double y_p = (Kz_p * (a1_p*x_p + a0_p*x_1_p)) - b1*y_1_p - b0*y_2_p;
		x_1_p = x_p;
		y_2_p = y_1_p;
		y_1_p = y_p;
		
		double y_omega = (Kz_omega * (a1_omega*x_omega + a0_omega*x_1_omega)) - b1*y_1_omega - b0*y_2_omega;
		x_1_omega = x_omega;
		y_2_omega = y_1_omega;
		y_1_omega = y_omega;
		
		double freqAbs = (this.omega_nom + y_p /*+ y_omega*/)/(2*Math.PI);
		
		System.out.printf("Kp: %f, Kz, %f, a1: %f, a0: %f, b2: %f, b1: %f,b0: %f\n", Kp, Kz_p, a1_p, a0_p, b2, b1, b0 );
		
		this.fSys.setValue(freqAbs);
	}

	@Override
	public void recompute() {
		computeStep(0.001);	
	}

	@Override
	public ParameterList getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SignalList getInputSignals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SignalList getOutputSignals() {
		// TODO Auto-generated method stub
		return null;
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
		GridFrequencyModel result = new GridFrequencyModel();
		result.assimilate(this);
		return result;
	}

	@Override
	public void initMap() {
		
		this.parList = new ParameterList();
		
		// No parameters to add
		
	}
	

	
}
