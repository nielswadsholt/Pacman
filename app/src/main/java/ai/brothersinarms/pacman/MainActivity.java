package ai.brothersinarms.pacman;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    GameView gameView;
    TextView timerView;
    Game game;

    private Timer timer;
    private int counter;
    private int period = 1000; // number of milliseconds between each update

    private Timer pacTimer;
    private int pacCounter;
    private int pacPeriod = 70; // number of milliseconds between each update

    private boolean running;
    Bundle runningInstanceState; // for saving state through stop / restart events

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runningInstanceState = new Bundle();

        //saying we want the game to run in one mode only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        gameView =  findViewById(R.id.gameView);

        gameView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (game.isActive() && !running) resumeGame();
                game.moveLeft();
            }
            @Override
            public void onSwipeRight() {
                if (game.isActive() && !running) resumeGame();
                game.moveRight();
            }
            @Override
            public void onSwipeUp() {
                if (game.isActive() && !running) resumeGame();
                game.moveUp();
            }
            @Override
            public void onSwipeDown() {
                if (game.isActive() && !running) resumeGame();
                game.moveDown();
            }

            @Override
            public void onSingleTap() {
                Log.d("OnSwipeTouchListener", "GAME PAUSED/RESUMED!");

                if (running) pauseGame();
                else resumeGame();
            }
        });

        TextView textView = findViewById(R.id.score);
        timerView = findViewById(R.id.clock);
//        timerView.setText(getResources().getString(R.string.time, 60));
        game = new Game(this, gameView, textView);

        // Get dimensions of gameView once created, then pass it to the game and finally pass THAT to the gameView
        gameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                gameView.setGame(game);
                game.newGame();

                Log.d("onCreate", "onCreate: gameView.getWidth() = " + gameView.getWidth());

                // Set up timers
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TimerMethod();
                    }

                }, 0, period);

                pacTimer = new Timer();
                pacTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        PacTimerMethod();
                    }

                }, 0, pacPeriod);

                resetTime();

                // start game
                running = true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lifeCycle", "onStop called");
        runningInstanceState.putBoolean("running", running); // save running state
        pauseGame();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lifeCycle", "onRestart called");
        running = runningInstanceState.getBoolean("running"); // restore running state
        if (running) resumeGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifeCycle", "onDestroy called");
        //just to make sure if the app is killed, that we stop the timer.
        timer.cancel();
        pacTimer.cancel();
    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            if (running)
            {
                counter--;
                timerView.setText(getResources().getString(R.string.time, counter));

                if (counter == 0) {
                    game.gameOver();
                }
            }
        }
    };

    private void PacTimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(PacTimer_Tick);
    }

    private Runnable PacTimer_Tick = new Runnable() {
        public void run() {

            if (running)
            {
                pacCounter++;

                if (pacCounter % 2 == 0) {
                    game.movePacman();
                }

                if (pacCounter % 3 == 0) {
                    game.moveGhosts();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            pauseGame();
            Toast.makeText(this,"settings clicked",Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_newGame) {
            resumeGame();
            gameView.restart();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void stopRunning() {
        running = false;
    }

    void resetTime() {
        running = false;
        counter = 60;
        pacCounter = 0;
        timerView.setText(getResources().getString(R.string.time, counter));
    }

    void pauseGame() {
        gameView.addPauseOverlay();
        running = false;
    }

    void resumeGame() {
        gameView.removePauseOverlay();
        running = true;
    }
}
