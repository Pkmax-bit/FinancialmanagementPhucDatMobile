package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.MessageReaction;
import java.util.List;

/**
 * Adapter for displaying message reactions
 */
public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder> {
    
    private List<MessageReaction> reactions;
    private String currentUserId;
    private OnReactionClickListener listener;
    
    public interface OnReactionClickListener {
        void onReactionClick(MessageReaction reaction);
    }
    
    public ReactionAdapter(List<MessageReaction> reactions, String currentUserId, OnReactionClickListener listener) {
        this.reactions = reactions;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_reaction, parent, false);
        return new ReactionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder, int position) {
        MessageReaction reaction = reactions.get(position);
        holder.bind(reaction, currentUserId, listener);
    }
    
    @Override
    public int getItemCount() {
        return reactions != null ? reactions.size() : 0;
    }
    
    public void updateReactions(List<MessageReaction> newReactions) {
        this.reactions = newReactions;
        notifyDataSetChanged();
    }
    
    static class ReactionViewHolder extends RecyclerView.ViewHolder {
        TextView textEmoji;
        TextView textCount;
        
        ReactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmoji = itemView.findViewById(R.id.text_emoji);
            textCount = itemView.findViewById(R.id.text_count);
        }
        
        void bind(MessageReaction reaction, String currentUserId, OnReactionClickListener listener) {
            textEmoji.setText(reaction.getEmoji());
            textCount.setText(String.valueOf(reaction.getCount()));
            
            // Highlight if current user has reacted
            boolean userReacted = reaction.hasUserReacted(currentUserId);
            itemView.setSelected(userReacted);
            
            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReactionClick(reaction);
                }
            });
        }
    }
}

