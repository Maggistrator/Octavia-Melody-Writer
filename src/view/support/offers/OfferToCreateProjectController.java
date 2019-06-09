package view.support.offers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import model.project.exceptions.ProjectLoadException;
import model.project.observer.ProjectManager;

/**
 * FXML Controller class
 *
 * @author Сова
 */
public class OfferToCreateProjectController implements Initializable {

    
    @FXML
    private Button createProjectButton;
    
    @FXML
    private Button loadProjectButton;
    
    ProjectManager manager = ProjectManager.getInstance();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void createProject(ActionEvent e){
            try {
            //загрузка рутпанели-наблюдателя
            Parent root = FXMLLoader.load(getClass().getResource("../modal/fxml/create_project_window.fxml"));
           
            //настройки модального окна
            Stage stage = new Stage();
            stage.setTitle("Создать проект");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
        public void loadProject(ActionEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select path to your project");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File dir = directoryChooser.showDialog(loadProjectButton.getScene().getWindow());
        if (dir != null) {
            try {
                manager.loadProject(dir.getAbsolutePath());
            } catch (ProjectLoadException ex) {
                if(ex.type == ProjectLoadException.Cause.PARSING){
                    //TODO: показать предложение восстановить проект, если он повреждён
                    JOptionPane.showMessageDialog(null, "При чтении файлов проекта произошла ошибка");
                }
                if(ex.type == ProjectLoadException.Cause.LOCATING){
                    //TODO: показать предложение создать новый проект в этой директории
                    JOptionPane.showMessageDialog(null, "Похоже, эта папка не является проектом Octavia Melody Writer");
                }
            }
        }
    }
}
