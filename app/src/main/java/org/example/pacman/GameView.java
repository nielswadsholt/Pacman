package org.example.pacman;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GameView extends View {

	Game game;
	int oldPacx, oldPacy;
    
    //Making a new paint object
    Paint paint = new Paint();
    Path path = new Path();

	public void setGame(Game game)
	{
		this.game = game;
        drawingSetup();
	}

	/* The next 3 constructors are needed for the Android view system,
	when we have a custom view.
	 */
	public GameView(Context context) {
		super(context);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

        Log.d("GameView", "GameView constructor called");
	}

	public GameView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	private void drawingSetup() {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(game.getWidth() / 16);
        paint.setStrokeCap(Paint.Cap.ROUND);

        Log.d("drawingSetup", "game.getWidth() = " + game.getWidth());
        Log.d("pacxy", "drawingSetup: pacx = " + game.getPacX() + ", paxy = " + game.getPacY());
    }

	//In the onDraw we put all our code that should be
	//drawn whenever we update the screen.
	@Override
	protected void onDraw(Canvas canvas) {
		int h = getHeight();
		int w = getWidth();

		canvas.drawColor(Color.BLACK);
        Bitmap boardBitmap = game.getBoardBitmap();
        canvas.drawBitmap(boardBitmap,w / 2 - boardBitmap.getWidth() / 2,
				h / 2 - boardBitmap.getHeight() / 2, paint);

//        Log.d("hwacc", "canvas.isHardwareAccelerated() = " + canvas.isHardwareAccelerated());

        // eraze (eat) dots
        int halfPac = game.getPacBitmap().getWidth() / 2;
        Log.d("pacxy", "pacx = " + game.getPacX() + ", pacy = " + game.getPacY() + ", halfPac = " + halfPac);

        path.addRect(
                game.getPacX() + game.getwOffset() + 20, game.getPacY() + game.gethOffset() + 20,
                game.getPacX() + game.getwOffset() + 21, game.getPacY() + game.gethOffset() + 21,
                Path.Direction.CCW);
        canvas.drawPath(path, paint);

		//draw the pac-man
        Bitmap pacBitmap = game.getPacBitmap();
		canvas.drawBitmap(pacBitmap, game.getPacMatrix(), paint);

		//TODO loop through the list of goldCoins and draw them.
		super.onDraw(canvas);
	}
}
