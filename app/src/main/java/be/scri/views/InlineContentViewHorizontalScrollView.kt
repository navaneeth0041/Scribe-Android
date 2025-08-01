// SPDX-License-Identifier: GPL-3.0-or-later

package be.scri.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewTreeObserver.OnDrawListener
import android.widget.HorizontalScrollView
import android.widget.inline.InlineContentView
import androidx.annotation.AttrRes
import androidx.core.view.allViews

/**
 * [HorizontalScrollView] adapted for holding [InlineContentView] instances
 * It can hold other views too, but it will ensure [InlineContentView] instances
 * these are properly clipped and not drawn over rest of the window,
 * but still remaining clickable
 * (since setting [InlineContentView.setZOrderedOnTop] to false prevents clicking)
 */
class InlineContentViewHorizontalScrollView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
    ) : HorizontalScrollView(context, attrs, defStyleAttr),
        OnDrawListener {
        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            viewTreeObserver.addOnDrawListener(this)
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            viewTreeObserver.removeOnDrawListener(this)
        }

        override fun onScrollChanged(
            l: Int,
            t: Int,
            oldl: Int,
            oldt: Int,
        ) {
            super.onScrollChanged(l, t, oldl, oldt)
            clipDescendantInlineContentViews()
        }

        override fun onDraw() {
            clipDescendantInlineContentViews()
        }

        private fun clipDescendantInlineContentViews() {
            allViews.forEach {
                if (it is InlineContentView) {
                    val parentBounds = Rect(scrollX, scrollY, width + scrollX, height + scrollY)
                    offsetRectIntoDescendantCoords(it, parentBounds)
                    it.clipBounds = parentBounds
                }
            }
        }
    }
