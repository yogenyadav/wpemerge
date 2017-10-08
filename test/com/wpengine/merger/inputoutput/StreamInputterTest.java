package com.wpengine.merger.inputoutput;

import com.wpengine.merger.TestUtil;
import com.wpengine.merger.model.Account;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;


public class StreamInputterTest {
    @Test
    public void testReadInputStream() throws IOException {
        InputStream stream = new ByteArrayInputStream(getCSV().getBytes(StandardCharsets.UTF_8));
        Inputter<Account> inputter = new StreamInputter<>(stream, Account.class);
        List<Account> l = new ArrayList<>();
        while (inputter.hasMoreItems()) {
            l.add(inputter.next(Inputter.FORMAT.CSV, TestUtil.getInputSchema()));
        }

        assertThat(l, containsInAnyOrder(
                new Account(12345, "lexcorp", "Lex", "1/12/11"),
                new Account(8172, "latveriaembassy", "Victor", "11/19/14"),
                new Account(1924, "brotherhood", "Max", "2/29/12"),
                new Account(222222, "leagueofassassins", "Ra's", "3/1/12"),
                new Account(48213, "kingpin", "Wilson", "7/7/15"),
                new Account(918299, "oscorp", "Norman", "4/29/14"),
                new Account(88888, "dococt", "Otto", "8/8/13")
        ));

    }

    private String getCSV() {
        return "12345,lexcorp,Lex,1/12/11\n" +
                "8172,latveriaembassy,Victor,11/19/14\n" +
                "1924,brotherhood,Max,2/29/12\n" +
                "222222,leagueofassassins,Ra's,3/1/12\n" +
                "48213,kingpin,Wilson,7/7/15\n" +
                "918299,oscorp,Norman,4/29/14\n" +
                "88888,dococt,Otto,8/8/13";
    }
}
