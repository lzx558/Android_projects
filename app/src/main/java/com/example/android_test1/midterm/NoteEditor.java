package com.example.android_test1.midterm;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.example.android_test1.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 期中考试 · NotePad 笔记编辑页
 *
 * <p>在官方示例 NoteEditor 的基础上：
 * <ul>
 *   <li>新增分类下拉框（附加功能：笔记分类）</li>
 *   <li>新增导出为 txt 并分享（附加功能：导出笔记）</li>
 *   <li>新增置顶开关（附加功能：置顶收藏）</li>
 *   <li>正文字号跟随偏好设置</li>
 *   <li>未保存返回时二次确认</li>
 * </ul>
 */
public class NoteEditor extends MidtermActivity {

    private static final String[] PROJECTION = {
            NotePad.Notes._ID,
            NotePad.Notes.COLUMN_NAME_TITLE,
            NotePad.Notes.COLUMN_NAME_NOTE,
            NotePad.Notes.COLUMN_NAME_CREATE_DATE,
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,
            NotePad.Notes.COLUMN_NAME_CATEGORY,
            NotePad.Notes.COLUMN_NAME_PINNED,
    };

    private static final int COL_TITLE = 1;
    private static final int COL_NOTE = 2;
    private static final int COL_CREATED = 3;
    private static final int COL_MODIFIED = 4;
    private static final int COL_CATEGORY = 5;
    private static final int COL_PINNED = 6;

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    private EditText etTitle;
    private EditText etNote;
    private Spinner spCategory;
    private TextView tvEditorTime;

    /** 当前编辑的笔记 URI；为 null 表示新建 */
    private Uri noteUri;
    private boolean isNew;
    private boolean pinned;
    private boolean dirty;

    /** 改标题后只回来更新输入框，避免重新加载把未保存的正文冲掉 */
    private final ActivityResultLauncher<Intent> titleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String newTitle = result.getData().getStringExtra(TitleEditor.EXTRA_TITLE);
                    if (newTitle != null) {
                        etTitle.setText(newTitle);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.midterm_note_editor);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.midterm_ic_back);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        etTitle = findViewById(R.id.et_title);
        etNote = findViewById(R.id.et_note);
        spCategory = findViewById(R.id.sp_category);
        tvEditorTime = findViewById(R.id.tv_editor_time);

        // 分类下拉（工作 / 学习 / 生活 / 灵感 / 其他）
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.midterm_edit_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // 正文字号跟随偏好设置
        etNote.setTextSize(Prefs.getFontSize(this));

        // 进入时给一个默认标题
        toolbar.setTitle(R.string.midterm_title_edit);

        noteUri = getIntent().getData();
        if (noteUri == null) {
            // ---------- 新建笔记 ----------
            isNew = true;
            toolbar.setTitle(R.string.midterm_title_create);
            tvEditorTime.setVisibility(android.view.View.GONE);
            selectCategory(Prefs.getDefaultCategory(this));
        } else {
            // ---------- 编辑已有笔记 ----------
            isNew = false;
            loadNote(noteUri);
        }

        // 记录是否有未保存的修改
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                dirty = true;
            }
        };
        etTitle.addTextChangedListener(watcher);
        etNote.addTextChangedListener(watcher);

        // 返回前检查未保存内容
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (dirty) {
                    confirmDiscard();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    /**
     * 从 provider 读取单条笔记并填到界面上。
     */
    private void loadNote(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, PROJECTION, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(this, "笔记加载失败", Toast.LENGTH_SHORT).show();
            finish();
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        etTitle.setText(cursor.getString(COL_TITLE));
        etNote.setText(cursor.getString(COL_NOTE));
        selectCategory(cursor.getString(COL_CATEGORY));
        pinned = cursor.getInt(COL_PINNED) == 1;

        long created = cursor.getLong(COL_CREATED);
        long modified = cursor.getLong(COL_MODIFIED);
        long shown = created > 0 ? created : modified;
        if (shown > 0) {
            tvEditorTime.setVisibility(android.view.View.VISIBLE);
            tvEditorTime.setText(getString(R.string.midterm_label_time, TIME_FORMAT.format(new Date(shown))));
        } else {
            tvEditorTime.setVisibility(android.view.View.GONE);
        }

        cursor.close();

        // 加载不算修改
        dirty = false;
        invalidateOptionsMenu();
    }

    /**
     * 在分类下拉中选中指定分类，找不到就选最后一项。
     */
    private void selectCategory(String category) {
        if (TextUtils.isEmpty(category)) {
            return;
        }
        for (int i = 0; i < spCategory.getCount(); i++) {
            if (category.equals(spCategory.getItemAtPosition(i))) {
                spCategory.setSelection(i);
                return;
            }
        }
        spCategory.setSelection(spCategory.getCount() - 1);
    }

    // ==================================================
    // 菜单
    // ==================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.midterm_editor_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem pinItem = menu.findItem(R.id.menu_pin);
        if (pinItem != null) {
            pinItem.setTitle(pinned ? R.string.midterm_menu_unpin : R.string.midterm_menu_pin);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_save) {
            saveNote();
            return true;
        } else if (id == R.id.menu_export) {
            exportNote();
            return true;
        } else if (id == R.id.menu_pin) {
            togglePin();
            return true;
        } else if (id == R.id.menu_rename) {
            openTitleEditor();
            return true;
        } else if (id == R.id.menu_revert) {
            revertChanges();
            return true;
        } else if (id == R.id.menu_delete) {
            confirmDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存笔记：新建走 insert，已有走 update。
     * provider 会在大局上补齐 created / modified 时间戳。
     */
    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        String category = spCategory.getSelectedItem() == null
                ? Prefs.getDefaultCategory(this) : spCategory.getSelectedItem().toString();

        if (title.isEmpty() && note.isEmpty()) {
            Toast.makeText(this, R.string.midterm_toast_nothing, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(NotePad.Notes.COLUMN_NAME_TITLE, title);
        values.put(NotePad.Notes.COLUMN_NAME_NOTE, note);
        values.put(NotePad.Notes.COLUMN_NAME_CATEGORY, category);
        values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, System.currentTimeMillis());

        if (isNew) {
            Uri created = getContentResolver().insert(NotePad.Notes.CONTENT_URI, values);
            if (created != null) {
                noteUri = created;
                isNew = false;
            }
        } else {
            getContentResolver().update(noteUri, values, null, null);
        }

        dirty = false;
        Toast.makeText(this, R.string.midterm_toast_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 置顶 / 取消置顶（附加功能）。
     */
    private void togglePin() {
        if (isNew || noteUri == null) {
            Toast.makeText(this, "请先保存笔记", Toast.LENGTH_SHORT).show();
            return;
        }
        pinned = !pinned;
        ContentValues values = new ContentValues();
        values.put(NotePad.Notes.COLUMN_NAME_PINNED, pinned ? 1 : 0);
        getContentResolver().update(noteUri, values, null, null);

        Toast.makeText(this,
                pinned ? R.string.midterm_toast_pinned : R.string.midterm_toast_unpinned,
                Toast.LENGTH_SHORT).show();
        invalidateOptionsMenu();
    }

    /**
     * 打开"修改标题"对话框 Activity。
     */
    private void openTitleEditor() {
        if (isNew || noteUri == null) {
            Toast.makeText(this, "请先保存笔记", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, TitleEditor.class).setData(noteUri);
        titleLauncher.launch(intent);
    }

    /**
     * 还原：新建则清空，已有则重新从数据库加载。
     */
    private void revertChanges() {
        if (isNew) {
            etTitle.setText("");
            etNote.setText("");
            selectCategory(Prefs.getDefaultCategory(this));
            dirty = false;
        } else {
            loadNote(noteUri);
        }
        Toast.makeText(this, R.string.midterm_menu_revert, Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除当前笔记。
     */
    private void confirmDelete() {
        String title = etTitle.getText().toString().trim();
        String shownTitle = TextUtils.isEmpty(title) ? "（无标题）" : title;
        new AlertDialog.Builder(this)
                .setTitle(R.string.midterm_dialog_delete_title)
                .setMessage(getString(R.string.midterm_dialog_delete_msg, shownTitle))
                .setNegativeButton(R.string.midterm_dialog_cancel, null)
                .setPositiveButton(R.string.midterm_dialog_ok, (dialog, which) -> {
                    if (!isNew && noteUri != null) {
                        getContentResolver().delete(noteUri, null, null);
                    }
                    Toast.makeText(this, R.string.midterm_toast_deleted, Toast.LENGTH_SHORT).show();
                    dirty = false;
                    finish();
                })
                .show();
    }

    /**
     * 返回前提示未保存。
     */
    private void confirmDiscard() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.midterm_dialog_discard_title)
                .setMessage(R.string.midterm_dialog_discard_msg)
                .setNegativeButton(R.string.midterm_dialog_discard_cancel, null)
                .setPositiveButton(R.string.midterm_dialog_discard_ok, (dialog, which) -> {
                    dirty = false;
                    finish();
                })
                .show();
    }

    // ==================================================
    // 附加功能：导出笔记
    // ==================================================

    /**
     * 把当前笔记写成 txt 存到应用外部私有目录，再通过 FileProvider 分享出去。
     */
    private void exportNote() {
        String title = etTitle.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        String category = spCategory.getSelectedItem() == null
                ? "" : spCategory.getSelectedItem().toString();

        if (title.isEmpty() && note.isEmpty()) {
            Toast.makeText(this, R.string.midterm_toast_nothing, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(title)) {
            title = "无标题笔记";
        }

        String content = "标题：" + title + "\n"
                + "分类：" + category + "\n"
                + "导出时间：" + TIME_FORMAT.format(new Date()) + "\n"
                + "------------------------------\n"
                + note + "\n";

        try {
            File externalDir = getExternalFilesDir(null);
            if (externalDir == null) {
                throw new IOException("外部存储不可用");
            }
            File dir = new File(externalDir, "exports");
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("无法创建导出目录");
            }
            // 文件名去掉标题里的非法字符
            String safeName = title.replaceAll("[\\\\/:*?\"<>|]", "_");
            File file = new File(dir, safeName + ".txt");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes("UTF-8"));
            fos.close();

            Uri fileUri = FileProvider.getUriForFile(
                    this, getPackageName() + ".midterm.fileprovider", file);

            Intent share = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_STREAM, fileUri)
                    .putExtra(Intent.EXTRA_SUBJECT,
                            getString(R.string.midterm_export_subject, title))
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(share, getString(R.string.midterm_export_chooser)));
            Toast.makeText(this, getString(R.string.midterm_toast_exported, file.getAbsolutePath()),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.midterm_toast_export_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
