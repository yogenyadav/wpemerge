package com.wpengine.merger.inputoutput;

import java.io.Closeable;
import java.io.IOException;

/**
 * Generic output interface.
 *
 */
public interface Outputter extends Closeable {
    void write(String s) throws IOException;
}
