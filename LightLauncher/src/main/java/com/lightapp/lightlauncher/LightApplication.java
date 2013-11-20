package com.lightapp.lightlauncher;

import android.app.Application;
import android.util.Log;

import com.vg.api.VGClient;

import java.util.ArrayList;

/**
 * Created by huadong on 11/18/13.
 */
public class LightApplication extends Application implements VGClient.ConsumableCallBack {
    final String TAG="LightApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "launcher LightApplication");
        VGClient.initBilling(
                this,
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhyB0f3W9fSH7KsvS9S0AmSblEqPXLwLS/e3k+zi+Cqb0ZEMvr+UEWyufVdEdv8PlN0DRQfpPLn3FKb56a62vBhJhe+RyRNM081a1B6A2oGR9YFGa0DWjxStix5ue7bWv4hWU1/lBwV+re65HpXSHA/wGMrA5OFCgVrIgcXABBctCiMDddgSHHTEeu7rAa3GRYRM9ixrdI2XZI8/0k59PaM5/ldhDzLuoS/3pBsxAB2/Z/DWc9gqelb4cqy7e6tcShPAUmKHFe/u+8w8KA0jLKPnErCksUFG5nfsFYBAAlzle483Dg4sUB+iJjDrXTWvxD7+O0rBJJRemJTVSCvv3FQIDAQAB"
                , this);
    }

    //this is not consumable
    private static final ArrayList<String> notConsumableSKUList = new ArrayList<String>();
    static{
        notConsumableSKUList.add("sku_ad_remove");

        //not consume all product
        notConsumableSKUList.add("goods_099");
        notConsumableSKUList.add("goods_1.49");
        notConsumableSKUList.add("goods_1.99");
        notConsumableSKUList.add("goods_2.99");
        notConsumableSKUList.add("goods_4.99");

        notConsumableSKUList.add("gold_coin_099");
        notConsumableSKUList.add("gold_coin_1.49");
        notConsumableSKUList.add("gold_coin_1.99");
        notConsumableSKUList.add("gold_coin_2.49");
        notConsumableSKUList.add("gold_coin_2.99");
        notConsumableSKUList.add("gold_coin_3.49");
        notConsumableSKUList.add("gold_coin_4.49");

        notConsumableSKUList.add("gold_coin_4.99");
        notConsumableSKUList.add("gold_coin_5.99");
        notConsumableSKUList.add("gold_coin_7.99");
        notConsumableSKUList.add("gold_coin_9.99");

        notConsumableSKUList.add("diamond_9.99");
        notConsumableSKUList.add("diamond_4.99");

        notConsumableSKUList.add("subcribe_1.99_year");
        notConsumableSKUList.add("subcribe_0.99_month");

    };

    @Override
    public boolean isConsumable(String sku) {
        return notConsumableSKUList.contains(sku) == false;
    }
}
