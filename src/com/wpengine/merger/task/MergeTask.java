package com.wpengine.merger.task;

import com.wpengine.merger.dataaccessor.AccountAccessor;
import com.wpengine.merger.exceptions.MissingInfoException;
import com.wpengine.merger.exceptions.UnknownException;
import com.wpengine.merger.inputoutput.Outputter;
import com.wpengine.merger.model.Account;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import static com.wpengine.merger.util.SerializationHelper.serializeToCsv;

/**
 * Task which is a callable to merge account from an input with account from account accessor.
 *
 */
public class MergeTask implements Callable<Boolean> {
    private final List<Account> accounts;
    private final AccountAccessor accountAccessor;
    private final Outputter outputter;

    private MergeTask(List<Account> accounts, AccountAccessor accountAccessor, Outputter outputter) {
        this.accounts = accounts;
        this.accountAccessor = accountAccessor;
        this.outputter = outputter;
    }

    public Boolean call() throws Exception {
        for (Account account : this.accounts) {
            try {
                AccountAccessor.ServiceAccount serviceAccount = this.accountAccessor.accountById(account.getAccountId());
                account.setStatus(serviceAccount.getStatus());
                account.setStatusSetOn(serviceAccount.getStatusSetOn());
            } catch (MissingInfoException e) {
                account.setComment("Missing info from service for this account." + " " + e);
            }
        }
        save();
        return true;
    }

    private void save() {
        try {
            for (Account account : accounts) {
                outputter.write(serializeToCsv(account));
            }
        } catch (IOException e) {
            throw new UnknownException("Something went wrong while saving accounts.", e);
        } finally {
            try {
                this.outputter.close();
            } catch (IOException e) {
                throw new UnknownException("Something went wrong while closing output stream.", e);
            }
        }
    }

    public static MergeTaskBuilder builder(){
        return new MergeTaskBuilder();
    }

    public static class MergeTaskBuilder {
        private List<Account> accounts;
        private AccountAccessor accountAccessor;
        private Outputter outputter;

        private MergeTaskBuilder() {
        }

        public MergeTaskBuilder withAccounts(List<Account> accounts) {
            this.accounts = accounts;
            return this;
        }

        public MergeTaskBuilder withAccountAccessor(AccountAccessor accountAccessor) {
            this.accountAccessor = accountAccessor;
            return this;
        }

        public MergeTaskBuilder withOutputter(Outputter outputter) {
            this.outputter = outputter;
            return this;
        }

        public MergeTask build() {
            if (accountAccessor == null || accounts == null || outputter == null) {
                throw new IllegalStateException("builder needs accounts, accountAccessor and outputter to create task.");
            }
            return new MergeTask(this.accounts, this.accountAccessor, outputter);
        }
    }
}
