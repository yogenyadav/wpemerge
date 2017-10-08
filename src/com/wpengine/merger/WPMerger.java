package com.wpengine.merger;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.wpengine.merger.dataaccessor.AccountAccessor;
import com.wpengine.merger.guice.WPMergerModule;
import com.wpengine.merger.inputoutput.Inputter;
import com.wpengine.merger.inputoutput.Outputter;
import com.wpengine.merger.inputoutput.StreamInputter;
import com.wpengine.merger.inputoutput.StreamOutputter;
import com.wpengine.merger.model.Account;
import com.wpengine.merger.task.MergeTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Main program that brings it all together.
 *
 */
public class WPMerger {
    private static final int CHUNK_SIZE = 5;
    private static final int TIMEOUT_SEC = 10;
    private static final String OUTPUT_HEADER = "";

    private final AccountAccessor accountAccessor;
    private final ExecutorService executorService;
    private final CsvSchema schema;

    private Map<String, Future<Boolean>> chunkedFiles = new HashMap<>();

    @Inject
    public WPMerger(AccountAccessor accountAccessor, ExecutorService executorService, CsvSchema schema) {
        this.accountAccessor = accountAccessor;
        this.executorService = executorService;
        this.schema = schema;
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        validateInputs(args);

        WPMerger wpMerger = Guice.createInjector(new WPMergerModule()).getInstance(WPMerger.class);
        String[] inputPath = args[0].split("/");
        String dir = String.join("/", Arrays.copyOfRange(inputPath, 0, inputPath.length - 1));
        String input = inputPath[inputPath.length - 1];
        String output = args[1];
        wpMerger.merge(dir, input, output);
    }

    private void merge(String dir, String input, String output) throws IOException, ExecutionException, InterruptedException {
        try (Inputter<Account> inputter = new StreamInputter(Files.newInputStream(Paths.get(dir + "/" + input)), Account.class);
             BufferedWriter bw = Files.newBufferedWriter(Paths.get(dir + "/" + output))) {

            addTasks(dir, inputter);

            executorService.shutdown();
            try {
                boolean b = executorService.awaitTermination(TIMEOUT_SEC, TimeUnit.SECONDS);
                if (!b) {
                    errorAndShutdown();
                }
            } catch (InterruptedException e) {
                errorAndShutdown();
            }

            mergeChunkedOutputs(bw);
        }
        System.out.println("File processing is completed.");
        System.exit(0);
    }

    private void mergeChunkedOutputs(BufferedWriter writer) throws IOException, ExecutionException, InterruptedException {
        writer.write(OUTPUT_HEADER);
        for (String file : this.chunkedFiles.keySet()) {
            this.chunkedFiles.get(file).get();
            Path path = Paths.get(file);
            try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                while ((line = r.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            Files.delete(path);
        }
    }

    private void addTasks(String dir, Inputter<Account> inputter) throws IOException {
        boolean done = false;
        while (!done) {
            List<Account> l = new ArrayList<>();
            for (int i = 0; i < CHUNK_SIZE; i++) {
                if (inputter.hasMoreItems()) {
                    l.add(inputter.next(Inputter.FORMAT.CSV, schema));
                } else {
                    done = true;
                }
            }
            if (l.size() > 0) {
                String fileName = String.valueOf(System.currentTimeMillis());
                Outputter outputter = new StreamOutputter(Files.newOutputStream(Paths.get(dir + "/" + fileName)));
                MergeTask task = MergeTask.builder()
                        .withAccounts(l)
                        .withAccountAccessor(accountAccessor)
                        .withOutputter(outputter)
                        .build();
                this.chunkedFiles.put(dir + "/" + fileName, executorService.submit(task));
            }
        }
    }

    private void errorAndShutdown() {
        System.out.println("Something went wrong or processing taking more than usual time, shutting down now, processing is incomplete.");
        executorService.shutdownNow();
        System.exit(-1);
    }

    private static void validateInputs(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: wp_merge <input file> <output file>");
            System.exit(1);
        } else {
            String input = args[0];
            String output = args[1];
            if (Strings.isNullOrEmpty(input) || Strings.isNullOrEmpty(output)) {
                System.out.println("Usage: wp_merge <input file> <output file>");
                System.exit(1);
            }

            File inputFile = new File(input);
            if (!inputFile.exists() || !inputFile.canRead()) {
                System.out.println("Input file either does not exists or not readable.");
                System.exit(1);
            }
        }
    }
}
