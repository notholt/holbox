package de.hsrt.holbox.project.dataset;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.*;

public class DatasetSettings extends Settings {

	// Project data structure   
    @XmlElement
    protected FileParameter		resourceFile;
    @XmlElement
    protected IntegerParameter	importFromLine;
    @XmlElement
    protected StringParameter	colDelimiter;
    @XmlElement
    protected StringParameter	decDelimiter;
    @XmlElement
    protected StringParameter	timeFormat;

    
    
    
	public DatasetSettings()
	{
		this.resourceFile 			= new FileParameter("resFile", "Input dataset file", null, "...");
		this.importFromLine 		= new IntegerParameter("impResFromLine", "Begin import on line (incl. column header)", "", 1 , 1, 65535);
		this.colDelimiter			= new StringParameter("colDelimiter", "Column delimiter", "", ",");
		this.decDelimiter			= new StringParameter("decDelimiter", "Decimal delimiter", "", ".");
		this.timeFormat				= new StringParameter("timeFormat", "Time format (s. Documentation)", "", "YYYYmmDD:HHMM");

		this.initMap();
	}
	
	
	public void initMap()
	{
		this.parList = new ParameterList();
		this.parList.add(this.resourceFile);
		this.parList.add(this.importFromLine);
		this.parList.add(this.colDelimiter);
		this.parList.add(this.decDelimiter);
		this.parList.add(this.timeFormat);
	}


	
}
