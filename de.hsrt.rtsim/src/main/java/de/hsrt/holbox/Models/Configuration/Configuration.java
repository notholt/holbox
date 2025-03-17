package de.hsrt.holbox.Models.Configuration;

import de.hsrt.holbox.util.ParameterList;

public abstract class Configuration {
	
	protected ParameterList	list		= new ParameterList();
	
	
	public ParameterList getParameterList() {
		return this.list;
	}

}
