package de.hsrt.holbox.project.powersystem;

import de.hsrt.holbox.util.Settings;

public abstract class PowerSystemComponentSettings extends Settings {

	public PowerSystemComponentSettings()
	{
		super();
	}
	
	public abstract String getId(); 
	
	public abstract String getDescriptor();
	
	public abstract PowerSystemComponentSettings getAssimilatedObject();
}
