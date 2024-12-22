package com.example.project_hc002;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SlideViewPagerAdapter extends RecyclerView.Adapter<SlideViewPagerAdapter.ViewHolder> {

    Context context;
    int[] images;
    String[] headings;
    String[] descriptions;

    public SlideViewPagerAdapter(Context context, int[] images, String[] headings, String[] descriptions) {
        this.context = context;
        this.images = images;
        this.headings = headings;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slide_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
        holder.headingView.setText(headings[position]);
        holder.descView.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return headings.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView headingView, descView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slideImg);
            headingView = itemView.findViewById(R.id.slideHeading);
            descView = itemView.findViewById(R.id.slideDescription);
        }
    }
}