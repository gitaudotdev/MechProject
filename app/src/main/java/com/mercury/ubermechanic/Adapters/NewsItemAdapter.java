package com.mercury.ubermechanic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercury.ubermechanic.Common.Common;
import com.mercury.ubermechanic.Model.NewsItem;
import com.mercury.ubermechanic.R;
import com.mercury.ubermechanic.WebActivity;
import com.mercury.ubermechanic.viewholder.NewsItemViewHolder;

import java.util.List;


public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemViewHolder> {
    Context ctx;
    List<NewsItem> newsItems;

    public NewsItemAdapter(Context ctx, List<NewsItem> newsItems) {
        this.ctx = ctx;
        this.newsItems = newsItems;
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsItemViewHolder holder, int position) {
        final NewsItem item = newsItems.get(position);

        //        holder.imageView.setImageResource(item.getDrawable());

        holder.titleTv.setText(item.getTitle());
        holder.messageTv.setText(item.getMessage());
        holder.timeTv.setText(item.getTime());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, WebActivity.class);
                i.putExtra("title",item.getTitle());
                i.putExtra("url", Common.newsUrl);
                ctx.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }
}
