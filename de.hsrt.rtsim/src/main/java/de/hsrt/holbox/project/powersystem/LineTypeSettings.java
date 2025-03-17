package de.hsrt.holbox.project.powersystem;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.*;

/**
 * Class representation of line Data input as per Master Data specifications (based on Simbench format)
 * @author notholt
 *
 */

public class LineTypeSettings extends PowerSystemComponentSettings{
	
	@XmlElement
	protected StringParameter id;
	@XmlElement
	protected DoubleParameter r;
	@XmlElement
	protected DoubleParameter x;
	@XmlElement
	protected DoubleParameter y2;
	@XmlElement
	protected DoubleParameter lineChar;
	
	public LineTypeSettings()
	{
		super();
		id 		= new StringParameter("ID", "Line ID", "", null);
		r		= new DoubleParameter("r'", "Resistance per unit length", "ohm/km", 0.01, 0.0001, Double.MAX_VALUE);
		x		= new DoubleParameter("x'", "Reactance per unit length at nominal frequency", "ohm/km", 0.01, 0.0001, Double.MAX_VALUE);
		y2		= new DoubleParameter("y2'", "Capactitance, parameter Y/2 of the Pi Equivalent", "ohm/km", 0.01, 0.0001, Double.MAX_VALUE);
		lineChar= new DoubleParameter("lineChar", "Line charging", "Mvar/km", 0.1, 0.0001, Double.MAX_VALUE);
		this.initMap();
	}
	
	public LineTypeSettings(	String id, 
								Double r, Double x, Double y2, Double lineChar) throws ModelCodeException
	{
		this();
		setValues(id, r, x, y2, lineChar);
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
							Double r, Double x, Double y2, Double lineChar) throws ModelCodeException
	{
		this.id.setValIfNotNull(id);
		this.r.setValIfNotNull(r);
		this.x.setValIfNotNull(x);
		this.y2.setValIfNotNull(y2);
		this.lineChar.setValIfNotNull(lineChar);
	}


	@Override
	public String getId() {
		
		return id.getVal();
	}

	@Override
	public void initMap() {
		// TODO Auto-generated method stub
		this.parList = new ParameterList();
		this.parList.add(id);
		this.parList.add(r);
		this.parList.add(x);
		this.parList.add(y2);
		this.parList.add(lineChar);
	}

	@Override
	public String getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public PowerSystemComponentSettings getAssimilatedObject()
	{
		LineTypeSettings result = new LineTypeSettings();
		result.assimilate(this);
		return result;
	}
	
}
