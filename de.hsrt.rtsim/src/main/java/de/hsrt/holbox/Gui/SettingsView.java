package de.hsrt.holbox.Gui;

import de.hsrt.holbox.project.powersystem.PowerSystemComponentSettings;
import de.hsrt.holbox.util.Parameter;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
/**
 * This class acts as a container for the settings view.
 * 
 * @author notholt
 *
 */

public class SettingsView{

	protected PowerSystemComponentSettings ref;
	protected ScrollPane fieldsPane;
	protected Pane pane;
	protected int labelWidth = 100;
	protected int dataWidth = 60;
	protected final int margin = 20;
	
	public SettingsView(PowerSystemComponentSettings init, int labelWidth, int dataWidth)
	{
		final int margin = 10;
		pane = new VBox();
		
		pane.setPrefWidth(labelWidth + 2*dataWidth + 2*margin);
		pane.setMinWidth(labelWidth + 2*dataWidth + 2*margin);
		HBox controls = new HBox();
		Button newBtn = new Button("New "+ init.getClass().getSimpleName() + "...");
		Button saveBtn = new Button("Save changes");
		
		controls.getChildren().add(newBtn);
		controls.getChildren().add(saveBtn);
		
		pane.getChildren().add(controls);
		
		this.labelWidth = labelWidth;
		this.dataWidth = dataWidth;
		
		this.fieldsPane = new ScrollPane();
		this.fieldsPane.setPrefWidth(labelWidth + 2*dataWidth + 2*margin);
		this.fieldsPane.setMinWidth(labelWidth + 2*dataWidth + 2*margin);
		
		pane.getChildren().add(this.fieldsPane);
		
		// Create Settings
		/*
		for(Parameter<?> p : init.getParameters().getMap().values())
		{
			HBox b = new HBox();
			b.getStyleClass().clear();
		    b.getStyleClass().add("parameter-pane");
		    
		    Label 		label = new Label(p.getName());
						label.setPrefWidth(labelWidth);
			Label		unit  = new Label(p.getUnit());
			
			b.getChildren().add(label);
			
			GridPane.setValignment(label, VPos.TOP);
			
			b.getChildren().add(unit);
			
			pane.getChildren().add(b);
			
		}*/
		
		
		
	}
	
	public Pane getPane()
	{
		return this.pane;
	}
	
	public void setRef(PowerSystemComponentSettings newRef)
	{
		this.ref = newRef;
		//Log.print(Lvl.DEBUG, "SettingsView:92 ID: "+ref.getId()+" Desc: " + ref.getDescriptor());
		layoutFields();
	}
	
	private void layoutFields()
	{

		
		VBox fields = new VBox();
		
		this.fieldsPane.setContent(fields);
		
		//Log.print(Lvl.DEBUG, "SettingsView:104 ID: "+ref.getId()+" Desc: " + ref.getDescriptor());
		
		for(Parameter<?> p : ref.getParameters().getMap().values())
		{
			HBox b = new HBox();
			b.getStyleClass().clear();
		    b.getStyleClass().add("parameter-pane");
		    
		    Label 		label = new Label(p.getName());
						label.setPrefWidth(this.labelWidth);
			Label		unit  = new Label(p.getUnit());
			
			b.getChildren().add(label);
			
			GridPane.setValignment(label, VPos.TOP);
			
			//Log.print(Lvl.DEBUG, "SettingsView:120 Param -> ["+p.getVarName()+"] = <"+p.getVal()+">");
			
			if(p.getVal() != null)
			{
				TextField 	field = new TextField(p.getVal().toString());
				field.setPrefWidth(this.dataWidth);
				b.getChildren().add(field);
				//Binding???
			}else
			{
				Button newVal = new Button("Null (create?)");
				b.getChildren().add(newVal);
			}
			
			
			
			
			
			
			
			b.getChildren().add(unit);
			
			fields.getChildren().add(b);
			
		}
	}
}
