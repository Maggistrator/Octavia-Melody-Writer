package view.general.navigation;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
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
    
    @FXML
    void initialize() {}

    @Override
    public void dispatch(ProjectEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setProjectManager(ProjectManager manager){
        this.manager = manager;
        System.out.println("Hello from navigation:setProjectManager()");
    }
    
}
