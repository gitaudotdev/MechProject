package com.mercury.ubermechanic.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercury.ubermechanic.R;


public class TodoViewHolder extends RecyclerView.ViewHolder {

    public TextView titleTv;
    public TextView detailTv;
    public TextView dateTv;


    public TodoViewHolder(View itemView) {
        super(itemView);

        titleTv = itemView.findViewById(R.id.titleTv);
        detailTv = itemView.findViewById(R.id.detailTv);
        dateTv = itemView.findViewById(R.id.dateTv);

    }
}
