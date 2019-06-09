package model.project;

import java.io.File;

/**
 *
 * @author Сова
 */
public interface ProjectNode {
    
    void save() throws Exception;
    
    File getSource();
    
}
