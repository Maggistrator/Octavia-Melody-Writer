package controller;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.general.MainScreenController;
 
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/general/main_screen.fxml")); 
            MainScreenController controller = new MainScreenController();
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Octavia Melody Writer");
            primaryStage.setOnCloseRequest((e)->{System.exit(0);});
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(String.format("Сценарий построения интерфейса повреждён, или отсутствует. Сообщение ошибки:\n%s", e.getMessage()));
            e.printStackTrace();
        }
    }
}
