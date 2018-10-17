package org.example.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    //bitmap of the pacman
    private Bitmap pacBitmap;
    //textView reference to points
    private TextView pointsView;
    private int pacx, pacy;
    //the list of goldCoins - initially empty
    private ArrayList<GoldCoin> coins = new ArrayList<>();
    //a reference to the gameView
    private GameView gameView;
    private int h, w; //height and width of the screen

    Game(Context context, TextView pointsView)
    {
        this.context = context;
        this.pointsView = pointsView;
        pacBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman);

    }

    public void setGameView(GameView view)
    {
        this.gameView = view;
    }

    //TODO initialize goldCoins also here
    void newGame()
    {
        pacx = 50;
        pacy = 400; //just some starting coordinates
        //reset the points
        points = 0;
        pointsView.setText(context.getResources().getString(R.string.points, points));
        gameView.invalidate(); //redraw screen
    }

    void setSize(int h, int w)
    {
        this.h = h;
        this.w = w;
    }

    void movePacman(int x, int y)
    {
        pacx = (pacx + x + w) % w;
        pacy = (pacy + y + h) % h;
        Log.d("movePacman", "x: " + pacx + ", y: " + pacy);
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

    int getPacx()
    {
        return pacx;
    }

    int getPacy()
    {
        return pacy;
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


}
