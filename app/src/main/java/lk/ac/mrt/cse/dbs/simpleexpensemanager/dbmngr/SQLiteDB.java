package lk.ac.mrt.cse.dbs.simpleexpensemanager.dbmngr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQLiteDB extends SQLiteOpenHelper {

    public SQLiteDB(@Nullable Context context) {

        super(context, "expense_mgr", null, 1);

    }

    /**
     * creates database expense_mgr in the first run
     * creates tables account, transaction_log
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // account table
        String accountTableCreateQuery = "CREATE TABLE expense_mgr.account " +
                "(account_no TEXT PRIMARY KEY AUTOINCREMENT," +
                "bank_name TEXT, account_holder_name TEXT," +
                "balance REAL);";
        sqLiteDatabase.execSQL(accountTableCreateQuery);

        // transaction_log table
        String transactionTableCreateQuery = "CREATE TABLE expense_mgr.transaction_log " +
                "(id INTEGER PRIMARY KEY, date TEXT, " +
                "account_no TEXT, transaction_type INTEGER, amount REAL, " +
                "FOREIGN KEY (account_no) REFERENCES account(account_no));";
        sqLiteDatabase.execSQL(transactionTableCreateQuery);

    }

    /**
     * adds new account object to the database system
     * @param account
     * @return boolean showing success of operation
     */
    public boolean addAccount(Account account) {

        SQLiteDatabase database = this.getWritableDatabase();

        // create content value and insert it to database
        ContentValues cv = new ContentValues();
        cv.put("account_no", account.getAccountNo());
        cv.put("bank_name", account.getBankName());
        cv.put("account_holder_name", account.getAccountHolderName());
        cv.put("balance", account.getBalance());

        long returnValue = database.insert("account", null,  cv);

        // check return value for success
        if (returnValue < 0)
            return false;
        else
            return true;

    }

    /**
     * add new transaction to database
     * @param transaction
     * @return boolean showing success of the operation
     */
    public boolean addTransactionLog(Transaction transaction) {

        SQLiteDatabase database = this.getWritableDatabase();

        // create content value and insert it to database
        ContentValues cv = new ContentValues();

        // need to store date as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String transactionDate = dateFormat.format(transaction.getDate());
        cv.put("date", transactionDate);

        cv.put("account_no", transaction.getAccountNo());

        // storing 1 (income) and 0 (expense) for Expense type in DB
        int transactionType = transaction.getExpenseType() == ExpenseType.INCOME ? 1 : 0;
        cv.put("transaction_type", transactionType);

        cv.put("amount", transaction.getAmount());

        long returnValue = database.insert("transaction_log", null, cv);

        // check return value for success
        if (returnValue < 0)
            return false;
        else
            return true;

    }

    /**
     * get list of all transactions saved in the database
     * @return arraylist of all transactions
     */
    public List<Transaction> getTransactionList() {

        List<Transaction> transactionList = new ArrayList();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT * FROM expense_mgr.transaction_log;";
        Cursor cursor = database.rawQuery(readQuery, null);

        // loop through cursor and fetch records
        if (cursor.moveToFirst()) {
            do {

                String transactionDate = cursor.getString(1);
                String accountNo = cursor.getString(2);
                ExpenseType expenseType = cursor.getInt(3) ==
                        1 ? ExpenseType.INCOME : ExpenseType.EXPENSE;
                double amount = cursor.getDouble(4);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date transactionDateObject = null;
                try {
                    transactionDateObject = dateFormat.parse(transactionDate);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                Transaction transaction =
                        new Transaction(transactionDateObject, accountNo, expenseType, amount);
                transactionList.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return transactionList;
    }

    /**
     * return list of all saved accounts
     * @return arraylist of account objects
     */
    public List<Account> getAccountList() {

        List<Account> accountList = new ArrayList<>();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT * FROM expense_mgr.account";
        Cursor cursor = database.rawQuery(readQuery, null);

        // loop through cursor and fetch records
        if (cursor.moveToFirst()) {
            do {

                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String holderName = cursor.getString(2);
                Double balance = cursor.getDouble(3);

                Account account = new Account(accountNo, bankName, holderName, balance);
                accountList.add(account);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return accountList;

    }

    /**
     * this is version 1.0
     * no need of upgrade code
     * will be needed in future versions
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

}