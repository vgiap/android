package com.lightapp.lightlauncher.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vg.api.VGOpenAPI;
import com.lightapp.lightlauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huadong on 11/14/13.
 */
public class GoodsCategoryFragment extends Fragment {
    public static final  String ARG_OBJECT = "Goods_Category";

    ListView listView;
    int pageIndex=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pview =  inflater.inflate(R.layout.goods_category_list, container, false);

        listView = (ListView)pview.findViewById(R.id.list_item);
        return pview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //load demo data

        pageIndex = savedInstanceState.getInt(ARG_OBJECT, 1);
    }

    @Override
    public void onResume()
    {
        //load data

        List<VGOpenAPI.Goods> goods = new ArrayList<VGOpenAPI.Goods>();

        GoodsListAdapter gla = new GoodsListAdapter(goods);

        listView.setAdapter(gla);
    }



    static class GoodsListAdapter extends BaseAdapter {

        List<VGOpenAPI.Goods> internalRef;
        public GoodsListAdapter(List<VGOpenAPI.Goods> goods)
        {
            internalRef = goods;
        }

        @Override
        public int getCount() {
            return internalRef.size();
        }

        @Override
        public Object getItem(int i) {
            return internalRef.get(i);
        }

        @Override
        public long getItemId(int i) {
            return internalRef.get(i).gid.hashCode();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }

        static class ViewHolder {
            public ImageView imageCover;
            public TextView textName;
        }
    }
}