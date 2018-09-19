package com.mercury.ubermechanic.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercury.ubermechanic.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView jobId;

    public HistoryViewHolder(View itemView) {
        super(itemView);

        jobId = itemView.findViewById(R.id.jobId);
    }
}
