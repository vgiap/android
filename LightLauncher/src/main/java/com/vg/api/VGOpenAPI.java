package com.vg.api;

/**
 * Created by huadong on 11/14/13.
 *
 *
 * will move to api lib project
 */
public class VGOpenAPI {
    public static class Goods
    {
        //for test will remove later
        public Goods(String name, String cover)
        {
            this.name = name;
            this.cover = cover;
        }
        public String gid;        //goods global id
        public String name;       //goods name
        public String cover;      //cover url
    }
}
