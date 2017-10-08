package com.wpengine.merger.inputoutput;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.Closeable;
import java.io.IOException;

/**
 * Generic input interface.
 *
 * @param <T>
 */
public interface Inputter<T> extends Closeable {
    boolean hasMoreItems();
    T next(FORMAT format, CsvSchema schema) throws IOException;

    public enum FORMAT {
        CSV,
        JSON
    }
}
