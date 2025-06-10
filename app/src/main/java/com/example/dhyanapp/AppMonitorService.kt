package com.example.dhyanapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat

class AppMonitorService : Service() {

    private val handler = Handler()
    private val checkInterval: Long = 1000 // 1 second

    private val monitorRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            handler.postDelayed(this, checkInterval)
        }
    }

    override fun onCreate() {
        super.onCreate()
        handler.post(monitorRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(monitorRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun checkForegroundApp() {
        if (!Settings.canDrawOverlays(this)) {
            return
        }

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val usageStats = usageStatsManager.queryUsageStats(
            android.app.usage.UsageStatsManager.INTERVAL_DAILY,
            currentTime - 1000 * 10,
            currentTime
        )

        val currentApp = usageStats
            .maxByOrNull { it.lastTimeUsed }
            ?.packageName

        if (currentApp != null && AppLocker.lockedApps.contains(currentApp)) {
            // Show lock screen
            val lockIntent = Intent(this, LockScreenActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(lockIntent)

        }
    }
}
