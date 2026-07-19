package com.example.autorefreshclaim

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ClaimAccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private val logTag = "ClaimAccessibility"
    private lateinit var prefs: SharedPreferences
    private var lastActionTime = 0L
    private var isProcessing = false

    private val claimKeywords = listOf("claim", "redeem", "collect")
    private val confirmKeywords = listOf("confirm", "ok", "yes", "submit")

    private val scanRunnable = object : Runnable {
        override fun run() {
            if (!isProcessing) {
                processWindow()
            }
            handler.postDelayed(this, getRefreshInterval().coerceAtLeast(1000L))
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = applicationContext.getSharedPreferences(Preferences.PREFS_FILE, Context.MODE_PRIVATE)
        LogRepository.add("Accessibility service connected")
        Log.i(logTag, "Accessibility service connected")
        startScanning()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if (!isProcessing) {
                handler.post { processWindow() }
            }
        }
    }

    override fun onInterrupt() {
        stopScanning()
    }

    override fun onDestroy() {
        stopScanning()
        super.onDestroy()
    }

    private fun startScanning() {
        handler.removeCallbacks(scanRunnable)
        handler.postDelayed(scanRunnable, getRefreshInterval().coerceAtLeast(1000L))
    }

    private fun stopScanning() {
        handler.removeCallbacks(scanRunnable)
    }

    private fun getRefreshInterval(): Long {
        return Preferences.loadRefreshInterval(prefs)
    }

    private fun processWindow() {
        val rootNode = rootInActiveWindow ?: return
        isProcessing = true

        val claimNodes = findNodesByKeywords(rootNode, claimKeywords)
        if (claimNodes.isNotEmpty()) {
            for (node in claimNodes) {
                if (clickNode(node)) {
                    lastActionTime = System.currentTimeMillis()
                    LogRepository.add("Clicked claim button")
                    Log.i(logTag, "Clicked claim button")
                    handler.postDelayed({ processConfirmWindow() }, 500)
                    isProcessing = false
                    return
                }
            }
        }

        isProcessing = false
    }

    private fun processConfirmWindow() {
        val rootNode = rootInActiveWindow ?: return
        isProcessing = true

        val confirmNodes = findNodesByKeywords(rootNode, confirmKeywords)
        for (node in confirmNodes) {
            if (clickNode(node)) {
                lastActionTime = System.currentTimeMillis()
                LogRepository.add("Clicked confirm button")
                Log.i(logTag, "Clicked confirm button")
                break
            }
        }

        isProcessing = false
    }

    private fun findNodesByKeywords(node: AccessibilityNodeInfo, keywords: List<String>): List<AccessibilityNodeInfo> {
        val result = mutableListOf<AccessibilityNodeInfo>()
        if (matchesKeywords(node, keywords)) {
            result.add(node)
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { result.addAll(findNodesByKeywords(it, keywords)) }
        }
        return result
    }

    private fun matchesKeywords(node: AccessibilityNodeInfo, keywords: List<String>): Boolean {
        val lowerText = node.text?.toString()?.lowercase() ?: ""
        val lowerDesc = node.contentDescription?.toString()?.lowercase() ?: ""
        val lowerRes = node.viewIdResourceName?.lowercase() ?: ""
        return keywords.any { keyword ->
            lowerText.contains(keyword) || lowerDesc.contains(keyword) || lowerRes.contains(keyword)
        }
    }

    private fun clickNode(node: AccessibilityNodeInfo): Boolean {
        if (node.isClickable && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
            return true
        }
        var parent = node.parent
        while (parent != null) {
            if (parent.isClickable && parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                return true
            }
            parent = parent.parent
        }
        return false
    }
}
