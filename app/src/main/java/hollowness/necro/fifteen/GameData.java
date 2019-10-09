package hollowness.necro.fifteen;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kurdyukov_ae on 09.03.2017.
 */

/*
    pictures are from
    https://pixabay.com/
    licensed CC0 Public Domain
    with mark "free for commercial use"
* */
//settings and score reader/writer
class GameData {
    private Context context = null;
    private SharedPreferences settings;
    private SharedPreferences scores;
    private ArrayList<LevelDesc> levels;
    private String basepath;
    private Boolean debug = false;
    private SoundPool soundPool;
    static int SOUND_SWAP;
    //static int SOUND_ENDDRAG;
    static int SOUND_WIN;
    private Boolean sfxOn;
    private Boolean bgmOn;
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private Bitmap levelBitmap;
    private String levelBitmapPath;
    ScoresDBOpenHelper ScoresDBHelper;
    private Map<String, GameDesc> games;

    static class ScoreItem {
        int Id;
        String GameId;
        String LevelId;
        String Name;
        int Score;
        Date Time;

        ScoreItem(int id, String gameId, String levelId, int score, Date time, String name) {
            Id = id;
            GameId = gameId;
            LevelId = levelId;
            Score = score;
            Time = time;
            Name = name;
        }

        @Override
        public String toString() {
            return Name;
        }
    }

    private GameData() {
    }

    private static GameData ourInstance = new GameData();

    static GameData getInstance(Context context) {
        if (context != null) {
            ourInstance.Init(context.getApplicationContext());
        }
        return ourInstance;
    }

    Boolean getSettingsBoolean(String name, Boolean defval) {
        return settings.getBoolean(name, defval);
    }

    void setSettingsBoolean(String name, Boolean val) {
        if (name == context.getString(R.string.opt_sfx)) {
            sfxOn = val;
        }
        if (name == context.getString(R.string.opt_bgm)) {
            bgmOn = val;
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name, val);
        editor.apply();
    }

    private int getLevelScore(LevelDesc level) {
        int score = scores.getInt("Score"+level.LevelId, 0);
        //Log.d(getClass().getSimpleName(),"get Score"+level.LevelId+" = "+score);
        return score;
    }

    void setLevelScore(LevelDesc level, int score) {
        level.Score = score;
        ScoresDBHelper.insertScore(basepath,level,score);
        int hiScore = level.HiScore;
        int loScore = level.LoScore;
        if (loScore == 0) loScore = Integer.MAX_VALUE;
        //Log.d(getClass().getSimpleName(),"set Score"+level.LevelId+" = "+score);
        SharedPreferences.Editor editor = scores.edit();
        editor.putInt("Score"+level.LevelId, score);
        if (hiScore < score) {
            level.HiScore = score;
            editor.putInt("HiScore"+level.LevelId, score);
            //Log.d(getClass().getSimpleName(),"set HiScore"+level.LevelId+" = "+score);
        }
        if (loScore > score) {
            level.LoScore = score;
            editor.putInt("LoScore"+level.LevelId, score);
            //Log.d(getClass().getSimpleName(),"set LoScore"+level.LevelId+" = "+score);
        }
        editor.apply();
    }

    private Boolean getLevelOpened(LevelDesc level) {
        Boolean opened = scores.getBoolean("Opened"+level.LevelId, level.Opened);
        //Log.d(this.getClass().getSimpleName(), "get Opened"+level.LevelId+" = "+opened);
        return opened;
    }

    void  setLevelOpened(LevelDesc level, Boolean opened) {
        level.Opened = opened;
        SharedPreferences.Editor editor = scores.edit();
        editor.putBoolean("Opened"+level.LevelId, opened);
        //Log.d(this.getClass().getSimpleName(), "set Opened"+level.LevelId+" = "+opened);
        editor.apply();
    }

    private int getLevelHiScore(LevelDesc level) {
        int score = scores.getInt("HiScore"+level.LevelId, 0);
        //Log.d(getClass().getSimpleName(),"get HiScore"+level.LevelId+" = "+score);
        return score;
    }

    private int getLevelLoScore(LevelDesc level) {
        int score = scores.getInt("LoScore"+level.LevelId, 0);
        //Log.d(getClass().getSimpleName(),"get LoScore"+level.LevelId+" = "+score);
        return score;
    }

    @SuppressWarnings("deprecation")
    private void Init(Context context) {
        this.context = context;
        settings = this.context.getSharedPreferences(this.context.getString(R.string.opt), MODE_PRIVATE);
        scores = this.context.getSharedPreferences(this.context.getString(R.string.scorefile), MODE_PRIVATE);
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        SOUND_SWAP = soundPool.load(this.context, R.raw.swap, 1);
        //SOUND_ENDDRAG = soundPool.load(this.context, R.raw.enddrag, 1);
        SOUND_WIN = soundPool.load(this.context, R.raw.win, 1);
        sfxOn = getSettingsBoolean(context.getString(R.string.opt_sfx), true);
        bgmOn = getSettingsBoolean(context.getString(R.string.opt_bgm), true);
        try {
            games = GameDesc.readGames(context, context.getAssets().open("game/games.json"));
        } catch (Exception e) {
        }
        ScoresDBHelper = new ScoresDBOpenHelper(context, "scores.sqlite", null,1);
    }

    void PlaySound(int id) {
        if (soundPool==null || !sfxOn) return;
        soundPool.play(id,1,1,0,0,1);
    }

    GameDesc getGameByPath(String path) {
        return games.get(path);
    }

    ArrayList<LevelDesc> getLevels(String basepath) throws Exception{
        if (!basepath.isEmpty() && !basepath.equals(this.basepath)) {
            levels = LevelDesc.readLevels(context, basepath, context.getAssets().open("game/"+basepath + ".levels.json"));
            this.basepath = basepath;
            LevelDesc level;
            for (int i=0; i<levels.size(); i++) {
                level = levels.get(i);
                level.Score = getLevelScore(level);
                level.HiScore = getLevelHiScore(level);
                level.LoScore = getLevelLoScore(level);
                level.Opened = getLevelOpened(level);
                try {
                    if (!levels.get(i).ImagePath.isEmpty()) {
                        level.Bitmap = BitmapFactory.decodeStream(context.getAssets().open("thumb/" + levels.get(i).ImagePath));
                    } else { //random image
                        level.Bitmap = BitmapFactory.decodeStream(context.getAssets().open("thumb/" + levels.get(i).Rules.getDefaultImagePath()));
                    }
                } catch (Exception e) {

                }
            }
        } else { //update scores
        }
        return levels;
    }

    void ResetData() {
        SharedPreferences.Editor editor = scores.edit();
        editor.clear();
        editor.commit();
    }

    void TemporaryRevealAllLevels() {
        for (int i=0; i<levels.size(); i++) {
            levels.get(i).Opened = true;
        }
    }

    Boolean getDebug() {
        return debug;
    }

    void setDebug(Boolean debug) {
        this.debug = debug;
    }

    boolean DumpLevel(GameField field) {
        String filename = ""+(new Date()).getTime()+".json";
        try {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE), "UTF-8"));
            writer.setIndent("\t");
            writer.beginObject();
            writer.name("id").value(field.getLevel().LevelId);
            writer.name("name").value(field.getLevel().Name);
            writer.name("path").value(field.ImagePath);
            writer.name("rules").value(field.getLevel().Rules.toString());
            writer.name("field");
            writer.beginArray();
            for (int value : field.field) {
                writer.value(value);
            }
            writer.endArray();
            writer.endObject();
            writer.close();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    Bitmap loadLevelBitmap(GameField field) {
        LevelDesc level = field.getLevel();
        String ImagePath = field.ImagePath.isEmpty()?level.ImagePath:field.ImagePath;
        if (ImagePath.isEmpty()) {
            //TODO: random image selection code
            try {
                String[] files = context.getAssets().list("game");
                int i;
                Random random = new Random();
                while (ImagePath.isEmpty()) {
                    i = random.nextInt(files.length);
                    if (files[i].contains(".png") || files[i].contains(".jp")) {
                        ImagePath = files[i];
                        files = null;
                    }
                }
            } catch (IOException e) {
                Log.d(getClass().getSimpleName(), "loadLevelBitmap: can't enum level bitmaps: fatal");
                return levelBitmap;
            }
        }
        field.ImagePath = ImagePath;
        Log.d(getClass().getSimpleName(), "loadLevelBitmap: ImagePath =  " + ImagePath);
        if (levelBitmap!=null && levelBitmapPath.equals(ImagePath)) {
            Log.d(getClass().getSimpleName(), "loadLevelBitmap: already loaded: " + levelBitmapPath);
            return levelBitmap;
        }
        options.inMutable = true;
        levelBitmapPath = ImagePath;
        if (levelBitmap!=null) {
            options.inBitmap = levelBitmap;
            Log.d(getClass().getSimpleName(), "loadLevelBitmap: first run: "+levelBitmapPath);
        }
        try {
            levelBitmap = BitmapFactory.decodeStream(context.getAssets().open("game/" + levelBitmapPath), null, options);
            Log.d(getClass().getSimpleName(), "loadLevelBitmap: loaded: "+levelBitmapPath);
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "loadLevelBitmap: exception: "+levelBitmapPath+" ["+e.getMessage()+"]");
            levelBitmap.recycle();
            levelBitmap = null;
            options.inBitmap = null;
            try { //load into new bitmap
                levelBitmap = BitmapFactory.decodeStream(context.getAssets().open("game/" + levelBitmapPath), null, options);
            } catch (Exception e2) {
                Log.d(getClass().getSimpleName(), "loadLevelBitmap: exception2: "+levelBitmapPath+" ["+e.getMessage()+"]");
            }
        }
        return levelBitmap;
    }
}
