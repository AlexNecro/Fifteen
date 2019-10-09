package hollowness.necro.fifteen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by kurdyukov_ae on 30.01.2017.
 */
public class GameFifteenThread extends Thread {

    private final SurfaceHolder mSurfaceHolder; //нужен, для получения canvas
    private GameFifteenActivity context;
    int gx, gy; //up pos in ingame coords
    //protected int dx, dy; //down pos in ingame coords
    int dtx, dty; //down in-tile pos in ingame coords

    private boolean mRunning; //запущен ли процесс
    //private long    mStartTime; //время начала анимации
    private long    mPrevRedrawTime; //предыдущее время перерисовки
    private Boolean isDebug;

    //BitmapDrawable numbers;
    private Bitmap bmNumbers;//, bmBG;

    private Paint mPaint;
    private Paint shadowPaint;

    private GameField field;
    private int iw, ih;
    private int orgX, orgY;
    private int sw, sh;
    private String ruleName;

    GameFifteenThread(GameFifteenActivity context, SurfaceHolder holder) {
        isDebug = GameData.getInstance(null).getDebug();
        mSurfaceHolder = holder;
        mRunning = false;
        this.context = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(35.0f);

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.WHITE);
        shadowPaint.setStrokeWidth(2.0f);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
        shadowPaint.setTextSize(35.0f);
        shadowPaint.setTextAlign(Paint.Align.CENTER);
        Log.d(getClass().getSimpleName(), "new thread constructed");
    }

    void Init(GameField _field) {
        //this.field = field;
        ruleName = _field.getRules().getName(context);
        if (field != _field) {//this is new level
            field = _field;
            try {
                bmNumbers = GameData.getInstance(null).loadLevelBitmap(field);
            }catch (Exception e) {
                Log.d(getClass().getSimpleName(),"Init(): loadLevelBitmap exception");
            }
        }
        try {
            Canvas canvas = mSurfaceHolder.lockCanvas();
            calcSizes(canvas);//init
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        } catch (Exception e) {//there's parallelism error
            Log.d(getClass().getSimpleName(), "Init(): exception on calcSizes(): "+e.getMessage());
        }
        mRunning = true;
        try {
            if (!isAlive()) start();
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "Init(): start() failed");
        }
        Log.d(getClass().getSimpleName(), "Init()");
    }

    void Stop() {
        mRunning = false;
        mPrevRedrawTime = getTime();
        Log.d(getClass().getSimpleName(), "Stop()");
    }

    private long getTime() {
        return System.nanoTime() / 1000000;
    }

    @Override
    public void run() {
        Canvas canvas;
        //mStartTime = getTime();

        while (mRunning) {
            long curTime = getTime();
            long elapsedTime = curTime - mPrevRedrawTime;
            int REDRAW_TIME = 10;
            if (elapsedTime < REDRAW_TIME)
                continue;
            canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas(); //получаем canvas
                synchronized (mSurfaceHolder) {
                    try {//bmNumbers may be recycled
                        draw(canvas); //функция рисования
                    } catch (Exception e) {
                        try {
                            Log.d(getClass().getSimpleName(),"run(): waiting bitmap: "+e.getMessage());
                            sleep(100);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            }
            catch (NullPointerException e) {/*если canvas не доступен*/}
            finally {
                if (canvas != null)
                    mSurfaceHolder.unlockCanvasAndPost(canvas); //освобождаем canvas
            }

            mPrevRedrawTime = curTime;
        }
        bmNumbers = null;
        Log.d(getClass().getSimpleName(), "Run(): ended");
    }

    int GetWorldX(int screenX) {//translate screen to world
        int x = (screenX - orgX) / sw + ((((screenX - orgX) % sw) > 0)?1:0);
        if (x>= 1 && x <= field.getSizeX()) return x-1;
        return -1;
    }

    int GetWorldY(int screenY) {
        int y = (screenY - orgY) / sh + ((((screenY - orgY) % sh) > 0)?1:0);
        if (y>= 1 && y<=field.getSizeY()) return y-1;
        return -1;
    }

    int GetTileX(int screenX) {//translate screen to in-tile offset
        int x = screenX - orgX - ((screenX - orgX) / sw)*sw;
        return x;
    }

    int GetTileY(int screenY) {
        int y = screenY - orgY - (int)((screenY - orgY) / sh)*sh;
        return y;
    }

    public int GetTileCountX(int pixels) {
        return Math.round(1.0f*pixels/sw);
    }

    public int GetTileCountY(int pixels) {
        return Math.round(1.0f*pixels/sh);
    }

    private void draw(Canvas canvas) {
        shadowPaint.setTextSize(35.0f);
        //debug print:
        if (isDebug)
            canvas.drawText(""+getTime(), canvas.getWidth() / 2, 40, shadowPaint);
        //calculate display sizes:
        calcSizes(canvas);
        int maxSize = ((canvas.getHeight() > canvas.getWidth())?canvas.getWidth():canvas.getHeight());

        //draw shadow copy of puzzle to the bg:
        canvas.drawColor(Color.LTGRAY);
        canvas.drawBitmap(bmNumbers, new Rect(0, 0, iw*field.getSizeX(), ih*field.getSizeY()),
                new Rect((canvas.getWidth()-maxSize)/2, (canvas.getHeight()-maxSize)/2, canvas.getWidth()-(canvas.getWidth()-maxSize)/2, canvas.getHeight() - (canvas.getHeight()-maxSize)/2),
                mPaint);
        canvas.drawARGB(192,0,0,0);
        canvas.drawRoundRect(new RectF(orgX-5, orgY-5, (canvas.getWidth()-orgX)+5, (canvas.getHeight()-orgY)+5), 5.0f, 5.0f, shadowPaint);
        if (isDebug)
            canvas.drawText(""+getTime(), canvas.getWidth() / 2, 40, shadowPaint);
        int iy;
        int ix;
        for (int y=0; y < field.getSizeY(); y++)
            for (int x=0; x < field.getSizeX(); x++) {
                if (field.getDragging() && y==field.getStartPoint().y && x==field.getStartPoint().x) {
                    int a = 0;
                    continue;
                }
                int number = field.XY(x,y); //get number at pos
                if (number == 0) {
                    ix = field.getSizeX()-1;
                    iy = field.getSizeY()-1;
                } else {
                    ix = (number-1)%(field.getSizeX());
                    iy = (number-1)/field.getSizeX();
                }
                if (!field.IsWon()) {
                    canvas.drawBitmap(bmNumbers, new Rect(ix * iw, iy * ih, (ix + 1) * iw, (iy + 1) * ih), new Rect(orgX + x * sw + 1, orgY + y * sh + 1, orgX + (x + 1) * sw - 1, orgY + (y + 1) * sh - 1), mPaint);
                } else {
                    canvas.drawBitmap(bmNumbers, new Rect(ix * iw, iy * ih, (ix + 1) * iw, (iy + 1) * ih), new Rect(orgX + x * sw, orgY + y * sh, orgX + (x + 1) * sw, orgY + (y + 1) * sh), mPaint);
                }
                if (isDebug) {
                    canvas.drawText("" + number, orgX + (int)((x+0.5)*sw), orgY + (int)((y+0.5)*sh), shadowPaint);
                    canvas.drawRoundRect(new RectF(orgX + x*sw + 2, orgY + y*sh + 2, orgX + (x+1)*sw - 2, orgY + (y+1)*sh - 2), 2.0f, 2.0f, shadowPaint);
                }
            }
        if (!field.IsWon() && field.getDragging()) {
            int number = field.XY(field.getStartPoint().x, field.getStartPoint().y); //get number at pos
            if (number == 0) { //because origin of this game is "15"...
                ix=field.getSizeX()-1;
                iy=field.getSizeY()-1;
            } else {
                ix = (number - 1) % (field.getSizeX());
                iy = (number - 1) / field.getSizeX();
            }
            canvas.drawBitmap(bmNumbers, new Rect(ix * iw, iy * ih, (ix+1) * iw, (iy+1) * ih),
                    new Rect(-dtx+field.getMovePoint().x, -dty+field.getMovePoint().y, -dtx+field.getMovePoint().x+sw, -dty+field.getMovePoint().y+sh), mPaint);
        }
        if (canvas.getWidth() <= canvas.getHeight()) {
            //draw level name:
            canvas.drawText(field.getName(), canvas.getWidth() / 2, orgY / 2, shadowPaint);
            //draw step count:
            canvas.drawText("" + field.getScore() + " / " + field.getLevel().LoScore, canvas.getWidth() / 2, canvas.getHeight() - orgY / 2, shadowPaint);
        } else {
            //draw level name:
            canvas.drawText(field.getName(), (canvas.getWidth() - sw*field.getSizeX())/4, canvas.getHeight()/2, shadowPaint);
            //draw step count:
            canvas.drawText("" + field.getScore() + " / " + field.getLevel().LoScore, canvas.getWidth() - (canvas.getWidth() - sw*field.getSizeX())/4, canvas.getHeight()/2, shadowPaint);
        }
    }

    private void calcSizes(Canvas canvas) {
        if (canvas == null) return;
        iw = bmNumbers.getWidth()/field.getSizeX(); //source item width
        ih = bmNumbers.getHeight()/field.getSizeY(); //source item height
        sw = (int)(canvas.getWidth()/(field.getSizeX()+0.5)); //screen item width
        sh = (int)(canvas.getHeight()/(field.getSizeY()+0.5)); //screen item height
        sw = (sw < sh)?sw:sh;
        sh = sw;
        orgX = (canvas.getWidth() - sw*field.getSizeX())/2;
        orgY = (canvas.getHeight() - sh*field.getSizeY())/2;
    }
}
