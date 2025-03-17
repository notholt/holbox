package de.hsrt.holbox.ClassicPowerFlow;

import de.hsrt.holbox.util.ModelCodeException;

//this enum speaks for itself:)

public enum BusMode {

	SLACK_BUS,
	PQ_BUS,
	PV_BUS;
	
	
	public String toString()
	{
		switch(this)
		{
		case PV_BUS:
			return "PV";
		case PQ_BUS:
			return "PQ";
		case SLACK_BUS:
			return "SLACK";
		default:
			return null;
		}
	}
	
	public static BusMode of(String s) throws ModelCodeException
	{
		if (s.equals("PV"))
		{
			return PV_BUS;
		} else if (s.equals("PQ"))
		{
			return PQ_BUS;
		} else if (s.equals("SLACK"))
		{
			return SLACK_BUS;
		}else {
			throw new ModelCodeException("String "+s+" is not a valid Bus mode");
			//return null;
		}
	}
	
}
