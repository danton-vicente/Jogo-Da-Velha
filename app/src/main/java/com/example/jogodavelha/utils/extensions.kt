package com.example.jogodavelha.utils

import android.view.View
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView

fun MaterialSwitch.putChecked(isChecked: Boolean) {
    this.isChecked = isChecked
}

fun View.isVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE
    else View.GONE
}

fun MaterialTextView.setTextAnimation(text: String, duration: Long = 100, completion: (() -> Unit)? = null) {
    fadOutAnimation(duration) {
        this.text = text
        fadInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun MaterialToolbar.setTextTitleAnimation(text: String, duration: Long = 100, completion: (() -> Unit)? = null) {
    fadOutAnimation(duration) {
        this.title = text
        fadInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun MaterialToolbar.setTextSubTitleAnimation(text: String, duration: Long = 100, completion: (() -> Unit)? = null) {
    fadOutAnimation(duration) {
        this.subtitle = text
        fadInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun MaterialButton.updateTextWithAnimation(text: String, duration: Long = 300, completion: (() -> Unit)? = null) {
    fadOutAnimation(duration) {
        this.text = text
        fadInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun View.fadOutAnimation(duration: Long = 100, visibility: Int = View.INVISIBLE, completion: (() -> Unit)? = null) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            this.visibility = visibility
            completion?.let {
                it()
            }
        }
}

fun View.fadInAnimation(duration: Long = 100, completion: (() -> Unit)? = null) {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(duration)
        .withEndAction {
            completion?.let {
                it()
            }
        }
}