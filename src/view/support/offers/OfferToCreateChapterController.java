/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.support.offers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.project.observer.ProjectManager;

/**
 * FXML Controller class
 *
 * @author Сова
 */
public class OfferToCreateChapterController implements Initializable {


    @FXML
    private Button createChapterButton;

    @FXML
    private Label projectNameLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ProjectManager manager = ProjectManager.getInstance();
        projectNameLabel.setText(manager.getProject().name); 
    }    
    
    
    
}
