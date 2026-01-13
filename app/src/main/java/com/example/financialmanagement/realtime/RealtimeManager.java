package com.example.financialmanagement.realtime;

import android.content.Context;
import android.util.Log;
import com.example.financialmanagement.auth.AuthManager;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Realtime Manager - Quản lý kết nối realtime
 * 
 * OPTIMIZED ARCHITECTURE:
 * 1. Smart Polling - Dynamic interval based on activity
 * 2. Connection Pooling - Reuse connections
 * 3. Exponential Backoff - Retry with increasing delays
 * 4. Delta Updates - Only fetch changes since last update
 * 
 * FUTURE: Supabase Realtime WebSocket (when needed for < 100ms latency)
 */
public class RealtimeManager {
    
    private static final String TAG = "RealtimeManager";
    
    // Polling intervals
    public static final int INTERVAL_ACTIVE = 3000;      // 3s when user active
    public static final int INTERVAL_INACTIVE = 10000;   // 10s when idle 1-5 min
    public static final int INTERVAL_BACKGROUND = 30000; // 30s when idle > 5 min
    public static final int INTERVAL_MAX_BACKOFF = 60000; // 60s max on errors
    
    private Context context;
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private List<ConnectionStateListener> stateListeners = new ArrayList<>();
    
    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR,
        RECONNECTING
    }
    
    public interface ConnectionStateListener {
        void onStateChanged(ConnectionState oldState, ConnectionState newState);
    }
    
    public RealtimeManager(Context context) {
        this.context = context;
    }
    
    /**
     * Add connection state listener
     */
    public void addStateListener(ConnectionStateListener listener) {
        if (!stateListeners.contains(listener)) {
            stateListeners.add(listener);
        }
    }
    
    /**
     * Remove connection state listener
     */
    public void removeStateListener(ConnectionStateListener listener) {
        stateListeners.remove(listener);
    }
    
    /**
     * Update connection state
     */
    private void updateState(ConnectionState newState) {
        ConnectionState oldState = this.connectionState;
        this.connectionState = newState;
        
        // Notify listeners
        for (ConnectionStateListener listener : stateListeners) {
            listener.onStateChanged(oldState, newState);
        }
        
        Log.d(TAG, "Connection state: " + oldState + " -> " + newState);
    }
    
    /**
     * Calculate optimal polling interval based on:
     * - User activity (typing, scrolling, sending)
     * - Error count (exponential backoff)
     * - Empty poll count (slow down if no new messages)
     */
    public static int calculateOptimalInterval(
            long lastActivityTime, 
            int errorCount, 
            int emptyPollCount
    ) {
        // Exponential backoff on errors
        if (errorCount > 0) {
            int backoff = INTERVAL_ACTIVE * (int) Math.pow(2, Math.min(errorCount, 5));
            return Math.min(backoff, INTERVAL_MAX_BACKOFF);
        }
        
        // Slow down if many empty polls
        if (emptyPollCount > 10) {
            return INTERVAL_INACTIVE;
        }
        
        // Check user inactivity
        long inactiveTime = System.currentTimeMillis() - lastActivityTime;
        
        if (inactiveTime > 5 * 60 * 1000) {
            // Inactive > 5 min
            return INTERVAL_BACKGROUND;
        } else if (inactiveTime > 60 * 1000) {
            // Inactive > 1 min
            return INTERVAL_INACTIVE;
        }
        
        // User is active
        return INTERVAL_ACTIVE;
    }
    
    /**
     * Get current connection state
     */
    public ConnectionState getConnectionState() {
        return connectionState;
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return connectionState == ConnectionState.CONNECTED;
    }
}

