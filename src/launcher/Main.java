package launcher;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
            primaryStage.show();
            setLoolAndFeelDecorated();
        } catch (IOException e) {
            System.out.println(String.format("Сценарий построения интерфейса повреждён, или отсутствует. Сообщение ошибки:\n%s", e.getMessage()));
            e.printStackTrace();
        }
    }
    
    private static void setLoolAndFeelDecorated() {
        //<editor-fold defaultstate="collapsed" desc="настройка Look and Feel">
        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Оформление окна отвалилось.. Причина: \n" + ex.getMessage());
        }
        //</editor-fold>
    }

}
