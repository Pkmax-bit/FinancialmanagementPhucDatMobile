package com.example.financialmanagement.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Member;
import java.util.List;

public class AvatarStackView extends LinearLayout {
    private int avatarSize;
    private int overlap;

    public AvatarStackView(Context context) {
        super(context);
        init();
    }

    public AvatarStackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        avatarSize = (int) (28 * getResources().getDisplayMetrics().density);
        overlap = (int) (8 * getResources().getDisplayMetrics().density);
    }

    public void setMembers(List<Member> members) {
        removeAllViews();
        if (members == null) return;

        int limit = 4;
        for (int i = 0; i < Math.min(members.size(), limit); i++) {
            Member member = members.get(i);
            TextView textView = new TextView(getContext());
            LayoutParams params = new LayoutParams(avatarSize, avatarSize);
            if (i > 0) {
                params.setMargins(-overlap, 0, 0, 0);
            }
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.drawable.bg_avatar_circle);
            textView.setGravity(Gravity.CENTER);
            textView.setText(member.getInitials());
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(10);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            
            try {
                if (member.getColor() != null) {
                    textView.getBackground().setTint(Color.parseColor(member.getColor()));
                }
            } catch (Exception e) {}

            addView(textView);
        }

        if (members.size() > limit) {
            TextView textView = new TextView(getContext());
            LayoutParams params = new LayoutParams(avatarSize, avatarSize);
            params.setMargins(-overlap, 0, 0, 0);
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.drawable.bg_avatar_circle);
            textView.getBackground().setTint(Color.parseColor("#F8F9FA"));
            textView.setGravity(Gravity.CENTER);
            textView.setText("+" + (members.size() - limit));
            textView.setTextColor(Color.parseColor("#6C757D"));
            textView.setTextSize(10);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            addView(textView);
        }
    }
}
