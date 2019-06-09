package model.project.observer;

import model.project.observer.events.ProjectEvent;

/**
 *
 * @author Сова
 */
public interface ProjectListener {
    void dispatch(ProjectEvent e);
}
