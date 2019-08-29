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
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

/**
 * Панель навигации 
 */
public class SimpleNavigationController implements ProjectListener {

    @FXML
    //Дерево навигации - основной компонент панели
    private TreeView<ProjectNode> navigator;

    @FXML
    //Заголовок панели, отображает имя текущего проекта
    private Label header;

    //Менеджер проекта
    ProjectManager manager = ProjectManager.getInstance();
    //переданная через специальный метод панель контента для управления вкладками
    TabPane contentPane;
    //Контекстное меню, с динамическим содержимым
    ContextMenu navigatorMenu = new ContextMenu();
    
    @FXML
    void initialize() {
        //подписываемся на события модели
        manager.subscribe(this);
        
        //устанавливаем обработчик на события узла дерева
        navigator.setOnMouseClicked((MouseEvent mouseEvent) -> {
            //реагирует исключительно на двойные клики
            if (mouseEvent.getClickCount() == 2) {
                TreeItem<ProjectNode> item = navigator.getSelectionModel().getSelectedItem();
                if (item != null) {
                    //открытие происходит только для объектов типа Глава (проверка рантайм-типа)
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
            //обработка события загрузки проекта
            if (e instanceof ProjectLoadedEvent) {
                ProjectLoadedEvent event = (ProjectLoadedEvent) e;
                dispatchProjectLoadEvent(event);
            }
            
            //обработка события создания узла проекта
            if (e instanceof ProjectNodeCreatedEvent) {
                ProjectNodeCreatedEvent event = (ProjectNodeCreatedEvent) e;
                //проверяем, является ли узел самим проектом
                if(event.newNode instanceof Project){
                    //если да - обрабатываем как проект
                    dispatchProjectCreationEvent(event);
                    //если нет - обрабатываем как узел проекта
                } else dispatchNodeCreation(event.parent, event.newNode);
            } 
            
            //обработка события редактирования узла дерева
            if(e instanceof ProjectNodeEditedEvent){
                ProjectNodeEditedEvent event = (ProjectNodeEditedEvent) e;
                dispatchNodeDeletion(event.node);
                dispatchNodeCreation(event.node.getParent(), event.node);
            }
            
            //обработка события удаления узла дерева
            if(e instanceof ProjectNodeDeletedEvent){
                dispatchNodeDeletion(((ProjectNodeDeletedEvent) e).node);
            }
            
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(null, "Ресурсы программы повреждены, попробуйте переустановить её!");
        } catch (IOException ex) {
            Logger.getLogger(SimpleNavigationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Функция обработки события загрузки проекта
     */
    private void dispatchProjectLoadEvent(ProjectLoadedEvent event) throws MalformedURLException {
        //установка нового имени в заголовок панели
        header.setText(event.project.name);

        //обновляем корень дерева, удаляя тем самым все старые элементы
        TreeItem<ProjectNode> root = new TreeItem(manager.getProject());
        navigator.setRoot(root);

        //загружаем пиктограмму корня проекта (книжная полка)
        File rootIconFile = new File("res/project icon.png");
        ImageView rootIcon = new ImageView(new Image(rootIconFile.toURI().toURL().toString()));
        //размеры пиктограммы корня самые большие - 32х32 пикселя
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
            navigator.getRoot().getChildren().add(archNode);
        }
    }

    /**
     * Функция обработки события создания проекта
     */
    private void dispatchProjectCreationEvent(ProjectNodeCreatedEvent event) throws MalformedURLException {
        //обновляем корень и название в верхней части панели
        TreeItem<ProjectNode> root = new TreeItem(manager.getProject());
        navigator.setRoot(root);
        header.setText(((Project)event.newNode).name);

        //добавляем пиктограмму корня проекта
        File rootIconFile = new File("res/project icon.png");
        ImageView rootIcon = new ImageView(new Image(rootIconFile.toURI().toURL().toString()));
        rootIcon.setFitHeight(32);
        rootIcon.setFitWidth(32);
        root.setGraphic(rootIcon);
    }

    //обработчик создания узла
    private void dispatchNodeCreation(ProjectNode parent, ProjectNode newNode) {
        //сначала, функция создания новых узлов применяется к корню..
        appendItemByParent(navigator.getRoot(), parent, newNode);
        //..затем к дочерним элементам
        navigator.getRoot().getChildren().forEach((item) -> {
            appendItemByParent(item, parent, newNode);
        });
    }

    /**
     * Функция-обработчик удаления узла
     * @param ProjectNode node - удаленный элемент
     */
    private void dispatchNodeDeletion(ProjectNode node) throws IOException {
            
        //если у нода есть родительский нод, значит это не проект, так как только у проекта getParent() == null
        if (node.getParent() != null) {
            //получаем файлы источников у искомого файла, его родительского узла, и корневого узла
            File neededPath = node.getSource();
            File parentPath = node.getParent().getSource();
            File rootPath = navigator.getRoot().getValue().getSource();
            
            //готовимся узнать родительский нод, и искомый
            TreeItem<ProjectNode> parentNode = null;
            TreeItem<ProjectNode> nodeToDelete = null;
            
            //если родительский нод является корнем..
            if (rootPath.getAbsolutePath().equals(parentPath.getAbsolutePath())) {
                //..проходимся по корню и ищем там
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
                        for (TreeItem<ProjectNode> leaf : parentNode.getChildren()) {
                            File leafPath = leaf.getValue().getSource();
                            if (leafPath.getAbsolutePath().equals(neededPath.getAbsolutePath())) {
                                nodeToDelete = leaf;
                            }
                        }
                    }
                }
            }
            //затем удаляем найденный нод
            //так как наличие искомого нода гарантируется, никакие проверки не требуются
            parentNode.getChildren().remove(nodeToDelete);
        } else {
            //если же удаленным узлом оказался сам проект, достаточно удалить корневой нод..
            navigator.setRoot(null);
            //сбросить имя..
            header.setText("Name");
            //и очистить панель контента
            contentPane.getTabs().clear();
        }
    }

    /**
     * Добавление нового узла, зная его родительский элемент в дереве
     * @param TreeItem<ProjectNode> item - текущий узел дерева, в котором производится поиск
     * @param ProjectNode parent - родительский элемент
     * @param ProjectNode toInsert - узел, для которого требуется создать новый нод в дереве
     */
    private void appendItemByParent(TreeItem<ProjectNode> item, ProjectNode parent, ProjectNode toInsert) {
        try {
            //файлы, по которым происходит сравнение нодов между собой
            File neededPath = parent.getSource();
            File currentPath = item.getValue().getSource();

            //сравнение нодов по содержимому
            if (neededPath.getAbsolutePath().equals(currentPath.getAbsolutePath())) {
                //главы вызывают одну функцию создания..
                if (toInsert instanceof Chapter) {
                    TreeItem chapterNode = wrapChapterNode((Chapter) toInsert);
                    item.getChildren().add(chapterNode);
                    expandTreeView(chapterNode);
                } else {
                    //..арки - другую
                    TreeItem<ProjectNode> archNode = wrapArchNode((Arch) toInsert);
                    navigator.getRoot().getChildren().add(archNode);
                    //в обоих случаях, дерево раскрывает все родительские узлы, вплоть до текущего
                    expandTreeView(archNode);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SimpleNavigationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //процедура, отвечающая за рекурсивное раскрытие дерева до выбранного элемента
    private void expandTreeView(TreeItem<ProjectNode> selectedItem) {
        if (selectedItem != null) {
            expandTreeView(selectedItem.getParent());

            if (!selectedItem.isLeaf()) {
                selectedItem.setExpanded(true);
            }
        }
    }

    //Функция создания нового узла дерева представляющего переданную в аргументах главу
    private TreeItem wrapChapterNode(Chapter chapter) throws MalformedURLException {
        TreeItem<ProjectNode> chapterNode = new TreeItem<>(chapter);
        //главе так же устанавливается пиктограмма - страница книги
        File chapterIconFile = new File("res/chapter icon3.png");
        ImageView chapterIcon = new ImageView(new Image(chapterIconFile.toURI().toURL().toString()));
        //размеры пиктограммы главы самые маленькие, 
        //чтобы отобразить её самый низкий уровень в иерархии нодов проекта
        chapterIcon.setFitHeight(16);
        chapterIcon.setFitWidth(16);
        chapterNode.setGraphic(chapterIcon);
        //функция возвращает сгененированный узел дерева
        return chapterNode;
    }

    //Функция "оборачивания"(англ. bind) арки в объект узла дерева, а так же обработка её содержимого
    private TreeItem wrapArchNodeAndContents(Arch arch) throws MalformedURLException {
        //Эта функция расширяет функциональность функции wrapArchNode(Arch), добавляя обработку содержимого
        TreeItem<ProjectNode> archNode = wrapArchNode(arch);
        //проходимся по всем дочерним главам конкретной арки, и вызываем для них функцию обработки глав
        arch.getChaptersList().forEach((Chapter chapter) -> {
            try {
                TreeItem chapterNode = wrapChapterNode(chapter);
                archNode.getChildren().add(chapterNode);
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(null, "Ресурсы программы повреждены, попробуйте переустановить её!");
            }
        });
        //возвращает сгенерированный узел-арку
        return archNode;
    }

    //Функция обработки арки, генерирующая соответствующий ему узел дерева
    private TreeItem wrapArchNode(Arch arch) throws MalformedURLException {
        TreeItem<ProjectNode> archNode = new TreeItem<>(arch);
        //этот обработчик событий использует т.н. FX-Properties - поля, позволяющие 
        //подписываться на изменение своего содержимого. В данном случае, отслеживается изменение состояния "свёрнут/развернут"
        archNode.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                try {
                    //В момент смены состояния, арка меняет пиктограмму с закрытой книги на открытую или обратно, в противовес текущему
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

        //устанавливаем арке пиктограмму по-умолчанию - закрытая книга
        File archIconFile = new File("res/arch icon.png");
        ImageView archIcon = new ImageView(new Image(archIconFile.toURI().toURL().toString()));
        //размер иконки арки - 24х24 пикселя, чтобы отобразить её средний уровень в иерархии узлов проекта
        archIcon.setFitHeight(24);
        archIcon.setFitWidth(24);
        archNode.setGraphic(archIcon);
        //возвращает сгененированный узел
        return archNode;
    }

    //поля элементов контекстного меню
    private MenuItem createArch, createChapter, delete, rename;
    
    //Функция компоновки контекстного меню, отрабатывающая по-разному, в зависимости от выбранного элемента дерева
    private void buildContextMenu(TreeItem<ProjectNode> target, ContextMenu menu) {
        //каждый раз контекстное меню очищается, чтобы был возможен вариант отсутствия пунктов меню
        menu.getItems().clear();
        
        //создаются объекты пунктов меню
        createChapter = new MenuItem("Создать главу");
        createArch = new MenuItem("Создать арку");
        rename = new MenuItem("Переименовать");
        delete = new MenuItem("Удалить");
        
        //использование ссылки на метод вызова формы создания арки, для создания обработчика
        createArch.setOnAction(this::createArch);
        //обработчик вызова формы создания проекта
        createChapter.setOnAction((e)->{
            //определение арки по-умолчанию, зависящей от выбранного элемента
            Arch initialArch = null;
            //если выбран проект - аркой по-умолчанию становится корневая арка проекта
            if(target.getValue() instanceof Project) initialArch = ((Project)target.getValue()).root;
            //если выбрана конкретная арка, в функцию создания главы будет передана именно она
            if(target.getValue() instanceof Arch) initialArch = (Arch)target.getValue();
            //вызов функции создания главы с выбранной выше аркой по-умолчанию
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
        
        //обработчик удаления узла проекта
        delete.setOnAction((e)-> {
            try {
                //эта операция сразу делегируется менеджеру проекта
                manager.deleteNode(target.getValue());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Удаление не удалось..");
            }
        });
        
        //в зависимости от выбранного компонента, формируется список пунктов меню:
        if(target != null){
            //корневой элемент, отображающий проект, позволяет создавать арки, создавать главы, и удалить проект
            if(target.equals(navigator.getRoot())) 
                menu.getItems().addAll(createArch, createChapter, delete);
            //Выбор арки позволяет создать главу и удалить арку, вместе со всем содержимым
            if(target.getValue() instanceof Arch)
                menu.getItems().addAll(createChapter, delete);
            //выбор главы позволяет удалить её
            if(target.getValue() instanceof Chapter)
                menu.getItems().addAll(delete);
        }
    }

    //Функция загрузки формы создания арки, идентичка функции в главном окне
    private void createArch(Event e) {
        try {
            if (manager.getProject() != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../support/modal/fxml/create_arch.fxml"));
                CreateArchController controller = new CreateArchController();
                controller.setParent(contentPane);
                loader.setController(controller);
                Parent root = loader.load();
                Tab newArchCreation = new Tab("Создать арку", root);
                contentPane.getTabs().add(newArchCreation);
                SingleSelectionModel<Tab> selectionModel = contentPane.getSelectionModel();
                selectionModel.select(newArchCreation);
            } else {
                JOptionPane.showMessageDialog(null, "Создайте или загрузите проект!");
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //функция загрузки формы создания новой главы, идентична такой же в главном окне
    private void createChapter(Arch initialArch){
        try {
            if(manager.getProject() != null){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../support/modal/fxml/create_chapter.fxml"));
                CreateChapterDialogController controller = new CreateChapterDialogController();
                controller.setParent(contentPane);
                if(initialArch != null) controller.setInitialArch(initialArch);
                loader.setController(controller);
                Parent root = loader.load();
                Tab newChapterCreation = new Tab("Создать главу", root);
                contentPane.getTabs().add(newChapterCreation);
                SingleSelectionModel<Tab> selectionModel = contentPane.getSelectionModel();
                selectionModel.select(newChapterCreation); 
            } else {
                JOptionPane.showMessageDialog(null, "Создайте или загрузите проект!");
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //функция открытия главы на редактирование в текстовой области
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

            //выбираем вкладку как текущую
            SingleSelectionModel<Tab> selectionModel = contentPane.getSelectionModel();
            selectionModel.select(tab);

            //добавляем обработчик закрытия вкладки
            tab.setOnClosed((Event event) -> {
                try {
                    //этот обработчик требует подтверждения закрытия редактора
                    int answer = JOptionPane.showConfirmDialog(null, "Сохранить изменения?");
                    if (answer == JOptionPane.OK_OPTION) manager.canselEditingChapter(chapter, true);
                    else manager.canselEditingChapter(chapter, false);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка записи!");
                }
            });
            //загруженный редактор добавляется на панель контента
            this.contentPane.getTabs().add(tab);
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //служебный метод отвечающий за передачу в этот контроллер панели контента 
    public void setContentPane(TabPane contentPane){
        this.contentPane = contentPane;
    }
}
