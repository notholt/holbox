package de.hsrt.holbox.project.powersystem;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.hsrt.holbox.util.DoubleParameter;
import de.hsrt.holbox.util.IntegerParameter;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.util.ParameterList;
import de.hsrt.holbox.util.StringParameter;

/**
 * Class representation of Node Data input as per Master Data specifications (based on Simbench format)
 * @author notholt
 *
 */

@XmlRootElement
public class BusSettings extends PowerSystemComponentSettings{
	
	@XmlElement
	protected StringParameter id;
	@XmlElement
	protected StringParameter pwrFlowType;
	@XmlElement
	protected StringParameter type;
	@XmlElement
	protected DoubleParameter vtgMagSetp;
	@XmlElement
	protected DoubleParameter vtgAngSetp;
	@XmlElement
	protected DoubleParameter vtgRtg;
	@XmlElement
	protected DoubleParameter vtgPuMax;
	@XmlElement
	protected DoubleParameter vtgPuMin;
	@XmlElement
	protected StringParameter coordID;
	@XmlElement
	protected StringParameter subnet;
	@XmlElement
	protected IntegerParameter vtgLvl;
	
	@XmlElement
	protected DoubleParameter pwrAtLd;
	@XmlElement
	protected DoubleParameter pwrRtLd;
	@XmlElement
	protected DoubleParameter pwrAtGen;
	@XmlElement
	protected DoubleParameter pwrRtGen;
	
	/*
	@XmlElement(name="genId")
	protected ArrayList<String> gen;
	@XmlElement(name="LdId")
	protected ArrayList<String> ld;
	*/
	
	
	public BusSettings()
	{
		super();
		id 			= new StringParameter("ID", "Node ID", "", null);
		pwrFlowType	= new StringParameter("pwrFlowType", "Node Type for Power Flow calculations", "", null);
		type 		= new StringParameter("type", "Type", "", null);
		vtgMagSetp	= new DoubleParameter("vtgMagSetp", "Voltage magnitude setpoint", "p.u.", null, 0.0, 2.0);
		vtgAngSetp	= new DoubleParameter("vtgAngSetp", "Voltage angle setpoint", "p.u.", null, -180.0, 180.0);
		vtgRtg		= new DoubleParameter("vtgRtg", "Voltage rating of node", "kV", 0.4, 0.1, 1000.0);
		vtgPuMax	= new DoubleParameter("vtgPuMax", "Maximum admisible node voltage", "p.u.", 1.2, 0.2, 2.0);
		vtgPuMin	= new DoubleParameter("vtgPuMin", "Minimum admisible node voltage", "p.u.", 0.8, 0.2, 2.0);
		coordID 	= new StringParameter("coordID", "ID from Coordinates Table", "", null);
		subnet	 	= new StringParameter("subnet", "Subnet", "", null);
		vtgLvl		= new IntegerParameter("vtgLvl", "Voltage Level", "", 0, 0, Integer.MAX_VALUE);
		
		pwrAtLd		= new DoubleParameter("pwrAtLd", "Static bus power load", "kW", 0.0, 0.0, 1000000.0);
		pwrRtLd		= new DoubleParameter("pwrRtLd", "Static bus reactive power load", "kvar", 0.0, -1000000.0, 1000000.0);
		pwrAtGen	= new DoubleParameter("pwrAtGen", "Static bus power generation", "kW", 0.0, 0.0, 1000000.0);
		pwrRtGen	= new DoubleParameter("pwrRtGen", "Static bus reactive power generation", "kvar", 0.0, -1000000.0, 1000000.0);
		
		/*
		gen			= new ArrayList<>(0);
		ld			= new ArrayList<>(0);
		*/
		this.initMap();
	}

	@Override
	public void initMap() {
		this.parList = new ParameterList();
		this.parList.add(this.id);
		this.parList.add(this.pwrFlowType);
		this.parList.add(this.type);
		this.parList.add(this.vtgMagSetp);
		this.parList.add(this.vtgAngSetp);
		this.parList.add(this.vtgRtg);
		this.parList.add(this.vtgPuMax);
		this.parList.add(this.vtgPuMin);
		this.parList.add(this.coordID);
		this.parList.add(this.subnet);
		this.parList.add(this.vtgLvl);
		this.parList.add(this.pwrAtLd);
		this.parList.add(this.pwrRtLd);
		this.parList.add(this.pwrAtGen);
		this.parList.add(this.pwrRtGen);
	}
	
	/**
	 * Sets the values of the given node. Null values mean the original values will not be overwritten
	 * @param id
	 * @param type
	 * @param vtgMagSetp
	 * @param vtgAngSetp
	 * @param vtgRtg
	 * @param vtgPuMax
	 * @param vtgPuMin
	 * @param subnet
	 * @param coordId
	 * @param vtgLvl
	 * @throws ModelCodeException 
	 */
	public void setValues(	String id, BusType bType, String type, 
							Double vtgMagSetp, Double vtgAngSetp, 
							Double vtgRtg, Double vtgPuMax, 
							Double vtgPuMin, String subnet, String coordId, Integer vtgLvl, Double pwrAtLd, Double pwrRtLd, Double pwrAtGen, Double pwrRtGen) throws ModelCodeException
	{
		this.id.setValIfNotNull(id);
		this.type.setValIfNotNull(type);
		this.pwrFlowType.setValIfNotNull(bType.toString());
		this.vtgMagSetp.setValIfNotNull(vtgMagSetp);
		this.vtgAngSetp.setValIfNotNull(vtgAngSetp);
		this.vtgRtg.setValIfNotNull(vtgRtg);
		this.vtgPuMax.setValIfNotNull(vtgPuMax);
		this.vtgPuMin.setValIfNotNull(vtgPuMin);
		this.subnet.setValIfNotNull(subnet);
		this.coordID.setValIfNotNull(coordId);
		this.vtgLvl.setValIfNotNull(vtgLvl);
		this.pwrAtLd.setValIfNotNull(pwrAtLd);
		this.pwrRtLd.setValIfNotNull(pwrRtLd);
		this.pwrAtGen.setValIfNotNull(pwrAtGen);
		this.pwrRtGen.setValIfNotNull(pwrRtGen);
	}
	
	public BusSettings(	String id, BusType bType, String type, 
			Double vtgMagSetp, Double vtgAngSetp, 
			Double vtgRtg, Double vtgPuMax, 
			Double vtgPuMin, String subnet, String coordId, Integer vtgLvl, Double pwrAtLd, Double pwrRtLd, Double pwrAtGen, Double pwrRtGen) throws ModelCodeException
	{
		this();
		this.setValues(id, bType, type,	vtgMagSetp, vtgAngSetp, vtgRtg, vtgPuMax, vtgPuMin, subnet, coordId, vtgLvl, pwrAtLd, pwrRtLd, pwrAtGen, pwrRtGen);
		this.initMap();
	}
	
	
	public String getId()
	{
		return this.id.getVal();
	}

	@Override
	public String getDescriptor() {
		return "\u2301 TYPE: [" + this.pwrFlowType.getVal() + "]  Voltage Set: |u| "
				+ vtgMagSetp.getVal() + "\u2220 " + vtgAngSetp.getVal() 
				+ "[p.u.] / PwrAtGen: " + pwrAtGen.getVal()
				+ "kW  PwrRtGen: " + pwrRtGen.getVal()
				+ "kvar  PwrAtLd: " + pwrAtLd.getVal()
				+ "kW  PwrRtLd: " + pwrRtLd.getVal()+"kvar";
	}
	
	/*public ArrayList<String> getGen()
	{
		return this.gen;
	}
	
	public ArrayList<String> getLd()
	{
		return this.ld;
	}*/

	public PowerSystemComponentSettings getAssimilatedObject()
	{
		BusSettings result = new BusSettings();
		result.assimilate(this);
		return result;
	}
}
