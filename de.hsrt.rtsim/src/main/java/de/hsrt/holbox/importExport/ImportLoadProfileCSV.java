package de.hsrt.holbox.importExport;

import java.io.FileReader;
import java.time.LocalTime;
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

public class ImportLoadProfileCSV {
	
	public static final String DATEFORMAT = "H:mm";
	public static final String DELIMITER = ",";
	public static final int FROMLINE = 4;
	public static final int ENTRIES = 96;
	

	
	public static void appendDailyLoadProfile(String fileName, Dataset dataset)
	{
		/// First we build a load profile dictionary (1 dimensional)
		ArrayList<LocalTime> dt  = new ArrayList<>();
		ArrayList<Float> load  = new ArrayList<>();
		
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
		    		dt.add(LocalTime.parse(values[0], formatter));
		    		load.add(Float.parseFloat(values[1]));
		    	}
		    	lineNo++;
		    }
		    br.close();
		}catch(Exception e) {System.out.println("[E] Parsing CSV on line "+ lineNo + ": " + e.getLocalizedMessage());}
		
		// Next we use the dictionary to fill out the dataset
		
		for (int i=0; i<dataset.getData().size(); i++)
		{
			dataset.getData().get(i).getData().add(0.0f);
			
			for(int j = 0; j < load.size(); j++)
			{
				
				if(Math.abs(
						dt.get(j).getHour()*60 + dt.get(j).getMinute() 
						- dataset.getData().get(i).getTimestamp().getHour()*60 + dataset.getData().get(i).getTimestamp().getMinute()) < 15)
				{
					//System.out.println(dt.get(j).getHour()*60 + dt.get(j).getMinute() + "<>" + (dataset.getData().get(i).getTimestamp().getHour()*60+ dataset.getData().get(i).getTimestamp().getMinute()));
					dataset.getData().get(i).getData().set(2, load.get(j).floatValue());
					break;
				}
			}
		}
		
		dataset.getSignals().add(new Signal("ldPwrAtNorm", "W", 0.0));
		
	}
	
	
}
