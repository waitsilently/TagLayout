package com.example.taglayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.max

class TagLayout(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var lineWidthUsed = 0
        var heightUsed = 0
        var lineMaxWidth = 0
        var lineMaxHeight = 0

        children.forEachIndexed { _, child ->
            selfMeasureChildWithMargins(
                child,
                widthMeasureSpec,
                lineWidthUsed,
                heightMeasureSpec,
                heightUsed
            )

            if (lineWidthUsed + child.measuredWidth >= widthSize) {

                heightUsed += lineMaxHeight
                lineMaxHeight = 0
                lineWidthUsed = 0
                selfMeasureChildWithMargins(
                    child,
                    widthMeasureSpec,
                    lineWidthUsed,
                    heightMeasureSpec,
                    heightUsed
                )
            }

            val tagLayoutParams = (child.layoutParams as TagLayoutParams)
            tagLayoutParams.left = lineWidthUsed
            tagLayoutParams.top = heightUsed
            tagLayoutParams.bottom = heightUsed + child.measuredHeight
            tagLayoutParams.right = lineWidthUsed + child.measuredWidth

            lineMaxHeight = max(lineMaxHeight, child.measuredHeight)
            lineWidthUsed += child.measuredWidth
            lineMaxWidth = max(lineMaxWidth, lineWidthUsed)
        }

        setMeasuredDimension(lineMaxWidth, heightUsed + lineMaxHeight)
    }

    private fun selfMeasureChildWithMargins(
        child: View,
        parentWidthMeasureSpec: Int, widthUsed: Int,
        parentHeightMeasureSpec: Int, heightUsed: Int
    ) {
        val lp = child.layoutParams as MarginLayoutParams
        val childWidthMeasureSpec = selfGetChildMeasureSpec(
            parentWidthMeasureSpec,
            paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin
                    + widthUsed, lp.width
        )
        val childHeightMeasureSpec = selfGetChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin
                    + heightUsed, lp.height
        )
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private fun selfGetChildMeasureSpec(spec: Int, padding: Int, childDimension: Int): Int {
        val specMode = MeasureSpec.getMode(spec)
        val specSize = MeasureSpec.getSize(spec)
        val size = max(0, specSize - padding)
        var resultSize = 0
        var resultMode = 0
        when (specMode) {
            MeasureSpec.EXACTLY -> when {
                childDimension >= 0 -> {
                    resultSize = childDimension
                    resultMode = MeasureSpec.EXACTLY
                }
                childDimension == LayoutParams.MATCH_PARENT -> {
                    // Child wants to be our size. So be it.
                    resultSize = size
                    resultMode = MeasureSpec.EXACTLY
                }
                childDimension == LayoutParams.WRAP_CONTENT -> {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size
                    resultMode = MeasureSpec.AT_MOST
                }
            }
            MeasureSpec.AT_MOST -> when {
                childDimension >= 0 -> {
                    // Child wants a specific size... so be it
                    resultSize = childDimension
                    resultMode = MeasureSpec.EXACTLY
                }
                childDimension == LayoutParams.MATCH_PARENT -> {
                    // Child wants to be our size, but our size is not fixed.
                    // Constrain child to not be bigger than us.
                    resultSize = size
                    resultMode = MeasureSpec.AT_MOST
                }
                childDimension == LayoutParams.WRAP_CONTENT -> {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size
                    resultMode = MeasureSpec.AT_MOST
                }
            }
            MeasureSpec.UNSPECIFIED -> if (childDimension >= 0) {
                // Child wants a specific size... let him have it
                resultSize = childDimension
                resultMode = MeasureSpec.EXACTLY
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size... find out how big it should
                // be
                resultSize = 0
                resultMode = MeasureSpec.UNSPECIFIED
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size.... find out how
                // big it should be
                resultSize = 0
                resultMode = MeasureSpec.UNSPECIFIED
            }
        }
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return TagLayoutParams(context, attrs)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (child in children) {
            val tagLayoutParams = child.layoutParams as TagLayoutParams
            child.layout(tagLayoutParams.left, tagLayoutParams.top, tagLayoutParams.right, tagLayoutParams.bottom)
        }
    }

    inner class TagLayoutParams(context: Context, attrs: AttributeSet?) : MarginLayoutParams(context, attrs) {
        var left = 0
        var right = 0
        var top = 0
        var bottom = 0
    }
}