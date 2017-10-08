package com.wpengine.merger.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;

/**
 * Serialization/deserialization helper methods.
 *
 */
public abstract class SerializationHelper {
    /**
     * Serializes to csv from Account.
     *
     * @param t
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> String serializeToCsv(T t) throws JsonProcessingException {
        ObjectWriter w = new CsvMapper().writerWithSchemaFor(t.getClass());
        return w.writeValueAsString(t);
    }

    /**
     * Deserializes to Account from CSV.
     *
     * @param <T>
     * @param str
     * @param t
     * @param schema
     * @return
     * @throws IOException
     */
    public static <T> T deserializeFromCsv(String str, Class<T> t, CsvSchema schema) throws IOException {
        ObjectReader r = new CsvMapper().reader(t).with(schema);
        return r.readValue(str);
    }

    /**
     * Deserializes to ServiceAccount from JSON.
     *
     * @param str
     * @param t
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T deserializeFromJson(String str, Class<T> t) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(str, t);
    }
}
