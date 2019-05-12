package model.project.observer;

import java.util.ArrayList;
import model.project.Project;
import model.project.observer.events.ProjectEvent;

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
    
    public void saveProject(){
        //если проект не null, то сохранить его, и уведомить слушателей 
    }
    
    public void loadProject(String path){
        //загрузить проект в этот ProjectManager, и уведомить слушателей 
    }

    public void createProject(String path){
        //создать проект в этом ProjectManager'e, и уведомить слушателей
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

}
