package com.digdream.androidrubiksolver.utils;


import android.content.Context;
import android.text.format.DateUtils;

import com.digdream.androidrubiksolver.R;

public class TextUtils {

    public static CharSequence getRelativeTimeDisplayString(Context context, long referenceTime) {
        long now = System.currentTimeMillis();
        long difference = now - referenceTime;
        return (difference >= 0 && difference <= DateUtils.MINUTE_IN_MILLIS) ?
                context.getResources().getString(R.string.just_now) :
                DateUtils.getRelativeTimeSpanString(
                        referenceTime,
                        now,
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE);
    }

}
