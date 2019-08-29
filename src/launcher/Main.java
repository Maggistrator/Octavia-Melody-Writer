package launcher;
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
 
//Класс-лаунчер обязан наследоваться от класса Application
public class Main extends Application {
	
    public static void main(String[] args) {
        //метод launch отвечает за стэк вызовов, в том числе и метода start
        launch(args);
    }
    
    @Override
    //метод start отвечает за запуск приложения
    public void start(Stage primaryStage) {
        try {
            //процесс загрузки основного окна
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/general/main_screen.fxml")); 
            //дань архитектуре Model-View-Controller - класс-контроллер, отвечающий за связь с бизнес-логикой
            MainScreenController controller = new MainScreenController();
            loader.setController(controller);
            Parent root = loader.load();
            //Scene - своего рода Canvas, позволяющий прямую отрисовку
            Scene scene = new Scene(root);
            //Stage - аналог JFrame из Swing, или Window из awt
            primaryStage.setScene(scene);
            primaryStage.setTitle("Octavia Melody Writer");
            primaryStage.show();
            //изменение глобального стиля Swing-компонентов FX-приложения
            setLoolAndFeelDecorated();
        } catch (IOException e) {
            System.out.println(String.format("Сценарий построения интерфейса повреждён, или отсутствует. Сообщение ошибки:\n%s", e.getMessage()));
        }
    }
    
    //метод, отвечающий за смену стиля
    private static void setLoolAndFeelDecorated() {
        try {
            //тут используется оформление, характерное для Windows, 
            //не смотря на общую кроссплатформенность приложения
        	try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Не удалось применить новый стиль UI! Причина: \n" + ex.getMessage());
        }
    }

}
