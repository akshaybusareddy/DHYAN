package com.example.dhyanapp

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class AppListActivity : AppCompatActivity() {

    private lateinit var appListLayout: LinearLayout
    private val selectedApps = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main container with button at bottom
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // ScrollView with app list
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // fill remaining space
            )
        }

        appListLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
        }
        scrollView.addView(appListLayout)

        // Confirm Button fixed at bottom
        val confirmButton = Button(this).apply {
            text = "Confirm Lock Selection"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
            setOnClickListener {
                AppLocker.lockedApps.clear()
                AppLocker.lockedApps.addAll(selectedApps)
                Toast.makeText(this@AppListActivity, "Apps Locked", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        rootLayout.addView(scrollView)
        rootLayout.addView(confirmButton)

        setContentView(rootLayout)

        loadLauncherApps()
    }

    private fun loadLauncherApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val launchableApps: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)

        launchableApps.sortedBy {
            it.loadLabel(pm).toString().lowercase()
        }.forEach { resolveInfo ->
            val appName = resolveInfo.loadLabel(pm).toString()
            val appIcon: Drawable = resolveInfo.loadIcon(pm)
            val packageName = resolveInfo.activityInfo.packageName

            val horizontalLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val iconView = ImageView(this).apply {
                setImageDrawable(appIcon)
                layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                    rightMargin = 32
                }
            }

            val checkBox = CheckBox(this).apply {
                text = appName
                textSize = 16f
                isChecked = AppLocker.isLocked(packageName)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedApps.add(packageName)
                    } else {
                        selectedApps.remove(packageName)
                    }
                }
            }

            horizontalLayout.addView(iconView)
            horizontalLayout.addView(checkBox)

            appListLayout.addView(horizontalLayout)
        }
    }
}
