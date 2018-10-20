package org.example.pacman;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {

    Game game;
    Paint paint = new Paint();
    Path path = new Path();

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
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(game.getTileSize());
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
        path.addRect(
                game.pacXScaled() + tileSize / 2 + game.getWidthOffset(),
                game.pacYScaled() + tileSize / 2 + game.getHeightOffset(),
                game.pacXScaled() + tileSize / 2 + game.getWidthOffset() + 1,
                game.pacYScaled() + tileSize / 2 + game.getHeightOffset() + 1,
                Path.Direction.CCW);
        canvas.drawPath(path, paint);

        //draw the pac-man
        Bitmap pacBitmap = game.getPacBitmap();
        canvas.drawBitmap(pacBitmap, game.getPacMatrix(), paint);

        //TODO loop through the list of goldCoins and draw them.
        super.onDraw(canvas);
    }
}
