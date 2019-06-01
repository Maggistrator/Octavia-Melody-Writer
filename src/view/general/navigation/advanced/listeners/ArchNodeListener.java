package view.general.navigation.advanced.listeners;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Сова
 */
class ArchNodeListener implements EventHandler<MouseEvent> {

    @Override
    public void handle(MouseEvent event) {
        if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
            Node node = (Node) event.getSource();
            node.getStyleClass().add("activeLabel");
        } 
        if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
            Node node = (Node) event.getSource();
            node.getStyleClass().removeAll("activeLabel");
        }
    }
}
