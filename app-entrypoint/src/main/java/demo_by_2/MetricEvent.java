package demo_by_2;

import java.util.HashMap;
import java.util.Map;

public class MetricEvent {

    public String name;

    public long timestamp;

    public Map<String, Object> fields = new HashMap<>();

    public Map<String, String> tags = new HashMap<>();

    public MetricEvent() {
    }

    public MetricEvent(String name, long timestamp, Map<String, Object> fields, Map<String, String> tags) {
        this.name = name;
        this.timestamp = timestamp;
        this.fields = fields;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}