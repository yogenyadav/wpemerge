package com.wpengine.merger.inputoutput;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.wpengine.merger.inputoutput.Inputter.FORMAT.CSV;
import static com.wpengine.merger.inputoutput.Inputter.FORMAT.JSON;
import static com.wpengine.merger.util.SerializationHelper.deserializeFromCsv;
import static com.wpengine.merger.util.SerializationHelper.deserializeFromJson;

/**
 * Stream based input.
 *
 * @param <T>
 */
public class StreamInputter<T> implements Inputter {
    private BufferedReader reader;
    private boolean hasMoreItems = false;
    private String nextLine;
    private final Class<T> type;

    public StreamInputter(InputStream iStream, Class<T> type) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));
        this.type = type;
        this.nextLine = this.reader.readLine();
        if (nextLine != null) {
            this.hasMoreItems = true;
        }
    }

    public boolean hasMoreItems() {
        return this.hasMoreItems;
    }

    public T next(FORMAT format, CsvSchema schema) throws IOException {
        String tmp = nextLine;
        this.nextLine = this.reader.readLine();
        if (nextLine == null) {
            this.hasMoreItems = false;
        }
        T out = null;
        if (format == CSV) {
            out = deserializeFromCsv(tmp, this.type, schema);
        } else if (format == JSON) {
            out = deserializeFromJson(tmp, this.type);
        }
        return out;
    }

    public void close() throws IOException {
        this.reader.close();
    }
}
