package com.lightapp.lightlauncher;

import android.annotation.TargetApi;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.lightapp.lightlauncher.fragment.GoodsCategoryFragment;

/**
 * Created by huadong on 11/14/13.
 */
public class GoodsPresentActivity extends ActionBarActivity {

    //license key for iap
    //MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhyB0f3W9fSH7KsvS9S0AmSblEqPXLwLS/e3k+zi+Cqb0ZEMvr+UEWyufVdEdv8PlN0DRQfpPLn3FKb56a62vBhJhe+RyRNM081a1B6A2oGR9YFGa0DWjxStix5ue7bWv4hWU1/lBwV+re65HpXSHA/wGMrA5OFCgVrIgcXABBctCiMDddgSHHTEeu7rAa3GRYRM9ixrdI2XZI8/0k59PaM5/ldhDzLuoS/3pBsxAB2/Z/DWc9gqelb4cqy7e6tcShPAUmKHFe/u+8w8KA0jLKPnErCksUFG5nfsFYBAAlzle483Dg4sUB+iJjDrXTWvxD7+O0rBJJRemJTVSCvv3FQIDAQAB

    ViewPager mViewPager;
    GoodsPageAdapter mGoodsPagerAdapter;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.virtual_goods_views);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        mGoodsPagerAdapter = new GoodsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mGoodsPagerAdapter);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener(){

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < 3; i++) {
            if(i==0)
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Local")
                            .setTabListener(tabListener));

            if(i==1)
                actionBar.addTab(
                        actionBar.newTab()
                                .setText("Purchase from Google")
                                .setTabListener(tabListener));

            if(i==2)
                actionBar.addTab(
                        actionBar.newTab()
                                .setText("Purchase by Gold Coin")
                                .setTabListener(tabListener));

        }



        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });


    }




    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class GoodsPageAdapter extends FragmentStatePagerAdapter {
        public GoodsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new GoodsCategoryFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(GoodsCategoryFragment.ARG_OBJECT, i + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

}