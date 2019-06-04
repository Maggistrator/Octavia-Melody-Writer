package view.functions;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
import model.project.observer.events.ChapterEditedEvent;
import model.project.observer.events.ProjectEvent;

public class AutorshipModeController implements ProjectListener{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane marker;

    @FXML
    private HTMLEditor textEditor;

    ProjectManager manager = ProjectManager.getInstance();
    String initialText = "";

    public AutorshipModeController() {
    }
    
    public AutorshipModeController(String initialText) {
        this.initialText = initialText;
    }
    
    @FXML
    void initialize() {
        //подписываем себя на события проекта
        manager.subscribe(this);
        if(textEditor!=null)textEditor.setHtmlText(initialText);
    }

    @Override
    public void dispatch(ProjectEvent e) {
        if(e instanceof ChapterEditedEvent){
            try {
                textEditor.setHtmlText(((ChapterEditedEvent) e).chapter.load());
            } catch (IOException ex) {
                Logger.getLogger(AutorshipModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
