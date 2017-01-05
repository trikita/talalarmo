package trikita.talalarmo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.widget.ToggleButton;

import trikita.talalarmo.App;

public class AmPmSwitch extends ToggleButton {

    private final static float TEXT_SIZE_COEF = 0.3f;    // from view height
    private final static float DRAWABLE_PADDING_COEF = 0.1f;
    private final static float DRAWABLE_WIDTH_COEF = 0.8f;
    private final static float CIRCLE_RADIUS_COEF = 0.2f;

    private Paint mPaint;

    public AmPmSwitch(Context c) {
        super(c);
        Typeface t = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Light.ttf");

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setStrokeWidth(4);
        mPaint.setTypeface(t);
        setBackgroundDrawable(null);
    }

    @Override
    public void onDraw(Canvas c) {
        int colorOff = Theme.get(App.getState().settings().theme()).primaryColor;
        int colorOn = Theme.get(App.getState().settings().theme()).accentColor;

        mPaint.setTextSize((int) (getHeight() * TEXT_SIZE_COEF));
        Rect amRect = new Rect();
        mPaint.getTextBounds("AM", 0, "AM".length(), amRect);
        float amW = mPaint.measureText("AM");
        Rect pmRect = new Rect();
        mPaint.getTextBounds("PM", 0, "PM".length(), pmRect);
        float pmW = mPaint.measureText("PM");
        float drawableWidth = getWidth() - amW - pmW;
        float lineWidth = drawableWidth * DRAWABLE_WIDTH_COEF;
        float padding = drawableWidth * DRAWABLE_PADDING_COEF;
        float r = lineWidth * CIRCLE_RADIUS_COEF;

        float startPosX = 0;
        float startPosY = (getHeight() + amRect.height()) / 2.0f;

        mPaint.setColor(colorOff);
        c.drawText("AM", startPosX, startPosY, mPaint);
        c.drawText("PM", startPosX + amW + drawableWidth, startPosY, mPaint);

        mPaint.setColor(colorOn);
        if (isChecked()) {
            c.drawCircle(startPosX + amW + padding + r, getHeight() / 2.0f, r, mPaint);
        } else {
            c.drawCircle(getWidth() - pmW - padding - r, getHeight() / 2.0f, r, mPaint);
        }
        c.drawLine(startPosX + amW + padding,
                getHeight() / 2.0f,
                startPosX + amW + padding + lineWidth,
                getHeight() / 2.0f,
                mPaint);
    }
}
