package com.mercury.ubermechanic.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.mercury.ubermechanic.Adapters.HomePagerAdapter;
import com.mercury.ubermechanic.Adapters.MenuItemAdapter;
import com.mercury.ubermechanic.Adapters.NewsItemAdapter;
import com.mercury.ubermechanic.Model.ADs;
import com.mercury.ubermechanic.Model.MenuItem;
import com.mercury.ubermechanic.Model.NewsItem;
import com.mercury.ubermechanic.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    View root;
    RecyclerView menuRecyclerView, newsRecyclerView;
    List<MenuItem> menuItemList =new ArrayList<>();
    List<ADs> adImages = new ArrayList<>();


    HomePagerAdapter adapter;
    ScrollView scrollView;
    View up,down;

    boolean isUp =true;


    public Home() {
        // Required empty public constructor
    }

    public static Home getInstance(){
        return new Home();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_home, container, false);
        setUpViews();
        return root;

    }
    private void setUpViews(){
        setUpViewPager();
        setUpMenu();
        setUpNews();

        View targetView =root.findViewById(R.id.focus);
        targetView.getParent().requestChildFocus(targetView,targetView);

        scrollView =root.findViewById(R.id.scrollView);
        up= root.findViewById(R.id.up);
        down =root.findViewById(R.id.down);

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusOnView(newsRecyclerView);
            }
        });
    }
        private final void focusOnView(final View view){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,view.getBottom());
            }
        });
        }
        private final void focusOnViewTop(final View view){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,view.getTop());
            }
        });

        }
        @SuppressLint("ClickableViewAccessibility")
        private void setUpViewPager(){
        setAdImages (adImages);
            ViewPager viewPager =root.findViewById(R.id.viewPager);
            adapter =new HomePagerAdapter(getContext(),adImages);
            viewPager.setAdapter(adapter);
            setUpSwitcher(viewPager);

            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    public void setUpSwitcher(final ViewPager viewPager){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ((viewPager.getCurrentItem()+1) == adImages.size()){
                                viewPager.setCurrentItem(0);
                            } else{
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            }
                        }
                    });
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        },100,3000);



    }

        private void setUpMenu(){
        setMenuItemList(menuItemList);
        menuRecyclerView =root.findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        menuRecyclerView.setAdapter(new MenuItemAdapter(getContext(),menuItemList));
        }

        private void setUpNews(){
        newsRecyclerView =root.findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsRecyclerView.setAdapter(new NewsItemAdapter(getContext(),getNews()));
        }

    private List<NewsItem> getNews() {
        List<NewsItem> newsItems = new ArrayList<>();
//        for(int i=0;i<newsItems.size();i++){
//            NewsItem newsItem = new NewsItem();
//            newsItem.getTitle();
//            newsItem.getMessage();
//            newsItem.getTime();
//            newsItem.getDrawable();
//        }
        newsItems.add(new NewsItem("Lorem ipsum dolor sit amet","Lorem ipsum dolor sit amet, consectetur adipiscing elit.","10:00pm"));
        newsItems.add(new NewsItem("Lorem ipsum dolor sit amet","Lorem ipsum dolor sit amet, consectetur adipiscing elit.","10:00pm"));
        newsItems.add(new NewsItem("Lorem ipsum dolor sit amet","Lorem ipsum dolor sit amet, consectetur adipiscing elit.","10:00pm"));
        newsItems.add(new NewsItem("Lorem ipsum dolor sit amet","Lorem ipsum dolor sit amet, consectetur adipiscing elit.","10:00pm"));
        return  newsItems;
    }

    public void setMenuItemList(List<MenuItem> menuItemList) {
        menuItemList.clear();

//         menuItemList.add(new MenuItem(1, "TO DO", R.drawable.ic_todo_list));

    }


    public void setAdImages(List<ADs> adImages) {
        adImages.clear();

        adImages.add(new ADs(R.drawable.mechanic_ad));
        adImages.add(new ADs(R.drawable.working_man));
        adImages.add(new ADs(R.drawable.more_info));
        adImages.add(new ADs(R.drawable.parts_and_services));

        adImages.add(new ADs(R.drawable.mechanic_ad));
        adImages.add(new ADs(R.drawable.working_man));
        adImages.add(new ADs(R.drawable.more_info));
        adImages.add(new ADs(R.drawable.parts_and_services));

        adImages.add(new ADs(R.drawable.mechanic_ad));
        adImages.add(new ADs(R.drawable.working_man));
        adImages.add(new ADs(R.drawable.more_info));
        adImages.add(new ADs(R.drawable.parts_and_services));


       this.adImages = adImages;
    }

}
