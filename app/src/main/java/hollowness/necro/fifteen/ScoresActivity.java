package hollowness.necro.fifteen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kurdyukov_ae on 09.03.2017.
 */

public class ScoresActivity extends Activity implements AdapterView.OnItemSelectedListener {
    List<GameDesc> values;

    static class ScoreListAdapter extends ArrayAdapter<GameData.ScoreItem> {
        private final Context context;
        private final List<GameData.ScoreItem> values;

        public ScoreListAdapter(Context context, List<GameData.ScoreItem> values) {
            super(context, R.layout.score_row, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.score_row, parent, false);
            } else {
                rowView = convertView;
            }
            TextView textLevel = (TextView) rowView.findViewById(R.id.level);
            TextView textScore = (TextView) rowView.findViewById(R.id.score);
            TextView textPosition = (TextView) rowView.findViewById(R.id.position);
            //TextView textTime = (TextView) rowView.findViewById(R.id.time);
            textLevel.setText(values.get(position).Name);
            textScore.setText(""+values.get(position).Score);
            textPosition.setText(""+(position+1));
            //java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
            //String s = dateFormat.format(values.get(position).Time);
            //textTime.setText(s);
            return rowView;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = (ListView) findViewById(R.id.scoretable);
        List<GameData.ScoreItem> scores = GameData.getInstance(null).ScoresDBHelper
                .getTopScores(values.get(position).path, null ,0,10);
        ScoreListAdapter adapter = new ScoreListAdapter(this, scores);
        listView.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        Spinner spinner = (Spinner) findViewById(R.id.levels);
        values = GameData.getInstance(null).ScoresDBHelper.getGames();
        ArrayAdapter<GameDesc> adapter = new ArrayAdapter<>(this,
                R.layout.games_row, R.id.level, values);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
