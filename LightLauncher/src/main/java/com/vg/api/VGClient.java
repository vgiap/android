package com.vg.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vg.api.account.AccountObserver;
import com.vg.billing.IABHelperService;
import com.vg.billing.IABHomeWork;
import com.vg.billing.db.OrderHelper;
import com.vg.billing.google.util.IabResult;
import com.vg.billing.google.util.Purchase;
import com.vg.billing.vc.util.VirtualCurrencyPurchase;

import org.json.JSONException;

import java.lang.reflect.Method;

import static com.vg.api.VGData.PayType.ALI_PAY;
import static com.vg.api.VGData.PayType.APPLE_STORE;
import static com.vg.api.VGData.PayType.FREE;
import static com.vg.api.VGData.PayType.GOOGLE_PLAYER;
import static com.vg.api.VGData.PayType.VIRTUAL_CURRENCY_COIN;
import static com.vg.api.VGData.PayType.VIRTUAL_CURRENCY_DIAMOND;

/**
 * Created by huadong on 11/19/13.
 */
public class VGClient{

    private static final String TAG = "VGClient";
    private static VGClient _instance;
    public VGClient getInstance()
    {
        if(_instance == null)
            _instance = new VGClient();

        return _instance;
    }

    public static Context getContext(){
        return mCurrentAndroidContext;
    }
    private static Context mCurrentAndroidContext;
    /*
     * google iap interface
     */
    public interface   BillingInterface {
        public void    purchase(Activity act, VGData.Goods product, GooglePurchaseListener mpListener);
        public void    consumeAsync(Context context);
        public boolean onActivityResult(int requestCode, int resultCode, Intent data);
        public boolean isEngineReady();
        public void    depose();
    }

    public interface PurchaseListener {
        public void   onException(Exception ex);
        public void   onBillingFinished(VGData.Goods goods, BillingResult  response);
        public void   onUploadOrderFinished(boolean res,    VGData.Receipt VGResponse);
    }

    public interface GooglePurchaseListener{
        public void   OnException(Exception ex);
        public void   onBillingFinished(boolean res, BillingResult response);
    }


    public interface IsGoodsPurchasedListener {
        public void onQueryFinished(boolean purchased);
    }

    /*
     * if you want do filter some sku not be consumed, don't return true
     */
    public interface ConsumableCallBack {
        boolean isConsumable(String sku);
    }

    /*
         *for test in app billing
         *
         */
    public static class TestPayloadInGoogldePlay{
        public static VGData.Payload google_099 = new VGData.Payload("Google 0.99", "goods_099",  GOOGLE_PLAYER);
        public static VGData.Payload google_149 = new VGData.Payload("Google 1.49", "goods_1.49", GOOGLE_PLAYER);
        public static VGData.Payload google_199 = new VGData.Payload("Google 1.99", "goods_1.99", GOOGLE_PLAYER);
        public static VGData.Payload google_299 = new VGData.Payload("Google 2.99", "goods_2.99", GOOGLE_PLAYER);
        public static VGData.Payload google_499 = new VGData.Payload("Google 4.99", "goods_4.99", GOOGLE_PLAYER);

        public static VGData.Payload google_coin_099 = new VGData.Payload("Google 0.99", "gold_coin_099", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_149 = new VGData.Payload("Google 1.49", "gold_coin_1.49", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_199 = new VGData.Payload("Google 1.99", "gold_coin_1.99", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_249 = new VGData.Payload("Google 2.49", "gold_coin_2.49", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_299 = new VGData.Payload("Google 2.99", "gold_coin_2.99", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_349 = new VGData.Payload("Google 3.49", "gold_coin_3.49", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_449 = new VGData.Payload("Google 4.49", "gold_coin_4.49", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_499 = new VGData.Payload("Google 4.99", "gold_coin_4.99", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_599 = new VGData.Payload("Google 5.99", "gold_coin_5.99", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_799 = new VGData.Payload("Google 7.99", "gold_coin_7.99", GOOGLE_PLAYER);
        public static VGData.Payload google_coin_999 = new VGData.Payload("Google 9.99", "gold_coin_9.99", GOOGLE_PLAYER);

        public static VGData.Payload google_diamond_999 = new VGData.Payload("Google 9.99", "diamond_9.99", GOOGLE_PLAYER);
        public static VGData.Payload google_diamond_499 = new VGData.Payload("Google 4.99", "diamond_4.99", GOOGLE_PLAYER);

        public static VGData.Payload google_subscribe_199_year  = new VGData.Payload("Google subscribe 1.99 year", "subcribe_1.99_year", GOOGLE_PLAYER);
        public static VGData.Payload google_subscribe_099_month = new VGData.Payload("Google subscribe 0.99 month", "subcribe_0.99_month", GOOGLE_PLAYER);


        //for virtual currency payment
        public static VGData.Payload coin_100   = new VGData.Payload("Gold coin 100",   "gold_coin_100",   VGData.PayType.VIRTUAL_CURRENCY_COIN);
        public static VGData.Payload coin_200   = new VGData.Payload("gold coin 200",   "gold_coin_200",   VGData.PayType.VIRTUAL_CURRENCY_COIN);
        public static VGData.Payload coin_10000 = new VGData.Payload("gold coin 10000", "gold_coin_10000", VGData.PayType.VIRTUAL_CURRENCY_COIN);

        public static VGData.Payload diamond_1   = new VGData.Payload("diamond 1",   "diamond_1",   VGData.PayType.VIRTUAL_CURRENCY_DIAMOND);
        public static VGData.Payload diamond_2   = new VGData.Payload("diamond 2",   "diamond_2",   VGData.PayType.VIRTUAL_CURRENCY_DIAMOND);
        public static VGData.Payload diamond_5   = new VGData.Payload("diamond 5",   "diamond_5",   VGData.PayType.VIRTUAL_CURRENCY_DIAMOND);

    }

    static VGData.User      currentLoginUser;

    public static final String baseAPIURL = "";
    public static String IABKEY           = "";

    private   static ConsumableCallBack consumableImpl = new ConsumableCallBack() {
        @Override
        public boolean isConsumable(String sku) {
            return true;
        }
    };

    public static boolean isConsumableProduct(String sku)
    {
        return consumableImpl.isConsumable(sku);
    }

    public static void initBilling(Context con, String appLicenseKey, ConsumableCallBack ccb)
    {
        IABKEY = appLicenseKey;
        if(ccb != null)
        {
            consumableImpl = ccb;
        }

        mCurrentAndroidContext = con.getApplicationContext();

        //start in app purchase background service
        con.startService(new Intent(con, IABHelperService.class));
    }



    public static boolean registerVGUser(String identify,
                                         String profile,
                                         String provider/*google.com, facebook.com*/,
                                         String appData)
    {
        AsyncVGHttp.getInstance().registerUser(identify, profile, provider, appData, new ApiCallBack.RegisterUser() {
            @Override
            public boolean finishRegisterUser(VGData.User user) {
                currentLoginUser = user.clone();
                //save currentLoginUser into preference

                AccountObserver.login(currentLoginUser);
                return true;
            }

            @Override
            public void OnException(Exception ex) {
                //tell BillService, to register again
            }
        });

        return true;
    }

    public static void logOut()
    {
        AccountObserver.logout();
        currentLoginUser = null;
    }

    public static void logIn(String identify,
                             String profile,
                             String provider/*google.com, facebook.com*/,
                             String appData)
    {
        registerVGUser(identify, profile, provider, appData);

    }

    public static VGData.User getCurrentUser()
    {
        if(currentLoginUser == null)
            return VGData.User.NullUser;

        return currentLoginUser;
    }

    public static boolean isUserIdentified()
    {
        return currentLoginUser!= null;
    }

    /*
     * 3-D developer just need do purchase,
     *
     */
    public static void purchase(Activity act, final VGData.Goods product,final int pay_count,final VGClient.PurchaseListener mpListener)
    {
        switch(product.pay.type)
        {
            case GOOGLE_PLAYER: //google play
                IABHomeWork.getIabHelperBridge().purchase(act, product, new VGClient.GooglePurchaseListener()
                {
                    @Override
                    public void OnException(Exception ex) {
                        mpListener.onException(ex);
                    }

                    @Override
                    public void onBillingFinished(boolean res, BillingResult response) {
                        if(res == true)
                        {
                            //update local database
                            try {
                                OrderHelper.insertIabOder(mCurrentAndroidContext, new Purchase(String.valueOf(response.billingType), response.originalJson, ""));
                            } catch (JSONException e) {
                                Log.e(TAG, "why error here, please check,  not valid google purchase data="+response.toString());
                                e.printStackTrace();
                            }

                            AsyncVGHttp.getInstance().purchaseGoods(product,
                                    pay_count,
                                    new VirtualCurrencyPurchase(response.originalJson, response.orderId),
                                    mpListener);
                        }
                        else
                        {
                            mpListener.onUploadOrderFinished(false, new VGData.Receipt(response));
                        }
                    }
                });
                break;

            case FREE:
            case VIRTUAL_CURRENCY_COIN:
            case VIRTUAL_CURRENCY_DIAMOND:
                AsyncVGHttp.getInstance().purchaseGoods(product,
                                                        pay_count,
                                                        new VirtualCurrencyPurchase("", ""),
                                                        mpListener);
                break;

            case APPLE_STORE:
            case ALI_PAY:
                mpListener.onException(new Exception("not supported pay in current"));
                break;

            default:
                break;
        }
    }

    /*
     * query from virtual goods server and local cache
     *
     * we don't check google product here
     *
     */
    public static void isGoodsPurchased(VGData.Goods product, VGClient.IsGoodsPurchasedListener purchasedListener)
    {
        //check local firstly
        boolean exist = OrderHelper.getInstance(getContext()).isGoodsExist(product.gid);
        if(exist == true)
        {
            purchasedListener.onQueryFinished(true);
        }
        else//call server check
        {
            //TODO
            if(product.pay.type == GOOGLE_PLAYER)
            {
                //call google query
            }
            else
            {
                //call vg query
            }

        }
    }
}
