package view.support.offers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javax.swing.JOptionPane;
import model.project.Arch;
import model.project.Chapter;
import model.project.ProjectNode;
import model.project.observer.ProjectManager;
import view.general.navigation.simple.SimpleNavigationController;
import view.support.modal.controllers.CreateChapterDialogController;

/**
 * FXML Controller class
 *
 * @author Сова
 */
public class OfferToCreateChapterController implements Initializable {


    @FXML
    private Button createChapterButton;

    @FXML
    private AnchorPane rootPane;
    
    @FXML
    private Label projectNameLabel;
    
    @FXML
    private ListView<Label> chaptersList;
    
    TabPane parent;
    Tab currentTab;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ProjectManager manager = ProjectManager.getInstance();
        projectNameLabel.setText(manager.getProject().name);

        //оборачиваем главы верхнего уровня в лейблы
        for (Chapter chapter : manager.getProject().root.getChaptersList()) {
            try {
                Label chapterLabel = wrapChapterInActiveLabel(chapter);
                chaptersList.getItems().add(chapterLabel);
            } catch (MalformedURLException ex) {
                Logger.getLogger(OfferToCreateChapterController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //оборачиваем арки в объекты узлов дерева
        for (Arch arch : manager.getProject().content) {
            //проходимся по всем дочерним главам конкретной арки
            arch.getChaptersList().forEach((Chapter chapter) -> {
                try {
                    Label chapterLabel = wrapChapterInActiveLabel(chapter);
                    chaptersList.getItems().add(chapterLabel);
                } catch (MalformedURLException ex) {
                    JOptionPane.showMessageDialog(null, "Ресурсы программы повреждены, попробуйте переустановить её!");
                }
            });
        }
    }

    @FXML
    private void createChapter(Event e) {
        try {
            //загружаем диалог создания главы            
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../modal/fxml/create_chapter.fxml"));  
                CreateChapterDialogController controller = new CreateChapterDialogController();
                controller.setParent(parent);
                loader.setController(controller);
                Parent root = loader.load();

            //убираем текущую вкладку
            parent.getTabs().removeIf((Tab t) -> {
                if (t.getContent().getId() == null)return false;
                else return t.getContent().getId().equals(rootPane.getId());
            });
            
            // создаём  новую вкладку и добавляем её на панель
            Tab newChapterCreation = new Tab("Создать главу", root);
            parent.getTabs().add(newChapterCreation);
            
            //выбираем новенькую вкладку как текущую
            SingleSelectionModel<Tab> selectionModel = parent.getSelectionModel();
            selectionModel.select(newChapterCreation); 
        } catch (IOException ex) {
            System.err.println("Сценарий служебной панели создания главы повреждён!");
            ex.printStackTrace();
        }
    }
    
    public void setParent(TabPane parent){
        this.parent = parent;
    }
    
    private Label wrapChapterInActiveLabel(Chapter chapter) throws MalformedURLException{
        //устанавливаем красивую иконку главе
        File chapterIconFile = new File("res/chapter icon3.png");
        ImageView chapterIcon = new ImageView(new Image(chapterIconFile.toURI().toURL().toString()));
        chapterIcon.setFitHeight(16);
        chapterIcon.setFitWidth(16);

        Label chapterLabel = new Label(chapter.getName(), chapterIcon);

        //эта колбаса смерти отвечает за клик
        chapterLabel.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                ProjectManager.getInstance().editChapter(chapter);
            }
        });
        //эта за наведение курсора
        chapterLabel.setOnMouseEntered((MouseEvent event) -> chapterLabel.setStyle("-fx-font-weight: bold;"));
        //это за увод за пределы компонента
        chapterLabel.setOnMouseExited((MouseEvent event) -> chapterLabel.setStyle("-fx-font-weight: normal;"));
        return chapterLabel;
    }
}
