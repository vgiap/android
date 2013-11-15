package com.lightapp.lightlauncher.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lightapp.lightlauncher.GoodsDetailActivity;
import com.lightapp.lightlauncher.view.GoodsItemView;
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

        //load data

        List<VGOpenAPI.Goods> goods = new ArrayList<VGOpenAPI.Goods>();

        pageIndex = getArguments().getInt(ARG_OBJECT, 1);

        if(pageIndex == 1)
        {
            goods.add(new VGOpenAPI.Goods("Local Goods 1", ""));
            goods.add(new VGOpenAPI.Goods("Local Goods 2", ""));
        }
        else if(pageIndex == 2)
        {
            goods.add(new VGOpenAPI.Goods("Google 0.99$", ""));
            goods.add(new VGOpenAPI.Goods("Google 1.99$", ""));
            goods.add(new VGOpenAPI.Goods("Golden Coin 100", ""));
            goods.add(new VGOpenAPI.Goods("Diamond 1", ""));
        }
        else if(pageIndex == 3)
        {
            goods.add(new VGOpenAPI.Goods("Online Free", ""));
            goods.add(new VGOpenAPI.Goods("Online Free", ""));
            goods.add(new VGOpenAPI.Goods("Online Free", ""));
            goods.add(new VGOpenAPI.Goods("Online Free", ""));
        }

        GoodsListAdapter gla = new GoodsListAdapter(goods, getActivity());

        listView.setAdapter(gla);


        return pview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //load demo data

        pageIndex = getArguments().getInt(ARG_OBJECT, 1);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), GoodsDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("GOODS", ((GoodsItemView)view).getContent());
                view.getContext().startActivity(intent);
            }
        });
    }

    static class GoodsListAdapter extends BaseAdapter {

        List<VGOpenAPI.Goods> internalRef;
        Context mContext;
        public GoodsListAdapter(List<VGOpenAPI.Goods> goods, Context con)
        {
            internalRef = goods;
            mContext = con.getApplicationContext();
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
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            GoodsItemView view = null;

            if(convertView == null)
            {
                view = new GoodsItemView(mContext);
                view.setGoods(internalRef.get(i));
            }
            else if(convertView instanceof GoodsItemView)
            {
                view = (GoodsItemView)convertView;
                view.setGoods(internalRef.get(i));
            }

            return view;
        }

        static class ViewHolder {
            public ImageView imageCover;
            public TextView textName;
        }
    }
}