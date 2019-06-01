package view.general.navigation.advanced.listeners;

import java.util.Stack;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import model.project.ProjectLevel;

/**
 *
 * @author Сова
 */
public class BackOptionNodeListener implements EventHandler<MouseEvent> {

    Stack<ProjectLevel> navigationStack;

    public BackOptionNodeListener(Stack<ProjectLevel> navigationStack) {
        this.navigationStack = navigationStack;
    }

    @Override
    public void handle(MouseEvent event) {
        if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
            Node node = (Node) event.getSource();
            node.getStyleClass().add("backLabel");
        } 
        if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
            Node node = (Node) event.getSource();
            node.getStyleClass().removeAll("backLabel");
        }
    }

}
