package com.example.dhyanapp

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppLockAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.packageName == null) return

        val currentPackage = event.packageName.toString()

        if (AppLocker.isLocked(currentPackage)) {
            val intent = Intent(this, LockScreenActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("packageName", currentPackage)
            }
            startActivity(intent)
        }
    }

    override fun onInterrupt() {}
}
