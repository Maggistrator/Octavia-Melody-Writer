package view.general.navigation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.project.Project;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
import model.project.observer.events.ProjectCreatedEvent;
import model.project.observer.events.ProjectEvent;

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

    ProjectManager manager;
    Project project;
    
    @FXML
    void initialize() {}

    @Override
    public void dispatch(ProjectEvent e) {
        if(e instanceof ProjectCreatedEvent){
            ProjectCreatedEvent event = (ProjectCreatedEvent)e;
            this.project = event.project;
            navigationNodesPane.getChildren().clear();
            header.setText(project.name);
        } 
            
    }
    
    public void setProjectManager(ProjectManager manager){
        this.manager = manager;
        System.out.println("view.general.navigation.NavigationController.setProjectManager()");
    }
    
}
