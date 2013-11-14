package com.lightapp.lightlauncher.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lightapp.lightlauncher.R;

/**
 * Created by huadong on 11/14/13.
 */
public class GoodsCategoryFragment extends Fragment {
    public static final  String ARG_OBJECT = "Goods_Category";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.goods_category_list, container, false);
    }
}