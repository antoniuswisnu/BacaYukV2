package com.nara.bacayuk.ui.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.updatePaddingRelative
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class CenterLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private lateinit var recyclerView: RecyclerView

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (childCount == 0 && state.itemCount > 0) {
            val firstChild = recycler.getViewForPosition(0)
            measureChildWithMargins(firstChild, 0, 0)
            recycler.recycleView(firstChild)
        }
        super.onLayoutChildren(recycler, state)
    }

    override fun measureChildWithMargins(child: View, widthUsed: Int, heightUsed: Int) {
        val lp = (child.layoutParams as RecyclerView.LayoutParams).absoluteAdapterPosition
        super.measureChildWithMargins(child, widthUsed, heightUsed)
        if (lp != 0 && lp != itemCount - 1) return
        when (orientation) {
            HORIZONTAL -> {
                val hPadding = ((width - child.measuredWidth) / 2).coerceAtLeast(0)
                if (!reverseLayout) {
                    if (lp == 0) recyclerView.updatePaddingRelative(start = hPadding)
                    if (lp == itemCount - 1) recyclerView.updatePaddingRelative(end = hPadding)
                } else {
                    if (lp == 0) recyclerView.updatePaddingRelative(end = hPadding)
                    if (lp == itemCount - 1) recyclerView.updatePaddingRelative(start = hPadding)
                }
            }
            VERTICAL -> {
                val vPadding = ((height - child.measuredHeight) / 2).coerceAtLeast(0)
                if (!reverseLayout) {
                    if (lp == 0) recyclerView.updatePaddingRelative(top = vPadding)
                    if (lp == itemCount - 1) recyclerView.updatePaddingRelative(bottom = vPadding)
                } else {
                    if (lp == 0) recyclerView.updatePaddingRelative(bottom = vPadding)
                    if (lp == itemCount - 1) recyclerView.updatePaddingRelative(top = vPadding)
                }
            }
        }
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        recyclerView = view
        super.onAttachedToWindow(view)
    }
}