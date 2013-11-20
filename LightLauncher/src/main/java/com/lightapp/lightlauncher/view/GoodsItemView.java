package com.lightapp.lightlauncher.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lightapp.lightlauncher.GoodsDetailActivity;
import com.lightapp.lightlauncher.R;
import com.vg.api.VGData;

/**
 * Created by huadong on 11/15/13.
 */
public class GoodsItemView extends RelativeLayout {
    public GoodsItemView(Context context) {
        super(context);

        initView();
    }

    VGData.Goods content;

    TextView  title;
    ImageView goods_cover;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setGoods(VGData.Goods goods)
    {
        content = goods;
        title.setText(goods.name);
    }

    private void initView()
    {
        this.removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.goods_item, null);
        title       = (TextView)view.findViewById(R.id.goods_title);
        goods_cover = (ImageView)view.findViewById(R.id.goods_bg_default);

        addView(view);

    }


    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), GoodsDetailActivity.class);
        view.getContext().startActivity(intent);
    }

    public VGData.Goods getContent() {
        return content;
    }
}
