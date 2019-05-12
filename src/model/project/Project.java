package model.project;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Класс произведения.
 * <br>Представляет собой обобщённую структуру - главы, файловая и службная 
 * информация, относящаяся к проекту
 * 
 * @author Сова
 */
public class Project {

    enum ProjectType {
        Autorship, Translation;
    }

    /**Cписок арок, состоящих из глав. Можно использовать как тома.*/
    public ArrayList<Arch> content = new ArrayList<>();
    
    /**Название произведения*/
    String name;
    /**Фандом, к которому относится произведение*/
    String fandom;
    /**Тип проекта - авторство или перевод*/
    ProjectType type;
    /**Краткое описание проекта*/
    String summory;
    
    /**Путь к корневой папке проекта, с которой производится синхронизация*/
    Path source;
    
    /**
     * Позволяет сохранить данный проект
     */
    public void save(){
        
    }
    
    /**
     * Позволяет загрузить проект по выбранному пути
     * @param path путь, который следует использовать
     * @return загруженный проект, или null, если произошла ошибка загрузки
     */
    public static Project load(String path){
        return null;
    }
    
    /**
     * Позволяет создать проект по выбранному пути
     * @param path путь, который следует использовать. 
     * Недостающие директории будут созданы автоматически
     */
    public static void create(String path){
        
    }
    
    /**
     * Удаляет текущий проект из файловой системы
     */
    public void delete(){
        
    }
}
