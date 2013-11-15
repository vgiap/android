package com.vg.api;

import java.io.Serializable;

/**
 * Created by huadong on 11/14/13.
 *
 *
 * will move to api lib project
 */
public class VGOpenAPI {

    /*
     *Serializable is to enable pass the data between android component
     *
     */
    public static class Goods implements Serializable
    {
        //for test will remove later
        public Goods(String name, String cover)
        {
            gid =name;
            this.name = name;
            this.cover = cover;
        }

        public String gid;        //goods global id
        public String name;       //goods name
        public String cover;      //cover url
    }
}
