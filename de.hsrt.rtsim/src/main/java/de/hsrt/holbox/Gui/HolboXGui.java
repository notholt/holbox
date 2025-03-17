package de.hsrt.holbox.Gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * @author notholt
 *
 */

public class HolboXGui extends Application {

    @Override
    public void start(Stage stage) throws Exception {
    	
    	// Starts GUI
    	
    	stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("HolboX GUI");
        stage.setMaximized(true);
        stage.setMinWidth(1048);
        stage.setMinHeight(748);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainGUI.fxml"));
    	
        Scene scene = new Scene(loader.load());
        
        // TODO: Controller
        GuiController controller = loader.<GuiController>getController();
        controller.setStage(stage);
        //controller.setCurrentProject(project);
        
        stage.setScene(scene);
        
        Image applicationIcon = new Image(getClass().getResourceAsStream("/fxml/img/icon.png"));
        stage.getIcons().add(applicationIcon);
       
        
        stage.show();
        
        

    }

    public static void main(String[] args) {
        launch(args);
    }

}
