package com.example.autorefreshclaim

import android.content.SharedPreferences

object Preferences {
    const val PREFS_FILE = "auto_refresh_claim_prefs"
    private const val KEY_REFRESH_INTERVAL = "refresh_interval"
    private const val KEY_AUTOMATION_ENABLED = "automation_enabled"
    private const val DEFAULT_INTERVAL_MS = 3000L

    fun loadRefreshInterval(prefs: SharedPreferences): Long {
        return prefs.getLong(KEY_REFRESH_INTERVAL, DEFAULT_INTERVAL_MS)
    }

    fun saveRefreshInterval(prefs: SharedPreferences, intervalMs: Long) {
        prefs.edit().putLong(KEY_REFRESH_INTERVAL, intervalMs).apply()
    }

    fun isAutomationEnabled(prefs: SharedPreferences): Boolean {
        return prefs.getBoolean(KEY_AUTOMATION_ENABLED, false)
    }

    fun saveAutomationEnabled(prefs: SharedPreferences, enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTOMATION_ENABLED, enabled).apply()
    }
}
