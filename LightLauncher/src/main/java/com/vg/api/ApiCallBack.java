package com.vg.api;

import java.util.List;

/**
 * Created by huadong on 11/18/13.
 *
 *
 */
public class ApiCallBack {

    protected interface CallBack {
        public void onException(Exception ex);
    }

    public interface GoodsListCallback  extends CallBack{
       boolean getGoodsList(List<VGData.Goods> goods);
    }

    public interface QueryPurchaseCallback extends CallBack{
        boolean queryPurchaseFinished(VGData.Receipt receipt);
    }


    public interface RegisterUser extends CallBack{
        boolean finishRegisterUser(VGData.User user);
    }

    public interface BatchUploadOrder extends CallBack
    {
        /*
         * orders are split by ","
         */
        boolean uploadedOrders(String orders);
    }}
