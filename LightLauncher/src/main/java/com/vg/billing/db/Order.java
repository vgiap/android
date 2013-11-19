package com.vg.billing.db;


import com.vg.billing.google.util.Purchase;

public class Order {

    public String   product_id;
    public String   user_id;
    public String   pay_code;
    public String   pay_type;
    public String   item_type;
    public String   jsonPurchaseInfo;
    public String   signature;
    public int      version_code;
    public String   iab_order_id;
    public Purchase purchase;
    //
    //to record the transaction,
    //server need save the order id to track sells
    //to make sure we record order id successfully,
    //only recorded order product can be consumed
    //
    //the goods id will be saved in developerPayload of Purchase data structure to identify goods
    //
    public boolean  has_ordered;
    public boolean  has_consumed;

}
