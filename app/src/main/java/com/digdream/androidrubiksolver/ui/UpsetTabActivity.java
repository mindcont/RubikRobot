package com.digdream.androidrubiksolver.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.fragment.FixedUpFragment;
import com.digdream.androidrubiksolver.fragment.SolverFragment;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class UpsetTabActivity extends AppCompatActivity implements MaterialTabListener{
    private CharSequence mTitle;
    private MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upset_tab);
        tabHost = (MaterialTabHost) this.findViewById(R.id.upsetMaterialTabHost);

        pager = (ViewPager) this.findViewById(R.id.upsetPager);

        // init view pager
        //初始化页面显示
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                //当用户点击时便签的变化
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        //插入来自pagerAdapter数据所有标签
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充菜单，向操作栏增加条目
        //getMenuInflater().inflate(R.menu.menu_upset_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //处理动作栏项目点击这里。将操作栏 自动在主/向上按钮操作的点击，
        //只要你在AndroidManifest.xml中指定一个父活动。
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    return new SolverFragment();
                case 1:
                    return new FixedUpFragment();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence pageTitle = null;
            switch (position) {
                case 0:
                    pageTitle = getString(R.string.upsetTab_section1);
                    break;
                case 1:
                    pageTitle = getString(R.string.upsetTab_section2);
                    break;
            }
            return pageTitle;
        }

    }
}
