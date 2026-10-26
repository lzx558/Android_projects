package com.example.android_test1.midterm;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.example.android_test1.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 期中考试 · 笔记列表的 CursorAdapter
 *
 * <p>相对官方示例（SimpleCursorAdapter 只绑一列标题），这里改为自定义 CursorAdapter，
 * 一个条目绑定四块内容：标题、时间戳、分类标签、置顶标记。
 */
public class NotesCursorAdapter extends CursorAdapter {

    /** 查询用的列，顺序与下面的索引常量一一对应 */
    public static final String[] PROJECTION = {
            NotePad.Notes._ID,                          // 0
            NotePad.Notes.COLUMN_NAME_TITLE,            // 1
            NotePad.Notes.COLUMN_NAME_NOTE,             // 2
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,// 3
            NotePad.Notes.COLUMN_NAME_CREATE_DATE,      // 4
            NotePad.Notes.COLUMN_NAME_CATEGORY,         // 5
            NotePad.Notes.COLUMN_NAME_PINNED,           // 6
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_NOTE = 2;
    public static final int COLUMN_MODIFIED = 3;
    public static final int COLUMN_CREATED = 4;
    public static final int COLUMN_CATEGORY = 5;
    public static final int COLUMN_PINNED = 6;

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    public NotesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.midterm_notes_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = view.findViewById(R.id.tv_item_title);
        TextView tvTime = view.findViewById(R.id.tv_item_time);
        TextView tvCategory = view.findViewById(R.id.tv_item_category);
        ImageView ivPin = view.findViewById(R.id.iv_item_pin);

        // ---------- 标题：为空时退回正文首行 ----------
        String title = cursor.getString(COLUMN_TITLE);
        if (TextUtils.isEmpty(title) || title.trim().isEmpty()) {
            title = firstLineOf(cursor.getString(COLUMN_NOTE));
        }
        if (TextUtils.isEmpty(title) || title.trim().isEmpty()) {
            title = "（无标题）";
        }
        tvTitle.setText(title);

        // ---------- 时间戳：优先显示修改时间，从未修改过则显示创建时间 ----------
        long modified = cursor.getLong(COLUMN_MODIFIED);
        long created = cursor.getLong(COLUMN_CREATED);
        long shown = modified > 0 ? modified : created;
        if (shown > 0) {
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(context.getString(
                    R.string.midterm_label_modified, timeFormat.format(new Date(shown))));
        } else {
            tvTime.setVisibility(View.GONE);
        }

        // ---------- 分类标签 ----------
        String category = cursor.getString(COLUMN_CATEGORY);
        if (TextUtils.isEmpty(category)) {
            tvCategory.setVisibility(View.GONE);
        } else {
            tvCategory.setVisibility(View.VISIBLE);
            tvCategory.setText(category);
        }

        // ---------- 置顶标记 ----------
        ivPin.setVisibility(cursor.getInt(COLUMN_PINNED) == 1 ? View.VISIBLE : View.GONE);
    }

    /**
     * 取正文第一行非空内容，作为没有标题时的显示文本。
     */
    private static String firstLineOf(String note) {
        if (TextUtils.isEmpty(note)) {
            return null;
        }
        for (String line : note.split("\n")) {
            if (!line.trim().isEmpty()) {
                return line.trim();
            }
        }
        return null;
    }
}
