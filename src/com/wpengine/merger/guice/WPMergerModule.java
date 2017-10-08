package com.wpengine.merger.guice;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.wpengine.merger.dataaccessor.AccountAccessor;
import com.wpengine.merger.dataaccessor.AccountServiceAccessor;
import okhttp3.OkHttpClient;

import javax.inject.Named;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Guice module for dependency injections.
 *
 */
public class WPMergerModule extends AbstractModule {
    protected void configure() {
        bind(AccountAccessor.class).to(AccountServiceAccessor.class).in(Singleton.class);
    }

    @Provides
    public static CsvSchema getInputSchema() {
        return CsvSchema.builder()
                .setColumnSeparator(',')
                .addColumn("accountId")
                .addColumn("accountName")
                .addColumn("firstName")
                .addColumn("createdOn").setUseHeader(false)
                .build();
    }

    @Provides
    public ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(5);
    }

    @Provides
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Named("serviceEndpoint")
    public String getServiceEndpoint() {
        return "http://interview.wpengine.io/v1/accounts";
    }
}
