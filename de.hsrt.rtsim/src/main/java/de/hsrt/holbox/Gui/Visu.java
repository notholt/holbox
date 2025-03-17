/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsrt.holbox.Gui;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import de.hsrt.holbox.util.DataPoint;
import de.hsrt.holbox.util.Dataset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author notholt
 */

public class Visu {
    private final VBox box;
    private final LineChart<Number, Number> chart;
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private Dataset dataset;
    private int NUM_SAMPLES = 72;
    //private int[] signals;
    private int counter, zoomCounter, zoomAmount;
    private XYChart.Series[] signalsChart;
    private static String date_format = "dd.MM. HH'h'mm";
    private String timezone = "GMT";

    Visu()
    {
        box = new VBox();
        box.fillWidthProperty().setValue(Boolean.TRUE);
        box.setPrefSize(1920, 1080);
        xAxis = new NumberAxis();
        xAxis.setTickLabelRotation(90);
       
       StringConverter sc = new EpochSecondsStringFormatter();
       xAxis.setTickLabelFormatter(sc);
       xAxis.setLabel("Date/time ["+date_format+"]");
       
        xAxis.forceZeroInRangeProperty().setValue(Boolean.FALSE);
        
        yAxis = new NumberAxis();
        yAxis.forceZeroInRangeProperty().setValue(Boolean.FALSE);
        chart = new LineChart<Number, Number>(xAxis, yAxis);
        chart.setPrefHeight(1080);
        
        //need arrow graphic 
        Button rtbtn = new Button(">"); 
        Button ltbtn = new Button("<");
        Button zoominbtn = new Button("+"); 
        Button zoomoutbtn = new Button("-"); 
        Button resetbtn = new Button("Reset");
        Button day = new Button("Show Day");
        Button week = new Button("Show Week");
        Button month = new Button("Show Month");
       
        // Get java's list of timezone ids
        Set<String> ids = ZoneId.getAvailableZoneIds();  
        
        ListView<String> timezone = new ListView<String>();
        ObservableList<String> items =FXCollections.observableArrayList();
        
        for (String id : ids) {
        	// Format timezones 
            DateTimeFormatter gmtFormatter = DateTimeFormatter.ofPattern("OOOO");
	        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(id));
	        
			String item = String.format("%-32s %9s", 
                    id.toString(), 
                    now.format(gmtFormatter));
			// Insert formatted timezones into timezone's items
			items.add(item);

        }
        timezone.setItems(items);
        
        rtbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                scrollDataset(dataset, "rtbtn");
                //number of times clicked
                counter++;
            }
        });
        ltbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                scrollDataset(dataset, "ltbtn");
                
                //number of times clicked
                if(counter>0) { counter--; }
            }
        });
        zoomoutbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
            	zoomCounter++; // Track how much chart has been zoomed
            	zoomAmount = 2 * zoomCounter; 
                scrollDataset(dataset, null);
            }
        });        
        zoominbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
            	zoomCounter--;
            	zoomAmount = 2 * zoomCounter; 
                scrollDataset(dataset, null);
            }
        });
        resetbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
            	// Set parameters to default values
                counter = 0; 
                zoomAmount = 0;
                zoomCounter = 0; 
                NUM_SAMPLES = 72;
                scrollDataset(dataset, null);
            }
        });
        day.setOnAction(new EventHandler<ActionEvent>() {
        	// Show just a day of data
            @Override public void handle(ActionEvent event) {
                scrollDataset(dataset, "day");
            }
        });
        week.setOnAction(new EventHandler<ActionEvent>() {
        	// Show a week of data
            @Override public void handle(ActionEvent event) {
                scrollDataset(dataset, "week");
            }
        });
        month.setOnAction(new EventHandler<ActionEvent>() {
        	// Show a month of data
            @Override public void handle(ActionEvent event) {
                scrollDataset(dataset, "month");
            }
        });
        
        chart.setOnScroll(new EventHandler<ScrollEvent>() {
        	@Override public void handle(ScrollEvent event) {
                double deltaY = event.getDeltaY();
                // if-else statement: scroll in or out?
                if (deltaY < 0){
                	// scroll in
                    zoomCounter--;
                } 
                else { zoomCounter++; } // scroll out
            	zoomAmount = 2 * zoomCounter; 
                scrollDataset(dataset, null);
        	}
        });

        HBox btnbox = new HBox();
        
        chart.setLegendSide(Side.TOP);
        //chart.setCreateSymbols(false);
        
        box.getChildren().add(chart);
        btnbox.getChildren().addAll(ltbtn, rtbtn, zoominbtn, zoomoutbtn, resetbtn, day, week, month);
        box.getChildren().add(btnbox);
                
    }
    
    public void setDateFormat(String dateFormat) 
    {
    	this.date_format = dateFormat;
    }
    
    public void setTimezone(String timezone) 
    { 
    	this.timezone = timezone;
    }
    
    public Node getNode()
    {
        return this.box;
    }
    
    public void setPrefHeight(int height)
    {
    	this.box.setPrefHeight(height);
    }
    
    public void setPrefWidth(int width)
    {
    	this.box.setPrefHeight(width);
    }
    
    public void setDataset(Dataset ds)
    {
        this.dataset = ds;
        
        this.chart.getData().clear();
        
        // Create new series
        signalsChart = new XYChart.Series[ds.getSignals().size()];
        
        for(int i=0; i<signalsChart.length; i++)
        {
            signalsChart[i] = new XYChart.Series<>();
            
            for(int j=0; j<Math.min(ds.getData().size(), NUM_SAMPLES); j++)
            { 
				// Make chart with timezone dates on the x axis, database's data on y axis
            	signalsChart[i].getData().add(new XYChart.Data<>(ds.getData().get(j).getTimestamp().toEpochSecond(ZoneOffset.ofHours(TimeZone.getTimeZone(timezone).getRawOffset())), ds.getData().get(j).getData().get(i)));

            }
            
            signalsChart[i].setName(ds.getSignals().get(i).getVarName() + " [" + ds.getSignals().get(i).getUnit() + "]");
            this.chart.getData().add(signalsChart[i]);
        }
        setTooltips(ds); // Show data (time and energy) on hover
        
    }
    
    public void setTooltips(Dataset ds) {

        for (XYChart.Series<Number, Number> s : chart.getData() ) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
            	long dt = (long) d.getXValue(); //convert d to a usable value
            	long unixSeconds = dt;
				Date date = new Date(unixSeconds*1000L); 
				SimpleDateFormat sdf = new SimpleDateFormat(date_format); // Get preferred date format
				sdf.setTimeZone(java.util.TimeZone.getTimeZone(timezone)); // Get preferred timezone
				String formattedDate = sdf.format(date); // Get date to display from sdf variable
                Tooltip.install(d.getNode(), new Tooltip("Date : " + formattedDate + "\n" + // Install tooltip with date and y axis value
                                "Energy : " + d.getYValue()));
            }
        }
    }
    
    public void setDataset(Dataset ds, int minNum, int num)
    {
    	double interval = Math.max(this.zoomAmount, 1); 

        this.dataset = ds;
        
        this.chart.getData().clear();
        
        // Create new series
        XYChart.Series[] signals = new XYChart.Series[ds.getSignals().size()];
        
        // Create array that accounts for zoom and is used for chart's x-axis
        List<DataPoint> zoomArray = new ArrayList<DataPoint>();
        for(int i=0; i < Math.min(ds.getData().size(), NUM_SAMPLES+minNum) * interval; i++)
        {
        	zoomArray.add(i, ds.getData().get(i));
        } 
        
        for(int i=num; i<signals.length; i++)
        {
            signals[i] = new XYChart.Series<>();
            
            for(int j=minNum; j<Math.min(ds.getData().size(), NUM_SAMPLES+minNum) * interval; j = j + Math.max(zoomAmount, 1))
            {
                signals[i].getData().add(new XYChart.Data<>(zoomArray.get(j).getTimestamp().toEpochSecond(ZoneOffset.ofHours(TimeZone.getTimeZone(timezone).getRawOffset())), ds.getData().get(j).getData().get(i)));
            }
            
        	setTooltips(ds);
            signals[i].setName(ds.getSignals().get(i).getVarName() + " [" + ds.getSignals().get(i).getUnit() + "]");
            this.chart.getData().add(signals[i]);
        }
        
    }
    
    public Dataset scrollDataset(Dataset ds, String btn)
    {
    	// switch based on which button is clicked
    	int interval = Math.max(this.zoomAmount, 1); 

    	int scale = 10;
    	this.dataset = ds;
    	    	
    	if (btn == "rtbtn") {
    		    		
        	setDataset(ds, counter*scale, 0);
    	}
    	else if (btn == "ltbtn") {
    		
        	setDataset(ds, counter*scale, 0);
    	}
    	else if (btn == "day") {
    		NUM_SAMPLES = 24; //hours in day
    		setDataset(ds, counter*scale, 0);
    	}
    	else if (btn == "week") {
    		NUM_SAMPLES = 168; //hours in week
    		setDataset(ds, counter*scale, 0);
    	}
    	else if (btn == "month") {
    		NUM_SAMPLES = 730; //hours in month
    		setDataset(ds, counter*scale, 0);
    	}
    	else {
    		setDataset(ds, counter*scale, 0);
    	}
    	return ds;
    }

    
    public void setDataset(Dataset ds, int[] signalidx)
    {
        this.dataset = ds;
        
        this.chart.getData().clear();
        
        // Create new series
        XYChart.Series[] signals = new XYChart.Series[signalidx.length];
        this.chart.getData().clear();
        for(int i=0; i<signalidx.length; i++)
        {
            signals[i] = new XYChart.Series<>();
            
            for(int j=0; j<Math.min(ds.getData().size(), NUM_SAMPLES); j++)
            {
                signals[i].getData().add(new XYChart.Data<>(ds.getData().get(j).getTimestamp().toEpochSecond(ZoneOffset.ofHours(TimeZone.getTimeZone(timezone).getRawOffset())), ds.getData().get(j).getData().get(signalidx[i])));
            }
        	setTooltips(ds);
            signals[i].setName(ds.getSignals().get(signalidx[i]).getVarName() + " [" + ds.getSignals().get(signalidx[i]).getUnit() + "]");
            this.chart.getData().add(signals[i]);
        }
        
    }
    
    public static class EpochSecondsStringFormatter extends StringConverter
    {

        @Override
        public String toString(Object t) {
            Number n = (Number)t;
            LocalDateTime dt = LocalDateTime.ofEpochSecond(n.intValue(), 0, ZoneOffset.UTC);
            return dt.format(DateTimeFormatter.ofPattern(date_format));
        }

        @Override
        public Object fromString(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
