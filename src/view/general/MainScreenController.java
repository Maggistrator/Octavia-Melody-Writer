package view.general;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.project.Project;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
import model.project.observer.events.ProjectCreatedEvent;
import model.project.observer.events.ProjectEvent;
import view.general.navigation.NavigationController;
import view.support.createproject.CreateProjectDialogController;
import view.support.offers.OfferToCreateChapterController;

/**
 * Этот контроллер управляет основным окном, в которое встраиваются остальные.
 *
 * @author Сова
 */
public class MainScreenController implements ProjectListener{

    @FXML
    MenuItem createProjectMenuItem;

    @FXML
    MenuItem saveProjectMenuItem;

    @FXML
    MenuItem loadProjectMenuItem;

    @FXML
    MenuItem exitMenuItem;

    @FXML
    MenuItem chapterSettingsMenuItem;

    @FXML
    MenuItem createChapterMenuItem;

    @FXML
    MenuItem autorModeMenuItem;

    @FXML
    MenuItem translatorModeMenuItem;

    @FXML
    VBox toolbar;

    @FXML
    SplitPane splitPane;
    
    @FXML
    ScrollPane contentPane;
    
    ProjectManager manager = ProjectManager.getInstance();
    
    @FXML
    void initialize() {
        //--Загрузка панели навигации--//
        try {
            Parent root = FXMLLoader.load(getClass().getResource("navigation/navigation.fxml"));
            this.splitPane.getItems().add(0, root);
            splitPane.setDividerPosition(0, 0.2);
        } catch (IOException ex) {
            System.err.println("Не удалось загрузить панель навигации");
            ex.printStackTrace();
        }
        
        
        //--Загрузка панели контента--//
        try {
            FXMLLoader loader = new FXMLLoader();
            //Parent root = loader.load(getClass().getResource("../functions/autor_mode.fxml"));
            Parent root = loader.load(getClass().getResource("../support/offers/OfferToCreateProject.fxml"));
            this.contentPane.setContent(root);
        } catch (IOException ex) {
            System.err.println("Не удалось загрузить панель контента");
            ex.printStackTrace();
        }
        
        //создаём обработчики событий для главного окна
        createProjectMenuItem.addEventHandler(ActionEvent.ACTION, this::createProject);
        exitMenuItem.addEventHandler(ActionEvent.ACTION, this::exitApplication);
        
        //Main panel подписывает себя на события проекта
        manager.subscribe(this);
    }
    
    /**
     * Функция вызова окна по созданию нового проекта
     * @param e не очень-то нужное событие, но оно необходимо по контракту обработчика
     */
    public void createProject(ActionEvent e) {
        try {
            //загрузка рутпанели-наблюдателя
            Parent root = FXMLLoader.load(getClass().getResource("../support/createproject/create_project_window.fxml"));
           
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

    public void loadProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select path to your project");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File dir = directoryChooser.showDialog(null);
        if (dir != null) {
            Project project = Project.load(dir.getAbsolutePath());
        }
    }

    @Override
    public void dispatch(ProjectEvent e) {
        if(e instanceof ProjectCreatedEvent){
            try {
                
          FXMLLoader loader = new FXMLLoader(getClass().getResource("../support/offers/OfferToCreateChapter.fxml"));
          loader.setController(new OfferToCreateChapterController());
                Parent offerToCreateChapterPanel = loader.load();
                contentPane.setContent(offerToCreateChapterPanel);
            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void exitApplication(ActionEvent e) {
        System.exit(0);
    }
}
