package de.hsrt.holbox;

import java.io.File;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.hsrt.holbox.project.*;
import de.hsrt.holbox.ScenarioManagement.*;
import de.hsrt.holbox.importExport.*;
import de.hsrt.holbox.project.dataset.*;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AT02_PowerManagementTest {

	Project testObj;
	final String rootDir = "/tmp/holbox/";
	
	@Test
	@Order(1)
	void PMSTest() {
		
		testObj = new Project();
		
		try {
			// We first read project settings
			//testObj.readXML(rootDir + "ProjectLissabon.xml");
			testObj.readXML(rootDir + "AT01_CV.xml");
			
			// We then read dataset
			//File dataset = new File(rootDir + "PvLast_Lissabon.csv");
			File dataset = new File(rootDir + "AT01.csv");
			DataManager.loadDataset(dataset, this.testObj.getDatasetSettings());
			
			// We now construct Scenario with logging
			ScenarioFactory sf = new ScenarioFactory(testObj.getPowerSystemSettings());	// First we construct a Scenario Factory

			sf.init(true);
			sf.setBattScale(3.0*0.5*0.5);
			sf.setPvScale(3.0*0.5);
			sf.setWtScale(0.0);
			
			Scenario s = sf.getScenario();	// We now get the scenario to be run
			
			s.getPowerFlowFactory().runPowerFlow();
			s.getPowerFlowFactory().printResults();
			
			s.runScenario(true);
			
			s.printResults();
			
			ExcelDatasetExporter.writeToFile(rootDir+"AT02_T01.xlsx", DataManager.getResultsDataset(), testObj, true);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
			
	}
	

}
