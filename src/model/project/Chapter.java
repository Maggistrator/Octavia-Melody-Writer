package model.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс, отвечающий за хранение параметров конкретной главы.
 * 
 * @author Сова
 */
public class Chapter implements ProjectNode{
    
    public File source;
    public boolean edited = false;
    private String contents;

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
    
    /**
     * Сохраняет главу, или создает её, если она ещё не существует
     * @throws java.io.IOException ошибка записи
     */
    @Override
    public void save() throws IOException {
        if(source.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(source, false))) {
                writer.write(contents);
                writer.close();
            }
        }
        else source.createNewFile();
    }

    public String load() throws IOException {
        Path preferredPath = Paths.get(source.toURI());
        
        StringBuilder sb = new StringBuilder();
        Files.lines(preferredPath, Charset.forName("utf-8")).forEach((line)->{
            sb.append(line);
            sb.append("<br>");
        });
        setText(sb.toString());
        return contents;
    }
    
    public void setText(String toString){
        contents = toString;
    }
    
    public String getText(){
        return contents;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
