package de.hsrt.holbox.Gui;

import de.hsrt.holbox.Models.Equipment;
import de.hsrt.holbox.Models.Configuration.EquipmentSettings;
import de.hsrt.holbox.project.powersystem.BusSettings;
import de.hsrt.holbox.project.powersystem.ComponentList;
import de.hsrt.holbox.project.powersystem.LineSettings;
import de.hsrt.holbox.project.powersystem.LineTypeSettings;
import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.project.powersystem.PowerSystemSettings;
import de.hsrt.holbox.util.FileParameter;
import de.hsrt.holbox.util.Parameter;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import de.hsrt.holbox.util.Settings;
import de.hsrt.holbox.util.StringParameter;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.*; 
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generic class for creating the GUI elements and binding them to its parameters contained on a given util.Settings implementation
 * @author notholt
 *
 */

public class SettingsFxNodeFactory {
	
	
	public static void generateSettingsView(Pane pane, URL fxmlPane, Settings s, int labelWidth, int width)
	{
		
		FXMLLoader loader = new FXMLLoader();
		
		
		try {
			
			loader.setLocation(fxmlPane);
			loader.load();
			Pane innerPane = loader.getRoot();
			generateSettingsView(innerPane,  s, labelWidth, width);
			pane.getChildren().add(innerPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void generatePowerSystemSettingsView(Pane pane, URL fxmlPane, PowerSystemSettings s, int labelWidth, int width)
	{
		
		FXMLLoader loader = new FXMLLoader();
		
		
		try {
			
			loader.setLocation(fxmlPane);
			loader.load();
			Pane innerPane = loader.getRoot();
			
			
			pane.getChildren().add(innerPane);
			
			// STEP 1: We create one HBox for the two parameters there
			HBox sysParams = new HBox();
			generateSettingsView(sysParams, s, labelWidth, width);
			sysParams.setMinHeight(50);
			innerPane.getChildren().add(sysParams);
			
			// STEP 2: Generate Tabs
			TabPane tabs = new TabPane();
			//tabs.setSide(Side.LEFT);
			tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
			tabs.setPrefHeight(1080);
			
			Tab buses = new Tab(" Buses  ", null); // Eventually new Label("Show all planes available")
			Tab lines = new Tab(" Lines  ", null);
			Tab lineTypes = new Tab(" Line Types  ", null);
			Tab equipment = new Tab(" Equipment  ", null);
			Tab equipmentSettings = new Tab(" Equipment Settings  ", null);
			
			// STEP 3: Populate Tabs
			final int lw = 350;
			final int dw = 100;
			generateComponentListSettingsView(buses, s.getBuses(), 0.1, 0.9, lw, dw, new BusSettings());
			generateComponentListSettingsView(lines, s.getLines(), 0.1, 0.9, lw, dw, new LineSettings());
			generateComponentListSettingsView(lineTypes, s.getLineTypes(), 0.1, 0.9, lw, dw, new LineTypeSettings());
			//Equipment debug = new Equipment();
			//Log.print(Lvl.DEBUG, "SettingsFxNodeFactory 115: Object: " + debug.toString() );
			generateComponentListSettingsView(equipment, s.getEquipment(), 0.1, 0.9, lw, dw, new Equipment());
			generateComponentListSettingsView(equipmentSettings, s.getEquipmentSettings(), 0.1, 0.9, lw, dw, new EquipmentSettings());
			
			tabs.getTabs().add(buses);
			tabs.getTabs().add(lines);
			tabs.getTabs().add(lineTypes);
			tabs.getTabs().add(equipment);
			tabs.getTabs().add(equipmentSettings);
			
			innerPane.getChildren().add(tabs);
					
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

	/**
	 * 	Geenrates componentlist
	 * 
	 * @param tab
	 * @param cl
	 * @param tblIdProp
	 * @param tblDescProp
	 * @param labelWidth
	 * @param dataWidth
	 * @param pattern		An empty intance of the object to be layouted
	 */
	public static void generateComponentListSettingsView(Tab tab, ComponentList<?> cl, double tblIdProp, double tblDescProp, int labelWidth, int dataWidth, PowerSystemComponentSettings pattern)
	{
		HBox pane = new HBox();
		tab.setContent(pane);
		
		SettingsView settingsView = new SettingsView(pattern, labelWidth, dataWidth);
		
		TableView<PowerSystemComponentSettings> tableView = generateTable(cl, tblIdProp, tblDescProp, settingsView);
		
		
		// STEP 5: Add to pane
		pane.getChildren().add(tableView);
		pane.getChildren().add(settingsView.getPane());
	}
	
	
	
	/**
	 * This helper function generates the tableview
	 * 
	 * @param cl
	 * @param tblIdProp
	 * @param tblDescProp
	 * @return
	 */
	private static TableView<PowerSystemComponentSettings> generateTable(ComponentList<?> cl, double tblIdProp, double tblDescProp, SettingsView sv)
	{
		TableView<PowerSystemComponentSettings> tableView = new TableView<>();
			
		TableColumn<PowerSystemComponentSettings, String> idColumn = 
			    new TableColumn<>("ID");
		
		idColumn.setCellValueFactory(
			    new PropertyValueFactory<>("id"));
		
		TableColumn<PowerSystemComponentSettings, String> descColumn = 
			    new TableColumn<>("Description");
		
		descColumn.setCellValueFactory(
			    new PropertyValueFactory<>("descriptor"));
				
		tableView.getColumns().add(idColumn);
		tableView.getColumns().add(descColumn);
		
		tableView.setPlaceholder(
			    new Label("No rows to display"));
		
		// Fixed Proportions of columns
		tableView.setPrefWidth(1920);
		tableView.setMaxWidth(1920);
		idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(tblIdProp));
		descColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(tblDescProp));
		
		
		// STEP3: Populate
		for(PowerSystemComponentSettings pscs : cl.getMap().values())
		{
			tableView.getItems().add(pscs);
		}
		
		
		// STEP 4: Add a listener to Component settings, if new data is added, we add it here as well 
		cl.setAddListener((newValue) -> {
			//Log.print(Lvl.INFO, "New component ["+newValue.getId()+"] added to FX Table." );
			tableView.getItems().add(newValue);
			});
		
		// STEP 5: Add a change listener to display and modify Item
		TableViewSelectionModel<PowerSystemComponentSettings> selectionModel = 
			    tableView.getSelectionModel();
		
		ObservableList<PowerSystemComponentSettings> selectedItems = 
			    selectionModel.getSelectedItems();

			selectedItems.addListener(
			  new ListChangeListener<PowerSystemComponentSettings>() {
			    @Override
			    public void onChanged(
			      Change<? extends PowerSystemComponentSettings> change) {
			        sv.setRef(change.getList().get(0));
			      }
			});
		
		return tableView;
	}
	
	
	/**
	 * 
	 * @param pane
	 * @param s
	 * @param width
	 */
	
	public static void generateSettingsViewGP(Pane pane, Settings s, int width)
	{
		// Generates a gridPane 
		GridPane gp = new GridPane();
		//Setting size for the pane  
	    gp.setMinSize(400, 200);
	    gp.getStyleClass().clear();
	    gp.getStyleClass().add("parameter-pane");
	    gp.setHgap(10); //horizontal gap in pixels => that's what you are asking for
	    gp.setVgap(5); //vertical gap in pixels
	    
	    
	    // A new counter to be used in foreach
	    AtomicInteger i = new AtomicInteger(0);
		
		s.getParameters().getMap().forEach((key, param) -> {
			
			Label 		label = new Label(param.getName());
			Label		unit  = new Label(param.getUnit());
			gp.add(label, 0, i.get());
			gp.add(unit, 2, i.get());
			GridPane.setValignment(label, VPos.TOP);
			
			if(param instanceof StringParameter)  // If it is a String parameter
			{
				int lines = ((StringParameter) param).getLines();
				if(lines > 0) // ANd it is supposed to be a long text
				{
					TextArea 	field = new TextArea(param.getVal().toString());
					field.setPrefWidth(width);
					field.setPrefRowCount(lines);
					field.setWrapText(true);
					bindProperties(field, param);
					gp.add(field, 1, i.get());
				}
				else
				{
					TextField 	field = new TextField(param.getVal().toString());
					field.setPrefWidth(width);
					bindProperties(field, param);
					gp.add(field, 1, i.get());
				}
			}else	// All other types //TODO: ComboBoxes for ENums
			{
				TextField 	field = new TextField(param.getVal().toString());
				field.setPrefWidth(width);
				bindProperties(field, param);
				gp.add(field, 1, i.get());
			}
			
			
			
			i.incrementAndGet();
		}
		);
		
		pane.getChildren().add(gp);	
	}
	
	
	public static void generateSettingsView(Pane pane, Settings s, int labelWidth, int width)
	{
	    
	    // A new counter to be used in foreach
	    //AtomicInteger i = new AtomicInteger(0);
		
		s.getParameters().getMap().forEach((key, param) -> {
			HBox		gp = new HBox(5);
			
			gp.setMinSize(400, 5);
			

		    gp.getStyleClass().clear();
		    gp.getStyleClass().add("parameter-pane");

		    
			Label 		label = new Label(param.getName());
			label.setPrefWidth(labelWidth);
			Label		unit  = new Label(param.getUnit());
			gp.getChildren().add(label);
			
			GridPane.setValignment(label, VPos.TOP);
			
			if(param instanceof StringParameter)  // If it is a String parameter
			{
				int lines = ((StringParameter) param).getLines();
				if(lines > 0) // ANd it is supposed to be a long text
				{
					TextArea 	field = new TextArea(param.getVal().toString());
					field.setPrefWidth(width);
					field.setPrefRowCount(lines);
					field.setWrapText(true);
					bindProperties(field, param);
					gp.getChildren().add(field);
				}
				else
				{
					TextField 	field = new TextField(param.getVal().toString());
					field.setPrefWidth(width);
					bindProperties(field, param);
					gp.getChildren().add(field);
				}
			}else if (param instanceof FileParameter) // If parameter is a pointer to a file
			{
				TextField 	field = new TextField(param.getVal().toString());
				field.setPrefWidth(width);
				bindProperties(field, param);
				gp.getChildren().add(field);
				// For opening files
				Button open = new Button("...");
				open.setPrefHeight(field.getPrefHeight()-5);
				open.setOnAction((e)->{
					FileChooser fileChooser = new FileChooser();			    	
			    	File f = fileChooser.showOpenDialog(pane.getScene().getWindow());
			    	
			    	if(f!=null)	{ field.setText(f.getAbsolutePath()); 	}
				});				
				gp.getChildren().add(open);
			}
			else	// All other types //TODO: ComboBoxes for ENums
			{
				TextField 	field = new TextField(param.getVal().toString());
				field.setPrefWidth(width);
				bindProperties(field, param);
				gp.getChildren().add(field);
			}
			
			gp.getChildren().add(unit);
			
			HBox.setMargin(gp, new Insets(0,0,0,0));
			
			pane.getChildren().add(gp);
		}
		);
		
		//pane.getChildren().add(gp);	
	}
	
	
	
	
	
	private static void bindProperties(TextInputControl node, Parameter<?> param)
	{
		node.textProperty().addListener((observable, oldValue, newValue) -> {
			try
			{
				param.setVal(newValue, false);
				node.setStyle(null);
			}catch(Exception e)
			{
				System.out.println("[E] Error writing parameter " + e.getLocalizedMessage());
				//e.printStackTrace();
				node.setStyle("-fx-text-fill: red;");
			}
			});
		param.setListener((newValue) -> {
				node.textProperty().set(newValue);
				});
		
	}
	
	
}
