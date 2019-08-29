package view.functions.autorship;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import model.project.Chapter;
import model.project.observer.ProjectManager;
import org.controlsfx.control.PopOver;

public class AutorshipModeController {

    @FXML
    private Pane marker;

    @FXML
    private HTMLEditor textEditor;

    ProjectManager manager = ProjectManager.getInstance();
    Chapter chapter;
    
    @FXML
    void initialize() {
        //пользовательская кнопка HTMLEditor'a - сохранение
        Node node = textEditor.lookup(".top-toolbar");
        if (node instanceof ToolBar) {
            try {
                ToolBar bar = (ToolBar) node;
                File buttonIconFile = new File("res/save icon.png");
                ImageView graphic = new ImageView(new Image(buttonIconFile.toURI().toURL().toString(), 16, 16, true, true));
                Button saveButton = new Button("", graphic);
                bar.getItems().add(saveButton);
                saveButton.setOnAction((event) -> {
                    try {
                        chapter.setText(textEditor.getHtmlText());
                        chapter.save();

                        PopOver popOver = new PopOver();
                        Label label = new Label(" Сохранение успешно! ");

                        popOver.setContentNode(label);
                        popOver.setAnimated(true);
                        popOver.setCloseButtonEnabled(false);
                        popOver.setAutoHide(true);
                        popOver.setFadeOutDuration(Duration.millis(400));
                        popOver.show(saveButton);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Ошибка сохранения!");
                    }
                });
            } catch (MalformedURLException ex) {
                        JOptionPane.showMessageDialog(null, "Ошибка загрузки ресурсов!");
            }
        }
    }

    public void initWithChapter(Chapter chapter) {
        try {
            this.chapter = chapter;
            String contents = chapter.load();
            if (contents != null && !contents.isEmpty()) {
                textEditor.setHtmlText(contents);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Не удалось загрузить главу!");
        }
    }
}
