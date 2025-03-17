package de.hsrt.holbox.project.powersystem;

import de.hsrt.holbox.util.ModelCodeException;

public enum BusType {
	PV,
	PQ,
	SLACK;
	
	public String toString()
	{
		switch(this)
		{
		case PV:
			return "PV";
		case PQ:
			return "PQ";
		case SLACK:
			return "SLACK";
		default:
			return null;
		}
	}
	
	public static BusType of(String s) throws ModelCodeException
	{
		if (s.equals("PV"))
		{
			return PV;
		} else if (s.equals("PQ"))
		{
			return PQ;
		} else if (s.equals("SLACK"))
		{
			return SLACK;
		}else {
			throw new ModelCodeException("String "+s+" is not a valid Bus mode");
			//return null;
		}
	}
	
	
}
