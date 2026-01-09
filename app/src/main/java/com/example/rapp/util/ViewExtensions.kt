package com.example.rapp.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

/**
 * Aplica padding automático para que el contenido no quede
 * debajo de las barras del sistema (status bar y navigation bar)
 */
fun View.applyInsetsWithPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        view.updatePadding(
            top = insets.top,
            bottom = insets.bottom,
            left = insets.left,
            right = insets.right
        )

        windowInsets
    }
}

/**
 * Aplica margin inferior automático (útil para FABs y botones)
 */
fun View.applyBottomMargin() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = insets.bottom + 16.dpToPx(view)
        }

        windowInsets
    }
}

private fun Int.dpToPx(view: View): Int {
    return (this * view.resources.displayMetrics.density).toInt()
}