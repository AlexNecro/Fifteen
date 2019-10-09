package hollowness.necro.fifteen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kurdyukov_ae on 29.03.2017.
 */

public class ScoresDBOpenHelper extends SQLiteOpenHelper {
    private static final int Version = 1;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Scores\n"+
                " (\n"+
                "id INTEGER PRIMARY KEY,\n"+
                "GameName TEXT NOT NULL,\n"+
                "LevelId TEXT NOT NULL,\n"+
                "Time TEXT,\n"+
                "Name TEXT,\n"+
                "Score INTEGER NOT NULL\n"+
        ");");
    }

    void insertScore(String gameName, LevelDesc level, int score) {
        //TODO: maybe insert score only if it is higher than other scores for level?
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("GameName", gameName);
        values.put("LevelId", level.LevelId);
        values.put("Name", level.Name);
        values.put("Time", System.currentTimeMillis());
        values.put("Score", score);
        db.insert("Scores", null, values);
        db.close();
    }

    int getScoreCount(String gameName, String levelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM Scores WHERE GameName = ? AND LevelId = ?", new String[] {gameName, levelId});
        int res = 0;
        if (cur.moveToFirst()) {
            res = cur.getInt(0);
        }
        cur.close();
        return res;
    }

    List<GameDesc> getGames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT GameName FROM Scores", null);
        List<GameDesc> games = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                games.add(GameData.getInstance(null).getGameByPath(cur.getString(0)));
            } while (cur.moveToNext());
        }
        cur.close();
        return games;
    }

    List<GameData.ScoreItem> getTopScores(String gameName, String levelId, int from, int count) {//get top _count scores from score _from
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur;
        if (levelId!=null)
            cur = db.rawQuery("SELECT Id, GameName, LevelId, Score, Time, Name FROM Scores WHERE GameName = ? AND LevelId = ? ORDER BY Score LIMIT ? OFFSET ?", new String[] {gameName, levelId, ""+count, ""+from});
        else
            cur = db.rawQuery("SELECT Id, GameName, LevelId, Score, Time, Name FROM Scores\n" +
                    "INNER JOIN (\n" +
                    "SELECT\n" +
                    "    Scores.LevelId AS _LevelId,\n" +
                    "\tScores.Score AS _Score,\n" +
                    "\tMAX(Scores.Time) AS _Time\n" +
                    "FROM Scores\n" +
                    "INNER JOIN\n" +
                    "(SELECT LevelId, MIN(Score) AS MinScore FROM Scores WHERE GameName = ? GROUP BY LevelId) AS Group1\n" +
                    "ON Group1.LevelId=Scores.LevelId AND Group1.MinScore=Scores.Score\n" +
                    "WHERE GameName = ?\n" +
                    "GROUP BY Scores.LevelId\n" +
                    ") AS Group2 ON LevelId=_LevelId AND Time=_Time AND Score=_Score WHERE GameName = ? ORDER BY Score LIMIT ? OFFSET ?", new String[] {gameName, gameName, gameName, ""+count, ""+from});
        List<GameData.ScoreItem> scores = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                GameData.ScoreItem score = new GameData.ScoreItem(cur.getInt(0),cur.getString(1),cur.getString(2),cur.getInt(3),new Date(cur.getLong(4)), cur.getString(5));
                scores.add(score);
            } while (cur.moveToNext());
        }
        cur.close();
        return scores;
    }


    ScoresDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
