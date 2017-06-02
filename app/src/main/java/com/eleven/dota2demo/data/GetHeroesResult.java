package com.eleven.dota2demo.data;

import java.util.List;

/**
 * Created by wusipeng on 17/5/19.
 */

public class GetHeroesResult {
    public List<HeroNameWrapper> heroes;
    public String status;
    public String count;
    public static class HeroNameWrapper {
        public String name;
        public String id;
        public String localized_name;
    }
}
