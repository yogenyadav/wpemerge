package test.com.wpengine.merger.util;

import com.google.common.base.Objects;
import org.junit.Test;

import java.io.IOException;

import static com.wpengine.merger.util.SerializationHelper.deserializeFromJson;
import static org.junit.Assert.assertEquals;

public class SerializationHelperTest {
    @Test
    public void testDeserializeFromJson() throws IOException {
        assertEquals(new Account("name", 1234),
                deserializeFromJson("{\"name\": \"name\", \"id\": 1234}", Account.class));
    }

    private static class Account {
        String name;
        int id;

        public Account() {

        }

        public Account(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Account account = (Account) o;
            return id == account.id &&
                    Objects.equal(name, account.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, id);
        }
    }
}
