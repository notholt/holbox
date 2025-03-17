package de.hsrt.holbox.util;

/**
 * Defines an error in the model, for example, when configuring new parameters.
 * @author notholt
 *
 */

public class ModelCodeException extends Exception {

	/**
	 * Serial version for UID serialization.
	 */
	private static final long serialVersionUID = 7121L;

	public ModelCodeException(String msg)
	{
		super(msg); // Currently we only throw one Exception with a string message.
	}
	
}
