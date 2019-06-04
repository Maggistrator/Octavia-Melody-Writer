package model.project.observer;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.transform.TransformerException;
import model.project.Chapter;
import model.project.Project;
import model.project.exceptions.ProjectLoadException;
import model.project.observer.events.ChapterEditedEvent;
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
    private final ArrayList<ProjectListener> bufferedListenersToAdd = new ArrayList<>();
    private final ArrayList<ProjectListener> bufferedListenersToRemove = new ArrayList<>();
    
    private Project project;
    
    //Коллекция редактируемых глав. Пока глава не сохранена, она не может покинуть этот список
    private ArrayList<Chapter> editedChapters = new ArrayList<>();
    
    //объект, требуемый паттерном Singleton
    private static ProjectManager instance = new ProjectManager();
    
    private boolean isStateClean = true;
    

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
        if (isStateClean) {
            listeners.add(listener);            
        } else {
            bufferedListenersToAdd.add(listener);  
        }
    }

    @Override
    public void unsubscribe(ProjectListener listener) {
        if (isStateClean) {
            listeners.remove(listener);
        } else {
            bufferedListenersToRemove.add(listener);
        }
    }

    @Override
    public void notify(ProjectEvent e) {
        //уведомляем слушателей
        listeners.forEach((listener) -> {
            isStateClean = false;
            listener.dispatch(e);
        });
        
        //перемещаем объекты из буфера в реестр слушателей
        listeners.addAll(bufferedListenersToAdd);
        listeners.removeAll(bufferedListenersToRemove);
        
        //очистка буфера
        bufferedListenersToAdd.clear();
        bufferedListenersToRemove.clear();
    }
    
    public void setProject(Project project){
        this.project = project; 
    }

    public Project getProject(){
        return project;
    }

    
    public void addChapterToEditedList(Chapter chapter){
        chapter.edited = true;
        editedChapters.add(chapter);
        notify(new ChapterEditedEvent(chapter));
    }
    
    public void removeChapterFromEditedList(Chapter chapter){
        chapter.edited = true;
        editedChapters.add(chapter);
    }

    public ArrayList<Chapter> getEditedChapters() {
        return editedChapters;
    }
    
}
