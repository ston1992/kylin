package com.kylinolap.cube.dataGen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kylinolap.common.util.JsonUtil;

/**
 * Created by honma on 5/29/14.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class GenConfig {

    @JsonProperty("columnConfigs")
    private ArrayList<ColumnConfig> columnConfigs;

    private HashMap<String, ColumnConfig> cache = new HashMap<String, ColumnConfig>();

    public ArrayList<ColumnConfig> getColumnConfigs() {
        return columnConfigs;
    }

    public void setColumnConfigs(ArrayList<ColumnConfig> columnConfigs) {
        this.columnConfigs = columnConfigs;
    }

    public ColumnConfig getColumnConfigByName(String columnName) {
        columnName = columnName.toLowerCase();

        if (cache.containsKey(columnName))
            return cache.get(columnName);

        for (ColumnConfig cConfig : columnConfigs) {
            if (cConfig.getColumnName().toLowerCase().equals(columnName)) {
                cache.put(columnName, cConfig);
                return cConfig;
            }
        }
        cache.put(columnName, null);
        return null;
    }

    public static GenConfig loadConfig(InputStream stream) {
        try {
            GenConfig config = JsonUtil.readValue(stream, GenConfig.class);
            return config;
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
