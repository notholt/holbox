package de.hsrt.holbox.util;

/**
 * Interface defining a ChangeListener for bidirectional Binding but independent from the JavaFX implementation
 * @author notholt
 *
 */

public interface ChangeListener<T> {
	
	void changed(T newValue);

}
