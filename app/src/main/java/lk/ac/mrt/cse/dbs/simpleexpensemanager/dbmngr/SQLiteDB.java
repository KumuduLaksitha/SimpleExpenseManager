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

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQLiteDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_mgr";
    private static final String ACCOUNT_TABLE = "account";
    private static final String TRANSACTION_TABLE = "transaction_log";

    private static final String COLUMN_ACCOUNT_NO = ACCOUNT_TABLE + "_no";
    private static final String COLUMN_HOLDER_NAME = ACCOUNT_TABLE + "_holder_name";
    private static final String COLUMN_BANK_NAME = "bank_name";
    private static final String COLUMN_BALANCE = "balance";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
    private static final String COLUMN_AMOUNT = "amount";

    /**
     * I implemented methods for all needed actions by DAO classes
     * There is many code duplications in this implementation
     * but this is faster than using for loops or any other sorting methods inside DAO classes
     * DAO classes are clean and straightforward now
     */


    /**
     * creates database expense_mgr in the first run
     * creates tables account, transaction_log
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // account table
        String accountTableCreateQuery = "CREATE TABLE " + ACCOUNT_TABLE + " " +
                "(" + COLUMN_ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                COLUMN_BANK_NAME + " TEXT, " + COLUMN_HOLDER_NAME + " TEXT," +
                COLUMN_BALANCE + " REAL);";
        sqLiteDatabase.execSQL(accountTableCreateQuery);

        // transaction_log table
        String transactionTableCreateQuery = "CREATE TABLE " + TRANSACTION_TABLE + " " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATE + " TEXT, " +
                COLUMN_ACCOUNT_NO + " TEXT, " + COLUMN_TRANSACTION_TYPE + " INTEGER, " + COLUMN_AMOUNT + " REAL, " +
                "FOREIGN KEY (" + COLUMN_ACCOUNT_NO + ") REFERENCES " + ACCOUNT_TABLE + "(" + COLUMN_ACCOUNT_NO + "));";
        sqLiteDatabase.execSQL(transactionTableCreateQuery);

    }

    /**
     * this is version 1.0
     * no need of upgrade code
     * will be needed in future versions
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    /**
     * adds new account object to the database system
     * @param account
     */
    public void addAccount(Account account) {

        SQLiteDatabase database = this.getWritableDatabase();

        // create content value and insert it to database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ACCOUNT_NO, account.getAccountNo());
        cv.put(COLUMN_BANK_NAME, account.getBankName());
        cv.put(COLUMN_HOLDER_NAME, account.getAccountHolderName());
        cv.put(COLUMN_BALANCE, account.getBalance());

        database.insert(ACCOUNT_TABLE, null,  cv);

    }

    /**
     * add new transaction to database
     * @param transaction
     */
    public void addTransactionLog(Transaction transaction) {

        SQLiteDatabase database = this.getWritableDatabase();

        // create content value and insert it to database
        ContentValues cv = new ContentValues();

        // need to store date as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String transactionDate = dateFormat.format(transaction.getDate());
        cv.put(COLUMN_DATE, transactionDate);

        cv.put(COLUMN_ACCOUNT_NO, transaction.getAccountNo());

        // storing 1 (income) and 0 (expense) for Expense type in DB
        int transactionType = transaction.getExpenseType() == ExpenseType.INCOME ? 1 : 0;
        cv.put(COLUMN_TRANSACTION_TYPE, transactionType);

        cv.put(COLUMN_AMOUNT, transaction.getAmount());

        database.insert(TRANSACTION_TABLE, null, cv);

    }

    /**
     * get list of all transactions saved in the database
     * @return arraylist of all transactions
     */
    public List<Transaction> getTransactionList() {

        List<Transaction> transactionList = new ArrayList();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT * FROM " + TRANSACTION_TABLE + ";";
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

                Transaction transaction = new Transaction(transactionDateObject, accountNo, expenseType, amount);
                transactionList.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return transactionList;
    }

    /**
     * returns list of limited no of transaction logs preferred by user
     * @param limit
     * @return
     */
    public List<Transaction> getPaginatedTransactionList(int limit) {
        List<Transaction> paginatedTransactionList = new ArrayList();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT * FROM " + TRANSACTION_TABLE + " LIMIT ?;";
        Cursor cursor = database.rawQuery(readQuery, new String[] {Integer.toString(limit)});

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

                Transaction transaction = new Transaction(transactionDateObject, accountNo, expenseType, amount);
                paginatedTransactionList.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paginatedTransactionList;
    }

    /**
     * return list of all saved accounts
     * @return arraylist of account objects
     */
    public List<Account> getAccountList() {

        List<Account> accountList = new ArrayList<>();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT * FROM " + ACCOUNT_TABLE;
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
     * return arraylist of account numbers saved
     * @return
     */
    public List<String> getAccountNoList() {

        List<String> accountList = new ArrayList<>();

        // reading values using sql query
        SQLiteDatabase database = this.getReadableDatabase();
        String readQuery = "SELECT " + COLUMN_ACCOUNT_NO + " FROM " + ACCOUNT_TABLE;
        Cursor cursor = database.rawQuery(readQuery, null);

        // loop through cursor and fetch records
        if (cursor.moveToFirst()) {
            do {

                String accountNo = cursor.getString(0);
                accountList.add(accountNo);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return accountList;

    }

    /**
     * gives account object for a given account number
     * throws InvalidAccountException if account is not found
     * @param accountNo
     * @return account object
     */
    public Account getAccountByNo(String accountNo) throws InvalidAccountException{

        SQLiteDatabase database = this.getReadableDatabase();
        String getBalanceQuery = "SELECT * FROM " + ACCOUNT_TABLE + " WHERE " + COLUMN_ACCOUNT_NO + " = ?;";
        Cursor cursor = database.rawQuery(getBalanceQuery, new String[] {accountNo});

        // no need to loop
        // choosing account by primary key
        // no repetitions can happen
        if (cursor.moveToFirst()) {
            String bankName = cursor.getString(1);
            String holderName = cursor.getString(2);
            Double balance = cursor.getDouble(3);

            Account account = new Account(accountNo, bankName, holderName, balance);

            return account;
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);

    }

    /**
     * removes the given account
     * throws InvalidAccountException if account is not found
     * @param accountNo
     */
    public void removeAccount(String accountNo) throws InvalidAccountException{

        SQLiteDatabase database = this.getWritableDatabase();
        int returnValue = database.delete(ACCOUNT_TABLE, COLUMN_ACCOUNT_NO + " = ?", new String[]{accountNo});

        // return value gives no of records deleted
        if (returnValue == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

    }

    /**
     * get balance of a give account
     * throws InvalidAccountException if account is not found
     * @param accountNo
     * @return balance
     */
    public double getBalance(String accountNo) throws InvalidAccountException{

        SQLiteDatabase database = this.getReadableDatabase();
        String getBalanceQuery = "SELECT " + COLUMN_BALANCE + " FROM " + ACCOUNT_TABLE + " WHERE " + COLUMN_ACCOUNT_NO + " = ?;";
        Cursor cursor = database.rawQuery(getBalanceQuery, new String[] {accountNo});

        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    /**
     * update the balance of a account using given value
     * @param accountNo
     * @param balance
     */
    public void updateAccountBalance(String accountNo, double balance) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BALANCE, balance);
        database.update(ACCOUNT_TABLE, cv, COLUMN_ACCOUNT_NO + " = ?", new String[]{accountNo});

    }

    public SQLiteDB(@Nullable Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

}
