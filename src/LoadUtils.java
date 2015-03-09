import org.apache.commons.lang.time.DateUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * Created by elenko on 03.03.15.
 */
public class LoadUtils {

    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy.MM.dd";
    public static final String DATE_FORMAT_DD_MM_YYYY = "dd.MM.yyyy";

    public static void fillFieldValue(Object swf, String name, Object value, String dateFormat) {
        if(value == null)
            return;
        for (Method m : swf.getClass().getDeclaredMethods()) {
            if (m.getName().toLowerCase().endsWith(getSetMethodName(name))) {
                try {
                    Class<?> aClass = m.getParameterTypes()[0];
                    if(value instanceof BigDecimal) {
                        m.invoke(swf, (BigDecimal) value);
                    } else if(value instanceof Integer) {
                        m.invoke(swf, (Integer) value);
                    } else if(value instanceof Long) {
                        m.invoke(swf, (Long) value);
                    } else if (aClass.equals(Long.class)) {
                        m.invoke(swf, Long.parseLong((String) value));
                    } else if (aClass.equals(BigDecimal.class)) {
                        m.invoke(swf, new BigDecimal((String) value));
                    } else if (aClass.equals(Integer.class)) {
                        m.invoke(swf, Integer.parseInt((String) value));
                    } else if (aClass.equals(Date.class)) {
                        Date date = null;
                        if(value instanceof Date) {
                            date = (Date) value;
                        } else {
                            try {
                                date = DateUtils.parseDate((String) value, new String[] { dateFormat });
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        m.invoke(swf, date);
                    } else {
                        m.invoke(swf, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static String getSetMethodName(String name) {
        return String.format("set%s", name.replace("_", "").toLowerCase());
    }

    public static List<PathNode> parseStringToListNodes(String str) {
        List<PathNode> list = new ArrayList<>();
        if(str.isEmpty())
            return list;
        String[] splited = str.split("/");
        for (int i = 0; i < splited.length; i++) {
            PathNode e = new PathNode();
            String s = splited[i];
            if(s.contains("@")) {
                Map<String, String> map = new HashMap();
                int endIndex = s.indexOf("[");
                String name = s.substring(0, endIndex);
                String inpar = s.substring(endIndex, s.indexOf("]"));
                String[] attrs = inpar.replace("[", "").replace("]", "").split(",");
                for (int j = 0; j < attrs.length; j++) {
                    String attr = attrs[j];
                    int beginIndex = attr.indexOf("=");
                    map.put(attr.substring(attr.indexOf("@") + 1, beginIndex), attr.substring(beginIndex + 1));
                }
                e.setAttributes(map);
                e.setNodeName(name);
            } else {
                e.setNodeName(s);
            }

            list.add(e);
        }
        return list;
    }

}
