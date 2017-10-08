package com.wpengine.merger.inputoutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Stream based output.
 *
 */
public class StreamOutputter implements Outputter {
    private final BufferedWriter writer;

    public StreamOutputter(OutputStream oStream) {
        this.writer = new BufferedWriter(new OutputStreamWriter(oStream, StandardCharsets.UTF_8));
    }

    public void write(String s) throws IOException {
        this.writer.write(s);
        this.writer.flush();
    }

    public void close() throws IOException {
        this.writer.close();
    }
}
