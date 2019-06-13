package model.project.observer.events;

import model.project.ProjectNode;

/**
 *
 * @author Сова
 */
public class ProjectNodeCreatedEvent implements ProjectEvent{

    /**
     * Конструктор события
     * @param newNode новосозданный нод
     * @param parent родительский нод, хранящий данный. Может быть null, если newNode является новым проектом
     */
    public ProjectNodeCreatedEvent(ProjectNode newNode, ProjectNode parent) {
        this.newNode = newNode;
        this.parent = parent;
    }
    
    public ProjectNode newNode;
    public ProjectNode parent;
}
