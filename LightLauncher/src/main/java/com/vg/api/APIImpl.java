package com.vg.api;

import java.util.HashMap;
import java.util.List;

/**
 * Created by huadong on 11/18/13.
 */
public class APIImpl extends ApiCallBack{

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
       return callback.done(fetchGoodsListImpl(url, parameters));
    }

    //call http
    //parse the data
    private List<VGOpenAPI.Goods> fetchGoodsListImpl(String url, HashMap parameters)
    {
        return null;
    }
    //end fetch goods

    //begin purchase
    public boolean purchaseGoods(String url, HashMap parameters, ApiCallBack.PurchaseCallback pcallback)
    {
        return pcallback.done(purchaseGoodsImpl(url, parameters));
    }

    private VGOpenAPI.Receipt purchaseGoodsImpl(String url, HashMap parameters)
    {
        return null;
    }
    //end purchase

 }