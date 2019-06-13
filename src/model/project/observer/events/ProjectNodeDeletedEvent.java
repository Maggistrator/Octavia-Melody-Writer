package model.project.observer.events;

import model.project.ProjectNode;

/**
 *
 * @author Сова
 */
public class ProjectNodeDeletedEvent implements ProjectEvent{
    public ProjectNode node;

    public ProjectNodeDeletedEvent(ProjectNode node) {
        this.node = node;
    }
}
