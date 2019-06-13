package view.support.modal.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import model.project.observer.ProjectManager;
import org.controlsfx.control.PopOver;

/**
 * FXML CreateChapterDialog сontroller class
 *
 * @author Сова
 */

public class CreateArchController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private Label projectNameLabel;

    @FXML
    private Button acceptButton;
    
    @FXML
    private AnchorPane rootPanel;

    ProjectManager manager = ProjectManager.getInstance();
    private TabPane parent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectNameLabel.setText(manager.getProject().name);
    }
    
    @FXML
    private void apply(Event e) {
        try {
            String name = nameField.getText();
            if (name.matches("[aA-zZ аА-яЯ 0-9 \\s]*$") && !name.isEmpty()) {
                manager.createArch(name);
                
                //убираем текущую вкладку
                parent.getTabs().removeIf((Tab t) -> {
                    //этот хитрый алгоритм ищет fx-id текущего окна во всех открытых вкладках
                    if (t.getContent().getId() == null)return false;
                    else return t.getContent().getId().equals(rootPanel.getId());
                });
                
            } else {
                PopOver popOver = new PopOver();
                Label label = new Label(" Недопустимые символы! ");
                label.setStyle("-fx-text-inner-color: red;");

                popOver.setContentNode(label);
                popOver.setAnimated(true);
                popOver.setCloseButtonEnabled(false);
                popOver.setAutoHide(true);
                popOver.setFadeOutDuration(Duration.millis(400));
                popOver.show(nameField);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "В процессе создания арки произошла ошибка!\n Причина:"+ex.getMessage());
        } 
    }
    
    public void setParent(TabPane parent){
        this.parent = parent;
    }
}

