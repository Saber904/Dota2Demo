package com.eleven.dota2demo;

import com.eleven.dota2demo.data.HeroDetail;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by wusipeng on 17/5/19.
 */

public interface Dota2Service {
    @GET("GetHeroes/v0001/")
    Call<ResponseBody> getHeroes(@Query("key") String key, @Query("language") String language);

    @GET("http://www.dota2.com/jsfeed/heropickerdata?l=chinese")
    Call<ResponseBody> getHeropickerdata();

    @GET("http://www.dota2.com/jsfeed/abilitydata?l=chinese")
    Call<ResponseBody> getAbilitydata();

//    @GET("GetHeroes/v0001/")
//    Call<List<HeroNamesWrapper>> getHeroes(@Query("key") String key, @Query("language") String language);
}
