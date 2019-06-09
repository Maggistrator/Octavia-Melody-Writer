package view.functions.autorship;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import model.project.Chapter;
import model.project.observer.ProjectListener;
import model.project.observer.ProjectManager;
import model.project.observer.events.ProjectEvent;
import model.project.observer.events.ProjectNodeEditedEvent;
import org.controlsfx.control.PopOver;

public class AutorshipModeController implements ProjectListener{

    @FXML
    private Pane marker;

    @FXML
    private HTMLEditor textEditor;

    ProjectManager manager = ProjectManager.getInstance();
    Chapter chapter;
    
    @FXML
    void initialize() {
        //подписываем себя на события проекта
        manager.subscribe(this);

        //паользовательская кнопка HTMLEditor'a - сохранение
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

    @Override
    public void dispatch(ProjectEvent e) {
        if (e instanceof ProjectNodeEditedEvent) {
            try {
                if (((ProjectNodeEditedEvent) e).node instanceof Chapter) {
                    this.chapter = (Chapter) ((ProjectNodeEditedEvent) e).node;
                }
                String contents = chapter.load();
                if (contents != null && !contents.isEmpty()) {
                    textEditor.setHtmlText(contents);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Не удалось загрузить главу!");
            }
        }
    }
}
