package de.hsrt.holbox.util;

/**
 * Defines a configuration error like, for example, if parameters are outside the possible values.
 * @author notholt
 *
 */

public class ConfigurationException extends Exception{
	/**
	 * Serial version for UID serialization.
	 */
	private static final long serialVersionUID = 7121L;

	public ConfigurationException(String msg)
	{
		super(msg); // Currently we only throw one Exception with a string message.
	}
}
