package ai.brothersinarms.pacman;

import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
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
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setStrokeJoin(Paint.Join.ROUND);
        dotPaint.setStrokeCap(Paint.Cap.ROUND);
        dotPaint.setStrokeWidth(game.getTileSize() / 2);
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
                            x - 3 + tileSize / 2 + game.getWidthOffset(),
                            y - 3 + tileSize / 2 + game.getHeightOffset(),
                            x + 3 + tileSize / 2 + game.getWidthOffset(),
                            y + 3 + tileSize / 2 + game.getHeightOffset(),
                            dotPaint);
                }
            }
        }

        if (won) {
            DeclareResult(getResources().getString(R.string.youwin));
            Log.d("GameWon", "You win!");
        }

        // draw the pac-man
        Bitmap pacBitmap = game.getPacBitmap();
        canvas.drawBitmap(pacBitmap, game.getPacMatrix(), paint);

        // draw enemies
        canvas.drawBitmap(game.getBlinkyBitmap(), game.getBlinkyMatrix(), paint);

        super.onDraw(canvas);
    }

    public void DeclareResult(CharSequence message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                game.newGame();
            }
        });
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                game.newGame();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
