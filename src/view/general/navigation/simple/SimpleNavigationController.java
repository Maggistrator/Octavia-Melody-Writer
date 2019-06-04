package view.general.navigation.simple;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import model.project.Arch;
import model.project.Chapter;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
import model.project.observer.events.ProjectCreatedEvent;
import model.project.observer.events.ProjectEvent;
import model.project.observer.events.ProjectLoadedEvent;
import model.project.ProjectNode;

public class SimpleNavigationController implements ProjectListener {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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

    @FXML
    void initialize() {
        manager.subscribe(this);
        navigator.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getClickCount() == 2) {
                TreeItem<ProjectNode> item = navigator.getSelectionModel().getSelectedItem();
                System.out.println("Selected file is : " + item.getValue());
                if(item.getValue() instanceof Chapter){
                    System.out.println("value:"+item.getValue());
                    if((Chapter) item.getValue() != null)
                    manager.addChapterToEditedList((Chapter) item.getValue());
                }
            }
        });
    }

    @Override
    public void dispatch(ProjectEvent e) {
        try {
            //если проект создан..
            if (e instanceof ProjectCreatedEvent) {
                ProjectCreatedEvent event = (ProjectCreatedEvent) e;
                
                //обновляем рут и шапку проекта
                TreeItem<ProjectNode> root = new TreeItem(manager.getProject());
                navigator.setRoot(root);
                header.setText(event.project.name);

                //украшаем рут красивой иконкой
                File rootIconFile = new File("res/project icon.png");
                ImageView rootIcon = new ImageView(new Image(rootIconFile.toURI().toURL().toString()));
                rootIcon.setFitHeight(32);
                rootIcon.setFitWidth(32);
                root.setGraphic(rootIcon);
            }

            //если проект загружен..
            if (e instanceof ProjectLoadedEvent) {
                ProjectLoadedEvent event = (ProjectLoadedEvent) e;
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
                    TreeItem<ProjectNode> chapterNode = new TreeItem<>(chapter);                            //устанавливаем красивую иконку главе
                    File chapterIconFile = new File("res/chapter icon3.png");
                    ImageView chapterIcon = new ImageView(new Image(chapterIconFile.toURI().toURL().toString()));
                    chapterIcon.setFitHeight(16);
                    chapterIcon.setFitWidth(16);
                    chapterNode.setGraphic(chapterIcon);
                    navigator.getRoot().getChildren().add(chapterNode);
                }

                //оборачиваем арки в объекты узлов дерева
                for (Arch arch : event.project.content) {
                    TreeItem<ProjectNode> archNode = new TreeItem<>(arch);

                    //проходимся по всем дочерним главам конкретной арки
                    arch.getChaptersList().forEach((Chapter chapter) -> {
                        try {
                            TreeItem<ProjectNode> chapterNode = new TreeItem<>(chapter);
                            //устанавливаем красивую иконку главе
                            File chapterIconFile = new File("res/chapter icon3.png");
                            ImageView chapterIcon = new ImageView(new Image(chapterIconFile.toURI().toURL().toString()));
                            chapterIcon.setFitHeight(16);
                            chapterIcon.setFitWidth(16);
                            chapterNode.setGraphic(chapterIcon);

                            archNode.getChildren().add(chapterNode);
                        } catch (MalformedURLException ex) {
                            JOptionPane.showMessageDialog(null, "Ресурсы программы повреждены, попробуйте переустановить её!");
                        }
                    });

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

                    //добавляем её в рут
                    navigator.getRoot().getChildren().add(archNode);
                }
            }
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(null, "Ресурсы программы повреждены, попробуйте переустановить её!");
        }
    }
}
