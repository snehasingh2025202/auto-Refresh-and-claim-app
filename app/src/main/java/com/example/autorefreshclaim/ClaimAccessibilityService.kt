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
    private var isProcessing = false
    private lateinit var prefs: SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = applicationContext.getSharedPreferences(Preferences.PREFS_FILE, Context.MODE_PRIVATE)
        LogRepository.add("Accessibility service connected")
        Log.i(logTag, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (!isProcessing) {
                isProcessing = true
                handler.postDelayed({ processWindow() }, getRefreshInterval())
            }
        }
    }

    override fun onInterrupt() {
        // no-op
    }

    private fun getRefreshInterval(): Long {
        return Preferences.loadRefreshInterval(prefs)
    }

    private fun processWindow() {
        val rootNode = rootInActiveWindow ?: run {
            isProcessing = false
            return
        }

        val claimNodes = findNodesByText(rootNode, "claim")
        var clickedClaim = false

        for (node in claimNodes) {
            if (clickNode(node)) {
                LogRepository.add("Clicked claim button")
                Log.i(logTag, "Clicked claim button")
                clickedClaim = true
                break
            }
        }

        if (clickedClaim) {
            handler.postDelayed({
                val confirmRoot = rootInActiveWindow ?: return@postDelayed
                val confirmNodesNext = findNodesByText(confirmRoot, "confirm")
                for (confirmNode in confirmNodesNext) {
                    if (clickNode(confirmNode)) {
                        LogRepository.add("Clicked confirm button")
                        Log.i(logTag, "Clicked confirm button")
                        break
                    }
                }
                isProcessing = false
            }, 500)
        } else {
            isProcessing = false
        }
    }

    private fun findNodesByText(node: AccessibilityNodeInfo, text: String): List<AccessibilityNodeInfo> {
        val lower = text.lowercase()
        val result = mutableListOf<AccessibilityNodeInfo>()
        if (node.text?.toString()?.lowercase()?.contains(lower) == true) {
            result.add(node)
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { result.addAll(findNodesByText(it, text)) }
        }
        return result
    }

    private fun clickNode(node: AccessibilityNodeInfo): Boolean {
        if (node.isClickable) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        var parent = node.parent
        while (parent != null) {
            if (parent.isClickable) {
                return parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            parent = parent.parent
        }
        return false
    }
}
