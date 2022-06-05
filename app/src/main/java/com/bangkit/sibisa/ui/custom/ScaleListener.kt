package com.bangkit.sibisa.ui.custom

import android.view.ScaleGestureDetector
import android.widget.ImageView
import kotlin.math.max
import kotlin.math.min

class ScaleListener internal constructor(private var mImageView: ImageView) :
    ScaleGestureDetector.SimpleOnScaleGestureListener() {
    private var mScaleFactor = 1.0f

    override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
        mScaleFactor *= scaleGestureDetector.scaleFactor
        mScaleFactor = max(
            0.1f,
            min(mScaleFactor, 2.5f)
        )
        mImageView.scaleX = mScaleFactor
        mImageView.scaleY = mScaleFactor
        return true
    }
}