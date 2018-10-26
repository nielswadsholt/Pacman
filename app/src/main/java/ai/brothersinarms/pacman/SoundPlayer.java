package ai.brothersinarms.pacman;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

class SoundPlayer {
    private static SoundPool soundPool;
    private static int openingMusic;
    private static int chompSound;
    private static int lostSound;
    private static boolean muted = false;

    SoundPlayer(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        openingMusic = soundPool.load(context, R.raw.pacman_opening, 1);
        chompSound = soundPool.load(context, R.raw.single_chomp, 1);
        lostSound = soundPool.load(context, R.raw.pacman_lost, 1);
    }

    void playOpeningMusic() {
        if (!muted) soundPool.play(openingMusic, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    void playChompSound() {
        if (!muted) soundPool.play(chompSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    void playLostSound() {
        if (!muted) soundPool.play(lostSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    void mute() {
        muted = true;
    }

    void turnOn() {
        muted = false;
    }
}
