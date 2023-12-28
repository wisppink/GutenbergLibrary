package org.example.LibraryService.Remote.Model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class Format {
    private final Map<String, String> formatMap = new HashMap<>();

    public Map<String, String> getFormatMap() {
        return formatMap;
    }

    @JsonAnySetter
    public void setDynamicProperty(String key, String value) {
        formatMap.put(key, value);
    }
}
