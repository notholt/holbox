package de.hsrt.holbox.project.powersystem;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.*;

/**
 * Class representation of line Data input as per Master Data specifications (based on Simbench format)
 * @author notholt
 *
 */

public class SwitchSettings extends Settings {
	
	@XmlElement
	protected StringParameter id;
	@XmlElement
	protected StringParameter nodeA;
	@XmlElement
	protected StringParameter nodeB;
	@XmlElement
	protected StringParameter type;
	@XmlElement
	protected DoubleParameter maxLoad;
	@XmlElement
	protected StringParameter subnet;
	@XmlElement
	protected IntegerParameter vtgLvl;
	@XmlElement
	protected BooleanParameter isClosed;
	
	public SwitchSettings()
	{
		id 		= new StringParameter("ID", "Line ID", "", null);
		nodeA	= new StringParameter("nodeA", "NodeA", "", null);
		nodeB	= new StringParameter("nodeB", "NodeB", "", null);
		type 	= new StringParameter("type", "Type", "", null);
		maxLoad = new DoubleParameter("maxLoad", "Maximum Load", "A", 0.0, 0.0, Double.MAX_VALUE);
		subnet 	= new StringParameter("subnet", "Subnet", "", null);
		vtgLvl	= new IntegerParameter("vtgLvl", "Voltage Level", "", 0, 0, Integer.MAX_VALUE);
		isClosed = new BooleanParameter("isClosed", "Deafult position of switch", "-", true);
	}

	//@Override
	public void initMap() {
		// TODO Auto-generated method stub
		
	}
	
	
}
