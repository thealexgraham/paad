package net.alexgraham.thesis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
	    changeSupport.firePropertyChange(new PropertyChangeEvent(this, propertyName,
	                                               oldValue, newValue));
	}
	
	public void firePropertyChange(String propertyName, double oldValue, double newValue) {
		firePropertyChange(propertyName, Double.valueOf(oldValue), Double.valueOf(newValue));
	}
}
