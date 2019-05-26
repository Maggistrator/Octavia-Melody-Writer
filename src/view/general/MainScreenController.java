package view.general;

import java.io.File;
import java.io.IOException;
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
import model.project.observer.events.ProjectEvent;
import view.general.navigation.NavigationController;
import view.support.createproject.CreateProjectDialogController;

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
    
    ProjectManager manager;
    
    @FXML
    void initialize() {
        //--Загрузка панели навигации--//
        try {
            //инициализируем загузчик
            FXMLLoader loader = new FXMLLoader(getClass().getResource("navigation/navigation.fxml"));
            //создаём и устанавливаем контроллер
            NavigationController controller = new NavigationController();
            loader.setController(controller);
            //задаём Наблюдаемого
            controller.setProjectManager(manager);
            Parent root = loader.load();
            this.splitPane.getItems().add(0, root);
            splitPane.setDividerPosition(0, 0.2);
            //подписываем нового наблюдателя
            manager.subscribe(controller);
        } catch (IOException ex) {
            System.err.println("Не удалось загрузить панель навигации");
            ex.printStackTrace();
        }
        
        
        //--Загрузка панели контента--//
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../functions/autor_mode.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../support/createproject/create_project_window.fxml"));
            CreateProjectDialogController controller = new CreateProjectDialogController();
            loader.setController(controller);
            controller.setProjectManager(manager);
            Parent root = loader.load();
            //----------------------------------

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
    }
    
    public void setProjectManager(ProjectManager manager){
        this.manager = manager;
        
    }
    
    public void exitApplication(ActionEvent e) {
        System.exit(0);
    }
}
