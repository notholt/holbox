/*
 * The Holbox Project
 */
package de.hsrt.holbox.project;

import java.io.File;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import de.hsrt.holbox.Economics.EconomicSettings;
import de.hsrt.holbox.project.dataset.DatasetSettings;
import de.hsrt.holbox.project.powersystem.PowerSystemSettings;
import de.hsrt.holbox.util.Dataset;
import de.hsrt.holbox.util.Log;

/**
 * The class project is the main data model used in the analysis and simulation
 * it is like a folder containing all information on a sizing project.
 * @author notholt
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "project")
public class Project {
	
    /**
     *  If the project has been saved or opened, this variable keeps the file name. If this is a new project the name is ""
     *  to prompt for a save as method. 
     */
	private File refFile;
    
    // Version of data model
	@XmlAttribute
	private int version	= 0;
	
	// Project Boundary conditions
    @XmlElement
    private ProjectSettings         projectSettings;
    @XmlElement
    private DatasetSettings			datasetSettings;
    @XmlElement
    private EconomicSettings		economicSettings;
    @XmlElement
    private PowerSystemSettings		powerSystemSettings;
    
    private Dataset					inputDataSet;  //TODO: Remove
    
 
    
    public Project()
    {
        // Initialize Properties
       
    	
        projectSettings		= new ProjectSettings();
        datasetSettings		= new DatasetSettings();
        economicSettings	= new EconomicSettings();
        powerSystemSettings = new PowerSystemSettings();
        
        //inputDataSet		= new Dataset();
        

    }
    

    //@XmlElement(name="project_settings")
    public  ProjectSettings getProjectSettings()
    {
        return this.projectSettings;
    }
    
    public  DatasetSettings getDatasetSettings()
    {
        return this.datasetSettings;
    }
    
    public  EconomicSettings getEconomicSettings()
    {
        return this.economicSettings;
    }
    
    public  PowerSystemSettings getPowerSystemSettings()
    {
        return this.powerSystemSettings;
    }
    
    public Dataset getInputDataset()
    {
    	return this.inputDataSet;
    }
    
    public void setInputDataset(Dataset ds)
    {
    	this.inputDataSet = ds;
    }
    
    
    public void saveXML(String filename)
    {
    	
        try
        {
        	this.projectSettings.getParameters().getElementById("lastUpd").setVal(LocalDateTime.now().toString());
            JAXBContext context = JAXBContext.newInstance(Project.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File f = new File(filename);
            m.marshal(this, f);
            Log.print(Log.Lvl.INFO,"Saved file "+f.getName());
            //System.out.println("[I] Saved file "+f.getName());
        }catch(Exception e)
        {
        	e.printStackTrace();
        	Log.print(Log.Lvl.ERROR,"Error writing file <"+ e +">");
            //System.out.println("[E] Error writing file <"+ e +">");
        }
    }
    
    public void readXML(String filename)
    {
        Project p = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(Project.class);
            Unmarshaller u = jc.createUnmarshaller();
            File f = new File(filename);
            this.refFile = f;
            p = (Project) u.unmarshal(f);

        } catch (Exception e) {
            e.printStackTrace();
            Log.print(Log.Lvl.ERROR,"An error occured while parsing XML file! Please make sure you have selected the correct file.");
        	//System.out.println("[E] An error occured while parsing XML file! Please make sure you have selected the correct file.");
        }
        if(p != null)
        {
            this.assimilate(p);
        }
    }
    
    public void setFile(File f)
    {
    	this.refFile = f;
    }
    
    public File getFile()
    {
    	return this.refFile;
    }
    
    public void saveXML(File f)
    {
    	saveXML(f.getAbsolutePath());
    }
    
    public void saveXML()
    {
    	if(this.refFile != null)
    	{
    		saveXML(this.refFile);
    	}
    	else
    	{
    		throw new RuntimeException("File not set in the project");
    	}
    	
    }
    
    
    /**
     * The assimilate method populates the current project with the settings of the XML file. 
     * @param p
     */
    
    private void assimilate(Project p)
    {
       // The way to go is to just call assimilate of the child data structures
    	this.projectSettings.assimilate(p.projectSettings);
    	this.datasetSettings.assimilate(p.datasetSettings);
    	this.economicSettings.assimilate(p.economicSettings);
    	this.powerSystemSettings.assimilate(p.powerSystemSettings);
    }
    
    /**
     * Resets this project to its default values (to prevent making dozens of new Project objects)
     */
    public void resetProjectToDefaults()
    {
    	this.refFile = null;
    	this.projectSettings.reset();
    }

    public boolean comparable(Project p2)
    {
    	boolean comparable = true;
    	
    	// Check if settings are comparable
    	comparable &= this.projectSettings.comparable(p2.projectSettings);
    	
    	comparable &= this.datasetSettings.comparable(p2.datasetSettings);
    	
    	comparable &= this.economicSettings.comparable(p2.economicSettings);
    	
    	comparable &= this.powerSystemSettings.comparable(p2.powerSystemSettings);
    	
    	return comparable;
    }
    
}


