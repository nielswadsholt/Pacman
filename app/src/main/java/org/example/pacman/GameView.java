package org.example.pacman;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GameView extends View {

    Game game;
    Paint paint = new Paint();
    Paint dotPaint = new Paint();

    public void setGame(Game game)
    {
        this.game = game;
        paintSetup();
    }

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void paintSetup() {

        dotPaint.setColor(getResources().getColor(R.color.colorDot));
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setStrokeJoin(Paint.Join.ROUND);
        dotPaint.setStrokeCap(Paint.Cap.ROUND);
        dotPaint.setStrokeWidth(game.getTileSize() / 6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight();
        int w = getWidth();
        int tileSize = game.getTileSize();

        canvas.drawColor(Color.BLACK);
        Bitmap boardBitmap = game.getBoardBitmap();
        canvas.drawBitmap(
                boardBitmap,
                w / 2 - boardBitmap.getWidth() / 2,
                h / 2 - boardBitmap.getHeight() / 2,
                paint);

        // draw dots and check for win
        char[][] board = game.getBoard();
        boolean won = true;

        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == '*') {
                    won = false;

                    int x = game.scaleToMap(j);
                    int y = game.scaleToMap(i);
                    canvas.drawRect(
                            x - 1 + tileSize / 2 + game.getWidthOffset(),
                            y - 1 + tileSize / 2 + game.getHeightOffset(),
                            x + 1 + tileSize / 2 + game.getWidthOffset(),
                            y + 1 + tileSize / 2 + game.getHeightOffset(),
                            dotPaint);
                }
            }
        }

        if (won) {
            Log.d("GameWon", "You win!");
        }

        //draw the pac-man
        Bitmap pacBitmap = game.getPacBitmap();
        canvas.drawBitmap(pacBitmap, game.getPacMatrix(), paint);

        super.onDraw(canvas);
    }
}
