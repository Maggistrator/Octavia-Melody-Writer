package model.project;

import java.io.File;
import java.util.ArrayList;

/**
 *  Арка - крупное подразделение произведения.
 *  <br>Можно использовать для логического деления глав, или как тома.
 * 
 *  @author Сова
 */
public class Arch  implements ProjectLevel{

    File source;
    private ArrayList<Chapter> chapters = new ArrayList<>();

    public Arch(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }
    
    public Arch(File source) {
        this.source = source;
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
}
