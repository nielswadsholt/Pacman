package org.example.pacman;

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
    private char[][]board;

    public int getHeight() { return h; }
    public int getPoints() { return points; }
    public ArrayList<GoldCoin> getCoins() { return coins; }
    Bitmap getPacBitmap() { return pacBitmap; }
    Bitmap getBoardBitmap(){ return boardBitmap; }
    Matrix getPacMatrix() { return pacMatrix; }
    int getWidthOffset() { return widthOffset; }
    int getHeightOffset() { return heightOffset; }
    int getTileSize() { return tileSize; }
    char[][] getBoard() { return board; }

    Game(Context context, GameView gameView, TextView pointsView)
    {
        this.context = context;
        this.gameView = gameView;
        this.pointsView = pointsView;
        boardBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_board);
        pacBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pacman_right);
        pacMatrix = new Matrix();
    }

    void newGame()
    {
        loadGameBoard("pac_map.txt");
        setSize(gameView.getWidth());
        pacX = 13;
        pacY = 23; //just some starting coordinates
        Log.d("newGame", "tileSize = " + tileSize + ", pacOffset = " + pacOffset + ", pacSize = " + pacSize);
        Log.d("newGame", "pacX = " + pacX + ", pacY = " + pacY + ", widthOffset = " + widthOffset + ", heightOffset = " + heightOffset);
        pacMatrix.setTranslate(scaleToMap(pacX) - pacOffset + widthOffset, scaleToMap(pacY) - pacOffset + heightOffset);

        //reset the points
        points = 0;
        pointsView.setText(context.getResources().getString(R.string.points, points));
        gameView.invalidate(); //redraw screen
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
        Log.d("gameSize", "pacman: " + pacSize);
    }

    private void movePacman(int x, int y)
    {
        int newPacX = (pacX + x + w ) % w;
        int newPacY = (pacY + y + h) % h;
        Log.d("movePacman", "w = " + w + ", h = " + h + ", newPacX = " + newPacX + ", newPacY = " + newPacY + ", x = " + x + ", y = " + y);

        int degrees = 0;
        if (x < 0) degrees = 180;
        else if (y > 0) degrees = 90;
        else if (y < 0) degrees = -90;

        Log.d("movePacman", "board[pacY-1][pacX-1] = " + board[newPacY][newPacX]);

        // make move if new tile is not a wall
        if (board[newPacY][newPacX] != '#') {
            pacX = newPacX;
            pacY = newPacY;
            pacMatrix.setRotate(degrees, pacBitmap.getWidth() / 2, pacBitmap.getHeight() / 2);
            pacMatrix.postTranslate(scaleToMap(pacX) - pacOffset + widthOffset, scaleToMap(pacY) - pacOffset + heightOffset);
        }

        eatDot();
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

    private void eatDot()
    {
        // eat dot and update score
        if (board[pacY][pacX] == '*') {
            points += 10;
            board[pacY][pacX] = ' ';
            pointsView.setText(context.getResources().getString(R.string.points, points));
        }
    }

    int scaleToMap(int coordinate){
        return (coordinate + 1) * tileSize;
    }

    private void loadGameBoard(String textFile) {
        // temporary hack because line count in txt is not known before they are read:
        ArrayList<char[]> tempBoard = new ArrayList<>();
        int width = 0;

        try {
            InputStream inputStream = context.getAssets().open(textFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int lineCount = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                char[] lineArray = line.toCharArray();
                width = lineArray.length;
                lineCount++;
                tempBoard.add(lineArray);
                Log.d("loadGameBoard", "lineArray[0] = " + lineArray[0] + " + lineArray[lineArray.length-1]" + lineArray[lineArray.length-1] + "line.length() = " + line.length() + "lineArray.length = " + lineArray.length);
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
}
