package com.wpengine.merger.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wpengine.merger.dataaccessor.AccountAccessor;
import org.junit.Test;

import java.io.IOException;

import static com.wpengine.merger.TestUtil.getInputSchema;
import static com.wpengine.merger.model.Account.STATUS.GOOD;
import static com.wpengine.merger.util.SerializationHelper.deserializeFromCsv;
import static com.wpengine.merger.util.SerializationHelper.deserializeFromJson;
import static com.wpengine.merger.util.SerializationHelper.serializeToCsv;
import static org.junit.Assert.assertEquals;

public class AccountTest {
    @Test
    public void testDeserializeAccount_fromCSV() throws IOException {
        assertEquals(new Account(1234, "aName", "fName", "2017-10-02"),
                deserializeFromCsv("1234,aName,fName,2017-10-02", Account.class, getInputSchema()));
    }

    @Test
    public void testDeserializeAccount_fromJSON() throws IOException {
        assertEquals(new AccountAccessor.ServiceAccount(1234, "2017-10-02", GOOD),
                deserializeFromJson("{\"account_id\":1234,\"created_on\":\"2017-10-02\",\"status\":\"good\"}", AccountAccessor.ServiceAccount.class));
    }

    @Test
    public void testSerializeAccount() throws JsonProcessingException {
        Account a = new Account(1234, "aName", "fName", "2017-10-02");
        a.setComment("");
        a.setStatusSetOn("2017-10-03");
        a.setStatus(GOOD);

        assertEquals("1234,aName,fName,2017-10-02,2017-10-03,GOOD,\n",
                serializeToCsv(a));
    }
}
