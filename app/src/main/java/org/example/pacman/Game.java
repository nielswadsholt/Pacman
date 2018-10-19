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
    //context is a reference to the activity
    private Context context;
    private int points = 0; //how many points do we have
    //bitmap of the game board
    private Bitmap boardBitmap;
    //bitmap of the pacman
    private Bitmap pacBitmap;
    private Matrix pacMatrix;
    //textView reference to points
    private TextView pointsView;
    private int pacX, pacY;
    //the list of goldCoins - initially empty
    private ArrayList<GoldCoin> coins = new ArrayList<>();
    //a reference to the gameView
    private GameView gameView;
    private int w, h; //height and width of the game map
    private int wOffset, hOffset; // distance from screen edge to game map

    Game(Context context, GameView gameView, TextView pointsView)
    {
        this.context = context;
        this.gameView = gameView;

        this.pointsView = pointsView;
        boardBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_board);
        pacBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman_right);
        pacMatrix = new Matrix();
    }

    //TODO initialize goldCoins also here
    void newGame()
    {
        // TODO: set pacX and pacY relative to game board
        pacX = 343;
        pacY = 591; //just some starting coordinates
        Log.d("newGame", "wOffset = " + wOffset + ", hOffset = " + hOffset);
        pacMatrix.postTranslate(pacX + wOffset, pacY + hOffset);
        //reset the points
        points = 0;
        pointsView.setText(context.getResources().getString(R.string.points, points));
        gameView.invalidate(); //redraw screen
    }

    void setSize(int w, int h)
    {
        // ensure that the drawn board is divisible by the number of tiles
        this.w = w / 28 * 28;
        this.h = w / 28 * 31;
        wOffset = (gameView.getWidth() - this.w) / 2 -  w / 28;
        hOffset = (gameView.getHeight() - this.h) / 2 -  w / 28;
        boardBitmap = Bitmap.createScaledBitmap(boardBitmap, this.w, this.h, true);
        Log.d("gameSize", "board: width = " + this.w + ", height = " + this.h);
        Log.d("gameSize", "wOffset = " + wOffset + ", hOffset = " + hOffset);

        int pacSize = this.w / 16;
        pacBitmap = Bitmap.createScaledBitmap(pacBitmap, pacSize, pacSize, true);
        Log.d("gameSize", "pacman: " + pacSize);
    }

    void movePacman(int x, int y)
    {
        pacX = (pacX + x + w) % w;
        pacY = (pacY + y + h) % h;
        Log.d("movePacman", "w = " + w + ", pacX = " + pacX + ", pacY = " + pacY + ", x = " + x + ", y = " + y);

        int degrees = 0;
        if (x < 0) degrees = 180;
        else if (y > 0) degrees = 90;
        else if (y < 0) degrees = -90;

        pacMatrix.setRotate(degrees, pacBitmap.getWidth() / 2, pacBitmap.getHeight() / 2);
        pacMatrix.postTranslate(pacX + wOffset, pacY + hOffset);

        doCollisionCheck();
        gameView.invalidate();
    }

    //TODO check if the pacman touches a goldCoin
    //and if yes, then update the necessary data
    //for the goldCoins and the points
    //so you need to go through the arrayList of goldCoins and
    //check each of them for a collision with the pacman
    private void doCollisionCheck()
    {

    }

    public int getHeight() {
        return h;
    }

    int getWidth() {
        return w;
    }

    int getPacX()
    {
        return pacX;
    }

    int getPacY()
    {
        return pacY;
    }

    public int getPoints()
    {
        return points;
    }

    public ArrayList<GoldCoin> getCoins()
    {
        return coins;
    }

    Bitmap getPacBitmap()
    {
        return pacBitmap;
    }

    Bitmap getBoardBitmap(){
        return boardBitmap;
    }

    Matrix getPacMatrix() {
        return pacMatrix;
    }

    int getwOffset() {
        return wOffset;
    }

    int gethOffset() {
        return hOffset;
    }
}
