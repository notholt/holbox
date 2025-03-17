package de.hsrt.holbox.Economics;

import de.hsrt.holbox.project.Project;
import de.hsrt.holbox.project.dataset.DataManager;


import java.io.File;
import java.util.ArrayList;

import de.hsrt.holbox.ScenarioManagement.Scenario;
import de.hsrt.holbox.ScenarioManagement.ScenarioFactory;



public class Validation {
	
	
//    public void writeDataToCsv(String fileName) {
//        CsvWriter.writeDataToCsv(y, fileName);
//    }


	
	  public static void main(String[] args) {
		  		  
		  
		  // Default economic settings
		  EconomicSettings testSettings = new EconomicSettings ();
		 
		  final String rootDir = "/tmp/holbox/";
		  
		  // Define a new Project
		  Project testObj = new Project();
			
			try {
				// We first read project settings
				//testObj.readXML(rootDir + "AT01_CV.xml");
				testObj.readXML(rootDir + "AT01_Cobija.xml");
				// We then read dataset
				//File dataset = new File(rootDir + "AT01.csv");
				File dataset = new File(rootDir + "AT01CBJ.csv");
				
				DataManager.loadDataset(dataset, testObj.getDatasetSettings());
				
				// We now construct Scenario with logging
				ScenarioFactory sf = new ScenarioFactory(testObj.getPowerSystemSettings());	// First we construct a Scenario Factory

				sf.init(true);
				//sf.setPvScale(0.0);
				sf.setWtScale(0.0);
				//sf.setBattScale(0.0001);
				
				// >>> Just for fun
				Scenario s = sf.getScenario();	// We now get the scenario to be run
				
				s.getPowerFlowFactory().runPowerFlow();
				s.getPowerFlowFactory().printResults();
				
				s.runScenario(false);
				
				
				
				//ExcelDatasetExporter.writeToFile(rootDir+"AT02_T01.xlsx", DataManager.getResultsDataset(), testObj, true);
				// <<< Just for fun
				
				// Ab hier wird es ernst (actual test)
				
				LCOE testConfiguration = new LCOE(testSettings, sf);
				
				System.out.println(testSettings);
				double nPv = 200.0;
				double nBatt = 40.0;
				
				sf.setPvScale(nPv);
				sf.setWtScale(1.0);
				sf.setBattScale(nBatt + 0.00001);
				sf.getScenario().runScenario(false);
				
				s.printResults();
				double lcoe = testConfiguration.calculateLCOE((int) nPv, (int) nBatt);
				
				
				ArrayList <OutputElement> y =  testConfiguration.determineLCOEminMethodOne((int) nPv, (int) nBatt);
				//double lcoe = testConfiguration.calculateLCOE((int) nPv, (int) nBatt);
			    CsvWriter.writeDataToCsv(y, "/tmp/holbox/validationOutput.csv");
				System.out.println(	"+--------------------------------------------------+------------------+");
				System.out.printf(	"| The LCOE                                         | %7.4f  USD/kWh |\n", lcoe);
				System.out.println(	"+--------------------------------------------------+------------------+");
				//System.out.println(	y);

			}catch(Exception e)
			{
				e.printStackTrace();
			}

		  
		  
	}


	
	
}
