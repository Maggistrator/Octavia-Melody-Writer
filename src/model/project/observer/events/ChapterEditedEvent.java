package model.project.observer.events;

import model.project.Chapter;

/**
 *
 * @author Сова
 */
public class ChapterEditedEvent implements ProjectEvent{
    public Chapter chapter;

    public ChapterEditedEvent(Chapter chapter) {
        this.chapter = chapter;
    }
    
}
