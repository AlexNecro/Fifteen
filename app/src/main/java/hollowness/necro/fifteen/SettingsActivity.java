package hollowness.necro.fifteen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    //SharedPreferences settings;
    GameData settings;
    Boolean sfx;
    Boolean bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = GameData.getInstance(null);
        sfx = settings.getSettingsBoolean(getString(R.string.opt_sfx), true);
        bgm = settings.getSettingsBoolean(getString(R.string.opt_bgm), true);
        ((TextView)findViewById(R.id.opt_sfx)).setText(getString(R.string.settings_sound) + ": " + (sfx ? getString(R.string.settings_on) : getString(R.string.settings_off)));
        ((TextView)findViewById(R.id.opt_bgm)).setText(getString(R.string.settings_music) + ": " + (bgm ? getString(R.string.settings_on) : getString(R.string.settings_off)));
        if (!BuildConfig.DEBUG) {
            findViewById(R.id.buttonDebug).setVisibility(View.GONE);
            findViewById(R.id.buttonReset).setVisibility(View.GONE);
            findViewById(R.id.buttonRevealLevels).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        settings.setSettingsBoolean(getString(R.string.opt_sfx), sfx);
        settings.setSettingsBoolean(getString(R.string.opt_bgm), bgm);
        //SharedPreferences.Editor editor = settings.edit();
        //editor.putBoolean(getString(R.string.opt_sfx), sfx);
        //editor.putBoolean(getString(R.string.opt_bgm), bgm);
        //editor.apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.opt_sfx:
                sfx = !sfx;
                ((TextView)findViewById(R.id.opt_sfx)).setText(getString(R.string.settings_sound) + ": " + (sfx ? getString(R.string.settings_on) : getString(R.string.settings_off)));
                break;
            case R.id.opt_bgm:
                bgm = !bgm;
                ((TextView)findViewById(R.id.opt_bgm)).setText(getString(R.string.settings_music) + ": " + (bgm ? getString(R.string.settings_on) : getString(R.string.settings_off)));
                break;
        }
    }

    public void onResetClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder
                .setTitle(getString(R.string.settings_resetalert_title))
                .setMessage(getString(R.string.settings_resetalert_question))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        GameData.getInstance(null).ResetData();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onRevealClick(View view) {
        GameData.getInstance(null).TemporaryRevealAllLevels();
    }

    public void onDebugClick(View view) {
        GameData.getInstance(null).setDebug(true);
    }
}
