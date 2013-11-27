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
        //call purchase
        if(view.getId() == R.id.button_purchase)
            VGClient.purchase(this.getActivity(), goods, 1, new VGClient.PurchaseListener() {
                @Override
                public void onException(Exception ex) {

                }

                @Override
                public void onBillingFinished(VGData.Goods goods, BillingResult response) {
                }

                @Override
                public void onUploadOrderFinished(boolean res,     VGData.Receipt billingResult) {

                }
            });
    }
}