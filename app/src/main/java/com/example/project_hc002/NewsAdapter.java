package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    Context context;
    List<News> newsList;
    private String layoutType;
    ApiService apiService;

    public NewsAdapter(Context context, List<News> newsList, String layoutType) {
        this.context = context;
        this.newsList = newsList;
        this.layoutType = layoutType;
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if ("home".equals(layoutType)) {
            view = layoutInflater.inflate(R.layout.item_news, null);
        } else {
            view = layoutInflater.inflate(R.layout.item_nwcard, parent, false);
        }
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        if ("home".equals(layoutType)) {
            if (holder.newstitle2 != null) {
                holder.newstitle2.setText(news.getTruncatedTitle());
            }
            if (holder.newspic != null && news.getImage() != null) {
                // Convert base64 to Bitmap
                byte[] decodedString = Base64.decode(news.getImage().split(",")[1], Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.newspic.setImageBitmap(decodedByte);
            }

            if (holder.cardNews != null) {
                holder.cardNews.setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), NewsDetailActivity.class);
                    intent.putExtra("news_title", news.getTitle());
                    intent.putExtra("news_desc", news.getDescription());
                    intent.putExtra("news_date", news.getCreatedAt());
                    intent.putExtra("news_image", news.getImage());
                    intent.putExtra("news_author", news.getAuthor());
                    view.getContext().startActivity(intent);
                });
            }
        }

        if ("newsAll".equals(layoutType)) {
            if (holder.newstitle != null) {
                holder.newstitle.setText(news.getTitle());
            }
            if (holder.newsdesc != null) {
                holder.newsdesc.setText(news.getDescription());
            }
            if (holder.newspicc != null && news.getImage() != null) {
                // Convert base64 to Bitmap
                byte[] decodedString = Base64.decode(news.getImage().split(",")[1], Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.newspicc.setImageBitmap(decodedByte);
            }

            if (holder.cardnw != null) {
                holder.cardnw.setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), NewsDetailActivity.class);
                    intent.putExtra("news_title", news.getTitle());
                    intent.putExtra("news_desc", news.getDescription());
                    intent.putExtra("news_date", news.getCreatedAt());
                    intent.putExtra("news_image", news.getImage());
                    intent.putExtra("news_author", news.getAuthor());
                    view.getContext().startActivity(intent);
                });
            }
        }
    }
    public int getItemNews() {return newsList.size();}

    //    class NewsItems extends RecylerView.ViewHolder {
//        TextView newstitle, newsdec, newtitle2;
//        LinearLayout cardNews;
//        public NewsViewHolder(@NonNull View itemView) {
//            super(itemView);
//            newstitle = itemView.findViewById(R.id.nwTitle);
//            newsdesc = itemView.findViewById(R.id.nwDet);
//            newspic = itemView.findViewById(R.id.imgNw);
//            newstitle2 = itemView.findViewById(R.id.newstitle2);
//        })
//    }
    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newstitle, newsdesc, newstitle2;
        ImageView newspic, newspicc;
        LinearLayout cardNews;
        CardView cardnw;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newstitle = itemView.findViewById(R.id.nwTitle);
            newsdesc = itemView.findViewById(R.id.nwDet);
            newspicc = itemView.findViewById(R.id.imgNw);
            newspic = itemView.findViewById(R.id.newspic);
            newstitle2 = itemView.findViewById(R.id.newstitle2);
            cardNews = itemView.findViewById(R.id.layNews);
            cardnw = itemView.findViewById(R.id.cardNw);
        }
    }

    public void setNews(List<News> newsList) {
        this.newsList = newsList;
    }
}
