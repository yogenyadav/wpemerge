package com.wpengine.merger.dataaccessor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.base.Objects;
import com.wpengine.merger.exceptions.MissingInfoException;
import com.wpengine.merger.model.Account;

import java.io.IOException;

import static com.wpengine.merger.model.Account.STATUS.GOOD;

/**
 * Account accessor interface.
 *
 */
public interface AccountAccessor {
    ServiceAccount accountById(int accountId) throws MissingInfoException;

    @JsonDeserialize(using = ItemDeserializer.class)
    static class ServiceAccount {
        private int accountId;
        private String statusSetOn;
        private Account.STATUS status;

        public ServiceAccount(int accountId, String statusSetOn, Account.STATUS status) {
            this.accountId = accountId;
            this.statusSetOn = statusSetOn;
            this.status = status;
        }

        public String getStatusSetOn() {
            return statusSetOn;
        }

        public Account.STATUS getStatus() {
            return status;
        }

        public int getAccountId() {
            return accountId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ServiceAccount that = (ServiceAccount) o;
            return accountId == that.accountId &&
                    Objects.equal(statusSetOn, that.statusSetOn) &&
                    status == that.status;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(accountId, statusSetOn, status);
        }
    }
    public static class ItemDeserializer extends StdDeserializer<ServiceAccount> {

        public ItemDeserializer() {
            this(null);
        }

        public ItemDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public ServiceAccount deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            int accountid = node.get("account_id").numberValue().intValue();
            String statusSetOn = node.get("created_on") != null ? node.get("created_on").asText() : null;
            Account.STATUS status = node.get("status").asText() != null && node.get("status").asText().equals("good")
                    ? GOOD : null;

            return new ServiceAccount(accountid, statusSetOn, status);
        }
    }
}
