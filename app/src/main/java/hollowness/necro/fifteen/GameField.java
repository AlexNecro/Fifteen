package hollowness.necro.fifteen;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by kurdyukov_ae on 17.03.2017.
 */
public abstract class GameField {
    int[] field = null; //indexed left-to-right, top-to-bottom
    int sizeX = 0;
    int sizeY = 0;
    Boolean isWon = false;
    LevelDesc level = null;
    String ImagePath = new String();
    int Score = 0;//less is better
    Boolean isPlaying = false;
    //user interaction:
    Point startPoint = new Point();
    Point endPoint = new Point();
    Point movePoint = new Point(); //for external use, in screen coordinates
    Boolean isDragging = false;

    static GameField CreateGameField(LevelDesc levelDesc) { //factory
        if (levelDesc.Rules.isClassic()) {
            return new GameFieldFifteen(levelDesc);
        } else if (levelDesc.Rules.isRevolve()) {
            return new GameFieldRevolve(levelDesc);
        } else if (levelDesc.Rules.isSwap()) {
            return new GameFieldSwap(levelDesc);
        }
        //construct PUZZLE by default:
        return new GameFieldPuzzle(levelDesc);
    }

    Boolean getDragging() {
        return isDragging;
    }

    LevelDesc getLevel() {
        return level;
    }Point getStartPoint() {
        return startPoint;
    }

    Point getMovePoint() {
        return movePoint;
    }

    Point getEndPoint() {
        return endPoint;
    }

    //user interaction (start drag):
    Boolean OnTouchDown(int x, int y) {return true;}

    //user interaction (end drag):
    Boolean OnTouchUp(int x, int y){return true;}

    Boolean OnMove(int x, int y) {
        movePoint.x = x;
        movePoint.y = y;
        return true;
    }

    int getSizeX() {
        return sizeX;
    }

    int getSizeY() {
        return sizeY;
    }

    int XY(int x, int y) {
        if (x >= sizeX || y >= sizeY || x<0 || y<0) {
            return -1;
        }
        return field[sizeX*y + x];
    }

    int getX(int index) {
        return index%sizeX;
    }

    int getY(int index) {
        return index/sizeX;
    }

    Boolean IsWon() {
        return isWon;
    }

    int getScore() {
        return Score;
    }

    void Init(LevelDesc level) {
        if (level != null)
            this.level = level;
        isPlaying = false;
        isWon = false;
        this.sizeX = this.level.Rules.getSizeX();
        this.sizeY = this.level.Rules.getSizeY();
        if (this.level.isRandomField()) {
            field = new int[sizeX*sizeY];//do not create field if it has equal size!
            for (int i = 0; i<(sizeX*sizeY-1); i++) {
                field[i] = (i+1);
            }
            field[sizeX*sizeY-1] = 0;
            if (!GameData.getInstance(null).getDebug())
                Shuffle();
        } else { //prebuilt
            field = this.level.field.clone();
        }
        isWon = false;
        isPlaying = true;
    }

    protected abstract void Shuffle();

    protected Boolean isRandomized(int stepCount) {
        if (stepCount<=0) return true;
        for (int i=0; i<field.length-1;i++)
            if (field[i]==(i+1)) return false;
        if (field[field.length-1]==0) return false;
        return true;
    }

    GameRules getRules() {
        return level.Rules;
    }

    public String getName() {
        return level.Name;
    }

    protected boolean Swap(int i, int j){
        if (isWon) return false;
        if (i<0 || j<0 || i>= sizeX*sizeY || j >= sizeX*sizeY) return false;
        int t = field[i];
        field[i] = field[j];
        field[j] = t;
        Commit();
        return true;
    }

    protected int sign(int arg) {
        if (arg > 0) return +1;
        if (arg < 0) return -1;
        return 0;
    }

    protected int initialIndexOf(int i) { //returns index of i in initial position
        if (i==0) return field.length-1;
        return i-1;
    }

    protected Boolean Commit() { //commits game step, increases stepcount, tests win
        if (!isPlaying) return isWon;
        Score++;
        isWon = false;
        for (int i=1; i < (sizeX*sizeY-1); i++) {
            if (field[i]!=field[i-1]+1) return isWon;
        }
        isWon = true;
        isPlaying = false;
        GameData.getInstance(null).setLevelScore(level, Score);
        try {
            Log.d(this.getClass().getSimpleName(), ""+GameData.getInstance(null).getLevels("").get(level.Index+1).LevelId+" set to opened");
            GameData.getInstance(null).setLevelOpened(GameData.getInstance(null).getLevels("").get(level.Index+1), true);
        } catch (Exception e) {

        }
        return isWon;
    }
}
