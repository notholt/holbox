package de.hsrt.holbox.ClassicPowerFlow;

import de.hsrt.holbox.util.Signal;

public class Sys {

	//Test data on Lines and Buses which later will be imported from Interface package
	
		//public static int 		qtyOfBuses = 4;		//Quantity of buses (nodes) - size of matrices - Not used anymore
		public static double	freqNom = 60.0;		//Nominal frequency
		public static Signal	sysFreq = new Signal("System Frequency", "Hz", freqNom); // System Frequency signal
		public static double	baseMVA = 100.0;
		public static boolean	steadyState = true;
		
}
