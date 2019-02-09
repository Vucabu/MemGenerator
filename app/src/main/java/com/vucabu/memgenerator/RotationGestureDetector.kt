package com.vucabu.memgenerator

import android.view.MotionEvent

class RotationGestureDetector(private val mListener: OnRotationGestureListener?, private val mStopListener: OnRotationStopListener) {
    private var fX: Float = 0.toFloat()
    private var fY: Float = 0.toFloat()
    private var sX: Float = 0.toFloat()
    private var sY: Float = 0.toFloat()
    private var ptrID1: Int = 0
    private var ptrID2: Int = 0
    var angle: Float = 0.toFloat()

    init {
        ptrID1 = INVALID_POINTER_ID
        ptrID2 = INVALID_POINTER_ID
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> ptrID1 = event.getPointerId(event.actionIndex)
            MotionEvent.ACTION_POINTER_DOWN -> {
                ptrID2 = event.getPointerId(event.actionIndex)
                sX = event.getX(event.findPointerIndex(ptrID1))
                sY = event.getY(event.findPointerIndex(ptrID1))
                fX = event.getX(event.findPointerIndex(ptrID2))
                fY = event.getY(event.findPointerIndex(ptrID2))
            }
            MotionEvent.ACTION_MOVE -> if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) {
                val nfX: Float = event.getX(event.findPointerIndex(ptrID2))
                val nfY: Float = event.getY(event.findPointerIndex(ptrID2))
                val nsX: Float = event.getX(event.findPointerIndex(ptrID1))
                val nsY: Float = event.getY(event.findPointerIndex(ptrID1))

                angle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY)

                mListener?.OnRotation(this)
            }
            MotionEvent.ACTION_UP -> ptrID1 = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                ptrID2 = INVALID_POINTER_ID
                mStopListener?.OnStopRotation(this)
            }
            MotionEvent.ACTION_CANCEL -> {
                ptrID1 = INVALID_POINTER_ID
                ptrID2 = INVALID_POINTER_ID
            }
        }
        return true
    }

    private fun angleBetweenLines(
        fX: Float,
        fY: Float,
        sX: Float,
        sY: Float,
        nfX: Float,
        nfY: Float,
        nsX: Float,
        nsY: Float
    ): Float {
        val angle1 = Math.atan2((fY - sY).toDouble(), (fX - sX).toDouble()).toFloat()
        val angle2 = Math.atan2((nfY - nsY).toDouble(), (nfX - nsX).toDouble()).toFloat()

        var angle = Math.toDegrees((angle1 - angle2).toDouble()).toFloat() % 360
        if (angle < -180f) angle += 360.0f
        if (angle > 180f) angle -= 360.0f
        return angle
    }

    interface OnRotationGestureListener {
        fun OnRotation(rotationDetector: RotationGestureDetector)
    }

    interface OnRotationStopListener {
        fun OnStopRotation(rotationDetector: RotationGestureDetector)
    }

    companion object {
        private val INVALID_POINTER_ID = -1
    }
}