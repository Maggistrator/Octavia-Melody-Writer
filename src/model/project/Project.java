package model.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

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
        File projectHome = new File(source.getAbsolutePath() + "/" + name);
        if (projectHome.mkdir()) createMetadataAndDirectories();
        else{
            //что-то сохраняем..
        }
    }
    
    private void createMetadataAndDirectories(){
            File projectHome = new File(source.getAbsolutePath()+"/"+name);
        File projectProperties = new File(source.getAbsolutePath()+"/"+name+"/"+name+".xml");
        if (projectHome.mkdir()) {
            try {
                projectProperties.createNewFile();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Невозможно создать файл проекта! \nПричина:"+ex);
            }
        }
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
