package com.example.pandasmartbot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlin.random.Random

class PandaAccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private var lastTapAt = 0L
    private var nextSideLeft = true
    private var gameplayLoopScheduled = false

    companion object {
        @Volatile var botEnabled: Boolean = false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!botEnabled || event == null) return

        // Start the tap loop only after an event from another app is observed.
        // This is a generic prototype guard, not reliable game-package detection.
        if (event.packageName?.toString() != packageName && !gameplayLoopScheduled) {
            gameplayLoopScheduled = true
            handler.post(gameplayLoop)
        }

        // UI text actions are deliberately conservative. Only click a visible,
        // enabled node whose label matches a known game action.
        val root = rootInActiveWindow ?: return
        val allText = collectText(root).lowercase()
        when {
            allText.contains("level complete") || allText.contains("level completed") -> {
                handler.postDelayed({ if (botEnabled) clickLabel(rootInActiveWindow, "claim") }, 3500L)
            }
            allText.contains("claim") -> {
                handler.postDelayed({ if (botEnabled) clickLabel(rootInActiveWindow, "claim") }, 700L)
            }
            allText.contains("level 3") || allText.contains("level 2") ||
                allText.contains("level 1") -> {
                // Do not auto-start on every content event; user starts game manually.
            }
        }
        root.recycle()
    }

    override fun onInterrupt() {
        botEnabled = false
        gameplayLoopScheduled = false
        handler.removeCallbacks(gameplayLoop)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    private fun collectText(node: AccessibilityNodeInfo): String {
        val out = StringBuilder()
        fun walk(n: AccessibilityNodeInfo) {
            n.text?.let { out.append(it).append(' ') }
            n.contentDescription?.let { out.append(it).append(' ') }
            for (i in 0 until n.childCount) n.getChild(i)?.let { child ->
                walk(child)
                child.recycle()
            }
        }
        walk(node)
        return out.toString()
    }

    private fun clickLabel(root: AccessibilityNodeInfo?, label: String): Boolean {
        if (root == null) return false
        fun search(node: AccessibilityNodeInfo): Boolean {
            val text = (node.text?.toString() ?: "") + " " +
                    (node.contentDescription?.toString() ?: "")
            if (text.contains(label, ignoreCase = true) && node.isEnabled) {
                if (node.isClickable && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
                val parent = node.parent
                val result = parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
                parent?.recycle()
                if (result) return true
            }
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                val found = search(child)
                child.recycle()
                if (found) return true
            }
            return false
        }
        return search(root)
    }

    // Prototype gameplay controller: alternates sides at a conservative interval.
    // It is not vision-based yet; keep the game supervised.
    private val gameplayLoop = object : Runnable {
        override fun run() {
            if (!botEnabled) return
            val now = System.currentTimeMillis()
            if (now - lastTapAt >= 850L) {
                val dm = resources.displayMetrics
                val x = if (nextSideLeft) dm.widthPixels * 0.25f else dm.widthPixels * 0.75f
                val y = dm.heightPixels * 0.55f
                tap(x, y)
                nextSideLeft = !nextSideLeft
                lastTapAt = now
            }
            handler.postDelayed(this, 300L)
        }
    }

    private fun tap(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 70)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        handler.removeCallbacksAndMessages(null)
        gameplayLoopScheduled = false
        return super.onUnbind(intent)
    }
}
