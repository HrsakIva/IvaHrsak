package hr.math.kolokvij;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ihrsak on 1/26/18.
 */

public class DBAdapter {

    static final String KEY_ROWID = "_id";
    static final String KEY_ROWID_PERIODA = "_idperioda";
    static final String KEY_NAZIV = "naziv";
    static final String KEY_AUTOR = "autor";
    static final String TAG = "DBAdapter";
    static final String KEY_RAZDOBLJE = "razdoblje";
    static final String KEY_GLAVNIP = "glavnip";

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE_SLIKE = "slike";
    static final String DATABASE_TABLE_PERIOD = "period";
    static final int DATABASE_VERSION = 2;

    static final String DATABASE_CREATE_SLIKE =
            "create table slike (_id integer primary key autoincrement, "
                    + "naziv text not null, autor text not null);";
    static final String DATABASE_CREATE_PERIOD = "create table period (_idperioda integer primary key autoincrement, "
            + "razdoblje text not null, glavnip text not null );";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE_SLIKE);
                db.execSQL(DATABASE_CREATE_PERIOD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading db from" + oldVersion + "to"
                    + newVersion );
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertSlike(String naziv, String autor)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAZIV, naziv);
        initialValues.put(KEY_AUTOR, autor);
        return db.insert(DATABASE_TABLE_SLIKE, null, initialValues);
    }
    public long insertPeriod(String razdoblje, String glavnip)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RAZDOBLJE, razdoblje);
        initialValues.put(KEY_GLAVNIP, glavnip);
        return db.insert(DATABASE_TABLE_PERIOD, null, initialValues);
    }

    //---deletes a particular contact---
    public boolean deleteSLike(long rowId)
    {
        return db.delete(DATABASE_TABLE_SLIKE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deletePeriod(long rowId)
    {
        return db.delete(DATABASE_TABLE_PERIOD, KEY_ROWID_PERIODA + "=" + rowId, null) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllSlike()
    {
        return db.query(DATABASE_TABLE_SLIKE, new String[] {KEY_ROWID, KEY_NAZIV,
                KEY_AUTOR}, null, null, null, null, null);
    }

    public Cursor getAllPeriod()
    {
        return db.query(DATABASE_TABLE_PERIOD, new String[] {KEY_ROWID_PERIODA, KEY_RAZDOBLJE,
                KEY_GLAVNIP}, null, null, null, null, null);
    }

    //---retrieves a particular contact---
    public Cursor getSlika(String autor) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_SLIKE, new String[] {KEY_ROWID,
                                KEY_NAZIV, KEY_AUTOR}, KEY_AUTOR + "='" + autor+"'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getPeriod(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_PERIOD, new String[] {KEY_ROWID_PERIODA,
                                KEY_RAZDOBLJE, KEY_GLAVNIP}, KEY_ROWID_PERIODA + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateSlike(long rowId, String naziv, String autor)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAZIV, naziv);
        args.put(KEY_AUTOR, autor);
        return db.update(DATABASE_TABLE_SLIKE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    public boolean updatePeriod(long rowId, String razdoblje, String glavnip)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_RAZDOBLJE, razdoblje);
        args.put(KEY_GLAVNIP, glavnip);
        return db.update(DATABASE_TABLE_PERIOD, args, KEY_ROWID_PERIODA + "=" + rowId, null) > 0;
    }

}
