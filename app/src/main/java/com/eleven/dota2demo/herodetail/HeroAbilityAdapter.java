package com.eleven.dota2demo.herodetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eleven.dota2demo.R;
import com.eleven.dota2demo.data.HeroDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wusipeng on 17/5/24.
 */

public class HeroAbilityAdapter extends RecyclerView.Adapter<HeroAbilityAdapter.HeroAbilityViewHolder> {

    private List<HeroDetail.Ability> abilities;
    private Context mContext;
    private OnAbilityClickListener mClickListener;

    public void setAbilityClickListener(OnAbilityClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public HeroAbilityAdapter(List<HeroDetail.Ability> abilities) {
        this.abilities = abilities;
    }

    public HeroAbilityAdapter() {
        abilities = new ArrayList<>();
    }

    public List<HeroDetail.Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<HeroDetail.Ability> abilities) {
        this.abilities = abilities;
    }

    private String imageUrlFromAbility(HeroDetail.Ability ability) {
        return String.format("http://cdn.dota2.com/apps/dota2/images/abilities/%s_hp1.png",ability.name);
    }

    @Override
    public HeroAbilityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.ability_list_view_item,null);

        return new HeroAbilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HeroAbilityViewHolder holder, int position) {
        holder.position = position;
        Picasso.with(mContext).load(imageUrlFromAbility(abilities.get(position))).into(holder.abilityView);
    }

    @Override
    public int getItemCount() {
        return abilities.size();
    }

    class HeroAbilityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView abilityView;
        int position;

        public HeroAbilityViewHolder(View itemView) {
            super(itemView);
            abilityView = (ImageView)itemView.findViewById(R.id.ability_image_view);
            abilityView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            abilityView.setFocusableInTouchMode(true);
            abilityView.requestFocus();
            if (mClickListener != null)
                mClickListener.onItemClick(position);
            abilityView.setFocusableInTouchMode(false);
        }
    }
}
