package net.alexgraham.thesis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

public abstract class ChangeSender {
	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport (this);

	public void addPropertyChangeListener (String propertyName, PropertyChangeListener listener)
	{
	    changeSupport.addPropertyChangeListener (propertyName, listener);
	}

	public void removePropertyChangeListener (String propertyName, PropertyChangeListener listener)
	{
	    changeSupport.removePropertyChangeListener (propertyName, listener);
	}

	public void firePropertyChange (String propertyName, BigDecimal oldValue, BigDecimal newValue)
	{
	    if (oldValue != null && newValue != null && oldValue.compareTo (newValue) == 0) {
	        return;
	    }
	    changeSupport.firePropertyChange(new PropertyChangeEvent(this, propertyName,
	                                               oldValue, newValue));
	}
	
    /**
     * Reports an double bound property update to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * No event is fired if old and new values are equal.
     * <p>
     * This is merely a convenience wrapper around the more general
     * {@link #firePropertyChange(String, Object, Object)}  method.
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      the old value of the property
     * @param newValue      the new value of the property
     */
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        if (oldValue != newValue) {
            firePropertyChange(propertyName, Double.valueOf(oldValue), Double.valueOf(newValue));
        }
    }
}
