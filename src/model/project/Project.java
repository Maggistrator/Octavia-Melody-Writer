package model.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import model.project.exceptions.ProjectLoadException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Класс произведения.
 * <br>Представляет собой обобщённую структуру - главы, файловая и службная 
 * информация, относящаяся к проекту
 * 
 * @author Сова
 */
public class Project implements ProjectLevel{

    public enum ProjectType {
        Autorship, Translation;
    }

    /**Cписок арок, состоящих из глав. Можно использовать как тома.*/
    public ArrayList<Arch> content = new ArrayList<>();

    /**
     * Арка по-умолчанию.
     * <br>Файлы, лежащие в ней - de-facto лежат в корне проекта
     */
    public Arch root;
    
    /**Название произведения*/
    public String name;
    /**Фандом, к которому относится произведение*/
    public String fandom;
    /**Тип проекта - авторство или перевод*/
    public ProjectType type;
    /**Краткое описание проекта*/
    public String description;
    
    /**Путь к корневой папке проекта, с которой производится синхронизация*/
    public File source;

    /**
     *
     * @param name имя проекта, его директории и некоторых метаданных
     * @param path папка, в которой содержатся файлы проекта - своего рода workspace
     */
    public Project(String name, File path) {
        this.name = name; 
        this.source = path;
    }
    
    /**
     * Позволяет сохранить проект, если он существует, или создать, если нет
     * @throws java.io.IOException на случай файловых ошибок - например - недостатка прав на запись
     * @throws javax.xml.transform.TransformerException ошибка записи в xml
     */
    public void save() throws IOException, TransformerException {
        //-------------Создание проекта, если он еще не существует----------//
        File projectHome = new File(source.getAbsolutePath() + "/" + name);
        if (!projectHome.exists()) {
            try {
                //создаем новую папку для этого проекта
                projectHome.mkdir();
                
                //project.xml
                File projectProperties = new File(projectHome.getAbsolutePath() + "/project.xml");
                projectProperties.createNewFile();
                
                //Записываем в него начальные параметры
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = documentBuilder.newDocument();
                
                Element root = document.createElement("book");
                document.appendChild(root);
                
                Element nameTag = document.createElement("name");
                nameTag.appendChild(document.createTextNode(name));
                root.appendChild(nameTag);
                
                Element fandomTag = document.createElement("fandom");
                fandomTag.appendChild(document.createTextNode(fandom));
                root.appendChild(fandomTag);
                
                Element typeTag = document.createElement("type");
                typeTag.appendChild(document.createTextNode(type.toString()));
                root.appendChild(typeTag);
                
                Element descriptionTag = document.createElement("description");
                descriptionTag.appendChild(document.createTextNode(description));
                root.appendChild(descriptionTag);
                
                // записываем результаты
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(projectProperties);        
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                
                // If you use
                // StreamResult result = new StreamResult(System.out);
                // the output will be pushed to the standard output ...
                // You can use that for debugging

                transformer.transform(domSource, streamResult);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //------------Сохранение, если проект существует-----------------//
        else{
        }
    }
    
    /**
     * Позволяет загрузить проект по выбранному пути
     * @param path путь, который следует использовать
     * @return загруженный проект, или null, если произошла ошибка загрузки
     * @throws model.project.exceptions.ProjectLoadException исключение, сигнализирующее о том, что файл конфигурации в выбранной директории не найден
     */
    public static Project load(String path) throws ProjectLoadException {
        File projectConfig = new File(path + "/project.xml");
        if (projectConfig.exists()) {
            try {
                Document document = null;

                //подготовка к генерации объекта при помощи фабрики
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                //генерируем document из XML
                document = dBuilder.parse(projectConfig);


                //парсинг имени проекта
                String name = document.getElementsByTagName("name").item(0).getTextContent();
                //парсинг описания
                String description = document.getElementsByTagName("description").item(0).getTextContent();
                //парсинг фандома
                String fandom = document.getElementsByTagName("fandom").item(0).getTextContent();
                //парсинг типа проекта
                String type = document.getElementsByTagName("type").item(0).getTextContent();
                
                //вводим ключевые поля проекта
                Project project = new Project(name, projectConfig);
                project.description = description;
                project.fandom = fandom;
                project.type = type.equals("Autorship") ? ProjectType.Autorship : ProjectType.Translation;
                
                //обрабатываем рабочую директорию проекта
                Arch rootArch = new Arch(projectConfig.getParentFile());
                project.root = rootArch;//добавляем место для файлов без главы или тома
                Stack<Arch> arches = new Stack<>();//создаём стэк арок
                arches.push(rootArch);
                //проходимся по файловой системе проекта
                Files.walk(Paths.get(path)).forEach((arg)->{
                    walkThrowProjectFiles(arg, arches, rootArch);
                });
                arches.remove(rootArch);
                //перепиливаем стэк в список
                project.content.addAll(arches);
                return project;
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                throw new ProjectLoadException(ProjectLoadException.Cause.PARSING);
            }
        } else {
            throw new ProjectLoadException(ProjectLoadException.Cause.LOCATING);
        }
    }

    private static void walkThrowProjectFiles(Path path, Stack<Arch> arches, Arch rootArch) {
        //каст б-г мерзкого Path к православному File
        File file = path.toFile();
        
        //если натыкаемся на папку - делаем её Аркой(Томом)
        if (file.isDirectory()) {
            //избавляемся от рута 
            if(!arches.peek().equals(rootArch))
                arches.push(new Arch(file));
        }
        
        //натыкаемся на файл - проверяем, кто является его большим папочкой
        else {
            File parentDirectory = file.getParentFile();
            Arch lastArch = arches.pop();
            //если родительской папкой файла de-facto является папка последней арки, то добавляем его в неё
            if (parentDirectory.getAbsolutePath().equals(lastArch.source.getAbsolutePath())) {
                lastArch.addChapter(new Chapter(file));
                arches.push(lastArch);
            } else//в противном случае....
                //....рекурсивный вызов той же функции, но уже с предыдущим элементом стэка
                walkThrowProjectFiles(path, arches, rootArch);
                //ограничения на рекурсию не требуются, так как в выборку никогда не попадёт файл за пределами папки проекта
        }
    }

    /**
     * Удаляет текущий проект из файловой системы
     */
    public void delete(){
        
    }
}
