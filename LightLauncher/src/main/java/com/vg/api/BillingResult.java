package com.vg.api;

/**
 * Created by huadong on 11/19/13.
 */
public class BillingResult {

        public static final int TYPE_IAB = 0;
        public static final int TYPE_IAP = 1;
        public String payCode;
        public String orderId;
        public String originalJson;
        public String tradeId;
        public int    billingType;

        public String response;

        public BillingResult(int billingType) {
            super();
            this.billingType = billingType;
        }
}
