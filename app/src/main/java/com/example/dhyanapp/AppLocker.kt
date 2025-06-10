package com.example.dhyanapp

object AppLocker {
    val lockedApps = mutableSetOf<String>()

    fun isLocked(packageName: String): Boolean {
        return lockedApps.contains(packageName)
    }
}
