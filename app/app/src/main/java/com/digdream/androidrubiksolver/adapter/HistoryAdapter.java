package com.digdream.androidrubiksolver.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digdream.androidrubiksolver.entity.HistoryMessage;
import com.digdream.androidrubiksolver.R;

import java.util.List;

/**
 * Created by dubuqingfeng on 2/25/15.
 */
public class HistoryAdapter extends BaseAdapter {
    private Context mContext = null;
    public List<HistoryMessage> data;

    public HistoryAdapter(Context ctx, List<HistoryMessage> data) {
        this.data = data;
        this.mContext = ctx;
    }

    public void updateData() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_history, parent, false);
            holder = new ViewHolder();
            holder.historylist = (RelativeLayout) convertView
                    .findViewById(R.id.historylist);

            holder.tv_history_time = (TextView) convertView
                    .findViewById(R.id.tv_history_time);
            holder.tv_history_recover = (TextView) convertView
                    .findViewById(R.id.tv_history_recover);
            holder.tv_history_upset = (TextView) convertView
                    .findViewById(R.id.tv_history_upset);
            holder.tv_history_test_time = (TextView) convertView
                    .findViewById(R.id.tv_history_test_time);
            holder.tv_history_classify = (TextView) convertView
                    .findViewById(R.id.tv_history_classify);

            convertView.setTag(holder);
        } else {// 有直接获得ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        Log.i("SharebookAdapter", "getView position=" + position);

        HistoryMessage msg = data.get(position);

        holder.tv_history_time.setText(msg.getHistory_recover_time());
        holder.tv_history_recover.setText(msg.getRecover());
        holder.tv_history_upset.setText(msg.getUpset());
        holder.tv_history_test_time.setText(msg.getTest_time());
        holder.tv_history_classify.setText(msg.getClassify());
        final ListView mListView = (ListView) convertView
                .findViewById(R.id.mListView);
        return convertView;
    }

    static class ViewHolder {
        RelativeLayout historylist;
        TextView tv_history_time;
        TextView tv_history_recover;
        TextView tv_history_upset;
        TextView tv_history_test_time;
        TextView tv_history_classify;
    }

    /**
     * 单击事件监听器
     */
    private onRightItemClickListener mListener = null;

    public void setOnRightItemClickListener(onRightItemClickListener listener) {
        mListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(View v, int position);
    }
}
