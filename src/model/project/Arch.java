package model.project;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *  Арка - крупное подразделение произведения.
 *  <br>Можно использовать для логического деления глав, или как тома.
 * 
 * @author Сова
 */
public class Arch {

    public Arch(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public Arch(Chapter[] chapters) {
        this.chapters.addAll(Arrays.asList(chapters));
    }
    
    ArrayList<Chapter> chapters = new ArrayList<>();
    
}
