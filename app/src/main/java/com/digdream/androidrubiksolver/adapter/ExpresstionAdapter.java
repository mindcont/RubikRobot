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

import com.digdream.androidrubiksolver.entity.ExpresstionMessage;
import com.digdream.androidrubiksolver.R;

import java.util.List;

/**
 * Created by dubuqingfeng on 2/25/15.
 */
public class ExpresstionAdapter extends BaseAdapter {
    private Context mContext = null;
    public List<ExpresstionMessage> data;

    public ExpresstionAdapter(Context ctx, List<ExpresstionMessage> data) {
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
                    R.layout.item_expression, parent, false);
            holder = new ViewHolder();
            holder.expresstionlist = (RelativeLayout) convertView
                    .findViewById(R.id.expresstionlist);

            holder.tv_expresstion_title = (TextView) convertView
                    .findViewById(R.id.tv_expresstion_title);
            holder.tv_expresstion_upset = (TextView) convertView
                    .findViewById(R.id.tv_expresstion_upset);
            holder.tv_expresstion_classify = (TextView) convertView
                    .findViewById(R.id.tv_expresstion_classify);

            convertView.setTag(holder);
        } else {// 有直接获得ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        Log.i("SharebookAdapter", "getView position=" + position);

        ExpresstionMessage msg = data.get(position);

        holder.tv_expresstion_title.setText(msg.getTitle());
        holder.tv_expresstion_upset.setText(msg.getUpset());
        holder.tv_expresstion_classify.setText(msg.getClassify());
        final ListView mListView = (ListView) convertView
                .findViewById(R.id.mListView);
        return convertView;
    }

    static class ViewHolder {
        RelativeLayout expresstionlist;
        TextView tv_expresstion_title;
        TextView tv_expresstion_classify;
        TextView tv_expresstion_upset;
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
