import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
* Created by elenko on 25.02.15.
*/
public class DictionaryConverter<D extends DictionaryObject> implements Converter {

    private Class<D> dictionaryClass;
    public static final String DATE_FORMAT = "yyyy.MM.dd";
    private String fieldName;
    private String fieldValue;

    public DictionaryConverter(Class<D> dictionaryClass, String fieldName, String fieldValue) {
        this.dictionaryClass = dictionaryClass;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException();
    }

    protected Class<D> getDictionaryClass() {
        return this.dictionaryClass;
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object swf = null;
        try {
            swf = getDictionaryClass().newInstance();

            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String name = reader.getAttribute(getFieldName());
                Object value = reader.getAttribute(getFieldValue());//TODO type
                LoadUtils.fillFieldValue(swf, name, value, LoadUtils.DATE_FORMAT_YYYY_MM_DD);
                reader.moveUp();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return swf;
    }

    @Override
    public boolean canConvert(Class type) {
        return getDictionaryClass().equals(type);
    }
}
