package de.hsrt.holbox.ClassicPowerFlow;

import java.util.*; //utility library
import org.apache.commons.numbers.complex.*; //Apache common numbers	

//import java.util.ArrayList; 					//they are already in all utilities
//import java.util.Collections;

public class PowerFlow {

	private ArrayList<Line> sysLines; // ArrayList of elements of type 'Line' with parameters in per units;
	// Serves as an 'entry point' or INPUT argument of the object (test case)

	private ArrayList<Bus> sysBuses; // ArrayList of elements of type 'Bus' with parameters in per units;
	// Serves as an 'entry point' or input argument of the object (test case)

	// private ConventionalGenerator mainGenset; //deal with that later // not to
	// forget to import the package

	private Line[][] lineArray; // array with lines (objects of type 'Line', see the 'Line' class for the
	// explanation of the fields); This attribute is to contain the RESULTS of power
	// flow calculation - an OUTPUT

	private Bus[] busArray; // array with buses (objects of type 'Bus', see the 'Bus' class for the
	// explanation of the fields); This attribute is to contain the RESULTS of power
	// flow calculation - an OUTPUT

	public PowerFlow(ArrayList<Line> sysLines, ArrayList<Bus> sysBuses) { // CONSTRUCTOR version No 1: no generator
		super();
		this.sysLines = sysLines;
		this.sysBuses = sysBuses;
	}

	protected static Complex[][] admY(Line[][] theLineMatrix, double freq) {
		// Method for generating the admittance matrix
		// with TRANSFORMERS
		// without shunt susceptance!

		int M = theLineMatrix.length;

		Complex[][] yA = new Complex[M][M]; // that will be an output variable

		// Updating the values of lineG and lineB of each element in the lineArray for
		// the current frequency value
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {
				if (theLineMatrix[i][j].lineIs) {
					theLineMatrix[i][j].lineGB(freq, Sys.freqNom);
				} else {
					theLineMatrix[i][j].lineG = 0; // The method will scan all the elements of the LineArray. If the  line doesn't exist, there should be zeros for G and B
					theLineMatrix[i][j].lineB = 0;
				}
			} // for-j
		} // for-i

		// The cycle generates the elements of admittance matrix

		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {

				if (i == j) { // dealing with diagonal elements: sum of all elements of lineArray's current row, but special treatment for transformers
					double sumG = 0, sumB = 0; // aux variables for accumulating the sums of Gs and Bs
					for (int k = 0; k < M; k++) { // going through the row
						double tapper;
						Line theLine = theLineMatrix[i][k];
						tapper = theLine.hasTransformer
								&& ((k < i && theLine.tapAtLowerIndex) || (k > i && !theLine.tapAtLowerIndex))
								? Math.pow(theLine.turnsRatio, 2)
										: 1; // tro-lolo logic
						sumG += tapper * theLineMatrix[i][k].lineG;
						sumB += tapper * (theLineMatrix[i][k].lineB + theLineMatrix[i][k].lineY05 * freq / Sys.freqNom);
					}
					yA[i][j] = Complex.ofCartesian(sumG, sumB); // Their admittances are sums of Ys of each adjacent line together with shunt capacitances
				} // if for diagonal element

				else { // handling non-diagonal elements
					double tapper;
					tapper = (theLineMatrix[i][j].hasTransformer) ? theLineMatrix[i][j].turnsRatio : 1;
					double g = -tapper * theLineMatrix[i][j].lineG;
					double b = -tapper * theLineMatrix[i][j].lineB;
					yA[i][j] = Complex.ofCartesian(g, b);
				}

			} // for-j
		} // for-i

		return yA;
	}

	protected static Complex[][] admY2(Line[][] theLineMatrix, Bus[] theBusMatrix, double freq) {
		// Method for generating the admittance matrix
		// with TRANSFORMERS
		// with SHUNT SUSCEPTANCE

		int M = theLineMatrix.length;

		Complex[][] yA = new Complex[M][M]; // that will be an output variable

		// Updating the values of lineG and lineB of each element in the lineArray for 		// the current frequency value
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {
				if (theLineMatrix[i][j].lineIs) {
					theLineMatrix[i][j].lineGB(freq, Sys.freqNom);
				} else {
					theLineMatrix[i][j].lineG = 0; // The method will scan all the elements of the LineArray. If the 					// line doesn't exist, there should be zeros for G and B
					theLineMatrix[i][j].lineB = 0;
				}
			} // for-j
		} // for-i

		// The cycle generates the elements of admittance matrix

		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {

				if (i == j) { // dealing with diagonal elements: sum of all elements of lineArray's current row, but special treatment for transformers
					double sumG = 0, sumB = 0; // aux variables for accumulating the sums of Gs and Bs
					for (int k = 0; k < M; k++) { // going through the row
						double tapper;
						Line theLine = theLineMatrix[i][k];
						tapper = theLine.hasTransformer && ((k < i && theLine.tapAtLowerIndex) || (k > i && !theLine.tapAtLowerIndex)) ? Math.pow(theLine.turnsRatio, 2) : 1; // tro-lolo logic
						sumG += tapper * theLineMatrix[i][k].lineG;
						double extraB = (theBusMatrix[i].hasShuntB) ? theBusMatrix[i].shuntB : 0;
						sumB += tapper * (theLineMatrix[i][k].lineB + theLineMatrix[i][k].lineY05 * freq / Sys.freqNom + extraB);
					}
					yA[i][j] = Complex.ofCartesian(sumG, sumB); // Their admittances are sums of Ys of each adjacent line together with shunt capacitances
				} // if for diagonal element

				else { // handling non-diagonal elements
					double tapper;
					tapper = (theLineMatrix[i][j].hasTransformer) ? theLineMatrix[i][j].turnsRatio : 1;
					double g = -tapper * theLineMatrix[i][j].lineG;
					double b = -tapper * theLineMatrix[i][j].lineB;
					yA[i][j] = Complex.ofCartesian(g, b);
				}

			} // for-j
		} // for-i

		return yA;
	}

	protected static Complex nodeVtg(Bus[] theBuses, int nodeNo, Complex[][] Y) {
		// A method for calculating one PQ-node's voltage

		/*
		 * Input arguments: theBus - the whole busArray whenever the size currently it
		 * has busNo - number of the bus (node) for which the voltage is to be
		 * calculated iterationNo - current iteration Y - admittance matrix at given
		 * frequency
		 */

		// Making more convenient notations:
		Complex result;
		int N = theBuses.length; 
		Bus node = theBuses[nodeNo]; 	// the bus/node the method currently deals with (calculates voltage for)
		double Psch = node.pwrAtSch; 	// scheduled P (is constant for all iterations)
		double Qsch = node.pwrRtSch; 	// scheduled Q
		Complex Vnk_1 = Complex.ofPolar(node.vtg, node.ang); // voltage of the given node at PREVIOUS iteration

		// Parts of equation (factors, addenda)
		Complex factor1 = Complex.ONE.divide(Y[nodeNo][nodeNo]); // 1/Y[i][i] - division by diagonal element of the
		// admittance matrix
		Complex addend1; // for (P-jQ)/Vconjugated
		Complex P_jQ = Complex.ofCartesian(Psch, -1 * Qsch);
		Complex Vconj = Vnk_1.conj();
		addend1 = Complex.ONE.multiply(P_jQ).divide(Vconj);

		// Cycle1: the sum of elements Y[i][j]*V[j][k] (before the main diagonal, other voltages at current iteration)
		Complex sum1 = Complex.ZERO; // sum1 to accumulate in the cycle
		for (int j = 0; j < nodeNo; j++) {
			Complex Vj = Complex.ofPolar(theBuses[j].vtg, theBuses[j].ang);
			Complex yV = Complex.ONE.multiply(Y[nodeNo][j]).multiply(Vj);
			sum1 = Complex.ZERO.add(sum1).add(yV);
		} // end for-n

		// Cycle2: the sum of elements Y[i][j]*V[j][k-1] (after the main diagonal, other voltages at previous iteration)
		Complex sum2 = Complex.ZERO; // sum2 to accumulate in the cycle
		for (int j = nodeNo + 1; j < N; j++) {
			Complex Vj = Complex.ofPolar(theBuses[j].vtg, theBuses[j].ang);
			Complex yV = Complex.ONE.multiply(Y[nodeNo][j]).multiply(Vj);
			sum2 = Complex.ZERO.add(sum2).add(yV);
		} // end for-n

		// comment: result = factor1*(addend1 - sum1 - sum2) - gathering all the  components of the equation
		Complex brackets = Complex.ZERO.add(addend1).subtract(sum1).subtract(sum2);
		result = Complex.ONE.multiply(factor1).multiply(brackets);

		return result;

	} // end of nodeVtg() method

	protected static double nodeQ(Bus[] theBuses, int busNo, Complex[][] Y) {
		// A method for calculating the PV-node's reactive power Q

		/*
		 * theBus - the whole busArray whenever the size currently it is busNo - number
		 * of the bus (node) for which the voltage is to be calculated iterationNo -
		 * current iteration
		 */

		// Making more convenient notations:
		int N = theBuses.length; 

		Bus node = theBuses[busNo]; 						// the node/bus we are dealing with
		Complex Vnk_1 = Complex.ofPolar(node.vtg, node.ang); // voltage of the given node at the current iteration

		double result;

		// Parts of equation (factors, addends)

		Complex Vconj = Vnk_1.conj();

		// Cycle1: the sum of elements Y[i][j]*V[j][k] (before the main diagonal /of Y/, other voltages at current iteration)
		Complex sum1 = Complex.ZERO; // sum1 to accumulate in the cycle
		for (int j = 0; j < busNo; j++) {
			Complex Vj = Complex.ofPolar(theBuses[j].vtg, theBuses[j].ang);
			Complex yV = Complex.ONE.multiply(Y[busNo][j]).multiply(Vj);
			sum1 = Complex.ZERO.add(sum1).add(yV);
		} // end for-n

		// Cycle2: the sum of elements Y[i][j]*V[j][k-1] (WITH and after the main diagonal /of Y/, voltages at previous iteration)
		Complex sum2 = Complex.ZERO; // sum2 to accumulate in the cycle
		for (int j = busNo; j < N; j++) { // whappaaa! here is the difference with nodeVtg method!!!
			Complex Vj = Complex.ofPolar(theBuses[j].vtg, theBuses[j].ang);
			Complex yV = Complex.ONE.multiply(Y[busNo][j]).multiply(Vj);
			sum2 = Complex.ZERO.add(sum2).add(yV);
		} // end for-n

		Complex brackets = Complex.ZERO.add(sum1).add(sum2);
		Complex product = Complex.ONE.multiply(Vconj).multiply(brackets);
		double imPart = product.getImaginary();

		result = -1 * imPart;

		return result;

	}// end of nodeQ method

	protected static double inLimit(double x, double xmin, double xmax) {
		// A method for putting any calculated value within reasonable limits
		// the code speaks for itself

		double y; // expected result

		if (x > xmax) {
			y = xmax;
		} else {
			if (x < xmin) {
				y = xmin;
			} else {
				y = x;
			} 
		} 

		return y;

	} // end of method

	protected void accumulateSlackBusPower(double freq) {
		/*
		 * A method for calculation of power demand from the slack bus
		 * 
		 */

		int noBuses = sysBuses.size();

		// Cycling through the lines (lineArray) to calculate the lines' currents and
		// P,Q losses
		for (int i = 0; i < noBuses; i++) {
			for (int j = 0; j < noBuses; j++) {
				Bus nodeI = busArray[i]; 	// the first node to which the line is attached to
				Bus nodeJ = busArray[j]; 	// obviously, the other end. Alles hat ein Ende, nur die Wurst hat zwei

				if (lineArray[i][j].lineIs) { 				// if this line exists
					lineArray[i][j].myCur(nodeI, nodeJ); 	// the method myCur calculates the current of the line, using the voltages of the buses it connects
					lineArray[i][j].lossP(); 				// losses of active power in the line
					lineArray[i][j].lossQ(); 				// same for reactive
					lineArray[i][j].lossY05(nodeI, nodeJ, freq); // charging losses
				} else {
					lineArray[i][j].lineLossP = 0;
					lineArray[i][j].lineLossQ = 0;
					lineArray[i][j].lineLossY05 = 0;
				}
			} // end of for - inner loop "j"
		} // end of for - outer loop "i"

		// Summation of consumed/scheduled powers in the buses and lines' losses to calculate the demand from the slack bus

		double busPwrAtTotal = 0, busPwrRtTotal = 0; 		// vars for the sum of active and reactive power from the buses
		double linePwrAtTotal = 0, linePwrRtTotal = 0; 		// - //- sum of losses in the lines
		double slackPwrAtTotDem = 0, slackPwrRtTotDem = 0; 	// vars for slack bus P and Q demand

		// summing up the scheduled powers for each BUS, except the swing bus
		for (int node = 1; node < noBuses; node++) {
			busPwrAtTotal += -1 * busArray[node].pwrAtSch; // Scheduled. But why minus? - because generation is taken with plus, while load is designated with minus
			busPwrRtTotal += -1 * busArray[node].pwrRtSch;
		} //
		// System.out.printf("\n");

		// summing up the losses for each LINE
		for (int i = 0; i < noBuses; i++) {
			for (int j = i; j < noBuses; j++) {
				linePwrAtTotal += lineArray[i][j].lineLossP;
				linePwrRtTotal += lineArray[i][j].lineLossQ + lineArray[i][j].lineLossY05;
			}
		}

		// Generated powers
		slackPwrAtTotDem = 1 * busArray[0].pwrAtDem + busPwrAtTotal + linePwrAtTotal;
		slackPwrRtTotDem = 1 * busArray[0].pwrRtDem + busPwrRtTotal + linePwrRtTotal;

		double slackPwrAtTotSch = busPwrAtTotal + linePwrAtTotal;
		double slackPwrRtTotSch = busPwrRtTotal + linePwrRtTotal;

		// Comment: the power needed to be generated by the slack bus is calculated as a sum of scheduled powers from all the other buses, plus losses in the lines,
		// plus own demand (if there is an own load at the slack bus)

		busArray[0].pwrAtGen = slackPwrAtTotDem;
		busArray[0].pwrRtGen = slackPwrRtTotDem; 

		busArray[0].pwrAtSch = slackPwrAtTotSch; 
		busArray[0].pwrRtSch = slackPwrRtTotSch; 

	}

	protected static double droop(double P) {

		// function for defining the output frequency of the Diesel generator
		// should it be here in 'Power Flow'?

		return Sys.freqNom;

	}

	protected void normalizeLineList(ArrayList<Line> sysLines, ArrayList<Bus> sysBuses) {
		// a placeholder/stub method used to consider the direction of transformation of
		// the transformer in the line
		// at current approach, the 'Final turn ratio' - turnsRatio attribute of 'Line'
		// class is written as it is given in the initial data
		// this method will check the direction of transformation (either out of HV-LV
		// considerations, or out of the data which side is the tap-changing side.
		// as a result, the turnsRatio either remains as is (the conventional definition
		// of transformer ratio: HV/LV...)
		// or the value for turnsRatio will be replaced with the inverse
		// method might require the data from buses in the future (to know which bus is
		// HV, which is LV)

		for (Line theLine : sysLines) {
			theLine.turnsRatio = theLine.turnsRatio;
		}

	}

	protected void initializeLineArray(ArrayList<Line> sysLines, ArrayList<Bus> sysBuses) {

		int noBuses = sysBuses.size(); // Quantity of buses. Size of the bus array. Note that max index is noBuses-1

		// --------------- composing LINE ARRAY-----------------------------
		// Composing lineArray (converting ArrayList to Array)

		this.lineArray = new Line[noBuses][noBuses];

		for (Line theLine : sysLines) {
			int i;
			int j;
			if (theLine.busNo1 < theLine.busNo2)		// ANo: For this to work, bus1 must always be less than bus2, if other way around, exchange
			{
				i = theLine.busNo1; // extracting numbers to which the current line is connected
				j = theLine.busNo2;
			} else {
				i = theLine.busNo2; // extracting numbers to which the current line is connected
				j = theLine.busNo1;
			}

			lineArray[i][j] = theLine; // and assigning that to the proper place in the array

			if (lineArray[i][j].lineIs) {
				lineArray[i][j].lineGB(Sys.freqNom, Sys.freqNom); // setting initial values of G and B with the nominal
				// frequency
			} else {
				lineArray[i][j].lineG = 0;
				lineArray[i][j].lineB = 0;
			} // if the line is
		} // for -theLine

		// Next we fill the values in the lineArray matrix, which was not declared, with a "null line" (isLine = false)
		// Here it is only for the elements up and to the right of the main diagonal
		for (int i = 0; i < noBuses; i++) {
			for (int j = i; j < noBuses; j++) {
				if (lineArray[i][j] == null) {
					lineArray[i][j] = new Line("empty", 0, 0, 0, 0, 0, 0, false);
				}
			}
		}

		// Filling the rest of lineArray below the main diagonal with symmetrical values
		for (int i = 0; i < noBuses; i++) {
			for (int j = 0; j <= i; j++) {
				if (i == j) {
					lineArray[i][j] = new Line("diag", 0, 0, 0, 0, 0, 0, false);
				} else {
					lineArray[i][j] = lineArray[j][i];
				}
			} // for-j
		} // for-i
	}

	protected void initializeBusArray(ArrayList<Bus> sysBuses) {

		int noBuses = sysBuses.size(); // Quantity of buses. Size of the bus array. Note that max index is noBuses-1

		// ------------------- composing BUS ARRAY ---------------
		// Converting them to array
		this.busArray = new Bus[noBuses]; // the initial P,Q,Vs a

		// The cycle fills the whole busArray with initial values. Certain values (V,Q) will be recalculated at each iteration
		for (Bus theBus : sysBuses) {
			int n = theBus.busNo;
			busArray[n] = theBus;
		}

		for (int n = 0; n < noBuses; n++) {
			busArray[n].pwrAtSch = busArray[n].pwrAtGen - busArray[n].pwrAtDem;
			busArray[n].pwrRtSch = busArray[n].pwrRtGen - busArray[n].pwrRtDem;
		}
	}

	public void runGaussSeidel() {

		int noBuses = sysBuses.size(); // Quantity of buses (size of the bus array). Note that max index is noBuses-1

		normalizeLineList(sysLines, sysBuses); 		// Normalizing line list to correct the transformation ratio, if necessary
		initializeLineArray(sysLines, sysBuses); 	// Composing lineArray
		initializeBusArray(sysBuses); 				// Composing busArray

		// ----------------------------------------- CYCLING
		// ------------------------------------------

		double freq = Sys.freqNom; 	// the value of frequency! will be re-calculated in the end of the cycle
		double maxQ = 4; 			// max allowed/reasonable reactive power [per units]
		double minQ = -4; 			// min allowed/reasonable reactive power [per units] - should it/can it be negative?

		// Complex[][] admMatrix = admY2(lineArray, busArray, freq); 	//Admittance matrix with shunt susceptance
		Complex[][] admMatrix = admY(lineArray, freq); 					// Admittance matrix without shunt susceptance

		// A Gauss-Seidel cycle: iterating through the whole network
		int noIterations = 36;
		for (int iteration = 1; iteration <= noIterations; iteration++) { // cycle through iterations

			// The cycle through BUSES (nodes)
			for (int nodeNo = 1; nodeNo < noBuses; nodeNo++) { 				// 1) we're skipping the slack bus, so start from the second node ( node = 1);
				Bus node = busArray[nodeNo];
				
				if (node.hasShuntB) { 										// handling the shunt succeptance
					node.updateQsch(freq); 									// corrects the scheduled reactive power
				}
				
				if (node.busType == BusMode.PQ_BUS) {
					Complex Vx = nodeVtg(busArray, nodeNo, admMatrix); 		// calculating voltage for the bus dealt
					node.updateVtg(Vx); 									// updating the voltage of the bus dealt (current node)
				} else {
					if (node.busType == BusMode.PV_BUS) {
						double Qy = nodeQ(busArray, nodeNo, admMatrix); 	// Estimated value of necessary reactive power
						double Qlimited = inLimit(Qy, minQ, maxQ); 			// Limited within reasonable bounds
						node.pwrRtSch = Qlimited; 							// Setting the reactive power for the node dealt with
						node.pwrRtGen = node.pwrRtSch + node.pwrRtDem;
						Complex Vy = nodeVtg(busArray, nodeNo, admMatrix);
						node.updateVtg(Vy); 								// updating the voltage and correcting the voltage to the set value; the method is described in the Bus // class
					} else {
						System.out.println("The bus is skipped since it is neither of PQ nor PV type");
					}
				} // end of the if-else that checks the type of the bus

			} // end of for through the nodes
		} // end of for-k (ITERATIONS)

		// Voila! Now the voltages and reactive powers for the buses except slack are calculated (at this iteration).

		accumulateSlackBusPower(freq);

		// ----- dealing with the DROOP ------------------------------------------------
		//double genPwrAtDem = busArray[0].pwrAtGen;
		//freq = droop(genPwrAtDem); 			// re-estimate frequency as a function of load

	} // end of main method

	// Getters and setters

	public Line[][] getLineArray() {
		return lineArray;
	}

	public void setLineArray(Line[][] lineArray) {
		this.lineArray = lineArray;
	}

	public Bus[] getBusArray() {
		return busArray;
	}

	public void setBusArray(Bus[] busArray) {
		this.busArray = busArray;
	}
	
	

} // end of class PowerFlow
