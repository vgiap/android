package com.lightapp.lightlauncher.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lightapp.lightlauncher.R;
import com.vg.api.BillingResult;
import com.vg.api.VGClient;
import com.vg.api.VGData;

/**
 * Created by huadong on 11/15/13.
 */

public class GoodsDetailsFragment extends Fragment implements View.OnClickListener{
    private final String TAG="GoodsDetailsFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    TextView        goodsName;
    Button          purchaseButton;
    VGData.Goods    goods;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View containView = inflater.inflate(R.layout.goods_details_fragment, container, false);
        purchaseButton = (Button) containView.findViewById(R.id.button_purchase);

        goodsName = (TextView)containView.findViewById(R.id.goods_name);

        purchaseButton.setOnClickListener(this);

        goods = (VGData.Goods)getArguments().getSerializable("GOODS");
        goodsName.setText(goods.name);

        return containView;
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_purchase)
        {
            //call purchase
            VGClient.purchase(this.getActivity(), goods, new VGClient.PurchaseListener() {
                @Override
                public void onBillingFinished(boolean isSuccess, BillingResult result) {
                    Log.d(TAG, "purchase result="+isSuccess + " billing result="+result);
                }
            });
        }
    }
}