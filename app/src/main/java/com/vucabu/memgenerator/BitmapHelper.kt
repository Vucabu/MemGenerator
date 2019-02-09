package com.vucabu.memgenerator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class BitmapHelper {

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap?

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap =
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    fun scaleDown(
        realImage: Bitmap, maxImageSize: Float,
        filter: Boolean
    ): Bitmap {
        val ratio = Math.min(
            maxImageSize / realImage.width,
            maxImageSize / realImage.height
        )
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)

        return Bitmap.createScaledBitmap(
            realImage, width,
            height, filter
        )
    }

    fun addBlackBorder(bmp: Bitmap, borderSize: Int): Bitmap {
        val bmpWithBorder = Bitmap.createBitmap(bmp.width + borderSize * 2, bmp.height + borderSize * 2, bmp.config)
        val canvas = Canvas(bmpWithBorder)
        canvas.drawColor(Color.BLACK)
        canvas.drawBitmap(bmp, borderSize.toFloat(), borderSize.toFloat(), null)
        return bmpWithBorder
    }
}
