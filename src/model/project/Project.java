package model.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Класс произведения.
 * <br>Представляет собой обобщённую структуру - главы, файловая и службная 
 * информация, относящаяся к проекту
 * 
 * @author Сова
 */
public class Project {

    public enum ProjectType {
        Autorship, Translation;
    }

    /**Cписок арок, состоящих из глав. Можно использовать как тома.*/
    public ArrayList<Arch> content = new ArrayList<>();
    
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
     */
    public static Project load(String path){
        return null;
    }
    
    /**
     * Удаляет текущий проект из файловой системы
     */
    public void delete(){
        
    }
}
