package com.vg.billing;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vg.billing.db.OrderHelper;
import com.vg.billing.google.util.Purchase;

public class IAPHelperService extends IntentService {
	private static final String TAG = IAPHelperService.class.getSimpleName();

	private Context     mContext;
	private IABHomeWork iabManager = null;

    public static final int TYPE_PURCHASE = 0;
    public static final int TYPE_UPLOAD   = 1;
    public static final int TYPE_CONSUME  = 2;
    public static final String SYNC_TYPE  = "sync_type";
    public static final String RESCHEDULE_NEXT_UPLOAD_ACTION="RESCHEDULE_NEXT_UPLOAD_ACTION";

    public IAPHelperService() {
        super(TAG);
    }  
    
  	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "----------onCreate()-----------");

        //just create database
        OrderHelper rh = new OrderHelper(this, false);
        rh.getLocalOrder("");

        rh.addSettings("test", "testvalue");


        mContext   = this.getApplicationContext();
        iabManager = new IABHomeWork(mContext,this);
	}
  	
  	@Override
  	public void onStart(Intent intent, int startId) {
  	    Log.v(TAG, "----------onStart()-----------");
  	    super.onStart(intent, startId);
  	}
  	
	@Override
	protected void onHandleIntent(Intent intent) {
        int syncType = intent.getIntExtra(SYNC_TYPE, -1);

        Log.v(TAG, "----------onHandleIntent()-----------syncType= " + syncType);
		performSyncTask(intent, syncType);
	}

	private boolean performSyncTask(Intent intent, int syncType) {
		try {
            switch(syncType)
            {
                case IAPHelperService.TYPE_PURCHASE:
                {
                    iabManager.queryPurchases();
                }
                case TYPE_UPLOAD:
                {
                    iabManager.batchUploadOrders();
                }
                case TYPE_CONSUME:
                {
                    iabManager.consumeAsync();
                }
                default:
                {
                    Log.w(TAG, "onSyncTaskFinish, do nothing with unknown type = " + syncType);
                }

                return true;
            }

		} catch (Exception e) {
			Log.e(TAG, "failed execute, logic exception.", e);
		} finally {
			Log.d(TAG, "finished do job, type = " + syncType);
		}
		return false;
	}
}

