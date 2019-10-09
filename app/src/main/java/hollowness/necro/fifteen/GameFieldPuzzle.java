package hollowness.necro.fifteen;

import android.util.Log;

import java.util.Random;

/**
 * Created by kurdyukov_ae on 01.02.2017.
 */


public class GameFieldPuzzle extends GameField {

    GameFieldPuzzle(LevelDesc level) {
        Init(level);
    }

    //user interaction (start drag):
    Boolean OnTouchDown(int x, int y) {
        startPoint.x = x;
        startPoint.y = y;
        isDragging = true;
        return true;
    }

    //user interaction (end drag):
    Boolean OnTouchUp(int x, int y) {
        endPoint.x = x;
        endPoint.y = y;
        Move(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        isDragging = false;
        return true;
    }

    private void Move(int x1, int y1, int x2, int y2) { //swaps any two tokens
        if (x1==x2 && y1==y2) return;
        if (x1 >= sizeX || y1 >= sizeY || x1<0 || y1<0) return;
        if (x2 >= sizeX || y2 >= sizeY || x2<0 || y2<0) return;
        Swap((sizeX * y1 + x1), (sizeX * y2 + x2));
    }

    protected void Shuffle() {
        Random random = new Random();
        int half = field.length/2;
        int odd = field.length - half*2;
        int next;
        for (int i = 0; i < half+odd; i++) {
            if (half-i == 0)
                next = field.length-1;
            else
                next = half+random.nextInt(half-i)+i;
            Swap(i, next);
            Log.d("Puzzle.Shuffle","{"+field.length+"} "+i+" <-> "+next);
        }
        Score = 0;
        Log.d("Puzzle.Shuffle","Done");
    }

}
