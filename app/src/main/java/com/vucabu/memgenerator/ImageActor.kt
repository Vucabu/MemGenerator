package com.vucabu.memgenerator

import android.graphics.*


class ImageActor {
    private var mBitmap: Bitmap
    private var mPosX: Float
    private var mPosY: Float
    var mDiffX: Float = 0.0f
    var mDiffY: Float = 0.0f
    var angle: Float = 0.0f
    var permanentAngle: Float = 0.0f
    private val matrix = Matrix()
    private val tmpM: Matrix = Matrix()

    private val rect: Rect
    private val bWidth: Int
    private val bHeight: Int
    var mScaleFactor = 1f

    constructor(bitmap: Bitmap, posX: Float, posY: Float) {
        this.mBitmap = bitmap
        bWidth = mBitmap.width
        bHeight = mBitmap.height
        rect = Rect(0, 0, bWidth, bHeight)
        this.mPosX = posX
        this.mPosY = posY
    }

    fun position(x: Float, y: Float) {
        this.mPosX = x - mDiffX
        this.mPosY = y - mDiffY
    }

    fun diff(x: Float, y: Float) {
        this.mDiffX = x - mPosX
        this.mDiffY = y - mPosY
    }

    fun draw(canvas: Canvas, mTextPaint: Paint) {
        canvas.save()
        matrix.reset()
        matrix.preScale(mScaleFactor, mScaleFactor)
        matrix.postRotate(-(angle + permanentAngle), bWidth.toFloat() / 2, bHeight.toFloat() / 2)
        matrix.postTranslate(mPosX, mPosY)

        canvas.drawBitmap(mBitmap, matrix, mTextPaint)
        canvas.restore()
    }

    /**
     * Checks whether a point is on the transformed image
     */
    fun isContains(x: Float, y: Float): Boolean {
        val p1 = floatArrayOf(0f, 0f)
        val p2 = floatArrayOf(x, y)
        tmpM.reset()
        matrix.invert(tmpM)
        tmpM.mapPoints(p1, 0, p2, 0, 1)
        return rect.contains(p1[0].toInt(), p1[1].toInt())
    }

}
