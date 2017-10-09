package org.example.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 *
 * This class should contain all your game logic
 */

public class Game {
    private Context context;

    Bitmap pacBitmap;
    private int pacx, pacy;
    private ArrayList<GoldCoin> coins = new ArrayList<>();
    private GameView gameView;
    private int h,w; //height and width of screen

    public Game(Context context)
    {
        this.context = context;
        pacBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman);

    }

    public void setGameView(GameView view)
    {
        this.gameView = view;
    }

    //TODO initialize goldcoins also here
    public void newGame()
    {
        pacx = 50;
        pacy = 400; //just some starting coordinates
        gameView.invalidate(); //redraw screen
    }

    public void setSize(int h, int w)
    {
        this.h = h;
        this.w = w;
    }

    public void movePacmanRight(int pixels)
    {
        //still within our boundaries?
        if (pacx+pixels+pacBitmap.getWidth()<w) {
            pacx = pacx + pixels;
            gameView.invalidate();
        }
    }

    public int getPacx()
    {
        return pacx;
    }

    public int getPacy()
    {
        return pacy;
    }

    public Bitmap getPacBitmap()
    {
        return pacBitmap;
    }


}
