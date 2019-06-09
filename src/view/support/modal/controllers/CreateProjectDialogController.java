package view.support.modal.controllers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import javax.xml.transform.TransformerException;
import model.project.Project;
import model.project.observer.ProjectManager;

/**
 *
 * @author Сова
 */
public class CreateProjectDialogController {

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
    
    private ProjectManager manager = ProjectManager.getInstance();
    
    @FXML
    private void accept(ActionEvent event) {
       //если все поля валидны..
       if(isValid()){
           //получаем пачку данных из них, и приводим их в порядок
           String name = nameField.getText();
           String path = pathField.getText();
           String fandom = "Ориджинал";
           fandom = fandomBox.getValue() == null || fandomBox.getValue().toString().isEmpty() ? "Ориджинал" : fandom;
           String summory = summoryTextArea.getText();
           
           Project.ProjectType type = Project.ProjectType.Autorship;
           switch (projectTypeBox.getValue()+"") {
               case "Autorship":
                   type = Project.ProjectType.Autorship;
                   break;

               case "Translation":
                   type = Project.ProjectType.Translation;
                   break;
           }
           
           //создаём объект Project
           Project project = new Project(name, new File(path+"/"+name));
           project.fandom = fandom;
           project.description = summory;
           project.type = type;
           
           try {
               //запихиваем в ProjectManager - об уведомлении заинтересованных лиц он сам позаботится
               manager.createProject(project);
               close();
           } catch (IOException ex) {
               Logger.getLogger(CreateProjectDialogController.class.getName()).log(Level.SEVERE, null, ex);
               //TODO: заменить на запись в служебный журнал ошибок в папке service
               JOptionPane.showMessageDialog(null, "Ошибка создания служебного файла! Сообщение: "+ex.getMessage());
           } catch (TransformerException ex) {
               Logger.getLogger(CreateProjectDialogController.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }
    
    
    boolean isValid(){
        boolean isValid = true;
        
        //ошибка валидации обрабатывается, и устанавливает в поле текст ошибки и соответствующий стиль
        try { validateName(); } catch (Exception ex) {
            isValid = false;
            nameField.setText(ex.getMessage());
            nameField.setStyle("-fx-text-inner-color: red;"); 
        }
        
        //ошибка валидации обрабатывается, и устанавливает в поле  текст ошибки и соответствующий стиль 
        try { validatePath(); } catch (Exception ex) {
            isValid = false;
            pathField.setText(ex.getMessage());
            pathField.setStyle("-fx-text-inner-color: red;"); 
        }
        return isValid;
    }
    
    @FXML
    private void selectPath(){
        //убеждаемся, что стиль окна дефолтный
        erroreousPathFocusGained(null); 
        //создаём модальное окно выбора директории
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выберите расположение проекта!");
        File path = chooser.showDialog((Stage) acceptButton.getScene().getWindow()); //получаем файл
        if(path != null) pathField.setText(path.getAbsolutePath()); // запихиваем его в поле 
    }
       
    /**валидация пути к проекту*/
    void validatePath() throws Exception{
        String path = pathField.getText();
        
        //поле пусто
        if(path.isEmpty() || path.equals("Путь не указан!")){
            throw new Exception("Путь не указан!");
        }
    }
       
    /**валидация имени проекта*/
    void validateName() throws Exception{
        String name = nameField.getText();
        
        //поле пусто
        if(name.isEmpty() || name.equals("Имя проекта не указано!")){
            throw new Exception("Имя проекта не указано!");
        }
    }
        
    @FXML
    private void erroreousPathFocusGained(Event event){
        //реакция на нажатие на поле - если в нём происходит ошибка, то невалидные данные стираются
        try { validatePath(); } catch (Exception ex) {
            pathField.clear();
            pathField.setStyle("-fx-text-inner-color: black;"); 
        }
    }
    
    @FXML
    private void erroreousNameFocusGained(Event event){
        //реакция на нажатие на поле - если в нём происходит ошибка, то невалидные данные стираются
        try { validateName(); } catch (Exception ex) {
            nameField.clear();
            nameField.setStyle("-fx-text-inner-color: black;"); 
        }
    }
    
    @FXML
    private void close() {
        Stage stage = (Stage) acceptButton.getScene().getWindow();
        stage.close();
    }

}
