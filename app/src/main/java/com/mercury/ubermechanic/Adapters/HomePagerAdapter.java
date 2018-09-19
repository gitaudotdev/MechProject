package com.mercury.ubermechanic.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mercury.ubermechanic.Model.ADs;
import com.mercury.ubermechanic.R;

import java.util.List;


public class HomePagerAdapter extends PagerAdapter {
    Context ctx;
    LayoutInflater layoutInflater;

    List<ADs> images;

    public HomePagerAdapter(Context context, List<ADs> images ) {
        ctx = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.ad_pager_item, container, false);
        ImageView imageView =  itemView.findViewById(R.id.imageView);
        imageView.setImageResource(images.get(position).getDrawable());
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
