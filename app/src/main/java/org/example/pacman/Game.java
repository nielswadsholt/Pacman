package org.example.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.TextView;


import java.util.ArrayList;

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
    private int pacX, pacY;
    //the list of goldCoins - initially empty
    private ArrayList<GoldCoin> coins = new ArrayList<>();
    private GameView gameView;
    private int w, h; //height and width of the game map
    private int widthOffset, heightOffset; // distance from screen edge to game map
    private int tileSize;
    private int pacSize;
    private int pacOffset;
    private int[][]board;
//    private int boardX, boardY;

    public int getHeight() { return h; }
    int getWidth() { return w; }
    int getPacX() { return pacX; }
    int getPacY() { return pacY; }
    public int getPoints() { return points; }
    public ArrayList<GoldCoin> getCoins() { return coins; }
    Bitmap getPacBitmap() { return pacBitmap; }
    Bitmap getBoardBitmap(){ return boardBitmap; }
    Matrix getPacMatrix() { return pacMatrix; }
    int getWidthOffset() { return widthOffset; }
    int getHeightOffset() { return heightOffset; }
    int getTileSize() { return tileSize; }

    Game(Context context, GameView gameView, TextView pointsView)
    {
        this.context = context;

        // initialize graphics
        this.gameView = gameView;
        this.pointsView = pointsView;
        boardBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_board);
        pacBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman_right);
        pacMatrix = new Matrix();

        // initialize logic
        w = 28;
        h = 31;
        board = new int[h][w];
        board[0][0] = 2; // wall for testing
    }

    //TODO initialize goldCoins also here
    void newGame()
    {
        pacX = 14;
        pacY = 24; //just some starting coordinates
        Log.d("newGame", "tileSize = " + tileSize + ", pacOffset = " + pacOffset + ", pacSize = " + pacSize);
        Log.d("newGame", "pacX = " + pacX + ", pacY = " + pacY + ", widthOffset = " + widthOffset + ", heightOffset = " + heightOffset);
        pacMatrix.setTranslate(pacX * tileSize - pacOffset + widthOffset, pacY * tileSize - pacOffset + heightOffset);
        //reset the points
        points = 0;
        pointsView.setText(context.getResources().getString(R.string.points, points));
        gameView.invalidate(); //redraw screen
    }

    void setSize(int viewWidth)
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
        Log.d("gameSize", "pacman: " + pacSize);
    }

    private void movePacman(int x, int y)
    {
        int newPacX = (pacX + x + w - 1) % w + 1;
        int newPacY = (pacY + y + h - 1) % h + 1;
        Log.d("movePacman", "w = " + w + ", h = " + h + ", newPacX = " + newPacX + ", newPacY = " + newPacY + ", x = " + x + ", y = " + y);

        int degrees = 0;
        if (x < 0) degrees = 180;
        else if (y > 0) degrees = 90;
        else if (y < 0) degrees = -90;

        Log.d("movePacman", "board[pacY-1][pacX-1] = " + board[newPacY-1][newPacX-1]);

        if (board[newPacY-1][newPacX-1] != 2) {
            pacX = newPacX;
            pacY = newPacY;
            pacMatrix.setRotate(degrees, pacBitmap.getWidth() / 2, pacBitmap.getHeight() / 2);
            pacMatrix.postTranslate(pacX * tileSize - pacOffset + widthOffset, pacY * tileSize - pacOffset + heightOffset);
        }
        else {
            Log.d("wallhit", "Wall hit prevented!");
        }

        doCollisionCheck();
        gameView.invalidate();
    }

    void moveLeft() {
        movePacman(-1, 0);
    }

    void moveRight() {
        movePacman(1, 0);
    }

    void moveUp() {
        movePacman(0, -1);
    }

    void moveDown() {
        movePacman(0, 1);
    }

    //TODO check if the pacman touches a goldCoin
    //and if yes, then update the necessary data
    //for the goldCoins and the points
    //so you need to go through the arrayList of goldCoins and
    //check each of them for a collision with the pacman
    private void doCollisionCheck()
    {

    }
}
