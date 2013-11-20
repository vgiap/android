package com.vg.billing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.vg.api.ApiCallBack;
import com.vg.api.AsyncVGHttp;
import com.vg.api.util.StaticReport;
import com.vg.api.VGClient;
import com.vg.api.VGData;
import com.vg.api.account.AccountListener;
import com.vg.api.account.AccountObserver;
import com.vg.billing.db.Order;
import com.vg.billing.db.OrderHelper;
import com.vg.billing.google.util.IabHelper;
import com.vg.billing.google.util.IabResult;
import com.vg.billing.google.util.Inventory;
import com.vg.billing.google.util.Purchase;
import com.vg.utils.BLog;

import java.util.ArrayList;
import java.util.List;

/*
 * Before launch new purchase, we need make sure, all order numbers are records
 *
 * all consumable products are consumed,
 *
 * here is to do homework before launch new purchase
 */
public class IABHomeWork  implements AccountListener {
    private static final String TAG = IABHomeWork.class.getSimpleName();
    
    public static final String SHARE_PREFERENCES_IAB_SETTING = "iab_setting";
    public static final String SP_EXTRAS_LAST_SYNC           = "last_sync_time";
    public static final String SP_EXTRAS_ERROR_COUNT         = "SP_EXTRAS_ERROR_COUNT";

    private AsyncVGHttp mApiUtil;
    private Context     mContext;
    private Service     mService;

    private static IabHelperBridge   iabBridge;

    public static IabHelperBridge getIabHelperBridge()
    {
        return iabBridge;
    }

    private Handler mHandler;
    public static int nErrorCount = 0;

    public IABHomeWork(Context context,Service service) {
    	mContext = context;
    	mService = service;


        mHandler  = new MainHandler();
        iabBridge = new IabHelperBridge(mContext, queryInvListener);
        nErrorCount = getErrorCount(context);

        AccountObserver.registerAccountListener(IABHomeWork.class.getName(), this);
    }

    public void destroy() {
        AccountObserver.unRegisterAccountListener(IABHomeWork.class.getName());
        iabBridge.depose();
    }

    private final static int BATCH_UPLOAD_ORDER     = 1;
    private final static int BATCH_UPLOAD_ORDER_END = 2;
    private final static int QUERY_PURCHASES        = 3;
    private final static int CONSUME_PURCHASE       = 4;
    private final static String RESULT = "RESULT";

    @Override
    public void onLogIn(VGData.User user) {

    }

    @Override
    public void onLogOut() {

    }

    private class MainHandler extends Handler {
        public MainHandler() {
            super();
            Log.d(TAG, "new FriendsHandler");
        }

        @Override
        public void handleMessage(Message msg) {
            if(!VGClient.isUserIdentified()) return;

            switch (msg.what) {
            case BATCH_UPLOAD_ORDER: {
                batchUploadOrders();
                break;
            }
            case QUERY_PURCHASES: {
                queryPurchases();
                break;
            }
            case BATCH_UPLOAD_ORDER_END: {
                boolean suc = msg.getData().getBoolean("RESULT");
                if (suc) {
                    setErrorCount(mContext, 0);
                    mHandler.sendEmptyMessage(CONSUME_PURCHASE);
                } else {

                    //re-launch again base on next 2*pre-wait time
                    setErrorCount(mContext, nErrorCount + 1);
                    rescheduleSync(false, IAPHelperService.TYPE_UPLOAD);
                    destroy();
                }
                break;
            }
            case CONSUME_PURCHASE: {
                consumeAsync();
                break;
            }
            }
        }
    }

//    private final Object mLocked = new Object();
    public void batchUploadOrders() {
        Log.v(TAG, "-------------------batchUploadOrders()------------------");
        final ArrayList<Order> orders = OrderHelper.queryOrderList(mContext, false, false);
        if(orders != null && orders.size() > 0) {
            mApiUtil = AsyncVGHttp.getInstance();
            mApiUtil.batchUploadOrders(orders, new ApiCallBack.BatchUploadOrder(){
                @Override
                public boolean uploadedOrders(String ordersResponse) {

                    if (orders != null && orders.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (int index = 0; index < orders.size(); index++) {
                            Order order = orders.get(index);
                            if (order != null) {
                                if (!TextUtils.isEmpty(order.product_id)) {
                                    if (sb.length() > 0) {
                                        sb.append(" , ");
                                    }
                                    sb.append("'" + order.product_id + "'");
                                    order.product_id = null;
                                }
                            }
                        }

                        if (sb.length() > 0) {
                            OrderHelper.batchUpdateIabOderStatus(mContext, sb.toString(), true);
                        }
                    }

                    Message mds = mHandler.obtainMessage(BATCH_UPLOAD_ORDER_END);
                    mds.getData().putBoolean(RESULT, true);
                    mHandler.sendMessage(mds);
                    if (mContext != null) {
                        StaticReport.report(mContext, "Market Report: user_id = " + VGClient.getCurrentUser().uid + " batchUploadOrders  response = " + ordersResponse);
                    }

                    return true;
                }

                @Override
                public void OnException(Exception ex) {
                    BLog.e(TAG, "batchUploadOrders Exception  " + ex.getMessage());

                    Message mds = mHandler.obtainMessage(BATCH_UPLOAD_ORDER_END);
                    mds.getData().putBoolean(RESULT, false);
                    mHandler.sendMessage(mds);

                    if (mContext != null) {
                        StaticReport.report(mContext, "Market Report: user_id = " + VGClient.getCurrentUser().uid + "  batchUploadOrders()  IOException:" + ex.toString());
                    }
                }
            });
        }else {
            Message mds = mHandler.obtainMessage(BATCH_UPLOAD_ORDER_END);
            mds.getData().putBoolean(RESULT, true);
            mHandler.sendMessage(mds);
        }
    }
    
    public void rescheduleSync(boolean force , int type) {
        BLog.v(TAG, "-------------------rescheduleSync()------------------type=" + type);
        long delaytime = 0L;
        
        if(force) {
            setErrorCount(mContext, 0);
        }else {
            nErrorCount = getErrorCount(mContext);
        }
        if(nErrorCount >= 10) {
            setErrorCount(mContext, 0);
        }
        delaytime = 60 * 1000L * ((int)Math.pow(2, nErrorCount) - 1);
        
        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        long current_time = System.currentTimeMillis();
        long nexttime = current_time + delaytime;
        BLog.v(TAG, "-------------------rescheduleSync()------------------delaytime = " + delaytime);

        Intent intent = new Intent(mContext, IAPHelperService.class);
        intent.setAction(IAPHelperService.RESCHEDULE_NEXT_UPLOAD_ACTION);
        intent.putExtra(IAPHelperService.SYNC_TYPE, type);
        PendingIntent nextLauncher = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.set(AlarmManager.RTC, nexttime, nextLauncher);
    }
    
    public void queryPurchases() {
        BLog.v(TAG, "-------------------queryPurchases()------------------");
        iabBridge.queryPurchases(new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory, List<Purchase> allPurchases) {
            BLog.v(TAG, "-------------------queryPurchases()------------result.isSuccess=" + result.isSuccess() +"   "+ result.getMessage());
            if (result.isFailure()) {
                setErrorCount(mContext, nErrorCount + 1);
                rescheduleSync(false, IAPHelperService.TYPE_PURCHASE);
                destroy();
                return;
            }
            setErrorCount(mContext, 0);
            BLog.v(TAG, "-------------------queryPurchases()------------result.isSucess------");
            if(mContext != null) {
                StaticReport.report(mContext, "Market Report: user_id = " +VGClient.getCurrentUser().uid + "  queryPurchases()  result.isFailure()" + result.isFailure());
            } 
            if(allPurchases != null && allPurchases.size() > 0) {
                BLog.v(TAG, "-------------------queryPurchases()------------ size = " + allPurchases.size());
                // write to DB
                for(Purchase p : allPurchases) {
                    OrderHelper.insertIabOder(mContext, p);
                }
                
                //upload order
                Message msd = mHandler.obtainMessage(BATCH_UPLOAD_ORDER);
                mHandler.sendMessage(msd);
            } else {
                BLog.v(TAG, "-------------------queryPurchases()------------ size = 0");
            }
        }
        });
    }
    
    IabHelper.QueryInventoryFinishedListener queryInvListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory, List<Purchase> allPurchases) {
            BLog.d(TAG, "onQueryInventoryFinished ");
            BLog.v(TAG, "-------------------queryPurchases()------------result.isSuccess=" + result.isSuccess() +"   "+ result.getMessage());
            if (result.isFailure()) {
                setErrorCount(mContext, nErrorCount + 1);
                rescheduleSync(false, IAPHelperService.TYPE_PURCHASE);
                destroy();
                return;
            }
            setErrorCount(mContext, 0);
            BLog.d(TAG, "-------------------queryPurchases()------------result.isSucess------");
            if(mContext != null) {
                StaticReport.report(mContext, "Market Report: user_id = " + VGClient.getCurrentUser().uid + "  queryPurchases()  result.isFailure()" + result.isFailure());
            } 
            if(allPurchases != null && allPurchases.size() > 0) {
                BLog.d(TAG, "onQueryInventoryFinished unConsume purchase size = " + allPurchases.size());
                // write to DB
                for(Purchase p : allPurchases) {
                    OrderHelper.insertIabOder(mContext, p);
                }
                
                //upload order
                Message msd = mHandler.obtainMessage(BATCH_UPLOAD_ORDER);
                mHandler.sendMessage(msd);
            }else {
                BLog.d(TAG, "onQueryInventoryFinished unConsume purchase size = 0");
            }
        }
        };
    
    public void consumeAsync() {
        BLog.v(TAG, "-------------------consumeAsync()------------------");
        List<Order> orderList = OrderHelper.queryOrderList(mContext, true, false);
        List<Purchase> purchases = null;
        if(orderList != null && orderList.size() > 0) {
            for(Order order : orderList) {
                if (!VGClient.isConsumableProduct(order.purchase.getSku())) {
                    continue;
                }

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
                iabBridge.consumeAsync(purchases, new IabHelper.OnConsumeMultiFinishedListener() {
                    
                    @Override
                    public void onConsumeMultiFinished(List<Purchase> purchases,
                            List<IabResult> results) {
                        BLog.d(TAG, "onConsumeMultiFinished");
                        try{
                            setErrorCount(mContext, 0);
                            if(results != null && results.size() > 0) {
                                BLog.d(TAG, "onConsumeMultiFinished results size = "+ results.size());
                                for(int i = 0; i< results.size(); i++) {
                                    if(results.get(i).isSuccess()) {
                                        BLog.d(TAG, "onConsumeMultiFinished results sucess = "+ purchases.get(i).toString());
                                        String productId = VGData.Goods.getProductIdFromPayload(purchases.get(i).getDeveloperPayload());
                                        if(!TextUtils.isEmpty(productId)) {
                                            OrderHelper.updateIabConsumeStatus(mContext, productId, true);
                                        }
                                    }else {
                                        BLog.d(TAG, "onConsumeMultiFinished results failed  "+ results.get(i).getMessage());
                                        if(mContext != null) {
                                            StaticReport.report(mContext, "Market Report: consumeAsync()---   " + purchases.get(i).toString());
                                        } 
                                    }
                                }
                                
                            }else {
                                BLog.d(TAG, "onConsumeMultiFinished results size = 0");
                            }
                        }catch(Exception e) {
                            if(mContext != null) {
                                StaticReport.report(mContext, "Market Report: user_id = " + VGClient.getCurrentUser().uid + "  Exception consumeAsync()  " + e.getMessage());
                            } 
                            BLog.d(TAG, "onConsumeMultiFinished exception: "+ e.getMessage());
                        }
                        
                    }
                });
            }catch(Exception e) {
                BLog.d(TAG, "onConsumeMultiFinished exception: "+ e.getMessage());
                if(mContext != null) {
                    setErrorCount(mContext, nErrorCount + 1); 
                    rescheduleSync(false, IAPHelperService.TYPE_CONSUME);
                    StaticReport.report(mContext, "Market Report: user_id = " +VGClient.getCurrentUser().uid  + "  Exception consumeAsync()  " + e.getMessage());
                }
            }
        }
    }

    private SharedPreferences getSharePreferences(Context context) {
        return context.getSharedPreferences(SHARE_PREFERENCES_IAB_SETTING, Context.MODE_PRIVATE);
    }
    private void setErrorCount(Context context, int count) {
        nErrorCount = count;
        SharedPreferences sp = getSharePreferences(context);
        SharedPreferences.Editor Editor = sp.edit();
        Editor.putInt(SP_EXTRAS_ERROR_COUNT, count);
        Editor.commit();
    }
    public static void initErrorCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_PREFERENCES_IAB_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor Editor = sp.edit();
        Editor.putInt(SP_EXTRAS_ERROR_COUNT, 0);
        Editor.commit();
    }
    
    private int getErrorCount(Context context) {
        SharedPreferences sp = getSharePreferences(context);
        return sp.getInt(SP_EXTRAS_ERROR_COUNT, 0);
    }

}
