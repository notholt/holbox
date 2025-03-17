package de.hsrt.holbox.project.powersystem;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.*;

/**
 * Class representation of line Data input as per Master Data specifications (based on Simbench format)
 * @author notholt
 *
 */

public class TransformerSettings extends Settings{
	
	@XmlElement
	protected StringParameter id;
	@XmlElement
	protected StringParameter nodeA;
	@XmlElement
	protected StringParameter nodeB;
	@XmlElement
	protected StringParameter type;
	@XmlElement
	protected DoubleParameter ratioAtoB;
	@XmlElement
	protected DoubleParameter seriesR;
	@XmlElement
	protected DoubleParameter seriesX;
	@XmlElement
	protected DoubleParameter parallelR;
	@XmlElement
	protected DoubleParameter parallelX;
	
	public TransformerSettings()
	{
		super();
		id 			= new StringParameter("ID", "Line ID", "", null);
		nodeA		= new StringParameter("nodeA", "NodeA", "", null);
		nodeB		= new StringParameter("nodeB", "NodeB", "", null);
		type 		= new StringParameter("type", "Type", "", null);
		ratioAtoB 	= new DoubleParameter("ratioAtoB", "Transformer Ratio a to b", "--", 1.0, 0.001, 1000.0);
		seriesR		= new DoubleParameter("seriesR", "Series resistance of Transformer", "p.u.", 0.01, 0.001, 10.0);
		seriesX		= new DoubleParameter("seriesX", "Series reactance of Transformer", "p.u.", 0.10, 0.001, 10.0);
		parallelR 	= new DoubleParameter("parallelR", "Parallel resistance (O.C. losses)", "p.u.", 10.0, 0.1, 1000.0);
		parallelX	= new DoubleParameter("parallelX", "Parallel reactance", "p.u.", 10.0, 0.1, 1000.0);
		this.initMap();
	}

	//@Override
	public void initMap() {
		// TODO Auto-generated method stub
		
	}
	
	
}
