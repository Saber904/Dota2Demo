package com.eleven.dota2demo.heroes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleven.dota2demo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wusipeng on 17/5/19.
 */

public class HeroesAdapter extends RecyclerView.Adapter<HeroesAdapter.HeroesListViewHolder> {

    private static final String TAG = "HEROSADAPTER";
    private Context mContext;
    private List<String> mHeroesDisplayNameList;
    private List<String> mHeroesEnglishNameList;

    public void setHeroesDisplayNameList(List<String> herosDisplayNameList) {
        this.mHeroesDisplayNameList = herosDisplayNameList;
    }

    public HeroesAdapter() {
        mHeroesDisplayNameList = new ArrayList<>();
    }

    public List<String> getHeroesEnglishNameList() {
        return mHeroesEnglishNameList;
    }

    public void setHeroesEnglishNameList(List<String> mHerosEnglishNameList) {
        this.mHeroesEnglishNameList = mHerosEnglishNameList;
    }

    private String imageUrlFromHero(String hero) {
        return String.format("http://cdn.dota2.com.cn/apps/dota2/images/heroes/%s_lg.png", hero);
    }

    public void setOnHeroClickListener(onHeroClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<String> getHeroesDisplayNameList() {
        return mHeroesDisplayNameList;
    }

    private onHeroClickListener onItemClickListener;

    @Override
    public HeroesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "on Create View holder");
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.heros_list_view_item, null);

        return new HeroesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HeroesListViewHolder holder, int position) {
        Log.i(TAG, "bind view holder");
        holder.position = position;
        holder.heroNameView.setText(mHeroesDisplayNameList.get(position));
        Picasso.with(mContext).load(imageUrlFromHero(mHeroesEnglishNameList.get(position))).into(holder.heroImageView);
    }

    @Override
    public int getItemCount() {
        return mHeroesDisplayNameList.size();
    }

    class HeroesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView heroImageView;
        public TextView heroNameView;
        public int position;

        public HeroesListViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            heroNameView = (TextView)itemView.findViewById(R.id.tv_hero_name);
            heroImageView = (ImageView)itemView.findViewById(R.id.iv_hero_image);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        }
    }
}
