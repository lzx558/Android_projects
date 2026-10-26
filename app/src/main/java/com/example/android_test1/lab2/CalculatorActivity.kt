package com.example.android_test1.lab2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_test1.R
import java.util.Locale

/**
 * 实验二 · 03 约束布局（一）——计算器
 * 4×4 按键通过 ConstraintLayout 的 horizontal/vertical chain 定位。
 */
class CalculatorActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var display: TextView

    private var accumulator: Double? = null
    private var pendingOp: String? = null
    private var typing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lab2_calculator)

        input = findViewById(R.id.et_input)
        display = findViewById(R.id.tv_display)

        val digitIds = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
            R.id.btn_8, R.id.btn_9
        )
        digitIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                appendDigit((it as Button).text.toString())
            }
        }
        findViewById<Button>(R.id.btn_dot).setOnClickListener { appendDigit(".") }

        findViewById<Button>(R.id.btn_add).setOnClickListener { applyOperator("+") }
        findViewById<Button>(R.id.btn_sub).setOnClickListener { applyOperator("-") }
        findViewById<Button>(R.id.btn_mul).setOnClickListener { applyOperator("*") }
        findViewById<Button>(R.id.btn_div).setOnClickListener { applyOperator("/") }
        findViewById<Button>(R.id.btn_eq).setOnClickListener { calculate(showResult = true) }
    }

    private fun currentNumber(): Double {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return 0.0
        return text.toDoubleOrNull() ?: 0.0
    }

    private fun appendDigit(d: String) {
        if (!typing) {
            input.setText("")
            typing = true
        }
        if (d == "." && input.text.contains(".")) return
        input.append(d)
    }

    private fun applyOperator(op: String) {
        if (pendingOp != null && typing) {
            calculate(showResult = false)
        } else {
            accumulator = currentNumber()
        }
        pendingOp = op
        typing = false
        display.text = format(accumulator ?: 0.0) + " " + symbol(op)
    }

    private fun calculate(showResult: Boolean) {
        val op = pendingOp
        if (op == null) {
            accumulator = currentNumber()
        } else {
            val second = if (typing) currentNumber() else (accumulator ?: 0.0)
            val first = accumulator ?: 0.0
            val result = when (op) {
                "+" -> first + second
                "-" -> first - second
                "*" -> first * second
                "/" -> if (second == 0.0) {
                    Toast.makeText(this, "不能除以 0", Toast.LENGTH_SHORT).show()
                    0.0
                } else first / second
                else -> second
            }
            accumulator = result
            if (showResult) display.text = format(result)
        }
        if (showResult) {
            pendingOp = null
            typing = false
            input.setText(format(accumulator ?: 0.0))
        }
    }

    private fun symbol(op: String) = when (op) {
        "*" -> "×"
        "/" -> "÷"
        "-" -> "−"
        else -> op
    }

    private fun format(v: Double): String =
        if (v.isNaN() || v.isInfinite()) "Error"
        else if (v % 1.0 == 0.0) String.format(Locale.US, "%.1f", v)
        else String.format(Locale.US, "%g", v)
}
