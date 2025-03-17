package de.hsrt.holbox;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.hsrt.holbox.project.Project;
import de.hsrt.holbox.project.dataset.DataManager;
import de.hsrt.holbox.ScenarioManagement.*;
import java.io.File;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AT04_LCOEMinDebug {

	Project testObj;
	final String rootDir = "/tmp/holbox/";
	
	@Test
	@Order(1)
	void PMSTest() {
		
		testObj = new Project();
		
		try {
			// We first read project settings
			testObj.readXML(rootDir + "ProjectLissabon.xml");
			
			// We then read dataset
			File dataset = new File(rootDir + "PvLast_Lissabon.csv");
			DataManager.loadDataset(dataset, this.testObj.getDatasetSettings());
			
			// We now construct Scenario with logging
			ScenarioFactory sf = new ScenarioFactory(testObj.getPowerSystemSettings());	// First we construct a Scenario Factory
			sf.init(true);

			
			/*
			sf.setPvScale(1.0);
			sf.setWtScale(1.0);
			
			Scenario s = sf.getScenario();	// We now get the scenario to be run
			
			s.getPowerFlowFactory().runPowerFlow();
			s.getPowerFlowFactory().printResults();
			
			s.runScenario(true);
			
			s.printResults();
			
			ExcelDatasetExporter.writeToFile(rootDir+"AT02_T01.xlsx", DataManager.getResultsDataset(), testObj, true);*/

		}catch(Exception e)
		{
			e.printStackTrace();
		}
			
	}

	

}
