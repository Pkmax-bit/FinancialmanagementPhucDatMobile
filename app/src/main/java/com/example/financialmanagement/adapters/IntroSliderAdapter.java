package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.IntroSlide;
import java.util.List;

public class IntroSliderAdapter extends RecyclerView.Adapter<IntroSliderAdapter.SliderViewHolder> {
    
    private List<IntroSlide> slides;
    
    public IntroSliderAdapter(List<IntroSlide> slides) {
        this.slides = slides;
    }
    
    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intro_slide, parent, false);
        return new SliderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        IntroSlide slide = slides.get(position);
        holder.title.setText(slide.getTitle());
        holder.description.setText(slide.getDescription());
        holder.image.setImageResource(slide.getImageResource());
        
        // Set gradient background based on position
        int gradientRes;
        switch (position) {
            case 0:
                gradientRes = R.drawable.bg_gradient_purple;
                break;
            case 1:
                gradientRes = R.drawable.bg_gradient_blue;
                break;
            case 2:
                gradientRes = R.drawable.bg_gradient_green;
                break;
            case 3:
                gradientRes = R.drawable.bg_gradient_orange;
                break;
            default:
                gradientRes = R.drawable.bg_gradient_blue;
        }
        holder.container.setBackgroundResource(gradientRes);
    }
    
    @Override
    public int getItemCount() {
        return slides.size();
    }
    
    static class SliderViewHolder extends RecyclerView.ViewHolder {
        View container;
        ImageView image;
        TextView title;
        TextView description;
        
        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.slide_container);
            image = itemView.findViewById(R.id.iv_slide_image);
            title = itemView.findViewById(R.id.tv_slide_title);
            description = itemView.findViewById(R.id.tv_slide_description);
        }
    }
}
