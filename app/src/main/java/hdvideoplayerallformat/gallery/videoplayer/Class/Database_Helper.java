package hdvideoplayerallformat.gallery.videoplayer.Class;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database_Helper extends SQLiteOpenHelper {

    private static final String Database_Name = "MX Video";

    private static final int Database_Version = 1;

    private static final String Table_Name = "History";

    private static final String Column_Id = "id";
    private static final String bucketId = "bucketId";
    private static final String bucketName = "bucketName";
    private static final String bucketPath = "bucketPath";
    private static final String name = "name";

    private static final String Create_Table = "Create table " + Table_Name
            + " ( " + Column_Id + " integer primary key autoincrement, " + bucketId
            + " text not null, " + bucketName + " text not null, " + bucketPath
            + " text not null," + name
            + " text not null)";

    private static final String Drop_Table = "Drop table if exists "
            + Table_Name;

    Context context;

    public Database_Helper(Context context) {
        super(context, Database_Name, null, Database_Version);
        this.context = context;
    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_Name, null, null);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int olderVersion, int newVersion) {
        db.execSQL(Drop_Table);
        onCreate(db);
    }

    public void insertHistoryData(BaseModel data) {

        getHistoryCheck(data.getBucketPath());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(bucketId, data.getBucketId());
        values.put(bucketName, data.getBucketName());
        values.put(bucketPath, data.getBucketPath());
        values.put(name, data.getName());
        db.insert(Table_Name, null, values);
        db.close();
    }

    public ArrayList<BaseModel> getHistoryData() {

        ArrayList<BaseModel> data = new ArrayList<BaseModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select_query = "Select * from " + Table_Name;

        Cursor cursor = db.rawQuery(select_query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    BaseModel data_model = new BaseModel(cursor.getString(1),
                            cursor.getString(2), cursor.getString(3),
                            cursor.getString(4));
                    data.add(data_model);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        db.close();
        return data;
    }

    public void getHistoryCheck(String path) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select_query = "Select * from " + Table_Name;

        Cursor cursor = db.rawQuery(select_query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(3).equals(path)) {
                        deleteOldHistory(path);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        db.close();
    }

    public void renameHistory(String from, String to) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select_query = "Select * from " + Table_Name;

        Cursor cursor = db.rawQuery(select_query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(3).equals(from)) {
                        updateHistory(from, to);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        db.close();
    }

    public void updateHistory(String from, String to) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(bucketPath, to);
        db.update(Table_Name, values, bucketPath + " = ?", new String[]{String.valueOf(from)});
        db.close();
    }

    public void deleteOldHistory(String path) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_Name, bucketPath + " = ?", new String[]{String.valueOf(path)});
        db.close();
    }

}
