package de.hsrt.holbox.importExport;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import de.hsrt.holbox.util.Dataset;
import de.hsrt.holbox.util.Signal;

import java.util.ArrayList;

/**
 * Imports PVGIS CSV File format
 * @author notholt
 *
 */

public class ImportPvGisCSV {
	
	public static final String DATEFORMAT = "yyyyMMdd:HHmm";
	public static final String DELIMITER = ",";
	public static final int FROMLINE = 12;
	public static final int ENTRIES = 8784;
	

	public static void appendHourlyData(String fileName, Dataset dataset)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATEFORMAT);
		int lineNo = 1;
		try 
		{	
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		    
			String line;
		    while ((line = br.readLine()) != null) {
		    	if((lineNo >= FROMLINE) && (lineNo < FROMLINE+ENTRIES))
		    	{
		    		String[] values = line.split(DELIMITER);
		    		LocalDateTime ts = LocalDateTime.parse(values[0], formatter);
		    		float pwrAtPvNorm = (float) (Float.parseFloat(values[1])/1000.0);
		    		float wndSpeed = Float.parseFloat(values[5]);
		    		ArrayList<Float> data = new ArrayList<>(2);
		    		data.add(0, pwrAtPvNorm);
		    		data.add(1, wndSpeed);
		    		
		    		dataset.addData(ts, data);
		    	}
		    	lineNo++;
		    }
		    br.close();
		    
		    dataset.addSignal(new Signal("pvPwrAtNorm", "kW", 0.0));
		    dataset.addSignal(new Signal("windSpeed", "m/s", 0.0));
		    dataset.setBegin(dataset.getData().get(0).getTimestamp());
		}catch(Exception e) {System.out.println("[E] Parsing CSV on line "+ lineNo + ": " + e.getLocalizedMessage());}
		
	}
	
	
}
