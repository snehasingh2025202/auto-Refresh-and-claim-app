package com.example.autorefreshclaim

import androidx.compose.runtime.mutableStateListOf

object LogRepository {
    val messages = mutableStateListOf<String>()

    fun add(message: String) {
        val entry = "${System.currentTimeMillis()}: $message"
        messages.add(0, entry)
        if (messages.size > 20) {
            messages.removeLast()
        }
    }

    fun clear() {
        messages.clear()
    }
}
