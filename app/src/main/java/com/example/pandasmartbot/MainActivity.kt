package com.example.pandasmartbot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 28, 28, 28)
        }
        val title = TextView(this).apply {
            text = "🐼 Panda Smart BOT (Prototype)"
            textSize = 22f
            gravity = Gravity.CENTER
        }
        val info = TextView(this).apply {
            text = "1) Enable Accessibility service below.\\n2) Open Panda Climbing Game yourself.\\n3) Return here and start/stop the test bot.\\n\\nPrototype note: gameplay taps alternate left/right. It does NOT yet visually detect branches or guarantee level completion."
            textSize = 16f
            setPadding(0, 24, 0, 24)
        }
        val settingsButton = Button(this).apply {
            text = "Enable Accessibility Service"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        val startButton = Button(this).apply {
            text = "Start BOT"
            setOnClickListener {
                PandaAccessibilityService.botEnabled = true
                info.text = "BOT is ON. Open the game and supervise it. Tap Stop BOT to stop."
            }
        }
        val stopButton = Button(this).apply {
            text = "Stop BOT"
            setOnClickListener {
                PandaAccessibilityService.botEnabled = false
                info.text = "BOT is OFF."
            }
        }
        layout.addView(title)
        layout.addView(info)
        layout.addView(settingsButton)
        layout.addView(startButton)
        layout.addView(stopButton)
        setContentView(layout)
    }
}
