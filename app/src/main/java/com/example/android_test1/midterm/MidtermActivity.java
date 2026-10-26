package com.example.android_test1.midterm;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * 期中考试所有页面的基类，只负责一件事：让"深色模式"设置正确生效。
 *
 * <p>为什么需要基类：
 * <ul>
 *   <li>{@code attachBaseContext} 里设置 localNightMode —— 这是唯一能在 Activity
 *       真正创建之前生效的时机。如果在 onCreate 里设，对已经 attach 完的页面太晚，
 *       会出现"从列表进入编辑页，编辑页还是浅色"的情况。</li>
 *   <li>{@code onResume} 里再校验一次 —— 用户可能在设置页改了开关，需要同步到
 *       已经存在于返回栈里的页面（例如列表页）。</li>
 * </ul>
 *
 * <p>用 localNightMode 而不是全局的 setDefaultNightMode，是为了让深色模式只影响
 * 期中考试的界面，不会把首页的 Compose 菜单和实验二、实验三一起变黑。
 */
public abstract class MidtermActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // 必须在 super 之前，Activity 创建时就带上正确的夜间模式
        getDelegate().setLocalNightMode(Prefs.isDarkMode(newBase)
                ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 配置与偏好不一致时（用户刚在设置页改过），这里会触发一次重建
        Prefs.applyNightMode(this);
    }
}
