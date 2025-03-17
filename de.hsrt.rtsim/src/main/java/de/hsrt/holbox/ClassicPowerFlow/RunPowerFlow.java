package de.hsrt.holbox.ClassicPowerFlow;

import java.util.*;
//import org.knowm.xchart.BitmapEncoder;
//import org.knowm.xchart.XYChart;
//import org.knowm.xchart.XYChartBuilder;
//import org.knowm.xchart.BitmapEncoder.BitmapFormat;
//import org.knowm.xchart.QuickChart;
//import org.knowm.xchart.SwingWrapper;
//import org.apache.commons.numbers.complex.*; //Apache common number

public class RunPowerFlow {

	static public void printResults(PowerFlow testConfig) {
		// method for printing the results of Power Flow Calculation
		
		Bus[] theBuses = testConfig.getBusArray();
		//Line[][] theLines = testConfig.getLineArray();
		
		//Printing the bus voltages in V[p.u] / angle [deg] format
		
		System.out.println("\nBus voltages");
		for (Bus theBus : theBuses) {
			//int busNr = theBus.getBusNo();	// Bus No
			double Vmgn = theBus.vtg;		// voltage magnitude of the bus
			//double angDeg = theBus.ang * 360 / (2 * Math.PI);	//angle in degrees
			System.out.printf("%.2f\n", Vmgn);
		}
		
		System.out.println("\nBus voltage angles [deg]");
		for (Bus theBus : theBuses) {
			//int busNr = theBus.getBusNo();	// Bus No
			//double Vmgn = theBus.vtg;		// voltage magnitude of the bus
			double angDeg = theBus.ang * 360 / (2 * Math.PI);	//angle in degrees
			System.out.printf("%.1f\n", angDeg);
		}
		
			
		//Slack bus
		Bus mySlackBus = theBuses[0];					//the slack bus (object of Bus) at final iteration
		Double slackP = mySlackBus.getPwrAtGen();		// P and Q generated values respectively
		Double slackQ = mySlackBus.getPwrRtGen();
		System.out.printf("\n\nResults for the slack bus: P = %.3f; Q = %.3f\n", slackP, slackQ);
		
//		double vtgMgnToll = (theBuses[8].vtg - 1.056)/1.056*100;
//		System.out.printf("\nBus 8 tolerance %.2f %%", vtgMgnToll);
	}
	
	public static void main(String[] args) {
		
		// Preparing collections for the test cases
		// initial ArrayLists with data about the buses; to be fed from GUI
		
		//Maple TestCase:
		ArrayList<Line> mapleLines = new ArrayList<Line>();			
		ArrayList<Bus> 	mapleBuses = new ArrayList<Bus>();
		
		//Maple + Transformer Test-case
		ArrayList<Line> mapleTransLines = new ArrayList<Line>();			
		ArrayList<Bus> 	mapleTransBuses = new ArrayList<Bus>();
		
		// IEEE 6-bus Test Feeder TestCase:
		ArrayList<Line> ieee6busLines = new ArrayList<Line>();			
		ArrayList<Bus> 	ieee6busBuses = new ArrayList<Bus>();

		// IEEE 14-bus Test Feeder  TestCase:
		ArrayList<Line> ieee14busLines = new ArrayList<Line>();			
		ArrayList<Bus> 	ieee14busBuses = new ArrayList<Bus>();			
		
						
		// - --------------   4-bus case 'Maple'
		
		//Lines
		mapleLines.add(new Line("0-1", 0.01008, 0.05040, 0.05125, 10.25, 0, 1, true));
		mapleLines.add(new Line("0-2", 0.00744, 0.03720, 0.03875, 7.75, 0, 2, true));
		mapleLines.add(new Line("1-3", 0.00744, 0.03720, 0.03875, 7.75, 1, 3, true));
		mapleLines.add(new Line("2-3", 0.01272, 0.06360, 0.06375, 12.75, 2, 3, true));
		//Buses
		mapleBuses.add(new Bus("Birch",	0,	BusMode.SLACK_BUS,	0,		0,	0.5,	0.3099,	1, 0, 1));
		mapleBuses.add(new Bus("Elm",	1,	BusMode.PQ_BUS,		0,		0,	1.7,	1.0535,	1, 0, 1));
		mapleBuses.add(new Bus("Pine",	2,	BusMode.PQ_BUS,		0,		0,	2.0,	1.2394,	1, 0, 1));
		mapleBuses.add(new Bus("Maple", 3,	BusMode.PV_BUS,		3.18,	0,	0.8,	0.4958,	1.02, 0, 1.02));
		

		// - --------------   5-bus case 'Maple + Transformer'
		
		//Lines
		mapleTransLines.add(new Line("0-1", 0.01008, 0.05040, 0.05125, 10.25, 0, 1, true));
		mapleTransLines.add(new Line("0-2", 0.00744, 0.03720, 0.03875, 7.75, 0, 2, true));
		mapleTransLines.add(new Line("1-3", 0.00744, 0.03720, 0.03875, 7.75, 1, 3, true));
		mapleTransLines.add(new Line("2-3", 0.01272, 0.06360, 0.06375, 12.75, 2, 3, true));
		mapleTransLines.add(new Line("2-4", 0.00000, 0.02000, 0.00000, 0.000, 2, 4, true, true, 0.964, false));
		//Buses
		mapleTransBuses.add(new Bus("Birch",		0,	BusMode.SLACK_BUS,	0,		0,	0.5,	0.3099,	1, 0, 1));
		mapleTransBuses.add(new Bus("Elm",			1,	BusMode.PQ_BUS,		0,		0,	1.7,	1.0535,	1, 0, 1));
		mapleTransBuses.add(new Bus("EmptyPine",	2,	BusMode.PQ_BUS,		0,		0,	0,		0.0,	1, 0, 1));
		mapleTransBuses.add(new Bus("NewPine",		4,	BusMode.PQ_BUS,		0,		0,	2.0,	1.2394,	1, 0, 1));
		mapleTransBuses.add(new Bus("Maple", 		3,	BusMode.PV_BUS,		3.18,	0,	0.8,	0.4958,	1.02, 0, 1.02));
		
		
		// ------------------ IEEE 6-bus Test Feeder
		
		//Lines
		ieee6busLines.add(new Line("1-2",	0.10,	0.40,	0,	0,	1,	2,	true));
		ieee6busLines.add(new Line("1-4",	0.15,	0.60,	0,	0,	1,	4,	true));
		ieee6busLines.add(new Line("1-5",	0.05,	0.20,	0,	0,	1,	5,	true));
		ieee6busLines.add(new Line("2-3",	0.05,	0.20,	0,	0,	2,	3,	true));
		ieee6busLines.add(new Line("2-4",	0.1,	0.40,	0,	0,	2,	4,	true));
		ieee6busLines.add(new Line("0-2",	0.01875,	0.075,	0,	0,	0,	2,	true));
		ieee6busLines.add(new Line("3-5",	0.025,	0.1,	0,	0,	3,	5,	true));
		ieee6busLines.add(new Line("0-4",	0.0375,	0.150,	0,	0,	0,	4,	true));
		
		//Buses
		ieee6busBuses.add(new Bus("Slack",	0,	BusMode.SLACK_BUS,	0,		0,	0,		0,		1.04,	0,	1.04));
		ieee6busBuses.add(new Bus("One",	1,	BusMode.PV_BUS,		0,		0,	0.29,	0,		1.02,	0,	1.02));
		ieee6busBuses.add(new Bus("Two",	2,	BusMode.PQ_BUS,		0,		0,	2.4,	0,		1.00,	0,	1.00));
		ieee6busBuses.add(new Bus("Three",	3,	BusMode.PV_BUS,		1.283,	0,	0,		0,		1.04,	0,	1.04));
		ieee6busBuses.add(new Bus("Four",	4,	BusMode.PQ_BUS,		0,		0,	1.6,	0.4,	1.00,	0,	1.00));
		ieee6busBuses.add(new Bus("Five",	5,	BusMode.PQ_BUS,		0,		0,	2.4,	0.0,	1.00,	0,	1.00));
		
		
		//	---       IEEE 14-Bus Test Feeder
		
		//Lines
		ieee14busLines.add(new Line("Line xx", 	0.01938, 0.05917, 	0.0264, 	0.0528, 	0, 1, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.05403, 0.22304, 	0.0246, 	0.0492, 	0, 4, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.04699, 0.19797, 	0.0219, 	0.0438, 	1, 2, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.05811, 0.17632, 	0.0170, 	0.0340, 	1, 3, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.05695, 0.17388, 	0.0173, 	0.0346, 	1, 4, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.06701, 0.17103, 	0.0064, 	0.0128, 	2, 3, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.01335, 0.04211, 	0.0000, 	0.0000, 	3, 4, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.00000, 0.20912, 	0.0000, 	0.0000, 	3, 6, 		true,	true,	0.978, true));
		ieee14busLines.add(new Line("Line xx", 	0.00000, 0.55619, 	0.0000, 	0.0000, 	3, 8, 		true,	true,	0.969, true));
		ieee14busLines.add(new Line("Line xx", 	0.00000, 0.25202, 	0.0000, 	0.0000, 	4, 5, 		true,	true,	0.932, true));
		ieee14busLines.add(new Line("Line xx", 	0.09498, 0.1989, 	0.0000, 	0.0000, 	5, 10, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.12291, 0.25581, 	0.0000, 	0.0000, 	5, 11, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.06615, 0.13027, 	0.0000, 	0.0000, 	5, 12, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.00000, 0.17615, 	0.0000, 	0.0000, 	6, 7, 		true,	true,	1, true));
		ieee14busLines.add(new Line("Line xx", 	0.00000, 0.11001, 	0.0000, 	0.0000, 	6, 8, 		true,	true,	1, true));
		ieee14busLines.add(new Line("Line xx", 	0.03181, 0.0845, 	0.0000, 	0.0000, 	8, 9, 		true,	true,	1, true));
		ieee14busLines.add(new Line("Line xx", 	0.12711, 0.27038, 	0.0000, 	0.0000, 	8, 13, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.08205, 0.19207, 	0.0000, 	0.0000, 	9, 10, 		true));
		ieee14busLines.add(new Line("Line xx", 	0.22092, 0.19988, 	0.0000, 	0.0000, 	11, 12, 	true));
		ieee14busLines.add(new Line("Line xx", 	0.17093, 0.34802, 	0.0000, 	0.0000, 	12, 13, 	true));
		//sysLines.add(new Line("Line xx", 	0.00000, 0.00000, 	0.0000, 	0.0000, 	0, 1, 	true));
		
		//Buses	
		ieee14busBuses.add(new Bus("Bus 1 HV", 0, BusMode.SLACK_BUS, 0.0, 0.000, 	0.000, 0.000,	1.060, 0, 1.060));
		ieee14busBuses.add(new Bus("Bus 2 HV", 1, BusMode.PV_BUS, 	0.4, 0.000,		0.217, 0.127, 	1.045, 0, 1.045));
		ieee14busBuses.add(new Bus("Bus 3 HV", 2, BusMode.PV_BUS, 	0.0, 0.000, 	0.942, 0.190, 	1.010, 0, 1.010));
		
		ieee14busBuses.add(new Bus("Bus 4 HV", 3, BusMode.PQ_BUS, 	0.0, 0.000, 	0.478, -0.039, 	1.000, 0, 1.000));
		ieee14busBuses.add(new Bus("Bus 5 HV", 4, BusMode.PQ_BUS, 	0.0, 0.000, 	0.076, 0.016, 	1.000, 0, 1.000));
		ieee14busBuses.add(new Bus("Bus 6 LV", 5, BusMode.PV_BUS, 	0.0, 0.000,		0.112, 0.075, 	1.070, 0, 1.070));
		
		ieee14busBuses.add(new Bus("Bus 7 ZV", 6, BusMode.PQ_BUS, 	0.0, 0.000,		0.000, 0.000, 	1.000, 0, 1.000));	//zero point of the transformer
		ieee14busBuses.add(new Bus("Bus 8 TV", 7, BusMode.PV_BUS, 	0.0, 0.000,		0.000, 0.000, 	1.090, 0, 1.090));	
		ieee14busBuses.add(new Bus("Bus 9 LV", 8, 	BusMode.PQ_BUS, 	0.0, 0.000,		0.295, 0.166, 	1.000, 0, 1.000,	true,	0.19));	//the only bus with shunt susceptance	
		
		ieee14busBuses.add(new Bus("Bus 10 LV", 9, 	BusMode.PQ_BUS, 	0.0, 0.000,		0.090, 0.058, 	1.000, 0, 1.000));	
		ieee14busBuses.add(new Bus("Bus 11 LV", 10, 	BusMode.PQ_BUS, 	0.0, 0.000,		0.035, 0.018, 	1.000, 0, 1.000));	
		ieee14busBuses.add(new Bus("Bus 12 LV", 11, 	BusMode.PQ_BUS, 	0.0, 0.000,		0.061, 0.016, 	1.000, 0, 1.000));	
		
		ieee14busBuses.add(new Bus("Bus 13 LV", 12, 	BusMode.PQ_BUS, 	0.0, 0.000,		0.135, 0.058, 	1.000, 0, 1.000));	
		ieee14busBuses.add(new Bus("Bus 14 LV", 13, 	BusMode.PQ_BUS, 	0.0, 0.000,		0.149, 0.050, 	1.000, 0, 1.000));	
		
		
		PowerFlow testCase = new PowerFlow(ieee6busLines, ieee6busBuses); 			// creating a Configuration: an instant of PowerFlow class 
		testCase.runGaussSeidel();	
		
		System.out.println("\nThe Power Flow has been calculated");
		
		printResults(testCase);
		
	
	} // end of the main method
} // end of class RunPowerFlow
