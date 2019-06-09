package view.support.modal.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import model.project.Arch;
import model.project.observer.ProjectManager;
import org.controlsfx.control.PopOver;

/**
 * FXML CreateChapterDialog сontroller class
 *
 * @author Сова
 */
public class CreateChapterDialogController implements Initializable {

    @FXML
    private Button applyButton;

    @FXML
    private TextField nameField;

    @FXML
    private ChoiceBox<Arch> archSelector;
    
    @FXML
    private AnchorPane rootPanel;

    ProjectManager manager = ProjectManager.getInstance();
    private TabPane parent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (Arch arch : manager.getProject().content) {
            archSelector.getItems().add(arch);
        }
        archSelector.getItems().add(manager.getProject().root);
    }
    
    @FXML
    private void apply(Event e) {
        try {
            //создаём главу
            String name = nameField.getText();
            
            if (name.matches("[aA-zZ аА-яЯ 0-9 \\s]*$") && !name.isEmpty()) {
                Arch parentArch = archSelector.getValue() == null ? manager.getProject().root : archSelector.getValue();
                manager.createChapter(name, parentArch);
                
                //убираем текущую вкладку
                parent.getTabs().removeIf((Tab t) -> {
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
            JOptionPane.showMessageDialog(null, "В процессе создания главы произошла ошибка!\n Причина:"+ex.getMessage());
        }
    }
    
    public void setParent(TabPane parent){
        this.parent = parent;
    }
}
