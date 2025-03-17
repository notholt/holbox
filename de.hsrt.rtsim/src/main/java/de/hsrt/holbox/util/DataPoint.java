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
import java.util.Locale;

/**
 *
 * @author notholt
 */
public class DataPoint {        
    long timestamp;
    ArrayList<Float> data;
    
   /* We shall prevent the misuse since timestamp can be in seconds or milliseconds, we set it as milliseconds
    *  DataPoint(long ts, ArrayList<Float> dat)
    {
        this.timestamp = ts;
        this.data = dat;
    }*/
    
    DataPoint(Instant ts, ArrayList<Float> dat)
    {
        this.timestamp = ts.toEpochMilli();
        this.data = dat;
    }
    
    public LocalDateTime getTimestamp()
    {
    	Instant i = Instant.ofEpochMilli(timestamp);
    	return LocalDateTime.ofInstant(i, ZoneOffset.UTC);
    }
    
    public ArrayList<Float> getData()
    {
    	return this.data;
    }
    
    @Override
    public String toString()
    {
        String tmp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC).toString();
        
        for (int i=0; i<data.size(); i++)
        {
            tmp+=String.format(Locale.US, "; %.3f ",data.get(i));
        }
        
        tmp+="\n";
        return tmp;
    }
    
    
}
