package model.project.observer;

import java.io.IOException;
import java.util.ArrayList;
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
 */
public class ProjectManager implements ObservableProject {

    private final ArrayList<ProjectListener> listeners = new ArrayList<>();
    private final ArrayList<ProjectListener> bufferedListenersToAdd = new ArrayList<>();
    private final ArrayList<ProjectListener> bufferedListenersToRemove = new ArrayList<>();
    
    private Project project;
    
    //Коллекция редактируемых глав. Пока глава не сохранена, она не может покинуть этот список
    private ArrayList<Chapter> editedChapters = new ArrayList<>();
    
    //флаг, не разрешающий изменение коллекции слушателей в момент оповещения о событии
    private boolean isStateClean = true;
    
    //объект данного класса создается в момент первого использования
    private static ProjectManager instance;

    //конструктор по-умолчанию становится приватным, 
    //чтобы исключить возможность случайного создания новых объектов
    private ProjectManager() {}
    
    //эта функция всегда возвращает один и тот же объект, как того требует паттерн Singleton
    public static ProjectManager getInstance(){
        if(instance == null) instance = new ProjectManager();
        return instance;
    }
    
    public void saveProject() throws IOException{
        //если проект не null, то сохранить его, и уведомить слушателей 
        project.save();
    }
    
    /**
     * Метод загрузки проекта
     * 
     * @param path путь, по которому следует искать файлы проекта
     * @throws model.project.exceptions.ProjectLoadException ошибка загрузки
     */
    public void loadProject(String path) throws ProjectLoadException{
        //реальная загрузка из файловой системы делегируется самому проекту
        this.project = (Project) Project.load(path);
        //задача данного метода - оповестить о событии загрузки представление
        notify(new ProjectLoadedEvent(project));
    }

    /**
     * Инкапсулирует в себе переданный проект, сохранет его и оповещает об этом слушателей
     * @param project новосозданный проект (должен быть создан извне)
     * @throws java.io.IOException ошибка файловых операций
     */
    public void createProject(Project project) throws IOException{
        this.project = project;
        //сохранение несозданного проекта приводит к его автоматическому созданию
        project.save();
        notify(new ProjectNodeCreatedEvent(project, null));
    }
    
    /**
     * Метод подписки на события проекта
     * @param listener объект наблюдателя
     */
    @Override
    public void subscribe(ProjectListener listener) {
        //если добавление слушателя проиходит в момент оповещения о событии
        //новый слушатель добавляется в буфер, и дожидается конца опроса
        if (isStateClean) {
            listeners.add(listener);            
        } else {
            bufferedListenersToAdd.add(listener);  
        }
    }

    /**
     * Отписка от событий проекта
     * @param listener
     */
    @Override
    public void unsubscribe(ProjectListener listener) {
        if (isStateClean) {
            listeners.remove(listener);
        } else {
            bufferedListenersToRemove.add(listener);
        }
    }

    /**
     * Оповещение всех слушателей о новом событии
     * @param e событие, которое следует распространить
     */
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
     * Этот метод создаёт новую главу, и оповещает всех заинтересованных
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
    
    /**
     * Создание новой арки, и оповещение слушателей
     * @param name имя арки    
     * @return объект созданной арки
     * @throws java.io.IOException ошибка файловой системы   
     */
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

    /**
     * Функция удаления узла проекта, и оповещения об этом слушателей
     * @param node узел, который следует удалить
     * @throws IOException ошибка удаления из файловой системы
     */
    public void deleteNode(ProjectNode node) throws IOException{
        node.delete();
        if(node instanceof Project) this.project = null;
        notify(new ProjectNodeDeletedEvent(node));
    }
    
    @Deprecated
    //Функция переименования узла, в разработке
    public void renameNode(ProjectNode node, String name) throws IOException{
        node.rename(name);
        notify(new ProjectNodeEditedEvent(node));
    }
    
    public ArrayList<Chapter> getEditedChapters() {
        return editedChapters;
    }
    
}