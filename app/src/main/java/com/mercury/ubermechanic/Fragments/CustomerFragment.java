package com.mercury.ubermechanic.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercury.ubermechanic.Adapters.CustomerAdapter;
import com.mercury.ubermechanic.Dialogs.CustomerDialog;
import com.mercury.ubermechanic.Model.Customer;
import com.mercury.ubermechanic.R;

import java.util.ArrayList;




public class CustomerFragment extends Fragment {

    View root;
    RecyclerView recyclerView;
    FloatingActionButton add;

    public CustomerFragment() {
        // Required empty public constructor
    }

    public static CustomerFragment getInstance(){
        return new CustomerFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_customers, container, false);
        setUpViews();
        return root;
    }

    private void setUpViews() {
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CustomerAdapter(getContext(),getCustomer()));

        add = root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomerDialog(getContext(), R.style.FullScreen).showDialog();
            }
        });
    }


    private ArrayList<Customer> getCustomer() {
        ArrayList<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Imran Mungai","0701116801","munga19ihub@gmail.com","2"));
        customers.add(new Customer("Peter Muchemi","0708234567","muchemipeter@gmail.com","3"));
        customers.add(new Customer("John Wainaina","0768789543","john32@gmail.com","4"));
        customers.add(new Customer("Michael M.","0790675925","michael23@gmail.com","1"));
        return customers;
    }


}
