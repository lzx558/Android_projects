package com.example.android_test1.midterm;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.android_test1.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * 期中考试 · NotePad 笔记列表页
 *
 * <p>在官方示例 NotesList 的基础上完成基本要求与附加功能：
 * <ul>
 *   <li>基本要求 1：条目显示时间戳（{@link NotesCursorAdapter} 中绑定）</li>
 *   <li>基本要求 2：按标题或内容搜索（顶部搜索框实时过滤）</li>
 *   <li>附加功能：分类筛选、置顶、长按菜单（打开/置顶/复制/删除）、排序跟随偏好设置</li>
 * </ul>
 *
 * <p>注意：官方示例用已废弃的 ListActivity + managedQuery()，这两个 API 在 Android 11
 * 之后已被移除，这里改用 AppCompatActivity + ContentResolver.query() 实现同样的逻辑。
 */
public class NotesList extends MidtermActivity {

    private EditText etSearch;
    private Spinner spFilterCategory;
    private ListView lvNotes;
    private TextView tvEmpty;
    private NotesCursorAdapter adapter;

    /** 当前分类筛选条件，空字符串表示"全部分类" */
    private String currentCategory = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.midterm_notes_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.midterm_ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        etSearch = findViewById(R.id.et_search);
        spFilterCategory = findViewById(R.id.sp_filter_category);
        lvNotes = findViewById(R.id.lv_notes);
        tvEmpty = findViewById(R.id.tv_empty);

        // ---------- 搜索：标题或内容实时过滤 ----------
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refresh();
            }
        });

        // ---------- 分类筛选 ----------
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.midterm_filter_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterCategory.setAdapter(categoryAdapter);
        spFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 第 0 项是"全部分类"
                currentCategory = position == 0
                        ? "" : parent.getItemAtPosition(position).toString();
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ---------- 点击进入编辑页 ----------
        lvNotes.setOnItemClickListener((parent, view, position, id) ->
                startActivity(new Intent(this, NoteEditor.class).setData(
                        ContentUris.withAppendedId(NotePad.Notes.CONTENT_URI, id))));

        // ---------- 长按上下文菜单 ----------
        registerForContextMenu(lvNotes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从编辑页返回后重新查询，保证时间戳、分类、置顶状态都是最新的
        refresh();
    }

    /**
     * 按当前搜索关键字 + 分类条件查询，并刷新列表。
     */
    private void refresh() {
        if (etSearch == null) {
            return;
        }

        String keyword = etSearch.getText().toString().trim();
        StringBuilder selection = new StringBuilder();
        List<String> args = new ArrayList<>();

        // 基本要求：根据标题或内容查询
        if (!keyword.isEmpty()) {
            selection.append("(")
                    .append(NotePad.Notes.COLUMN_NAME_TITLE).append(" LIKE ? OR ")
                    .append(NotePad.Notes.COLUMN_NAME_NOTE).append(" LIKE ?)");
            String like = "%" + keyword + "%";
            args.add(like);
            args.add(like);
        }

        // 附加功能：按分类筛选
        if (!currentCategory.isEmpty()) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(NotePad.Notes.COLUMN_NAME_CATEGORY).append(" = ?");
            args.add(currentCategory);
        }

        Cursor cursor = getContentResolver().query(
                NotePad.Notes.CONTENT_URI,
                NotesCursorAdapter.PROJECTION,
                selection.length() > 0 ? selection.toString() : null,
                args.isEmpty() ? null : args.toArray(new String[0]),
                // 附加功能：排序方式来自偏好设置（置顶永远优先）
                Prefs.getSortOrder(this));

        if (adapter == null) {
            adapter = new NotesCursorAdapter(this, cursor);
            lvNotes.setAdapter(adapter);
        } else {
            adapter.changeCursor(cursor);
        }

        // 空状态提示
        boolean empty = cursor == null || cursor.getCount() == 0;
        if (empty) {
            tvEmpty.setVisibility(View.VISIBLE);
            boolean filtering = !keyword.isEmpty() || !currentCategory.isEmpty();
            tvEmpty.setText(filtering ? R.string.midterm_empty_search : R.string.midterm_empty_list);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    // ==================================================
    // 选项菜单
    // ==================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.midterm_list_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            // 新建笔记：打开空白的编辑页
            startActivity(new Intent(this, NoteEditor.class));
            return true;
        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(this, NoteSettings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ==================================================
    // 长按上下文菜单：打开 / 置顶 / 复制 / 删除
    // ==================================================

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (!(menuInfo instanceof AdapterView.AdapterContextMenuInfo)) {
            return;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Cursor cursor = (Cursor) lvNotes.getItemAtPosition(info.position);
        if (cursor == null) {
            return;
        }

        getMenuInflater().inflate(R.menu.midterm_list_context, menu);

        String title = cursor.getString(NotesCursorAdapter.COLUMN_TITLE);
        menu.setHeaderTitle(TextUtils.isEmpty(title) ? "（无标题）" : title);

        // 置顶项的文字随当前状态变化
        boolean pinned = cursor.getInt(NotesCursorAdapter.COLUMN_PINNED) == 1;
        menu.findItem(R.id.context_pin)
                .setTitle(pinned ? R.string.midterm_menu_unpin : R.string.midterm_menu_pin);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!(item.getMenuInfo() instanceof AdapterView.AdapterContextMenuInfo)) {
            return super.onContextItemSelected(item);
        }
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Cursor cursor = (Cursor) lvNotes.getItemAtPosition(info.position);
        if (cursor == null) {
            return super.onContextItemSelected(item);
        }

        long noteId = cursor.getLong(NotesCursorAdapter.COLUMN_ID);
        Uri noteUri = ContentUris.withAppendedId(NotePad.Notes.CONTENT_URI, noteId);
        int id = item.getItemId();

        if (id == R.id.context_open) {
            startActivity(new Intent(this, NoteEditor.class).setData(noteUri));
            return true;
        } else if (id == R.id.context_pin) {
            // 附加功能：置顶 / 取消置顶
            boolean pinned = cursor.getInt(NotesCursorAdapter.COLUMN_PINNED) == 1;
            ContentValues values = new ContentValues();
            values.put(NotePad.Notes.COLUMN_NAME_PINNED, pinned ? 0 : 1);
            getContentResolver().update(noteUri, values, null, null);
            Toast.makeText(this,
                    pinned ? R.string.midterm_toast_unpinned : R.string.midterm_toast_pinned,
                    Toast.LENGTH_SHORT).show();
            refresh();
            return true;
        } else if (id == R.id.context_copy) {
            copyToClipboard(cursor);
            return true;
        } else if (id == R.id.context_delete) {
            confirmDelete(noteUri, cursor.getString(NotesCursorAdapter.COLUMN_TITLE));
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 复制笔记内容到剪贴板。
     */
    private void copyToClipboard(Cursor cursor) {
        String title = cursor.getString(NotesCursorAdapter.COLUMN_TITLE);
        String note = cursor.getString(NotesCursorAdapter.COLUMN_NOTE);
        String text = (TextUtils.isEmpty(title) ? "" : title + "\n") + (note == null ? "" : note);
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("note", text));
            Toast.makeText(this, R.string.midterm_toast_copy_done, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除前二次确认。
     */
    private void confirmDelete(Uri noteUri, String title) {
        String shownTitle = TextUtils.isEmpty(title) ? "（无标题）" : title;
        new AlertDialog.Builder(this)
                .setTitle(R.string.midterm_dialog_delete_title)
                .setMessage(getString(R.string.midterm_dialog_delete_msg, shownTitle))
                .setNegativeButton(R.string.midterm_dialog_cancel, null)
                .setPositiveButton(R.string.midterm_dialog_ok, (dialog, which) -> {
                    getContentResolver().delete(noteUri, null, null);
                    Toast.makeText(this, R.string.midterm_toast_deleted, Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .show();
    }
}
