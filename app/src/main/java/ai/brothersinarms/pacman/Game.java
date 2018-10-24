package ai.brothersinarms.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * This class should contain all your game logic
 */

public class Game {
    private Context context;
    private int points = 0; //how many points do we have
    private TextView pointsView;
    private Bitmap boardBitmap;
    private Bitmap pacBitmap;
    private Matrix pacMatrix;
    private Ghost[] ghosts;
    private int pacX, pacY; // current position of pacman
    private int dirX, dirY; // current direction of pacman
    private int nextDirX, nextDirY; // next direction of pacman
    private GameView gameView;
    private int widthOffset, heightOffset; // distance from screen edge to game map
    private int tileSize;
    private int pacSize;
    private int pacOffset;
    private char[][]board; // the logical game board
    private int w, h; // height and width of the logical game board (# of tiles)
    private int dotCount;
    private boolean active;
    private static Random random = new Random();

    public int getHeight() { return h; }
    public int getPoints() { return points; }
    Bitmap getPacBitmap() { return pacBitmap; }
    Bitmap getBoardBitmap(){ return boardBitmap; }
    Matrix getPacMatrix() { return pacMatrix; }
    Ghost[] getGhosts() { return ghosts; }
    int getPacX() {
        return pacX;
    }
    int getPacY() {
        return pacY;
    }
    int getWidthOffset() { return widthOffset; }
    int getHeightOffset() { return heightOffset; }
    int getTileSize() { return tileSize; }
    boolean isActive() { return active; }

    Game(Context context, GameView gameView, TextView pointsView)
    {
        this.context = context;
        this.gameView = gameView;
        this.pointsView = pointsView;
        boardBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.game_board_dots);
        pacBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.pacman_right);
        pacMatrix = new Matrix();

        ghosts = new Ghost[4];
        Bitmap[] ghostBitmaps = new Bitmap[] {
                BitmapFactory.decodeResource(context.getResources(), R.drawable.blinky),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.pinky),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.inky),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.clyde)
        };

        for (int i = 0; i < ghosts.length; i++) {
            Ghost ghost = new Ghost();
            ghost.bitmap = ghostBitmaps[i];
            ghost.matrix = new Matrix();
            ghosts[i] = ghost;
        }
    }

    void newGame()
    {
        loadGameBoard("pac_map.txt");
        setSize(gameView.getWidth());

        // reset pacman
        pacX = 13;
        pacY = 23; //just some starting coordinates
        Log.d("newGame", "tileSize = " + tileSize
                + ", pacOffset = " + pacOffset
                + ", pacSize = " + pacSize);
        Log.d("newGame", "pacX = " + pacX
                + ", pacY = " + pacY + ", widthOffset = " + widthOffset
                + ", heightOffset = " + heightOffset);
        pacMatrix.setTranslate(
                scaleToMap(pacX) - pacOffset + widthOffset,
                scaleToMap(pacY) - pacOffset + heightOffset);

        // reset ghosts
        int[][] ghostsXY = new int[][] {
                new int[] {14, 11},
                new int[] {12, 14},
                new int[] {13, 14},
                new int[] {15, 14}
        };


        for (int i = 0; i < ghosts.length; i++) {
            Ghost ghost = ghosts[i];
            ghost.x = ghostsXY[i][0];
            ghost.y = ghostsXY[i][1];
            ghost.dirX = 1;
            ghost.dirY = 0;
            ghost.nextDirX = 0;
            ghost.nextDirY = -1;
            ghost.matrix.setTranslate(
                    scaleToMap(ghost.x) - pacOffset + widthOffset,
                    scaleToMap(ghost.y) - pacOffset + heightOffset);

        }

        //reset points
        points = 0;
        pointsView.setText(context.getResources().getString(R.string.points, points));

        // start game
        active = true;

        //redraw screen
        gameView.invalidate();
    }

    private void loadGameBoard(String textFile) {
        // temporary ArrayList used here because line count in txt is not known beforehand
        ArrayList<char[]> tempBoard = new ArrayList<>();
        int width = 0;
        dotCount = 0;

        try {
            InputStream inputStream = context.getAssets().open(textFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int lineCount = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                char[] lineArray = line.toCharArray();
                width = Math.max(width, lineArray.length); // in case lines have different lengths
                lineCount++;
                tempBoard.add(lineArray);
                Log.d("loadGameBoard", "lineArray[0] = " + lineArray[0] + " + lineArray[lineArray.length-1]" + lineArray[lineArray.length-1] + "line.length() = " + line.length() + "lineArray.length = " + lineArray.length);

                // count initial dots
                for (char ch : lineArray) {
                    if (ch == '*') {
                        dotCount++;
                    }
                }
            }

            Log.d("loadGameBoard", "lineCount = " + lineCount + ", width = " + width);
        } catch (IOException e) {
            e.printStackTrace();
        }

        h = tempBoard.size();
        w = width;
        board = new char[h][w];

        for (int i = 0; i < h; i++) {
            board[i] = tempBoard.get(i);
        }
    }

    private void setSize(int viewWidth)
    {
        // tile size in pixels = width in pixels divided by width in tiles
        tileSize = viewWidth / this.w;

        // offset left/top board edges so they begin at half of excess space
        widthOffset = (gameView.getWidth() - this.w * tileSize) / 2 - tileSize;
        heightOffset = (gameView.getHeight() - this.h * tileSize) / 2 - tileSize;

        // scale size of drawn characters
        pacSize = this.w * tileSize / 16;

        // offset each character's drawing coordinates relative to its tile
        pacOffset = (pacSize - tileSize) / 2;

        // scale bitmaps according to computed measures
        boardBitmap = Bitmap.createScaledBitmap(boardBitmap, this.w * tileSize, this.h * tileSize, true);
        pacBitmap = Bitmap.createScaledBitmap(pacBitmap, pacSize, pacSize, true);

        for (Ghost ghost : ghosts) {
            ghost.bitmap = Bitmap.createScaledBitmap(ghost.bitmap, pacSize, pacSize, true);
        }

        Log.d("gameSize", "board: width = " + this.w * tileSize + ", height = " + this.h * tileSize);
        Log.d("gameSize", "widthOffset = " + widthOffset + ", heightOffset = " + heightOffset);
        Log.d("gameSize", "pacSize: " + pacSize);
    }

    void moveGhosts() {
        for (Ghost ghost : ghosts) {
            boolean directionChanged = false;
            int newX = (ghost.x + ghost.nextDirX + w ) % w;
            int newY = (ghost.y + ghost.nextDirY + h) % h;

            // change direction when possible
            if (board[newY][newX] == '#') {
                newX = (ghost.x + ghost.dirX + w) % w;
                newY = (ghost.y + ghost.dirY + h) % h;
            }
            else {
                ghost.dirX = ghost.nextDirX;
                ghost.dirY = ghost.nextDirY;
                directionChanged = true;
            }

            // change direction randomly when reaching a wall
            if (board[newY][newX] == '#') {
                if (ghost.dirX == 0) {
                    ghost.dirX = new int[] {-1, 1}[random.nextInt(2)];
                    ghost.dirY = 0;
                    ghost.nextDirX = 0;
                    ghost.nextDirY = new int[] {-1, 1}[random.nextInt(2)];
                }
                else {
                    ghost.dirX = 0;
                    ghost.dirY = new int[] {-1, 1}[random.nextInt(2)];
                    ghost.nextDirX = new int[] {-1, 1}[random.nextInt(2)];
                    ghost.nextDirY = 0;
                }
                newX = (ghost.x + ghost.dirX + w) % w;
                newY = (ghost.y + ghost.dirY + h) % h;

                // try the opposite way if hitting another wall
                if (board[newY][newX] == '#') {
                    ghost.dirX *= -1;
                    ghost.dirY *= -1;
                }

                directionChanged = true;
            }

//        int degrees = 0;
//        if (ghost.dirX < 0) degrees = 180;
//        else if (ghost.dirY > 0) degrees = 90;
//        else if (ghost.dirY < 0) degrees = -90;

            // make move if new tile is not a wall
            if (board[newY][newX] != '#') {
                ghost.x = newX;
                ghost.y = newY;
                ghost.matrix.setRotate(0, ghost.bitmap.getWidth() / 2, ghost.bitmap.getHeight() / 2);
                ghost.matrix.postTranslate(scaleToMap(ghost.x) - pacOffset + widthOffset, scaleToMap(ghost.y) - pacOffset + heightOffset);
            }

            // change next direction if current direction changed
            if (directionChanged) {
                if (ghost.dirX == 0) {
                    ghost.nextDirX = new int[] {-1, 1}[random.nextInt(2)];
                    ghost.nextDirY = 0;
                }
                else {
                    ghost.nextDirX = 0;
                    ghost.nextDirY =  new int[] {-1, 1}[random.nextInt(2)];
                }
            }
        }

        gameView.invalidate();
    }

    void movePacman()
    {
        int newPacX = (pacX + nextDirX + w ) % w;
        int newPacY = (pacY + nextDirY + h) % h;
        Log.d("movePacman", "w = " + w + ", h = " + h + ", newPacX = " + newPacX + ", newPacY = " + newPacY + ", dirX = " + dirX + ", dirY = " + dirY);

        // change direction when possible
        if (board[newPacY][newPacX] == '#') {
            newPacX = (pacX + dirX + w ) % w;
            newPacY = (pacY + dirY + h) % h;
        }
        else {
            dirX = nextDirX;
            dirY = nextDirY;
        }

        int degrees = 0;
        if (dirX < 0) degrees = 180;
        else if (dirY > 0) degrees = 90;
        else if (dirY < 0) degrees = -90;

        Log.d("movePacman", "board[pacY-1][pacX-1] = " + board[newPacY][newPacX]);

        // make move if new tile is not a wall
        if (board[newPacY][newPacX] != '#') {
            pacX = newPacX;
            pacY = newPacY;
            pacMatrix.setRotate(degrees, pacBitmap.getWidth() / 2, pacBitmap.getHeight() / 2);
            pacMatrix.postTranslate(scaleToMap(pacX) - pacOffset + widthOffset, scaleToMap(pacY) - pacOffset + heightOffset);
        }

        eatDot();

        // game over or won?
        if (isEaten()) {
            active = false;
            gameView.endGame(gameView.getResources().getString(R.string.gameover));
        } else if (dotCount < 1) {
            active = false;
            gameView.endGame(gameView.getResources().getString(R.string.youwin));
        }

        gameView.invalidate();
    }

    private void changeDirection(int x, int y) {
        nextDirX = x;
        nextDirY = y;
    }

    void moveLeft() {
        changeDirection(-1, 0);
    }

    void moveRight() {
        changeDirection(1, 0);
    }

    void moveUp() {
        changeDirection(0, -1);
    }

    void moveDown() {
        changeDirection(0, 1);
    }

    private void eatDot()
    {
        // eat dot and update score
        if (board[pacY][pacX] == '*') {
            dotCount--;
            points += 10;
            board[pacY][pacX] = ' ';
            pointsView.setText(context.getResources().getString(R.string.points, points));
        }
    }

    private boolean isEaten() {
        for (Ghost ghost : ghosts) {
            if (Math.sqrt(Math.pow(pacX - ghost.x, 2) + Math.pow(pacY - ghost.y, 2)) <= 1) {
                return true;
            }
        }

        return false;
    }

    int scaleToMap(int coordinate){
        return (coordinate + 1) * tileSize;
    }
}
