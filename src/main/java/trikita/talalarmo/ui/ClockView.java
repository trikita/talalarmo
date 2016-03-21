package trikita.talalarmo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class ClockView extends SeekBar {
	private final static String tag = "ClockView";

	private final static float RING_WIDTH_COEF = 0.09f;
	private final static float PADDING_COEF = RING_WIDTH_COEF * 2;

	private int mPrimaryColor;
	private int mAccentColor;

	private Paint mPaint = new Paint();
	private OnSeekBarChangeListener mListener;

	public ClockView(Context c) {
		super(c);
		mPaint.setAntiAlias(true);
		mPrimaryColor = Theme.LIGHT.primaryColor;
		mAccentColor = Theme.LIGHT.accentColor;
	}

	@Override
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
		mListener = l;
	}

	@Override
	public void onDraw(Canvas c) {
		int w = getWidth();

		DisplayMetrics dm = getResources().getDisplayMetrics();

		float padding = w * PADDING_COEF;
		float center = w / 2.0f;
		float radius = (w - padding) / 2.0f;

		// draw ring
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		mPaint.setColor(mPrimaryColor);

		RectF rectf = new RectF(center - radius, center - radius, center + radius, center + radius);
		float a = (float) (getProgress() * 360 / getMax());
		if (a == 0) {
			a = 360;
		}
		c.drawArc(rectf, a - 90, 360 - a, false, mPaint);
		mPaint.setColor(mAccentColor);
		c.drawArc(rectf, -90, a, false, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		PointF p = new PointF(event.getX(), event.getY());
		if (event.getAction() == MotionEvent.ACTION_DOWN ||
				event.getAction() == MotionEvent.ACTION_MOVE) {
			if (isTouched(p)) {
				double angle = pointToAngle(p);
				mListener.onProgressChanged(this, (int) (angle / Math.PI / 2 * getMax()), true);
				return true;
            }
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			mListener.onProgressChanged(this, (int) (pointToAngle(p)/ Math.PI / 2 * getMax()), true);
			return true;
		}
		return false;
	}

	public double pointToAngle(PointF p) {
		double dx = getWidth()/2.0f - p.x;
		double dy = getHeight()/2.0f - p.y;
		return (Math.atan2(dy, dx) + 3 * Math.PI/2) % (Math.PI * 2);
	}

	public double getDistanceToCenter(PointF p) {
		int center = (int) (getWidth() / 2.0f);
		return Math.sqrt((p.x-center)*(p.x-center)+
				(p.y-center)*(p.y-center));
	}

	public boolean isTouched(PointF p) {
		double dist = getDistanceToCenter(p);
		float r = getWidth()/2 * (1-PADDING_COEF);
		return (dist > (r * 0.6) && dist < (r * 1.3));
	}
}
