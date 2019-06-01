package model.project.observer.events;

import model.project.Project;

/**
 * Событие, обозначающее загрузку проекта
 * @author Сова
 */
public class ProjectLoadedEvent implements ProjectEvent{
    public Project project;

    public ProjectLoadedEvent(Project project) {
        this.project = project;
    }
}
