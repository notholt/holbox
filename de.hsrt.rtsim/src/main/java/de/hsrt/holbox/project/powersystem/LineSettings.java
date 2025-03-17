package de.hsrt.holbox.project.powersystem;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.*;

/**
 * Class representation of line Data input as per Master Data specifications (based on Simbench format)
 * @author notholt
 *
 */

public class LineSettings extends PowerSystemComponentSettings{
	
	@XmlElement
	protected StringParameter id;
	@XmlElement
	protected StringParameter nodeA;
	@XmlElement
	protected StringParameter nodeB;
	@XmlElement
	protected StringParameter type;
	@XmlElement
	protected DoubleParameter length;
	@XmlElement
	protected DoubleParameter maxLoad;
	@XmlElement
	protected StringParameter subnet;
	@XmlElement
	protected IntegerParameter vtgLvl;
	
	public LineSettings()
	{
		super();
		id 		= new StringParameter("ID", "Line ID", "", null);
		nodeA	= new StringParameter("nodeA", "NodeA", "", null);
		nodeB	= new StringParameter("nodeB", "NodeB", "", null);
		type 	= new StringParameter("type", "Type", "", null);
		length	= new DoubleParameter("length", "Length of line", "km", 0.0, 0.0, Double.MAX_VALUE);
		maxLoad = new DoubleParameter("maxLoad", "Maximum Load", "A", 0.0, 0.0, Double.MAX_VALUE);
		subnet 	= new StringParameter("subnet", "Subnet", "", null);
		vtgLvl	= new IntegerParameter("vtgLvl", "Voltage Level", "", 0, 0, Integer.MAX_VALUE);
		this.initMap();
	}
	
	public LineSettings(	String id, 
							String nodeA, String nodeB, 
							String type, 
							Double length, Double maxLoad, 
							String subnet, Integer vtgLvl) throws ModelCodeException
	{
		this();
		setValues(id, nodeA, nodeB, type, length, maxLoad, subnet, vtgLvl);
	}
	
	
	/**
	 * 
	 * @param id
	 * @param nodeA
	 * @param nodeB
	 * @param type
	 * @param length
	 * @param maxLoad
	 * @param subnet
	 * @param vtgLvl
	 */
	public void setValues(	String id, 
							String nodeA, String nodeB, 
							String type, 
							Double length, Double maxLoad, 
							String subnet, Integer vtgLvl) throws ModelCodeException
	{
		this.id.setValIfNotNull(id);
		this.nodeA.setValIfNotNull(nodeA);
		this.nodeB.setValIfNotNull(nodeB);
		this.type.setValIfNotNull(type);
		this.length.setValIfNotNull(length);
		this.maxLoad.setValIfNotNull(maxLoad);
		this.subnet.setValIfNotNull(subnet);
		this.vtgLvl.setValIfNotNull(vtgLvl);
	}

	@Override
	public void initMap() {
		this.parList = new ParameterList();
		this.parList.add(this.id);
		this.parList.add(this.nodeA);
		this.parList.add(this.nodeB);
		this.parList.add(this.type);
		this.parList.add(this.length);
		this.parList.add(this.maxLoad);
		this.parList.add(this.subnet);
		this.parList.add(this.vtgLvl);
	}

	@Override
	public String getId() {
		
		return id.getVal();
	}

	@Override
	public String getDescriptor() {
		return "[" + nodeA.getVal() + "] \u21A3 ["+ nodeB.getVal() + "] Type: " + type.getVal() +" Length: " + length.getVal() + "km";
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		LineSettings result = new LineSettings();
		result.assimilate(this);
		return result;
	}
	
}
