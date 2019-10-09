package hollowness.necro.fifteen;

import android.util.Log;

import java.util.Random;

/**
 * Created by kurdyukov_ae on 01.02.2017.
 */


public class GameFieldFifteen extends GameField {

    GameFieldFifteen(LevelDesc level) {
        Init(level);
    }

    Boolean OnTouchDown(int x, int y) {
        SlideClassic(x,y);
        return true;
    }

    private boolean SlideClassic(int x, int y) {
        if (x >= sizeX || y >= sizeY || x<0 || y<0) return false;
        if (XY(x - 1, y) == 0) {
            return Swap( (sizeX * y + x), (sizeX * y + (x - 1)));
        } else if (XY(x + 1, y) == 0) {
            return Swap((sizeX * y + x),  (sizeX * y + (x + 1)));
        } else if (XY(x, y - 1) == 0) {
            return Swap( (sizeX * y + x),  (sizeX * (y - 1) + x));
        } else if (XY(x, y + 1) == 0) {
            return Swap( (sizeX * y + x),  (sizeX * (y + 1) + x));
        }
        return false;
    }

    protected void Shuffle() {
        Random random = new Random();
        /*
            1. find 0
            2. get random tile (h or v) near 0, but not tile that was 0 prev turn!
            3. slide it
            4. repeat (count?)
         */
        int zeroX, zeroY, nextX, nextY;
        int[] possibilities = new int[4];
        int possibilitiesCount, possibilityIndex;
        int zeroIndex = ZeroIndex();

        for (int sortCount = 0; sortCount<field.length; sortCount++)
            for (int i = 0; i< field.length; i++) {
                zeroX = getX(zeroIndex);
                zeroY = getY(zeroIndex);
                //generally we can move x-1, x+1, y-1, y+1, except field edges
                possibilitiesCount = 0;
                if (zeroX>0)
                    possibilities[possibilitiesCount++] = zeroX-1 + zeroY*sizeX;
                if (zeroX<sizeX-1)
                    possibilities[possibilitiesCount++] = zeroX+1 + zeroY*sizeX;
                if (zeroY>0)
                    possibilities[possibilitiesCount++] = zeroX + (zeroY-1)*sizeX;
                if (zeroY<sizeY-1)
                    possibilities[possibilitiesCount++] = zeroX + (zeroY+1)*sizeX;
                possibilityIndex = random.nextInt(possibilitiesCount);
                zeroIndex = possibilities[possibilityIndex];
                nextX = getX(zeroIndex);
                nextY = getY(zeroIndex);
                Log.d("Fifteen.Shuffle","{"+field.length+"} ("+nextX+","+nextY+") <-> ("+zeroX+","+zeroY+")");
                SlideClassic(nextX, nextY);
            }
        Log.d("Fifteen.Shuffle","Done");
        Score = 0;
    }

    int ZeroIndex() {
        for (int i=field.length-1;i>=0;i--)
            if (field[i]==0) return i;
        return -1;
    }
}
