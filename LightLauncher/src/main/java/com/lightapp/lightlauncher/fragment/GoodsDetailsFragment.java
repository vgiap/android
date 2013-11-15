package com.lightapp.lightlauncher.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lightapp.lightlauncher.R;

/**
 * Created by huadong on 11/15/13.
 */

public class GoodsDetailsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.goods_details_fragment, container, false);
    }
}