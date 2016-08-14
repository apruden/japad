package com.monolito.japad;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 */
public class SketchModel {

    private PropertyChangeSupport propertyChangeSupport;
    private List<Object> watches;
    private String source;

    /**
     * 
     */
    public SketchModel() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.watches = new ArrayList<Object>();
        this.setSource("");
    }

    /**
     * 
     * @param obj
     */
    public void addWatch(Object obj) {
        this.watches.add(obj);
        this.propertyChangeSupport.firePropertyChange("watches", null,
                this.watches);
    }

    /**
	 * 
	 */
    public void clearWatches() {
        this.watches.clear();
        this.propertyChangeSupport.firePropertyChange("watches", null,
                this.watches);
    }

    /**
     * 
     * @return
     */
    public String getSource() {
        return source;
    }

    /**
     * 
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 
     * @return
     */
    public List<Object> getWatches() {
        return this.watches;
    }

    /**
     * 
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * 
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * 
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }
}