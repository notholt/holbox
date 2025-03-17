package de.hsrt.holbox.playground;

import org.apache.commons.numbers.complex.*;



public class SimpleTransferFunction {
	private double freqRef; 	//f_0
	private double pwrAtLd;		//P_l
	//private double freq;		//f
	
	//private double J;		// Moment of inertia
	//private double Tm;		// Time constant
	//private double Kp;		// Controller gain
	
	private Complex n1 = Complex.ZERO;		// Zero 1
	private Complex p1 = Complex.ZERO;		// Pole 1
	private Complex p2 = Complex.ZERO;		// Pole 2
	private double Ks = 0;					//Static gain
	
	private double x_1 = 0;
	private double y_1 = 0;
	private double y_2 = 0;
	
	//private double t = 0; 
	
	private double Kz, a1, a0, b2, b1, b0;

	public SimpleTransferFunction(double Pnom, double J, double Tm, double Kp, double f_nom, double Ts)
	{
		//this.Tm = Tm;
		//this.J = J;
		//this.Kp = Kp;
		
		if(Tm > 0 && J > 0 && f_nom > 0)
		{
			double a = 2*Math.PI*J*Tm*f_nom*(2*Math.PI); 
			double b = 2*Math.PI*J*f_nom*(2*Math.PI);
			double c = Pnom*Kp;
			Ks = 1/c;
			Complex det = Complex.ZERO.add(b*b).subtract(4*a*c).sqrt();
			n1 = n1.add(-1/Tm);  // Calculate Zero
			p1 = p1.add(-b).add(det).divide(2*a);
			p2 = p2.add(-b).subtract(det).divide(2*a);
			this.freqRef = f_nom;
		}
		
		computeFactors(Ts);
		
		
	}
	
	public void reset()
	{
		x_1 = 0;
		y_1 = 0;
		y_2 = 0;
	}
	
	private void computeFactors(double Ts)
	{
		double pr = Math.exp(p1.multiply(Ts).getReal());
		//double pi = 0;
		if(p1.getImaginary()*Ts < 1)
		{
			//pi= Math.exp(p1.getImaginary()*Ts);
		}
		a1 = 1;
		a0 = -Math.exp(n1.multiply(Ts).getReal());
		b2 = 1;
		b1 = -2*pr;
		b0 = Math.pow(pr, 2)*1.3;// + Math.pow(pi, 2);
		Kz = (Ks*(b2+b1+b0))/(a1 + a0);
	}
	
	public double computeStep(double x)
	{
		//double x = pwrAtLd;
		double y = (Kz * (a1*x + a0*x_1)) - b1*y_1 - b0*y_2;
		x_1 = x;
		y_2 = y_1;
		y_1 = y;
		
		return y + this.freqRef;
	}
	
	
	
}
