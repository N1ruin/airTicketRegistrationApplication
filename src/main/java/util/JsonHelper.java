package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonHelper {
    private static final Logger log = LogManager.getLogger(JsonHelper.class);
    private final ObjectMapper objectMapper;

    public JsonHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Serialization error", e);
            throw new RuntimeException("Failed to serialize to JSON", e);
        }
    }

    public <T> T fromJson(String json, Class<T> targetClass) {
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            log.error("Deserialization error", e);
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
