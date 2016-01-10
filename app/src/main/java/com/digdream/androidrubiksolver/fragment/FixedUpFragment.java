package com.digdream.androidrubiksolver.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.adapter.ExpresstionAdapter;
import com.digdream.androidrubiksolver.entity.ExpresstionMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 *
 */
public class FixedUpFragment extends Fragment {

    private View mView;
    private ExpresstionAdapter mExpresstionAdapter;
    private List<ExpresstionMessage> data = new ArrayList<>();
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_fixed_up, null);
        findViewsById();
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        mExpresstionAdapter = new ExpresstionAdapter(this.getActivity(), data);
        mListView.setAdapter(mExpresstionAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.e("tag", "clickPosition" + position);
            }
        });

        List<ExpresstionMessage> datahandler = new ArrayList<ExpresstionMessage>();

        for (int i = 0; i < 1; i++) {
            ExpresstionMessage msg = new ExpresstionMessage();
            msg.setTitle("六面回字公式");
            msg.setClassify("机器人三速");
            msg.setUpset("打乱步骤：U2F2R2");
            datahandler.add(msg);
        }

        Message msg = mHandler.obtainMessage(11, datahandler);
        mHandler.sendMessage(msg);

    }

    private void findViewsById() {
        mListView = (ListView) mView.findViewById(R.id.mListView);

    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11:
                    if (mExpresstionAdapter != null) {
                        mExpresstionAdapter.data.addAll((ArrayList<ExpresstionMessage>) msg.obj);
                        mExpresstionAdapter.updateData();
                    }
                    break;
            }
        }

    };


}
