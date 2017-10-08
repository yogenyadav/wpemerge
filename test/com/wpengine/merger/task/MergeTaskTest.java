package com.wpengine.merger.task;

import com.google.common.collect.Lists;
import com.wpengine.merger.dataaccessor.AccountAccessor;
import com.wpengine.merger.exceptions.MissingInfoException;
import com.wpengine.merger.inputoutput.StreamOutputter;
import com.wpengine.merger.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MergeTaskTest {
    @Mock AccountAccessor accessor;

    @Test
    public void testSuccess() throws Exception {
        when(accessor.accountById(anyInt())).thenReturn(new AccountAccessor.ServiceAccount(12345, "2017-10-03", Account.STATUS.GOOD));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MergeTask task = MergeTask.builder()
                .withOutputter(new StreamOutputter(bos))
                .withAccountAccessor(accessor)
                .withAccounts(getAccounts())
                .build();
        task.call();
        assertEquals("12345,lexcorp,Lex,1/12/11,2017-10-03,GOOD,\n" +
                "8172,latveriaembassy,Victor,11/19/14,2017-10-03,GOOD,\n",
                new String(bos.toByteArray()));
    }

    @Test
    public void test_missing_info() throws Exception {
        when(accessor.accountById(anyInt())).thenThrow(new MissingInfoException("set status on not found"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MergeTask task = MergeTask.builder()
                .withOutputter(new StreamOutputter(bos))
                .withAccountAccessor(accessor)
                .withAccounts(getAccounts())
                .build();
        task.call();
        assertEquals("12345,lexcorp,Lex,1/12/11,,,\"Missing info from service for this account. com.wpengine.merger.exceptions.MissingInfoException: set status on not found\"\n" +
                "8172,latveriaembassy,Victor,11/19/14,,,\"Missing info from service for this account. com.wpengine.merger.exceptions.MissingInfoException: set status on not found\"\n",
                new String(bos.toByteArray()));
    }

    public List<Account> getAccounts() {
        return Lists.newArrayList(
                new Account(12345, "lexcorp", "Lex", "1/12/11"),
                new Account(8172, "latveriaembassy", "Victor", "11/19/14")
        );
    }
}
