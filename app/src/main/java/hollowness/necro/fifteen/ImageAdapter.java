package hollowness.necro.fifteen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kurdyukov_ae on 06.02.2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    ArrayList<LevelDesc> levels;//(levelname, bitmap) pairs
    String basePath; //directory

    public ImageAdapter(Context c, ArrayList<LevelDesc> levels) throws Exception{
        context = c;
        this.levels = levels;
    }

    public int getCount() {
        return levels.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = inflater.inflate(R.layout.level_thumb, null);
        } else {
            grid = convertView;
        }
        LevelDesc level = levels.get(position);
        grid.setEnabled(level.Opened);
        for ( int i = 0 ; i < ((ViewGroup)grid).getChildCount() ; i++ ) {
            ((ViewGroup)grid).getChildAt(i).setEnabled(level.Opened);
        }
        grid.findViewById(R.id.grid_lock).setVisibility(level.Opened?View.INVISIBLE:View.VISIBLE);
        grid.findViewById(R.id.grid_swap).setVisibility(level.Rules.isSwap()?View.VISIBLE:View.INVISIBLE);
        grid.findViewById(R.id.grid_revolve).setVisibility(level.Rules.isRevolve()?View.VISIBLE:View.INVISIBLE);
        grid.findViewById(R.id.grid_puzzle).setVisibility(level.Rules.isPuzzle()?View.VISIBLE:View.INVISIBLE);
        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
        textView.setText(""+level.Rules.getSizeX()+"x"+level.Rules.getSizeY()+" ("+level.TopScore+")");
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap(level.Bitmap);
        RatingBar score = (RatingBar) grid.findViewById(R.id.grid_score);
        score.setMax(3);
        if (level.TopScore > 0 && level.Opened) {
            score.setVisibility(View.VISIBLE);
            if (level.LoScore > 0) {
                score.setRating(score.getMax() * level.TopScore / level.LoScore);
            } else {
                score.setRating(0.0f);
            }
        } else {//no top score data
            score.setVisibility(View.INVISIBLE);
        }
        return grid;
    }
}