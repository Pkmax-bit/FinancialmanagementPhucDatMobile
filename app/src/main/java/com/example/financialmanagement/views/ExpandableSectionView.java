package com.example.financialmanagement.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.financialmanagement.R;

public class ExpandableSectionView extends LinearLayout {

    private TextView textTitle;
    private ImageView imageIcon;
    private ImageView imageArrow;
    private FrameLayout contentContainer;
    private View header;
    private boolean isExpanded = false;

    public ExpandableSectionView(Context context) {
        super(context);
        init(context, null);
    }

    public ExpandableSectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_expandable_section, this, true);
        
        textTitle = findViewById(R.id.text_section_title);
        imageIcon = findViewById(R.id.image_section_icon);
        imageArrow = findViewById(R.id.image_expand_arrow);
        contentContainer = findViewById(R.id.section_content);
        header = findViewById(R.id.section_header);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableSectionView);
            String title = a.getString(R.styleable.ExpandableSectionView_sectionTitle);
            int iconRes = a.getResourceId(R.styleable.ExpandableSectionView_sectionIcon, R.drawable.ic_info);
            
            textTitle.setText(title);
            imageIcon.setImageResource(iconRes);
            a.recycle();
        }

        header.setOnClickListener(v -> toggle());
    }

    public void toggle() {
        if (isExpanded) {
            collapse();
        } else {
            expand();
        }
    }

    public void expand() {
        contentContainer.setVisibility(VISIBLE);
        rotateArrow(0, 90);
        isExpanded = true;
    }

    public void collapse() {
        contentContainer.setVisibility(GONE);
        rotateArrow(90, 0);
        isExpanded = false;
    }

    private void rotateArrow(float from, float to) {
        RotateAnimation rotate = new RotateAnimation(from, to, 
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        imageArrow.startAnimation(rotate);
    }

    public void setContentView(View view) {
        contentContainer.removeAllViews();
        contentContainer.addView(view);
    }
    
    public ViewGroup getContainer() {
        return contentContainer;
    }
}
