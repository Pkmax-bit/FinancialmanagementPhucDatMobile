package com.example.financialmanagement.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import java.io.IOException;

/**
 * Helper class for playing audio
 */
public class AudioPlayer {
    
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable progressRunnable;
    private OnPlaybackListener listener;
    private boolean isPlaying = false;
    
    public interface OnPlaybackListener {
        void onProgressUpdate(int currentPosition, int duration);
        void onPlaybackComplete();
        void onPlaybackError(String error);
    }
    
    public AudioPlayer() {
        handler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Start playing audio from URL
     */
    public void play(String audioUrl, OnPlaybackListener listener) {
        this.listener = listener;
        
        try {
            // Release previous player if exists
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                startProgressUpdates();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                if (listener != null) {
                    listener.onPlaybackComplete();
                }
                stopProgressUpdates();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                isPlaying = false;
                if (listener != null) {
                    listener.onPlaybackError("Playback error: " + what);
                }
                stopProgressUpdates();
                return true;
            });
            
            mediaPlayer.prepareAsync();
            
        } catch (IOException e) {
            if (listener != null) {
                listener.onPlaybackError("Failed to play audio: " + e.getMessage());
            }
        }
    }
    
    /**
     * Pause playback
     */
    public void pause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            stopProgressUpdates();
        }
    }
    
    /**
     * Resume playback
     */
    public void resume() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            startProgressUpdates();
        }
    }
    
    /**
     * Stop playback
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            stopProgressUpdates();
        }
    }
    
    /**
     * Seek to position
     */
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }
    
    /**
     * Check if playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /**
     * Get duration
     */
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }
    
    /**
     * Get current position
     */
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    
    /**
     * Start progress updates
     */
    private void startProgressUpdates() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPlaying && mediaPlayer != null && listener != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    listener.onProgressUpdate(currentPosition, duration);
                    handler.postDelayed(this, 100); // Update every 100ms
                }
            }
        };
        handler.post(progressRunnable);
    }
    
    /**
     * Stop progress updates
     */
    private void stopProgressUpdates() {
        if (progressRunnable != null) {
            handler.removeCallbacks(progressRunnable);
        }
    }
    
    /**
     * Release resources
     */
    public void release() {
        stop();
    }
}


