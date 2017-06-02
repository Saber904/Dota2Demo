package com.eleven.dota2demo.data.source;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import com.eleven.dota2demo.Dota2Service;
import com.eleven.dota2demo.data.HeroDetail;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wusipeng on 17/5/22.
 */

public class HeroDetailRepository {
    private static HeroDetailRepository instance = null;
    private Map<String, HeroDetail> heroDetails = null; //改成存数据库
    private boolean mCacheIsDirty = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    private HeroDetailRepository() {
    }

    public static HeroDetailRepository getInstance() {
        if (instance == null) {
            instance = new HeroDetailRepository();
        }
        return instance;
    }

    public void getHeroDetail(final String heroName, final DataLoadCallback<HeroDetail> callback) {
        if (heroDetails != null && !mCacheIsDirty) {
            HeroDetail heroDetail = heroDetails.get(heroName);
            if (heroDetail == null) {
                mCacheIsDirty = true;
                getHeroDetail(heroName, callback);
            }
            if (callback != null) {
                callback.onDataLoaded(heroDetail);
            }
        }
        if (mCacheIsDirty) {
            getHeroDetailsFromRemote(new DataLoadCallback<Map<String, HeroDetail>>() {
                @Override
                public void onDataLoaded(Map<String, HeroDetail> data) {
                    heroDetails = data;
                    mCacheIsDirty = false;
                    if (callback != null) {
                        callback.onDataLoaded(heroDetails.get(heroName));
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    if (callback != null) {
                        callback.onDataNotAvailable();
                    }
                }
            });
        } else {
            getHeroDetailsFromLocal(new DataLoadCallback<Map<String, HeroDetail>>() {
                @Override
                public void onDataLoaded(Map<String, HeroDetail> data) {
                    heroDetails = data;
                    if (callback != null) {
                        callback.onDataLoaded(heroDetails.get(heroName));
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    mCacheIsDirty = true;
                    getHeroDetail(heroName, callback);
                }
            });
        }
    }

    public interface DataLoadCallback <T> {
        void onDataLoaded(T data);
        void onDataNotAvailable();
    }

    private void getHeroDetailsFromLocal(DataLoadCallback<Map<String, HeroDetail>> callback) {
        //// TODO: 17/5/24 从数据库读取
        callback.onDataNotAvailable();
    }

    private void getHeroDetailsFromRemote(final DataLoadCallback<Map<String, HeroDetail>> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final Map<String, HeroDetail> heroDetails = new LinkedHashMap<>();
                Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.steampowered.com/IEconDOTA2_570/").addConverterFactory(GsonConverterFactory.create()).build();
                Dota2Service service = retrofit.create(Dota2Service.class);
                Call<ResponseBody> heropickerCall = service.getHeropickerdata();
                Call<ResponseBody> abilityCall = service.getAbilitydata();
                try {
                    Response<ResponseBody> heroResponse = heropickerCall.execute();
                    Response<ResponseBody> abilityResponse = abilityCall.execute();
                    try {
                        Gson gson = new Gson();
                        JsonObject heroJsonObject = new JsonParser().parse(heroResponse.body().string()).getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> heroJsonSet = heroJsonObject.entrySet();

                        JsonObject abilityJsonObject = new JsonParser().parse(abilityResponse.body().string()).getAsJsonObject().getAsJsonObject("abilitydata");
                        Set<Map.Entry<String, JsonElement>> abilityJsonSet = abilityJsonObject.entrySet();
                        for(Map.Entry entry : heroJsonSet) {
                            HeroDetail heroDetail = gson.fromJson((JsonElement)entry.getValue(), HeroDetail.class);
                            heroDetails.put((String)entry.getKey(), heroDetail);
                        }
                        for(Map.Entry entry : abilityJsonSet) {
                            String abilityName = (String)entry.getKey();
                            if (!abilityName.startsWith("special_bonus")) {
                                HeroDetail.Ability ability = gson.fromJson((JsonElement) entry.getValue(), HeroDetail.Ability.class);
                                ability.name = abilityName;
                                int endIndex = abilityName.indexOf("_");
                                String heroName = abilityName.substring(0, endIndex);
                                if (heroDetails.containsKey(heroName)) {
                                    heroDetails.get(heroName).addAbility(ability);
                                } else {
                                    endIndex = abilityName.indexOf("_", endIndex + 1);
                                    if (endIndex != -1) {
                                        heroName = abilityName.substring(0, endIndex);
                                        if (heroDetails.containsKey(heroName)) {
                                            heroDetails.get(heroName).addAbility(ability);
                                        }
                                    }

                                }
                            }
                        }
                        //// TODO: 17/5/24 写入数据库
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback!=null) {
                                    callback.onDataLoaded(heroDetails);
                                }
                            }
                        });

                    } catch (IOException e) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback!=null) {
                                    callback.onDataNotAvailable();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                heropickerCall.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        try {
//                            Gson gson = new Gson();
//                            JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
//                            Set<Map.Entry<String, JsonElement>> jsonSet = jsonObject.entrySet();
//                            for(Map.Entry entry : jsonSet) {
//                                HeroDetail heroDetail = gson.fromJson((JsonElement)entry.getValue(), HeroDetail.class);
//                                heroDetails.put((String)entry.getKey(), heroDetail);
//                            }
//                            //// TODO: 17/5/24 写入数据库
//                            if (callback!=null) {
//                                callback.onDataLoaded(heroDetails);
//                            }
//                        } catch (IOException e) {
//                            if (callback!=null) {
//                                callback.onDataNotAvailable();
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        if (callback!=null) {
//                            callback.onDataNotAvailable();
//                        }
//                    }
//                });
            }
        });

    }
}
