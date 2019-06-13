package view.general;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import model.project.Project;
import model.project.exceptions.ProjectLoadException;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
import model.project.observer.events.ProjectEvent;
import model.project.observer.events.ProjectLoadedEvent;
import model.project.observer.events.ProjectNodeCreatedEvent;
import view.general.navigation.simple.SimpleNavigationController;
import view.support.modal.controllers.CreateArchController;
import view.support.modal.controllers.CreateChapterDialogController;
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
    MenuItem createArchMenuItem;

    @FXML
    MenuItem autorModeMenuItem;

    @FXML
    MenuItem translatorModeMenuItem;

    @FXML
    VBox toolbar;

    @FXML
    SplitPane splitPane;
    
    @FXML
    TabPane contentPane;
    
    ProjectManager manager = ProjectManager.getInstance();
    
    @FXML
    void initialize() {
        //--Загрузка панели навигации--//
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("navigation/simple/simple_navigation_bar.fxml"));
            SimpleNavigationController controller = new SimpleNavigationController();
            controller.setContentPane(contentPane);
            loader.setController(controller);
            Parent root = loader.load();
            
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
            this.contentPane.getTabs().add(new Tab("General", root));
        } catch (IOException ex) {
            System.err.println("Не удалось загрузить панель контента");
            ex.printStackTrace();
        }
        
        //создаём обработчики событий для главного окна
        createProjectMenuItem.addEventHandler(ActionEvent.ACTION, this::createProject);
        loadProjectMenuItem.addEventHandler(ActionEvent.ACTION, this::loadProject);
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
            Parent root = FXMLLoader.load(getClass().getResource("../support/modal/fxml/create_project_window.fxml"));
           
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

        File dir = directoryChooser.showDialog(contentPane.getScene().getWindow());
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

    @Override
    public void dispatch(ProjectEvent e) {
        if (e instanceof ProjectNodeCreatedEvent) {
            ProjectNodeCreatedEvent event = ((ProjectNodeCreatedEvent) e);
            
            if (event.newNode instanceof Project)
                processProjectCreatedEvent(e);
        }
        
        if(e instanceof ProjectLoadedEvent){
            processProjectCreatedEvent(e);
        }
    }
    
    private void processProjectCreatedEvent(ProjectEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../support/offers/OfferToCreateChapter.fxml"));
            OfferToCreateChapterController controller = new OfferToCreateChapterController();
            controller.setParent(contentPane);
            loader.setController(controller);
            Parent offerToCreateChapterPanel = loader.load();
            this.contentPane.getTabs().clear();
            this.contentPane.getTabs().add(new Tab(manager.getProject().name, offerToCreateChapterPanel));
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void createChapter(Event e){
        try {
            if(manager.getProject() != null){
                //загружаем диалог создания главы
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../support/modal/fxml/create_chapter.fxml"));
                CreateChapterDialogController controller = new CreateChapterDialogController();
                controller.setParent(contentPane);
                loader.setController(controller);
                Parent root = loader.load();

                // создаём  новую вкладку и добавляем её на панель
                Tab newChapterCreation = new Tab("Создать главу", root);
                contentPane.getTabs().add(newChapterCreation);

                //выбираем новенькую вкладку как текущую
                SingleSelectionModel<Tab> selectionModel = contentPane.getSelectionModel();
                selectionModel.select(newChapterCreation); 
            } else {
                JOptionPane.showMessageDialog(null, "Создайте или загрузите проект!");
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void createArch() {
        try {
            if(manager.getProject() != null){
                //загружаем диалог создания главы
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../support/modal/fxml/create_arch.fxml"));
                CreateArchController controller = new CreateArchController();
                controller.setParent(contentPane);
                loader.setController(controller);
                Parent root = loader.load();

                // создаём  новую вкладку и добавляем её на панель
                Tab newArchCreation = new Tab("Создать арку", root);
                contentPane.getTabs().add(newArchCreation);

                //выбираем новенькую вкладку как текущую
                SingleSelectionModel<Tab> selectionModel = contentPane.getSelectionModel();
                selectionModel.select(newArchCreation);
            } else {
                JOptionPane.showMessageDialog(null, "Создайте или загрузите проект!");
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exitApplication(ActionEvent e) {
        System.exit(0);
    }
}
