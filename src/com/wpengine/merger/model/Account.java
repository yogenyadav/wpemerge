package com.wpengine.merger.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Objects;

import java.io.IOException;

/**
 * Account model class.
 *
 */
@JsonDeserialize(using = Account.ItemDeserializer.class)
@JsonSerialize(using = Account.ItemSerializer.class)
@JsonPropertyOrder({"accountId", "accountName", "firstName", "createdOn", "statusSetOn", "status", "comment"})
public class Account {
    private int accountId;
    private String accountName;
    private String firstName;
    private String createdOn;
    private String statusSetOn;
    private STATUS status;
    private String comment;

    public int getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getStatusSetOn() {
        return statusSetOn;
    }

    public STATUS getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public void setStatusSetOn(String statusSetOn) {
        this.statusSetOn = statusSetOn;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                Objects.equal(accountName, account.accountName) &&
                Objects.equal(firstName, account.firstName) &&
                Objects.equal(createdOn, account.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId, accountName, firstName, createdOn);
    }

    public Account(int accountId, String accountName, String firstName, String createdOn) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.firstName = firstName;
        this.createdOn = createdOn;

    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", statusSetOn='" + statusSetOn + '\'' +
                ", status=" + status +
                '}';
    }

    //    public String serialize

    // File
    // Account ID, Account Name, First Name, and Created On

    // Service
    // account_id": 12345, "status": "good", "created_on": "2011-01-12"

    // Output
    // Account ID, First Name, Created On, Status, and Status Set On

    public static enum STATUS {
        GOOD
    }

    public static class ItemDeserializer extends StdDeserializer<Account> {

        public ItemDeserializer() {
            this(null);
        }

        public ItemDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Account deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            int accountid = Integer.valueOf(node.get("accountId").textValue());
            String accountName = node.get("accountName").asText();
            String firstName = node.get("firstName").asText();
            String createdOn = node.get("createdOn").asText();

            return new Account(accountid, accountName, firstName, createdOn);
        }
    }
    public static class ItemSerializer extends StdSerializer<Account> {

        public ItemSerializer() {
            this(null);
        }

        public ItemSerializer(Class<Account> t) {
            super(t);
        }

        @Override
        public void serialize(
                Account value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

            jgen.writeStartObject();
            jgen.writeNumberField("accountId", value.accountId);
            jgen.writeStringField("accountName", value.accountName);
            jgen.writeStringField("firstName", value.firstName);
            jgen.writeStringField("createdOn", value.createdOn);
            jgen.writeStringField("status", value.status !=null ? value.status.name() : null);
            jgen.writeStringField("statusSetOn", value.statusSetOn);
            jgen.writeStringField("comment", value.comment);
            jgen.writeEndObject();
        }
    }
}
