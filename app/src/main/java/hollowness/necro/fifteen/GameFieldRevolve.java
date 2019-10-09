package hollowness.necro.fifteen;

import java.util.Random;

/**
 * Created by kurdyukov_ae on 01.02.2017.
 */


public class GameFieldRevolve extends GameField {

    GameFieldRevolve(LevelDesc level) {
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
        Revolve(startPoint.x, startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
        isDragging = false;
        return true;
    }

    private void Revolve(int x, int y, int dx, int dy) { //rolls row or column
        if (dx==dy) return;
        if (x >= sizeX || y >= sizeY || x<0 || y<0) return;
        if (Math.abs(dx) > Math.abs(dy)) {
            dy = 0;
        } else {
            dx = 0;
        }
        if (dx!=0) {
            for (int i = 0; i< Math.abs(dx);i++) {
                RevolveRow(y, sign(dx));
                Commit();
            }
        } else {
            for (int i = 0; i< Math.abs(dy);i++) {
                RevolveColumn(x, sign(dy));
                Commit();
            }
        }
    }

    private void RevolveColumn(int x, int direction) {
        if (direction<0) {//move upwards
            int t = field[0+x];//store top cell (x,0)
            for (int y = 1; y < sizeY; y++) {
                field[sizeX * (y - 1) + x] = field[sizeX * y + x];
            }
            field[sizeX * (sizeY - 1) + x] = t;
        } else {//move downwards
            int t = field[sizeX * (sizeY - 1) + x];//store bottom cell
            for (int y = sizeY-1; y > 0; y--) {
                field[sizeX * y + x] = field[sizeX * (y-1) + x];
            }
            field[0+x] = t;
        }
    }

    private void RevolveRow(int y, int direction) {
        if (direction<0) {//move upwards
            int t = field[sizeX * y];//store top cell (x,0)
            for (int x = 1; x < sizeX; x++) {
                field[sizeX * y + (x - 1)] = field[sizeX * y + x];
            }
            field[sizeX * y + sizeX-1] = t;
        } else {//move downwards
            int t = field[sizeX * y + sizeX-1];//store bottom cell
            for (int x = sizeX-1; x > 0; x--) {
                field[sizeX * y + x] = field[sizeX * y + (x - 1)];
            }
            field[sizeX * y] = t;
        }
    }

    protected void Shuffle() {
        Random random = new Random();
        /*
            we just select row or column
            and move it N times in M direction
         */
        int movesCount;

        for (int i=0; i<field.length;i++) {
            if (i%2==0) { //column
                movesCount = random.nextInt(sizeY-1)+1;
                if (random.nextInt(2)==0)
                    for (int m=0;m<movesCount;m++)
                        RevolveColumn(random.nextInt(sizeX), -1);
                else
                    for (int m=0;m<movesCount;m++)
                        RevolveColumn(random.nextInt(sizeX), 1);
            } else {
                movesCount = random.nextInt(sizeX-1)+1;
                if (random.nextInt(2) == 0)
                    for (int m=0;m<movesCount;m++)
                        RevolveRow(random.nextInt(sizeY), -1);
                else
                    for (int m=0;m<movesCount;m++)
                        RevolveRow(random.nextInt(sizeY), 1);
            }
        }
        Score = 0;
    }

}
