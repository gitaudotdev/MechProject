package com.mercury.ubermechanic.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercury.ubermechanic.R;


public class NewsItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView titleTv;
    public TextView messageTv;
    public TextView timeTv;

    public NewsItemViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);

        titleTv = itemView.findViewById(R.id.titleTv);
        messageTv = itemView.findViewById(R.id.messageTv);
        timeTv = itemView.findViewById(R.id.timeTv);
    }
}
