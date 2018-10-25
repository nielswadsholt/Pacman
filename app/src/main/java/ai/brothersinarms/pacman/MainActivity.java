package ai.brothersinarms.pacman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private Bundle runningInstanceState; // for saving state through stop / restart events

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runningInstanceState = new Bundle();

        // Set action bar title font to Pac-Man font
        TextView titleView = new TextView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        titleView.setLayoutParams(lp);
        titleView.setText(R.string.app_name);
        titleView.setTextSize(26);
        titleView.setTextColor(Color.WHITE);
        Typeface tf = ResourcesCompat.getFont(this, R.font.crackman);
        titleView.setTypeface(tf);
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();

        if (actionbar != null) {
            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionbar.setCustomView(titleView);
        }

        //saying we want the game to run in one mode only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        gameView =  findViewById(R.id.gameView);

        gameView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (game.state != Game.FINISHED && !running) resumeGame();
                game.moveLeft();
            }
            @Override
            public void onSwipeRight() {
                if (game.state != Game.FINISHED && !running) resumeGame();
                game.moveRight();
            }
            @Override
            public void onSwipeUp() {
                if (game.state != Game.FINISHED && !running) resumeGame();
                game.moveUp();
            }
            @Override
            public void onSwipeDown() {
                if (game.state != Game.FINISHED && !running) resumeGame();
                game.moveDown();
            }

            @Override
            public void onSingleTap() {
                Log.d("OnSwipeTouchListener", "GAME PAUSED/RESUMED!");

                if (running) pauseGame();
                else resumeGame();
            }
        });

        TextView hiscoreView = findViewById(R.id.hiscore);
        TextView scoreView = findViewById(R.id.score);
        timerView = findViewById(R.id.clock);

        SharedPreferences sharedPref =
                getPreferences(Context.MODE_PRIVATE);
        int hiscore = sharedPref.getInt(getString(R.string.hiscore_key), 0);

        game = new Game(this, gameView, scoreView, hiscoreView, hiscore);

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
        Log.d("lifeCycle", "onDestroy called");
        //just to make sure if the app is killed, that we stop the timer.
        timer.cancel();
        pacTimer.cancel();

        // save hi-score
        game.updateHiscore();

        SharedPreferences sharedPref =
                getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.hiscore_key), game.getHiscore());
        editor.apply();

        super.onDestroy();
    }

    private void TimerMethod()
    {
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            if (running)
            {
                counter--;
                timerView.setText(getResources().getString(R.string.time_txt, counter));

                if (counter == 0) {
                    game.gameOver();
                }
            }
        }
    };

    private void PacTimerMethod()
    {
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
        SharedPreferences timeLimitPref;
        SharedPreferences.Editor timeLimitEditor;

        switch (item.getItemId()) {
            case R.id.action_newGame:
                resumeGame();
                gameView.restart();
                return true;
            case R.id.action_settings:
                pauseGame();
                return true;
            case R.id.action_reset_hiscore:
                game.resetHiscore();
                return true;
            case R.id.action_time_limit:
                return true;
            case R.id.time_60:
                timeLimitPref =
                        getPreferences(Context.MODE_PRIVATE);
                timeLimitEditor = timeLimitPref.edit();
                timeLimitEditor.putInt(getString(R.string.time_limit_key), 60);
                timeLimitEditor.apply();

                resumeGame();
                gameView.restart();

                return true;
            case R.id.time_120:
                timeLimitPref =
                        getPreferences(Context.MODE_PRIVATE);
                timeLimitEditor = timeLimitPref.edit();
                timeLimitEditor.putInt(getString(R.string.time_limit_key), 120);
                timeLimitEditor.apply();

                resumeGame();
                gameView.restart();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void stopRunning() {
        running = false;
    }

    void resetTime() {
        running = false;

        SharedPreferences sharedPref =
                getPreferences(Context.MODE_PRIVATE);
        counter = sharedPref.getInt(getString(R.string.time_limit_key), 0);
        pacCounter = 0;
        timerView.setText(getResources().getString(R.string.time_txt, counter));
    }

    void pauseGame() {
        gameView.addPauseOverlay();
        running = false;
    }

    void resumeGame() {
        game.state = Game.ACTIVE;
        gameView.removeOverlay();
        running = true;
    }
}
