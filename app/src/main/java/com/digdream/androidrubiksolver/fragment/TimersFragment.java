package com.digdream.androidrubiksolver.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.android.RubiksCubeGLActivity;
import com.digdream.androidrubiksolver.android.RubiksCubeGLSurfaceView;


/**
 * Created by dubuqingfeng on 16/12/14.
 */
public class TimersFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "ReFragment";
    private View mView;
    private RubiksCubeGLSurfaceView glView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        glView = new RubiksCubeGLSurfaceView(this.getActivity());
        mView = inflater.inflate(R.layout.fragment_timer, null);
        return mView;
    }

    private void findViewsById() {
        LinearLayout buttonsLayout = (LinearLayout) mView.findViewById(R.id.buttonsLayout);
        final String[] names = {
                "Front",
                "Right",
                "Back",
                "Left",
                "Top",
                "Bottom"
        };
        for (int i = 0; i < 6; i++) {
            Button btn = new Button(this.getActivity());
            btn.setText(names[i]);
            final int id = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    glView.getRenderer().rotating(id);
                    Log.e(TAG, "" + id);
                }
            });
            buttonsLayout.addView(btn);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = (LinearLayout) this.getActivity().findViewById(R.id.mainLayout);
        linearLayout.addView(glView);
        findViewsById();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.getActivity().getMenuInflater().inflate(R.menu.activity_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.menu_settings:
                return true;
            case R.id.menu_scramble:
                glView.getRenderer().toggleScrambleCube();
                return true;
            case R.id.menu_solve:
                glView.getRenderer().toggleSolveCube();
                return true;
            case R.id.menu_reset_camera:
                glView.getRenderer().resetCamera();
                return true;
            case R.id.menu_reset_cube:
                glView.getRenderer().resetCube();
                return true;
        }
        return false;
    }
}