package com.vg.api;

import java.io.Serializable;
import java.util.List;

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
        public Goods(String name, String cover, Payload sku)
        {
            this.gid   = name;
            this.name  = name;
            this.cover = cover;
            this.pay   = sku;
        }

        public String gid;        //goods global id
        public String name;       //goods name
        public String cover;      //cover url

        public Payload pay;
    }

    public enum PayType{
        FREE, GOOGLE_PLAYER, APPLE_STORE,ALI_PAY, VIRTUAL_CURRENCY_COIN,VIRTUAL_CURRENCY_DIAMOND
    };

    public static Payload FreePayLoad =new VGOpenAPI.Payload("Free", "no sku", PayType.FREE);

    public static class Payload implements Serializable{
        public String   displayName;
        public String   sku;
        public PayType  type;



        public Payload(String displayName, String sku_string, PayType paytype)
        {
            this.displayName = displayName;
            this.sku = sku_string;
            this.type = paytype;
        }
    }

    public static class TestPayLoadInGoogldePlay{
        public static Payload google_099 = new Payload("Google 0.99", "goods_099",  PayType.GOOGLE_PLAYER);
        public static Payload google_149 = new Payload("Google 1.49", "goods_1.49", PayType.GOOGLE_PLAYER);
        public static Payload google_199 = new Payload("Google 1.99", "goods_1.99", PayType.GOOGLE_PLAYER);
        public static Payload google_299 = new Payload("Google 2.99", "goods_2.99", PayType.GOOGLE_PLAYER);
        public static Payload google_499 = new Payload("Google 4.99", "goods_4.99", PayType.GOOGLE_PLAYER);

        public static Payload google_coin_099 = new Payload("Google 0.99", "gold_coin_099", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_149 = new Payload("Google 1.49", "gold_coin_1.49", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_199 = new Payload("Google 1.99", "gold_coin_1.99", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_249 = new Payload("Google 2.49", "gold_coin_2.49", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_299 = new Payload("Google 2.99", "gold_coin_2.99", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_349 = new Payload("Google 3.49", "gold_coin_3.49", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_449 = new Payload("Google 4.49", "gold_coin_4.49", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_499 = new Payload("Google 4.99", "gold_coin_4.99", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_599 = new Payload("Google 5.99", "gold_coin_5.99", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_799 = new Payload("Google 7.99", "gold_coin_7.99", PayType.GOOGLE_PLAYER);
        public static Payload google_coin_999 = new Payload("Google 9.99", "gold_coin_9.99", PayType.GOOGLE_PLAYER);

        public static Payload google_diamond_999 = new Payload("Google 9.99", "diamond_9.99", PayType.GOOGLE_PLAYER);
        public static Payload google_diamond_499 = new Payload("Google 4.99", "diamond_4.99", PayType.GOOGLE_PLAYER);

        public static Payload google_subscribe_199_year  = new Payload("Google subscribe 1.99 year", "subcribe_1.99_year", PayType.GOOGLE_PLAYER);
        public static Payload google_subscribe_099_month = new Payload("Google subscribe 0.99 month", "subcribe_0.99_month", PayType.GOOGLE_PLAYER);


        //for virtual currency payment
        public static Payload coin_100   = new Payload("Gold coin 100",   "gold_coin_100",   PayType.VIRTUAL_CURRENCY_COIN);
        public static Payload coin_200   = new Payload("gold coin 200",   "gold_coin_200",   PayType.VIRTUAL_CURRENCY_COIN);
        public static Payload coin_10000 = new Payload("gold coin 10000", "gold_coin_10000", PayType.VIRTUAL_CURRENCY_COIN);

        public static Payload diamond_1   = new Payload("diamond 1",   "diamond_1",   PayType.VIRTUAL_CURRENCY_DIAMOND);
        public static Payload diamond_2   = new Payload("diamond 2",   "diamond_2",   PayType.VIRTUAL_CURRENCY_DIAMOND);
        public static Payload diamond_5   = new Payload("diamond 5",   "diamond_5",   PayType.VIRTUAL_CURRENCY_DIAMOND);

    }

    public static class Receipt{
        public String orderNumber;
    }


}
