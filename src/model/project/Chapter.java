package model.project;

import java.io.File;

/**
 * Класс, отвечающий за хранение параметров конкретной главы.
 * 
 * @author Сова
 */
public class Chapter implements ProjectLevel{
    
    File source;

    public Chapter(File source) {
        this.source = source;
    }    
    
    public String getName(){
        return source.getName();
    }
}
