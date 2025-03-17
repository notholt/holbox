package de.hsrt.holbox.util;

/**
 * The interface describes the common necessary functions to make a class simulatable by Holbox engine
 * @author notholt
 *
 */
public interface Simulatable {
	
	/**
	 * updates output signals based on input signals and integration time Ts
	 * @param Ts Integration/sample time
	 * @throws ModelCodeException 
	 */
	void computeStep(double Ts) throws ModelCodeException;
	
	/**
	 * Resets class to its default state
	 */
	void reset();
	
	/**
	 * Recompute is intended in the future for iterations, if required...
	 */
	void recompute();
	
	/**
	 * Gets the parameter list of the class
	 * @return
	 */
	ParameterList getParameters();
	
	/**
	 * Gets the input Signals	
	 * @return
	 */
	SignalList getInputSignals();
	
	/**
	 * Gets the output signals
	 * @return
	 */
	SignalList getOutputSignals();
	
	/**
	 * Returns the identifier of the simulatable object
	 * @return
	 */	
	String getName();
	void setName(String name);

}
