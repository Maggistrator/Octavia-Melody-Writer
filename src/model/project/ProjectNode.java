package model.project;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Сова
 */
public interface ProjectNode {
    
    void save() throws IOException;
    
    void delete() throws IOException;
    
    void rename(String newName) throws IOException;
    
    ProjectNode getParent();
    
    File getSource();
}
