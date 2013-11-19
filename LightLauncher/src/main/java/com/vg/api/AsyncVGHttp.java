package com.vg.api;

import com.vg.billing.db.Order;

import java.util.HashMap;
import java.util.List;

/**
 * Created by huadong on 11/18/13.
 */
public class AsyncVGHttp extends ApiCallBack{

    private static AsyncVGHttp _asyncVGHttp = new AsyncVGHttp();

    public static AsyncVGHttp getInstance()
    {
        return _asyncVGHttp;
    }

    //----------------------------------Begin Google Play IAP Call--------------------------------------


    //begin purchase
    /*
     * purchase goods
     *
     */
    public boolean purchaseGoods(String url, HashMap parameters, ApiCallBack.PurchaseCallback pcallback)
    {
        return pcallback.purchaseFinished(purchaseGoodsImpl(url, parameters));
    }

    private VGData.Receipt purchaseGoodsImpl(String url, HashMap parameters)
    {
        return null;
    }
    //end purchase
    //----------------------------------End Google Play IAP Call--------------------------------------


    //--------------------------------------Begin VG http Call----------------------------------------------
     /*
     * Design
     * call back is to handle the data
     *
     * public api is out api
     * xxxImpl is to process http call, which take care thread allocate,
     *
     */
    //fetch goods
    public boolean fetchGoodsList(String url, HashMap parameters, ApiCallBack.GoodsListCallback callback)
    {
        return callback.getGoodsList(fetchGoodsListImpl(url, parameters));
    }

    //call http
    //parse the data
    private List<VGData.Goods> fetchGoodsListImpl(String url, HashMap parameters)
    {
        return null;
    }
    //end fetch goods

    /*
     * register user
     *
     */
    public void registerUser(String identify,
                                    String profile,
                                    String provider/*google.com, facebook.com*/,
                                    String appData, ApiCallBack.RegisterUser registUserCallBack)
    {
        registUserCallBack.finishRegisterUser(registerUserImpl(identify, profile,profile, appData));
    }

    private VGData.User registerUserImpl(String identify,
                                         String profile,
                                         String provider/*google.com, facebook.com*/,
                                         String appData)
    {
        return null;
    }

    /*
     * batch upload orders
     */
    public void batchUploadOrders(List<Order>orders, BatchUploadOrder orderUploadCallBack)
    {
        orderUploadCallBack.uploadedOrders(batchUploadOrdersImpl(orders));
    }

    private String batchUploadOrdersImpl(List<Order> orders)
    {
        return null;
    }

 }