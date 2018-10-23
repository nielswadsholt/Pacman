package ai.brothersinarms.pacman;

import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GameView extends View {

    Context context;
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
        this.context = context;
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void paintSetup() {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
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
                game.scaleToMap(game.getPacX()) + game.getWidthOffset(),
                game.scaleToMap(game.getPacY()) + game.getHeightOffset(),
                game.scaleToMap(game.getPacX()) + tileSize + game.getWidthOffset() + 1,
                game.scaleToMap(game.getPacY()) + tileSize + game.getHeightOffset() + 1,
                Path.Direction.CCW);
        canvas.drawPath(path, paint);

        // draw the pac-man
        Bitmap pacBitmap = game.getPacBitmap();
        canvas.drawBitmap(pacBitmap, game.getPacMatrix(), paint);

        // draw enemies
        canvas.drawBitmap(game.getBlinkyBitmap(), game.getBlinkyMatrix(), paint);

        super.onDraw(canvas);
    }

    void declareResult(CharSequence message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)context).resetTime();
                path.reset();
                game.newGame();
            }
        });
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ((MainActivity)context).resetTime();
                path.reset();
                game.newGame();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void endGame(CharSequence message) {
        Log.d("endGame", "endGame called");
        ((MainActivity)context).stopRunning();
        declareResult(message);
    }
}
