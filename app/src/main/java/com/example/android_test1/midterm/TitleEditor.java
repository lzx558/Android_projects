package com.example.android_test1.midterm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.android_test1.R;

/**
 * 期中考试 · 修改笔记标题（沿用官方示例 TitleEditor 的对话框式 Activity）
 *
 * <p>作为 NoteEditor 的"修改标题"动作独立存在，改完直接写回 provider，
 * 并把新标题通过 setResult 带回给 NoteEditor，避免重新加载整条笔记。
 */
public class TitleEditor extends MidtermActivity {

    public static final String EXTRA_TITLE = "title";

    private EditText etTitle;
    private Uri noteUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.midterm_title_editor);

        etTitle = findViewById(R.id.et_title_edit);
        Button btnCancel = findViewById(R.id.btn_title_cancel);
        Button btnOk = findViewById(R.id.btn_title_ok);

        noteUri = getIntent().getData();
        if (noteUri == null) {
            finish();
            return;
        }

        // 把当前标题填进输入框
        Cursor cursor = getContentResolver().query(noteUri,
                new String[]{NotePad.Notes.COLUMN_NAME_TITLE}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                etTitle.setText(cursor.getString(0));
                etTitle.setSelection(etTitle.getText().length());
            }
            cursor.close();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                ContentValues values = new ContentValues();
                values.put(NotePad.Notes.COLUMN_NAME_TITLE, title);
                values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, System.currentTimeMillis());
                getContentResolver().update(noteUri, values, null, null);

                setResult(Activity.RESULT_OK, new Intent().putExtra(EXTRA_TITLE, title));
                finish();
            }
        });
    }
}
