package hollowness.necro.fifteen;

import android.content.Context;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by kurdyukov_ae on 06.02.2017.
 */

public class LevelDesc  implements Serializable {
    public LevelDesc(String levelId, String name, String imagePath, GameRules rules, int topScore, Boolean opened, int index, boolean isRandomImage) {
        LevelId = levelId;
        Name = name;
        ImagePath = imagePath;
        Rules = rules;
        TopScore = topScore;
        Opened = opened;
        Index = index;
        IsRandomImage = isRandomImage;
    }

    @Override
    public String toString() {
        return Name+" / "+LevelId;
    }

    Boolean isRandomField() {
        return field==null;
    }

    Boolean isRandomImage() {
        return IsRandomImage;
    }

    static ArrayList<LevelDesc> readLevels(Context context, String basepath, InputStream in) throws IOException {
        ArrayList<LevelDesc> levels = new ArrayList<>();

        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                levels.add(readLevel(context, basepath, reader, levels.size()));
            }
            reader.endArray();
        } finally {
            reader.close();
        }
        return levels;
    }

    private static LevelDesc readLevel(Context context, String basepath, JsonReader reader, int index) throws IOException {
        LevelDesc level = new LevelDesc("", "", "", GameRules.NONE, 0, false, index, true);
        String locale_postfix = Locale.getDefault().getLanguage();
        String localized_name = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                level.LevelId = reader.nextString();
            } else if (name.equals("name_"+locale_postfix)) {
                localized_name = reader.nextString();}
            else if (name.equals("name")) {
                level.Name = reader.nextString();
            } else if (name.equals("path")) { //can be empty
                level.ImagePath = reader.nextString();
                level.IsRandomImage = false;
            } else if (name.equals("rules")) {
                level.Rules = GameRules.fromString(reader.nextString());
            } else if (name.equals("topscore")) {
                level.TopScore = Integer.parseInt(reader.nextString());
            } else if (name.equals("opened")) {
                level.Opened = Integer.parseInt(reader.nextString()) == 1;
            } else if (name.equals("field")) {
                readField(level, reader);
            } else {
                reader.skipValue();
            }
        }
        if (!localized_name.equals("")) {
            level.Name = localized_name;
        }
        if (level.Name.isEmpty()) {
            level.Name = level.Rules.getFullName(context)+" ("+(index+1)+")";
        }
        if (level.LevelId.isEmpty()) {
            level.LevelId = level.ImagePath+level.Rules.getFullName(context);
        }
        reader.endObject();
        return level;
    }

    private static void readField(LevelDesc level, JsonReader reader) throws IOException {
        int field[] = new int[100];
        int i = 0;
        reader.beginArray();
        while (reader.hasNext()) {
            field[i] = reader.nextInt();
            i++;
        }
        level.field = new int[i];
        for (i=0;i<level.field.length;i++)
            level.field[i] = field[i];
        reader.endArray();
    }


    int Index;
    String LevelId;
    String Name;
    String ImagePath;
    boolean IsRandomImage;
    GameRules Rules;
    int TopScore;
    Boolean Opened;
    int Score;
    int HiScore;
    int LoScore;
    transient android.graphics.Bitmap Bitmap;
    int[] field = null;
}
