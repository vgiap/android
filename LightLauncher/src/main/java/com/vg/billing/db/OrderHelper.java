package com.vg.billing.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.vg.api.util.StaticReport;
import com.vg.api.VGClient;
import com.vg.api.VGData;
import com.vg.billing.google.util.Purchase;

import org.json.JSONException;

import java.util.ArrayList;

public class OrderHelper {
    private final static String TAG = "OerderHelper";
    private static OrderHelper _instance;
    private Context mContext;

    private static final HandlerThread sWorkerThread = new HandlerThread("order-db");
    static {
        sWorkerThread.start();
    }
    public static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    private OrderHelper(Context context) {
        mContext = context;
    }

    public OrderHelper(Context context, boolean forSync) {
        mContext = context;
    }

    public static OrderHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new OrderHelper(context);
        }

        return _instance;
    }

    public static void closeCursor(Cursor cursor) {
        if (null != cursor && !cursor.isClosed()) {
            cursor.close();
            cursor = null;
        }
    }

    //
    //it is for add, not include update
    public void addSettings(String name, String value)
    {
        ContentValues values = new ContentValues();

        values.put("name",  name);
        values.put("value", value);

        if(!TextUtils.isEmpty(name)) {
            String where = "name='" + name + "'";
            int count = mContext.getContentResolver().update(OrderProvider.getContentURI(mContext, "settings"), values, where, null);
            if(count == 0) {
                mContext.getContentResolver().insert(OrderProvider.getContentURI(mContext, "settings"), values);
            }
        }
    }

    public static void insertIabOder(Context context, Purchase p) {
        try{
            if(p == null) return;
            ContentValues values = new ContentValues();

            String payload       = p.getDeveloperPayload();
            String product_id    = VGData.Goods.getProductIdFromPayload(payload);
            String user_id       = VGData.Goods.getUserIdFromPayload(payload);

            values.put(OrderColumns.PRODUCT_ID, product_id);
            if(TextUtils.isEmpty(user_id)) {
                values.put(OrderColumns.USER_ID, VGClient.getCurrentUser().uid);
            }else {
                values.put(OrderColumns.USER_ID, user_id);
            }

            values.put(OrderColumns.PAY_CODE,  p.getSku());
            values.put(OrderColumns.PAY_TYPE,  "TYPE_IAB");
            values.put(OrderColumns.ITEM_TYPE, p.getItemType());
            values.put(OrderColumns.JSON_PURCHASE_INFO, p.getOriginalJson());
            values.put(OrderColumns.SIGNATURE, p.getSignature());
            values.put(OrderColumns.VERSION_CODE, VGData.Goods.getVersionCodeFromPayload(payload));
            values.put(OrderColumns.HAS_CONSUMED, 0);

            if(!TextUtils.isEmpty(product_id)) {
                String where = OrderColumns.PRODUCT_ID + "='" + product_id + "'";
                int count = context.getContentResolver().update(OrderProvider.getContentURI(context, OrderProvider.TABLE_ORDER), values, where, null);
                if(count == 0) {
                    values.put(OrderColumns.HAS_ORDERED, 0);
                    context.getContentResolver().insert(OrderProvider.getContentURI(context, OrderProvider.TABLE_ORDER), values);
                }
            }
        }catch(Exception e) {
            StaticReport.report(context, e.getMessage());
        }
    }
    
    public static void updateIabOderStatus(Context context, String product_id, boolean has_ordered) {
        try{
            String userID = VGClient.getCurrentUser().uid;

            if(TextUtils.isEmpty(userID))
                return;


            ContentValues values = new ContentValues();
            values.put(OrderColumns.HAS_ORDERED, has_ordered? 1:0);

            if(!TextUtils.isEmpty(product_id)) {
                String where = OrderColumns.PRODUCT_ID + "='" + product_id + "'   and " + OrderColumns.USER_ID + " = '" + userID + "'";
                context.getContentResolver().update(OrderProvider.getContentURI(context, OrderProvider.TABLE_ORDER), values, where, null);
            }
        }catch(Exception e) {
            StaticReport.report(context, e.getMessage());
        }
    }
    
    public static void batchUpdateIabOderStatus(Context context, String ids, boolean has_ordered) {
        try{
            String userID = VGClient.getCurrentUser().uid;
            if(TextUtils.isEmpty(userID)) return;

            ContentValues values = new ContentValues();
            values.put(OrderColumns.HAS_ORDERED, has_ordered? 1:0);
            if(!TextUtils.isEmpty(ids)) {
                String where = OrderColumns.PRODUCT_ID + "  in(" + ids + ")  and " + OrderColumns.USER_ID + " = '" + userID + "'";
                context.getContentResolver().update(OrderProvider.getContentURI(context, OrderProvider.TABLE_ORDER), values, where, null);
            }
        }catch(Exception e) {
            StaticReport.report(context, e.getMessage());
        }
    }
    
    public static void updateIabConsumeStatus(Context context, String product_id, boolean has_consumed) {
        try{
            String userID = VGClient.getCurrentUser().uid;
            if(TextUtils.isEmpty(userID)) return;

            ContentValues values = new ContentValues();
            values.put(OrderColumns.HAS_CONSUMED, has_consumed? 1:0);
            if(!TextUtils.isEmpty(product_id)) {
                String where = OrderColumns.PRODUCT_ID + "='" + product_id + "'   and " + OrderColumns.USER_ID + " = '" + userID + "'";
                context.getContentResolver().update(OrderProvider.getContentURI(context, OrderProvider.TABLE_ORDER), values, where, null);
            }
        }catch(Exception e) {
            StaticReport.report(context, e.getMessage());
        }
    }
    
    public static ArrayList<Order> queryOrderList(Context context, boolean has_ordered, boolean has_consumed) {
        ArrayList<Order>  list = null;
        try{
            String userID = VGClient.getCurrentUser().uid;
            if(TextUtils.isEmpty(userID)) return null;

            StringBuilder where = new StringBuilder(OrderColumns.HAS_ORDERED + " = '" + (has_ordered?1:0) + "'");
            where.append(" and " + OrderColumns.HAS_CONSUMED + " = '" + (has_consumed?1:0) + "'");
            where.append(" and " + OrderColumns.USER_ID + " = '" + userID + "'");
            Cursor cursor = context.getContentResolver().query(OrderProvider.getContentURI(context, OrderProvider.TABLE_ORDER),OrderColumns.PROJECTION, where.toString(), null, null);
            if (cursor != null) {
                if( cursor.getCount() > 0) {
                    list = new ArrayList<Order>();
                    while(cursor.moveToNext()) {
                        Order orderInfo = formartOrder(cursor);

                        list.add(orderInfo);
                    }
                }
                cursor.close();
            }
        }catch(Exception e) {
            StaticReport.report(context, e.getMessage());
        }
        return list;
    }

    private static  Order formartOrder(Cursor cursor)
    {
        Order orderInfo = new Order();
        orderInfo.product_id = cursor.getString(cursor.getColumnIndexOrThrow(OrderColumns.PRODUCT_ID));
        orderInfo.user_id = cursor.getString(cursor.getColumnIndexOrThrow(OrderColumns.USER_ID));
        orderInfo.pay_code = cursor.getString(cursor.getColumnIndexOrThrow(OrderColumns.PAY_CODE));
        orderInfo.pay_type = cursor.getString(cursor.getColumnIndexOrThrow(OrderColumns.PAY_TYPE));
        orderInfo.item_type = cursor.getString(cursor.getColumnIndexOrThrow(OrderColumns.ITEM_TYPE));
        orderInfo.jsonPurchaseInfo = cursor.getString(cursor.getColumnIndexOrThrow(OrderColumns.JSON_PURCHASE_INFO));
        orderInfo.signature = cursor.getString(cursor.getColumnIndexOrThrow(OrderColumns.SIGNATURE));
        orderInfo.version_code = cursor.getInt(cursor.getColumnIndexOrThrow(OrderColumns.VERSION_CODE));
        orderInfo.iab_order_id = cursor.getString(cursor.getColumnIndexOrThrow(OrderColumns.IAB_ORDER_ID));
        orderInfo.has_ordered = cursor.getInt(cursor.getColumnIndexOrThrow(OrderColumns.HAS_ORDERED)) == 1 ? true:false;
        orderInfo.has_consumed = cursor.getInt(cursor.getColumnIndexOrThrow(OrderColumns.HAS_CONSUMED)) == 1 ? true:false;

        try {
            orderInfo.purchase = new Purchase(orderInfo.item_type, orderInfo.jsonPurchaseInfo, orderInfo.signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return orderInfo;
    }

    public boolean isGoodsExist(String goods_id)
    {
        String where = OrderColumns.PRODUCT_ID + "='" + goods_id;
        Cursor cursor = mContext.getContentResolver().query(
                OrderProvider.getContentURI(mContext, OrderProvider.TABLE_ORDER),
                OrderColumns.PROJECTION, where, null, null);

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            Order orderInfo = formartOrder(cursor);
            cursor.close();
            return true;
        }else {
            cursor.close();
            return false;
        }
    }
    
    public Order getLocalOrder(String productID) {
        String userID = VGClient.getCurrentUser().uid;
        if(TextUtils.isEmpty(userID)) return null;

        String where = OrderColumns.PRODUCT_ID + "='" + productID + "'   and " + OrderColumns.USER_ID + " = '" + userID + "'";
        Cursor cursor = mContext.getContentResolver().query(
                OrderProvider.getContentURI(mContext, OrderProvider.TABLE_ORDER),
                OrderColumns.PROJECTION, where, null, null);

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            Order orderInfo = formartOrder(cursor);
            cursor.close();
            return orderInfo;
        } else {
            cursor.close();
            return null;
        }
    }
}
