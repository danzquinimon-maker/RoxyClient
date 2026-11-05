package com.roxyclient.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxyclient.ui.theme.RoxyClientTheme

class FloatingMenuService : Service() {
    private var windowManager: WindowManager? = null
    private var floatingView: ComposeView? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (floatingView != null) return START_STICKY

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val view = ComposeView(this).apply {
            setContent {
                RoxyClientTheme {
                    FloatingMenuButton()
                }
            }
        }
        floatingView = view

        val params = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.TRANSLUCENT
            // Keep the overlay touchable so its Compose click handler can receive taps.
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            width = 80
            height = 80
            x = 0
            y = 100
            gravity = Gravity.TOP or Gravity.START
        }

        try {
            windowManager?.addView(view, params)
        } catch (e: WindowManager.BadTokenException) {
            floatingView = null
            stopSelf(startId)
        } catch (e: SecurityException) {
            floatingView = null
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        floatingView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (_: IllegalArgumentException) {
                // The view was already detached.
            }
        }
        floatingView = null
        windowManager = null
        super.onDestroy()
    }
}

@Composable
fun FloatingMenuButton() {
    var isMenuOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                color = Color(0xFF7C3AED),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { isMenuOpen = !isMenuOpen },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isMenuOpen) "✕" else "R",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}
