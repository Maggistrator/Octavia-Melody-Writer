package model.project.observer.events;

import model.project.ProjectNode;

/**
 *
 * @author Сова
 */
public class ProjectNodeCreatedEvent implements ProjectEvent{

    public ProjectNodeCreatedEvent(ProjectNode newNode, ProjectNode parent) {
        this.newNode = newNode;
        this.parent = parent;
    }
    
    public ProjectNode newNode;
    public ProjectNode parent;
}
