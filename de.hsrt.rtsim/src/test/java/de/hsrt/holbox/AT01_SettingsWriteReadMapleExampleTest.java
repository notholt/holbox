package de.hsrt.holbox;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.hsrt.holbox.Models.*;
import de.hsrt.holbox.Models.Configuration.*;
import de.hsrt.holbox.project.Project;
import de.hsrt.holbox.project.dataset.DataManager;
import de.hsrt.holbox.project.powersystem.BusSettings;
import de.hsrt.holbox.project.powersystem.BusType;
import de.hsrt.holbox.project.powersystem.LineSettings;
import de.hsrt.holbox.project.powersystem.LineTypeSettings;
import de.hsrt.holbox.project.powersystem.PowerFlowFactory;
import de.hsrt.holbox.util.Log;
import de.hsrt.holbox.util.Log.Lvl;
import de.hsrt.holbox.ScenarioManagement.*;
import de.hsrt.holbox.importExport.ExcelDatasetExporter;

import java.io.File;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AT01_SettingsWriteReadMapleExampleTest {

	Project testObj;
	final String rootDir = "/tmp/holbox/";
	
	@Test
	@Order(1)
	void createProjectFile() {
		
		testObj = new Project();
		
		// We first fill up the Data with some predefined values (different than default)
		try {
			// Project Settings
			testObj.getProjectSettings().getParameters().getElementById("prjName").setVal("Test Project (T001)");
			testObj.getProjectSettings().getParameters().getElementById("prjDesc").setVal("Test Project described (T001)");
			testObj.getProjectSettings().getParameters().getElementById("prjOwner").setVal("HolBoX Tester");
			testObj.getProjectSettings().getParameters().getElementById("prjRev").setVal("R.Alpha.Tester");
			testObj.getProjectSettings().getParameters().getElementById("prjLat").setVal(11.0);
			testObj.getProjectSettings().getParameters().getElementById("prjLon").setVal(33.0);
			// Dataset settings
			testObj.getDatasetSettings().getParameters().getElementById("resFile").setVal(rootDir + "AT01.csv");
			testObj.getDatasetSettings().getParameters().getElementById("impResFromLine").setVal(11);
			testObj.getDatasetSettings().getParameters().getElementById("colDelimiter").setVal(",");
			testObj.getDatasetSettings().getParameters().getElementById("decDelimiter").setVal(".");
			testObj.getDatasetSettings().getParameters().getElementById("timeFormat").setVal("yyyyMMdd:HHmm");
			// Economics
			testObj.getEconomicSettings().getParameters().getElementById("CCEquWT").setVal(1.0);
			testObj.getEconomicSettings().getParameters().getElementById("CCEquDG").setVal(2.0);
			testObj.getEconomicSettings().getParameters().getElementById("CCEquPV").setVal(3.0);
			testObj.getEconomicSettings().getParameters().getElementById("InitBAT").setVal(4.0);
			testObj.getEconomicSettings().getParameters().getElementById("AnRepBat").setVal(5.0);
			testObj.getEconomicSettings().getParameters().getElementById("InInvPowEqBAT").setVal(6.0);
			testObj.getEconomicSettings().getParameters().getElementById("OpMainWT").setVal(7.0);
			testObj.getEconomicSettings().getParameters().getElementById("OpMainPV").setVal(8.0);
			//testObj.getEconomicSettings().getParameters().getElementById("OpMainBAT").setVal(9.0);
			testObj.getEconomicSettings().getParameters().getElementById("OpDG").setVal(10.0);
			testObj.getEconomicSettings().getParameters().getElementById("MaintenDG").setVal(11.0);
			testObj.getEconomicSettings().getParameters().getElementById("COCost").setVal(12.0);
			testObj.getEconomicSettings().getParameters().getElementById("COemission").setVal(13.0);
			testObj.getEconomicSettings().getParameters().getElementById("DisRtWT").setVal(14.0);
			testObj.getEconomicSettings().getParameters().getElementById("DisRtPV").setVal(15.0);
			testObj.getEconomicSettings().getParameters().getElementById("DisRtDG").setVal(16.0);
			testObj.getEconomicSettings().getParameters().getElementById("DisRtBAT").setVal(17.0);
			testObj.getEconomicSettings().getParameters().getElementById("UseLfTWT").setVal(18.0);
			testObj.getEconomicSettings().getParameters().getElementById("UseLfTPV").setVal(19.0);
			testObj.getEconomicSettings().getParameters().getElementById("UseLfTDG").setVal(20.0);
			testObj.getEconomicSettings().getParameters().getElementById("UseLfTBAT").setVal(21.0);
			// Power System
			testObj.getPowerSystemSettings().getParameters().getElementById("sysFreqNom").setVal(50.0);
			testObj.getPowerSystemSettings().getParameters().getElementById("baseMVA").setVal(1.0);
			// Buses			
			testObj.getPowerSystemSettings().getBuses().add(new BusSettings("01", BusType.PQ, null, null, null, 1.0, 1.1, 0.9, null, null, 0, 1700.0, 1053.5, 0.0, 0.0));
			testObj.getPowerSystemSettings().getBuses().add(new BusSettings("02", BusType.PQ, null, null, null, 1.0, 1.1, 0.9, null, null, 0, 2000.0, 1239.4, 0.0, 0.0));
			testObj.getPowerSystemSettings().getBuses().add(new BusSettings("03", BusType.PV, null, 1.02, null, 1.0, 1.1, 0.9, null, null, 0, 800.0, 0495.8, 3180.0, null));
			// Intentionally set the slack node to the end to test the proper ordering of the node!!
			testObj.getPowerSystemSettings().getBuses().add(new BusSettings("00", BusType.SLACK, null, 1.0, 0.0, 1.0, 1.1, 0.9, null, null, 0, 0500.0, 0309.9, null, null));
			
			testObj.getPowerSystemSettings().getLines().add(new LineSettings("1-2", "00", "01", "A", 1.0, null, null, 1));
			testObj.getPowerSystemSettings().getLines().add(new LineSettings("1-3", "00", "02", "B", 1.0, null, null, 1));
			testObj.getPowerSystemSettings().getLines().add(new LineSettings("2-4", "01", "03", "B", 1.0, null, null, 1));
			testObj.getPowerSystemSettings().getLines().add(new LineSettings("3-4", "02", "03", "C", 1.0, null, null, 1));
			
			testObj.getPowerSystemSettings().getLineTypes().add(new LineTypeSettings("A", 0.01008, 0.05040, 0.05125, null));
			testObj.getPowerSystemSettings().getLineTypes().add(new LineTypeSettings("B", 0.00744, 0.03720, 0.03875, null));
			testObj.getPowerSystemSettings().getLineTypes().add(new LineTypeSettings("C", 0.01272, 0.06360, 0.06375, null));
			
			// Adding some Equipment Settings
			ControlledDERSettings pvS = new ControlledDERSettings("TEST_PV_Sys1");
			testObj.getPowerSystemSettings().getEquipmentSettings().add(pvS);
			
			
			// Adding some Equipment
			// PV as controlled resource
			ControlledDER pv1 = new ControlledDER("PV1", null, null, pvS);
			pv1.setEqClass(EqClass.PV);
			pv1.getParameters().getElementById("managed").setVal(1);
			
			testObj.getPowerSystemSettings().getEquipment().add(pv1);
			
			// Wind as uncontrolled resource
			DistributedEnergyResource wt1 = new DistributedEnergyResource("WT1", null, 250.0, 200.0, "wtPwrAtNorm", 1.0);
			wt1.setEqClass(EqClass.WIND);
			wt1.getParameters().getElementById("managed").setVal(1);
			
			// Energy storage as controlled source
			SimpleBattery batt = new SimpleBattery("BATT1", 1000.0, 1000.0, 1000.0, null, null);
			testObj.getPowerSystemSettings().getEquipment().add(batt);
			
			testObj.getPowerSystemSettings().getEquipment().add(wt1);
			testObj.getPowerSystemSettings().getEquipment().add(new DistributedEnergyResource("H0", null, 1000.0, 1000.0, "ldPwrAtNorm", 1.0));

			// Adding 3 Gensets with same settings
			ConventionalGeneratorSettings gs = new ConventionalGeneratorSettings("CAT1");
			testObj.getPowerSystemSettings().getEquipmentSettings().add(gs);
			ConventionalGenerator gen1		= new ConventionalGenerator("G1", gs);
			ConventionalGenerator gen2		= new ConventionalGenerator("G2", gs);
			ConventionalGenerator gen3		= new ConventionalGenerator("G3", gs);
			gen1.addBus("00", 1.0, BusAttachment.Type.GEN);
			gen2.addBus("00", 1.0, BusAttachment.Type.GEN);
			gen3.addBus("00", 1.0, BusAttachment.Type.GEN);
			testObj.getPowerSystemSettings().getEquipment().add(gen1);
			testObj.getPowerSystemSettings().getEquipment().add(gen2);
			testObj.getPowerSystemSettings().getEquipment().add(gen3);
			
			
			// Locating Equipment on Buses
			testObj.getPowerSystemSettings().getEquipment().getElementById("PV1").addBus("01", 1.0, BusAttachment.Type.GEN);
			testObj.getPowerSystemSettings().getEquipment().getElementById("BATT1").addBus("01", 1.0, BusAttachment.Type.GEN);
			testObj.getPowerSystemSettings().getEquipment().getElementById("WT1").addBus("03", 1.0, BusAttachment.Type.GEN);
			testObj.getPowerSystemSettings().getEquipment().getElementById("H0").addBus("02", 1.0, BusAttachment.Type.LOAD);
			
			
			
			
			// Setting a power management
			testObj.getPowerSystemSettings().setPowerManagement(new SimplePowerManagement());
			//Setting a frequency model
			testObj.getPowerSystemSettings().setFrequencyModel(new GridFrequencyModel());
			
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			fail("Failed to set parameters");
		}
		
		
		// We now write the complete project file
		try {
			testObj.saveXML(rootDir+"AT01.xml");
		}catch(Exception e)
		{
			fail("Failed to write file");
		}
		
		// Reading same file
		
		Project readObject = new Project();
		
		try {
			readObject.readXML(rootDir+"AT01.xml");
			testObj.equals(readObject);
		}catch(Exception e)
		{
			e.printStackTrace();
			fail("Failed to read file");
		}
		
		
		assertEquals(true, testObj.comparable(readObject), "Written and read objects are not equal");
		Log.print(Lvl.TEST_PASS, "T01.01 - Successfully wrote project XML and reread it back to master data structure ");
			
	}
	
	@SuppressWarnings("static-access")
	@Test
	@Order(2)
	void testMaplePowerFlowCalc()
	{
		
		this.testObj = new Project();
		
		this.testObj.readXML(rootDir + "AT01.xml");
		
		PowerFlowFactory pff = new PowerFlowFactory(this.testObj.getPowerSystemSettings());
		
		pff.runPowerFlow();
		
		pff.printResults();
		
		// TODO reduce error tolerances
		double errorVtg = 0.001;
		double errorAng = 0.01;
		
		assertEquals(1.0	, pff.getBuses().get(0).getVtg(), errorVtg, "Magintude of Node 0 is wrong");
		assertEquals(0.0	, pff.inDeg(pff.getBuses().get(0).getAng()), errorAng, "Angle of Node 0 is wrong");
		
		assertEquals(0.982	, pff.getBuses().get(1).getVtg(), errorVtg, "Magintude of Node 1 is wrong");
		assertEquals(-0.976	, pff.inDeg(pff.getBuses().get(1).getAng()), errorAng, "Angle of Node 1 is wrong");
		
		assertEquals(0.969	, pff.getBuses().get(2).getVtg(), errorVtg, "Magintude of Node 2 is wrong");
		assertEquals(-1.872	, pff.inDeg(pff.getBuses().get(2).getAng()), errorAng, "Angle of Node 2 is wrong");
		
		assertEquals(1.020	, pff.getBuses().get(3).getVtg(), errorVtg, "Magintude of Node 4 is wrong");
		assertEquals(1.523	, pff.inDeg(pff.getBuses().get(3).getAng()), errorAng, "Angle of Node 4 is wrong");
		
		
		Log.print(Lvl.TEST_PASS, "T01.02 - Successfully ran Power Flow Calculation");
		assertTrue(true, "T01.02 - Successfully ran Power Flow Calculation");
	}
	
	@Test
	@Order(3)
	void readDataset()
	{
		this.testObj = new Project();
		this.testObj.readXML(rootDir + "AT01.xml");
		
		File dataset = new File(rootDir + "AT01.csv");
		
		try
		{
			DataManager.loadDataset(dataset, this.testObj.getDatasetSettings());
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "While loading Dataset: " + e.getMessage());
			fail("Failed after attempting to load file");
		}
		
		Log.print(Lvl.TEST_PASS, "T01.03 - Successfully read CSV dataset");
		assertTrue(true, "T01.03 - Successfully read CSV dataset");
	}
	
	@Test
	@Order(4)
	void runScenario()
	{
		this.testObj = new Project();
		this.testObj.readXML(rootDir + "AT01.xml");
		
		File dataset = new File(rootDir + "AT01.csv");
		
		try
		{
			DataManager.loadDataset(dataset, this.testObj.getDatasetSettings());
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "While loading Dataset: " + e.getMessage());
			fail("Failed after attempting to load file");
		}
		
		// Scenario testing.
		ScenarioFactory sf = new ScenarioFactory(this.testObj.getPowerSystemSettings());
		try 
		{
			sf.init(true);	
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "Initializing the Scenario with reason: " + e.getMessage());
			e.printStackTrace();
			fail("Failed after attempting generate Scenario");
		}
		
		try 
		{
			Scenario s = sf.getScenario();
			s.runScenario(true);
			ExcelDatasetExporter.writeToFile(rootDir+"AT01_T03.xlsx", DataManager.getResultsDataset(), testObj);
			//ExcelDatasetExporter.writeToFile(rootDir+"AT01_T03.xlsx", DataManager.getInputDataset(), testObj);
			
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "Running scenario and exporting with reason: " + e.getMessage());
			e.printStackTrace();
			fail("Failed after attempting to Run and export Scenario");
		}
		
		
		
		
	}

}
