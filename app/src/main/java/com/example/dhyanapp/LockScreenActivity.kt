package com.example.dhyanapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LockScreenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageName = intent.getStringExtra("packageName") ?: "Unknown App"

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 200, 50, 200)
        }

        val title = TextView(this).apply {
            text = "This app is locked.\nUnlock by scanning your NFC card."
            textSize = 20f
            gravity = android.view.Gravity.CENTER
        }

        val closeBtn = Button(this).apply {
            text = "Close"
            setOnClickListener {
                finishAffinity() // Closes the locked app
            }
        }

        layout.addView(title)
        layout.addView(closeBtn)
        setContentView(layout)
    }

    override fun onBackPressed() {
        // Disable back
    }
}
