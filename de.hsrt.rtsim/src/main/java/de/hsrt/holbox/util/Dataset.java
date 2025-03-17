/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsrt.holbox.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author notholt
 */
public class Dataset {
    protected final List<DataPoint> data  = new ArrayList<>();
    protected final SignalList signals  = new SignalList();
    //protected int data_width = 0;
    protected LocalDateTime tsBegin = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
    protected LocalDateTime tsEnd = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
    protected boolean valid = false;
    protected int index = -1;
    protected Instant currentInstant;
    
    public Dataset()
    {
    }
    
    public SignalList getSignals()
    {
        return this.signals;
    }
    
    public List<DataPoint> getData()
    {
        return this.data;
    }
    
    public int size()
    {
    	return this.data.size();
    }
    
   
    
    public DataPoint getCurrent()
    {
        return this.data.get(index);
    }
    
    public void advance()
    {
        index++;
    }
    
    
    public DataPoint getNext()
    {
        if(index >= 0 && index < data.size())
        {
            return data.get(index++);
        }
        else
        {
            throw(new IndexOutOfBoundsException("Out of bounds!!"));
        }
    }
    
    public void setBegin(LocalDateTime dt)
    {
        this.tsBegin = LocalDateTime.ofInstant(dt.toInstant(ZoneOffset.UTC),ZoneOffset.UTC);
    }
    
    public void addData(LocalDateTime dt, ArrayList<Float> data)
    {
        
        DataPoint dp = new DataPoint(dt.toInstant(ZoneOffset.UTC), data);
        this.data.add(dp);
        if(dt.toInstant(ZoneOffset.UTC).isAfter(tsEnd.toInstant(ZoneOffset.UTC)))
        {
            tsEnd = LocalDateTime.ofInstant(dt.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        }
    }
    
    public void addData(Instant i, ArrayList<Float> data)
    {
        
        DataPoint dp = new DataPoint(i, data);
        this.data.add(dp);
        if(i.isAfter(tsEnd.toInstant(ZoneOffset.UTC)))
        {
            tsEnd = LocalDateTime.ofInstant(i, ZoneOffset.UTC);
        }
    }
    
    /**
     * This function just gets the Signal information and creates a new data point at the given DateTime 
     */
    public void logSignals(LocalDateTime dt)
    {
    	ArrayList<Float> newData = new ArrayList<>(0);
    	for(Signal s: signals.getMap().values())
    	{
    		newData.add((float)s.getValue());
    	}
    	addData(dt, newData);
    }
    
    public void setIndex(int idx)
    {
        if(idx >= 0 && idx < data.size())
        {
            index = idx;
        }
        else
        {
            throw(new IndexOutOfBoundsException("Out of bounds!!"));
        }
    }
    
    public boolean updateSignals()
    {
        if(index >= 0 && index < data.size() && this.signals.size()>0)
        {
            DataPoint currentData = data.get(index);
            currentInstant = LocalDateTime.ofEpochSecond(currentData.timestamp, 0, ZoneOffset.UTC).toInstant(ZoneOffset.UTC);
            
            int i=0;
            for(Signal signal : this.signals.getMap().values())
            {
            	signal.updateValue(currentData.data.get(i), currentInstant);
            	i++;
            }
            /*
            for(int i=0; i<this.signals.size(); i++)
            {
                signals.get(i).updateValue(currentData.data.get(i), currentInstant);
            }*/
            return true;
        }
        else
        {
            throw(new IndexOutOfBoundsException("Out of bounds!!"));
        }
    }
    
    public void resetIndex()
    {
        index = 0;
    }
    
    public Instant getTime()
    {
        return Instant.ofEpochMilli(data.get(index).timestamp);
    }
    
    public void addSignal(Signal signal)
    {
    	this.signals.add(signal);
    }
    
    public void addSignalWithPrefix(Signal signal, String prefix)
    {
    	this.signals.add(signal, prefix);
    }
    
    public Instant getStartTime()
    {
    	return Instant.ofEpochSecond(data.get(0).timestamp);
    }
    
    public void setTimestampAndUpdate(Instant timestamp, boolean interpolate) throws IndexOutOfBoundsException
    {
    	/*TODO: Interpolate algorithm*/
    	boolean found = false;
    	// Debug
    	//System.out.println("Asked: " + timestamp.toEpochMilli() + " Beginning: " + data.get(0).timestamp);
    	// Scan from current index
    	for (int i =  0; i < this.size(); i++)
    	{
    		if(data.get(i).timestamp >= timestamp.toEpochMilli())
    		{
    			found = true;
    			this.setIndex(i);
    			break;
    		}
    	}
    	if(found)
    	{
    		this.updateSignals();
    	}else
    	{
    		throw new IndexOutOfBoundsException("timestamp not found");
    	}
    }
    

    public void addFromSignals(Instant i)
    {
    	ArrayList<Float> temp = new ArrayList<>(1);
    	for(Signal s : this.signals.getMap().values()) 
    	{
    		temp.add((float)s.Value);
    	}
    	this.addData(i, temp);
    }
    
    
}
