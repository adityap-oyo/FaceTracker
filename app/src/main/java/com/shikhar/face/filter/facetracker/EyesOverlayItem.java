
package com.shikhar.face.filter.facetracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.shikhar.face.filter.facetracker.ui.camera.Overlay;


/**
 * Graphics class for rendering Eyes on a graphic overlay given the current eye positions.
 */
class EyesOverlayItem extends Overlay.OverlayItem {
    private static final float EYE_RADIUS_PROPORTION = 0.45f;
    private static final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;

    private Paint mEyeWhitesPaint;
    private Paint mEyeIrisPaint;
    private Paint mEyeOutlinePaint;
    private Paint mEyeLidPaint;
    private Paint mBoxPaint;

    // Keep independent physics state for each eye.
    private EyeLocationCalculator mLeftPhysics = new EyeLocationCalculator();
    private EyeLocationCalculator mRightPhysics = new EyeLocationCalculator();

    private volatile PointF mLeftPosition;
    private volatile boolean mLeftOpen;

    private volatile PointF mRightPosition;
    private volatile boolean mRightOpen;

    private Context mContext;

    //==============================================================================================
    // Methods
    //==============================================================================================

    EyesOverlayItem(Overlay overlay, Context context) {
        super(overlay);

        mEyeWhitesPaint = new Paint();
        mEyeWhitesPaint.setColor(Color.WHITE);
        mEyeWhitesPaint.setStyle(Paint.Style.FILL);

        mEyeLidPaint = new Paint();
        mEyeLidPaint.setColor(Color.YELLOW);
        mEyeLidPaint.setStyle(Paint.Style.FILL);

        mEyeIrisPaint = new Paint();
        mEyeIrisPaint.setColor(Color.BLACK);
        mEyeIrisPaint.setStyle(Paint.Style.FILL);

        mEyeOutlinePaint = new Paint();
        mEyeOutlinePaint.setColor(Color.BLACK);
        mEyeOutlinePaint.setStyle(Paint.Style.STROKE);
        mEyeOutlinePaint.setStrokeWidth(5);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(Color.RED);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(5);

        this.mContext = context;
    }

    /**
     * Updates the eye positions and state from the detection of the most recent frame.  Invalidates
     * the relevant portions of the overlay to trigger a redraw.
     */
    void updateEyes(PointF leftPosition, boolean leftOpen,
                    PointF rightPosition, boolean rightOpen) {
        mLeftPosition = leftPosition;
        mLeftOpen = leftOpen;

        mRightPosition = rightPosition;
        mRightOpen = rightOpen;

        postInvalidate();
    }

    /**
     * Draws the current eye state to the supplied canvas.  This will draw the eyes at the last
     * reported position from the tracker, and the iris positions according to the physics
     * simulations for each iris given motion and other forces.
     */
    @Override
    public void draw(Canvas canvas) {
        PointF detectLeftPosition = mLeftPosition;
        PointF detectRightPosition = mRightPosition;
        if ((detectLeftPosition == null) || (detectRightPosition == null)) {
            return;
        }

        PointF leftPosition =
                new PointF(translateX(detectLeftPosition.x), translateY(detectLeftPosition.y));
        PointF rightPosition =
                new PointF(translateX(detectRightPosition.x), translateY(detectRightPosition.y));

        // Use the inter-eye distance to set the size of the eyes.
        float distance = (float) Math.sqrt(
                Math.pow(rightPosition.x - leftPosition.x, 2) +
                Math.pow(rightPosition.y - leftPosition.y, 2));
        float eyeRadius = EYE_RADIUS_PROPORTION * distance;
        float irisRadius = IRIS_RADIUS_PROPORTION * distance;

        /*Resources res = mContext.getResources();
        Bitmap glassBitmap = BitmapFactory.decodeResource(res, R.drawable.ic_aviator);

        canvas.drawBitmap(glassBitmap, leftPosition.x-eyeRadius-6.0f, leftPosition.y-eyeRadius-32.0f, mBoxPaint);*/


        // Advance the current left iris position, and draw left eye.
        PointF leftIrisPosition =
                mLeftPhysics.nextIrisPosition(leftPosition, eyeRadius, irisRadius);
        drawEye(canvas, leftPosition, eyeRadius, leftIrisPosition, irisRadius, mLeftOpen);

        // Advance the current right iris position, and draw right eye.
        PointF rightIrisPosition =
                mRightPhysics.nextIrisPosition(rightPosition, eyeRadius, irisRadius);
        drawEye(canvas, rightPosition, eyeRadius, rightIrisPosition, irisRadius, mRightOpen);
    }

    /**
     * Draws the eye, either closed or open with the iris in the current position.
     */
    private void drawEye(Canvas canvas, PointF eyePosition, float eyeRadius,
                         PointF irisPosition, float irisRadius, boolean isOpen) {
        if (isOpen) {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeWhitesPaint);
            canvas.drawCircle(irisPosition.x, irisPosition.y, irisRadius, mEyeIrisPaint);
        } else {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeLidPaint);
            float y = eyePosition.y;
            float start = eyePosition.x - eyeRadius;
            float end = eyePosition.x + eyeRadius;
            canvas.drawLine(start, y, end, y, mEyeOutlinePaint);
        }
        canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeOutlinePaint);
    }
}
