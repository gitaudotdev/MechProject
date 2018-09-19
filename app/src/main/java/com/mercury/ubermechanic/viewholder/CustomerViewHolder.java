package com.mercury.ubermechanic.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercury.ubermechanic.R;


public class CustomerViewHolder extends RecyclerView.ViewHolder {

    public TextView nameTv;
    public TextView phoneTv;
    public TextView emailTv;



    public CustomerViewHolder(View itemView) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.nameTv);
        phoneTv = itemView.findViewById(R.id.phoneTv);
        emailTv = itemView.findViewById(R.id.emailTv);


    }
}
