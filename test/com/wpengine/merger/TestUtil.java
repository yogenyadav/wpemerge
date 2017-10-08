package com.wpengine.merger;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public abstract class TestUtil {
    public static CsvSchema getInputSchema() {
        return CsvSchema.builder()
                .setColumnSeparator(',')
                .addColumn("accountId")
                .addColumn("accountName")
                .addColumn("firstName")
                .addColumn("createdOn").setUseHeader(false)
                .build();
    }

}
