package hollowness.necro.fifteen;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
/**
 * Created by kurdyukov_ae on 30.01.2017.
 */
public class SplashThread extends Thread {
    private final int REDRAW_TIME    = 10;
    private final int ANIMATION_TIME = 1000;

    private final SurfaceHolder mSurfaceHolder;
    private final Activity mActivity;

    private boolean mRunning;
    private long    mStartTime;
    private long    mPrevRedrawTime;

    private Paint mPaint;
    private ArgbEvaluator mArgbEvaluator;
    private Bitmap mBitmap;
    private int mStartSize;

    public SplashThread(SurfaceHolder holder, Activity activity) {
        mSurfaceHolder = holder;
        mActivity = activity;
        mRunning = false;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        //mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextAlign(Paint.Align.LEFT);
        //mPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
        //get logical density ( DENSITY_LOW, DENSITY_MEDIUM, or DENSITY_HIGH. )
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.d(getClass().getSimpleName(),""+dm.densityDpi);
        if (dm.densityDpi <= dm.DENSITY_MEDIUM) {
            mStartSize = 48;
        } else if (dm.densityDpi <= dm.DENSITY_HIGH) {
            mStartSize = 72;
        } else if (dm.densityDpi <= dm.DENSITY_XHIGH) {
            mStartSize = 96;
        } else {
            mStartSize = 144;
        }
        //Log.d(getClass().getSimpleName(),""+dm.densityDpi);
        try {
            mBitmap = BitmapFactory.decodeStream(activity.getAssets().open(activity.getString(R.string.mainicon)));
        } catch (Exception e) {
            mBitmap = null;
        }
        mArgbEvaluator = new ArgbEvaluator();
    }

    public void setRunning(boolean running) {
        mRunning = running;
        mPrevRedrawTime = getTime();
    }

    public long getTime() {
        return System.nanoTime() / 1000000;
    }

    @Override
    public void run() {
        Canvas canvas;
        mStartTime = getTime();

        while (mRunning) {
            long curTime = getTime();
            long elapsedTime = curTime - mPrevRedrawTime;
            if (mStartTime + ANIMATION_TIME < curTime) {
                mRunning = false;
            }
            if (elapsedTime < REDRAW_TIME)
                continue;
            canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    draw(canvas);
                }
            }
            catch (NullPointerException e) {}
            finally {
                if (canvas != null)
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
            }

            mPrevRedrawTime = curTime;
        }
        closeSplash();
    }

    protected void closeSplash() {
        mActivity.startActivity(new Intent(mActivity, MenuActivity.class));
        mActivity.finish();
    }

    private void draw(Canvas canvas) {
        long curTime = getTime() - mStartTime;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.drawColor(Color.BLACK);

        int centerX = width / 2;
        int centerY = height / 2;

        //float maxSize = Math.min(width, height) / 2;
        float maxSize = Math.max(width, height);

        float fraction = (float) (curTime) / (ANIMATION_TIME*1.5f);
        int size = (int)(maxSize * fraction);

        int color = (Integer) mArgbEvaluator.evaluate(fraction, Color.argb(255,0xf0,0xf0,0x30), Color.BLACK);
        mPaint.setColor(color);
        canvas.drawBitmap(mBitmap, new Rect(0,0, mBitmap.getWidth(), mBitmap.getHeight()), new Rect(centerX-size/2, centerY-size/2, centerX+size/2, centerY+size/2), mPaint);
    }
}
