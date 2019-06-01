package model.project.observer;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.transform.TransformerException;
import model.project.Project;
import model.project.exceptions.ProjectLoadException;
import model.project.observer.events.ProjectCreatedEvent;
import model.project.observer.events.ProjectEvent;
import model.project.observer.events.ProjectLoadedEvent;

/**
 * ProjectManager следит за событиями проекта, и уведомляет о его изменениях заинтересованных слушателей
 * <br>До некоторой степени управляет работой с проектом - позволяет загружать, сохранять, удалять и обновлять проект, 
 * хоть и не выполняет эти операции самостоятельно
 * 
 * @author Сова
 */
public class ProjectManager implements ObservableProject {

    private final ArrayList<ProjectListener> listeners = new ArrayList<>();
    private Project project;
    
    private static ProjectManager instance = new ProjectManager();

    private ProjectManager() {}
    
    public static ProjectManager getInstance(){
        return instance;
    }
    
    public void saveProject(){
        //если проект не null, то сохранить его, и уведомить слушателей 
    }
    
    public void loadProject(String path) throws ProjectLoadException{
        this.project = Project.load(path);
        notify(new ProjectLoadedEvent(project));
    }

    /**
     * Инкапсулирует в себе переданный проект, сохранет его и оповещает об этом слушателей
     * @param project новосозданный проект (должен быть создан извне)
     */
    public void createProject(Project project) throws IOException, TransformerException{
        this.project = project;
        project.save();
        notify(new ProjectCreatedEvent(project));
    }
    
    public void deleteProject(){
        //удалить проект, живущий в этом ProjectManager'e, и уведомить слушателей
    }
    
    @Override
    public void subscribe(ProjectListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(ProjectListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notify(ProjectEvent e) {
        listeners.forEach((listener) -> {
            listener.dispatch(e);
        });
    }
    
    public void setProject(Project project){
        this.project = project; 
    }

    public Project getProject(){
        return project;
    }

}
