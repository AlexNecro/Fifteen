package hollowness.necro.fifteen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;


/**
 * Created by kurdyukov_ae on 03.02.2017.
 */

public class LevelSelectionActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    ArrayList<LevelDesc> levels;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectlevel);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String basepath = intent.getStringExtra("game");
        GridView gridview = (GridView) findViewById(R.id.gridview);
        try {
            levels = GameData.getInstance(null).getLevels(basepath);
            if (levels == null) throw new Exception("getLevels("+basepath+") returned null");
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(),"onResume(), exception on getLevels()");
            if (e!=null)
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            finish();
            return;
        }
        try {
            if (levels.size() == 1 && levels.get(0).Opened) { //we have just 1 level, start immediately
                Intent newIntent = new Intent(this, GameFifteenActivity.class);
                newIntent.putExtra("level", levels.get(0).Index);
                startActivity(newIntent);
                finish();
            } else {
                gridview.setAdapter(new ImageAdapter(this, levels));
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(),"onResume(), catched exception");
            if (e!=null)
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            finish();
            return;
        }

        gridview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LevelDesc level = levels.get(position);
        if (!level.Opened) return;
        Intent intent = new Intent(this, GameFifteenActivity.class);
        intent.putExtra("level", level.Index);
        startActivity(intent);
    }
}
