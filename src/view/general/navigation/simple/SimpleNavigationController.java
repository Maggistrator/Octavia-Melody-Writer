package view.general.navigation.simple;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import model.project.Arch;
import model.project.Chapter;
import model.project.Project;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
import model.project.observer.events.ProjectEvent;
import model.project.observer.events.ProjectLoadedEvent;
import model.project.ProjectNode;
import model.project.observer.events.ProjectNodeCreatedEvent;
import model.project.observer.events.ProjectNodeDeletedEvent;
import model.project.observer.events.ProjectNodeEditedEvent;
import view.functions.autorship.AutorshipModeController;
import view.general.MainScreenController;
import view.support.modal.controllers.CreateArchController;
import view.support.modal.controllers.CreateChapterDialogController;

public class SimpleNavigationController implements ProjectListener {

    @FXML
    private TreeView<ProjectNode> navigator;

    @FXML
    private Label header;

    @FXML
    private TextArea hintArea;

    @FXML
    private Separator separator;

    @FXML
    private Label hideButton;

    ProjectManager manager = ProjectManager.getInstance();
    TabPane contentPane;
    ContextMenu navigatorMenu = new ContextMenu();
    
    @FXML
    void initialize() {
        //подписываемся на события модели
        manager.subscribe(this);
        
        //устанавливаем обработчик на события узла дерева
        navigator.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getClickCount() == 2) {
                TreeItem<ProjectNode> item = navigator.getSelectionModel().getSelectedItem();
                if (item != null) {
                    if (item.getValue() instanceof Chapter) {
                        if ((Chapter) item.getValue() != null) {
                            editChapter((Chapter) item.getValue());
                        }
                    }
                }
            }
        });
        
        //Устанавливаем слушатель контекстного меню, отвечающий за корректный выбор опций меню
        navigator.setOnContextMenuRequested((ContextMenuEvent event) -> {
            //передаём первый выбранный элемент
            TreeItem<ProjectNode> selected = navigator.getSelectionModel().getSelectedItems().get(0);
            buildContextMenu(selected, navigatorMenu);
        });
        
        //устанавливаем выпадающее меню на дерево
        navigator.setContextMenu(navigatorMenu);
    }

    @Override
    public void dispatch(ProjectEvent e) {
        try {
            //если проект загружен..
            if (e instanceof ProjectLoadedEvent) {
                ProjectLoadedEvent event = (ProjectLoadedEvent) e;
                dispatchProjectLoadEvent(event);
            }
            //если создан нод..
            if (e instanceof ProjectNodeCreatedEvent) {
                ProjectNodeCreatedEvent event = (ProjectNodeCreatedEvent) e;
                //проверяем, является ли он проектом
                if(event.newNode instanceof Project){
                    //если да - дипатчим
                    dispatchProjectCreationEvent(event);
                    //если нет - диспатчим(по-другому) TODO: смержить диспатчи!!!
                } else dispatchNodeCreation(event.parent, event.newNode);
            } 
            if(e instanceof ProjectNodeEditedEvent){
                ProjectNodeEditedEvent event = (ProjectNodeEditedEvent) e;
                dispatchNodeDeletion(event.node);
                dispatchNodeCreation(event.node.getParent(), event.node);
            }
            if(e instanceof ProjectNodeDeletedEvent){
                dispatchNodeDeletion(((ProjectNodeDeletedEvent) e).node);
            }
            
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(null, "Ресурсы программы повреждены, попробуйте переустановить её!");
        } catch (IOException ex) {
            Logger.getLogger(SimpleNavigationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void dispatchProjectLoadEvent(ProjectLoadedEvent event) throws MalformedURLException {
        header.setText(event.project.name);

        //обновляем рут
        TreeItem<ProjectNode> root = new TreeItem(manager.getProject());
        navigator.setRoot(root);

        //украшаем рут красивой иконкой
        File rootIconFile = new File("res/project icon.png");
        ImageView rootIcon = new ImageView(new Image(rootIconFile.toURI().toURL().toString()));
        rootIcon.setFitHeight(32);
        rootIcon.setFitWidth(32);
        root.setGraphic(rootIcon);

        //оборачиваем главы верхнего уровня в объекты узлов дерева
        for (Chapter chapter : event.project.root.getChaptersList()) {
            TreeItem<ProjectNode> chapterNode = wrapChapterNode(chapter);
            navigator.getRoot().getChildren().add(chapterNode);
        }

        //оборачиваем арки в объекты узлов дерева
        for (Arch arch : event.project.content) {
            TreeItem<ProjectNode> archNode = wrapArchNodeAndContents(arch);
            //добавляем её в рут
            navigator.getRoot().getChildren().add(archNode);
        }
    }

    private void dispatchProjectCreationEvent(ProjectNodeCreatedEvent event) throws MalformedURLException {
        //обновляем рут и шапку проекта
        TreeItem<ProjectNode> root = new TreeItem(manager.getProject());
        navigator.setRoot(root);
        header.setText(((Project)event.newNode).name);

        //украшаем рут красивой иконкой
        File rootIconFile = new File("res/project icon.png");
        ImageView rootIcon = new ImageView(new Image(rootIconFile.toURI().toURL().toString()));
        rootIcon.setFitHeight(32);
        rootIcon.setFitWidth(32);
        root.setGraphic(rootIcon);
    }

    private void dispatchNodeCreation(ProjectNode parent, ProjectNode newNode) {
        //пробуем прикрутить новый нод к руту
        appendItemByParent(navigator.getRoot(), parent, newNode);
        //прикручиваем ко всему остальному, на авось
        navigator.getRoot().getChildren().forEach((item) -> {
            appendItemByParent(item, parent, newNode);
        });
    }

    private void dispatchNodeDeletion(ProjectNode node) throws IOException {
            System.out.println("parent:"+node.getParent());
            
        //если у нода есть родительский нод, значит это не проект
        if (node.getParent() != null) {
            File neededPath = node.getSource();
            File parentPath = node.getParent().getSource();
            File rootPath = navigator.getRoot().getValue().getSource();
            
            //готовимся узнать родительский нод, и искомый
            TreeItem parentNode = null;
            TreeItem nodeToDelete = null;
            
            //если родительский нод является корнем..
            if (rootPath.getAbsolutePath().equals(parentPath.getAbsolutePath())) {
                //..проходимся по руту и ищем там
                for (TreeItem<ProjectNode> rootChild : navigator.getRoot().getChildren()) {
                    File currentPath = rootChild.getValue().getSource();
                    if (neededPath.getAbsolutePath().equals(currentPath.getAbsolutePath())) {
                        parentNode = navigator.getRoot();
                        nodeToDelete = rootChild;
                    }
                }
            //в противном случае...
            } else {
                //сначала ищем законного владельца..
                for (TreeItem<ProjectNode> rootChild : navigator.getRoot().getChildren()) {
                    File archPath = rootChild.getValue().getSource();
                    if (archPath.getAbsolutePath().equals(parentPath.getAbsolutePath())) {
                        parentNode = rootChild;
                        //..и только потом искомого
                        for (TreeItem leaf : rootChild.getChildren()) {
                            File leafPath = rootChild.getValue().getSource();
                            if (leafPath.getAbsolutePath().equals(neededPath.getAbsolutePath())) {
                                nodeToDelete = rootChild;
                            }
                        }
                    }
                }
            }
            
            parentNode.getChildren().remove(nodeToDelete);
        } else {
            navigator.setRoot(null);
        }
    }

    private void appendItemByParent(TreeItem<ProjectNode> item, ProjectNode parent, ProjectNode toInsert) {
        try {
            File neededPath = parent.getSource();
            File currentPath = item.getValue().getSource();

            if (neededPath.getAbsolutePath().equals(currentPath.getAbsolutePath())) {
                if (toInsert instanceof Chapter) {
                    TreeItem chapterNode = wrapChapterNode((Chapter) toInsert);
                    item.getChildren().add(chapterNode);
                    expandTreeView(chapterNode);
                } else {
                    TreeItem<ProjectNode> archNode = wrapArchNode((Arch) toInsert);
                    navigator.getRoot().getChildren().add(archNode);
                    expandTreeView(archNode);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SimpleNavigationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void expandTreeView(TreeItem<ProjectNode> selectedItem) {
        if (selectedItem != null) {
            expandTreeView(selectedItem.getParent());

            if (!selectedItem.isLeaf()) {
                selectedItem.setExpanded(true);
            }
        }
    }

    private TreeItem wrapChapterNode(Chapter chapter) throws MalformedURLException {
        TreeItem<ProjectNode> chapterNode = new TreeItem<>(chapter);
        //устанавливаем красивую иконку главе
        File chapterIconFile = new File("res/chapter icon3.png");
        ImageView chapterIcon = new ImageView(new Image(chapterIconFile.toURI().toURL().toString()));
        chapterIcon.setFitHeight(16);
        chapterIcon.setFitWidth(16);
        chapterNode.setGraphic(chapterIcon);
        return chapterNode;
    }

    private TreeItem wrapArchNodeAndContents(Arch arch) throws MalformedURLException {
        TreeItem<ProjectNode> archNode = wrapArchNode(arch);
        //проходимся по всем дочерним главам конкретной арки
        arch.getChaptersList().forEach((Chapter chapter) -> {
            try {
                TreeItem chapterNode = wrapChapterNode(chapter);
                archNode.getChildren().add(chapterNode);
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(null, "Ресурсы программы повреждены, попробуйте переустановить её!");
            }
        });
        return archNode;
    }

    private TreeItem wrapArchNode(Arch arch) throws MalformedURLException {
        TreeItem<ProjectNode> archNode = new TreeItem<>(arch);
        archNode.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                try {
                    File archIconFile = !archNode.isExpanded() ? new File("res/arch icon.png") : new File("res/expanded arch icon.png");
                    ImageView archIcon = new ImageView(new Image(archIconFile.toURI().toURL().toString()));
                    archIcon.setFitHeight(24);
                    archIcon.setFitWidth(24);
                    archNode.setGraphic(archIcon);
                    
                } catch (MalformedURLException ex) {
                    Logger.getLogger(SimpleNavigationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //устанавливаем арке красивую иконку
        File archIconFile = new File("res/arch icon.png");
        ImageView archIcon = new ImageView(new Image(archIconFile.toURI().toURL().toString()));
        archIcon.setFitHeight(24);
        archIcon.setFitWidth(24);
        archNode.setGraphic(archIcon);
        return archNode;
    }

    private MenuItem createArch, createChapter, delete, rename;
    
    private void buildContextMenu(TreeItem<ProjectNode> target, ContextMenu menu) {
        menu.getItems().clear();
        
        createChapter = new MenuItem("Создать главу");
        createArch = new MenuItem("Создать арку");
        rename = new MenuItem("Переименовать");
        delete = new MenuItem("Удалить");
        
        createArch.setOnAction(this::createArch);
        createChapter.setOnAction((e)->{
            System.out.println("target:"+target);
            System.out.println("target.getValue() instanceof Project:"+(target.getValue() instanceof Project));
            System.out.println("target.getValue() instanceof Arch:"+(target.getValue() instanceof Arch));
            Arch initialArch = null;
            if(target.getValue() instanceof Project) initialArch = ((Project)target.getValue()).root;
            if(target.getValue() instanceof Arch) initialArch = (Arch)target.getValue();
            createChapter(initialArch);
        });
        
        rename.setOnAction((e)->{
            try {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("Выберите новое имя:");
                Optional<String> showAndWait = dialog.showAndWait();
                if(showAndWait.isPresent()) manager.renameNode(target.getValue(), showAndWait.get());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Переименование не удалось..");
            }
        });
        
        delete.setOnAction((e)-> {
            try {
                manager.deleteNode(target.getValue());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Удаление не удалось..");
            }
        });
        
        if(target != null){
            if(target.equals(navigator.getRoot())) 
                menu.getItems().addAll(createArch, createChapter, delete);
            if(target.getValue() instanceof Arch)
                menu.getItems().addAll(createChapter, delete);
            if(target.getValue() instanceof Chapter)
                menu.getItems().addAll(delete);
        }
    }
    
        private void createArch(Event e) {
        try {
            if(manager.getProject() != null){
                //загружаем диалог создания главы
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../support/modal/fxml/create_arch.fxml"));
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
    private void createChapter(Arch initialArch){
        try {
            if(manager.getProject() != null){
                //загружаем диалог создания главы
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../support/modal/fxml/create_chapter.fxml"));
                CreateChapterDialogController controller = new CreateChapterDialogController();
                controller.setParent(contentPane);
                if(initialArch != null) controller.setInitialArch(initialArch);
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
    
        private void editChapter(Chapter chapter) {
        try {
            //загружаем главу в текстовую область
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../functions/autorship/autor_mode.fxml"));
            AutorshipModeController controller = new AutorshipModeController();
            loader.setController(controller);
            Parent autorshipPanel = loader.load();
            controller.initWithChapter(chapter);
            //создаём новую вкладку
            Tab tab = new Tab(chapter.getName(), autorshipPanel);

            //выбираем новенькую вкладку как текущую
            SingleSelectionModel<Tab> selectionModel = contentPane.getSelectionModel();
            selectionModel.select(tab);

            //добавляем слушатель закрытия вкладки
            tab.setOnClosed((Event event) -> {
                try {
                    int answer = JOptionPane.showConfirmDialog(null, "Сохранить изменения?");
                    if (answer == JOptionPane.OK_OPTION) manager.canselEditingChapter(chapter, true);
                    else manager.canselEditingChapter(chapter, false);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка записи!");
                }
            });
            this.contentPane.getTabs().add(tab);
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setContentPane(TabPane contentPane){
        this.contentPane = contentPane;
    }
}
