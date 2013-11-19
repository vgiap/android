package com.lightapp.lightlauncher;

import android.app.Application;

import com.vg.api.VGClient;

/**
 * Created by huadong on 11/18/13.
 */
public class LightApplication extends Application implements VGClient.ConsumableCallBack {

    @Override
    public void onCreate() {
        super.onCreate();

        VGClient.initBilling(
                this,
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhyB0f3W9fSH7KsvS9S0AmSblEqPXLwLS/e3k+zi+Cqb0ZEMvr+UEWyufVdEdv8PlN0DRQfpPLn3FKb56a62vBhJhe+RyRNM081a1B6A2oGR9YFGa0DWjxStix5ue7bWv4hWU1/lBwV+re65HpXSHA/wGMrA5OFCgVrIgcXABBctCiMDddgSHHTEeu7rAa3GRYRM9ixrdI2XZI8/0k59PaM5/ldhDzLuoS/3pBsxAB2/Z/DWc9gqelb4cqy7e6tcShPAUmKHFe/u+8w8KA0jLKPnErCksUFG5nfsFYBAAlzle483Dg4sUB+iJjDrXTWvxD7+O0rBJJRemJTVSCvv3FQIDAQAB"
                , this);
    }

    //this is not consumable
    private static final String SKU_AD_REMOVAL = "sku_ad_remove";
    @Override
    public boolean isConsumable(String sku) {
        return sku.equals(SKU_AD_REMOVAL) == false;
    }
}
