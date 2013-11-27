package com.vg.billing.vc.util;

import com.vg.billing.common.PurchaseData;

/**
 * Created by huadong on 11/27/13.
 */
public class VirtualCurrencyPurchase implements PurchaseData{
    String fakeJson  = "";
    String fakeOrder = "";
    public VirtualCurrencyPurchase(String jsonData, String order)
    {
        fakeJson  = jsonData;
        fakeOrder = order;
    }

    @Override
    public String toJson() {
        return fakeOrder;
    }

    @Override
    public String getOrderId() {
        return fakeOrder;
    }
}
