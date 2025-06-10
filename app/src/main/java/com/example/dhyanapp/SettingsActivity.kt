package com.example.dhyanapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 64, 32, 32)
        }

        // Button to select apps to lock
        val selectAppsButton = Button(this).apply {
            text = "Select Apps to Lock"
            setOnClickListener {
                startActivity(Intent(this@SettingsActivity, AppListActivity::class.java))
            }
        }

        // Button to unlock all apps
        val unlockAppsButton = Button(this).apply {
            text = "Unlock All Apps"
            setOnClickListener {
                AppLocker.lockedApps.clear()
                Toast.makeText(this@SettingsActivity, "All Apps Unlocked", Toast.LENGTH_SHORT).show()
            }
        }

        layout.addView(selectAppsButton)
        layout.addView(unlockAppsButton)

        setContentView(layout)
    }
}
