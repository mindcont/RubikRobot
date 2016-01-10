package com.digdream.androidrubiksolver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.adapter.HistoryAdapter;
import com.digdream.androidrubiksolver.android.RubiksCubeGLActivity;
import com.digdream.androidrubiksolver.entity.HistoryMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dubuqingfeng on 16/12/14.
 */
public class HistoryFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "HistoryFragment";
    private View mView;
    private HistoryAdapter mHistoryAdapter;
    private List<HistoryMessage> data = new ArrayList<HistoryMessage>();
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_history, null);
        findViewsById();

        return mView;
    }

    private void findViewsById() {
        mListView = (ListView) mView.findViewById(R.id.mListView);
        mListView.setAdapter(mHistoryAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        mHistoryAdapter = new HistoryAdapter(this.getActivity(), data);
        mListView.setAdapter(mHistoryAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.e("tag", "clickPosition" + position);
            }
        });

        List<HistoryMessage> datahandler = new ArrayList<HistoryMessage>();

        for (int i = 0; i < 3; i++) {
            HistoryMessage msg = new HistoryMessage();
            msg.setHistory_recover_time("12'12'");
            msg.setClassify("机器人三速");
            msg.setRecover("复原步骤：U2F2R2");
            msg.setUpset("打乱步骤：U2F2R2");
            msg.setTest_time("12 小时前");
            datahandler.add(msg);
        }

        Message msg = mHandler.obtainMessage(11, datahandler);
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11:
                    if (mHistoryAdapter != null) {
                        mHistoryAdapter.data.addAll((ArrayList<HistoryMessage>) msg.obj);
                        mHistoryAdapter.updateData();
                    }
                    break;
            }
        }

    };
}