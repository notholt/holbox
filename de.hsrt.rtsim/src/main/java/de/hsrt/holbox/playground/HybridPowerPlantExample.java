package de.hsrt.holbox.playground;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.numbers.complex.Complex;

import de.hsrt.holbox.ClassicPowerFlow.Bus;
import de.hsrt.holbox.ClassicPowerFlow.BusMode;
import de.hsrt.holbox.ClassicPowerFlow.Line;
import de.hsrt.holbox.ClassicPowerFlow.PowerFlow;
import de.hsrt.holbox.ClassicPowerFlow.Sys;
import de.hsrt.holbox.Models.*;
import de.hsrt.holbox.util.Dataset;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.Signal;
import de.hsrt.holbox.Models.Configuration.*;

/**
 * This class implements a simple hybrid wind park based on Paper by E. Muljadi:
 * Method of Equivalencing for a Large Wind Power Plant with Multiple Turbine Representation
 * NREL/CP-500-42886
 * @author notholt
 *
 */

public class HybridPowerPlantExample {
	
	private static final ZoneId UTC = ZoneId.of("UTC");
	private ArrayList<Bus> buses;
	private ArrayList<Line> lines;
	private Dataset inputDataset;
	private Dataset outputDataset;
	private ArrayList<Signal> controlSignals;
	private ArrayList<Signal> statusSignals;
		
	private PowerFlow park;
	
	
	
	private SimplePhotovoltaic pv1;
	private ControlledDERSettings pvcConf = new ControlledDERSettings();
	private ControlledDER pvC1;
	private SimplePhotovoltaic wt1, wt2;
	private SimpleBattery batt1;
	
	// Status Signals
	private Signal pvPwrAt;
	private Signal wt1PwrAt;
	private Signal wt2PwrAt;
	private Signal battPwrAt;
	private Signal pvPwrRt;
	private Signal wt1PwrRt;
	private Signal wt2PwrRt;
	private Signal battPwrRt;
	
	private Instant prevTimestamp;
	
	// Control Signals
	private Signal pvPwrAtMaxPc = new Signal("PvPwrAtMaxPc", "%", 100.0); 
	private Signal pvPwrRtSetpPc = new Signal("PvPwrRtSetpPc", "%", 0.0);
	private Signal wt1PwrAtMaxPc = new Signal("Wt1PwrAtMaxPc", "%", 100.0); 
	private Signal wt1PwrRtSetpPc = new Signal("Wt2PwrRtSetpPc", "%", 0.0);
	private Signal wt2PwrAtMaxPc = new Signal("Wt1PwrAtMaxPc", "%", 100.0); 
	private Signal wt2PwrRtSetpPc = new Signal("Wt2PwrRtSetpPc", "%", 0.0);


	private Signal battPwrAtSetp = new Signal("BattPwrAtSetp", "kW", 0.0); 
	private Signal battPwrRtSetp = new Signal("BattPwrRtSetp", "kvar", 0.0);
	private Signal battSoC = new Signal("BattSoC", "%", 0.0);
	
	private Signal sysFreq = new Signal("SysF", "Hz", 50.0); 
	private Signal slackPwr = new Signal("slackPwr", "kW", 0.0);
	private Signal freq0 = new Signal("freq0", "Hz", 50.0);
	private Signal loadPwr = new Signal("loadPwr", "kW", 0.0);
	
	private double sBase = 100000.0; //kW
	
	private double fnom = 50.0;
	private double pnom = 21000*0.3;
	private double T0 = 3.0;//s
	//private double J = 2*Math.PI*fnom*T0*pnom;
	//private double Tm = 0.200;//s
	//private double Kp = 0.4;
	
	private Signal freqDependent = new Signal("FreqDepEna", "-", 0.0);
	private GridFrequencyModel genset; 	
	
	
	
	public HybridPowerPlantExample(Dataset input) throws ModelCodeException
	{
		this.inputDataset = input;
		// init Buses
		this.buses = new ArrayList<>();
		buses.add(new Bus("GRID", 	0, 	BusMode.SLACK_BUS, 	0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("53", 	1, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("52", 	2, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("51", 	3, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("5", 		4, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("4", 		5, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("3", 		6, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("2", 		7, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("1", 		8, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("9", 		9, 	BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("8", 		10, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("7", 		11, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("6", 		12, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("12",		13, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("11",		14, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("10",		15, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("18",		16, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("17",		17, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("16",		18, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("15",		19, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("14",		20, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("13",		21, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("UWL",	22, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		buses.add(new Bus("UWH/NAP",23, BusMode.PQ_BUS,		0, 0, 0, 0, 1, 0, 1));
		// Init lines
		this.lines = new ArrayList<>();
		lines.add(new Line("NAP-GRID", 	0.03311, 0.3311, 0.000000, 0.0, 23, 	0, true));
		lines.add(new Line("UWL-UWH", 	0.0200, 0.0900, 0.0012/2, 0.0, 22, 	23, true));
		
		lines.add(new Line("53-UWL", 	0.0003, 0.0338, 0.0012/2, 0.0, 1, 	22, true));
		lines.add(new Line("52-UWL", 	0.0049, 0.0456, 0.0002/2, 0.0, 2, 	22, true));
		lines.add(new Line("51-52", 	0.0074, 0.0034, 0.0011/2, 0.0, 3, 	2, true));
		// 1-5
		lines.add(new Line("1-2", 		0.0035, 0.0263, 0.0000/2, 0.0, 8, 	7, true));
		lines.add(new Line("2-3", 		0.0018, 0.0254, 0.0013/2, 0.0, 7, 	6, true));
		lines.add(new Line("3-4", 		0.0080, 0.0226, 0.0008/2, 0.0, 6, 	5, true));
		lines.add(new Line("4-5", 		0.0023, 0.0193, 0.0005/2, 0.0, 5, 	4, true));
		lines.add(new Line("5-51", 		0.0074, 0.0248, 0.0000/2, 0.0, 4, 	3, true));
		//6-9
		lines.add(new Line("6-7", 		0.0031, 0.0171, 0.0014/2, 0.0, 12, 	11, true));
		lines.add(new Line("7-8", 		0.0061, 0.0143, 0.0015/2, 0.0, 11, 	10, true));
		lines.add(new Line("8-9", 		0.0069, 0.0107, 0.0004/2, 0.0, 10, 	9, true));
		lines.add(new Line("9-51", 		0.0070, 0.0033, 0.0004/2, 0.0, 9, 	3, true));
		//10-12
		lines.add(new Line("10-11",		0.0078, 0.0371, 0.0003/2, 0.0, 15, 	14, true));
		lines.add(new Line("11-12",		0.0001, 0.0005, 0.0004/2, 0.0, 14, 	13, true));
		lines.add(new Line("12-52",		0.0083, 0.0259, 0.0004/2, 0.0, 13, 	2, true));
		//13-18
		lines.add(new Line("13-14",		0.0078, 0.0371, 0.0003/2, 0.0, 21, 	20, true));
		lines.add(new Line("14-15",		0.0001, 0.0005, 0.0004/2, 0.0, 20, 	19, true));
		lines.add(new Line("15-16",		0.0083, 0.0259, 0.0004/2, 0.0, 19, 	18, true));
		lines.add(new Line("16-17",		0.0078, 0.0371, 0.0003/2, 0.0, 18, 	17, true));
		lines.add(new Line("17-18",		0.0001, 0.0005, 0.0004/2, 0.0, 17, 	16, true));
		lines.add(new Line("18-53",		0.0083, 0.0259, 0.0004/2, 0.0, 16, 	1, true));
		
		
		this.outputDataset = new Dataset();
		this.outputDataset.addSignal(new Signal("Vtg53", "p.u.", 0.0));
		this.outputDataset.addSignal(new Signal("Vtg52", "p.u.", 0.0));
		this.outputDataset.addSignal(new Signal("Vtg51", "p.u.", 0.0));
		this.outputDataset.addSignal(new Signal("Vtg01", "p.u.", 0.0));
		this.outputDataset.addSignal(new Signal("Vtg06", "p.u.", 0.0));
		this.outputDataset.addSignal(new Signal("Vtg10", "p.u.", 0.0));
		this.outputDataset.addSignal(new Signal("Vtg13", "p.u.", 0.0));
		
		
		this.outputDataset.addSignal(new Signal("pvPwrAt", "kW", 0.0));
		
		
		try
		{
			GridFrequencyModelSettings cfg = new GridFrequencyModelSettings();
			cfg.tm.setVal(0.100); // No primary control :-S and No Droop
			cfg.droop.setVal(5.0);
			cfg.dSys.setVal(0.0);
			genset = new GridFrequencyModel(cfg, 21155*0.3, slackPwr, freq0, sysFreq);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		// We initialize THe power flow problem
		park = new PowerFlow(lines, buses);
		
		// We set up the Components
		
		// Gensets

		// PV OLD
		/*pv1 = new SimplePhotovoltaic("PV1", input.getSignals().get(2));
		pv1.setPeakPower(0.25*18400); // Input from real PV plant divided by 4
		pv1.setPwrAtMaxPc(pvPwrAtMaxPc);
		pv1.setPwrRtSetpPc(pvPwrRtSetpPc);*/
		
		// PV NEW
		//pvcConf.pwrAtPk.setVal(0.2*18400); // Input from real PV plant divided by 5
		pvcConf.setPwrAtNom(0.2*18400); // Input from real PV plant divided by 5
		pvcConf.setPwrApNom(0.2*18400); // Input from real PV plant divided by 5
		// TODO: Fix?
		//pvC1 = new ControlledPhotovoltaic("PVC1", pvPwrAtMaxPc, pvPwrRtSetpPc, input.getSignals().get(2), pvcConf);
		
		// ATTENTION! Frequency response?
		pvcConf.hzStart.setVal(50.2);		// Change to turn off :-)
		//pvC1.setFrequencyInput(sysFreq);
		
		// WT1 class
		wt1 = new SimplePhotovoltaic("WT1", input.getSignals().get(0));
		wt1.setPeakPower(2100.0); // Input from real WT
		wt1.setPwrAtMaxPc(wt1PwrAtMaxPc);
		wt1.setPwrRtSetpPc(wt1PwrRtSetpPc);
		

		// WT1 class
		wt2 = new SimplePhotovoltaic("WT2", input.getSignals().get(1));
		wt2.setPeakPower(2100.0); // Input from real WT
		wt2.setPwrAtMaxPc(wt2PwrAtMaxPc);
		wt2.setPwrRtSetpPc(wt2PwrRtSetpPc);
				
		// Battery
		batt1 = new SimpleBattery("BATT1", 2.0*9200.0, 9200.0, 9200.0, this.battPwrAtSetp,  this.battPwrRtSetp);
		this.batt1.setEnergy(1.0*9200.0);
		this.battSoC = batt1.getStatusSignals().getElementById("stgSoC");
		//this.batt1.setFrequencySignal(sysFreq, 50.0);
		
		// We set up 
		
		
		
		
	}
	
	public void setup()
	{
		// Control signals
		this.controlSignals = new ArrayList<>();
		this.controlSignals.add(freq0);
		this.controlSignals.add(pvPwrAtMaxPc);
		this.controlSignals.add(pvPwrRtSetpPc);
		this.controlSignals.add(wt1PwrAtMaxPc);
		this.controlSignals.add(wt1PwrRtSetpPc);
		this.controlSignals.add(wt2PwrAtMaxPc);
		this.controlSignals.add(wt2PwrRtSetpPc);
		this.controlSignals.add(battPwrAtSetp);
		this.controlSignals.add(battPwrRtSetp);
		this.controlSignals.add(freqDependent);
		this.controlSignals.add(loadPwr);
		
		
		//pvPwrAt = this.pv1.getStatusSignals().getElementById("PwrAt");//OLD
		pvPwrAt = this.pvC1.getStatusSignals().getElementById("PwrAt");
		wt1PwrAt = this.wt1.getStatusSignals().getElementById("PwrAt");
		wt2PwrAt = this.wt1.getStatusSignals().getElementById("PwrAt");
		battPwrAt = this.batt1.getStatusSignals().getElementById("PwrAt");
		
		//pvPwrRt = this.pv1.getStatusSignals().getElementById("PwrRt");//OLD
		pvPwrRt = this.pvC1.getStatusSignals().getElementById("PwrRt");
		wt1PwrRt = this.wt1.getStatusSignals().getElementById("PwrRt");
		wt2PwrRt = this.wt1.getStatusSignals().getElementById("PwrRt");
		battPwrRt = this.batt1.getStatusSignals().getElementById("PwrRt");
		
		this.statusSignals = new ArrayList<>();
		this.statusSignals.add(new Signal("PlantVtg", "p.u.", 1.0)); //0
		this.statusSignals.add(new Signal("PlantPwrAt", "kW", 0.0)); //1
		this.statusSignals.add(new Signal("PlantPwrRt", "kvar", 0.0)); //2
		this.statusSignals.add(new Signal("PvPwrAt", "kW", 0.0)); //3
		this.statusSignals.add(new Signal("PvPwrRt", "kvar", 0.0)); //4
		this.statusSignals.add(new Signal("Wt1PwrAt", "kW", 0.0)); //5
		this.statusSignals.add(new Signal("Wt1PwrRt", "kvar", 0.0)); //6
		this.statusSignals.add(new Signal("Wt2PwrAt", "kW", 0.0)); //7
		this.statusSignals.add(new Signal("Wt2PwrRt", "kvar", 0.0)); //8
		this.statusSignals.add(this.battPwrAt); //9 
		this.statusSignals.add(this.battPwrRt); //10
		this.statusSignals.add(this.battSoC); //11
		this.statusSignals.add(this.sysFreq); //11
		
		
		
		prevTimestamp = inputDataset.getData().get(0).getTimestamp().toInstant(ZoneOffset.UTC);
		
		System.out.println("Timestamp begin: " + prevTimestamp.toString() + "on Instant: " + prevTimestamp.toEpochMilli());
		
		System.out.printf("| YYYY-MM-DDThh:mm:ssZ ||  PCC  |   51  |   52  |   53  |   01  |   06  |   10  |   13  ||  PV  |  WT1  |  WT2  | P_PCC | Q_PCC | P_PCC | Q_PCC |\n");
	}
	
	public void run(Instant timestamp)
	{	
			
			//inputDataset.setIndex(i);
			inputDataset.setTimestampAndUpdate(timestamp, false);
			//inputDataset.updateSignals();
			
			double Ts = Math.max((timestamp.toEpochMilli() - prevTimestamp.toEpochMilli())/1000.0, 0.01);
			//pv1.computeStep(prevTimestamp.until(LocalDateTime.ofInstant(timestamp, UTC), ChronoUnit.MILLIS)/1000.0); //OLD
			try {
				pvC1.computeStep(Ts);
			} catch (ModelCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // NEW
			wt1.computeStep(Ts);
			wt2.computeStep(Ts);
			batt1.computeStep(Ts);
			
			// Update generation & Load
			double pvPwrAtGen = pvPwrAt.getValue();
			double wtPwrAtGen1 = wt1PwrAt.getValue(); 
			double wtPwrAtGen2 = wt2PwrAt.getValue();
			double battPwrAtGen = battPwrAt.getValue();
			
			double pvPwrRtGen = pvPwrRt.getValue();
			double wtPwrRtGen1 = wt1PwrRt.getValue(); 
			double wtPwrRtGen2 = wt2PwrRt.getValue();
			double battPwrRtGen = battPwrRt.getValue();
			
			this.statusSignals.get(0).setValue(buses.get(23).getVtg());
			
			this.statusSignals.get(3).setValue(pvPwrAtGen*4.0);
			this.statusSignals.get(4).setValue(pvPwrRtGen*4.0);
			
			this.statusSignals.get(5).setValue(wtPwrAtGen1*8.0);
			this.statusSignals.get(6).setValue(wtPwrRtGen1*8.0);
			
			this.statusSignals.get(7).setValue(wtPwrAtGen2*5.0);
			this.statusSignals.get(8).setValue(wtPwrRtGen2*5.0);
			
			// Battery signals directly routed :-)
			
			
			
			//System.out.println(timestamp.toString() + ", " + pvPwrAtGenNorm +  ", " + ldPwrAtGenNorm);
			
			//PV output on nodes 13, 14, 15, 16, 17
			buses.get(21).setPwrAtGen(pvPwrAtGen/sBase);
			buses.get(21).setPwrRtGen(pvPwrRtGen/sBase);
			
			buses.get(20).setPwrAtGen(pvPwrAtGen/sBase);
			buses.get(20).setPwrRtGen(pvPwrRtGen/sBase);
			
			buses.get(19).setPwrAtGen(pvPwrAtGen/sBase);
			buses.get(19).setPwrRtGen(pvPwrRtGen/sBase);
			
			buses.get(18).setPwrAtGen(pvPwrAtGen/sBase);
			buses.get(18).setPwrRtGen(pvPwrRtGen/sBase);

			buses.get(17).setPwrAtGen(pvPwrAtGen/sBase);
			buses.get(17).setPwrRtGen(pvPwrRtGen/sBase);

			
			// WT1 output on nodes 1,2,3,4,5 & 10,11,12
			buses.get(4).setPwrAtGen(wtPwrAtGen1/sBase);
			buses.get(4).setPwrRtGen(wtPwrRtGen1/sBase);
			
			buses.get(5).setPwrAtGen(wtPwrAtGen1/sBase);
			buses.get(5).setPwrRtGen(wtPwrRtGen1/sBase);
			
			buses.get(6).setPwrAtGen(wtPwrAtGen1/sBase);
			buses.get(6).setPwrRtGen(wtPwrRtGen1/sBase);
			
			buses.get(7).setPwrAtGen(wtPwrAtGen1/sBase);
			buses.get(7).setPwrRtGen(wtPwrRtGen1/sBase);

			
			buses.get(8).setPwrAtGen(wtPwrAtGen1/sBase);
			buses.get(8).setPwrRtGen(wtPwrRtGen1/sBase);
			
			buses.get(15).setPwrAtGen(wtPwrAtGen1/sBase);
			buses.get(15).setPwrRtGen(wtPwrRtGen1/sBase);
			
			buses.get(14).setPwrAtGen(wtPwrAtGen1/sBase);
			buses.get(14).setPwrRtGen(wtPwrRtGen1/sBase);
			
			buses.get(13).setPwrAtGen(wtPwrAtGen1/sBase);
			buses.get(13).setPwrRtGen(wtPwrRtGen1/sBase);
			
			// WT1 output on nodes 6,7,8,9
			buses.get(12).setPwrAtGen(wtPwrAtGen2/sBase);
			buses.get(12).setPwrRtGen(wtPwrRtGen2/sBase);
			
			buses.get(11).setPwrAtGen(wtPwrAtGen2/sBase);
			buses.get(11).setPwrRtGen(wtPwrRtGen2/sBase);
			
			buses.get(10).setPwrAtGen(wtPwrAtGen2/sBase);
			buses.get(10).setPwrRtGen(wtPwrRtGen2/sBase);
			
			
			buses.get(9).setPwrAtGen(wtPwrAtGen2/sBase);
			buses.get(9).setPwrRtGen(wtPwrRtGen2/sBase);
			

			
			// Battery on Node 18
			buses.get(14).setPwrAtGen(battPwrAtGen/sBase);
			buses.get(14).setPwrRtGen(battPwrRtGen/sBase);
			
			
			// Load on NAP
			buses.get(23).setPwrAtDem(loadPwr.getValue()/sBase);
			buses.get(23).setPwrRtDem(-449.1/sBase);
			
			// Run Power flow calculation
			
			//this.park.runGaussSeidel(lines, buses); // TODO: Old implementation marked for delete
			this.park.runGaussSeidel();
			
			Complex pwrSlack = Complex.ofCartesian(buses.get(0).getVtg(), 0.0).multiply(lines.get(0).getLineCur().conj());
			/**
			 * WORKSHOP: Use U calculated P, Q (PT1 Verhalten) or 
			 */
			Complex pwrPlant = Complex.ofCartesian(buses.get(22).getVtg(), 0.0).multiply(lines.get(1).getLineCur().conj());
			
			//this.statusSignals.get(1).setValue(-buses.get(0).getPwrAtGen()*sBase);
			this.statusSignals.get(1).setValue(-pwrPlant.getReal()*sBase);
			//this.statusSignals.get(2).setValue(-buses.get(0).getPwrRtGen()*sBase);
			this.statusSignals.get(2).setValue(-pwrPlant.getImaginary()*sBase);
			
			
			//slackPwr.setValue(this.statusSignals.get(1));
			slackPwr.setValue(-buses.get(0).getPwrAtGen()*sBase*this.freqDependent.getValue());
			
			if(this.freqDependent.getValue()<0.5)
			{
				this.genset.reset();
			}
			
			this.genset.computeStep(Ts);
			Sys.sysFreq.setValue(this.sysFreq);
			
			
			System.out.printf("| %s || %1.3f | %1.3f | %1.3f | %1.3f | %1.3f | %1.3f | %1.3f | %1.3f || % 03.1f | % 03.1f | % 03.1f | % 03.1f | % 03.1f | % 03.1f | % 03.1f |\n",
					timestamp.toString(),
					buses.get(22).getVtg(),
					buses.get(3).getVtg(),
					buses.get(2).getVtg(),
					buses.get(1).getVtg(),
					buses.get(8).getVtg(),
					buses.get(12).getVtg(),
					buses.get(15).getVtg(),
					buses.get(21).getVtg(),
					//pvPwrAtGen);
					pvPwrAtGen*4.0/1000.0, 
					wtPwrAtGen1*8.0/1000.0,
					wtPwrAtGen2*5.0/1000.0, -buses.get(0).getPwrAtGen()*sBase/1000.0, -buses.get(0).getPwrRtGen()*sBase/1000.0, pwrSlack.getReal()*sBase/1000.0, pwrSlack.getImaginary()*sBase/1000.0);

			//Put data in output dataset
			ArrayList<Float> data = new ArrayList<>();
			data.add((float)((buses.get(0).getPwrAtGen()-buses.get(0).getPwrAtDem()*0)*sBase));
			data.add((float)((buses.get(1).getPwrAtGen()-buses.get(1).getPwrAtDem()*0)*sBase));
			data.add((float)((buses.get(2).getPwrAtGen()-buses.get(2).getPwrAtDem()*0)*sBase));
			data.add((float)((buses.get(3).getPwrAtGen()-buses.get(3).getPwrAtDem()*0)*sBase));
			data.add((float)buses.get(0).getVtg());
			data.add((float)buses.get(1).getVtg());
			data.add((float)buses.get(2).getVtg());
			data.add((float)buses.get(3).getVtg());
			data.add((float)pvPwrAtGen);
			this.outputDataset.addData(LocalDateTime.ofInstant(timestamp, UTC), data);
			
			//System.out.println(timestamp.toString() + ", " + data.get(0) + ", " 
			//					+ data.get(1) + ", " + data.get(2) + ", " + data.get(3) 
			//					+ ", " + data.get(4) + ", " + data.get(5) + ", " + data.get(6) + ", " + data.get(7));
			
			//System.out.println(timestamp.toString() + ", " + lines.get(0).getLineCur().abs() + ", " 
			//		+ lines.get(1).getLineCur().abs() + ", " + lines.get(4).getLineCur().abs() + ", " + lines.get(5).getLineCur().abs());
			prevTimestamp = prevTimestamp.with(timestamp);
		
		
	}
	
	public Dataset getOutputDataset()
	{
		return this.outputDataset;
	}
	
	public void reset()
	{
		this.inputDataset.resetIndex();
		
	}
	
	public void setPvPwrAtNom(double pPv)
	{
		// TODO: implement
	}
	
	public void setWindPwrAtNom(double pWT)
	{
		// TODO: implement
	}
	
	public void setBattEgyNom(double egyTot)
	{
		// TODO: implement
	}
	
	public double getGenFuelVol()
	{
		return 0.0;
	}
	
	public double getEgyPvTot()
	{
		return 0.0;
	}
	
	public double getEgyWindTot()
	{
		return 0.0;
	}

	
	public double getEgyBatTot()
	{
		return 0.0;
	}

	public double getEgyGenTot()
	{
		return 0.0;
	}

	public double getEgyLoadTot()
	{
		return 0.0;
	}

	public List<Signal> getControlSignals()
	{
		return this.controlSignals;
	}
	
	public List<Signal> getStatusSignals()
	{
		return this.statusSignals;
	}
	
}
