package hollowness.necro.fifteen;

import android.content.Context;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kurdyukov_ae on 31.03.2017.
 */

public class GameDesc {
    String name;
    String path;

    public GameDesc(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Map<String, GameDesc> readGames (Context context, InputStream in) throws IOException {
        Map<String, GameDesc> games = new HashMap<>();

        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                GameDesc game = readGame(context, reader);
                games.put(game.path, game);
            }
            reader.endArray();
        } finally {
            reader.close();
        }
        return games;
    }

    public static GameDesc readGame (Context context, JsonReader reader) throws IOException {
        GameDesc game = new GameDesc("", "");

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                game.name = reader.nextString();
                String strValue = context.getString(
                        context.getResources().getIdentifier(game.name, "string", context.getPackageName()));
                game.name = strValue;
            } else if (name.equals("path")) { //can be empty
                game.path = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return game;
    }
}
