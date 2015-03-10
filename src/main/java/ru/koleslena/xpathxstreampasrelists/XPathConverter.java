package ru.koleslena.xpathxstreampasrelists;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.*;

/**
 * Created by elenko on 09.03.15.
 */
public class XPathConverter<D extends DictionaryObject> implements Converter {
    private final Map<String, String> attributes;

    private final Class<?> clazz;

    private final Mapper mapper;

    private final ReflectionProvider reflectionProvider;
    private final Class<D> dictionaryClass;
    private String initPath;
    private List<PathNode> pathNodes;

    public XPathConverter(Mapper mapper,
                       ReflectionProvider reflectionProvider, Class<?> clazz,
                       Map<String, String> attributes, String path, Class<D> dictionaryClass) {
        super();
        this.mapper = mapper;
        this.reflectionProvider = reflectionProvider;
        this.attributes = attributes;
        this.clazz = clazz;
        this.pathNodes = LoadUtils.parseStringToListNodes(path);
        this.dictionaryClass = dictionaryClass;
        this.initPath = pathNodes.get(0).getNodeName();
    }

    @Override
    public boolean canConvert(Class cls) {
        return cls == clazz;
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        for (String key : attributes.keySet()) {
            writer.addAttribute(key, attributes.get(key));
        }

        Converter converter = new ReflectionConverter(mapper,
                reflectionProvider);
        context.convertAnother(value, converter);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {
        XmlRoot root = new XmlRoot();
        List list = new ArrayList<>();
        root.setList(list);

        repeat(LoadUtils.parseStringToListNodes(initPath), reader, context, list);

        return root;
    }

    private void createDict(HierarchicalStreamReader reader, UnmarshallingContext context, List list) {
        list.add(context.convertAnother(reader.getValue(), dictionaryClass));
    }

    private void repeat(List<PathNode> initPath, HierarchicalStreamReader reader, UnmarshallingContext context, List list) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            PathNode format = getNewNode(reader);
            initPath.add(format);
            if (pathContains(initPath)) {
                if(pathNodes.size() == initPath.size()) {
                    createDict(reader, context, list);
                    initPath.remove(format);
                } else {
                    repeat(initPath, reader, context, list);
                }

            } else {
                initPath.remove(format);
                repeat(initPath, reader, context, list);
            }
            reader.moveUp();
        }
    }

    private PathNode getNewNode(HierarchicalStreamReader reader) {
        PathNode pathNode = new PathNode();
        pathNode.setNodeName(reader.getNodeName());

        Map<String, String> map = new HashMap<>();

        Iterator attributeNames = reader.getAttributeNames();
        for(Iterator<String> it = attributeNames; it.hasNext();) {
            String name = it.next();
            map.put(name, String.format("'%s'", reader.getAttribute(name)));
        }

        pathNode.setAttributes(map);
        return pathNode;
    }

    private boolean pathContains(List<PathNode> pNodes) {
        if(pathNodes.size() < pNodes.size())
            return false;

        for (int i = 0; i < pNodes.size(); i++) {
            if(!pathNodes.get(i).contains(pNodes.get(i))) {
                return false;
            }
        }

        return true;
    }


}
