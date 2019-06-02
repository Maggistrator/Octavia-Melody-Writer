package model.project;

import java.io.File;

/**
 * Класс, отвечающий за хранение параметров конкретной главы.
 * 
 * @author Сова
 */
public class Chapter implements ProjectNode{
    
    File source;

    public Chapter(File source) {
        this.source = source;
    }    
    
    /**
     * Метод, возвращающий название главы, на основании имени файла
     * 
     * @return название данной главы
     */
    public String getName(){
        String filename = source.getName();
        int fileExtensionStartsAt = filename.lastIndexOf('.');
        String name = fileExtensionStartsAt > 0 ? filename.substring(0, fileExtensionStartsAt) : filename;
        return name;
    }

    @Override
    public File getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getName();
    }
}
