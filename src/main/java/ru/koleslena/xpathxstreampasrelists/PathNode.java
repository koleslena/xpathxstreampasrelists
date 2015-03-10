package ru.koleslena.xpathxstreampasrelists;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elenko on 09.03.15.
 */
public class PathNode {
    String nodeName;
    Map<String, String> attributes = new HashMap<>();


    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean contains(Object obj) {
        if(obj != null && obj instanceof PathNode) {
            PathNode node = (PathNode) obj;
            Map<String, String> nodeAttributes = node.getAttributes();
            Map<String, String> thisAttributes = this.getAttributes();
            if (this.getNodeName().equals(node.getNodeName())) {
                if(!thisAttributes.isEmpty()) {
                    for (String k : nodeAttributes.keySet()) {
                        if (!thisAttributes.containsKey(k) || !nodeAttributes.get(k).equals(thisAttributes.get(k))) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
