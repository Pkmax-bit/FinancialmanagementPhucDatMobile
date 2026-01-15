package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import java.util.List;

/**
 * Adapter for displaying emojis in grid
 */
public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {
    
    private List<String> emojis;
    private OnEmojiClickListener listener;
    
    public interface OnEmojiClickListener {
        void onEmojiClick(String emoji);
    }
    
    public EmojiAdapter(List<String> emojis, OnEmojiClickListener listener) {
        this.emojis = emojis;
        this.listener = listener;
    }
    
    public void updateEmojis(List<String> newEmojis) {
        this.emojis = newEmojis;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Simple TextView for emoji
        TextView textView = new TextView(parent.getContext());
        textView.setTextSize(32);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        int padding = (int) (8 * parent.getContext().getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);
        
        // Add ripple effect
        android.util.TypedValue outValue = new android.util.TypedValue();
        parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        textView.setBackgroundResource(outValue.resourceId);
        
        return new EmojiViewHolder(textView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        String emoji = emojis.get(position);
        holder.textEmoji.setText(emoji);
        holder.textEmoji.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmojiClick(emoji);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return emojis != null ? emojis.size() : 0;
    }
    
    static class EmojiViewHolder extends RecyclerView.ViewHolder {
        TextView textEmoji;
        
        EmojiViewHolder(View itemView) {
            super(itemView);
            textEmoji = (TextView) itemView;
        }
    }
}


