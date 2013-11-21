package com.vg.api;

import com.vg.billing.db.Order;
import com.vg.http.AsyncVGRunner;
import com.vg.http.HttpManager;
import com.vg.http.RequestListener;
import com.vg.http.VGException;
import com.vg.http.VGParameters;

import java.io.IOException;
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
                                    String appData, final ApiCallBack.RegisterUser registUserCallBack)
    {
        VGParameters vp = new VGParameters();
        vp.add("human", identify);
        vp.add("provider", provider);
        vp.add("profile", profile);
        vp.add("appData", appData);

        new AsyncVGRunner().request(VGClient.baseAPIURL + "/register_user", vp, HttpManager.HTTPMETHOD_GET,new RequestListener() {

            @Override
            public void onComplete(String response) {
                try{
                    registUserCallBack.finishRegisterUser(VGData.User.parseJson(response));
                }catch (Exception ne){registUserCallBack.OnException(ne);}
            }

            @Override
            public void onError(VGException e) {
                registUserCallBack.OnException(e);
            }
        });
    }



    /*
     * batch upload orders
     */
    public void batchUploadOrders(List<Order>orders, final BatchUploadOrder orderUploadCallBack)
    {
        VGParameters vp = new VGParameters();
        new AsyncVGRunner().request(VGClient.baseAPIURL + "/upload_orders", vp, HttpManager.HTTPMETHOD_POST,new RequestListener() {

            @Override
            public void onComplete(String response) {
                try{
                    orderUploadCallBack.uploadedOrders(response);
                }catch (Exception ne){orderUploadCallBack.OnException(ne);}
            }

            @Override
            public void onError(VGException e) {
                orderUploadCallBack.OnException(e);
            }
        });
    }

 }