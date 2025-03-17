package de.hsrt.holbox.Economics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter {
    public static void writeDataToCsv(List<OutputElement> elements, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("nbrBat,nbrPV, eLd, ePv, eWt, eDg, fuel, hours, sF, eCur, possible, lcoe\n");
            for (OutputElement element : elements) {
                writer.append(String.valueOf(element.getNbrBat()))
                      .append(',')
                      .append(String.valueOf(element.getNbrPV()))
                      .append(',')
                      .append(String.valueOf(element.getELd()/1000.0))
                      .append(',')
                      .append(String.valueOf(element.getEPv()/1000.0))
                      .append(',')
                      .append(String.valueOf(element.getEWt()/1000.0))
                      .append(',')
                      .append(String.valueOf(element.getEDg()/1000.0))
                      .append(',')
                      .append(String.valueOf(element.getFuel()/1000.0))
                      .append(',')
                      .append(String.valueOf(element.getHours()))
                      .append(',')
                      .append(String.valueOf(element.getSF()))
                      .append(',')
                      .append(String.valueOf(element.getECur()/1000.0))
                      .append(',')
                      .append(String.valueOf(element.getPossible()))
                      .append(',')
                      .append(String.valueOf(element.getLcoe()))
                      .append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
