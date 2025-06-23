package com.example.tunetally;

import android.media.MediaPlayer;
import java.io.IOException;

public class MediaPlayerManager {
    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private String[] playList;
    private int currentIndex = 0;

    private MediaPlayerManager() {
        mediaPlayer = new MediaPlayer();
    }

    public static boolean existed() {
        return instance != null;
    }
    public static void initialize() {
        instance = null;
    }
    public static synchronized MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public void setPlaylist(String[] playlist) {
        this.playList = playlist;
        preparePlayer();
    }

    private void preparePlayer() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playList[currentIndex]);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> playNext());
        } catch (IOException e) {
            throw new RuntimeException("Error setting data source: " + e.getMessage(), e);
        }
    }

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void playNext() {
        currentIndex = (currentIndex + 1) % playList.length;
        preparePlayer();
        play();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        this.release();
    }
}
