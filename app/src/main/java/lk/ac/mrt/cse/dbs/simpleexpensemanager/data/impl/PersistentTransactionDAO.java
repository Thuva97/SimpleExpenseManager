package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    public static final String DATABASE_NAME = "170644R.db";
    public static final String TABLE_NAME = "transaction_table";
    public static final Date COL_1 = "date";
    public static final String COL_2 = "accountNo";
    public static final ExpenseType COL_3 = "expenseType";
    public static final double COL_4 = "amount";

    public PersistentTransactionDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (date Date,accountNo STRING ,expenseType ExpenseType,amount DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        List<Transaction> transactionList = new ArrayList<>();

        while (res.moveToNext()) {
            Transaction t = new Transaction(res.getDate(0), res.getString(1), ExpenseType.(res.getString(2)), res.getDouble(3));
            TransactionList.add(t);
        }
        return TransactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        List<Transaction> transactions = getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        else {
            return transactions.subList(size - limit, size);
        }
    }

}