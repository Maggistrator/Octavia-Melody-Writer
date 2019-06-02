package view.general.navigation.advanced;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.project.Arch;
import model.project.Chapter;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
import model.project.observer.events.ProjectCreatedEvent;
import model.project.observer.events.ProjectEvent;
import model.project.observer.events.ProjectLoadedEvent;
import view.general.navigation.advanced.listeners.BackOptionNodeListener;
import view.general.navigation.advanced.listeners.ChapterNodeListener;
import model.project.ProjectNode;

/**
 * Контроллер панели навигации
 *
 * @author Сова
 */
public class NavigationController implements ProjectListener{

    @FXML
    private VBox navigationNodesPane;

    @FXML
    private Label header;

    @FXML
    private TextArea hintArea;

    @FXML
    private Separator separator;

    @FXML
    private Label hideButton;

    @FXML
    private AnchorPane rootNavigationPane;

    ProjectManager manager = ProjectManager.getInstance();
    
    Stack<ProjectNode> level = new Stack<>();
            
    
    @FXML
    void initialize() {
        manager.subscribe(this); 
    }

    @Override
    public void dispatch(ProjectEvent e) {
        //если проект создан..
        if(e instanceof ProjectCreatedEvent){
            //зачищаем панель, и добавляем в шапку название
            ProjectCreatedEvent event = (ProjectCreatedEvent)e;
            level.clear();
            navigationNodesPane.getChildren().clear();
            header.setText(event.project.name);
            level.push(event.project);
        } 
    
        //если проект загружен..
        if (e instanceof ProjectLoadedEvent) {
            ProjectLoadedEvent event = (ProjectLoadedEvent) e;
            header.setText(event.project.name);
            
            //зачищаем панель и стак вложенности
            navigationNodesPane.getChildren().clear();
            level.clear();
            
            //добавляем в стак новый проект
            level.push(event.project);
            //оборачиваем главы верхнего уровня в кликабельные лейблы
            ArrayList<Label> projectChaptersLabelList = new ArrayList<>();
            for (Chapter chapter : event.project.root.getChaptersList()) {
                Label label = new Label(chapter.getName());
                label.setFont(new Font(12));
                label.addEventHandler(MouseEvent.ANY, new ChapterNodeListener());
                projectChaptersLabelList.add(label);
            }
            navigationNodesPane.getChildren().addAll(projectChaptersLabelList);
            //оборачиваем арки в кликабельные лейблы
            ArrayList<Label> archesLabelList = new ArrayList<>();
            for (Arch arch : event.project.content) {
                Label label = new Label(arch.getName());
                label.setFont(new Font(12));
                archesLabelList.add(label);
            }
            navigationNodesPane.getChildren().addAll(archesLabelList);
            
            Label label = new Label("<- На уровень выше");
            label.setFont(new Font(12));
            label.addEventHandler(MouseEvent.ANY, new BackOptionNodeListener(level));
            navigationNodesPane.getChildren().add(label);
        }
    }
}
