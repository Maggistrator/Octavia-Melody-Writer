package model.project.observer;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.transform.TransformerException;
import model.project.Arch;
import model.project.Chapter;
import model.project.Project;
import model.project.ProjectNode;
import model.project.exceptions.ProjectLoadException;
import model.project.observer.events.ProjectNodeEditedEvent;
import model.project.observer.events.ProjectEvent;
import model.project.observer.events.ProjectLoadedEvent;
import model.project.observer.events.ProjectNodeCreatedEvent;
import model.project.observer.events.ProjectNodeDeletedEvent;

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
    
    public void saveProject() throws IOException{
        //если проект не null, то сохранить его, и уведомить слушателей 
        project.save();
    }
    
    public void loadProject(String path) throws ProjectLoadException{
        this.project = (Project) Project.load(path);
        notify(new ProjectLoadedEvent(project));
    }

    /**
     * Инкапсулирует в себе переданный проект, сохранет его и оповещает об этом слушателей
     * @param project новосозданный проект (должен быть создан извне)
     */
    public void createProject(Project project) throws IOException, TransformerException{
        this.project = project;
        project.save();
        notify(new ProjectNodeCreatedEvent(project, null));
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
    
    /**
     * Этот метод создаёт новую главу
     * @param name имя главы
     * @param parent арка, в которой она находится
     * @return объект созданной главы
     * @throws java.io.IOException ошибка записи
     */
    public Chapter createChapter(String name, Arch parent) throws IOException{
        Chapter newChapter = project.createChapter(name, parent);
        notify(new ProjectNodeCreatedEvent(newChapter, parent));
        return newChapter;
    }
    
    public Arch createArch(String name) throws IOException {
        Arch newArch = project.createArch(name);
        notify(new ProjectNodeCreatedEvent(newArch, project));
        return newArch;
    }
    
    /**
     * Метод, снимающий с главы статус редактируемой, и уведомляющей об этом слушателей 
     * @param chapter глава, редактирование которой прекращено
     * @param needSave флаг, указывающий на то, нужно ли сохранять изменения
     * @throws java.io.IOException ошибка записи
     */
    public void canselEditingChapter(Chapter chapter, boolean needSave) throws IOException{
        if(needSave) chapter.save();
        chapter.edited = false;
        editedChapters.remove(chapter);
    }

    public void deleteNode(ProjectNode node) throws IOException{
        node.delete();
        if(node instanceof Project) this.project = null;
        notify(new ProjectNodeDeletedEvent(node));
    }
    
    public void renameNode(ProjectNode node, String name) throws IOException{
        node.rename(name);
        notify(new ProjectNodeEditedEvent(node));
    }
    
    public ArrayList<Chapter> getEditedChapters() {
        return editedChapters;
    }
    
}