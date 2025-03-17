package de.hsrt.holbox.Models;

public enum EqClass {
	
	PV("PV"),
	WIND("WIND"),
	STOR("STORAGE"),
	GEN("GENSET"),
	LOAD("LOAD"),
	UNDEFINED("UNDEFINED");
	
	String name;
	
	private EqClass(String s)
	{
		name = s;
	}
	
	public String toString()
	{
		return name;
	}
	
	public EqClass ofString(String s)
	{
		if(s.contentEquals(PV.toString()))	{return PV;}
		if(s.contentEquals(WIND.toString())){return WIND;}
		if(s.contentEquals(STOR.toString())){return STOR;}
		if(s.contentEquals(GEN.toString()))	{return GEN;}
		if(s.contentEquals(LOAD.toString())){return LOAD;}
		return UNDEFINED;
	}

}
