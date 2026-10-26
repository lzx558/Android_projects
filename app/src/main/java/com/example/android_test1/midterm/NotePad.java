package com.example.android_test1.midterm;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 期中考试 · NotePad 记事本应用 —— 数据契约
 *
 * <p>沿用 Android 官方 NotePad 示例的契约（Contract）设计：把 ContentProvider 的
 * authority、URI、MIME 类型和列名集中定义在一个不可实例化的类里，客户端只依赖这里的常量。
 *
 * <p>相对原版新增了两列：
 * <ul>
 *   <li>{@link Notes#COLUMN_NAME_CATEGORY} —— 笔记分类（附加功能：笔记分类）</li>
 *   <li>{@link Notes#COLUMN_NAME_PINNED} —— 是否置顶（附加功能：置顶收藏）</li>
 * </ul>
 */
public final class NotePad {

    /**
     * ContentProvider 的授权标识。必须全局唯一，否则会与其他应用/示例冲突。
     */
    public static final String AUTHORITY = "com.example.android_test1.notepad";

    // 工具类，不允许实例化
    private NotePad() {
    }

    /**
     * notes 表的契约。
     */
    public static final class Notes implements BaseColumns {

        // 不允许实例化
        private Notes() {
        }

        /**
         * 表名
         */
        public static final String TABLE_NAME = "notes";

        /*
         * ---------------- URI 定义 ----------------
         */

        private static final String SCHEME = "content://";

        /** 全部笔记：/notes */
        private static final String PATH_NOTES = "/notes";

        /** 单条笔记：/notes/ */
        private static final String PATH_NOTE_ID = "/notes/";

        /** 单条笔记 URI 中 ID 段的位置（0 开始） */
        public static final int NOTE_ID_PATH_POSITION = 1;

        /** 全部笔记的 URI */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_NOTES);

        /** 单条笔记 URI 的前缀，调用方需在末尾拼接笔记 ID */
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_NOTE_ID);

        /** 单条笔记 URI 的匹配模式，用于 UriMatcher 或构造 Intent */
        public static final Uri CONTENT_ID_URI_PATTERN =
                Uri.parse(SCHEME + AUTHORITY + PATH_NOTE_ID + "/#");

        /*
         * ---------------- MIME 类型 ----------------
         */

        /** 笔记目录的 MIME 类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android_test1.note";

        /** 单条笔记的 MIME 类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android_test1.note";

        /*
         * ---------------- 列定义 ----------------
         */

        /** 笔记标题，TEXT */
        public static final String COLUMN_NAME_TITLE = "title";

        /** 笔记正文，TEXT */
        public static final String COLUMN_NAME_NOTE = "note";

        /** 创建时间，INTEGER（System.currentTimeMillis()） */
        public static final String COLUMN_NAME_CREATE_DATE = "created";

        /** 最后修改时间，INTEGER（System.currentTimeMillis()） */
        public static final String COLUMN_NAME_MODIFICATION_DATE = "modified";

        /** 分类，TEXT（期中考试新增） */
        public static final String COLUMN_NAME_CATEGORY = "category";

        /** 是否置顶，INTEGER：1 置顶 / 0 普通（期中考试新增） */
        public static final String COLUMN_NAME_PINNED = "pinned";

        /**
         * 默认排序：置顶优先，然后按修改时间倒序。
         * 期中考试的排序偏好会在查询时覆盖这个默认值。
         * <p>注意：必须放在列常量之后，Java 不允许 static final 常量前向引用。
         */
        public static final String DEFAULT_SORT_ORDER =
                COLUMN_NAME_PINNED + " DESC, " + COLUMN_NAME_MODIFICATION_DATE + " DESC";
    }
}
