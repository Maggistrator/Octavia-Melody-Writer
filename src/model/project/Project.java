package model.project;

import java.io.File;
import java.util.ArrayList;

/**
 * Класс произведения.
 * <br>Представляет собой обобщённую структуру - главы, файловая и службная 
 * информация, относящаяся к проекту
 * 
 * @author Сова
 */
public class Project {

    public enum ProjectType {
        Autorship, Translation;
    }

    /**Cписок арок, состоящих из глав. Можно использовать как тома.*/
    public ArrayList<Arch> content = new ArrayList<>();
    
    /**Название произведения*/
    public String name;
    /**Фандом, к которому относится произведение*/
    public String fandom;
    /**Тип проекта - авторство или перевод*/
    public ProjectType type;
    /**Краткое описание проекта*/
    public String summory;
    
    /**Путь к корневой папке проекта, с которой производится синхронизация*/
    public File source;

    /**
     *
     * @param name имя проекта, его директории и некоторых метаданных
     * @param path папка, в которой содержатся файлы проекта - своего рода workspace
     */
    public Project(String name, File path) {
        this.name = name; 
        this.source = path;
    }
    
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
     * Удаляет текущий проект из файловой системы
     */
    public void delete(){
        
    }
}
