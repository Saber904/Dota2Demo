package com.eleven.dota2demo.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by wusipeng on 17/5/22.
 */

public class HeroDetail {
    public String name;
    public String bio;
    public String atk;
    public String atk_l;
    public String[] roles;
    public String[] roles_l;

    public List<Ability> abilities = new ArrayList<>();

    public static class Ability {
        public String dname;
        public String affects;
        public String desc;
        public String notes;
        public String dmg;
        public String attrib;
        public String cmb;
        public String lure;
        public String hurl;
        public String name;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public String dname;
    public String pa;
    public Map attribs;
    public String dac;
    public String droles;

    public static class Attribs {
        Map<String, String> str;
        Map<String, String> iNT;
        Map<String, String> agi;
        Map<String, String> dmg;
        String ms;
        String armor;
    }
}
