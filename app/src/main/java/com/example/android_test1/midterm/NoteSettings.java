package com.example.android_test1.midterm;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.android_test1.R;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * 期中考试 · 偏好设置页（附加功能：偏好设置）
 *
 * <p>四项设置都写入 SharedPreferences（见 {@link Prefs}）：
 * 排序方式影响列表页的 ORDER BY，正文字号影响编辑页，
 * 深色模式只作用于记事本页面的界面（用 AppCompatDelegate.setLocalNightMode），
 * 默认分类决定新建笔记时预选的分类。
 */
public class NoteSettings extends MidtermActivity {

    private Spinner spSort;
    private SeekBar sbFont;
    private TextView tvFontValue;
    private SwitchCompat swDark;
    private Spinner spDefaultCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.midterm_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.midterm_ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        spSort = findViewById(R.id.sp_sort);
        sbFont = findViewById(R.id.sb_font);
        tvFontValue = findViewById(R.id.tv_font_value);
        swDark = findViewById(R.id.sw_dark);
        spDefaultCategory = findViewById(R.id.sp_default_category);
        Button btnReset = findViewById(R.id.btn_reset);

        setupSort();
        setupFontSize();
        setupDarkMode();
        setupDefaultCategory();

        btnReset.setOnClickListener(v -> {
            Prefs.reset(this);
            Toast.makeText(this, R.string.midterm_settings_reset, Toast.LENGTH_SHORT).show();
            // 重新绑定界面到默认值
            recreate();
        });
    }

    /** 排序方式 */
    private void setupSort() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.midterm_sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(adapter);
        spSort.setSelection(Prefs.getSort(this));

        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Prefs.setSort(NoteSettings.this, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** 正文字号 */
    private void setupFontSize() {
        sbFont.setMax(Prefs.FONT_SIZE_MAX - Prefs.FONT_SIZE_MIN);
        sbFont.setProgress(Prefs.getFontSize(this) - Prefs.FONT_SIZE_MIN);
        showFontValue(Prefs.getFontSize(this));

        sbFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = Prefs.FONT_SIZE_MIN + progress;
                showFontValue(size);
                if (fromUser) {
                    Prefs.setFontSize(NoteSettings.this, size);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void showFontValue(int size) {
        tvFontValue.setText(getString(R.string.midterm_settings_font_value, size));
    }

    /** 深色模式 */
    private void setupDarkMode() {
        swDark.setChecked(Prefs.isDarkMode(this));
        swDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Prefs.setDarkMode(this, isChecked);
            // 立即生效：只改本 Activity 的夜间模式，不影响其他实验界面
            getDelegate().setLocalNightMode(isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    /** 新建笔记的默认分类 */
    private void setupDefaultCategory() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.midterm_edit_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDefaultCategory.setAdapter(adapter);

        String current = Prefs.getDefaultCategory(this);
        for (int i = 0; i < spDefaultCategory.getCount(); i++) {
            if (current.equals(spDefaultCategory.getItemAtPosition(i))) {
                spDefaultCategory.setSelection(i);
                break;
            }
        }

        spDefaultCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Prefs.setDefaultCategory(NoteSettings.this,
                        parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
