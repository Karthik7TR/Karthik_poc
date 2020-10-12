package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;

/**
 * Utility methods to marshall and unmarshal Java/JSON objects.
 *
 */
public class JsonUtils {
    public static <T> String toJson(final T obj) throws JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(obj);
        return json;
    }

    public static <T> T fromJson(final String json, final Class<T> clazz)
        throws JsonMappingException, JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final T obj = mapper.readValue(json, clazz);
        return obj;
    }

    public static <T> T fromJson(final String json, final TypeReference<T> type) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new EBookException(e);
        }
    }
}
