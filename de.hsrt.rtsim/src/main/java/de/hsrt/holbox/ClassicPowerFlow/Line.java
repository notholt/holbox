package de.hsrt.holbox.ClassicPowerFlow;

import org.apache.commons.numbers.complex.*;	//Apache common numbers	

/**
 * The class represents the transmission line in the electric power grid
 * 
 * @author Alexander Balakhontsev
 *
 */
public class Line {

	//A pattern class for generating ONE single line. To rename to LinePU? (its in per-units)
	
		protected String lineID;		//
		
		//The line is constructed using the data of R,X, Y05 provided at NOMINAL frequency.
		//G and B are the fields being updated during the power flow calculations according to the ACTUAl frequency
		
		protected double lineG;			//active admittance in per units at ACTUAL frequency. Is calculated from R,X,f (see method lineGB below)
		protected double lineB;			//reactive admittance in per units at ACTUAL frequency. Is calculated from R,X,f (see method lineGB below)
		protected double lineR;			//line's Ohmic resistance in per units. 
		protected double lineX;			//line's reactance in per units at NOMINAL FREQUENCY. 
		protected double lineY05;		//half of the shunt admittance (charging) in per units at nominal frequency
		protected double lineChar;		//Line charging - reactive power in per units. Reserved but not used so far
		
		protected int busNo1, busNo2;	//Numbers (or should there be IDs?) of the buses to which the line is connected to
		protected boolean lineIs;		//Just in case indicator whether line between the node exists. true/false
		
		protected Complex lineCur;		//current in the line [per unit], to be calculated
		protected double lineLossP;		//active losses in the line [per unit], to be calculated
		protected double lineLossQ;		//losses of reactive power in the line [per unit], to be calculated
		protected double lineLossY05;	//Charging losses in the line [per unit], to be calculated
		
		protected boolean hasTransformer;	//indicates whether there is a transformer in the line
		protected double turnsRatio;		//final turns ratio, high voltage to low voltage; 	//Important! refers to magnitude correction. Should be the value e.g. 0.985; it doesn't refer to transformation of the base voltage
		protected boolean tapAtLowerIndex;	//if true - the tap-changing side is buNo1, opposite otherwise
		
		
				//Constructor with R,X - no transformer
		
		/**
		 * @param lineID
		 * @param lineR
		 * @param lineX
		 * @param lineY05
		 * @param lineChar
		 * @param busNo1
		 * @param busNo2
		 * @param lineIs
		 * @param lineCur
		 * @param lineLossP
		 * @param lineLossQ
		 */
		public Line(String lineID, double lineR, double lineX, double lineY05, double lineChar, int busNo1,
				int busNo2, boolean lineIs) {
			super();
			this.lineID = lineID;
			this.lineR = lineR;
			this.lineX = lineX;
			this.lineY05 = lineY05;
			this.lineChar = lineChar;
			this.busNo1 = busNo1;
			this.busNo2 = busNo2;
			this.lineIs = lineIs;
		}
		
		
			//Constructor with R,X and a transformer
		
			/**
			 * @param lineID 	Line ID
			 * @param lineR 	Line's ohmic resistance [per units]
			 * @param lineX		Line's reactive impedance [per units]
			 * @param lineY05	Line's half of shunt succeptance [per units]
			 * @param lineChar	Line's total charging [per units]
			 * @param busNo1	Tap-bus (tap-changing) bus number
			 * @param busNo2	Z-tap number
			 * @param lineIs	if line exists
			 * @param hasTransformer	if the Line has a transformer
			 * @param turnsRatio		turns Ratio
			 */
			public Line(String lineID, double lineR, double lineX, double lineY05, double lineChar, int busNo1,
					int busNo2, boolean lineIs, boolean hasTransformer, double turnsRatio, boolean tapAtLowerIndex) {
				super();
				this.lineID = lineID;
				this.lineR = lineR;
				this.lineX = lineX;
				this.lineY05 = lineY05;
				this.lineChar = lineChar;
				this.busNo1 = busNo1;
				this.busNo2 = busNo2;
				this.lineIs = lineIs;
				this.hasTransformer = hasTransformer;
				this.turnsRatio = turnsRatio;
				this.tapAtLowerIndex = tapAtLowerIndex;
			}
		



											//Business
						
		public void lineGB (double freq, double fnom) {
			//method re-calculates both G and B attributes depending on actual frequency
			
			double Xfreq = this.lineX * freq/fnom;					//re-calculating reactance for the given frequency
			Complex Z = Complex.ofCartesian(lineR, Xfreq);
			Complex Y = Complex.ONE.divide(Z);
			
			this.lineG = Y.getReal();
			this.lineB = Y.getImaginary();
			
			
		}
		
		
	


		public void myCur(Bus bus1, Bus bus2) {
			// Calculates current in the given line
			// Re-think: maybe, the method doesnt need any input arguments, sine the line 'knows' the buses it is attached to 
			Complex V1, V2, deltaV;
			V1 = Complex.ofPolar(bus1.vtg, bus1.ang);
			V2 = Complex.ofPolar(bus2.vtg, bus2.ang);
			deltaV = Complex.ZERO.add(V1).subtract(V2);
			Complex Y = Complex.ofCartesian(lineG, lineB); //Good that G and B already depend on the actual frequency:)
			
			lineCur = Complex.ONE.multiply(deltaV).multiply(Y);
		}
		
		
		public void lossP() {
			// Active losses in the line
			double Irms = lineCur.abs();
			Complex Y = Complex.ofCartesian(lineG, lineB);
			Complex S;												//apparent power
			S = Complex.ONE.multiply(Irms*Irms).divide(Y);
			lineLossP = S.getReal();
		}
		
		public void lossQ() {
			// Reactive losses in the line
			double Irms = lineCur.abs();
			Complex Y = Complex.ofCartesian(lineG, lineB);
			Complex S;												//apparent power
			S = Complex.ONE.multiply(Irms*Irms).divide(Y);
			lineLossQ = S.getImaginary();
		} 
		
		public void lossY05 (Bus bus1, Bus bus2, double freq) {
			//The charging losses in the line for the given value of frequency
			
			double V1rms = bus1.vtg;
			double V2rms = bus2.vtg;
			double Y05freq;
			double Q1, Q2, Qresult;
			
			Y05freq = this.lineY05 * freq / Sys.freqNom;	//Calculating the value of the half shunt admittance for actual frequency
			
			Q1 = V1rms * V1rms * Y05freq;
			Q2 = V2rms * V2rms * Y05freq;
			Qresult = Q1 + Q2;
			
			lineLossY05 = -1 * Qresult;
		}
		
											//Getters and setters
		public String getLineID() {
			return lineID;
		}


		public void setLineID(String lineID) {
			this.lineID = lineID;
		}


		public double getLineG() {
			return lineG;
		}


		public void setLineG(double lineG) {
			this.lineG = lineG;
		}


		public double getLineB() {
			return lineB;
		}


		public void setLineB(double lineB) {
			this.lineB = lineB;
		}


		public double getLineY05() {
			return lineY05;
		}


		public void setLineY05(double lineY05) {
			this.lineY05 = lineY05;
		}


		public double getLineChar() {
			return lineChar;
		}


		public void setLineChar(double lineChar) {
			this.lineChar = lineChar;
		}


		public int getBusNo1() {
			return busNo1;
		}


		public void setBusNo1(int busNo1) {
			this.busNo1 = busNo1;
		}


		public int getBusNo2() {
			return busNo2;
		}


		public void setBusNo2(int busNo2) {
			this.busNo2 = busNo2;
		}


		public boolean isLineIs() {
			return lineIs;
		}


		public void setLineIs(boolean lineIs) {
			this.lineIs = lineIs;
		}

		public Complex getLineCur()
		{
			return this.lineCur;
		}
														//Service

		@Override
		public String toString() {
			return "Line [lineID=" + lineID + ", lineG=" + lineG + ", lineB=" + lineB + ", lineY05=" + lineY05
					+ ", lineChar=" + lineChar + ", busNo1=" + busNo1 + ", busNo2=" + busNo2 + ", lineIs=" + lineIs + "]";
		}
	
}
