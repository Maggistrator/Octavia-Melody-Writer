package model.project.observer.events;

import model.project.ProjectNode;

/**
 *
 * @author Сова
 */
public class ProjectNodeEditedEvent implements ProjectEvent{
    public ProjectNode node;

    public ProjectNodeEditedEvent(ProjectNode node) {
        this.node = node;
    }
}
