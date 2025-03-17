package de.hsrt.holbox.project.powersystem;
/**
 * The PowerFlowFactory class generates either a power flow problem or the list of buses and lines as required by the PowerFlow class.
 * @author notholt
 *
 */

import java.util.ArrayList;

import de.hsrt.holbox.ClassicPowerFlow.Bus;
import de.hsrt.holbox.ClassicPowerFlow.BusMode;
import de.hsrt.holbox.ClassicPowerFlow.Line;
import de.hsrt.holbox.ClassicPowerFlow.PowerFlow;
import de.hsrt.holbox.ClassicPowerFlow.Sys;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.StringParameter;

public class PowerFlowFactory {
	
	private ArrayList<Bus> buses;
	private ArrayList<Line> lines;
	private Bus 			slackBus;
	
	private PowerSystemSettings settings;
	private PowerFlow pf;
	
	private double baseMVA = 150.0;
	
	boolean foundSlackBus = false;
	
	public PowerFlowFactory(ArrayList<Line> sysLines, ArrayList<Bus> sysBuses, double baseMVA)
	{
		this.buses = sysBuses;
		this.lines = sysLines;
		this.baseMVA = baseMVA;
		this.pf = new PowerFlow(lines, buses);
	}
	
	public PowerFlowFactory(PowerSystemSettings s)
	{
		this.settings = s;
		
		// Set base apparent power
		this.baseMVA = (s.baseMVA.getVal() != 0.0 ? s.baseMVA.getVal(): 1.0);
		
		// Compute Buses and Lines
		try {
			computeBusList();
			computeLineList();
			pf = new PowerFlow(lines, buses);
		}catch(Exception e)
		{
			System.out.println("Error initializing PowerFlowFactory: " + e.getMessage());
		}
	}
	
	public ArrayList<Bus> getBuses()
	{
		return this.buses;
	}
	
	public ArrayList<Line> getLines()
	{
		return this.lines;
	}
	
	private void computeBusList() throws ModelCodeException
	{
		
		this.buses = new ArrayList<>(1);
		// Iterate through all buses
		settings.buses.getMap().forEach((id, bus)->{
			
			
			Bus convertedBus = null;
			// STEP 0: We try to convert the bus settings to something we actually can use.
			try
			{
				convertedBus = convertToBus(bus);
			}catch(Exception e)
			{
				System.out.println("[E] Error converting to bus structure with error: " + e.getMessage());
			}
			
			// STEP 1: If Bus is slack bus, we put it at the beginning
			if(bus.pwrFlowType.getVal().equals(BusType.SLACK.toString()))
			{
				this.buses.add(0, convertedBus);
				this.slackBus = convertedBus;
				foundSlackBus = true;
			}else
			{
				this.buses.add(convertedBus);
			}
		});
		
		// STEP 3: Reindex the Buslist
		
		for(int busNo = 0; busNo < this.buses.size(); busNo++ )
		{
			this.buses.get(busNo).setBusNo(busNo);
		}
		
		// STEP 4: If no slack bus found then error
		if(!foundSlackBus) {throw new ModelCodeException("Power System description has no slack bus!");}
	}
	
	
	private void computeLineList() throws ModelCodeException
	{
		
		this.lines = new ArrayList<>(1);
		// Iterate through all lines
		
		for(LineSettings line : this.settings.lines.getMap().values())
		{
			// STEP 0: We try to convert the line settings to something we actually can use.
			Line convertedLine = convertToLine(line);
			// STEP 1: We add the line to the array
			this.lines.add(convertedLine);
		}
	}
	
	private Bus convertToBus(BusSettings in) throws ModelCodeException
	{
		
		return new Bus(	in.id.getVal(), 														// Bus ID
						-1,																		// Bus number not yet known
						BusMode.of(in.pwrFlowType.getVal()),									// BusMode as parsed
						(in.pwrAtGen.getVal() == null ? 0.0 : 0.001*in.pwrAtGen.getVal()/baseMVA),	// P generated if null, we set 0
						(in.pwrRtGen.getVal() == null ? 0.0 : 0.001*in.pwrRtGen.getVal()/baseMVA),
						(in.pwrAtLd.getVal() == null ? 0.0 : 0.001*in.pwrAtLd.getVal()/baseMVA),
						(in.pwrRtLd.getVal() == null ? 0.0 : 0.001*in.pwrRtLd.getVal()/baseMVA),
						(in.vtgMagSetp.getVal() == null ? 1.0 : in.vtgMagSetp.getVal()),
						(in.vtgAngSetp.getVal() == null ? 0.0 : in.vtgAngSetp.getVal()),
						(in.vtgMagSetp.getVal() == null ? 1.0 : in.vtgMagSetp.getVal())//,
						//in.vtgRtg
						);
	}
	
	private Line convertToLine(LineSettings in) throws ModelCodeException
	{
		
		int nodeA = getNodeNumber(in.nodeA);
		int nodeB = getNodeNumber(in.nodeB);
		//double vtgNom = this.buses.get(nodeA).getBaseVtg();
		// If Sascha does not use the rated voltage, we get it from the bus settings directly
		double vtgNom = settings.buses.getElementById(this.buses.get(nodeA).getBusID()).vtgRtg.getVal();
		if (vtgNom == 0.0) {throw new ModelCodeException("Nominal voltage may not be 0.0");}
		double Zbase = vtgNom*vtgNom/this.baseMVA;
		double length = in.length.getVal();
		
		LineTypeSettings lt = settings.linetypes.getElementById(in.type.getVal());
		
		double puR = lt.r.getVal()*length/Zbase;
		double puX = lt.x.getVal()*length/Zbase;
		double puY2 = lt.y2.getVal()*length/Zbase;
		double lineChar = lt.lineChar.getVal()*length/this.baseMVA;
		
		
		// Right now we do not handle transformers. Anyway transformers will get their own method.
		return new Line(in.id.getVal(), 		// Line ID
						puR,					// R in pu
						puX,					// X in pu
						puY2,					// Y05 in pu
						lineChar,					// Line charging in p.u.
						nodeA,					// bus No. 1
						nodeB,					// Bus No. 2
						true,					// always true (LineIs)
						false,					// No transformer here
						1.0,					// Irrelevant since no transformer
						true					// Irrelevant, no transformer
						);
	}
	
	private int getNodeNumber(StringParameter node) throws ModelCodeException
	{
		String nodeID = node.getVal();
		for(Bus b : this.buses)
		{
			if(nodeID.equals(b.getBusID()))
			{
				return b.getBusNo();
			}
		}
		throw new ModelCodeException("Couldn't find nodeID: "+nodeID);
	}
	
	public PowerFlow getPowerFlow()
	{
		return this.pf;
	}
	
	public void runPowerFlow()
	{
		//this.pf.runGaussSeidel(lines, buses); // TODO: Old implementation, marked for delete
		this.pf.runGaussSeidel();
	}
	
	public void printResults()
	{
		
		System.out.println("                                 +-----------------+-----------------+-----------------+");
		System.out.println("                                 |  Generation     |  Demand         |  Scheduled      |");
		System.out.println("+------+--------+-------+--------+--------+--------+--------+--------+--------+--------+");
		System.out.println("| Node |  Type  |  |V|  |  angle |    P   |    Q   |    P   |    Q   |    P   |    Q   |");
		System.out.println("+------+--------+-------+--------+--------+--------+--------+--------+--------+--------+");
		for(int i=0; i<this.buses.size(); i++)
		{
			Bus b = this.buses.get(i);
			System.out.printf( "|  %03d |  %5s | %1.3f | %6.3f | %6.3f | %6.3f | %6.3f | %6.3f | %6.3f | %6.3f |\n", 	b.getBusNo(),
																					b.getBusType().toString(),
																					b.getVtg(),
																					inDeg(b.getAng()),
																					b.getPwrAtGen(),
																					b.getPwrRtGen(),
																					b.getPwrAtDem(),
																					b.getPwrRtDem(),
																					b.getPwrAtSch(),
																					b.getPwrRtSch());
		}
		System.out.println("+------+--------+-------+--------+--------+--------+--------+--------+--------+--------+");
	}
	
	public static double inDeg(double rad)
	{
		return rad*360.0/(2*Math.PI);
	}
	
	public Bus getSlackBus()
	{
		return this.slackBus;
	}
	
	public void resetPowerSystemPowers()
	{
		for(Bus b : this.buses)
		{
			BusSettings bs = this.settings.getBuses().getElementById(b.getBusID());
			b.setPwrAtGen((double)bs.getParameters().getElementById("pwrAtGen").getVal()/(1000.0*Sys.baseMVA));
			b.setPwrRtGen((double)bs.getParameters().getElementById("pwrRtGen").getVal()/(1000.0*Sys.baseMVA));
			b.setPwrAtDem((double)bs.getParameters().getElementById("pwrAtLd").getVal()/(1000.0*Sys.baseMVA));
			b.setPwrRtDem((double)bs.getParameters().getElementById("pwrRtLd").getVal()/(1000.0*Sys.baseMVA));
		}
	}
	
	public double getPwrAtLoss()
	{
		double pwrAtGen = 0.0;
		double pwrAtLd = 0.0;
		for(int i=1; i<this.buses.size(); i++)
		{
			Bus b = this.buses.get(i);
			pwrAtGen += b.getPwrAtGen();
			pwrAtLd += b.getPwrAtDem();
		}
		
		double pwrAtLoss = this.buses.get(0).getPwrAtGen() - (pwrAtLd + this.buses.get(0).getPwrAtDem() - pwrAtGen);
		//System.out.printf("SlackPwrAt: %5.3f, pwrAtGen: %5.3f, pwrAtLd: %5.3f, pwrAtLoss %5.3f\n", this.buses.get(0).getPwrAtGen(), pwrAtGen, pwrAtLd, pwrAtLoss);
		return pwrAtLoss * Sys.baseMVA * 1000.0;
	}
	
}
