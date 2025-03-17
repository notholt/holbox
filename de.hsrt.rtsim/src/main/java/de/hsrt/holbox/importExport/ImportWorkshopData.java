package de.hsrt.holbox.importExport;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import de.hsrt.holbox.util.Dataset;
import de.hsrt.holbox.util.Signal;

import java.util.ArrayList;

/**
 * Imports Own CSV File format for Workshop
 * @author notholt
 *
 */

public class ImportWorkshopData {
	
	public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String DELIMITER = ";";
	public static final int FROMLINE = 2;
	public static final int ENTRIES = 580;
	

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
		    		float pwrAtWt1 = Float.parseFloat(values[1])/2100;
		    		float pwrAtWt2 = Float.parseFloat(values[3])/2100;
		    		float pwrAtPv1 = Float.parseFloat(values[5])/(24000);
		    		ArrayList<Float> data = new ArrayList<>(3);
		    		data.add(0, pwrAtWt1);
		    		data.add(1, pwrAtWt2);
		    		data.add(2, pwrAtPv1);
		    		
		    		dataset.addData(ts, data);
		    	}
		    	lineNo++;
		    }
		    br.close();
		    
		    dataset.addSignal(new Signal("WtPwrAt1", "kW", 0.0));
		    dataset.addSignal(new Signal("WtPwrAt2", "kW", 0.0));
		    dataset.addSignal(new Signal("PvPwrAt1", "kW", 0.0));
		    dataset.setBegin(dataset.getData().get(0).getTimestamp());
		}catch(Exception e) {System.out.println("[E] Parsing CSV on line "+ lineNo + ": " + e.getLocalizedMessage());}
		
	}
	
	
}
