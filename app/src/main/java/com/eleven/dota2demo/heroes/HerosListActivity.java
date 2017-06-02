package com.eleven.dota2demo.heroes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.eleven.dota2demo.Dota2Service;
import com.eleven.dota2demo.R;
import com.eleven.dota2demo.data.GetHeroesResult;
import com.eleven.dota2demo.herodetail.HeroDetailActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HerosListActivity extends AppCompatActivity {
    private static final String TAG = HerosListActivity.class.getSimpleName();
    private RecyclerView herosListView;
    private HeroesAdapter mHeroesAdapter;
    private static final String KEY = "1BC59FDE489868B94EB0BDF217A70DF2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heros_list);
        herosListView = (RecyclerView)findViewById(R.id.heros_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mHeroesAdapter = new HeroesAdapter();
        herosListView.setLayoutManager(layoutManager);
        herosListView.setAdapter(mHeroesAdapter);
        mHeroesAdapter.setOnHeroClickListener(new onHeroClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i(TAG, mHeroesAdapter.getHeroesDisplayNameList().get(position) + " clicked");
                Intent intent = new Intent(HerosListActivity.this, HeroDetailActivity.class);
                intent.putExtra("HERO_NAME", mHeroesAdapter.getHeroesEnglishNameList().get(position));
                intent.putExtra("HERO_CN_NAME", mHeroesAdapter.getHeroesDisplayNameList().get(position));

                startActivity(intent);
            }
        });
//        HeroesAdapter mHeroesAdapter = new HeroesAdapter();
//        mHeroesAdapter.setOnHeroClickListener(new onHeroClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Log.i(TAG, ""+position);
//            }
//        });
        fetchHerosNameList();
//        herosListView.setAdapter(mHeroesAdapter);
    }

    private void fetchHerosNameList() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.steampowered.com/IEconDOTA2_570/").addConverterFactory(GsonConverterFactory.create()).build();
        Dota2Service service = retrofit.create(Dota2Service.class);
        Call<ResponseBody> call = service.getHeroes(KEY, "zh_cn");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
                    GetHeroesResult getHeroesResult = gson.fromJson(jsonObject.get("result"), GetHeroesResult.class);
                    Log.i(TAG, getHeroesResult.count);
                    List<GetHeroesResult.HeroNameWrapper> heroes = getHeroesResult.heroes;
                    List<String> heroCNNames = new ArrayList<>(heroes.size());//可用rxjava重写
                    List<String> heroENNames = new ArrayList<>(heroes.size());
                    for(GetHeroesResult.HeroNameWrapper heroNameWrapper : heroes) {
                        heroCNNames.add(heroNameWrapper.localized_name);
                        heroENNames.add(heroNameWrapper.name.replace("npc_dota_hero_", ""));
                    }
                    mHeroesAdapter.setHeroesDisplayNameList(heroCNNames);
                    mHeroesAdapter.setHeroesEnglishNameList(heroENNames);
                    mHeroesAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }
}
