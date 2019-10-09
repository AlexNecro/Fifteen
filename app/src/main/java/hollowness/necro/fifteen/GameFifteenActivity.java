package hollowness.necro.fifteen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RatingBar;

public class GameFifteenActivity extends BaseActivity implements View.OnTouchListener, SurfaceHolder.Callback{
    private GameFifteenThread mMyThread;
    protected GameField field;
    protected int slidethreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamefifteen);
        try {
            field = GameField.CreateGameField(GameData.getInstance(null).getLevels("").get((int)getIntent().getSerializableExtra("level")));
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(),"onCreate() failed: "+e.getMessage());
        }
        SurfaceView view = (SurfaceView)findViewById(R.id.gameViewport);
        view.setOnTouchListener(this);
        slidethreshold = 30; //some not too big number
        view.getHolder().addCallback(this);
        findViewById(R.id.gameWinButtons).setVisibility(View.GONE);
        findViewById(R.id.gameWinAll).setVisibility(View.GONE);
        if (!GameData.getInstance(null).getDebug())
            findViewById(R.id.debug).setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) { //change screen size
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(getClass().getSimpleName(),"onPause()");
        //if (mMyThread!=null)
            //mMyThread.Stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(),"onResume()");
        //if (mMyThread!=null)
            //mMyThread.Init(field);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMyThread = new GameFifteenThread(this, holder);
        mMyThread.Init(field);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mMyThread.Stop();

        while(retry) {
            try {
                mMyThread.join();
                retry = false;
            }
            catch (InterruptedException e) {
            }
        }
        mMyThread = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (field == null) return true;
        if (field.IsWon()) return true;
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mMyThread.dtx = mMyThread.GetTileX((int)event.getX());
                    mMyThread.dty = mMyThread.GetTileY((int)event.getY());
                    field.OnTouchDown(mMyThread.GetWorldX((int)event.getX()), mMyThread.GetWorldY((int)event.getY()));
                    field.OnMove((int)event.getX(), (int)event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    GameData.getInstance(null).PlaySound(GameData.SOUND_SWAP);
                    field.OnTouchUp(mMyThread.GetWorldX(x), mMyThread.GetWorldY(y));
                    break;
                case MotionEvent.ACTION_MOVE:
                    field.OnMove((int)event.getX(), (int)event.getY());
                    break;
            }
        if (field.IsWon()) onGameWin();
        return true;
    }

    public void onGameWin() {
        try {
            if (GameData.getInstance(null).getLevels("").size() > field.getLevel().Index + 1) {
                RatingBar score = (RatingBar) findViewById(R.id.grid_score);
                score.setMax(3);
                score.setRating(score.getMax() * field.getLevel().TopScore / field.getScore());

                findViewById(R.id.gameWinButtons).setVisibility(View.VISIBLE);
                findViewById(R.id.gameWinButtons).bringToFront();
            } else { //last level
                findViewById(R.id.gameWinAll).setVisibility(View.VISIBLE);
                findViewById(R.id.gameWinAll).bringToFront();
            }
            GameData.getInstance(null).PlaySound(GameData.SOUND_WIN);
        } catch (Exception e) {

        }
    }

    public void onReplayClick(View view) {
        findViewById(R.id.gameWinButtons).setVisibility(View.GONE);
        findViewById(R.id.gameWinAll).setVisibility(View.GONE);
        field.Init(null);
    }

    public void onNextLevelClick(View view) {
        findViewById(R.id.gameWinButtons).setVisibility(View.GONE);
        findViewById(R.id.gameWinButtons).bringToFront();
        try {
            LevelDesc level = GameData.getInstance(null).getLevels("").get(field.getLevel().Index+1);
            if (level.Opened) {
                field = GameField.CreateGameField(level);
                mMyThread.Init(field);
            }
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(),"onNextLevelClick() failed: "+e.getMessage());
        }
    }

    public void onSaveClick(View view) {
        GameData.getInstance(null).DumpLevel(field);
    }
}
