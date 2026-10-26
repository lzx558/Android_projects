package com.example.android_test1.midterm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * 期中考试 · 偏好设置的读写封装（附加功能：偏好设置）
 *
 * <p>用 SharedPreferences 保存四项设置：
 * <ul>
 *   <li>列表排序方式（修改时间 / 创建时间 / 标题）</li>
 *   <li>编辑页正文字号</li>
 *   <li>深色模式开关</li>
 *   <li>新建笔记的默认分类</li>
 * </ul>
 */
public final class Prefs {

    private static final String PREF_NAME = "midterm_notepad_settings";

    private static final String KEY_SORT = "sort";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_DEFAULT_CATEGORY = "default_category";

    /** 排序：修改时间倒序 */
    public static final int SORT_MODIFIED = 0;
    /** 排序：创建时间倒序 */
    public static final int SORT_CREATED = 1;
    /** 排序：标题升序 */
    public static final int SORT_TITLE = 2;

    /** 正文字号范围与默认值 */
    public static final int FONT_SIZE_MIN = 12;
    public static final int FONT_SIZE_MAX = 24;
    public static final int FONT_SIZE_DEFAULT = 16;

    private Prefs() {
    }

    private static SharedPreferences sp(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static int getSort(Context context) {
        return sp(context).getInt(KEY_SORT, SORT_MODIFIED);
    }

    public static void setSort(Context context, int sort) {
        sp(context).edit().putInt(KEY_SORT, sort).apply();
    }

    /**
     * 把排序偏好翻译成 SQL ORDER BY 子句。
     * 无论选哪种排序，置顶的笔记永远排在最前面。
     */
    public static String getSortOrder(Context context) {
        switch (getSort(context)) {
            case SORT_CREATED:
                return NotePad.Notes.COLUMN_NAME_PINNED + " DESC, "
                        + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " DESC";
            case SORT_TITLE:
                return NotePad.Notes.COLUMN_NAME_PINNED + " DESC, "
                        + NotePad.Notes.COLUMN_NAME_TITLE + " COLLATE LOCALIZED ASC";
            case SORT_MODIFIED:
            default:
                return NotePad.Notes.COLUMN_NAME_PINNED + " DESC, "
                        + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " DESC";
        }
    }

    public static int getFontSize(Context context) {
        int size = sp(context).getInt(KEY_FONT_SIZE, FONT_SIZE_DEFAULT);
        if (size < FONT_SIZE_MIN) {
            size = FONT_SIZE_MIN;
        } else if (size > FONT_SIZE_MAX) {
            size = FONT_SIZE_MAX;
        }
        return size;
    }

    public static void setFontSize(Context context, int size) {
        sp(context).edit().putInt(KEY_FONT_SIZE, size).apply();
    }

    public static boolean isDarkMode(Context context) {
        return sp(context).getBoolean(KEY_DARK_MODE, false);
    }

    public static void setDarkMode(Context context, boolean dark) {
        sp(context).edit().putBoolean(KEY_DARK_MODE, dark).apply();
    }

    public static String getDefaultCategory(Context context) {
        return sp(context).getString(KEY_DEFAULT_CATEGORY, "其他");
    }

    public static void setDefaultCategory(Context context, String category) {
        sp(context).edit().putString(KEY_DEFAULT_CATEGORY, category).apply();
    }

    /**
     * 恢复默认设置。
     */
    public static void reset(Context context) {
        sp(context).edit().clear().apply();
    }

    /**
     * 把"深色模式"偏好同步到当前 Activity 的夜间模式。
     *
     * <p>需要在两个时机调用：
     * <ul>
     *   <li>onCreate 的 super 之前 —— 决定这个页面一打开就是深色还是浅色</li>
     *   <li>onResume 中 —— 用户可能在设置页改了开关，返回时已经存在的页面要跟着变</li>
     * </ul>
     * 只有当前配置与偏好不一致时才调用 setLocalNightMode，避免无谓的重建。
     *
     * <p>用 setLocalNightMode（而不是全局的 setDefaultNightMode）是为了让深色模式
     * 只作用于记事本页面的界面，不把实验二、实验三和首页菜单也变黑。
     */
    public static void applyNightMode(AppCompatActivity activity) {
        boolean preferDark = isDarkMode(activity);
        int currentMode = activity.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        boolean currentDark = currentMode == Configuration.UI_MODE_NIGHT_YES;
        if (preferDark != currentDark) {
            activity.getDelegate().setLocalNightMode(preferDark
                    ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
