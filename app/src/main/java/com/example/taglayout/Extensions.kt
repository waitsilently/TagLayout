package com.example.taglayout

import android.content.res.Resources
import android.util.TypedValue

val Int.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)