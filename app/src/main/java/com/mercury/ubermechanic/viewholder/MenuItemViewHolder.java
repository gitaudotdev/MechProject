package com.mercury.ubermechanic.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercury.ubermechanic.R;


public class MenuItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView nameTv;

    public MenuItemViewHolder(View itemView) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.nameTv);
        imageView = itemView.findViewById(R.id.imageView);
    }
}
