package de.hsrt.holbox;

//import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.hsrt.holbox.project.*;
import de.hsrt.holbox.ScenarioManagement.*;
import de.hsrt.holbox.importExport.*;
import de.hsrt.holbox.project.dataset.*;
import de.hsrt.holbox.Economics.*;

import java.io.File;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AT03_EconomicsDebug {

	Project testObj;
	final String rootDir = "/tmp/holbox/";
	
	@Test
	@Order(1)
	void PMSTest() {
		
		testObj = new Project();
		
		try {
			// We first read project settings
			//testObj.readXML(rootDir + "AT01_CV.xml");
			testObj.readXML(rootDir + "AT01_Cobija.xml");
			
			// We then read dataset
			File dataset = new File(rootDir + "AT01CBJ.csv");
			DataManager.loadDataset(dataset, this.testObj.getDatasetSettings());
			
			// We now construct Scenario with logging
			ScenarioFactory sf = new ScenarioFactory(testObj.getPowerSystemSettings());	// First we construct a Scenario Factory
			
			double nPv = 1.0;
			double nBatt = 22.0;
			
			sf.init(true);
			sf.setPvScale(nPv);
			sf.setWtScale(1.0);
			sf.setBattScale(nBatt + 0.00001);
			
			Scenario s = sf.getScenario();	// We now get the scenario to be run
			
			s.getPowerFlowFactory().runPowerFlow();
			s.getPowerFlowFactory().printResults();
			
			s.runScenario(true);
			
			LCOE testConfiguration = new LCOE(new EconomicSettings(), sf);
			
			s.printResults();
			
			double lcoe = testConfiguration.calculateLCOE((int) nPv, (int) nBatt);
			System.out.println(	"+--------------------------------------------------+------------------+");
			System.out.printf(	"| The LCOE                                         | %7.4f  USD/kWh |\n", lcoe);
			System.out.println(	"+--------------------------------------------------+------------------+");

			
			ExcelDatasetExporter.writeToFile(rootDir+"AT02_T01.xlsx", DataManager.getResultsDataset(), testObj, true);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
			
	}

	

}
