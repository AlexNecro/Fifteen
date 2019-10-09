package hollowness.necro.fifteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MenuActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //MobileAds.initialize(getApplicationContext(), getString(R.string.admob_id));
        GameData.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit: DoExit(); break;
            case R.id.about: startActivity(new Intent(this, AboutActivity.class)); break;
            case R.id.settings: startActivity(new Intent(this, SettingsActivity.class)); break;
            case R.id.scores: startActivity(new Intent(this, ScoresActivity.class)); break;
            case R.id.game_puzzles_random: startGameLevelSelection("puzzles.random"); break;
            case R.id.game_puzzles: startGameLevelSelection("puzzles.challenge"); break;
            case R.id.game_fifteen_random: startGameLevelSelection("fifteen.random"); break;
            case R.id.game_fifteen: startGameLevelSelection("fifteen.challenge"); break;
        }
    }

    private void DoExit() {
        finish();
    }

    protected void startGameLevelSelection(String game) {
        Intent intent = new Intent(this, LevelSelectionActivity.class);
        intent.putExtra("game", game);
        startActivity(intent);
    }
}
