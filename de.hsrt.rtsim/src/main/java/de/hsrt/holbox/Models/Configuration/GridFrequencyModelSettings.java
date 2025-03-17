package de.hsrt.holbox.Models.Configuration;

import de.hsrt.holbox.util.DoubleParameter;

public class GridFrequencyModelSettings {
	
	public DoubleParameter 	sysPwrApBase	= new DoubleParameter("SysPwrApBase", "Base system power for p.u. calculations", "MVA", 5.0, 0.001, 10000.0);
	public DoubleParameter 	sysVtgBase 		= new DoubleParameter("SysVtgBase", "Base system voltage", "kV", 11.0, 0.2, 1000.0);
	public DoubleParameter 	freqBase 		= new DoubleParameter("FreqBase", "Base system nominal Frequency", "Hz", 50.0, 10.0, 440.0);
	public DoubleParameter 	hSys	 		= new DoubleParameter("Hsys", "System's Intertia constant", "s", 3.0, 0.1, 10.0);
	public DoubleParameter 	dSys	 		= new DoubleParameter("Dsys", "Systems Damping constant", "-", 0.0, 0.0, 1.0);
	public DoubleParameter 	tm		 		= new DoubleParameter("Tm", "System's regulation equivalent reaction time", "s", 0.1, 0.0, 100.0);
	public DoubleParameter 	droop	 		= new DoubleParameter("Droop", "System Droop Characteristic", "%", 5.0, 0.1, 100.0);
}
