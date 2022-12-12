package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqliteimpl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqliteimpl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager{

    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;

    public PersistentExpenseManager(Context context) {

        transactionDAO = new PersistentTransactionDAO(context);
        accountDAO = new PersistentAccountDAO(context);

        try {

            setup();

        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setup() throws ExpenseManagerException{

        setTransactionsDAO(transactionDAO);
        setAccountsDAO(accountDAO);

    }
}
