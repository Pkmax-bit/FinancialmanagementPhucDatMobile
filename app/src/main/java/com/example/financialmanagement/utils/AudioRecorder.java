package com.example.financialmanagement.utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.IOException;

/**
 * Helper class for recording audio
 */
public class AudioRecorder {
    
    private MediaRecorder mediaRecorder;
    private String outputFilePath;
    private boolean isRecording = false;
    private long startTime;
    private Handler handler;
    private Runnable durationRunnable;
    private OnRecordingListener listener;
    
    public interface OnRecordingListener {
        void onDurationUpdate(long durationMs);
        void onRecordingComplete(String filePath, long durationMs);
        void onRecordingError(String error);
    }
    
    public AudioRecorder(Context context) {
        handler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Start recording audio
     */
    public void startRecording(Context context, OnRecordingListener listener) {
        this.listener = listener;
        
        try {
            // Create output file
            File outputDir = context.getCacheDir();
            File outputFile = File.createTempFile("voice_", ".m4a", outputDir);
            outputFilePath = outputFile.getAbsolutePath();
            
            // Setup MediaRecorder
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncodingBitRate(128000);
            mediaRecorder.setOutputFile(outputFilePath);
            
            mediaRecorder.prepare();
            mediaRecorder.start();
            
            isRecording = true;
            startTime = System.currentTimeMillis();
            
            // Start duration update timer
            durationRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isRecording && listener != null) {
                        long duration = System.currentTimeMillis() - startTime;
                        listener.onDurationUpdate(duration);
                        handler.postDelayed(this, 100); // Update every 100ms
                    }
                }
            };
            handler.post(durationRunnable);
            
        } catch (IOException e) {
            if (listener != null) {
                listener.onRecordingError("Failed to start recording: " + e.getMessage());
            }
        }
    }
    
    /**
     * Stop recording and return file path
     */
    public void stopRecording() {
        if (mediaRecorder != null && isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                
                long duration = System.currentTimeMillis() - startTime;
                
                if (listener != null) {
                    listener.onRecordingComplete(outputFilePath, duration);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onRecordingError("Failed to stop recording: " + e.getMessage());
                }
            } finally {
                mediaRecorder = null;
                isRecording = false;
                
                // Stop duration timer
                if (durationRunnable != null) {
                    handler.removeCallbacks(durationRunnable);
                }
            }
        }
    }
    
    /**
     * Cancel recording and delete file
     */
    public void cancelRecording() {
        if (mediaRecorder != null && isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                // Ignore
            } finally {
                mediaRecorder = null;
                isRecording = false;
                
                // Delete file
                if (outputFilePath != null) {
                    File file = new File(outputFilePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                
                // Stop duration timer
                if (durationRunnable != null) {
                    handler.removeCallbacks(durationRunnable);
                }
            }
        }
    }
    
    /**
     * Check if currently recording
     */
    public boolean isRecording() {
        return isRecording;
    }
    
    /**
     * Get output file path
     */
    public String getOutputFilePath() {
        return outputFilePath;
    }
    
    /**
     * Format duration in ms to MM:SS
     */
    public static String formatDuration(long durationMs) {
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}

