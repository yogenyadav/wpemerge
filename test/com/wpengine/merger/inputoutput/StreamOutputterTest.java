package com.wpengine.merger.inputoutput;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class StreamOutputterTest {
    @Test
    public void testOutput() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Outputter o = new StreamOutputter(bos);
            o.write("8172,latveriaembassy,Victor,11/19/14\n");
            o.write("1924,brotherhood,Max,2/29/12");
            assertEquals("8172,latveriaembassy,Victor,11/19/14\n1924,brotherhood,Max,2/29/12",
                    new String(bos.toByteArray(), StandardCharsets.UTF_8));
        }
    }
}
