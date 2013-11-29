package com.vg.api;

import android.text.TextUtils;

import com.vg.http.VGException;

import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by huadong on 11/19/13.
 */
public class VGData {

    public static class Goods implements Serializable
    {
        //for test will remove later
        public Goods(String name, String cover, Payload sku)
        {
            this.gid   = name;
            this.name  = name;
            this.cover = cover;
            this.pay   = sku;
        }

        public String gid;        //goods global id
        public String name;       //goods name
        public String cover;      //cover url

        public boolean paid;
        public long    paid_date;

        public Payload pay;

        public String getSku()
        {
            return pay.sku;
        }

        //
        //help function
        //
        //
        public String getPayload() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("product_id").append("=").append(gid).append(",")
                    .append("name").append("=").append(name).append(",")
                    .append("user_id").append("=").append(VGClient.getCurrentUser().uid).append(",")
                    .append("mGoogleSku").append("=").append(pay.sku).append(",");
            return stringBuilder.toString();
        }

        public static String getProductIdFromPayload(String payload) {
            if(TextUtils.isEmpty(payload)) return null;
            return getValue(payload, "product_id");
        }

        public static String getUserIdFromPayload(String payload) {
            if(TextUtils.isEmpty(payload)) return null;
            return getValue(payload, "user_id");
        }

        public static int getVersionCodeFromPayload(String payload) {
            if(!TextUtils.isEmpty(payload)) {
                String value =  getValue(payload, "version_code");
                if(!TextUtils.isEmpty(value)) {
                    return Integer.valueOf(value);
                }
            }
            return 1;
        }

        private static String getValue(String payload, String key) {
            String[] pStrs = payload.split(",");
            String value = null;
            for(String str : pStrs) {
                if(str.contains(key)) {
                    value = str.substring(key.length()+1, str.length());
                    break;
                }
            }
            return value;
        }
    }

    // Virtual goods internal account user
    public static class User {
        public String uid;             // user id, it is virtual goods internal user identify
        public String human;           // 3d account user identify, email
        public String appData;         // 3d account login returned data, we saved one raw data in server
        public String ticket;          // ticket in virtual goods system

        public User clone() {
            User item = new User();
            item.uid = this.uid;
            item.human = this.human;
            item.appData = this.appData;
            item.ticket  = this.ticket;

            return item;
        }

        public User()
        {
            uid = "";
            human = "I have no user";
            appData = "";
            ticket  = "";
        }

        public static User NullUser = new User();

        //TODO
        public static User parseJson(String response) throws VGException{
            return null;
        }
    }

    /*
     * please map to server-endpoint definition
     */
    public static class PayType{
        public final static int FREE         =0;
        public final static int GOOGLE_PLAYER=1;
        public final static int APPLE_STORE  =2;
        public final static int ALI_PAY      =3;
        public final static int VIRTUAL_CURRENCY_COIN   =4;
        public final static int VIRTUAL_CURRENCY_DIAMOND=5;
    };

    public static Payload FreePayLoad =new VGData.Payload("Free", "no sku", PayType.FREE);

    public static class Payload implements Serializable{
        public String   displayName;
        public String   sku;
        public int      type;



        public Payload(String displayName, String sku_string, int pay_type)
        {
            this.displayName = displayName;
            this.sku  = sku_string;
            this.type = pay_type;
        }
    }


    public static class Receipt{
        public String orderNumber;

        //create one receipt from google billing result, it should be return from VG service
        public Receipt(BillingResult response) {

        }

        //TODO
        public static Receipt parseJson(String response)throws JSONException{
            return null;
        }
    }
}
