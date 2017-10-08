package com.wpengine.merger.dataaccessor;

import com.wpengine.merger.exceptions.DoesNotExistException;
import com.wpengine.merger.exceptions.MissingInfoException;
import com.wpengine.merger.exceptions.ServiceNotAvailableException;
import com.wpengine.merger.exceptions.UnknownException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static com.wpengine.merger.model.Account.STATUS.GOOD;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceAccessorTest {
    @Mock private OkHttpClient httpClient;
    @Mock private Call call;
    @Mock private okhttp3.Response response;
    @Mock private okhttp3.ResponseBody body;

    @Test
    public void testAccountById_Success() throws IOException, MissingInfoException {
        when(httpClient.newCall(anyObject())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(body);
        when(body.string()).thenReturn("{\"account_id\":12345,\"created_on\":\"2017-10-03\",\"status\":\"good\"}");
        AccountAccessor.ServiceAccount sa = new AccountServiceAccessor(httpClient, "http://abc.com").accountById(10);
        assertEquals(new AccountAccessor.ServiceAccount(12345, "2017-10-03", GOOD), sa);
    }

    @Test
    public void testAccountById_call_retried() throws IOException, MissingInfoException {
        when(httpClient.newCall(anyObject())).thenReturn(call);
        when(call.execute()).thenThrow(new IOException());
        try {
            new AccountServiceAccessor(httpClient, "http://abc.com").accountById(10);
        } catch (ServiceNotAvailableException e) {
            verify(call, times(3)).execute();
            verifyNoMoreInteractions(call);
        }
    }

    @Test(expected = DoesNotExistException.class)
    public void testAccountById_does_not_exist() throws IOException, MissingInfoException {
        when(httpClient.newCall(anyObject())).thenReturn(call);
        when(call.execute()).thenThrow(new RuntimeException("400"));
        new AccountServiceAccessor(httpClient, "http://abc.com").accountById(10);
    }

    @Test(expected = MissingInfoException.class)
    public void testAccountById_missing_info() throws IOException, MissingInfoException {
        when(httpClient.newCall(anyObject())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(body);
        when(body.string()).thenReturn("{\"account_id\":12345,\"status\":\"good\"}");
        new AccountServiceAccessor(httpClient, "http://abc.com").accountById(10);
    }

    @Test(expected = UnknownException.class)
    public void testAccountById_unknown() throws IOException, MissingInfoException {
        when(httpClient.newCall(anyObject())).thenReturn(call);
        when(call.execute()).thenThrow(new RuntimeException("500"));
        new AccountServiceAccessor(httpClient, "http://abc.com").accountById(10);
    }
}
