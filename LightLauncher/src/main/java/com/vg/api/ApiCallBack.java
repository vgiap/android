package com.vg.api;

import java.util.List;

/**
 * Created by huadong on 11/18/13.
 */
public class ApiCallBack {
   public interface GoodsListCallback  extends CallBack{
       boolean done(List<VGOpenAPI.Goods> goods);
   }

    interface PurchaseCallback extends CallBack{
        boolean done(VGOpenAPI.Receipt receipt);
    }


}
