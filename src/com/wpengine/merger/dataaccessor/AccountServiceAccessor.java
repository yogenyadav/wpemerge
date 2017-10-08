package com.wpengine.merger.dataaccessor;

import com.github.rholder.retry.*;
import com.wpengine.merger.exceptions.DoesNotExistException;
import com.wpengine.merger.exceptions.MissingInfoException;
import com.wpengine.merger.exceptions.ServiceNotAvailableException;
import com.wpengine.merger.exceptions.UnknownException;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.wpengine.merger.util.SerializationHelper.deserializeFromJson;

/**
 * Account accessor implementation for a restful account service.
 *
 */
public class AccountServiceAccessor implements AccountAccessor {
    private static final int RETRIES = 3;

    private final OkHttpClient httpClient;
    private final String serviceEndpoint;

    @Inject
    public AccountServiceAccessor(OkHttpClient httpClient,
                                  @Named("serviceEndpoint") String serviceEndpoint) {
        this.httpClient = httpClient;
        this.serviceEndpoint = serviceEndpoint;
    }

    public ServiceAccount accountById(final int accountId) throws MissingInfoException {
        Retryer<ServiceAccount> retryer = RetryerBuilder.<ServiceAccount>newBuilder()
                .retryIfExceptionOfType(IOException.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(RETRIES))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .build();
        try {
            return retryer.call(() -> {
                Request request = new Request.Builder()
                        .url(this.serviceEndpoint + "/" + accountId)
                        .build();
                String result = this.httpClient.newCall(request).execute().body().string();
                ServiceAccount account = deserializeFromJson(result, ServiceAccount.class);
                validate(account);
                return account;
            });
        } catch (RetryException e) {
            throw new ServiceNotAvailableException("Retries exhausted.", e);
        } catch (ExecutionException e) {
            Throwable t = e.getCause();
            if (t.getMessage() != null && t.getMessage().equals("400")) {
                throw new DoesNotExistException();
            } else if (t instanceof MissingInfoException) {
                throw (MissingInfoException)t;
            } else {
                throw new UnknownException(String.format("Something went wrong when called service for account %s", accountId) , t);
            }
        }
    }

    private void validate(ServiceAccount account) throws MissingInfoException {
        if (account.getStatusSetOn() == null) {
            throw new MissingInfoException(String.format("No status set on found for account Id %s", account.getAccountId()));
        }
    }
}
