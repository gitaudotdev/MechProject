package com.mercury.ubermechanic.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercury.ubermechanic.Model.Customer;
import com.mercury.ubermechanic.R;
import com.mercury.ubermechanic.viewholder.CustomerViewHolder;

import java.util.ArrayList;


public class CustomerAdapter extends RecyclerView.Adapter<CustomerViewHolder> {

    Context ctx;
    ArrayList<Customer> customers;

    public CustomerAdapter(Context ctx, ArrayList<Customer> customers) {
        this.ctx = ctx;
        this.customers = customers;
    }



    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);

        holder.nameTv.setText(customer.getName());
        holder.phoneTv.setText(customer.getPhone());
        holder.emailTv.setText(customer.getEmail());


    }

    @Override
    public int getItemCount() {
        return customers.size();
    }
}
