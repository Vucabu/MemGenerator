package com.vucabu.memgenerator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import java.util.*


class LabledImageView : android.support.v7.widget.AppCompatImageView, RotationGestureDetector.OnRotationGestureListener,
    RotationGestureDetector.OnRotationStopListener {


    private val INVALID_POINTER_ID = -1

    // The ‘active pointer’ is the one currently moving our object.
    private var mActivePointerId = INVALID_POINTER_ID
    private var mIcon: Drawable
    private var actors: List<ImageActor>
    private var selectedActor: ImageActor? = null

    private var mScaleDetector: ScaleGestureDetector
    val mTextPaint = Paint()
    private var mRotationDetector: RotationGestureDetector

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mRotationDetector = RotationGestureDetector(this, this)
        mIcon = context.resources.getDrawable(R.drawable.template_1)
        mIcon.setBounds(0, 0, 50, 50)

        val bitmapTemp = BitmapHelper().drawableToBitmap(mIcon)
        var mBitmap = BitmapHelper().scaleDown(bitmapTemp!!, 300.toFloat(), false)
        mBitmap = BitmapHelper().addBlackBorder(mBitmap, 2)
        actors = Arrays.asList(ImageActor(mBitmap, 0.0f, 0.0f), ImageActor(mBitmap, 500.0f, 500.0f))
        mTextPaint.color = Color.RED
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.textSize = 50f

        // Create our ScaleGestureDetector
        mScaleDetector = ScaleGestureDetector(context, scaleListener)
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        // canvas.translate(mPosX, mPosY)
        // canvas.scale(mScaleFactor, mScaleFactor)
        actors.forEach {
            if (it == selectedActor) {
                it.draw(canvas, mTextPaint)
            } else {
                it.draw(canvas, mTextPaint)
            }
        }
        canvas.restore()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev)
        mRotationDetector.onTouchEvent(ev)
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                actors.forEach {
                    if (selectedActor == null && it.isContains(ev.x, ev.y)) {
                        selectedActor = it
                        mRotationDetector.angle = it.angle
                        it.diff(ev.x, ev.y)
                        it.position(ev.x, ev.y)
                    }
                }
                // Save the ID of this pointer
                mActivePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                // Find the index of the active pointer and fetch its position
                val pointerIndex = ev.findPointerIndex(mActivePointerId)
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (selectedActor != null) {
                    selectedActor?.position(x, y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedActor = null
                mRotationDetector.angle = 0f
                mActivePointerId = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_POINTER_UP -> {
                // Extract the index of the pointer that left the touch sensor
                val pointerIndex =
                    (action and MotionEvent.ACTION_POINTER_INDEX_MASK) shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }

        return true
    }

    override fun OnRotation(rotationDetector: RotationGestureDetector) {
        val angle = rotationDetector.angle
        Log.d("RotationGestureDetector", "Rotation: " + java.lang.Float.toString(angle))
        selectedActor?.angle = angle
    }

    override fun OnStopRotation(rotationDetector: RotationGestureDetector) {
        val angle = rotationDetector.angle

        if (selectedActor != null) {
            selectedActor?.permanentAngle = selectedActor?.permanentAngle?.plus(angle)!!
            selectedActor?.angle = 0f
        }
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = selectedActor?.mScaleFactor
            if (mScaleFactor != null) {
                mScaleFactor *= detector.scaleFactor
                Log.d("PUSSY", "Scale factor=$mScaleFactor, Multiple=${detector.scaleFactor}")
                // Don't let the object get too small or too large.
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))

                selectedActor?.mScaleFactor = mScaleFactor
                invalidate()
            }
            return true
        }
    }

}
