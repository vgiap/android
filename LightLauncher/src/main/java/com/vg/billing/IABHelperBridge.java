package com.vg.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.vg.api.BillingResult;
import com.vg.api.util.StaticReport;
import com.vg.api.VGClient;
import com.vg.api.VGData;
import com.vg.billing.db.Order;
import com.vg.billing.db.OrderHelper;
import com.vg.billing.google.util.IabHelper;
import com.vg.billing.google.util.IabResult;
import com.vg.billing.google.util.Purchase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huadong on 11/19/13.
 */
public class IABHelperBridge implements VGClient.BillingInterface{
    private static final String TAG = IABHelperBridge.class.getSimpleName();

    public static final int RC_REQUEST = 10001;
    private String string_iab_not_available = "In app Billing service is not available.";

    private Context context;
    IabHelper mHelper = null;
    boolean mEngineReady = false;
    boolean hasInited    = false;

    public IABHelperBridge(Context context, IabHelper.QueryInventoryFinishedListener listener) {
        hasInited = false;
        this.context = context.getApplicationContext();
        createIabHelper(VGClient.IABKEY, listener);
    }


    public void createIabHelper(String base64EncodedPublicKey, final IabHelper.QueryInventoryFinishedListener listener) {
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(context, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");
                mEngineReady = result.isSuccess();
                hasInited = true;
                if (!mEngineReady) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    if( VGClient.isUserIdentified()) {
                        StaticReport.report(context, "Market Report:user_id = " + VGClient.getCurrentUser().uid
                                + "------ and email = " +  VGClient.getCurrentUser().human + "Problem setting up in-app billing: " + result);
                    }
                    return;
                }
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    Log.d(TAG, "mHelper == null.");
                    return;
                }

                if(listener != null)
                {
                    try {
                        Log.d(TAG, "Querying inventory.");
                        mHelper.queryInventoryAsync(listener);
                    } catch (Exception e) {
                        Log.e(TAG, "queryInventoryAsync, failed after setup succeed? should not be here!");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void queryPurchases(final IabHelper.QueryInventoryFinishedListener listener) {
        if(mHelper != null && mEngineReady) {
            try {
                mHelper.queryInventoryAsync(listener);
            } catch (Exception e) {
                Log.e(TAG, "queryInventoryAsync, failed after setup succeed? should not be here!");
                e.printStackTrace();
            }
        }else {
            Log.v(TAG, "(mHelper != null && mEngineReady) is false" );
        }
    }

    public void consumeAsync(List<Purchase> purchases, IabHelper.OnConsumeMultiFinishedListener listener) {
        if (mEngineReady) {
            mHelper.consumeAsync(purchases, listener);
        }
    }

    private void launchPurchaseFlow(Activity act, String sku,
                                    IabHelper.OnIabPurchaseFinishedListener listener, String payload, VGClient.GooglePurchaseListener mpListener) {
        if(act == null) return;
        if(act.isFinishing()) return;
        if (!mEngineReady) {
            StaticReport.report(context, "Market Report:user_id = " + VGClient.getCurrentUser().uid
                    + "------ and email = " + VGClient.getCurrentUser().human + "Problem setting up in-app billing: Bill Unavailable for " + payload );
            Log.e(TAG, "Problem setting up in-app billing: Bill Unavailable for " + payload);

            if(mpListener != null) {
                BillingResult result = new BillingResult(BillingResult.TYPE_IAB);
                result.response = string_iab_not_available;
                mpListener.onBillingFinished(false, result);
            }

        } else {
            mHelper.launchPurchaseFlow(act, sku, RC_REQUEST,
                    listener, payload);
        }
    }

    @Override
    public void purchase(Activity act, VGData.Goods product,final VGClient.GooglePurchaseListener mpListener) {
        try{
            final String sku = product.getSku();
            final String payload = product.getPayload();
            launchPurchaseFlow(act, sku,
                    new IabHelper.OnIabPurchaseFinishedListener() {
                        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                            try{
                                Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
                                if (result.isFailure()) {
                                    Log.v(TAG, "Error purchasing: " + result);
                                    BillingResult mResult = new BillingResult(BillingResult.TYPE_IAB);
                                    mResult.response = result.getMessage();
                                    mpListener.onBillingFinished(false, mResult);
                                    return;
                                }
                                if(context != null) {
                                    OrderHelper.insertIabOder(context, purchase);
                                }


                                BillingResult billingResult = null;
                                if(result.isSuccess()) {
                                    billingResult = new BillingResult(BillingResult.TYPE_IAB);
                                    if (purchase != null) {
                                        billingResult.orderId = purchase.getOrderId();
                                        billingResult.payCode = purchase.getSku();
                                        billingResult.originalJson = purchase.getOriginalJson();
                                    }
                                }
                                mpListener.onBillingFinished(result.isSuccess(), billingResult);
                                Log.d(TAG, "Purchase successful.");
                                if(context != null) {
                                    StaticReport.report(context,
                                            "Market Report: purchase()  result.isSuccess() = " + result.isSuccess()
                                                    + "    purchase.tostring  = " + purchase.toString());
                                }
                            }catch(Exception e) {
                                if(context != null) {
                                    StaticReport.report(context, "Market Report: purchase()  " + e.getMessage());
                                }
                            }
                        }
                    }
                    , payload, mpListener);
        }catch(Exception e) {
            if(context != null) {
                StaticReport.report(context, "Market Report Exception purchase()  " + e.getMessage());
            }
        }

    }

    public void consumeAsync(final Context ctx) {
        if(TextUtils.isEmpty(VGClient.getCurrentUser().uid)) return;
        List<Order> orderList = OrderHelper.queryOrderList(ctx, true, false);
        List<Purchase> purchases = null;
        if(orderList != null && orderList.size() > 0) {
            for(Order order : orderList) {
                if(order != null && order.purchase != null) {
                    if(purchases == null) {
                        purchases = new ArrayList<Purchase>();
                    }
                    purchases.add(order.purchase);
                }
            }
        }
        if(purchases != null && purchases.size() > 0) {
            try{
                consumeAsync(purchases, new IabHelper.OnConsumeMultiFinishedListener() {

                    @Override
                    public void onConsumeMultiFinished(List<Purchase> purchases,
                                                       List<IabResult> results) {
                        try{
                            if(results != null && results.size() > 0) {
                                for(int i = 0; i< results.size(); i++) {
                                    if(results.get(i).isSuccess()) {
                                        String productId = VGData.Goods.getProductIdFromPayload(purchases.get(i).getDeveloperPayload());
                                        if(!TextUtils.isEmpty(productId)) {
                                            OrderHelper.updateIabConsumeStatus(ctx, productId, true);
                                        }
                                    }
                                }
                            }
                        }catch(Exception e) {
                            if(ctx != null) {
                                StaticReport.report(ctx, "Market Report: consumeAsync()  " + e.getMessage());
                            }
                        }

                    }
                });
            }catch(Exception e) {
                Log.d(TAG, "consumeAsync exception :" + e.getMessage());
                if(ctx != null) {
                    StaticReport.report(ctx, "Market Report: consumeAsync()  " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RC_REQUEST == requestCode) {
            // TODO: handle the result.
            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to
                // in-app
                // billing...
                return false;
            } else {
                Log.d(TAG, "onActivityResult handled by IABUtil.");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isEngineReady() {
        return mEngineReady;
    }


    @Override
    public void depose() {
        if (mEngineReady && mHelper != null) mHelper.dispose();
        mEngineReady = false;
        mHelper = null;
    }

}
