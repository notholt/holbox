package de.hsrt.holbox.project;

import javax.xml.bind.annotation.XmlElement;

import de.hsrt.holbox.util.*;

public class ProjectSettings extends Settings {

	// Project data structure   
    @XmlElement
    private StringParameter			projectName;
    @XmlElement
    private StringParameter			projectDescription;
    @XmlElement
    private StringParameter			projectOwner;
    @XmlElement
    private StringParameter			projectRevision;
    @XmlElement
    private DoubleParameter			projectLatitude;
    @XmlElement
    private DoubleParameter			projectLongitude;
    @XmlElement
    private StringParameter			lastSaved;
    
    
    
	public ProjectSettings()
	{
		super();
		this.projectName 			= new StringParameter("prjName", "Project Name", null, "New Project");
		this.projectDescription 	= new StringParameter("prjDesc", "Project Description", null, "Description of the new project", 5);
		this.projectOwner 			= new StringParameter("prjOwner", "Project Owner", null, "Myself");
		this.projectRevision 		= new StringParameter("prjRev", "Project Revision", null, "0.0.E");
		this.lastSaved		 		= new StringParameter("lastUpd", "Last Saved", null, "never");
		
		this.projectLatitude		= new DoubleParameter("prjLat", "Project Latitude (N+, S-)", "°" , 21.537, -90.0, 90.0);
		this.projectLongitude		= new DoubleParameter("prjLon", "Project Longitude (E+, W-)", "°", -87.22, -180.0, 180.0);
		this.initMap();
	}
	
	
	@Override
	public void initMap()
	{
		this.parList = new ParameterList();
		this.parList.add(this.projectName);
		this.parList.add(this.projectDescription);
		this.parList.add(this.projectOwner);
		this.parList.add(this.projectRevision);
		this.parList.add(this.lastSaved);
		this.parList.add(this.projectLatitude);
		this.parList.add(this.projectLongitude);
	}


	
}
