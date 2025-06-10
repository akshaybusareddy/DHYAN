package com.example.dhyanapp

import android.app.AppOpsManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root layout with scroll
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(50, 100, 50, 100)
        }
        scrollView.addView(layout)
        setContentView(scrollView)

        // Triangle View (your custom visual)
        val triangleView = TriangleView(this).apply {
            layoutParams = LinearLayout.LayoutParams(300, 300)
        }

        // Status Text
        statusText = TextView(this).apply {
            text = "Scan your NFC card to continue"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 0)
        }

        // Open Accessibility Settings Button
        val accessibilityButton = Button(this).apply {
            text = "Enable Accessibility for App Lock"
            setOnClickListener {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
                Toast.makeText(
                    this@MainActivity,
                    "Enable 'DhyanApp' in Accessibility settings to lock apps.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        layout.addView(triangleView)
        layout.addView(statusText)
        layout.addView(accessibilityButton)

        // Initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not supported", Toast.LENGTH_LONG).show()
            finish()
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        if (!hasUsageStatsPermission()) {
            Toast.makeText(this, "Grant Usage Access Permission", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Grant Draw Over Other Apps Permission", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            "android:get_usage_stats",
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            onNfcScanned()
        }
    }

    private fun onNfcScanned() {
        statusText.text = "NFC scanned. Opening App Locker..."

        // Start app monitoring service
        val serviceIntent = Intent(this, AppMonitorService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)

        // Open app list screen
        val intent = Intent(this, AppListActivity::class.java)
        startActivity(intent)
    }
}
