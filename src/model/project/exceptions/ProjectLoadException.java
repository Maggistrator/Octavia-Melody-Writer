package model.project.exceptions;

/**
 * Исключение в ходе загрузки проекта
 * @author Сова
 */
public class ProjectLoadException extends Exception {
    public final Cause type;
    
    public enum Cause{
        LOCATING, PARSING 
    }

    public ProjectLoadException(Cause type) {
        this.type = type;
    }
    
    public ProjectLoadException(Cause type, String message) {
        super(message);
        this.type = type;
    }
}
