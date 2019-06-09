package model.project.observer;

import model.project.observer.events.ProjectEvent;

/**
 * Интерфейс Наблюдателя за проектом
 * 
 * @author Сова
 */
public interface ObservableProject {
    //типичнейшая реализация паттерна, в комментариях не нуждается
    
    void subscribe(ProjectListener listener);
    void unsubscribe(ProjectListener listener);
    void notify(ProjectEvent e);
}