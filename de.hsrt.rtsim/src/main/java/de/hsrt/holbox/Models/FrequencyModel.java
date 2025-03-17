package de.hsrt.holbox.Models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.Simulatable;

@XmlSeeAlso({	GridFrequencyModel.class})

public abstract class FrequencyModel extends PowerSystemComponentSettings implements Simulatable{
	
	//@XmlElement
	protected String name = "SimpleGridFreqControl";
	
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
