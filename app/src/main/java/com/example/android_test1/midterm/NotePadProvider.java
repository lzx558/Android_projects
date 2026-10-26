package com.example.android_test1.midterm;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * 期中考试 · NotePad 记事本应用 —— ContentProvider 数据层
 *
 * <p>结构与官方 NotePad 示例一致：SQLiteOpenHelper 建库 + UriMatcher 分派 URI + 标准增删改查。
 * 相比原版：
 * <ul>
 *   <li>表结构增加 category、pinned 两列</li>
 *   <li>insert / update 时自动写入 created、modified 时间戳（列表页的时间戳就来自这里）</li>
 *   <li>provider 不对外导出（exported=false），仅供本应用内部使用</li>
 * </ul>
 */
public class NotePadProvider extends ContentProvider {

    private static final String TAG = "NotePadProvider";

    private static final String DATABASE_NAME = "midterm_notepad.db";
    private static final int DATABASE_VERSION = 1;

    private static final int NOTES = 1;
    private static final int NOTE_ID = 2;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DatabaseHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(NotePad.AUTHORITY, "notes", NOTES);
        matcher.addURI(NotePad.AUTHORITY, "notes/#", NOTE_ID);
        return matcher;
    }

    /**
     * 建库建表。
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + NotePad.Notes.TABLE_NAME + " ("
                    + NotePad.Notes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + NotePad.Notes.COLUMN_NAME_TITLE + " TEXT,"
                    + NotePad.Notes.COLUMN_NAME_NOTE + " TEXT,"
                    + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER,"
                    + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER,"
                    + NotePad.Notes.COLUMN_NAME_CATEGORY + " TEXT,"
                    + NotePad.Notes.COLUMN_NAME_PINNED + " INTEGER DEFAULT 0"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 课程作业场景：结构变化时重建表，不做数据迁移
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NotePad.Notes.TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NotePad.Notes.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case NOTES:
                break;
            case NOTE_ID:
                qb.appendWhere(NotePad.Notes._ID + "=" + uri.getPathSegments().get(
                        NotePad.Notes.NOTE_ID_PATH_POSITION));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // 调用方没有指定排序时用默认排序（置顶优先 + 修改时间倒序）
        String orderBy = TextUtils.isEmpty(sortOrder)
                ? NotePad.Notes.DEFAULT_SORT_ORDER : sortOrder;

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // 数据变化时通知监听者（CursorAdapter 依赖这个机制刷新）
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                return NotePad.Notes.CONTENT_TYPE;
            case NOTE_ID:
                return NotePad.Notes.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // 只允许向笔记目录插入
        if (sUriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values = initialValues != null
                ? new ContentValues(initialValues) : new ContentValues();

        // 自动补全时间戳：这是"列表条目显示时间戳"的数据来源
        Long now = System.currentTimeMillis();
        if (!values.containsKey(NotePad.Notes.COLUMN_NAME_CREATE_DATE)) {
            values.put(NotePad.Notes.COLUMN_NAME_CREATE_DATE, now);
        }
        if (!values.containsKey(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE)) {
            values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, now);
        }
        if (!values.containsKey(NotePad.Notes.COLUMN_NAME_TITLE)) {
            values.put(NotePad.Notes.COLUMN_NAME_TITLE, "");
        }
        if (!values.containsKey(NotePad.Notes.COLUMN_NAME_CATEGORY)) {
            values.put(NotePad.Notes.COLUMN_NAME_CATEGORY, "");
        }
        if (!values.containsKey(NotePad.Notes.COLUMN_NAME_PINNED)) {
            values.put(NotePad.Notes.COLUMN_NAME_PINNED, 0);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(NotePad.Notes.TABLE_NAME, NotePad.Notes.COLUMN_NAME_NOTE, values);

        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(NotePad.Notes.CONTENT_ID_URI_BASE, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;

        switch (sUriMatcher.match(uri)) {
            case NOTES:
                count = db.update(NotePad.Notes.TABLE_NAME, values, where, whereArgs);
                break;
            case NOTE_ID:
                String noteId = uri.getPathSegments().get(NotePad.Notes.NOTE_ID_PATH_POSITION);
                count = db.update(NotePad.Notes.TABLE_NAME, values,
                        NotePad.Notes._ID + "=" + noteId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""),
                        whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;

        switch (sUriMatcher.match(uri)) {
            case NOTES:
                count = db.delete(NotePad.Notes.TABLE_NAME, where, whereArgs);
                break;
            case NOTE_ID:
                String noteId = uri.getPathSegments().get(NotePad.Notes.NOTE_ID_PATH_POSITION);
                count = db.delete(NotePad.Notes.TABLE_NAME,
                        NotePad.Notes._ID + "=" + noteId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""),
                        whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
