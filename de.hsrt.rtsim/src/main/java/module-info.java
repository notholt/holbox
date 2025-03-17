module holbox {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires javafx.media;
  requires org.apache.commons.math4.core;
  requires org.apache.commons.numbers.complex;
  requires org.apache.commons.numbers.core;
  requires org.apache.poi.poi;
  requires org.apache.poi.ooxml;
  requires org.apache.commons.io;
  requires java.xml.bind;
  //requires org.junit.jupiter.api;
  
  

  opens de.hsrt.holbox.Gui to javafx.graphics, javafx.fxml;
  opens de.hsrt.holbox.project to java.xml.bind;
  opens de.hsrt.holbox.util;
  opens de.hsrt.holbox.project.dataset;
  opens de.hsrt.holbox.Economics;
  opens de.hsrt.holbox.project.powersystem;
  opens de.hsrt.holbox.Models;
  opens de.hsrt.holbox.Models.Configuration;
  exports de.hsrt.holbox.ClassicPowerFlow;
}


