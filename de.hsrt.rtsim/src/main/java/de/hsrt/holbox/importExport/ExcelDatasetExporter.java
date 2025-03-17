package de.hsrt.holbox.importExport;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import de.hsrt.holbox.project.Project;
import de.hsrt.holbox.util.DataPoint;
import de.hsrt.holbox.util.Dataset;
import de.hsrt.holbox.util.Log;
import de.hsrt.holbox.util.Log.Lvl;

/**
 * This class exports a dataset to a given Excel file
 * @author notholt
 *
 */

public class ExcelDatasetExporter {
	
	public static void writeToFile(String filename, Dataset ds, Project p) throws Exception
	{
		writeToFile(filename, ds, p, false);
	}
	
	public static void writeToFile(String filename, Dataset ds, Project p, boolean simple) throws Exception
	{
		Workbook workbook = null;
		CellStyle style = null;
		
		if(filename.endsWith("xlsx")){
			workbook = new XSSFWorkbook();
			style = workbook.createCellStyle();
			style.setDataFormat((short) 22);
		}else
		{
			throw new RuntimeException("Only writing to .xlsx is supported. Please use proper extension");
		}
		
		Sheet data = workbook.createSheet("data");
		
		if(!simple)
		{
			// Write header of table with project information
			Row prjInfo = data.createRow(0);
			Cell prjInfoCell = prjInfo.createCell(0);
			
			prjInfoCell.setCellValue("HolBoX Hybrid");
			
			prjInfoCell = prjInfo.createCell(1);
			
			prjInfoCell.setCellValue(	"Export for Project ["
										+ p.getProjectSettings().getParameters().getElementById("prjName").getVal()
										+ "] Updated on: "+LocalDateTime.now().toString());
		}
		writeSignals(data, ds, 1, simple);
		
		if(!simple)
		{
			writeDataPoints(style, data, ds, 3);
		}else
		{
			writeDataPoints(style, data, ds, 2);
		}
		
		
		FileOutputStream fos = new FileOutputStream(filename);
		workbook.write(fos);
		fos.close();		
		workbook.close();
		
		Log.print(Lvl.SUCCESS, "Data has been written to " + filename);
		
	}
	
	private static void writeSignals(Sheet sheet, Dataset ds, int fromRow, boolean simple)
	{
		Row signalNames = sheet.createRow(fromRow);
		Row signalUnits = null; 
		
		if (!simple) {signalUnits = sheet.createRow(fromRow+1);}
		
		signalNames.createCell(0).setCellValue("DateTime");
		//signalUnits.createCell(0).setCellValue("m/d/yy h:mm");
		
		int col = 1;
		for(String id : ds.getSignals().getMap().keySet())
		{
			signalNames.createCell(col).setCellValue(id);
			if(!simple) {signalUnits.createCell(col).setCellValue(ds.getSignals().getElementById(id).getUnit());}
			col++;
		}
	}
	
	private static void writeDataPoints(CellStyle sty, Sheet sheet, Dataset ds, int fromRow)
	{
		int i = fromRow;
		for (DataPoint dp : ds.getData())
		{
			Row data = sheet.createRow(i);
			// We write first the date & time
			Cell dt = data.createCell(0);
			dt.setCellValue(dp.getTimestamp());
			dt.setCellStyle(sty);
			
			for(int j = 0; j < dp.getData().size(); j++)
			{
				data.createCell(j+1).setCellValue(dp.getData().get(j));
			}
			
			i++;
		}
		
	}
	
}
