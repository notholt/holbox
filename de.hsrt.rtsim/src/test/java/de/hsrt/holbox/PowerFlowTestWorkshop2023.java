package de.hsrt.holbox;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.hsrt.holbox.util.Dataset;
import de.hsrt.holbox.util.ModelCodeException;
import de.hsrt.holbox.importExport.ImportWorkshopData;
import de.hsrt.holbox.playground.HybridPowerPlantExample;

class PowerFlowTestWorkshop2023 {

	@Test
	void test() {
		
		Dataset inputDataset = new Dataset();
		
		ImportWorkshopData.appendHourlyData("C:\\holboxPrj\\workshop\\MessDatenWorkshop.csv", inputDataset);
		
		try {
			HybridPowerPlantExample sys = new HybridPowerPlantExample(inputDataset);
			sys.setup();
		} catch (ModelCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//assertTrue(false);
		//fail("Not yet implemented");
	}

}
