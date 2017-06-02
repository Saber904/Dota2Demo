package com.eleven.dota2demo.herodetail;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleven.dota2demo.R;
import com.eleven.dota2demo.data.HeroDetail;
import com.eleven.dota2demo.data.source.HeroDetailRepository;
import com.squareup.picasso.Picasso;

public class HeroDetailActivity extends Activity {
    private static final String TAG = HeroDetailActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private ImageView mHeroView;
    private TextView mBioView;
    private TextView mTypeView;
    private RecyclerView mAbilityView;
    private TextView mAbilityIntroView;
    private HeroAbilityAdapter mHeroAbilityAdapter;

    private String mHeroName;
    private String mHeroCNName;

    private HeroDetailRepository heroDetailRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_detail);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mHeroView = (ImageView)findViewById(R.id.app_bar_image);
        mBioView = (TextView)findViewById(R.id.hero_bio);
        mTypeView = (TextView)findViewById(R.id.hero_type);
        mAbilityView = (RecyclerView)findViewById(R.id.ability_list_view);
        mAbilityIntroView = (TextView)findViewById(R.id.ability_intro);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        mAbilityView.setLayoutManager(layoutManager);

        mHeroAbilityAdapter = new HeroAbilityAdapter();
        mAbilityView.setAdapter(mHeroAbilityAdapter);

        mHeroName = getIntent().getStringExtra("HERO_NAME");
        mHeroCNName = getIntent().getStringExtra("HERO_CN_NAME");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        mToolbar.setTitle(mHeroCNName);

        heroDetailRepository = HeroDetailRepository.getInstance();
        heroDetailRepository.getHeroDetail(mHeroName, new HeroDetailRepository.DataLoadCallback<HeroDetail>() {
            @Override
            public void onDataLoaded(final HeroDetail data) {
                mHeroAbilityAdapter.setAbilities(data.abilities);
                mHeroAbilityAdapter.notifyDataSetChanged();
                mHeroAbilityAdapter.setAbilityClickListener(new OnAbilityClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        mAbilityIntroView.setVisibility(View.VISIBLE);
                        HeroDetail.Ability ability = data.abilities.get(position);
                        mAbilityIntroView.setText(ability.dname);
                        mAbilityIntroView.append("\n");
                        mAbilityIntroView.append(Html.fromHtml(ability.desc));
                        if (!TextUtils.isEmpty(ability.affects)) {
                            mAbilityIntroView.append("\n");
                            mAbilityIntroView.append(Html.fromHtml(ability.affects));
                        }
                        if (!TextUtils.isEmpty(ability.attrib)) {
                            mAbilityIntroView.append("\n");
                            mAbilityIntroView.append(Html.fromHtml(ability.attrib));
                        }
                        if (!TextUtils.isEmpty(ability.cmb)) {
                            mAbilityIntroView.append("\n");
                            mAbilityIntroView.append(Html.fromHtml(ability.cmb));
                        }
                    }
                });

                mBioView.setText(data.bio);
//                mBioView.setText(Html.fromHtml("<div class=\\\"cooldownMana\\\"><div class=\\\"mana\\\"><img alt=\\\"魔法消耗\\\" title=\\\"魔法消耗\\\" class=\\\"manaImg\\\" src=\\\"http://cdn.dota2.com/apps/dota2/images/tooltips/mana.png\\\" width=\\\"16\\\" height=\\\"16\\\" border=\\\"0\\\" /> 60/60/60/60</div><div class=\\\"cooldown\\\"><img alt=\\\"冷却时间\\\" title=\\\"冷却时间\\\" class=\\\"cooldownImg\\\" src=\\\"http://cdn.dota2.com/apps/dota2/images/tooltips/cooldown.png\\\" width=\\\"16\\\" height=\\\"16\\\" border=\\\"0\\\" /> 12/9/7/5</div><br clear=\\\"left\\\" /></div>"));
                StringBuilder typeBuilder = new StringBuilder();
                typeBuilder.append(data.atk_l);
                typeBuilder.append(",");
                for(String role : data.roles_l) {
                    typeBuilder.append(role);
                    typeBuilder.append(",");
                }
                mTypeView.setText(typeBuilder.toString());


            }

            @Override
            public void onDataNotAvailable() {
                Log.i(TAG, "NO DATA");
            }
        });

        Picasso.with(this).load(String.format("http://cdn.dota2.com.cn/apps/dota2/images/heroes/%s_full.png", mHeroName)).into(mHeroView);
    }
}
