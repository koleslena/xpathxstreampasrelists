package ru.koleslena.xpathxstreampasrelists;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static String path = "table/record/inventory[@name='rrr']/book";
    public static List<PathNode> pathNodes = LoadUtils.parseStringToListNodes(path);

    public static void main(String[] args) {
        InputStream resource = Main.class.getResourceAsStream("my3.xml");

        String nodeName = pathNodes.get(0).getNodeName();

        XStream x = new XStream();

        x.ignoreUnknownElements();

        Mapper mapper = x.getMapper();
        ReflectionProvider reflectionProvider = x.getReflectionProvider();
        x.alias(nodeName, XmlRoot.class);
        Map<String,String> map = new HashMap<>();

        //for attributes converter, my3.xml
        x.registerConverter(new DictionaryConverter(Book.class, "name", "value"));
        x.registerConverter(new XPathConverter<Book>(mapper, reflectionProvider,
                XmlRoot.class, map, path, Book.class));

        XmlRoot o = (XmlRoot) x.fromXML(resource);

        List list = o.getList();
        o.toString();
    }
}
