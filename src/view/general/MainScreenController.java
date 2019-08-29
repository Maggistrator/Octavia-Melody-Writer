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
 */
public class MainScreenController implements ProjectListener{
    /*
    * У JavaFX есть механизм уникальных идентификаторо элементов пользовательского
    * интерфейса - т.н. fx-id. Этот механизм позволяет обращаться к элементу игнорируя инкапсуляцию.
    */
    @FXML
    //пункт основного меню, отвечающий за возможность создания нового проекта
    MenuItem createProjectMenuItem;

    @FXML
    //пункт меню, отвечающий за возможность загрузки проекта из файловой системы
    MenuItem loadProjectMenuItem;

    @FXML
    //пункт меню, позволяющий завершить работу приложения
    MenuItem exitMenuItem;

    @FXML
    //пункт меню, позволяющий создать новую главу в существующем проекте
    MenuItem createChapterMenuItem;
    
    @FXML
    //пункт меню, позволяющий создать новую арку 
    MenuItem createArchMenuItem;

    @FXML
    //панель навигации, заданная панелью с вертикальным layout-менеджером компоновки
    VBox toolbar;

    @FXML
    //панель с динамическим разделителем - управляет процентным соотношением 
    //занимаемого основными элементами UI места
    SplitPane splitPane;
    
    @FXML
    //основная панель контента - вкладочная панель, для размещения функций
    TabPane contentPane;
    
    //Менеджер проектов, о коем позже
    ProjectManager manager = ProjectManager.getInstance();
    
    @FXML
    void initialize() {
        //--Загрузка панели навигации--//
        try {
            //подробности этого механизма описаны при создании основного окна
            FXMLLoader loader = new FXMLLoader(getClass().getResource("navigation/simple/simple_navigation_bar.fxml"));
            SimpleNavigationController controller = new SimpleNavigationController();
            //контроллеру запрещено иметь пользовательский конструктор, и этот 
            //метод реализует передачу панели контента в другой контроллер
            controller.setContentPane(contentPane);
            loader.setController(controller);
            Parent root = loader.load();
            
            //установка панели навигации в левую часть панели с динамическим разделителем
            this.splitPane.getItems().add(0, root);
            //выделение панели навигации 20% свободного места экрана по ширине
            splitPane.setDividerPosition(0, 0.2);
        } catch (IOException ex) {
            System.err.println("Не удалось загрузить панель навигации");
        }
        
        //загрузка начального экрана приложения, с предложением создать или загрузитьь проект
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../support/offers/OfferToCreateProject.fxml"));
            this.contentPane.getTabs().add(new Tab("General", root));
        } catch (IOException ex) {
            System.err.println("Не удалось загрузить начальный экран");
        }
        
        //создание обработчиков событий для главного окна
        createProjectMenuItem.addEventHandler(ActionEvent.ACTION, this::createProject);
        loadProjectMenuItem.addEventHandler(ActionEvent.ACTION, this::loadProject);
        exitMenuItem.addEventHandler(ActionEvent.ACTION, this::exitApplication);
        
        //Main panel подписывает себя на события проекта через предусмотренный метод паттерна Observer
        manager.subscribe(this);
    }
    
    /**
     * Функция вызова окна по созданию нового проекта
     * @param e событие необходимое по контракту обработчика
     */
    public void createProject(ActionEvent e) {
        try {
            //загрузка рутпанели-наблюдателя
            Parent root = FXMLLoader.load(getClass().getResource("../support/modal/fxml/create_project_window.fxml"));
           
            //настройки модального окна
            Stage stage = new Stage();
            stage.setTitle("Создать проект");
            stage.setScene(new Scene(root));
            //отключение возможности изменять размеры модального окна
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            //метод, задающий реальную модальность: 
            //блокирование ввода до тех пор, пока это окно не закончит работу
            stage.showAndWait();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Функция загрузки проекта 
     * @param e событие необходимое по контракту обработчика
     */
    public void loadProject(ActionEvent e) {
        //DirectoryChooser - класс, реализующий стандартное окно выбора папки
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите корневую директорию проекта:");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File dir = directoryChooser.showDialog(contentPane.getScene().getWindow());
        if (dir != null) {
            try {
                //непосредственная загрузка проекта при помощи менеджера проектов
                manager.loadProject(dir.getAbsolutePath());
            } catch (ProjectLoadException ex) {
                //обработка пользовательского исключения на случай повреждения файла метаинформации
                if(ex.type == ProjectLoadException.Cause.PARSING){
                    JOptionPane.showMessageDialog(null, "При чтении файлов проекта произошла ошибка");
                }
                //ошибка поиска служебного файла - вероятно, это значит, что корневая директория выбрана неверно
                if(ex.type == ProjectLoadException.Cause.LOCATING){
                    JOptionPane.showMessageDialog(null, "Похоже, эта папка не является проектом Octavia Melody Writer");
                }
            }
        }
    }

    @Override
    public void dispatch(ProjectEvent e) {
        //обработка создания нового узла проекта
        if (e instanceof ProjectNodeCreatedEvent) {
            ProjectNodeCreatedEvent event = ((ProjectNodeCreatedEvent) e);
            //если узлом проекта оказывается сам проект, то вызывается соответствующая функция
            if (event.newNode instanceof Project)
                processProjectCreatedEvent(e);
        }
        
        //событие загрузки проекта обрабатывается отдельно
        if(e instanceof ProjectLoadedEvent){
            processProjectCreatedEvent(e);
        }
    }
    
    /**
     * Отдельный обработчик события создания проекта
     */
    private void processProjectCreatedEvent(ProjectEvent e) {
        try {
            //Загрузка панели быстрого начала работы с проектом
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../support/offers/OfferToCreateChapter.fxml"));
            OfferToCreateChapterController controller = new OfferToCreateChapterController();
            controller.setParent(contentPane);
            loader.setController(controller);
            Parent offerToCreateChapterPanel = loader.load();
            //очистка панели контента, необходимая на случай создания нового проекта после работы со старым
            this.contentPane.getTabs().clear();
            //добавление новосозданной панели быстрого начала работы как новой вкладки
            this.contentPane.getTabs().add(new Tab(manager.getProject().name, offerToCreateChapterPanel));
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    /**
     * Функция создания главы
     * Отвечает за загрузку в панель контента соответствующей формы
     */
    private void createChapter(Event e){
        try {
            //для создания главы необходим открытый проект!
            if(manager.getProject() != null){
                //загружаем диалог создания главы
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../support/modal/fxml/create_chapter.fxml"));
                CreateChapterDialogController controller = new CreateChapterDialogController();
                controller.setParent(contentPane);
                loader.setController(controller);
                Parent root = loader.load();

                //создаём новую вкладку и добавляем её на панель
                Tab newChapterCreation = new Tab("Создать главу", root);
                contentPane.getTabs().add(newChapterCreation);

                //выбираем новосозданную вкладку как текущую
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
    /**
     * Функция создания новой арки
     */
    private void createArch() {
        try {
            //требует существования открытого или созданного проекта!
            if(manager.getProject() != null){
                //загружаем диалог создания главы
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../support/modal/fxml/create_arch.fxml"));
                CreateArchController controller = new CreateArchController();
                controller.setParent(contentPane);
                loader.setController(controller);
                Parent root = loader.load();

                //создаём новую вкладку и добавляем её на панель
                Tab newArchCreation = new Tab("Создать арку", root);
                contentPane.getTabs().add(newArchCreation);

                //выбираем новосозданную вкладку как текущую
                SingleSelectionModel<Tab> selectionModel = contentPane.getSelectionModel();
                selectionModel.select(newArchCreation);
            } else {
                JOptionPane.showMessageDialog(null, "Создайте или загрузите проект!");
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Функция, отвечающая за возможность выйти из приложения
     * @param e событие необходимое по контракту обработчика
     */
    public void exitApplication(ActionEvent e) {
        System.exit(0);
    }
}
