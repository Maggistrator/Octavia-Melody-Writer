package view.support;

import java.io.File;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Сова
 */
public class NewProjectDialogContoroller {

    @FXML
    private Button chooseProjectPathButton;

    @FXML
    private Button declineButton;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<?> projectTypeBox;

    @FXML
    private ComboBox<?> fandomBox;

    @FXML
    private TextField pathField;

    @FXML
    private TextArea summoryTextArea;

    @FXML
    private Button acceptButton;
        
    @FXML
    void accept(ActionEvent event) {}
}
