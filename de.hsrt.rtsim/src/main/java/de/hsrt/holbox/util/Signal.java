package de.hsrt.holbox.util;


import java.time.Instant;




/**
 * Represents a signal conveyed by any object
 * @author notholt
 * @version 1.1
 * @created 03-May-2023 10:27:14
 */
public class Signal {

	protected Simulatable parent;
	protected Instant LastUpd = Instant.ofEpochMilli(0);
	protected String Name = "N/A";
    protected Double linkedvar;
    protected double inputScaling = 1.0;
    protected boolean logged = false;
    protected double min = Double.NEGATIVE_INFINITY;
    protected double max = Double.POSITIVE_INFINITY;
    
	/**
	 * ENUM if SignalValue is not valid (should be ignored)
	 */
	public static final char SIGNAL_STT_INVALID = 1;
	public static final char SIGNAL_STT_OOB_MAX = 2;
	public static final char SIGNAL_STT_OOB_MIN = 4;
	/**
	 * Status of the signal (invalid, too old, etc.) 0 equals, the signal is alive and
	 * valid
	 */
	protected char Stt = 255;
	protected String Unit = "N/A";
	protected double Value;

        public Signal(String name, String unit, double defVal, Double varaddr){
            this.Name = name;
            this.Unit = unit;
            this.Value = defVal;
            this.linkedvar = varaddr;
	}
        
     public Signal(String name, String unit, double defVal, boolean isLogged){
            this.Name = name;
            this.Unit = unit;
            this.Value = defVal;
            this.linkedvar = null;
            this.logged = isLogged;
	}
        
     public Signal(String name, String unit, double defVal){
            this(name, unit, defVal, false);
	}

   	public String getVarName(){
    		return Name;
    	}


	public String getUnit(){
		return this.Unit;
	}

	/**
	 * Status of the signal (invalid, too old, etc.) 0 equals, the signal is alive and
	 * valid
        * @return Status of the signal
	 */
	public char getStt(){
		return Stt;
	}

	public double getValue(){
		return Value;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setName(String newVal){
		Name = newVal;
	}

	/**
	 * Status of the signal (invalid, too old, etc.) 0 equals, the signal is alive and
	 * valid
	 * 
	 * @param newVal
	 */
	private void setStt(char newVal){
		Stt = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setValue(double newVal){
		Value = Math.max(Math.min(newVal*this.inputScaling, this.max), this.min);
	}
	
	/**
	 * This method takes the value of a given signal and assigns it to this signal
	 * @param newVal
	 */
	public void setValue(Signal newVal){
		Value =  Math.max(Math.min(newVal.getValue()*this.inputScaling, this.max), this.min);
	}
	
	/**
	 * 
	 * @param newVal
	 */
	public void addValue(double newVal){
		Value += newVal*this.inputScaling;
	}

	/**
	 * @param newVal
	 * @param clock
	 */
	public void updateValue(double newVal, Instant when){
		Value = newVal;
        LastUpd = LastUpd.with(when);
        // If value is linked, updates the linked value
        if(this.linkedvar != null)
        {
            this.linkedvar = newVal;
        }
	}
        
    public static Signal copyOf(Signal from)
    {
        return new Signal(from.Name, from.Unit, from.Value);
    }
    
    public void setInputScaling(double s)
    {
    	this.inputScaling = s;
    }
    
    public double getInputScaling()
    {
    	return this.inputScaling;
    }
    
    public void setLogged(boolean logged)
    {
    	this.logged = logged;
    }
    
    public boolean isLogged()
    {
    	return this.logged;
    }
    
    public void setInputSaturation(Double min, Double max )
    {
    	if(min != null)
    	{
    		this.min = min;
    	}
    	if(max != null)
    	{
    		this.max = max;
    	}
    }
        
}//end Signal