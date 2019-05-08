package view.general;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Этот контроллер управляет основным окном, в которое встраиваются остальные.
 *
 * @author Сова
 */
public class MainScreenController {

    @FXML
    MenuItem createProjectMenuItem;

    @FXML
    MenuItem saveProjectMenuItem;

    @FXML
    MenuItem loadProjectMenuItem;

    @FXML
    MenuItem exitMenuItem;

    @FXML
    MenuItem chapterSettingsMenuItem;

    @FXML
    MenuItem createChapterMenuItem;

    @FXML
    MenuItem autorModeMenuItem;

    @FXML
    MenuItem translatorModeMenuItem;

    @FXML
    VBox toolbar;

    @FXML
    ScrollPane contentPane;

    public void Kek() {
        System.exit(0);
    }
}
