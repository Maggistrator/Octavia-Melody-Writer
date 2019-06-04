package model.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    
    File source;
    public boolean edited = false;

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
    
    public void save(){}

    public String load() throws IOException {
        Path preferredPath = Paths.get(source.toURI());
        
        StringBuilder sb = new StringBuilder();
        Files.lines(preferredPath, Charset.forName("windows-1251")).forEach((line)->{
            sb.append(line);
        });
//
//        try (BufferedReader br = Files.newBufferedReader(preferredPath)) {
//
//            // read line by line
//            String line;
//            while ((line = br.readLine()) != null) {
//                sb.append(line).append("\n");
//            }
//
//        } catch (IOException e) {
//            System.err.format("IOException: %s%n", e);
//        }

        System.out.println(sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        return getName();
    }
}
