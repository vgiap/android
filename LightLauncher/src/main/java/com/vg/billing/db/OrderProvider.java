package com.vg.billing.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


public class OrderProvider extends ContentProvider {
    public static SQLiteOpenHelper mOpenHelper;
    public static final String DATABASE_NAME = "order.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_ORDER = "order_info";

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor result = qb.query(db, projection, args.where, args.args,
                args.groupby, null, sortOrder);
        // result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = db.insert(args.table, null, values);
        if (rowId <= 0)
            return null;
        else {
             getContext().getContentResolver().notifyChange(uri, null);
        }
        uri = ContentUris.withAppendedId(uri, rowId);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DatabaseHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTableOrder(db);
        }



        
        private void createTableOrder(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_ORDER + " (" + OrderColumns._ID
                    + " INTEGER PRIMARY KEY,"
                    + OrderColumns.PRODUCT_ID + " TEXT,"
                    + OrderColumns.USER_ID + " TEXT,"
                    + OrderColumns.PAY_CODE + " TEXT,"
                    + OrderColumns.PAY_TYPE + " TEXT,"
                    + OrderColumns.ITEM_TYPE + " TEXT,"
                    + OrderColumns.JSON_PURCHASE_INFO + " TEXT,"
                    + OrderColumns.SIGNATURE + " TEXT,"
                    + OrderColumns.VERSION_CODE + " INTEGER,"
                    + OrderColumns.IAB_ORDER_ID + " TEXT,"
                    + OrderColumns.HAS_ORDERED + " INTEGER DEFAULT 0,"
                    + OrderColumns.HAS_CONSUMED + " INTEGER DEFAULT 0 );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
        
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion,
                int newVersion) {
            dropTables(db);
            onCreate(db);
        }

        private void dropTables(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        }
    }

    static class SqlArguments {
        public final String table;
        public final String where;
        public final String[] args;
        public String groupby = null;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException(
                        "WHERE clause not supported: " + url);
            } else {

                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;

            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }
    
    private static final String AUTHORITIES_SUFFIX = ".order";
    
    /*
     * 
     * <provider android:name="com.borqs.market.db.DownLoadProvider"
                  android:exported="false"
                  android:authorities="com.lightapp.lightlauncher.order"/>
     */
    public static Uri getContentURI(Context ctx, String tableName) {
        String pkgName = ctx.getPackageName();
        StringBuilder sb = new StringBuilder("content://");
        sb.append(pkgName).append(AUTHORITIES_SUFFIX).append("/").append(tableName);
        return Uri.parse(sb.toString());
    }
}


