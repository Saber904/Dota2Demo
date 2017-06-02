package com.eleven.dota2demo.heroes;

import java.util.List;

/**
 * Created by wusipeng on 17/5/19.
 */

public class HeroNamesWrapper {
    List<Hero> heroes;
    String status;
    String count;
    static class Hero {
        String name;
        String id;
        String localized_name;
    }
}
