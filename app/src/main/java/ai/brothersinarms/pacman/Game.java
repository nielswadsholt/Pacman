package ai.brothersinarms.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
    private Ghost blinky;
    private int pacX, pacY; // current position of pacman
    private int dirX, dirY; // current direction of pacman
    private int nextDirX, nextDirY; // next direction of pacman
    private GameView gameView;
    private int w, h; //height and width of the game map
    private int widthOffset, heightOffset; // distance from screen edge to game map
    private int tileSize;
    private int pacSize;
    private int pacOffset;
    private char[][]board;
    private int dotCount;
    private static Random random = new Random();

    public int getHeight() { return h; }
    public int getPoints() { return points; }
    Bitmap getPacBitmap() { return pacBitmap; }
    Bitmap getBoardBitmap(){ return boardBitmap; }
    Matrix getPacMatrix() { return pacMatrix; }
    Bitmap getBlinkyBitmap() { return blinky.bitmap; }
    Matrix getBlinkyMatrix() { return blinky.matrix; }

    int getPacX() {
        return pacX;
    }

    int getPacY() {
        return pacY;
    }

    int getWidthOffset() { return widthOffset; }
    int getHeightOffset() { return heightOffset; }
    int getTileSize() { return tileSize; }

    Game(Context context, GameView gameView, TextView pointsView)
    {
        this.context = context;
        this.gameView = gameView;
        this.pointsView = pointsView;
        boardBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_board_dots);
        pacBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman_right);
        pacMatrix = new Matrix();

        blinky = new Ghost();
        blinky.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blinky);
        blinky.matrix = new Matrix();
    }

    void newGame()
    {
        loadGameBoard("pac_map_test.txt");
        setSize(gameView.getWidth());
        pacX = 13;
        pacY = 23; //just some starting coordinates
        Log.d("newGame", "tileSize = " + tileSize + ", pacOffset = " + pacOffset + ", pacSize = " + pacSize);
        Log.d("newGame", "pacX = " + pacX + ", pacY = " + pacY + ", widthOffset = " + widthOffset + ", heightOffset = " + heightOffset);
        pacMatrix.setTranslate(scaleToMap(pacX) - pacOffset + widthOffset, scaleToMap(pacY) - pacOffset + heightOffset);
        blinky.x = 14;
        blinky.y = 11;
        blinky.dirX = 1;
        blinky.dirY = 0;
        blinky.nextDirX = 0;
        blinky.nextDirY = -1;
        blinky.matrix.setTranslate(scaleToMap(blinky.x) - pacOffset + widthOffset, scaleToMap(blinky.y) - pacOffset + heightOffset);

        //reset the points
        points = 0;
        pointsView.setText(context.getResources().getString(R.string.points, points));

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

                // add dots to count
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
        tileSize = viewWidth / this.w;
        widthOffset = (gameView.getWidth() - this.w * tileSize) / 2 - tileSize;
        heightOffset = (gameView.getHeight() - this.h * tileSize) / 2 - tileSize;
        pacSize = this.w * tileSize / 16;
        pacOffset = (pacSize - tileSize) / 2; // offsets pac-man's drawing coordinates relative to its tile's
        boardBitmap = Bitmap.createScaledBitmap(boardBitmap, this.w * tileSize, this.h * tileSize, true);
        Log.d("gameSize", "board: width = " + this.w * tileSize + ", height = " + this.h * tileSize);
        Log.d("gameSize", "widthOffset = " + widthOffset + ", heightOffset = " + heightOffset);
        pacBitmap = Bitmap.createScaledBitmap(pacBitmap, pacSize, pacSize, true);
        blinky.bitmap = Bitmap.createScaledBitmap(blinky.bitmap, pacSize, pacSize, true);
        Log.d("gameSize", "pacman: " + pacSize);
    }

    void moveGhosts() {
        boolean directionChanged = false;
        int newX = (blinky.x + blinky.nextDirX + w ) % w;
        int newY = (blinky.y + blinky.nextDirY + h) % h;

        // change direction when possible
        if (board[newY][newX] == '#') {
            newX = (blinky.x + blinky.dirX + w) % w;
            newY = (blinky.y + blinky.dirY + h) % h;
        }
        else {
            blinky.dirX = blinky.nextDirX;
            blinky.dirY = blinky.nextDirY;
            directionChanged = true;
        }

        // change direction randomly when reaching a wall
        if (board[newY][newX] == '#') {
            if (blinky.dirX == 0) {
                blinky.dirX = new int[] {-1, 1}[random.nextInt(2)];
                blinky.dirY = 0;
                blinky.nextDirX = 0;
                blinky.nextDirY = new int[] {-1, 1}[random.nextInt(2)];
            }
            else {
                blinky.dirX = 0;
                blinky.dirY = new int[] {-1, 1}[random.nextInt(2)];
                blinky.nextDirX = new int[] {-1, 1}[random.nextInt(2)];
                blinky.nextDirY = 0;
            }
            newX = (blinky.x + blinky.dirX + w) % w;
            newY = (blinky.y + blinky.dirY + h) % h;

            // try the opposite way if hitting another wall
            if (board[newY][newX] == '#') {
                blinky.dirX *= -1;
                blinky.dirY *= -1;
            }

            directionChanged = true;
        }

//        int degrees = 0;
//        if (blinky.dirX < 0) degrees = 180;
//        else if (blinky.dirY > 0) degrees = 90;
//        else if (blinky.dirY < 0) degrees = -90;

        // make move if new tile is not a wall
        if (board[newY][newX] != '#') {
            blinky.x = newX;
            blinky.y = newY;
            blinky.matrix.setRotate(0, blinky.bitmap.getWidth() / 2, blinky.bitmap.getHeight() / 2);
            blinky.matrix.postTranslate(scaleToMap(blinky.x) - pacOffset + widthOffset, scaleToMap(blinky.y) - pacOffset + heightOffset);
        }

        // change next direction if current direction changed
        if (directionChanged) {
            if (blinky.dirX == 0) {
                blinky.nextDirX = new int[] {-1, 1}[random.nextInt(2)];
                blinky.nextDirY = 0;
            }
            else {
                blinky.nextDirX = 0;
                blinky.nextDirY =  new int[] {-1, 1}[random.nextInt(2)];
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
            gameView.endGame(gameView.getResources().getString(R.string.gameover));
        } else if (dotCount < 1) {
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
        return Math.sqrt(Math.pow(pacX - blinky.x, 2) + Math.pow(pacY - blinky.y, 2)) <= 1;
    }

    int scaleToMap(int coordinate){
        return (coordinate + 1) * tileSize;
    }
}
