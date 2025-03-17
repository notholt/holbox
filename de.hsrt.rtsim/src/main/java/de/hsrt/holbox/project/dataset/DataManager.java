package de.hsrt.holbox.project.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hsrt.holbox.util.Dataset;
import de.hsrt.holbox.util.Log;
import de.hsrt.holbox.util.Signal;
import de.hsrt.holbox.util.SignalList;
import de.hsrt.holbox.util.Log.Lvl;
import de.hsrt.holbox.util.ModelCodeException;

/**
 * General class which handles all data in the project. The first version loads all data in the input dataset to memory so the possible resolution 
 * and length of the data is limited only by the memory available to the VM  
 * @author notholt
 *
 */
public class DataManager {

		private static DatasetSettings s_in;	// DatasetSettings
	
		private static Dataset in;				// Input dataset
		private static Dataset results = new Dataset();			// Results dataset
		private static Dataset intResults = new Dataset();			// Results dataset
		
		private static SignalList signals;		// Master repository of all data signals to be monitored in the Data Manager
		
		
		/**
		 * Loads a dataset (BIG table in CSV Format) and overwrites the current input dataset
		 * @param f
		 */
		public static void loadDataset(File f, DatasetSettings sl) throws Exception
		{
			DatasetSettings ds;
			// STEP 1: Check if settings are there
			if(sl == null)
			{
				if(s_in!=null)
				{
					ds = s_in;
				}else
				{
					throw new RuntimeException("No DatasetSettings known, please revise.");
				}
			}else
			{
				ds = sl;
			}
			
			// STEP 2: Populate necessary settings and open file
			String 	DATEFORMAT 	= ds.timeFormat.getVal();
			String 	DELIMITER 	= ds.colDelimiter.getVal();
			int 	FROMLINE	= ds.importFromLine.getVal();
			
			// STEP 3: Open file for read
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = "";
				
				// STEP 4: Read first line, where Signals are defined (defined by FROMLINE)
				for(int i=0; i < FROMLINE; i++)
				{
					line = br.readLine();
				}
				// STEP 5: Create new dataset object
				in = new Dataset();
				populateSignalList(in, line, DELIMITER);
				
				// STEP 6: 
				int signalNumber = in.getSignals().size();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATEFORMAT);
				int importedDataEntries = 0;
				
				while ((line = br.readLine()) != null) 
				{
					String[] values 		= line.split(DELIMITER);
					
					if(values.length-1 == signalNumber)
					{
						LocalDateTime ts	= LocalDateTime.parse(values[0], formatter);
						ArrayList<Float> data 	= new ArrayList<>(signalNumber);
						
						for(int i=0; i<signalNumber; i++)
						{
							data.add(i, (float) (Float.parseFloat(values[i+1])*in.getSignals().get(i).getInputScaling()));
						}
						in.addData(ts, data);
						importedDataEntries++;
					}
					else
					{
						break;
					}
				}	
				Log.print(Lvl.INFO, "Parsed CSV dataset from file <"+ f.getName()+"> with " + importedDataEntries + " entries.");
				br.close();
			}catch(Exception e)
			{
				//e.printStackTrace();
				throw new RuntimeException("Error opening dataset file with error: "+e.getMessage());
			}
			
			recomputeMasterSignalRoster();
			
		}
		
		
		public static void setInputDatasetSettings(DatasetSettings ds)
		{
			s_in = ds;
		}
		
		public static Dataset getInputDataset()
		{
			return in;
		}
		
		public static Dataset getResultsDataset()
		{
			return results;
		}
		
		public static Dataset getIntermediateResultsDataset()
		{
			return intResults;
		}
		
		/**
		 * Takes as argument the first line of the CSV file (column header) and generates signal list with the following pattern:
		 * Signal_Name(Scaling){Signal description}[Signal Unit]
		 * @param ds
		 * @param line
		 * @param DELIMITER
		 */
		private static void populateSignalList(Dataset ds, String line, String DELIMITER)
		{
			String[] signals = line.split(DELIMITER);
			Log.print(Log.Lvl.INFO, "Recognized "+ signals.length + " signals");
			
			for(int i =1; i<signals.length; i++) // For each column (after the first one, which must be time) we try to decypher the available signals
			{
				try 
				{
					String signalName 	= signals[i].split("[\\< \\{ \\[ ]")[0];
					Double signalFactor = (getBetween(signals[i], "<",">")!=null? Double.parseDouble(getBetween(signals[i], "<",">")):1.0);
					String signalDesc 	= (getBetween(signals[i], "{","}")!=null? getBetween(signals[i], "{","}"):"No description");
					String signalUnit 	= (getBetween(signals[i], "[","]")!=null? getBetween(signals[i], "[","]"):"-");
					
					Log.print(Log.Lvl.INFO, "Parsed Signal "+ signalName +" {"+ signalDesc +"} ["+signalUnit+"] with scaling: "+signalFactor);
					
					Signal sig = new Signal(signalName, signalUnit, 0.0);
					
					sig.setInputScaling(signalFactor);
					ds.addSignal(sig);
				}catch(Exception e)
				{
					throw new RuntimeException("Error setting values for Signal: "+signals[i]);
				}
			}
		}
		
		private static String getBetween(String in, String begin, String end)
		{
			Pattern regex = Pattern.compile("\\" + begin + "([^()]*)\\" + end);
			Matcher regexMatcher = regex.matcher(in);
			
			if(regexMatcher.find())  // This function only returns the first occurrence of the enclosed value
			{
				String result = regexMatcher.group(); 
				return result.substring(1, result.length()-1);
			}

			return null;
		}
		
		/**
		 * This function recomputes the master signal roster. 
		 */
		public static void recomputeMasterSignalRoster() throws ModelCodeException
		{
			signals = new SignalList();
			for(int i = 0; i < in.getSignals().size(); i++)
			{
				try
				{
					signals.getMap().put("in."+ in.getSignals().get(i).getVarName(), in.getSignals().get(i));
				}catch(Exception e)
				{
					throw new ModelCodeException("Could not add signal [" + "in."+ in.getSignals().get(i).getVarName() + "]. Reason: " + e.getMessage() );
				}
			}
		}
		
		public static void logResults(Instant when)
		{
			results.addFromSignals(when);
		}
	
}
