package hollowness.necro.fifteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;

public class SplashActivity extends BaseActivity implements View.OnClickListener , SurfaceHolder.Callback{
    private SplashThread mMyThread; //наш поток прорисовки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MenuActivity.class));
        finish();
//        SurfaceView view = new SurfaceView(this);
//        setContentView(view);
//        view.setOnClickListener(this);
//        view.getHolder().addCallback(this);
    }

    @Override
    public void onClick(View v) {
        mMyThread.setRunning(false);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMyThread = new SplashThread(holder, this);
        mMyThread.setRunning(true);
        mMyThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mMyThread.setRunning(false);

        while(retry) {
            try {
                mMyThread.join();
                retry = false;
            }
            catch (InterruptedException e) {
            }
        }
    }
}
