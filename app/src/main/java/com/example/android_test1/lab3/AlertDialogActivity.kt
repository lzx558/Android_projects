package com.example.android_test1.lab3

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.android_test1.R

/**
 * 实验三 · 02 自定义 AlertDialog
 * 用 dialog_custom 布局创建登录对话框：橙色标题栏、用户名/密码，
 * Cancel 关闭、Sign in 提示用户名，密码框回车等同登录。
 */
class AlertDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lab3_alert_dialog)
        title = "AlertDialog"

        findViewById<Button>(R.id.btn_show_dialog).setOnClickListener {
            showCustomDialog()
        }
    }

    private fun showCustomDialog() {
        val view = layoutInflater.inflate(R.layout.lab3_dialog_custom, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        val etUsername = view.findViewById<EditText>(R.id.et_username)
        val etPassword = view.findViewById<EditText>(R.id.et_password)

        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.btn_signin).setOnClickListener {
            onSignIn(etUsername, dialog)
        }

        // 密码框按回车触发登录
        etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSignIn(etUsername, dialog)
                true
            } else false
        }

        dialog.show()
    }

    private fun onSignIn(etUsername: EditText, dialog: AlertDialog) {
        val user = etUsername.text.toString()
        Toast.makeText(this, "用户名: $user", Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }
}
