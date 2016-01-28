package com.digdream.androidrubiksolver.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digdream.androidrubiksolver.R;

public class LapViewAdapter extends ArrayAdapter<String> {

	private final Context mContext;
	private final ArrayList<String> mValuesLapSplit;
	int mLayoutResourceId;

	public LapViewAdapter(Context context, int resourceID,
			ArrayList<String> valuesLapSplit) {
		super(context, resourceID, valuesLapSplit);
		mContext = context;
		mLayoutResourceId = resourceID;
		mValuesLapSplit = valuesLapSplit;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(mLayoutResourceId, parent, false);

		TextView lapNumber = (TextView) rowView.findViewById(R.id.tvLapNumber);
		TextView lapSplitTime = (TextView) rowView
				.findViewById(R.id.tvSplitTimeLap);

        int i = mValuesLapSplit.size() - position;

        switch(i){
            case 1:
                lapNumber.setText(mContext.getString(R.string.cross));
                break;
            case 2:
                lapNumber.setText(mContext.getString(R.string.f2l));
                break;
            case 3:
                lapNumber.setText(mContext.getString(R.string.oll));
                break;
            case 4:
                lapNumber.setText(mContext.getString(R.string.pll));
                break;
            default:
                lapNumber.setText(mContext.getString(R.string.lap) + " "
                        + Integer.toString(i));
        }
		lapSplitTime.setText(mValuesLapSplit.get(position));

		return rowView;
	}

}