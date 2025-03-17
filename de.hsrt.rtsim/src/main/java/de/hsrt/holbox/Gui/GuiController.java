package de.hsrt.holbox.Gui;




import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;


import de.hsrt.holbox.ScenarioManagement.Scenario;
import de.hsrt.holbox.ScenarioManagement.ScenarioFactory;
import de.hsrt.holbox.importExport.ExcelDatasetExporter;
import de.hsrt.holbox.project.Project;
import de.hsrt.holbox.project.dataset.DataManager;
import de.hsrt.holbox.util.Dataset;
import de.hsrt.holbox.util.Log;
import de.hsrt.holbox.util.Log.Lvl;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
//import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import de.hsrt.holbox.Economics.*;

public class GuiController  implements Initializable {
	
	private Stage parentStage;
	
	private Project currentProject;
	
	@FXML
	private HBox projectView;
	@FXML
	private VBox projectSettingsView;
	@FXML
	private VBox dataImportView;
	@FXML
	private Button prjButton;
	@FXML
	private Button dataImportButton;
	
	
	@FXML
	private VBox systemView;
	@FXML
	private Button equipButton;
	@FXML
	private WebView sysView;
	
	
	@FXML
	private VBox resultsView;
	@FXML
	private Button resultsButton;
	
	@FXML
	//private HBox economicsView;
	private TabPane economicsView;
	
	@FXML
	private HBox economicSettingsView;
	@FXML
	private Button economicsButton;
	@FXML
	private HBox economicsResultsView;
	@FXML
	private Button runEconomicsBtn;
	
	
	
	// QaD Solution for presntation
	
	@FXML
	private TextField pvNumMax;
	//@FXML
	//private TextField wtNumMax;
	@FXML
	private TextField battNumMax;	
	@FXML
	private Label pPvDG;
	@FXML
	private Label pPvOpt;
	@FXML
	private Label pPvSavings;
	@FXML
	private Label pBattDG;
	@FXML
	private Label pBattOpt;
	@FXML
	private Label pBattSavings;	
	@FXML
	private Label pDgDG;
	@FXML
	private Label pDgOpt;
	@FXML
	private Label pDgSavings;	
	@FXML
	private Label ldEgyDG;
	@FXML
	private Label ldEgyOpt;
	@FXML
	private Label ldEgySavings;
	@FXML
	private Label pvEgyDG;
	@FXML
	private Label pvEgyOpt;
	@FXML
	private Label pvEgySavings;
	@FXML
	private Label wtEgyDG;
	@FXML
	private Label wtEgyOpt;
	@FXML
	private Label wtEgySavings;
	@FXML
	private Label dgEgyDG;
	@FXML
	private Label dgEgyOpt;
	@FXML
	private Label dgEgySavings;	
	@FXML
	private Label fuelVolDG;
	@FXML
	private Label fuelVolOpt;
	@FXML
	private Label fuelVolSavings;
	@FXML
	private Label dgTmDG;
	@FXML
	private Label dgTmOpt;
	@FXML
	private Label dgTmSavings;
	@FXML
	private Label rfDG;
	@FXML
	private Label rfOpt;
	@FXML
	private Label rfSavings;
	@FXML
	private Label lcoeDG;
	@FXML
	private Label lcoeOpt;
	@FXML
	private Label lcoeSavings;
	@FXML
	private Label costDG;
	@FXML
	private Label costOpt;
	@FXML
	private Label costSavings;
	private double pvScale = 1;
	private double battScale = 1;
	
	
	private TabPane battPane;
	private ArrayList<GridPane> pvWtPane;
	
	
	@FXML
    private WebView map;
	
	@FXML
	private TextArea logs;
	
	private Visu inputDataVisu = new Visu();
	private Visu outputDataVisuP = new Visu();	
	private Visu outputDataVisuU = new Visu();
	
	public void initialize(URL url, ResourceBundle rb) {

		this.currentProject = new Project();


		SettingsFxNodeFactory.generateSettingsView(projectSettingsView,
													getClass().getResource("/fxml/ProjectGeneralView.fxml"),
													currentProject.getProjectSettings(), 200 , 200);
		
		SettingsFxNodeFactory.generateSettingsView(dataImportView,
													getClass().getResource("/fxml/DataImportView.fxml"),
													currentProject.getDatasetSettings(), 180 , 100);
		
		SettingsFxNodeFactory.generatePowerSystemSettingsView(systemView,
													getClass().getResource("/fxml/PowerSystemSettingsView.fxml"),
													currentProject.getPowerSystemSettings(), 300 , 100);
		
		
		SettingsFxNodeFactory.generateSettingsView(economicSettingsView,
													getClass().getResource("/fxml/EconomicSettingsView.fxml"),
													currentProject.getEconomicSettings(), 300 , 100);
		
		
		Button btn = new Button();
		btn.setText("Load Data...");
		btn.setMinSize(100, 50);
		inputDataVisu.setPrefHeight(1080);
		btn.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		        loadData();
		    }
		});
		
		
		dataImportView.getChildren().add(btn);
		
		dataImportView.getChildren().add(inputDataVisu.getNode());

		Button btn2 = new Button();
		btn2.setText("Run simulation!");
		btn2.setMinSize(300, 40);
		btn2.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		        runSimulation();
		    }
		});
		
		resultsView.getChildren().add(btn2);
		
		resultsView.getChildren().add(outputDataVisuP.getNode());
		//resultsView.getChildren().add(outputDataVisuU.getNode());

		// Set log
		PrintStream out = new PrintStream(System.out) {
            @Override
            public void print(String text) {
                logs.appendText(text + "\n");
            }
        };
		Log.setOut(out);
		
		// Set Active view
		setActive(0);
	}

    public void setStage(Stage s)
    {
        this.parentStage = s;
    }
    

    
    @FXML
    void saveProjectAs()
    {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("project.xml");
    	fileChooser.getExtensionFilters().addAll(
    			new FileChooser.ExtensionFilter("HolboXML File", "*.xml")
    	);
    	
    	File f = fileChooser.showSaveDialog(parentStage);
    	
    	if(f!= null) // If user has chosen a file
    	{
	    		/*
		    	if(f.exists()) // If file exists ask for confirmation (HANDLED BY OS in Windows??)
		    	{
		    		Alert a = new Alert(AlertType.CONFIRMATION);
		    		a.setTitle("Confirmation Dialog");
		    		a.setHeaderText("Look, a Confirmation Dialog");
		    		a.setContentText("Are you ok with this?");
		    		a.showAndWait();
		    		Optional<ButtonType> result = a.showAndWait();
		    		if (result.get() == ButtonType.OK){
		    			
		    		}else
		    		{
		    			
		    		}
		    	}*/
    		
	    	this.currentProject.saveXML(f);
	    	this.currentProject.setFile(f);
	    	
    	}
    	
    }
    
    
	@FXML
	void saveProject()
	{
		try
		{
			this.currentProject.saveXML();
		}catch(Exception e)
		{
			System.out.println("[W] Project not yet saved, using Save As...");
			saveProjectAs();
		}
	}
	
	@FXML
	void openProject()
	{
		FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("project.xml");
    	fileChooser.getExtensionFilters().addAll(
    			new FileChooser.ExtensionFilter("HolboXML File", "*.xml")
    	);
    	
    	File f = fileChooser.showOpenDialog(parentStage);
    	
    	if(f!=null)
    	{
    		this.currentProject.readXML(f.getAbsolutePath());
    	}
		
	}
	
	@FXML
	void newProject()
	{
		System.out.println("[I] Reset project to defaults");
		this.currentProject.resetProjectToDefaults();
	}
	
	
	@FXML
	void setActivePrj()
	{
		setActive(0);
	}
	

	@FXML
	void setActiveEquip()
	{
		setActive(2);
	}
	
	@FXML
	void setActiveImport()
	{
		setActive(1);
	}
	
	@FXML
	void setActiveResults()
	{
		setActive(3);
	}
	
	@FXML
	void setActiveEconomics()
	{
		setActive(4);
	}
	
	
	
	@FXML 
	void openDebug()
	{
		sysView.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
	}

	
	private void setActive(int number)
	{
		Node[] nodes = new Node[5];
		nodes[0] = projectView;
		nodes[1] = dataImportView;
		nodes[2] = systemView;
		nodes[3] = resultsView;
		nodes[4] = economicsView;
		
		Button[] buttons = new Button[5];
		buttons[0] = prjButton;
		buttons[1] = dataImportButton;
		buttons[2] = equipButton;
		buttons[3] = resultsButton;
		buttons[4] = economicsButton;
		
		
		for(int i = 0; i<nodes.length; i++)
		{
			nodes[i].setVisible(false);
			nodes[i].setManaged(false);
			buttons[i].getStyleClass().clear();
			buttons[i].getStyleClass().add("btn-process");
		}
		
		nodes[number].setVisible(true);
		nodes[number].setManaged(true);
		buttons[number].getStyleClass().clear();
		buttons[number].getStyleClass().add("btn-process-selected");
		
	}
	
	@FXML
	void loadData()
	{
		try
		{
			DataManager.loadDataset(new File(this.currentProject.getDatasetSettings().getParameters().getElementById("resFile").getVal().toString()), 
									this.currentProject.getDatasetSettings());
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "While loading Dataset: " + e.getMessage());
		}
		
		//this.inputDataVisu.setDataset(currentProject.getInputDataset()); // TODO: Marked for deletion
		this.inputDataVisu.setDataset(DataManager.getInputDataset());
	}
	
	@FXML
	void saveResultDataset()
	{
		LocalDateTime now = LocalDateTime.now();
		String today = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH'h'mm"));
		try {
			ExcelDatasetExporter.writeToFile(this.currentProject.getFile().getParent()+today+"_resultsDataset.xlsx", DataManager.getResultsDataset(), this.currentProject);
		} catch (Exception e) {
			Log.print(Lvl.ERROR, "Error writing file: " + e.getMessage());
		}
	}
	
	void runSimulation()
	{
		// Scenario testing.
		ScenarioFactory sf = new ScenarioFactory(this.currentProject.getPowerSystemSettings());
		try 
		{
			sf.init(true);
			sf.setPvScale(this.pvScale);
			sf.setBattScale(this.battScale);
			Log.print(Lvl.SUCCESS, "Scenario successfully initialized");
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "Initializing the Scenario with reason: " + e.getMessage());
			//e.printStackTrace();
		}
		
		try 
		{
			Scenario s = sf.getScenario();
			s.runScenario(true);
			Log.print(Lvl.SUCCESS, "Scenario successfully run");
			
			int[] indexesP = getPMIndexes(DataManager.getResultsDataset());
			
			this.outputDataVisuP.setDataset(DataManager.getResultsDataset(), indexesP);
			this.outputDataVisuU.setDataset(DataManager.getIntermediateResultsDataset());
			
			
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "Running scenario with reason: " + e.getMessage());
		}
		
	}
	
	
	private int[] getPMIndexes(Dataset ds)
	{
		ArrayList<Integer> indexes = new ArrayList<>();
		
		for(int i = 0; i<ds.getSignals().size(); i++)
		{
			//System.out.println(ds.getSignals().get(i).getVarName());
			if(ds.getSignals().get(i).getVarName().contains("Tot"))
			{
				indexes.add(i);
				System.out.println(ds.getSignals().get(i).getVarName());
			}
		}
		
		int[] result = new int[indexes.size()];
		for(int i = 0; i<result.length; i++)
		{
			result[i] = indexes.get(i);
		}
		return result;
	}

	
	
	@FXML
	void runEconomics()
	{
		Log.print(Lvl.INFO, "Running economic sweep... please wait");
		
		int numPv = Integer.parseInt(this.pvNumMax.getText());
		//int numWind = Integer.parseInt(this.wtNumMax.getText());
		int numBatt = Integer.parseInt(this.battNumMax.getText());
		
		// Scenario testing.
		ScenarioFactory sf = new ScenarioFactory(this.currentProject.getPowerSystemSettings());
		try 
		{
			sf.init(true);
			Log.print(Lvl.SUCCESS, "Scenario successfully initialized");
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "Initializing the Scenario with reason: " + e.getMessage());
			//e.printStackTrace();
		}
		
		try 
		{
			double lcoemin = 1000.0;
			LCOE lcoeObj = new LCOE(new EconomicSettings(), sf);
			int iWt = 1;
			
			double genEgyTot = 0.1;
			double fuelVolTot =0.1;
			double runTm = 0.1;
			double lcoeBase = 1;
		
			
			for(int iPv = 0; iPv <= numPv ; iPv+=2)
			{
				for(int iBatt = 0; iBatt <= numBatt ; iBatt+=2)
				{
					Scenario s = sf.getScenario();
					sf.setBattScale(iBatt+0.001);
					sf.setPvScale(iPv);
					sf.setWtScale(iWt);
					s.runScenario(false);
					double lcoe = lcoeObj.calculateLCOE(iPv, iBatt);
					
					if (iBatt==0 && iPv == 0)
					{
						this.pPvDG.setText(String.format("%1$,.0f kWp /", s.getPvPwrAtNomTot()*iPv)
								+String.format("%1$,.0f kW", s.getWtPwrAtNomAvg()*iWt));
						//this.pPvOpt.setText(String.format("%1$,.0f kWp / %1$,.0f kW", s.getPvPwrAtNomTot()*iPv, s.getWtPwrAtNomAvg()*iWt));
						this.pPvSavings.setText("");
						this.pBattDG.setText(String.format("%1$,.0f kWh / ", s.getBattEgyNomTot()*iBatt)
											+ String.format("%1$,.0f kW",  s.getBattPwrToEgyRatioAvg()*s.getBattEgyNomTot()*iBatt));
						this.pBattOpt.setText(String.format("%1$,.0f kWh / ", s.getBattEgyNomTot()*iBatt)
								+ String.format("%1$,.0f kW",  s.getBattPwrToEgyRatioAvg()*s.getBattEgyNomTot()*iBatt));
						this.pBattSavings.setText("");	
						this.pDgDG.setText(String.format("%1$,.0f ", s.getGenRunNb())+ String.format("x %1$,.0f kVA", s.getGenRunPwrAtAvg()));
						this.pDgOpt.setText(String.format("%1$,.0f ", s.getGenRunNb())+ String.format("x %1$,.0f kVA", s.getGenRunPwrAtAvg()));
						this.pDgSavings.setText("");	
						this.ldEgyDG.setText(String.format("%1$,.0f MWh", s.getLdEgyTot()/1000.0));
						this.ldEgyOpt.setText(String.format("%1$,.0f MWh", s.getLdEgyTot()/1000.0));
						this.ldEgySavings.setText("");
						this.pvEgyDG.setText(String.format("%1$,.0f MWh", s.getPvEgyTot()/1000.0));
						this.pvEgyOpt.setText(String.format("%1$,.0f MWh", s.getPvEgyTot()/1000.0));
						this.pvEgySavings.setText("");
						this.wtEgyDG.setText(String.format("%1$,.0f MWh", s.getWtEgyTot()/1000.0));
						this.wtEgyOpt.setText(String.format("%1$,.0f MWh", s.getWtEgyTot()/1000.0));
						this.wtEgySavings.setText("");
						this.dgEgyDG.setText(String.format("%1$,.0f MWh", s.getGenEgyTot()/1000.0));
						genEgyTot = s.getGenEgyTot();
						this.dgEgyOpt.setText(String.format("%1$,.0f MWh", s.getGenEgyTot()/1000.0));
						this.dgEgySavings.setText(String.format("%1$,.1f%%", 100*(1-(s.getGenEgyTot()/genEgyTot))));
						this.fuelVolDG.setText(String.format("%1$,.0f m³", s.getGenFuelVolTot()/1000.0));
						fuelVolTot = s.getGenFuelVolTot();
						this.fuelVolOpt.setText(String.format("%1$,.0f m³", s.getGenFuelVolTot()/1000.0));
						this.fuelVolSavings.setText(String.format("%1$,.1f%%", 100*(1-(s.getGenFuelVolTot()/fuelVolTot))));
						this.dgTmDG.setText(String.format("%1$,.0f h", s.getGenRunTm()));
						runTm = s.getGenRunTm();
						this.dgTmOpt.setText(String.format("%1$,.0f h", s.getGenRunTm()));
						this.dgTmSavings.setText(String.format("%1$,.1f%%", 100*(1-(s.getGenRunTm()/runTm))));
						this.rfDG.setText(String.format("%1$,.1f%%", 100.0*(s.getPvEgyTot()+s.getWtEgyTot())/s.getLdEgyTot()));
						this.rfOpt.setText(String.format("%1$,.1f%%", 100.0*(s.getPvEgyTot()+s.getWtEgyTot())/s.getLdEgyTot()));
						this.rfSavings.setText("");
						this.lcoeDG.setText(String.format("%1$,.4f USD/kWh", lcoe));
						lcoeBase = lcoe;
						this.lcoeOpt.setText(String.format("%1$,.4f USD/kWh", lcoe));
						this.lcoeSavings.setText(String.format("%1$,.0f%%", 100*(1-(lcoe/lcoeBase))));
						this.costDG.setText(String.format("%1$,.0f USD", lcoe*s.getLdEgyTot()));
						this.costOpt.setText(String.format("%1$,.0f USD", lcoe*s.getLdEgyTot()));
						this.costSavings.setText(String.format("%1$,.0f USD", (lcoeBase-lcoe)*s.getLdEgyTot()));
						
					}
					
					if(lcoe < lcoemin)
					{
						//this.pPvDG.setText(String.format("%1$,.0f kWp", s.getPvPwrAtNomTot()*iPv)
						//		+String.format("%1$,.0f kW", s.getWtPwrAtNomAvg()*iWt));
						this.pPvOpt.setText(String.format("%1$,.0f kWp / ", s.getPvPwrAtNomTot()*iPv)
											+String.format("%1$,.0f kW", s.getWtPwrAtNomAvg()*iWt));
						this.pPvSavings.setText("");
						//this.pBattDG.setText(String.format("%1$,.0f kW / ", s.getBattEgyNomTot()*iBatt)
						//		+ String.format("%1$,.0f kW / ",  s.getBattPwrToEgyRatioAvg()*s.getBattEgyNomTot()*iBatt));
						this.pBattOpt.setText(String.format("%1$,.0f kWh / ", s.getBattEgyNomTot()*iBatt)
								+ String.format("%1$,.0f kW",  s.getBattPwrToEgyRatioAvg()*s.getBattEgyNomTot()*iBatt));
						this.pBattSavings.setText("");	
						//this.pDgDG.setText(String.format("%1$,.0f ", s.getGenRunNb())+ String.format("x %1$,.0f kVA", s.getGenRunPwrAtAvg()));
						this.pDgOpt.setText(String.format("%1$,.0f ", s.getGenRunNb())+ String.format("x %1$,.0f kVA", s.getGenRunPwrAtAvg()));
						this.pDgSavings.setText("");	
						//this.ldEgyDG.setText(String.format("%1$,.0f MWh", s.getLdEgyTot()/1000.0));
						this.ldEgyOpt.setText(String.format("%1$,.0f MWh", s.getLdEgyTot()/1000.0));
						this.ldEgySavings.setText("");
						//this.pvEgyDG.setText(String.format("%1$,.0f MWh", s.getPvEgyTot()/1000.0));
						this.pvEgyOpt.setText(String.format("%1$,.0f MWh", s.getPvEgyTot()/1000.0));
						this.pvEgySavings.setText("");
						//this.wtEgyDG.setText(String.format("%1$,.0f MWh", s.getWtEgyTot()/1000.0));
						this.wtEgyOpt.setText(String.format("%1$,.0f MWh", s.getWtEgyTot()/1000.0));
						this.wtEgySavings.setText("");
						//this.dgEgyDG.setText(String.format("%1$,.0f MWh", s.getGenEgyTot()/1000.0));
						//genEgyTot = s.getGenEgyTot();
						this.dgEgyOpt.setText(String.format("%1$,.0f MWh", s.getGenEgyTot()/1000.0));
						this.dgEgySavings.setText(String.format("%1$,.1f%% \u2198", 100*(1-(s.getGenEgyTot()/genEgyTot))));
						//this.fuelVolDG.setText(String.format("%1$,.0f m³", s.getGenFuelVolTot()/1000.0));
						//fuelVolTot = s.getGenFuelVolTot();
						this.fuelVolOpt.setText(String.format("%1$,.0f m³", s.getGenFuelVolTot()/1000.0));
						this.fuelVolSavings.setText(String.format("%1$,.1f%% \u2198", 100*(1-(s.getGenFuelVolTot()/fuelVolTot))));
						//this.dgTmDG.setText(String.format("%1$,.0f h", s.getGenRunTm()));
						//runTm = s.getGenRunTm();
						this.dgTmOpt.setText(String.format("%1$,.0f h", s.getGenRunTm()));
						this.dgTmSavings.setText(String.format("%1$,.1f%% \u2198", 100*(1-(s.getGenRunTm()/runTm))));
						//this.rfDG.setText(String.format("%1$,.0f%%", 100.0*(s.getPvEgyTot()+s.getWtEgyTot())/s.getLdEgyTot()));
						this.rfOpt.setText(String.format("%1$,.1f%%", 100.0*(s.getPvEgyTot()+s.getWtEgyTot())/s.getLdEgyTot()));
						this.rfSavings.setText("");
						//this.lcoeDG.setText(String.format("%1$,.4f USD/kWh", lcoe));
						//lcoeBase = lcoe;
						this.lcoeOpt.setText(String.format("%1$,.4f USD/kWh", lcoe));
						this.lcoeSavings.setText(String.format("%1$,.1f%% \u2198", 100*(1-(lcoe/lcoeBase))));
						//this.costDG.setText(String.format("%1$,.0f USD", lcoe*s.getLdEgyTot()));
						this.costOpt.setText(String.format("%1$,.0f USD", lcoe*s.getLdEgyTot()));
						this.costSavings.setText(String.format("%1$,.0f USD", (lcoeBase-lcoe)*s.getLdEgyTot()));
						lcoemin = lcoe;
						//s.printResults();
						this.pvScale = iPv;
						this.battScale = iBatt;
					}
				}
			}
			
			
			
		}catch(Exception e)
		{
			Log.print(Lvl.ERROR, "Running scenario with reason: " + e.getMessage());
		}
		
	}
	
	
	
	void initEconomicsTable(int numPv, int numWt, int numBatt)
	{
		this.economicsResultsView.getChildren().removeAll();
		this.economicsResultsView.setPrefSize(1920, 1080);
		this.pvWtPane = new ArrayList<>(0);
		this.battPane = new TabPane();
		this.battPane.setPrefSize(1920, 1080);
		battPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		for(int i = 0; i < numBatt; i++)
		{
			Tab tab = new Tab("Batteries: "+ i);
			GridPane gp = new GridPane();
			gp.setVgap(20);
			gp.setHgap(20);
			for(int j = 0; j<numPv; j++) 
			{
				gp.add(new Label("PV: "+ j ), j+1, 0);
			}
			
			for (int k=0;k<numWt; k++)
			{
				gp.add(new Label("WT: "+k ), 0, k+1);
			}
			pvWtPane.add(gp);
			tab.setContent(gp);
			battPane.getTabs().add(tab);
		}
		
		this.economicsResultsView.getChildren().add(battPane);
	}
	
	void updateTable(int pvi, int wti, int batti, double lcoe, double minLCOE, double maxLCOE)
	{
		GridPane gp = pvWtPane.get(batti);
		Label labl = new Label(String.format(Locale.UK, "%.4f", lcoe));
		gp.add(labl, pvi+1, wti+1);
	}
	
	void recolorAll(int numPv, int numWt, int numBatt, double minLCOE, double maxLCOE)
	{
		for(int i = 0; i < numBatt; i++)
		{
			GridPane gp = (GridPane)this.battPane.getTabs().get(i).getContent();
			for(Node n : gp.getChildren()) 
			{
				
				Label l = (Label)n;
				//System.out.println("FOund value "+l.getText());
				try {
					double val = Double.parseDouble(l.getText());
					double hue = 120 - 120*(val-minLCOE)/Math.max((maxLCOE-minLCOE), 0.001);
					l.setTextFill(Color.hsb(hue, 1.0, 0.75));
					//l.setBackground(new Background(new BackgroundFill(Color.hsb(hue, 1.0, 0.75), CornerRadii.EMPTY, new Insets(0))));
				}catch(Exception e)
				{
					//System.out.println("Ingored value "+l.getText());
				}
			}
		}
	}
	
}
