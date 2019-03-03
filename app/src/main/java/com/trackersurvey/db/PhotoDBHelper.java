package com.trackersurvey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.trackersurvey.bean.CommentMediaFilesData;
import com.trackersurvey.bean.InterestMarkerData;

/**
 * Created by zh931 on 2018/5/11.
 * 兴趣点本地数据库
 */

public class PhotoDBHelper {
    public static final int DBREAD  = 1;
    public static final int DBWRITE = 2;

    private static final String DB_NAME    = "happyNavi.db";
    private static final int    DB_VERSION = 5;

    private static final String   USEREVENT_TABLE = "UserEvent";
    public static final  String[] COLUMNS_UE      = {"CreateTime", "PoiNo", "Longitude",
            "Latitude", "Altitude", "Country", "Province", "City", "PlaceName", "Context", "TraceID",
            "FileNum", "Video", "Audio", "UserID", "Feeling", "Behaviour",
            "Duration", "Companion", "Relationship", "StateType", "PoiID", "Share"};

    private static final String   FILE_TABLE   = "EventFile";
    public static final  String[] COLUMNS_FILE = {"FileNO", "FileName",
            "CreateTime", "FileType", "ThumbnailName", "FileID"};

    private DBOpenHelper   helper;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        private static final String CREATE_USEREVENT = "create table "
                + USEREVENT_TABLE + "(" + COLUMNS_UE[0] + " datetime," + COLUMNS_UE[1] + " integer default 0, "
                + COLUMNS_UE[2] + " real not null," + COLUMNS_UE[3]
                + " real not null," + COLUMNS_UE[4] + " real not null,"
                + COLUMNS_UE[5] + " text ," + COLUMNS_UE[6] + " text ,"
                + COLUMNS_UE[7] + " text ," + COLUMNS_UE[8] + " text ,"
                + COLUMNS_UE[9] + " text , "
                + COLUMNS_UE[10] + " integer not null," + COLUMNS_UE[11]
                + " integer not null, " + COLUMNS_UE[12] + " integer,"
                + COLUMNS_UE[13] + " integer," + COLUMNS_UE[14] + " text, "
                + COLUMNS_UE[15] + " integer default 0,"
                + COLUMNS_UE[16] + " integer default 0,"
                + COLUMNS_UE[17] + " integer default 0,"
                + COLUMNS_UE[18] + " integer default 0,"
                + COLUMNS_UE[19] + " integer default 0,"
                + COLUMNS_UE[20] + " integer default 0,"
                + COLUMNS_UE[21] + " integer default 0,"
                + " PRIMARY KEY(" + COLUMNS_UE[0] + "," + COLUMNS_UE[14]
                //                + " PRIMARY KEY(" + COLUMNS_UE[0]
                + "));";
        private static final String CREATE_EVENTFILE = "CREATE TABLE "
                + FILE_TABLE + "(" + COLUMNS_FILE[0] + " integer ,"
                + COLUMNS_FILE[1] + " TEXT ,"
                + COLUMNS_FILE[2] + " datetime NOT NULL,"
                + COLUMNS_FILE[3] + " INTEGER NOT NULL,"
                + COLUMNS_FILE[4] + " TEXT , "
                + COLUMNS_FILE[5] + " INTEGER NOT NULL," +
                "PRIMARY KEY(" + COLUMNS_FILE[0] + "," + COLUMNS_FILE[2] + ")," +
                "FOREIGN KEY(" + COLUMNS_FILE[1] + ") " +
                "REFERENCES " + USEREVENT_TABLE + "(" + COLUMNS_UE[0] + "));";

        public DBOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_USEREVENT);
            db.execSQL(CREATE_EVENTFILE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL("drop table if exists " + USEREVENT_TABLE);// 删除旧版表格
            //onCreate(db); // 创建表格
            if (oldVersion == 1) {
                db.execSQL("ALTER TABLE " + USEREVENT_TABLE + " RENAME TO " + USEREVENT_TABLE + "_TEMP");
                db.execSQL(CREATE_USEREVENT);
                db.execSQL("INSERT INTO " + USEREVENT_TABLE + "(" + COLUMNS_UE[0] + ","
                        + COLUMNS_UE[1] + "," + COLUMNS_UE[2] + "," + COLUMNS_UE[3] + ","
                        + COLUMNS_UE[4] + "," + COLUMNS_UE[5] + "," + COLUMNS_UE[6] + ","
                        + COLUMNS_UE[7] + "," + COLUMNS_UE[8] + "," + COLUMNS_UE[9] + ","
                        + COLUMNS_UE[10] + ")" + " SELECT " + COLUMNS_UE[0] + ","
                        + COLUMNS_UE[1] + "," + COLUMNS_UE[2] + "," + COLUMNS_UE[3] + ","
                        + COLUMNS_UE[4] + "," + COLUMNS_UE[5] + "," + COLUMNS_UE[6] + ","
                        + COLUMNS_UE[7] + "," + COLUMNS_UE[8] + "," + COLUMNS_UE[9] + ","
                        + COLUMNS_UE[10] + " FROM " + USEREVENT_TABLE + "_TEMP");
                db.execSQL("DROP TABLE " + USEREVENT_TABLE + "_TEMP");
            } else if (oldVersion == 2) {
                db.execSQL("drop table if exists " + USEREVENT_TABLE);
                db.execSQL(CREATE_USEREVENT);
            } else if (oldVersion == 3) {
                db.execSQL("ALTER TABLE " + USEREVENT_TABLE + " RENAME TO " + USEREVENT_TABLE + "_TEMP");
                db.execSQL(CREATE_USEREVENT);
                db.execSQL("insert into " + USEREVENT_TABLE + "(" + "CreateTime,Longitude,Latitude,Altitud,"
                        + "PlaceName,Context,TraceNo,FileNum,Video,Audio,UserID,Feeling,Behaviour,"
                        + "Duration,Companion,Relationship" + ") " + "select CreateTime,Longitude,Latitude,Altitud,"
                        + "PlaceName,Context,TraceNo,FileNum,Video,Audio,UserID,Feeling,Behaviour,Duration,Companion,"
                        + "Relationship from " + USEREVENT_TABLE + "_TEMP");
                db.execSQL("drop table " + USEREVENT_TABLE + "_TEMP");

            } else if (oldVersion == 4) {
                db.execSQL("ALTER TABLE " + FILE_TABLE + " RENAME TO " + FILE_TABLE + "_TEMP");
                db.execSQL(CREATE_EVENTFILE);
                db.execSQL("insert into " + FILE_TABLE + "(" + "FileNO, FileName, CreateTime, FileType, ThumbnailName" +
                        ")" + "select FileNO, FileName, CreateTime, FileType, ThumbnailName from " + FILE_TABLE + "_TEMP");
                db.execSQL("drop table " + FILE_TABLE + "_TEMP");
            }
        }

    }

    /**
     * @param context
     * @param operation DBREAD只读 DBWRITE读写
     */
    public PhotoDBHelper(Context context, int operation) {
        helper = new DBOpenHelper(context);
        switch (operation) {
            case DBREAD:
                dbRead = helper.getReadableDatabase(); // 获得只读数据库
                break;
            case DBWRITE:
                dbWrite = helper.getWritableDatabase(); // 获得读写的数据库
                break;
        }
    }

    public int insertEvent(InterestMarkerData event) {

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMNS_UE[0], event.getCreateTime());
            values.put(COLUMNS_UE[2], event.getLongitude());
            values.put(COLUMNS_UE[3], event.getLatitude());
            values.put(COLUMNS_UE[4], event.getAltitude());
            values.put(COLUMNS_UE[5], event.getCountry());
            values.put(COLUMNS_UE[6], event.getProvince());
            values.put(COLUMNS_UE[7], event.getCity());
            values.put(COLUMNS_UE[8], event.getPlaceName());
            values.put(COLUMNS_UE[9], event.getCmt());
            values.put(COLUMNS_UE[10], event.getTraceID());
            values.put(COLUMNS_UE[11], event.getImageCount());
            values.put(COLUMNS_UE[12], event.getVideoCount());
            values.put(COLUMNS_UE[13], event.getAudioCount());
            values.put(COLUMNS_UE[14], event.getUserId());
            values.put(COLUMNS_UE[15], event.getMotionType());
            values.put(COLUMNS_UE[16], event.getActivityType());
            values.put(COLUMNS_UE[17], event.getRetentionType());
            values.put(COLUMNS_UE[18], event.getCompanionType());
            values.put(COLUMNS_UE[19], event.getRelationType());
            values.put(COLUMNS_UE[20], event.getStateType());
            values.put(COLUMNS_UE[21], event.getPoiID());
            //            values.put(COLUMNS_UE[21], event.getShare());
            Log.i("dongsiyuanPhotoDB", "insertEvent: " + event.getImageCount() + " " + event.getVideoCount());
            long row = dbWrite.insert(USEREVENT_TABLE, null, values);
            Log.i("dongsiyuanPhotoDB", "PhotoDB, row: " + row);
        } catch (SQLException e) {
            return -1;
        }
        return 0;
    }

    /**
     * 删除事件，同时删除事件文件
     *
     * @param where
     * @return
     */
//    public int deleteEvent(String where) {
//        try {
//            dbWrite.delete(USEREVENT_TABLE, where, null);
//            deleteFiles(where);
//        } catch (SQLException e) {
//            return -1;
//        }
//        return 0;
//    }

    // 从用户表中查询
    public Cursor selectEvent(String[] columns, String selection,
                              String[] selectionArgs, String groupBy, String having,
                              String orderBy) {
        Cursor cursor = dbRead.query(USEREVENT_TABLE, columns, selection,
                selectionArgs, groupBy, having, orderBy);
        return cursor;
    }

    /**
     * 插入到file表
     *
     * @param files
     * @return
     */
    public int inserFile(CommentMediaFilesData files) {
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMNS_FILE[0], files.getFileNo());
            values.put(COLUMNS_FILE[1], files.getFileName());
            values.put(COLUMNS_FILE[2], files.getDateTime());
            values.put(COLUMNS_FILE[3], files.getFileType());
            values.put(COLUMNS_FILE[4], files.getThumbnailName());
            values.put(COLUMNS_FILE[5], files.getFileID());

            long row = dbWrite.insert(FILE_TABLE, null, values);
            Log.i("PhotoDB", "insertFile, row = " + row + " files.getFileID() " + files.getFileID());
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    /**
     * 查询文件
     *
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public Cursor selectFiles(String[] columns, String selection,
                              String[] selectionArgs, String groupBy, String having,
                              String orderBy) {
        Cursor cursor = dbRead.query(FILE_TABLE, columns, selection,
                selectionArgs, groupBy, having, orderBy);
        return cursor;
    }

    /**
     * 删除对应CreateTime的所有文件
     *
     * @param where
     * @return
     */
    public int deleteFiles(String where) {
        try {
            return dbWrite.delete(FILE_TABLE, where, null);
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * 更新file表
     *
     * @param values
     * @param where
     * @return
     */
    public int updateFile(ContentValues values, String where) {
        try {
            return dbWrite.update(FILE_TABLE, values, where, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @param dateTime 要删除的事件的创建时间
     * @param traceID   要删除的兴趣点所属的轨迹
     * @return
     */
    public int deleteEvent(String dateTime, String traceID) {
        try {
            String UEwhereClause = COLUMNS_UE[0] + "=? and " + COLUMNS_UE[10] + "=?";
            String[] UEwhereArgs = {dateTime, traceID};
            dbWrite.delete(USEREVENT_TABLE, UEwhereClause, UEwhereArgs);

            String FileWhere = COLUMNS_FILE[2] + "=?";
            String[] FileArgs = {dateTime};

            dbWrite.delete(FILE_TABLE, FileWhere, FileArgs);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    /**
     * 删除一条轨迹的所有兴趣点
     * @param traceID
     * @return
     */
    public int deleteEvent(String traceID) {
        try {
            String UEwhereClause = COLUMNS_UE[10] + "=?";
            String[] UEwhereArgs = {traceID};
            dbWrite.delete(USEREVENT_TABLE, UEwhereClause, UEwhereArgs);

            String FileWhere = COLUMNS_FILE[2] + "=?";
//            String[] FileArgs = {dateTime};

            dbWrite.delete(FILE_TABLE, FileWhere, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    public int deleteEvent(String startTime, String endTime, String userId) {
        try {
            String UEwhereClause = COLUMNS_UE[0] + " between ? and ? and " + COLUMNS_UE[10] + "=?";
            String[] UEwhereArgs = {startTime, endTime, userId};
            dbWrite.delete(USEREVENT_TABLE, UEwhereClause, UEwhereArgs);

            String FileWhere = COLUMNS_FILE[2] + " between ? and ? ";
            String[] FileArgs = {startTime, endTime};

            dbWrite.delete(FILE_TABLE, FileWhere, FileArgs);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    /*
     * 关闭数据库
     */
    public void closeDB() {
        if (dbWrite != null) {
            dbWrite.close();
        }
        if (dbRead != null) {
            dbRead.close();
        }
    }
}
