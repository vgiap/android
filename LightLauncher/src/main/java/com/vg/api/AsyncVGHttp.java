package com.vg.api;

import com.vg.billing.common.PurchaseData;
import com.vg.billing.db.Order;
import com.vg.billing.db.OrderHelper;
import com.vg.billing.google.util.Purchase;
import com.vg.http.AsyncVGRunner;
import com.vg.http.HttpManager;
import com.vg.http.RequestListener;
import com.vg.http.VGException;
import com.vg.http.VGParameters;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.vg.api.VGData.PayType.ALI_PAY;
import static com.vg.api.VGData.PayType.APPLE_STORE;
import static com.vg.api.VGData.PayType.FREE;
import static com.vg.api.VGData.PayType.GOOGLE_PLAYER;
import static com.vg.api.VGData.PayType.VIRTUAL_CURRENCY_COIN;
import static com.vg.api.VGData.PayType.VIRTUAL_CURRENCY_DIAMOND;

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
    public boolean purchaseGoods(final VGData.Goods product, int pay_count, PurchaseData app_data, final VGClient.PurchaseListener pCallback)
    {
        VGParameters vp = new VGParameters();
        vp.add("cost_type",       product.pay.type);
        vp.add("goods_id",        product.gid);
        vp.add("count",           pay_count);
        vp.add("cost_real_money", product.pay.displayName);
        vp.add("pay_channel",     product.pay.type);
        vp.add("pay_id",          app_data.getOrderId());
        vp.add("app_data",        app_data.toJson());

        new AsyncVGRunner().request(VGClient.baseAPIURL + "/buy", vp, HttpManager.HTTPMETHOD_POST,new RequestListener() {

            @Override
            public void onComplete(String response) {
                try{

                    VGData.Receipt rec = VGData.Receipt.parseJson(response);

                    if(rec != null)
                    {
                        //update local database
                        OrderHelper.updateIabOderStatus(VGClient.getContext(), product.gid, true);

                        switch(product.pay.type)
                        {
                            case GOOGLE_PLAYER: //google play
                                pCallback.onUploadOrderFinished(true, rec);
                                break;
                            case FREE:
                            case VIRTUAL_CURRENCY_COIN:
                            case VIRTUAL_CURRENCY_DIAMOND:
                                pCallback.onBillingFinished(product, null);
                                pCallback.onUploadOrderFinished(true, rec);
                                break;

                            case APPLE_STORE:
                            case ALI_PAY:
                                break;
                        }
                    }
                    else//fail to call api buy
                    {
                        switch(product.pay.type)
                        {
                            case GOOGLE_PLAYER: //google play
                                pCallback.onUploadOrderFinished(false, null);
                                break;
                            case FREE:
                            case VIRTUAL_CURRENCY_COIN:
                            case VIRTUAL_CURRENCY_DIAMOND:
                                pCallback.onException(new Exception("Fail to purchase goods="+product));
                                break;

                            default:
                                pCallback.onException(new Exception("Fail to purchase goods="+product));
                                break;
                        }

                    }
                }catch (Exception ne){pCallback.onException(ne);}
            }

            @Override
            public void onError(VGException e) {
                pCallback.onException(e);
            }
        });

        return true;
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
     * @param identify               display name
     * @param profile               logo url
     * @param provider              account provider, such as google.com, facebook.com
     * @param registerUserCallBack  client callback
     */
    public void registerUser(String identify,
                                    String profile,
                                    String provider/*google.com, facebook.com*/,
                                    String appData,
                                    final ApiCallBack.RegisterUser registerUserCallBack)
    {
        VGParameters vp = new VGParameters();
        vp.add("human", identify);
        vp.add("provider", provider);
        vp.add("profile", profile);
        vp.add("appData", appData);

        new AsyncVGRunner().request(VGClient.baseAPIURL + "/register_user", vp, HttpManager.HTTPMETHOD_POST,new RequestListener() {

            @Override
            public void onComplete(String response) {
                try{
                    registerUserCallBack.finishRegisterUser(VGData.User.parseJson(response));
                }catch (Exception ne){registerUserCallBack.onException(ne);}
            }

            @Override
            public void onError(VGException e) {
                registerUserCallBack.onException(e);
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
                }catch (Exception ne){orderUploadCallBack.onException(ne);}
            }

            @Override
            public void onError(VGException e) {
                orderUploadCallBack.onException(e);
            }
        });
    }


    /*
     * batch upload orders
     */
    public void isPurchased(String goodsID, final ApiCallBack.QueryPurchaseCallback purchaseCallback)
    {
        VGParameters vp = new VGParameters();
        vp.add("goods_id", goodsID);

        new AsyncVGRunner().request(VGClient.baseAPIURL + "/buy/goods/query", vp, HttpManager.HTTPMETHOD_POST,new RequestListener() {

            @Override
            public void onComplete(String response) {
                try{
                    purchaseCallback.queryPurchaseFinished(VGData.Receipt.parseJson(response));
                }catch (Exception ne){purchaseCallback.onException(ne);}
            }

            @Override
            public void onError(VGException e) {
                purchaseCallback.onException(e);
            }
        });
    }
 }