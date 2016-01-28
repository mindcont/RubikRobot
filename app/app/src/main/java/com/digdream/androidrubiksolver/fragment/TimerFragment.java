package com.digdream.androidrubiksolver.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.adapter.LapViewAdapter;
import com.digdream.androidrubiksolver.scramble.ScrambleGenerator;
import com.digdream.androidrubiksolver.widget.Chronometer;

import java.util.ArrayList;


/**
 * Created by dubuqingfeng on 16/12/14.
 */
public class TimerFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "TimerFragment";
    private View mView;
    Chronometer mChronometer;
    Boolean mChronoPaused = false;
    long mElapsedTime = 0;
    ImageButton mStartButton, mPauseButton, mStopButton;
    static ArrayList<String> mSplitTimes = new ArrayList<String>();
    LapViewFragment splitTimesFragment;
    private TextView scramble;
    private String scrambleText;
    FrameLayout mFrameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_timer, null);
        findViewsById();
        return mView;
    }

    private void findViewsById() {

        mChronometer = (Chronometer) mView.findViewById(R.id.chronometer);

        mStartButton = (ImageButton) mView.findViewById(R.id.bStart);
        mStartButton.setOnClickListener(startListener);

        mPauseButton = (ImageButton) mView.findViewById(R.id.bPause);
        mPauseButton.setOnClickListener(pauseListener);

        scramble = (TextView) mView.findViewById(R.id.scramble);

        mStopButton = (ImageButton) mView.findViewById(R.id.bStop);
        mStopButton.setOnClickListener(stopListener);

        mFrameLayout = (FrameLayout) mView.findViewById(R.id.flLapView);

        if (getFragmentManager().findFragmentById(R.id.flLapView) == null) {
            splitTimesFragment = new LapViewFragment();
            this.getActivity().getFragmentManager().beginTransaction()
                    .add(R.id.flLapView, splitTimesFragment).commit();
        }
        displayNextScramble();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewsById();
    }

    public void displayNextScramble() {
        scrambleText = ScrambleGenerator.nextScramble("3x3x3");
        Runnable refreshScramble = new Runnable() {
            @Override
            public void run() {
                scramble.setText(scrambleText);
            }
        };
        getActivity().runOnUiThread(refreshScramble);
    }

    public void hideScramble() {
        Runnable hideScramble = new Runnable() {
            @Override
            public void run() {
                scramble.setVisibility(View.GONE);
            }
        };
        getActivity().runOnUiThread(hideScramble);
    }

    public void showScramble() {
        scrambleText = ScrambleGenerator.nextScramble("3x3x3");
        Runnable showScramble = new Runnable() {
            @Override
            public void run() {
                scramble.setVisibility(View.VISIBLE);
                scramble.setText(scrambleText);
            }
        };
        getActivity().runOnUiThread(showScramble);
    }


    //onRestoreInstanceState
    //onSaveInstanceState

    View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View v) {

            if (mChronoPaused) {
                // chronometer was paused, now resume
                mPauseButton.setVisibility(View.VISIBLE);
                mStopButton.setVisibility(View.VISIBLE);
                Log.v(TAG, "start-chrono was paused");
                mChronometer.setBase(SystemClock.elapsedRealtime()
                        - mElapsedTime);
                TimerFragment.this.getActivity().getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (!mChronometer.isStarted()) {
                // chronometer was stopped, restart

                mPauseButton.setVisibility(View.VISIBLE);
                mStopButton.setVisibility(View.VISIBLE);
                ((FrameLayout) TimerFragment.this.getActivity().findViewById(R.id.flLapView)).removeAllViews();

                splitTimesFragment = new LapViewFragment();
                TimerFragment.this.getActivity().getFragmentManager().beginTransaction()
                        .add(R.id.flLapView, splitTimesFragment).commit();
                Log.v(TAG, "start-chrono was stopped");
                mChronometer.setBase(SystemClock.elapsedRealtime());
                TimerFragment.this.getActivity().getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            } else if (!mChronoPaused) {
                // chronometer, is running so split into new lap
                Log.v(TAG, "split button pressed");

                mSplitTimes
                        .add(0,
                                timeFormat((SystemClock.elapsedRealtime() - mChronometer
                                        .getBase())));

                splitTimesFragment.refresh();

            }

            hideScramble();
            mChronometer.start();
            mStartButton.setImageResource(R.drawable.split);
            mChronoPaused = false;
        }

        private String timeFormat(long l) {
            int minutes;
            float seconds;
            int milliseconds;
            String mins;
            String secs;
            String mill;

            float time = (float) l / 1000;

            minutes = (int) (time / 60);
            seconds = (time % 60);
            milliseconds = (int) (((int) l % 1000) / 10);

            if (minutes < 10) {
                mins = "0" + minutes;
            } else {
                mins = "" + minutes;
            }

            if (seconds < 10) {
                secs = "0" + (int) seconds;
            } else {
                secs = "" + (int) seconds;
            }

            if (milliseconds < 10){
                mill = "0" + (int) milliseconds;
            } else {
                mill = "" + (int) milliseconds;
            }

            return "\t\t\t" + mins + ":" + secs + "." + mill;
        }
    };

    View.OnClickListener pauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mChronoPaused) {
                Log.v(TAG, "pause");
                mPauseButton.setVisibility(View.GONE);
                mChronometer.stop();
                mElapsedTime = SystemClock.elapsedRealtime()
                        - mChronometer.getBase();
                mChronoPaused = true;
                mStartButton.setImageResource(R.drawable.start);
                TimerFragment.this.getActivity().getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    };

    View.OnClickListener stopListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.v(TAG, "stop");
           // Log.v(TAG, String.valueOf(mFrameLayout.getHeight()));
            mStartButton.setVisibility(View.VISIBLE);
            mPauseButton.setVisibility(View.GONE);
            mStopButton.setVisibility(View.GONE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mSplitTimes = new ArrayList<String>();

            mStartButton.setImageResource(R.drawable.start);
            mChronoPaused = false;
            TimerFragment.this.getActivity().getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            showScramble();
        }
    };

    public static class LapViewFragment extends ListFragment {

        LapViewAdapter mLapViewAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mLapViewAdapter = new LapViewAdapter(getActivity(),
                    R.layout.list_lap, mSplitTimes);
            setListAdapter(mLapViewAdapter);
        }

        public void refresh() {
            Log.i("LapViewFragment", "trigger refresh");
            mLapViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }
}