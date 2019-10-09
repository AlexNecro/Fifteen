package hollowness.necro.fifteen;

import android.util.Log;

import java.util.Random;

/**
 * Created by kurdyukov_ae on 01.02.2017.
 */


public class GameFieldSwap extends GameField {

    GameFieldSwap(LevelDesc level) {
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
        Slide(startPoint.x, startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
        isDragging = false;
        return true;
    }

    private boolean Slide(int x, int y, int dx, int dy) { //swaps two nearest tokens
        if (dx==dy) return false;
        if (x >= sizeX || y >= sizeY || x<0 || y<0) return false;
        if (Math.abs(dx) > Math.abs(dy)) {
            dy = 0;
        } else {
            dx = 0;
        }
        if (Math.abs(dx) > 1 || Math.abs(dy) > 1) return false;
        dx = sign(dx);
        dy = sign(dy);

        if (x+dx <0 || y+dy < 0 || x+dx >= sizeX || y+dy >= sizeY) return false;
        return Swap((byte) (sizeX * y + x), (byte) (sizeX * (y + dy) + x + dx));
    }

    protected void Shuffle() {
        Random random = new Random();
        int index, zeroX,zeroY,nextX,nextY;
        int[] possibilities = new int[4];
        int possibilitiesCount, possibilityIndex;

        //for (int sortCount = 0; sortCount<field.length; sortCount++)
            for (int i = 0; i < field.length; i++) {
                if (i%2==1)
                    index = field.length-i/2-1;
                else
                    index = i/2;

                zeroX = index%sizeX;
                zeroY = index/sizeX;
                //generally we can move x-1, x+1, y-1, y+1, except field edges
                //but we don't want to move tile to position in which it settled at very beginning
                possibilitiesCount = 0;
                if (zeroX>0 && (zeroX-1 + zeroY*sizeX) != initialIndexOf(field[index]))
                    possibilities[possibilitiesCount++] = -1;
                if (zeroX<sizeX-1 && (zeroX+1 + zeroY*sizeX) != initialIndexOf(field[index]))
                    possibilities[possibilitiesCount++] = +1;
                if (zeroY>0 && (zeroX + (zeroY-1)*sizeX) != initialIndexOf(field[index]))
                    possibilities[possibilitiesCount++] = -2;
                if (zeroY<sizeY-1 && (zeroX + (zeroY+1)*sizeX) != initialIndexOf(field[index]))
                    possibilities[possibilitiesCount++] = +2;
                possibilityIndex = random.nextInt(possibilitiesCount);
                nextX = possibilities[possibilityIndex]==-1?-1:(possibilities[possibilityIndex]==1?1:0);
                nextY = possibilities[possibilityIndex]==-2?-1:(possibilities[possibilityIndex]==2?1:0);

                Log.d("Swap.Shuffle","{"+field.length+"} ["+i+"] ("+possibilitiesCount+") ("+zeroX+","+zeroY+") <-> ("+nextX+","+nextY+"): "+Slide(zeroX, zeroY, nextX, nextY));
            }
        Score = 0;
        Log.d("Swap.Shuffle","Done");
    }

}
