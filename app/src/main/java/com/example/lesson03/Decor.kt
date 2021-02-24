package com.example.lesson03

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

public class Decor(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

//    private val greenStr : String = "#76ff03"

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        outRect.right = verticalSpaceHeight/2
        outRect.left = verticalSpaceHeight / 2
        outRect.bottom = verticalSpaceHeight
    }
}