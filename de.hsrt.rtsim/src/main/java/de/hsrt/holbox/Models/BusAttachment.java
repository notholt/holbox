package de.hsrt.holbox.Models;

import javax.xml.bind.annotation.XmlAttribute;
import de.hsrt.holbox.ClassicPowerFlow.Bus;

/**
 * This class describes simple bus attachments
 * 
 * @author notholt
 *
 */

//@XmlRootElement
public class BusAttachment {

	@XmlAttribute(name="id")
	public String id = null;
	@XmlAttribute(name="proportion")
	public Double scale = 1.0;
	@XmlAttribute(name="attachedAs")
	public Type as = Type.GEN;

	public enum Type{
		GEN, LOAD
	}
	
	public Bus bus;
		
	public BusAttachment()
	{
		this("", 1.0, Type.GEN);
	}
	
	public BusAttachment(String id, double scale, Type as)
	{
		this.id = id;
		this.scale = scale;
		this.as = as;
	}
}
