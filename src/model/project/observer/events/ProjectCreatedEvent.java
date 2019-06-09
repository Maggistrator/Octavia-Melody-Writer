package model.project.observer.events;

import model.project.Project;

/**
 *
 * @author Сова
 */
public class ProjectCreatedEvent implements ProjectEvent{
    public Project project;

    public ProjectCreatedEvent(Project project) {
        this.project = project;
    }
}
