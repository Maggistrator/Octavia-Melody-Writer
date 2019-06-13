package model.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Арка - крупное подразделение произведения.
 *  <br>Можно использовать для логического деления глав, или как тома.
 * 
 *  @author Сова
 */
public class Arch implements ProjectNode{

    File source;
    private ArrayList<Chapter> chapters = new ArrayList<>();
    private Project parent;
            
    public Arch(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }
    
    public Arch(File source, Project parent) {
        this.source = source;
        this.parent = parent;
    }

    public void addChapter(Chapter chapter){
        chapters.add(chapter);
    }
    
    public void removeChapter(Chapter chapter){
        chapters.remove(chapter);
    }
    
    public void removeChapter(int index){
        chapters.remove(index);
    }
    
    public Chapter getChapter(int index){
        return chapters.get(index);
    }
    
    public ArrayList<Chapter> getChaptersList(){
        return chapters;
    }
    
    /**
     * Возвращает главу по имени файла.
     * <br>- Если имя будет встречаться несколько раз, вернётся последнее вхождение!
     * <br>- Если имя не найдено, вернется <b>null</b>
     * @param name имя главы, по которому ведётся поиск
     * @return 
     */
    public Chapter getChapterByName(String name){
        Chapter chapter = null;
        for(Chapter ch: chapters){
            if(ch.getName().equals(name)) chapter = ch;
        }
        return chapter;
    }
    
    public String getName(){
        return source.getName();
    }

    @Override
    public File getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void save() throws IOException {
        if(source.exists()){
            chapters.forEach((Chapter chapter)-> {
                try {
                    save();
                } catch (IOException ex) {
                    Logger.getLogger(Arch.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        else source.mkdir();
    }

    /**
     * Удаляет арку из файловой системы
     * @throws java.io.IOException ошибка удаления
     */
    @Override
    public void delete() throws IOException{
        parent.content.remove(this);
        Files.walk(Paths.get(source.toURI()))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    @Override
    public void rename(String newName) throws IOException {
        File oldFile = source;
        File newFile = new File(source.getParent()+"/"+newName);
        
        if(!oldFile.renameTo(newFile)) throw new IOException("Rename failed!");
    }

    @Override
    public ProjectNode getParent() {
        return parent;
    }
    
}
